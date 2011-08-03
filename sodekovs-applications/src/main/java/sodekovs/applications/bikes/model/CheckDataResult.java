package sodekovs.applications.bikes.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import sodekovs.util.misc.TimeConverter;

/**
 * Is used to store results of checked data: Can this day in this city be used with this bucketSize for data analysis?  
 * 
 * @author Vilenica
 *
 */

public class CheckDataResult {

	private String cityName = null;
	private long day = -1;
	//Integer -> BucketSize -> 5 / 10 / 15 / 20 minutes
	private HashMap<Integer,Boolean> dataResult = new HashMap<Integer,Boolean>();
	
	
	/**
	 * @return the cityName
	 */
	public String getCityName() {
		return cityName;
	}
	/**
	 * @param cityName the cityName to set
	 */
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	/**
	 * @return the day
	 */
	public long getDay() {
		return day;
	}
	/**
	 * @param day the day to set
	 */
	public void setDay(long day) {
		this.day = day;
	}
	/**
	 * @return the dataResult
	 */
	public HashMap<Integer, Boolean> getDataResult() {
		return dataResult;
	}
	/**
	 * @param dataResult the dataResult to set
	 */
	public void setDataResult(HashMap<Integer, Boolean> dataResult) {
		this.dataResult = dataResult;
	}
	
	public String toStringShort(){
		 ArrayList<Integer> keys = sortHashMapKeys();
		 StringBuffer buf = new StringBuffer();
		 
		 for(Integer k : keys){
			 if(this.dataResult.get(k) == true){
			 buf.append(k);			 
			 buf.append(", ");
			 }
		 }		
		return this.cityName + " - " + TimeConverter.longTime2DateString(this.day) + " -> " + buf.toString();
	}
	
	public String toString(){
		 ArrayList<Integer> keys = sortHashMapKeys();
		 StringBuffer buf = new StringBuffer();
		 
		 for(Integer k : keys){
			 buf.append(k);
			 buf.append("=");
			 buf.append(this.dataResult.get(k));
			 buf.append("; ");
		 }
		
		return this.cityName + " - " + TimeConverter.longTime2DateString(this.day) + " -> " + buf.toString();
	}
	
	/**
	 * Sort the keys of the HashMap
	 * @return
	 */
	private ArrayList<Integer> sortHashMapKeys(){
		List<Integer> list = new ArrayList<Integer>(dataResult.keySet());

//		List<Integer> list = new List<Integer>(this.dataResult.keySet());
//		this.dataResult.keySet().addAll(list);
//		List<String> list = new ArrayList<String>(m.keySet());
		Collections.sort(list);
		return (ArrayList) list;
	}
}
