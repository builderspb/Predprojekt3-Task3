document.addEventListener("DOMContentLoaded", function() {
    fetch('/api/admin/user-info')
        .then(response => response.json())
        .then(data => {
            const tbody = document.querySelector('tbody');
            tbody.innerHTML = '';
            data.forEach(user => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td>${user.id}</td>
                    <td>${user.userName}</td>
                    <td>${user.lastName}</td>
                    <td>${user.phoneNumber}</td>
                    <td>${user.email}</td>
                    <td class="user-actions">
                        <button class="btn btn-primary btn-sm editButton" data-id="${user.id}">Изменить</button>
                        <button class="btn btn-danger btn-sm deleteButton" data-id="${user.id}">Удалить</button>
                    </td>
                `;
                tbody.appendChild(tr);
            });
        });
});