document.addEventListener("DOMContentLoaded", () => {
    const chapterId = document.querySelector('meta[name="chapter-id"]').getAttribute("content");
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute("content");
    let lastPage = parseInt(document.querySelector('meta[name="last-page"]').getAttribute("content"), 10) || 0;

    let currentPage = 0;
    const pageSize = 5;
    let isLoading = false;
    let hasMorePages = true;
    let shouldScrollToLastPage = lastPage > 0;

    const loadingSpinner = document.getElementById('loading-spinner');
    const container = document.getElementById('comicPages-container');

    async function loadPages() {
        if (isLoading || !hasMorePages) return;

        isLoading = true;
        loadingSpinner.style.display = 'block';

        try {
            const response = await fetch(`/chapters/${chapterId}/pages?page=${currentPage}&size=${pageSize}`);
            const pages = await response.json();

            if (pages.length === 0) {
                hasMorePages = false;
                loadingSpinner.style.display = 'none';
                return;
            }

            pages.forEach((page, index) => {
                const pageContainer = document.createElement('div');
                // pageContainer.className = 'mb-3';
                pageContainer.setAttribute('data-page-number', currentPage * pageSize + index + 1);

                const img = document.createElement('img');
                img.src = page.imagePath;
                img.alt = `Сторінка ${page.pageNumber}`;
                img.className = 'comic-page';
                img.loading = 'lazy';

                pageContainer.appendChild(img);
                container.appendChild(pageContainer);

                // Якщо це остання завантажена сторінка і нам потрібно прокрутити до lastPage
                if (shouldScrollToLastPage && currentPage * pageSize + index + 1 === lastPage) {
                    img.onload = () => {
                        setTimeout(() => {
                            pageContainer.scrollIntoView({ behavior: 'auto' });
                            shouldScrollToLastPage = false;
                        }, 100);
                    };
                }
            });

            if (pages.length < pageSize) {
                hasMorePages = false;
            }

            currentPage++;

            // Якщо ми ще не досягли lastPage, завантажуємо наступну порцію сторінок
            if (shouldScrollToLastPage && currentPage * pageSize < lastPage) {
                loadPages();
            }
        } catch (error) {
            console.error('Error loading pages:', error);
        } finally {
            isLoading = false;
            loadingSpinner.style.display = 'none';
        }
    }

    function updateProgress() {
        const images = document.querySelectorAll('.comic-page');
        const viewportHeight = window.innerHeight;
        const scrollTop = window.scrollY;

        let lastVisiblePage = 0;

        images.forEach((img) => {
            const container = img.parentElement;
            const pageNumber = parseInt(container.getAttribute('data-page-number'));
            const imgTop = img.offsetTop;
            const imgBottom = imgTop + img.offsetHeight;

            if (scrollTop + viewportHeight / 2 >= imgTop && scrollTop + viewportHeight / 2 <= imgBottom) {
                lastVisiblePage = pageNumber;
            }
        });

        if (lastVisiblePage > 0) {
            fetch(`/chapters/${chapterId}/progress?lastPage=${lastVisiblePage}`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "X-CSRF-TOKEN": csrfToken,
                },
                credentials: "include",
            }).catch((error) => console.error("Error updating progress:", error));
        }
    }

    function handleScroll() {
        if (window.innerHeight + window.scrollY >= document.body.offsetHeight - 500) {
            loadPages();
        }
    }

    let scrollTimeout;
    window.addEventListener('scroll', () => {
        if (scrollTimeout) clearTimeout(scrollTimeout);
        scrollTimeout = setTimeout(() => {
            handleScroll();
            updateProgress();
        }, 100);
    });

    loadPages();
});