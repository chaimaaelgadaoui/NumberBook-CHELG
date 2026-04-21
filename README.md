# 📱 NumberBook - CHELG

Application Android développée dans le cadre d’un TP de programmation mobile, permettant de lire les contacts du téléphone et de les synchroniser avec un serveur distant via une API REST.

---

## Auteur

**Chaimaa ELGADAOUI **

---

## Objectif du projet

Ce projet a pour but de mettre en pratique :

* L’accès aux contacts Android via `ContentResolver`
* La gestion des permissions (`READ_CONTACTS`)
* L’utilisation de `RecyclerView`
* La communication client/serveur avec **Retrofit**
* La création d’une **API REST en PHP**
* La persistance des données dans une base **MySQL**

---

## Architecture du projet

```bash
NumberBook-CHELG/
│
├── android-app/     # Application Android (Java + Retrofit)
│
├── backend-api/     # API REST (PHP + MySQL)
│
└── README.md
```

---

## Partie Android (Client)

Fonctionnalités principales :

* 📥 Charger les contacts du téléphone
* 📋 Afficher les contacts avec RecyclerView
* 🔄 Synchroniser les contacts vers le serveur
* 🔍 Rechercher un contact (nom ou numéro)

Technologies utilisées :

* Java
* Retrofit
* Gson Converter
* RecyclerView
* Material Design

---

## 🌐 Partie Backend (Serveur)

API développée en PHP :

* `insertContact.php` → insertion des contacts
* `getAllContacts.php` → récupération des contacts
* `searchContact.php` → recherche par mot-clé

Base de données :

* MySQL
* Table : `contact`

---

## 🔗 Communication Android ↔ Backend

L’application utilise Retrofit pour envoyer des requêtes HTTP vers l’API PHP.

Exemple de flux :

1. Android lit les contacts
2. Envoie les données en JSON
3. PHP reçoit et traite la requête
4. Les données sont stockées dans MySQL

---

## ⚙️ Configuration

### 🔹 Backend

1. Installer XAMPP
2. Placer le dossier `backend-api` dans :

```bash
htdocs/
```

3. Démarrer :

* Apache
* MySQL

4. Accéder à :

```bash
http://localhost/numberbook-api/api/getAllContacts.php
```

---

### 🔹 Android

Dans `RetrofitClient.java` :

```java
private static final String BASE_URL = "http://10.0.2.2/numberbook-api/api/";
```

⚠️ `10.0.2.2` est utilisé pour accéder au localhost depuis l’émulateur Android.

---

💡 *Projet réalisé dans un objectif d’apprentissage et de maîtrise des technologies mobiles modernes.*
