// Immediate execution to prevent flash of light mode during page load
(function applyThemeImmediately() {
    try {
        var savedTheme = localStorage.getItem('theme');
        if (savedTheme === 'dark') {
            document.documentElement.classList.add('dark-mode');
            if (document.body) {
                document.body.classList.add('dark-mode');
            }
        } else if (savedTheme === 'light') {
            document.documentElement.classList.remove('dark-mode');
            if (document.body) {
                document.body.classList.remove('dark-mode');
            }
        }
    } catch (e) {
        console.error('Theme initialization error:', e);
    }
})();

function toggleTheme() {
    const isDark = document.documentElement.classList.contains('dark-mode') || (document.body && document.body.classList.contains('dark-mode'));

    if (isDark) {
        document.documentElement.classList.remove('dark-mode');
        if (document.body) document.body.classList.remove('dark-mode');
        localStorage.setItem('theme', 'light');
        updateToggleButtons('🌙');
    } else {
        document.documentElement.classList.add('dark-mode');
        if (document.body) document.body.classList.add('dark-mode');
        localStorage.setItem('theme', 'dark');
        updateToggleButtons('☀️');
    }
}

function updateToggleButtons(icon) {
    const toggleBtns = document.querySelectorAll('#themeToggle, .theme-toggle-btn');
    toggleBtns.forEach(function(btn) {
        btn.textContent = icon;
    });
}

function syncThemeState() {
    const savedTheme = localStorage.getItem('theme');
    const isDark = savedTheme === 'dark';
    
    if (isDark) {
        document.documentElement.classList.add('dark-mode');
        if (document.body) document.body.classList.add('dark-mode');
        updateToggleButtons('☀️');
    } else {
        document.documentElement.classList.remove('dark-mode');
        if (document.body) document.body.classList.remove('dark-mode');
        updateToggleButtons('🌙');
    }
}

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', syncThemeState);
} else {
    syncThemeState();
}
