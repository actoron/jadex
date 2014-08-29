/**
 * 
 */
package jadex.bpmn.examples.health;

import jadex.bridge.service.types.email.Email;
import jadex.commons.gui.PropertiesPanel;

import java.util.StringTokenizer;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *  Simple panel to enter email data.
 */
public class EmailPanel extends JPanel
{
	/** The sender. */
	protected JTextField sender;
	
	/** The receivers. */
	protected JTextField receivers;
	
	/** The subject. */
	protected JTextField subject;
	
	/** The content. */
	protected JTextArea content;
	
	/**
	 *  Create a new EmailPanel.java.
	 */
	public EmailPanel()
	{
		PropertiesPanel pp = new PropertiesPanel();
		sender = pp.createTextField("Sender");
		receivers = pp.createTextField("Receivers");
		subject = pp.createTextField("Subject");
		content = new JTextArea(20, 20);
		pp.addComponent("Text", content);
	}
	
	/**
	 * 
	 */
	public void setEmail(Email email)
	{
		sender.setText(email.getSender());
		String[] recs = email.getReceivers();
		StringBuffer rec = new StringBuffer();
		if(recs!=null)
		{
			for(int i=0; i<recs.length; i++)
			{
				rec.append(recs[i]);
				if(i+1<recs.length)
					rec.append(", ");
			}
		}
		receivers.setText(rec.toString());
	}
	
	/**
	 *  Get the email from the data entered.
	 */
	public Email getEmail()
	{
		Email ret = new Email();
		ret.setSender(sender.getText());
		String rec = receivers.getText();
		StringTokenizer stok = new StringTokenizer(rec, ",");
		String[] recs = new String[stok.countTokens()];
		for(int i=0; stok.hasMoreTokens(); i++)
		{
			recs[i] = stok.nextToken();
		}
		ret.setReceivers(new String[]{receivers.getText()});
		ret.setSubject(subject.getText());
		ret.setContent(content.getText());
		return ret;
	}
}
