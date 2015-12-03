package jadex.platform.service.awareness.management;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.IPojoComponentFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.awareness.DiscoveryInfo;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.CollectionResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.transformation.annotations.Classname;


/**
 * Helper class for AwarenessSettings. Provides common functionality that can be
 * used by different GUI implementations.
 */
public class AwarenessManagementAgentHelper
{
	private IExternalAccess	component;

	/**
	 *  Create a new helper.
	 */
	public AwarenessManagementAgentHelper(IExternalAccess component)
	{
		this.component = component;
	}

	/**
	 * Enables or disables Discovery Mechanisms.
	 * 
	 * @param type Name of the Awareness Subcomponent
	 * @param on activates/deactivates the mechanism if true/false.
	 * @return Void
	 */
	public IFuture<Void> setDiscoveryMechanismState(final String type, final boolean on)
	{
		return component.scheduleStep(new IComponentStep<Void>()
		{
			@Classname("deoractivateDiscoveryMechanism")
			public IFuture<Void> execute(final IInternalAccess ia)
			{
				final Future<Void> ret = new Future<Void>();
				getChildrenAccesses(ia).addResultListener(ia.getComponentFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<Collection<IExternalAccess>, Void>(ret)
				{
					public void customResultAvailable(Collection<IExternalAccess> subs)
					{
						IComponentIdentifier found = null;
						for(Iterator<IExternalAccess> it = subs.iterator(); it.hasNext();)
						{
							IExternalAccess exta = it.next();
							if(type.equals(exta.getLocalType()))
							{
								found = exta.getComponentIdentifier();
								break;
							}
						}

						// Start relay mechanism agent
						if(on && found == null)
						{
							SServiceProvider.getService(ia, IComponentManagementService.class, RequiredServiceInfo.SCOPE_GLOBAL).addResultListener(
								ia.getComponentFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
							{
								public void customResultAvailable(IComponentManagementService cms)
								{
									CreationInfo info = new CreationInfo(ia.getComponentIdentifier());
									cms.createComponent(null, type, info, null).addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, Void>(ret)
									{
										public void customResultAvailable(IComponentIdentifier result)
										{
											ret.setResult(null);
										}
									});
								};
							}));
						}

						// Stop relay mechanism agent
						else if(!on && found != null)
						{
							final IComponentIdentifier cid = found;
							SServiceProvider.getService(ia, IComponentManagementService.class, RequiredServiceInfo.SCOPE_GLOBAL).addResultListener(
								ia.getComponentFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
							{
								public void customResultAvailable(IComponentManagementService cms)
								{
									cms.destroyComponent(cid).addResultListener(new ExceptionDelegationResultListener<Map<String, Object>, Void>(ret)
									{
										public void customResultAvailable(Map<String, Object> result)
										{
											ret.setResult(null);
										}
									});
								};
							}));
						}

						// No change required.
						else
						{
							ret.setResult(null);
						}
					};
				}));
				return ret;
			}
		});
	}

	/**
	 * Asks the AwarenessManagementAgent for the current Settings.
	 * 
	 * @return the current AwarenessSettings
	 */
	public IFuture<AwarenessSettingsData> getSettings()
	{
		return component.scheduleStep(new IComponentStep<AwarenessSettingsData>()
		{
			@Classname("refreshSettings")
			public IFuture<AwarenessSettingsData> execute(IInternalAccess ia)
			{
				AwarenessManagementAgent agent = (AwarenessManagementAgent)ia.getComponentFeature(IPojoComponentFeature.class).getPojoAgent();
				AwarenessSettingsData ret = new AwarenessSettingsData();
				// Object[] ai = agent.getAddressInfo();
				// ret.address = (InetAddress)ai[0];
				// ret.port = (Integer)ai[1];
				ret.delay = agent.getDelay();
				ret.fast = agent.isFastAwareness();
				ret.autocreate = agent.isAutoCreateProxy();
				ret.autodelete = agent.isAutoDeleteProxy();
				ret.includes = agent.getIncludes();
				ret.excludes = agent.getExcludes();
				return new Future<AwarenessSettingsData>(ret);
			}
		});
	}

	/**
	 * Returns all remote Discovery Infos.
	 * 
	 * @return Array of {@link DiscoveryInfo}s
	 */
	public IFuture<DiscoveryInfo[]> getDiscoveryInfos()
	{
		return component.scheduleStep(new IComponentStep<DiscoveryInfo[]>()
		{
			@Classname("getDiscoveryInfos")
			public IFuture<DiscoveryInfo[]> execute(IInternalAccess ia)
			{
				AwarenessManagementAgent agent = (AwarenessManagementAgent)ia.getComponentFeature(IPojoComponentFeature.class).getPojoAgent();
//				AwarenessManagementAgent agent = (AwarenessManagementAgent)ia;
				return new Future<DiscoveryInfo[]>(agent.getDiscoveryInfos());
			}
		});
	}

	/**
	 * Returns a Set of active Discovery Mechanism types.
	 * 
	 * @return Set of types
	 */
	public IFuture<Set<String>> getActiveDiscoveryMechanisms()
	{
		return component.scheduleStep(new IComponentStep<Set<String>>()
		{
			@Classname("getDiscoveryMechanisms")
			public IFuture<Set<String>> execute(IInternalAccess ia)
			{
				final Future<Set<String>> ret = new Future<Set<String>>();

				getChildrenAccesses(ia).addResultListener(ia.getComponentFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<Collection<IExternalAccess>, Set<String>>(ret)
				{
					public void customResultAvailable(Collection<IExternalAccess> result)
					{
						Set<String> res = new HashSet<String>();
						for(Iterator<IExternalAccess> it = result.iterator(); it.hasNext();)
						{
							IExternalAccess child = it.next();
							// System.out.println("child: "+child.getLocalType()+" "+child.getComponentIdentifier());
							res.add(child.getLocalType());
						}
						ret.setResult(res);
					}
				}));

				return ret;
			}
		});
	}

	/**
	 * Transfers new Settings to the Agent.
	 * 
	 * @param settings The new {@link AwarenessSettingsData}
	 * @return {@link Void}
	 */
	public IFuture<Void> setSettings(final AwarenessSettingsData settings)
	{
		return component.scheduleStep(new IComponentStep<Void>()
		{
			@Classname("applySettings")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				AwarenessManagementAgent agent = (AwarenessManagementAgent)ia;
				// agent.setAddressInfo(settings.address, settings.port);
				agent.setDelay(settings.delay);
				agent.setFastAwareness(settings.fast);
				agent.setAutoCreateProxy(settings.autocreate);
				agent.setAutoDeleteProxy(settings.autodelete);
				agent.setIncludes(settings.includes);
				agent.setExcludes(settings.excludes);
				return IFuture.DONE;
			}
		});
	}

	/**
	 * Creates or deletes a local proxy for a given remote component.
	 * 
	 * @param cid {@link BasicComponentIdentifier} of the component to create/delete
	 *        the proxy for
	 * @param create true if proxy should be created, false if it should be
	 *        deleted
	 * @return Void
	 */
	public IFuture<Void> createOrDeleteProxy(final IComponentIdentifier cid, final boolean create)
	{
		return component.scheduleStep(new IComponentStep<Void>()
		{
			@Classname("createDeleteProxy")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				IFuture<Void> ret;
				AwarenessManagementAgent agent = (AwarenessManagementAgent)ia;
				DiscoveryInfo dif = agent.getDiscoveryInfo(cid);
				if(create && dif != null)
				{
					final Future<Void> fut = new Future<Void>();
					ret = fut;
					agent.createProxy(dif).addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, Void>(fut)
					{
						public void customResultAvailable(IComponentIdentifier result)
						{
							fut.setResult(null);
						}

						public void exceptionOccurred(Exception exception)
						{
							System.out.println("hhhuuu: " + exception);
							super.exceptionOccurred(exception);
						}
					});
				}
				else if(dif != null)
				{
					ret = agent.deleteProxy(dif);
				}
				else
				{
					ret = IFuture.DONE;
				}
				return ret;
				// return IFuture.DONE;
			}
		});
	}

	/**
	 * Get the children (if any).
	 * 
	 * @return The children.
	 */
	public static IFuture<Collection<IExternalAccess>> getChildrenAccesses(final IInternalAccess component)
	{
		final Future<Collection<IExternalAccess>> ret = new Future<Collection<IExternalAccess>>();

		SServiceProvider.getService(component, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(
			new ExceptionDelegationResultListener<IComponentManagementService, Collection<IExternalAccess>>(ret)
			{
				public void customResultAvailable(IComponentManagementService result)
				{
					final IComponentManagementService cms = (IComponentManagementService)result;

					cms.getChildren(component.getComponentIdentifier()).addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier[], Collection<IExternalAccess>>(ret)
					{
						public void customResultAvailable(IComponentIdentifier[] children)
						{
							IResultListener<IExternalAccess> crl = new CollectionResultListener<IExternalAccess>(children.length, true, new DelegationResultListener<Collection<IExternalAccess>>(ret));
							for(int i = 0; !ret.isDone() && i < children.length; i++)
							{
								cms.getExternalAccess(children[i]).addResultListener(crl);
							}
						}
					});
				}
			});

		return ret;
	}
}
