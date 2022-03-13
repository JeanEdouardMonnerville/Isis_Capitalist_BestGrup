package ISIS.FIE4.MarieJeanEdouard.ISIS_Capitalist.tools;

import generated.PallierType;
import generated.ProductType;
import generated.World;
import java.io.FileNotFoundException;
import javax.xml.bind.JAXBException;

public class AngelUpgradeService {

    private WorldServices worldServices;

    public AngelUpgradeService() {
        worldServices = new WorldServices();
    }

    public boolean updateAngelUpgrade(PallierType newAngelUpgrade, String username) throws JAXBException, FileNotFoundException {
        World world = worldServices.getWorld(username);
        PallierType angelUpgrade = findAngelByName(username, world);
        if (angelUpgrade == null) {
            return false;
        }

        if (world.getActiveangels() > angelUpgrade.getSeuil() && angelUpgrade.isUnlocked()==false) {
            //On enregistre les anges gagné
            world.setTotalangels(world.getTotalangels()+world.getActiveangels());
            //On soustrait le coût de l'ange
            world.setTotalangels(world.getActiveangels() - angelUpgrade.getSeuil());
            //On indique qu'il est déblocké
            angelUpgrade.setUnlocked(true);
            
            //On applique son bonus
            switch(angelUpgrade.getTyperatio()){
                case GAIN:
                    applyAngelUpgradeForAllGain(angelUpgrade, world);
                    break;
                case VITESSE:
                    applyAngelUpgradeForAllVitesse(angelUpgrade, world);
                    break;
                case ANGE:
                    applyAngeUpgrade(newAngelUpgrade, world);
                    break;
            }
        }

        worldServices.saveWorldToXml(world, username);
        return true;
    }

    private PallierType findAngelByName(String name, World world) {
        PallierType result = null;
        for (PallierType pt : world.getAngelupgrades().getPallier()) {
            if (pt.getName().equals(name)) {
                result = pt;
            }
        }
        return result;
    }

    public void applyAngelUpgradeForAllGain(PallierType ange, World world) {
        for (ProductType product : world.getProducts().getProduct()) {
            product.setRevenu(product.getRevenu() * ange.getRatio());
        }
    }

    public void applyAngelUpgradeForAllVitesse(PallierType ange, World world) {
        for (ProductType product : world.getProducts().getProduct()) {
            product.setRevenu(product.getVitesse()/ ange.getRatio());
        }
    }

    private void applyAngeUpgrade(PallierType ange, World world) {
        world.setAngelbonus((int) (world.getAngelbonus() + ange.getRatio()));
    }

}
