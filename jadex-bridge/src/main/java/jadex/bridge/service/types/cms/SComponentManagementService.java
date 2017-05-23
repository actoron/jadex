package jadex.bridge.service.types.cms;

import java.util.Map;

import jadex.base.PlatformConfiguration;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.factory.IPlatformComponentAccess;

/**
 *  Static CMS methods.
 *
 */
public class SComponentManagementService
{
	/**
	 *  Gets the external access of a local component.
	 *  
	 *  @param cid The component id.
	 *  @return External Access.
	 */
	public static final IExternalAccess getLocalExternalAccess(IComponentIdentifier cid)
	{
		assert cid != null;
		
		IComponentIdentifier platform = cid.getRoot();
		
		IPlatformComponentAccess comp = getComponentMap(platform).get(cid);
		if (comp == null)
			throw new RuntimeException("Component not found: " + cid);
		return comp.getInternalAccess().getExternalAccess();
	}
	
	/**
	 *  Gets the classloader of a local component.
	 *  
	 *  @param cid The component id.
	 *  @return ClassLoader.
	 */
	public static final ClassLoader getLocalClassLoader(IComponentIdentifier cid)
	{
		assert cid != null;
		
		IComponentIdentifier platform = cid.getRoot();
		
		IPlatformComponentAccess comp = getComponentMap(platform).get(cid);
		if (comp == null)
			throw new RuntimeException("Component not found: " + cid);
		return comp.getInternalAccess().getClassLoader();
	}
	
	/**
	 *  Gets a local component map.
	 *  
	 *  @param platform Platform ID.
	 *  @return The component map.
	 */
	protected static final Map<IComponentIdentifier, IPlatformComponentAccess> getComponentMap(IComponentIdentifier platform)
	{
		@SuppressWarnings("unchecked")
		Map<IComponentIdentifier, IPlatformComponentAccess> ret = (Map<IComponentIdentifier, IPlatformComponentAccess>) PlatformConfiguration.getPlatformValue(platform, PlatformConfiguration.DATA_COMPONENTMAP);
		if (ret == null)
			throw new IllegalArgumentException("Platform not found: " + platform);
		return ret;
	}
}
