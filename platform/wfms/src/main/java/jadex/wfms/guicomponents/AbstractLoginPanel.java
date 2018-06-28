package jadex.wfms.guicomponents;

import jadex.commons.collection.IndexMap;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.wfms.gui.images.SImage;
import jadex.wfms.service.IExternalWfmsService;

import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ListDataListener;

public abstract class AbstractLoginPanel extends JPanel
{
	protected static final String LOGIN_DIALOG_TITLE = "Login";
	
	protected static final String USER_NAME_LABEL = "Username: ";
	protected static final String PASSWORD_LABEL = "Password: ";
	protected static final String WFMS_LABEL = "Server: ";
	
	protected static final String LOGIN_BUTTON_TEXT = "Login";
	
	protected static final String INITIAL_USERNAME_TEXT = "TestAdmin";
	
	protected JTextField userNameField;
	
	protected JTextField passwordField;
	
	protected JComboBox wfmsChooser;
	
	protected JButton loginButton;
	
	//protected IBDIExternalAccess agent;
	
	protected boolean connect = false;
	
	protected Action loginaction;
	
	public AbstractLoginPanel()
	{
		//super(owner, LOGIN_DIALOG_TITLE, true);
		
		//this.agent = agent;
		
		setLayout(new GridBagLayout());
		
		addUserNameField();
		addPasswordField();
		addWfmsField();
		
		try
		{
			final BufferedImage logo = ImageIO.read(SImage.class.getResource("/" + SImage.IMAGE_PATH + "go4flexlogo.png"));
			JPanel logoFiller = new JPanel()
			{
				public void paint(java.awt.Graphics g)
				{
					((Graphics2D)g).setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
					double sf = Math.min(getWidth() / (double) logo.getWidth(), getHeight() / (double) logo.getHeight());
					int sizeX = (int)((logo.getWidth() - 1) * sf);
					int sizeY = (int)((logo.getHeight() - 1) * sf);
					int minX = (int)((getWidth() - 1 - sizeX) * 0.5);
					int minY = (int)((getHeight() - 1 - sizeY) * 0.5);
					g.drawImage(logo, minX, minY, minX + sizeX, minY + sizeY, 0, 0, logo.getWidth() - 1, logo.getHeight() - 1, null);
				}
			};
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridy = 0;
			gbc.gridwidth = 3;
			gbc.weightx = 1;
			gbc.weighty = 1;
			gbc.fill = GridBagConstraints.BOTH;
			add(logoFiller, gbc);
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}
		
		JLabel lineFiller = new JLabel(" ");
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridy = 4;
		gbc.gridwidth = 3;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(lineFiller, gbc);
		
		JPanel buttonFiller = new JPanel();
		gbc = new GridBagConstraints();
		gbc.gridy = 5;
		gbc.gridwidth = 2;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(buttonFiller, gbc);
		
		loginButton = new JButton();
		loginButton.setAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				connect = true;
				if (loginaction != null)
					loginaction.actionPerformed(e);
			}
		});
		loginButton.setEnabled(false);
		loginButton.setText(LOGIN_BUTTON_TEXT);
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 5;
		gbc.insets = new Insets(0, 0, 10, 10);
		gbc.anchor = GridBagConstraints.EAST;
		add(loginButton, gbc);
	}
	
	public void setLoginAction(Action action)
	{
		this.loginaction = action;
	}
	
	public String getUserName()
	{
		return userNameField.getText();
	}
	
	public String getPassword()
	{
		return passwordField.getText();
	}
	
	public IExternalWfmsService getWfms()
	{
		return (IExternalWfmsService)((MapComboModel) wfmsChooser.getModel()).getSelectedValue();
	}
	
	public boolean isConnect()
	{
		return connect;
	}
	
	protected void addUserNameField()
	{
		JLabel userNameLabel = new JLabel(USER_NAME_LABEL);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = 1;
		gbc.weightx = 0;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.insets = new Insets(5, 0, 0, 0);
		add(userNameLabel, gbc);
		
		userNameField = new JTextField();
		userNameField.setText(INITIAL_USERNAME_TEXT);
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = 1;
		gbc.weightx = 3;
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.insets = new Insets(5, 0, 0, 0);
		add(userNameField, gbc);
	}
	
	protected void addPasswordField()
	{
		JLabel passwordLabel = new JLabel(PASSWORD_LABEL);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = 1;
		gbc.weightx = 0;
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.insets = new Insets(5, 0, 0, 0);
		add(passwordLabel, gbc);
		
		passwordField = new JTextField();
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = 1;
		gbc.weightx = 3;
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.insets = new Insets(5, 0, 0, 0);
		add(passwordField, gbc);
	}
	
	protected void addWfmsField()
	{
		JLabel passwordLabel = new JLabel(WFMS_LABEL);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = 1;
		gbc.weightx = 0;
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.insets = new Insets(5, 0, 0, 0);
		add(passwordLabel, gbc);
		
		wfmsChooser = new JComboBox();
		wfmsChooser.setEditable(false);
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = 1;
		gbc.weightx = 3;
		gbc.gridx = 1;
		gbc.gridy = 3;
		gbc.insets = new Insets(5, 0, 0, 0);
		add(wfmsChooser, gbc);
		
		JButton refreshButton = new JButton();
		refreshButton.setAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				updateWfmsList();
			}
		});
		refreshButton.setIcon(SImage.createImageIcon("new_refresh_small.png"));
		refreshButton.setMargin(new Insets(1,1,1,1));
		gbc = new GridBagConstraints();
		gbc.gridwidth = 1;
		gbc.gridx = 2;
		gbc.gridy = 3;
		gbc.insets = new Insets(5, 0, 0, 0);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		add(refreshButton, gbc);
	}
	
	protected void updateWfmsList()
	{
		discoverWfms().addResultListener(new SwingDefaultResultListener()
		{
			public void customResultAvailable(Object result)
			{
				final Collection coll = (Collection) result;
				System.out.println("NO " + coll.size());
				getWfmsNames(coll).addResultListener(new SwingDefaultResultListener()
				{
					public void customResultAvailable(Object result)
					{
						Map data = (Map) result;
						wfmsChooser.setModel(new MapComboModel(data));
						if (!data.isEmpty())
						{
							wfmsChooser.setSelectedIndex(0);
							loginButton.setEnabled(true);
						}
					}
				});
			}
		});
	}
	
	protected IFuture getWfmsNames(final Collection wfmslist)
	{
		final Future ret = new Future();
		final List idlist = new ArrayList();
		
		CounterResultListener wfmsCounter = new CounterResultListener(wfmslist.size(), true, new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				Object[] ids = idlist.toArray();
				Object[] wfms = wfmslist.toArray();
				Map data = new HashMap();
				for (int i = 0; i < ids.length; ++i)
					if (ids[i] != null)
						data.put(ids[i], wfms[i]);
				ret.setResult(data);
			}
		})
		{
			public boolean intermediateExceptionOccurred(
					Exception exception)
			{
				idlist.add(null);
				return false;
			}
			
			public void intermediateResultAvailable(
					Object result)
			{
				idlist.add(result);
			}
		};
		
		for (Iterator it = wfmslist.iterator(); it.hasNext(); )
		{
			IExternalWfmsService wfms = (IExternalWfmsService) it.next();
			getWfmsName(wfms).addResultListener(wfmsCounter);
		}
		
		return ret;
	}
	
	protected abstract IFuture discoverWfms();
	
	protected abstract IFuture getWfmsName(IExternalWfmsService wfms);
	
	protected static class MapComboModel implements ComboBoxModel
	{
		protected IndexMap data;
		
		protected Object selection;
		
		public MapComboModel(Map data)
		{
			List keys = new ArrayList(data.keySet());
			Collections.sort(keys, new Comparator()
			{
				protected Collator collator = Collator.getInstance();
				public int compare(Object o1, Object o2)
				{
					return collator.compare(o1.toString(), o2.toString());
				}
				
				public int hashCode()
				{
					return toString().hashCode();
				}
			});
			this.data = new IndexMap(keys, data);
		}
		
		public Object getSelectedItem()
		{
			return selection;
		}

		public void setSelectedItem(Object anItem)
		{
			selection = anItem;
		}
		
		public Object getElementAt(int index)
		{
			return data.getKey(index);
		}

		public int getSize()
		{
			return data.size();
		}
		
		public Object getSelectedValue()
		{
			return data.get(getSelectedItem());
		}

		public void addListDataListener(ListDataListener l)
		{
		}
		public void removeListDataListener(ListDataListener l)
		{
		}
	}
}
