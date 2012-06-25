package jadex.xml.stax;

public class JadexLocationWrapper implements javax.xml.stream.Location
{
	private Location location;

	public JadexLocationWrapper(Location loc)
	{
		this.location = loc;
	}

	@Override
	public int getLineNumber()
	{
		return location.getLineNumber();
	}

	@Override
	public int getColumnNumber()
	{
		return location.getColumnNumber();
	}

	@Override
	public int getCharacterOffset()
	{
		return location.getCharacterOffset();
	}

	@Override
	public String getPublicId()
	{
		return location.getPublicId();
	}

	@Override
	public String getSystemId()
	{
		return location.getSystemId();
	}

	public static javax.xml.stream.Location fromLocation(Location loc)
	{
		return new JadexLocationWrapper(loc);
	}
}
