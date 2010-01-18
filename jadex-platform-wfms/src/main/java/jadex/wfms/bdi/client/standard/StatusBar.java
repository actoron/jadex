package jadex.wfms.bdi.client.standard;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

public class StatusBar extends JPanel
{
	private JLabel statusTextLabel;
	
	private JPanel iconPanel;
	
	private Map icons;
	
	public StatusBar()
	{
		super(new GridBagLayout());
		
		icons = new HashMap();
		
		statusTextLabel = new JLabel();
		GridBagConstraints g = new GridBagConstraints();
		g.gridx = 0;
		g.anchor = GridBagConstraints.EAST;
		add(statusTextLabel, g);
		
		JPanel filler = new JPanel();
		g = new GridBagConstraints();
		g.gridx = 1;
		g.weightx = 1.0;
		g.fill = GridBagConstraints.HORIZONTAL;
		g.anchor = GridBagConstraints.CENTER;
		add(filler, g);
		
		iconPanel = new JPanel();
		iconPanel.setLayout(new BoxLayout(iconPanel, BoxLayout.LINE_AXIS));
		iconPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		g = new GridBagConstraints();
		g.gridx = 2;
		g.anchor = GridBagConstraints.EAST;
		add(iconPanel, g);
	}
	
	public void setText(String text)
	{
		statusTextLabel.setText(text);
	}
	
	public void addIcon(String name, String path)
	{
		ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource(path));
		JLabel label = new JLabel(icon);
		icons.put(name, label);
		
		iconPanel.add(label);
	}
	
	public void replaceIcon(String name, String path)
	{
		JLabel label = (JLabel) icons.get(name);
		label.setIcon(new ImageIcon(getClass().getClassLoader().getResource(path)));
	}
	
	public void removeIcon(String name)
	{
		JLabel label = (JLabel) icons.remove(name);
		iconPanel.remove(label);
	}
}
