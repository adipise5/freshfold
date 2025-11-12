async function loadStatistics() {
    try {
        const stats = await apiRequest('/admin/statistics');

        if (stats) {
            document.getElementById('totalCompleted').textContent = stats.totalCompleted || 0;
            document.getElementById('totalRevenue').textContent = formatCurrency(stats.totalRevenue || 0);
            document.getElementById('activeOrders').textContent = stats.activeOrders || 0;
        }
    } catch (error) {
        console.error('Error loading statistics:', error);
    }
}

// Load hostel performance
async function loadHostelPerformance() {
    const container = document.getElementById('hostelPerformanceTable');
    if (!container) return;

    try {
        const hostels = await apiRequest('/admin/hostel-performance');

        if (!hostels || hostels.length === 0) {
            container.innerHTML = '<tr><td colspan="4" style="text-align: center;">No data available</td></tr>';
            return;
        }

        container.innerHTML = hostels.map(hostel => `
            <tr>
                <td>${hostel.name}</td>
                <td>${hostel.totalOrders}</td>
                <td>${formatCurrency(hostel.totalRevenue)}</td>
                <td>${hostel.avgRating ? hostel.avgRating.toFixed(2) : 'N/A'}</td>
            </tr>
        `).join('');
    } catch (error) {
        console.error('Error loading hostel performance:', error);
    }
}

// Load personnel performance
async function loadPersonnelPerformance() {
    const container = document.getElementById('personnelPerformanceTable');
    if (!container) return;

    try {
        const personnel = await apiRequest('/admin/personnel-performance');

        if (!personnel || personnel.length === 0) {
            container.innerHTML = '<tr><td colspan="5" style="text-align: center;">No data available</td></tr>';
            return;
        }

        container.innerHTML = personnel.map(p => `
            <tr>
                <td>${p.name}</td>
                <td>${p.totalOrders}</td>
                <td>${formatCurrency(p.totalEarnings)}</td>
                <td>${p.rating.toFixed(2)}</td>
                <td>${p.completionRate.toFixed(2)}%</td>
            </tr>
        `).join('');
    } catch (error) {
        console.error('Error loading personnel performance:', error);
    }
}

// Load recent orders
async function loadRecentOrders() {
    const container = document.getElementById('recentOrdersTable');
    if (!container) return;

    try {
        const orders = await apiRequest('/admin/recent-orders');

        if (!orders || orders.length === 0) {
            container.innerHTML = '<tr><td colspan="7" style="text-align: center;">No recent orders</td></tr>';
            return;
        }

        container.innerHTML = orders.map(order => `
            <tr>
                <td>#${order.id}</td>
                <td>${order.studentName}</td>
                <td>${order.hostel}</td>
                <td>${order.personnelName}</td>
                <td>${order.status}</td>
                <td>${formatCurrency(order.totalPrice)}</td>
                <td>${getTimeAgo(order.createdAt)}</td>
            </tr>
        `).join('');
    } catch (error) {
        console.error('Error loading recent orders:', error);
    }
}

// Download report
function downloadReport(format) {
    window.location.href = `${API_BASE}/admin/download-report?format=${format}`;
}

// Initialize admin dashboard
function initAdminDashboard() {
    loadStatistics();
    loadHostelPerformance();
    loadPersonnelPerformance();
    loadRecentOrders();
}