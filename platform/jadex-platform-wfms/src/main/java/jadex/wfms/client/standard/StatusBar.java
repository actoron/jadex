package jadex.wfms.client.standard;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Action;
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
	
	private Map iconPaths;
	
	public StatusBar()
	{
		super(new GridBagLayout());
		
		icons = new HashMap();
		this.iconPaths = new HashMap();
		
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
	
	public void addIcon(String name, String path, String tooltip)
	{
		ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource(path));
		JLabel label = new JLabel(icon);
		label.setToolTipText(tooltip);
		icons.put(name, label);
		iconPaths.put(name, path);
		
		iconPanel.add(label);
	}
	
	public void replaceIcon(String name, String path, String tooltip)
	{
		JLabel label = (JLabel) icons.get(name);
		label.setIcon(new ImageIcon(getClass().getClassLoader().getResource(path)));
		label.setToolTipText(tooltip);
		iconPaths.put(name, path);
	}
	
	public void removeIcon(String name)
	{
		JLabel label = (JLabel) icons.remove(name);
		iconPaths.remove(name);
		iconPanel.remove(label);
	}
	
	public void setIconAction(final String name, final Action action)
	{
		final JLabel label = (JLabel) icons.get(name);
		MouseListener[] listeners = label.getMouseListeners();
		for (int i = 0; i < listeners.length; ++i)
			label.removeMouseListener(listeners[i]);
		label.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1)
					action.actionPerformed(new ActionEvent(label, 0, (String) iconPaths.get(name)));
			}
		});
	}
}
