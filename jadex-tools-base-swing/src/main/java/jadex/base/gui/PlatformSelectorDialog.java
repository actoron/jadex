package jadex.base.gui;

import jadex.base.gui.componenttree.ComponentIconCache;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.ICMSComponentListener;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.remote.IProxyAgentService;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.future.SwingIntermediateResultListener;
import jadex.commons.gui.future.SwingResultListener;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * 
 */
public class PlatformSelectorDialog extends ComponentSelectorDialog
{
	protected JList pllist;
	
	/** The value mappings. (from proxy cid -> remote cid. */
	protected Map<IComponentIdentifier, IComponentIdentifier> valmap;
	
	//-------- constructors --------

	/**
	 *  Create a new AgentSelectorDialog.
	 */
	public PlatformSelectorDialog(Component parent, IExternalAccess access, CMSUpdateHandler cmshandler, ComponentIconCache iconcache)
	{
		super(parent, access, cmshandler, iconcache);
		this.valmap = new HashMap<IComponentIdentifier, IComponentIdentifier>();
	}
	
	/**
	 * 
	 */
	protected JComponent createTreeView()
	{
		this.pllist = new JList(new DefaultListModel());
//		list.setCellRenderer(new DefaultListCellRenderer()
//		{
//			public Component getListCellRendererComponent(final JList list, Object value,
//				int index, boolean isSelected, boolean cellHasFocus)
//			{
//				return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
//			}
//		});
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
		
		cmshandler.addCMSListener(access.getComponentIdentifier().getRoot(), new ICMSComponentListener()
		{
			public IFuture<Void> componentRemoved(final IComponentDescription desc, Map<String, Object> results)
			{
				System.out.println("removed: "+desc.getName()+" "+desc.getModelName());
				IComponentIdentifier cid = valmap.remove(desc.getName());
				if(cid!=null)
				{
					((DefaultListModel)pllist.getModel()).removeElement(cid);
				}
				return IFuture.DONE;
			}
			
			public IFuture<Void> componentChanged(IComponentDescription desc)
			{
				return IFuture.DONE;
			}
			
			public IFuture<Void> componentAdded(final IComponentDescription desc)
			{
				System.out.println("added: "+desc);
				// Hack for speed
				if(desc.getModelName().equals("jadex.platform.service.remote.Proxy"))
				{
					SServiceProvider.getService(access.getServiceProvider(), desc.getName(), IProxyAgentService.class)
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
				return IFuture.DONE;
			}
		});
		
		final Runnable action = new Runnable()
		{
			public void run()
			{
				((DefaultListModel)pllist.getModel()).removeAllElements();
				
				SServiceProvider.getServices(access.getServiceProvider(), IProxyAgentService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(new SwingIntermediateResultListener<IProxyAgentService>(new IIntermediateResultListener<IProxyAgentService>()
				{
					public void intermediateResultAvailable(final IProxyAgentService ser)
					{
//						System.out.println("found: "+result);
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
		
//		JButton bu = new JButton("Refresh");
//		bu.addActionListener(new ActionListener()
//		{
//			public void actionPerformed(ActionEvent e)
//			{
//				action.run();
//			}
//		});
		
//		JPanel p = new JPanel(new GridBagLayout());
//		p.add(pllist, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, 
//			GridBagConstraints.BOTH, new Insets(2,2,2,2), 0, 0));
//		p.add(bu, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.CENTER, 
//			GridBagConstraints.NONE, new Insets(2,2,2,2), 0, 0));
//		
		return new JScrollPane(pllist);
	}
	
	/**
	 * 
	 */
	protected void addPlatform(final IProxyAgentService ser)
	{
		ser.getRemoteComponentIdentifier().addResultListener(new SwingResultListener<IComponentIdentifier>(new IResultListener<IComponentIdentifier>()
		{
			public void resultAvailable(IComponentIdentifier cid)
			{
				valmap.put(((IService)ser).getServiceIdentifier().getProviderId(), cid);
				((DefaultListModel)pllist.getModel()).add(0, cid);
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
}
