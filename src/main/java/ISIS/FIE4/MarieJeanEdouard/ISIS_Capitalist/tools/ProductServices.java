package ISIS.FIE4.MarieJeanEdouard.ISIS_Capitalist.tools;

import generated.PallierType;
import generated.ProductType;
import generated.World;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.xml.bind.JAXBException;

public class ProductServices {

    private WorldServices worldServices;

    public ProductServices() {
        this.worldServices = new WorldServices();
    }

    /**
     * prend en paramètre le pseudo du joueur et le produit sur lequel une
     * action a eu lieu (lancement manuel de production ou achat d’une certaine
     * quantité de produit) renvoie false si l’action n’a pas pu être traitée
     *
     * @param username = pseudo du joueur
     * @param newproduct = produit client
     */
    public Boolean updateProduct(String username, ProductType newproduct) throws JAXBException, FileNotFoundException {
        // aller chercher le monde qui correspond au joueur
        World world = worldServices.getWorld(username);

        // trouver dans ce monde, le produit équivalent à celui passé
        // en paramètre
        ProductType product = findProductById(world, newproduct.getId());
        if (product == null) {
            return false;
        }

        // calculer la variation de quantité. Si elle est positive c'est
        // que le joueur a acheté une certaine quantité de ce produit
        // sinon c’est qu’il s’agit d’un lancement de production.
        int qtchange = newproduct.getQuantite() - product.getQuantite();
        if (qtchange > 0) {
            // soustraire de l'argent du joueur le cout de la quantité
            double cout=0;
            for(int i = product.getQuantite();i< newproduct.getQuantite();i++){
                cout=+ coutDachatDesProduits(product, i);
            }
            
            world.setMoney(world.getMoney() - cout);
            
            
            
            // achetée et mettre à jour la quantité de product
            product.setQuantite(newproduct.getQuantite());
            worldServices.checkUpgradeIsAvailable(world);
            // mise à jour du coût du produit 
            product.setCout(product.getCout() + product.getCroissance() * product.getCout());
            //mise à jour du score 
            world.setScore(world.getScore() + product.getRevenu());
        } else {
            // initialiser product.timeleft à product.vitesse
            // pour lancer la production
            product.setTimeleft(product.getVitesse());
        }
        // sauvegarder les changements du monde
        worldServices.saveWorldToXml(world, username);
        return true;
    }

    public ProductType findProductById(World world, int id) {
        for (ProductType pt : world.getProducts().getProduct()) {
            if (id == pt.getId()) {
                return pt;
            }
        }
        return null;
    }
    
    public double coutDachatDesProduits(ProductType product,int qte){
        return product.getCout()*Math.pow(product.getCroissance(),qte-1);
    }
    

    
    
    

}
