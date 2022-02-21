package ISIS.FIE4.MarieJeanEdouard.ISIS_Capitalist.WebService;

import ISIS.FIE4.MarieJeanEdouard.ISIS_Capitalist.tools.ManagerServices;
import generated.PallierType;
import java.io.FileNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.xml.bind.JAXBException;

@Path("generic")
public class WebServiceManager {
    
    ManagerServices services;

    public WebServiceManager() {
        services = new ManagerServices();
    }
    
    @PUT
    @Path("manager")
    @Consumes({"application/xml","application/json"})
    public void updateManager(@Context HttpServletRequest request,PallierType newmanager) throws JAXBException, FileNotFoundException{
        String username = request.getHeader("X-user");
        services.updateManager(username, newmanager);
    }
        
}
