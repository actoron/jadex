package jadex.base.gui;

import jadex.commons.BrowserLauncher2;
import jadex.commons.SUtil;
import jadex.commons.gui.SGUI;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.UIDefaults;

/**
 * Button to start a web browser showing the Jadex Home page.
 */
public class JadexLogoButton extends JButton
{
	private static UIDefaults icons = new UIDefaults(new Object[]
	{
		"JadexLogo", SGUI.makeIcon(JadexLogoButton.class, "/jadex/base/gui/images/jadexlogo.png"),
		"JadexLogoV", SGUI.makeIcon(JadexLogoButton.class, "/jadex/base/gui/images/jadexlogoV.png"),
	});

	private final JToolBar tb;
	private int last_orient;

	/**
	 * Constructor for JadexLogoButton.
	 * @param tb
	 */
	public JadexLogoButton(final JToolBar tb)
	{
		super(icons.getIcon("JadexLogo"));
		this.tb = tb;
		setBorder(BorderFactory.createRaisedBevelBorder());
//      setMargin(new Insets(1,1,1,1));
		setToolTipText("Go to Jadex Home Page");
		addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					BrowserLauncher2.openURL("http://vsis-www.informatik.uni-hamburg.de/projects/jadex");
				}
				catch(java.io.IOException ex)
				{
					String txt = SUtil.wrapText("Could not open URL in browser\n\n"+ex.getMessage());
					JOptionPane.showMessageDialog(SGUI.getWindowParent(tb), txt, "Browser Error", JOptionPane.ERROR_MESSAGE);
					//ex.printStackTrace();
				}
			}
		});
		last_orient = tb.getOrientation();
	}


	/**
	 */
	public Dimension getPreferredSize()
	{
		int orient = tb.getOrientation();
		if(orient!=last_orient)
		{
			switch(tb.getOrientation())
			{
				case JToolBar.HORIZONTAL:
					setIcon(icons.getIcon("JadexLogo"));
					break;
				case JToolBar.VERTICAL:
					setIcon(icons.getIcon("JadexLogoV"));
			}
			last_orient = orient;
		}

		return super.getPreferredSize();
	}
}