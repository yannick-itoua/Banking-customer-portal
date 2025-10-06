#!/bin/bash
# Railway DATABASE_URL conversion script
# Railway provides postgresql:// but Spring Boot needs jdbc:postgresql://

if [ -n "$DATABASE_URL" ]; then
  # Convert postgresql:// to jdbc:postgresql://
  export DATABASE_URL=$(echo $DATABASE_URL | sed 's/^postgresql:/jdbc:postgresql:/')
  echo "✅ Converted DATABASE_URL for Spring Boot: $DATABASE_URL"
fi

# Start the Spring Boot application
echo "🚀 Starting Banking Customer Portal Backend..."
exec java -jar app.jar