package jadex.base.service.awareness;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *  Panel that allows editing a list of strings.
 */
public class ListPanel	extends JPanel
{
	//-------- attributes  --------
	
	/** The list model. */
	protected DefaultListModel	lm;
	
	/** The list. */
	protected JList	list;
	
	/** The text field. */
	protected JTextField	text;
	
	/** The add button. */
	protected JButton	add;

	/** The remove button. */
	protected JButton	remove;
	
	//-------- constructors --------
	
	public ListPanel(String title)
	{
		super(new GridBagLayout());
		
		this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), title));
		
		GridBagConstraints	gbc	= new GridBagConstraints();
		gbc.fill	= GridBagConstraints.BOTH;
		gbc.insets	= new Insets(1, 1, 1, 1);
		gbc.gridwidth	= GridBagConstraints.REMAINDER;
		gbc.weightx	= 1;
		gbc.weighty	= 1;
		
		this.lm	= new DefaultListModel();
		
		this.list	= new JList(lm);
		this.add(new JScrollPane(list), gbc);
		
		this.text	= new JTextField(10);
		text.setToolTipText("Enter start of platform or host name or IP address.");
		gbc.weighty	= 0;
		gbc.fill	= GridBagConstraints.HORIZONTAL;
		gbc.gridy	= 1;
		gbc.gridx	= GridBagConstraints.RELATIVE;
		gbc.gridwidth	= 1;
		this.add(text, gbc);
		
		this.add	= new JButton("Add");
		add.setToolTipText("Add entered value to list.");
		gbc.weightx	= 0;
		gbc.fill	= GridBagConstraints.NONE;
		gbc.insets	= new Insets(1, 4, 1, 1);	// Hack to resemble flow layout gap!
		this.add(add, gbc);
		
		this.remove	= new JButton("Remove");
		remove.setToolTipText("Remove selected values from list.");
		this.add(remove, gbc);

		add.setMinimumSize(remove.getMinimumSize());
		add.setPreferredSize(remove.getPreferredSize());
		
		add.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String	s	= text.getText().trim();
				if(s!=null)
				{
					lm.addElement(s);
				}
			}
		});
		remove.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Object[]	cs	= list.getSelectedValues();
				for(int i=0; i<cs.length; i++)
				{
					lm.removeElement(cs[i]);
				}
			}
		});
		
		lm.addListDataListener(new ListDataListener()
		{
			public void intervalRemoved(ListDataEvent e)
			{
				updateButtonState();
			}
			public void intervalAdded(ListDataEvent e)
			{
				updateButtonState();
			}
			public void contentsChanged(ListDataEvent e)
			{
				updateButtonState();
			}
		});
		text.addKeyListener(new KeyListener()
		{
			public void keyTyped(KeyEvent e)
			{
				updateButtonState();
			}
			public void keyReleased(KeyEvent e)
			{
				updateButtonState();
			}
			public void keyPressed(KeyEvent e)
			{
				updateButtonState();
			}
		});
		list.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				remove.setEnabled(!list.isSelectionEmpty());
			}
		});
		
		updateButtonState();
	}

	protected void updateButtonState()
	{
		add.setEnabled(!"".equals(text.getText().trim()) &&	!lm.contains(text.getText().trim()));
		remove.setEnabled(!list.isSelectionEmpty());
	}
	
	//-------- methods --------
	
	/**
	 *  Get the current list contents.
	 */
	public List	getEntries()
	{
		ArrayList	ret	= new ArrayList();
		for(int i=0; i<lm.size(); i++)
		{
			ret.add(lm.getElementAt(i));
		}
		return ret;
	}
	
	/**
	 *  Set the current list contents.
	 */
	public void	setEntries(List entries)
	{
		lm.removeAllElements();
		for(int i=0; i<entries.size(); i++)
		{
			lm.addElement(entries.get(i));
		}
	}
}

