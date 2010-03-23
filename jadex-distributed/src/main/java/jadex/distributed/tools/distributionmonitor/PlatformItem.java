package jadex.distributed.tools.distributionmonitor;

import jadex.distributed.service.IMonitorServiceListener;
import jadex.distributed.service.Workload;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

/**
 * Represents a single item in the list of all known platforms. The platforms are grouped in a JPanel, which is the left JPanel of a JSplitPane.
 * 
 * @author daniel
 * 
 */
public class PlatformItem extends JPanel {

	/** Instanzvariablen für die Anzeige auf dem JPanel **/
	/* IP and Port; should be static through the whole life of this JPanel */
	// private String ip;
	// private int port;
	/*
	 * CPU and RAM usage can vary and need to be updated regulary; usage varies from 0 to 100, from 0 to 100%
	 */
	// private byte cpuUsage = 15; // dummy data; gather it from JMX
	// private byte ramUsage = 70;

	private InetSocketAddress machine;
	private Workload workload;

	public PlatformItem(InetSocketAddress machine, Workload workload) {
		this.machine = machine;
		this.workload = workload;
		System.out.println("=== PlatformItem hier ===  mein user.dir ist: " + System.getProperty("user.dir"));
		// === PlatformItem hier === mein user.dir ist: /home/daniel/Informatik/Diplom/Software/Jadex Software/dummy_workspace/GUI_Test

	}

	@Override
	protected void paintComponent(Graphics gi) {
		super.paintComponent(gi); // to setup background etc.
		Graphics2D g = (Graphics2D) gi;

		/* IP:Port */
		StringBuilder sb = new StringBuilder(21); // ALWAYS use StringBuilder
		sb.append(machine.getHostName()).append(":").append(machine.getPort());
		g.setPaint(Color.black);
		g.drawString(sb.toString(), 3, 15);

		/* CPU usage */
		g.setPaint(getPaint(workload.getCpuUsage()));
		g.fillRect(3, 20, workload.getCpuUsage(), 15);
		g.setPaint(new Color(0, 0, 0));
		sb = new StringBuilder(4);
		sb.append(workload.getCpuUsage()).append("%");
		int length = String.valueOf(workload.getCpuUsage()).length();
		if(length == 1)
			sb.insert(0, "  ");
		else if(length == 2)
			sb.insert(0, " ");
		g.drawString(sb.toString(), 40, 32);
		// TODO string look ugly with drawString(...); beautify with
		// AttributedCharacterIterator http://www.fh-wedel.de/~si/seminare/ws00/Ausarbeitung/11.java2d/java2d7.htm

		/* RAM usage */
		g.setPaint(new Color(0, 255, 0)); // R, G, B; red, green, blue
		g.fillRect(3, 40, workload.getRamUsage(), 15);
		g.setPaint(new Color(0, 0, 0));
		sb = new StringBuilder(4);
		sb.append(workload.getRamUsage()).append("%");
		length = String.valueOf(workload.getRamUsage()).length();
		if(length == 1)
			sb.insert(0, "  ");
		else if(length == 2)
			sb.insert(0, " ");
		g.drawString(sb.toString(), 40, 52);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(160, 60); // for the parent JComponent to scale properly
	}

	// TODO benutzen, wenn Farbverlauf nicht kontinuierlich, sondern diskret
	// sein soll
	public void next(int r, int g, int b) {

	}
	
	/**
	 * Return paint green, yellow or red depending on the value of <code>value</code>
	 * @param value - typically a value between 0 and 100 inclusively
	 * @return
	 */
	private Paint getPaint(int value) {
		Paint paint;
		if(0<=value && value<=33)
			paint = Color.green;
		else if(34<=value && value<=66)
			paint = Color.yellow;
		else
			paint = Color.red; // this also applies to a int value bigger then 100
		return paint;
	}
}
