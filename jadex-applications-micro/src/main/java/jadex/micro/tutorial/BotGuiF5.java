package jadex.micro.tutorial;

import jadex.base.gui.SwingDefaultResultListener;
import jadex.base.gui.componentviewer.AbstractComponentViewerPanel;
import jadex.bridge.IComponentChangeEvent;
import jadex.bridge.IComponentListener;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.IFilter;
import jadex.commons.future.IFuture;
import jadex.micro.IPojoMicroAgent;
import jadex.xml.annotation.XMLClassname;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

/**
 *  The gui for the chat bot allows
 *  changing the keyword and reply message. 
 *
 */
public class BotGuiF5 extends AbstractComponentViewerPanel
{
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
		
		// Fetch initial values for text fields.
		getActiveComponent().scheduleStep(new IComponentStep()
		{
			@XMLClassname("fetch_values")
			public Object execute(IInternalAccess ia)
			{
				ChatBotF5Agent	chatbot	= (ChatBotF5Agent)((IPojoMicroAgent)ia).getPojoAgent();
				return new String[]{chatbot.getKeyword(), chatbot.getReply()};
			}
		}).addResultListener(new SwingDefaultResultListener()
		{
			public void customResultAvailable(Object result)
			{
				String[]	values	= (String[])result;
				tfkeyword.setText(values[0]);
				tfreply.setText(values[1]);
			}
		});
		
		// Set new keyword.
		tfkeyword.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				final String	keyword	= tfkeyword.getText();
				getActiveComponent().scheduleStep(new IComponentStep()
				{
					@XMLClassname("set_keyword")
					public Object execute(IInternalAccess ia)
					{
						ChatBotF5Agent	chatbot	= (ChatBotF5Agent)((IPojoMicroAgent)ia).getPojoAgent();
						chatbot.setKeyword(keyword);
						return null;
					}
				});
			}
		});
		
		// Set new reply message.
		tfkeyword.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				final String	reply	= tfreply.getText();
				getActiveComponent().scheduleStep(new IComponentStep()
				{
					@XMLClassname("set_reply")
					public Object execute(IInternalAccess ia)
					{
						ChatBotF5Agent	chatbot	= (ChatBotF5Agent)((IPojoMicroAgent)ia).getPojoAgent();
						chatbot.setReply(reply);
						return null;
					}
				});
			}
		});
		
		getActiveComponent().addComponentListener(new IComponentListener()
		{
			public IFilter getFilter()
			{
				return IFilter.ALWAYS;
			}
			
			public IFuture eventOccured(IComponentChangeEvent cce)
			{
				return IFuture.DONE;
			}
		});
		
		return panel;
	}
}
