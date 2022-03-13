package ISIS.FIE4.MarieJeanEdouard.ISIS_Capitalist.tools;

import generated.PallierType;
import generated.ProductType;
import generated.ProductsType;
import generated.World;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class WorldServices {

    private String filePath = "src/main/resources/";
    private GenericResources tool;

    public WorldServices() {
        tool = new GenericResources();
    }

    public World readWorldFromXml(String pseudo) throws JAXBException, FileNotFoundException {

        World world = null;
        try {
            JAXBContext cont = JAXBContext.newInstance(World.class);
            Unmarshaller u = cont.createUnmarshaller();
            File file = new File(filePath + pseudo + "-world.xml");
            if (file.exists()) {
                world = (World) u.unmarshal(file);
            } else {
                InputStream input = getClass().getClassLoader().getResourceAsStream("world.xml");
                world = (World) u.unmarshal(input);
                assert input != null;
                input.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        tool.updateScore(world);
        checkUpgradeIsAvailable(world);
        calculNbAngeActif(world);
        return world;
    }

    public void saveWorldToXml(World world, String pseudo) {
        try {
            JAXBContext cont = JAXBContext.newInstance(World.class
            );
            Marshaller m = cont.createMarshaller();
            File file = new File(filePath + pseudo + "-world.xml");

            if (!file.exists()) {
                OutputStream output = new FileOutputStream(filePath + pseudo + "-world.xml");
                m.marshal(world, output);
                output.close();
            } else {
                OutputStream output = new FileOutputStream(filePath + pseudo + "-world.xml");
                m.marshal(world, output);

                output.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public World getWorld(String pseudo) throws JAXBException, FileNotFoundException {
        return readWorldFromXml(pseudo);
    }

    public void deleteworld(String pseudo) {
        try {
            //Récupération des données du monde initial
            JAXBContext cont = JAXBContext.newInstance(World.class
            );
            InputStream input = getClass().getClassLoader().getResourceAsStream("World.xml");
            Unmarshaller unmarshaller = cont.createUnmarshaller();
            World NewWorld = (World) unmarshaller.unmarshal(input);

            //Récupération des données du monde actuel
            InputStream input2 = getClass().getClassLoader().getResourceAsStream(pseudo + "-world.xml");
            Unmarshaller unmarshaller2 = cont.createUnmarshaller();
            World OldWorld = (World) unmarshaller.unmarshal(input2);

            //On garde les anges et le score
            NewWorld.setScore(OldWorld.getScore());
            NewWorld.setActiveangels(OldWorld.getActiveangels());
            NewWorld.setTotalangels(OldWorld.getTotalangels());

            saveWorldToXml(NewWorld, pseudo);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Fonction qui vérifie que le seuil pour débloquer un upgrade global est atteint et l'applique 
    public void checkUpgradeIsAvailable(World world) {
        ProductsType products = world.getProducts();
        int minQte = products.getProduct().get(0).getQuantite();
        for (int i = 1; i < products.getProduct().size(); i++) {
            if (products.getProduct().get(i).getQuantite() < minQte) {
                minQte = products.getProduct().get(i).getQuantite();
            }
        }
        for (PallierType upgrade : world.getAllunlocks().getPallier()) {
            if (minQte > upgrade.getSeuil() && upgrade.isUnlocked() == false) {
                switch (upgrade.getTyperatio()) {
                    case GAIN:
                        applyUpgradeForAllGain(upgrade, world);
                        break;
                    case VITESSE:
                        applyUpgradeForAllVitesse(upgrade, world);
                        break;
                    case ANGE:
                        applyAngeUpgrade(upgrade, world);
                        break;
                }
            }
        }
    }

    public void applyUpgradeForAllGain(PallierType upgrade, World world) {
        for (ProductType product : world.getProducts().getProduct()) {
            product.setRevenu(product.getRevenu() * upgrade.getRatio());
        }
        upgrade.setUnlocked(true);
    }

    public void applyUpgradeForAllVitesse(PallierType upgrade, World world) {
        for (ProductType product : world.getProducts().getProduct()) {
            product.setVitesse((int) (product.getVitesse() / upgrade.getRatio()));
        }
        upgrade.setUnlocked(true);
    }

    private void applyAngeUpgrade(PallierType upgrade, World world) {
        world.setAngelbonus((int) (world.getAngelbonus() + upgrade.getRatio()));
        upgrade.setUnlocked(true);
    }

    //angeActif = 150 x rac(score/10^15)-nombre total ange
    private double calculNbAngeActif(World world) {
        double result = 150;
        result = result * Math.pow((world.getScore() / Math.pow(10, 15)), 0.5);
        result = result - world.getTotalangels();
        return result;
    }

}
