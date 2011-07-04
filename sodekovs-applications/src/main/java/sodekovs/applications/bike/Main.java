package sodekovs.applications.bike;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Timer;

import sodekovs.util.bike.persistence.CapitalBikesLogDAO;

public class Main {

	public static void  main (String[] args){
		
//		downloadData();
//		showHistory();
		
		// Timer is used to check when png is created and to re-load png to panel.
		long TIME_TO_START = 500;
		long DELAY_BETWEEN_POLLS = 300000;
		Timer timer = new Timer();		
		timer.schedule(new DownloadFileThread(), TIME_TO_START, DELAY_BETWEEN_POLLS);
			
	}
	
	private static void downloadData(){
		// Verbindung aufbauen
		 URL url;
		 ByteArrayOutputStream result = null;
		 
		 
		try {
			url = new URL("http://www.capitalbikeshare.com/stations/bikeStations.xml");
			URLConnection connection = url.openConnection();
			
			 // XML Daten einlesen
			 result = new ByteArrayOutputStream();
			 InputStream input = connection.getInputStream();
			 byte[] buffer = new byte[1000];
			 int amount = 0;    
			 
			 // Inhalt lesen
			 while(amount != -1){
			 
			   result.write(buffer, 0, amount);
			   amount = input.read(buffer);
			 
			 }
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 System.out.println(result.toString());
		 
		 CapitalBikesLogDAO	bikeDAO = CapitalBikesLogDAO.getInstance();
		 bikeDAO.insertNewLog(result.toString());
	}
	
	private static void showHistory(){
		CapitalBikesLogDAO	bikeDAO = CapitalBikesLogDAO.getInstance();
		 String[] history  = bikeDAO.loadAllLogs();
		 
		 for(String log : history){
			 System.out.println(log);
		 }
	}
}
