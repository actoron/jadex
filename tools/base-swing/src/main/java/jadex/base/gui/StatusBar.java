package jadex.base.gui;

import java.awt.Button;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;

/**
 *  A status bar can be used to display important application information.
 */
public class StatusBar extends JPanel
{
	//-------- attributes --------

	/** The text label. */
	protected JLabel textl;

	/** The timeout. */
	protected long timeout;

	/** The timer. */
	protected Timer timer;

	/** The components. */
	protected Map components;

	//-------- constructors --------

	/**
	 *  Create a new status bar.
	 */
	public StatusBar()
	{
		this.components = new LinkedHashMap();
		this.textl = new JLabel(" ");
		this.timer = new Timer(5000, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				textl.setText(" ");
				timer.stop();
			}
		});

		this.setBorder(new BevelBorder(BevelBorder.LOWERED));
		this.setLayout(new GridBagLayout());
		layoutComponents();
	}

	//-------- methods --------

	/**
	 *  Set the text of the status bar.
	 */
	public void setText(String text)
	{
		final String txt = text==null || text.length()==0? " ": text;
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				textl.setText(txt);
				timer.restart();
			}
		});
	}

	/**
	 *  Add a status component.
	 */
	public void addStatusComponent(Object id, Component comp)
	{
		if(!components.containsKey(id))
		{
			components.put(id, comp);
			layoutComponents();
		}
	}

	/**
	 *  Get a status component.
	 */
	public Component getStatusComponent(Object id)
	{
		return (Component)components.get(id);
	}

	/**
	 *  Remove a status component.
	 */
	public void removeStatusComponent(Object id)
	{
		// todo: shift other components
		if(components.containsKey(id))
		{
			components.remove(id);
			layoutComponents();
		}
	}

	/**
	 *  Relayout components after components have been added or removed.
	 */
	protected void layoutComponents()
	{
		// Remove and re-add all components in inverse insertion order (adding from right-to-left).
		this.removeAll();
		this.add(textl, new GridBagConstraints(0, 0, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2,4,4,2), 0, 0));
		int pos	= components.size();
		for(Iterator it=components.values().iterator(); it.hasNext(); )
		{
			this.add((Component)it.next(), new GridBagConstraints(pos--, 0, 1, 1, 0, 0,
				GridBagConstraints.EAST, GridBagConstraints.VERTICAL, new Insets(0,0,0,0), 0, 0));
		}
		this.invalidate();
		this.doLayout();
		this.repaint();
	}

	// todo: add replaceStatusComponent etc.

	//-------- static part --------

	/**
	 *  Main for testing.
	 */
	public static void main(String[] args)
	{
		JFrame f = new JFrame();
		StatusBar sb = new StatusBar();

		f.add("South", sb);
		f.setSize(200, 200);
		f.setVisible(true);

		sb.setText("hallo");
		Component b = new Button("b");
		sb.addStatusComponent("a", new Button("a"));
		sb.addStatusComponent("b", b);
		sb.addStatusComponent("c", new Button("c"));

		sb.removeStatusComponent("b");
	}
}
