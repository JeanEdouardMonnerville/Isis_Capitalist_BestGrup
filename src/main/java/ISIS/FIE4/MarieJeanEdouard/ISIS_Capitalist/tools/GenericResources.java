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
                    world.setScore(world.getScore() + calculRevenu(product, quantiteProduit, world));
                    world.setMoney(world.getMoney() + calculRevenu(product, quantiteProduit, world));

                } else {
                    if (product.getTimeleft() != 0 && product.getTimeleft() < deltaTime) {
                        //Mise à jour des gains gagnés pendant l'inaction
                        world.setScore(world.getScore() + calculRevenu(product, 1, world));
                        world.setMoney(world.getMoney() + calculRevenu(product, 1, world));
                        product.setTimeleft(0);
                    } else if (product.getQuantite() != 0 && product.getTimeleft() != 0 && product.getTimeleft() > deltaTime) {
                        product.setTimeleft(product.getTimeleft() - deltaTime);
                    }
                }
            }
        }
    }

    private double calculRevenu(ProductType product, int qte, World world) {
        double result = 0;
        result = product.getRevenu() * qte * product.getQuantite();

        //Ajout de l'impact des anges 
        if (world.getActiveangels() >= 1) {
            result = result * (1 + world.getActiveangels() * world.getAngelbonus() / 100);
        }
        return result;
    }
}
