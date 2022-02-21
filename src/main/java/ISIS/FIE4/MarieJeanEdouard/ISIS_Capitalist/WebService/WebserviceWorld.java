package ISIS.FIE4.MarieJeanEdouard.ISIS_Capitalist.WebService;
import ISIS.FIE4.MarieJeanEdouard.ISIS_Capitalist.tools.WorldServices;
import generated.World;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Path("generic")
public class WebserviceWorld {
    
    WorldServices services;
    
    public WebserviceWorld(){
        services= new WorldServices();
    }
    
    @GET
    @Path("world") 
    @Produces({"application/xml","application/json"})
    public World getWorld(@Context HttpServletRequest request){
        String username = request.getHeader("X-user");
        return services.getWorld(username);
    }
    
}
