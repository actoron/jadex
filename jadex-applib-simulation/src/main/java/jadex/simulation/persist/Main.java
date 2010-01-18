package jadex.simulation.persist;

import jadex.simulation.helper.XMLHandler;
import jadex.simulation.model.SimulationConfiguration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class Main {

	public static void main(String[] args) {
//		Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("Europe/Berlin"));
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));
//		Calendar cal = Calendar.getInstance();		
		System.out.println("adad1: " + cal.getTime().toGMTString());
		cal.setTimeInMillis(System.currentTimeMillis());		
		System.out.println("adad2: " + cal.getTime().toGMTString() + " - " + Calendar.getInstance().getTimeZone().getDisplayName());
		System.out.println("adad3: " + Calendar.getInstance().getTimeZone().getDSTSavings());
		System.out.println("Here we go....Read");
		SimulationConfiguration simConf = (SimulationConfiguration) XMLHandler.parseXML("../jadex-applications-bdi/src/main/java/jadex/bdi/simulation/persist/TestXML.xml", SimulationConfiguration.class);
		XMLHandler.writeXML(simConf, "test.xml", SimulationConfiguration.class);
//		
		
//		SimulationConfiguration conf = null;
//		try {
//			conf = readXML();
//		} catch (JAXBException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}

		// Create Example Instance
//		OutputModel output = new OutputModel();
//		output.setParam(22);
//		output.setRun(1);
//		output.setDate(new Date(System.currentTimeMillis()));
//		ArrayList<String> list = new ArrayList<String>();
//		list.add("alpha");
//		list.add("beta");
//		
//		HelpElement helpElement = new HelpElement();
//		helpElement.setMyName("Antisa");
//		output.setHelpElement(helpElement);		
//		output.setRes(list);
//		
//		
//		SimulationConfiguration output =  new SimulationConfiguration();
//		output.setName("MarsWorldi");
//		output.setApplicationReference("/xml/mars");
//		Observer obs = new Observer();		
//		obs.setName("Obs1");
//		obs.setObjectSource("source1");
//		Filter fil = new Filter();
//		fil.setMode("myMode");
//		obs.setFilter(fil);
//		Observer obs2 = new Observer();
//		obs2.setName("Obs2");
//		
//		ArrayList<Observer> obsList = new ArrayList<Observer>();
//		obsList.add(obs);
//		obsList.add(obs2);
//		output.setObserverList(obsList);
//		
//		
//
		// Write
//		Writer w = null;
//
//		try {
//
////			JAXBContext context = JAXBContext.newInstance(OutputModel.class);
//			JAXBContext context = JAXBContext.newInstance(SimulationConfiguration.class);
//			Marshaller m = context.createMarshaller();
//			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
////			m.marshal(output, System.out);
//			m.marshal(conf, System.out);
//
//			w = new FileWriter("club-jaxb.xml");
//			m.marshal(conf, w);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (JAXBException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally {
//			try {
//				w.close();
//			} catch (Exception e) {
//			}
//		}
	}
	
	private static  SimulationConfiguration readXML() throws JAXBException{
		
		// configure input:
		JAXBContext ctx = JAXBContext.newInstance(SimulationConfiguration.class);
		Unmarshaller u = ctx.createUnmarshaller();
	    
		// read and return:
		try {			
			SimulationConfiguration obj =  (SimulationConfiguration) u.unmarshal(new FileInputStream("../jadex-applications-bdi/src/main/java/jadex/bdi/simulation/persist/TestXML.xml"));
//			OutputModel obj =  (OutputModel) u.unmarshal(new FileInputStream("D:\\Workspaces\\playground\\testJadexV2\\jadex\\jadex-applications-bdi\\src\\main\\java\\jadex\\bdi\\simulation\\persist\\TestXML.xml"));
			System.out.println("res of XML: ") ;
			return obj;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
