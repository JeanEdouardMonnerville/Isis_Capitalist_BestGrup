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

    //Indication d'un upgrade atteint pour un produit
    public boolean updateUpgrade(PallierType newUpgrade, String username) throws JAXBException, FileNotFoundException {
        World world = worldServices.getWorld(username);
        ProductType product = productServices.findProductById(world, newUpgrade.getIdcible());
        if (product == null) {
            return false;
        }
        PallierType upgrade = findUpgradeByName(newUpgrade.getName(), product);
        if (upgrade == null) {
            return false;
        }

        if (product.getQuantite() > upgrade.getSeuil()) {
            switch (upgrade.getTyperatio()) {
                case GAIN:
                    applyUpgradeGain(upgrade, product);
                    break;
                case VITESSE:
                    applyUpgradeVitesse(upgrade, product);
                    break;
                case ANGE:
                    applyAngeUpgrade(upgrade, world);
                    break;
            }
        }
        worldServices.saveWorldToXml(world, username);
        return true;
    }

    public void applyUpgradeGain(PallierType upgrade, ProductType product) {
        product.setRevenu(product.getRevenu() * upgrade.getRatio());
        upgrade.setUnlocked(true);
    }

    public void applyUpgradeVitesse(PallierType upgrade, ProductType product) {
        product.setRevenu(product.getTimeleft() * upgrade.getRatio());
        upgrade.setUnlocked(true);
    }

    private void applyAngeUpgrade(PallierType upgrade, World world) {
        world.setAngelbonus((int) (world.getAngelbonus() + upgrade.getRatio()));
        upgrade.setUnlocked(true);
    }

    private PallierType findUpgradeByName(String name, ProductType product) {
        PallierType result = null;
        for (PallierType pt : product.getPalliers().getPallier()) {
            if (pt.getName().equals(name)) {
                result = pt;
            }
        }
        return result;
    }
}
