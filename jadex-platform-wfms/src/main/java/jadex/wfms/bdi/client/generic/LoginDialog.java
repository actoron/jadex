package jadex.wfms.bdi.client.generic;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class LoginDialog extends JDialog
{
	private static final String LOGIN_DIALOG_TITLE = "Login";
	
	private static final String USER_NAME_LABEL = "Username";
	private static final String PASSWORD_LABEL = "Password";
	
	private static final String LOGIN_BUTTON_TEXT = "Login";
	
	private static final String INITIAL_USERNAME_TEXT = "TestUser";
	
	private JTextField userNameField;
	
	private JTextField passwordField;
	
	public LoginDialog(Frame owner)
	{
		super(owner, LOGIN_DIALOG_TITLE, true);
		
		setLayout(new GridBagLayout());
		
		addUserNameField();
		addPasswordField();
		
		JButton loginButton = new JButton();
		loginButton.setAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				LoginDialog.this.setVisible(false);
			}
		});
		loginButton.setText(LOGIN_BUTTON_TEXT);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.weighty = 1;
		//gbc.gridwidth = 2;
		gbc.gridy = 4;
		gbc.anchor = GridBagConstraints.SOUTH;
		getContentPane().add(loginButton, gbc);
		
		pack();
		setSize(400, 150);
	}
	
	public String getUserName()
	{
		return userNameField.getText();
	}
	
	public String getPassword()
	{
		return passwordField.getText();
	}
	
	private void addUserNameField()
	{
		JLabel userNameLabel = new JLabel(USER_NAME_LABEL);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.gridx = 0;
		gbc.gridy = 0;
		add(userNameLabel, gbc);
		
		userNameField = new JTextField();
		userNameField.setText(INITIAL_USERNAME_TEXT);
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 2;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.ipadx = 30;
		add(userNameField, gbc);
	}
	
	private void addPasswordField()
	{
		JLabel passwordLabel = new JLabel(PASSWORD_LABEL);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.gridx = 0;
		gbc.gridy = 2;
		add(passwordLabel, gbc);
		
		passwordField = new JTextField();
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 2;
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.ipadx = 30;
		add(passwordField, gbc);
	}
}
