package jadex.wfms.guicomponents;

import jadex.bdi.runtime.IBDIExternalAccess;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JDialog;

public class BDILoginDialog extends JDialog
{
	protected static final String LOGIN_DIALOG_TITLE = "Login";
	
	protected BDILoginPanel loginpanel;
	
	public BDILoginDialog(IBDIExternalAccess agent, Frame owner)
	{
		super(owner, LOGIN_DIALOG_TITLE, true);
		loginpanel = new BDILoginPanel(agent);
		add(new BDILoginPanel(agent));
		
		loginpanel.setLoginAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				BDILoginDialog.this.setVisible(false);
			}
		});
		pack();
		setSize(500, 273);
	}
	
	public BDILoginPanel getLoginPanel()
	{
		return loginpanel;
	}
}
