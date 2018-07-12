package jadex.bdi.examples.blackjack.manager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.table.TableModel;

import jadex.base.gui.CMSUpdateHandler;
import jadex.base.gui.ComponentSelectorDialog;
import jadex.base.gui.componenttree.ComponentIconCache;
import jadex.bdi.examples.blackjack.Player;
import jadex.bdi.examples.blackjack.gui.GUIImageLoader;
import jadex.bdi.examples.blackjack.player.strategies.AbstractStrategy;
import jadex.bdiv3x.features.IBDIXAgentFeature;
import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IMonitoringComponentFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.commons.future.DefaultTuple2ResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.commons.gui.future.SwingIntermediateResultListener;
import jadex.commons.gui.future.SwingResultListener;
import jadex.commons.transformation.annotations.Classname;

/**
 *  The manager frame.
 */
public class ManagerFrame extends JFrame implements ActionListener, WindowListener
{
	//-------- constants --------

	/** The dealer default adf. */
	protected static final String LOCAL_DEALER = "BlackjackDealer";

	//-------- attributes --------

	protected JPanel playerpan;

	protected JPanel dealerpan;
	protected JTextField dealertf;
	protected IComponentIdentifier dealeraid;

	protected JButton exitButton;

	protected JLabel localDealerLabel;
	protected JButton localDealerButton;
	protected String localDealerNameString = "";
	protected String localDealerMaxPlayerString = "";
	protected String localDealerPlayerPlayingString = "";

	protected Timer enableTimer;

	protected JTable dealertable;
	protected TableModel dealermodel;

	protected IExternalAccess agent;
	
	//-------- constructors --------

	/**
	 * Create a new plan.
	 */
	public ManagerFrame(final IExternalAccess access)
	{
		super("Blackjack Manager");
		
		// set the icon to be displayed for the frame
		ImageIcon icon = GUIImageLoader.getImage("heart_small_m");
		this.setIconImage(icon.getImage());

		this.agent = access;
		this.addWindowListener(this);

		// let this class completly handle the window-closing (see exit()-method)
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		enableTimer = new Timer(2000, new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				localDealerButton.setEnabled(true);
				enableTimer.stop();
			}
		});

		Container cp = this.getContentPane();
		cp.setBackground(Color.WHITE);
		cp.setLayout(new BorderLayout());

		// init player panel
		playerpan = new JPanel();
		playerpan.setBorder(BorderFactory.createTitledBorder(" Player "));
		playerpan.setBackground(Color.WHITE);

		// init dealer panel
		dealerpan = new JPanel();
		dealerpan.setBorder(BorderFactory.createTitledBorder(" Dealer "));
		dealerpan.setBackground(Color.WHITE);
		access.scheduleStep(new IComponentStep<Void>()
		{
			@Classname("dealerpan")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				IFuture<IComponentManagementService>	cms	= ia.getFeature(IRequiredServicesFeature.class).getService("cms");
//				if(cms.isDone() && cms.get(null)==null)
//					Thread.dumpStack();
				cms.addResultListener(new SwingResultListener<IComponentManagementService>(new IResultListener<IComponentManagementService>()
				{
					public void resultAvailable(final IComponentManagementService ces)
					{
//						dealeraid = ces.createComponentIdentifier(LOCAL_DEALER, access.getComponentIdentifier().getParent(), null);
						dealeraid = new BasicComponentIdentifier(LOCAL_DEALER, access.getId().getParent());
						dealertf.setText(dealeraid.getName());
					}
					public void exceptionOccurred(Exception exception)
					{
					}
				}));
				return IFuture.DONE;
			}
		});
		
		dealertf = new JTextField(20);
		dealertf.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				access.scheduleStep(new IComponentStep<Void>()
				{
					@Classname("dealertf")
					public IFuture<Void> execute(IInternalAccess ia)
					{
						ia.getFeature(IRequiredServicesFeature.class).getService("cms")
							.addResultListener(new SwingDefaultResultListener(ManagerFrame.this)
						{
							public void customResultAvailable(Object result)
							{
								final IComponentManagementService ces = (IComponentManagementService)result;
//								dealeraid = ces.createComponentIdentifier(dealertf.getText(), false, null);
								dealeraid = new BasicComponentIdentifier(dealertf.getText());
							}
						});
						return IFuture.DONE;
					}
				});
			}
		});
		
		final	CMSUpdateHandler	cmsuh	= new CMSUpdateHandler(access);
		
		final	ComponentSelectorDialog	csd	= new ComponentSelectorDialog(ManagerFrame.this, access, access, cmsuh, null, new ComponentIconCache(access));
		JButton	dealerbut	= new JButton("...");
		dealerbut.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				IComponentIdentifier	cid	= csd.selectAgent(dealeraid);
				if(cid!=null)
				{
					dealeraid	= cid;
					dealertf.setText(cid.getName());
				}
			}
		});
		
		dealerpan.add(dealertf);
		dealerpan.add(dealerbut);

		JPanel centerpan = new JPanel(new BorderLayout());
		centerpan.add(playerpan, BorderLayout.CENTER);
		centerpan.add(dealerpan, BorderLayout.SOUTH);

		JPanel buttonpan = new JPanel();
		buttonpan.setBackground(Color.WHITE);
		localDealerButton = new JButton("Start local Dealer");
		localDealerButton.addActionListener(this);
		exitButton = new JButton("Exit Blackjack");
		exitButton.addActionListener(this);
		buttonpan.add(localDealerButton);
		buttonpan.add(exitButton);

		//cp.add(new JLabel(loadLogo()), BorderLayout.NORTH);
		cp.add(new JLabel(GUIImageLoader.getImage("logo")), BorderLayout.NORTH);
		cp.add(centerpan, BorderLayout.CENTER);
		cp.add(buttonpan, BorderLayout.SOUTH);
		this.setSize(480, 570);

		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension dim = toolkit.getScreenSize();
		this.setLocation((int)(dim.getWidth()/2-this.getWidth()/2),
			(int)(dim.getHeight()/2-this.getHeight()/2));

		this.setLocation(SGUI.calculateMiddlePosition(this));
		this.setVisible(true);
		
		// Dispose frame on exception.
		IResultListener<Void>	dislis	= new IResultListener<Void>()
		{
			public void exceptionOccurred(Exception exception)
			{
				dispose();
			}
			public void resultAvailable(Void result)
			{
			}
		};
		
//		EventQueue.invokeLater(new Runnable()
//		{
//			/**
//			 * creates the Panel, where the player-information is shown.
//			 * This method handles some special cases, i.e. a player - once created - should
//			 * be shown as long as it is stopped, even if the dealer is killed,
//			 * so don't take a look too close at all these for-loops, most of them
//			 * are really just for gui-convenience purposes ;-)
//			 */
//			public void run()
//			{
				// create new Player Panels with the properties as specified in the Manager.xml
		
		agent.scheduleStep(new IComponentStep<Void>()
		{
			@Classname("players")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				IBDIXAgentFeature bia = ia.getFeature(IBDIXAgentFeature.class);
				final Player[] players = (Player[])bia.getBeliefbase().getBeliefSet("players").getFacts();
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						JPanel playerDealerPanel = (JPanel)getContentPane().getComponent(1);
						JPanel playerPanel = (JPanel)playerDealerPanel.getComponent(0);
						playerPanel.setLayout(new GridLayout(players.length, 1, 0, 0));
						playerPanel.setBackground(Color.WHITE);

						for(int i = 0; i<players.length; i++)
						{
							playerPanel.add(new ManagerPlayerPanel(i+1, players[i]));
						}

						getContentPane().add(playerDealerPanel, 1);
						getContentPane().validate();
					}
				});
				return IFuture.DONE;
			}
		}).addResultListener(dislis);

		agent.scheduleStep(new IComponentStep<Void>()
		{
			@Classname("dispose")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				ia.getFeature(IMonitoringComponentFeature.class).subscribeToEvents(IMonitoringEvent.TERMINATION_FILTER, false, PublishEventLevel.COARSE)
					.addResultListener(new SwingIntermediateResultListener<IMonitoringEvent>(new IntermediateDefaultResultListener<IMonitoringEvent>()
				{
					public void intermediateResultAvailable(IMonitoringEvent result)
					{
						ManagerFrame.this.dispose();
						cmsuh.dispose();
					}
				}));
				return IFuture.DONE;
			}
		}).addResultListener(dislis);
	}

	/**
	 * @param playerPlaying
	 */
	public void setPlayerPlaying(String playerPlaying)
	{
		localDealerPlayerPlayingString = playerPlaying;
		//setDealerLabels(localDealerNameString, null);
	}

	/**
	 * @param startMode
	 */
	public void setLocalDealerButtonMode(boolean startMode)
	{
		if(startMode)
		{
			localDealerButton.setForeground(Color.BLACK);
			localDealerButton.setText("Start local Dealer");
		}
		else
		{
			localDealerButton.setForeground(Color.RED);
			localDealerButton.setText("Stop local Dealer");
		}
	}

	/**
	 * @param e
	 */
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource()==localDealerButton)
		{
			if(localDealerButton.getText().startsWith("Start"))
			{
				setLocalDealerButtonMode(false);
				localDealerButton.setEnabled(false);
				enableTimer.start();
				startLocalDealer();
			}
			else
			{
				setLocalDealerButtonMode(true);
				stopLocalDealer();
			}
		}
		else if(e.getSource()==exitButton)
		{
			this.exit();
		}
	}

	/**
	 * 
	 */
	protected void exit()
	{
		Object[] options = {"Yes", "No", "Cancel"};
		final int n = JOptionPane.showOptionDialog(this, "Kill all local BlackJack-Agents (Player and Dealer) ?",
				"Close Agents on exit", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
				null, options, options[0]);
		if(n!=JOptionPane.CANCEL_OPTION)
		{
			if(n==JOptionPane.YES_OPTION)
			{
				agent.scheduleStep(new IComponentStep<Void>()
				{
					@Classname("close")
					public IFuture<Void> execute(IInternalAccess ia)
					{
						ia.getFeature(IRequiredServicesFeature.class).getService("cms").addResultListener(new SwingDefaultResultListener(ManagerFrame.this)
						{
							public void customResultAvailable(Object result)
							{
								final IComponentManagementService	cms	= (IComponentManagementService)result;
								cms.destroyComponent(agent.getId().getParent());
							}
						});

						return IFuture.DONE;
					}
				});
			}
			else
			{
				agent.killComponent();
			}

			this.setVisible(false);
			this.dispose();
		}
	}
	
	// ---------- windowListener-methods --------------
	
	public void windowClosing(WindowEvent e)
	{
		this.exit();
	}

	public void windowClosed(WindowEvent e)
	{
		// nothing to do
	}

	public void windowOpened(WindowEvent e)
	{
		// nothing to do
	}

	public void windowIconified(WindowEvent e)
	{
		// nothing to do
	}

	public void windowDeiconified(WindowEvent e)
	{
		// nothing to do
	}

	public void windowActivated(WindowEvent e)
	{
		// nothing to do
	}

	public void windowDeactivated(WindowEvent e)
	{
		// nothing to do
	}

	//-------- helper methods --------

	/**
	 * Start a local dealer agent.
	 */
	protected void startLocalDealer()
	{
		agent.scheduleStep(new IComponentStep<Void>()
		{
			@Classname("startDealer")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				final Future<Void>	ret	= new Future<Void>();
				final IBDIXAgentFeature bia = ia.getFeature(IBDIXAgentFeature.class);
				
				IComponentManagementService	cms	= ia.getFeature(IRequiredServicesFeature.class)
					.searchService(new ServiceQuery<>(IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)).get();
				cms.createComponent("BlackjackDealer", "jadex/bdi/examples/blackjack/dealer/Dealer.agent.xml",
					new CreationInfo(ia.getId().getParent()))
				.addResultListener(new DefaultTuple2ResultListener<IComponentIdentifier, Map<String, Object>>()
				{
					@Override
					public void firstResultAvailable(IComponentIdentifier dealer)
					{
						bia.getLogger().info("local DealerAgent started: "+dealer);
						//access.getBeliefbase().getBelief("localDealerAID").setFact(start.getResult());
						bia.getBeliefbase().getBelief("localDealerAID").setFact(dealer);
						ret.setResult(null);
					}
					
					@Override
					public void secondResultAvailable(Map<String, Object> result)
					{
					}
					
					@Override
					public void exceptionOccurred(Exception exception)
					{
						ret.setExceptionIfUndone(exception);
					}
				});

				return ret;
			}
		}).addResultListener(new SwingDefaultResultListener<Void>()	// Add listener for automatic error dialog.
		{
			public void customResultAvailable(Void result)
			{
				// dealer successfully created.
			}
		});
	}

	/**
	 * Stop the local dealer agent.
	 */
	protected void stopLocalDealer()
	{
		agent.scheduleStep(new IComponentStep<Void>()
		{
			@Classname("destroy")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				IBDIXAgentFeature bia = ia.getFeature(IBDIXAgentFeature.class);
				IComponentIdentifier dealer = (IComponentIdentifier)bia.getBeliefbase().getBelief("localDealerAID").getFact();
				if(dealer!=null)
				{
					IComponentManagementService	cms	= ia.getFeature(IRequiredServicesFeature.class)
						.searchService(new ServiceQuery<>(IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)).get();
					cms.destroyComponent(dealer);
					bia.getBeliefbase().getBelief("localDealerAID").setFact(null);
				}
				return IFuture.DONE;
			}
		});
	}

	/**
	 *
	 */
	public class ManagerPlayerPanel extends JPanel implements ActionListener
	{
		//-------- attributes --------

		private JButton colorButton;
		private JButton actionButton;
		private JTextField name;
		private JTextField initialAccount;
		private JComboBox strategy;

		private Player player;

		/**
		 * Timer, that enables the start/stop-button 1 second after the user
		 * pressed the button (pressing the button, disables it, for stability-reasons
		 */
		private Timer enableTimer;


		//-------- constructors --------

		/**
		 * Create a new playerPanel.
		 * A playerPanel contains all neccessary startup-information about the player
		 */
		public ManagerPlayerPanel(int id, Player player)
		{
			super();
			//System.out.println("Player: "+nameString+" "+colorString);
			this.player = player;

			this.setBackground(Color.WHITE);

			name = new JTextField(player.getName(), 4);
			name.setToolTipText("Agent's name");
			initialAccount = new JTextField(""+player.getAccount(), 4);
			initialAccount.setToolTipText("Agent's initial account");
			colorButton = new JButton("Color");
			colorButton.setBackground(player.getColor());
			colorButton.addActionListener(this);
			colorButton.setToolTipText("Agent's color");

			DefaultComboBoxModel cModel = new DefaultComboBoxModel(AbstractStrategy.getStrategyNames());
			cModel.addElement(AbstractStrategy.HUMAN_PLAYER);

			strategy = new JComboBox(cModel);
			strategy.setBackground(Color.WHITE);
			strategy.setSelectedItem(player.getStrategyName());
			strategy.setToolTipText("Agent's Strategy");

			actionButton = new JButton("Start");
			actionButton.setActionCommand("start");
			actionButton.addActionListener(this);
			actionButton.setToolTipText("start/stop Agent");
			//actionButton.setBackground(new Color(221,221,221));

			this.add(new JLabel(id+"."));
			this.add(name);
			this.add(initialAccount);
			this.add(colorButton);
			this.add(strategy);
			// this.add(localRemote); (used in next Jadex-Release)
			this.add(actionButton);

			enableTimer = new Timer(1000, new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					actionButton.setEnabled(true);
					enableTimer.stop();
				}
			});
		}

		public void actionPerformed(ActionEvent e)
		{
			if(e.getSource()==colorButton)
			{
				Color newColor = JColorChooser.showDialog(this, "Choose Agent Color",
						colorButton.getBackground());
				if((newColor!=null) && (!newColor.equals(Color.WHITE)))
					colorButton.setBackground(newColor);
			}
			else if(e.getSource()==actionButton)
			{
				boolean startPlayerAgent = e.getActionCommand().equals("start");

				actionButton.setEnabled(false);
				enableTimer.start();

				colorButton.setEnabled(!startPlayerAgent);
				name.setEnabled(!startPlayerAgent);
				initialAccount.setEnabled(!startPlayerAgent);
				strategy.setEnabled(!startPlayerAgent);

				if(startPlayerAgent)
				{
					actionButton.setForeground(Color.RED);
					actionButton.setText("Stop");
					actionButton.setActionCommand("stop");

					// Copy values to player object.
					player.setState(Player.STATE_UNREGISTERED);
					player.setName(name.getText());
					player.setColorValue(colorButton.getBackground().getRGB());
					player.setAccount(Integer.parseInt(initialAccount.getText()));
					player.setBet(0);
					player.setStrategyName((String)strategy.getSelectedItem());
//					player.setStrategy(null);

					startPlayer(player);
					// this.remove(localRemote); (used in next Jadex-Release)
					// this.add(detailButton,5); (used in next Jadex-Release)
				}
				else
				{
					actionButton.setForeground(Color.BLACK);
					actionButton.setText("Start");
					actionButton.setActionCommand("start");
					// this.remove(detailButton); (used in next Jadex-Release)
					// this.add(localRemote,5); (used in next Jadex-Release)
					stopPlayer(player);
				}
			}
		}

		//-------- helper methods --------

		/**
		 * Start a player agent.
		 */
		protected void startPlayer(final Player player)
		{
			// try to start player-Agent.
			
//			agent.getLogger().info("starting playerAgent: "+player.getName());
			agent.scheduleStep(new IComponentStep<Void>()
			{
				@Classname("start")
				public IFuture<Void> execute(IInternalAccess ia)
				{
					final Future<Void>	ret	= new Future<Void>();
					try
					{
						IBDIXAgentFeature bia = ia.getFeature(IBDIXAgentFeature.class);
						bia.getLogger().info("starting playerAgent: "+player.getName());
						
						IComponentManagementService	cms	= ia.getFeature(IRequiredServicesFeature.class)
							.searchService(new ServiceQuery<>(IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)).get();
						Map<String, Object> args = new HashMap<String, Object>();
						args.put("myself", player);
						args.put("dealer", dealeraid);
						cms.createComponent(player.getName(), "jadex/bdi/examples/blackjack/player/Player.agent.xml",
							new CreationInfo(args, ia.getId().getParent()))
						.addResultListener(new DefaultTuple2ResultListener<IComponentIdentifier, Map<String, Object>>()
						{
							@Override
							public void firstResultAvailable(IComponentIdentifier playerid)
							{
								player.setAgentID(playerid);
								ret.setResult(null);
							}
							
							@Override
							public void secondResultAvailable(Map<String, Object> result)
							{
							}
							
							@Override
							public void exceptionOccurred(Exception exception)
							{
								ret.setExceptionIfUndone(exception);
							}
						});
					}
					catch(RuntimeException e)
					{
						ret.setException(e);
					}
					return ret;
				}
			});
		}

		/**
		 * Stop a player agent.
		 */
		protected void stopPlayer(final Player player)
		{
			agent.scheduleStep(new IComponentStep<Void>()
			{
				@Classname("stop")
				public IFuture<Void> execute(IInternalAccess ia)
				{
					IComponentManagementService	cms	= ia.getFeature(IRequiredServicesFeature.class)
						.searchService(new ServiceQuery<>(IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)).get();
					cms.destroyComponent(player.getAgentID());
					return IFuture.DONE;
				}
			});
		}
	}
}
