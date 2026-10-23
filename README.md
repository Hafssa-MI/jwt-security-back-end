# Gestion des incidents - Backend
# Auteur : HAFSSA MIFTAH IDRISSI
# SDIA2 : 2026/2027

API REST sécurisée avec Spring Boot, Spring Security et JWT. Elle permet à des utilisateurs d'une entreprise de déclarer et de suivre des incidents informatiques ou de cybersécurité.

## Technologies

- Java 21
- Spring Boot 4.1.1 (Web MVC, Data JPA, Validation)
- Spring Security + JWT (jjwt)
- Base de données H2 en mémoire
- Swagger / OpenAPI (springdoc)

## Lancer le backend

Prérequis : JDK 21.

```
cd backend
mvnw.cmd spring-boot:run
```

On peut aussi lancer la classe `DemoApplication` depuis IntelliJ.

- API : http://localhost:8080
- Swagger : http://localhost:8080/swagger-ui/index.html
- Console H2 : http://localhost:8080/h2-console (JDBC URL : `jdbc:h2:mem:securitydb`, utilisateur `sa`, mot de passe vide)

La base est en mémoire : les données sont perdues à chaque redémarrage.

## Configuration

Dans `src/main/resources/application.properties` :

| Propriété | Description |
|---|---|
| `server.port` | Port du serveur (8080) |
| `jwt.secret` | Clé de signature du JWT (Base64) |
| `jwt.expiration` | Durée de vie du token en ms (300000 = 5 minutes) |

CORS autorise l'application React sur `http://localhost:5173`.

## Comptes de test

Le technicien et l'administrateur sont créés automatiquement au démarrage.

| Rôle | Email | Mot de passe |
|---|---|---|
| ADMIN | admin@test.com | admin123 |
| TECHNICIAN | tech@test.com | tech123 |
| EMPLOYEE | à créer avec `/auth/register` | au choix (6 caractères minimum) |

## Rôles

- **EMPLOYEE** : déclare ses incidents, consulte les siens, modifie son profil.
- **TECHNICIAN** : consulte les incidents qui lui sont affectés, change leur statut, ajoute des notes de traitement.
- **ADMIN** : consulte tous les incidents et utilisateurs, affecte les incidents, active ou désactive les comptes, consulte les statistiques.

L'inscription attribue toujours le rôle EMPLOYEE.

## Endpoints principaux

| Méthode | URL | Accès |
|---|---|---|
| POST | /auth/register | Public |
| POST | /auth/login | Public |
| GET | /api/profile | Authentifié |
| PUT | /api/profile | Authentifié |
| PUT | /api/profile/password | Authentifié |
| POST | /api/incidents | EMPLOYEE |
| GET | /api/incidents/my | EMPLOYEE |
| GET | /api/incidents/assigned | TECHNICIAN |
| GET | /api/incidents | ADMIN |
| GET | /api/incidents/{id} | Propriétaire, technicien affecté ou ADMIN |
| PUT | /api/incidents/{id}/status | TECHNICIAN ou ADMIN |
| PUT | /api/incidents/{id}/assign/{technicianId} | ADMIN |
| POST | /api/incidents/{id}/comments | TECHNICIAN |
| GET | /api/admin/users | ADMIN |
| PUT | /api/admin/users/{id}/enable | ADMIN |
| PUT | /api/admin/users/{id}/disable | ADMIN |
| GET | /api/admin/statistics | ADMIN |

Pour tester une route protégée dans Swagger : appeler `/auth/login`, copier le token, cliquer sur **Authorize** et coller le token (sans le mot `Bearer`).

## Sécurité

- Mots de passe encodés avec BCrypt.
- JWT contenant l'email, la date de création, la date d'expiration et le rôle.
- Sessions stateless (`SessionCreationPolicy.STATELESS`).
- Routes protégées avec `@PreAuthorize` et `/api/admin/**` réservé à ADMIN.
- Vérification du propriétaire d'un incident côté serveur (un employé ne peut pas lire l'incident d'un autre).
- Un technicien ne modifie que les incidents qui lui sont affectés.
- Un incident CLOSED ne peut plus changer de statut.
- Un compte désactivé ne peut plus se connecter, même avec un ancien token.
- Validation des données avec `@Valid`, `@NotBlank`, `@Email`, `@Size`.
- Erreurs renvoyées en JSON propre (`status`, `message`) via `@RestControllerAdvice`.

## Fonctionnalités réalisées

- Inscription et connexion avec JWT
- Gestion des rôles et protection des routes
- Déclaration et suivi des incidents
- Affectation à un technicien
- Historique des changements d'un incident
- Notes de traitement
- Gestion du profil et changement de mot de passe
- Statistiques pour le tableau de bord administrateur


## Structure

```
src/main/java/com/example/demo
  config/       SecurityConfig, DataInitializer, OpenApiConfig
  controller/   AuthController, ProfileController, IncidentController, AdminController
  dto/          objets de requête et de réponse
  entity/       AppUser, Incident, IncidentHistory, IncidentComment
  exception/    ApiException, GlobalExceptionHandler
  repository/   interfaces JPA
  security/     JwtAuthenticationFilter
  service/      JwtService, CustomUserDetailsService, IncidentService, ProfileService
```