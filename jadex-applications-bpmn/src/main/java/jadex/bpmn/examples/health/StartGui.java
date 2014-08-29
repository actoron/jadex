/**
 * 
 */
package jadex.bpmn.examples.health;

import java.awt.BorderLayout;

import javax.swing.JPanel;

/**
 *
 */
public class StartGui
{
	/**
	 * 
	 */
	protected JPanel createPanel()
	{
		JPanel pan = new JPanel(new BorderLayout());
		EmailPanel ep = new EmailPanel();
		
		
		return pan;
	}
	
	/**
	 *  Main for starting.
	 */
	public static void main(String[] args)
	{
		
	}
	
	
}
