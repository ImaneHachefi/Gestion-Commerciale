package main;

import main.entities.*;
import main.service.*;
import main.util.SessionUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;

public class App {

    static final String DB_URL = "jdbc:mysql://localhost:3307/gestion_commerciale"
            + "?useSSL=false&allowPublicKeyRetrieval=true"
            + "&serverTimezone=UTC&createDatabaseIfNotExist=true";
    static final String DB_USER = "root";
    static final String DB_PASS = "password";

    public static void main(String[] args) {
        System.out.println("Base de données : " + DB_URL);

        FamilleService familleService = new FamilleService();
        ArticleService articleService = new ArticleService();
        GestionService<Vente, Long> venteService = new GestionService<>(Vente.class);
        GestionService<Personne, Long> personneService = new GestionService<>(Personne.class);
        RapportService rapportService = new RapportService();

        try {
            // 1) Première insertion : une famille
            Famille info = familleService.trouverOuCreer("Informatique");
            System.out.println("Famille créée/trouvée, ID = " + info.getId());

            // 2) Famille + articles (relation OneToMany avec cascade)
            Famille bureau = new Famille("Papeterie-" + System.currentTimeMillis() % 100000);
            bureau.addArticle(new Article("Stylo", new BigDecimal("1.20")));
            bureau.addArticle(new Article("Cahier", new BigDecimal("3.50")));
            familleService.create(bureau);

            Article clavier = new Article("Clavier", new BigDecimal("49.99"), info);
            articleService.create(clavier);
            Article souris = new Article("Souris", new BigDecimal("19.90"), info);
            articleService.create(souris);

            // 3) Héritage JOINED
            personneService.create(new Acheteur("Amine Benali", LocalDate.now()));
            personneService.create(new Vendeur("TechSupply", "Marrakech, Guéliz"));

            // 4) Vente N:N avec clé composée (daté du mois précédent pour le rapport)
            Vente vente = new Vente();
            vente.setDateVente(LocalDate.now().minusMonths(1).withDayOfMonth(10));
            vente.addArticle(clavier, 3);
            vente.addArticle(souris, 5);

            Vente venteSauvee = venteService.creerOuFusionner(vente);   // récupérer le retour
            System.out.println("Vente créée avec ID = " + venteSauvee.getId());

            // 5) Named Query / Criteria / pagination / JOIN FETCH
            System.out.println("\n--- Articles de la famille Informatique ---");
            articleService.findByFamille(info.getId()).forEach(System.out::println);

            System.out.println("\n--- Recherche libellé 'sou' ---");
            articleService.rechercherParLibelle("sou").forEach(System.out::println);

            System.out.println("\n--- Criteria : prix entre 1 et 20 ---");
            articleService.rechercher(null, new BigDecimal("1"), new BigDecimal("20"))
                    .forEach(System.out::println);

            System.out.println("\n--- Page 1 (taille 3) ---");
            articleService.findPage(1, 3).forEach(System.out::println);

            Article charge = articleService.findByIdAvecFamille(clavier.getId());
            System.out.println("\nFamille du clavier : " + charge.getFamille().getNom());

            // 6) Transaction avec rollback (prix négatif => annulée)
            articleService.creerAvecVerification(new Article("Article invalide", new BigDecimal("-5"), info));

            // 7) Rapport : CA par famille du mois précédent
            System.out.println("\n--- CA du mois précédent par famille ---");
            for (Object[] l : rapportService.chiffreAffairesMoisPrecedent()) {
                System.out.println(l[0] + " : " + l[1]);
            }

            // 8) Vérification directe en SQL via JDBC
            testerAvecSQL();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            SessionUtil.shutdown();
        }
    }

    /** Exécute des requêtes SQL directement sur la base pour vérifier les résultats. */
    private static void testerAvecSQL() throws SQLException {
        String[][] requetes = {
                {"Familles",
                        "SELECT id, nom FROM familles"},
                {"Articles avec leur famille",
                        "SELECT a.id, a.libelle, a.prix, f.nom AS famille "
                                + "FROM articles a JOIN familles f ON a.famille_id = f.id"},
                {"Personnes (JOINED : personnes + acheteurs/vendeurs)",
                        "SELECT p.id, p.nom, ac.date_inscription, v.adresse "
                                + "FROM personnes p LEFT JOIN acheteurs ac ON ac.id = p.id "
                                + "LEFT JOIN vendeurs v ON v.id = p.id"},
                {"Lignes de vente",
                        "SELECT lv.vente_id, a.libelle, lv.quantite, a.prix, (a.prix * lv.quantite) AS total "
                                + "FROM lignes_vente lv JOIN articles a ON a.id = lv.article_id"},
                {"CA par famille",
                        "SELECT f.nom, SUM(a.prix * lv.quantite) AS ca "
                                + "FROM lignes_vente lv JOIN articles a ON a.id = lv.article_id "
                                + "JOIN familles f ON f.id = a.famille_id GROUP BY f.id, f.nom"}
        };

        try (Connection cn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement st = cn.createStatement()) {
            for (String[] q : requetes) {
                System.out.println("\n=== SQL : " + q[0] + " ===");
                System.out.println(q[1]);
                try (ResultSet rs = st.executeQuery(q[1])) {
                    ResultSetMetaData md = rs.getMetaData();
                    while (rs.next()) {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 1; i <= md.getColumnCount(); i++) {
                            sb.append(md.getColumnLabel(i)).append("=").append(rs.getString(i)).append("  ");
                        }
                        System.out.println(sb);
                    }
                }
            }
        }
    }
}