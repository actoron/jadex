package jadex.rules.tools.reteviewer;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *  Display properties.
 */
public class PropertyPanel extends JPanel
{
	//-------- attributes --------
	
	/** The node. */
	protected Map properties;
	
	//-------- constructors --------
	
	/**
	 *  Create a property panel.
	 */
	public PropertyPanel(Map properties)
	{
		this.setLayout(new GridBagLayout());
//		this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createBevelBorder(
//			BevelBorder.LOWERED), title));
		
		setProperties(properties);
	}
	
	//-------- methods --------
	
	/**
	 *  Set the properties.
	 *  @param properties The properties.
	 */
	public void setProperties(Map properties)
	{
		this.properties = properties;
		refresh();
	}
	
	/**
	 *  Refresh the panel.
	 */
	public void refresh()
	{
		removeAll();
		
		int y = 0;
		for(Iterator it=properties.keySet().iterator(); it.hasNext(); )
		{
			String name = (String)it.next();
			JLabel lab = new JLabel(name);
			JTextField val = new JTextField(""+properties.get(name));
			add(lab, new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.NORTHEAST,
				GridBagConstraints.NONE, new Insets(2,4,4,2), 0, 0));
			add(val, new GridBagConstraints(1, y++, 1, 1, 1, 0, GridBagConstraints.NORTHEAST,
					GridBagConstraints.HORIZONTAL, new Insets(2,4,4,2), 0, 0));
		}
		
		/*SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				PropertyPanel.this.invalidate();
				PropertyPanel.this.doLayout();
				PropertyPanel.this.repaint();
			}
		});*/
	}
}
