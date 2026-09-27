# Exercices - TP4


## Retrospective

#### 1. Décrivez deux problématiques liées à votre processus et proposez deux plans distincts pour les résoudre. Soyez constructifs dans vos critiques et évitez d'attribuer la faute à une personne ou un groupe en particulier ?

- **Problématique 1** : Manque de clarté dans la configuration locale pour des tests
  Plan de résolution : Documenter clairement dans le dépôt de code

- **Problématique 2** : Manque de rigueur dans la synchronisation des implémentations
  Plan de résolution : Mettre en place une check-list technique à utiliser lors de chaque ajout ou modification d’une interface. 

#### 2. Décrivez la démarche que vous avez entreprise pour intégrer de nouveaux outils technologiques. Quelles étaient les étapes du processus ? Comment avez-vous réagi aux différents bogues? Avez-vous exploré ces outils à l'aide de tests unitaires ou manuels ? Qu'avez-vous appris grâce à cette démarche ?

Nous avons intégré MongoDB comme solution de persistance des données.
- **Etapes du processus** :  
Choix de MongoDB; Mise en place d'un environnement local avec docker; création d'une implementation de persistence Mongo conforme a l'interface 

- **Gestion des bogues** :
Utilisation de logs pour déboguer les erreurs de persistance ; Comparaison avec le comportement de InMemory pour détecter les écarts

- Tests effectués :
Nous avons exploré les fonctionnalités avec des tests manuels, principalement via Postman.

- **Ce que nous avons appris** :
L’importance d’avoir une architecture bien structurée pour faciliter l’intégration de nouvelles technologies.

#### 3. Quels sont les bons coups de votre équipe ? De quelles réalisations êtes-vous fiers? Nommez-en trois .

Les principaux bons coups de notre équipe incluent l'implémentation complète et sans bogue de la plupart des fonctionnalités. Nous sommes particulièrement fiers de :
- L'intégration complète de MongoDB avec Docker
- L'ajout d'un en-tête d'authentification
- La gestion des comptes


#### 4. Quels conseils donneriez-vous aux prochains étudiants qui réaliseront ce projet ?

- Organisez votre équipe et votre méthode de travail dès le début
- Répartissez les tâches efficacement : Divisez les livrables entre les membres de l'équipe et fixez vous des échéances plus courtes pour chaque tâche.
- Adoptez une attitude professionnelle et positive tout au long du projet

#### 5. Quelles apprentissages, astuces ou techniques acquises lors de ce projet pensez-vous pouvoir réutiliser plus tard ? Décrivez-en au moins deux . Il peut s'agir d'apprentissages techniques, pratiques, liés au travail d'équipe ou encore au processus de développement.

- La collaboration et communication en équipe
- La compétence de concevoir et exécuter des tests (Unitaire , integration et persistances) fonctionnels pour valider le bon fonctionnement des exigences.
- L'acquisition des bonnes pratiques de programmation (Clean code)
- L’utilisation de Docker pour simuler un environnement de production

---

## Déclaration d'utilisation de l'IA

- L'IA a été utilisé a plusieurs reprises faciliter le débogage du développement de la fonctionnalité de renommage de groupe.

- Utilisation de l'IA pour synthétriser la documentation techniques et identifier des pistes de solutions aux problèmes techniques. L'analyse cognitive a aussi été testée pour valider la capacité à identifier les éléments clés dans une masse de documents de référence.

---

## Open Sourcing

1. Nommez trois avantages de contribuer à des projets open source en tant qu'entreprise et expliquez en quoi cela peut être bénéfique pour tous.
     >1. Réduction des coûts associés au développement. En particulier pour du code "boiler plate" qui n'a pas d'avantage concurrentiel., C'est une alternative à la décision "Build or buy"
     >2. Attirer des talents. Les projets Open Source attirent la visibilité par les développeurs passionnés. Contribuer à un projet Open Source permet donc de faire la promotion d'une culture interne de collaboration et de partage de connaissances, ce qui est attractif pour les développeurs engagés qui recherchent ce type de milieu de travail.
     >3. Améliorer la sécurité et la qualité du code. La diversité et le nombre de contributeur qui testent et font la revue du code permet de détecter rapidement des vulnérabilités et des problèmes d'architecture. On peut penser ici à la vulnérabilité "Log4J" qui avait été détectée par un contributeur Open Source.
2. Décrivez trois défis liés à la mise en place d'un projet open source et justifiez votre réponse.
     > 1. Le choix de la licence appropriée. Les implications juridiques sont majeures, et une modification ultérieure est complexe et ne retire pas les droits d'utilisations déjà accordés (ex. MySQL).
     > 2. La gouvernance et la gestion des attentes. Les contributeurs sont souvent passionnés et le font sans rémunération, ce qui entraîne des défis plus complexes que dans une entreprise où il y a une autorité hiérarchique définie. Certains désaccords peuvent venir de visions irréconciliables, ce qui peut mettre à risque la survie du projet et l'atteindre de sa mission.
     > 3. Avoir une documentation claire et accessible dès le début du projet. C'est un défi en soi en entreprise, mais encore plus grand dans un projet Open Source, où les contributeurs peuvent venir de partout dans le monde, avec une expérience très variable et variée. L'équilibre est difficile à trouver, entre la complétude, la simplicité et la quantité d'information communiquée. 
3. Quelle information vous a le plus surpris à propos de l'open source?
     > Qu'un dépôt public sans licence explicite n'accorde aucun droit d'utilisation, ce n'est pas considéré Open Source.

### Licence choisie
La licence MIT a été choisie principalement pour :
- Sa simplicité d'utilisation, ce qui facilite l'intégration par des développeurs junior. 
- Pour permettre à des développeurs innovateurs de rentabiliser leur projet en version commerciale fermée.
- Pour que le projet soit utilisé comme dépendance, puisque splitUL est définitivement la pierre angulaire d'une technologie de rupture émergente.

### Code de conduite
Le modèle de code de conduite Code Covenant a été choisi pour sa clarté et sa vaste utilisation. À l'instar de la licence MIT, nous voulons faciliter la compréhension du code de conduite pour le plus grand nombre de contributeurs, et ce, partout dans le monde. 

### Contribution
Le modère de contribution choisi a été celui de GitHub, car la vaste majorité des projets Open Source y sont hébergés. Ce guide de contribution est un incontournable dans le parcours de tout projet Open Source naissant.

## Outils d'analyse de code (SCA)

### Couverture des tests Jacoco
#### Analyse sommaire
![img.png](images/tp4/img.png)

#### Analyse détaillée
![img_1.png](images/tp4/img_1.png)
![img_2.png](images/tp4/img_2.png)

### Qualité du code : Checkstyle
#### Analyse sommaire
![img_3.png](images/tp4/img_3.png)
![img_4.png](images/tp4/img_4.png)

#### Analyse détaillée
![img_5.png](images/tp4/img_5.png)
![img_6.png](images/tp4/img_6.png)

### Sécurité du code : Snyk
#### Analyse sommaire
![img_7.png](images/tp4/img_7.png)

#### Analyse détaillée
![img_8.png](images/tp4/img_8.png)

### Métriques
#### Analyse sommaire
![img_9.png](images/tp4/img_9.png)
#### Analyse détaillée
![img_10.png](images/tp4/img_10.png)
![img_12.png](images/tp4/img_12.png)

### Vers des pratiques plus sécures
#### Identifiez trois pratiques à intégrer dans un processus de développement logiciel afin de réduire les risques de vulnérabilités.
* ##### Mise en place d’un processus d’intégration et de déploiement sécurisé (CI/CD/CS)
Cette pratique consiste à automatiser les étapes de build, de tests, et de déploiement tout en y intégrant des vérifications de sécurité. Elle permet de détecter rapidement les anomalies, de réagir efficacement aux incidents, et de garantir la traçabilité des changements effectués sur le système.
* ##### Intégration de la sécurité dès la phase de conception
Inspirée du modèle SSDLC, cette pratique consiste à considérer les aspects de sécurité dès la conception de l’architecture logicielle. Cela inclut l’analyse des menaces potentielles, la définition de règles de sécurité claires, et l’identification de points sensibles avant même d’écrire du code.
* ##### Revue continue des pratiques de codage et de gestion des accès
 Conformément aux principes DevSecOps, la sécurité doit être une responsabilité partagée. Cela implique de maintenir des standards de codage sécurisé, d’appliquer le principe du moindre privilège pour les accès, et de valider régulièrement les droits attribués aux utilisateurs et aux services.


## Github Project
![ig.png](images/tp4/ig.png)

## Milestone
![img_1.png](images/tp4/ig_1.png)

## Issues
### Issue 1
![img_2.png](images/tp4/ig_2.png)
![img_3.png](images/tp4/ig_3.png)

### Issue 2
![img_6.png](images/tp4/ig_6.png)
![img_7.png](images/tp4/ig_7.png)

### Issue 3
![img_4.png](images/tp4/ig_4.png)

## Pull Requests
### Pull Request 1
![img_8.png](images/tp4/ig_8.png)

### Pull Request 2
![img_9.png](images/tp4/ig_9.png)

### Pull Request 3
![img_10.png](images/tp4/ig_10.png)
![img_11.png](images/tp4/img_11.png)

## Git Tree
![img.png](images/tp4/imrg.png)
![img.png](images/tp4/irmg.png)


Adapté de [https://docs.github.com/en/github/building-a-strong-community/setting-guidelines-for-repository-contributors](https://docs.github.com/en/github/building-a-strong-community/setting-guidelines-for-repository-contributors). 

---

