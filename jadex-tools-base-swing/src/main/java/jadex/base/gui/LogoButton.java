package jadex.base.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;

import jadex.commons.BrowserLauncher2;
import jadex.commons.SUtil;
import jadex.commons.gui.SGUI;

/**
 * Button to start a web browser showing the a home page.
 */
public class LogoButton extends JButton
{
	private int last_orient;
	private Icon	logoh;
	private Icon	logov;

	/**
	 * Constructor for JadexLogoButton.
	 * @param tb
	 */
	public LogoButton(Icon logoh, Icon logov, String tooltip, final String linkurl)
	{
		super(logoh);
		this.logoh	= logoh;
		this.logov	= logov;
		setBorder(BorderFactory.createRaisedBevelBorder());
//      setMargin(new Insets(1,1,1,1));
		setToolTipText(tooltip);
		addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					BrowserLauncher2.openURL(linkurl);
				}
				catch(java.io.IOException ex)
				{
					String txt = SUtil.wrapText("Could not open URL in browser\n\n"+ex.getMessage());
					JOptionPane.showMessageDialog(SGUI.getWindowParent(LogoButton.this), txt, "Browser Error", JOptionPane.ERROR_MESSAGE);
					//ex.printStackTrace();
				}
			}
		});
		last_orient = -1;
	}


	/**
	 */
	public Dimension getPreferredSize()
	{
		JToolBar	tb	= null;
		Component	comp	= this;
		while(comp!=null && tb==null)
		{
			if(comp instanceof JToolBar)
			{
				tb	= (JToolBar) comp;
			}
			comp	= comp.getParent();
		}
		
		if(tb!=null)
		{
			int orient = tb.getOrientation();
			if(orient!=last_orient)
			{
				switch(tb.getOrientation())
				{
					case JToolBar.HORIZONTAL:
						setIcon(logoh);
						break;
					case JToolBar.VERTICAL:
						setIcon(logov);
				}
				last_orient = orient;
			}
		}

		return super.getPreferredSize();
	}
}