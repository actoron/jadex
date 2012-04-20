package jadex.simulation.analysis.common.superClasses.service.view.session;

import jadex.simulation.analysis.common.data.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.common.superClasses.events.IAEvent;
import jadex.simulation.analysis.common.superClasses.service.analysis.IAnalysisSessionService;
import jadex.simulation.analysis.common.superClasses.service.view.session.subprocess.ATaskInternalFrame;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyVetoException;
import java.util.UUID;

import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Default view for a session
 * @author 5Haubeck
 *
 */
public class ADefaultSessionView extends JDesktopPane implements IASessionView
{
	protected SessionProperties prop = null;
	protected JPanel content = new JPanel(new GridBagLayout());
	final protected ADefaultSessionView me = this;

	public ADefaultSessionView(IAnalysisSessionService service, final String id, final IAParameterEnsemble configuration)
	{
		super();
		service.addListener(this);
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				prop = new SessionProperties(id, configuration);
				Insets insets = new Insets(1, 1, 1, 1);
				
				content.add(prop, new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				content.setPreferredSize(new Dimension(850, 300));
				
				ATaskInternalFrame frame = new ATaskInternalFrame("Workflow", false, false, false, true);
				frame.setVisible(true);
				frame.add(content);
				add(frame);
				try
				{
					frame.setMaximum(true);
					frame.setSelected(true);
				}
				catch (PropertyVetoException e1)
				{
					//omit
				}
			}
		});
	}

	@Override
	public void update(IAEvent event)
	{
		//omit
	}

	@Override
	public SessionProperties getSessionProperties()
	{
		return prop;
	}

	@Override
	public JComponent getComponent()
	{
		return content;
	}

}
