const itemPrices = {
    'Shirts': 15,
    'T-Shirts': 12,
    'Pants': 20,
    'Jeans': 25,
    'Undergarments': 8,
    'Socks': 5,
    'Bed Sheets': 30,
    'Towels': 15
};

let uploadedPhotoPath = '';

// Load personnel list
async function loadPersonnel() {
    try {
        const response = await apiRequest('/student/personnel-list');
        const select = document.getElementById('personnel');

        if (!select) return;

        select.innerHTML = '<option value="">Choose Personnel</option>';

        response.forEach(p => {
            const stars = '★'.repeat(Math.round(p.rating)) + '☆'.repeat(5 - Math.round(p.rating));
            select.innerHTML += `<option value="${p.id}">${p.name} (${p.experience} years) - ${stars}</option>`;
        });
    } catch (error) {
        console.error('Error loading personnel:', error);
        showError('Failed to load personnel list', 'personnel');
    }
}

// Update item quantity
function updateQty(item, change) {
    const input = document.getElementById(item + '-qty');
    if (!input) return;

    let value = parseInt(input.value) + change;
    if (value < 0) value = 0;
    input.value = value;

    const price = value * itemPrices[item.split('-')[0].charAt(0).toUpperCase() + item.split('-')[0].slice(1).replace(/([A-Z])/g, ' $1').trim()];
    const priceElement = document.getElementById(item + '-price');
    if (priceElement) {
        priceElement.textContent = formatCurrency(price);
    }

    calculatePrice();
}

// Calculate total price
function calculatePrice() {
    let subtotal = 0;

    for (let item in itemPrices) {
        const itemKey = item.toLowerCase().replace(/\s+/g, '').replace(/-/g, '');
        const qtyInput = document.getElementById(itemKey + '-qty');
        if (qtyInput) {
            const qty = parseInt(qtyInput.value) || 0;
            subtotal += qty * itemPrices[item];
        }
    }

    const serviceType = document.getElementById('serviceType')?.value || 'NORMAL';
    let serviceCharge = 0;

    if (serviceType === 'URGENT') {
        serviceCharge = subtotal * 0.5;
    }

    const total = subtotal + serviceCharge;

    const subtotalEl = document.getElementById('subtotal');
    const serviceChargeEl = document.getElementById('serviceCharge');
    const totalPriceEl = document.getElementById('totalPrice');

    if (subtotalEl) subtotalEl.textContent = formatCurrency(subtotal);
    if (serviceChargeEl) serviceChargeEl.textContent = formatCurrency(serviceCharge);
    if (totalPriceEl) totalPriceEl.textContent = formatCurrency(total);
}

// Handle photo upload
async function handlePhotoUpload(event) {
    const file = event.target.files[0];
    if (!file) return;

    // Validate file type
    if (!file.type.match('image.*')) {
        alert('Please upload an image file');
        return;
    }

    // Validate file size (5MB)
    if (file.size > 5 * 1024 * 1024) {
        alert('File size should not exceed 5MB');
        return;
    }

    // Preview image
    const reader = new FileReader();
    reader.onload = function(e) {
        const preview = document.getElementById('previewImg');
        const uploadZone = document.getElementById('uploadZone');

        if (preview) {
            preview.src = e.target.result;
            preview.style.display = 'block';
        }
        if (uploadZone) {
            uploadZone.classList.add('has-image');
        }
    };
    reader.readAsDataURL(file);

    // Upload to server
    const formData = new FormData();
    formData.append('photo', file);

    try {
        const response = await fetch(API_BASE + '/upload-photo', {
            method: 'POST',
            body: formData,
            credentials: 'include'
        });

        const result = await response.json();
        if (result.success) {
            uploadedPhotoPath = result.filePath;
        } else {
            alert('Photo upload failed: ' + result.message);
        }
    } catch (error) {
        console.error('Upload error:', error);
        alert('Photo upload failed');
    }
}

// Create laundry request
async function handleCreateRequest(event) {
    event.preventDefault();

    const submitBtn = document.getElementById('submitBtn');
    const messageDiv = document.getElementById('createMessage');

    if (submitBtn) submitBtn.disabled = true;
    if (submitBtn) submitBtn.textContent = 'Creating request...';
    if (messageDiv) messageDiv.innerHTML = '';

    // Collect items
    const items = [];
    for (let itemName in itemPrices) {
        const itemKey = itemName.toLowerCase().replace(/\s+/g, '').replace(/-/g, '');
        const qtyInput = document.getElementById(itemKey + '-qty');
        if (qtyInput) {
            const qty = parseInt(qtyInput.value) || 0;
            if (qty > 0) {
                items.push({
                    type: itemName,
                    quantity: qty
                });
            }
        }
    }

    // Validate
    if (items.length === 0) {
        showError('Please add at least one item', 'createMessage');
        if (submitBtn) {
            submitBtn.disabled = false;
            submitBtn.textContent = 'Submit Request';
        }
        return;
    }

    if (!uploadedPhotoPath) {
        showError('Please upload a photo of your laundry', 'createMessage');
        if (submitBtn) {
            submitBtn.disabled = false;
            submitBtn.textContent = 'Submit Request';
        }
        return;
    }

    const requestData = {
        personnelId: parseInt(document.getElementById('personnel').value),
        serviceType: document.getElementById('serviceType').value,
        items: items,
        photoPath: uploadedPhotoPath
    };

    try {
        const result = await apiRequest('/student/create-request', {
            method: 'POST',
            body: JSON.stringify(requestData)
        });

        if (result && result.success) {
            showSuccess('Request created successfully! Request ID: #' + result.requestId, 'createMessage');
            setTimeout(() => {
                event.target.reset();
                uploadedPhotoPath = '';
                calculatePrice();
                switchTab('orders');
            }, 2000);
        } else {
            showError(result?.message || 'Failed to create request', 'createMessage');
        }
    } catch (error) {
        showError('Error creating request: ' + error.message, 'createMessage');
    } finally {
        if (submitBtn) {
            submitBtn.disabled = false;
            submitBtn.textContent = 'Submit Request';
        }
    }
}

// Load student orders
async function loadOrders() {
    const ordersList = document.getElementById('ordersList');
    if (!ordersList) return;

    showLoading('ordersList');

    try {
        const orders = await apiRequest('/student/orders');

        if (!orders || orders.length === 0) {
            ordersList.innerHTML = '<p style="text-align: center; color: #6b7280; padding: 2rem;">No orders yet. Create your first laundry request!</p>';
            return;
        }

        ordersList.innerHTML = orders.map(order => `
            <div class="order-card">
                <div class="order-header">
                    <div>
                        <h3>Order #${order.id}</h3>
                        <p style="color: #6b7280;">Personnel: ${order.personnelName || 'N/A'}</p>
                    </div>
                    ${getStatusBadge(order.status)}
                </div>
                <div class="order-details">
                    <p><strong>Service:</strong> ${order.serviceType}</p>
                    <p><strong>Items:</strong> ${order.items.map(i => i.quantity + ' ' + i.type).join(', ')}</p>
                    ${order.rejectionReason ? `<p style="color: #991b1b;"><strong>Rejection Reason:</strong> ${order.rejectionReason}</p>` : ''}
                </div>
                <div style="display: flex; justify-content: space-between; margin-top: 1rem; padding-top: 1rem; border-top: 1px solid #e5e7eb;">
                    <span><strong>Total:</strong> ${formatCurrency(order.totalPrice)}</span>
                    <span style="color: #6b7280;">${getTimeAgo(order.createdAt)}</span>
                </div>
            </div>
        `).join('');
    } catch (error) {
        showError('Failed to load orders', 'ordersList');
    }
}