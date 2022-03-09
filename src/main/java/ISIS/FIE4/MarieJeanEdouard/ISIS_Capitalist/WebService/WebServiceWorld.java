
package ISIS.FIE4.MarieJeanEdouard.ISIS_Capitalist.WebService;

import ISIS.FIE4.MarieJeanEdouard.ISIS_Capitalist.tools.WorldServices;
import generated.World;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

public class WebServiceWorld {

    WorldServices services;

    public WebServiceWorld() {
        services = new WorldServices();
    }

    @GET
    @Path("world")
    @Produces({"application/xml", "application/json"})
    public World getWorld(@Context HttpServletRequest request) {
        //String username = request.getHeader("X-user");
        //return services.getWorld(username);
        return services.getWorld();

    }

    @DELETE
    @Path("world")
    @Consumes({"application/xml", "application/json"})
    public void deleteWorld(@Context HttpServletRequest request) {
        services.deleteworld();
    }
}
