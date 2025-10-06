#!/bin/bash
# Railway DATABASE_URL conversion script
# Railway provides postgresql://user:pass@host:port/db but Spring Boot needs jdbc:postgresql://host:port/db

echo "🔧 Processing Railway DATABASE_URL..."

if [ -n "$DATABASE_URL" ]; then
  echo "📋 Original DATABASE_URL: $DATABASE_URL"
  
  # Extract components from postgresql://user:pass@host:port/db
  # Convert to jdbc:postgresql://host:port/db and set individual properties
  if [[ $DATABASE_URL =~ postgresql://([^:]+):([^@]+)@([^:]+):([0-9]+)/(.+) ]]; then
    DB_USER="${BASH_REMATCH[1]}"
    DB_PASSWORD="${BASH_REMATCH[2]}"
    DB_HOST="${BASH_REMATCH[3]}"
    DB_PORT="${BASH_REMATCH[4]}"
    DB_NAME="${BASH_REMATCH[5]}"
    
    # Set Spring Boot compatible URL
    export SPRING_DATASOURCE_URL="jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}"
    export SPRING_DATASOURCE_USERNAME="${DB_USER}"
    export SPRING_DATASOURCE_PASSWORD="${DB_PASSWORD}"
    
    echo "✅ Converted for Spring Boot:"
    echo "   URL: $SPRING_DATASOURCE_URL"
    echo "   Username: $SPRING_DATASOURCE_USERNAME"
    echo "   Password: [HIDDEN]"
  else
    echo "⚠️  Could not parse DATABASE_URL format, using as-is with jdbc prefix"
    export DATABASE_URL=$(echo $DATABASE_URL | sed 's/^postgresql:/jdbc:postgresql:/')
  fi
else
  echo "ℹ️  No DATABASE_URL found, using application.properties defaults"
fi

# Start the Spring Boot application
echo "🚀 Starting Banking Customer Portal Backend..."
exec java -jar app.jar