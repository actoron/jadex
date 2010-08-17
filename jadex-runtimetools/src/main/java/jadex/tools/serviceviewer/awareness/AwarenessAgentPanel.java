package jadex.tools.serviceviewer.awareness;

import jadex.base.service.awareness.AwarenessAgent;
import jadex.bridge.IExternalAccess;
import jadex.commons.ICommand;
import jadex.commons.Properties;
import jadex.micro.IMicroExternalAccess;
import jadex.tools.common.plugin.IControlCenter;
import jadex.tools.serviceviewer.IComponentViewerPanel;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
		
		final SpinnerNumberModel spm = new SpinnerNumberModel(5, 0, 100000, 1);
		JSpinner delay = new JSpinner(spm);
		spm.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				component.scheduleStep(new ICommand()
				{
					public void execute(Object args)
					{
						AwarenessAgent agent = (AwarenessAgent)args;
						agent.setDelay(((Integer)spm.getValue()).intValue()*1000);
					}
				});
			}
		});
		
		final JTextField address = new JTextField(20);
		component.scheduleStep(new ICommand()
		{
			public void execute(Object args)
			{
				AwarenessAgent agent = (AwarenessAgent)args;
				final Object[] ai = agent.getAddressInfo();
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						address.setText(((InetAddress)ai[0]).toString()+":"+((Integer)ai[1]).intValue());
					}
				});
			}
		});
		
		p.add(new JLabel("Delay between sending awareness infos [secs]:", JLabel.RIGHT), new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.EAST, 
			GridBagConstraints.HORIZONTAL, new Insets(1,1,1,1), 0, 0));
		p.add(delay, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.EAST, 
			GridBagConstraints.NONE, new Insets(1,1,1,1), 0, 0));
		p.add(new JLabel("IP-multicast address [ip:port]:", JLabel.RIGHT), new GridBagConstraints(0, 1, 1, 1, 1, 0, GridBagConstraints.EAST, 
			GridBagConstraints.HORIZONTAL, new Insets(1,1,1,1), 0, 0));
		p.add(address, new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.EAST, 
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
}
