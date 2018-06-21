package jadex.base.gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIDefaults;
import javax.swing.border.Border;

import jadex.bridge.VersionInfo;
import jadex.commons.gui.BrowserPane;
import jadex.commons.gui.SGUI;


/**
 *  Display Jadex info.
 */
public class AboutDialog extends JAutoPositionDialog
{
	//-------- static part --------

	/** The image icons. */
	protected static final UIDefaults	icons	= new UIDefaults(new Object[]{"logo",
		SGUI.makeIcon(AboutDialog.class, "/jadex/base/gui/images/jadex_logo.png"),});

	/** The text to display. */
	public String infotext;

	//-------- constructors --------

	/**
	 *  Open the gui.
	 */
	public AboutDialog(Frame owner)
	{
		super(owner);
		this.infotext = generateText();
		
		setTitle("About Jadex");
		Container cp = getContentPane();
		cp.setLayout(new GridBagLayout());
		ImageIcon logo = (ImageIcon)icons.getIcon("logo");

		BrowserPane bp = new BrowserPane();
		bp.setText(infotext);
		bp.setDefaultOpenMode(true);

		JLabel lab = new JLabel(logo);
		cp.add(lab, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
		cp.add(bp, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(0, 10, 10, 10), 0, 0));
		Color bg = new Color(247, 248, 253);
		//Color bg = new Color(0xd8, 0xdf, 0xf2);
		cp.setBackground(bg);
		bp.setBackground(bg);
		lab.setBackground(bg);

		setUndecorated(true);
		Border bl = BorderFactory.createLineBorder(Color.black);
		((JComponent)cp).setBorder(bl);
		addWindowFocusListener(new WindowFocusListener()
		{
			public void windowLostFocus(WindowEvent e)	{dispose();}
			public void windowGainedFocus(WindowEvent e){/*NOP*/}
		});
		pack();
		setVisible(true);
	}
	
	/**
	 *  Generate the text to display.
	 */
	public String generateText()
	{
		infotext = "<head/><body>(c) 2002-2014<br>"
			+ "Alexander Pokahr, Lars Braubach<br>"
			+ "All rights reserved<br>";
		String rn = VersionInfo.getInstance().getVersion();
		String rd = VersionInfo.getInstance().getNumberDateString();
		infotext += "Version " + rn + " (" + rd + ")<br>";
		infotext += "<a href=\"http://www.activecomponents.org\">http://www.activecomponents.org</a><br>";
		infotext += "</body>";
		
		return infotext;
	}
	
	/**
	 *  Main for testing.
	 *  @param args
	 */
	public static void main(String[] args)
	{
		JFrame f = new JFrame();
		f.setSize(500, 400);
		f.setVisible(true);
		new AboutDialog(f);
	}
}
