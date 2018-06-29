package jadex.bdi.examples.garbagecollector_classic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIDefaults;

import jadex.commons.beans.PropertyChangeEvent;
import jadex.commons.beans.PropertyChangeListener;
import jadex.commons.gui.SGUI;

/**
 *  The map panel for displaying the environment.
 */
public class MapPanel extends JPanel
{
	//-------- constants --------

	/** The image icons. */
	public static final UIDefaults	icons	= new UIDefaults(new Object[]
	{
		"burner",	SGUI.makeIcon(MapPanel.class, "/jadex/bdi/examples/garbagecollector_classic/images/burner.png"),
		"collector",	SGUI.makeIcon(MapPanel.class, "/jadex/bdi/examples/garbagecollector_classic/images/collector.png"),
		"garbage", SGUI.makeIcon(MapPanel.class, "/jadex/bdi/examples/garbagecollector_classic/images/garbage.png"),
	});

	//-------- attributes --------

	/** The component to display burners. */
	protected JLabel burner;

	/** The component to display collector. */
	protected JLabel collector;

	/** The component to display garbage. */
	protected JLabel garbage;

	/** The burner image. */
	protected Image	burner_image;

	/** The collector image. */
	protected Image	collector_image;

	/** The burner image. */
	protected Image	garbage_image;

	/** The environment. */
	protected Environment env;

	/** Flag to indicate that component has changed and sizes have to be recalculated. */
	protected boolean	rescale;

	/**
	 *  Create a new panel.
	 */
	public MapPanel(Environment env)
	{
		this.env = env;

		// Create icon images for objects in the world.
		this.burner_image	= ((ImageIcon)icons.getIcon("burner")).getImage();
		this.collector_image	= ((ImageIcon)icons.getIcon("collector")).getImage();
		this.garbage_image	= ((ImageIcon)icons.getIcon("garbage")).getImage();

		// Create components for objects in the world.
		this.burner	= new JLabel(new ImageIcon(burner_image), JLabel.CENTER);
		this.collector	= new JLabel(new ImageIcon(collector_image), JLabel.CENTER);
		this.garbage	= new JLabel(new ImageIcon(garbage_image), JLabel.CENTER);

		// Trigger rescaling of images.
		this.addComponentListener(new ComponentAdapter()
		{
			public void	componentResized(ComponentEvent ce)
			{
				rescale	= true;
			}
		});
		
		// Update panel on changes.
		env.addPropertyChangeListener(new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent evt)
			{
				MapPanel.this.invalidate();
				MapPanel.this.repaint();
			}
		});
	}

	/**
	 * 	Overridden paint method.
	 */
	protected void	paintComponent(Graphics g)
	{
		Rectangle	bounds	= getBounds();
		g.setColor(getBackground());
		g.fillRect((int)bounds.getX(), (int)bounds.getY(),
			(int)bounds.getWidth(), (int)bounds.getHeight());

		double cellw = bounds.getWidth()/env.getGridSize();
		double cellh = bounds.getHeight()/env.getGridSize();

		// Rescale images if necessary.
		if(rescale)
		{
			((ImageIcon)burner.getIcon()).setImage(
				burner_image.getScaledInstance((int)cellw, (int)cellh, Image.SCALE_DEFAULT));
			((ImageIcon)collector.getIcon()).setImage(
				collector_image.getScaledInstance((int)cellw, (int)cellh, Image.SCALE_DEFAULT));
			((ImageIcon)garbage.getIcon()).setImage(
				garbage_image.getScaledInstance((int)cellw, (int)cellh,Image.SCALE_DEFAULT));

			rescale	= false;
		}

		// Paint creatures.
		Object[] wos = env.getWorldObjects();
		for(int i=0; i<wos.length; i++)
		{
			renderObject(g, (WorldObject)wos[i], cellw, cellh);
		}

		// Paint grid.
		g.setColor(Color.black);
		g.drawRect(0,0, bounds.width-1, bounds.height-1);
		for(double x=0; x<bounds.width; x+=cellw)
		{
			g.drawLine((int)x, 0, (int)x, bounds.height-1);
		}
		for(double y=0; y<bounds.height; y+=cellh)
		{
			g.drawLine(0, (int)y, bounds.width-1, (int)y);
		}
	}

	/**
	 *  Render an object on map.
	 *  @param g	The graphics object.
	 *  @param wo	The object to render.
	 *  @param cellw	The cell width.
	 *  @param cellh	The cell height.
	 */
	protected void	renderObject(Graphics g, WorldObject wo, double cellw, double cellh)
	{
		Position loc	= wo.getPosition();

		// Calculate bounds of object (leave one pixel for cell grid).
		/*Rectangle	bounds	= new Rectangle(
			(int)(cellw*loc.getX())+1,
			(int)(cellh*loc.getY())+1,
			(int)(cellw*(loc.getX()+1)) - (int)(cellw*loc.getX()) - 2,
			(int)(cellh*(loc.getY()+1)) - (int)(cellh*loc.getY()) - 2);*/

		// Determine component.
		Component	comp;
		if(wo.getType().equals(Environment.BURNER))
			comp	= burner;
		else if(wo.getType().equals(Environment.COLLECTOR))
			comp	= collector;
		else if(wo.getType().equals(Environment.GARBAGE))
			comp	= garbage;
		else
			throw new RuntimeException("Unknown type of object: "+wo);

		// Paint component into map.
		/*comp.setBounds(bounds);
		g.translate(bounds.x, bounds.y);
		comp.paint(g);
		g.translate(-bounds.x, -bounds.y);*/
		SGUI.renderObject(g, comp, cellw, cellh, loc.getX(), loc.getY(), 1);
	}
}
