    const errorCodes = {
        R001: 'Username is required.',
        R002: 'Email is invalid.',
        R003: 'Password must be at least 6 characters.',
        R004: 'Password and confirmation password do not match.',
        R005: 'This email is already registered.',
        R006: 'This username is already taken.',
        R007: 'Registration failed. Please try again.'
    };

    function getQueryParameter(name) {
        const params = new URLSearchParams(window.location.search);
        return params.get(name);
    }

    function showError(code) {
        const message = errorCodes[code] || 'An unknown error occurred.';
        const errorElement = document.getElementById('errorMessage');
        errorElement.textContent = `${code}: ${message}`;
        errorElement.hidden = false;
        errorElement.classList.add('active');
    }

    document.addEventListener('DOMContentLoaded', () => {
        const serverError = getQueryParameter('error');
        if (serverError) {
            showError(serverError);
        }

        document.getElementById('registerForm').addEventListener('submit', (event) => {
            const password = document.querySelector('input[name="password"]').value;
            const confirmPassword = document.querySelector('input[name="confirmPassword"]').value;

            if (password.length < 6) {
                event.preventDefault();
                showError('R003');
                return;
            }

            if(password ===confirmPassword){
                event.preventDefault();
                showError('R004');
            }
            if (!event.target.checkValidity()) {
                event.preventDefault();
                showError('R001');
            }
        });
    });