package jadex.simulation.helper;

import java.util.ArrayList;

public class SynchObject {

	private int counter = 0;
	private ArrayList<String> observedEvents = new ArrayList<String>();
	
	public void incrementCounter(){
		counter++;
	}
	
	public void reduceCounter(){
		counter--;
	}
	
	public void addResult2List(String event){
		this.observedEvents.add(event);
	}
	
	public int getCounter(){
		return this.counter;
	}
	
}
