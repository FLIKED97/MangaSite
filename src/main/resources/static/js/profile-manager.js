class ProfileManager {
    constructor() {
        this.contentElement = document.getElementById('content');
        this.personId = document.body.getAttribute('data-person-id');
        this.currentSection = 'bookmarks';
        this.comicsCache = new Map();
        this.currentTabId = null;
        this.init();
    }

    init() {
        this.initEventListeners();
        this.loadSection('bookmarks');
    }

    initEventListeners() {
        document.querySelectorAll('.custom-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                // Видаляємо активний клас у всіх кнопок
                document.querySelectorAll('.custom-btn').forEach(button => {
                    button.classList.remove('active');
                });
                // Додаємо активний клас натиснутій кнопці
                e.target.classList.add('active');

                const section = e.target.getAttribute('data-section');
                this.loadSection(section);
            });
        });

        document.addEventListener('click', (e) => {
            if (e.target.closest('#heading-clickable')) {
                this.showEditModal();
            }
        });
    }

    async loadSection(section) {
        try {
            this.showLoader();
            this.currentSection = section;

            const endpoints = {
                bookmarks: `/tabs/person/${this.personId}`,
                comments: '/comment/show',
                friends: `/friends/${this.personId}`,
                statistics: '/profile/combined-statistics'
            };

            const response = await fetch(endpoints[section]);
            if (!response.ok) throw new Error(`Network response was not ok: ${response.status}`);

            const html = await response.text();
            this.contentElement.innerHTML = html;

            if (section === 'bookmarks') {
                this.initBookmarksHandlers();
            } else if (section === 'comments') {
                new CommentManager();
            } else if (section === 'friends') {
                this.initFriendsHandlers();
            } else if (section === 'statistics') {
                // Wait for the DOM to fully render
                setTimeout(() => this.initStatisticsHandlers(), 100);
            }
        } catch (error) {
            console.error(`Error loading ${section}:`, error);
            this.showError(`Failed to load ${section}: ${error.message}`);
        }
    }

    async initStatisticsHandlers() {
        console.log("Initializing statistics charts");

        const weeklyChartElement = document.getElementById('weeklyChart');
        const monthlyChartElement = document.getElementById('monthlyChart');

        if (!weeklyChartElement || !monthlyChartElement) {
            console.warn("Chart elements not found");
            return;
        }

        if (typeof Chart === 'undefined') {
            console.error("Chart.js is not loaded");
            return;
        }

        try {
            // Fetch statistics data
            const response = await fetch(`/profile/combined-statistics/data`);
            if (!response.ok) throw new Error(`Failed to load statistics data: ${response.status}`);

            const data = await response.json();
            console.log("Stats data loaded:", data);

            // Initialize weekly chart
            const weeklyLabels = Object.keys(data.weeklyStats);
            const weeklyValues = Object.values(data.weeklyStats);

            new Chart(weeklyChartElement, {
                type: 'bar',
                data: {
                    labels: weeklyLabels.map(week => {
                        const [year, weekNum] = week.split('-');
                        return `Тиждень ${weekNum}`;
                    }),
                    datasets: [{
                        label: 'Прочитані глави',
                        data: weeklyValues,
                        backgroundColor: 'rgba(54, 162, 235, 0.5)',
                        borderColor: 'rgba(54, 162, 235, 1)',
                        borderWidth: 1
                    }]
                },
                options: {
                    responsive: true,
                    scales: {
                        y: {
                            beginAtZero: true,
                            ticks: { precision: 0 }
                        }
                    }
                }
            });

            // Initialize monthly chart
            const monthlyLabels = Object.keys(data.monthlyStats);
            const monthlyValues = Object.values(data.monthlyStats);
            const monthNames = [
                'Січень', 'Лютий', 'Березень', 'Квітень', 'Травень', 'Червень',
                'Липень', 'Серпень', 'Вересень', 'Жовтень', 'Листопад', 'Грудень'
            ];

            new Chart(monthlyChartElement, {
                type: 'line',
                data: {
                    labels: monthlyLabels.map(month => {
                        const [year, monthNum] = month.split('-');
                        return `${monthNames[parseInt(monthNum) - 1]}`;
                    }),
                    datasets: [{
                        label: 'Прочитані глави',
                        data: monthlyValues,
                        fill: true,
                        backgroundColor: 'rgba(75, 192, 192, 0.2)',
                        borderColor: 'rgba(75, 192, 192, 1)',
                        borderWidth: 2,
                        tension: 0.1
                    }]
                },
                options: {
                    responsive: true,
                    scales: {
                        y: {
                            beginAtZero: true,
                            ticks: { precision: 0 }
                        }
                    }
                }
            });

            console.log("Charts initialized successfully");
        } catch (error) {
            console.error("Error initializing charts:", error);
        }
    }

    initChart(element, type, datasetConfig) {
        try {
            // Отримуємо дані з data-атрибутів або через inline JavaScript
            let labels = [];
            let data = [];

            // Перевіряємо наявність Chart
            if (typeof Chart === 'undefined') {
                console.error("Chart.js is not loaded");
                return null;
            }

            // Створюємо базову конфігурацію графіка
            const config = {
                type: type,
                data: {
                    labels: labels,
                    datasets: [{
                        label: datasetConfig.label,
                        data: data,
                        backgroundColor: datasetConfig.backgroundColor,
                        borderColor: datasetConfig.borderColor,
                        borderWidth: 1,
                        ...datasetConfig
                    }]
                },
                options: {
                    responsive: true,
                    scales: {
                        y: {
                            beginAtZero: true,
                            ticks: { precision: 0 }
                        }
                    }
                }
            };

            // Створюємо новий графік
            return new Chart(element, config);
        } catch (error) {
            console.error("Error initializing chart:", error);
            return null;
        }
    }

    initFriendsHandlers() {
        const tabButtons = document.querySelectorAll('.tab-button');
        const tabContents = document.querySelectorAll('.tab-content');

        // Перевіряємо наявність контенту в кожній вкладці
        tabContents.forEach(content => {
            const list = content.querySelector('ul');
            if (list && list.children.length === 0) {
                list.innerHTML = '<li class="empty-message">У вас тут пусто...</li>';
            }
        });

        tabButtons.forEach(button => {
            button.addEventListener('click', () => {
                const tabId = button.getAttribute('data-tab');

                // Оновлюємо активну кнопку
                tabButtons.forEach(btn => btn.classList.remove('active'));
                button.classList.add('active');

                // Оновлюємо активний контент
                tabContents.forEach(content => {
                    if (content.id === tabId) {
                        content.classList.add('active');
                    } else {
                        content.classList.remove('active');
                    }
                });
            });
        });
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
        container.innerHTML = '';
        items.forEach(item => container.appendChild(item));
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
        this.contentElement.innerHTML = '<div class="text-center p-5"><div class="spinner-border text-primary" role="status"><span class="visually-hidden">Завантаження...</span></div></div>';
    }

    showError(message) {
        this.contentElement.innerHTML = `
            <div class="alert alert-danger my-3">
                <i class="fas fa-exclamation-triangle me-2"></i>
                ${message}
                <button class="btn btn-sm btn-outline-danger ms-3" onclick="location.reload()">Спробувати знову</button>
            </div>`;
    }
}

// Ініціалізація після завантаження DOM
document.addEventListener('DOMContentLoaded', () => {
    new ProfileManager();
});