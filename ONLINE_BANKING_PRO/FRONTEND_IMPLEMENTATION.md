# Frontend Implementation - React/TypeScript Online Banking Application

**Version:** 1.0.0  
**Technology:** React 18, TypeScript 5, Redux Toolkit, Material-UI  
**Status:** Production-Ready Template

---

## Project Structure

```
frontend/
├── public/
│   ├── index.html
│   └── favicon.ico
├── src/
│   ├── index.tsx
│   ├── App.tsx
│   ├── components/
│   │   ├── common/
│   │   │   ├── Navbar.tsx
│   │   │   ├── Sidebar.tsx
│   │   │   ├── Footer.tsx
│   │   │   ├── Loading.tsx
│   │   │   └── ErrorBoundary.tsx
│   │   ├── auth/
│   │   │   ├── LoginForm.tsx
│   │   │   ├── RegisterForm.tsx
│   │   │   ├── OtpVerification.tsx
│   │   │   └── ForgotPassword.tsx
│   │   ├── dashboard/
│   │   │   ├── Dashboard.tsx
│   │   │   ├── AccountSummary.tsx
│   │   │   ├── RecentTransactions.tsx
│   │   │   └── QuickActions.tsx
│   │   ├── accounts/
│   │   │   ├── AccountList.tsx
│   │   │   ├── AccountDetails.tsx
│   │   │   ├── OpenAccount.tsx
│   │   │   └── AccountSettings.tsx
│   │   ├── transactions/
│   │   │   ├── TransferForm.tsx
│   │   │   ├── TransactionHistory.tsx
│   │   │   ├── StatementDownload.tsx
│   │   │   └── ScheduledTransfers.tsx
│   │   ├── beneficiaries/
│   │   │   ├── BeneficiaryList.tsx
│   │   │   ├── AddBeneficiary.tsx
│   │   │   └── BeneficiaryManage.tsx
│   │   ├── cards/
│   │   │   ├── CardList.tsx
│   │   │   ├── RequestCard.tsx
│   │   │   └── CardSettings.tsx
│   │   ├── loans/
│   │   │   ├── LoanApplication.tsx
│   │   │   ├── LoanTracker.tsx
│   │   │   └── RepaymentSchedule.tsx
│   │   ├── analytics/
│   │   │   ├── SpendingAnalysis.tsx
│   │   │   ├── Charts.tsx
│   │   │   └── Insights.tsx
│   │   └── chatbot/
│   │       └── ChatWidget.tsx
│   ├── pages/
│   │   ├── LoginPage.tsx
│   │   ├── RegisterPage.tsx
│   │   ├── DashboardPage.tsx
│   │   ├── AccountsPage.tsx
│   │   ├── TransactionsPage.tsx
│   │   ├── CardsPage.tsx
│   │   ├── LoansPage.tsx
│   │   ├── AnalyticsPage.tsx
│   │   └── ProfilePage.tsx
│   ├── store/
│   │   ├── index.ts
│   │   ├── slices/
│   │   │   ├── authSlice.ts
│   │   │   ├── accountSlice.ts
│   │   │   ├── transactionSlice.ts
│   │   │   ├── uiSlice.ts
│   │   │   └── notificationSlice.ts
│   │   └── thunks/
│   │       ├── authThunks.ts
│   │       ├── accountThunks.ts
│   │       └── transactionThunks.ts
│   ├── services/
│   │   ├── api.ts
│   │   ├── authService.ts
│   │   ├── accountService.ts
│   │   ├── transactionService.ts
│   │   ├── cardService.ts
│   │   └── analyticsService.ts
│   ├── hooks/
│   │   ├── useAuth.ts
│   │   ├── useAccount.ts
│   │   ├── useTransaction.ts
│   │   ├── useNotification.ts
│   │   └── useFetch.ts
│   ├── types/
│   │   ├── user.ts
│   │   ├── account.ts
│   │   ├── transaction.ts
│   │   ├── card.ts
│   │   ├── loan.ts
│   │   └── api.ts
│   ├── utils/
│   │   ├── formatters.ts
│   │   ├── validators.ts
│   │   ├── constants.ts
│   │   └── storage.ts
│   ├── styles/
│   │   ├── theme.ts
│   │   ├── global.css
│   │   └── breakpoints.ts
│   ├── context/
│   │   ├── AuthContext.tsx
│   │   └── ThemeContext.tsx
│   └── App.css
├── .env.example
├── package.json
├── tsconfig.json
├── jest.config.js
└── README.md
```

---

## Setup & Installation

### 1. Initialize Project

```bash
# Create project
npx create-react-app online-banking --template typescript

cd online-banking

# Remove unnecessary files
rm -rf src/App.test.tsx src/logo.svg
```

### 2. Install Dependencies

```bash
npm install \
  redux @reduxjs/toolkit react-redux \
  @mui/material @emotion/react @emotion/styled \
  react-router-dom \
  axios \
  react-hook-form \
  yup \
  date-fns \
  recharts \
  socket.io-client \
  react-toastify \
  lodash \
  uuid

# Dev dependencies
npm install -D \
  @testing-library/react @testing-library/jest-dom \
  @types/jest \
  typescript \
  prettier \
  eslint-config-airbnb
```

### 3. Create .env File

```env
REACT_APP_API_URL=http://localhost:8080/api/v1
REACT_APP_SOCKET_URL=http://localhost:8080
REACT_APP_ENV=development
REACT_APP_DEBUG=true
```

---

## Core Components

### 1. Type Definitions

**src/types/user.ts**
```typescript
export interface User {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  kyc: {
    status: 'PENDING' | 'APPROVED' | 'REJECTED';
    verifiedAt?: string;
  };
  preferences: {
    theme: 'light' | 'dark';
    twoFactorEnabled: boolean;
    notificationsEnabled: boolean;
  };
  createdAt: string;
  updatedAt: string;
}

export interface AuthToken {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
}

export interface LoginRequest {
  email: string;
  password: string;
  mfaToken?: string;
}

export interface RegisterRequest {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  phone: string;
  acceptTerms: boolean;
}
```

**src/types/account.ts**
```typescript
export interface Account {
  id: string;
  accountNumber: string;
  accountType: 'SAVINGS' | 'CHECKING' | 'CREDIT';
  balance: number;
  currency: string;
  status: 'ACTIVE' | 'INACTIVE' | 'BLOCKED';
  createdAt: string;
  updatedAt: string;
}

export interface TransferRequest {
  fromAccountId: string;
  toAccountId: string;
  amount: number;
  description: string;
}
```

**src/types/transaction.ts**
```typescript
export interface Transaction {
  id: string;
  accountId: string;
  type: 'DEBIT' | 'CREDIT';
  amount: number;
  balance: number;
  description: string;
  status: 'SUCCESS' | 'PENDING' | 'FAILED';
  fraudRiskScore?: number;
  transactionDate: string;
  createdAt: string;
}

export interface TransactionFilter {
  startDate?: string;
  endDate?: string;
  minAmount?: number;
  maxAmount?: number;
  type?: string;
  page?: number;
  limit?: number;
}
```

---

### 2. Redux Store Setup

**src/store/slices/authSlice.ts**
```typescript
import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { User, AuthToken } from '../../types/user';

interface AuthState {
  user: User | null;
  tokens: AuthToken | null;
  loading: boolean;
  error: string | null;
  isAuthenticated: boolean;
  mfaPending: boolean;
}

const initialState: AuthState = {
  user: null,
  tokens: null,
  loading: false,
  error: null,
  isAuthenticated: false,
  mfaPending: false,
};

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    loginStart: (state) => {
      state.loading = true;
      state.error = null;
    },
    loginSuccess: (state, action: PayloadAction<{ user: User; tokens: AuthToken }>) => {
      state.user = action.payload.user;
      state.tokens = action.payload.tokens;
      state.isAuthenticated = true;
      state.loading = false;
      localStorage.setItem('accessToken', action.payload.tokens.accessToken);
      localStorage.setItem('refreshToken', action.payload.tokens.refreshToken);
    },
    loginFailure: (state, action: PayloadAction<string>) => {
      state.error = action.payload;
      state.loading = false;
    },
    mfaRequired: (state) => {
      state.mfaPending = true;
      state.loading = false;
    },
    logout: (state) => {
      state.user = null;
      state.tokens = null;
      state.isAuthenticated = false;
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
    },
    setUser: (state, action: PayloadAction<User>) => {
      state.user = action.payload;
    },
  },
});

export const { loginStart, loginSuccess, loginFailure, mfaRequired, logout, setUser } = authSlice.actions;
export default authSlice.reducer;
```

**src/store/slices/accountSlice.ts**
```typescript
import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { Account } from '../../types/account';

interface AccountState {
  accounts: Account[];
  selectedAccount: Account | null;
  loading: boolean;
  error: string | null;
}

const initialState: AccountState = {
  accounts: [],
  selectedAccount: null,
  loading: false,
  error: null,
};

const accountSlice = createSlice({
  name: 'accounts',
  initialState,
  reducers: {
    setAccounts: (state, action: PayloadAction<Account[]>) => {
      state.accounts = action.payload;
    },
    selectAccount: (state, action: PayloadAction<Account>) => {
      state.selectedAccount = action.payload;
    },
    addAccount: (state, action: PayloadAction<Account>) => {
      state.accounts.push(action.payload);
    },
    updateAccountBalance: (state, action: PayloadAction<{ accountId: string; balance: number }>) => {
      const account = state.accounts.find((a) => a.id === action.payload.accountId);
      if (account) {
        account.balance = action.payload.balance;
      }
    },
    setLoading: (state, action: PayloadAction<boolean>) => {
      state.loading = action.payload;
    },
    setError: (state, action: PayloadAction<string | null>) => {
      state.error = action.payload;
    },
  },
});

export const { setAccounts, selectAccount, addAccount, updateAccountBalance, setLoading, setError } = accountSlice.actions;
export default accountSlice.reducer;
```

**src/store/index.ts**
```typescript
import { configureStore } from '@reduxjs/toolkit';
import authReducer from './slices/authSlice';
import accountReducer from './slices/accountSlice';
import transactionReducer from './slices/transactionSlice';
import notificationReducer from './slices/notificationSlice';

export const store = configureStore({
  reducer: {
    auth: authReducer,
    accounts: accountReducer,
    transactions: transactionReducer,
    notifications: notificationReducer,
  },
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
```

---

### 3. API Service

**src/services/api.ts**
```typescript
import axios, { AxiosInstance, AxiosError, AxiosResponse } from 'axios';
import store from '../store';
import { logout } from '../store/slices/authSlice';

class ApiService {
  private api: AxiosInstance;

  constructor() {
    this.api = axios.create({
      baseURL: process.env.REACT_APP_API_URL,
      timeout: 10000,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    // Request interceptor
    this.api.interceptors.request.use((config) => {
      const token = localStorage.getItem('accessToken');
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
      return config;
    });

    // Response interceptor
    this.api.interceptors.response.use(
      (response) => response,
      (error: AxiosError) => {
        if (error.response?.status === 401) {
          // Token expired, try refresh
          return this.handleTokenRefresh(error.config);
        }
        return Promise.reject(error);
      }
    );
  }

  private async handleTokenRefresh(config: any) {
    try {
      const refreshToken = localStorage.getItem('refreshToken');
      const response = await this.api.post('/auth/refresh', { refreshToken });
      const { accessToken } = response.data;
      localStorage.setItem('accessToken', accessToken);
      config.headers.Authorization = `Bearer ${accessToken}`;
      return this.api(config);
    } catch (error) {
      // Refresh failed, logout user
      store.dispatch(logout());
      return Promise.reject(error);
    }
  }

  // Authentication
  async login(email: string, password: string) {
    return this.api.post('/auth/login', { email, password });
  }

  async register(data: any) {
    return this.api.post('/auth/register', data);
  }

  async verifyOtp(otp: string, mfaToken: string) {
    return this.api.post('/auth/verify-otp', { otp, mfaToken });
  }

  // Accounts
  async getAccounts() {
    return this.api.get(`/accounts`);
  }

  async getAccount(accountId: string) {
    return this.api.get(`/accounts/${accountId}`);
  }

  async createAccount(data: any) {
    return this.api.post('/accounts', data);
  }

  // Transactions
  async getTransactions(accountId: string, params?: any) {
    return this.api.get(`/accounts/${accountId}/transactions`, { params });
  }

  async transferMoney(data: any) {
    return this.api.post('/transactions/transfer', data);
  }

  async getTransactionHistory(filters?: any) {
    return this.api.get('/transactions/history', { params: filters });
  }

  // Cards
  async getCards() {
    return this.api.get('/cards');
  }

  async requestCard(data: any) {
    return this.api.post('/cards/request', data);
  }

  // Generic error handler
  handleError(error: AxiosError) {
    if (error.response) {
      // Server responded with error status
      return {
        status: error.response.status,
        message: (error.response.data as any)?.message || 'An error occurred',
      };
    } else if (error.request) {
      // Request made but no response
      return {
        status: 0,
        message: 'No response from server',
      };
    } else {
      return {
        status: 0,
        message: error.message,
      };
    }
  }
}

export default new ApiService();
```

---

### 4. Login Component

**src/components/auth/LoginForm.tsx**
```typescript
import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  Button,
  TextField,
  Typography,
  Container,
  Alert,
  CircularProgress,
  Link,
} from '@mui/material';
import { loginStart, loginSuccess, loginFailure, mfaRequired } from '../../store/slices/authSlice';
import apiService from '../../services/api';

const schema = yup.object({
  email: yup
    .string()
    .email('Invalid email')
    .required('Email is required'),
  password: yup
    .string()
    .min(8, 'Password must be 8+ characters')
    .required('Password is required'),
});

interface LoginFormInputs {
  email: string;
  password: string;
}

export const LoginForm: React.FC = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { loading, error } = useSelector((state: any) => state.auth);
  const [rememberMe, setRememberMe] = useState(false);

  const { register, handleSubmit, formState: { errors } } = useForm<LoginFormInputs>({
    resolver: yupResolver(schema),
  });

  const onSubmit = async (data: LoginFormInputs) => {
    try {
      dispatch(loginStart() as any);

      const response = await apiService.login(data.email, data.password);

      if (response.data.mfaRequired) {
        dispatch(mfaRequired() as any);
        // Redirect to OTP verification
        navigate('/verify-otp', { state: { mfaToken: response.data.mfaToken } });
      } else {
        dispatch(
          loginSuccess({
            user: response.data.user,
            tokens: response.data.tokens,
          }) as any
        );

        if (rememberMe) {
          localStorage.setItem('rememberMe', 'true');
        }

        navigate('/dashboard');
      }
    } catch (err: any) {
      const errorMessage = apiService.handleError(err).message;
      dispatch(loginFailure(errorMessage) as any);
    }
  };

  return (
    <Container maxWidth="sm">
      <Box sx={{ mt: 8, display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
        <Typography component="h1" variant="h4" sx={{ mb: 3 }}>
          Online Banking
        </Typography>

        {error && <Alert severity="error" sx={{ mb: 2, width: '100%' }}>{error}</Alert>}

        <Box component="form" onSubmit={handleSubmit(onSubmit)} noValidate sx={{ mt: 1 }}>
          <TextField
            margin="normal"
            fullWidth
            id="email"
            label="Email Address"
            {...register('email')}
            error={!!errors.email}
            helperText={errors.email?.message}
            disabled={loading}
          />

          <TextField
            margin="normal"
            fullWidth
            label="Password"
            type="password"
            id="password"
            {...register('password')}
            error={!!errors.password}
            helperText={errors.password?.message}
            disabled={loading}
          />

          <Button
            type="submit"
            fullWidth
            variant="contained"
            sx={{ mt: 3, mb: 2 }}
            disabled={loading}
          >
            {loading ? <CircularProgress size={24} /> : 'Sign In'}
          </Button>

          <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
            <Link href="/forgot-password" variant="body2">
              Forgot Password?
            </Link>
            <Link href="/register" variant="body2">
              Create Account
            </Link>
          </Box>
        </Box>
      </Box>
    </Container>
  );
};

export default LoginForm;
```

---

### 5. Dashboard Component

**src/components/dashboard/Dashboard.tsx**
```typescript
import React, { useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { Box, Grid, Paper, Typography, Button, CircularProgress } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { setAccounts, setLoading } from '../../store/slices/accountSlice';
import apiService from '../../services/api';
import AccountSummary from './AccountSummary';
import RecentTransactions from './RecentTransactions';
import QuickActions from './QuickActions';

export const Dashboard: React.FC = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { user } = useSelector((state: any) => state.auth);
  const { accounts, loading } = useSelector((state: any) => state.accounts);

  useEffect(() => {
    fetchAccounts();
  }, []);

  const fetchAccounts = async () => {
    try {
      dispatch(setLoading(true) as any);
      const response = await apiService.getAccounts();
      dispatch(setAccounts(response.data) as any);
    } catch (error) {
      console.error('Failed to fetch accounts:', error);
    } finally {
      dispatch(setLoading(false) as any);
    }
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box sx={{ flexGrow: 1, p: 3 }}>
      <Typography component="h1" variant="h4" sx={{ mb: 3 }}>
        Welcome, {user?.firstName}!
      </Typography>

      <Grid container spacing={3}>
        {/* Account Summary */}
        <Grid item xs={12} md={8}>
          <Paper sx={{ p: 3 }}>
            <Typography variant="h6" sx={{ mb: 2 }}>
              Your Accounts
            </Typography>
            {accounts.length > 0 ? (
              <Grid container spacing={2}>
                {accounts.map((account) => (
                  <AccountSummary key={account.id} account={account} />
                ))}
              </Grid>
            ) : (
              <Typography color="textSecondary">
                No accounts found.{' '}
                <Button onClick={() => navigate('/accounts/open')}>
                  Open a new account
                </Button>
              </Typography>
            )}
          </Paper>
        </Grid>

        {/* Quick Actions */}
        <Grid item xs={12} md={4}>
          <QuickActions />
        </Grid>

        {/* Recent Transactions */}
        <Grid item xs={12}>
          <RecentTransactions />
        </Grid>
      </Grid>
    </Box>
  );
};

export default Dashboard;
```

---

### 6. Transfer Component

**src/components/transactions/TransferForm.tsx**
```typescript
import React, { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { useSelector } from 'react-redux';
import {
  Box,
  Button,
  TextField,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  Card,
  Alert,
  CircularProgress,
} from '@mui/material';
import apiService from '../../services/api';

const schema = yup.object({
  fromAccountId: yup.string().required('Source account is required'),
  toAccountId: yup.string().required('Destination account is required'),
  amount: yup
    .number()
    .positive('Amount must be positive')
    .required('Amount is required'),
  description: yup.string().max(200, 'Description too long'),
});

interface TransferFormInputs {
  fromAccountId: string;
  toAccountId: string;
  amount: number;
  description: string;
}

export const TransferForm: React.FC = () => {
  const { accounts } = useSelector((state: any) => state.accounts);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);

  const { register, handleSubmit, watch, formState: { errors }, reset } = useForm<TransferFormInputs>({
    resolver: yupResolver(schema),
  });

  const selectedFromAccount = watch('fromAccountId');
  const selectedToAccount = watch('toAccountId');
  const amount = watch('amount');

  // Calculate available balance
  const availableBalance = accounts
    .find((acc: any) => acc.id === selectedFromAccount)
    ?.balance || 0;

  const onSubmit = async (data: TransferFormInputs) => {
    if (amount > availableBalance) {
      setError('Insufficient balance');
      return;
    }

    try {
      setLoading(true);
      setError(null);

      await apiService.transferMoney({
        fromAccountId: data.fromAccountId,
        toAccountId: data.toAccountId,
        amount: data.amount,
        description: data.description,
      });

      setSuccess(true);
      reset();
      setTimeout(() => setSuccess(false), 5000);
    } catch (err: any) {
      const errorMessage = apiService.handleError(err).message;
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Card sx={{ p: 3, maxWidth: 500 }}>
      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
      {success && <Alert severity="success" sx={{ mb: 2 }}>Transfer successful!</Alert>}

      <Box component="form" onSubmit={handleSubmit(onSubmit)}>
        <FormControl fullWidth margin="normal">
          <InputLabel>From Account</InputLabel>
          <Select
            {...register('fromAccountId')}
            label="From Account"
            defaultValue=""
          >
            {accounts.map((account: any) => (
              <MenuItem key={account.id} value={account.id}>
                {account.accountNumber} - {account.accountType} - ${account.balance}
              </MenuItem>
            ))}
          </Select>
        </FormControl>

        {selectedFromAccount && (
          <Box sx={{ mt: 1, p: 1, bgcolor: '#f5f5f5', borderRadius: 1 }}>
            Available: ${availableBalance.toFixed(2)}
          </Box>
        )}

        <FormControl fullWidth margin="normal">
          <InputLabel>To Account</InputLabel>
          <Select
            {...register('toAccountId')}
            label="To Account"
            defaultValue=""
          >
            {accounts
              .filter((account: any) => account.id !== selectedFromAccount)
              .map((account: any) => (
                <MenuItem key={account.id} value={account.id}>
                  {account.accountNumber} - {account.accountType}
                </MenuItem>
              ))}
          </Select>
        </FormControl>

        <TextField
          fullWidth
          margin="normal"
          label="Amount"
          type="number"
          inputProps={{ step: '0.01', min: '0' }}
          {...register('amount')}
          error={!!errors.amount}
          helperText={errors.amount?.message}
        />

        <TextField
          fullWidth
          margin="normal"
          label="Description (optional)"
          multiline
          rows={3}
          {...register('description')}
          error={!!errors.description}
          helperText={errors.description?.message}
        />

        <Button
          type="submit"
          fullWidth
          variant="contained"
          sx={{ mt: 3 }}
          disabled={loading}
        >
          {loading ? <CircularProgress size={24} /> : 'Transfer Money'}
        </Button>
      </Box>
    </Card>
  );
};

export default TransferForm;
```

---

### 7. Custom Hooks

**src/hooks/useAuth.ts**
```typescript
import { useSelector, useDispatch } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { logout } from '../store/slices/authSlice';
import { RootState } from '../store';

export const useAuth = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { user, tokens, isAuthenticated } = useSelector((state: any) => state.auth);

  const handleLogout = () => {
    dispatch(logout() as any);
    navigate('/login');
  };

  return {
    user,
    tokens,
    isAuthenticated,
    logout: handleLogout,
  };
};
```

**src/hooks/useFetch.ts**
```typescript
import { useState, useEffect } from 'react';
import axios from 'axios';

interface UseFetchResult<T> {
  data: T | null;
  loading: boolean;
  error: Error | null;
}

export const useFetch = <T,>(
  url: string,
  options?: any
): UseFetchResult<T> => {
  const [data, setData] = useState<T | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<Error | null>(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const response = await axios.get<T>(url, options);
        setData(response.data);
        setError(null);
      } catch (err) {
        setError(err as Error);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [url, options]);

  return { data, loading, error };
};
```

---

### 8. Protected Routes

**src/App.tsx**
```typescript
import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { useSelector } from 'react-redux';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import DashboardPage from './pages/DashboardPage';
import AccountsPage from './pages/AccountsPage';
import TransactionsPage from './pages/TransactionsPage';

const ProtectedRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { isAuthenticated } = useSelector((state: any) => state.auth);

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  return <>{children}</>;
};

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />

        <Route
          path="/dashboard"
          element={
            <ProtectedRoute>
              <DashboardPage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/accounts"
          element={
            <ProtectedRoute>
              <AccountsPage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/transactions"
          element={
            <ProtectedRoute>
              <TransactionsPage />
            </ProtectedRoute>
          }
        />

        <Route path="/" element={<Navigate to="/dashboard" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
```

---

### 9. Utility Functions

**src/utils/formatters.ts**
```typescript
export const formatCurrency = (amount: number, currency: string = 'USD'): string => {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency,
  }).format(amount);
};

export const formatDate = (date: string | Date): string => {
  return new Date(date).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  });
};

export const formatTime = (date: string | Date): string => {
  return new Date(date).toLocaleTimeString('en-US', {
    hour: '2-digit',
    minute: '2-digit',
  });
};

export const maskAccountNumber = (accountNumber: string): string => {
  const last4 = accountNumber.slice(-4);
  return `****${last4}`;
};

export const maskCardNumber = (cardNumber: string): string => {
  const last4 = cardNumber.slice(-4);
  return `**** **** **** ${last4}`;
};
```

**src/utils/validators.ts**
```typescript
export const validateEmail = (email: string): boolean => {
  const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return regex.test(email);
};

export const validatePassword = (password: string): boolean => {
  // Min 8 chars, 1 uppercase, 1 lowercase, 1 number, 1 special char
  const regex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
  return regex.test(password);
};

export const validateAccountNumber = (accountNumber: string): boolean => {
  return /^\d{10,12}$/.test(accountNumber);
};

export const validateAmount = (amount: number | string): boolean => {
  const num = typeof amount === 'string' ? parseFloat(amount) : amount;
  return !isNaN(num) && num > 0;
};
```

---

## Testing Examples

**src/__tests__/LoginForm.test.tsx**
```typescript
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { Provider } from 'react-redux';
import { BrowserRouter } from 'react-router-dom';
import { store } from '../store';
import LoginForm from '../components/auth/LoginForm';
import apiService from '../services/api';

jest.mock('../services/api');

describe('LoginForm Component', () => {
  const mockApiService = apiService as jest.Mocked<typeof apiService>;

  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('renders login form', () => {
    render(
      <Provider store={store}>
        <BrowserRouter>
          <LoginForm />
        </BrowserRouter>
      </Provider>
    );

    expect(screen.getByLabelText(/email/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/password/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /sign in/i })).toBeInTheDocument();
  });

  test('shows validation errors for empty fields', async () => {
    render(
      <Provider store={store}>
        <BrowserRouter>
          <LoginForm />
        </BrowserRouter>
      </Provider>
    );

    const submitButton = screen.getByRole('button', { name: /sign in/i });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText(/email is required/i)).toBeInTheDocument();
      expect(screen.getByText(/password is required/i)).toBeInTheDocument();
    });
  });

  test('calls login API with correct credentials', async () => {
    mockApiService.login.mockResolvedValueOnce({
      data: {
        user: { id: '1', email: 'test@mail.com' },
        tokens: { accessToken: 'token123' },
      },
    });

    render(
      <Provider store={store}>
        <BrowserRouter>
          <LoginForm />
        </BrowserRouter>
      </Provider>
    );

    fireEvent.change(screen.getByLabelText(/email/i), { target: { value: 'test@mail.com' } });
    fireEvent.change(screen.getByLabelText(/password/i), { target: { value: 'Password123!' } });
    fireEvent.click(screen.getByRole('button', { name: /sign in/i }));

    await waitFor(() => {
      expect(mockApiService.login).toHaveBeenCalledWith('test@mail.com', 'Password123!');
    });
  });
});
```

---

## Performance Optimization

```typescript
// Use React.memo for expensive components
import React from 'react';

interface AccountCardProps {
  account: Account;
}

const AccountCard = React.memo(({ account }: AccountCardProps) => {
  return (
    <Card>
      <Typography>{account.accountNumber}</Typography>
      <Typography>${account.balance}</Typography>
    </Card>
  );
});

export default AccountCard;
```

---

## Environment Variables

**.env.example**
```env
# API Configuration
REACT_APP_API_URL=http://localhost:8080/api/v1
REACT_APP_SOCKET_URL=http://localhost:8080

# Feature Flags
REACT_APP_ENABLE_2FA=true
REACT_APP_ENABLE_FRAUD_DETECTION=true
REACT_APP_ENABLE_ANALYTICS=true
REACT_APP_ENABLE_CHATBOT=true

# Environment
REACT_APP_ENV=development
REACT_APP_DEBUG=true

# Analytics
REACT_APP_GOOGLE_ANALYTICS_ID=UA-XXXX-X

# Sentry (Error Tracking)
REACT_APP_SENTRY_DSN=https://xxxx@sentry.io/xxxx
```

---

**Next: Deploy with Docker, setup CI/CD, and perform load testing.**
