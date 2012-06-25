package jadex.xml.stax;

public class StaxLocationWrapper implements ILocation
{

	private javax.xml.stream.Location location;

	public StaxLocationWrapper(javax.xml.stream.Location loc)
	{
		this.location = loc;
	}

	public int getLineNumber()
	{
		return location.getLineNumber();
	}

	public int getColumnNumber()
	{
		return location.getColumnNumber();
	}

	public int getCharacterOffset()
	{
		return location.getCharacterOffset();
	}

	public String getPublicId()
	{
		return location.getPublicId();
	}

	public String getSystemId()
	{
		return location.getSystemId();
	}

	public static ILocation fromLocation(javax.xml.stream.Location loc)
	{
		return new StaxLocationWrapper(loc);
	}

}
