<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<footer class="footer">
    <div class="footer-container">
        <div class="footer-content">
            <div class="footer-section">
                <div class="footer-brand">
                    <i class="fas fa-book-open"></i>
                    <span>Pahana Edu Bookshop</span>
                </div>
                <p class="footer-description">
                    Comprehensive bookshop management system for educational institutions.
                    Streamline your inventory, customer management, and billing processes.
                </p>
            </div>
            
            <div class="footer-section">
                <h4>Quick Links</h4>
                <ul class="footer-links">
                    <li><a href="${pageContext.request.contextPath}/dashboard">Dashboard</a></li>
                    <li><a href="${pageContext.request.contextPath}/customer/manage">Customers</a></li>
                    <li><a href="${pageContext.request.contextPath}/item/manage">Inventory</a></li>
                    <li><a href="${pageContext.request.contextPath}/billing/manage">Billing</a></li>
                </ul>
            </div>
            
            <div class="footer-section">
                <h4>Support</h4>
                <ul class="footer-links">
                    <li><a href="${pageContext.request.contextPath}/help">Help Center</a></li>
                    <li><a href="${pageContext.request.contextPath}/documentation">Documentation</a></li>
                    <li><a href="${pageContext.request.contextPath}/contact">Contact Support</a></li>
                    <li><a href="${pageContext.request.contextPath}/feedback">Feedback</a></li>
                </ul>
            </div>
            
            <div class="footer-section">
                <h4>System Info</h4>
                <div class="system-info">
                    <div class="info-item">
                        <i class="fas fa-user"></i>
                        <span>Logged in as: ${currentUser.username}</span>
                    </div>
                    <div class="info-item">
                        <i class="fas fa-shield-alt"></i>
                        <span>Role: ${userRole}</span>
                    </div>
                    <div class="info-item">
                        <i class="fas fa-clock"></i>
                        <span id="footerTime"></span>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="footer-bottom">
            <div class="footer-bottom-content">
                <div class="copyright">
                    <p>&copy; 2025 Pahana Edu Bookshop Management System. All rights reserved.</p>
                </div>
                <div class="footer-meta">
                    <span class="version">Version 1.0.0</span>
                    <span class="separator">•</span>
                    <span class="build">Build: ${pageContext.servletContext.getAttribute('buildNumber') != null ? pageContext.servletContext.getAttribute('buildNumber') : 'DEV'}</span>
                </div>
            </div>
        </div>
    </div>
</footer>

<style>
.footer {
    background: var(--gray-900);
    color: var(--gray-300);
    margin-top: var(--spacing-16);
    border-top: 1px solid var(--gray-800);
}

.footer-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 var(--spacing-4);
}

.footer-content {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: var(--spacing-8);
    padding: var(--spacing-12) 0 var(--spacing-8);
}

.footer-section h4 {
    color: var(--white);
    font-size: var(--font-size-lg);
    font-weight: 600;
    margin-bottom: var(--spacing-4);
}

.footer-brand {
    display: flex;
    align-items: center;
    gap: var(--spacing-3);
    font-size: var(--font-size-xl);
    font-weight: 700;
    color: var(--primary-color);
    margin-bottom: var(--spacing-4);
}

.footer-brand i {
    font-size: var(--font-size-2xl);
}

.footer-description {
    color: var(--gray-400);
    line-height: 1.6;
    margin-bottom: 0;
}

.footer-links {
    list-style: none;
    padding: 0;
    margin: 0;
}

.footer-links li {
    margin-bottom: var(--spacing-2);
}

.footer-links a {
    color: var(--gray-400);
    text-decoration: none;
    transition: color var(--transition-fast);
    font-size: var(--font-size-sm);
}

.footer-links a:hover {
    color: var(--primary-color);
}

.system-info {
    display: flex;
    flex-direction: column;
    gap: var(--spacing-2);
}

.info-item {
    display: flex;
    align-items: center;
    gap: var(--spacing-2);
    font-size: var(--font-size-sm);
    color: var(--gray-400);
}

.info-item i {
    color: var(--primary-color);
    width: 16px;
}

.footer-bottom {
    border-top: 1px solid var(--gray-800);
    padding: var(--spacing-6) 0;
}

.footer-bottom-content {
    display: flex;
    align-items: center;
    justify-content: space-between;
    flex-wrap: wrap;
    gap: var(--spacing-4);
}

.copyright p {
    margin: 0;
    color: var(--gray-500);
    font-size: var(--font-size-sm);
}

.footer-meta {
    display: flex;
    align-items: center;
    gap: var(--spacing-2);
    font-size: var(--font-size-xs);
    color: var(--gray-500);
}

.separator {
    color: var(--gray-600);
}

/* Responsive Design */
@media (max-width: 768px) {
    .footer-content {
        grid-template-columns: 1fr;
        gap: var(--spacing-6);
        padding: var(--spacing-8) 0 var(--spacing-6);
    }
    
    .footer-bottom-content {
        flex-direction: column;
        text-align: center;
        gap: var(--spacing-2);
    }
    
    .footer-meta {
        flex-direction: column;
        gap: var(--spacing-1);
    }
    
    .separator {
        display: none;
    }
}
</style>

<script>
// Update footer time
function updateFooterTime() {
    const now = new Date();
    const timeString = now.toLocaleTimeString('en-US', {
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
    });
    const footerTimeElement = document.getElementById('footerTime');
    if (footerTimeElement) {
        footerTimeElement.textContent = timeString;
    }
}

// Update time every second
updateFooterTime();
setInterval(updateFooterTime, 1000);
</script>

