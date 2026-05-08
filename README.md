# ScoreLab - Backend (API REST) ⚽

Este es el servidor central de **ScoreLab**, desarrollado con **Spring Boot 3**. Gestiona la lógica de negocio, la seguridad JWT y la persistencia de datos en una base de datos **PostgreSQL**.

---

## 🛠️ Requisitos Técnicos
Antes de comenzar, asegúrate de tener instalado:
* **Java JDK 17** o superior.
* **PostgreSQL 14** o superior.
* **Maven** (integrado en IntelliJ).
* **IntelliJ IDEA** (Recomendado).

---

## Guía de Configuración Paso a Paso

### 1. Crear la Base de Datos (Vacia)
Antes de correr el proyecto, **debes crear la base de datos manualmente** para que Spring Boot pueda conectarse a ella.

Abre tu terminal de PostgreSQL (`psql`) o **pgAdmin 4** y ejecuta:

```sql
CREATE DATABASE scorelab_db;
```
### 2. Configurar el archivo application.properties
Busca el archivo en `src/main/resources/application.properties` y ajusta tus credenciales de PostgreSQL:

---

```properties
# Configuración de PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/scorelab
spring.datasource.username=TU_USUARIO
spring.datasource.password=TU_CONTRASEÑA

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.format_sql=true
```
---


