# MISE EN PLACE PRATIQUE DE PIPELINE CI/CD

**Étudiant :** TAMBAT Yvan Dimitry
**Filière :** EADL4
**Date :** Février-Mars 2026
**Projet :** API REST Task Manager - DeployFast
**Stack Technique :** Java 21, Spring Boot 3.4.5, JWT, GitHub Actions, Docker, SonarCloud, PostgreSQL

---

## QUESTION 1 – CONCEPTION ARCHITECTURE ET MODÉLISATION

### 1.1 Reformulation détaillée du besoin fonctionnel

L'objectif central de ce projet est de concevoir et de déployer une API REST de gestion de tâches ("Task Manager") robuste et sécurisée. Cette application doit s'insérer dans un cycle DevOps moderne grâce à un pipeline CI/CD automatisé.

**Identification des acteurs et rôles :**

- **Administrateur Système :** Garant de l'intégrité de la plateforme, il gère les comptes utilisateurs, supervise les journaux de sécurité et assure la maintenance globale du service.
- **Responsable RH / Manager :** Acteur de supervision chargé d'analyser la productivité, de suivre l'avancement des objectifs globaux et de consulter les rapports de performance des équipes.
- **Collaborateur (Client final) :** Utilisateur cœur du système, responsable de la saisie opérationnelle de ses tâches, de la mise à jour de leurs états et de la gestion de ses propres priorités.
- **Système (Interface API) :** Responsable du traitement des requêtes, de la validation des données d'entrée, de l'application des règles de sécurité et de la persistance en base de données.

**Modules et fonctionnalités clés :**

- **Sécurité et Identité :** Inscription, authentification sécurisée et gestion de session via tokens JWT.
- **Gestion des Tâches (CRUD) :** Création, consultation (liste paginée et détail), mise à jour et suppression des tâches.
- **Organisation :** Possibilité de filtrer les tâches par statut (terminée ou en cours) et gestion de la pagination pour optimiser les performances.

**Contraintes techniques majeures :**

- Utilisation d'une authentification **stateless** avec JWT pour garantir la scalabilité.
- Objectif de couverture de code de **60%** via des tests automatisés.
- Documentation interactive via OpenAPI/Swagger pour faciliter l'intégration.

---

### 1.2 Modélisation complète des données

La modélisation repose sur une base de données relationnelle PostgreSQL, choisie pour sa fiabilité et sa gestion rigoureuse des contraintes d'intégrité.

**Structure des tables :**

- **users :** Contient l'identifiant unique (PK), l'email (unique et obligatoire), le mot de passe haché et le rôle.
- **tasks :** Contient l'identifiant unique (PK), le titre, la description, le statut (booléen) et l'identifiant du propriétaire (FK).

**Relations et contraintes d'intégrité :**

- **Relation One-To-Many :** Un utilisateur peut posséder plusieurs tâches, mais une tâche appartient strictement à un seul utilisateur.
- **Intégrité Référentielle :** Mise en œuvre de la contrainte `ON DELETE CASCADE` garantissant que la suppression d'un compte utilisateur entraîne automatiquement celle de ses tâches associées, évitant ainsi les données orphelines.
- **Validation SQL :** Utilisation de contraintes `NOT NULL` sur les champs critiques et indexation sur l'email pour des recherches d'authentification rapides.

**Justification :** Le modèle relationnel permet une structuration stricte des données, indispensable pour une application métier où la cohérence entre les utilisateurs et leurs ressources est primordiale.

---

### 1.3 Structure REST de l'API

L'interface de programmation respecte les principes fondamentaux de l'architecture REST pour assurer une interopérabilité maximale.

**Points d'entrée (Endpoints) sous le préfixe `/api/v1/` :**

- `POST /auth/register` : Création de compte (retourne `201 Created`).
- `POST /auth/login` : Authentification et retour du token JWT (retourne `200 OK`).
- `GET /tasks` : Récupération de la liste paginée des tâches de l'utilisateur.
- `GET /tasks/{id}` : Récupération du détail d'une tâche spécifique par son identifiant.
- `POST /tasks` : Création d'une nouvelle tâche.
- `PUT /tasks/{id}` : Mise à jour complète des informations d'une tâche.
- `DELETE /tasks/{id}` : Suppression d'une tâche (retourne `204 No Content`).

**Gestion des erreurs et réponses :**

- Format de réponse JSON uniforme intégrant l'horodatage, le code de statut HTTP, le libellé de l'erreur et le message détaillé.
- Centralisation du traitement des exceptions via `@RestControllerAdvice` pour garantir une réponse cohérente même en cas d'erreur imprévue.
- Le versioning via `/v1/` permet de maintenir la compatibilité descendante lors des futures évolutions de l'API.

---

### 1.4 Architecture applicative détaillée

L'application adopte une architecture en couches, favorisant le découplage et la maintenabilité.

**Organisation structurelle (Packages) :**

- **Controller :** Reçoit les requêtes HTTP, valide les entrées et délègue le traitement à la couche service.
- **Service :** Contient l'ensemble de la logique métier et les règles de gestion.
- **Repository :** Gère l'accès aux données via Spring Data JPA et l'abstraction des requêtes SQL.
- **Model (Entities) :** Représente la structure des données en base de données.
- **DTO (Data Transfer Objects) :** Assure le transfert sécurisé des données entre l'API et l'extérieur, sans exposer directement les entités JPA.

**Principes de conception :**

- **Séparation des responsabilités :** Chaque couche ne communique qu'avec la couche immédiatement inférieure.
- **Injection de dépendances :** Utilisation de l'IOC de Spring pour faciliter le testage et l'évolution du code.
- **SOLID et Clean Code :** Respect des bonnes pratiques de nommage et limitation de la complexité des méthodes.

---

## QUESTION 2 – RÉALISATION, QUALITÉ ET SÉCURITÉ

### 2.1 Mise en œuvre de la Sécurité

La sécurité est intégrée dès la conception (Security by Design).

- **Hachage BCrypt :** Les mots de passe ne sont jamais stockés en clair; ils sont hachés avec un sel dynamique via `BCryptPasswordEncoder`.
- **Stateless JWT :** L'authentification repose sur des tokens signés (HS256). L'API ne maintient aucune session côté serveur, ce qui facilite le déploiement en grappe (cluster).
- **Filtre de Sécurité :** Un filtre personnalisé intercepte chaque requête pour valider la signature et l'expiration du token.
- **Contrôle d'accès granulaire :** L'accès aux ressources est filtré par l'identifiant de l'utilisateur authentifié récupéré dans le token, empêchant ainsi un utilisateur d'accéder aux tâches d'un autre.

### 2.2 Stratégie de Qualité et Tests

Pour garantir la robustesse de l'application, une suite de tests rigoureuse a été mise en place.

- **Tests Unitaires :** Utilisation de JUnit 5 et Mockito pour valider la logique des services en isolant les bases de données.
- **Couverture de Code (JaCoCo) :** Configuration d'un seuil de **60%** d'instructions couvertes, vérifié automatiquement lors de chaque compilation.
- **Validation des entrées :** Utilisation des annotations `@Valid` de Jakarta Bean Validation pour rejeter les emails invalides ou les titres vides directement au niveau du contrôleur.

---

## QUESTION 3 – CONCEPTION DU PIPELINE CI/CD

### 3.1 Analyse et Stratégie d'automatisation

L'automatisation du cycle de vie logiciel est orchestrée pour minimiser les erreurs humaines.

- **Gestion des branches (GitFlow) :** Utilisation de la branche `main` pour la production, `develop` pour l'intégration et des branches `feature/` pour les nouveaux développements.
- **Déclencheurs du pipeline :** Le pipeline se déclenche automatiquement lors de chaque `push` sur les branches de développement ou lors d'une `Pull Request` vers la branche principale.
- **Workflow séquentiel :**
  1. Build : Compilation du projet et vérification des dépendances.
  2. Test : Exécution de la suite de tests et calcul de la couverture.
  3. Analyse de Qualité : Scan statique du code pour détecter les bugs potentiels et la dette technique.
  4. Sécurité : Scan de vulnérabilités dans les bibliothèques tierces.
  5. Docker : Création de l'image si toutes les étapes précédentes sont au vert.

---

## QUESTION 4 – IMPLÉMENTATION GITHUB ACTIONS

### 4.1 Caractéristiques du pipeline

L'implémentation repose sur GitHub Actions via le fichier `.github/workflows/ci-cd.yml`.

- **Isolation :** Chaque exécution se fait dans un environnement Linux éphémère et propre.
- **Mise en cache :** Utilisation du cache Maven pour réduire considérablement le temps de téléchargement des dépendances à chaque build.
- **Rapports :** Les résultats des tests et les rapports de couverture sont générés et archivés pour une traçabilité totale.

---

## QUESTION 5 – DÉPLOIEMENT AUTOMATIQUE (DOCKER)

### 5.1 Dockerisation et Orchestration

L'application est conteneurisée pour garantir la portabilité entre les environnements.

- **Dockerfile Multi-stage :** Une première étape compile le code, tandis qu'une seconde étape ne conserve que le binaire final dans un environnement d'exécution léger (Alpine JRE), optimisant ainsi la sécurité et la taille de l'image.
- **Docker Compose :** Permet de déployer en une seule commande l'API et sa base de données PostgreSQL, avec une gestion intégrée des réseaux et des volumes de données.
- **Redémarrage automatique :** Configuration des services pour redémarrer automatiquement en cas de défaillance matérielle ou logicielle.

---

## QUESTION 6 – OPTIMISATION ET MAINTENANCE

### 6.1 Documentation OpenAPI et Clean Code

- **Swagger UI :** Une interface interactive est disponible sur `/swagger-ui.html`, permettant de tester tous les endpoints directement depuis le navigateur.
- **DRY et KISS :** Les principes "Don't Repeat Yourself" (évitement de la duplication) et "Keep It Simple, Stupid" (simplicité du code) sont appliqués pour faciliter la maintenance future.
- **Observabilité :** Utilisation de Spring Boot Actuator pour surveiller la santé des services et l'utilisation de la mémoire en temps réel.

**Justification finale :** L'ensemble de ces choix techniques et méthodologiques assure que le projet DeployFast répond aux standards industriels de qualité, de sécurité et de performance.
