package jadex.bdi.examples.blackjack.gui;

import javax.swing.ImageIcon;
import javax.swing.UIDefaults;

import jadex.commons.gui.SGUI;

/**
 *  This class serves as an image-loader and image-cache.
 *	Images are often requested during a blackjack-game, and
 *	for convenience- as well as performance-purposes these
 *	images are loaded and cached by this class.
 */
public class GUIImageLoader
{
	/** The image icons. */
	public static final UIDefaults	icons	= new UIDefaults(new Object[]
	{
		"logo",	SGUI.makeIcon(GUIImageLoader.class, "/jadex/bdi/examples/blackjack/gui/images/logo.png"),
		"blackjack",	SGUI.makeIcon(GUIImageLoader.class, "/jadex/bdi/examples/blackjack/gui/images/blackjack.png"),
		"heart_small_d",	SGUI.makeIcon(GUIImageLoader.class, "/jadex/bdi/examples/blackjack/gui/images/heart_small_d.png"),
		"heart_small_s",	SGUI.makeIcon(GUIImageLoader.class, "/jadex/bdi/examples/blackjack/gui/images/heart_small_s.png"),
		"heart_small_m",	SGUI.makeIcon(GUIImageLoader.class, "/jadex/bdi/examples/blackjack/gui/images/heart_small_m.png"),
		"statistics",	SGUI.makeIcon(GUIImageLoader.class, "/jadex/bdi/examples/blackjack/gui/images/statistics.png"),
		"bet",	SGUI.makeIcon(GUIImageLoader.class, "/jadex/bdi/examples/blackjack/gui/images/bet.png"),
		"hit",	SGUI.makeIcon(GUIImageLoader.class, "/jadex/bdi/examples/blackjack/gui/images/hit.png"),
		"stand",	SGUI.makeIcon(GUIImageLoader.class, "/jadex/bdi/examples/blackjack/gui/images/stand.png"),

		"j_club",	SGUI.makeIcon(GUIImageLoader.class, "/jadex/bdi/examples/blackjack/gui/images/j_club.png"),
		"j_diamond",	SGUI.makeIcon(GUIImageLoader.class, "/jadex/bdi/examples/blackjack/gui/images/j_diamond.png"),
		"j_heart",	SGUI.makeIcon(GUIImageLoader.class, "/jadex/bdi/examples/blackjack/gui/images/j_heart.png"),
		"j_spade",	SGUI.makeIcon(GUIImageLoader.class, "/jadex/bdi/examples/blackjack/gui/images/j_spade.png"),
		"q_club",	SGUI.makeIcon(GUIImageLoader.class, "/jadex/bdi/examples/blackjack/gui/images/q_club.png"),
		"q_diamond",	SGUI.makeIcon(GUIImageLoader.class, "/jadex/bdi/examples/blackjack/gui/images/q_diamond.png"),
		"q_heart",	SGUI.makeIcon(GUIImageLoader.class, "/jadex/bdi/examples/blackjack/gui/images/q_heart.png"),
		"q_spade",	SGUI.makeIcon(GUIImageLoader.class, "/jadex/bdi/examples/blackjack/gui/images/q_spade.png"),
		"k_club",	SGUI.makeIcon(GUIImageLoader.class, "/jadex/bdi/examples/blackjack/gui/images/k_club.png"),
		"k_diamond",	SGUI.makeIcon(GUIImageLoader.class, "/jadex/bdi/examples/blackjack/gui/images/k_diamond.png"),
		"k_heart",	SGUI.makeIcon(GUIImageLoader.class, "/jadex/bdi/examples/blackjack/gui/images/k_heart.png"),
		"k_spade",	SGUI.makeIcon(GUIImageLoader.class, "/jadex/bdi/examples/blackjack/gui/images/k_spade.png"),
		"a_club",	SGUI.makeIcon(GUIImageLoader.class, "/jadex/bdi/examples/blackjack/gui/images/a_club.png"),
		"a_diamond",	SGUI.makeIcon(GUIImageLoader.class, "/jadex/bdi/examples/blackjack/gui/images/a_diamond.png"),
		"a_heart",	SGUI.makeIcon(GUIImageLoader.class, "/jadex/bdi/examples/blackjack/gui/images/a_heart.png"),
		"a_spade",	SGUI.makeIcon(GUIImageLoader.class, "/jadex/bdi/examples/blackjack/gui/images/a_spade.png"),
		"a_spade",	SGUI.makeIcon(GUIImageLoader.class, "/jadex/bdi/examples/blackjack/gui/images/a_spade.png")
	});

	static
	{
		for(int i=2; i<11; i++)
		{
			icons.put(i+"_club", SGUI.makeIcon(GUIImageLoader.class, "/jadex/bdi/examples/blackjack/gui/images/"+i+"_club.png"));
			icons.put(i+"_diamond", SGUI.makeIcon(GUIImageLoader.class, "/jadex/bdi/examples/blackjack/gui/images/"+i+"_diamond.png"));
			icons.put(i+"_heart", SGUI.makeIcon(GUIImageLoader.class, "/jadex/bdi/examples/blackjack/gui/images/"+i+"_heart.png"));
			icons.put(i+"_spade", SGUI.makeIcon(GUIImageLoader.class, "/jadex/bdi/examples/blackjack/gui/images/"+i+"_spade.png"));
		}
	}

	/**
	 *	Load the image of the card passed as a parameter
	 *	@param card String-representation of the card to load
	 *	the image for, cardName == fileName
	 */
	public static ImageIcon getImage(String card)
	{
		//System.out.println("Loading:"+card);

		assert card!=null;
		assert icons.getIcon(card.toLowerCase())!=null: "Could not load: "+card.toLowerCase();

		return (ImageIcon)icons.getIcon(card.toLowerCase());
	}

	/**
	 *  Main for testing.
	 */
	public static void main(String[] args)
	{
		System.out.println(getImage("7_club"));
		System.out.println(getImage("q_club"));
	}

	/**
	 *  Get bthe height of the icons.
	 */
	public static int	getCardIconHeight()
	{
		return getImage("7_club").getIconHeight();
	}
}