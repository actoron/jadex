package jadex.bpmn.editor.gui;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

/**
 *  Progress Bar for background tasks.
 *
 */
public class BackgroundProgressBar extends JPanel
{
	/** Label for the GUI message. */
	protected JLabel label;
	
	/** The progress bar. */
	protected JProgressBar progressbar;
	
	/** Monitor for locking. */
	protected Object monitor;
	
	/**
	 *  Creates a new progress bar.
	 */
	public BackgroundProgressBar()
	{
		super(new GridLayout(1, 2));
		setBorder(new EmptyBorder(0, 5, 0, 0));
		monitor = new Object();
		progressbar = new JProgressBar();
		progressbar.setVisible(false);
		add(progressbar);
		label = new JLabel();
		label.setBorder(new EmptyBorder(0, 5, 0, 0));
		label.setVisible(false);
		add(label);
	}
	
	/**
	 *  Starts a background operation.
	 *  
	 *  @param operationdesc Description of the operation.
	 *  @param maxval Maximum value the operation aims to achieve.
	 */
	public void start(final String operationdesc, final int maxval)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				label.setText(operationdesc);
				label.setVisible(true);
				progressbar.setMaximum(maxval);
				progressbar.setVisible(true);
//				getParent().repaint();
			}
		});
	}
	
	/**
	 *  Updates the progress bar.
	 *  
	 *  @param newval New value reached by the operation.
	 */
	public void update(final int newval)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				progressbar.setValue(newval);
			}
		});
	}
	
	/**
	 *  Finishes the operation.
	 */
	public void finish()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				label.setVisible(false);
				progressbar.setVisible(false);
			}
		});
	}
	
	public Object getMonitor()
	{
		return monitor;
	}
}
