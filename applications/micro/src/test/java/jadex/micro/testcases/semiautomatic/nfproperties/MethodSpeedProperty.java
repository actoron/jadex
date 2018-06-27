package jadex.micro.testcases.semiautomatic.nfproperties;

import jadex.bridge.nonfunctional.AbstractNFProperty;
import jadex.bridge.nonfunctional.NFPropertyMetaInfo;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Example property returning a String about the method speed.
 * 	This also demonstrates String properties, a real-world
 * 	implementation would probably use a quantifiable measure
 *  like response time.
 */
public class MethodSpeedProperty extends AbstractNFProperty<String, Void>
{
	protected String speed;

	public MethodSpeedProperty()
	{
		super(new NFPropertyMetaInfo("methodspeed", String.class, null, false, -1, false, null));
		speed = "Very fast, indeed!";
	}

//	public IFuture<String> getValue(Class<Void> unit)
	public IFuture<String> getValue(Void unit)
	{
		return new Future<String>(speed);
	}
}
