package ISIS.FIE4.MarieJeanEdouard.ISIS_Capitalist.tools;

import generated.World;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class WorldServices {

    private GenericResources tool = new GenericResources();

    public WorldServices() {
    }

//    public World readWorldFromXml(String pseudo) {
    public World readWorldFromXml() {

        World result = null;
        try {
            JAXBContext context = JAXBContext.newInstance(World.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            //InputStream input = getClass().getClassLoader().getResourceAsStream(pseudo+"-world.xml");
            InputStream input = getClass().getClassLoader().getResourceAsStream("World.xml");
            result = (World) unmarshaller.unmarshal(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        tool.updateScore(result);
        return result;
    }

    public void saveWorldToXml(World world, String pseudo) throws JAXBException, FileNotFoundException {
        JAXBContext context = JAXBContext.newInstance(World.class);
        Marshaller marshaller = context.createMarshaller();

        OutputStream output = new FileOutputStream(pseudo + "-world.xml");

        marshaller.marshal(world, output);
    }

//    public World getWorld(String pseudo) {
//        return readWorldFromXml(pseudo);
//    }
    public World getWorld() {
        
        return readWorldFromXml();
    }

    public void deleteworld() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
