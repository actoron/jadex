package jadex.wfms.guicomponents;

import jadex.bdi.runtime.IBDIExternalAccess;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JDialog;

public class LoginDialog extends JDialog
{
	protected static final String LOGIN_DIALOG_TITLE = "Login";
	
	protected LoginPanel loginpanel;
	
	public LoginDialog(IBDIExternalAccess agent, Frame owner)
	{
		super(owner, LOGIN_DIALOG_TITLE, true);
		loginpanel = new LoginPanel(agent);
		add(new LoginPanel(agent));
		
		loginpanel.setLoginAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				LoginDialog.this.setVisible(false);
			}
		});
		pack();
		setSize(500, 273);
	}
	
	public LoginPanel getLoginPanel()
	{
		return loginpanel;
	}
}
