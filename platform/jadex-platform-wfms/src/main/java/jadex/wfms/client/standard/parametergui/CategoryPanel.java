package jadex.wfms.client.standard.parametergui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

/** 
 * A GUI-component for categories of parameters.
 */
public class CategoryPanel extends JPanel
{
	/** Category name */
	private String name;
	
	/** Current vertical layout position */
	private int yPosition;
	
	/**
	 * Creates a new CategoryPanel
	 * @param name name of the category
	 */
	public CategoryPanel(String name)
	{
		super(new GridBagLayout());
		this.name = name;
		setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), name));
		yPosition = 0;
	}
	
	public void addParameterPanel(JLabel panelLabel, AbstractParameterPanel panel)
	{
		int x = 0;
		int width = 2;
		
		if (panelLabel != null)
		{
			GridBagConstraints g = new GridBagConstraints();
			g.gridx = x++;
			g.gridy = yPosition;
			g.weightx = 1.0;
			g.fill = GridBagConstraints.HORIZONTAL;
			g.insets = new Insets(5, 5 , 5, 5);
			add(panelLabel, g);
			width--;
		}
		
		GridBagConstraints g = new GridBagConstraints();
		g.gridx = x;
		g.gridy = yPosition++;
		g.gridwidth = width;
		g.weightx = 1.0;
		g.fill = GridBagConstraints.HORIZONTAL;
		g.insets = new Insets(5, 5 , 5, 5);
		add(panel, g);
	}
	
	/**
	 * Returns the name of the category
	 * @return name of the category
	 */
	public String getName()
	{
		return name;
	}
}
