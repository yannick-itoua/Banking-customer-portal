# 🏦 Banking Customer Portal

A modern and secure banking portal developed with Spring Boot and Next.js, offering a complete digital banking management solution for financial institutions and thei### Environment Variables

**SECURITY NOTICE**: The project now uses environment variables for all sensitive configuration.

**Required Environment Variables**:
```bash
# JWT Configuration (CRITICAL - Generate unique values)
JWT_SECRET=your_secure_jwt_secret_base64_encoded_min_256_bits
JWT_EXPIRATION=86400000

# Database Configuration
POSTGRES_DB=banking_portal
POSTGRES_USER=banking_user
POSTGRES_PASSWORD=your_secure_database_password

# Spring Boot Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/banking_portal
SPRING_DATASOURCE_USERNAME=banking_user
SPRING_DATASOURCE_PASSWORD=your_secure_database_password
```

**Setup Process**:
1. Copy `.env.example` to `.env`
2. Generate secure values for all secrets
3. Never commit `.env` files to version control
4. Use different secrets for development/production# 🎯 Problem Solved in Society

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