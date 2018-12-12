package jadex.bdi.examples.hunterprey_classic.environment;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;
import javax.swing.UIDefaults;

import jadex.bdi.examples.hunterprey_classic.Creature;
import jadex.bdi.examples.hunterprey_classic.CurrentVision;
import jadex.bdi.examples.hunterprey_classic.Food;
import jadex.bdi.examples.hunterprey_classic.Hunter;
import jadex.bdi.examples.hunterprey_classic.Location;
import jadex.bdi.examples.hunterprey_classic.Observer;
import jadex.bdi.examples.hunterprey_classic.Obstacle;
import jadex.bdi.examples.hunterprey_classic.Prey;
import jadex.bdi.examples.hunterprey_classic.WorldObject;
import jadex.commons.gui.SGUI;


/**
 *  The map for the hunter prey world.
 */
public class MapPanel	extends JPanel
{
	//-------- constants --------

	/** The image icons. */
	public static final UIDefaults	icons	= new UIDefaults(new Object[]
	{
		"food",	SGUI.makeIcon(MapPanel.class, "/jadex/bdi/examples/hunterprey_classic/images/food.png"),
		"obstacle",	SGUI.makeIcon(MapPanel.class, "/jadex/bdi/examples/hunterprey_classic/images/obstacle.png"),
		"hunter", SGUI.makeIcon(MapPanel.class, "/jadex/bdi/examples/hunterprey_classic/images/hunter.png"),
		"prey", SGUI.makeIcon(MapPanel.class, "/jadex/bdi/examples/hunterprey_classic/images/prey.png"),
		"observer", SGUI.makeIcon(MapPanel.class, "/jadex/bdi/examples/hunterprey_classic/images/observer.png"),
		"background", SGUI.makeIcon(MapPanel.class, "/jadex/bdi/examples/hunterprey_classic/images/background.png")
	});

	//-------- attributes --------

	/** The vision to display in the panel. */
	protected CurrentVision	cv;

	/** The component to display obstacles. */
	protected JLabel	obstacle;

	/** The component to display food. */
	protected JLabel	food;

	/** The component to display prey. */
	protected JLabel	prey;

	/** The component to display hunters. */
	protected JLabel	hunter;

	/** The background image. */
	protected Image	background_image;

	/** The obstacle image. */
	protected Image	obstacle_image;

	/** The food image. */
	protected Image	food_image;

	/** The prey image. */
	protected Image	prey_image;

	/** The hunter image. */
	protected Image	hunter_image;

	/** Flag to indicate that component has changed and sizes have to be recalculated. */
	protected boolean	rescale;

	//-------- constructors --------

	/**
	 *  Create a new map panel.
	 */
	public MapPanel()
	{
		// Create icon images for objects in the world.
		this.background_image	= ((ImageIcon)icons.getIcon("background")).getImage();
		this.food_image	= ((ImageIcon)icons.getIcon("food")).getImage();
		this.obstacle_image	= ((ImageIcon)icons.getIcon("obstacle")).getImage();
		this.hunter_image	= ((ImageIcon)icons.getIcon("hunter")).getImage();
		this.prey_image	= ((ImageIcon)icons.getIcon("prey")).getImage();

		// Create components for objects in the world.
		this.obstacle	= new JLabel(new ImageIcon(obstacle_image), JLabel.CENTER);
		this.food	= new JLabel(new ImageIcon(food_image), JLabel.CENTER);
		this.hunter	= new JLabel(new ImageIcon(hunter_image), JLabel.CENTER);
		hunter.setVerticalTextPosition(JLabel.BOTTOM);
		hunter.setHorizontalTextPosition(JLabel.CENTER);
		this.prey	= new JLabel(new ImageIcon(prey_image), JLabel.CENTER);
		prey.setVerticalTextPosition(JLabel.BOTTOM);
		prey.setHorizontalTextPosition(JLabel.CENTER);

		// Trigger rescaling of images.
		this.addComponentListener(new ComponentAdapter()
		{
			public void	componentResized(ComponentEvent ce)
			{
				rescale	= true;
			}
		});

		// Activate tooltips.
		ToolTipManager.sharedInstance().registerComponent(this);
	}

	//-------- methods --------

	/**
	 *  Update the map with a new current vision.
	 *  @param cv	The new current vision to display.
	 */
	public void	update(CurrentVision cv)
	{
		this.cv	= cv;
		//this.invalidate();
		this.repaint();
	}

	//-------- paint methods --------

	// overridden paint method.
	protected void	paintComponent(Graphics g)
	{
		if(cv==null)
		{
			g.setColor(Color.BLACK);
			g.drawString("No vision!", getBounds().width/2, getBounds().height/2);
			return;
		}

		int	worldwidth	= cv.getCreature().getWorldWidth();
		int	worldheight	= cv.getCreature().getWorldHeight();
		Rectangle	bounds	= getBounds();
		double cellw = bounds.getWidth()/(double)worldwidth;
		double cellh = bounds.getHeight()/(double)worldheight;


		// Rescale images if necessary.
		if(rescale)
		{
			((ImageIcon)obstacle.getIcon()).setImage(
				obstacle_image.getScaledInstance((int)cellw, (int)cellh, Image.SCALE_DEFAULT));
			((ImageIcon)food.getIcon()).setImage(
				food_image.getScaledInstance((int)cellw, (int)cellh, Image.SCALE_DEFAULT));
			((ImageIcon)hunter.getIcon()).setImage(
				hunter_image.getScaledInstance((int)cellw, (int)cellh,Image.SCALE_DEFAULT));
			((ImageIcon)prey.getIcon()).setImage(
				prey_image.getScaledInstance((int)cellw, (int)cellh, Image.SCALE_DEFAULT));

			rescale	= false;
		}

		// Paint background.
		Image	image	= background_image;
		int w	= image.getWidth(this);
		int h	= image.getHeight(this);
		if(w>0 && h>0)
		{
			for(int y=0; y<bounds.height; y+=h)
			{
				for(int x=0; x<bounds.width; x+=w)
				{
					g.drawImage(image, x, y, this);
				}
			}
		}

		WorldObject[]	objects	= cv.getVision().getObjects();

		// Paint obstacles and food.
		for(int i=0; i<objects.length; i++)
		{
			if(objects[i] instanceof Obstacle
				|| objects[i] instanceof Food)
			{
				renderObject(g, objects[i], cellw, cellh);
			}
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

		// Paint the creature visions (paint all visions before creates to avoid overpainting).
		for(int i=0; i<objects.length; i++)
		{
			if(objects[i] instanceof Creature)
			{
				// Determine lifeness.
				double	lifeness	= ((Creature)objects[i]).getLeaseticks() / (double)Environment.DEFAULT_LEASE_TICKS;

				// Paint vision on primary position.
				// Draw as concentric rectangles around creature.
				int	vision	= ((Creature)objects[i]).getVisionRange();
				Location	loc	= objects[i].getLocation();
				g.setColor(new Color(255,255, 0, (int)(64*lifeness)));
				for(int j=1; j<=vision; j++)
				{
					g.fillRect((int)(cellw*(loc.getX()-j)), (int)(cellh*(loc.getY()-j)), (int)(cellw*(j*2+1)), (int)(cellh*(j*2+1)));
				}

				// Paint vision on opposite sides of map (for "borderline" creatures).
				if(loc.getX()<vision)
				{
					for(int j=1; j<=vision; j++)
					{
						g.fillRect((int)(cellw*(loc.getX()-j+worldwidth)), (int)(cellh*(loc.getY()-j)), (int)(cellw*(j*2+1)), (int)(cellh*(j*2+1)));
					}
				}
				else if(loc.getX()>=worldwidth-vision)
				{
					for(int j=1; j<=vision; j++)
					{
						g.fillRect((int)(cellw*(loc.getX()-j-worldwidth)), (int)(cellh*(loc.getY()-j)), (int)(cellw*(j*2+1)), (int)(cellh*(j*2+1)));
					}
				}
				if(loc.getY()<vision)
				{
					for(int j=1; j<=vision; j++)
					{
						g.fillRect((int)(cellw*(loc.getX()-j+worldheight)), (int)(cellh*(loc.getY()-j)), (int)(cellw*(j*2+1)), (int)(cellh*(j*2+1)));
					}
				}
				else if(loc.getY()>=worldheight-vision)
				{
					for(int j=1; j<=vision; j++)
					{
						g.fillRect((int)(cellw*(loc.getX()-j-worldheight)), (int)(cellh*(loc.getY()-j)), (int)(cellw*(j*2+1)), (int)(cellh*(j*2+1)));
					}
				}
			}
		}

		// Paint creatures.
		for(int i=0; i<objects.length; i++)
		{
			if(objects[i] instanceof Creature)
			{
				renderObject(g, objects[i], cellw, cellh);
			}
		}
	}

	/**
	 *  Render an object on map.
	 *  @param g	The graphics object.
	 *  @param obj	The object to render.
	 *  @param cellw	The cell width.
	 *  @param cellh	The cell height.
	 */
	protected void	renderObject(Graphics g, WorldObject obj, double cellw, double cellh)
	{
		Location	loc	= obj.getLocation();
		// Calculate bounds of object (leave one pixel for cell grid).
		/*Rectangle	bounds	= new Rectangle(
			(int)(cellw*loc.getX())+1,
			(int)(cellh*loc.getY())+1,
			(int)(cellw*(loc.getX()+1)) - (int)(cellw*loc.getX()) - 2,
			(int)(cellh*(loc.getY()+1)) - (int)(cellh*loc.getY()) - 2);*/

		// Determine component.
		Component	comp;
		if(obj instanceof Obstacle)
			comp	= obstacle;
		else if(obj instanceof Food)
			comp	= food;
		else if(obj instanceof Hunter)
			comp	= hunter;
		else if(obj instanceof Prey)
			comp	= prey;
		else if(obj instanceof Observer)
			// Don't render observers.
			return;
		else
			throw new RuntimeException("Unknown type of object: "+obj);

		// Determine lifeness.
		double	lifeness	= 1.0;
		if(obj instanceof Creature)
		{
			lifeness	=  ((Creature)obj).getLeaseticks() / (double)Environment.DEFAULT_LEASE_TICKS;
		}
		
		// Paint component into map.
		/*comp.setBounds(bounds);
		g.translate(bounds.x, bounds.y);
		comp.paint(g);
		g.translate(-bounds.x, -bounds.y);*/

		// Todo: fade out creature image.
		SGUI.renderObject(g, comp, cellw, cellh, loc.getX(), loc.getY(), 1);

		// Render labels for creatures
		if(obj instanceof Creature)
		{
			// Create label component for text layout.
			Creature	creature	= (Creature)obj;
			comp	= new JLabel(creature.getName()+" "+creature.getAge()+" "+creature.getPoints());
			Color	col	= comp.getForeground();
			comp.setForeground(new Color(col.getRed(), col.getGreen(), col.getBlue(), (int)(255*lifeness)));
			Dimension	d	= comp.getPreferredSize();

			// Calculate bounds for label (centered below the creature).
			Rectangle bounds	= new Rectangle(
				(int)(cellw*(loc.getX()+0.5)) - d.width/2,
				(int)(cellh*(loc.getY()+1.5)) - d.height/2,
				d.width+1, d.height);

			// Paint label component onto map.
			comp.setBounds(bounds);
			g.translate(bounds.x, bounds.y);
			comp.paint(g);
			g.translate(-bounds.x, -bounds.y);
		}
	}

	/**
	 *  Get the tooltip text.
	 */
	public String	getToolTipText(MouseEvent event)
	{
		if(cv==null) return getToolTipText();

		int	worldwidth	= cv.getCreature().getWorldWidth();
		int	worldheight	= cv.getCreature().getWorldHeight();
		Rectangle	bounds	= getBounds();
		double cellw = bounds.getWidth()/(double)worldwidth;
		double cellh = bounds.getHeight()/(double)worldheight;

		int	x	= (int)(event.getX()/cellw);
		int	y	= (int)(event.getY()/cellh);
		return "("+x+","+ y+")";
	}

	//-------- helper methods --------

	/**
	 *  Main for testing.
	 */
	public static void main(String[] args)
	{
		MapPanel mp = new MapPanel();
		Frame f = new Frame();
		f.add("Center", mp);
		f.pack();
		f.setLocation(SGUI.calculateMiddlePosition(f));
		f.setVisible(true);
	}
}

