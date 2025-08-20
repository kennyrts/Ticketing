# Guide d'implémentation du système de paiement

## 🎯 Ce qui a été implémenté

Un système de paiement simple intégré dans l'application WildFly existante, respectant la structure du projet.

### 📊 Base de données
- **Table `paiement`** ajoutée au script SQL principal (`bdd/script.sql`)
- Champs : `id`, `reservation_id`, `montant`, `date_paiement`, `date_creation`
- Index sur `reservation_id` pour optimiser les performances

### 🏗️ Backend Java
- **Modèle `Paiement.java`** : Gestion simple des paiements avec méthodes CRUD
- **Méthodes ajoutées dans `FrontOfficeController.java`** :
  - `front_paiement_form` : Affiche le formulaire de paiement
  - `front_paiement_traiter` : Traite le paiement soumis

### 🎨 Interface utilisateur
- **Page de paiement** (`front_paiement_form.jsp`) avec :
  - Détails de la réservation
  - Input pour le montant (pré-rempli avec le prix de la réservation)
  - Input pour la date de paiement (pré-rempli avec la date actuelle)
- **Page des réservations modifiée** (`front_reservations.jsp`) avec :
  - Bouton "Payer" pour les réservations non payées
  - Indicateur "Payé" pour les réservations déjà payées
  - Messages de succès/erreur

## 🚀 Comment utiliser

### Pour l'utilisateur :
1. Se connecter et aller dans "My Reservations"
2. Cliquer sur le bouton "💳 Payer" à côté d'une réservation non payée
3. Vérifier les détails de la réservation
4. Ajuster le montant si nécessaire
5. Modifier la date de paiement si nécessaire
6. Cliquer sur "Confirmer le paiement"
7. Retour automatique à la liste des réservations avec message de succès

### Statuts visuels :
- **Bouton bleu "Payer"** : Réservation non payée
- **Badge vert "✅ Payé"** : Réservation déjà payée

## 📁 Fichiers modifiés/créés

### Nouveaux fichiers :
- `src/model/Paiement.java` - Modèle de données pour les paiements
- `web/front_paiement_form.jsp` - Formulaire de paiement

### Fichiers modifiés :
- `bdd/script.sql` - Ajout de la table paiement
- `src/controllers/FrontOfficeController.java` - Ajout des méthodes de paiement
- `web/front_reservations.jsp` - Ajout du bouton payer et vérification du statut

## 🔧 Installation

1. **Mettre à jour la base de données** :
   ```sql
   -- Exécuter le script SQL mis à jour
   psql -U postgres -d ticketing -f bdd/script.sql
   ```

2. **Compiler les nouvelles classes** :
   ```bash
   cd wildfly
   javac -cp "lib/*;web/WEB-INF/classes" -d "web/WEB-INF/classes" src/model/Paiement.java
   javac -cp "lib/*;web/WEB-INF/classes" -d "web/WEB-INF/classes" src/controllers/FrontOfficeController.java
   ```

3. **Déployer l'application** :
   ```bash
   deploy.bat
   ```

## ✅ Fonctionnalités

### Sécurité :
- Vérification que la réservation appartient à l'utilisateur connecté
- Vérification qu'une réservation ne peut être payée qu'une seule fois
- Validation des données côté serveur

### Interface :
- Design cohérent avec le reste de l'application
- Messages de feedback (succès/erreur)
- Formulaire pré-rempli pour faciliter l'utilisation
- Responsive design

### Base de données :
- Relation avec la table `reservation` via clé étrangère
- Suppression en cascade si une réservation est supprimée
- Index pour optimiser les requêtes

## 🎨 Personnalisation

Le système est volontairement simple et peut être étendu :

### Ajouts possibles :
- Méthodes de paiement multiples (carte, PayPal, etc.)
- Validation de paiement par un admin
- Historique des paiements
- Remboursements
- Notifications par email

### Modification des styles :
Les styles CSS sont dans `front_reservations.jsp` et `front_paiement_form.jsp` et peuvent être facilement personnalisés.

## 🐛 Dépannage

### Erreurs courantes :
1. **Erreur de compilation** : Vérifier que `sprint.jar` est dans `lib/`
2. **Erreur SQL** : Vérifier que la table `paiement` a été créée
3. **Erreur 404** : Vérifier que les URLs sont correctes dans le contrôleur

### Logs à vérifier :
- Logs WildFly pour les erreurs de déploiement
- Logs de base de données pour les erreurs SQL
- Console du navigateur pour les erreurs JavaScript

---

**Système de paiement simple et fonctionnel intégré avec succès !** ✅