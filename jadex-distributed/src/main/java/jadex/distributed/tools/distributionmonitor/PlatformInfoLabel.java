package jadex.distributed.tools.distributionmonitor;

import jadex.distributed.service.monitor.PlatformInfo;
import jadex.distributed.service.monitor.Workload;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.io.IOException;
import java.net.InetSocketAddress;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class PlatformInfoLabel extends JLabel implements ListCellRenderer { // JPanel wäre zu viel, da keine input events verarbeitet werden
	
	private PlatformInfo _platformInfo;
	
	public PlatformInfoLabel() {
		//setOpaque(true);
		//setBackground(Color.white);
	}
	
	@Override
	protected void paintComponent(Graphics gi) {
		super.paintComponent(gi); // to setup background etc.
		Graphics2D g = (Graphics2D) gi;

		Font font = new Font(Font.SANS_SERIF, Font.BOLD, 12);
		g.setFont(font);
		/*Font font = null; // TODO man wird es doch wohl in Java schaffen einen monospace font zu laden der schön aussieht >_<
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("fonts/FreeMonoBold.ttf"));
			//font = font.deriveFont(Font.BOLD, (float) 14);
			font = font.deriveFont((float) 14);
		} catch (FontFormatException e) {
			System.err.println("The specified font is bad.");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Font file couldn't be loaded");
			font = new Font(Font.SANS_SERIF, Font.BOLD, 12);
			e.printStackTrace();
		}
		g.setFont(font);*/
		
		/* IP:Port */
		StringBuilder sb = new StringBuilder(21);
		sb.append(_platformInfo.getIp().getHostAddress()).append(":4711"); // Which port should I enter here? The RMI port? Or the the multicast port?
		g.setPaint(Color.black);
		g.drawString(sb.toString(), 3, 15);
		
		/* CPU usage */
		int load = (int) (_platformInfo.getCpuLoad()*100);
		g.setPaint(Color.black);
		g.drawRect(2, 19, 100+1, 15+1);
		g.setPaint(getPaint( load ));
		g.fillRect(3, 20, load, 15);
		g.setPaint(new Color(0, 0, 0));
		sb = new StringBuilder(4);
		sb.append( load ).append("%");
		int length = String.valueOf( load ).length();
		if(length == 1)
			sb.insert(0, "  ");
		else if(length == 2)
			sb.insert(0, " ");
		g.drawString(sb.toString(), 40, 32);
		g.drawString("CPU", 120, 32);
		// TODO string look ugly with drawString(...); beautify with
		// AttributedCharacterIterator http://www.fh-wedel.de/~si/seminare/ws00/Ausarbeitung/11.java2d/java2d7.htm
		
		/* RAM usage */
		g.setPaint(Color.black);
		g.drawRect(2, 39, 100+1, 15+1);
		g.setPaint( getPaint( _platformInfo.getHeapUsage() ) );
		g.fillRect(3, 40, _platformInfo.getHeapUsage(), 15);
		g.setPaint(new Color(0, 0, 0));
		sb = new StringBuilder(4);
		sb.append( _platformInfo.getHeapUsage() ).append("%");
		length = String.valueOf( _platformInfo.getHeapUsage() ).length();
		if(length == 1)
			sb.insert(0, "  ");
		else if(length == 2)
			sb.insert(0, " ");
		g.drawString(sb.toString(), 40, 52);
		g.drawString("RAM", 120, 52);
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(160, 60); // for the parent JComponent to scale properly
	}
	
	/**
	 * Return paint green, yellow or red depending on the value of <code>value</code>
	 * @param value - typically a value between 0 and 100 inclusively
	 * @return the Paint green, yellow, or red depending the given input value
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

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		this._platformInfo = (PlatformInfo) value;
		return this;
	}
}
