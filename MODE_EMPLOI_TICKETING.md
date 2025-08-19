# Mode d'emploi - Système de Ticketing

## Vue d'ensemble du projet

Le projet Ticketing est un système de réservation de billets d'avion composé de trois parties principales :

1. **Sprint** - Framework web fait maison (exporté en .jar)
2. **WildFly** - Application web utilisant Sprint et déployée sur WildFly
3. **SpringBoot** - Extension de l'application principale utilisant Spring Boot

## Architecture du projet

```
Ticketing/
├── bdd/                    # Scripts de base de données
├── sprint/                 # Framework web fait maison
├── wildfly/               # Application WildFly utilisant Sprint
├── springboot/            # Application Spring Boot
└── README.md
```

## 1. Base de données (dossier `bdd/`)

### Structure de la base de données

La base de données PostgreSQL `ticketing` contient les tables suivantes :

#### Tables principales :
- **avion** : Modèles d'avions avec date de fabrication
- **ville** : Villes de départ et d'arrivée
- **utilisateur** : Utilisateurs du système (admin/user)
- **type_siege** : Types de sièges (Business, Économique)
- **vol** : Vols avec horaires et restrictions
- **reservation** : Réservations des utilisateurs
- **enfant** : Réductions pour enfants

#### Vues importantes :
- **vol_details** : Vue complète des vols avec calculs de disponibilité
- **vol_disponibles** : Vols disponibles à la réservation

### Installation de la base de données

```bash
# Se connecter à PostgreSQL
psql -U postgres

# Exécuter le script de création
\i bdd/script.sql
```

### Données de test

Le script inclut des données de base :
- 2 types de sièges (Business, Économique)
- 2 utilisateurs (admin/admin, user/user)
- 5 villes françaises
- 4 modèles d'avions

## 2. Framework Sprint (dossier `sprint/`)

### Qu'est-ce que Sprint ?

Sprint est un framework web MVC fait maison qui fournit :
- **Contrôleur frontal** (FrontController)
- **Annotations** pour le mapping d'URLs
- **Gestion des formulaires** avec validation
- **Upload de fichiers**
- **Authentification et autorisation**
- **API REST** avec JSON

### Fonctionnalités principales

#### Annotations disponibles :
- `@Controller` : Marque une classe comme contrôleur
- `@Url("path")` : Associe une URL à une méthode
- `@Get` / `@Post` : Spécifie le type de requête HTTP
- `@Param(name="param")` : Récupère un paramètre de requête
- `@FormObject` : Lie automatiquement un formulaire à un objet
- `@FormField(name="field")` : Mappe un champ de formulaire
- `@FileUpload(name="file")` : Gestion d'upload de fichiers
- `@Auth("role")` : Contrôle d'accès par rôle
- `@Restapi` : Retourne du JSON au lieu d'une vue

#### Annotations de validation :
- `@Required(message="...")` : Champ obligatoire
- `@Email(message="...")` : Validation email
- `@Min(value=x, message="...")` : Valeur minimale
- `@Max(value=x, message="...")` : Valeur maximale

### Compilation du framework

```bash
# Aller dans le dossier sprint
cd sprint

# Exécuter le script de compilation
script.bat

# Le fichier sprint.jar sera généré dans le dossier
```

### Utilisation dans un projet web

1. **Copier le JAR** : Placer `sprint.jar` dans `WEB-INF/lib/`

2. **Configuration web.xml** :
```xml
<context-param>
    <param-name>package</param-name>
    <param-value>controllers</param-value>
</context-param>

<context-param>
    <param-name>session-role</param-name>
    <param-value>ROLE-USER</param-value>
</context-param>

<servlet>
    <servlet-name>FrontController</servlet-name>
    <servlet-class>mg.itu.prom16.controller.FrontController</servlet-class>
</servlet>
<servlet-mapping>
    <servlet-name>FrontController</servlet-name>
    <url-pattern>/</url-pattern>
</servlet-mapping>
```

3. **Créer un contrôleur** :
```java
package controllers;

import mg.itu.prom16.annotations.*;
import mg.itu.prom16.util.ModelView;

@Controller
public class VolController {
    
    @Url("vol/list")
    @Get
    public ModelView listVols() {
        ModelView mv = new ModelView("vol_list.jsp");
        // Ajouter des données
        mv.addObject("vols", volService.getAllVols());
        return mv;
    }
    
    @Url("vol/create")
    @Post
    public ModelView createVol(@FormObject VolForm form) {
        // Traitement du formulaire
        ModelView mv = new ModelView("vol_list.jsp");
        volService.createVol(form);
        return mv;
    }
}
```

4. **Objet de formulaire** :
```java
public class VolForm {
    @FormField(name="modele")
    @Required(message="Le modèle est obligatoire")
    private String modele;
    
    @FormField(name="date_fabrication")
    private String dateFabrication;
    
    // Getters et setters...
}
```

## 3. Application WildFly (dossier `wildfly/`)

### Structure de l'application

```
wildfly/
├── src/
│   ├── controllers/        # Contrôleurs Sprint
│   ├── dbconnect/         # Connexion base de données
│   └── model/             # Modèles de données
├── web/
│   ├── *.jsp              # Pages JSP
│   └── WEB-INF/
│       └── web.xml        # Configuration
├── lib/                   # Bibliothèques (sprint.jar)
└── deploy.bat            # Script de déploiement
```

### Déploiement sur WildFly

1. **Compiler l'application** :
```bash
cd wildfly
# Compiler les sources Java
javac -cp "lib/*" -d web/WEB-INF/classes src/**/*.java
```

2. **Déployer** :
```bash
# Exécuter le script de déploiement
deploy.bat
```

### Fonctionnalités de l'application

- **Gestion des vols** : Création, modification, recherche
- **Réservations** : Système de réservation avec validation
- **Authentification** : Login admin/utilisateur
- **Upload de photos** : Pour les réservations
- **Recherche avancée** : Par ville, date, type de siège

## 4. Application Spring Boot (dossier `springboot/`)

### Qu'est-ce que l'application Spring Boot ?

C'est une extension moderne de l'application principale utilisant :
- **Spring Boot 3.4.4**
- **Spring Security** pour l'authentification
- **Spring Data JPA** pour la persistance
- **Thymeleaf** pour les vues
- **PostgreSQL** comme base de données

### Structure Maven

```
springboot/
├── src/main/java/         # Code source Java
├── src/main/resources/    # Ressources (templates, config)
├── src/test/             # Tests unitaires
└── pom.xml               # Configuration Maven
```

### Lancement de l'application

```bash
cd springboot

# Avec Maven Wrapper (recommandé)
./mvnw spring-boot:run

# Ou avec Maven installé
mvn spring-boot:run
```

L'application sera accessible sur `http://localhost:8080`

### Fonctionnalités supplémentaires

- **API REST moderne** avec Spring Web
- **Sécurité renforcée** avec Spring Security
- **Validation avancée** avec Bean Validation
- **Interface moderne** avec Thymeleaf
- **Tests automatisés** avec Spring Boot Test

## Guide de développement pour étudiants

### Prérequis

1. **Java 21** ou version supérieure
2. **PostgreSQL** installé et configuré
3. **WildFly** pour l'application Jakarta EE
4. **Maven** pour l'application Spring Boot
5. **IDE** (Eclipse, IntelliJ IDEA, VS Code)

### Étapes pour commencer

1. **Cloner le projet** :
```bash
git clone <url-du-projet>
cd Ticketing
```

2. **Configurer la base de données** :
```bash
psql -U postgres -f bdd/script.sql
```

3. **Compiler Sprint** :
```bash
cd sprint
script.bat
```

4. **Tester l'application WildFly** :
```bash
cd ../wildfly
# Copier sprint.jar dans lib/
# Compiler et déployer
```

5. **Tester l'application Spring Boot** :
```bash
cd ../springboot
./mvnw spring-boot:run
```

### Conseils de développement

#### Pour modifier Sprint :
- Les sources sont dans `sprint/src/mg/itu/prom16/`
- Recompiler avec `script.bat` après modifications
- Copier le nouveau JAR dans les projets utilisant Sprint

#### Pour l'application WildFly :
- Contrôleurs dans `wildfly/src/controllers/`
- Vues JSP dans `wildfly/web/`
- Utiliser les annotations Sprint pour le routing

#### Pour l'application Spring Boot :
- Suivre les conventions Spring Boot
- Contrôleurs dans `src/main/java/com/example/ticketing/controller/`
- Templates Thymeleaf dans `src/main/resources/templates/`

### Dépannage courant

#### Erreur de compilation Sprint :
- Vérifier que Java est dans le PATH
- S'assurer que toutes les dépendances sont présentes

#### Erreur de base de données :
- Vérifier que PostgreSQL est démarré
- Contrôler les paramètres de connexion
- Exécuter le script de création de base

#### Erreur de déploiement WildFly :
- Vérifier que WildFly est démarré
- Contrôler les logs de WildFly
- S'assurer que sprint.jar est dans lib/

#### Erreur Spring Boot :
- Vérifier la configuration dans `application.properties`
- Contrôler les dépendances Maven
- Vérifier les logs de démarrage

## Conclusion

Ce système de ticketing démontre trois approches différentes du développement web Java :

1. **Sprint** : Framework fait maison, léger et éducatif
2. **WildFly** : Application Jakarta EE traditionnelle
3. **Spring Boot** : Approche moderne et productive

Chaque approche a ses avantages et permet d'apprendre différents aspects du développement web Java.