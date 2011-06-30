package sodekovs.applications.bike;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.TimerTask;

import sodekovs.util.bike.persistence.CapitalBikesLogDAO;

/**
 * Check when file is created and update PNG on panel.
 * @author vilenica
 *
 */
public class DownloadFileThread extends TimerTask {
		
	CapitalBikesLogDAO	bikeDAO = CapitalBikesLogDAO.getInstance();
	
	public DownloadFileThread() {		
	}

	public void run() {
		
//		download();
		//	this.cancel();
		
	
		 
		 System.out.println(new Date(System.currentTimeMillis()).toString() + " - Table Size: " + bikeDAO.getRowSizeOfLogTable());
		
	}
	
	private void download(){
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
		 
//		 System.out.println(result.toString());		 
		 bikeDAO.insertNewLog(result.toString());
	}
}
