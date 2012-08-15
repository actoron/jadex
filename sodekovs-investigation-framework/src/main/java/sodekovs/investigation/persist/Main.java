package sodekovs.investigation.persist;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import sodekovs.investigation.model.ObservedEvent;
import sodekovs.investigation.model.InvestigationConfiguration;
import sodekovs.investigation.model.result.ExperimentResult;
import sodekovs.investigation.model.result.RowResult;
import sodekovs.investigation.model.result.SimulationResult;
import sodekovs.util.misc.XMLHandler;

public class Main {

	public static void main(String[] args) {

		double a = 13;
		double b = 6;
		int c = (int) Math.ceil(a/b);
//		;
		System.out.println("a: "  + Math.ceil(a/b) + " -  "  + c );
		
		testList();
		testXMLOutput();
		// Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("Europe/Berlin"));
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));
		// Calendar cal = Calendar.getInstance();
		System.out.println("adad1: " + cal.getTime().toGMTString());
		cal.setTimeInMillis(System.currentTimeMillis());
		System.out.println("adad2: " + cal.getTime().toGMTString() + " - " + Calendar.getInstance().getTimeZone().getDisplayName());
		System.out.println("adad3: " + Calendar.getInstance().getTimeZone().getDSTSavings());
		System.out.println("Here we go....Read");
		InvestigationConfiguration investigationConf = (InvestigationConfiguration) XMLHandler.parseXMLFromXMLFile("../jadex-applib-simulation/src/main/java/jadex/simulation/persist/TestXML.xml", InvestigationConfiguration.class);

		HelpElement event = new HelpElement();
		event.setApplicationName("Wow1");
		// XMLHandler.writeXML(event, "test.xml", HelpElement.class);
		// XMLHandler.writeXML(simConf, "test.xml", SimulationConfiguration.class);
		//		

		// SimulationConfiguration conf = null;
		// try {
		// conf = readXML();
		// } catch (JAXBException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }

		// Create Example Instance
		// OutputModel output = new OutputModel();
		// output.setParam(22);
		// output.setRun(1);
		// output.setDate(new Date(System.currentTimeMillis()));
		// ArrayList<String> list = new ArrayList<String>();
		// list.add("alpha");
		// list.add("beta");
		//		
		// HelpElement helpElement = new HelpElement();
		// helpElement.setMyName("Antisa");
		// output.setHelpElement(helpElement);
		// output.setRes(list);
		//		
		//		
		// SimulationConfiguration output = new SimulationConfiguration();
		// output.setName("MarsWorldi");
		// output.setApplicationReference("/xml/mars");
		// Observer obs = new Observer();
		// obs.setName("Obs1");
		// obs.setObjectSource("source1");
		// Filter fil = new Filter();
		// fil.setMode("myMode");
		// obs.setFilter(fil);
		// Observer obs2 = new Observer();
		// obs2.setName("Obs2");
		//		
		// ArrayList<Observer> obsList = new ArrayList<Observer>();
		// obsList.add(obs);
		// obsList.add(obs2);
		// output.setObserverList(obsList);
		//		
		//		
		//
		// Write
		// Writer w = null;
		//
		// try {
		//
		// // JAXBContext context = JAXBContext.newInstance(OutputModel.class);
		// JAXBContext context = JAXBContext.newInstance(SimulationConfiguration.class);
		// Marshaller m = context.createMarshaller();
		// m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		// // m.marshal(output, System.out);
		// m.marshal(conf, System.out);
		//
		// w = new FileWriter("club-jaxb.xml");
		// m.marshal(conf, w);
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (JAXBException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } finally {
		// try {
		// w.close();
		// } catch (Exception e) {
		// }
		// }
	}

	private static InvestigationConfiguration readXML() throws JAXBException {

		// configure input:
		JAXBContext ctx = JAXBContext.newInstance(InvestigationConfiguration.class);
		Unmarshaller u = ctx.createUnmarshaller();

		// read and return:
		try {
			InvestigationConfiguration obj = (InvestigationConfiguration) u.unmarshal(new FileInputStream("../jadex-applications-bdi/src/main/java/jadex/bdi/simulation/persist/TestXML.xml"));
			// OutputModel obj = (OutputModel) u.unmarshal(new FileInputStream("D:\\Workspaces\\playground\\testJadexV2\\jadex\\jadex-applications-bdi\\src\\main\\java\\jadex\\bdi\\simulation\\persist\\TestXML.xml"));
			System.out.println("res of XML: ");
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

	private static void testXMLOutput() {

		SimulationResult simRes = new SimulationResult();
		simRes.setEndtime(2198);
		simRes.setId("wow1");
		simRes.setName("MyName");
		simRes.setStarttime(1457);

		RowResult rowRes = new RowResult();
		rowRes.setId("row1");
		rowRes.setName("myRow1");

		ExperimentResult exRes = new ExperimentResult();
		exRes.setId("ExResId1");
		exRes.setName("NameExRes");

		ObservedEvent event = new ObservedEvent();
//		event.setValue("123");
		event.setApplicationName("EventId1");
//		event.setDataReference(new Data());

		ObservedEvent event2 = new ObservedEvent();
//		event2.setValue("12345");
		event2.setApplicationName("EventId2");
//		event2.setDataReference(new Data());

		exRes.addEvent(event);
		exRes.addEvent(event2);
		rowRes.addExperimentsResults(exRes);
		simRes.addRowsResults(rowRes);

		// rowRes = new RowResult();
		// rowRes.setId("row2");
		// rowRes.setName("myRow2");
		//				
		// simRes.addRowsResults(rowRes);

		// XMLHandler.writeXML(simRes, "abc.xml", SimulationResult.class);
		XMLHandler.writeXMLToFile(exRes, "abc.xml", ExperimentResult.class);

	}

	private static void testList() {
		ArrayList<ObservedEvent> list = new ArrayList<ObservedEvent>();
		Long s = new Long(2);

//		list.add(new ObservedEvent(null, null, 45, null, null));
//		list.add(new ObservedEvent(null, null, 2, null, null));
//		list.add(new ObservedEvent(null, null, 1, null, null));
//		list.add(new ObservedEvent(null, null, 101, null, null));
//		list.add(new ObservedEvent(null, null, 247, null, null));

		Collections.sort(list, new Comparator() {
			public int compare(Object arg0, Object arg1) {
				return new Long(((ObservedEvent) arg0).getAbsoluteTimestamp()).compareTo(new Long(((ObservedEvent) arg1).getAbsoluteTimestamp()));
			}
		});
		
		for(ObservedEvent event  : list){
		System.out.println(event);
		}
	}

}
