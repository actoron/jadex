package jadex.tools.serviceviewer.awareness;

import jadex.base.service.awareness.AwarenessAgent;
import jadex.bridge.IExternalAccess;
import jadex.commons.ICommand;
import jadex.commons.IResultCommand;
import jadex.commons.Properties;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.concurrent.SwingDefaultResultListener;
import jadex.micro.IMicroExternalAccess;
import jadex.tools.common.plugin.IControlCenter;
import jadex.tools.serviceviewer.IComponentViewerPanel;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * 
 */
public class AwarenessAgentPanel implements IComponentViewerPanel
{
	/** The jcc. */
	protected IControlCenter jcc;
	
	/** The component. */
	protected IMicroExternalAccess component;
	
	/**
	 *  Called once to initialize the panel.
	 *  Called on the swing thread.
	 *  @param jcc	The jcc.
	 * 	@param component The component.
	 */
	public void init(IControlCenter jcc, IExternalAccess component)
	{
		this.jcc = jcc;
		this.component = (IMicroExternalAccess)component;
	}
	
	/**
	 *  Informs the panel that it should stop all its computation
	 */
	public void shutdown()
	{
	}

	/**
	 *  The id used for mapping properties.
	 */
	public String getId()
	{
		return "awarenessviewer";
	}

	/**
	 *  The component to be shown in the gui.
	 *  @return	The component to be displayed.
	 */
	public JComponent getComponent()
	{
		JPanel p = new JPanel(new GridBagLayout());
		
		final SpinnerNumberModel spm = new SpinnerNumberModel(0, 0, 100000, 1);
		JSpinner delay = new JSpinner(spm);
		component.scheduleResultStep(new GetDelayCommand()).addResultListener(new SwingDefaultResultListener(p)
		{
			public void customResultAvailable(Object source, Object result)
			{
				spm.setValue(result);
			}
		});
		
		spm.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				component.scheduleStep(new SetDelayCommand(((Number)spm.getValue()).longValue()));
			}
		});
		
		final JTextField tfipaddress = new JTextField();
		final JTextField tfport = new JTextField();
		final JButton setaddr = new JButton("set");
		component.scheduleResultStep(new GetAddressCommand()).addResultListener(new SwingDefaultResultListener(p)
		{
			public void customResultAvailable(Object source, Object result)
			{
				System.out.println("here: "+result);
				Object[] ai = (Object[])result;
				tfipaddress.setText(""+((InetAddress)ai[0]).getHostAddress());
				tfport.setText(""+ai[1]);
			}
		});
		setaddr.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				try
				{
					final InetAddress address = InetAddress.getByName(tfipaddress.getText());
					final int port = Integer.parseInt(tfport.getText());
					component.scheduleStep(new SetAddressCommand(address, port));
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		});
		
		p.add(new JLabel("Delay between sending awareness infos [secs]:", JLabel.RIGHT), new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.EAST, 
			GridBagConstraints.HORIZONTAL, new Insets(1,1,1,1), 0, 0));
		p.add(delay, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.EAST, 
			GridBagConstraints.NONE, new Insets(1,1,1,1), 0, 0));
		
		p.add(new JLabel("IP-multicast address [ip:port]:", JLabel.RIGHT), new GridBagConstraints(0, 1, 1, 1, 1, 0, GridBagConstraints.EAST, 
			GridBagConstraints.HORIZONTAL, new Insets(1,1,1,1), 0, 0));
		p.add(tfipaddress, new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.EAST, 
			GridBagConstraints.NONE, new Insets(1,1,1,1), 0, 0));
		p.add(tfport, new GridBagConstraints(2, 1, 1, 1, 0, 0, GridBagConstraints.EAST, 
			GridBagConstraints.NONE, new Insets(1,1,1,1), 0, 0));
		p.add(setaddr, new GridBagConstraints(3, 1, 1, 1, 0, 0, GridBagConstraints.EAST, 
			GridBagConstraints.NONE, new Insets(1,1,1,1), 0, 0));

		return p;
	}

	/**
	 *  Advices the the panel to restore its properties from the argument
	 */
	public void setProperties(Properties ps)
	{
	}

	/**
	 *  Advices the panel provide its setting as properties (if any).
	 *  This is done on project close or save.
	 */
	public Properties	getProperties()
	{
		return null;
	}
	
	/**
	 *  Get delay command.
	 */
	public static class GetDelayCommand implements IResultCommand
	{
		public Object execute(Object args)
		{
			AwarenessAgent agent = (AwarenessAgent)args;
			final long delay = agent.getDelay()/1000;
			System.out.println("Delay: "+delay);
			return new Long(delay);
		}
	}
	
	/**
	 *  Set delay command.
	 */
	public static class SetDelayCommand implements ICommand
	{
		public long delay;
		
		public SetDelayCommand()
		{
		}

		public SetDelayCommand(long delay)
		{
			this.delay = delay;
		}
		
		public void execute(Object args)
		{
			AwarenessAgent agent = (AwarenessAgent)args;
			agent.setDelay(delay);
		}
	};
	
	/**
	 *  Get address command.
	 */
	public static class GetAddressCommand implements IResultCommand
	{
		public Object execute(Object args)
		{
			AwarenessAgent agent = (AwarenessAgent)args;
			return agent.getAddressInfo();
		}
	};

	/**
	 *  Set address command.
	 */
	public static class SetAddressCommand implements ICommand
	{
		public InetAddress address;
		public int port;
		
		public SetAddressCommand()
		{
		}

		public SetAddressCommand(InetAddress address, int port)
		{
			this.address = address;
			this.port = port;
		}
		
		public void execute(Object args)
		{
			AwarenessAgent agent = (AwarenessAgent)args;
			agent.setAddressInfo(address, port);
		}
	};
}

