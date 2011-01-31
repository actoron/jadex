package jadex.base.gui.componenttree;

import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.concurrent.SwingDefaultResultListener;
import jadex.commons.service.IService;
import jadex.commons.service.IServiceContainer;
import jadex.commons.service.SServiceProvider;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTree;

/**
 *  Node object representing a service container.
 */
public class ComponentTreeNode	extends AbstractComponentTreeNode implements IActiveComponentTreeNode
{
	//-------- attributes --------
	
	/** The component description. */
	protected IComponentDescription	desc;
		
	/** The component management service. */
	protected final IComponentManagementService	cms;
		
	/** The icon cache. */
	protected final ComponentIconCache	iconcache;
		
	/** The properties component (if any). */
	protected ComponentProperties	propcomp;
		
	//-------- constructors --------
	
	/**
	 *  Create a new service container node.
	 */
	public ComponentTreeNode(IComponentTreeNode parent, ComponentTreeModel model, JTree tree, IComponentDescription desc,
		IComponentManagementService cms, ComponentIconCache iconcache)
	{
		super(parent, model, tree);
		
		assert desc!=null;
		
//		System.out.println("node: "+getClass()+" "+desc.getName());
		
		this.desc	= desc;
		this.cms	= cms;
		this.iconcache	= iconcache;
		
		model.registerNode(this);
	}
	
	//-------- AbstractComponentTreeNode methods --------
	
	/**
	 *  Get the id used for lookup.
	 */
	public Object	getId()
	{
		return desc.getName();
	}

	/**
	 *  Get the icon for a node.
	 */
	public Icon	getIcon()
	{
		return iconcache.getIcon(this, desc.getType());
	}
	
	/**
	 *  Refresh the node.
	 *  @param recurse	Recursively refresh subnodes, if true.
	 */
	public void refresh(boolean recurse, boolean force)
	{
		cms.getComponentDescription(desc.getName()).addResultListener(new SwingDefaultResultListener()
		{
			public void customResultAvailable(Object result)
			{
				ComponentTreeNode.this.desc	= (IComponentDescription)result;
				getModel().fireNodeChanged(ComponentTreeNode.this);
			}
			public void customExceptionOccurred(Exception exception)
			{
				// ignore
			}
		});

		super.refresh(recurse, force);
	}
	
	/**
	 *  Asynchronously search for children.
	 *  Should call setChildren() once children are found.
	 */
	protected void	searchChildren(boolean force)
	{
		final List	children	= new ArrayList();
		final boolean	ready[]	= new boolean[2];	// 0: children, 1: services;
		final Future	future	= new Future();	// future for determining when services can be added to service container.

		cms.getChildren(desc.getName())
			.addResultListener(new SwingDefaultResultListener()
		{
			public void customResultAvailable(Object result)
			{
				final IComponentIdentifier[] achildren = (IComponentIdentifier[])result;
				final int[]	childcnt	= new int[]{0};
				if(achildren!=null && achildren.length > 0)
				{
					for(int i=0; i<achildren.length; i++)
					{
						cms.getComponentDescription(achildren[i]).addResultListener(new SwingDefaultResultListener()
						{
							public void customResultAvailable(Object result)
							{
								IComponentDescription	desc	= (IComponentDescription)result;
								IComponentTreeNode	node	= getModel().getNode(desc.getName());
								if(node==null)
								{
									createComponentNode(desc).addResultListener(new SwingDefaultResultListener()
									{
										public void customResultAvailable(Object result)
										{
//											System.err.println(getModel().hashCode()+", "+ready.hashCode()+" searchChildren.add "+result);
											children.add(result);
											childcnt[0]++;
											
											// Last child? -> inform listeners
											if(childcnt[0] == achildren.length)
											{
												ready[0]	= true;
												if(ready[0] &&  ready[1])
												{
													setChildren(children).addResultListener(new DelegationResultListener(future));
												}
											}
										}
										
										public void customExceptionOccurred(Exception exception)
										{
											// May happen, when component removed in mean time.
											childcnt[0]++;
											
											// Last child? -> inform listeners
											if(childcnt[0] == achildren.length)
											{
												ready[0]	= true;
												if(ready[0] &&  ready[1])
												{
													setChildren(children).addResultListener(new DelegationResultListener(future));
												}
											}
										}
									});
								}
								else
								{
//									System.err.println(getModel().hashCode()+", "+ready.hashCode()+" searchChildren.add "+node);
									children.add(node);
									childcnt[0]++;
			
									// Last child? -> inform listeners
									if(childcnt[0] == achildren.length)
									{
										ready[0]	= true;
										if(ready[0] &&  ready[1])
										{
											setChildren(children).addResultListener(new DelegationResultListener(future));
										}
									}
								}
							}
							public void customExceptionOccurred(Exception exception)
							{
								childcnt[0]++;
								
								// Last child? -> inform listeners
								if(childcnt[0] == achildren.length)
								{
									ready[0]	= true;
									if(ready[0] &&  ready[1])
									{
										setChildren(children).addResultListener(new DelegationResultListener(future));
									}
								}
							}
						});
					}
				}
				else
				{
					ready[0]	= true;
					if(ready[0] &&  ready[1])
					{
						setChildren(children).addResultListener(new DelegationResultListener(future));
					}
				}
			}
			public void customExceptionOccurred(Exception exception)
			{
				System.out.println("here1: "+exception);
				// ignore
			}
		});
		
		
//		// Search services and only add container node when services are found.
//		cms.getExternalAccess(desc.getName())
//			.addResultListener(new SwingDefaultResultListener()
//		{
//			public void customResultAvailable(Object result)
//			{
//				final IExternalAccess ea = (IExternalAccess)result;
//				ea.scheduleStep(new IComponentStep()
//				{
//					public Object execute(IInternalAccess ia)
//					{
//						Future ret = new Future();
//						SServiceProvider.getDeclaredServices(ia.getServiceProvider())
//							.addResultListener(new DelegationResultListener(ret));
//						return ret;
//					}
//				}).addResultListener(new SwingDefaultResultListener()
//				{
//					public void customResultAvailable(Object result)
//					{
//						List	services	= (List)result;
//						if(services!=null && !services.isEmpty())
//						{
//							ServiceContainerNode	scn	= (ServiceContainerNode)getModel().getNode(desc.getName().getName()+"ServiceContainer");
//							if(scn==null)
//								scn	= new ServiceContainerNode(ComponentTreeNode.this, getModel(), getTree(), (IServiceContainer)ea.getServiceProvider());
////							System.err.println(getModel().hashCode()+", "+ready.hashCode()+" searchChildren.add "+scn);
//							children.add(0, scn);
//							
//							final List	subchildren	= new ArrayList();
//							for(int i=0; i<services.size(); i++)
//							{
//								IService service	= (IService)services.get(i);
//								ServiceNode	sn	= (ServiceNode)getModel().getNode(service.getServiceIdentifier());
//								if(sn==null)
//									sn	= new ServiceNode(scn, getModel(), getTree(), service);
//								subchildren.add(sn);
//							}
//							
//							final ServiceContainerNode	node	= scn;
//							future.addResultListener(new SwingDefaultResultListener()
//							{
//								public void customResultAvailable(Object result)
//								{
//									node.setChildren(subchildren);
//								}
//								public void customExceptionOccurred(Exception exception)
//								{
//									// Shouldn't happen???
//								}
//							});
//						}
//
//						ready[1]	= true;
//						if(ready[0] &&  ready[1])
//						{
//							setChildren(children).addResultListener(new DelegationResultListener(future));
//						}
//					}
//					public void customExceptionOccurred(Exception exception)
//					{
//						ready[1]	= true;
//						if(ready[0] &&  ready[1])
//						{
//							setChildren(children).addResultListener(new DelegationResultListener(future));
//						}
//					}
//				});
//			}
//
//			public void customExceptionOccurred(Exception exception)
//			{
//				System.out.println("here2: "+exception);
//				// May happen, when components already removed.
//			}
//		});
		
		// Search services and only add container node when services are found.
//		System.out.println("name: "+desc.getName());
		cms.getExternalAccess(desc.getName())
			.addResultListener(new SwingDefaultResultListener()
		{
			public void customResultAvailable(Object result)
			{
				final IExternalAccess	ea	= (IExternalAccess)result;
				
				SServiceProvider.getDeclaredServices(ea.getServiceProvider())
					.addResultListener(new SwingDefaultResultListener()
				{
					public void customResultAvailable(Object result)
					{
						List	services	= (List)result;
						if(services!=null && !services.isEmpty())
						{
							ServiceContainerNode	scn	= (ServiceContainerNode)getModel().getNode(desc.getName().getName()+"ServiceContainer");
							if(scn==null)
								scn	= new ServiceContainerNode(ComponentTreeNode.this, getModel(), getTree(), (IServiceContainer)ea.getServiceProvider());
//							System.err.println(getModel().hashCode()+", "+ready.hashCode()+" searchChildren.add "+scn);
							children.add(0, scn);
							
							final List	subchildren	= new ArrayList();
							for(int i=0; i<services.size(); i++)
							{
								IService service	= (IService)services.get(i);
								ServiceNode	sn	= (ServiceNode)getModel().getNode(service.getServiceIdentifier());
								if(sn==null)
									sn	= new ServiceNode(scn, getModel(), getTree(), service);
								subchildren.add(sn);
							}
							
							final ServiceContainerNode	node	= scn;
							future.addResultListener(new SwingDefaultResultListener()
							{
								public void customResultAvailable(Object result)
								{
									node.setChildren(subchildren);
								}
								public void customExceptionOccurred(Exception exception)
								{
									// Shouldn't happen???
								}
							});
						}

						ready[1]	= true;
						if(ready[0] &&  ready[1])
						{
							setChildren(children).addResultListener(new DelegationResultListener(future));
						}
					}
					public void customExceptionOccurred(Exception exception)
					{
						ready[1]	= true;
						if(ready[0] &&  ready[1])
						{
							setChildren(children).addResultListener(new DelegationResultListener(future));
						}
					}
				});
			}

			public void customExceptionOccurred(Exception exception)
			{
				System.out.println("here2: "+exception);
				// May happen, when components already removed.
			}
		});

	}
	
	//-------- methods --------
	
	/**
	 *  Create a new component node.
	 */
	public IFuture createComponentNode(final IComponentDescription desc)
	{
		final Future ret = new Future();
		
		cms.getExternalAccess(desc.getName()).addResultListener(new SwingDefaultResultListener()
		{
			public void customResultAvailable(Object result)
			{
				IComponentTreeNode node	= getModel().getNode(desc.getName());
				if(node==null)
				{
					IExternalAccess exta = (IExternalAccess)result;
					boolean proxy = "jadex.base.service.remote.Proxy".equals(exta.getModel().getFullName());
					if(proxy)
					{
						node = new ProxyComponentTreeNode(ComponentTreeNode.this, getModel(), getTree(), desc, cms, iconcache);
					}
					else
					{
						node = new ComponentTreeNode(ComponentTreeNode.this, getModel(), getTree(), desc, cms, iconcache);
					}
				}
				ret.setResult(node);
			}
			
			public void customExceptionOccurred(Exception exception)
			{
				ret.setException(exception);
			}
		});
	
		return ret;
	}
	
	/**
	 *  Create a string representation.
	 */
	public String toString()
	{
		return desc.getName().getLocalName();
	}
	
	/**
	 *  Get the component description.
	 */
	public IComponentDescription	getDescription()
	{
		return desc;
	}
	
	/**
	 *  Get the component id.
	 */
	public IComponentIdentifier getComponentIdentifier()
	{
		return desc!=null? desc.getName(): null;
	}

	/**
	 *  Set the component description.
	 */
	public void setDescription(IComponentDescription desc)
	{
		this.desc	= desc;
		if(propcomp!=null)
		{
			propcomp.setDescription(desc);
			propcomp.repaint();
		}
	}

	/**
	 *  True, if the node has properties that can be displayed.
	 */
	public boolean	hasProperties()
	{
		return true;
	}

	
	/**
	 *  Get or create a component displaying the node properties.
	 *  Only to be called if hasProperties() is true;
	 */
	public JComponent	getPropertiesComponent()
	{
		if(propcomp==null)
		{
			propcomp	= new ComponentProperties();
		}
		propcomp.setDescription(desc);
		return propcomp;
	}
}
