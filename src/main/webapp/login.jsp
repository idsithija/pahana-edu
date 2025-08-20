<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Pahana Edu Bookshop</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
</head>
<body class="login-body">
<div class="login-container">
    <div class="login-card">
        <div class="login-header">
            <div class="logo">
                <i class="fas fa-book-open"></i>
                <h1>Pahana Edu Bookshop</h1>
            </div>
            <p class="subtitle">Management System</p>
        </div>

        <div class="login-form-container">
            <h2>Welcome Back</h2>
            <p class="login-description">Please sign in to your account</p>

            <!-- Display error messages -->
            <c:if test="${not empty param.error}">
                <div class="alert alert-error">
                    <i class="fas fa-exclamation-circle"></i>
                    <c:choose>
                        <c:when test="${param.error == 'session_expired'}">
                            Your session has expired. Please log in again.
                        </c:when>
                        <c:when test="${param.error == 'account_disabled'}">
                            Your account has been disabled. Please contact administrator.
                        </c:when>
                        <c:otherwise>
                            Invalid username or password.
                        </c:otherwise>
                    </c:choose>
                </div>
            </c:if>

            <!-- Display success messages -->
            <c:if test="${not empty param.message}">
                <div class="alert alert-success">
                    <i class="fas fa-check-circle"></i>
                    <c:choose>
                        <c:when test="${param.message == 'logged_out'}">
                            You have been successfully logged out.
                        </c:when>
                        <c:otherwise>
                            ${param.message}
                        </c:otherwise>
                    </c:choose>
                </div>
            </c:if>

            <!-- Display custom error message -->
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-error">
                    <i class="fas fa-exclamation-circle"></i>
                        ${errorMessage}
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/login" method="post" class="login-form">
                <div class="form-group">
                    <label for="username">
                        <i class="fas fa-user"></i>
                        Username
                    </label>
                    <input type="text"
                           id="username"
                           name="username"
                           value="${username}"
                           required
                           autocomplete="username"
                           placeholder="Enter your username">
                </div>

                <div class="form-group">
                    <label for="password">
                        <i class="fas fa-lock"></i>
                        Password
                    </label>
                    <div class="password-input-container">
                        <input type="password"
                               id="password"
                               name="password"
                               required
                               autocomplete="current-password"
                               placeholder="Enter your password">
                        <button type="button" class="password-toggle" onclick="togglePassword()">
                            <i class="fas fa-eye" id="password-toggle-icon"></i>
                        </button>
                    </div>
                </div>

                <button type="submit" class="login-btn">
                    <i class="fas fa-sign-in-alt"></i>
                    Sign In
                </button>
            </form>

            <div class="login-footer">
                <p class="demo-info">
                    <i class="fas fa-info-circle"></i>
                    Demo Credentials: admin/admin123 or operator/operator123
                </p>
            </div>
        </div>
    </div>
</div>

<script>
    function togglePassword() {
        const passwordInput = document.getElementById('password');
        const toggleIcon = document.getElementById('password-toggle-icon');

        if (passwordInput.type === 'password') {
            passwordInput.type = 'text';
            toggleIcon.classList.remove('fa-eye');
            toggleIcon.classList.add('fa-eye-slash');
        } else {
            passwordInput.type = 'password';
            toggleIcon.classList.remove('fa-eye-slash');
            toggleIcon.classList.add('fa-eye');
        }
    }

    // Auto-hide alerts after 5 seconds
    document.addEventListener('DOMContentLoaded', function () {
        const alerts = document.querySelectorAll('.alert');
        alerts.forEach(alert => {
            setTimeout(() => {
                alert.style.opacity = '0';
                setTimeout(() => {
                    alert.remove();
                }, 300);
            }, 5000);
        });

        // Focus on username field
        document.getElementById('username').focus();
    });

    // Form validation
    document.querySelector('.login-form').addEventListener('submit', function (e) {
        const username = document.getElementById('username').value.trim();
        const password = document.getElementById('password').value;

        if (!username || !password) {
            e.preventDefault();
            alert('Please enter both username and password.');
            return false;
        }
    });
</script>
</body>
</html>

