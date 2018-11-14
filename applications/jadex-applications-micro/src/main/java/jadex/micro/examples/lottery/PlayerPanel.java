package jadex.micro.examples.lottery;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingResultListener;

/**
 * 
 */
public class PlayerPanel extends JPanel
{
	protected ILotteryService ls;
	
	protected JTextField tfitem;
	
	protected JTextArea taclaimed;
	
	protected JButton buclaim;
	
	/**
	 * 
	 */
	public PlayerPanel(final ILotteryService ls)
	{
		this.ls = ls;
		this.tfitem = new JTextField();
		this.buclaim = new JButton("Claim");
		this.taclaimed = new JTextArea();
		
		buclaim.setEnabled(false);;
		
		buclaim.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				final String item = tfitem.getText();
				
				ls.claimItem(item).addResultListener(new SwingResultListener<Boolean>(new IResultListener<Boolean>()
				{
					public void resultAvailable(Boolean result)
					{
						if(result.booleanValue())
						{
							taclaimed.append(item+"\n");
						}
						if(tfitem.getText().equals(item))
						{
							tfitem.setText("Did not get item: "+item);
							buclaim.setEnabled(false);
						}
					}
					
					public void exceptionOccurred(Exception exception)
					{
						tfitem.setText("");
						buclaim.setEnabled(false);
					}
				}));
			}
		});
		
		JPanel north = new JPanel(new BorderLayout());
		north.add(new JLabel("Offered item"), BorderLayout.WEST);
		north.add(tfitem, BorderLayout.CENTER);
		
		setLayout(new BorderLayout());
		add(north, BorderLayout.NORTH);
		add(taclaimed, BorderLayout.CENTER);
		add(buclaim, BorderLayout.SOUTH);
	}
	
	/**
	 * 
	 */
	public void setOfferedItem(String item)
	{
		tfitem.setText(item);
		buclaim.setEnabled(true);
	}
	
	/**
	 *  Create a gui frame.
	 * @param ls
	 * @return
	 */
	public static IFuture<PlayerPanel> createGui(final ILotteryService ls)
	{
		final Future<PlayerPanel> ret = new Future<PlayerPanel>();
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				JFrame f = new JFrame();
				f.setLayout(new BorderLayout());
				PlayerPanel pp = new PlayerPanel(ls);
				f.setContentPane(pp);
				f.pack();
				f.setLocation(SGUI.calculateMiddlePosition(f));
				f.setVisible(true);
				ret.setResult(pp);
			}
		});
		
		return ret;
	}
	
}
