package jadex.platform.service.message.transport.udpmtp;

import jadex.platform.service.message.transport.udpmtp.sending.FlowControl;
import jadex.platform.service.message.transport.udpmtp.sending.SendingThreadTask;
import jadex.platform.service.message.transport.udpmtp.sending.TxMessage;
import jadex.platform.service.message.transport.udpmtp.sending.TxPacket;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class DebugGui extends JFrame
{
	/** Information about known peers. */
	protected Map<InetSocketAddress, PeerInfo> peerinfos;
	
	/** Messages in-flight. */
	protected Map<Integer, TxMessage> inflightmessages;
	
	/** The transmission queue. */
	protected PriorityBlockingQueue<TxPacket> packetqueue;
	
	/** The remaining send quota. */
	protected AtomicInteger sendquota;
	
	/** The flow control */
	protected FlowControl flowcontrol;
	
	/** The sending thread. */
	protected SendingThreadTask sendingthread;
	
	public DebugGui(Map<InetSocketAddress, PeerInfo> pi,
					Map<Integer, TxMessage> im,
					PriorityBlockingQueue<TxPacket> pq,
					AtomicInteger sq,
					FlowControl fc,
					SendingThreadTask st)
	{
		super("UDP Transport Debug Gui");
		this.peerinfos = pi;
		this.inflightmessages = im;
		this.packetqueue = pq;
		this.sendquota = sq;
		this.flowcontrol = fc;
		this.sendingthread = st;
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				setLayout(new GridLayout(8,2));
				getContentPane().add(new JLabel("peerinfos size"));
				final JTextField pitf = new JTextField();
				getContentPane().add(pitf);
				getContentPane().add(new JLabel("inflightmessages size"));
				final JTextField iftf = new JTextField();
				getContentPane().add(iftf);
				getContentPane().add(new JLabel("packetqueue size"));
				final JTextField pqtf = new JTextField();
				getContentPane().add(pqtf);
				getContentPane().add(new JLabel("sendquota"));
				final JTextField sqtf = new JTextField();
				getContentPane().add(sqtf);
				getContentPane().add(new JLabel("maxsendquota"));
				final JTextField msqtf = new JTextField();
				getContentPane().add(msqtf);
				getContentPane().add(new JLabel("top inflightmessage"));
				final JTextField tiftf = new JTextField();
				getContentPane().add(tiftf);
				getContentPane().add(new JLabel("Sending Thread State"));
				final JTextField ststf = new JTextField();
				getContentPane().add(ststf);
				
				getContentPane().add(new JButton(new AbstractAction("Refresh")
				{
					public void actionPerformed(ActionEvent e)
					{
						pitf.setText(String.valueOf(peerinfos.size()));
						iftf.setText(String.valueOf(inflightmessages.size()));
						pqtf.setText(String.valueOf(packetqueue.size()));
						sqtf.setText(String.valueOf(sendquota.get()));
						msqtf.setText(String.valueOf(flowcontrol.getMaxsendQuota()));
						synchronized (inflightmessages)
						{
							if (inflightmessages.size() > 0)
							{
								tiftf.setText(String.valueOf(inflightmessages.values().iterator().next().getMsgId()));
							}
						}
						ststf.setText(sendingthread.getState());
					}
				}));
				
				pack();
				setSize(600, 400);
				setVisible(true);
			}
		});
	}
}
