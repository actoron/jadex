package jadex.distributed.tools.distributionmonitor;

import jadex.distributed.service.IMonitorServiceListener;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * Represents a single item in the list of all known platforms. The platforms
 * are grouped in a JPanel, which is the left JPanel of a JSplitPane.
 * @author daniel
 *
 */
public class DistributionMonitorItem extends JPanel implements IMonitorServiceListener {

	/* IP and Port; should be static through the whole life of this JPanel */
	private String ip;
	private int port;
	
	/* CPU and RAM usage can vary and need to be updated regulary; usage varies from 0 to 100, from 0 to 100% */
	private byte cpuUsage = 15; // dummy data; gather it from JMX
	private byte ramUsage = 70;
	// müssen auch aktualisiert werden; ergo: Listener-Pattern mit callback wieder angebracht?
	
	public DistributionMonitorItem(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}
	
	@Override
	protected void paintComponent(Graphics gi) {
		super.paintComponent(gi); // to setup background etc.
		Graphics2D g = (Graphics2D) gi;
		
		/* IP:Port */
		g.setPaint(Color.black);
		StringBuilder sb = new StringBuilder(21); // use ALWAYS StringBuilder
		sb.append(ip).append(":").append(port);
		g.drawString(sb.toString(), 3, 15);
		
		/* CPU usage */
		g.setPaint(new Color(0, 255, 0)); // R, G, B; red, green, blue
		g.fillRect(3, 20, 100, 15); // 100 auf aktuelle CPU-Auslastung anpassen
		g.setPaint(new Color(0, 0, 0));
		// TODO build here string with stringbuilder,   100%   ' '+45% to better align strings
		g.drawString("dummy", 40, 32); // TODO string look ugly with drawString(...); beautify with AttributedCharacterIterator http://www.fh-wedel.de/~si/seminare/ws00/Ausarbeitung/11.java2d/java2d7.htm
		
		/* RAM usage */
		g.setPaint(new Color(0, 255, 0)); // R, G, B; red, green, blue
		g.fillRect(3, 40, 100, 15); // 100 auf aktuelle CPU-Auslastung anpassen
		g.setPaint(new Color(0, 0, 0));
		// TODO build here string with stringbuilder; ( == ) ? :  if-else shortcut may good choice here
		g.drawString( "dummy", 40, 52);
		
		//g.setPaint(new Color(0, 255, 0)); // R, G, B; red, green, blue
		//g.fillRect(3, 20, 100, 15); // 100 auf aktuelle CPU-Auslastung anpassen
	}

	@Override
	public Dimension getPreferredSize() {
		//return super.getPreferredSize();
		return new Dimension(160, 60);
	}
	
	public void next(int r, int g, int b) {
		
	}
}
