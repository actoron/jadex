package jadex.xml.stax;

public class StaxLocationWrapper implements Location
{

	private javax.xml.stream.Location location;

	public StaxLocationWrapper(javax.xml.stream.Location loc)
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

	public static Location fromLocation(javax.xml.stream.Location loc)
	{
		return new StaxLocationWrapper(loc);
	}

}
