package jadex.commons.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.WindowConstants;

import jadex.commons.ICommand;
import jadex.commons.collection.IdentityHashSet;

/**
 *  
 *
 */
public class JWizard extends JPanel
{
	/** ID for finish events. */
	public static final int FINISH_ID = 0;
	
	/** ID for cancel events. */
	public static final int CANCEL_ID = 1;
	
	/** Start frame of the wizard. */
	protected WizardNode start;
	
	/** Current frame of the wizard. */
	protected WizardNode current;
	
	/** The main panel. */;
	protected JPanel mainpanel;
	
	/** The current button panel. */
	protected JPanel buttonpanel;
	
	/** The button for the next/finish action, can be disabled. */
	protected JButton nextbutton;
	
	/** The busy ring. */
	protected JBusyRing busyring;
	
	/** Listeners called when wizard terminates. */
	protected Set<ActionListener> terminationlisteners;
	
	/**
	 *  Creates the wizard.
	 */
	public JWizard()
	{
		terminationlisteners = new LinkedHashSet<ActionListener>();
		mainpanel = new JPanel();
		mainpanel.setLayout(new GridLayout(1, 1));
		
		busyring = new JBusyRing();
		
		setLayout(new BorderLayout());
		add(mainpanel, BorderLayout.CENTER);
	}
	
	/**
	 *  Sets the enabled state of all buttons (next, back etc.).
	 *  
	 *  @param enabled True, if enabled
	 */
	public void setAllButtonsEnabled(boolean enabled)
	{
		Component[] comps = buttonpanel.getComponents();
		for (Component comp : comps)
		{
			if (comp instanceof AbstractButton)
				comp.setEnabled(enabled);
		}
	}
	
	/**
	 *  Sets the enabled state of the next/finish button.
	 *  
	 *  @param enabled True, if enabled
	 */
	public void setEnableNext(boolean enabled)
	{
		nextbutton.setEnabled(enabled);
	}
	
	/** 
	 * Finish the wizard.
	 */
	public void finish()
	{
		for (ActionListener lis : terminationlisteners)
		{
			ActionEvent e = new ActionEvent(this, FINISH_ID, "Finished");
			lis.actionPerformed(e);
		}
	}
	
	/** 
	 * Cancel the wizard.
	 */
	public void cancel()
	{
		for (ActionListener lis : terminationlisteners)
		{
			ActionEvent e = new ActionEvent(this, CANCEL_ID, "Canceled");
			lis.actionPerformed(e);
		}
	}
	
	/**
	 *  Adds a termination listener.
	 *  
	 *  @param lis The listener.
	 */
	public void addTerminationListener(ActionListener lis)
	{
		terminationlisteners.add(lis);
	}
	
	/**
	 *  Removes a termination listener.
	 *  
	 *  @param lis The listener.
	 */
	public void removeTerminationListener(ActionListener lis)
	{
		terminationlisteners.remove(lis);
	}
	
	/**
	 *  Shows the next panel.
	 */
	protected void next()
	{
		if (current != null)
		{
			mainpanel.remove(current);
			WizardNode next = current.getSelectedChild();
			next.getParentNodeStack().push(current);
			current = next;
		}
		else
		{
			current = start;
		}
		
		mainpanel.add(current, BorderLayout.CENTER);
		configureButtons();
		current.onShow();
	}
	
	/**
	 *  Shows the next panel.
	 */
	protected void back()
	{
		if (current != null)
		{
			mainpanel.remove(current);
			current.getSelectedChild();
			current = current.getParentNodeStack().pop();
		}
		else
		{
			current = start;
		}
		mainpanel.add(current, BorderLayout.CENTER);
		current.onShow();
		configureButtons();
	}
	
	/**
	 *  Resets the wizard.
	 */
	protected void reset()
	{
		if (current != null)
		{
			current.getSelectedChild();
			mainpanel.remove(current);
		}
		current = null;
		
		performOnAllNodes(new ICommand<WizardNode>()
		{
			public void execute(WizardNode arg)
			{
				arg.getParentNodeStack().clear();
			}
		});
		
//		configureButtons();
	}
	
	/**
	 *  Recursively runs command all nodes.
	 *  
	 *  @param command The command.
	 */
	protected void performOnAllNodes(ICommand<WizardNode> command)
	{
		Set<WizardNode> known = new IdentityHashSet<WizardNode>();
		performOnAllNodesRecur(command, start, known);
	}
	
	/**
	 *  Recursively runs command all sub-nodes.
	 *  
	 *  @param command The command.
	 *  @param node The start node.
	 *  @param known Known nodes to prune loops.
	 */
	protected void performOnAllNodesRecur(ICommand<WizardNode> command, WizardNode node, Set<WizardNode> known)
	{
		if (!known.contains(node))
		{
			known.add(node);
			command.execute(node);
			WizardNode[] children = node.getChildren().toArray(new WizardNode[node.getChildren().size()]);
			for (WizardNode child : children)
			{
				performOnAllNodesRecur(command, child, known);
			}
		}
	}
	
	/**
	 *  Configures the buttons.
	 */
	protected void configureButtons()
	{
		if (buttonpanel != null)
		{
			buttonpanel.remove(busyring);
			remove(buttonpanel);
		}
		
		buttonpanel = new JPanel();
		BoxLayout layout = new BoxLayout(buttonpanel, BoxLayout.LINE_AXIS);
		buttonpanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK));
		buttonpanel.setLayout(layout);
		
		JButton backbutton = new JButton(new AbstractAction("< Back")
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					current.onBack();
					back();
				}
				catch (Exception e1)
				{
				}
			}
		});
		
		backbutton.setEnabled(!current.getParentNodeStack().isEmpty());
		
		AbstractAction nextaction = new AbstractAction("Next >")
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					current.onNext();
					next();
				}
				catch (Exception e1)
				{
				}
			}
		};
		if (current.getChildren().size() == 0)
		{
			nextaction = new AbstractAction("Finish")
			{
				public void actionPerformed(ActionEvent e)
				{
					try
					{
						current.onFinish();
						finish();
					}
					catch (Exception e1)
					{
					}
				}
			};
		}
		nextbutton = new JButton(nextaction);
		
		JButton cancelbutton = new JButton(new AbstractAction("Cancel")
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					current.onCancel();
					cancel();
				}
				catch (Exception e1)
				{
				}
			}
		});
		
		buttonpanel.add(busyring);
		buttonpanel.add(Box.createHorizontalGlue());
		buttonpanel.add(backbutton);
		buttonpanel.add(nextbutton);
		buttonpanel.add(Box.createHorizontalStrut(10));
		buttonpanel.add(cancelbutton);
		
		add(buttonpanel, BorderLayout.SOUTH);
		invalidate();
		validate();
		doLayout();
		repaint();
	}
	
	/**
	 *  Creates a frame for the wizard that closes on finish.
	 *  
	 *  @param title Title of the frame.
	 *  @param wizard The wizard.
	 *  @return The frame.
	 */
	public static final JFrame createFrame(String title, JWizard wizard)
	{
		final JFrame frame = new JFrame(title);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.getRootPane().setLayout(new BorderLayout());
		frame.getRootPane().add(wizard, BorderLayout.CENTER);
		frame.setSize(800, 600);
		frame.setMinimumSize(frame.getRootPane().getPreferredSize());
		frame.setLocation(SGUI.calculateMiddlePosition(frame));
		wizard.addTerminationListener(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				frame.dispose();
			}
		});
		return frame;
	}
	
	/**
	 * 
	 *
	 */
	protected static class WizardNode extends JPanel
	{
		/** The parent node stack. */
		protected LinkedList<WizardNode> parents = new LinkedList<WizardNode>();
		
		/** Wizard child nodes. */
		protected List<WizardNode> children = new ArrayList<WizardNode>();
		
		/** The next item, 0 for default. */
		protected int nextitem;
		
		/**
		 *  Creates the node.
		 */
		public WizardNode()
		{
		}
		
		/**
		 *  Called when the component is shown.
		 */
		public void onShow()
		{
		}
		
		/**
		 *  Helper for adding a single centered component.
		 *  
		 *  @param centeredpanel The panel.
		 */
		public void addCentered(JComponent comp)
		{
			setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
			Box hbox = Box.createHorizontalBox(); 
			hbox.add(Box.createHorizontalGlue()); 
			hbox.add(comp); 
			hbox.add(Box.createHorizontalGlue());
			Box vbox = Box.createVerticalBox();
			vbox.add(Box.createVerticalGlue()); 
			vbox.add(hbox);
			vbox.add(Box.createVerticalGlue());
			add(vbox);
		}
		
		/**
		 *  Adds a child.
		 *  
		 *  @param next The child.
		 */
		public void addChild(WizardNode child)
		{
			children.add(child);
		}
		
		/**
		 *  Removes a child.
		 *  
		 *  @param next The child.
		 */
		public void removeChild(WizardNode child)
		{
			children.remove(child);
		}

		/**
		 *  Gets the children.
		 *
		 *  @return The children.
		 */
		public List<WizardNode> getChildren()
		{
			return children;
		}

		/**
		 *  Sets the children.
		 *
		 *  @param children The children.
		 */
		public void setChildren(List<WizardNode> children)
		{
			this.children = children;
		}
		
		/**
		 *  Gets the parent.
		 *
		 *  @return The parent.
		 */
		public LinkedList<WizardNode> getParentNodeStack()
		{
			return parents;
		}
		
		/**
		 *  Gets the selected child and resets choice.
		 *  
		 *  @return Selected child.
		 */
		public WizardNode getSelectedChild()
		{
			int sel = nextitem;
			nextitem = 0;
			return sel < children.size() ? children.get(sel) : null;
		}
		
		/**
		 *  Called when "Finish" is clicked.
		 */
		protected void onFinish()
		{
			onNext();
		}
		
		/**
		 *  Called when "Next" is clicked.
		 */
		protected void onNext()
		{
		}
		
		/**
		 *  Called when "Back" is clicked.
		 */
		protected void onBack()
		{
		}
		
		/**
		 *  Called when "Cancel" is clicked.
		 */
		protected void onCancel()
		{
		}
	}
	
	/**
	 *  A node for implementing a multiple choice fork.
	 *
	 */
	public static class ChoiceNode extends WizardNode
	{
		protected ButtonGroup bgroup;
		
		/**
		 *  Creates the node.
		 */
		public ChoiceNode(String choicetitle, String[] choices)
		{
			this(choicetitle, choices, 0);
		}
		
		/**
		 *  Creates the node.
		 */
		public ChoiceNode(String choicetitle, String[] choices, final int defaultchoice)
		{
			bgroup = new ButtonGroup();
			
			JComponent[] buttons = new JComponent[choices.length];
			for (int i = 0; i < choices.length; ++i)
			{
				JRadioButton choicebutton = new JRadioButton(choices[i]);
				if (i == defaultchoice)
					choicebutton.setSelected(true);
				bgroup.add(choicebutton);
				buttons[i] = choicebutton;
			}
			
			JPanel inner = new JPanel();
			if (choicetitle != null)
				inner.setBorder(BorderFactory.createTitledBorder(choicetitle));
			else
				inner.setBorder(BorderFactory.createEtchedBorder());
			
			SGUI.createVerticalGroupLayout(inner, buttons, true);
			
			setLayout(new BorderLayout());
			add(inner, BorderLayout.CENTER);
//			addCentered(inner);
		}
		
		/**
		 *  Gets the selected child and resets choice.
		 *  
		 *  @return Selected child.
		 */
		public WizardNode getSelectedChild()
		{
			int sel = SGUI.getSelectedButton(bgroup);
			return children.get(sel);
		}
	}
}
