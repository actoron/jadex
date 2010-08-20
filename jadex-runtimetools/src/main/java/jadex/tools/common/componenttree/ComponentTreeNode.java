package jadex.tools.common.componenttree;

import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.concurrent.SwingDefaultResultListener;
import jadex.service.IService;
import jadex.service.SServiceProvider;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

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
	protected PropertiesComponent	propcomp;
		
	//-------- constructors --------
	
	/**
	 *  Create a new service container node.
	 */
	public ComponentTreeNode(IComponentTreeNode parent, ComponentTreeModel model, IComponentDescription desc,
		IComponentManagementService cms, Component ui, ComponentIconCache iconcache)
	{
		super(parent, model);
		this.desc	= desc;
		this.cms	= cms;
		this.ui	= ui;
		this.iconcache	= iconcache;
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
	public void refresh(boolean recurse)
	{
		cms.getComponentDescription(desc.getName()).addResultListener(new SwingDefaultResultListener(ui)
		{
			public void customResultAvailable(Object source, Object result)
			{
				ComponentTreeNode.this.desc	= (IComponentDescription)result;
				getModel().fireNodeChanged(ComponentTreeNode.this);
			}
		});

		super.refresh(recurse);
	}
	
	/**
	 *  Asynchronously search for children.
	 *  Called once for each node.
	 *  Should call setChildren() once children are found.
	 */
	protected void	searchChildren()
	{
		final List	children	= new ArrayList();
		final boolean	ready[]	= new boolean[2];	// 0: children, 1: services;
		final Future	future	= new Future();	// future for determining when services can be added to service container.

		cms.getChildren(desc.getName()).addResultListener(new SwingDefaultResultListener(ui)
		{
			public void customResultAvailable(Object source, Object result)
			{
				final IComponentIdentifier[] achildren = (IComponentIdentifier[])result;
				if(achildren!=null && achildren.length > 0)
				{
					for(int i=0; i<achildren.length; i++)
					{
						final int index = i;
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
											children.add(result);
											
											// Last child? -> inform listeners
											if(index == achildren.length - 1)
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
										}
									});
								}
								else
								{
									children.add(node);
			
									// Last child? -> inform listeners
									if(index == achildren.length - 1)
									{
										ready[0]	= true;
										if(ready[0] &&  ready[1])
										{
											setChildren(children).addResultListener(new DelegationResultListener(future));
										}
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
		cms.getExternalAccess(desc.getName()).addResultListener(new SwingDefaultResultListener(ui)
		{
			public void customResultAvailable(Object source, Object result)
			{
				IExternalAccess	ea	= (IExternalAccess)result;
				SServiceProvider.getDeclaredServices(ea.getServiceProvider()).addResultListener(new SwingDefaultResultListener(ui)
				{
					public void customResultAvailable(Object source, Object result)
					{
						List	services	= (List)result;
						if(services!=null && !services.isEmpty())
						{
							ServiceContainerNode	scn	= (ServiceContainerNode)getModel().getNode(desc.getName().getName()+"ServiceContainer");
							if(scn==null)
								scn	= new ServiceContainerNode(ComponentTreeNode.this, getModel());
							children.add(0, scn);
							
							final List	subchildren	= new ArrayList();
							for(int i=0; i<services.size(); i++)
							{
								IService service	= (IService)services.get(i);
								ServiceNode	sn	= (ServiceNode)getModel().getNode(service.getServiceIdentifier());
								if(sn==null)
									sn	= new ServiceNode(scn, getModel(), service);
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
				IExternalAccess exta = (IExternalAccess)result;
				boolean proxy = "jadex.base.service.remote.Proxy".equals(exta.getModel().getFullName());
				IComponentTreeNode node;
				if(proxy)
				{
					node = new ProxyComponentTreeNode(ComponentTreeNode.this, getModel(), desc, cms, ui, iconcache);
				}
				else
				{
					node = new ComponentTreeNode(ComponentTreeNode.this, getModel(), desc, cms, ui, iconcache);
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
			propcomp	= new PropertiesComponent();
		propcomp.setDescription(desc);
		return propcomp;
	}
	
	//-------- helper classes --------
	
	/**
	 *  Panel for showing component properties.
	 */
	public static class PropertiesComponent	extends	JPanel
	{
		//-------- attributes --------
		
		/** The name textfield. */
		protected JTextField	tfname;
		
		/** The type textfield. */
		protected JTextField	tftype;
		
		/** The ownership textfield. */
		protected JTextField	tfownership;
		
		/** The state textfield. */
		protected JTextField	tfstate;
		
		/** The processingstate textfield. */
		protected JTextField	tfprocstate;
		
		/** The master checkbox. */
		protected JCheckBox	cbmaster;
		
		/** The daemon checkbox. */
		protected JCheckBox	cbdaemon;
		
		/** The autoshutdown checkbox. */
		protected JCheckBox	cbautoshutdown;
		
		//-------- constructors --------
		
		/**
		 *  Create new component proeprties panel.
		 */
		public PropertiesComponent()
		{
			super(new GridBagLayout());
			setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), " Component properties "));

			GridBagConstraints	gbc	= new GridBagConstraints();
			gbc.gridy	= 0;
			gbc.anchor	= GridBagConstraints.NORTHWEST;
			gbc.fill	= GridBagConstraints.HORIZONTAL;
			gbc.insets	= new Insets(1,1,1,1);

			tfname	= createTextField("Name", gbc);
			tftype	= createTextField("Type", gbc);
			tfownership	= createTextField("Ownership", gbc);
			tfstate	= createTextField("State", gbc);
			tfprocstate	= createTextField("Processing state", gbc);
			
			cbmaster	= createCheckBox("Master", gbc);
			cbdaemon	= createCheckBox("Daemon", gbc);

			// Last component gets remaining space
			gbc.weighty	= 1.0;

			cbautoshutdown	= createCheckBox("Auto shutdown", gbc);
		}
		
		//-------- methods --------
		
		/**
		 *  Set the description.
		 */
		public void	setDescription(IComponentDescription desc)
		{
			tfname.setText(desc.getName().getName());
			tftype.setText(desc.getType());
			tfownership.setText(desc.getOwnership());
			tfstate.setText(desc.getState());
			tfprocstate.setText(desc.getProcessingState());
			cbmaster.setSelected(desc.isMaster());
			cbdaemon.setSelected(desc.isDaemon());
			cbautoshutdown.setSelected(desc.isAutoShutdown());
		}

		//-------- helper methods --------
		
		/**
		 *  Create a text field and add it to the panel.
		 */
		protected JTextField	createTextField(String name, GridBagConstraints gbc)
		{
			gbc.weightx	= 0;
			add(new JLabel(name), gbc);
			JTextField	tf	= new JTextField();
			tf.setEditable(false);
			gbc.weightx	= 1;
			add(tf, gbc);
			gbc.gridy++;
			return tf;
		}
		
		/**
		 *  Create a check box and add it to the panel.
		 */
		protected JCheckBox	createCheckBox(String name, GridBagConstraints gbc)
		{
			gbc.weightx	= 0;
			add(new JLabel(name), gbc);
			JCheckBox	cb	= new JCheckBox("");
			cb.setMargin(new Insets(0,0,0,0));
			cb.setEnabled(false);
			gbc.weightx	= 1;
			add(cb, gbc);
			gbc.gridy++;
			return cb;
		}
	}
}
