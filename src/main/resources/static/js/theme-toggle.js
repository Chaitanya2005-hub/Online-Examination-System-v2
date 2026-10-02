// Theme toggle functionality
function toggleTheme() {
    const body = document.body;
    const isDark = body.classList.contains('dark-mode');

    if (isDark) {
        body.classList.remove('dark-mode');
        const toggleBtn = document.getElementById('themeToggle');
        if (toggleBtn) toggleBtn.textContent = '🌙';
        localStorage.setItem('theme', 'light');
    } else {
        body.classList.add('dark-mode');
        const toggleBtn = document.getElementById('themeToggle');
        if (toggleBtn) toggleBtn.textContent = '☀️';
        localStorage.setItem('theme', 'dark');
    }
}

// Load saved theme on page load
document.addEventListener('DOMContentLoaded', function() {
    const savedTheme = localStorage.getItem('theme');
    if (savedTheme === 'dark') {
        document.body.classList.add('dark-mode');
        const toggleBtn = document.getElementById('themeToggle');
        if (toggleBtn) toggleBtn.textContent = '☀️';
    }
});
