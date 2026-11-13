# Backlog Jira — Sprint 3, Brief 3

Ce backlog est prêt à être saisi dans Jira. Le projet ne dispose pas d’une connexion Jira configurée dans cet environnement.

## US 1 — Tests automatisés de l’API

**En tant que** développeur AtlasRH, **je veux** tester les services, endpoints et requêtes JPA, **afin de** détecter les régressions des règles RH avant livraison.

**Critères d’acceptation**

- Les règles de matricule/e-mail, manager, contrat actif, jours ouvrés, solde, décision et annulation ont des cas positifs et négatifs.
- Les tests MVC contrôlent JSON et codes HTTP 200, 201, 400, 404 et 409.
- Les tests JPA vérifient filtres, recherche, pagination et tri.
- Tous les tests sont reproductibles et aucun n’est ignoré.

**Tâches** : tests Mockito de chaque service; tests `@WebMvcTest`/MockMvc; tests `@DataJpaTest`; compléter les cas d’erreur.

## US 2 — Validation de bout en bout

**En tant que** responsable technique, **je veux** valider les parcours fonctionnels à travers toutes les couches, **afin de** confirmer que l’API et la persistance fonctionnent ensemble.

**Critères d’acceptation**

- Un scénario crée département et employés, soumet puis approuve une demande.
- Un scénario refuse la suppression d’un département occupé.
- Les tests utilisent exclusivement une base dédiée aux tests, séparée de la base de développement.

**Tâches** : configurer H2 sous profil `test`; écrire les parcours `@SpringBootTest`; vérifier l’isolation de la base.

## US 3 — Mesure et rapport de qualité

**En tant que** direction technique, **je veux** consulter des indicateurs de couverture et d’analyse statique, **afin de** décider de l’aptitude à la préproduction.

**Critères d’acceptation**

- `mvn verify` réussit et génère JaCoCo avec au moins 80 % de couverture des lignes.
- SonarQube fournit les nombres de bugs, vulnérabilités, code smells et le statut du Quality Gate.
- Le rapport documente les résultats mesurés, corrections et limites; le README explique les commandes de lancement.
- La démonstration de 15 minutes montre l’exécution, JaCoCo et SonarQube.

**Tâches** : ajouter JaCoCo et son seuil; lancer l’analyse SonarQube; corriger les constats prioritaires; compléter le rapport et le README; préparer la démonstration.
