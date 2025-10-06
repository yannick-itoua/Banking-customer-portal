# 🚀 Railway Deployment Summary

## Quick Deployment Checklist

### ✅ Prerequisites
- [ ] GitHub repository with Banking Customer Portal
- [ ] Railway account (free tier available)
- [ ] Repository access permissions for Railway

### 🗄️ Step 1: Database Setup
1. **Create PostgreSQL Database**
   - Railway Dashboard → **New** → **Database** → **PostgreSQL**
   - Note the auto-generated DATABASE_URL

### 🔧 Step 2: Backend Deployment
1. **Create Backend Service**
   - Railway Dashboard → **New** → **GitHub Repo**
   - **Repository**: Select your repo
   - **Root Directory**: `backend/`
   
2. **Environment Variables** (Auto-configured)
   - `DATABASE_URL` (linked from PostgreSQL)
   - `PORT` (auto-set by Railway)
   
3. **Generate Domain**
   - Settings → Networking → Generate Domain
   - **Save this URL**: `https://backend-xyz.up.railway.app`

### ⚛️ Step 3: Frontend Deployment
1. **Create Frontend Service**
   - Railway Dashboard → **New** → **GitHub Repo**
   - **Repository**: Select your repo
   - **Root Directory**: `frontend/`

2. **Environment Variables** (REQUIRED)
   ```bash
   NEXT_PUBLIC_API_URL=https://backend-xyz.up.railway.app
   PORT=8080
   ```

3. **Generate Domain**
   - Settings → Networking → Generate Domain
   - **Your app URL**: `https://frontend-abc.up.railway.app`

### 🔗 Step 4: Final Configuration
1. **Test Health Endpoints**
   - Backend: `https://backend-xyz.up.railway.app/api/debug/health`
   - Frontend: `https://frontend-abc.up.railway.app/health`

2. **Test Application**
   - Open: `https://frontend-abc.up.railway.app`
   - Register/Login to verify API communication

## 🎯 Key Railway Settings

### Backend Service
- **Build Command**: Auto-detected (Docker)
- **Start Command**: `./start.sh`
- **Port**: Auto-detected (8080)
- **Environment**: `DATABASE_URL` linked from PostgreSQL

### Frontend Service
- **Build Command**: Auto-detected (Docker)
- **Start Command**: `node server.js`
- **Port**: **MANUALLY SET TO 8080** ⚠️
- **Environment**: `NEXT_PUBLIC_API_URL` = backend domain

### Database Service
- **Type**: PostgreSQL
- **Auto-linking**: Enabled for backend service
- **Backups**: Automatic
- **Connection**: Via DATABASE_URL environment variable

## 🔥 Pro Tips

1. **Port Configuration**: Railway requires manual port setting to 8080 for frontend
2. **CORS Ready**: Backend already configured for Railway domains
3. **Auto-healing**: Railway automatically restarts failed services
4. **Logs**: Monitor deployment in Railway dashboard logs
5. **Custom Domains**: Available in Railway Pro plan

## 🛠️ Troubleshooting Commands

```bash
# Check Railway CLI status (if using Railway CLI)
railway status

# View logs
railway logs --tail

# Connect to database
railway connect
```

## 📞 Support
- Railway Documentation: https://docs.railway.app
- Banking Portal Repository Issues: Create GitHub issue for application-specific problems