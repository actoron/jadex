package jadex.bytecode.vmhacks;

import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

/**
 *  This class abuses the java.security.* API to establish a
 *  VM-wide object store. 
 *
 */
class SecurityProviderStore extends Provider
{
	/**
	 *  ID
	 */
	public static final int ID = 23070273;
	
	/** Flag if injected. */
	public static boolean INJECTED = false;
	
	/** The delegate list. */
	private List<Object> realstore;
	
	/**
	 *  Creates the store.
	 */
	private SecurityProviderStore()
	{
		super(String.valueOf(ID), 1.0, "Jadex_VM_Hacks");
		realstore = new ArrayList<Object>();
		realstore.add(new LinkedBlockingQueue<Object>());
		realstore.add(new ClassStore());
	}
	
	/**
	 *  Returns the list.
	 */
	public Collection<Object> values()
	{
		return realstore;
	}
	
	/**LoggerFilterStore
	 *  Injects the store.
	 */
	protected static final void inject()
	{
		if (!INJECTED)
		{
			synchronized(SecurityProviderStore.class)
			{
				if (!INJECTED)
				{
					SecurityProviderStore store = new SecurityProviderStore();
					Security.addProvider(store);
					INJECTED = true;
				}
			}
		}
	}
	
	/**
	 *  Returns a store object.
	 *  
	 *  @param i Store object index.
	 *  @param clazz Class marker.
	 *  @return The object.
	 */
	protected static final ArrayList<Object> getStore()
	{
		Provider prov = Security.getProvider(String.valueOf(SecurityProviderStore.ID));
		return (ArrayList<Object>) prov.values();
	}
}
