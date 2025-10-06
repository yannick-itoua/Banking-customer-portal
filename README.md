# 🏦 Banking Customer Portal

A modern and secure banking portal developed with Spring Boot and Next.js, offering a complete digital banking management solution for financial institutions and their customers.

## 🚀 Railway Deployment Guide

This application is optimized for deployment on Railway with PostgreSQL. Follow this guide for production deployment.

### 📋 Project Architecture
```
Banking-customer-portal/
├── backend/                 # Spring Boot API (Java 17)
│   ├── src/main/java/
│   ├── pom.xml
│   ├── Dockerfile
│   └── start.sh            # Railway DATABASE_URL conversion
├── frontend/               # Next.js App (React 18)
│   ├── src/
│   ├── package.json
│   ├── server.js          # Custom Express server
│   └── Dockerfile
└── README.md
```

### 🗄️ Railway Database Setup

#### 1. Create PostgreSQL Database
1. In Railway dashboard: **New** → **Database** → **PostgreSQL**
2. Railway auto-generates connection details
3. Note the **DATABASE_URL** format: `postgresql://user:pass@host:port/db`

#### 2. Database Configuration
The backend is configured to automatically use Railway's `DATABASE_URL` environment variable:
- **Production**: Uses Railway's `DATABASE_URL` (auto-converted from `postgresql://` to `jdbc:postgresql://`)
- **Development**: Falls back to local PostgreSQL connection

### 🔧 Backend Deployment (Spring Boot)

#### 1. Create Backend Service
1. **New Service** → **GitHub Repo** → Select your repository
2. **Root Directory**: Set to `backend/`
3. Railway auto-detects the Dockerfile

#### 2. Environment Variables
Railway will automatically set:
- `DATABASE_URL` (linked from PostgreSQL service)
- `PORT` (Railway sets this automatically)

Optional environment variables:
```bash
JWT_SECRET=your_secure_jwt_secret_base64_encoded_min_256_bits
JWT_EXPIRATION=86400000
```

#### 3. Generate Domain
- **Settings** → **Networking** → **Generate Domain**
- Note the backend URL (e.g., `https://backend-prod.up.railway.app`)

### ⚛️ Frontend Deployment (Next.js)

#### 1. Create Frontend Service
1. **New Service** → **GitHub Repo** → Select your repository
2. **Root Directory**: Set to `frontend/`
3. Railway auto-detects the Dockerfile

#### 2. Environment Variables
**CRITICAL**: Set this environment variable:
```bash
NEXT_PUBLIC_API_URL=https://your-backend-domain.up.railway.app
```

#### 3. Port Configuration
**IMPORTANT**: Manually set port in Railway service settings:
- **Settings** → **Environment** → **PORT** = `8080`

#### 4. Generate Domain
- **Settings** → **Networking** → **Generate Domain**
- Your app will be available at this URL

### 🔗 Final Configuration

#### 1. Update Backend CORS (If Needed)
If you encounter CORS errors, update the backend's `SecurityConfig.java`:
```java
configuration.setAllowedOriginPatterns(Arrays.asList(
    "http://localhost:*",
    "https://localhost:*", 
    "https://*.railway.app",
    "https://*.up.railway.app",
    "https://your-frontend-domain.up.railway.app"  // Add your specific domain
));
```

#### 2. Health Checks
Both services include health check endpoints:
- **Backend**: `https://your-backend-domain.up.railway.app/api/debug/health`
- **Frontend**: `https://your-frontend-domain.up.railway.app/health`

### 📊 Final Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Next.js App   │    │  Spring Boot    │    │   PostgreSQL    │
│  (Frontend)     │◄──►│    (Backend)    │◄──►│   (Database)    │
│                 │    │                 │    │                 │
│ Port: 8080      │    │ Port: Auto      │    │ Port: 5432      │
│ Express Server  │    │ Tomcat Server   │    │ Railway Managed │
└─────────────────┘    └─────────────────┘    └─────────────────┘
       │                        │                        │
       │                        │                        │
   Railway                  Railway                  Railway
   Service 1                Service 2                Service 3
```

## 🔍 Troubleshooting

### Common Issues

**502 Errors**
- Cause: Railway can't detect your app port
- Solution: Manually set `PORT=8080` in frontend service settings

**CORS Errors**
- Cause: Backend doesn't allow frontend domain
- Solution: Add frontend domain to CORS configuration in `SecurityConfig.java`

**Database Connection Issues**
- Cause: Wrong URL format
- Solution: The `start.sh` script automatically converts Railway's `postgresql://` to `jdbc:postgresql://`

**Build Failures**
- Cause: Environment variables not available during build
- Solution: Set `NEXT_PUBLIC_API_URL` in Railway environment variables before deployment

---

## 🎯 Problem Solved in Society

### Challenges of Traditional Banking Institutions

Banks today face several major challenges:

- **📱 Digital Transformation**: Customers demand banking services accessible 24/7
- **🔒 Enhanced Security**: Need to protect sensitive financial data
- **⚡ User Experience**: Growing demand for modern and intuitive interfaces
- **📊 Administrative Management**: Need for efficient tools for client account management
- **🌐 Accessibility**: Banking services available from any device

### Our Solution

The **Banking Customer Portal** addresses these challenges by offering:

✅ **Modern Client Portal** - Intuitive interface for daily banking operations  
✅ **Administrator Dashboard** - Complete management tools for banking staff  
✅ **JWT Security** - Robust and secure authentication  
✅ **Transaction Management** - Detailed history with advanced filters  
✅ **Profile Management** - Secure updates of personal information  
✅ **Scalable Architecture** - Ready for production deployment  

## 🚀 Key Features

### For Clients
- 🔐 **Secure Login** with JWT authentication
- 💰 **Balance Consultation** in real-time
- 📋 **Transaction History** with filters by date, amount, and type
- 👤 **Profile Management** customizable
- 📱 **Responsive Interface** adapted to all devices

### For Administrators
- 👥 **Complete User Management**
- 📊 **Administrative Dashboard** with statistics
- 🔍 **Search and Filtering** of client accounts
- ⚙️ **Role Management** and permissions
- 📈 **Banking Activity Monitoring**

## 🛠️ Technologies Used

### Backend
- **Spring Boot 3.1.5** - Robust Java framework
- **Spring Security** - Security and JWT authentication
- **JPA/Hibernate** - Data management
- **PostgreSQL** - Relational database
- **Maven** - Dependency management

### Frontend
- **Next.js 14** - Modern React framework
- **TypeScript** - Static typing for robustness
- **Tailwind CSS** - Modern design system
- **React Context** - Global state management
- **Axios** - API communication

### Infrastructure
- **Docker & Docker Compose** - Containerization
- **PostgreSQL 15** - Containerized database
- **Nginx** (production ready)

## 🚀 Installation and Setup

### Prerequisites
- Docker and Docker Compose installed
- Git to clone the project

### Quick Start

```bash
# Clone the project
git clone https://github.com/yannick-itoua/Banking-customer-portal.git
cd Banking-customer-portal

# IMPORTANT: Set up environment variables for security
cp .env.example .env
# Edit .env file with your production values, especially JWT_SECRET

# Start all services
docker-compose up --build

# Or in background
docker-compose up -d --build
```

### ⚠️ Security Setup (REQUIRED)

**CRITICAL**: This application requires environment variables for security:

1. **Copy the environment template**:
   ```bash
   cp .env.example .env
   ```

2. **Generate a new JWT secret**:
   ```bash
   # Using OpenSSL (recommended)
   openssl rand -base64 64
   
   # Or using PowerShell
   [Convert]::ToBase64String([Text.Encoding]::UTF8.GetBytes((1..64|%{[char](Get-Random -Min 65 -Max 122)})-join''))
   ```

3. **Update your .env file** with the new JWT secret and secure database passwords

4. **Never commit .env files** - they are excluded in .gitignore for security

### Service Access

- **Frontend (Client Interface)**: http://localhost:3000
- **Backend API**: http://localhost:8080
- **Database**: localhost:5432

## 👥 Demo Accounts

### Administrator
- **Username**: `admin`
- **Password**: `admin123`
- **Access**: Admin dashboard + all features

### Test Client
- **Username**: `client`
- **Password**: `client123`
- **Access**: Standard client interface

## 📁 Project Structure

```
Banking-customer-portal/
├── backend/                 # Spring Boot application
│   ├── src/main/java/
│   │   └── com/banking/
│   │       ├── config/     # Security configuration
│   │       ├── controller/ # REST API endpoints
│   │       ├── model/      # JPA entities
│   │       ├── repository/ # Data access
│   │       └── service/    # Business logic
│   └── pom.xml
├── frontend/               # Next.js application
│   ├── app/               # Next.js pages and layouts
│   ├── components/        # Reusable React components
│   ├── contexts/         # React state management
│   ├── lib/              # Utilities and API client
│   └── package.json
└── docker-compose.yml     # Service orchestration
```

## 🔐 Security

### Implemented Security Measures

- **🔒 JWT Authentication** - Secure tokens with expiration (configurable via environment)
- **🛡️ CORS Protection** - Restrictive configuration
- **🔐 Password Hashing** - BCrypt for storage
- **🚫 Input Validation** - Protection against injections
- **👤 Role-based Authorization** - Access based on permissions
- **🌐 HTTPS Ready** - Ready for secure deployment
- **🔑 Environment Variables** - No hardcoded secrets in code
- **📋 Security Audit** - Git history cleaned of exposed secrets

## 📊 API Endpoints

### Authentication
```
POST /api/auth/login     # User login
POST /api/auth/logout    # User logout
```

### Account Management
```
GET  /api/accounts       # List accounts
GET  /api/accounts/{id}  # Account details
```

### Transactions
```
GET  /api/transactions           # List transactions
GET  /api/transactions/filter    # Filtered transactions
```

### User Profile
```
GET  /api/user/profile   # Profile information
PUT  /api/user/profile   # Profile update
```

### Administration (Admin only)
```
GET  /api/admin/users        # List users
GET  /api/admin/users/{id}   # User details
DELETE /api/admin/users/{id} # Delete user
```

## 🔧 Configuration

### Environment Variables

The project uses the following configurations:

```yaml
# Database
POSTGRES_DB: banking_db
POSTGRES_USER: banking_user
POSTGRES_PASSWORD: banking_password

# Spring Boot Backend
SERVER_PORT: 8080
JWT_SECRET: your-secret-key

# Next.js Frontend
NEXT_PUBLIC_API_URL: http://localhost:8080
```

## 🧪 Testing

### Feature Testing

The project has been comprehensively tested:

- ✅ Admin and client authentication
- ✅ Transaction management with filters
- ✅ User profile updates
- ✅ Administration interface
- ✅ API security
- ✅ Responsive design

### Running Tests

```bash
# Backend tests
cd backend
./mvnw test

# Frontend tests
cd frontend
npm test
```

## 🚀 Production Deployment

### Production Preparation

1. **SSL/HTTPS Configuration**
2. **Secure environment variables**
3. **Monitoring and logs**
4. **Automatic backups**
5. **Load balancing** if necessary

### Docker in Production

```bash
# Optimized build for production
docker-compose -f docker-compose.prod.yml up -d
```

## 🤝 Contributing

Contributions are welcome! Here's how to contribute:

1. Fork the project
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is under MIT license. See the `LICENSE` file for more details.

## 👨‍💻 Author

**Yannick Itoua**
- GitHub: [@yannick-itoua](https://github.com/yannick-itoua)
- LinkedIn: [Yannick Itoua](https://linkedin.com/in/yannick-itoua)

## 🙏 Acknowledgments

- Spring Boot Community for the excellent framework
- Next.js Team for React innovation
- PostgreSQL for database robustness
- Docker for deployment simplification

---

## 🔧 Support & Maintenance

For any questions or issues:

1. Check the documentation
2. Review [GitHub Issues](https://github.com/yannick-itoua/Banking-customer-portal/issues)
3. Create a new issue if necessary

**Made with ❤️ for modern banking solutions**