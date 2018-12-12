package jadex.bdi.examples.hunterprey_classic.environment;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Image;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import jadex.bdi.examples.hunterprey_classic.Creature;
import jadex.bdi.examples.hunterprey_classic.Hunter;
import jadex.bdi.examples.hunterprey_classic.Observer;
import jadex.commons.SUtil;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.jtable.ObjectTableModel;
import jadex.commons.gui.jtable.ResizeableTableHeader;
import jadex.commons.gui.jtable.SorterFilterTableModel;
import jadex.commons.gui.jtable.VisibilityTableColumnModel;

/**
 *  Display an array of cretures in a table.
 */
public class CreaturePanel extends JPanel
{
	//-------- attributes --------

	/** The creatures. */
	protected Creature[] creatures;

	/** The table model. */
	protected ObjectTableModel tablemodel;

	/** The table. */
	protected JTable table;

	/** The table model. */
	protected ObjectTableModel tablemodel2;

	/** The table. */
	protected JTable table2;

	/** The hunter image. */
	protected Icon hunter;

	/** The prey image. */
	protected Icon prey;

	/** The observer image. */
	protected Icon observer;

	/** Flag to activate observer panel (only shows observers). */
	protected boolean	obs;

	//-------- constructors --------

	/**
	 *  Create a creature panel.
	 */
	public CreaturePanel()
	{
		this(false);
	}

	/**
	 *	Create a creatuire or observer panel.
	 */
	public CreaturePanel(boolean obs)
	{
		this.obs	= obs;
		Image hunter_image	= ((ImageIcon)MapPanel.icons.getIcon("hunter")).getImage();
		Image prey_image	= ((ImageIcon)MapPanel.icons.getIcon("prey")).getImage();
		Image observer_image	= ((ImageIcon)MapPanel.icons.getIcon("observer")).getImage();
		hunter = new ImageIcon(hunter_image.getScaledInstance(20, 20, Image.SCALE_DEFAULT));
		prey = new ImageIcon(prey_image.getScaledInstance(20, 20, Image.SCALE_DEFAULT));
		observer = new ImageIcon(observer_image.getScaledInstance(20, 20, Image.SCALE_DEFAULT));

		if(!obs)
			this.tablemodel = new ObjectTableModel(new String[]{"Rank", "Type", "Name", "Age", "Points"});
		else
			this.tablemodel = new ObjectTableModel(new String[]{"No.", "Type", "Name", "Age"});
		tablemodel.setColumnClass(Integer.class, 0);
		tablemodel.setColumnClass(Icon.class, 1);
		tablemodel.setColumnClass(Integer.class, 3);
		if(!obs)
			tablemodel.setColumnClass(Integer.class, 4);
		SorterFilterTableModel tm = new SorterFilterTableModel(tablemodel);

		VisibilityTableColumnModel colmodel = new VisibilityTableColumnModel();
        ResizeableTableHeader header = new ResizeableTableHeader(colmodel);
        header.setIncludeHeaderWidth(true);
 
		this.table = new JTable(tm, colmodel);
		table.setFont(new Font("Arial", Font.PLAIN, 14));

		table.setRowHeight(hunter.getIconHeight()+4);
		table.setShowVerticalLines(false);
		table.createDefaultColumnsFromModel();
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setTableHeader(header); 

		for(int i=0; i<table.getColumnModel().getColumnCount(); i++)
        	table.getColumnModel().getColumn(i).setPreferredWidth(40);
    	table.getColumnModel().getColumn(2).setPreferredWidth(75);

		// add sortrenderer
        tm.addMouseListener(table);
        // add column visibility popup
        colmodel.addMouseListener(table);

		JScrollPane sp = new JScrollPane(table);
		this.setLayout(new BorderLayout());
		this.add("Center", sp);
	}

	/**
	 *
	 */
	public CreaturePanel(Creature[] creatures)
	{
		this();
		update(creatures);
	}

	/**
	 *  Update the creatures.
	 */
	public void update(final Creature[] creatures)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				tablemodel.removeAllRows();
				for(int i=0; i<creatures.length; i++)
				{
					if(!obs && !(creatures[i] instanceof Observer))
					{
						tablemodel.addRow(new Object[]{
							""+(i+1),
							creatures[i] instanceof Hunter? hunter: prey,
							creatures[i].getName(),
							Integer.valueOf(creatures[i].getAge()),
							Integer.valueOf(creatures[i].getPoints())},
							creatures[i]);
					}
					if(obs && (creatures[i] instanceof Observer))
					{
						tablemodel.addRow(new Object[]{
							""+(i+1),
							observer,
							creatures[i].getName(),
							Integer.valueOf(creatures[i].getAge())},
							creatures[i]);
					}
				}
			}
		});
	}

	/**
	 *  Main for testing.
	 */
	public static void main(String[] args)
	{
		// Read highscore list.
		java.util.List creatures = new ArrayList();
		try
		{
			ObjectInputStream is = new ObjectInputStream(SUtil.getResource("highscore.dmp", CreaturePanel.class.getClassLoader()));
			creatures = SUtil.arrayToList(is.readObject());
			is.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		JComponent t = new CreaturePanel((Creature[])creatures.toArray(new Creature[creatures.size()]));
		JFrame f = new JFrame();
		f.add("Center", t);
		f.pack();
		f.setLocation(SGUI.calculateMiddlePosition(f));
		f.setVisible(true);
	}
}
