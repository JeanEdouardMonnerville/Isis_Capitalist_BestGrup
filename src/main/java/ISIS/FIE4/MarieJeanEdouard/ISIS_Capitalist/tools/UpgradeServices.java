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
import java.io.IOException;
import javax.xml.bind.JAXBException;

public class UpgradeServices {

   
    ProductServices productServices;
    WorldServices worldServices;

    public UpgradeServices() {
        worldServices=new WorldServices();
        productServices = new ProductServices();
    }

    //Demande d'achat d'un upgrade singulié pour un produit 
    public boolean updateUpgrade(PallierType newUpgrade,String username) throws JAXBException, FileNotFoundException {
        World world = worldServices.getWorld(username);
        ProductType product = productServices.findProductById(world, newUpgrade.getIdcible());
        if (product == null) {
            return false;
        }
        if (world.getMoney() > newUpgrade.getSeuil()) {
            world.setMoney(world.getMoney() - newUpgrade.getSeuil());
            switch (newUpgrade.getTyperatio()) {
                case GAIN:
                    applyUpgradeGain(newUpgrade, product);
                    break;
                case VITESSE:
                    applyUpgradeVitesse(newUpgrade, product);
                    break;
            }
        }
        worldServices.saveWorldToXml(world, username);
        return true;
    }

  

    public void applyUpgradeGain(PallierType upgrade, ProductType product) {
        product.setRevenu(product.getRevenu() * upgrade.getRatio());
    }

    public void applyUpgradeVitesse(PallierType upgrade, ProductType product) {
        product.setRevenu(product.getTimeleft() * upgrade.getRatio());
    }

}
