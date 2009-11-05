// Title         : Agile Processes
// Description   : Demonstrator for more flexibility in large business processes
//                 using beliefs, desires and intentions.
// Copyright (c) : 2005-2007 DaimlerChrysler AG All right reserved
// Company       : MentalProof Software GmbH
//
package com.daimler.client.gui.components.parts;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.plaf.basic.BasicButtonUI;

import com.daimler.util.swing.icon.ShadowedIcon;

/**
 * The Help-Button for the input fields if help is available.
 * 
 * @author Christian Wiech (christian.wiech@mentalproof.com)
 * 
 */
public class HelpButton extends JButton implements ActionListener
{
	/**
	 * The Icon for the Button (a question mark).
	 */
	private ImageIcon icon;

	/**
	 * The help message or an URL to the help message (e.g. an HTML-file).
	 */
	private String helpMessage = "No Help available";

	/**
	 * Constructs a help button that will display <code>helpMessage</code> on
	 * button press.
	 * 
	 * @param helpMessage
	 *            the message or URL to a file that will be displayed as help
	 *            message
	 */
	public HelpButton(String helpMessage)
	{
		this(helpMessage, (new JButton()).getBackground());
	}

	/**
	 * Constructs a help button with specified background color that will
	 * display <code>helpMessage</code> on button press.
	 * 
	 * @param helpMessage
	 *            the message or URL to a file that will be displayed as help
	 *            message
	 * @param bgColor
	 *            the background color for the button
	 */
	public HelpButton(String helpMessage, Color bgColor)
	{
		super();
		icon = new ImageIcon(Toolkit.getDefaultToolkit().createImage(
				ClassLoader.getSystemResource(getClass().getPackage().getName()
						.replaceAll("components\\.parts", "images.")
						.replaceAll("\\.", "/")
						+ "HelpIcon.png")).getScaledInstance(17, 17,
				Image.SCALE_SMOOTH));
		setIcon(icon);
		addActionListener(this);
		setUI(bbui);
		setRolloverEnabled(true);
		setBackground(bgColor);
		setBorder(BorderFactory.createEmptyBorder());
		setRolloverIcon(new ShadowedIcon(icon));
		setToolTipText("Help");
		addActionListener(this);
		this.helpMessage = helpMessage;
		setBackground(bgColor);
	}

	/**
	 * Calls the HelpBrowser to show the help message.
	 * 
	 * @param e
	 *            {@inheritDoc}
	 */
	public void actionPerformed(ActionEvent e)
	{
		// FIXME: Put HelpBrowser here!
		// GuiBuilderConnector.getTheHelpBrowser().show(getTheHelpMessage());
		GuiHelpBrowser b = new GuiHelpBrowser();
		b.show(getHelpMessage());
	}

	/**
	 * Returns the help message.
	 * 
	 * @return the <code>theHelpMessage</code>
	 */
	public String getHelpMessage()
	{
		return helpMessage;
	}

	/**
	 * Sets the help message that will be displayed on button click.
	 * 
	 * @param theHelpMessage
	 *            the message to display
	 */
	public void setHelpMessage(String theHelpMessage)
	{
		this.helpMessage = theHelpMessage;
	}

	/**
	 * Custom ButtonUI that prevents the button from being repaintet when
	 * pressed. So no pseudo shadows will be drawn.
	 */
	private static final BasicButtonUI bbui = new BasicButtonUI()
	{
		// this way the rectangle for the button won't be
		// painted when it is pressed
		protected void paintButtonPressed(final Graphics g,
				final AbstractButton b)
		{
		}
	};
}
