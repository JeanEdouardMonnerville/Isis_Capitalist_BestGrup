package ISIS.FIE4.MarieJeanEdouard.ISIS_Capitalist.tools;

import generated.PallierType;
import generated.ProductType;
import generated.World;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.xml.bind.JAXBException;

public class ManagerServices {

    private WorldServices worldServices;
    private ProductServices productService;

    // prend en paramètre le pseudo du joueur et le produit
    // sur lequel une action a eu lieu (lancement manuel de production ou 
    // achat d’une certaine quantité de produit)
    // renvoie false si l’action n’a pas pu être traitée 
    public ManagerServices() {
        this.worldServices = new WorldServices();
        this.productService = new ProductServices();
    }

    // prend en paramètre le pseudo du joueur et le manager acheté.
    // renvoie false si l’action n’a pas pu être traitée 
    public Boolean updateManager(String username, PallierType newmanager) throws JAXBException, FileNotFoundException {
        // aller chercher le monde qui correspond au joueur
        World world = worldServices.getWorld(username);

        // trouver dans ce monde, le manager équivalent à celui passé
        // en paramètre
        PallierType manager = findManagerByName(world, newmanager.getName());
        if (manager == null) {
            return false;
        }

        // débloquer ce manager
        // trouver le produit correspondant au manager
        ProductType product = productService.findProductById(world, manager.getIdcible());
        if (product == null) {
            return false;
        }

        if (world.getMoney() >= manager.getSeuil()) {
            // soustraire de l'argent du joueur le cout du manager
            world.setMoney(world.getMoney() - manager.getSeuil());
            // débloquer le manager de ce produit
            manager.setUnlocked(true);
            product.setManagerUnlocked(true);
        }else{
            return false;
        }

        // sauvegarder les changements au monde
        worldServices.saveWorldToXml(world, username);
        return true;
    }

    private PallierType findManagerByName(World world, String name) {
        for (PallierType pt : world.getManagers().getPallier()) {
            if (name.equals(pt.getName())) {
                return pt;
            }
        }
        return null;
    }
}
