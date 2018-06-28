package jadex.base.gui;

import javax.swing.UIDefaults;

import jadex.commons.gui.SGUI;

/**
 * Button to start a web browser showing the Jadex Home page.
 */
public class JadexLogoButton extends LogoButton
{
	private static UIDefaults icons = new UIDefaults(new Object[]
	{
		"JadexLogo", SGUI.makeIcon(JadexLogoButton.class, "/jadex/base/gui/images/jadexlogo.png"),
		"JadexLogoV", SGUI.makeIcon(JadexLogoButton.class, "/jadex/base/gui/images/jadexlogoV.png")
	});

	/**
	 * Constructor for JadexLogoButton.
	 * @param tb
	 */
	public JadexLogoButton()
	{
		super(icons.getIcon("JadexLogo"), icons.getIcon("JadexLogoV"), "Go to Jadex Home Page", "http://www.activecomponents.org/");
	}
}