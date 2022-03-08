package ISIS.FIE4.MarieJeanEdouard.ISIS_Capitalist.WebService;

import ISIS.FIE4.MarieJeanEdouard.ISIS_Capitalist.tools.AngelUpgradeService;
import generated.PallierType;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

@Path("generic")
public class WebServiceAngelUpgrade {
    private AngelUpgradeService services;

    public WebServiceAngelUpgrade(AngelUpgradeService services) {
        this.services = services;
    }
    
    @PUT
    @Path("upgrade")
    @Consumes({"application/xml","application/json"})
    public void updateAngelUpgrade(@Context HttpServletRequest request,PallierType newAngelUpgrade){
        services.updateAngelUpgrade(newAngelUpgrade);
    }
}
 