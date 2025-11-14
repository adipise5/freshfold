import React, { useState, useEffect } from 'react';
import { BrowserRouter, Routes, Route, Navigate, useNavigate, Link } from 'react-router-dom';
import axios from 'axios';
import './App.css';

// Configure axios
const API_URL = 'http://localhost:8080/api';
axios.defaults.baseURL = API_URL;

// ================ CONSTANTS ================
const HOSTELS = [
    'Ram Bhawan', 'Budh Bhawan', 'Gandhi Bhawan', 'Shankar Bhawan', 'Vyas Bhawan',
    'Vishwakarma Bhawan', 'Bhagirath Bhawan', 'Rana Pratap Bhawan', 'Ashok Bhawan',
    'Malviya Bhawan', 'Meera Bhawan'
];

const ITEM_PRICES = {
    'Shirt': 15, 'T-Shirt': 12, 'Pants': 20, 'Jeans': 25,
    'Undergarments': 8, 'Socks': 5, 'Bed Sheets': 30, 'Towels': 15
};

// =================== Utility Components ===================

/**
 * OrderPhotoGallery
 * Fetches ordered image URLs for a given orderId and displays them in a responsive grid.
 *
 * Expects backend endpoint: GET /api/student/orders/{orderId}/photos
 * Backend may return absolute URLs (e.g., http://host/api/uploads/file.jpg) or relative paths (/api/uploads/..).
 */
function OrderPhotoGallery({ orderId }) {
    const [photos, setPhotos] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        if (!orderId) return;
        setLoading(true);
        axios.get(`/student/orders/${orderId}/photos`)
            .then(res => {
                // Expect an array of URLs or relative paths
                setPhotos(Array.isArray(res.data) ? res.data : []);
            })
            .catch(() => setPhotos([]))
            .finally(() => setLoading(false));
    }, [orderId]);

    if (loading) return <p style={{ color: "#777" }}>Loading photos...</p>;
    if (!photos || photos.length === 0)
        return <p style={{ color: "#777", fontStyle: "italic" }}>No photos uploaded</p>;

    const normalizeSrc = (p) => {
        if (!p) return '';
        if (p.startsWith('http://') || p.startsWith('https://')) return p;
        // If backend returned a relative path like "/api/uploads/..." or "/uploads/..."
        // prefix host + port
        return `http://localhost:8080${p}`;
    };

    return (
        <div className="photo-gallery" style={{ marginTop: '0.75rem' }}>
            <strong>Clothing Photos:</strong>
            <div className="photo-grid" style={{ marginTop: 8 }}>
                {photos.map((p, idx) => (
                    <img
                        key={idx}
                        src={normalizeSrc(p)}
                        alt={`order-${orderId}-${idx+1}`}
                        className="gallery-photo"
                    />
                ))}
            </div>
        </div>
    );
}

// =================== Pages & Components ===================

function LandingPage() {
    const navigate = useNavigate();

    return (
        <div className="landing-page">
            <nav className="lp-navbar">
                <div className="lp-logo">🧺 FreshFold</div>
                <div className="lp-buttons">
                    <button onClick={() => navigate('/login')} className="lp-btn-secondary">Login</button>
                    <button onClick={() => navigate('/signup')} className="lp-btn-primary">Sign Up</button>
                </div>
            </nav>

            <section className="lp-hero">
                <h1 className="lp-title">
                    Smart Laundry Service for <span className="brand">BITS Pilani</span>
                </h1>
                <p className="lp-subtitle">Fast • Safe • Reliable • Hostel-to-Hostel Delivery</p>

                <div className="lp-features">
                    {[
                        { icon: "⚡", title: "Fast Delivery", desc: "Laundry in 1 – 3 days" },
                        { icon: "🤝", title: "Trusted Staff", desc: "Verified & trained personnel" },
                        { icon: "🔒", title: "Secure Handling", desc: "Guaranteed safe care" },
                        { icon: "✨", title: "Premium Quality", desc: "Washing + ironing service" }
                    ].map((f, i) => (
                        <div key={i} className="lp-feature-box">
                            <span className="lp-icon">{f.icon}</span>
                            <h3>{f.title}</h3>
                            <p>{f.desc}</p>
                        </div>
                    ))}
                </div>

                <button className="lp-hero-btn" onClick={() => navigate('/signup')}>
                    Get Started
                </button>
            </section>
        </div>
    );
}

function LoginPage() {
    const [role, setRole] = useState('STUDENT');
    const [formData, setFormData] = useState({ email: '', password: '' });
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        try {
            const response = await axios.post('/auth/login', { ...formData, role });
            const userData = response.data;
            localStorage.setItem('user', JSON.stringify(userData));

            if (role === 'STUDENT') navigate('/student/dashboard');
            else if (role === 'PERSONNEL') navigate('/personnel/dashboard');
            else if (role === 'ADMIN') navigate('/admin/dashboard');
        } catch (err) {
            setError(err.response?.data?.message || 'Login failed');
        }
    };

    return (
        <div className="auth-page">
            <div className="auth-container">
                <h2>🧺 FreshFold Login</h2>

                <div className="role-tabs">
                    <button className={role === 'STUDENT' ? 'active' : ''} onClick={() => setRole('STUDENT')}>Student</button>
                    <button className={role === 'PERSONNEL' ? 'active' : ''} onClick={() => setRole('PERSONNEL')}>Personnel</button>
                    <button className={role === 'ADMIN' ? 'active' : ''} onClick={() => setRole('ADMIN')}>Admin</button>
                </div>

                <form onSubmit={handleSubmit}>
                    <input
                        type="text"
                        placeholder={role === 'ADMIN' ? 'Admin ID or Email' : 'Email'}
                        value={formData.email}
                        onChange={(e) => setFormData({...formData, email: e.target.value})}
                        required
                    />
                    <input
                        type="password"
                        placeholder="Password"
                        value={formData.password}
                        onChange={(e) => setFormData({...formData, password: e.target.value})}
                        required
                    />
                    {error && <p className="error">{error}</p>}
                    <button type="submit" className="btn-primary">Login</button>
                </form>

                <p className="auth-switch">
                    Don't have an account? <Link to="/signup">Sign up</Link>
                </p>
            </div>
        </div>
    );
}

function SignupPage() {
    const [role, setRole] = useState('STUDENT');
    const [formData, setFormData] = useState({});
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');

        try {
            const endpoint = role === 'STUDENT' ? '/auth/signup/student' : '/auth/signup/personnel';
            await axios.post(endpoint, formData);
            setSuccess('Registration successful! Redirecting to login...');
            setTimeout(() => navigate('/login'), 1500);
        } catch (err) {
            setError(err.response?.data?.message || 'Signup failed');
        }
    };

    return (
        <div className="auth-page">
            <div className="auth-container">
                <h2>🧺 FreshFold Signup</h2>

                <div className="role-tabs">
                    <button className={role === 'STUDENT' ? 'active' : ''} onClick={() => setRole('STUDENT')}>Student</button>
                    <button className={role === 'PERSONNEL' ? 'active' : ''} onClick={() => setRole('PERSONNEL')}>Personnel</button>
                </div>

                <form onSubmit={handleSubmit}>
                    <input
                        type="text"
                        placeholder="Full Name"
                        onChange={(e) => setFormData({...formData, fullName: e.target.value})}
                        required
                    />

                    {role === 'STUDENT' ? (
                        <>
                            <input
                                type="text"
                                placeholder="Student ID"
                                onChange={(e) => setFormData({...formData, studentId: e.target.value})}
                                required
                            />
                            <input
                                type="email"
                                placeholder="BITS Email (@pilani.bits-pilani.ac.in)"
                                onChange={(e) => setFormData({...formData, email: e.target.value})}
                                required
                            />
                            <select onChange={(e) => setFormData({...formData, hostel: e.target.value})} required>
                                <option value="">Select Hostel</option>
                                {HOSTELS.map(h => <option key={h} value={h}>{h}</option>)}
                            </select>
                            <input
                                type="text"
                                placeholder="Room Number"
                                onChange={(e) => setFormData({...formData, roomNumber: e.target.value})}
                                required
                            />
                        </>
                    ) : (
                        <>
                            <input
                                type="text"
                                placeholder="Employee ID"
                                onChange={(e) => setFormData({...formData, employeeId: e.target.value})}
                                required
                            />
                            <input
                                type="email"
                                placeholder="Email"
                                onChange={(e) => setFormData({...formData, email: e.target.value})}
                                required
                            />
                            <input
                                type="number"
                                placeholder="Years of Experience"
                                onChange={(e) => setFormData({...formData, yearsExperience: parseInt(e.target.value)})}
                                required
                            />
                        </>
                    )}

                    <input
                        type="tel"
                        placeholder="Phone Number"
                        onChange={(e) => setFormData({...formData, phoneNumber: e.target.value})}
                        required
                    />
                    <input
                        type="password"
                        placeholder="Password"
                        onChange={(e) => setFormData({...formData, password: e.target.value})}
                        required
                    />

                    {error && <p className="error">{error}</p>}
                    {success && <p className="success">{success}</p>}
                    <button type="submit" className="btn-primary">Sign Up</button>
                </form>

                <p className="auth-switch">
                    Already have an account? <Link to="/login">Login</Link>
                </p>
            </div>
        </div>
    );
}

// ================ STUDENT DASHBOARD ================
function StudentDashboard() {
    const [activeTab, setActiveTab] = useState('create');
    const [user, setUser] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        const userData = JSON.parse(localStorage.getItem('user'));
        if (!userData || userData.role !== 'STUDENT') {
            navigate('/login');
        } else {
            setUser(userData);
        }
    }, [navigate]);

    const handleLogout = () => {
        localStorage.removeItem('user');
        navigate('/');
    };

    if (!user) return null;

    return (
        <div className="dashboard">
            <nav className="dashboard-nav">
                <div className="logo">🧺 FreshFold</div>
                <div className="user-info">
                    <span>{user.fullName}</span>
                    <button onClick={handleLogout} className="btn-secondary">Logout</button>
                </div>
            </nav>

            <div className="dashboard-content">
                <div className="tabs">
                    <button
                        className={activeTab === 'create' ? 'active' : ''}
                        onClick={() => setActiveTab('create')}
                    >
                        Create Request
                    </button>
                    <button
                        className={activeTab === 'orders' ? 'active' : ''}
                        onClick={() => setActiveTab('orders')}
                    >
                        My Orders
                    </button>
                </div>

                <div className="tab-content">
                    {activeTab === 'create' && <CreateRequest user={user} />}
                    {activeTab === 'orders' && <MyOrders userId={user.id} />}
                </div>
            </div>
        </div>
    );
}

// ================ CREATE REQUEST (updated flow) ================
function CreateRequest({ user }) {
    const [personnel, setPersonnel] = useState([]);
    const [selectedPersonnel, setSelectedPersonnel] = useState(null);
    const [items, setItems] = useState({});
    const [serviceType, setServiceType] = useState('NORMAL');
    const [urgencyDays, setUrgencyDays] = useState(3);
    const [photo, setPhoto] = useState(null);
    const [photoPreview, setPhotoPreview] = useState(null);
    const [message, setMessage] = useState('');

    useEffect(() => {
        axios.get('/student/personnel').then(res => setPersonnel(res.data));
    }, []);

    const handlePhotoChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            setPhoto(file);
            setPhotoPreview(URL.createObjectURL(file));
        }
    };

    const updateQuantity = (itemType, delta) => {
        setItems(prev => ({
            ...prev,
            [itemType]: Math.max(0, (prev[itemType] || 0) + delta)
        }));
    };

    const calculatePrice = () => {
        let base = Object.entries(items).reduce(
            (sum, [type, qty]) => sum + qty * ITEM_PRICES[type],
            0
        );
        if (urgencyDays === 1) return base * 1.5;
        if (urgencyDays === 2) return base * 1.25;
        return base;
    };

    const handleSubmit = async () => {
        if (!selectedPersonnel) {
            setMessage('Please select personnel');
            return;
        }
        if (!photo) {
            setMessage('Please upload a photo');
            return;
        }

        try {
            // 1) Create order first (photoUrl left blank; will upload image next)
            const orderItems = Object.entries(items)
                .filter(([_, qty]) => qty > 0)
                .map(([type, qty]) => ({
                    itemType: type,
                    quantity: qty,
                    pricePerItem: ITEM_PRICES[type]
                }));

            const orderPayload = {
                studentId: user.id,
                personnelId: selectedPersonnel,
                items: orderItems,
                serviceType,
                urgencyDays,
                pickupLocation: `${user.userData.hostel}, Room ${user.userData.roomNumber}`,
                photoUrl: ""
            };

            const orderResponse = await axios.post('/student/orders', orderPayload);
            const orderId = orderResponse.data.data.id;

            // 2) Upload photo using the orderId (backend will name it correctly: orderId.jpg / orderId_1.jpg ...)
            const formData = new FormData();
            formData.append('file', photo);

            await axios.post(
                `/student/upload-photo?orderId=${orderId}`,
                formData,
                { headers: { 'Content-Type': 'multipart/form-data' } }
            );

            setMessage('Order created successfully!');
            setTimeout(() => window.location.reload(), 1200);

        } catch (err) {
            setMessage('Failed to create order: ' + (err.response?.data?.message || err.message));
        }
    };

    return (
        <div className="create-request">
            <h3>Create New Request</h3>

            <div className="form-group">
                <label>Select Personnel</label>
                <select onChange={(e) => setSelectedPersonnel(Number(e.target.value))} value={selectedPersonnel || ''}>
                    <option value="">Choose personnel...</option>
                    {personnel.map(p => (
                        <option key={p.id} value={p.id}>
                            {p.fullName} - {p.yearsExperience} years - {'★'.repeat(Math.floor(p.rating))}
                        </option>
                    ))}
                </select>
            </div>

            <div className="form-group">
                <label>Clothing Items</label>
                <div className="items-grid">
                    {Object.keys(ITEM_PRICES).map(item => (
                        <div key={item} className="item-control">
                            <span>{item} (₹{ITEM_PRICES[item]})</span>
                            <div className="quantity-control">
                                <button onClick={() => updateQuantity(item, -1)}>−</button>
                                <span>{items[item] || 0}</span>
                                <button onClick={() => updateQuantity(item, 1)}>+</button>
                            </div>
                        </div>
                    ))}
                </div>
            </div>

            <div className="form-group">
                <label>Service Type</label>
                <div className="service-type">
                    <label>
                        <input type="radio" checked={serviceType === 'NORMAL'} onChange={() => { setServiceType('NORMAL'); setUrgencyDays(3); }} />
                        Normal (3 days)
                    </label>
                    <label>
                        <input type="radio" checked={serviceType === 'URGENT'} onChange={() => setServiceType('URGENT')} />
                        Urgent
                    </label>
                </div>
                {serviceType === 'URGENT' && (
                    <select value={urgencyDays} onChange={(e) => setUrgencyDays(Number(e.target.value))}>
                        <option value={1}>1 day (+50%)</option>
                        <option value={2}>2 days (+25%)</option>
                    </select>
                )}
            </div>

            <div className="form-group">
                <label>Upload Photo (Required)</label>
                <input type="file" accept="image/*" onChange={handlePhotoChange} />
                {photoPreview && (
                    <div className="photo-preview">
                        <img src={photoPreview} alt="Preview" />
                        <button onClick={() => { setPhoto(null); setPhotoPreview(null); }}>Delete</button>
                    </div>
                )}
            </div>

            <div className="price-summary">
                <h4>Total Price: ₹{calculatePrice().toFixed(2)}</h4>
            </div>

            {message && <p className={message.includes('success') ? 'success' : 'error'}>{message}</p>}
            <button onClick={handleSubmit} className="btn-primary">Submit Request</button>
        </div>
    );
}

// ================ MY ORDERS (Updated with gallery) ================
function MyOrders({ userId }) {
    const [orders, setOrders] = useState([]);

    useEffect(() => {
        fetchOrders();
    }, [userId]);

    const fetchOrders = () => {
        axios.get(`/student/orders/${userId}`).then(res => setOrders(res.data));
    };

    const getStatusColor = (status) => {
        const colors = {
            PENDING: '#FFA500', ACCEPTED: '#4CAF50', WASHING: '#2196F3',
            IRONING: '#9C27B0', DONE: '#4CAF50', REJECTED: '#F44336'
        };
        return colors[status] || '#666';
    };

    const submitRating = async (orderId, rating) => {
        try {
            await axios.post(`/student/orders/${orderId}/rating`, { rating });
            alert('Rating submitted successfully!');
            fetchOrders();
        } catch (err) {
            alert(err.response?.data?.message || 'Failed to submit rating');
        }
    };

    const StarRating = ({ orderId, currentRating }) => {
        const [hoveredStar, setHoveredStar] = useState(0);

        if (currentRating) {
            return (
                <div className="rating-display">
                    <span>Your Rating: </span>
                    {[1, 2, 3, 4, 5].map(star => (
                        <span key={star} className={star <= currentRating ? "star" : "star empty"}>★</span>
                    ))}
                </div>
            );
        }

        return (
            <div className="rating-input">
                <span>Rate this service: </span>
                {[1, 2, 3, 4, 5].map(star => (
                    <span
                        key={star}
                        className={star <= hoveredStar ? "star" : "star empty"}
                        onMouseEnter={() => setHoveredStar(star)}
                        onMouseLeave={() => setHoveredStar(0)}
                        onClick={() => submitRating(orderId, star)}
                        style={{ cursor: 'pointer', fontSize: '1.5rem' }}
                    >
            ★
          </span>
                ))}
            </div>
        );
    };

    return (
        <div className="my-orders">
            <h3>My Orders</h3>
            {orders.length === 0 ? (
                <p>No orders yet</p>
            ) : (
                <div className="orders-list">
                    {orders.map(order => (
                        <div key={order.id} className="order-card">
                            <div className="order-header">
                                <span className="order-id">Order #{order.id}</span>
                                <span className="status" style={{ backgroundColor: getStatusColor(order.status) }}>
                  {order.status}
                </span>
                            </div>
                            <p><strong>Personnel:</strong> {order.personnel?.fullName || 'Not assigned'}</p>
                            <p><strong>Service:</strong> {order.serviceType} ({order.urgencyDays} days)</p>
                            <p><strong>Total:</strong> ₹{order.totalPrice}</p>
                            <p><strong>Created:</strong> {order.createdAt}</p>
                            {order.rejectionReason && <p className="rejection"><strong>Rejection:</strong> {order.rejectionReason}</p>}
                            <div className="order-items">
                                {order.items.map((item, idx) => (
                                    <span key={idx}>{item.itemType} x{item.quantity}</span>
                                ))}
                            </div>

                            {/* Photo gallery for students to view uploaded images */}
                            <div style={{ marginTop: 8 }}>
                                <OrderPhotoGallery orderId={order.id} />
                            </div>

                            {order.status === 'DONE' && (
                                <div className="order-rating" style={{ marginTop: '1rem', padding: '1rem', background: '#f9f9f9', borderRadius: '8px' }}>
                                    <StarRating orderId={order.id} currentRating={order.studentRating} />
                                </div>
                            )}
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}

// ================ PERSONNEL DASHBOARD ================
function PersonnelDashboard() {
    const [activeTab, setActiveTab] = useState('new');
    const [user, setUser] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        const userData = JSON.parse(localStorage.getItem('user'));
        if (!userData || userData.role !== 'PERSONNEL') {
            navigate('/login');
        } else {
            setUser(userData);
        }
    }, [navigate]);

    const handleLogout = () => {
        localStorage.removeItem('user');
        navigate('/');
    };

    if (!user) return null;

    return (
        <div className="dashboard">
            <nav className="dashboard-nav">
                <div className="logo">🧺 FreshFold</div>
                <div className="user-info">
                    <span>{user.fullName}</span>
                    <button onClick={handleLogout} className="btn-secondary">Logout</button>
                </div>
            </nav>

            <div className="dashboard-content">
                <div className="tabs">
                    <button className={activeTab === 'new' ? 'active' : ''} onClick={() => setActiveTab('new')}>New Requests</button>
                    <button className={activeTab === 'progress' ? 'active' : ''} onClick={() => setActiveTab('progress')}>In Progress</button>
                    <button className={activeTab === 'completed' ? 'active' : ''} onClick={() => setActiveTab('completed')}>Completed</button>
                </div>

                <div className="tab-content">
                    {activeTab === 'new' && <NewRequests userId={user.id} />}
                    {activeTab === 'progress' && <InProgress userId={user.id} />}
                    {activeTab === 'completed' && <Completed userId={user.id} />}
                </div>
            </div>
        </div>
    );
}

// ================ NEW REQUESTS (Fixed Photo Display) ================
function NewRequests({ userId }) {
    const [requests, setRequests] = useState([]);

    const fetchRequests = () => {
        axios.get('/personnel/orders/pending').then(res => setRequests(res.data)).catch(() => setRequests([]));
    };

    useEffect(() => {
        fetchRequests();
    }, []);

    const handleAccept = async (orderId) => {
        try {
            await axios.post(`/personnel/orders/${orderId}/accept`, { personnelId: userId });
            alert('Order accepted!');
            fetchRequests();
        } catch (err) {
            alert('Failed to accept order');
        }
    };

    const handleReject = async (orderId) => {
        const reason = prompt('Enter rejection reason:');
        if (!reason) return;

        try {
            await axios.post(`/personnel/orders/${orderId}/reject`, { rejectionReason: reason });
            alert('Order rejected');
            fetchRequests();
        } catch (err) {
            alert('Failed to reject order');
        }
    };

    return (
        <div className="requests-list">
            <h3>New Requests</h3>
            {requests.length === 0 ? (
                <p>No new requests</p>
            ) : (
                requests.map(req => (
                    <div key={req.id} className="request-card two-column-card">

                        {/* LEFT COLUMN */}
                        <div className="left-column">
                            <h4>Order #{req.id}</h4>

                            <p><strong>Student:</strong> {req.student.fullName}</p>
                            <p><strong>Hostel:</strong> {req.student.hostel}, Room {req.student.roomNumber}</p>
                            <p><strong>Phone:</strong> {req.student.phoneNumber}</p>

                            <p><strong>Service:</strong> {req.serviceType} ({req.urgencyDays} days)</p>
                            <p><strong>Total:</strong> ₹{req.totalPrice}</p>
                            <p><strong>Pickup:</strong> {req.pickupLocation}</p>

                            <div className="request-items">
                                <strong>Items:</strong>
                                {req.items.map((item, idx) => (
                                    <div key={idx}>{item.itemType} x{item.quantity}</div>
                                ))}
                            </div>
                        </div>

                        {/* RIGHT COLUMN */}
                        <div className="right-column">
                            <OrderPhotoGallery orderId={req.id} />
                        </div>

                        {/* BOTTOM ACTION BUTTONS (OUTSIDE GRID) */}
                        <div className="request-actions-bottom">
                            <button onClick={() => handleAccept(req.id)} className="btn-success">
                                Accept
                            </button>
                            <button onClick={() => handleReject(req.id)} className="btn-danger">
                                Reject
                            </button>
                        </div>

                    </div>

                ))
            )}
        </div>
    );
}

// ================ IN PROGRESS ================
// ================ IN PROGRESS (Two Column Layout) ================
function InProgress({ userId }) {
    const [orders, setOrders] = useState([]);

    const fetchOrders = () => {
        axios
            .get(`/personnel/orders/inprogress/${userId}`)
            .then(res => setOrders(res.data))
            .catch(() => setOrders([]));
    };

    useEffect(() => {
        fetchOrders();
    }, [userId]);

    const getNextStatus = (currentStatus) => {
        const flow = {
            'ACCEPTED': 'PENDING_COLLECTION',
            'PENDING_COLLECTION': 'WASHING',
            'WASHING': 'IRONING',
            'IRONING': 'DONE'
        };
        return flow[currentStatus];
    };

    const handleUpdateStatus = async (orderId, currentStatus) => {
        const next = getNextStatus(currentStatus);
        if (!next) return;

        try {
            await axios.put(`/personnel/orders/${orderId}/status`, { status: next });
            alert('Status updated!');
            fetchOrders();
        } catch (err) {
            alert('Failed to update status');
        }
    };

    return (
        <div className="requests-list">
            <h3>In Progress</h3>
            {orders.length === 0 ? (
                <p>No orders in progress</p>
            ) : (
                orders.map(order => (
                    <div key={order.id} className="request-card two-column-card">

                        {/* LEFT SIDE */}
                        <div className="left-column">
                            <h4>Order #{order.id}</h4>
                            <p><strong>Status:</strong> {order.status}</p>
                            <p><strong>Student:</strong> {order.student.fullName}</p>
                            <p><strong>Phone:</strong> {order.student.phoneNumber}</p>
                            <p><strong>Total:</strong> ₹{order.totalPrice}</p>

                            <div className="request-items">
                                <strong>Items:</strong>
                                {order.items.map((item, idx) => (
                                    <div key={idx}>{item.itemType} x{item.quantity}</div>
                                ))}
                            </div>
                        </div>

                        {/* RIGHT SIDE (PHOTOS) */}
                        <div className="right-column">
                            <OrderPhotoGallery orderId={order.id} />
                        </div>

                        {/* ACTION BUTTON */}
                        <div className="request-actions-bottom">
                            <button
                                className="btn-primary"
                                onClick={() => handleUpdateStatus(order.id, order.status)}
                            >
                                Update to {getNextStatus(order.status)}
                            </button>
                        </div>

                    </div>
                ))
            )}
        </div>
    );
}


// ================ COMPLETED (Two Column Layout) ================
function Completed({ userId }) {
    const [orders, setOrders] = useState([]);
    const [stats, setStats] = useState({ completedOrders: 0, totalEarnings: 0 });

    useEffect(() => {
        axios
            .get(`/personnel/orders/completed/${userId}`)
            .then(res => setOrders(res.data))
            .catch(() => setOrders([]));

        axios
            .get(`/personnel/stats/${userId}`)
            .then(res => setStats(res.data))
            .catch(() => {});
    }, [userId]);

    return (
        <div className="requests-list">

            {/* TOP STATS */}
            <div className="stats-summary">
                <div className="stat-card">
                    <h4>Completed Orders</h4>
                    <p className="stat-value">{stats.completedOrders}</p>
                </div>
                <div className="stat-card">
                    <h4>Total Earnings</h4>
                    <p className="stat-value">₹{stats.totalEarnings}</p>
                </div>
            </div>

            <h3>Completed Orders</h3>

            {orders.length === 0 ? (
                <p>No completed orders yet</p>
            ) : (
                orders.map(order => (
                    <div key={order.id} className="request-card two-column-card">

                        {/* LEFT SIDE */}
                        <div className="left-column">
                            <h4>Order #{order.id}</h4>
                            <p><strong>Student:</strong> {order.student.fullName}</p>
                            <p><strong>Total:</strong> ₹{order.totalPrice}</p>
                            <p><strong>Completed on:</strong> {order.updatedAt}</p>

                            <div className="request-items">
                                <strong>Items:</strong>
                                {order.items.map((item, idx) => (
                                    <div key={idx}>{item.itemType} x{item.quantity}</div>
                                ))}
                            </div>
                        </div>

                        {/* RIGHT SIDE (PHOTOS) */}
                        <div className="right-column">
                            <OrderPhotoGallery orderId={order.id} />
                        </div>

                        {/* ACTION (optional future btns) */}
                        <div className="request-actions-bottom">
                            <button className="btn-primary">
                                View Details
                            </button>
                        </div>

                    </div>
                ))
            )}
        </div>
    );
}


// ================ ADMIN DASHBOARD ================
function AdminDashboard() {
    const [stats, setStats] = useState(null);
    const [recentOrders, setRecentOrders] = useState([]);
    const navigate = useNavigate();

    useEffect(() => {
        const userData = JSON.parse(localStorage.getItem('user'));
        if (!userData || userData.role !== 'ADMIN') {
            navigate('/login');
            return;
        }

        axios.get('/admin/stats').then(res => setStats(res.data)).catch(()=>{});
        axios.get('/admin/orders/recent').then(res => setRecentOrders(res.data)).catch(()=>{});
    }, [navigate]);

    const handleLogout = () => {
        localStorage.removeItem('user');
        navigate('/');
    };

    if (!stats) return <div>Loading...</div>;

    return (
        <div className="dashboard">
            <nav className="dashboard-nav">
                <div className="logo">🧺 FreshFold Admin</div>
                <button onClick={handleLogout} className="btn-secondary">Logout</button>
            </nav>

            <div className="admin-content">
                <h2>Dashboard Overview</h2>

                <div className="stats-grid">
                    <div className="stat-card">
                        <h3>Total Orders</h3>
                        <p className="stat-value">{stats.totalOrders}</p>
                    </div>
                    <div className="stat-card">
                        <h3>Completed</h3>
                        <p className="stat-value">{stats.completedOrders}</p>
                    </div>
                    <div className="stat-card">
                        <h3>Pending</h3>
                        <p className="stat-value">{stats.pendingOrders}</p>
                    </div>
                    <div className="stat-card">
                        <h3>Total Revenue</h3>
                        <p className="stat-value">₹{stats.totalRevenue}</p>
                    </div>
                </div>

                <div className="reports-section">
                    <div className="report-box">
                        <h3>Hostel-wise Performance</h3>
                        <table>
                            <thead>
                            <tr><th>Hostel</th><th>Orders</th><th>Revenue</th></tr>
                            </thead>
                            <tbody>
                            {stats.hostelStats?.map(h => (
                                <tr key={h.hostelName}>
                                    <td>{h.hostelName}</td>
                                    <td>{h.orderCount}</td>
                                    <td>₹{h.revenue}</td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </div>

                    <div className="report-box">
                        <h3>Personnel Performance</h3>
                        <table>
                            <thead>
                            <tr><th>Name</th><th>Orders</th><th>Earnings</th><th>Rating</th></tr>
                            </thead>
                            <tbody>
                            {stats.personnelStats?.map(p => (
                                <tr key={p.name}>
                                    <td>{p.name}</td>
                                    <td>{p.ordersCompleted}</td>
                                    <td>₹{p.earnings}</td>
                                    <td>{'★'.repeat(Math.floor(p.rating))}</td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </div>
                </div>

                <div className="recent-orders">
                    <h3>Recent Orders</h3>
                    <table>
                        <thead>
                        <tr><th>ID</th><th>Student</th><th>Personnel</th><th>Status</th><th>Amount</th></tr>
                        </thead>
                        <tbody>
                        {recentOrders.map(o => (
                            <tr key={o.id}>
                                <td>#{o.id}</td>
                                <td>{o.student.fullName}</td>
                                <td>{o.personnel?.fullName || 'N/A'}</td>
                                <td>{o.status}</td>
                                <td>₹{o.totalPrice}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
}

// ================ MAIN APP ================
function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<LandingPage />} />
                <Route path="/login" element={<LoginPage />} />
                <Route path="/signup" element={<SignupPage />} />
                <Route path="/student/dashboard" element={<StudentDashboard />} />
                <Route path="/personnel/dashboard" element={<PersonnelDashboard />} />
                <Route path="/admin/dashboard" element={<AdminDashboard />} />
                <Route path="*" element={<Navigate to="/" />} />
            </Routes>
        </BrowserRouter>
    );
}

export default App;
