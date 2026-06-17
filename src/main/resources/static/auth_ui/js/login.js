const statusBanner = document.getElementById('statusBanner');
const params = new URLSearchParams(window.location.search);

function showStatus(text, type) {
    if (!statusBanner) return;
    statusBanner.textContent = text;
    // Ensure any previous classes are cleared before applying new type
    statusBanner.className = `status-banner ${type}`;
}

// params.has() returns true whether the value is '', 'true', or anything else
// so ?error, ?error=true, and ?error=1 all work
if (params.has('error')) {
    showStatus('Login failed. Please check your email/username and password.', 'error');
} else if (params.has('registered')) {
    showStatus('Account created! You can now log in.', 'success');
} else if (params.has('logout')) {
    showStatus('You have been logged out. Sign in to continue.', 'info');
}