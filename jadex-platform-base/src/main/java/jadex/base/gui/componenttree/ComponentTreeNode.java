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

import java.awt.Component;
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
		
	/** The UI component used for displaying error messages. */
	// Todo: status bar for longer lasting actions?
	protected final Component	ui;
		
	/** The icon cache. */
	protected final ComponentIconCache	iconcache;
		
	/** The properties component (if any). */
	protected ComponentProperties	propcomp;
		
	//-------- constructors --------
	
	/**
	 *  Create a new service container node.
	 */
	public ComponentTreeNode(IComponentTreeNode parent, ComponentTreeModel model, JTree tree, IComponentDescription desc,
		IComponentManagementService cms, Component ui, ComponentIconCache iconcache)
	{
		super(parent, model, tree);
		this.desc	= desc;
		this.cms	= cms;
		this.ui	= ui;
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
		cms.getComponentDescription(desc.getName()).addResultListener(new SwingDefaultResultListener(ui)
		{
			public void customResultAvailable(Object source, Object result)
			{
				ComponentTreeNode.this.desc	= (IComponentDescription)result;
				getModel().fireNodeChanged(ComponentTreeNode.this);
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

		cms.getChildren(desc.getName()).addResultListener(new SwingDefaultResultListener(ui)
		{
			public void customResultAvailable(Object source, Object result)
			{
				final IComponentIdentifier[] achildren = (IComponentIdentifier[])result;
				final int[]	childcnt	= new int[]{0};
				if(achildren!=null && achildren.length > 0)
				{
					for(int i=0; i<achildren.length; i++)
					{
						cms.getComponentDescription(achildren[i]).addResultListener(new SwingDefaultResultListener(ui)
						{
							public void customResultAvailable(Object source, Object result)
							{
								IComponentDescription	desc	= (IComponentDescription)result;
								IComponentTreeNode	node	= getModel().getNode(desc.getName());
								if(node==null)
								{
									createComponentNode(desc).addResultListener(new SwingDefaultResultListener(ui)
									{
										public void customResultAvailable(Object source, Object result)
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
										
										public void customExceptionOccurred(Object source, Exception exception)
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
							public void customExceptionOccurred(Object source, Exception exception)
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
		});
		
		
		// Search services and only add container node when services are found.
		cms.getExternalAccess(desc.getName()).addResultListener(new SwingDefaultResultListener((Component)null)
		{
			public void customResultAvailable(Object source, Object result)
			{
				final IExternalAccess	ea	= (IExternalAccess)result;
				SServiceProvider.getDeclaredServices(ea.getServiceProvider()).addResultListener(new SwingDefaultResultListener(ui)
				{
					public void customResultAvailable(Object source, Object result)
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
							future.addResultListener(new SwingDefaultResultListener(ui)
							{
								public void customResultAvailable(Object source, Object result)
								{
									node.setChildren(subchildren);
								}
							});
						}

						ready[1]	= true;
						if(ready[0] &&  ready[1])
						{
							setChildren(children).addResultListener(new DelegationResultListener(future));
						}
					}
				});
			}

			public void customExceptionOccurred(Object source, Exception exception)
			{
				// May happen, when components already removed.
			}
		});

	}
	
	//-------- methods --------
	
	/**
	 *  Get the UI for displaying errors.
	 */
	protected Component	getUI()
	{
		return ui;
	}
	
	/**
	 *  Create a new component node.
	 */
	public IFuture createComponentNode(final IComponentDescription desc)
	{
		final Future ret = new Future();
		
		cms.getExternalAccess(desc.getName()).addResultListener(new SwingDefaultResultListener(ui)
		{
			public void customResultAvailable(Object source, Object result)
			{
				IComponentTreeNode node	= getModel().getNode(desc.getName());
				if(node==null)
				{
					IExternalAccess exta = (IExternalAccess)result;
					boolean proxy = "jadex.base.service.remote.Proxy".equals(exta.getModel().getFullName());
					if(proxy)
					{
						node = new ProxyComponentTreeNode(ComponentTreeNode.this, getModel(), getTree(), desc, cms, ui, iconcache);
					}
					else
					{
						node = new ComponentTreeNode(ComponentTreeNode.this, getModel(), getTree(), desc, cms, ui, iconcache);
					}
				}
				ret.setResult(node);
			}
			
			public void customExceptionOccurred(Object source, Exception exception)
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
