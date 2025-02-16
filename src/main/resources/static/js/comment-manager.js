class CommentManager {
    constructor() {
        // Перевіряємо, чи ми на сторінці коментарів
        if (document.getElementById('comments-section')) {
            this.filtersContainer = document.querySelector('.col-md-3 .list-group');
            this.commentsContainer = document.querySelector('.col-md-9 .list-group');
            this.initFilters();
        }
    }

    initFilters() {
        // Отримуємо всі radio buttons (і для типу коментарів, і для сортування)
        const typeRadioButtons = document.querySelectorAll('input[name="commentType"]');
        const sortRadioButtons = document.querySelectorAll('input[name="dateSort"]');
        const searchInput = document.getElementById('commentSearch');

        if (typeRadioButtons) {
            typeRadioButtons.forEach(radio => {
                radio.addEventListener('change', () => this.applyFilters());
            });
        }

        if (sortRadioButtons) {
            sortRadioButtons.forEach(radio => {
                radio.addEventListener('change', () => this.applyFilters());
            });
        }

        if (searchInput) {
            searchInput.addEventListener('input', this.debounce(() => this.applyFilters(), 300));
        }
    }

    applyFilters() {
        if (!this.commentsContainer) {
            console.error('Comments container not found');
            return;
        }
        const commentType = document.querySelector('input[name="commentType"]:checked')?.value || 'all';
        const dateSort = document.querySelector('input[name="dateSort"]:checked')?.value || 'newest';
        const searchText = document.getElementById('commentSearch')?.value.toLowerCase() || '';

        // Важливо: тепер шукаємо коментарі тільки в контейнері коментарів
        const comments = this.commentsContainer.querySelectorAll('.list-group-item');

        comments.forEach(comment => {
            let showComment = true;

            // Фільтр за типом
            if (commentType !== 'all') {
                const type = comment.getAttribute('data-type');
                if (type !== commentType) {
                    showComment = false;
                }
            }

            // Фільтр за текстом
            if (searchText) {
                const commentText = comment.querySelector('p')?.textContent.toLowerCase() || '';
                const titleText = comment.querySelector('a')?.textContent.toLowerCase() || '';
                if (!commentText.includes(searchText) && !titleText.includes(searchText)) {
                    showComment = false;
                }
            }

            comment.style.display = showComment ? '' : 'none';
        });

        // Сортування за датою
        this.sortComments(dateSort);
    }

    sortComments(sortOrder) {
        if (!this.commentsContainer) return;

        const comments = Array.from(this.commentsContainer.children);

        comments.sort((a, b) => {
            const dateA = new Date(a.getAttribute('data-date'));
            const dateB = new Date(b.getAttribute('data-date'));
            return sortOrder === 'newest' ? dateB - dateA : dateA - dateB;
        });

        // Очищаємо і додаємо відсортовані коментарі
        comments.forEach(comment => this.commentsContainer.appendChild(comment));
    }

    debounce(func, wait) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    }
}