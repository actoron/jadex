package sodekovs.applications.bikes.map;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.jdesktop.swingx.JXMapKit;
import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.Waypoint;
import org.jdesktop.swingx.mapviewer.WaypointPainter;
import org.jdesktop.swingx.mapviewer.WaypointRenderer;

public class MapViewer extends JFrame {

	public static final GeoPosition POSITION_HAMBURG = new GeoPosition(53.55035428228909, 9.99305248260498);
	public static final GeoPosition POSITION_LONDON = new GeoPosition(51.48822432632349, -0.097503662109375);
	public static final GeoPosition POSITION_WASHINGTON = new GeoPosition(38.88942947447528, -77.03544616699219);
	public static final GeoPosition POSITION_PARIS = new GeoPosition(48.869076, 2.309983);
	public static final GeoPosition POSITION_WIEN = new GeoPosition(48.208488, 16.372998);
	public static final GeoPosition POSITION_MONTREAL = new GeoPosition(45.508813, -73.555377);
	
	public static final String BIKE_GREEN = "/sodekovs-applications/src/main/java/sodekovs/applications/bikes/map/bike-green.png";
	public static final String BIKE_GREEN_ICON = "/sodekovs-applications/src/main/java/sodekovs/applications/bikes/map/bike-green-icon.png";
	public static final String BIKE_MONKEY = "/sodekovs-applications/src/main/java/sodekovs/applications/bikes/map/bike-monkey.png";

	/**
	 * 
	 */
	private static final long serialVersionUID = -1562914566290625300L;

	private JXMapKit jXMapKit = null;

	private WaypointPainter<JXMapViewer> waypointPainter = null;

	private Set<Waypoint> waypoints = null;

	private BufferedImage image = null;

	public MapViewer() {
		initComponents();
	}

	private void initComponents() {
		jXMapKit = new JXMapKit();
		jXMapKit.setDefaultProvider(JXMapKit.DefaultProviders.OpenStreetMaps);
		// jXMapKit.setAddressLocation(POSITION_HAMBURG);
		jXMapKit.setCenterPosition(POSITION_HAMBURG);
		jXMapKit.setZoom(6);

		try {
			image = ImageIO.read(new File(new File("..").getCanonicalFile() + BIKE_MONKEY));
		} catch (IOException e) {
			e.printStackTrace();
		}

		waypoints = new HashSet<Waypoint>();
		waypointPainter = new WaypointPainter<JXMapViewer>();
		waypointPainter.setWaypoints(waypoints);
		waypointPainter.setRenderer(new WaypointRenderer() {

			@Override
			public boolean paintWaypoint(Graphics2D g, JXMapViewer map, Waypoint waypoint) {
				// g.drawRect(-5, -5, 10, 10);
				// g.setColor(Color.BLUE);
				if (image != null) {
					g.drawImage(image, image.getWidth() / -2, image.getHeight() / -2, null);
				}

				return false;
			}
		});
		jXMapKit.getMainMap().setOverlayPainter(waypointPainter);

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		getContentPane().add(jXMapKit, BorderLayout.CENTER);
		this.setTitle("OpenStreetMaps Visualization");
		this.setSize(1680, 1050);
	}

	public static void main(String args[]) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				new MapViewer().setVisible(true);
			}
		});
	}

	public void removeWaypoint(Waypoint wp) {
		this.waypoints.remove(wp);
		this.jXMapKit.getMainMap().repaint();
	}

	public void addWaypoint(Waypoint wp) {
		this.waypoints.add(wp);
		this.jXMapKit.getMainMap().repaint();
	}
}