# Security Fix Script for Banking Customer Portal
# This script removes sensitive JWT key from Git history

Write-Host "🚨 SECURITY FIX: Removing exposed JWT secret from Git history" -ForegroundColor Red
Write-Host "================================================" -ForegroundColor Yellow

# Step 1: Add current security fixes
Write-Host "✅ Adding security fixes to staging..." -ForegroundColor Green
git add .
git commit -m "SECURITY: Remove hardcoded JWT secret and implement environment variables

- Remove exposed JWT secret from application.properties
- Implement environment variable configuration
- Add .env.example for secure deployment
- Update docker-compose.yml to use environment variables

BREAKING CHANGE: JWT_SECRET environment variable now required"

# Step 2: Force push the security fix (WARNING: This rewrites history)
Write-Host "⚠️  WARNING: About to rewrite Git history to remove exposed secrets" -ForegroundColor Yellow
Write-Host "This will force push to remove the exposed JWT key from all commits" -ForegroundColor Yellow

$response = Read-Host "Continue with history rewrite? This cannot be undone. (y/N)"

if ($response -eq "y" -or $response -eq "Y") {
    Write-Host "🔧 Filtering Git history to remove exposed JWT secret..." -ForegroundColor Blue
    
    # Use git filter-branch to remove the exposed secret from all commits
    git filter-branch --force --index-filter `
        "git rm --cached --ignore-unmatch backend/src/main/resources/application.properties || true; git rm --cached --ignore-unmatch backend/src/main/resources/application-docker.properties || true" `
        --prune-empty --tag-name-filter cat -- --all
    
    # Force push to rewrite remote history
    Write-Host "🚀 Force pushing cleaned history..." -ForegroundColor Blue
    git push origin --force --all
    
    # Clean up local refs
    git for-each-ref --format="%(refname)" refs/original/ | ForEach-Object { git update-ref -d $_ }
    git reflog expire --expire=now --all
    git gc --prune=now --aggressive
    
    Write-Host "✅ Git history cleaned! The exposed JWT secret has been removed." -ForegroundColor Green
    Write-Host "🔐 New secure configuration is now in place." -ForegroundColor Green
} else {
    Write-Host "❌ History rewrite cancelled. The exposed secret is still in Git history." -ForegroundColor Red
    Write-Host "⚠️  RECOMMENDATION: Consider creating a new repository for maximum security." -ForegroundColor Yellow
}

Write-Host "`n📋 NEXT STEPS:" -ForegroundColor Cyan
Write-Host "1. Create .env file with your production JWT secret" -ForegroundColor White
Write-Host "2. Never commit .env files to Git" -ForegroundColor White
Write-Host "3. Use environment variables in production" -ForegroundColor White
Write-Host "4. Rotate JWT secrets regularly" -ForegroundColor White
Write-Host "5. Monitor for any usage of the old exposed key" -ForegroundColor White