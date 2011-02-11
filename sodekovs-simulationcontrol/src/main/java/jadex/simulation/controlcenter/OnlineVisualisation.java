package jadex.simulation.controlcenter;

import jadex.application.space.envsupport.evaluation.AbstractChartDataConsumer;
import jadex.commons.gui.SGUI;
import jadex.simulation.model.Observer;
import jadex.simulation.model.SimulationConfiguration;
import jadex.simulation.model.result.IntermediateResult;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.CharBuffer;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

/**
 * Gui, showing details about the simulation setting and the progress.
 */
public class OnlineVisualisation extends JFrame {
	// -------- attributes --------

	private JPanel mainPanel;
	private boolean exit = true;

	// private List<AbstractChartDataConsumer> consumers;

	/**
	 * Create a gui.
	 * 
	 */
	public OnlineVisualisation(List<AbstractChartDataConsumer> consumers) {
		super("Online Visualisation");
		// mainPanel = new JPanel(neow GridLayout(2, 1));
		// this.consumers = consumers;
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setOpaque(true);
		mainPanel.setPreferredSize(new Dimension(250, 200));

		for (AbstractChartDataConsumer consumer : consumers)
			mainPanel.add(consumer.getChartPanel());

		mainPanel.setVisible(true);
		setContentPane(mainPanel);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		pack();
		setLocation(SGUI.calculateMiddlePosition(this));
		// setSize(620, 800);
		setSize(400, 400);
		setVisible(true);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		// addWindowListener(new WindowAdapter() {
		// public void windowClosing(WindowEvent e) {
		// }
		// });
//		testUpdate();

		Thread t1 = new Thread(new Runnable() {

			@Override
			public void run() {

				while (getExit()) {
					try {
//						System.out.println("!---->" + exit);
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println("#OnlineVisualisation#...interrupted");
					}
					update();
				}
			}
		});
		t1.start();
	}

	private void update() {
		mainPanel.revalidate();
		mainPanel.repaint();

		// for(AbstractChartDataConsumer consumer : consumers)
		// consumer.refresh();
	}

	public synchronized void setExit() {
		this.exit = false;
	}

	public synchronized boolean getExit() {
		return this.exit;
	}

	private void testUpdate() {
		while (getExit()) {
			try {
				System.out.println("!---->" + exit);
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("#OnlineVisualisation#...interrupted");
			}
			update();
		}
	}

}
