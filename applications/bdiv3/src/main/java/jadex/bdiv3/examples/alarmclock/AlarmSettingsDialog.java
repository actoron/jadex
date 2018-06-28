package jadex.bdiv3.examples.alarmclock;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIDefaults;

import com.toedter.calendar.JDateChooser;

import jadex.bdiv3.examples.alarmclock.AlarmclockBDI.PlaySongGoal;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.SGUI;
import jadex.commons.transformation.annotations.Classname;

/**
 *  A panel for editing the settings of an alarm.
 */
public class AlarmSettingsDialog extends JDialog
{
	//-------- static part --------

	/** The image icons. */
	protected static final UIDefaults	icons	= new UIDefaults(new Object[]
	{
		"Browse", SGUI.makeIcon(AlarmSettingsDialog.class,	"/jadex/bdiv3/examples/alarmclock/images/dots_small.png"),
		"Play", SGUI.makeIcon(AlarmSettingsDialog.class,	"/jadex/bdiv3/examples/alarmclock/images/resume.png"),
		"Stop", SGUI.makeIcon(AlarmSettingsDialog.class,	"/jadex/bdiv3/examples/alarmclock/images/stop.png")
	});

	//-------- attributes --------

	/** The alarm. */
	protected Alarm alarm;

	/** The mode. */
	protected JComboBox mode;

	/** The date. */
	protected JDateChooser date;

	/** The time. */
	protected TimeSpinner time;

	/** The alarm text. */
	protected JTextField alarmtf;

	/** The message text. */
	protected JTextField messagetf;

	/** Result state. */
	protected boolean state_ok;

	/** Playing state. */
	protected PlaySongGoal playing;
	
	/** The agent. */
	protected IExternalAccess	agent;

	//-------- constructors --------

	/**
	 *  Create a new alarm settings panel.
	 */
	public AlarmSettingsDialog(final IExternalAccess agent, JFrame owner, Alarm alarm)
	{
		super(owner, "Alarm Settings", true);
		this.agent	= agent;
		JPanel content = new JPanel(new GridBagLayout());
		//content.setBorder(BorderFactory.createTitledBorder(
		//BorderFactory.createEtchedBorder(), "Alarm settings"));

		mode = new JComboBox(Alarm.ALARMS);
		date = new JDateChooser();
		time = new TimeSpinner();
		JButton now = new JButton("Now");
		now.setMargin(new Insets(0,0,0,0));

		alarmtf = new JTextField();
		JButton browse = new JButton(icons.getIcon("Browse"));
		browse.setMargin(new Insets(0,0,0,0));
		final JButton play = new JButton(icons.getIcon("Play"));
		play.setMargin(new Insets(0,0,0,0));

		messagetf = new JTextField("");

		JButton ok = new JButton("OK");
		JButton cancel = new JButton("Cancel");

		final JFileChooser filechooser = new JFileChooser(".");
		filechooser.setAcceptAllFileFilterUsed(true);
		final javax.swing.filechooser.FileFilter load_filter = new javax.swing.filechooser.FileFilter()
		{
			public String getDescription()
			{
				return "Music files (*.mp3)";//;, *.wma, *.wav)";
			}

			public boolean accept(File f)
			{
				String name = f.getName();
				return f.isDirectory() ||  (name.endsWith(".mp3"));
//					|| name.endsWith(".wma") || name.endsWith(".wav"));
			}
		};
		filechooser.addChoosableFileFilter(load_filter);

		mode.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String mo = (String)mode.getSelectedItem();
				if(Alarm.ONCE.equals(mo))
				{
					date.setEnabled(true);
					date.setDateFormatString("d M yyyy");
					time.setFormat("HH:mm:ss");
				}
				else if(Alarm.HOURLY.equals(mo))
				{
					date.setEnabled(false);
					time.setFormat("mm:ss");
				}
				else if(Alarm.DAILY.equals(mo))
				{
					date.setEnabled(false);
					time.setFormat("HH:mm:ss");
				}
				else if(Alarm.WEEKLY.equals(mo))
				{
					date.setEnabled(true);
					date.setDateFormatString("E");
					time.setFormat("HH:mm:ss");
				}
				else if(Alarm.MONTHLY.equals(mo))
				{
					date.setEnabled(true);
					date.setDateFormatString("d");
					time.setFormat("HH:mm:ss");
				}
				else if(Alarm.YEARLY.equals(mo))
				{
					date.setEnabled(true);
					date.setDateFormatString("d M");
					time.setFormat("HH:mm:ss");
				}
				else if(Alarm.TIMER.equals(mo))
				{
					date.setEnabled(false);
					time.setFormat("HH:mm:ss");
				}
			}
		});
		now.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				agent.scheduleStep(new IComponentStep<Void>()
				{
					@Classname("setTime")
					public IFuture<Void> execute(IInternalAccess ia)
					{
//						BDIAgent bia = (BDIAgent)ia;
						long cur = SServiceProvider.getLocalService(ia, IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM).getTime();
						Date now = new Date(cur);
						date.setDate(now);
						time.setValue(now);								
						return IFuture.DONE;
					}
				});
			}
		});
		browse.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(filechooser.showDialog(SGUI.getWindowParent(AlarmSettingsDialog.this)
					, "Load")==JFileChooser.APPROVE_OPTION)
				{
					File file = filechooser.getSelectedFile();
					//System.out.println("File is: "+file);
					alarmtf.setText(""+file);
					if(getAlarm()!=null)
					{
						getAlarm().setFilename(""+file);
					}
				}
			}
		});
		play.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(playing==null)
				{
					try
					{
						play.setIcon(icons.getIcon("Stop"));
						final URL song = new URL("file:///"+alarmtf.getText());
						//System.out.println("Song is: "+song);
						
						agent.scheduleStep(new IComponentStep<Void>()
						{
							@Classname("play")
							public IFuture<Void> execute(IInternalAccess ia)
							{
//								BDIAgent bia = (BDIAgent)ia;
								playing = new PlaySongGoal(song);
								ia.getComponentFeature(IBDIAgentFeature.class).dispatchTopLevelGoal(playing)
									.addResultListener(new IResultListener<Object>()
								{
									public void resultAvailable(Object result)
									{
										play.setIcon(icons.getIcon("Play"));
										stopPlaying();
									}
									
									public void exceptionOccurred(Exception exception)
									{
										play.setIcon(icons.getIcon("Play"));
										stopPlaying();
									}
								});
								return IFuture.DONE;
							}
						});
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}
				}
				else
				{
					play.setIcon(icons.getIcon("Play"));
					stopPlaying();
				}
			}
		});
		ok.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				stopPlaying();
				state_ok = true;
				setVisible(false);
			}
		});
		cancel.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				stopPlaying();
				state_ok = false;
				setVisible(false);
			}
		});

		content.add(new JLabel("Alarm Mode:"), new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.WEST,
			GridBagConstraints.NONE, new Insets(4,2,2,4),0,0));
		content.add(mode, new GridBagConstraints(1,0,1,1,1,0,GridBagConstraints.NORTHWEST,
			GridBagConstraints.HORIZONTAL, new Insets(4,2,2,4),0,0));

		content.add(new JLabel("Date:"), new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST,
			GridBagConstraints.NONE, new Insets(4,2,2,4),0,0));
		content.add(date, new GridBagConstraints(1,1,1,1,1,0,GridBagConstraints.NORTHWEST,
			GridBagConstraints.HORIZONTAL, new Insets(4,2,2,4),0,0));
		content.add(new JLabel("Time:"), new GridBagConstraints(2,1,1,1,0,0,GridBagConstraints.NORTHWEST,
			GridBagConstraints.BOTH, new Insets(4,10,2,4),0,0));
		content.add(time, new GridBagConstraints(3,1,1,1,1,0,GridBagConstraints.NORTHWEST,
			GridBagConstraints.HORIZONTAL, new Insets(4,2,2,4),0,0));
		content.add(now, new GridBagConstraints(4,1,1,1,0,0,GridBagConstraints.NORTHEAST,
			GridBagConstraints.HORIZONTAL, new Insets(4,5,2,4),0,0));

		content.add(new JLabel("Sound file:"), new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.NORTHWEST,
			GridBagConstraints.NONE, new Insets(4,2,2,4),0,0));
		content.add(alarmtf, new GridBagConstraints(1,2,3,1,1,0,GridBagConstraints.NORTHWEST,
			GridBagConstraints.HORIZONTAL, new Insets(4,2,2,4),0,0));
		JPanel buts = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buts.add(browse);
		buts.add(play);
		content.add(buts, new GridBagConstraints(4,2,1,1,0,0,GridBagConstraints.NORTHWEST,
			GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0),0,0));

		content.add(new JLabel("Message:"), new GridBagConstraints(0,3,1,1,0,0,GridBagConstraints.NORTHWEST,
			GridBagConstraints.NONE, new Insets(4,2,2,4),0,0));
		content.add(messagetf, new GridBagConstraints(1,3,GridBagConstraints.REMAINDER,1,1,0,GridBagConstraints.NORTHWEST,
			GridBagConstraints.HORIZONTAL, new Insets(4,2,2,4),0,0));

		JPanel buts2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buts2.add(ok);
		buts2.add(cancel);
		content.add(buts2, new GridBagConstraints(0,4,5,1,1,0,GridBagConstraints.NORTHEAST,
			GridBagConstraints.NONE, new Insets(0,0,0,0),0,0));

		Dimension md = cancel.getMinimumSize();
		Dimension pd = cancel.getPreferredSize();
		ok.setMinimumSize(md);
		ok.setPreferredSize(pd);

		md = browse.getMinimumSize();
		pd = browse.getPreferredSize();
		play.setMinimumSize(md);
		play.setPreferredSize(pd);

		md = date.getMinimumSize();
		pd = date.getPreferredSize();
		// Hack to make to date look nice :-(
		date.setMinimumSize(new Dimension(md.width+20, md.height));
		date.setPreferredSize(new Dimension(pd.width+20, pd.height));

		getContentPane().add("Center", content);

		// Set the alarm and refresh the view.
		// Alarm is cloned to avoid modifying the original object.
		
		if(alarm==null)
		{
			final Alarm al = new Alarm();
			agent.scheduleStep(new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					long cur = SServiceProvider.getLocalService(ia, IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM).getTime();
					al.setTime(new Time(new Date(cur)));
					setAlarm(al);
					return IFuture.DONE;
				}
			});
		}
		else
		{
			alarm = (Alarm)alarm.clone();
			setAlarm(alarm);
		}
	}

	//-------- methods --------

	/**
	 *  Set the alarm.
	 *  @param alarm The alarm.
	 */
	public void setAlarm(Alarm alarm)
	{
		this.alarm = alarm;
		refreshGui();
	}

	/**
	 *  Get the alarm.
	 *  @return The alarm.
	 */
	public Alarm getAlarm()
	{
		refreshModel();
		return alarm;
	}

	/**
	 *  Test if result state is ok.
	 *  @return True, if ok.
	 */
	public boolean isStateOk()
	{
		return state_ok;
	}

	/**
	 * Refresh the model.
	 */
	public void refreshModel()
	{
		if(alarm==null)
		{
			this.alarm = new Alarm();
//			alarm.setClock((IClockService)agent.getComponentFeature(IRequiredServicesFeature.class).getService(IClockService.class));
		}

		alarm.setMode((String)mode.getSelectedItem());
		alarm.setFilename(alarmtf.getText());
		alarm.setMessage(messagetf.getText());

		//Time alarmtime = alarm.getTime();
		Time alarmtime = new Time();

		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime((Date)time.getValue());
		alarmtime.setSeconds(cal.get(Calendar.SECOND));
		alarmtime.setMinutes(cal.get(Calendar.MINUTE));
		// Do not set hour if mode hourly
		if(!alarm.getMode().equals(Alarm.HOURLY))
			alarmtime.setHours(cal.get(Calendar.HOUR_OF_DAY));

		if(alarm.getMode().equals(Alarm.ONCE))
		{
			cal.setTime(date.getDate());
			alarmtime.setMonthday(cal.get(Calendar.DAY_OF_MONTH));
			alarmtime.setMonth(cal.get(Calendar.MONTH));
			alarmtime.setYear(cal.get(Calendar.YEAR));
		}
		else if(alarm.getMode().equals(Alarm.WEEKLY))
		{
			cal.setTime(date.getDate());
			alarmtime.setWeekday(cal.get(Calendar.DAY_OF_WEEK));
		}
		else if(alarm.getMode().equals(Alarm.MONTHLY))
		{
			cal.setTime(date.getDate());
			alarmtime.setMonthday(cal.get(Calendar.DAY_OF_MONTH));
		}
		else if(alarm.getMode().equals(Alarm.YEARLY))
		{
			cal.setTime(date.getDate());
			alarmtime.setMonthday(cal.get(Calendar.DAY_OF_MONTH));
			alarmtime.setMonth(cal.get(Calendar.MONTH));
		}

		alarm.setTime(alarmtime);
		//alarm.setNextAlarmtime(); // Hack! todo: do automatically
		//System.out.println("date: "+date.getDate());
		//System.out.println("time: "+time.getValue());
	}

	/**
	 * Refresh the gui.
	 */
	public void refreshGui()
	{
		mode.getModel().setSelectedItem(alarm.getMode());
		alarmtf.setText(alarm.getFilename());
		messagetf.setText(alarm.getMessage());

		Time t = alarm.getTime();
		GregorianCalendar cal = new GregorianCalendar(t.getYear(), t.getMonth(), t.getMonthday(),
			t.getHours(), t.getMinutes(), t.getSeconds());
		time.setValue(cal.getTime());
		date.setDate(cal.getTime());
	}

	/**
	 *
	 */
	public synchronized void stopPlaying()
	{
		agent.scheduleStep(new IComponentStep<Void>()
		{
			@Classname("play")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				if(playing!=null)
				{
					ia.getComponentFeature(IBDIAgentFeature.class).dropGoal(playing);
				}
				playing = null;
				return IFuture.DONE;
			}
		});
	}

	/**
	 *  Show the dialog and return the result.
	 *  @param owner The owner.
	 *  @param alarm The alarm.
	 *  @return The new alarm or null.
	 */
	public static Alarm showDialog(IExternalAccess agent, JFrame owner, Alarm alarm)
	{
		Alarm ret = null;
		AlarmSettingsDialog asd = new AlarmSettingsDialog(agent, owner, alarm);
		asd.pack();
		asd.setLocation(SGUI.calculateMiddlePosition(owner, asd));
		asd.setVisible(true);
		if(asd.isStateOk())
			ret = asd.getAlarm();
		return ret;
	}

	/**
	 *  Main for testing.
	 *  @param args The arguments.
	 */
	public static void main(String[] args)
	{
		Dialog dia = new AlarmSettingsDialog(null,new JFrame(), null);
		dia.pack();
		dia.setVisible(true);
	}
}
