const errorCodes = {
    L001: 'Username is required.',
    L002: 'Incorrect email/username or password. Please try again.',
    L003: 'You have been logged out. Sign in to continue.',
    L004: 'Your session has expired. Please log in again.',
    L005: 'Account created successfully! You can now log in.',
    L006: 'Login failed. Please try again.'
};

function getQueryParameter(name) {
    const params = new URLSearchParams(window.location.search);
    return params.get(name);
}

// Mirrors register's showError exactly — same element ID, same logic
function showError(code) {
    const message = errorCodes[code] || 'An unknown error occurred.';
    const errorElement = document.getElementById('errorMessage');
    errorElement.textContent = `${code}: ${message}`;
    errorElement.hidden = false;
    errorElement.classList.add('active');
}

// Separate function for non-error info messages (logout, registration success)
// Uses a softer style — reuses the same element but with a different class
function showInfo(code) {
    const message = errorCodes[code] || 'An unknown error occurred.';
    const errorElement = document.getElementById('errorMessage');
    errorElement.textContent = message; // No code prefix for info messages
    errorElement.hidden = false;
    errorElement.classList.add('info'); // You can style .error-message.info in CSS
}

document.addEventListener('DOMContentLoaded', () => {

    // Check for server-sent error codes (from Spring Security failureUrl)
    const serverError = getQueryParameter('error');
    if (serverError) {
        showError(serverError);
    }

    // Check for info messages (logout, successful registration redirect)
    const msg = getQueryParameter('msg');
    if (msg) {
        showInfo(msg);
    }

    // Client-side validation — runs before form submits
    document.getElementById('loginForm').addEventListener('submit', (event) => {
        const username = document.querySelector('input[name="username"]').value.trim();

        if (!username) {
            event.preventDefault();
            showError('L001');
            return;
        }
        // Password emptiness is handled by the `required` attribute + browser
        // Spring Security handles wrong credentials server-side → redirects to ?error=L002
    });
});