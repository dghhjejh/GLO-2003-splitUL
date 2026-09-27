## This is a fork of the actual project



# Projet - SplitUL
Application de gestion des factures d'appartement

## Description du projet Open Source
Le projet SplitUL est une application Open Source développée pour fournir un moteur puissant d'API pour la gestion de factures d'appartements, sous la licence MIT.

### Documentation Open Source
- [Licence](./LICENCE.md)
- [Code de conduite](./CODE_OF_CONDUCT.md)
- [Guide de contribution](./CONTRIBUTING.md)

## Fonctionnalités

- [x] Gestion des groupes de facturation
- [x] Gestion des factures d'appartement
- [x] Persistance des données

## Installation

```bash
git clone https://github.com/GLO-2003-H25-eq12/GLO-2003-splitUL.git
cd GLO-2003-splitUL
mvn test -f pom.xml
mvn prettier:write -f pom.xml
mvn prettier:check -f pom.xml
```
## Pipeline CI

[![Continuous Integration Pipeline](https://github.com/GLO-2003-H25-eq12/GLO-2003-splitUL/actions/workflows/continuous_integration.yml/badge.svg)](https://github.com/GLO-2003-H25-eq12/GLO-2003-splitUL/actions/workflows/continuous_integration.yml)

[![Create and publish a Docker image on github](https://github.com/GLO-2003-H25-eq12/GLO-2003-splitUL/actions/workflows/continuous_deployment.yml/badge.svg)](https://github.com/GLO-2003-H25-eq12/GLO-2003-splitUL/actions/workflows/continuous_deployment.yml)


## Requis

- Java 21
- Maven 3.x

## Commandes

### Compilation

```
mvn compile
```

### Exécution

```
mvn exec:java
```
