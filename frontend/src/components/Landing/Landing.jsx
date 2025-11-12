// Common JavaScript functions for all pages

const API_BASE = '/freshfold/api';

// Check authentication
function checkAuth() {
    const userId = localStorage.getItem('userId');
    if (!userId) {
        window.location.href = 'login.html';
        return false;
    }
    return true;
}

// Check role authorization
function checkRole(requiredRole) {
    const userType = localStorage.getItem('userType');
    if (userType !== requiredRole) {
        alert('Unauthorized access');
        window.location.href = 'index.html';
        return false;
    }
    return true;
}

// Logout function
function logout() {
    if (confirm('Are you sure you want to logout?')) {
        fetch(API_BASE + '/auth/logout', {
            method: 'POST',
            credentials: 'include'
        })
            .then(() => {
                localStorage.clear();
                window.location.href = 'index.html';
            })
            .catch(error => {
                console.error('Logout error:', error);
                localStorage.clear();
                window.location.href = 'index.html';
            });
    }
}

// Format date to readable format
function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleString('en-IN', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

// Format currency
function formatCurrency(amount) {
    return '₹' + parseFloat(amount).toFixed(2);
}

// Get time ago
function getTimeAgo(dateString) {
    const date = new Date(dateString);
    const now = new Date();
    const seconds = Math.floor((now - date) / 1000);

    if (seconds < 60) return 'just now';
    if (seconds < 3600) return Math.floor(seconds / 60) + ' minutes ago';
    if (seconds < 86400) return Math.floor(seconds / 3600) + ' hours ago';
    if (seconds < 604800) return Math.floor(seconds / 86400) + ' days ago';

    return formatDate(dateString);
}

// Get status badge HTML
function getStatusBadge(status) {
    const statusClasses = {
        'PENDING': 'status-pending',
        'ACCEPTED': 'status-accepted',
        'PENDING_COLLECTION': 'status-accepted',
        'WASHING': 'status-washing',
        'IRONING': 'status-ironing',
        'DONE': 'status-done',
        'REJECTED': 'status-rejected'
    };

    const displayNames = {
        'PENDING': 'Pending',
        'ACCEPTED': 'Accepted',
        'PENDING_COLLECTION': 'Pending Collection',
        'WASHING': 'Washing',
        'IRONING': 'Ironing',
        'DONE': 'Completed',
        'REJECTED': 'Rejected'
    };

    const className = statusClasses[status] || 'status-pending';
    const displayName = displayNames[status] || status;

    return `<span class="status-badge ${className}">${displayName}</span>`;
}

// Show loading spinner
function showLoading(elementId) {
    const element = document.getElementById(elementId);
    if (element) {
        element.innerHTML = '<div class="loading">Loading...</div>';
    }
}

// Show error message
function showError(message, elementId) {
    const element = document.getElementById(elementId);
    if (element) {
        element.innerHTML = `<div class="error-message">${message}</div>`;
    } else {
        alert(message);
    }
}

// Show success message
function showSuccess(message, elementId) {
    const element = document.getElementById(elementId);
    if (element) {
        element.innerHTML = `<div class="success-message">${message}</div>`;
    } else {
        alert(message);
    }
}

// Make authenticated API request
async function apiRequest(endpoint, options = {}) {
    const defaultOptions = {
        credentials: 'include',
        headers: {
            'Content-Type': 'application/json',
            ...options.headers
        }
    };

    try {
        const response = await fetch(API_BASE + endpoint, {
            ...defaultOptions,
            ...options
        });

        if (response.status === 401) {
            localStorage.clear();
            window.location.href = 'login.html';
            return null;
        }

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('application/json')) {
            return await response.json();
        }

        return await response.text();
    } catch (error) {
        console.error('API request failed:', error);
        throw error;
    }
}

// Switch between tabs
function switchTab(tabName, tabPrefix = '') {
    // Hide all tab contents
    document.querySelectorAll('.tab-content').forEach(content => {
        content.classList.remove('active');
    });

    // Remove active class from all tab buttons
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.classList.remove('active');
    });

    // Show selected tab content
    const tabContent = document.getElementById(tabPrefix + tabName + '-tab');
    if (tabContent) {
        tabContent.classList.add('active');
    }

    // Add active class to clicked button
    const clickedButton = event?.target;
    if (clickedButton) {
        clickedButton.classList.add('active');
    }
}

// Validate form
function validateForm(formId) {
    const form = document.getElementById(formId);
    if (!form) return false;

    const inputs = form.querySelectorAll('input[required], select[required], textarea[required]');
    for (let input of inputs) {
        if (!input.value.trim()) {
            alert(`Please fill in ${input.name || 'all required fields'}`);
            input.focus();
            return false;
        }
    }
    return true;
}

// Debounce function
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}