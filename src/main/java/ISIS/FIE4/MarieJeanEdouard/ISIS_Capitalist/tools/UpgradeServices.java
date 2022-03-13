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

    ProductServices productServices;
    WorldServices worldServices;

    public UpgradeServices() {
        worldServices = new WorldServices();
        productServices = new ProductServices();
    }

    //Achat d'un cash upgrade
    public boolean updateUpgrade(PallierType newUpgrade, String username) throws JAXBException, FileNotFoundException {
        World world = worldServices.getWorld(username);
        ProductType product = productServices.findProductById(world, newUpgrade.getIdcible());

        PallierType upgrade = findUpgradeByName(newUpgrade.getName(), world);
        if (upgrade == null) {
            return false;
        }

        if (world.getMoney() > upgrade.getSeuil() && upgrade.isUnlocked()==false) {
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
        worldServices.saveWorldToXml(world, username);
        return true;
    }

    public void applyUpgradeForAllGain(PallierType upgrade, World world) {
        for (ProductType product : world.getProducts().getProduct()) {
            product.setRevenu(product.getRevenu() * upgrade.getRatio());
        }
    }

    public void applyUpgradeForAllVitesse(PallierType upgrade, World world) {
        for (ProductType product : world.getProducts().getProduct()) {
            product.setVitesse((int) (product.getVitesse()/ upgrade.getRatio()));
        }
    }

    private void applyAngeUpgrade(PallierType upgrade, World world) {
        world.setAngelbonus((int) (world.getAngelbonus() + upgrade.getRatio()));
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
