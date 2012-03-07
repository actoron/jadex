
package jadex.xml.tutorial.jibx.example11;

public class Flight
{
    private Carrier carrier;
    private int number;
    private String departure;
    private String arrival;

    public Carrier getCarrier()
	{
		return this.carrier;
	}

	public void setCarrier(Carrier carrier)
	{
		this.carrier = carrier;
	}

	public int getNumber()
	{
		return this.number;
	}

	public void setNumber(int number)
	{
		this.number = number;
	}

	public String getDeparture()
	{
		return this.departure;
	}

	public void setDeparture(String departure)
	{
		this.departure = departure;
	}

	public String getArrival()
	{
		return this.arrival;
	}

	public void setArrival(String arrival)
	{
		this.arrival = arrival;
	}
    
}
