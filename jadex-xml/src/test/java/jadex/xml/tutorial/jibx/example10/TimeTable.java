
package jadex.xml.tutorial.jibx.example10;

import java.util.ArrayList;

public class TimeTable
{
    private ArrayList carriers;
    private Object[] airports;
    
    public void addCarrier(Carrier carrier)
    {
    	if(carriers==null)
    		carriers = new ArrayList();
    	carriers.add(carrier);
    }
    
    // Hack! todo: better support bulk
    public void addAirport(Airport carrier)
    {
    	if(airports==null)
    	{
    		airports = new Object[]{carrier};
    	}
    	else
    	{
    		Object[] copy = new Object[airports.length+1];
    		System.arraycopy(airports, 0, copy, 0, airports.length);
    		copy[airports.length] = carrier;
    	}
    }

}
