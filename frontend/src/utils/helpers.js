<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - FreshFold</title>
    <link rel="stylesheet" href="css/style.css">
    <link rel="stylesheet" href="css/auth.css">
</head>
<body>
<div class="auth-container">
    <div class="auth-card">
        <div class="auth-header">
            <h1>🧺 FreshFold</h1>
            <p>Login to Your Account</p>
        </div>

        <div class="tabs">
            <button class="tab active" onclick="switchLoginTab('student')">Student</button>
            <button class="tab" onclick="switchLoginTab('personnel')">Personnel</button>
            <button class="tab" onclick="switchLoginTab('admin')">Admin</button>
        </div>

        <!-- Student Login -->
        <div id="student-login" class="tab-content active">
            <form onsubmit="handleLogin(event, 'student')">
                <div class="form-group">
                    <label for="student-email">BITS Email ID</label>
                    <input type="email" id="student-email" name="email" required
                           placeholder="your.email@pilani.bits-pilani.ac.in">
                </div>
                <div class="form-group">
                    <label for="student-password">Password</label>
                    <input type="password" id="student-password" name="password" required
                           placeholder="Enter your password">
                </div>
                <button type="submit" class="btn btn-primary" id="student-login-btn">Login as Student</button>
                <div class="form-footer">
                    Don't have an account? <a href="signup.html">Sign up</a>
                </div>
            </form>
        </div>

        <!-- Personnel Login -->
        <div id="personnel-login" class="tab-content">
            <form onsubmit="handleLogin(event, 'personnel')">
                <div class="form-group">
                    <label for="personnel-email">Email</label>
                    <input type="email" id="personnel-email" name="email" required
                           placeholder="your.email@freshfold.com">
                </div>
                <div class="form-group">
                    <label for="personnel-password">Password</label>
                    <input type="password" id="personnel-password" name="password" required
                           placeholder="Enter your password">
                </div>
                <button type="submit" class="btn btn-primary" id="personnel-login-btn">Login as Personnel</button>
                <div class="form-footer">
                    Don't have an account? <a href="signup.html">Sign up</a>
                </div>
            </form>
        </div>

        <!-- Admin Login -->
        <div id="admin-login" class="tab-content">
            <form onsubmit="handleLogin(event, 'admin')">
                <div class="form-group">
                    <label for="admin-email">Email</label>
                    <input type="email" id="admin-email" name="email" required
                           placeholder="admin@freshfold.com">
                </div>
                <div class="form-group">
                    <label for="admin-password">Password</label>
                    <input type="password" id="admin-password" name="password" required
                           placeholder="Enter admin password">
                </div>
                <button type="submit" class="btn btn-primary" id="admin-login-btn">Login as Admin</button>
            </form>
        </div>

        <a href="index.html" class="back-link">← Back to Home</a>
    </div>
</div>

<!-- Firebase SDKs -->
<script src="https://www.gstatic.com/firebasejs/10.7.1/firebase-app-compat.js"></script>
<script src="https://www.gstatic.com/firebasejs/10.7.1/firebase-auth-compat.js"></script>
<script src="https://www.gstatic.com/firebasejs/10.7.1/firebase-firestore-compat.js"></script>
<script src="https://www.gstatic.com/firebasejs/10.7.1/firebase-storage-compat.js"></script>

<!-- Firebase Config -->
<script src="js/firebase-config.js"></script>

<script>
    function switchLoginTab(tabName) {
        document.querySelectorAll('.tab-content').forEach(content => {
            content.classList.remove('active');
        });
        document.querySelectorAll('.tab').forEach(tab => {
            tab.classList.remove('active');
        });

        document.getElementById(tabName + '-login').classList.add('active');
        event.target.classList.add('active');
    }

    async function handleLogin(event, userType) {
        event.preventDefault();

        const formData = new FormData(event.target);
        const email = formData.get('email');
        const password = formData.get('password');

        const submitBtn = event.target.querySelector('button[type="submit"]');
        const originalText = submitBtn.textContent;
        submitBtn.textContent = 'Logging in...';
        submitBtn.disabled = true;

        try {
            // Firebase sign in
            const result = await FirebaseHelper.signIn(email, password);

            if (result.success && result.userData) {
                const userData = result.userData;

                // Verify user type matches
                if (userData.userType.toUpperCase() !== userType.toUpperCase()) {
                    alert('Please login with the correct user type');
                    submitBtn.textContent = originalText;
                    submitBtn.disabled = false;
                    return;
                }

                // Store user data in localStorage
                localStorage.setItem('userUid', result.user.uid);
                localStorage.setItem('userType', userData.userType);
                localStorage.setItem('userName', userData.fullName);
                localStorage.setItem('userEmail', userData.email);

                // Get role-specific data and redirect
                if (userData.userType === 'STUDENT') {
                    const studentData = await FirebaseHelper.getStudentData(result.user.uid);
                    if (studentData) {
                        localStorage.setItem('hostel', studentData.hostel);
                        localStorage.setItem('roomNumber', studentData.roomNumber);
                    }
                    window.location.href = 'student-dashboard.html';
                } else if (userData.userType === 'PERSONNEL') {
                    const personnelData = await FirebaseHelper.getPersonnelData(result.user.uid);
                    if (personnelData) {
                        localStorage.setItem('employeeId', personnelData.employeeId);
                    }
                    window.location.href = 'personnel-dashboard.html';
                } else if (userData.userType === 'ADMIN') {
                    window.location.href = 'admin-dashboard.html';
                }
            } else {
                alert('Login failed: ' + (result.error || 'Invalid credentials'));
                submitBtn.textContent = originalText;
                submitBtn.disabled = false;
            }
        } catch (error) {
            console.error('Login error:', error);
            alert('Login error: ' + error.message);
            submitBtn.textContent = originalText;
            submitBtn.disabled = false;
        }
    }
</script>
</body>
</html>