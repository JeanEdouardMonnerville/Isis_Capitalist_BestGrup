package ISIS.FIE4.MarieJeanEdouard.ISIS_Capitalist.tools;

import generated.PallierType;
import generated.ProductType;
import generated.World;

public class ApplyUpgrade {

    public ApplyUpgrade() {
    }
    
    public void applyUpgradeForAllGain(PallierType upgrade, World world) {
        for (ProductType product : world.getProducts().getProduct()) {
            product.setRevenu(product.getRevenu() * upgrade.getRatio());
            //On indique qu'il est déblocké
            upgrade.setUnlocked(true);
        }
    }

    public void applyUpgradeForAllVitesse(PallierType upgrade, World world) {
        for (ProductType product : world.getProducts().getProduct()) {
            product.setRevenu(product.getVitesse() / upgrade.getRatio());
            //On indique qu'il est déblocké
            upgrade.setUnlocked(true);
        }
    }

    public void applyAngeUpgrade(PallierType upgrade, World world) {
        world.setAngelbonus((int) (world.getAngelbonus() + upgrade.getRatio()));
        //On indique qu'il est déblocké
        upgrade.setUnlocked(true);
    }

    public void applyUpgradeGain(PallierType upgrade, ProductType product) {
        product.setRevenu(product.getRevenu() * upgrade.getRatio());
        upgrade.setUnlocked(true);
    }

    public void applyUpgradeVitesse(PallierType upgrade, ProductType product) {
        product.setVitesse((int) (product.getVitesse() / upgrade.getRatio()));
        upgrade.setUnlocked(true);
    }


}
