package jadex.tools.jcc;

import jadex.commons.MultiStream;
import jadex.commons.gui.SGUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
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
 *  and err messages.
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
	
	/** The document. */
	protected StyledDocument doc;
	
	/** The old output stream. */
	protected PrintStream out;
	
	/** The old err stream. */
	protected PrintStream err;
	
	/** The new document output stream. */
	protected StyledDocumentOutputStream sdout;
	
	/** The new document err stream. */
	protected StyledDocumentOutputStream sderr;
	
	/** The new output stream. */
	//protected PrintStream myout;
	protected MultiStream multiout;
	
	/** The new err stream. */
	//protected PrintStream myerr;
	protected MultiStream multierr;
	
	/** The on/off button. */
	protected JButton onoff;
	
	//-------- constructors --------
	
	/**
	 *  Create a new console panel.
	 */
	public ConsolePanel()
	{
		this("Console Output");
	}
	
	/**
	 *  Create a new console panel.
	 */
	public ConsolePanel(String title)
	{
		this.out = System.out;
		this.err = System.err;
		
		JTextPane tp = new JTextPane();
		this.doc = tp.getStyledDocument();
	
		Style def = StyleContext.getDefaultStyleContext().
	    	getStyle(StyleContext.DEFAULT_STYLE);
		Style outstyle = doc.addStyle("out", def);
		Style errorstyle = doc.addStyle("error", def);
		StyleConstants.setForeground(errorstyle, Color.red);
      	  
		this.sdout = new StyledDocumentOutputStream(doc, outstyle);
		this.sderr = new StyledDocumentOutputStream(doc, errorstyle);
			
		multiout = new MultiStream(new PrintStream[]{out, sdout});
		PrintStream myout = new PrintStream(multiout);
		
		multierr = new MultiStream(new PrintStream[]{err, sderr});
		PrintStream myerr = new PrintStream(multierr);
	
		System.setOut(myout);
		System.setErr(myerr);
		
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
			
		final JScrollPane center = new JScrollPane(tp);
		
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
		north.add(new JLabel(title), new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST,
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
	 *  @param True, for enabling console.
	 */
	public void setConsoleEnabled(boolean enable)
	{
		if(!enable)
		{
			//System.out.println("off1");
			//System.setOut(out);
			//System.setErr(err);
			multiout.setEnabled(sdout, false);
			multierr.setEnabled(sderr, false);
			onoff.setIcon(icons.getIcon("on"));
			onoff.setToolTipText("Turn on the console");
			//System.out.println("off2");
		}
		else
		{
			//System.out.println("on1");
			//System.setOut(myout);
			//System.setErr(myerr);
			multiout.setEnabled(sdout, true);
			multierr.setEnabled(sderr, true);
			onoff.setIcon(icons.getIcon("off"));
			onoff.setToolTipText("Turn off the console");
			//System.out.println("on2");
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
		System.setOut(this.out);
		System.setErr(this.err);
		sdout.close();
		sderr.close();
	}
	
	/**
	 *
	 */
	public static void main(String[] args)
	{
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info("aaa");
		
		JFrame f = new JFrame();
		ConsolePanel cp = new ConsolePanel();
		f.getContentPane().add(cp);
		f.pack();
		f.setVisible(true);
		
		for(int i=0; i<1000000; i++)
		{
			System.out.println(i);
			System.err.println(i);
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(""+i);
			try{Thread.sleep(1000);}
			catch(InterruptedException e){e.printStackTrace();}
		}
	}
}
