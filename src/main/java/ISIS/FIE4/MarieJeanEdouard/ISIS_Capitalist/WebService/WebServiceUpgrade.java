
package ISIS.FIE4.MarieJeanEdouard.ISIS_Capitalist.WebService;

import ISIS.FIE4.MarieJeanEdouard.ISIS_Capitalist.tools.UpgradeServices;
import ISIS.FIE4.MarieJeanEdouard.ISIS_Capitalist.tools.WorldServices;
import generated.PallierType;
import java.io.FileNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.xml.bind.JAXBException;
import org.springframework.web.bind.annotation.RequestBody;

@Path("generic")
public class WebServiceUpgrade {
    
    private UpgradeServices services;

    public WebServiceUpgrade() {
        this.services = new UpgradeServices();
    }

    

    @PUT
    @Path("upgrade")
    @Consumes({"application/xml","application/json"})
    public void updateUpgrade(@Context HttpServletRequest request,@RequestBody PallierType newUpgrade) throws JAXBException, FileNotFoundException{
        String username= request.getHeader("X-user");
        services.updateUpgrade(newUpgrade,username);
    }
}
