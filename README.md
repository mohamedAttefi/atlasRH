# AtlasRH API REST

API de gestion RH en Java 17, Spring Boot, Spring Data JPA, PostgreSQL, Liquibase et MapStruct.

## Lancement

```bash
docker compose up -d
./mvnw spring-boot:run
```

PostgreSQL écoute sur `localhost:5434` (base `atlasrh_db`, identifiants de développement définis dans `compose.yml`). Liquibase crée les tables et initialise les types de congé. L’API écoute sur `http://localhost:8081`. Swagger UI : `/swagger-ui.html`; spécification : `/v3/api-docs`.

## Architecture

- `controller`: HTTP et validation de forme; jamais de logique métier.
- `service`: règles métiers et transactions; les controllers ne renvoient que des DTO.
- `repository`: accès JPA.
- `entity`: modèle persistant.
- `dto` et `mapper`: contrats JSON et conversion MapStruct.
- `exception`: erreurs HTTP centralisées (400, 404, 409).

## Endpoints principaux

| Domaine | Routes |
|---|---|
| Départements | `POST/GET /api/departments`, `GET/PUT/DELETE /api/departments/{id}` |
| Employés | `POST/GET /api/employees`, `GET/PUT /api/employees/{id}`, `GET /api/employees/{id}/team` |
| Contrats | `POST /api/contracts`, `POST /api/contracts/{id}/end`, `GET /api/contracts/employee/{employeeId}` |
| Congés | `POST /api/leave-requests`, `GET /api/employees/{id}/leave-requests?status=EN_ATTENTE`, `POST /api/employees/{id}/leave-requests/{requestId}/cancel` |
| Manager | `GET /api/managers/{id}/leave-requests/pending`, `POST /api/managers/{id}/leave-requests/{requestId}/decision` |
| Solde | `GET /api/employees/{id}/leave-balance`, `GET /api/employees/{id}/seniority` |

Exemple de création employé :

```json
{"matricule":"AT-001","firstName":"Salma","lastName":"Amrani","email":"salma@example.test","position":"Analyste RH","hireDate":"2024-02-01","departmentId":1,"managerId":null}
```

Exemple de décision manager : `{"approved":false,"comment":"Période non couverte"}`. Les requêtes sont en JSON. Aucune authentification n’est activée : les identifiants employés/managers sont donc passés dans les routes conformément au périmètre V1.

Les demandes calculent les jours ouvrés du lundi au vendredi, dates comprises. Les soldes sont calculés à partir des congés approuvés de l’année civile. L’annulation est réservée au propriétaire et aux demandes en attente; une décision négative exige un commentaire.

## Diagramme métier

```mermaid
erDiagram
  Department ||--o{ Employee : regroupe
  Employee o|--o{ Employee : encadre
  Employee ||--o{ Contract : possede
  Employee ||--o{ LeaveRequest : soumet
  LeaveType ||--o{ LeaveRequest : categorise
  Department { bigint id PK string name UK string description }
  Employee { bigint id PK string matricule UK string firstName string lastName string email UK string position date hireDate bigint departmentId FK bigint managerId FK }
  Contract { bigint id PK enum type date startDate date endDate decimal grossSalary boolean active }
  LeaveType { bigint id PK string name UK int daysPerYear }
  LeaveRequest { bigint id PK date startDate date endDate int requestedDays string reason enum status string comment }
```

## Planification Jira proposée

- **Epic RH**: US départements, employés, contrats. Critères: unicité nom/matricule/e-mail, département non supprimable s’il est occupé, manager différent du salarié, un contrat actif maximum.
- **Epic Congés**: US demande, décision, consultation du solde. Critères: jours ouvrés, pas de chevauchement, demande au statut attendu, solde suffisant, refus commenté, annulation propriétaire seulement.
- **Tâches transverses**: modèle Liquibase et données de référence, DTO/MapStruct, gestion des erreurs, OpenAPI, README/diagramme, démonstration.
