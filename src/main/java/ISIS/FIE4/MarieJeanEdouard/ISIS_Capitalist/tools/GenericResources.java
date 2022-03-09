package ISIS.FIE4.MarieJeanEdouard.ISIS_Capitalist.tools;

import com.google.gson.Gson;
import generated.PallierType;
import generated.ProductType;

public class GenericResources {

    public ProductType convertJSONToProductType(String json) {
        return new Gson().fromJson(json, ProductType.class);
    }

    public PallierType convertJSONToPallierType(String json) {
        return new Gson().fromJson(json, PallierType.class);
    }
}
