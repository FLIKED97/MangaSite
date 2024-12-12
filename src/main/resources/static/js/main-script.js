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
//TODO Доробити нижні функції більш оптимізовано
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
//     console.log(`Перемикаємось на таб: ${tab}`);
//     currentTab = tab;
//     page = 1;
//
//     // Очищення контейнера
//     const containerId = tab === 'bookmarked' ? 'bookmarked-comics-container' : 'new-comics-container';
//     const container = document.getElementById(containerId);
//
//     if (!container) {
//         console.error(`Контейнер ${containerId} не знайдено`);
//         return;
//     }
//     container.innerHTML = '';
//
//     // Завантаження контенту
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
document.addEventListener('DOMContentLoaded', function() {
    // Функція для очищення та виведення результатів
    function displayResults(resultsElement, data, type) {
        resultsElement.innerHTML = ''; // Очистити попередні результати

        if (data.length === 0) {
            resultsElement.innerHTML = '<li class="list-group-item text-muted">Нічого не знайдено</li>';
            return;
        }

        data.forEach(item => {
            const li = document.createElement('li');
            li.className = 'list-group-item bg-secondary text-light d-flex align-items-center';

            switch(type) {
                case 'comics':
                    const imageUrl = item.coverImageBase64
                        ? `data:${item.imageType};base64,${item.coverImageBase64}`
                        : '/default-comic-image.png';

                    li.innerHTML = `
                        <img src="${imageUrl}"
                             alt="${item.title}"
                             class="mr-3 img-thumbnail"
                             style="width: 60px; height: 60px; object-fit: cover;">
                        <div>
                            <h5>${item.title}</h5>
                            <small>${new Date(item.createdAt).toLocaleDateString()}</small>
                        </div>
                    `;
                    break;

                case 'authors':
                    li.innerHTML = `
                        <div>
                            <h5>${item.name}</h5>
                        </div>
                    `;
                    break;

                case 'users':
                    li.innerHTML = `
        <img src="/default-avatar.png" alt="Аватарка" class="mr-3 rounded-circle" style="width: 40px; height: 40px;">
        <div>
            <a href="/profile/${item.id}" class="text-light">
                <h5>${item.username}</h5>
            </a>
            <small>${item.email}</small>
        </div>
    `;
                    break;

                case 'groups':
                    li.innerHTML = `
                        <div>
                            <h5>${item.name}</h5>
                            <small>Учасників: ${item.membersCount || 0}</small>
                        </div>
                    `;
                    break;
            }

            resultsElement.appendChild(li);
        });
    }

    // Функція debounce для зменшення кількості запитів
    function debounce(func, delay) {
        let timeoutId;
        return function() {
            const context = this;
            const args = arguments;
            clearTimeout(timeoutId);
            timeoutId = setTimeout(() => func.apply(context, args), delay);
        };
    }

    // Додавання пошуку для кожної вкладки
    const searchTypes = ['comics', 'authors', 'users', 'groups'];

    searchTypes.forEach(type => {
        const searchInput = document.getElementById(`${type}SearchInput`);
        const resultsElement = document.getElementById(`${type}Results`);

        if (searchInput && resultsElement) {
            searchInput.addEventListener('input', debounce(function() {
                const term = this.value;

                // Перевірка мінімальної довжини терміну пошуку
                if (term.length < 2) {
                    resultsElement.innerHTML = '';
                    return;
                }

                fetch(`/search/${type}?term=${encodeURIComponent(term)}`)
                    .then(response => {
                        if (!response.ok) {
                            throw new Error('Network response was not ok');
                        }
                        return response.json();
                    })
                    .then(data => {
                        displayResults(resultsElement, data, type);
                    })
                    .catch(error => {
                        console.error(`Помилка завантаження результатів ${type}:`, error);
                        resultsElement.innerHTML = '<li class="list-group-item text-danger">Помилка пошуку</li>';
                    });
            }, 300)); // затримка 300 мс
        }
    });
});

function changeDays(days) {
    fetch(`/main/data?days=${days}`)
        .then(response => response.json())
        .then(data => {
            updateComicsSection('newCreatedComics', data.newCreatedComics);
            updateComicsSection('currentlyPopularReading', data.currentlyPopularReading);
            updateComicsSection('popularComics', data.popularComics);
        })
        .catch(error => console.error('Error fetching data:', error));
}

function updateComicsSection(sectionId, comics) {
    const container = document.getElementById(sectionId);
    container.innerHTML = ''; // Очищуємо старі дані

    comics.forEach(comic => {
        const comicDiv = document.createElement('div');
        comicDiv.className = 'd-flex align-items-center mb-3';
        comicDiv.innerHTML = `
            <img src="/comics/image/${comic.id}" alt="Comic Cover" class="img-fluid rounded me-3" style="width: 60px; height: 90px;">
            <div>
                <h6 class="mb-1">${comic.title}</h6>
                <p class="mb-0 small">Манга</p>
            </div>
        `;
        container.appendChild(comicDiv);
    });
}

