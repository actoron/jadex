
package jadex.xml.tutorial.jibx.example11;

import java.util.ArrayList;

public class TimeTable
{
    private ArrayList carriers;
    private ArrayList airports;
    private ArrayList routes;
    
    public void addCarrier(Carrier carrier)
    {
    	if(carriers==null)
    		carriers = new ArrayList();
    	carriers.add(carrier);
    }
    
    public void addAirport(Airport airport)
    {
    	if(airports==null)
    		airports = new ArrayList();
    	airports.add(airport);
    }
    
    public void addRoute(Route route)
    {
    	if(routes==null)
    		routes = new ArrayList();
    	routes.add(route);
    }
    
}
