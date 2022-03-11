package ISIS.FIE4.MarieJeanEdouard.ISIS_Capitalist.tools;

import com.google.gson.Gson;
import generated.PallierType;
import generated.ProductType;
import generated.World;

public class GenericResources {

    public GenericResources() {
    }

    public ProductType convertJSONToProductType(String json) {
        return new Gson().fromJson(json, ProductType.class);
    }

    public PallierType convertJSONToPallierType(String json) {
        return new Gson().fromJson(json, PallierType.class);
    }
    
    //Fonction excécuter dans WorldServices.readWorldFromXml()
    public void updateScore(World world) {
        //temps écoulé depuis la dernière connexion
        long deltaTime = System.currentTimeMillis() - world.getLastupdate();
        //réinitialisation de last Update
        world.setLastupdate(System.currentTimeMillis());
        if (deltaTime > 0) {
            for (ProductType product : world.getProducts().getProduct()) {

                if (product.isManagerUnlocked()) {
                    //Calcul du quotient 
                    int quantiteProduit = (int) (deltaTime / product.getVitesse());
                    //Mise à jour de la quantite du produit 
                    product.setQuantite(product.getQuantite() + quantiteProduit);
                    //Mise à jour des gains gagnés pendant l'inaction
                    world.setScore(world.getScore() + product.getRevenu() * quantiteProduit);
                    
                    product.setCout(product.getCout() + product.getCroissance() * product.getCout()*quantiteProduit);
                    
                    //On calcule le temps de production effectué + le reste de 
                    //la division euclidienne correspondant au temps qui ne 
                    //permet pas de produire un nouveau produit.
                    long majTimeleft = (product.getVitesse() - product.getTimeleft())
                            + (deltaTime % product.getVitesse());
                    
                    if (majTimeleft > product.getVitesse()) {
                        //Mise à jour de la quantite du produit 
                        product.setQuantite(product.getQuantite() + 1);
                        //Mise à jour des gains gagnés pendant l'inaction
                        world.setScore(world.getScore() + product.getRevenu());
                        //Mise à jour time left
                        product.setTimeleft(product.getVitesse()-majTimeleft);
                        product.setCout(product.getCout() + product.getCroissance() * product.getCout());
                    }else{
                        product.setTimeleft(majTimeleft);
                    }
                } else {
                    if (product.getTimeleft() != 0 & product.getTimeleft() < 0) {
                        //Mise à jour de la quantite du produit 
                        product.setQuantite(product.getQuantite() + 1);
                        product.setCout(product.getCout() + product.getCroissance() * product.getCout());
                        //Mise à jour des gains gagnés pendant l'inaction
                        world.setScore(world.getScore() + product.getRevenu());
                    } else {
                        product.setTimeleft(product.getTimeleft() - deltaTime);
                    }
                }
            }
        }
    }
}
