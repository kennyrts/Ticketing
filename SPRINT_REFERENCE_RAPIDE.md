# Sprint Framework - Référence Rapide

## 📋 Table des Matières
1. [Configuration de Base](#configuration-de-base)
2. [Annotations de Contrôleur](#annotations-de-contrôleur)
3. [Routage d'URLs](#routage-durls)
4. [Gestion des Paramètres](#gestion-des-paramètres)
5. [Upload de Fichiers](#upload-de-fichiers)
6. [Authentification](#authentification)
7. [API REST](#api-rest)
8. [Validation](#validation)
9. [Classes Utilitaires](#classes-utilitaires)
10. [Exemples Complets](#exemples-complets)

---

## Configuration de Base

### web.xml
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

---

## Annotations de Contrôleur

### @Controller
```java
@Controller
public class HomeController {
    // Méthodes du contrôleur
}
```

### @Controller avec authentification
```java
@Controller
@Auth("admin")  // Toute la classe nécessite le rôle admin
public class AdminController {
    // Méthodes protégées
}
```

---

## Routage d'URLs

### @Url - Mapping d'URL
```java
@Url("accueil")
public ModelView home() {
    return new ModelView("home.jsp");
}
```

### @Get - Requêtes GET uniquement
```java
@Get
@Url("formulaire")
public ModelView showForm() {
    return new ModelView("form.jsp");
}
```

### @Post - Requêtes POST uniquement
```java
@Post
@Url("formulaire")
public ModelView processForm(@FormObject UserForm form) {
    return new ModelView("success.jsp");
}
```

---

## Gestion des Paramètres

### @Param - Paramètre simple
```java
@Url("user/details")
public ModelView userDetails(@Param(name = "id") String userId) {
    User user = userService.getById(Integer.parseInt(userId));
    ModelView mv = new ModelView("user_details.jsp");
    mv.addObject("user", user);
    return mv;
}
```

### @FormObject - Objet de formulaire
```java
// Classe de formulaire
public class ContactForm {
    @FormField(name = "nom")
    private String nom;
    
    @FormField(name = "email")
    private String email;
    
    // Getters et setters...
}

// Contrôleur
@Post
@Url("contact")
public ModelView submitContact(@FormObject ContactForm form) {
    contactService.save(form);
    return new ModelView("contact_success.jsp");
}
```

### @FormField - Mapping de champs
```java
public class ProductForm {
    @FormField(name = "product_name")
    private String name;
    
    @FormField(name = "product_price")
    private double price;
}
```

---

## Upload de Fichiers

### @FileUpload - Upload simple
```java
@Post
@Url("upload")
public ModelView handleUpload(@FileUpload(name = "document") UploadedFile file) {
    ModelView mv = new ModelView("upload_result.jsp");
    
    if (file != null) {
        String fileName = file.getFileName();
        String contentType = file.getContentType();
        long size = file.getSize();
        InputStream content = file.getInputStream();
        
        // Sauvegarder le fichier
        fileService.save(file);
        mv.addObject("success", "Fichier uploadé avec succès");
    }
    
    return mv;
}
```

### Upload avec validation
```java
@Post
@Url("photo/upload")
public ModelView uploadPhoto(@FileUpload(name = "photo") UploadedFile photo) {
    ModelView mv = new ModelView("upload_result.jsp");
    
    if (photo != null && !photo.getFileName().isEmpty()) {
        // Validation du type
        if (!photo.getContentType().startsWith("image/")) {
            mv.addObject("error", "Seules les images sont acceptées");
            return mv;
        }
        
        // Sauvegarde
        String fileName = System.currentTimeMillis() + "_" + photo.getFileName();
        File uploadDir = new File("uploads");
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        
        File destFile = new File(uploadDir, fileName);
        try (InputStream in = photo.getInputStream();
             FileOutputStream out = new FileOutputStream(destFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        }
        
        mv.addObject("success", "Photo uploadée avec succès");
        mv.addObject("fileName", fileName);
    }
    
    return mv;
}
```

---

## Authentification

### @Auth - Contrôle d'accès
```java
// Au niveau de la classe
@Controller
@Auth("admin")
public class AdminController {
    // Toutes les méthodes nécessitent le rôle admin
}

// Au niveau de la méthode
@Controller
public class MixedController {
    
    @Auth("admin")
    @Url("admin/dashboard")
    public ModelView adminDashboard() {
        return new ModelView("admin_dashboard.jsp");
    }
    
    @Auth("user")
    @Url("user/profile")
    public ModelView userProfile() {
        return new ModelView("user_profile.jsp");
    }
}
```

### Gestion de session
```java
@Post
@Url("login")
public ModelView login(@Param(name = "username") String username,
                      @Param(name = "password") String password,
                      MySession session) {
    ModelView mv = new ModelView();
    
    User user = userService.authenticate(username, password);
    if (user != null) {
        session.add("id", user.getId());
        session.add("username", user.getUsername());
        session.add("ROLE-USER", user.getRole());
        mv.setUrl("dashboard.jsp");
    } else {
        mv.setUrl("login.jsp");
        mv.addObject("error", "Identifiants invalides");
    }
    
    return mv;
}

@Url("logout")
public ModelView logout(MySession session) {
    session.delete("id");
    session.delete("username");
    session.delete("ROLE-USER");
    return new ModelView("login.jsp");
}
```

---

## API REST

### @Restapi - Retour JSON
```java
// Liste simple
@Restapi
@Get
@Url("api/users")
public List<User> getAllUsers() {
    return userService.findAll();
}

// Avec ModelView pour plus de contrôle
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

// API POST
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
```

---

## Validation

### @Required - Champ obligatoire
```java
public class UserForm {
    @FormField(name = "username")
    @Required(message = "Le nom d'utilisateur est obligatoire")
    private String username;
}
```

### @Email - Validation email
```java
public class ContactForm {
    @FormField(name = "email")
    @Email(message = "Format d'email invalide")
    private String email;
}
```

### @Min - Valeur minimale
```java
public class ProductForm {
    @FormField(name = "price")
    @Min(value = 0, message = "Le prix doit être positif")
    private double price;
    
    @FormField(name = "quantity")
    @Min(value = 1, message = "La quantité doit être au moins 1")
    private int quantity;
}
```

### @Max - Valeur maximale
```java
public class UserForm {
    @FormField(name = "age")
    @Max(value = 120, message = "L'âge doit être réaliste")
    private int age;
}
```

### Validation combinée
```java
public class RegistrationForm {
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

---

## Classes Utilitaires

### ModelView - Vue avec données
```java
// Constructeur avec URL
ModelView mv = new ModelView("products.jsp");

// Ou setter
ModelView mv = new ModelView();
mv.setUrl("products.jsp");

// Ajouter des données
mv.addObject("title", "Liste des produits");
mv.addObject("products", productService.getAll());

// Ajouter un map de données
HashMap<String, Object> data = new HashMap<>();
data.put("currentUser", getCurrentUser());
data.put("totalProducts", productService.count());
mv.setData(data);

return mv;
```

### MySession - Gestion de session
```java
// Ajouter des données
session.add("userId", user.getId());
session.add("username", user.getUsername());

// Récupérer des données
int userId = (int) session.get("userId");
String username = (String) session.get("username");

// Supprimer des données
session.delete("userId");

// Invalider la session
session.invalidate();
```

### UploadedFile - Fichier uploadé
```java
if (file != null) {
    String fileName = file.getFileName();      // Nom du fichier
    String contentType = file.getContentType(); // Type MIME
    long size = file.getSize();                // Taille en bytes
    InputStream content = file.getInputStream(); // Contenu du fichier
    
    // Traitement du fichier...
}
```

---

## Exemples Complets

### Contrôleur CRUD complet
```java
@Controller
@Auth("admin")
public class ProductController {
    
    // Liste des produits
    @Get
    @Url("products")
    public ModelView listProducts() {
        ModelView mv = new ModelView("products.jsp");
        mv.addObject("products", productService.getAll());
        return mv;
    }
    
    // Formulaire de création
    @Get
    @Url("product/new")
    public ModelView newProductForm() {
        ModelView mv = new ModelView("product_form.jsp");
        mv.addObject("categories", categoryService.getAll());
        return mv;
    }
    
    // Création d'un produit
    @Post
    @Url("product/create")
    public ModelView createProduct(@FormObject ProductForm form) {
        ModelView mv = new ModelView();
        try {
            Product product = productService.create(form);
            mv.setUrl("products");
            mv.addObject("success", "Produit créé avec succès");
        } catch (Exception e) {
            mv.setUrl("product_form.jsp");
            mv.addObject("error", "Erreur : " + e.getMessage());
            mv.addObject("categories", categoryService.getAll());
        }
        return mv;
    }
    
    // Détails d'un produit
    @Get
    @Url("product/details")
    public ModelView productDetails(@Param(name = "id") String productId) {
        ModelView mv = new ModelView();
        try {
            Product product = productService.getById(Integer.parseInt(productId));
            mv.setUrl("product_details.jsp");
            mv.addObject("product", product);
        } catch (Exception e) {
            mv.setUrl("products");
            mv.addObject("error", "Produit non trouvé");
        }
        return mv;
    }
    
    // Suppression d'un produit
    @Post
    @Url("product/delete")
    public ModelView deleteProduct(@Param(name = "id") String productId) {
        ModelView mv = new ModelView("products");
        try {
            boolean deleted = productService.delete(Integer.parseInt(productId));
            if (deleted) {
                mv.addObject("success", "Produit supprimé avec succès");
            } else {
                mv.addObject("error", "Produit non trouvé");
            }
        } catch (Exception e) {
            mv.addObject("error", "Erreur lors de la suppression");
        }
        mv.addObject("products", productService.getAll());
        return mv;
    }
}
```

### Formulaire avec validation et upload
```java
// Classe de formulaire
public class ProductForm {
    @FormField(name = "name")
    @Required(message = "Le nom est obligatoire")
    private String name;
    
    @FormField(name = "description")
    private String description;
    
    @FormField(name = "price")
    @Required(message = "Le prix est obligatoire")
    @Min(value = 0, message = "Le prix doit être positif")
    private double price;
    
    @FormField(name = "category_id")
    @Required(message = "La catégorie est obligatoire")
    private int categoryId;
    
    // Getters et setters...
}

// Contrôleur
@Post
@Url("product/create")
public ModelView createProduct(@FormObject ProductForm form,
                              @FileUpload(name = "image") UploadedFile image) {
    ModelView mv = new ModelView();
    
    try {
        // Traitement de l'image
        String imagePath = null;
        if (image != null && !image.getFileName().isEmpty()) {
            // Validation du type d'image
            if (!image.getContentType().startsWith("image/")) {
                throw new Exception("Seules les images sont acceptées");
            }
            
            // Sauvegarde de l'image
            imagePath = imageService.save(image);
        }
        
        // Création du produit
        Product product = productService.create(form, imagePath);
        
        mv.setUrl("products");
        mv.addObject("success", "Produit créé avec succès");
        
    } catch (Exception e) {
        mv.setUrl("product_form.jsp");
        mv.addObject("error", "Erreur : " + e.getMessage());
        mv.addObject("categories", categoryService.getAll());
    }
    
    return mv;
}
```

### API REST complète
```java
@Controller
public class ProductApiController {
    
    // GET /api/products - Liste tous les produits
    @Restapi
    @Get
    @Url("api/products")
    public ModelView getAllProducts() {
        ModelView response = new ModelView();
        try {
            List<Product> products = productService.getAll();
            response.addObject("products", products);
            response.addObject("count", products.size());
            response.addObject("status", "success");
        } catch (Exception e) {
            response.addObject("error", e.getMessage());
            response.addObject("status", "error");
        }
        return response;
    }
    
    // GET /api/product?id=1 - Récupère un produit
    @Restapi
    @Get
    @Url("api/product")
    public ModelView getProduct(@Param(name = "id") String id) {
        ModelView response = new ModelView();
        try {
            Product product = productService.getById(Integer.parseInt(id));
            response.addObject("product", product);
            response.addObject("status", "success");
        } catch (Exception e) {
            response.addObject("error", e.getMessage());
            response.addObject("status", "error");
        }
        return response;
    }
    
    // POST /api/product/create - Crée un produit
    @Restapi
    @Post
    @Url("api/product/create")
    public ModelView createProduct(@FormObject ProductForm form) {
        ModelView response = new ModelView();
        try {
            Product product = productService.create(form);
            response.addObject("product", product);
            response.addObject("status", "created");
        } catch (Exception e) {
            response.addObject("error", e.getMessage());
            response.addObject("status", "error");
        }
        return response;
    }
    
    // POST /api/product/delete - Supprime un produit
    @Restapi
    @Post
    @Url("api/product/delete")
    public ModelView deleteProduct(@Param(name = "id") String id) {
        ModelView response = new ModelView();
        try {
            boolean deleted = productService.delete(Integer.parseInt(id));
            if (deleted) {
                response.addObject("message", "Produit supprimé avec succès");
                response.addObject("status", "success");
            } else {
                response.addObject("error", "Produit non trouvé");
                response.addObject("status", "error");
            }
        } catch (Exception e) {
            response.addObject("error", e.getMessage());
            response.addObject("status", "error");
        }
        return response;
    }
}
```

---

## 🚀 Conversion de Types Automatique

Sprint convertit automatiquement les paramètres :
- `String` → `String` (par défaut)
- `"123"` → `int`, `Integer`
- `"123.45"` → `double`, `Double`
- `"true"` → `boolean`, `Boolean`
- `"2023-12-25T10:30"` → `java.sql.Timestamp`

---

## ⚠️ Points Importants

1. **Pas de redirection automatique** : Utilisez `mv.setUrl("page")` au lieu de `redirect:/url`
2. **Validation automatique** : Les erreurs de validation redirigent automatiquement vers le formulaire
3. **Session role** : Configurez `session-role` dans web.xml pour l'authentification
4. **Multipart** : Configurez `<multipart-config>` pour l'upload de fichiers
5. **Compilation** : Utilisez `-parameters` pour préserver les noms de paramètres

---

## 📁 Structure de Projet Type

```
src/
├── controllers/
│   ├── HomeController.java
│   ├── UserController.java
│   └── ApiController.java
├── model/
│   ├── User.java
│   └── Product.java
└── forms/
    ├── UserForm.java
    └── ProductForm.java

WEB-INF/
├── lib/
│   └── sprint.jar
├── classes/
└── web.xml

web/
├── index.jsp
├── user_form.jsp
└── product_list.jsp
```

---

## 🔧 Compilation et Déploiement

```bash
# Compilation avec préservation des noms de paramètres
javac -parameters -cp "WEB-INF/lib/*" -d WEB-INF/classes src/**/*.java

# Ou avec script
./compile.sh
```

---

*Framework Sprint - Version éducative pour l'apprentissage du MVC en Java*