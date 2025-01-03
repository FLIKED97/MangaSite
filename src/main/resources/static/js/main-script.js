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
                div.className = 'd-flex align-items-center chapter-container';

                if (currentTab === 'bookmarked') {
                    const timeDifference = getTimeDifference(item.releaseDate);
                    div.innerHTML = `
                    <hr>
                    <img src="/comics/image/${item.comics.id}"
                         alt="Comic Thumbnail"
                         class=""
                         style="width: 80px; height: 112px; object-fit: cover;">
                    <div style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; background: linear-gradient(to bottom, rgba(0, 0, 0, 0.6), rgba(0, 0, 0, 0) 30%);"></div>
                    <span class="image-label" style="position: absolute; top: 4px; left: 50%; transform: translateX(-50%); color: white; font-size: 12px; padding: 2px 6px;">Манга</span>
                    <div class="z3_c">
                        <h6>${item.comics.title}</h6>
                        <small>Глава ${item.chapterNumber}</small>
                        <p class="release-date z3_ox" th:data-release-date="${item.releaseDate}"></p>
                    </div>
                    <hr>
                `;
                } else {
                    div.innerHTML = `
                    <hr>
                    <img src="/comics/image/${item.comics.id}"
                         alt="Comic Thumbnail"
                         class=""
                         style="width: 80px; height: 112px; object-fit: cover;">
                    <div style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; background: linear-gradient(to bottom, rgba(0, 0, 0, 0.6), rgba(0, 0, 0, 0) 30%);"></div>
                    <span class="image-label" style="position: absolute; top: 4px; left: 50%; transform: translateX(-50%); color: white; font-size: 12px; padding: 2px 6px;">Манга</span>

                    <div class="z3_c">
                        <h6>${item.comics.title}</h6>
                        <small>Глава ${item.chapterNumber}</small>
                            <p class="release-date z3_ox" th:data-release-date="${item.releaseDate}"></p>
                    </div>
                    <hr>
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
    const allUpdatesButton = document.getElementById('allUpdatesButton');
    const myUpdatesButton = document.getElementById('myUpdatesButton');

    const newComicsSection = document.getElementById('new-comics-section');
    const bookmarkedComicsSection = document.getElementById('bookmarked-comics-section');

    // Встановити "Усі оновлення" як активне при завантаженні сторінки
    if (allUpdatesButton && myUpdatesButton && newComicsSection && bookmarkedComicsSection) {
        newComicsSection.classList.remove('d-none');
        bookmarkedComicsSection.classList.add('d-none');
        allUpdatesButton.classList.add('active');
        myUpdatesButton.classList.remove('active');

        allUpdatesButton.addEventListener('click', () => {
            newComicsSection.classList.remove('d-none');
            bookmarkedComicsSection.classList.add('d-none');
            allUpdatesButton.classList.add('active');
            myUpdatesButton.classList.remove('active');
        });

        myUpdatesButton.addEventListener('click', () => {
            bookmarkedComicsSection.classList.remove('d-none');
            newComicsSection.classList.add('d-none');
            myUpdatesButton.classList.add('active');
            allUpdatesButton.classList.remove('active');
        });

        // Запобігти зникненню підкреслення при натисканні на інші місця
        document.addEventListener('click', (event) => {
            if (event.target !== allUpdatesButton && event.target !== myUpdatesButton) {
                if (allUpdatesButton.classList.contains('active')) {
                    allUpdatesButton.classList.add('active');
                } else if (myUpdatesButton.classList.contains('active')) {
                    myUpdatesButton.classList.add('active');
                }
            }
        });
    } else {
        console.error("Some elements could not be found in the DOM.");
    }
});
document.querySelectorAll('.release-date').forEach(element => {
    const releaseDate = new Date(element.getAttribute('data-release-date'));
    const now = new Date();
    const diff = Math.floor((now - releaseDate) / 1000);

    let relativeTime = '';
    if (diff < 60) {
        relativeTime = 'Опубліковано щойно';
    } else if (diff < 3600) {
        relativeTime = `${Math.floor(diff / 60)} хв назад`;
    } else if (diff < 86400) {
        relativeTime = `${Math.floor(diff / 3600)} год назад`;
    } else {
        relativeTime = `${Math.floor(diff / 86400)} днів назад`;
    }

    element.textContent = relativeTime;
});
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
            console.log('CreatedAt:', item.createdAt);
            console.log('Data object:', item);
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
                            <small>${new Intl.DateTimeFormat('uk-UA').format(new Date(item.createdAt))}</small>
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
        <img src="/avatarPerson.webp" alt="Аватарка" class="mr-3 rounded-circle" style="width: 40px; height: 40px;">
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
        comicDiv.className = 'd-flex align-items-center tg_sg two-line-title';
        comicDiv.innerHTML = `
            <img src="/comics/image/${comic.id}" alt="Comic Cover" class="images rounded me-3" style="width: 60px; height: 90px;">
            <div>
                <div class="mb-1 tg_si">${comic.title}</div>
                <span class="mb-0 small ts_f4">Манга</span>
            </div>
        `;
        container.appendChild(comicDiv);
    });
}

