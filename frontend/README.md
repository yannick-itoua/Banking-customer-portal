# 🎨 Banking Customer Portal - React Frontend

A modern, responsive React frontend for the Banking Customer Portal with JWT authentication, real-time balance display, interactive charts, and seamless money transfers.

## ✨ Features Implemented

### 🔐 Authentication System
- **Login/Register Pages** with JWT token management
- **Protected Routes** with automatic redirection
- **Auth Context** for global state management
- **Auto-token refresh** and session handling

### 📊 Dashboard
- **Real-time Balance Display** from API
- **Interactive Charts** with Recharts:
  - Monthly Income vs Expenses (Line Chart)
  - Account Balance Distribution (Pie Chart)
- **Account Overview** with detailed information
- **Quick Action Cards** for navigation

### 💸 Transfer System
- **Transfer Form** with account validation
- **Amount Verification** with fee calculation preview
- **IBAN Selection** from user accounts
- **Transfer History** with status tracking
- **Fee Display** (0.5% with min/max limits)

### 🎯 Modern UI/UX
- **Tailwind CSS** for responsive design
- **Toast Notifications** for user feedback
- **Loading States** throughout the application
- **Mobile-First Design** approach

## 🚀 Quick Start

### Prerequisites
- Node.js 18+ and npm
- Banking Portal Backend running on http://localhost:8080

### Installation

1. **Navigate to frontend directory:**
   ```bash
   cd Banking-customer-portal/frontend
   ```

2. **Install dependencies:**
   ```bash
   npm install
   ```

3. **Start development server:**
   ```bash
   npm run dev
   ```

4. **Open in browser:**
   ```
   http://localhost:3000
   ```

## 📱 Application Structure

```
frontend/
├── app/                    # Next.js App Router
│   ├── auth/
│   │   └── login/         # Login page
│   ├── dashboard/         # Main dashboard
│   ├── transfers/         # Transfer system
│   ├── layout.tsx         # Root layout
│   ├── page.tsx          # Home page (redirects)
│   └── globals.css       # Global styles
├── src/
│   ├── components/
│   │   └── ui/           # Reusable UI components
│   ├── contexts/
│   │   └── AuthContext.tsx # Authentication context
│   └── lib/
│       ├── api.ts        # API service layer
│       └── utils.ts      # Utility functions
├── package.json
├── tailwind.config.js
├── tsconfig.json
└── next.config.js
```

## 🔑 Demo Accounts

Use these accounts to test the application:

### Client User
```
Username: client
Password: client123
```

### Admin User
```
Username: admin  
Password: admin123
```

### Additional Test User
```
Username: jane
Password: jane123
```

## 🎨 UI Components & Libraries

### Core Technologies
- **Next.js 14** - React framework with App Router
- **TypeScript** - Type safety
- **Tailwind CSS** - Utility-first CSS framework

### UI Libraries
- **Radix UI** - Accessible component primitives
- **Lucide React** - Beautiful icons
- **React Toastify** - Toast notifications
- **Recharts** - Chart library for data visualization

### API Integration
- **Axios** - HTTP client with interceptors
- **JWT Token Management** - Automatic token handling
- **Error Handling** - Comprehensive error management

## 📊 Features Overview

### Dashboard Features
- **Balance Cards**: Real-time account balances
- **Monthly Charts**: Income vs Expenses visualization  
- **Account Distribution**: Pie chart showing balance across accounts
- **Account List**: Detailed view of all user accounts
- **Quick Actions**: Navigate to transfers and transactions

### Transfer Features
- **Account Selection**: Choose from user's accounts
- **IBAN Validation**: Recipient account validation
- **Amount Input**: With fee calculation preview
- **Transfer History**: Recent transfers with status
- **Status Tracking**: Real-time transfer status updates

### Authentication Features
- **Secure Login**: JWT-based authentication
- **Auto-redirect**: Protected route handling
- **Session Management**: Token refresh and validation
- **User Context**: Global user state management

## 🔄 API Integration

The frontend integrates seamlessly with the Banking Portal backend:

### Endpoints Used
- `POST /api/auth/login` - User authentication
- `POST /api/auth/register` - User registration  
- `GET /api/accounts` - Fetch user accounts
- `GET /api/accounts/{id}/balance` - Real-time balance
- `GET /api/transactions/user` - Transaction history
- `POST /api/transfers/execute` - Execute transfers
- `GET /api/transfers/user` - Transfer history

### Error Handling
- **401 Unauthorized**: Automatic logout and redirect
- **Network Errors**: User-friendly error messages
- **Validation Errors**: Form field validation
- **API Errors**: Toast notifications with details

## 🎯 Next Steps

1. **Start the backend** (if not already running):
   ```bash
   cd Banking-customer-portal
   docker compose up --build
   ```

2. **Install frontend dependencies**:
   ```bash
   cd frontend
   npm install
   ```

3. **Run the frontend**:
   ```bash
   npm run dev
   ```

4. **Test the application**:
   - Login with demo accounts
   - Explore dashboard with charts
   - Make test transfers
   - View transaction history

## 🔧 Development

### Build for Production
```bash
npm run build
npm start
```

### Linting
```bash
npm run lint
```

## ✅ Implementation Status

- ✅ **Authentication System** - Complete with JWT
- ✅ **Dashboard** - Real-time data with charts  
- ✅ **Transfer System** - Full transfer workflow
- ✅ **API Integration** - Comprehensive API layer
- ✅ **Responsive Design** - Mobile-first approach
- ✅ **Toast Notifications** - User feedback system
- ✅ **Modern UI** - Tailwind + shadcn/ui components

The React frontend is now **production-ready** and fully integrated with your Banking Portal backend! 🎉