package jadex.distributed.tools.distributionmonitor;

import jadex.distributed.service.Workload;

import java.awt.Dimension;
import java.net.InetSocketAddress;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Represents a single item in the list of all known platforms. The platforms are grouped in a JPanel, which is the left JPanel of a JSplitPane.
 * 
 * @author daniel
 * 
 */
public class PlatformItem extends JPanel {

	private InetSocketAddress machine; // TODO diese beiden Referenzen werden hier nicht mehr benötigt !?!
	private Workload workload;
	
	private JLabel icon;
	private PlatformInfo info;

	public PlatformItem(InetSocketAddress machine, Workload workload) {
		this.machine = machine;
		this.workload = workload;
		
		//System.out.println("=== PlatformItem hier ===  mein user.dir ist: " + System.getProperty("user.dir"));
		// === PlatformItem hier === mein user.dir ist: /home/daniel/Informatik/Diplom/Software/Jadex Software/dummy_workspace/GUI_Test
		// TODO der user.dir ist nicht nötig, denn es gibt ja zum Glück getClass().getResource(...) ;)
		
		this.info = new PlatformInfo(machine, workload);
		
		//URL image = getClass().getResource("images/client.png");
		URL image = PlatformItem.class.getResource("images/client.png"); // hat keinen Unterschied zur oberen Zeile; welche besser/konformtable/generischer !?!
		
		if(image != null) {
			icon = new JLabel( new ImageIcon(image, "A client platform.") );
			//icon = new ImageIcon(image, "A client platform.");
		} else {
			System.err.println("Bild nicht gefunden in Pfad " + image.toString()); // TODO auf Logger-Ausgabe umstellen
		}
		
		// JPanel zusammenbauen
		add(icon);
		add(info);
	}

	// TODO benutzen, wenn Farbverlauf nicht kontinuierlich, sondern diskret
	// sein soll
	public void next(int r, int g, int b) {

	}
}
