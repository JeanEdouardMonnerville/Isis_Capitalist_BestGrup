package ISIS.FIE4.MarieJeanEdouard.ISIS_Capitalist.tools;

import generated.PallierType;
import generated.ProductType;
import generated.World;
import java.io.FileNotFoundException;
import javax.xml.bind.JAXBException;

public class AngelUpgradeService {

    private WorldServices worldServices;
    private ApplyUpgrade applyUpgrade;
    private ProductServices productServices;

    public AngelUpgradeService() {
        worldServices = new WorldServices();
        applyUpgrade = new ApplyUpgrade();
        productServices = new ProductServices();
    }

    public boolean updateAngelUpgrade(PallierType newAngelUpgrade, String username) throws JAXBException, FileNotFoundException {
        World world = worldServices.getWorld(username);
        PallierType angelUpgrade = findAngelByName(newAngelUpgrade.getName(), world);
        if (angelUpgrade == null) {
            return false;
        }

        if (world.getActiveangels() > angelUpgrade.getSeuil() && angelUpgrade.isUnlocked() == false) {
            //On enregistre les anges gagné
            world.setTotalangels(world.getTotalangels() + world.getActiveangels());
            //On soustrait le coût de l'ange
            world.setActiveangels(world.getActiveangels() - angelUpgrade.getSeuil());
            //On applique son bonus
            switch (angelUpgrade.getTyperatio()) {
                case GAIN:
                    applyUpgrade.applyUpgradeForAllGain(angelUpgrade, world);
                    break;
                case VITESSE:
                    applyUpgrade.applyUpgradeForAllVitesse(angelUpgrade, world);
                    break;
                case ANGE:
                    applyUpgrade.applyAngeUpgrade(angelUpgrade, world);
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

}
