package jadex.webservice.examples.rs.chart;

import jadex.base.gui.SwingDefaultResultListener;
import jadex.commons.gui.PropertiesPanel;
import jadex.commons.gui.SGUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/**
 * 
 */
public class ChartPanel extends JPanel
{
	/**
	 * 
	 */
	public ChartPanel(final IChartService chartservice)
	{
		setLayout(new BorderLayout());
		JPanel iconp = new JPanel(new BorderLayout());
		final JPanel cp = new JPanel(new GridBagLayout());
		final JLabel chartlabel = new JLabel();
//		final JButton chartbutton = new JButton();
		cp.add(chartlabel, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, 
			GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0));
//		JScrollPane sp = new JScrollPane(cp);
//		sp.add(chartlabel, BorderLayout.CENTER);
		add(cp, BorderLayout.CENTER);
//		sp.setPreferredSize(new Dimension(200, 100));
		
		final PropertiesPanel pp = new PropertiesPanel("Settings");
		final JComboBox charttype = pp.createComboBox("charttype", new String[]{"Bar Chart", "Line Chart", "Pie Chart"});
		final JTextField width = pp.createTextField("width", "250", true);
		final JTextField height = pp.createTextField("height", "200", true);
		final JList chartdata = new JList(new Integer[]{30, 50, 20, 90});
		pp.addFullLineComponent("chartdata", chartdata);
		JButton draw = pp.createButtons("buts", new String[]{"draw"}, 0)[0];
		add(pp, BorderLayout.SOUTH);
		
		draw.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int num = chartdata.getModel().getSize();
				double[] data = new double[num];
				for(int i=0; i<num; i++)
				{
					Object elem = chartdata.getModel().getElementAt(i);
					data[i] = Double.parseDouble(""+elem);
				}
				int w = Integer.parseInt(width.getText());
				int h = Integer.parseInt(height.getText());

				if("Bar Chart".equals(charttype.getSelectedItem()))
				{
					chartservice.getBarChart(w, h, data, null).addResultListener(new SwingDefaultResultListener<byte[]>()
					{
						public void customResultAvailable(byte[] data)
						{
							ImageIcon icon = new ImageIcon(data);
							chartlabel.setIcon(icon);
//							chartbutton.setIcon(icon);
//							ChartPanel.this.pack();
//							ChartPanel.this.invalidate();
//							ChartPanel.this.doLayout();
//							chartlabel.repaint();
						}
					});
				}
				else if("Line Chart".equals(charttype.getSelectedItem()))
				{
					chartservice.getLineChart(w, h, data, null).addResultListener(new SwingDefaultResultListener<byte[]>()
					{
						public void customResultAvailable(byte[] data)
						{
							ImageIcon icon = new ImageIcon(data);
							chartlabel.setIcon(icon);
						}
					});
					chartservice.getLineChart(w, h, data, null);
				}
				else if("Pie Chart".equals(charttype.getSelectedItem()))
				{
					chartservice.getPieChart(w, h, data, null).addResultListener(new SwingDefaultResultListener<byte[]>()
					{
						public void customResultAvailable(byte[] data)
						{
							ImageIcon icon = new ImageIcon(data);
							chartlabel.setIcon(icon);
						}
					});
				}
			}
		});
	}
	
	/**
	 * 
	 */
	public static JFrame createChartFrame(IChartService chartservice)
	{
		JFrame f = new JFrame();
		JPanel p = new ChartPanel(chartservice);
		f.add(p, BorderLayout.CENTER);
		f.pack();
		f.setVisible(true);
		f.setLocation(SGUI.calculateMiddlePosition(f));
		return f;
	}
}
