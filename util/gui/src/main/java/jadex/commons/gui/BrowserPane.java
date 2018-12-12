package jadex.commons.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.ImageView;

import jadex.commons.SUtil;


/**
 *  A text pane that is able to follow hyperlinks when clicked.
 *  Also supports an "open in external Browser" option.
 */
public class BrowserPane extends JTextPane
{
	//-------- attributes --------

	/** The current URL under the mouse cursor. */
	protected URL	url;

	/** The current URL under the mouse cursor when local reference (i.e. #ref). */
	protected String	reference;

//	/** The current URL (as frame event, if available). */
//	protected HTMLFrameHyperlinkEvent	frameevent;

	/** Indicates that the last mouse click included a popup trigger. */
	protected boolean	popup;

	/** The url history. */
//	protected List	history;

	/** The external references. */
	protected Map<String, String>	externals;

	/** Open links per click in external browser. */
	protected boolean inbrowser;

	//-------- constructor --------

	/**
	 *  Create a new BrowserPane.
	 */
	public BrowserPane()
	{
//		this.history	= new ArrayList();

		setContentType("text/html");
		setEditable(false);
		setEditorKit(new ClasspathHTMLEditorKit());
		
		// Listen for hyperlink events, to remember current url.
		// todo: Problem: does only work when setEditable(false) was called!!!
		this.addHyperlinkListener(new HyperlinkListener()
		{
			public void hyperlinkUpdate(HyperlinkEvent e)
			{
				if(e.getEventType()==HyperlinkEvent.EventType.ENTERED)
				{
					BrowserPane.this.url	= e.getURL();
					if(url==null && e.getDescription()!=null)
					{
						if(e.getDescription().startsWith("#"))
						{
							reference	= e.getDescription().substring(1);
						}
						else
						{
							// Try to create relative file url.
							File	dir	= new File(".");
							try
							{
								url	= new URL("file:///"+dir.getAbsolutePath()+"/"+e.getDescription());
							}
							catch(MalformedURLException ex)
							{
								System.out.println("url: "+"file:///"+dir.getAbsolutePath()+"/"+e.getDescription()+", "+ex);
							}
						}
					}
//					else
//					{
//						System.out.println(url);
//						System.out.println("source="+e.getSourceElement());
//						for(Enumeration enum=e.getSourceElement().getAttributes().getAttributeNames(); enum.hasMoreElements(); )
//						{
//							Object	attr	= enum.nextElement();
//							System.out.println(attr+" = "+e.getSourceElement().getAttributes().getAttribute(attr));
//						}
//					}

//					if(e instanceof HTMLFrameHyperlinkEvent)
//					{
//						BrowserPane.this.frameevent	= (HTMLFrameHyperlinkEvent)e;
//						HTMLDocument	doc = (HTMLDocument)BrowserPane.this.getDocument();
//						doc.processHTMLFrameHyperlinkEvent(frameevent);
//					}
				}
				else if(e.getEventType()==HyperlinkEvent.EventType.EXITED)
				{
					BrowserPane.this.url	= null;
					BrowserPane.this.reference	= null;
//					BrowserPane.this.frameevent	= null;
				}
			}
		});

		// Listen for mouseclicks and open last url.
		this.addMouseListener(new MouseAdapter()
		{
			// When popup trigger, open url in external browser.
			public void mousePressed(MouseEvent e)
			{
				if(popup=e.isPopupTrigger())
				{
					doPopup(e.getPoint());
				}
			}

			// When popup trigger, open url in external browser.
			public void mouseReleased(MouseEvent e)
			{
				if(popup=e.isPopupTrigger())
				{
					doPopup(e.getPoint());
				}
			}

			// When not popup trigger, open url in same window.
			public void mouseClicked(MouseEvent e)
			{
				//System.out.println("ha");
				if(!popup && url!=null)
				{
					//System.out.println("opening: "+url);
					if(inbrowser)
						openExternal(url);
					else
						setPage(url);
					url	= null;
				}
				if(!popup && reference!=null)
				{
					//System.out.println("referencing: "+reference);
					if(externals!=null && externals.containsKey(reference))
					{
						setText(externals.get(reference));
					}
					else
					{
						scrollToReference(reference);
					}
					reference	= null;
				}
//				else if(!popup && frameevent!=null)
//				{
//					HTMLDocument	doc = (HTMLDocument)BrowserPane.this.getDocument();
//					doc.processHTMLFrameHyperlinkEvent(frameevent);
//				}
			}
		});
	}

	//-------- methods --------

	/* *
	 *  Show a document in this pane.
	 * /
	// Hack!!! Do not override, internally used to often
	public void setDocument(Document doc)
	{
System.out.println("setDocument");
		super.setDocument(doc);
		if(history!=null)	// Hack!!! setDocument called from JTextPane constructor.
			history.add(doc);
	}*/
	
	/**
	 *  Show a document in this pane.
	 */
	public void setStyledDocument(StyledDocument doc)
	{
		super.setStyledDocument(doc);
//		history.add(doc);
//		System.out.println("SD: "+doc);
		if(doc instanceof HTMLDocument)
		{
			((HTMLDocument)doc).setBase(null);
		}
	}
	
	/**
	 *  Open an url in this pane.
	 */
	public void	setPage(URL url)
	{
		try
		{
			// Hack??? Allow relative file urls to be loaded
			setDocument(getEditorKit().createDefaultDocument());

			super.setPage(url);
//			history.add(url);
		}
		catch(IOException ex)
		{
			BrowserPane.this.setText("Could not open page: "+ex);
		}
	}

	/**
	 *  Show some text in this pane.
	 */
	public void setText(String text)
	{
		setDocument(getEditorKit().createDefaultDocument());
		super.setText(text);
//		history.add(text);
		if(getDocument() instanceof HTMLDocument)
		{
			((HTMLDocument)getDocument()).setBase(null);
			//System.out.println("Base is now: "+((HTMLDocument)getDocument()).getBase());
		}
		
		Font font = new Font("Arial", Font.ITALIC, 10);
		setJTextPaneFont(this, font, Color.GREEN);
//		setFont(font);
	}

	/**
	 *  Set the externals of this BrowserPane.
	 *  @param externals The externals to set.
	 */
	public void setExternals(Map<String, String> externals)
	{
		this.externals = externals;
	}

	/**
	 *  Set the default open mode for a link click.
	 *  @param inbrowser True, for opening clicked links in external browser.
	 */
	public void setDefaultOpenMode(boolean inbrowser)
	{
		this.inbrowser = inbrowser;
	}

	//-------- helper methods --------

	/**
	 *  React on popup trigger.
	 */
	protected void	doPopup(Point p)
	{
		JPopupMenu	menu	= new JPopupMenu();

//		// Back action
//		if(history.size()>1)
//		{
//			menu.add(new AbstractAction("Back")
//			{
//				public void actionPerformed(ActionEvent e)
//				{
//					// Remove current page.
//					history.remove(history.size()-1);
//
//					// Remove previous page (will be re-added to history).
//					Object	prev	= history.remove(history.size()-1);
//					if(prev instanceof String)
//						setText((String)prev);
//					else if(prev instanceof StyledDocument)
//						setStyledDocument((StyledDocument)prev);
//					else if(prev instanceof Document)
//						setDocument((Document)prev);
//					else if(prev instanceof URL)
//						setPage((URL)prev);
//				}
//			});
//		}

		// Open... actions.
		if(url!=null)
		{
			final URL url	= this.url;
			menu.add(new AbstractAction("Open")
			{
				public void actionPerformed(ActionEvent e)
				{
					setPage(url);
				}
			});
			menu.add(new AbstractAction("Open link in external browser")
			{
				public void actionPerformed(ActionEvent e)
				{
					openExternal(url);
				}
			});
		}

		// Open menu, when some actions are available.
		if(menu.getComponentCount()>0)
			menu.show(this, p.x, p.y);
	}

	/**
	 *  Open an url in external browser.
	 */
	protected void	openExternal(URL url)
	{
		try
		{
			BrowserLauncher2.openURL(url.toString());
		}
		catch(IOException e)
		{
			BrowserPane.this.setText("Could not start browser: "+e);
		}
	}
	
	/**
     * Utility method for setting the font and color of a JTextPane. The
     * result is roughly equivalent to calling setFont(...) and
     * setForeground(...) on an AWT TextArea.
     */
    public static void setJTextPaneFont(JTextPane jtp, Font font, Color c) 
    {
        // Start with the current input attributes for the JTextPane. This
        // should ensure that we do not wipe out any existing attributes
        // (such as alignment or other paragraph attributes) currently
        // set on the text area.
        MutableAttributeSet attrs = jtp.getInputAttributes();

        // Set the font family, size, and style, based on properties of
        // the Font object. Note that JTextPane supports a number of
        // character attributes beyond those supported by the Font class.
        // For example, underline, strike-through, super- and sub-script.
        StyleConstants.setFontFamily(attrs, font.getFamily());
//        StyleConstants.setFontSize(attrs, font.getSize());
//        StyleConstants.setItalic(attrs, (font.getStyle() & Font.ITALIC) != 0);
//        StyleConstants.setBold(attrs, (font.getStyle() & Font.BOLD) != 0);

        // Set the font color
//        StyleConstants.setForeground(attrs, c);

        // Retrieve the pane's document object
        StyledDocument doc = jtp.getStyledDocument();

        // Replace the style for the entire document. We exceed the length
        // of the document by 1 so that text entered at the end of the
        // document uses the attributes.
        doc.setCharacterAttributes(0, doc.getLength() + 1, attrs, false);
    }
	
	//-------- helper classes --------
	
	/**
	 *  An HTML editor kit for supporting images loaded from classpath.
	 */
	public static class	ClasspathHTMLEditorKit	extends HTMLEditorKit
	{
		/** The view factory. */
		protected ViewFactory	thefactory	= new HTMLFactory()
		{
			public View create(Element elem)
			{
				View	ret;
				Object	attr	= elem.getAttributes().getAttribute(StyleConstants.NameAttribute);
				if(attr==HTML.Tag.IMG)
				{
					ret	= new ImageView(elem)
					{
						public URL getImageURL()
						{
							URL	ret	= super.getImageURL();
							if(ret==null)
							{
								String	src	= (String)getElement().getAttributes().getAttribute(HTML.Attribute.SRC);
								if(src!=null)
								{
									ret	= SUtil.class.getClassLoader().getResource(src.startsWith("/") ? src.substring(1) : src);
								}
							}
							return ret;
						}
					};
				}
				else
				{
					ret	= super.create(elem);
				}
				return ret;
			}
		};
		
		/**
		 *  Return the vioew factory.
		 */
		public ViewFactory getViewFactory()
		{
			return thefactory;
		}
	}
}
