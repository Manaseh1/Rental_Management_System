const statusBanner = document.getElementById('statusBanner');
const params = new URLSearchParams(window.location.search);

function showStatus(text, type) {
    if (!statusBanner) return;
    statusBanner.textContent = text;
    statusBanner.className = `status-banner ${type}`;
}

if (params.has('error')) {
    showStatus('Login failed. Check your email and password.', 'error');
} else if (params.has('registered')) {
    showStatus('Registration successful! Please log in.', 'success');
} else if (params.has('logout')) {
    showStatus('You are logged out. Sign in again to continue.', 'info');
}
