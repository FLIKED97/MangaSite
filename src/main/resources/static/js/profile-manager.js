// Основний клас для керування профілем
class ProfileManager {
    constructor() {
        this.contentElement = document.getElementById('content');
        this.personId = document.body.getAttribute('data-person-id');
        this.currentSection = 'bookmarks';
        this.comicsCache = new Map(); // Кешування коміксів для кожної закладки
        this.currentTabId = null; // Поточна активна закладка


        this.init();
    }

    init() {
        this.initEventListeners();
        this.loadSection('bookmarks');
    }

    initEventListeners() {
        // Обробник для кнопок навігації
        document.querySelectorAll('.custom-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const section = e.target.getAttribute('data-section');
                this.loadSection(section);
            });
        });

        // Обробник для модального вікна редагування
        document.addEventListener('click', (e) => {
            if (e.target.closest('#heading-clickable')) {
                this.showEditModal();
            }
        });
    }

    async loadSection(section) {
        try {
            this.showLoader();

            const endpoints = {
                bookmarks: `/tabs/person/${this.personId}`,
                comments: '/comment/show',
                friends: `/friends/${this.personId}`
            };

            const response = await fetch(endpoints[section]);
            if (!response.ok) throw new Error('Network response was not ok');

            const html = await response.text();

            // Якщо це секція коментарів, зберігаємо поточні фільтри
            let currentFilters = null;
            if (section === 'comments') {
                const filtersSection = document.querySelector('.col-md-3');
                if (filtersSection) {
                    currentFilters = filtersSection.cloneNode(true);
                }
            }

            this.contentElement.innerHTML = html;

            // Відновлюємо фільтри, якщо вони були
            if (currentFilters && section === 'comments') {
                const newFiltersSection = document.querySelector('.col-md-3');
                if (newFiltersSection) {
                    newFiltersSection.replaceWith(currentFilters);
                }
            }

            if (section === 'bookmarks') {
                this.initBookmarksHandlers();
            } else if (section === 'comments') {
                // Ініціалізуємо CommentManager після завантаження секції коментарів
                new CommentManager();
            }
        } catch (error) {
            this.showError(`Failed to load ${section}`);
        }
    }

    initBookmarksHandlers() {
        // Тільки обробники для закладок
        const tabItems = document.querySelectorAll('.list-group-item');
        tabItems.forEach(tab => {
            tab.addEventListener('click', async (e) => {
                this.handleTabClick(e.target);
            });
        });

        // Ініціалізуємо пошук і сортування тільки якщо їх ще не було ініціалізовано
        const searchInput = document.getElementById('search-input');
        if (searchInput && !searchInput.hasAttribute('data-initialized')) {
            searchInput.setAttribute('data-initialized', 'true');
            searchInput.addEventListener('input', (e) => this.handleSearch(e.target.value));
        }

        const sortSelect = document.getElementById('sort-comics');
        if (sortSelect && !sortSelect.hasAttribute('data-initialized')) {
            sortSelect.setAttribute('data-initialized', 'true');
            sortSelect.addEventListener('change', (e) => this.handleSort(e.target.value));
        }
    }

    async handleTabClick(tabElement) {
        try {
            this.setActiveTab(tabElement);

            const { id: tabId, personId } = tabElement.dataset;
            this.currentTabId = tabId;

            // Перевіряємо кеш
            if (this.comicsCache.has(tabId)) {
                document.getElementById('comics-container').innerHTML = this.comicsCache.get(tabId);
                return;
            }

            const response = await fetch(`/tabs/person/${personId}/tab/${tabId}`);
            if (!response.ok) throw new Error('Failed to load comics');

            const html = await response.text();
            // Зберігаємо в кеш
            this.comicsCache.set(tabId, html);
            document.getElementById('comics-container').innerHTML = html;

            // Скидаємо поля пошуку та сортування
            document.getElementById('search-input').value = '';
            document.getElementById('sort-comics').value = 'name';
        } catch (error) {
            this.showError('Помилка завантаження коміксів');
        }
    }

    setActiveTab(activeTab) {
        document.querySelectorAll('.list-group-item').forEach(tab => {
            tab.classList.remove('active');
        });
        activeTab.classList.add('active');
    }

    handleSort(sortType) {
        if (!this.currentTabId) return;

        const container = document.getElementById('comics-container');
        const items = Array.from(container.querySelectorAll('.comic-card'));

        items.sort((a, b) => {
            if (sortType === 'name') {
                const titleA = a.querySelector('.comic-title').textContent.toLowerCase();
                const titleB = b.querySelector('.comic-title').textContent.toLowerCase();
                return titleA.localeCompare(titleB);
            } else if (sortType === 'date') {
                // Припускаємо, що дата додавання зберігається в data-date
                const dateA = new Date(a.dataset.date || 0);
                const dateB = new Date(b.dataset.date || 0);
                return dateB - dateA; // Сортуємо від найновіших до найстаріших
            }
            return 0;
        });

        // Очищаємо контейнер і додаємо відсортовані елементи
        const fragment = document.createDocumentFragment();
        items.forEach(item => fragment.appendChild(item));
        container.appendChild(fragment);
    }

    handleSearch(query) {
        console.log("Пошуковий запит:", query);
        if (!this.currentTabId) return;

        const comicsContainer = document.getElementById('comics-container');
        const items = comicsContainer.querySelectorAll('.comic-card');
        console.log("Найдено коміксів:", items.length);

        const normalizedQuery = query.toLowerCase().trim();

        const existingMsg = comicsContainer.querySelector('.no-results-message');
        if (existingMsg) {
            existingMsg.remove();
        }

        let foundMatch = false;
        items.forEach(item => {
            const title = item.querySelector('.comic-title')?.textContent.toLowerCase() || '';
            const description = item.querySelector('.comic-description')?.textContent.toLowerCase() || '';

            const isVisible = title.includes(normalizedQuery) || description.includes(normalizedQuery);
            // Застосовуємо правило з !important
            item.style.setProperty('display', isVisible ? '' : 'none', 'important');

            if (isVisible) {
                foundMatch = true;
            }
            console.log(`Комікс: ${title}, містить запит: ${isVisible}`);
        });

        if (!foundMatch && normalizedQuery !== '') {
            const noResultsMsg = document.createElement('div');
            noResultsMsg.className = 'no-results-message alert alert-info mt-3';
            noResultsMsg.textContent = 'Нічого не знайдено';
            comicsContainer.insertBefore(noResultsMsg, comicsContainer.firstChild);
        }
    }

    showEditModal() {
        const modalEl = document.getElementById('edit-lists-modal');
        if (modalEl) {
            const modal = new bootstrap.Modal(modalEl);
            modal.show();
        }
    }

    showLoader() {
        this.contentElement.innerHTML = '<div class="text-center"><div class="spinner-border" role="status"></div></div>';
    }

    showError(message) {
        this.contentElement.innerHTML = `<div class="alert alert-danger">${message}</div>`;
    }
}

// Ініціалізація після завантаження DOM
document.addEventListener('DOMContentLoaded', () => {
    new ProfileManager();
});