# Mode d'emploi - Framework Sprint

## Introduction

Sprint est un framework web MVC fait maison inspiré de Spring MVC, conçu pour l'apprentissage des concepts fondamentaux du développement web Java. Il fournit une architecture simple mais complète pour créer des applications web.

## Architecture du Framework

### Structure des packages

```
mg.itu.prom16/
├── annotations/           # Annotations du framework
│   ├── validation/       # Annotations de validation
│   ├── Controller.java   # Marque les contrôleurs
│   ├── Url.java         # Mapping d'URLs
│   ├── Get.java         # Requêtes GET
│   ├── Post.java        # Requêtes POST
│   ├── Param.java       # Paramètres de requête
│   ├── FormObject.java  # Objets de formulaire
│   ├── FormField.java   # Champs de formulaire
│   ├── FileUpload.java  # Upload de fichiers
│   ├── Auth.java        # Authentification
│   └── Restapi.java     # API REST
├── controller/           # Contrôleur frontal
│   └── FrontController.java
└── util/                # Classes utilitaires
    ├── ModelView.java   # Vue avec modèle
    ├── MySession.java   # Gestion de session
    ├── UploadedFile.java # Fichier uploadé
    └── ...
```

## Fonctionnalités détaillées

### 1. Contrôleur frontal (FrontController)

Le `FrontController` est le point d'entrée unique de l'application. Il :

- **Scanne automatiquement** les contrôleurs au démarrage
- **Route les requêtes** vers les bonnes méthodes
- **Gère les paramètres** et la conversion de types
- **Traite les formulaires** avec validation
- **Gère l'authentification** et l'autorisation
- **Supporte les uploads** de fichiers
- **Fournit des API REST** avec JSON

#### Configuration dans web.xml

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
    <multipart-config>
        <max-file-size>10485760</max-file-size>
        <max-request-size>20971520</max-request-size>
    </multipart-config>
</servlet>
<servlet-mapping>
    <servlet-name>FrontController</servlet-name>
    <url-pattern>/</url-pattern>
</servlet-mapping>
```

### 2. Annotations principales

#### @Controller
Marque une classe comme contrôleur Sprint.

```java
@Controller
public class VolController {
    // Méthodes du contrôleur
}
```

#### @Url(value = "chemin")
Associe une URL à une méthode.

```java
@Url("vol/list")
public ModelView listVols() {
    // Logique métier
}
```

#### @Get et @Post
Spécifient le type de requête HTTP accepté.

```java
@Get
@Url("vol/search")
public ModelView searchForm() {
    return new ModelView("search_form.jsp");
}

@Post
@Url("vol/search")
public ModelView searchResults(@FormObject SearchForm form) {
    // Traitement de la recherche
}
```

#### @Param(name = "parametre")
Récupère un paramètre de requête spécifique.

```java
@Url("vol/details")
public ModelView volDetails(@Param(name = "id") String volId) {
    Vol vol = volService.getById(Integer.parseInt(volId));
    ModelView mv = new ModelView("vol_details.jsp");
    mv.addObject("vol", vol);
    return mv;
}
```

#### @FormObject
Lie automatiquement les paramètres de requête à un objet.

```java
@Post
@Url("vol_create")
public ModelView createVol(@FormObject Vol vol) {
    ModelView mv = new ModelView();
    try {
        // Insert the flight
        int volId = Vol.insert(
            vol.getAvionId(), 
            vol.getVilleDepartId(), 
            vol.getVilleArriveeId(), 
            vol.getHeureDepart(), 
            vol.getHeuresAvantReservation(), 
            vol.getHeuresAvantAnnulation()
        );

        // Redirect back to form with success message
        mv.setUrl("vol_form");
        mv.addObject("success", "Flight created successfully with ID: " + volId);
    } catch (Exception e) {
        mv.setUrl("vol_form");
        mv.addObject("error", "Error creating flight: " + e.getMessage());
    }
    return mv;
}
```

#### @FormField(name = "champ")
Mappe un champ de formulaire HTML à un attribut d'objet.

```java
public class VolForm {
    @FormField(name = "modele_avion")
    private String modele;
    
    @FormField(name = "ville_depart")
    private String villeDepart;
    
    // Getters et setters...
}
```

#### @FileUpload(name = "fichier")
Gère l'upload de fichiers.

```java
@Post
@Url("front_vol_reserver")
public ModelView reserverVol(@Param(name = "vol_type_siege_id") String volTypeSiegeId,
                            @Param(name = "vol_id") String volId,
                            @Param(name = "type_siege_id") String typeSiegeId,
                            @Param(name = "prix") String prixStr,
                            @FileUpload(name = "photo") UploadedFile photo,
                            MySession session) {
    ModelView mv = new ModelView();
    
    try {
        int userId = (int) session.get("id");
        int volIdInt = Integer.parseInt(volId);
        int typeSiegeIdInt = Integer.parseInt(typeSiegeId);
        double prix = Double.parseDouble(prixStr);
        
        // Traiter la photo
        String photoPath = null;
        if (photo != null && !photo.getFileName().isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + photo.getFileName();
            File uploadDir = new File("uploads/reservations");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // Save the file using InputStream
            File destFile = new File(uploadDir, fileName);
            try (InputStream in = photo.getInputStream();
                FileOutputStream out = new FileOutputStream(destFile)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
            }
            photoPath = "reservations/" + fileName;
        }            
        
        // Insérer la réservation
        Reservation.insert(volIdInt, userId, typeSiegeIdInt, false, prix, photoPath);
        
        mv.setUrl("front_reservations");
        mv.addObject("success", "Réservation effectuée avec succès!");
        
    } catch (Exception e) {
        mv.setUrl("front_vol_search_form.jsp");
        mv.addObject("error", "Erreur lors de la réservation: " + e.getMessage());
    }
    
    return mv;
}
```

#### @Auth(value = "role")
Contrôle l'accès par rôle utilisateur.

```java
@Controller
@Auth("admin")  // Toute la classe nécessite le rôle admin
public class AdminController {
    
    @Url("admin/dashboard")
    public ModelView dashboard() {
        // Accessible uniquement aux admins
    }
    
    @Auth("user")  // Cette méthode nécessite seulement le rôle user
    @Url("admin/profile")
    public ModelView profile() {
        // Accessible aux users et admins
    }
}
```

#### @Restapi
Retourne du JSON au lieu d'une vue HTML.

```java
@Restapi
@Url("api/vols")
public List<Vol> getVolsApi() {
    return volService.getAllVols();
}

@Restapi
@Url("api/vol/details")
public ModelView getVolDetailsApi(@Param(name = "id") String id) {
    ModelView mv = new ModelView();
    mv.addObject("vol", volService.getById(Integer.parseInt(id)));
    mv.addObject("status", "success");
    return mv; // Sera converti en JSON
}
```

### 3. Annotations de validation

#### @Required(message = "message d'erreur")
Champ obligatoire.

```java
public class VolForm {
    @FormField(name = "modele")
    @Required(message = "Le modèle d'avion est obligatoire")
    private String modele;
}
```

#### @Email(message = "message d'erreur")
Validation d'adresse email.

```java
public class UserForm {
    @FormField(name = "email")
    @Email(message = "Format d'email invalide")
    private String email;
}
```

#### @Min(value = x, message = "message")
Valeur minimale pour les nombres.

```java
public class ReservationForm {
    @FormField(name = "age")
    @Min(value = 0, message = "L'âge doit être positif")
    private int age;
}
```

#### @Max(value = x, message = "message")
Valeur maximale pour les nombres.

```java
public class ReservationForm {
    @FormField(name = "age")
    @Max(value = 120, message = "L'âge doit être réaliste")
    private int age;
}
```

### 4. Classes utilitaires

#### ModelView
Encapsule une vue et ses données.

**Important** : Dans Sprint, il n'y a PAS de système de redirection automatique comme `redirect:/url`. À la place, vous utilisez `mv.setUrl("nom_de_la_page")` pour spécifier vers quelle page aller.

```java
// ❌ INCORRECT - N'existe pas dans Sprint
return new ModelView("redirect:/vol/list");

// ✅ CORRECT - Utilisation réelle dans Sprint
ModelView mv = new ModelView();
mv.setUrl("vol_list");  // Redirige vers vol_list (qui appellera vol_list.jsp)
return mv;

// ✅ CORRECT - Ou directement dans le constructeur
return new ModelView("vol_list.jsp");
```

```java
public class VolController {
    
    @Url("vol/list")
    public ModelView listVols() {
        ModelView mv = new ModelView("vol_list.jsp");
        
        // Ajouter des données individuelles
        mv.addObject("title", "Liste des vols");
        mv.addObject("vols", volService.getAllVols());
        
        // Ou ajouter un map de données
        HashMap<String, Object> data = new HashMap<>();
        data.put("currentUser", getCurrentUser());
        data.put("totalVols", volService.count());
        mv.setData(data);
        
        return mv;
    }
}
```

#### MySession
Wrapper pour la session HTTP.

```java
@Post
@Url("back_login_post")
public ModelView login(@Param(name = "login") String login, 
                      @Param(name = "mdp") String mdp, 
                      MySession session) {
    ModelView mv = new ModelView();
    try {
        User user = User.login(login, mdp, "admin");    
        if (user != null) {
            session.add("id", user.getId());
            session.add("login", user.getLogin());
            session.add("ROLE-USER", user.getRole());                
            session.add("nom", user.getNom());
            mv.setUrl("vol_list");                
        } else {
            mv.setUrl("back_login.jsp");
            mv.addObject("error", "Invalid login credentials");
        }
    } catch (Exception e) {
        mv.setUrl("back_login.jsp");
        mv.addObject("error", e.getMessage());
    }
    return mv;
}
```

#### UploadedFile
Représente un fichier uploadé.

```java
@Post
@Url("document/upload")
public ModelView uploadDocument(@FileUpload(name = "document") UploadedFile file) {
    if (file != null) {
        String fileName = file.getFileName();
        String contentType = file.getContentType();
        long size = file.getSize();
        InputStream content = file.getInputStream();
        
        // Validation du type de fichier
        if (!contentType.startsWith("image/")) {
            ModelView mv = new ModelView("upload_form.jsp");
            mv.addObject("error", "Seules les images sont acceptées");
            return mv;
        }
        
        // Sauvegarder le fichier
        documentService.save(fileName, content);
    }
    
    return new ModelView("...");
}
```

## Exemples complets

### Exemple 1 : Contrôleur simple

```java
package controllers;

import mg.itu.prom16.annotations.*;
import mg.itu.prom16.util.ModelView;
import mg.itu.prom16.util.MySession;

@Controller
public class HomeController {
    
    @Get
    @Url("home")
    public ModelView home() {
        ModelView mv = new ModelView("home.jsp");
        mv.addObject("message", "Bienvenue sur Sprint Framework");
        return mv;
    }
    
    @Get
    @Url("about")
    public String about() {
        return "<h1>À propos de Sprint</h1><p>Framework MVC fait maison</p>";
    }
}
```

### Exemple 2 : Formulaire avec validation

```java
// Classe de formulaire
public class ContactForm {
    @FormField(name = "nom")
    @Required(message = "Le nom est obligatoire")
    private String nom;
    
    @FormField(name = "email")
    @Required(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    private String email;
    
    @FormField(name = "age")
    @Min(value = 18, message = "Vous devez être majeur")
    @Max(value = 100, message = "Âge invalide")
    private int age;
    
    // Getters et setters...
}

// Contrôleur
@Controller
public class ContactController {
    
    @Get
    @Url("contact")
    public ModelView contactForm() {
        return new ModelView("contact_form.jsp");
    }
    
    @Post
    @Url("contact")
    public ModelView submitContact(@FormObject ContactForm form) {
        // Si la validation échoue, Sprint redirige automatiquement
        // vers le formulaire avec les erreurs
        
        contactService.save(form);
        
        ModelView mv = new ModelView("contact_success.jsp");
        mv.addObject("message", "Votre message a été envoyé");
        return mv;
    }
}
```

### Exemple 3 : API REST

```java
@Controller
public class ApiController {
    
    @Restapi
    @Get
    @Url("api/users")
    public List<User> getAllUsers() {
        return userService.findAll();
    }
    
    @Restapi
    @Get
    @Url("api/user")
    public ModelView getUser(@Param(name = "id") String id) {
        ModelView response = new ModelView();
        
        try {
            User user = userService.findById(Integer.parseInt(id));
            response.addObject("user", user);
            response.addObject("status", "success");
        } catch (Exception e) {
            response.addObject("error", e.getMessage());
            response.addObject("status", "error");
        }
        
        return response;
    }
    
    @Restapi
    @Post
    @Url("api/user/create")
    public ModelView createUser(@FormObject UserForm form) {
        ModelView response = new ModelView();
        
        try {
            User user = userService.create(form);
            response.addObject("user", user);
            response.addObject("status", "created");
        } catch (Exception e) {
            response.addObject("error", e.getMessage());
            response.addObject("status", "error");
        }
        
        return response;
    }
}
```

### Exemple 4 : Upload de fichiers

```java
@Controller
public class FileController {
    
    @Get
    @Url("upload")
    public ModelView uploadForm() {
        return new ModelView("upload_form.jsp");
    }
    
    @Post
    @Url("upload")
    public ModelView handleUpload(
        @FormObject FileForm form,
        @FileUpload(name = "fichier") UploadedFile file) {
        
        ModelView mv = new ModelView("upload_result.jsp");
        
        if (file != null) {
            try {
                // Validation
                if (file.getSize() > 5 * 1024 * 1024) { // 5MB max
                    mv.addObject("error", "Fichier trop volumineux");
                    return mv;
                }
                
                // Sauvegarder
                String savedPath = fileService.save(file, form.getDescription());
                
                mv.addObject("success", "Fichier uploadé avec succès");
                mv.addObject("fileName", file.getFileName());
                mv.addObject("filePath", savedPath);
                
            } catch (Exception e) {
                mv.addObject("error", "Erreur lors de l'upload : " + e.getMessage());
            }
        } else {
            mv.addObject("error", "Aucun fichier sélectionné");
        }
        
        return mv;
    }
}
```

## Gestion des erreurs

Sprint gère automatiquement plusieurs types d'erreurs :

### Erreurs de validation
Quand la validation d'un `@FormObject` échoue, Sprint :
1. Redirige vers le formulaire original
2. Ajoute les erreurs dans `request.getAttribute("errors")`
3. Conserve les valeurs saisies dans `request.getAttribute("submittedValues")`

### Erreurs HTTP
Sprint génère automatiquement des pages d'erreur pour :
- **404** : URL non trouvée
- **403** : Accès refusé (authentification)
- **405** : Méthode HTTP non autorisée
- **500** : Erreur interne du serveur

### Gestion personnalisée des erreurs

```java
@Controller
public class ErrorController {
    
    @Url("error/404")
    public ModelView notFound() {
        ModelView mv = new ModelView("error_404.jsp");
        mv.addObject("message", "Page non trouvée");
        return mv;
    }
}
```

## Compilation et déploiement

### Compilation du framework

```bash
# Dans le dossier sprint/
javac -d bin -cp "lib/*" src/mg/itu/prom16/**/*.java
jar cf sprint.jar -C bin .
```

### Script de compilation (script.bat)

```batch
@echo off
echo Compilation du framework Sprint...

if not exist bin mkdir bin

javac -d bin -cp "lib/*" src/mg/itu/prom16/annotations/*.java
javac -d bin -cp "lib/*" src/mg/itu/prom16/annotations/validation/*.java
javac -d bin -cp "lib/*" src/mg/itu/prom16/util/*.java
javac -d bin -cp "lib/*" src/mg/itu/prom16/controller/*.java

jar cf sprint.jar -C bin .

echo Framework Sprint compilé avec succès !
echo Le fichier sprint.jar est prêt à être utilisé.
```

### Utilisation dans un projet

1. **Copier sprint.jar** dans `WEB-INF/lib/`
2. **Configurer web.xml** avec le FrontController
3. **Créer le package de contrôleurs** spécifié dans web.xml
4. **Compiler avec l'option -parameters** :
```bash
javac -parameters -cp "WEB-INF/lib/*" -d WEB-INF/classes src/**/*.java
```

## Bonnes pratiques

### Organisation des contrôleurs

```java
// Grouper les URLs par fonctionnalité
@Controller
public class VolController {
    @Url("vol/list")        // GET par défaut
    @Url("vol/create")      // Formulaire de création
    @Url("vol/edit")        // Formulaire d'édition
    @Url("vol/delete")      // Suppression
}
```

### Gestion des sessions

```java
@Controller
public class AuthController {
    
    @Post
    @Url("login")
    public ModelView login(@FormObject LoginForm form, MySession session) {
        User user = authenticate(form);
        if (user != null) {
            session.set("user", user);
            session.set("ROLE-USER", user.getRole());
        }
        // ...
    }
    
    @Url("logout")
    public ModelView logout(MySession session) {
        session.invalidate();
        return new ModelView("login");
    }
}
```

### Validation robuste

```java
public class UserForm {
    @FormField(name = "username")
    @Required(message = "Nom d'utilisateur obligatoire")
    private String username;
    
    @FormField(name = "email")
    @Required(message = "Email obligatoire")
    @Email(message = "Format d'email invalide")
    private String email;
    
    @FormField(name = "age")
    @Min(value = 13, message = "Âge minimum : 13 ans")
    @Max(value = 120, message = "Âge maximum : 120 ans")
    private int age;
}
```

## Limitations
- Pas de support des WebSockets
- Pas de cache intégré
- Pas de support des templates (JSP uniquement)
- Validation limitée aux types de base

## Conclusion

Sprint est un framework éducatif qui démontre les concepts fondamentaux des frameworks web modernes. Il offre une base solide pour comprendre :

- Le pattern MVC
- L'injection de dépendances basique
- La gestion des requêtes HTTP
- La validation de formulaires
- L'authentification et l'autorisation
- Les API REST

Ce framework peut servir de base pour des projets étudiants ou comme point de départ pour développer un framework plus complet.