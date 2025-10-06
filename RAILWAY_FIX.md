# 🚨 Railway Deployment Fix Guide

## Issue: "Could not find root directory: .../backend"

### 🔍 Common Causes
1. Incorrect root directory configuration in Railway
2. Repository branch mismatch
3. Railway cache issues
4. Incorrect service setup order

### ✅ Step-by-Step Fix

#### 1. Delete and Recreate Services (Recommended)
If you have existing Railway services, delete them and start fresh:

1. **Delete existing services** in Railway dashboard
2. **Clear Railway cache** (Railway settings)

#### 2. Correct Service Creation Order

**A. Create PostgreSQL Database First**
1. Railway Dashboard → **New Project**
2. **Add Service** → **Database** → **PostgreSQL**
3. Note the database service name (e.g., `postgres`)

**B. Create Backend Service**
1. **Add Service** → **GitHub Repo**
2. **Repository**: `yannick-itoua/Banking-customer-portal`
3. **Branch**: `main`
4. **Root Directory**: `backend` (exactly like this, no slashes)
5. **Service Name**: Give it a clear name like `banking-backend`

**C. Link Database to Backend**
1. Go to backend service
2. **Variables** tab
3. **New Variable** → **Reference** → Select PostgreSQL service
4. Variable name: `DATABASE_URL`
5. Railway will auto-link the database

**D. Create Frontend Service**
1. **Add Service** → **GitHub Repo**
2. **Repository**: `yannick-itoua/Banking-customer-portal`
3. **Branch**: `main`
4. **Root Directory**: `frontend` (exactly like this, no slashes)
5. **Service Name**: Give it a clear name like `banking-frontend`

#### 3. Configure Environment Variables

**Backend Service Variables:**
```
DATABASE_URL → (Auto-linked from PostgreSQL)
PORT → (Auto-set by Railway)
JWT_SECRET → your_secure_jwt_secret_here
```

**Frontend Service Variables:**
```
NEXT_PUBLIC_API_URL → https://your-backend-domain.up.railway.app
PORT → 8080
```

#### 4. Generate Domains
1. **Backend Service** → Settings → Networking → Generate Domain
2. **Frontend Service** → Settings → Networking → Generate Domain
3. **Update frontend** `NEXT_PUBLIC_API_URL` with backend domain

### 🚀 Alternative: Manual Directory Check

If the issue persists, verify the GitHub repository structure:

1. Go to: https://github.com/yannick-itoua/Banking-customer-portal
2. Verify these folders exist at root level:
   - ✅ `backend/` (with Dockerfile inside)
   - ✅ `frontend/` (with Dockerfile inside)

### 🔧 Railway CLI Alternative

If web interface fails, try Railway CLI:

```bash
# Install Railway CLI
npm install -g @railway/cli

# Login
railway login

# Create new project
railway new

# Deploy backend
cd backend
railway up

# Deploy frontend  
cd ../frontend
railway up
```

### 📞 Final Troubleshooting

If none of the above works:

1. **Check Railway Status**: https://status.railway.app
2. **Repository Permissions**: Ensure Railway has access to your GitHub repo
3. **Branch Protection**: Make sure `main` branch is accessible
4. **File Case Sensitivity**: Ensure `Dockerfile` has correct capitalization

### ✅ Success Indicators

You'll know it's working when:
- ✅ Railway detects Dockerfile automatically
- ✅ Build logs show successful Docker build
- ✅ Services generate URLs
- ✅ Health checks respond successfully

---

**Need help?** Check the Railway logs for specific error messages and share them for more targeted troubleshooting.