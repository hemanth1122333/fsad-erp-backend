# Backend Deployment Guide

## Option 1: Railway (Recommended for Spring Boot)

### 1. Create Railway Account
- Go to https://railway.app
- Sign up with GitHub

### 2. Create New Project
- Click "New Project"
- Choose "Deploy from GitHub repo"
- Select your repository

### 3. Configure Environment
- Go to project Variables tab
- Add these environment variables:
```
CORS_ALLOWED_ORIGINS=https://your-frontend-domain.com
SPRING_DATASOURCE_URL=jdbc:mysql://your-mysql-host:3306/erp_db?useSSL=true&serverTimezone=UTC
SPRING_DATASOURCE_USERNAME=your_db_username
SPRING_DATASOURCE_PASSWORD=your_db_password
SPRING_DATASOURCE_DRIVER_CLASS_NAME=com.mysql.cj.jdbc.Driver
JWT_SECRET=your-256-bit-secret-key-here
```

### 4. Database Setup
- Add MySQL database in Railway
- Or use external MySQL (AWS RDS, PlanetScale, etc.)

### 5. Deploy
- Railway auto-deploys on git push
- Your API will be at: `https://your-project.up.railway.app`

## Option 2: Render

### 1. Create Render Account
- Go to https://render.com
- Sign up with GitHub

### 2. Create Web Service
- Click "New" > "Web Service"
- Connect your GitHub repo
- Set build command: `./mvnw clean package -DskipTests`
- Set start command: `java -jar target/*.jar`

### 3. Environment Variables
```
CORS_ALLOWED_ORIGINS=https://your-frontend-domain.com
SPRING_DATASOURCE_URL=jdbc:mysql://your-mysql-host:3306/erp_db?useSSL=true&serverTimezone=UTC
SPRING_DATASOURCE_USERNAME=your_db_username
SPRING_DATASOURCE_PASSWORD=your_db_password
SPRING_DATASOURCE_DRIVER_CLASS_NAME=com.mysql.cj.jdbc.Driver
JWT_SECRET=your-256-bit-secret-key-here
```

## Option 3: Heroku

### 1. Create Heroku Account
- Go to https://heroku.com
- Install Heroku CLI

### 2. Prepare for Deployment
```bash
# Create Procfile in backend directory
echo "web: java -jar target/*.jar" > Procfile

# Create system.properties
echo "java.runtime.version=17" > system.properties
```

### 3. Deploy
```bash
heroku create your-erp-backend
heroku config:set CORS_ALLOWED_ORIGINS=https://your-frontend-domain.com
heroku config:set SPRING_DATASOURCE_URL=jdbc:mysql://your-mysql-host:3306/erp_db?useSSL=true&serverTimezone=UTC
heroku config:set SPRING_DATASOURCE_USERNAME=your_db_username
heroku config:set SPRING_DATASOURCE_PASSWORD=your_db_password
heroku config:set JWT_SECRET=your-256-bit-secret-key-here
git push heroku main
```

## Option 4: AWS EC2

### 1. Launch EC2 Instance
- Choose Amazon Linux 2 or Ubuntu
- Install Java 17 and Maven

### 2. Build and Deploy
```bash
# On your EC2 instance
sudo yum update -y  # or apt update on Ubuntu
sudo yum install java-17-amazon-corretto maven -y

# Clone and build
git clone your-repo
cd backend
mvn clean package -DskipTests

# Run with environment variables
CORS_ALLOWED_ORIGINS=https://your-frontend-domain.com \
SPRING_DATASOURCE_URL=jdbc:mysql://your-rds-endpoint:3306/erp_db \
SPRING_DATASOURCE_USERNAME=your_db_user \
SPRING_DATASOURCE_PASSWORD=your_db_pass \
java -jar target/*.jar
```

## Database Setup

### MySQL on AWS RDS
1. Create RDS MySQL instance
2. Note the endpoint URL
3. Create database: `erp_db`
4. Run the schema.sql to create tables

### Alternative: PlanetScale
- Go to https://planetscale.com
- Create database
- Get connection string
- Set as SPRING_DATASOURCE_URL

## Testing Deployment

After deployment, test that:
1. Backend starts without errors
2. Database connection works
3. CORS allows your frontend domain
4. All API endpoints respond correctly
5. Authentication works with JWT

## Health Check

Your backend will have a health endpoint at:
`https://your-backend-domain.com/actuator/health`

Or you can test with:
```bash
curl https://your-backend-domain.com/api/dashboard/summary
# Should return 401 (unauthorized) if working
```