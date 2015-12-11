package jadex.tools.generic;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIDefaults;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.PropertiesPanel;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingDefaultResultListener;

/**
 *  A panel that allows automatic and manual
 *  refresh of its contents.
 */
public abstract class AutoRefreshPanel extends JSplitPane
{
	//-------- constants --------
	
	/** The icons. */
	protected static final UIDefaults	icons	= new UIDefaults(new Object[]{
		"refresh_counter_0", SGUI.makeIcon(AutoRefreshPanel.class, "/jadex/tools/common/images/refresh_counter_0_20.png"),
		"refresh_counter_1", SGUI.makeIcon(AutoRefreshPanel.class, "/jadex/tools/common/images/refresh_counter_1_20.png"),
		"refresh_counter_2", SGUI.makeIcon(AutoRefreshPanel.class, "/jadex/tools/common/images/refresh_counter_2_20.png"),
		"refresh_counter_3", SGUI.makeIcon(AutoRefreshPanel.class, "/jadex/tools/common/images/refresh_counter_3_20.png"),
		"refresh_counter_4", SGUI.makeIcon(AutoRefreshPanel.class, "/jadex/tools/common/images/refresh_counter_4_20.png"),
		"refresh_counter_5", SGUI.makeIcon(AutoRefreshPanel.class, "/jadex/tools/common/images/refresh_counter_5_20.png"),
		"refresh_counter_6", SGUI.makeIcon(AutoRefreshPanel.class, "/jadex/tools/common/images/refresh_counter_6_20.png"),
		"refresh_counter_7", SGUI.makeIcon(AutoRefreshPanel.class, "/jadex/tools/common/images/refresh_counter_7_20.png"),
		"refresh_auto", SGUI.makeIcon(AutoRefreshPanel.class, "/jadex/tools/common/images/refresh_auto_20.png"),
		"refresh_0", SGUI.makeIcon(AutoRefreshPanel.class, "/jadex/tools/common/images/refresh.png"),
	});
	
	//-------- attributes --------
	
	/** Enable / disable autorefresh. */
	protected boolean	autorefresh;

	/** The refresh delay (millis). */
	protected int	delay;

	/** The cumulated time since last refresh. */
	protected long	waited;

	/** The absolute time of the last timer event. */
	protected long	etime;
	
	/** The refresh button. */
	protected JButton	refresh;
	
	/** The autorefresh timer. */
	protected Timer	timer;
	
	/** Refresh in progress? */
	protected boolean	refreshing;

	//-------- constructors --------
	
	/**
	 *  Create a new auto refresh panel.
	 */
	public AutoRefreshPanel()
	{
		super(VERTICAL_SPLIT);
		this.delay	= 5000;
		this.autorefresh	= true;
		this.setOneTouchExpandable(true);
		
		createInnerPanel().addResultListener(new SwingDefaultResultListener<JComponent>(this)
		{
			public void customResultAvailable(JComponent result)
			{
				// Panel components & layout.
				refresh	= new JButton(icons.getIcon("refresh_counter_0"));
				refresh.setMargin(new Insets(0, 0, 0, 0));
				refresh.setPreferredSize(refresh.getPreferredSize());
				refresh.setMinimumSize(refresh.getMinimumSize());
				refresh.setMaximumSize(refresh.getMaximumSize());
				refresh.setToolTipText("Refresh the panel.");
				
				SpinnerNumberModel spmdelay = new SpinnerNumberModel(delay/1000, 1, 100, 1);
				final JSpinner	spdelay	= new JSpinner(spmdelay);
				spdelay.setPreferredSize(spdelay.getPreferredSize());	// Remeber preferred size before setting max to 0 (grrr).
				spmdelay.setMaximum(null); // unbounded
				spdelay.setEnabled(autorefresh);
				final JLabel	ldelay	= new JLabel("delay (sec.)");

				final JCheckBox	cbauto	= new JCheckBox("autorefresh");
				cbauto.setSelected(autorefresh);

				JPanel	options	= new JPanel(new GridBagLayout());
				GridBagConstraints	gbc	= new GridBagConstraints();
				gbc.insets	= new Insets(3, 3, 3, 3);
				gbc.gridy	= 0;
				gbc.anchor	= GridBagConstraints.WEST;
				gbc.fill	= GridBagConstraints.NONE;
				options.add(refresh, gbc);
				options.add(ldelay, gbc);
				options.add(spdelay, gbc);
				gbc.weightx	= 1;
				options.add(cbauto, gbc);
				
				AutoRefreshPanel.this.setTopComponent(options);
				AutoRefreshPanel.this.setBottomComponent(result);
				
				// Panel behavior (listeners, etc).				
				timer	= new Timer(0, new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						waited	+= (System.nanoTime()-etime)/1000000;
						etime	= System.nanoTime();
						if(waited<delay)
						{
							refresh.setIcon(icons.getIcon("refresh_counter_"+(waited*8/delay)));
						}
						else
						{
							doRefresh();
						}
					}
				});
				
				resetTimer();
				
				refresh.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						refresh.setEnabled(false);
						doRefresh().addResultListener(new SwingDefaultResultListener<Void>(AutoRefreshPanel.this)
						{
							public void customResultAvailable(Void result)
							{
								refresh.setEnabled(true);
							}
						});
					}
				});
				
				cbauto.addChangeListener(new ChangeListener()
				{
					public void stateChanged(ChangeEvent e)
					{
						autorefresh	= cbauto.isSelected();
						spdelay.setEnabled(autorefresh);
						ldelay.setEnabled(autorefresh);
						resetTimer();
					}
				});
				
				spdelay.addChangeListener(new ChangeListener()
				{
					public void stateChanged(ChangeEvent e)
					{
						delay	= ((Number)spdelay.getValue()).intValue() * 1000;
						if(waited<delay)
						{
							timer.setDelay(delay/8);
							refresh.setIcon(icons.getIcon("refresh_counter_"+(waited*8/delay)));
						}
						else
						{
							doRefresh();
						}
					}
				});
				
				// To spare resources: start / stop timer, when component is shown / hidden.
				AutoRefreshPanel.this.addComponentListener(new ComponentListener()
				{
					public void componentShown(ComponentEvent e)
					{
						resetTimer();
					}
					
					public void componentHidden(ComponentEvent e)
					{
						resetTimer();
					}
					
					public void componentMoved(ComponentEvent e)
					{
					}
					
					public void componentResized(ComponentEvent e)
					{
					}
				});
			}
		});
	}
	
	//-------- methods --------
	
	/**
	 *  (Re-)start the timer, if autorefresh is true
	 *  or otherwise stop the timer.
	 */
	protected void	resetTimer()
	{
		if(autorefresh && isShowing())
		{
			// Only restart if not currently updating (will restart after update finished).
			if(!refreshing && delay>0)
			{
				waited	= 0;
				etime	= System.nanoTime();
				refresh.setIcon(icons.getIcon("refresh_counter_0"));
				timer.setDelay(delay/8);
				timer.start();
			}
		}
		else
		{
			timer.stop();
			if(!refreshing)
			{
				refresh.setIcon(icons.getIcon("refresh_0"));
			}
		}
	}
	
	/**
	 *  Do a refresh.
	 */
	protected IFuture<Void>	doRefresh()
	{
		assert SwingUtilities.isEventDispatchThread();
		final Future<Void>	ret	= new Future<Void>();
		
		if(!refreshing)
		{
			refreshing	= true;
			timer.stop();
			refresh.setIcon(autorefresh	? icons.getIcon("refresh_auto") : icons.getIcon("refresh_0"));
			refresh().addResultListener(
				new SwingDefaultResultListener<Void>(AutoRefreshPanel.this)
			{
				public void customResultAvailable(Void result)
				{
					refreshing	= false;
					resetTimer();
					ret.setResult(null);
				}
				
				public void customExceptionOccurred(Exception exception)
				{
					refreshing	= false;
					ret.setResult(null);
					super.customExceptionOccurred(exception);
				}
			});
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}
	
	
	//-------- template methods --------
	
	/**
	 *  Create the inner panel.
	 */
	public abstract IFuture<JComponent>	createInnerPanel();
	
	/**
	 *  Refresh the inner panel.
	 */
	public abstract IFuture<Void>	refresh();

	//-------- main for testing --------
	
	/**
	 *  Main for testing
	 */
	public static void	main(String[] args)
	{
		JFrame	f	= new JFrame("AutoRefreshPanel");
		JTabbedPane	tabs	= new JTabbedPane();
		f.getContentPane().add(tabs, BorderLayout.CENTER);
		tabs.addTab("Refresh", new AutoRefreshPanel()
		{
			PropertiesPanel	pp;
			
			public IFuture<JComponent> createInnerPanel()
			{
				pp	= new PropertiesPanel("test");
				pp.createTextField("date", new Date().toString(), false, 0);
				return new Future<JComponent>(pp);
			}
			
			public IFuture<Void> refresh()
			{
				final Future<Void>	ret	= new Future<Void>();
				pp.getTextField("date").setText(new Date().toString());
				final Timer	timer	= new Timer(1000, null);
				timer.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						timer.stop();
						ret.setResult(null);
					}
				});
				timer.start();
				return ret;
			}
		});
		tabs.add("Other Panel", new JLabel("other"));
		f.pack();
		f.setLocation(SGUI.calculateMiddlePosition(f));
		f.setVisible(true);
	}
}
