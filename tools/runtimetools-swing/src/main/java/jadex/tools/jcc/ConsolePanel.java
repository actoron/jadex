package jadex.tools.jcc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
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

import jadex.base.SRemoteGui;
import jadex.bridge.IExternalAccess;
import jadex.bridge.RemoteChangeListenerHandler;
import jadex.commons.ChangeEvent;
import jadex.commons.IRemoteChangeListener;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;

/**
 *  A console panel for displaying the console out
 *  and err messages of a local or remote platform.
 */
public class ConsolePanel extends JPanel
{
//	-------- static part --------

	/** The image icons. */
	protected static final UIDefaults	icons	= new UIDefaults(new Object[]
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
	
	/** The console in. */
	protected JTextField in;
	
	/** The console title. */
	protected JLabel	label;
	
	//-------- constructors --------
	
	/**
	 *  Create a new console panel.
	 */
	public ConsolePanel(IExternalAccess platformaccess, IExternalAccess jccaccess)
	{
		this(platformaccess, jccaccess, "Console In- and Output");
	}
	
	/**
	 *  Create a new console panel.
	 */
	public ConsolePanel(final IExternalAccess platformaccess, IExternalAccess jccaccess, String title)
	{
		this.platformaccess	= platformaccess;
		this.jccaccess	= jccaccess;
		this.console = new JTextPane();
		this.doc = console.getStyledDocument();
		this.label	= new JLabel(title);
		this.in = new JTextField();
	
		in.setToolTipText("Use this text field to enter data in System.in");
		
		Style def = StyleContext.getDefaultStyleContext().
	    	getStyle(StyleContext.DEFAULT_STYLE);
		Style outstyle = doc.addStyle("out", def);
		Style errorstyle = doc.addStyle("error", def);
		StyleConstants.setForeground(errorstyle, Color.red);
      	  
		this.sdout = new StyledDocumentOutputStream(doc, outstyle);
		this.sderr = new StyledDocumentOutputStream(doc, errorstyle);
			
		final List<String> history = new ArrayList<String>();
		final int[] hpos = new int[1];
		in.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				final String txt = in.getText()+SUtil.LF;
				history.add(0, txt);
				if(history.size()>100)
					history.remove(history.size()-1);
				hpos[0] = 0;
				in.setText("");
				SRemoteGui.redirectInput(platformaccess, txt);
			}
		});
		in.addKeyListener(new KeyListener()
		{
			public void keyTyped(KeyEvent e)
			{
			}
			
			public void keyReleased(KeyEvent e)
			{
			}
			
			public void keyPressed(KeyEvent e)
			{
				if(KeyEvent.VK_UP==e.getKeyCode())
				{
					int hs =history.size();
					if(hs>hpos[0])
					{
						in.setText(history.get(hpos[0]));
						if(hs>hpos[0]+1)
							hpos[0]++;
					}
				}
				else if(KeyEvent.VK_DOWN==e.getKeyCode())
				{
					if(hpos[0]-1>-1)
					{
						hpos[0]--;
						in.setText(history.get(hpos[0]));
					}
				}
			}
		});
		
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
		this.add(center, BorderLayout.CENTER);
		this.add(north, BorderLayout.NORTH);
		this.add(in, BorderLayout.SOUTH);
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
		final String id = jccaccess.getIdentifier().getPlatformName()+"#console@"+hashCode();
		if(!enable)
		{
//			in.setEnabled(false);
			in.setEditable(false);
			onoff.setIcon(icons.getIcon("on"));
			onoff.setToolTipText("Turn on the console");
			if(!label.getText().endsWith(" (off)"))
				label.setText(label.getText()+" (off)");

			SRemoteGui.removeConsoleListener(platformaccess, id);
		}
		else
		{
//			in.setEnabled(true);
			in.setEditable(true);
			final IRemoteChangeListener	rcl	= new IRemoteChangeListener()
			{
				final static String	OUT_OCCURRED	= "out" + RemoteChangeListenerHandler.EVENT_OCCURRED;
				final static String	ERR_OCCURRED	= "err" + RemoteChangeListenerHandler.EVENT_OCCURRED;
				
//				@Security(Security.UNRESTRICTED)
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
			
			SRemoteGui.addConsoleListener(platformaccess, id, rcl);
			
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
}




