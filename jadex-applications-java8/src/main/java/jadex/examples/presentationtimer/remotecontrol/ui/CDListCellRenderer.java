package jadex.examples.presentationtimer.remotecontrol.ui;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.ITransportComponentIdentifier;
import jadex.bridge.service.component.BasicServiceInvocationHandler;
import jadex.examples.presentationtimer.common.State;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

public class CDListCellRenderer extends JPanel implements ListCellRenderer<CDListItem>
{

	protected JLabel	platformLabel;
	protected JTextArea	platformAddresses;
	protected JLabel	stateLabel;
	protected JLabel	timeLabel;

	public CDListCellRenderer()
	{
		setOpaque(true);

		BoxLayout mgr = new BoxLayout(this, BoxLayout.Y_AXIS);
		setLayout(mgr);
		
		add(Box.createRigidArea(new Dimension(0,5)));
		
		add(new JLabel("platformname") {{
			setAlignmentX(LEFT_ALIGNMENT);
			platformLabel = this;
		}});
		
		add(new JPanel() {{
			setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			setOpaque(false);
			setAlignmentX(LEFT_ALIGNMENT);
			add(new JLabel("state") {{
				setForeground(Color.gray);
				stateLabel = this;
				setAlignmentX(LEFT_ALIGNMENT);
			}});
			
			add(Box.createRigidArea(new Dimension(10, 0)));

			add(new JLabel("time") {{
				setForeground(Color.gray);
				timeLabel = this;
				setAlignmentX(LEFT_ALIGNMENT);
			}});
		}});
		
		
		add(new JTextArea("addrs") {{
			Font font = getFont();
			setFont(font.deriveFont((float)(font.getSize()*0.85)));
			setAlignmentX(LEFT_ALIGNMENT);
			platformAddresses = this;
			setEditable(false);
			setOpaque(false);
		}});
		
		add(Box.createRigidArea(new Dimension(0,5)));
	}
	
	@Override
	public Component getListCellRendererComponent(JList< ? extends CDListItem> list, CDListItem value, int index, boolean isSelected, boolean cellHasFocus)
	{
		CDListItem item = list.getModel().getElementAt(index);
		
		InvocationHandler invocationHandler = Proxy.getInvocationHandler(item.getService());
		if (invocationHandler instanceof BasicServiceInvocationHandler) {
			BasicServiceInvocationHandler sih = (BasicServiceInvocationHandler) invocationHandler;
			
			IComponentIdentifier providerId = sih.getServiceIdentifier().getProviderId();
			String platformName = providerId.getName();
			String[] addr;
			if (providerId instanceof ITransportComponentIdentifier) {
				addr = ((ITransportComponentIdentifier)providerId).getAddresses();
			} else {
				addr = new String[0];
			}
			this.platformLabel.setText(platformName);
			this.platformAddresses.setText(formatAddrs(addr));
		}
		
		State state = item.getStatus();
		String time = item.getTime();
		
		if (state != null) {
			stateLabel.setText(state.toString());
		} else {
			stateLabel.setText("State unknown");
		}
		
		if (time != null) {
			timeLabel.setText(time);
		} else {
			timeLabel.setText("??:??");
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
		StringBuilder stringBuilder = new StringBuilder("");
		for (String string : addr) {
			stringBuilder.append("\n");
			stringBuilder.append(string);
		}
		return stringBuilder.toString();
	}
	
}
