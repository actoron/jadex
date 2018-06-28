
package jadex.xml.tutorial.jibx.example08;

import java.util.List;

public class TimeTable
{
    @SuppressWarnings("unused")
	private List carriers;
    @SuppressWarnings("unused")
	private List airports;
    @SuppressWarnings("unused")
	private String[] notes;
    
//    public void addCarrier(Carrier carrier)
//    {
//    	if(carriers==null)
//    		carriers = new ArrayList();
//    	carriers.add(carrier);
//    }
//    
//    public void addAirport(Airport carrier)
//    {
//    	if(airports==null)
//    		airports = new LinkedList();
//    	airports.add(carrier);
//    }
    
    /**
	 *  Set the carriers.
	 *  @param carriers The carriers to set.
	 */
	public void setCarriers(List carriers)
	{
		this.carriers = carriers;
	}

	/**
	 *  Set the airports.
	 *  @param airports The airports to set.
	 */
	public void setAirports(List airports)
	{
		this.airports = airports;
	}
    
//    public void addNote(String note)
//    {
//    	if(notes==null)
//    	{
//    		notes = new String[]{note};
//    	}
//    	else
//    	{
//    		String[] copy = new String[notes.length+1];
//    		System.arraycopy(notes, 0, copy, 0, notes.length);
//    		copy[notes.length] = note;
//    	}
//    }
    
	public void setNotes(String[] notes)
    {
    	this.notes = notes;
    }
}
