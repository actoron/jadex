package jadex.tools.common;

import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IMessageService;
import jadex.commons.concurrent.SwingDefaultResultListener;
import jadex.service.IServiceProvider;
import jadex.service.SServiceProvider;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

/**
 *  A panel for displaying/editing a component identifier.
 */
public class ComponentIdentifierPanel extends JPanel
{
	//-------- attributes --------

//	/** The component management service.*/
//	protected IComponentManagementService cms;
	
	/** The service provider. */
	protected IServiceProvider provider;
	
	/** The component identifier.*/
	protected IComponentIdentifier cid;

	/** The name textfield. */
	protected JTextField tfname; 

	/** Listener for name updates. */
	protected DocumentListener namelistener;	
	
	/** Flag indicating that the user is currently editing the name. */
	protected boolean nameediting;	
	
	/** The addresses table. */
	protected EditableList taddresses;

	/** The editable state. */
	protected boolean editable;	
	
	/** The list of local transport address schemes. */
	protected DefaultTableModel schemes;
	
	//-------- constructors --------

	/**
	 *  Create a new component identifier panel.
	 *  @param cid The component identifier (or null for new).
	 */
	public ComponentIdentifierPanel(IComponentIdentifier cid, final IServiceProvider provider)
	{
		this.provider = provider;
		this.cid = cid!=null? cid: new ComponentIdentifier();//cms.createComponentIdentifier(null, false, null);
		this.editable	= true;

		// Initialize component.
		JPanel content = new JPanel();
		content.setLayout(new GridBagLayout());

		// Name
		tfname	= new JTextField(this.cid.getName(), 20);
		this.namelistener	= new NameListener();
		tfname.getDocument().addDocumentListener(namelistener);
		content.add(new JLabel("Name: "), new GridBagConstraints(0, 0, 1, 1, 0, 0,
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(1,1,1,1), 0, 0));
		content.add(tfname, new GridBagConstraints(1, 0, 1, 1, 1, 0,
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(1,1,1,1), 0, 0));

		taddresses = new EditableList("Addresses");
		taddresses.getModel().addTableModelListener(new TableModelListener()
		{
			public void tableChanged(TableModelEvent e)
			{
				//System.out.println("event: "+e);
				SServiceProvider.getService(provider, IComponentManagementService.class).addResultListener(new SwingDefaultResultListener()
				{
					public void customResultAvailable(Object source, Object result)
					{
						IComponentManagementService cms = (IComponentManagementService)result;
						ComponentIdentifierPanel.this.cid = cms.createComponentIdentifier(ComponentIdentifierPanel.this.cid.getName(), false, taddresses.getEntries());
						cidChanged();
					}
				});
			}
		});
		
		JScrollPane	scroll	= new JScrollPane(taddresses);
		scroll.setPreferredSize(new Dimension(400, 200));
		content.add(scroll, new GridBagConstraints(0, 1, 2, 1, 1, 1,
			GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(1,1,1,1), 0, 0));
//		content.add(new JButton("a"));
		
		schemes = new DefaultTableModel(new String[]{"Local transport address schemes"}, 0);
		JTable adr  = new JTable(schemes);
		
//		JScrollPane scp = new JScrollPane(adr);

//		JPanel valp = new JPanel(new BorderLayout());
//		valp.add(new JLabel("Validate address with local cms: ", JLabel.RIGHT), BorderLayout.CENTER);
//		JButton val = new JButton("Validate");
//		val.addActionListener(new ActionListener()
//		{
//			public void actionPerformed(ActionEvent e)
//			{
//				validate();
//			}
//		});
//		valp.add(val, BorderLayout.EAST);
		
		JPanel help = new JPanel(new BorderLayout());
//		help.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), " Local transport address schemes "));
		help.add(adr.getTableHeader(), BorderLayout.NORTH);
		help.add(adr, BorderLayout.CENTER);
//		help.add(valp, BorderLayout.SOUTH);
		
//		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
//		split.add(content);
//		split.add(help);
//		split.setOneTouchExpandable(true);
		
		setLayout(new BorderLayout());
//		add(split, BorderLayout.CENTER);
		add(content, BorderLayout.CENTER);
		add(help, BorderLayout.SOUTH);
		
		SServiceProvider.getService(provider, IMessageService.class).addResultListener(new SwingDefaultResultListener()
		{
			public void customResultAvailable(Object source, Object result)
			{
				IMessageService ms = (IMessageService)result;
				String[] ss = ms.getAddressSchemes();
				for(int i=0; i<ss.length; i++)
				{
					schemes.addRow(new Object[]{ss[i]});
				}
			}
		});
	}

	/**
	 *  Template method to be overriden by subclasses.
	 *  Called when the CID has been changed through user input.
	 */
	protected void cidChanged()
	{
	}

	//-------- methods --------

	/**
	 *  Get the agent identifier.
	 */
	public IComponentIdentifier	getAgentIdentifier()
	{
		return this.cid;
	}

	/**
	 *  Set the component identifier.
	 */
	public void setComponentIdentifier(IComponentIdentifier cid)
	{
		this.cid	= cid!=null? cid: new ComponentIdentifier(); 
//		this.cid	= aid!=null ? aid : cms.createComponentIdentifier(null, false, null);
		refresh();
	}

	/**
	 *  Change the editable state.
	 */
	public void	setEditable(boolean editable)
	{
		if(this.editable!=editable)
		{
			this.editable	= editable;
			tfname.setEditable(editable);
			refresh();
		}
	}
	
	//-------- helper methods --------
	
	/**
	 *  Update the ui, when the aid has changed.
	 */
	protected void refresh()
	{
		// Update the gui.
		if(!nameediting)
		{
//			tfname.getDocument().removeDocumentListener(namelistener);
			tfname.setText(this.cid.getName());
//			tfname.getDocument().addDocumentListener(namelistener);
		}
		
		taddresses.setEntries(this.cid.getAddresses());
		taddresses.refresh();
		this.invalidate();
		this.validate();
		this.repaint();
	}

	//-------- helper classes --------

	public class NameListener implements DocumentListener
	{
		public void changedUpdate(DocumentEvent e)
		{
			update();
		}

		public void insertUpdate(DocumentEvent e)
		{
			update();
		}

		public void removeUpdate(DocumentEvent e)
		{
			update();
		}
		
		protected void	update()
		{
			SServiceProvider.getService(provider, IComponentManagementService.class).addResultListener(new SwingDefaultResultListener()
			{
				public void customResultAvailable(Object source, Object result)
				{
					IComponentManagementService cms = (IComponentManagementService)result;
					nameediting	= true;
					ComponentIdentifierPanel.this.cid	= cms.createComponentIdentifier(tfname.getText(), false, cid.getAddresses());
					cidChanged();
					nameediting	= false;
				}
			});
		}
	}
	
}
