package sodekovs.applications.bikes.dataanalyzer;

import java.util.ArrayList;
import java.util.Collections;

import sodekovs.applications.bikes.model.SystemSnapshot;

public class CheckData {

	private static StationsDAO stationsDAO = StationsDAO.getInstance();
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		isValidTimerange("Washington");

	}

	public static void isValidTimerange(String cityName) {
		ArrayList<SystemSnapshot> snapshots = stationsDAO.loadAllLogsForCity(cityName);
		
		System.out.println("Snapshot size for city: " + cityName  + "; size: " + snapshots.size());
		
//		for(SystemSnapshot s: snapshots){
//		System.out.println(s.toString());	
//		}
		
//		Collections.sort(list);
	}

}
