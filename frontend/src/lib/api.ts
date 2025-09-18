import axios from 'axios';

export interface User {
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  role: 'CLIENT' | 'ADMIN';
}

export interface Account {
  id: number;
  iban: string;
  balance: number;
  accountType: 'CHECKING' | 'SAVINGS';
  accountName: string;
  createdAt: string;
  isActive: boolean;
}

export interface Transaction {
  id: number;
  amount: number;
  transactionType: 'DEBIT' | 'CREDIT';
  description: string;
  referenceNumber: string;
  transactionDate: string;
  balanceAfter: number;
  transferFee?: number;
}

export interface Transfer {
  id: number;
  amount: number;
  transferFee: number;
  fromIban: string;
  toIban: string;
  beneficiaryName: string;
  description: string;
  referenceNumber: string;
  status: 'PENDING' | 'COMPLETED' | 'FAILED' | 'CANCELLED';
  createdAt: string;
  processedAt?: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
  email: string;
  firstName: string;
  lastName: string;
}

export interface LoginResponse {
  token: string;
  type: string;
  id: number;
  username: string;
  email: string;
  role: string;
}

export interface TransferRequest {
  fromIban: string;
  amount: number;
  toIban: string;
  beneficiaryName: string;
  description: string;
}

export interface AccountCreationRequest {
  accountName: string;
  accountType: 'CHECKING' | 'SAVINGS';
  initialBalance?: number;
  userId: number;
}

// API Configuration
const API_BASE_URL = 'http://localhost:8080'; // Always use localhost for browser requests

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add auth token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('authToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor to handle auth errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('authToken');
      localStorage.removeItem('user');
      window.location.href = '/auth/login';
    }
    return Promise.reject(error);
  }
);

// Auth API
export const authAPI = {
  login: (credentials: LoginRequest): Promise<LoginResponse> =>
    api.post('/api/auth/login', credentials).then(res => res.data),
  
  register: (userData: RegisterRequest): Promise<string> =>
    api.post('/api/auth/register', userData).then(res => res.data),
  
  refreshToken: (): Promise<LoginResponse> =>
    api.post('/api/auth/refresh').then(res => res.data),
};

// Accounts API
export const accountsAPI = {
  getUserAccounts: (): Promise<Account[]> =>
    api.get('/api/accounts').then(res => res.data),
  
  getAccountById: (accountId: number): Promise<Account> =>
    api.get(`/api/accounts/${accountId}`).then(res => res.data),
  
  getAccountByIban: (iban: string): Promise<Account> =>
    api.get(`/api/accounts/iban/${iban}`).then(res => res.data),
  
  getAccountBalance: (accountId: number): Promise<number> =>
    api.get(`/api/accounts/${accountId}/balance`).then(res => res.data),
};

// Transactions API
export const transactionsAPI = {
  getAccountTransactions: (accountId: number, page = 0, size = 10): Promise<{
    content: Transaction[];
    totalPages: number;
    totalElements: number;
  }> =>
    api.get(`/api/transactions/account/${accountId}?page=${page}&size=${size}`).then(res => res.data),
  
  getUserTransactions: (): Promise<Transaction[]> =>
    api.get('/api/transactions/user').then(res => res.data),
  
  getTransactionById: (transactionId: number): Promise<Transaction> =>
    api.get(`/api/transactions/${transactionId}`).then(res => res.data),
};

// Transfers API
export const transfersAPI = {
  executeTransfer: (transferData: TransferRequest): Promise<Transfer> =>
    api.post('/api/transfers/execute', transferData).then(res => res.data),
  
  getUserTransfers: (page = 0, size = 10): Promise<{
    content: Transfer[];
    totalPages: number;
    totalElements: number;
  }> =>
    api.get(`/api/transfers/user?page=${page}&size=${size}`).then(res => res.data),
  
  getTransferById: (transferId: number): Promise<Transfer> =>
    api.get(`/api/transfers/${transferId}`).then(res => res.data),
  
  cancelTransfer: (transferId: number, reason: string): Promise<Transfer> =>
    api.post(`/api/transfers/${transferId}/cancel?reason=${encodeURIComponent(reason)}`).then(res => res.data),
};

// Admin API endpoints
export const adminAPI = {
  // Admin Accounts
  getAllAccounts: (): Promise<Account[]> =>
    api.get('/api/accounts/all').then(res => res.data),
  
  createAccount: (accountData: AccountCreationRequest): Promise<Account> =>
    api.post('/api/accounts', accountData).then(res => res.data),
  
  // Admin Transactions
  getAllTransactions: (): Promise<Transaction[]> =>
    api.get('/api/transactions/all').then(res => res.data),
  
  reverseTransaction: (transactionId: number, reason: string): Promise<Transaction> =>
    api.post(`/api/transactions/${transactionId}/reverse?reason=${encodeURIComponent(reason)}`).then(res => res.data),
  
  // Admin Transfers
  getAllTransfers: (): Promise<Transfer[]> =>
    api.get('/api/transfers/all').then(res => res.data),
  
  getPendingTransfers: (): Promise<Transfer[]> =>
    api.get('/api/transfers/pending').then(res => res.data),
  
  // Admin Users (Note: These endpoints don't exist yet in backend)
  getAllUsers: (): Promise<User[]> =>
    api.get('/api/admin/users').then(res => res.data).catch(() => {
      console.warn('Admin users endpoint not implemented yet');
      return [];
    }),
  
  getUserById: (userId: number): Promise<User> =>
    api.get(`/api/admin/users/${userId}`).then(res => res.data).catch(() => {
      console.warn('Admin user by ID endpoint not implemented yet');
      return {} as User;
    }),
  
  updateUser: (userId: number, userData: Partial<User>): Promise<User> =>
    api.put(`/api/admin/users/${userId}`, userData).then(res => res.data).catch(() => {
      console.warn('Admin update user endpoint not implemented yet');
      return {} as User;
    }),
  
  deleteUser: (userId: number): Promise<void> =>
    api.delete(`/api/admin/users/${userId}`).then(res => res.data).catch(() => {
      console.warn('Admin delete user endpoint not implemented yet');
      return;
    }),
};

// Profile API
export interface ProfileUpdateRequest {
  firstName: string;
  lastName: string;
  email: string;
}

export interface ProfileResponse {
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  role: string;
}

export const profileAPI = {
  getProfile: (): Promise<ProfileResponse> =>
    api.get('/api/user/profile').then(res => res.data),
  
  updateProfile: (profileData: ProfileUpdateRequest): Promise<{
    message: string;
    user: ProfileResponse;
  }> =>
    api.put('/api/user/profile', profileData).then(res => res.data),
};

export default api;