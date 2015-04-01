package jadex.examples.presentationtimer.remotecontrol.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridBagLayoutInfo;
import java.awt.GridLayout;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Arrays;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.component.BasicServiceInvocationHandler;
import jadex.examples.presentationtimer.common.ICountdownService;

import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

public class CDListCellRenderer extends JPanel implements ListCellRenderer<ICountdownService>
{

	protected JLabel	platformLabel;
	protected JTextArea	platformAddresses;
	protected JLabel	stateLabel;
	protected JLabel	timeLabel;

	public CDListCellRenderer()
	{
		setOpaque(true);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(new JLabel("platformname") {{
			platformLabel = this;
//			setOpaque(true);
		}});
		
		add(new JPanel() {{
			setOpaque(false);
			add(new JLabel("state") {{
				stateLabel = this;
			}});
			
			add(new JLabel("time") {{
				timeLabel = this;
			}});
		}});
		
		
		add(new JTextArea("addrs") {{
			platformAddresses = this;
			setEditable(false);
			setOpaque(false);
		}});
		
	}
	
	@Override
	public Component getListCellRendererComponent(JList< ? extends ICountdownService> list, ICountdownService value, int index, boolean isSelected, boolean cellHasFocus)
	{
		ICountdownService cds = list.getModel().getElementAt(index);
		
		InvocationHandler invocationHandler = Proxy.getInvocationHandler(cds);
		if (invocationHandler instanceof BasicServiceInvocationHandler) {
			BasicServiceInvocationHandler sih = (BasicServiceInvocationHandler) invocationHandler;
			
			IComponentIdentifier providerId = sih.getServiceIdentifier().getProviderId();
			String platformName = providerId.getName();
			String[] addr = providerId.getAddresses();
			this.platformLabel.setText(platformName);
			this.platformAddresses.setText(formatAddrs(addr));
		}
		
//		cds.getState().addResultListener(state -> {
//			stateLabel.setText(state.toString());
//		});
//		
//		cds.getTime().addResultListener(timeString -> {
//			timeLabel.setText(timeString);
//		});

		if(isSelected)
		{
			// Schriftfarbe
			// UIManager.getColor("List.selectionForeground") gibt die
			// Standard Schriftfarbe für ein markiertes Listen Element zurück
			this.setForeground(UIManager.getColor("List.selectionForeground"));
			// Hintergrund
			// UIManager.getColor("List.selectionBackground") gibt die
			// Standard Hintergrundfarbe für ein markiertes Listen Element
			// zurück
			this.setBackground(UIManager.getColor("List.selectionBackground"));
		}
		// Element aus der Liste ist nicht markiert
		else
		{
			// Schriftfarbe
			this.setForeground(UIManager.getColor("List.foreground"));
			// Hintergrund
			this.setBackground(UIManager.getColor("List.background"));
		}
		return this;
	}

	private String formatAddrs(String[] addr) {
		StringBuilder stringBuilder = new StringBuilder(addr[0]);
		for (String string : addr) {
			stringBuilder.append("\n");
			stringBuilder.append(string);
		}
		return stringBuilder.toString();
	}
	
}
