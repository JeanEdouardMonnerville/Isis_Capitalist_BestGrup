package ISIS.FIE4.MarieJeanEdouard.ISIS_Capitalist.tools;

import generated.PallierType;
import generated.ProductType;
import generated.World;
import java.io.FileNotFoundException;
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
    }

    // prend en paramètre le pseudo du joueur et le manager acheté.
// renvoie false si l’action n’a pas pu être traitée 
    public Boolean updateManager(String username, PallierType newmanager) throws JAXBException, FileNotFoundException {
        // aller chercher le monde qui correspond au joueur
//        World world = worldServices.getWorld(username);
        World world = worldServices.getWorld();

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
        // débloquer le manager de ce produit
        manager.setUnlocked(true);
        // soustraire de l'argent du joueur le cout du manager
        world.setMoney(world.getMoney() - manager.getSeuil());//TBD
        // sauvegarder les changements au monde
        worldServices.saveWorldToXml(world, username);
        return true;
    }

    private PallierType findManagerByName(World world, String name) {
        for (PallierType pt : world.getManagers().getPallier()) {
            if (name == pt.getName()) {
                return pt;
            }
        }
        return null;
    }
}
