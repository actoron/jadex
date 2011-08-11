package jadex.simulation.analysis.service.basic.view.session;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyVetoException;
import java.util.UUID;

import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import jadex.commons.gui.PropertiesPanel;
import jadex.simulation.analysis.common.data.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.common.util.AConstants;
import jadex.simulation.analysis.service.basic.analysis.IAnalysisSessionService;
import jadex.simulation.analysis.service.basic.view.session.subprocess.ATaskInternalFrame;

public class SessionProperties extends PropertiesPanel
{
	public SessionProperties(UUID session, final IAParameterEnsemble config)
	{
		super(" Session Eigenschaften ");
		setPreferredSize(new Dimension(900, 150));
		createTextField("SessionID", session.toString());
		createTextField("Status", AConstants.SERVICE_SESSION_START);
//		createTextField("Konfiguration", "> IAParameterEnsemble").addMouseListener(new MouseListener()
//		{
//
//			@Override
//			public void mouseReleased(MouseEvent e)
//			{
//
//			}
//
//			@Override
//			public void mousePressed(MouseEvent e)
//			{
//
//			}
//
//			@Override
//			public void mouseExited(MouseEvent e)
//			{
//
//			}
//
//			@Override
//			public void mouseEntered(MouseEvent e)
//			{
//
//			}
//
//			@Override
//			public void mouseClicked(MouseEvent e)
//			{
//				ATaskInternalFrame frame = new ATaskInternalFrame("Konfiguration: IAParameterEnsemble", true, true, true, true);
//				frame.setVisible(true);
//				frame.add(config.getView().getComponent());
//				frame.setSize(new Dimension(750, 300));
//				parent.add(frame);
//				try
//				{
////					frame.setMaximizable(true);
//					frame.setSelected(true);
//				}
//				catch (PropertyVetoException e1)
//				{
//					// omit
//				}
//
//			}
//		});
		;

		// addFullComponent("Konfiguration", config.getView().getComponent(), 1);

		// setPreferredSize(new Dimension(400,200));

	}
}
