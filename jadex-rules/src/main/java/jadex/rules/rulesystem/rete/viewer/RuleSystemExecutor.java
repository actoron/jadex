package jadex.rules.rulesystem.rete.viewer;

import jadex.commons.ICommand;
import jadex.commons.SGUI;
import jadex.commons.concurrent.Executor;
import jadex.commons.concurrent.IExecutable;
import jadex.commons.concurrent.IThreadPool;
import jadex.commons.concurrent.ThreadPoolFactory;
import jadex.rules.rulesystem.Activation;
import jadex.rules.rulesystem.IRule;
import jadex.rules.rulesystem.RuleSystem;
import jadex.rules.state.viewer.OAVTreeModel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIDefaults;

/**
 * 
 */
public class RuleSystemExecutor implements ISteppable
{
	//-------- constants --------

	/**
	 * The image icons.
	 */
	protected static final UIDefaults	icons	= new UIDefaults(new Object[]{
		"show_state", SGUI.makeIcon(RuleSystemExecutor.class, "/jadex/rules/rulesystem/rete/viewer/images/bulb2.png"),
		"show_rete", SGUI.makeIcon(RuleSystemExecutor.class, "/jadex/rules/rulesystem/rete/viewer/images/bug_small.png"),
	});

	//-------- attributes --------

	/** The stepmode flag. */
	protected boolean stepmode;
	
	/** Flag indicating that a single step should be performed. */
	protected boolean dostep;
	
	/** The agenda. */
	protected RuleSystem rulesystem;
	
	/** The executor. */
	protected Executor executor;
	
	/** The breakpoints (i.e. rules that set the interpreter to step mode, when activated). */
	protected Set breakpoints;
	
	/** The breakpoint commands. */
	protected ICommand[]	breakpointcommands;
	
	//-------- constructors --------
	
	/**
	 * 
	 */
	public RuleSystemExecutor(final RuleSystem rulesystem, boolean stepmode)
	{
		this(rulesystem, stepmode, null);
	}
	
	/**
	 * 
	 */
	public RuleSystemExecutor(final RuleSystem rulesystem, boolean stepmode, IThreadPool threadpool)
	{
		this.rulesystem = rulesystem;
		this.executor = new Executor(threadpool!=null? threadpool: ThreadPoolFactory.getThreadPool("default"), 
			new IExecutable()
		{
			public boolean execute()
			{
				// Check for breakpoints, if any.
				if(breakpoints!=null)
				{
					Iterator	it	= rulesystem.getAgenda().getActivations().iterator();
					while(it.hasNext())
					{
						IRule	rule	= ((Activation)it.next()).getRule();
						if(breakpoints.contains(rule))
						{
							setStepmode(true);
							
							// Notify listeners
							if(breakpointcommands!=null)
							{
								for(int i=0; i<breakpointcommands.length; i++)
								{
									breakpointcommands[i].execute(rule);
								}
							}
							break;
						}
					}
				}

				if(!isStepmode() || RuleSystemExecutor.this.dostep)
				{
					RuleSystemExecutor.this.dostep = false;
					// synchronized(monitor)
					{
						rulesystem.getAgenda().fireRule();
						rulesystem.getState().expungeStaleObjects();
						rulesystem.getState().notifyEventListeners();
						
//						for(Iterator it= rulesystem.getState().getObjects(); it.hasNext(); )
//							System.out.println(it.next());
					}
				}
				
				return !rulesystem.getAgenda().isEmpty() && !isStepmode();
			}
		});
		
		setStepmode(stepmode);
	}
	
	//-------- steppable interface --------
	
	/**
	 *  Execute a step.
	 */
	public void doStep()
	{
		dostep = true;
		if(stepmode)
			executor.execute();
	}
	
	/**
	 *  Set the stepmode.
	 *  @param stepmode True for stepmode.
	 */
	public void setStepmode(boolean stepmode)
	{
		this.stepmode = stepmode;
		if(!stepmode)
			executor.execute();
	}
	
	/**
	 *  Test if in stepmode.
	 *  @return True, if in stepmode.
	 */
	public boolean isStepmode()
	{
		return this.stepmode;
	}
	
	/**
	 *  Add a breakpoint to the interpreter.
	 */
	public void	addBreakpoint(IRule rule)
	{
		if(breakpoints==null)
			breakpoints	= new HashSet();
		breakpoints.add(rule);
	}
	
	/**
	 *  Remove a breakpoint from the interpreter.
	 */
	public void	removeBreakpoint(IRule rule)
	{
		if(breakpoints.remove(rule) && breakpoints.isEmpty())
			breakpoints	= null;
	}
	
	/**
	 *  Check if a rule is a breakpoint for the interpreter.
	 */
	public boolean	isBreakpoint(IRule rule)
	{
		return breakpoints!=null && breakpoints.contains(rule);
	}
	
	/**
	 *  Add a command to be executed, when a breakpoint is reached.
	 */
	public void	addBreakpointCommand(ICommand command)
	{
		if(breakpointcommands==null)
		{
			breakpointcommands	= new ICommand[]{command};
		}
		else
		{
			ICommand[]	newarray	= new ICommand[breakpointcommands.length+1];
			System.arraycopy(breakpointcommands, 0, newarray, 0, breakpointcommands.length);
			newarray[breakpointcommands.length]	= command;
			breakpointcommands	= newarray;
		}
	}
	
	//-------- methods --------
	
	/**
	 *  Create a frame for a rete structure.
	 *  @param title	The title for the frame.
	 *  @param rs	The rule system.
	 *  @return	The frame.
	 */
	public JFrame createReteFrame(String title)
	{
		JComponent	tabs	= createToolPanel(rulesystem, this);
		JFrame f = new JFrame(title);
		f.getContentPane().setLayout(new BorderLayout());
		f.add("Center", tabs);
		f.setSize(800,600);
        f.setVisible(true);
        
        // todo: integrate state viewer
		
		return f;
	
	}

	/**
	 *  Create a panel for a steppable.
	 */
	public static JComponent	createToolPanel(final RuleSystem rulesystem, final ISteppable steppable)
	{
		JPanel	oavpanel	= OAVTreeModel.createOAVPanel(rulesystem.getState());
		RetePanel rp = new RetePanel(rulesystem, steppable);
		rp.getRulebasePanel().getList().setCellRenderer(new DefaultListCellRenderer()
		{
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
			{
				Component	ret	= super.getListCellRendererComponent(list, ((IRule)value).getName(), index, isSelected, cellHasFocus);
				if(steppable.isBreakpoint((IRule)value))
					setBackground(Color.red);
				return ret;
			}
		});
		rp.getRulebasePanel().getList().addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				if(e.isPopupTrigger())
					doPopup(e);
			}
			public void mouseReleased(MouseEvent e)
			{
				if(e.isPopupTrigger())
					doPopup(e);
			}
			public void	doPopup(MouseEvent e)
			{
				JList	list	= (JList)e.getSource();
				int index	= list.locationToIndex(e.getPoint());
				if(index!=-1)
				{
					Iterator	it	= rulesystem.getRulebase().getRules().iterator();
					for(int i=0; i<index && it.hasNext(); i++)
					{
						it.next();
					}
					if(it.hasNext())
					{
						IRule	rule	= (IRule)it.next();
						if(steppable.isBreakpoint(rule))
							steppable.removeBreakpoint(rule);
						else
							steppable.addBreakpoint(rule);
						list.repaint();
					}
				}
			}
		});
		
		JComponent[]	tools	= new JComponent[]{oavpanel, rp};
		oavpanel.setName("Working Memory");
		rp.setName("Rule Engine");
        Icon[]	toolicons	= new Icon[]{icons.getIcon("show_state"), icons.getIcon("show_rete")};
        JTabbedPane	tabs	= new JTabbedPane();
        
        boolean selected	= false;
        for(int i=0; i<tools.length; i++)
		{
			tabs.addTab(tools[i].getName(), toolicons[i], tools[i]);

			// Select first active tab.
			if(!selected)
			{
				tabs.setSelectedIndex(i);
				selected	= true;
			}
		}

        return tabs;
	}
}
