package jadex.bridge.service.types.context;

public abstract class JadexAndroidEvent implements IJadexAndroidEvent
{

	@Override
	public String getType()
	{
		return this.getClass().getCanonicalName();
	}

}
