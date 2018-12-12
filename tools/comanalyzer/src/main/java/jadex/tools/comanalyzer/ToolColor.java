package jadex.tools.comanalyzer;

import java.awt.Color;
import java.awt.Paint;


/**
 * Class to extend the number of Colors available. This extends the
 * java.awt.Color object and extends the number of final Colors publically
 * accessible.
 */
public class ToolColor extends Color
{

	/** A very dark red color. */
	public static final Color VERY_DARK_RED = new Color(0x80, 0x00, 0x00);

	/** A dark red color. */
	public static final Color DARK_RED = new Color(0xc0, 0x00, 0x00);

	/** A light red color. */
	public static final Color LIGHT_RED = new Color(0xFF, 0x40, 0x40);

	/** A very light red color. */
	public static final Color VERY_LIGHT_RED = new Color(0xFF, 0x80, 0x80);

	/** A very dark yellow color. */
	public static final Color VERY_DARK_YELLOW = new Color(0x80, 0x80, 0x00);

	/** A dark yellow color. */
	public static final Color DARK_YELLOW = new Color(0xC0, 0xC0, 0x00);

	/** A light yellow color. */
	public static final Color LIGHT_YELLOW = new Color(0xFF, 0xFF, 0x40);

	/** A very light yellow color. */
	public static final Color VERY_LIGHT_YELLOW = new Color(0xFF, 0xFF, 0x80);

	/** A very dark green color. */
	public static final Color VERY_DARK_GREEN = new Color(0x00, 0x80, 0x00);

	/** A dark green color. */
	public static final Color DARK_GREEN = new Color(0x00, 0xC0, 0x00);

	/** A light green color. */
	public static final Color LIGHT_GREEN = new Color(0x40, 0xFF, 0x40);

	/** A very light green color. */
	public static final Color VERY_LIGHT_GREEN = new Color(0x80, 0xFF, 0x80);

	/** A very dark cyan color. */
	public static final Color VERY_DARK_CYAN = new Color(0x00, 0x80, 0x80);

	/** A dark cyan color. */
	public static final Color DARK_CYAN = new Color(0x00, 0xC0, 0xC0);

	/** A light cyan color. */
	public static final Color LIGHT_CYAN = new Color(0x40, 0xFF, 0xFF);

	/** Aa very light cyan color. */
	public static final Color VERY_LIGHT_CYAN = new Color(0x80, 0xFF, 0xFF);

	/** A very dark blue color. */
	public static final Color VERY_DARK_BLUE = new Color(0x00, 0x00, 0x80);

	/** A dark blue color. */
	public static final Color DARK_BLUE = new Color(0x00, 0x00, 0xC0);

	/** A light blue color. */
	public static final Color LIGHT_BLUE = new Color(0x40, 0x40, 0xFF);

	/** A very light blue color. */
	public static final Color VERY_LIGHT_BLUE = new Color(0x80, 0x80, 0xFF);

	/** A very dark magenta/purple color. */
	public static final Color VERY_DARK_MAGENTA = new Color(0x80, 0x00, 0x80);

	/** A dark magenta color. */
	public static final Color DARK_MAGENTA = new Color(0xC0, 0x00, 0xC0);

	/** A light magenta color. */
	public static final Color LIGHT_MAGENTA = new Color(0xFF, 0x40, 0xFF);

	/** A very light magenta color. */
	public static final Color VERY_LIGHT_MAGENTA = new Color(0xFF, 0x80, 0xFF);

	/**
	 * Creates a color with red, green and blue values in range 0-255.
	 * 
	 * @param r the red component.
	 * @param g the green component.
	 * @param b the blue component.
	 */
	public ToolColor(int r, int g, int b)
	{
		super(r, g, b);
	}

	/**
	 * Convenience method to return an array of colors.
	 * 
	 * @return An array of colors..
	 */
	public static Paint[] createDefaultPaintArray()
	{

		return new Paint[]{new Color(0xFF, 0x55, 0x55), new Color(0x55, 0x55, 0xFF), new Color(0x55, 0xFF, 0x55), new Color(0xFF, 0xFF, 0x55), new Color(0xFF, 0x55, 0xFF),
				new Color(0x55, 0xFF, 0xFF), Color.pink, Color.gray, ToolColor.DARK_RED, ToolColor.DARK_BLUE, ToolColor.DARK_GREEN, ToolColor.DARK_YELLOW, ToolColor.DARK_MAGENTA, ToolColor.DARK_CYAN,
				Color.darkGray, ToolColor.LIGHT_RED, ToolColor.LIGHT_BLUE, ToolColor.LIGHT_GREEN, ToolColor.LIGHT_YELLOW, ToolColor.LIGHT_MAGENTA, ToolColor.LIGHT_CYAN, Color.lightGray,
				ToolColor.VERY_DARK_RED, ToolColor.VERY_DARK_BLUE, ToolColor.VERY_DARK_GREEN, ToolColor.VERY_DARK_YELLOW, ToolColor.VERY_DARK_MAGENTA, ToolColor.VERY_DARK_CYAN,
				ToolColor.VERY_LIGHT_RED, ToolColor.VERY_LIGHT_BLUE, ToolColor.VERY_LIGHT_GREEN, ToolColor.VERY_LIGHT_YELLOW, ToolColor.VERY_LIGHT_MAGENTA, ToolColor.VERY_LIGHT_CYAN};
	}

}
