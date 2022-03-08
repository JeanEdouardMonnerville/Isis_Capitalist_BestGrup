
package ISIS.FIE4.MarieJeanEdouard.ISIS_Capitalist.WebService;

import ISIS.FIE4.MarieJeanEdouard.ISIS_Capitalist.tools.UpradeServices;
import generated.PallierType;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

@Path("generic")
public class WebServiceUpgrade {
    
    private UpradeServices services;

    public WebServiceUpgrade(UpradeServices services) {
        this.services = services;
    }
    

    @PUT
    @Path("upgrade")
    @Consumes({"application/xml","application/json"})
    public void updateUpgrade(@Context HttpServletRequest request,PallierType newUpgrade){
        services.updateUpgrade(newUpgrade);
    }
}
