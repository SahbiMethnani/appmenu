# Déploiement Render pour appmenu

## Repo GitHub
- URL : https://github.com/SahbiMethnani/appme
- Branche : main

## Configuration Render
- Service type : Web Service
- Environment : Docker
- Root Directory : (laisser vide)
- Dockerfile Path : Dockerfile
- Auto Deploy : true (optionnel)

## Variables d'environnement à ajouter dans Render
Le service Render doit recevoir ces variables :

```env
DATABASE_URL="jdbc:postgresql://ep-shy-fire-ahmfq2gw-pooler.c-3.us-east-1.aws.neon.tech/neondb?sslmode=require"
DB_USERNAME="neondb_owner"
DB_PASSWORD="npg_4xwFI8RgBpqv"

APP_ADMIN_USERNAME="admin"
APP_ADMIN_PASSWORD_HASH="$2a$12$KWGLOlguCW/xJJ4mvJdCYeH/TfK9Uay6VaoVZj.SX8e/m7tT4RAt."
APP_JWT_SECRET="WXAn6cpKe3HU4YzV1QQqGMOGu0CR0NgsQ4Q8X4zIP0O_MYEkkT3HLMVnzH-mSmgeFlsqyLcWuwgtVsqseqFr6w"

SPRING_PROFILES_ACTIVE="prod"
JAVA_OPTS="-Xms256m -Xmx512m"
```

### Important
- `PORT` n'est pas nécessaire dans Render : Render le fournit automatiquement.
- Si tu as déjà un fichier `.env.example`, tu peux utiliser ces mêmes valeurs dans le dashboard Render.

## Vérifier le Dockerfile
Ton `Dockerfile` doit contenir :

```dockerfile
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=$PORT -jar app.jar"]
```

## Déploiement manuel
1. Ouvre Render.
2. Crée un nouveau service `Web Service`.
3. Choisis GitHub et sélectionne le dépôt `SahbiMethnani/appme`.
4. Configure :
   - `Root Directory` = vide
   - `Dockerfile Path` = `Dockerfile`
5. Ajoute les variables d'environnement ci-dessus.
6. Lance `Manual Deploy` ou pousse une nouvelle modification si `Auto Deploy` est activé.

## Erreur courante
- Si Render affiche `Root directory "appmenu" does not exist`, cela signifie que tu as mis `Root Directory = appmenu` alors que le `Dockerfile` est à la racine du repo.
- Dans ce cas, laisse la racine vide.
