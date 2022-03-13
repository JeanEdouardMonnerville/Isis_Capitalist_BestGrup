/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ISIS.FIE4.MarieJeanEdouard.ISIS_Capitalist.tools;

import generated.PallierType;
import generated.ProductType;
import generated.ProductsType;
import static generated.TyperatioType.*;
import generated.World;
import java.io.FileNotFoundException;
import javax.xml.bind.JAXBException;

public class UpgradeServices {

    private ProductServices productServices;
    private WorldServices worldServices;
    private ApplyUpgrade applyUpgrade;

    public UpgradeServices() {
        worldServices = new WorldServices();
        productServices = new ProductServices();
        applyUpgrade = new ApplyUpgrade();
    }

    //Achat d'un cash upgrade
    public boolean updateUpgrade(PallierType newUpgrade, String username) throws JAXBException, FileNotFoundException {
        World world = worldServices.getWorld(username);

        PallierType upgrade = findUpgradeByName(newUpgrade.getName(), world);
        if (upgrade == null) {
            return false;
        }

        if (world.getMoney() >= upgrade.getSeuil() && upgrade.isUnlocked() == false) {
            if (newUpgrade.getIdcible() == 0) {
                switch (upgrade.getTyperatio()) {
                    case GAIN:
                        applyUpgrade.applyUpgradeForAllGain(upgrade, world);
                        break;
                    case VITESSE:
                        applyUpgrade.applyUpgradeForAllVitesse(upgrade, world);
                        break;
                    case ANGE:
                        applyUpgrade.applyAngeUpgrade(upgrade, world);
                        break;
                }
            } else {
                ProductType product = productServices.findProductById(world, newUpgrade.getIdcible());
                if (product == null) {
                    return false;
                }
                //On applique son bonus
                switch (upgrade.getTyperatio()) {
                    case GAIN:
                        applyUpgrade.applyUpgradeGain(upgrade, product);
                        break;
                    case VITESSE:
                        applyUpgrade.applyUpgradeVitesse(upgrade, product);
                        break;
                }
            }

        }
        worldServices.saveWorldToXml(world, username);
        return true;
    }

    private PallierType findUpgradeByName(String name, World world) {
        PallierType result = null;
        for (PallierType pt : world.getUpgrades().getPallier()) {
            if (pt.getName().equals(name)) {
                result = pt;
            }
        }
        return result;
    }
}
