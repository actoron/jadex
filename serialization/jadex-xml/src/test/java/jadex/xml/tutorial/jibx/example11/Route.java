
package jadex.xml.tutorial.jibx.example11;

import java.util.ArrayList;

public class Route
{
    private Airport from;
    private Airport to;
    private ArrayList flights;

    public Airport getFrom()
	{
		return this.from;
	}

	public void setFrom(Airport from)
	{
		this.from = from;
	}

	public Airport getTo()
	{
		return this.to;
	}

	public void setTo(Airport to)
	{
		this.to = to;
	}
  
	public void addFlight(Flight flight)
    {
    	if(flights==null)
    		flights = new ArrayList();
    	flights.add(flight);
    }
    
}
