#  API Restaurant - Menu & Commandes

API REST pour la gestion d'un menu de restaurant et de commandes en temps réel.

## 🚀 Technologies utilisées

- **Backend** : Spring Boot 3.3.4
- **Base de données** : PostgreSQL (Neon)
- **Sécurité** : JWT + Spring Security
- **Documentation** : Swagger/OpenAPI
- **Build** : Maven

## 🚢 Déploiement sur Render

Ce backend est prêt pour Render avec le `Dockerfile` et `render.yaml`.

### Variables d'environnement à définir dans Render

- `DATABASE_URL` : URL JDBC PostgreSQL
- `DB_USERNAME`
- `DB_PASSWORD`
- `APP_ADMIN_USERNAME`
- `APP_ADMIN_PASSWORD_HASH`
- `APP_JWT_SECRET`
- `SPRING_PROFILES_ACTIVE` : `prod`

Render fournit automatiquement `PORT`, et le `Dockerfile` lance l'application avec :

`java $JAVA_OPTS -Dserver.port=$PORT -jar app.jar`

### Commandes utiles

- Construire localement : `docker build -t appmenu-backend .`
- Lancer localement : `docker run -e PORT=8080 -p 8080:8080 appmenu-backend`

> En production, définis les valeurs secrètes dans Render plutôt que dans le dépôt.
