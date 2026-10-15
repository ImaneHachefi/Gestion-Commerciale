# Gestion commerciale : Hibernate / JPA

Application Java (Maven, WAR) de gestion commerciale utilisant **Hibernate 5.6** (API JPA) et **MySQL 8**.

## Fonctionnalités
- Configuration Hibernate (`hibernate.cfg.xml`) + `SessionUtil`
- Entités : `Famille`, `Article`, `Vente`, `LigneVente` (clé composée), `Personne` / `Acheteur` / `Vendeur` (héritage JOINED)
- `BaseDAO` générique + `GestionService` générique
- Named Queries, Criteria API, pagination JPQL, JOIN FETCH
- Transaction explicite avec rollback
- Rapport : chiffre d'affaires par famille du mois précédent (servlet `/rapport`)

## Structure
```
src/main/java/main/App.java
src/main/java/org/example/{entities,dao,service,util,web}
src/main/resources/hibernate.cfg.xml
src/main/webapp/WEB-INF/web.xml
```

## Prérequis
- JDK 17, Maven 3.8+
- MySQL 8 démarré sur `localhost:3306`

## Configuration
Modifier si besoin dans `hibernate.cfg.xml` **et** dans `App.java` :
```
jdbc:mysql://localhost:3306/gestion_commerciale?useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true
utilisateur : root / mot de passe : password
```

## Lancement
1. `mvn clean install`
2. Exécuter `main.App` depuis IntelliJ (clic droit > Run 'App.main()')
3. (optionnel) Déployer le WAR sur Tomcat 9 puis ouvrir `http://localhost:8080/gestion-commerciale/rapport`

---

## Résultats et captures d'écran

### 1. Structure du projet

<img width="524" height="818" alt="image" src="https://github.com/user-attachments/assets/df698b82-b050-442a-a7a8-83474d8d4bb1" />

### 2. Création automatique des tables (hbm2ddl = update)

<img width="723" height="796" alt="image" src="https://github.com/user-attachments/assets/bb6193be-ca85-4e57-b5c0-5f190d77123a" />

### 3. Première insertion : Famille
<img width="509" height="260" alt="image" src="https://github.com/user-attachments/assets/f1cb5aad-531a-4f8c-8033-64adea65785b" />

### 4. Articles liés à leur famille (ManyToOne / OneToMany)
<img width="1155" height="805" alt="image" src="https://github.com/user-attachments/assets/83fc7e9a-0bfb-4480-9a0d-abf6e1b5b992" />

### 5. Héritage JOINED (personnes / acheteurs / vendeurs)
<img width="1420" height="803" alt="image" src="https://github.com/user-attachments/assets/c431cced-d3f6-45f3-a7ce-edd9c8393482" />

### 6. Vente avec clé composée (lignes_vente)
<img width="1327" height="800" alt="image" src="https://github.com/user-attachments/assets/003eb49f-0360-42c1-a001-9c4faac34489" />

### 7. Named Queries, Criteria API, pagination
<img width="598" height="513" alt="image" src="https://github.com/user-attachments/assets/fd969a97-0bef-4b10-9b56-ef36492d9581" />
<img width="937" height="671" alt="image" src="https://github.com/user-attachments/assets/8df849d5-eb66-4e1f-a8c3-a5121a5e9fa8" />

<img width="726" height="440" alt="image" src="https://github.com/user-attachments/assets/18cb6d2c-ba65-4eeb-84d0-5d95ec2b9e10" />
<img width="958" height="663" alt="image" src="https://github.com/user-attachments/assets/481c2588-aa90-4a15-862b-4864f56db031" />

<img width="692" height="610" alt="image" src="https://github.com/user-attachments/assets/526ce256-a4b4-4fa4-8a6b-a3dd4413eadb" />
<img width="962" height="672" alt="image" src="https://github.com/user-attachments/assets/66506abf-da50-4d96-8212-67bb06475af2" />

<img width="670" height="847" alt="image" src="https://github.com/user-attachments/assets/fd4f6cbb-6dc5-41d4-bcf1-7c7055dba293" />
<img width="1007" height="676" alt="image" src="https://github.com/user-attachments/assets/8f6f1ef1-dd38-4c52-901b-5733dc051352" />


### 8. Transaction annulée (rollback)
<img width="796" height="739" alt="image" src="https://github.com/user-attachments/assets/811442ab-bf00-4965-b5ce-bcff088a5e9c" />

### 9. Rapport CA par famille
<img width="765" height="726" alt="image" src="https://github.com/user-attachments/assets/f076eea3-abe3-482e-8a6c-05e09c3e6e33" />
<img width="1452" height="830" alt="image" src="https://github.com/user-attachments/assets/0e41849a-9e4d-4b05-a11c-d34a1f5d3494" />
<img width="1691" height="225" alt="image" src="https://github.com/user-attachments/assets/84d478b1-b22c-4cfb-844b-cb0ea94f04c8" />

### 10. Tests JUnit
> Capture : `mvn test` : BUILD SUCCESS
![Tests](docs/captures/10-tests.png)

---

## Requêtes SQL de vérification
```sql
USE gestion_commerciale;

SELECT * FROM familles;

SELECT a.id, a.libelle, a.prix, f.nom AS famille
FROM articles a JOIN familles f ON a.famille_id = f.id;

SELECT p.id, p.nom, ac.date_inscription, v.adresse
FROM personnes p
LEFT JOIN acheteurs ac ON ac.id = p.id
LEFT JOIN vendeurs v ON v.id = p.id;

SELECT lv.vente_id, a.libelle, lv.quantite, (a.prix * lv.quantite) AS total
FROM lignes_vente lv JOIN articles a ON a.id = lv.article_id;

SELECT f.nom, SUM(a.prix * lv.quantite) AS ca
FROM lignes_vente lv
JOIN articles a ON a.id = lv.article_id
JOIN familles f ON f.id = a.famille_id
GROUP BY f.id, f.nom;
```

## Bonnes pratiques appliquées
- Paramètres nommés (jamais de concaténation dans les requêtes)
- Un `EntityManager` par unité de travail, toujours fermé dans `finally`
- `JOIN FETCH` plutôt que `EAGER` pour éviter `LazyInitializationException`
- `SessionUtil.shutdown()` appelé via `SessionListener` à l'arrêt du serveur
