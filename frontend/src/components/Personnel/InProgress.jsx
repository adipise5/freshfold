async function loadPendingRequests() {
    const container = document.getElementById('pendingRequestsList');
    if (!container) return;

    showLoading('pendingRequestsList');

    try {
        const requests = await apiRequest('/personnel/pending-requests');

        if (!requests || requests.length === 0) {
            container.innerHTML = '<p style="text-align: center; color: #6b7280; padding: 2rem;">No pending requests</p>';
            return;
        }

        container.innerHTML = requests.map(req => `
            <div class="request-card">
                <div class="request-header">
                    <div>
                        <h3>Request #${req.id}</h3>
                        <p><strong>Student:</strong> ${req.studentName}</p>
                        <p><strong>Location:</strong> ${req.studentHostel}, Room ${req.studentRoom}</p>
                        <p><strong>Phone:</strong> ${req.studentPhone}</p>
                    </div>
                    <div>
                        <span style="background: ${req.serviceType === 'URGENT' ? '#fef3c7' : '#e0e7ff'}; padding: 0.5rem 1rem; border-radius: 20px; font-weight: 600;">
                            ${req.serviceType}
                        </span>
                    </div>
                </div>
                <div style="margin: 1rem 0;">
                    <p><strong>Items:</strong> ${req.items.map(i => i.quantity + ' ' + i.type).join(', ')}</p>
                    <p><strong>Total Price:</strong> ${formatCurrency(req.totalPrice)}</p>
                    ${req.photoPath ? `<img src="${req.photoPath}" alt="Laundry" style="max-width: 200px; border-radius: 8px; margin-top: 1rem;">` : ''}
                </div>
                <div class="request-actions">
                    <button class="btn btn-secondary" onclick="acceptRequest(${req.id})">Accept</button>
                    <button class="btn btn-danger" onclick="rejectRequest(${req.id})">Reject</button>
                </div>
            </div>
        `).join('');
    } catch (error) {
        showError('Failed to load requests', 'pendingRequestsList');
    }
}

// Load in-progress requests
async function loadInProgressRequests() {
    const container = document.getElementById('inProgressList');
    if (!container) return;

    showLoading('inProgressList');

    try {
        const requests = await apiRequest('/personnel/in-progress');

        if (!requests || requests.length === 0) {
            container.innerHTML = '<p style="text-align: center; color: #6b7280; padding: 2rem;">No orders in progress</p>';
            return;
        }

        container.innerHTML = requests.map(req => `
            <div class="request-card">
                <div class="request-header">
                    <div>
                        <h3>Request #${req.id}</h3>
                        <p><strong>Student:</strong> ${req.studentName}</p>
                        <p><strong>Location:</strong> ${req.studentHostel}, Room ${req.studentRoom}</p>
                    </div>
                    ${getStatusBadge(req.status)}
                </div>
                <div style="margin: 1rem 0;">
                    <p><strong>Items:</strong> ${req.items.map(i => i.quantity + ' ' + i.type).join(', ')}</p>
                    <p><strong>Total Price:</strong> ${formatCurrency(req.totalPrice)}</p>
                </div>
                <div style="margin-top: 1rem;">
                    <select class="status-select" id="status-${req.id}">
                        <option value="ACCEPTED" ${req.status === 'ACCEPTED' ? 'selected' : ''}>Accepted</option>
                        <option value="PENDING_COLLECTION" ${req.status === 'PENDING_COLLECTION' ? 'selected' : ''}>Pending Collection</option>
                        <option value="WASHING" ${req.status === 'WASHING' ? 'selected' : ''}>Washing</option>
                        <option value="IRONING" ${req.status === 'IRONING' ? 'selected' : ''}>Ironing</option>
                        <option value="DONE" ${req.status === 'DONE' ? 'selected' : ''}>Done</option>
                    </select>
                    <button class="btn btn-primary" onclick="updateStatus(${req.id})">Update Status</button>
                </div>
            </div>
        `).join('');
    } catch (error) {
        showError('Failed to load requests', 'inProgressList');
    }
}

// Load completed orders
async function loadCompletedOrders() {
    const container = document.getElementById('completedList');
    if (!container) return;

    showLoading('completedList');

    try {
        const orders = await apiRequest('/personnel/completed-orders');

        if (!orders || orders.length === 0) {
            container.innerHTML = '<p style="text-align: center; color: #6b7280; padding: 2rem;">No completed orders yet</p>';
            return;
        }

        const totalEarnings = orders.reduce((sum, order) => sum + order.totalPrice, 0);

        container.innerHTML = `
            <div class="card" style="background: #eff6ff; margin-bottom: 2rem;">
                <h3>Total Earnings: ${formatCurrency(totalEarnings)}</h3>
                <p>Completed Orders: ${orders.length}</p>
            </div>
            ${orders.map(order => `
                <div class="order-card">
                    <div class="order-header">
                        <div>
                            <h3>Order #${order.id}</h3>
                            <p><strong>Student:</strong> ${order.studentName}</p>
                        </div>
                        ${getStatusBadge('DONE')}
                    </div>
                    <div style="display: flex; justify-content: space-between; margin-top: 1rem;">
                        <span><strong>Amount:</strong> ${formatCurrency(order.totalPrice)}</span>
                        <span style="color: #6b7280;">${getTimeAgo(order.createdAt)}</span>
                    </div>
                </div>
            `).join('')}
        `;
    } catch (error) {
        showError('Failed to load orders', 'completedList');
    }
}

// Accept request
async function acceptRequest(requestId) {
    if (!confirm('Accept this request?')) return;

    try {
        const result = await apiRequest(`/personnel/accept-request/${requestId}`, {
            method: 'POST'
        });

        if (result && result.success) {
            alert('Request accepted successfully!');
            loadPendingRequests();
        } else {
            alert('Failed to accept request');
        }
    } catch (error) {
        alert('Error accepting request');
    }
}

// Reject request
async function rejectRequest(requestId) {
    const reason = prompt('Enter rejection reason:');
    if (!reason) return;

    try {
        const result = await apiRequest(`/personnel/reject-request/${requestId}`, {
            method: 'POST',
            body: JSON.stringify({ reason: reason })
        });

        if (result && result.success) {
            alert('Request rejected');
            loadPendingRequests();
        } else {
            alert('Failed to reject request');
        }
    } catch (error) {
        alert('Error rejecting request');
    }
}

// Update status
async function updateStatus(requestId) {
    const newStatus = document.getElementById(`status-${requestId}`).value;

    try {
        const result = await apiRequest(`/personnel/update-status/${requestId}`, {
            method: 'PUT',
            body: JSON.stringify({ status: newStatus })
        });

        if (result && result.success) {
            alert('Status updated successfully!');
            loadInProgressRequests();
        } else {
            alert('Failed to update status');
        }
    } catch (error) {
        alert('Error updating status');
    }
}