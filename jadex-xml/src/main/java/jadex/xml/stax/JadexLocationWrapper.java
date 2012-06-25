package jadex.xml.stax;

public class JadexLocationWrapper implements javax.xml.stream.Location
{
	private ILocation location;

	public JadexLocationWrapper(ILocation loc)
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

	public static javax.xml.stream.Location fromLocation(ILocation loc)
	{
		return new JadexLocationWrapper(loc);
	}
}
