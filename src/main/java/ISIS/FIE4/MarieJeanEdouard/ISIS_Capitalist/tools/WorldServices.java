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
    private ApplyUpgrade applyUpgrade;
    public WorldServices() {
        tool = new GenericResources();
        applyUpgrade=new ApplyUpgrade();
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
            //R??cup??ration des donn??es du monde initial
            JAXBContext cont = JAXBContext.newInstance(World.class
            );
            InputStream input = getClass().getClassLoader().getResourceAsStream("World.xml");
            Unmarshaller unmarshaller = cont.createUnmarshaller();
            World NewWorld = (World) unmarshaller.unmarshal(input);

            
            //R??cup??ration des donn??es du monde actuel
            InputStream input2 = getClass().getClassLoader().getResourceAsStream(pseudo + "-world.xml");
            Unmarshaller unmarshaller2 = cont.createUnmarshaller();
            World OldWorld = (World) unmarshaller.unmarshal(input2);
            
            OldWorld.setActiveangels(calculNbAngeActif(OldWorld));
            
            //On garde les anges et le score
            NewWorld.setScore(OldWorld.getScore());
            NewWorld.setActiveangels(OldWorld.getActiveangels());
            NewWorld.setTotalangels(OldWorld.getTotalangels());

            saveWorldToXml(NewWorld, pseudo);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Fonction qui v??rifie que le seuil pour d??bloquer un upgrade global est atteint et l'applique 
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
                        applyUpgrade.applyUpgradeForAllGain(upgrade, world);
                        break;
                    case VITESSE:
                        applyUpgrade.applyUpgradeForAllVitesse(upgrade, world);
                        break;
                }
            }
        }
    }



    //angeActif = 150 x rac(score/10^15)-nombre total ange
    private double calculNbAngeActif(World world) {
        double result = 150;
        result = result * Math.pow((world.getScore() / Math.pow(10, 15)), 0.5);
        result = result - world.getTotalangels();
        return result;
    }

}
