package jadex.tools.jcc;

import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.RemoteChangeListenerHandler;
import jadex.bridge.service.annotation.Security;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.commons.IRemoteChangeListener;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;
import jadex.commons.transformation.annotations.Classname;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

/**
 *  A console panel for displaying the console out
 *  and err messages of a local or remote platform.
 */
public class ConsolePanel extends JPanel
{
//	-------- static part --------

	/** The image icons. */
	protected static UIDefaults	icons	= new UIDefaults(new Object[]
	{
		"clear", SGUI.makeIcon(ConsolePanel.class,	"/jadex/tools/common/images/new_console_clear.png"),
		"on", SGUI.makeIcon(ConsolePanel.class,	"/jadex/tools/common/images/new_console_on.png"),
		"off", SGUI.makeIcon(ConsolePanel.class,	"/jadex/tools/common/images/new_console_off.png"),
	});
	
	//-------- attributes --------
	
	/** The platform component. */
	protected IExternalAccess	platformaccess;
	
	/** The jcc component. */
	protected IExternalAccess	jccaccess;
	
	/** The document. */
	protected StyledDocument doc;
	
	/** The new document output stream. */
	protected StyledDocumentOutputStream sdout;
	
	/** The new document err stream. */
	protected StyledDocumentOutputStream sderr;
	
	/** The on/off button. */
	protected JButton onoff;
	
	/** The console text pane. */
	protected JTextPane	console;
	
	/** The console title. */
	protected JLabel	label;
	
	//-------- constructors --------
	
	/**
	 *  Create a new console panel.
	 */
	public ConsolePanel(IExternalAccess platformaccess, IExternalAccess jccaccess)
	{
		this(platformaccess, jccaccess, "Console Output");
	}
	
	/**
	 *  Create a new console panel.
	 */
	public ConsolePanel(IExternalAccess platformaccess, IExternalAccess jccaccess, String title)
	{
		this.platformaccess	= platformaccess;
		this.jccaccess	= jccaccess;
		this.console = new JTextPane();
		this.doc = console.getStyledDocument();
		this.label	= new JLabel(title);
	
		Style def = StyleContext.getDefaultStyleContext().
	    	getStyle(StyleContext.DEFAULT_STYLE);
		Style outstyle = doc.addStyle("out", def);
		Style errorstyle = doc.addStyle("error", def);
		StyleConstants.setForeground(errorstyle, Color.red);
      	  
		this.sdout = new StyledDocumentOutputStream(doc, outstyle);
		this.sderr = new StyledDocumentOutputStream(doc, errorstyle);
			
		JButton clear = new JButton(icons.getIcon("clear"));
		clear.setMargin(new Insets(0,0,0,0));
		clear.setToolTipText("Clear the console output");
		clear.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				clear();
			}
		});
		this.onoff = new JButton(icons.getIcon("off"));
		onoff.setMargin(new Insets(0,0,0,0));
		onoff.setToolTipText("Turn off the console");
		onoff.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				setConsoleEnabled(onoff.getIcon()==icons.getIcon("on"));
			}
		});
			
		final JScrollPane center = new JScrollPane(console);
		
		doc.addDocumentListener(new DocumentListener()
		{
			public void insertUpdate(DocumentEvent e)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						JScrollBar bar = center.getVerticalScrollBar();
						if(bar!=null)
						{
							bar.setValue(bar.getMaximum());
						}
					}
				});
			}
			public void changedUpdate(DocumentEvent e)
			{
			}
			public void removeUpdate(DocumentEvent e)
			{
			}
		});
		
		JPanel north = new JPanel(new GridBagLayout());
		north.add(label, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST,
			GridBagConstraints.NONE, new Insets(2,2,2,2), 0, 0));
		north.add(onoff, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.EAST,
			GridBagConstraints.NONE, new Insets(2,2,2,2), 0, 0));
		north.add(clear, new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.EAST,
			GridBagConstraints.NONE, new Insets(2,2,2,2), 0, 0));
		
		this.setLayout(new BorderLayout());
		this.add("Center", center);
		this.add("North", north);
	}
	
	//-------- methods --------
	
	/**
	 *  Clear the document.
	 */
	public void clear()
	{
		try
		{
			synchronized(doc)
			{
				doc.remove(0, doc.getLength());
			}
		}
		catch(BadLocationException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 *  Enable console.
	 *  @param enable True, for enabling console.
	 */
	public void setConsoleEnabled(boolean enable)
	{
		final String	id	= jccaccess.getComponentIdentifier().getPlatformName()+"#console@"+hashCode();
		if(!enable)
		{
			onoff.setIcon(icons.getIcon("on"));
			onoff.setToolTipText("Turn on the console");
			if(!label.getText().endsWith(" (off)"))
				label.setText(label.getText()+" (off)");
			
			platformaccess.scheduleImmediate(new IComponentStep<Void>()
			{
				@Classname("removeListener")
				public IFuture<Void> execute(IInternalAccess ia)
				{
					ConsoleListener	cl	= new ConsoleListener(id, ia, null);
					SUtil.removeSystemOutListener(cl);
					SUtil.removeSystemErrListener(cl);
					return IFuture.DONE;
				}
			});
		}
		else
		{
			final IRemoteChangeListener	rcl	= new IRemoteChangeListener()
			{
				final static String	OUT_OCCURRED	= "out" + RemoteChangeListenerHandler.EVENT_OCCURRED;
				final static String	ERR_OCCURRED	= "err" + RemoteChangeListenerHandler.EVENT_OCCURRED;
				
				@Security(Security.UNRESTRICTED)
				public IFuture changeOccurred(ChangeEvent event)
				{
					IFuture	ret;
					if(isConsoleEnabled())
					{
						handleEvent(event);
						ret	= IFuture.DONE;
					}
					else
					{
						// Reply with exception to trigger deregistration.
						ret	= new Future(new RuntimeException("console off"));
					}
					return ret;
				}
				
				public void	handleEvent(ChangeEvent event)
				{
					if(RemoteChangeListenerHandler.EVENT_BULK.equals(event.getType()))
					{
						for(Iterator it=((Collection)event.getValue()).iterator(); it.hasNext(); )
						{
							handleEvent((ChangeEvent)it.next());
						}
					}
					else if(OUT_OCCURRED.equals(event.getType()))
					{
						sdout.println(event.getValue());
					}
					else if(ERR_OCCURRED.equals(event.getType()))
					{
						sderr.println(event.getValue());
					}
				}
			};
			
			platformaccess.scheduleImmediate(new IComponentStep<Void>()
			{
				@Classname("installListener")
				public IFuture<Void> execute(IInternalAccess ia)
				{
					ConsoleListener	cl	= new ConsoleListener(id, ia, rcl);
					SUtil.addSystemOutListener(cl);
					SUtil.addSystemErrListener(cl);
					return IFuture.DONE;
				}
			});
			
			onoff.setIcon(icons.getIcon("off"));
			onoff.setToolTipText("Turn off the console");
			if(label.getText().endsWith(" (off)"))
				label.setText(label.getText().substring(0, label.getText().length()-6));
		}
	}
	
	/**
	 *  Test if console is enabled.
	 *  @return True, for enabled console.
	 */
	public boolean isConsoleEnabled()
	{
		return onoff.getIcon()==icons.getIcon("off");
	}
	
	/**
	 *  Close the console.
	 */
	public void close()
	{
		if(isConsoleEnabled())
			setConsoleEnabled(false);
		sdout.close();
		sderr.close();
	}
	
	//-------- helper classes --------
	
	public static class	ConsoleListener	extends RemoteChangeListenerHandler	implements IChangeListener
	{
		//-------- constants --------
		
		/** The limit of characters sent in one event. */
		public static final int LIMIT	= 1;//4096;
		
		//-------- constructors --------
		
		/**
		 *  Create a console listener.
		 */
		public ConsoleListener(String id, IInternalAccess instance, IRemoteChangeListener rcl)
		{
			super(id, instance, rcl);
		}
		
		//-------- IChangeListener interface --------
		
		/**
		 *  Called when a change occurs.
		 *  @param event The event.
		 */
		public void changeOccurred(final ChangeEvent event)
		{
			instance.getExternalAccess().scheduleImmediate(new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					// Merge new output with last output, if not yet sent.
					boolean	merged	= false;
					ArrayList	list	= (ArrayList)occurred.get(event.getType()); 
					if(list!=null && !list.isEmpty())
					{
						String	val	= (String)list.get(list.size()-1);
						if(val.length()<LIMIT)
						{
							val	+= "\n"+event.getValue();
							list.set(list.size()-1, val);
							merged	= true;
						}
					}
					
					if(!merged)
						occurrenceAppeared(event.getType(), event.getValue());
					
					return IFuture.DONE;
				}
			});
		}
		
		//-------- RemoteChangeListenerHandler methods --------
		
		/**
		 *  Remove local listeners.
		 */
		protected void dispose()
		{
			super.dispose();
			
			SUtil.removeSystemOutListener(this);
			SUtil.removeSystemErrListener(this);
		}
	}
}
