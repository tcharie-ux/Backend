# \# Greenfront - Backend API

# 

# Ce projet constitue la partie Backend (API REST) de la plateforme GreenTrack. Il est développé avec \*\*Spring Boot\*\* et gère la logique métier, la persistance des données et l'exposition des points de terminaison (endpoints) pour le frontend.

# 

# \##  Technologies utilisées

# \* \*\*Java 17\*\* (ou votre version de Java)

# \* \*\*Spring Boot 3.x\*\* (Spring Web, Spring Data JPA, Spring Security)

# \* \*\*Maven\*\* (Gestionnaire de dépendances)

# \* \*\*PostgreSQL\*\* / \*\*MySQL\*\* (Base de données)

# 

# \##  Prérequis

# Avant de lancer l'application, assurez-vous d'avoir installé :

# \* Java Development Kit (JDK 17 ou supérieur)

# \* Maven

# \* Un serveur de base de données actif

# 

# \##  Configuration

# 1\. Créez une base de données nommée `greentrack\_db` (à adapter).

# 2\. Ouvrez le fichier `src/main/resources/application.properties` (ou `.yml`) et configurez vos accès à la base de données :

# &#x20;  ```properties

# &#x20;  spring.datasource.url=jdbc:mysql://localhost:3306/greentrack\_db

# &#x20;  spring.datasource.username=votre\_utilisateur

# &#x20;  spring.datasource.password=votre\_mot\_de\_passe

# &#x20;  spring.jpa.hibernate.ddl-auto=update

