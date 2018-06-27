package jadex.micro.tutorial;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import jadex.base.gui.componentviewer.AbstractComponentViewerPanel;

/**
 *  The gui for the chat bot allows
 *  changing the keyword and reply message. 
 */
public class BotGuiF2 extends AbstractComponentViewerPanel
{
	/**
	 *  Get the component.
	 */
	public JComponent getComponent()
	{
		JPanel	panel	= new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "ChatBot Settings"));
		
		final JTextField	tfkeyword	= new JTextField();
		final JTextField	tfreply	= new JTextField();
		
		GridBagConstraints	gbc	= new GridBagConstraints();
		gbc.fill	= GridBagConstraints.HORIZONTAL;
		gbc.gridy	= 0;
		gbc.weightx	= 0;
		panel.add(new JLabel("keyword"), gbc);
		gbc.weightx	= 1;
		panel.add(tfkeyword, gbc);
		gbc.gridy	= 1;
		gbc.weightx	= 0;
		panel.add(new JLabel("reply"), gbc);
		gbc.weightx	= 1;
		panel.add(tfreply, gbc);
		
		return panel;
	}
}
