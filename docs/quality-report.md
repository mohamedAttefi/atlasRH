# Rapport de tests et qualité — Sprint 3, Brief 3

## Périmètre livré

| Niveau | Vérifications |
|---|---|
| Service | Unicité matricule et e-mail, manager de soi-même, contrat actif unique, jours ouvrés, approbation selon le solde, annulation et refus d’annulation après décision |
| Controller | HTTP 200, 201, 400, 404 et 409; validation, JSON et liste paginée |
| Repository | Filtre par département, recherche insensible à la casse, pagination et tri |
| Intégration | Parcours création département/employés/demande puis approbation; suppression refusée pour département avec employés |

La suite d’intégration active `application-test.yml` : H2 en mémoire distinct de la base PostgreSQL de développement.

## Exécution et couverture

Commande : `mvn verify` (exécutée avec succès; 32 tests, 0 échec, 0 ignoré).

Le plugin JaCoCo génère le rapport HTML dans `target/site/jacoco/` et vérifie un seuil de couverture des lignes du bundle de 80 %.

| Mesure | Résultat |
|---|---:|
| Lignes couvertes | 280 / 342 |
| Couverture des lignes | 81,9 % |
| Branches couvertes | 56 / 102 (54,9 %) |
| Tests réussis | 32 / 32 |

Rapport HTML : `target/site/jacoco/index.html`.

## SonarQube

**État : analyse non exécutée.** Le socket Docker/Podman local (`/run/user/1000/podman/podman.sock`) n’est pas présent, et aucun exécutable `sonar-scanner` n’est installé. Il n’y a donc ni instance joignable ni résultat SonarQube; le nombre de bugs, vulnérabilités, code smells et statut Quality Gate reste à relever après démarrage d’une instance accessible et exécution de `mvn verify sonar:sonar`.

| Indicateur | Avant | Après |
|---|---:|---:|
| Bugs | Non mesuré | Non mesuré |
| Vulnérabilités | Non mesuré | Non mesuré |
| Code smells | Non mesuré | Non mesuré |
| Quality Gate | Non mesuré | Non mesuré |

### Corrections établies par les tests

- La création d’un employé sans manager était rejetée parce que deux identifiants `null` étaient comparés comme une auto-affectation. La règle vérifie maintenant l’auto-affectation seulement si un manager est fourni.
- Les configurations de test partageaient un nom de base H2 fixe entre contextes Spring. L’URL de test utilise désormais un UUID, et le test de démarrage active explicitement le profil `test`; la suite ne sélectionne plus la base PostgreSQL de développement.
- Le test d’approbation du congé configure maintenant un quota réaliste sur son type de congé afin de vérifier le scénario favorable plutôt qu’un refus causé par la valeur Java par défaut.

## Démonstration (15 minutes)

1. (2 min) Présenter le découpage unitaires / MVC / JPA / intégration et le profil H2.
2. (5 min) Lancer `mvn verify` et montrer les tests exécutés.
3. (3 min) Ouvrir `target/site/jacoco/index.html` et commenter couverture lignes/branches et zones non couvertes.
4. (3 min) Montrer le projet et le Quality Gate SonarQube; présenter les constats réels et corrections appliquées.
5. (2 min) Questions.

## Limites et suites

Les parcours d’intégration utilisent H2 et ne remplacent pas une validation de compatibilité PostgreSQL. Ajouter Testcontainers PostgreSQL est une suite possible. Exécuter les tests pour obtenir les mesures et compléter les résultats SonarQube réels avant présentation.
