package jadex.platform.service.serialization;

import jadex.bridge.IInternalAccess;
import jadex.bridge.component.impl.ExecutionComponentFeature;
import jadex.bridge.component.impl.remotecommands.IMethodReplacement;
import jadex.bridge.service.types.cms.PlatformComponent;

/**
 *  Default replacement code for equals() method.
 */
public class GetExternalFeatureMethodReplacement implements IMethodReplacement
{
	/**
	 *  Invoke the method on the given object with the given args.
	 */
	public Object invoke(Object obj, Object[] args)
	{
		Class<?> iface = (Class<?>)args[0];
		IInternalAccess ia = ExecutionComponentFeature.LOCAL.get();
		ClassLoader cl = ia!=null? ia.getClassLoader(): this.getClass().getClassLoader();
		return PlatformComponent.getExternalFeature(iface, cl, obj);
	}
}