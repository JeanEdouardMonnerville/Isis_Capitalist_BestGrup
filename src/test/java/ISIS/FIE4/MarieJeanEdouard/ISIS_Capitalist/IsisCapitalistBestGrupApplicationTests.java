package ISIS.FIE4.MarieJeanEdouard.ISIS_Capitalist;

import generated.PallierType;
import generated.ProductType;
import generated.TyperatioType;
import generated.World;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import static  io.restassured.RestAssured.*;
import static  io.restassured.matcher.RestAssuredMatchers.*;
import static java.lang.ClassLoader.getSystemResourceAsStream;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import static  org.hamcrest.Matchers.*;


public class IsisCapitalistBestGrupApplicationTests {
    String serviceUrl = "http://localhost:8081/adventureisis/generic/";
    String uniqueID;

    public IsisCapitalistBestGrupApplicationTests() {
    }

    @BeforeClass
    public static void setUpClass() {
        // tire au sort un login unique

    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        uniqueID = UUID.randomUUID().toString();
        System.out.println(uniqueID);

    }

    @After
    public void tearDown() {
    }

    private int calcMaxCanBuy(ProductType p, double money) {
        return (int) Math.floor(Math.log(1 - (money / p.getCout()) * (1 - p.getCroissance())) / Math.log(p.getCroissance()));
    }

    private double calcCostBuyxProducts(ProductType p, int qt) {
        return p.getCout() * ((1 - Math.pow(p.getCroissance(), qt)) / (1 - p.getCroissance()));
    }

    private void checkUnlocks(ProductType p) {
        Stream<PallierType> unlocks = p.getPalliers().getPallier().stream().filter(u -> !u.isUnlocked() && u.getSeuil() < p.getQuantite());
        unlocks.forEach(u -> {
            u.setUnlocked(true);
            applyUpgrade(u, p);
        });
    }

    private void applyUpgrade(PallierType u, ProductType p) {
        if (u.getTyperatio() == TyperatioType.VITESSE) {
            p.setVitesse((int) (p.getVitesse() / u.getRatio()));
        } else {
            p.setRevenu(p.getRevenu() * u.getRatio());
        }
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @TestService. For example:
    //
    @Test
    public void checkXMLValidation() {
        System.out.println("Testing : checkXMLValidation");
        // teste si le monde retourn?? au format XML est bien conforme au sch??ma
        given().header("Accept", "application/xml").when().get(serviceUrl + "world").then().assertThat().body(matchesXsd(getSystemResourceAsStream("worldSchema.xsd")));
        System.err.println("");
    }

    @Test
    public void checkJSONlogo() {
        System.out.println("Testing : checkJSONlogo");
        // teste si le monde retourn?? au format JSON contient bien un world.logo qui d??signe une image (jpg, gif, ou png)
        given().header("Accept", "application/json").when().get(serviceUrl + "world").then().assertThat().body("logo", anyOf(endsWith(".jpg"), endsWith(".gif"), endsWith(".png")));
    }

    @Test
    public void checkSixProducts() {
        System.out.println("Testing : checkSixProducts");
        // teste si le monde retourn?? contient bien six produits
        given().header("Accept", "application/json").when().get(serviceUrl + "world").then().assertThat().body("products.product", hasSize(6));
    }

    @Test
    public void checkThreeUnlocksByProducts() {
        System.out.println("Testing : checkThreeUnlocksByProducts");
        // teste si chaque produit contient au moins 3 unlocks
        given().header("Accept", "application/json").when().get(serviceUrl + "world").then().assertThat().body("products.product.findAll { it.palliers.pallier.size()>=3 }", hasSize(6));
    }

    @Test
    public void checkAManagerForEachProduct() {
        System.out.println("Testing : checkAManagerForEachProduct");
        // teste si il y a autant de managers que de produits
        int nproduct = given().header("Accept", "application/json").when().get(serviceUrl + "world").then().extract().path("products.product.size()");
        int nmanager = given().header("Accept", "application/json").when().get(serviceUrl + "world").then().extract().path("managers.pallier.size()");
        assertEquals(nproduct, nmanager);
    }

    @Test
    public void checkAtLeastOneFirstProduct() {
        System.out.println("Testing : checkAtLeastOneFirstProduct");
        // teste si il y a au moins un exemplaire du premier produit
        given().header("Accept", "application/json").header("X-User", uniqueID).when().get(serviceUrl + "world").then().assertThat().body("products.product[0].quantite", equalTo(1));
    }

    @Test
    public void checkLauchProductionFirstProduct() throws InterruptedException {
        System.out.println("Testing : checkLauchProductionFirstProduct");
        // teste la bonne mise en production du premier produit 
        World w = given().header("Accept", "application/json").header("X-User", uniqueID).get(serviceUrl + "world").as(World.class);
        ProductType p = w.getProducts().getProduct().get(0);
        given().contentType("application/json").header("X-User", uniqueID).body(p).put(serviceUrl + "product");
        // on attend un peu plus que le temps de production
        Thread.sleep(p.getVitesse() + 100);
        // on recharge le monde
        World w2 = given().header("Accept", "application/json").header("X-User", uniqueID).get(serviceUrl + "world").as(World.class);
        // on v??rifie qu'on a bien gagn?? l'argent attendu ?? 0.1 de pr??cision pret
        assertEquals(w2.getMoney(), w.getMoney() + p.getRevenu() * p.getQuantite(), 0.1);
    }

    @Test
    public void checkLauchAndBuyProduct() throws InterruptedException {
        System.out.println("Testing : checkLauchAndBuyProduct");
        // teste l'achat du premier produit avec accumulation d'assez d'argent pour l'acheter 
        World w = given().header("Accept", "application/json").header("X-User", uniqueID).get(serviceUrl + "world").as(World.class);
        ProductType p = w.getProducts().getProduct().get(0);
        while (w.getMoney() < p.getCout()) {
            // production d'un exemplaire
            given().contentType("application/json").header("X-User", uniqueID).body(p).put(serviceUrl + "product");
            // on attend un peu plus que le temps de production
            Thread.sleep(p.getVitesse() + 100);
            // on met ?? jour l'argent gagn?? 
            w.setMoney(w.getMoney() + p.getRevenu());
        }
        // on achete un exemplaire du produit
        p.setQuantite(p.getQuantite() + 1);
        given().contentType("application/json").header("X-User", uniqueID).body(p).put(serviceUrl + "product");
        // on recharge le monde
        World w2 = given().header("Accept", "application/json").header("X-User", uniqueID).get(serviceUrl + "world").as(World.class);
        // on v??rifie qu'on a bien d??pens?? l'argent pour achat et qu'on a un produit en plus (donc deux)
        assertEquals(w2.getMoney(), w.getMoney() - p.getCout(), 0.1);
        ProductType p2 = w2.getProducts().getProduct().get(0);
        assertEquals(p2.getQuantite(), 2);
        // on v??rifie aussi que le cout du produit a bien ??t?? mis ?? jour
        assertEquals(p2.getCout(), p.getCout() * p.getCroissance(), 0.1);
    }

    @Test
    public void checkUnlockFirstProduct() throws InterruptedException {
        System.out.println("Testing : checkUnlockFirstProduct");
        // teste l'achat de plusieurs exemplaires du premier produit avec l'activation de l'unlock et sa bonne prise en compte sur les revenus 
        World w = given().header("Accept", "application/json").header("X-User", uniqueID).get(serviceUrl + "world").as(World.class);
        ProductType p = w.getProducts().getProduct().get(0);
        PallierType unlock = p.getPalliers().getPallier().get(0);
        while (p.getQuantite() < unlock.getSeuil()) {
            // production d'un exemplaire
            given().contentType("application/json").header("X-User", uniqueID).body(p).put(serviceUrl + "product");
            // on attend un peu plus que le temps de production
            Thread.sleep(p.getVitesse() + 100);
            // on met ?? jour l'argent gagn?? 
            w.setMoney(w.getMoney() + p.getRevenu() * p.getQuantite());
            // si possible on achete des exemplaire suppl??mentaire
            while (w.getMoney() >= p.getCout()) {
                p.setQuantite(p.getQuantite() + 1);
                given().contentType("application/json").header("X-User", uniqueID).body(p).put(serviceUrl + "product");
                w.setMoney(w.getMoney() - p.getCout());
                p.setCout(p.getCout() * p.getCroissance());
            }
        }
        // si on est l?? c'est que l'unlock doit ??tre d??bloqu??, on v??rifie : 
        World w2 = given().header("Accept", "application/json").header("X-User", uniqueID).get(serviceUrl + "world").as(World.class);
        ProductType p2 = w2.getProducts().getProduct().get(0);
        // on teste la bonne prise en compte du unlock
        assertEquals(p2.getPalliers().getPallier().get(0).isUnlocked(), true);
        if (unlock.getTyperatio() == TyperatioType.VITESSE) {
            p.setVitesse((int) (p.getVitesse() / unlock.getRatio()));
            assertEquals(p2.getVitesse(), p.getVitesse());
        } else {
            p.setRevenu(p.getRevenu() * unlock.getRatio());
            assertEquals(p.getRevenu(), p2.getRevenu(), 0.1);
        }

        // on teste la prochaine production pour voir si le serveur calcule toujours bien
        given().contentType("application/json").header("X-User", uniqueID).body(p).put(serviceUrl + "product");
        // on attend un peu plus que le temps de production
        Thread.sleep(p.getVitesse() + 100);
        // on recharge le monde
        World w3 = given().header("Accept", "application/json").header("X-User", uniqueID).get(serviceUrl + "world").as(World.class);
        // on v??rifie qu'on a bien gagn?? l'argent attendu ?? 0.1 de pr??cision pret
        assertEquals(w3.getMoney(), w.getMoney() + p.getRevenu() * p.getQuantite(), 0.1);
    }

    @Test
    public void checkBuy3Products() throws InterruptedException {
        System.out.println("Testing : checkBuy3Products");
        // teste l'achat de plusieurs exemplaires d'un coup du premier produit 
        World w = given().header("Accept", "application/json").header("X-User", uniqueID).get(serviceUrl + "world").as(World.class);
        ProductType p = w.getProducts().getProduct().get(0);
        PallierType unlock = p.getPalliers().getPallier().get(0);
        while (calcMaxCanBuy(p, w.getMoney()) < 3) {
            // production d'un exemplaire
            given().contentType("application/json").header("X-User", uniqueID).body(p).put(serviceUrl + "product");
            // on attend un peu plus que le temps de production
            Thread.sleep(p.getVitesse() + 100);
            // on met ?? jour l'argent gagn?? 
            w.setMoney(w.getMoney() + p.getRevenu() * p.getQuantite());
        }
        // on achete 3 produits
        p.setQuantite(p.getQuantite() + 3);
        given().contentType("application/json").header("X-User", uniqueID).body(p).put(serviceUrl + "product");
        // on v??rifie : 
        World w2 = given().header("Accept", "application/json").header("X-User", uniqueID).get(serviceUrl + "world").as(World.class);
        ProductType p2 = w2.getProducts().getProduct().get(0);
        // on teste la bonne prise en compte de l'achat
        assertEquals(p2.getQuantite(), 4);
        assertEquals(w2.getMoney(), w.getMoney() - calcCostBuyxProducts(p, 3), 0.1);
    }

    @Test
    public void checkBuyManager() throws InterruptedException {
        System.out.println("Testing : checkBuyManager");
        // teste l'achat de premier manager. Cela suppose d'accumuler assez d'argent pour y parvenir. 
        World w = given().header("Accept", "application/json").header("X-User", uniqueID).get(serviceUrl + "world").as(World.class);
        ProductType p = w.getProducts().getProduct().get(0);
        // on cherche le manager du premier produit et on v??rifie qu'on le trouve
        Optional<PallierType> manager = w.getManagers().getPallier().stream().filter(m -> m.getIdcible() == p.getId()).findFirst();
        assert (manager.isPresent());

        while (w.getMoney() < manager.get().getSeuil()) {
            // production d'un exemplaire
            given().contentType("application/json").header("X-User", uniqueID).body(p).put(serviceUrl + "product");
            // on attend un peu plus que le temps de production
            Thread.sleep(p.getVitesse() + 100);
            // on met ?? jour l'argent gagn?? 
            w.setMoney(w.getMoney() + p.getRevenu() * p.getQuantite());
            // pour optimer, on achete uniquement si on peut doubler la quantit?? d??tenue
            int achatpossible = calcMaxCanBuy(p, w.getMoney());
            if (achatpossible >= p.getQuantite()) {
                int qtchange = p.getQuantite();
                w.setMoney(w.getMoney() - calcCostBuyxProducts(p, qtchange));
                p.setQuantite(p.getQuantite() + qtchange);
                given().contentType("application/json").header("X-User", uniqueID).body(p).put(serviceUrl + "product");
                p.setCout(p.getCout() * Math.pow(p.getCroissance(), qtchange));
                System.out.println("buy " + qtchange);
            }
            // on v??rifie le ou les unlocks ??ventuels
            checkUnlocks(p);
        }
        System.out.println("manager unlock");
        // on achete le manager
        manager.get().setUnlocked(true);
        given().contentType("application/json").header("X-User", uniqueID).body(manager.get()).put(serviceUrl + "manager");
        // a partir de maintenant la production est automatis??e. On m??morise le timestamp.
        long now = System.currentTimeMillis();
        // on v??rifie que le manager est bien d??bloqu?? cot?? serveur : 
        World w2 = given().header("Accept", "application/json").header("X-User", uniqueID).get(serviceUrl + "world").as(World.class);
        ProductType p2 = w2.getProducts().getProduct().get(0);
        manager = w2.getManagers().getPallier().stream().filter(m -> m.getIdcible() == p2.getId()).findFirst();
        assert (manager.get().isUnlocked());
        // on attend de quoi produire au moins 3 produits
        Thread.sleep(p.getVitesse() * 3);
        // et on v??rifie que le serveur a bien calcul??
        World w3 = given().header("Accept", "application/json").header("X-User", uniqueID).get(serviceUrl + "world").as(World.class);
        ProductType p3 = w3.getProducts().getProduct().get(0);
        long elapseTime = System.currentTimeMillis() - now;
        // on calcule combien de produits on du ??tre produit
        int qtProduite = (int) (elapseTime / p3.getVitesse());
        // et on v??rifie que l'argent a bien augment?? comme il faut
        assertEquals(w2.getMoney() + qtProduite * p2.getRevenu() * p2.getQuantite(), w3.getMoney(), 0.1);
    }

    @Test
    public void checkUpgradeFirstProduct() throws InterruptedException {
        System.out.println("Testing : checkUpgradeFirstProduct");
        // teste l'achat du premier upgrade du premier produit et sa bonne prise en compte sur les revenus 
        World w = given().header("Accept", "application/json").header("X-User", uniqueID).get(serviceUrl + "world").as(World.class);
        ProductType p = w.getProducts().getProduct().get(0);
        // on cherche le premier upgrade du produit et on v??rifie qu'il existe
        Optional<PallierType> upgrade = w.getUpgrades().getPallier().stream().filter(m -> m.getIdcible() == p.getId()).findFirst();
        assert (upgrade.isPresent());
        while (w.getMoney() < upgrade.get().getSeuil()) {
            // production d'un exemplaire
            given().contentType("application/json").header("X-User", uniqueID).body(p).put(serviceUrl + "product");
            // on attend un peu plus que le temps de production
            Thread.sleep(p.getVitesse() + 100);
            // on met ?? jour l'argent gagn?? 
            w.setMoney(w.getMoney() + p.getRevenu() * p.getQuantite());
            // pour optimer, on achete uniquement si on peut doubler la quantit?? d??tenue
            int achatpossible = calcMaxCanBuy(p, w.getMoney());
            if (achatpossible >= p.getQuantite()) {
                int qtchange = p.getQuantite();
                w.setMoney(w.getMoney() - calcCostBuyxProducts(p, qtchange));
                p.setQuantite(p.getQuantite() + qtchange);
                given().contentType("application/json").header("X-User", uniqueID).body(p).put(serviceUrl + "product");
                p.setCout(p.getCout() * Math.pow(p.getCroissance(), qtchange));
                System.out.println("buy " + qtchange);
            }
            // on v??rifie le ou les unlocks ??ventuels
            checkUnlocks(p);
        }
        System.out.println("upgrade unlock");
        // on achete l'upgrade
        upgrade.get().setUnlocked(true);
        given().contentType("application/json").header("X-User", uniqueID).body(upgrade.get()).put(serviceUrl + "upgrade");
        // on calcule l'effet de l'upgrade
        applyUpgrade(upgrade.get(), p);
        w.setMoney(w.getMoney() - upgrade.get().getSeuil());

        // on produit 1 produit
        given().contentType("application/json").header("X-User", uniqueID).body(p).put(serviceUrl + "product");
        Thread.sleep(p.getVitesse());
        w.setMoney(w.getMoney() + p.getRevenu() * p.getQuantite());
        // on v??rifie que :
        // l'upgrade est bien d??bloqu?? cot?? serveur : 
        World w2 = given().header("Accept", "application/json").header("X-User", uniqueID).get(serviceUrl + "world").as(World.class);
        ProductType p2 = w2.getProducts().getProduct().get(0);
        upgrade = w2.getUpgrades().getPallier().stream().filter(m -> m.getIdcible() == p2.getId()).findFirst();
        assert (upgrade.get().isUnlocked());
        // et que le score a bien ??volu?? comme il fallait
        assertEquals(w2.getMoney(), w.getMoney(), 0.1);
    }

    @Test
    public void checkDeleteWorld() throws InterruptedException {
        System.out.println("Testing : checkDeleteWorld");
        // teste la bonne remise ?? z??ro du monde avec conservation du score sur un delete 
        World w = given().header("Accept", "application/json").header("X-User", uniqueID).get(serviceUrl + "world").as(World.class);
        ProductType p = w.getProducts().getProduct().get(0);
        // production d'un exemplaire
        given().contentType("application/json").header("X-User", uniqueID).body(p).put(serviceUrl + "product");
        Thread.sleep(p.getVitesse() + 100);
        w.setMoney(w.getMoney() + p.getRevenu() * p.getQuantite());
        // v??rification de la bonne ??volution de l'argent et du score
        World w2 = given().header("Accept", "application/json").header("X-User", uniqueID).get(serviceUrl + "world").as(World.class);
        assertEquals(w.getMoney(), w2.getMoney(), 0.1);
        assertEquals(w2.getScore(), w2.getMoney(), 0.1);
        // effacement du monde 
        given().contentType("application/json").header("X-User", uniqueID).delete(serviceUrl + "world");
        Thread.sleep(p.getVitesse() + 100);
        // v??rification de la bonne remise ?? z??ro de l'argent avec conservation du score
        World w3 = given().header("Accept", "application/json").header("X-User", uniqueID).get(serviceUrl + "world").as(World.class);
        assertEquals(w3.getMoney(), 0, 0.1);
        assertEquals(w3.getScore(), w2.getScore(), 0.1);
    }

}
