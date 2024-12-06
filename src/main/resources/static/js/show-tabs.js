document.addEventListener("DOMContentLoaded", function () {
    // Завантаження коміксів при натисканні на закладку
    document.querySelectorAll(".list-group-item").forEach(tab => {
        tab.addEventListener("click", function () {
            const tabId = this.dataset.id;
            const personId = this.dataset.personId;

            $.ajax({
                url: `/tabs/person/${personId}/tab/${tabId}`,
                method: "GET",
                success: function (response) {
                    // Замінюємо контент центральної панелі
                    document.getElementById("comics-container").innerHTML = response;
                },
                error: function () {
                    alert("Помилка завантаження коміксів.");
                }
            });
        });
    });

    // Сортування коміксів
    document.getElementById("sort-comics").addEventListener("change", function () {
        const sortType = this.value;
        const comicsContainer = document.getElementById("comics-container");
        const comics = Array.from(comicsContainer.children);

        comics.sort((a, b) => {
            if (sortType === "name") {
                return a.textContent.localeCompare(b.textContent);
            } else if (sortType === "date") {
                return new Date(a.dataset.date) - new Date(b.dataset.date);
            }
        });

        comics.forEach(comic => comicsContainer.appendChild(comic));
    });

    // Відкрити модальне вікно
    document.getElementById("edit-lists-btn").addEventListener("click", function () {
        const modal = new bootstrap.Modal(document.getElementById("edit-lists-modal"));
        modal.show();
    });
});