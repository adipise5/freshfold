<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>FreshFold - Laundry Management System</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Poppins', sans-serif;
            color: #333;
            background: #f9fafb;
            scroll-behavior: smooth;
        }

        /* Navigation */
        nav {
            background: #fff;
            box-shadow: 0 2px 8px rgba(0,0,0,0.08);
            padding: 1rem 0;
            position: sticky;
            top: 0;
            z-index: 100;
            transition: 0.3s ease;
        }

        nav.scrolled {
            background: #2563eb;
        }

        .nav-container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 0 2rem;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .logo {
            font-size: 1.8rem;
            font-weight: 700;
            color: #2563eb;
            letter-spacing: 1px;
        }

        nav.scrolled .logo {
            color: #fff;
        }

        .nav-buttons button {
            margin-left: 1rem;
            padding: 0.6rem 1.5rem;
            border: 2px solid #2563eb;
            background: #fff;
            color: #2563eb;
            border-radius: 8px;
            cursor: pointer;
            font-size: 1rem;
            font-weight: 600;
            transition: all 0.3s ease;
        }

        .nav-buttons button:hover {
            background: #2563eb;
            color: #fff;
            transform: translateY(-2px);
        }

        nav.scrolled .nav-buttons button {
            border-color: #fff;
            color: #fff;
            background: transparent;
        }

        nav.scrolled .nav-buttons button:hover {
            background: #fff;
            color: #2563eb;
        }

        /* Hero Section */
        .hero {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 7rem 2rem;
            text-align: center;
            background-attachment: fixed;
        }

        .hero h1 {
            font-size: 3.2rem;
            margin-bottom: 1rem;
            animation: fadeInDown 1s;
        }

        .hero p {
            font-size: 1.2rem;
            margin-bottom: 2rem;
            opacity: 0.9;
            animation: fadeInUp 1s;
        }

        .hero-buttons {
            display: flex;
            gap: 1rem;
            justify-content: center;
        }

        .btn-primary {
            padding: 1rem 2.5rem;
            background: #fff;
            color: #667eea;
            border: none;
            border-radius: 10px;
            font-size: 1.1rem;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s;
            box-shadow: 0 5px 15px rgba(0,0,0,0.15);
        }

        .btn-primary:hover {
            transform: translateY(-3px);
            box-shadow: 0 15px 25px rgba(0,0,0,0.2);
        }

        .btn-secondary {
            padding: 1rem 2.5rem;
            background: transparent;
            color: #fff;
            border: 2px solid #fff;
            border-radius: 10px;
            font-size: 1.1rem;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s;
        }

        .btn-secondary:hover {
            background: #fff;
            color: #667eea;
        }

        /* Features Section */
        .features {
            max-width: 1200px;
            margin: 5rem auto;
            padding: 0 2rem;
            text-align: center;
        }

        .features h2 {
            font-size: 2.5rem;
            margin-bottom: 3rem;
            color: #2563eb;
            position: relative;
        }

        .feature-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
            gap: 2rem;
        }

        .feature-card {
            background: #fff;
            padding: 2rem;
            border-radius: 15px;
            box-shadow: 0 5px 20px rgba(0,0,0,0.08);
            transition: transform 0.3s, box-shadow 0.3s;
        }

        .feature-card:hover {
            transform: translateY(-10px);
            box-shadow: 0 10px 30px rgba(0,0,0,0.12);
        }

        .feature-icon {
            font-size: 3rem;
            margin-bottom: 1rem;
            color: #2563eb;
        }

        /* Testimonials Section */
        .testimonials {
            background: #eef2ff;
            padding: 5rem 2rem;
            text-align: center;
        }

        .testimonials h2 {
            font-size: 2.5rem;
            margin-bottom: 2.5rem;
            color: #2563eb;
        }

        .testimonial-cards {
            display: flex;
            flex-wrap: wrap;
            justify-content: center;
            gap: 2rem;
        }

        .testimonial {
            background: #fff;
            padding: 1.5rem 2rem;
            border-radius: 15px;
            max-width: 350px;
            box-shadow: 0 5px 20px rgba(0,0,0,0.08);
            font-style: italic;
        }

        .testimonial span {
            display: block;
            margin-top: 1rem;
            font-weight: 600;
            color: #2563eb;
        }

        /* Contact Section */
        .cta {
            text-align: center;
            padding: 5rem 2rem;
            background: linear-gradient(135deg, #2563eb, #3b82f6);
            color: white;
        }

        .cta h2 {
            font-size: 2rem;
            margin-bottom: 1rem;
        }

        .cta p {
            margin-bottom: 2rem;
            opacity: 0.9;
        }

        /* Footer */
        footer {
            background: #1e293b;
            color: #fff;
            text-align: center;
            padding: 2rem;
            margin-top: 0;
        }

        footer p {
            font-size: 0.95rem;
        }

        @keyframes fadeInDown {
            from { opacity: 0; transform: translateY(-30px); }
            to { opacity: 1; transform: translateY(0); }
        }

        @keyframes fadeInUp {
            from { opacity: 0; transform: translateY(30px); }
            to { opacity: 1; transform: translateY(0); }
        }

        @media (max-width: 768px) {
            .hero h1 { font-size: 2.2rem; }
            .hero p { font-size: 1rem; }
            .hero-buttons { flex-direction: column; }
        }
    </style>
</head>
<body>

<nav id="navbar">
    <div class="nav-container">
        <div class="logo">🧺 FreshFold</div>
        <div class="nav-buttons">
            <button onclick="location.href='login.html'">Login</button>
            <button onclick="location.href='signup.html'">Sign Up</button>
        </div>
    </div>
</nav>

<section class="hero">
    <h1>Welcome to FreshFold</h1>
    <p>Smart Laundry Management for BITS Pilani Hostels</p>
    <div class="hero-buttons">
        <button class="btn-primary" onclick="location.href='signup.html'">Get Started</button>
        <button class="btn-secondary" onclick="location.href='login.html'">Login</button>
    </div>
</section>

<section class="features">
    <h2>Why Choose FreshFold?</h2>
    <div class="feature-grid">
        <div class="feature-card">
            <div class="feature-icon">⚡</div>
            <h3>Quick Service</h3>
            <p>Normal delivery in 3 days, urgent service available in 1-2 days.</p>
        </div>
        <div class="feature-card">
            <div class="feature-icon">👥</div>
            <h3>Trusted Personnel</h3>
            <p>Experienced laundry professionals with verified ratings.</p>
        </div>
        <div class="feature-card">
            <div class="feature-icon">🔒</div>
            <h3>Secure Tracking</h3>
            <p>Track your laundry at every step with photo verification.</p>
        </div>
        <div class="feature-card">
            <div class="feature-icon">✨</div>
            <h3>Premium Quality</h3>
            <p>Professional washing and ironing for all clothing types.</p>
        </div>
    </div>
</section>

<section class="testimonials">
    <h2>What Students Say</h2>
    <div class="testimonial-cards">
        <div class="testimonial">
            “The best laundry service I've used on campus. Super quick and reliable!”
            <span>— Priya, BITS Pilani</span>
        </div>
        <div class="testimonial">
            “FreshFold made my hostel life easier. I can track everything from my phone!”
            <span>— Rohan, CS Department</span>
        </div>
        <div class="testimonial">
            “Great support team and quality service. Highly recommend!”
            <span>— Aditi, ME Student</span>
        </div>
    </div>
</section>

<section class="cta">
    <h2>Ready to Experience Hassle-Free Laundry?</h2>
    <p>Join hundreds of BITS students using FreshFold today!</p>
    <button class="btn-primary" onclick="location.href='signup.html'">Sign Up Now</button>
</section>

<footer>
    <p>© 2025 FreshFold - BITS Pilani Laundry Management System<br>Developed by <strong>@adipise05</strong></p>
</footer>

<script>
    // Navbar scroll effect
    window.addEventListener("scroll", function() {
        const navbar = document.getElementById("navbar");
        navbar.classList.toggle("scrolled", window.scrollY > 50);
    });
</script>

</body>
</html>
