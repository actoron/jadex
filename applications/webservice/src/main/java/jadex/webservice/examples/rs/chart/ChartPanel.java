package jadex.webservice.examples.rs.chart;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.future.IFuture;
import jadex.commons.gui.EditableList;
import jadex.commons.gui.PropertiesPanel;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingDefaultResultListener;

/**
 * 
 */
public class ChartPanel extends JPanel
{
	/**
	 * 
	 */
	public ChartPanel(final IExternalAccess agent)
	{
		setLayout(new BorderLayout());
//		JPanel iconp = new JPanel(new BorderLayout());
		final JPanel cp = new JPanel(new GridBagLayout());
		final JLabel chartlabel = new JLabel();
//		final JButton chartbutton = new JButton();
		cp.add(chartlabel, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, 
			GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0));
		JScrollPane sp = new JScrollPane(cp);
		add(sp, BorderLayout.CENTER);
//		add(cp, BorderLayout.CENTER);
		sp.setPreferredSize(new Dimension(400, 250));
		
		final PropertiesPanel pp = new PropertiesPanel("Settings");
		final JComboBox charttype = pp.createComboBox("charttype", new String[]{"Bar Chart", "Line Chart", "Pie Chart"});
		final JTextField width = pp.createTextField("width", "250", true);
		final JTextField height = pp.createTextField("height", "200", true);
//		final JList chartdata = new JList(Integer.valueOf[]{30, 50, 20, 90});
		final JTextField labels = pp.createTextField("labels", "a, b, c, d", true);
		final JTextField colors = pp.createTextField("colors", "#FF0000, #334499", true);
		final EditableList chartdata = new EditableList("Chart Data");
		chartdata.setEntries(new String[]{"30, 50, 20, 90", "55, 88, 11, 14"});
		pp.addComponent("chart data", chartdata);
		JButton draw = pp.createButtons("buts", new String[]{"draw"}, 0)[0];
		add(pp, BorderLayout.SOUTH);
		
		draw.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					String[] entries = chartdata.getEntries();
					final double[][] data = new double[entries.length][];
					for(int i=0; i<entries.length; i++)
					{
						StringTokenizer stok = new StringTokenizer(entries[i], ",");
						double[] serdata = new double[stok.countTokens()];
						data[i] = serdata;
						for(int j=0; stok.hasMoreTokens(); j++)
						{
							serdata[j] = Double.parseDouble(stok.nextToken());
						}
					}
					
					StringTokenizer stok = new StringTokenizer(labels.getText(), ",");
					final String labs[]  = new String[stok.countTokens()];
					for(int i=0; i<labs.length; i++)
					{
						labs[i] = stok.nextToken().trim();
					}
					
					stok = new StringTokenizer(colors.getText(), ",");
					final Color cols[]  = new Color[stok.countTokens()];
					for(int i=0; i<cols.length; i++)
					{
						cols[i] = SGUI.stringToColor(stok.nextToken().trim());
					}
					
					final int w = Integer.parseInt(width.getText());
					final int h = Integer.parseInt(height.getText());
	
					final Object chartt = charttype.getSelectedItem();
				
					agent.scheduleStep(new IComponentStep<Void>()
					{
						public IFuture<Void> execute(IInternalAccess ia)
						{
							IFuture<IChartService> csfut = ia.getFeature(IRequiredServicesFeature.class).getService("chartservice");
							csfut.addResultListener(new SwingDefaultResultListener<IChartService>()
							{
								public void customResultAvailable(IChartService chartservice)
								{
									if("Bar Chart".equals(chartt))
									{
										chartservice.getBarChart(w, h, data, labs, cols).addResultListener(new SwingDefaultResultListener<byte[]>()
										{
											public void customResultAvailable(byte[] data)
											{
												ImageIcon icon = new ImageIcon(data);
												chartlabel.setIcon(icon);
											}
										});
									}
									else if("Line Chart".equals(chartt))
									{
										chartservice.getLineChart(w, h, data, labs, cols).addResultListener(new SwingDefaultResultListener<byte[]>()
										{
											public void customResultAvailable(byte[] data)
											{
												ImageIcon icon = new ImageIcon(data);
												chartlabel.setIcon(icon);
											}
										});
									}
									else if("Pie Chart".equals(chartt))
									{
										chartservice.getPieChart(w, h, data, labs, cols).addResultListener(new SwingDefaultResultListener<byte[]>()
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
							return IFuture.DONE;
						}
					});
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
					JOptionPane.showMessageDialog(ChartPanel.this, "Error reading settings: "
						+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}
	
	/**
	 * 
	 */
	public static JFrame createChartFrame(IExternalAccess agent)
	{
		JFrame f = new JFrame(agent.getId().getName());
		JPanel p = new ChartPanel(agent);
		f.add(p, BorderLayout.CENTER);
		f.pack();
		f.setVisible(true);
		f.setLocation(SGUI.calculateMiddlePosition(f));
		return f;
	}
	
	public static void main(String[] args)
	{
		System.out.println(Integer.toHexString(Color.RED.getRGB()));
	}
}
