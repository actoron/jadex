package jadex.bdi.examples.marsworld_classic.environment;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIDefaults;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

import jadex.bdi.examples.marsworld_classic.AgentInfo;
import jadex.bdi.examples.marsworld_classic.Environment;
import jadex.bdi.examples.marsworld_classic.Homebase;
import jadex.bdi.examples.marsworld_classic.Location;
import jadex.bdi.examples.marsworld_classic.Target;
import jadex.bdiv3x.features.IBDIXAgentFeature;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IMonitoringComponentFeature;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingIntermediateResultListener;
import jadex.commons.gui.future.SwingResultListener;
import jadex.commons.transformation.annotations.Classname;

/**
 *  This class displays the GUI of the sample application.
 *  It shows the Environment and the positions of the single agents.
 *  Red dots denote the targets that the Sentry Agent is going to check for ore,
 *  to call other Agents to reduce the amount at the targets.
 *  <p/>
 *  The Environment is simulated with methods - when the agents would
 *  interact with the environment, they will call methods.
 */
public class MarsworldGui	extends JFrame
{
	//-------- constants ---------

	/** The image icons. */
	private static UIDefaults icons = new UIDefaults(new Object[]
	{
		"background", SGUI.makeIcon(MarsworldGui.class, "/jadex/bdi/examples/marsworld_classic/images/mars.png"),
		"homebase", SGUI.makeIcon(MarsworldGui.class, "/jadex/bdi/examples/marsworld_classic/images/homebase.png"),
		"target", SGUI.makeIcon(MarsworldGui.class, "/jadex/bdi/examples/marsworld_classic/images/target.png"),
		"sentry", SGUI.makeIcon(MarsworldGui.class, "/jadex/bdi/examples/marsworld_classic/images/sentryagent.png"),
		"production", SGUI.makeIcon(MarsworldGui.class, "/jadex/bdi/examples/marsworld_classic/images/productionagent.png"),
		"carry", SGUI.makeIcon(MarsworldGui.class, "/jadex/bdi/examples/marsworld_classic/images/carryagent.png")
	});

	//-------- attributes --------

	/** The timer for continuously repainting the gui. */
	protected Timer	timer;

	/** The marsworld panel. */ 
	protected JPanel	map;
	
	protected boolean disposed;

	//-------- constructors --------

	/**
	 *  Create a new gui.
	 */
	public MarsworldGui(final IExternalAccess agent)
	{
		super("Mars Environment - Agents collecting ore from targets...");
		
//		// Read the images.
//		this.images = new HashMap();
//		images.put(Environment.SENTRY_AGENT+"_original", loadImage(SENTRY_AGENT_ICON_PATH));
//		images.put(Environment.PRODUCTION_AGENT+"_original", loadImage(PRODUCTION_AGENT_ICON_PATH));
//		images.put(Environment.CARRY_AGENT+"_original", loadImage(CARRY_AGENT_ICON_PATH));
//
//		images.put("homebase", loadImage(HOMEBASE_PATH));
//		images.put("target", loadImage(TARGET_ICON_PATH));
//		images.put("background", loadImage(BACKGROUND_ICON_PATH));

		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				agent.killComponent();
			}
		});
		
		agent.scheduleStep(new IComponentStep<Void>()
		{
			@Classname("dispose")
			public IFuture<Void> execute(IInternalAccess ia)
			{
//				IBDIInternalAccess bia = (IBDIInternalAccess)ia;
//				bia.addComponentListener(new TerminationAdapter()
//				{
//					public void componentTerminated()
//					{
//						SwingUtilities.invokeLater(new Runnable()
//						{
//							public void run()
//							{
//								if(timer!=null)
//									timer.stop();
//								MarsworldGui.this.dispose();
//							}
//						});
//					}
//				});
				
				ia.getFeature(IMonitoringComponentFeature.class).subscribeToEvents(IMonitoringEvent.TERMINATION_FILTER, false, PublishEventLevel.COARSE)
					.addResultListener(new SwingIntermediateResultListener<IMonitoringEvent>(new IntermediateDefaultResultListener<IMonitoringEvent>()
				{
					public void intermediateResultAvailable(IMonitoringEvent result)
					{
						if(timer!=null)
							timer.stop();
						MarsworldGui.this.dispose();
					}
				}));
				return IFuture.DONE;
			}
		}).addResultListener(new SwingResultListener<Void>(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
			}
			
			public void exceptionOccurred(Exception exception)
			{
				if(timer!=null)
					timer.stop();
				MarsworldGui.this.dispose();
			}
		}));
		
//		agent.addAgentListener(new IAgentListener()
//		{
//			public void agentTerminating(AgentEvent ae)
//			{
//				SwingUtilities.invokeLater(new Runnable()
//				{
//					public void run()
//					{
//						MarsworldGui.this.timer.stop();
//						MarsworldGui.this.dispose();
//					}
//				});
//			}
//			
//			public void agentTerminated(AgentEvent ae)
//			{
//			}
//		});

		agent.scheduleStep(new IComponentStep<Void>()
		{
			@Classname("env")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				IBDIXAgentFeature capa = ia.getFeature(IBDIXAgentFeature.class);
				final Environment env = (Environment)capa.getBeliefbase().getBelief("environment").getFact();
		
				// On what thread?
				map	= createMarsworldPanel(env);
				
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						if(!disposed)
						{
							JLabel sentrylabel = new JLabel(": Sentry Agent", icons.getIcon("sentry"), JLabel.LEADING);
							JLabel prodname = new JLabel(": Production Agent", icons.getIcon("production"), JLabel.LEADING);
							JLabel carryname = new JLabel(": Carry Agent", icons.getIcon("carry"), JLabel.LEADING);
							JLabel homename = new JLabel(": Homebase", icons.getIcon("homebase"), JLabel.LEADING);
							JLabel targetname = new JLabel(": Target", icons.getIcon("target"), JLabel.LEADING);
			
							JTextPane helptext = new JTextPane();
							helptext.setText("A group of robots is searching for ore on Mars...\n\n" +
								"This example was inspired by the book " +
								"'Multiagentsystems' written by Jaques Ferber.");
							helptext.setEnabled(false);
							helptext.setEditable(false);
			
							JPanel p1 = new JPanel();
							p1.setLayout(new BoxLayout(p1, BoxLayout.Y_AXIS));
							p1.add(homename);
							p1.add(sentrylabel);
							p1.add(prodname);
							p1.add(carryname);
							p1.add(targetname);
							//p1.setBounds(513, 224, 148, 186);
							p1.setBorder(BorderFactory.createTitledBorder(null, "Description",
								TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null,null));
			
							JPanel p2 = new JPanel();
							p2.setLayout(new GridBagLayout());
							p2.add(helptext, new GridBagConstraints(0, 0, 1, 1, 1, 0,
								GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
							//p2.setBounds(515, 10, 143, 177);
							p2.setBorder(BorderFactory.createTitledBorder(null, "Mars Robots",
								TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
			
			
							Insets insets = new Insets(2, 4, 2, 4);
							JPanel content = new JPanel();
							content.setLayout(new GridBagLayout());
							content.add(map, new GridBagConstraints(0, 0, 1, 3, 1, 1,
								GridBagConstraints.CENTER, GridBagConstraints.BOTH, insets, 0, 0));
							content.add(p1, new GridBagConstraints(1, 0, 1, 1, 0, 0,
								GridBagConstraints.CENTER, GridBagConstraints.NONE, insets, 0, 0));
							content.add(p2, new GridBagConstraints(1, 1, 1, 1, 0, 0,
								GridBagConstraints.CENTER, GridBagConstraints.NONE, insets, 0, 0));
							content.add(new JPanel(), new GridBagConstraints(1, 2, 1, 1, 0, 1,
								GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));
							content.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
							content.setEnabled(false);
			
							p2.setMinimumSize(new Dimension((int)p1.getMinimumSize().getWidth(), 160));
							p2.setPreferredSize(new Dimension((int)p1.getPreferredSize().getWidth(),  160));
			
							setContentPane(content);
							setSize(600, 450);
							setLocation(SGUI.calculateMiddlePosition(MarsworldGui.this));
							setVisible(true);
//							System.out.println("marsworld set visible");
							
							// Continuously repaint the gui.
							timer	= new Timer(100, new ActionListener()
							{
								public void actionPerformed(ActionEvent e)
								{
									MarsworldGui.this.map.repaint();
								}
							});
							timer.start();
						}
					}
				});
				
				return IFuture.DONE;
			}
		});
			
		// Create the gui.
//		agent.getBeliefbase().getBeliefFact("environment").addResultListener(new SwingDefaultResultListener(MarsworldGui.this)
//		{
//			public void customResultAvailable(Object source, Object result)
//			{
//				Environment	env	= (Environment)result;
//				
//				map	= createMarsworldPanel(env);
//
//				JLabel sentrylabel = new JLabel(": Sentry Agent", icons.getIcon("sentry"), JLabel.LEADING);
//				JLabel prodname = new JLabel(": Production Agent", icons.getIcon("production"), JLabel.LEADING);
//				JLabel carryname = new JLabel(": Carry Agent", icons.getIcon("carry"), JLabel.LEADING);
//				JLabel homename = new JLabel(": Homebase", icons.getIcon("homebase"), JLabel.LEADING);
//				JLabel targetname = new JLabel(": Target", icons.getIcon("target"), JLabel.LEADING);
//
//				JTextPane helptext = new JTextPane();
//				helptext.setText("A group of robots is searching for ore on Mars...\n\n" +
//					"This example was inspired by the book " +
//					"'Multiagentsystems' written by Jaques Ferber.");
//				helptext.setEnabled(false);
//				helptext.setEditable(false);
//
//				JPanel p1 = new JPanel();
//				p1.setLayout(new BoxLayout(p1, BoxLayout.Y_AXIS));
//				p1.add(homename);
//				p1.add(sentrylabel);
//				p1.add(prodname);
//				p1.add(carryname);
//				p1.add(targetname);
//				//p1.setBounds(513, 224, 148, 186);
//				p1.setBorder(BorderFactory.createTitledBorder(null, "Description",
//					TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null,null));
//
//				JPanel p2 = new JPanel();
//				p2.setLayout(new GridBagLayout());
//				p2.add(helptext, new GridBagConstraints(0, 0, 1, 1, 1, 0,
//					GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
//				//p2.setBounds(515, 10, 143, 177);
//				p2.setBorder(BorderFactory.createTitledBorder(null, "Mars Robots",
//					TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
//
//
//				Insets insets = new Insets(2, 4, 2, 4);
//				JPanel content = new JPanel();
//				content.setLayout(new GridBagLayout());
//				content.add(map, new GridBagConstraints(0, 0, 1, 3, 1, 1,
//					GridBagConstraints.CENTER, GridBagConstraints.BOTH, insets, 0, 0));
//				content.add(p1, new GridBagConstraints(1, 0, 1, 1, 0, 0,
//					GridBagConstraints.CENTER, GridBagConstraints.NONE, insets, 0, 0));
//				content.add(p2, new GridBagConstraints(1, 1, 1, 1, 0, 0,
//					GridBagConstraints.CENTER, GridBagConstraints.NONE, insets, 0, 0));
//				content.add(new JPanel(), new GridBagConstraints(1, 2, 1, 1, 0, 1,
//					GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));
//				content.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
//				content.setEnabled(false);
//
//				p2.setMinimumSize(new Dimension((int)p1.getMinimumSize().getWidth(), 160));
//				p2.setPreferredSize(new Dimension((int)p1.getPreferredSize().getWidth(),  160));
//
//				setContentPane(content);
//				setSize(600, 450);
//				setLocation(SGUI.calculateMiddlePosition(MarsworldGui.this));
//				setVisible(true);
//				
//				// Continuously repaint the gui.
//				timer	= new Timer(100, new ActionListener()
//				{
//					public void actionPerformed(ActionEvent e)
//					{
//						MarsworldGui.this.map.repaint();
//					}
//				});
//				timer.start();
//			}
//		});
	}
	
	public void dispose()
	{
		disposed	= true;
		super.dispose();
	}

	//-------- methods --------

	/**
	 *  Create the map panel.
	 *  @return The created panel.
	 */
	protected JPanel createMarsworldPanel(final Environment env)
	{
		JPanel	ret	= new JPanel(true)
		{
			/** Copies of icons scaled to fit current window size. */
			Map	images	= new HashMap();

			/** The current scale factor. */
			double	scale;
			
			/**
			 *  Overridden paint method,
			 *  actual display of the items is done here.
			 */
			protected void paintComponent(Graphics g)
			{
				super.paintComponent(g);
				Rectangle bounds = getBounds();

				// Paint background.
				Image image = ((ImageIcon)icons.getIcon("background")).getImage();
				int w = image.getWidth(this);
				int h = image.getHeight(this);
				if(w>0 && h>0)
				{
					for(int y=0; y<bounds.height; y+=h)
					{
						for(int x=0; x<bounds.width; x+=w)
						{
							g.drawImage(image, x, y, this);
						}
					}
				}

				// Do scaling of images.
				double newscale = bounds.getWidth()/600.0;
				if(Math.abs(newscale-scale) > 0.00001)
				{
					scale = newscale;
					Image img = ((ImageIcon)icons.getIcon("sentry")).getImage();
					int wx = (int)(img.getWidth(this)*scale);
					int wy = (int)(img.getHeight(this)*scale);
					if(wx>0 && wy>0)
						images.put(Environment.SENTRY_AGENT, img.getScaledInstance(wx, wy, Image.SCALE_DEFAULT));

					img = ((ImageIcon)icons.getIcon("production")).getImage();
					wx = (int)(img.getWidth(this)*scale);
					wy = (int)(img.getHeight(this)*scale);
					if(wx>0 && wy>0)
						images.put(Environment.PRODUCTION_AGENT, img.getScaledInstance(wx, wy, Image.SCALE_DEFAULT));
					
					img = ((ImageIcon)icons.getIcon("carry")).getImage();
					wx = (int)(img.getWidth(this)*scale);
					wy = (int)(img.getHeight(this)*scale);
					if(wx>0 && wy>0)
						images.put(Environment.CARRY_AGENT, img.getScaledInstance(wx, wy, Image.SCALE_DEFAULT));
				}

				// Display the agents.
				AgentInfo[] infos = env.getAgentInfos();
				for(int i=0; i < infos.length; i++)
				{
					Image img = (Image)images.get(infos[i].getType());
					if(img!=null)
					{
						Location loc = infos[i].getLocation();
						int x = (int)(loc.getX()*bounds.getWidth());
						int y = (int)(loc.getY()*bounds.getHeight());
						int wx = img.getWidth(this);
						int wy = img.getHeight(this);
						int vw = (int)(infos[i].getVision()*bounds.getWidth());
						g.drawImage(img, x-wx/2, y-wy/2, this);
						g.setColor(new Color(250, 250, 30, 50));
						g.fillOval(x-vw, y-vw, vw*2, vw*2);
						String txt = infos[i].getName();
						g.setColor(Color.black);
						g.drawString(txt, x-wx/2, y+wy/2+12); // todo: do not use not absolute coords
					}
				}

				// Paint homebase.
				Homebase home = env.getHomebase();
				Location loc = home.getLocation();
				int x = (int)(loc.getX()*bounds.getWidth());
				int y = (int)(loc.getY()*bounds.getHeight());
				int wx = (int)(0.14*bounds.getWidth());
				int wy = (int)(0.12*bounds.getWidth());
				g.setColor(new Color(30,30,30,70));
				g.fillRect(x-wx/2, y-wy/2, wx, wy);
				g.setColor(Color.black);
				g.drawRect(x-wx/2, y-wy/2, wx, wy);
				g.drawString("Collected ore: "+home.getOre(), x-wx/2, y+wy/2+12);
				SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
				try
				{
					g.drawString("Remaining time: "+sdf.format(new Date(env.getHomebase()
							.getRemainingMissionTime())), x-wx/2, y+wy/2+22);
				}
				catch(NullPointerException e)
				{
					// Can happen when clock already terminated but dipose() not yet called on swing thread.
				}
				/*Image img = (Image)images.get("homebase");
				if(img!=null)
				{
					int wx = img.getWidth(this);
					int wy = img.getHeight(this);
					//System.out.println("wx: "+wx+" "+wy);
					g.drawImage(img, x-wx/2, y-wy/2, this);
					g.setColor(Color.black);
				}*/

				// Display the targets.
				Target[]	targets	= env.getTargets();
				for(int i = 0; i<targets.length; i++)
				{
					loc = targets[i].getLocation();
					x = (int)(loc.getX()*bounds.getWidth());
					y = (int)(loc.getY()*bounds.getHeight());
					int wxy = Math.min(12, Math.max(1, (int)(0.02*bounds.getWidth())));

					if((targets[i]).isMarked())
					{
						g.setColor(Color.black);
						g.drawString("ore: "+targets[i].getOre(), x+20, y); // todo: do not use not absolute coords
						g.drawString("capacity:"+targets[i].getOreCapacity(), x+10, y+10);
						g.setColor(new Color(50, 50, 50, 150));
					}
					else
						g.setColor(new Color(200, 0, 0, 150));

					g.fillOval(x-wxy/2, y-wxy/2, wxy, wxy);
				}
			}
		};
		//ret.setBounds(10, 10, 500, 400);
		ret.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		return ret;
	}
}