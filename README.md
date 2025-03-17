# Bombit Game

## Objetivo
El objetivo principal de este proyecto es desarrollar una versión moderna y optimizada del clásico juego Bomberman, enfocada en la jugabilidad en tiempo real para cuatro jugadores. Se busca mejorar la experiencia multijugador mediante tecnologías de redes eficientes, garantizando baja latencia y una experiencia de juego competitiva.
El proyecto implementa una arquitectura moderna basada en microservicios para proporcionar una experiencia de juego fluida, escalable y de alta disponibilidad.

## Infraestructura

### Cloud

El proyecto está completamente desplegado en **Azure**, aprovechando los siguientes servicios:

- **Azure App Service**: Alojamiento de la aplicación web del frontend
- **Azure Container Apps**: Orquestación de contenedores para los microservicios backend
- **Azure Blob Storage**: Almacenamiento de assets del juego (sprites, sonidos, etc.)
- **Azure Entra ID**: Autenticación y gestión de identidades
- **Azure Monitor**: Monitorización y logging centralizado
- **Azure Container Registry**: Almacenamiento de imágenes Docker

### Backend (Spring Boot)

La capa de backend está implementada como una serie de microservicios desarrollados con Spring Boot:

- **API Gateway (Spring Cloud Gateway)**: Punto de entrada único para todas las peticiones
- **Microservicios**:
  - User Service: Gestión de perfiles y progreso de los jugadores
  - Auth Service: Integración con Entra ID para autenticación
  - Game Engine Service: Lógica principal del juego
  - Map Service: Generación y gestión de mapas
- **Servicios en Tiempo Real**:
  - WebSocket Service: Comunicación bidireccional en tiempo real
  - Game State Service: Gestión del estado del juego

### Frontend (React JS)

La interfaz de usuario está desarrollada con React JS:

- Game Canvas: Renderizado del juego usando Pixi.js
- Redux/Context API: Gestión del estado global
- Authentication Module: Integración con Azure Entra ID

### Bases de Datos

- **MongoDB**: Base de datos principal para almacenar información de usuarios, partidas y mapas
- **Redis**: Caché en memoria para estado del juego en tiempo real y sesiones

### Comunicación

- **REST API**: Comunicación asíncrona entre servicios y cliente
- **WebSockets**: Comunicación en tiempo real para actualizaciones del juego
- **Kafka**: Sistema de mensajería para eventos entre microservicios

## Despliegue

### Prerequisitos

- Cuenta de Azure con permisos adecuados
- Azure CLI instalado
- Docker instalado localmente
- Node.js y npm para desarrollo frontend
- JDK y Maven para desarrollo backend

### Configuración de Azure Container Apps

1. **Crear un grupo de recursos**:
   ```bash
   az group create --name bombit-rg --location eastus
   ```

2. **Crear Azure Container Registry**:
   ```bash
   az acr create --resource-group bombit-rg --name bombitregistry --sku Basic
   ```

3. **Configurar Azure Container Apps Environment**:
   ```bash
   az containerapp env create --name bombit-env --resource-group bombit-rg --location eastus
   ```

### Pipeline de CI/CD

El proyecto utiliza GitHub Actions para CI/CD con los siguientes jobs:

```yaml
name: Build, Test, and Deploy

on:
  push:
    branches: [ "main", "develop" ]
  pull_request:
    branches: [ "main", "develop" ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout code
        uses: actions/checkout@v4
      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
          cache: maven
      - name: Build with Maven
        run: mvn clean compile

  test:
    needs: build
    runs-on: ubuntu-latest
    steps:
      - name: Checkout code
        uses: actions/checkout@v4
      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
          cache: maven
      - name: Run tests with Maven
        run: mvn clean verify
      - name: Upload Test Report (Jacoco)
        uses: actions/upload-artifact@v4
        with:
          name: SpringBoot Test Report
          path: target/site/jacoco/

  sonarcloud:
    needs: test
    uses: ZayraGS1403/central-pipelines/.github/workflows/sonarcloud-analysis.yml@v0.1.1
    with:
      java-version: '21'
      branch-name: 'main'
    secrets:
      SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
      SONAR_ORGANIZATION: ${{ secrets.SONAR_ORGANIZATION }}
      SONAR_PROJECT_KEY: ${{ secrets.SONAR_PROJECT_KEY }}

  deploy:
    needs: sonarcloud
    runs-on: ubuntu-latest
    steps:
      - name: Checkout code
        uses: actions/checkout@v4
      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
          cache: maven
      - name: Build and Package with Maven
        run: mvn clean package -DskipTests
      - name: Deploy to Azure Web App
        uses: azure/webapps-deploy@v2
        with:
          app-name: 'EciBombit'
          publish-profile: ${{ secrets.AZURE_WEBAPP_PUBLISH_PROFILE }}
          package: './target/EciBombit-0.0.1-SNAPSHOT.jar'
```

Este pipeline se activa automáticamente cuando se hace push o se abre un pull request en las ramas main o develop, y consta de los siguientes pasos:

1. **Build**: Compilación del código fuente con Java 21 y Maven
2. **Test**: Ejecución de pruebas y generación de informes de cobertura con Jacoco
3. **SonarCloud**: Análisis estático de código para asegurar la calidad y seguridad
4. **Deploy**: Empaquetado y despliegue a Azure Web App utilizando el perfil de publicación configurado


## Buenas Prácticas

### Desarrollo

- **Arquitectura Hexagonal**: Separación clara entre dominio, aplicación e infraestructura
- **API-First Design**: Definición de contratos API mediante OpenAPI/Swagger
- **Testing Automatizado**: Cobertura mínima del 80% con pruebas unitarias e integración
- **Clean Code**: Seguimiento de principios SOLID y convenciones de código

### Monitorización y Operaciones

- **Logging Centralizado**: Todos los logs se envían a Azure Monitor
- **Alertas Automáticas**: Configuración de alertas para anomalías y errores
- **Dashboards Operativos**: Visualización de métricas clave de rendimiento

## Próximos Pasos

- Implementación de un sistema de matchmaking avanzado
- Incorporación de logros y sistema de recompensas
- Desarrollo de editor de mapas para la comunidad
- Integración con análisis de telemetría para balance del juego
