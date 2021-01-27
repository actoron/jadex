package jadex.micro.examples.mandelbrot_new;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import jadex.bridge.IExternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.types.appstore.IAppGui;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;

/**
 * 
 */
public class MandelbrotPanel extends JPanel //implements IAppGui
{
	/** The agent. */
	protected IExternalAccess agent;
	
	/** The display panel. */
	protected DisplayPanel dispanel;
	
	/**
	 * 
	 */
	public MandelbrotPanel()
	{
		this.setLayout(new BorderLayout());
//		this.add(new JButton("test"), BorderLayout.CENTER);
	}
	
	/**
	 * 
	 */
	public IFuture<Void> init(IExternalAccess agent)
	{
		this.agent = agent;
		this.dispanel = new DisplayPanel(agent);
		this.add(dispanel, BorderLayout.CENTER);
		return IFuture.DONE;
	}
	
	/**
	 * 
	 */
	public static void main(String[] args)
	{
		JFrame fr = new JFrame();
		fr.add(new MandelbrotPanel());
		fr.setLocation(SGUI.calculateMiddlePosition(fr));
		fr.pack();
		fr.setVisible(true);
	}
}
