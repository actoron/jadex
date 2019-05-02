package jadex.bridge.service.types.cms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.types.factory.IPlatformComponentAccess;
import jadex.commons.Tuple;
import jadex.commons.Tuple2;
import jadex.commons.Tuple3;
import jadex.commons.collection.IAutoLock;
import jadex.commons.collection.IRwMap;
import jadex.commons.collection.LRU;
import jadex.commons.collection.RwMapWrapper;
import jadex.commons.future.IFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;

/**
 *  Class representing the state information for component management.
 *
 */
public class CmsState
{
	/** The component map. */
	protected IRwMap<IComponentIdentifier, CmsComponentState> componentmap;
	
	/** The local types. */
	protected IRwMap<Tuple, String> localtypes;
	
	/** ClassLoader cache. */
	protected IRwMap<IResourceIdentifier, ClassLoader> classloaders;
	
	/** The model cache. */
	protected IRwMap<Tuple2<String, ClassLoader>, Tuple3<IModelInfo, ClassLoader, Collection<IComponentFeatureFactory>>> modelcache;
	
	/** The component ID counts. */
	protected IRwMap<String, Integer> cidcounts;
	
	/** Listeners listening to all components. */
	protected Collection<SubscriptionIntermediateFuture<CMSStatusEvent>> allcomponentslisteners;
	
	/**
	 *  Creates the state.
	 */
	public CmsState()
	{
		ReentrantReadWriteLock lock = new ReentrantReadWriteLock(false);
		
		allcomponentslisteners = new ArrayList<>();
		componentmap = new RwMapWrapper<>(new HashMap<>(), lock);
		localtypes = new RwMapWrapper<>(new HashMap<>(), lock);
		classloaders = new RwMapWrapper<>(new LRU<>(500), lock);
		modelcache = new RwMapWrapper<>(new LRU<>(1000), lock);
		cidcounts = new RwMapWrapper<>(new HashMap<>(), lock);
	}
	
	/**
	 *  Convenience method to get the access of a component.
	 * 
	 *  @param cid The component ID.
	 *  @return The access of a component.
	 */
	public IPlatformComponentAccess getAccess(IComponentIdentifier cid)
	{
		CmsComponentState state = componentmap.get(cid);
		return state != null ? state.getAccess() : null;
	}
	
	/**
	 *  Convenience method to get the cleanup future of a component.
	 * 
	 *  @param cid The component ID.
	 *  @return The cleanup future of a component.
	 */
	public IFuture<Map<String, Object>> getCleanupFuture(IComponentIdentifier cid)
	{
		CmsComponentState state = componentmap.get(cid);
		return state != null ? state.getCleanupFuture() : null;
	}
	
	/**
	 *  Convenience method to get the init info of a component.
	 * 
	 *  @param cid The component ID.
	 *  @return The init info of a component.
	 */
	public InitInfo getInitInfo(IComponentIdentifier cid)
	{
		CmsComponentState state = componentmap.get(cid);
		return state != null ? state.getInitInfo() : null;
	}
	
	/**
	 *  Get the CMS listeners of a component or listen-to-all listeners if cid == null.
	 * 
	 *  @param cid The component ID or null.
	 *  @return The CMS listeners.
	 */
	public Collection<SubscriptionIntermediateFuture<CMSStatusEvent>> getCmsListeners(IComponentIdentifier cid)
	{
		if (cid == null)
			return allcomponentslisteners;
		
		CmsComponentState state = componentmap.get(cid);
		return state != null ? state.getCmsListeners() : null;
	}
	
	public Collection<SubscriptionIntermediateFuture<CMSStatusEvent>> getAllListeners()
	{
		return allcomponentslisteners;
	}
	
	/**
	 *  Convenience method to get the state of a component.
	 * 
	 *  @param cid The component ID.
	 *  @return The state of a component.
	 */
	public CmsComponentState getComponent(IComponentIdentifier cid)
	{
		return componentmap.get(cid);
	}
	
	/**
	 *  Gets the component map.
	 *
	 *  @return The component map.
	 */
	public Map<IComponentIdentifier, CmsComponentState> getComponentMap()
	{
		return componentmap;
	}
	
	/**
	 *  Gets the local types.
	 *
	 *  @return The local types.
	 */
	public IRwMap<Tuple, String> getLocalTypes()
	{
		return localtypes;
	}

	/**
	 *  Gets the class loaders.
	 *
	 *  @return The class loaders.
	 */
	public IRwMap<IResourceIdentifier, ClassLoader> getClassLoaders()
	{
		return classloaders;
	}

	/**
	 *  Gets the model cache.
	 *
	 *  @return The model cache.
	 */
	public IRwMap<Tuple2<String, ClassLoader>, Tuple3<IModelInfo, ClassLoader, Collection<IComponentFeatureFactory>>> getModelCache()
	{
		return modelcache;
	}

	/**
	 *  Gets the cid counts.
	 *
	 *  @return The cid counts.
	 */
	public IRwMap<String, Integer> getCidCounts()
	{
		return cidcounts;
	}
	
	/**
	 *  Locks the read lock for resource-based locking.
	 */
	public IAutoLock readLock()
	{
		return componentmap.readLock();
	}
	
	/**
	 *  Locks the write lock for resource-based locking.
	 */
	public IAutoLock writeLock()
	{
		return componentmap.writeLock();
	}

	/** State for a particular component. */
	public static class CmsComponentState
	{
		/** The component access. */
		protected IPlatformComponentAccess access;
		
		/** The initialization infos. */
		protected InitInfo initinfo;
		
		/** The child count. */
		protected int childcount;
		
		/** The cleanup future. */
		protected IFuture<Map<String, Object>> cleanupfuture;
		
		/** Component lock for locking parent. */
		protected LockEntry lock;
		
		/** The cms listeners for the component. */
		protected Collection<SubscriptionIntermediateFuture<CMSStatusEvent>> cmslisteners;
		
		/**
		 *  Creates the component state.
		 */
		public CmsComponentState()
		{
		}

		/**
		 *  Gets the access. This may return null, e.g. if component is terminating.
		 *
		 *  @return The access.
		 */
		public IPlatformComponentAccess getAccess()
		{
			return access;
		}

		/**
		 *  Sets the access.
		 *  @param access The access to set.
		 */
		public void setAccess(IPlatformComponentAccess access)
		{
			this.access = access;
		}

		/**
		 *  Gets the initinfo.
		 *
		 *  @return The initinfo.
		 */
		public InitInfo getInitInfo()
		{
			return initinfo;
		}

		/**
		 *  Sets the initinfo.
		 *  @param initinfo The initinfo to set.
		 */
		public void setInitInfo(InitInfo initinfo)
		{
			this.initinfo = initinfo;
		}

		/**
		 *  Gets the childcount.
		 *
		 *  @return The childcount.
		 */
		public int getChildCount()
		{
			return childcount;
		}

		/**
		 *  Sets the childcount.
		 *  @param childcount The childcount to set.
		 */
		public void setChildCount(int childcount)
		{
			this.childcount = childcount;
		}

		/**
		 *  Gets the cleanupfuture.
		 *
		 *  @return The cleanupfuture.
		 */
		public IFuture<Map<String, Object>> getCleanupFuture()
		{
			return cleanupfuture;
		}

		/**
		 *  Sets the cleanupfuture.
		 *  @param cleanupfuture The cleanupfuture to set.
		 */
		public void setCleanupFuture(IFuture<Map<String, Object>> cleanupfuture)
		{
			this.cleanupfuture = cleanupfuture;
		}

		/**
		 *  Gets the lock.
		 *
		 *  @return The lock.
		 */
		public LockEntry getLock()
		{
			return lock;
		}

		/**
		 *  Sets the lock.
		 *  @param lock The lock to set.
		 */
		public void setLock(LockEntry lock)
		{
			this.lock = lock;
		}

		/**
		 *  Gets the cmslisteners.
		 *
		 *  @return The cmslisteners.
		 */
		public Collection<SubscriptionIntermediateFuture<CMSStatusEvent>> getCmsListeners()
		{
			return cmslisteners;
		}

		/**
		 *  Sets the cmslisteners.
		 *  @param cmslisteners The cmslisteners to set.
		 */
		public void setCmsListeners(Collection<SubscriptionIntermediateFuture<CMSStatusEvent>> cmslisteners)
		{
			this.cmslisteners = cmslisteners;
		}
	}
}
