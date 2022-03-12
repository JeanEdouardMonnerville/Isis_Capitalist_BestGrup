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
                    //Calcul de la quantite de production
                    int quantiteProduit = (int) (deltaTime / product.getVitesse());

                    //Mise à jour des gains gagnés pendant l'inaction
                    world.setScore(world.getScore() + calculRevenu(product, quantiteProduit,world));
                    world.setMoney(world.getMoney() + calculRevenu(product, quantiteProduit,world));
                    //On calcule le temps de production effectué + le reste de 
                    //la division euclidienne correspondant au temps qui ne 
                    //permet pas de produire un nouveau produit.
                    long majTimeleft = (product.getVitesse() - product.getTimeleft())
                            + (deltaTime % product.getVitesse());

                    if (majTimeleft > product.getVitesse()) {

                        //Mise à jour des gains gagnés pendant l'inaction
                        world.setScore(world.getScore() + calculRevenu(product, 1,world));
                        world.setMoney(world.getMoney() + calculRevenu(product, 1,world));
                        //Mise à jour time left
                        product.setTimeleft(product.getVitesse() - majTimeleft);
                    } else {
                        product.setTimeleft(majTimeleft);
                    }
                } else {
                    if (product.getTimeleft() != 0 & product.getTimeleft() < 0) {
                        //Mise à jour des gains gagnés pendant l'inaction
                        world.setScore(world.getScore() + calculRevenu(product, 1,world));
                        world.setMoney(world.getMoney() + calculRevenu(product, 1,world));
                    } else {
                        product.setTimeleft(product.getTimeleft() - deltaTime);
                    }
                }
            }
        }

    }

    private double calculRevenu(ProductType product, int qte,World world) {
        double result = 0;
        result = product.getRevenu() * qte;

        //Ajout de l'impact des anges 
        if (world.getActiveangels() >= 1) {
            result = result * (1 + world.getActiveangels() * world.getAngelbonus() / 100);
        }
        return result;
    }
}
