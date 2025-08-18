# Système de Ticketing - Projet Éducatif

## Vue d'ensemble

Le projet Ticketing est un système complet de réservation de billets d'avion développé à des fins éducatives. Il démontre trois approches différentes du développement web Java :

1. **Sprint Framework** - Framework web MVC fait maison
2. **Application WildFly** - Application Jakarta EE utilisant Sprint
3. **Application Spring Boot** - Extension moderne avec Spring Boot

## Structure du projet

```
Ticketing/
├── 📁 bdd/                    # Scripts de base de données PostgreSQL
│   ├── script.sql            # Script de création et données de test
│   └── test.sql             # Données de test supplémentaires
├── 📁 sprint/                # Framework web fait maison
│   ├── src/                 # Code source du framework
│   ├── lib/                 # Dépendances
│   ├── script.bat          # Script de compilation
│   └── README.md           # Documentation Sprint
├── 📁 wildfly/              # Application WildFly
│   ├── src/                # Code source Java
│   ├── web/                # Pages JSP et configuration
│   ├── lib/                # Bibliothèques (sprint.jar)
│   └── deploy.bat          # Script de déploiement
├── 📁 springboot/           # Application Spring Boot
│   ├── src/                # Code source Spring Boot
│   ├── pom.xml             # Configuration Maven
│   └── mvnw                # Maven Wrapper
├── 📖 MODE_EMPLOI_TICKETING.md  # Guide complet du projet
├── 📖 MODE_EMPLOI_SPRINT.md     # Documentation détaillée Sprint
└── 📖 README.md                 # Ce fichier
```

## Fonctionnalités principales

### Système de réservation
- ✈️ Gestion des vols (création, modification, recherche)
- 🎫 Réservations avec différents types de sièges
- 👥 Gestion des utilisateurs (admin/client)
- 🏙️ Gestion des villes et aéroports
- 📸 Upload de photos pour les réservations
- 💰 Système de promotions et réductions

### Framework Sprint
- 🎯 Contrôleur frontal avec routing automatique
- 📝 Annotations pour mapping d'URLs (@Url, @Get, @Post)
- 🔐 Authentification et autorisation (@Auth)
- 📋 Gestion de formulaires avec validation
- 📤 Upload de fichiers
- 🌐 Support API REST avec JSON
- 🛡️ Validation avancée (@Required, @Email, @Min, @Max)

## Démarrage rapide

### Prérequis
- ☕ Java 21+
- 🐘 PostgreSQL
- 🔥 WildFly (pour l'app Jakarta EE)
- 📦 Maven (pour Spring Boot)

### Installation

1. **Cloner le projet**
```bash
git clone <url-du-projet>
cd Ticketing
```

2. **Configurer la base de données**
```bash
psql -U postgres -f bdd/script.sql
```

3. **Compiler le framework Sprint**
```bash
cd sprint
script.bat
```

4. **Tester l'application Spring Boot**
```bash
cd springboot
./mvnw spring-boot:run
```

## Documentation complète

📚 **[MODE_EMPLOI_TICKETING.md](MODE_EMPLOI_TICKETING.md)** - Guide complet du projet
- Architecture détaillée
- Configuration de la base de données
- Déploiement des applications
- Guide de développement pour étudiants

📚 **[MODE_EMPLOI_SPRINT.md](MODE_EMPLOI_SPRINT.md)** - Documentation du framework Sprint
- Fonctionnalités détaillées
- Exemples d'utilisation
- Guide des annotations
- Bonnes pratiques

## Technologies utilisées

### Framework Sprint (fait maison)
- Jakarta Servlets
- Annotations Java
- Reflection API
- Gson (JSON)

### Application WildFly
- Jakarta EE
- JSP/JSTL
- Framework Sprint
- PostgreSQL JDBC

### Application Spring Boot
- Spring Boot 3.4.4
- Spring Security
- Spring Data JPA
- Thymeleaf
- PostgreSQL

## Objectifs pédagogiques

Ce projet permet d'apprendre :

1. **Concepts fondamentaux**
   - Architecture MVC
   - Pattern Front Controller
   - Injection de dépendances
   - Gestion des sessions

2. **Développement web Java**
   - Servlets et JSP
   - Annotations personnalisées
   - Validation de formulaires
   - Upload de fichiers

3. **Frameworks modernes**
   - Comparaison framework maison vs Spring
   - Migration d'applications
   - Bonnes pratiques

4. **Base de données**
   - Modélisation relationnelle
   - Requêtes SQL complexes
   - Vues et contraintes

## Contribution

Ce projet est conçu pour l'apprentissage. Les étudiants peuvent :

- 🔧 Étendre le framework Sprint
- 🎨 Améliorer l'interface utilisateur
- 📊 Ajouter des fonctionnalités de reporting
- 🧪 Écrire des tests unitaires
- 📱 Créer une version mobile

## Support

Pour toute question ou problème :

1. Consulter la documentation complète
2. Vérifier les logs d'erreur
3. Tester avec les données de base fournies
4. Contacter l'équipe pédagogique

## Licence

Projet éducatif - Libre d'utilisation pour l'apprentissage

---

**Développé pour l'apprentissage du développement web Java** 🎓