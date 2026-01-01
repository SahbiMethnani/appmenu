#  API Restaurant - Menu & Commandes

API REST pour la gestion d'un menu de restaurant et de commandes en temps réel.

## 🚀 Technologies utilisées

- **Backend** : Spring Boot 3.3.4
- **Base de données** : PostgreSQL (Neon)
- **Sécurité** : JWT + Spring Security
- **Documentation** : Swagger/OpenAPI
- **Build** : Maven

## 📋 Fonctionnalités

- ✅ Gestion du menu (CRUD plats)
- ✅ Passage de commandes
- ✅ Gestion des statuts de commande
- ✅ Authentification JWT pour admin
- ✅ Statistiques en temps réel
- ✅ Upload d'images de catégories
- ✅ Configuration dynamique du nombre de tables

## 🔧 Installation

### Prérequis

- Java 17+
- Maven 3.6+
- PostgreSQL (ou compte Neon)

### Configuration

1. Clonez le repository :
```bash
git clone https://github.com/VOTRE_USERNAME/appmenu.git
cd appmenu
```

2. Copiez le fichier de configuration d'exemple :
```bash
cp application.yml.example src/main/resources/application.yml
```

3. Modifiez `application.yml` avec vos propres credentials :
    - URL de base de données
    - Username et password
    - Secret JWT
    - Hash du mot de passe admin

4. Installez les dépendances :
```bash
mvn clean install
```

5. Lancez l'application :
```bash
mvn spring-boot:run
```

L'API sera accessible sur : `http://localhost:8001`

## 📘 Documentation API

Une fois l'application lancée, accédez à Swagger UI