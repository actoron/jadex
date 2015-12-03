package jadex.tools.security;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import jadex.commons.ICommand;
import jadex.commons.SUtil;

/**
 * 
 */
public class PasswordTablePanel extends JPanel
{
	/** The remote passwords. */
	protected DefaultTableModel	tmodel;
	
	/**
	 *  Create a new password panel.
	 */
	public PasswordTablePanel(String name, String[] colnames, final ICommand addaction, final ICommand remaction)
	{
		JPanel	premote	= new JPanel(new BorderLayout());
		premote.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), name));
		tmodel = new DefaultTableModel(colnames, 0)
		{
			public boolean isCellEditable(int row, int column) 
			{
				return column==1;
			};
		};
		final JTable table = new JTable(tmodel);
		premote.add(new JScrollPane(table), BorderLayout.CENTER);
		
		final JPopupMenu popup = new JPopupMenu();
		if(remaction!=null)
		{
			popup.add(new JMenuItem(new AbstractAction("Remove entry") 
			{
				public void actionPerformed(ActionEvent e)
				{
					int sel = table.getSelectedRow();
					if(sel!=-1)
					{
						remaction.execute(new String[]{(String)tmodel.getValueAt(sel, 0), null});
//						tmodel.removeRow(sel);
					}
				}
			}));
		}
		
		final JTextField tfname = new JTextField();
	    final JTextField tfpass = new JTextField();
	    JButton buadd = new JButton("Add");
	    buadd.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(addaction!=null)
				{
					addaction.execute(new String[]{tfname.getText(), tfpass.getText()});
				}
			}
		});
		
	    table.addMouseListener(new MouseAdapter()
        {
	    	public void mousePressed(MouseEvent e)
	        {
	        	mouseClicked(e);
	        }
	        
	    	public void mouseReleased(MouseEvent e)
	        {
	        	mouseClicked(e);
	        }
	        
	    	public void mouseClicked(MouseEvent e)
	    	{
	    		if(e.isPopupTrigger())
	            {
	    			popup.show(e.getComponent(), e.getX(), e.getY());
	            }
	    		else
	    		{
	    			int sel = table.getSelectedRow();
	    			if(sel!=-1)
	    			{
	    				tfname.setText((String)tmodel.getValueAt(sel, 0));
	    				tfpass.setText((String)tmodel.getValueAt(sel, 1));
	    			}
	    		}
	    	}
        });
	    
	    tmodel.addTableModelListener(new TableModelListener()
		{
			public void tableChanged(TableModelEvent e)
			{
//				System.out.println("e: "+e.getType());
				if(e.getType()==e.UPDATE && addaction!=null)
				{
//					System.out.println("update");
					addaction.execute(new String[]{(String)tmodel.getValueAt(e.getFirstRow(), 0), 
						(String)tmodel.getValueAt(e.getFirstRow(), 1)});
					tfpass.setText((String)tmodel.getValueAt(e.getFirstRow(), 1));
				}
			}
		});
	    
	    JPanel padd = new JPanel(new GridBagLayout());
	    padd.add(new JLabel(colnames[0]), new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.EAST, 
	    	GridBagConstraints.BOTH, new Insets(2,2,2,2), 0, 0));
	    padd.add(tfname, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.EAST, 
		    GridBagConstraints.BOTH, new Insets(2,2,2,2), 0, 0));
	    padd.add(new JLabel(colnames[1]), new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.EAST, 
		    GridBagConstraints.BOTH, new Insets(2,2,2,2), 0, 0));
	    padd.add(tfpass, new GridBagConstraints(3, 0, 1, 1, 1, 0, GridBagConstraints.EAST, 
		    GridBagConstraints.BOTH, new Insets(2,2,2,2), 0, 0));
	    padd.add(buadd, new GridBagConstraints(4, 0, 1, 1, 0, 0, GridBagConstraints.EAST, 
	    	GridBagConstraints.BOTH, new Insets(2,2,2,2), 0, 0));
	    
	    this.setLayout(new BorderLayout());
	    this.add(premote, BorderLayout.CENTER);
	    this.add(padd, BorderLayout.SOUTH);
	}
	
	/**
	 *  Update the passwords.
	 */
	public void update(Map<String, String> passwords)
	{
		// Update table in place to avoid GUI flickering.
		for(int i=tmodel.getRowCount()-1; i>=0; i--)
		{
			// Update existing value.
			String key = (String)tmodel.getValueAt(i, 0);
			if(passwords.containsKey(key))
			{
				String	newval	= passwords.remove(tmodel.getValueAt(i, 0));
				if(!SUtil.equals(tmodel.getValueAt(i, 1), newval))
				{
//					System.out.println("set: "+newval);
					tmodel.setValueAt(newval, i, 1);
				}
			}
			
			// Remove non-existing values.
			else
			{
				tmodel.removeRow(i);
			}
		}
		
		// Add new values.
		for(Iterator<String> it=passwords.keySet().iterator(); it.hasNext(); )
		{
			String	key	= it.next();
			tmodel.addRow(new Object[]{key, passwords.get(key)});
		}
	}
}
