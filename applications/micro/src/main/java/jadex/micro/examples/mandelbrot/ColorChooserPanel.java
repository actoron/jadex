package jadex.micro.examples.mandelbrot;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

/**
 *  Panel that allows choosing a set of colors.
 */
public class ColorChooserPanel	extends JPanel
{
	//-------- constructors --------
	
	public ColorChooserPanel(final DisplayPanel panel)
	{
		super(new GridBagLayout());
		
		this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Color Scheme"));
		
		GridBagConstraints	gbc	= new GridBagConstraints();
		gbc.fill	= GridBagConstraints.BOTH;
		gbc.gridwidth	= GridBagConstraints.REMAINDER;
		gbc.weightx	= 1;
		gbc.weighty	= 1;
		gbc.insets	= new Insets(1, 1, 1, 1);
		
		final JCheckBox	cycle	= new JCheckBox("Cycle", GenerateService.ALGORITHMS[0].useColorCycle());
		
		final DefaultListModel	lm	= new DefaultListModel();
		lm.addElement(new Color(204, 204, 255));
		lm.addElement(new Color(0, 0, 255));
		lm.addElement(new Color(0, 0, 51));
		lm.addElement(new Color(0, 0, 255));
		setColorScheme(panel, lm, cycle.isSelected());

		
		final JList	colors	= new JList(lm);
		this.add(new JScrollPane(colors), gbc);
		colors.setCellRenderer(new ListCellRenderer()
		{
			public Component getListCellRendererComponent(JList list, final Object value,
					int index, final boolean selected, boolean focus)
			{
				return new JComponent()
				{
					protected void paintComponent(Graphics g)
					{
						Rectangle	bounds	= getBounds();
						Insets	insets = getInsets();
						if(insets!=null)
						{
							bounds.x	= insets.left;
							bounds.y	= insets.top;
							bounds.width	-= insets.left+insets.right;
							bounds.height	-= insets.top+insets.bottom;
						}
						else
						{
							bounds.x	= 0;
							bounds.y	= 0;
						}
						
						g.setColor(selected ? UIManager.getColor("List.selectionBackground") : UIManager.getColor("List.background"));
						g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
						g.setColor(selected ? UIManager.getColor("List.selectionForeground") : UIManager.getColor("List.foreground"));
						g.drawRect(bounds.x+1, bounds.y+1, bounds.width-3, bounds.height-3);
						g.setColor((Color)value);
						g.fillRect(bounds.x+2, bounds.y+2, bounds.width-4, bounds.height-4);
					}
					
					public Dimension getPreferredSize()
					{
						return new Dimension(16, 16);
					}
					
					public Dimension getMinimumSize()
					{
						return new Dimension(8, 8);
					}
				};
			}
		});
		
		JButton	add	= new JButton("add")
		{
			public Insets getInsets()
			{
				return new Insets(0,3,0,3);
			}
		};
		add.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Color	c	= JColorChooser.showDialog(ColorChooserPanel.this, "Choose a color to add", Color.yellow);
				if(c!=null)
				{
					lm.addElement(c);
					setColorScheme(panel, lm, cycle.isSelected());
				}
			}
		});
		gbc.weighty	= 0;
		gbc.fill	= GridBagConstraints.NONE;
		gbc.anchor	= GridBagConstraints.EAST;
		gbc.gridy	= 1;
		gbc.gridwidth	= 1;
		this.add(add, gbc);
		
		JButton	remove	= new JButton("rem")
		{
			public Insets getInsets()
			{
				return new Insets(0,3,0,3);
			}
		};
		remove.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Object[]	cs	= colors.getSelectedValues();
				for(int i=0; i<cs.length; i++)
				{
					lm.removeElement(cs[i]);
				}
				setColorScheme(panel, lm, cycle.isSelected());
			}
		});
		gbc.weightx	= 0;
		gbc.gridx	= 1;
		this.add(remove, gbc);
		
		cycle.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				setColorScheme(panel, lm, cycle.isSelected());
			}
		});
		gbc.gridx	= 0;
		gbc.gridy	= 2;
		gbc.gridwidth	= GridBagConstraints.REMAINDER;
		this.add(cycle, gbc);

		add.setMinimumSize(remove.getMinimumSize());
		add.setPreferredSize(remove.getPreferredSize());
	}

	/**
	 *  Set the current color scheme on the panel.
	 */
	protected void setColorScheme(DisplayPanel panel, DefaultListModel lm, boolean cycle)
	{
		Color[]	scheme	= new Color[lm.getSize()];
		lm.copyInto(scheme);
		panel.setColorScheme(scheme, cycle);
	}
}

