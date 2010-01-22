
package jadex.xml.tutorial.example08;

import java.util.ArrayList;
import java.util.LinkedList;

public class TimeTable
{
    private ArrayList carriers;
    private LinkedList airports;
    private String[] notes;
    
    public void addCarrier(Carrier carrier)
    {
    	if(carriers==null)
    		carriers = new ArrayList();
    	carriers.add(carrier);
    }
    
    public void addAirport(Airport carrier)
    {
    	if(airports==null)
    		airports = new LinkedList();
    	airports.add(carrier);
    }
    
    // Hack! todo: support setNotes() below
    public void addNote(String note)
    {
    	if(notes==null)
    	{
    		notes = new String[]{note};
    	}
    	else
    	{
    		String[] copy = new String[notes.length+1];
    		System.arraycopy(notes, 0, copy, 0, notes.length);
    		copy[notes.length] = note;
    	}
    }
    
//    public void setNotes(String[] notes)
//    {
//    	this.notes = notes;
//    }
}
