package ISIS.FIE4.MarieJeanEdouard.ISIS_Capitalist.config;


import ISIS.FIE4.MarieJeanEdouard.ISIS_Capitalist.WebService.WebServiceManager;
import ISIS.FIE4.MarieJeanEdouard.ISIS_Capitalist.WebService.WebServiceProduct;
import ISIS.FIE4.MarieJeanEdouard.ISIS_Capitalist.WebService.WebserviceWorld;
import ISIS.FIE4.MarieJeanEdouard.ISIS_Capitalist.security.CORSResponseFilter;
import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

@Component
@ApplicationPath("/adventureisis")
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(WebserviceWorld.class);
        register(WebServiceProduct.class);
        register(WebServiceManager.class);
        register(CORSResponseFilter.class);
    }

}
