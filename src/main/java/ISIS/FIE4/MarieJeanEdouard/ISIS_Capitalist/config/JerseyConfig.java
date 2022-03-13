package ISIS.FIE4.MarieJeanEdouard.ISIS_Capitalist.config;


import ISIS.FIE4.MarieJeanEdouard.ISIS_Capitalist.WebService.WebServiceAngelUpgrade;
import ISIS.FIE4.MarieJeanEdouard.ISIS_Capitalist.WebService.WebServiceManager;
import ISIS.FIE4.MarieJeanEdouard.ISIS_Capitalist.WebService.WebServiceProduct;
import ISIS.FIE4.MarieJeanEdouard.ISIS_Capitalist.WebService.WebServiceUpgrade;
import ISIS.FIE4.MarieJeanEdouard.ISIS_Capitalist.WebService.WebServiceWorld;
import ISIS.FIE4.MarieJeanEdouard.ISIS_Capitalist.security.CORSResponseFilter;
import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

@Component
@ApplicationPath("/adventureisis")
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(WebServiceWorld.class);
        register(CORSResponseFilter.class);
        register(WebServiceProduct.class);
        register(WebServiceManager.class);
        register(WebServiceUpgrade.class);
        register(WebServiceAngelUpgrade.class);
        
    }

}
