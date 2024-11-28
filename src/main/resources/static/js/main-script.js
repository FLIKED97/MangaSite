let page = 1; // Поточна сторінка
let isLoading = false;
let currentTab = 'bookmarked'; // Встановіть початковий таб

// Завантаження нового контенту при прокручуванні сторінки
window.addEventListener('scroll', () => {
    const scrollHeight = document.documentElement.scrollHeight;
    const scrollTop = document.documentElement.scrollTop;
    const clientHeight = document.documentElement.clientHeight;

    if (!isLoading && scrollHeight - scrollTop <= clientHeight + 50) {
        loadMoreContent();
    }
});

// Завантаження додаткових даних для поточного табу
function loadMoreContent() {
    isLoading = true;
    document.getElementById('loading').style.display = 'block';

    const url = currentTab === 'bookmarked'
        ? `/main/comics/bookmarked?page=${page}`
        : `/main/comics/new?page=${page}`;

    fetch(url)
        .then(response => response.json())
        .then(data => {
            const container = currentTab === 'bookmarked'
                ? document.getElementById('bookmarked-comics-container')
                : document.getElementById('new-comics-container');

            data.forEach(item => {
                const div = document.createElement('div');
                div.className = 'd-flex align-items-center mb-3';

                if (currentTab === 'bookmarked') {
                    div.innerHTML = `
                        <img src="/comics/image/${item.comics.id}"
                             alt="Comic Thumbnail"
                             class="mr-3"
                             style="width: 60px; height: 60px; object-fit: cover;">
                        <div>
                            <h6>${item.comics.title}</h6>
                            <small>Глава ${item.chapterNumber}</small>
                            <p>Додано: ${item.releaseDate}</p>
                        </div>
                    `;
                } else {
                    div.innerHTML = `
                        <img src="/comics/image/${item.id}"
                             alt="Comic Thumbnail"
                             class="mr-3"
                             style="width: 60px; height: 60px; object-fit: cover;">
                        <div>
                            <h6>${item.title}</h6>
                        </div>
                    `;
                }

                container.appendChild(div);
            });

            if (data.length > 0) {
                page++; // Збільшуємо номер сторінки
            }

            isLoading = false;
            document.getElementById('loading').style.display = 'none';
        })
        .catch(error => {
            console.error('Помилка при завантаженні:', error);
            isLoading = false;
            document.getElementById('loading').style.display = 'none';
        });
}

// Обробка подій на кнопках для перемикання табів
document.addEventListener('DOMContentLoaded', () => {
    const newComicsButton = document.getElementById('newComicsButton');
    const bookmarkedComicsButton = document.getElementById('bookmarkedComicsButton');

    const newComicsSection = document.getElementById('new-comics-section');
    const bookmarkedComicsSection = document.getElementById('bookmarked-comics-section');

    newComicsButton.addEventListener('click', () => {
        newComicsSection.classList.remove('d-none');
        bookmarkedComicsSection.classList.add('d-none');
        newComicsButton.classList.add('btn-primary');
        newComicsButton.classList.remove('btn-secondary');
        bookmarkedComicsButton.classList.add('btn-secondary');
        bookmarkedComicsButton.classList.remove('btn-primary');
    });

    bookmarkedComicsButton.addEventListener('click', () => {
        bookmarkedComicsSection.classList.remove('d-none');
        newComicsSection.classList.add('d-none');
        bookmarkedComicsButton.classList.add('btn-primary');
        bookmarkedComicsButton.classList.remove('btn-secondary');
        newComicsButton.classList.add('btn-secondary');
        newComicsButton.classList.remove('btn-primary');
    });
});
// document.addEventListener('DOMContentLoaded', () => {
//     const newComicsButton = document.getElementById('newComicsButton');
//     const bookmarkedComicsButton = document.getElementById('bookmarkedComicsButton');
//
//     const newComicsSection = document.getElementById('new-comics-section');
//     const bookmarkedComicsSection = document.getElementById('bookmarked-comics-section');
//
//     // Перемикання на "Нові комікси"
//     newComicsButton.addEventListener('click', () => {
//         switchTab('new', newComicsSection, bookmarkedComicsSection, newComicsButton, bookmarkedComicsButton);
//     });
//
//     // Перемикання на "Закладки"
//     bookmarkedComicsButton.addEventListener('click', () => {
//         switchTab('bookmarked', bookmarkedComicsSection, newComicsSection, bookmarkedComicsButton, newComicsButton);
//     });
//
//     // Ініціалізація: показуємо закладки як перший таб
//     bookmarkedComicsButton.click();
// });
//
// // Зміна табу
// function switchTab(tab, showSection, hideSection, activeButton, inactiveButton) {
//     currentTab = tab;
//     page = 1; // Скидаємо сторінку при зміні табу
//
//     // Очищення контейнерів
//     const containerId = tab === 'bookmarked' ? 'bookmarked-comics-container' : 'new-comics-container';
//     document.getElementById(containerId).innerHTML = '';
//
//     // Завантаження контенту для обраного табу
//     loadMoreContent();
//
//     // Перемикання видимості секцій
//     showSection.classList.remove('d-none');
//     hideSection.classList.add('d-none');
//
//     // Зміна стилю кнопок
//     activeButton.classList.add('btn-primary');
//     activeButton.classList.remove('btn-secondary');
//     inactiveButton.classList.add('btn-secondary');
//     inactiveButton.classList.remove('btn-primary');
// }
