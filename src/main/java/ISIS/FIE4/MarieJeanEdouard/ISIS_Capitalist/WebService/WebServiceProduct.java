package ISIS.FIE4.MarieJeanEdouard.ISIS_Capitalist.WebService;

import ISIS.FIE4.MarieJeanEdouard.ISIS_Capitalist.tools.ProductServices;
import generated.ProductType;
import generated.World;
import java.io.FileNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.xml.bind.JAXBException;

@Path("generic")
public class WebServiceProduct {
    
    ProductServices services;

    public WebServiceProduct() {
        services=new ProductServices();
    }
    

    @Path("product")
    @PUT
    @Consumes({"application/xml","application/json"})
    public void updtateProduct(@Context HttpServletRequest request,ProductType newProduct) throws JAXBException, FileNotFoundException{
        String username = request.getHeader("X-user");
        services.updateProduct(username, newProduct);
    }
}
