package jadex.base.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import jadex.base.gui.componenttree.ComponentIconCache;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.ITransportComponentIdentifier;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.ICMSComponentListener;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.remote.IProxyAgentService;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.future.SwingIntermediateResultListener;
import jadex.commons.gui.future.SwingResultListener;

/**
 *  Dialog for selecting among known platforms.
 */
public class PlatformSelectorDialog extends ComponentSelectorDialog
{
	protected JList pllist;
	
	/** The value mappings. (from proxy cid -> remote cid. */
	protected Map<IComponentIdentifier, IComponentIdentifier> valmap;
	
	/** The registered cms listener. */
	protected ICMSComponentListener cmslistener;
	
	//-------- constructors --------

	/**
	 *  Create a new AgentSelectorDialog.
	 */
	public PlatformSelectorDialog(Component parent, IExternalAccess access, IExternalAccess jccaccess, CMSUpdateHandler cmshandler, 
		PropertyUpdateHandler prophandler, ComponentIconCache iconcache)
	{
		super(parent, access, jccaccess, cmshandler, prophandler, iconcache);
		this.valmap = new HashMap<IComponentIdentifier, IComponentIdentifier>();
	}
	
	/**
	 *  Create the tree view.
	 */
	protected JComponent createTreeView()
	{
		this.pllist = new JList(new DefaultListModel())
		{
			public Dimension getMinimumSize() 
			{
				Dimension ret = super.getMinimumSize();
				ret.width = ret.width<50? 150: ret.width;
				return ret;
			}
			
			public Dimension getPreferredSize() 
			{
				Dimension ret = super.getMinimumSize();
				ret.width = ret.width<50? 150: ret.width;
				return ret;
			}
		};
		pllist.setSelectionMode(singleselection? ListSelectionModel.SINGLE_SELECTION: ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		pllist.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				boolean	selectenabled = pllist.getSelectedValue()!=null;
				select.setEnabled(selectenabled);
			}
		});
		pllist.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				if(e.getClickCount()==2)
				{
					addSelected();
				}
			}
		});
		
		this.cmslistener = new ICMSComponentListener()
		{
			public IFuture<Void> componentRemoved(final IComponentDescription desc, Map<String, Object> results)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
//						System.out.println("removed: "+desc.getName()+" "+desc.getModelName());
						IComponentIdentifier cid = valmap.remove(desc.getName());
						if(cid!=null)
						{
							((DefaultListModel)pllist.getModel()).removeElement(cid);
						}
						else
						{
							System.out.println("Could not remove: "+desc.getName());
						}
					}
				});
				return IFuture.DONE;
			}
			
			public IFuture<Void> componentChanged(IComponentDescription desc)
			{
				return IFuture.DONE;
			}
			
			public IFuture<Void> componentAdded(final IComponentDescription desc)
			{
//				System.out.println("added: "+desc);
				
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						// Hack for speed
						if(desc.getModelName().equals("jadex.platform.service.remote.Proxy"))
						{
							SServiceProvider.getService(access, desc.getName(), IProxyAgentService.class)
								.addResultListener(new IResultListener<IProxyAgentService>()
							{
								public void resultAvailable(IProxyAgentService ser)
								{
									addPlatform(ser);
								}
								
								public void exceptionOccurred(Exception exception)
								{
								}
							});
						}
					}
				});
				
				return IFuture.DONE;
			}
		};
		
		cmshandler.addCMSListener(access.getComponentIdentifier().getRoot(), cmslistener);
		
		final Runnable action = new Runnable()
		{
			public void run()
			{
				((DefaultListModel)pllist.getModel()).removeAllElements();
				
				IComponentIdentifier self = access.getComponentIdentifier().getRoot();
				valmap.put(null, self);
				((DefaultListModel)pllist.getModel()).add(0, self);
				
				SServiceProvider.getServices(access, IProxyAgentService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(new SwingIntermediateResultListener<IProxyAgentService>(new IIntermediateResultListener<IProxyAgentService>()
				{
					public void intermediateResultAvailable(final IProxyAgentService ser)
					{
//						System.out.println("found: "+ser);
						addPlatform(ser);
					}
					
					public void finished()
					{
//						System.out.println("fini");
					}
					
					public void resultAvailable(Collection<IProxyAgentService> result)
					{
						if(result!=null)
						{
							for(IProxyAgentService ser: result)
							{
								intermediateResultAvailable(ser);
							}
						}
						finished();
					}
					
					public void exceptionOccurred(Exception exception)
					{
						exception.printStackTrace();
					}
				}));
			}
		};
		
		action.run();
		
		return new JScrollPane(pllist);
	}
	
	/**
	 * 
	 */
	protected void addPlatform(final IProxyAgentService ser)
	{
		ser.getRemoteComponentIdentifier().addResultListener(new SwingResultListener<ITransportComponentIdentifier>(new IResultListener<ITransportComponentIdentifier>()
		{
			public void resultAvailable(ITransportComponentIdentifier cid)
			{
				IComponentIdentifier key = ((IService)ser).getServiceIdentifier().getProviderId();
				if(!valmap.containsKey(key))
				{
					valmap.put(key, cid);
				
					DefaultListModel lm = (DefaultListModel)pllist.getModel();
					String name = cid.getName();
					boolean done = false;
					for(int i=0; i<lm.getSize() && !done; i++)
					{
						if(name.compareTo(((IComponentIdentifier)lm.get(i)).getName())<=0)
						{
							lm.add(i, cid);
							done = true;
						}
					}
					if(!done)
					{
						lm.add(lm.getSize(), cid);
					}
				}
			}
			
			public void exceptionOccurred(Exception exception)
			{
			}
		}));
	}
	
	/**
	 * 
	 */
	protected void disposeTreeView()
	{
		cmshandler.removeCMSListener(access.getComponentIdentifier().getRoot(), cmslistener);
		valmap.clear();
	}
	
	/**
	 * 
	 */
	protected boolean isTreeViewSelectionEmpty()
	{
		return pllist.getSelectedValue()==null;
	}
	
	/**
	 * 
	 */
	protected IComponentIdentifier getSelectedObject()
	{
		return (IComponentIdentifier)pllist.getSelectedValue();
	}
	
	/**
	 * 
	 */
	protected String getDialogName()
	{
		return "Select/Enter Platform Identifier";
	}
	
	/**
	 * 
	 */
	protected String getTreeViewName()
	{
		return " Known Platforms ";
	}
	
	/**
	 * 
	 */
	protected String getSelectedListName()
	{
		return " Selected Platforms ";
	}
}
