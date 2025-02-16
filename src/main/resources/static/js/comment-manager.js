class CommentManager {
    constructor() {
        if (document.getElementById('comments-section')) {
            this.filtersContainer = document.querySelector('.col-md-3 .list-group');
            this.commentsContainer = document.querySelector('.col-md-9 .list-group.comments-list');
            this.originalComments = this.commentsContainer.innerHTML; // Зберігаємо оригінальну структуру
            this.initFilters();
        }
    }

    initFilters() {
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

        // Відновлюємо оригінальну структуру перед застосуванням фільтрів
        this.commentsContainer.innerHTML = this.originalComments;

        const commentType = document.querySelector('input[name="commentType"]:checked')?.value || 'all';
        const dateSort = document.querySelector('input[name="dateSort"]:checked')?.value || 'newest';
        const searchText = document.getElementById('commentSearch')?.value.toLowerCase() || '';

        const comments = Array.from(this.commentsContainer.querySelectorAll('.list-group-item'));

        comments.forEach(comment => {
            let showComment = true;

            if (commentType !== 'all') {
                const type = comment.getAttribute('data-type');
                if (type !== commentType) {
                    showComment = false;
                }
            }

            if (searchText) {
                const commentText = comment.querySelector('p')?.textContent.toLowerCase() || '';
                const titleText = comment.querySelector('.news-title')?.textContent.toLowerCase() || '';
                if (!commentText.includes(searchText) && !titleText.includes(searchText)) {
                    showComment = false;
                }
            }

            comment.style.display = showComment ? 'block' : 'none';
        });

        if (dateSort) {
            this.sortComments(dateSort);
        }
    }

    sortComments(sortOrder) {
        if (!this.commentsContainer) return;

        const comments = Array.from(this.commentsContainer.querySelectorAll('.list-group-item'));
        const sortedComments = comments.sort((a, b) => {
            const dateA = new Date(a.getAttribute('data-date'));
            const dateB = new Date(b.getAttribute('data-date'));
            return sortOrder === 'newest' ? dateB - dateA : dateA - dateB;
        });

        // Видаляємо всі коментарі
        comments.forEach(comment => comment.remove());

        // Додаємо відсортовані коментарі назад
        sortedComments.forEach(comment => {
            this.commentsContainer.appendChild(comment);
        });
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