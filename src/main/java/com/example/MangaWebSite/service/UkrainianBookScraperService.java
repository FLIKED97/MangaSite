package com.example.MangaWebSite.service;

import com.example.MangaWebSite.models.Comics;
import com.example.MangaWebSite.models.ComicsType;
import com.example.MangaWebSite.models.ComicsUtil;
import com.example.MangaWebSite.models.PublicationType;
import com.example.MangaWebSite.repository.ComicsRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
//            Elements items = doc.select("div.product-listing.view-category");
/**
 * Сервіс для скрейпінгу українських книжок із категорії «Українська література» на Yakaboo.ua.
 * Назва й опис беруться із сайту (українською), решта полів — рандом/заглушки.
 */
@Service
@RequiredArgsConstructor
public class UkrainianBookScraperService {

    private final ComicsRepository comicsRepository;
    private final EmbeddingService embeddingService;

    // URL категорії «Українська література» із пагінацією
    private static final String CATEGORY_URL =
            "https://www.yakaboo.ua/ua/knigi/hudozhestvennaja-literatura/ukrainskaja-literatura.html?p=";

    private final Random random = new Random();

    @Transactional
    public void scrapeAndSaveUkrainianBooks(int count) throws IOException {
        List<Comics> resultList = new ArrayList<>(count);
        Set<String> seenUrls = new HashSet<>();

        int page = 75;
        while (page < count) {
            String pageUrl = CATEGORY_URL + page;
            System.out.println("\n=== Завантажуємо сторінку №" + page + ": " + pageUrl);

            Document doc;
            try {
                doc = Jsoup.connect(pageUrl)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                                "AppleWebKit/537.36 (KHTML, like Gecko) " +
                                "Chrome/115.0 Safari/537.36")
                        .timeout(10_000)
                        .get();
                System.out.println("Сторінка №" + page + " успішно завантажена.");
            } catch (IOException ex) {
                System.err.println("❌ Не вдалося завантажити сторінку: " + pageUrl);
                break;
            }

            // === 1. Знаходимо контейнер з усіма картками ===
            // У поточній версії Yakaboo картки лежать у <div class="category__cards">
            Element cardsContainer = doc.selectFirst("div.category__cards");
            if (cardsContainer == null) {
                System.out.println("⚠️ Не знайдено контейнера карток (div.category__cards) на сторінці №" + page);
                break;
            }

            // === 2. З усіх прямих дочірніх <div> отримуємо кожну картку книги ===
            Elements items = cardsContainer.select("> div");
            if (items.isEmpty()) {
                System.out.println("⚠️ Усередині div.category__cards не знайдено жодної картки.");
                break;
            } else {
                System.out.println("✅ Знайдено " + items.size() + " карток книги на сторінці №" + page);
            }

            // Прапорець, чи хоч одну нову картку додано з цієї сторінки
            boolean addedAtLeastOne = false;

            // === 3. Обробляємо кожну картку ===
            for (Element item : items) {
                if (resultList.size() >= count) break;

                // 3.1. Шукаємо посилання на detail-сторінку (зазвичай <a class="category-card__image" href="…">)
                Element linkEl = item.selectFirst("a.category-card__image");
                if (linkEl == null) {
                    System.out.println("  ⚠️ Не знайдено лінк (a.category-card__image). Пропускаємо.");
                    continue;
                }
                String relativeHref = linkEl.attr("href").trim();
                if (relativeHref.isEmpty()) {
                    System.out.println("  ⚠️ href пустий. Пропускаємо картку.");
                    continue;
                }
                String detailUrl = relativeHref.startsWith("http")
                        ? relativeHref
                        : "https://www.yakaboo.ua" + relativeHref;

                // 3.1.1. Якщо вже обробляли цю URL — пропускаємо
                if (seenUrls.contains(detailUrl)) {
                    System.out.println("  ℹ️ Уже оброблено URL: " + detailUrl + " → пропускаємо дублікат.");
                    continue;
                }
                // Інакше додаємо в seenUrls та вмикаємо прапорець addedAtLeastOne
                seenUrls.add(detailUrl);
                addedAtLeastOne = true;
                System.out.println("  🔗 Детальна сторінка: " + detailUrl);

                // 3.2. Завантажуємо detail-сторінку
                Document detailDoc;
                try {
                    detailDoc = Jsoup.connect(detailUrl)
                            .userAgent("Mozilla/5.0")
                            .timeout(10_000)
                            .get();
                } catch (IOException e) {
                    System.err.println("    ❌ Не вдалося завантажити detail-сторінку: " + detailUrl);
                    continue;
                }

                // 3.3. Беремо назву з <h1 id="product-title">
                Element titleEl = detailDoc.selectFirst("h1#product-title");
                if (titleEl == null) {
                    System.out.println("    ⚠️ Не знайдено назву (h1#product-title). Пропускаємо.");
                    continue;
                }
                String title = titleEl.text().trim();
                if (title.isEmpty()) {
                    System.out.println("    ⚠️ Назва порожня. Пропускаємо.");
                    continue;
                }
                System.out.println("    📘 Назва: " + title);

                // 3.4. Дістаємо опис з <div id="product-description"> → <div class="description__content">
                String description = "";
                Element containerDesc = detailDoc.selectFirst("div#product-description");
                if (containerDesc != null) {
                    Element descEl = containerDesc.selectFirst("div.description__content");
                    if (descEl != null) {
                        description = descEl.text().trim();
                        if (description.isEmpty()) {
                            System.out.println("    ⚠️ Опис є, але він пустий.");
                        } else {
                            String snippet = description.length() > 60
                                    ? description.substring(0, 60) + "…"
                                    : description;
                            System.out.println("    📝 Опис отримано: " + snippet);
                        }
                    } else {
                        System.out.println("    ⚠️ Не знайдено внутрішнього блока опису (div.description__content).");
                    }
                } else {
                    System.out.println("    ⚠️ Не знайдено контейнера опису (div#product-description).");
                }

                // === 4. Формуємо об'єкт Comics ===
                Comics book = new Comics();
                book.setTitle(title);
                book.setOriginalTitle(title);
                book.setAuthor("Автор невідомий");
                book.setDescription(description);
                book.setComicsType(ComicsType.BOOKS);
                book.setPublicationType(PublicationType.BOOK);
                book.setCreatedAt(LocalDateTime.now());
                book.setPublishedAt(randomPastDate(5));
                book.setStatus("PUBLISHED");
                book.setCoverImage(null);
                book.setImageType(null);

                int vc = random.nextInt(1000);
                double pop = 1.0 + random.nextDouble() * 4.0;
                book.setViewCount(vc);
                book.setPopularityRating(pop);
                System.out.println("  🎯 viewCount=" + vc + ", popularityRating=" + String.format("%.2f", pop));

                if (!description.isBlank()) {
                    try {
                        float[] embedding = embeddingService.getEmbedding(description);
                        byte[] embBytes = ComicsUtil.serializeFloatArray(embedding);
                        book.setDescriptionEmbedding(embBytes);
                        System.out.println("    ✅ Ембеддінг успішно згенеровано.");
                    } catch (Exception ex) {
                        book.setDescriptionEmbedding(null);
                        System.err.println("    ⚠️ Помилка при ембеддінгу: " + ex.getMessage());
                    }
                } else {
                    book.setDescriptionEmbedding(null);
                }

                book.setGenres(new ArrayList<>());
                book.setChapters(new ArrayList<>());
                book.setComments(new ArrayList<>());
                book.setTabs(new ArrayList<>());
                book.setStatistics(new ArrayList<>());
                book.setRatings(new ArrayList<>());

                resultList.add(book);
                System.out.println("  ✅ Додано до списку (тепер " + resultList.size() + " книг).");
            }

            // Якщо з цієї сторінки не додалося жодного нового URL → виходимо з циклу
            if (!addedAtLeastOne) {
                System.out.println("\n🛑 На сторінці №" + page + " не знайдено жодного нового запису → завершуємо скрейпінг.");
                break;
            }

            page++;
        }

        // === 5. Зберігаємо всі зібрані книги одним saveAll(...) ===
        if (!resultList.isEmpty()) {
            comicsRepository.saveAll(resultList);
            System.out.println("\n✅ Успішно збережено " + resultList.size() + " українських книжок із Yakaboo.");
        } else {
            System.out.println("\n⚠️ Не знайдено жодної книжки для збереження.");
        }
    }

    /**
     * Генерує випадкову дату у межах останніх {@code yearsBack} років.
     */
    private LocalDate randomPastDate(int yearsBack) {
        LocalDate now = LocalDate.now();
        int daysBack = random.nextInt(yearsBack * 365 + 1);
        return now.minusDays(daysBack);
    }
}


//
///**
// * Сервіс, що скрейпить назви й описи українських романів із Вікіпедії:
// * категорія «Українські романи».
// * Інші поля об'єктів (author, status, viewCount тощо) заповнюються заглушками або випадковими значеннями.
// */
//@Service
//@RequiredArgsConstructor
//public class UkrainianBookScraperService {
//
//    private final ComicsRepository comicsRepository;
//    private final EmbeddingService embeddingService;
//
//    // Адреса категорії «Українські романи» у Вікіпедії
//    private static final String WIKI_CATEGORY_URL =
//            "https://uk.wikipedia.org/wiki/Категорія:Українські_романи";
//
//    private final Random random = new Random();
//
//    /**
//     * Скрейпить перші {@code count} творів із категорії «Українські романи» у Вікіпедії:
//     * бере «title» (заголовок сторінки) та «description» (перший значущий абзац).
//     * Решта полів (author, status, viewCount тощо) — заглушками чи рандомними значеннями.
//     *
//     * @param count кількість книжок (романів) для додавання, наприклад, 100
//     * @throws IOException у разі проблем із завантаженням чи парсингом HTML
//     */
//    @Transactional
//    public void scrapeAndSaveUkrainianBooks(int count) throws IOException {
//        System.out.println("=== Починаємо скрейпінг категорії «Українські романи» (Вікіпедія) ===");
//
//        // 1) Завантажуємо HTML самої категорії
//        Document categoryDoc;
//        try {
//            categoryDoc = Jsoup.connect(WIKI_CATEGORY_URL)
//                    .userAgent("Mozilla/5.0")
//                    .timeout(10_000)
//                    .get();
//            System.out.println("✅ Успішно завантажили сторінку категорії: " + WIKI_CATEGORY_URL);
//        } catch (IOException e) {
//            System.err.println("❌ Не вдалося підключитися до: " + WIKI_CATEGORY_URL);
//            throw e;
//        }
//
//        // 2) Збираємо усі посилання на статті у категорії
//        //    Сторінка Вікіпедії: усі посилання лежать у <div class="mw-category"> → div.mw-category-group → ul → li → a
//        Elements linkElements = categoryDoc.select("div.mw-category div.mw-category-group ul li a");
//        if (linkElements.isEmpty()) {
//            System.err.println("⚠️ Не знайдено жодного посилання у категорії «Українські романи»");
//            return;
//        }
//        System.out.println("🔍 Знайдено в категорії «Українські романи» посилань (романів): " + linkElements.size());
//
//        // 3) Вибираємо перші count (або, якщо їх менше, — усі)
//        int totalFound = linkElements.size();
//        int toTake = Math.min(count, totalFound);
//        System.out.println("📋 Плануємо взяти перших " + toTake + " романів для збереження.");
//
//        List<Comics> resultList = new ArrayList<>(toTake);
//
//        // 4) Для кожного посилання:
//        //    - href видає шлях /wiki/Назва_сторінки
//        //    - текст елемента — це назва роману українською
//        for (int i = 0; i < toTake; i++) {
//            Element a = linkElements.get(i);
//            String href = a.attr("href").trim();
//            String title = a.text().trim();
//
//            if (href.isEmpty() || title.isEmpty()) {
//                System.out.println("  ⚠️ Пропускаємо елемент №" + (i + 1) + ": href чи title пустий.");
//                continue;
//            }
//
//            // Повний URL на статтю
//            String articleUrl = href.startsWith("http")
//                    ? href
//                    : "https://uk.wikipedia.org" + href;
//
//            System.out.println("\n--- Обробляємо роман №" + (i + 1) + " із списку ---");
//            System.out.println("URL статті: " + articleUrl);
//            System.out.println("Назва (title): " + title);
//
//            // 5) Завантажуємо детальну сторінку роману й витягуємо перший значущий <p>
//            String description = "";
//            try {
//                Document articleDoc = Jsoup.connect(articleUrl)
//                        .userAgent("Mozilla/5.0")
//                        .timeout(10_000)
//                        .get();
//                // У Вікіпедії після <div id="mw-content-text"> йдуть параграфи <p>
//                Elements paragraphs = articleDoc.select("div.mw-parser-output > p");
//                // Іноді перший <p> буває пустим або містить лише курсивний шаблон “(…)”,
//                // тому проходимося поки не знайдемо непорожній текст.
//                for (Element p : paragraphs) {
//                    String text = p.text().trim();
//                    if (!text.isEmpty()) {
//                        description = text;
//                        break;
//                    }
//                }
//                if (description.isEmpty()) {
//                    System.out.println("  ⚠️ Не знайшли жодного непорожнього абзацу у статті. Опис залишиться пустим.");
//                } else {
//                    System.out.println("  📝 Перший абзац-опис (приблизно 60 символів): " +
//                            (description.length() > 60 ? description.substring(0, 60) + "…" : description));
//                }
//            } catch (IOException ex) {
//                System.err.println("  ❌ Не вдалося завантажити детальну сторінку: " + articleUrl);
//                description = "";
//            }
//
//            // 6) Створюємо об’єкт Comics і заповнюємо всі поля
//            Comics book = new Comics();
//
//            // 6.1 Назва та originalTitle українською (назва статті)
//            book.setTitle(title);
//            book.setOriginalTitle(title);
//
//            // 6.2 Автор — заглушка «Автор невідомий»
//            book.setAuthor("Автор невідомий");
//
//            // 6.3 Опис (витягнутий із Вікіпедії)
//            book.setDescription(description);
//
//            // 6.4 Типи — BOOKS
//            book.setComicsType(ComicsType.BOOKS);
//            book.setPublicationType(PublicationType.BOOK);
//
//            // 6.5 Дата створення запису (зараз) та випадкова дата публікації
//            book.setCreatedAt(LocalDateTime.now());
//            book.setPublishedAt(randomPastDate(5));  // випадкова дата за останні 5 років
//
//            // 6.6 Статус — «PUBLISHED»
//            book.setStatus("PUBLISHED");
//
//            // 6.7 Обкладинка та imageType — поки що null
//            book.setCoverImage(null);
//            book.setImageType(null);
//
//            // 6.8 ViewCount та popularRating — випадкові значення
//            int vc = random.nextInt(1000);
//            double pop = 1.0 + random.nextDouble() * 4.0;
//            book.setViewCount(vc);
//            book.setPopularityRating(pop);
//            System.out.println("  🎯 viewCount=" + vc + ", popularityRating=" + String.format("%.2f", pop));
//
//            // 6.9 Ембеддінг опису (якщо опис не порожній)
//            if (!description.isBlank()) {
//                try {
//                    float[] embedding = embeddingService.getEmbedding(description);
//                    byte[] embBytes = ComicsUtil.serializeFloatArray(embedding);
//                    book.setDescriptionEmbedding(embBytes);
//                    System.out.println("    ✅ Ембеддінг успішно згенеровано.");
//                } catch (Exception ex) {
//                    book.setDescriptionEmbedding(null);
//                    System.err.println("    ⚠️ Помилка при ембеддінгу: " + ex.getMessage());
//                }
//            } else {
//                book.setDescriptionEmbedding(null);
//            }
//
//            // 6.10 Порожні списки для зв’язків (жанри, chapters, comments тощо)
//            book.setGenres(new ArrayList<>());
//            book.setChapters(new ArrayList<>());
//            book.setComments(new ArrayList<>());
//            book.setTabs(new ArrayList<>());
//            book.setStatistics(new ArrayList<>());
//            book.setRatings(new ArrayList<>());
//
//            // 6.11 Додаємо до фінального списку
//            resultList.add(book);
//            System.out.println("  ✅ Додано до списку (тепер у списку " + resultList.size() + " книг).");
//        }
//
//        // 7) Зберігаємо всі зібрані об’єкти одним saveAll(...)
//        if (!resultList.isEmpty()) {
//            comicsRepository.saveAll(resultList);
//            System.out.println("\n✅ Успішно збережено " +
//                    resultList.size() +
//                    " українських романів (зі статей Вікіпедії).");
//        } else {
//            System.out.println("\n⚠️ Не знайдено жодного роману для збереження.");
//        }
//    }
//
//    /** Генерує випадкову дату публікації у межах останніх {@code yearsBack} років. */
//    private LocalDate randomPastDate(int yearsBack) {
//        LocalDate now = LocalDate.now();
//        int daysBack = random.nextInt(yearsBack * 365 + 1);
//        return now.minusDays(daysBack);
//    }
//}
