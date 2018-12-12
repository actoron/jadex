package jadex.bridge.service.types.context;

/**
 * To create own events, just extend this class and add getters+setters for custom properties.
 * @author kalinowski
 *
 */
public abstract class JadexAndroidEvent implements IJadexAndroidEvent
{

	@Override
	public String getType()
	{
		return this.getClass().getCanonicalName();
	}

}
