package jadex.tools.common;

import jadex.commons.SReflect;
import jadex.commons.SUtil;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeListener;
import javax.swing.text.html.HTMLDocument;

/**
 *  The element panel has the task to show the
 *  details of an element. The element has to be
 *  provided in the encodable representation (as map).
 */
public class ElementPanel extends JTabbedPane
{
	//-------- constants --------

	/** The default tab id. */
	public static final String	DEFAULT	= "default";

	//-------- attributes ---------

	/** The stylesheet for styled display. */
	protected String	stylesheet;

	/** The maximum number of tabs to display. */
	protected int	max;

	/** The identifiers for the tabs (id->component). */
	protected Map	tabs;

	/** The html representation converter. */
	protected IRepresentationConverter	htmlconv;
	protected boolean htmlinited;

	/** The default representation converter. */
	protected IRepresentationConverter	defconv;

	//-------- constructors ---------

	/**
	 *  Create a new element panel.
	 *  @param title	The title of the initial tab.
	 *  @param text	The initial text to be displayed (or null).
	 */
	public ElementPanel(String title, String text)
	{
		this(title, text, "element.css", 5);
	}

	/**
	 *  Create a new element panel.
	 *  @param title	The title of the panel.
	 *  @param text	The initial text to be displayed (or null).
	 *  @param stylesheet	File name of stylesheet to use for styled display.
	 *  @param max	The maximum number of tabs to display.
	 */
	public ElementPanel(String title, String text, String stylesheet, int max)
	{
		this.stylesheet	= stylesheet;
		this.max	= max;
		this.tabs	= new HashMap();
		if(title!=null)
			setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), title));

		if(text!=null)
		{
			// Hack ???
			if(text.indexOf("<html>")!=-1 || text.indexOf("<HTML>")!=-1)
			{
				try
				{
					addHTMLContent("Info", null, text, DEFAULT, null);
				}
				catch(Exception e)
				{
					System.out.println(e);
					addTextContent("Info", null, text, DEFAULT);
				}
			}
			else
			{
				addTextContent("Info", null, text, DEFAULT);
			}
		}
	}

	//-------- methods --------

	/**
	 *  Display some plain text content.
	 *  @param title	The title of the tab.
	 *  @param icon	The icon to show in the tab (if any).
	 *  @param text	The text to display.
	 *  @param id	The id of the tab. If a tab with this id already exists,
	 *    it will be replaced.
	 */
	public synchronized void addTextContent(String title, Icon icon, String text, Object id)
	{
		// Create text area.
		JTextArea	textcomp	= new JTextArea(text);
		textcomp.setEditable(false);

		// Create and show tab.
		addTab(title, icon, textcomp, id);
	}

	/**
	 *  Display some html content.
	 *  @param title	The title of the tab.
	 *  @param icon	The icon to show in the tab (if any).
	 *  @param text	The html text to display.
	 *  @param id	The id of the tab. If a tab with this id already exists,
	 *    it will be replaced.
	 */
	public void addHTMLContent(String title, Icon icon, String text, Object id, Map externals)
	{
		// Todo: Generate header and set base tag.
		// Create text pane.
		BrowserPane	textcomp	= new BrowserPane();
		textcomp.setText(text);
		textcomp.setCaretPosition(0);
		textcomp.setExternals(externals);
		setStylesheet(textcomp);

		// Create and show tab.
		addTab(title, icon, textcomp, id);
	}

	/**
	 *  Add an element to display.
	 *  Tries all available converters (e.g. html, plain text).
	 *  @param element	The element to be displayed.
	 */
	public void	addElement(Map element, Icon icon)
	{
		// Extract id, title and icon.
		Object	id	= element.get("id");
		Object	clazz	= element.get("class");
		Object	name	= element.get("name");
		String	title	= name!=null ? name.toString()
			: clazz!=null ? "Unnamed " + clazz.toString() : "Unnamed Element";
		//Icon	icon	= clazz!=null ? GuiProperties.getElementIcon(clazz) : null;

		// Convert and show element.
		if(getHTMLConverter()!=null)
		{
			String html = getHTMLConverter().convert(element);
			try
			{
				this.addHTMLContent(title, icon, html, id, null);
			}
			catch(Exception e)
			{
				System.out.println(e);
				String text = getDefaultConverter().convert(element);
				this.addTextContent(title, icon, text, id);
			}
		}
		else
		{
			String text = getDefaultConverter().convert(element);
			this.addTextContent(title, icon, text, id);
		}
	}

	/**
	 *  Get the id for a component.
	 *  @param comp	The component.
	 *  @return	The id.
	 */
	public Object	getId(Component comp)
	{
		for(Iterator i=tabs.keySet().iterator(); i.hasNext();)
		{
			Object	key	= i.next();
			if(tabs.get(key)==comp)
			{
				return key;
			}
		}
		return null;
	}

	/**
	 *  Get the id for selected card.
	 *  @return	The id.
	 */
	public Object	getId()
	{
		return getId(getSelectedComponent());
	}

	//-------- helper methods --------

	/**
	 *  Set the stylesheet to be used for styled (not plain) content.
	 *  @param textcomp	The text component.
	 */
	public void setStylesheet(JTextPane textcomp)
	{
		// Update style of JTextPane.
		if(stylesheet!=null)
		{
			try
			{
				// Read in CSS file.
				StringBuffer	cssbuf	= new StringBuffer();
				Reader	in	= new InputStreamReader(getClass().getResourceAsStream(stylesheet));
				char[]	buf	= new char[256];
				int cnt;
				while((cnt=in.read(buf))!=-1)
				{
					cssbuf.append(buf, 0, cnt);
				}
				String	css	= cssbuf.toString();

				// Replace colors/fonts with actual values.
				UIDefaults	def	= UIManager.getLookAndFeelDefaults();
				String	title_background	= SUtil.colorToHTML((Color)def.get("InternalFrame.activeTitleBackground"));
				String	title_foreground	= SUtil.colorToHTML((Color)def.get("InternalFrame.activeTitleForeground"));
				String	title_font	= SUtil.fontToHTML((Font)def.get("InternalFrame.titleFont"));
				String	border	= SUtil.colorToHTML((Color)def.get("InternalFrame.borderShadow"));
				String	attribute_background	= SUtil.colorToHTML((Color)def.get("Label.background"));
				String	attribute_foreground	= SUtil.colorToHTML((Color)def.get("Label.foreground"));
				String	attribute_font	= SUtil.fontToHTML((Font)def.get("Label.font"));
				String	text_background	= SUtil.colorToHTML((Color)def.get("TextPane.background"));
				String	text_foreground	= SUtil.colorToHTML((Color)def.get("TextPane.foreground"));
				String	text_font	= SUtil.fontToHTML((Font)def.get("TextPane.font"));
				css	= SUtil.replace(css, "$title_background", title_background);
				css	= SUtil.replace(css, "$title_foreground", title_foreground);
				css	= SUtil.replace(css, "$title_font", title_font);
				css	= SUtil.replace(css, "$border", border);
				css	= SUtil.replace(css, "$attribute_background", attribute_background);
				css	= SUtil.replace(css, "$attribute_foreground", attribute_foreground);
				css	= SUtil.replace(css, "$attribute_font", attribute_font);
				css	= SUtil.replace(css, "$text_background", text_background);
				css	= SUtil.replace(css, "$text_foreground", text_foreground);
				css	= SUtil.replace(css, "$text_font", text_font);
//				System.out.println(css);

				// Apply style sheet.
				HTMLDocument doc = (HTMLDocument)textcomp.getStyledDocument();
				doc.getStyleSheet().loadRules(new StringReader(css.toString()), null);
			}
			catch(Exception e)
			{
				throw new RuntimeException("Could not load stylesheet: "+stylesheet+"\n"+e);
			}
		}
	}

	/**
	 *  Add or replace a tab.
	 *  If the maximum number of tabs is reached, another tab is removed.
	 *  @param title	The title of the tab.
	 *  @param icon	The icon to show in the tab (if any).
	 *  @param comp	The component to display.
	 *  @param id	The id of the tab. If a tab with this id already exists,
	 *    it will be replaced.
	 */
	protected void addTab(String title, Icon icon, JComponent comp, Object id)
	{
		ChangeListener[] cls = getChangeListeners();
		for(int i=0; i<cls.length; i++)
			removeChangeListener(cls[i]);

		int	index	= getTabCount();

		// Remove old tab (if any).
		if(id==null)	id	= DEFAULT;
		if(tabs.get(id)!=null)
		{
			Object	tab	= tabs.get(id);
			for(int i=0; i<getTabCount(); i++)
			{
				if(tab==getComponentAt(i))
				{
					index	= i;
					remove(i);
					break;
				}
			}
		}

		// Remove some tabs, if maximum is exceeded.
		while(max>=0 && getTabCount()>=max)
		{
			// Remove tab and lookup entry.
			tabs.remove(getId(getComponentAt(0)));
			remove(0);
			index	= Math.min(index, getTabCount());
		}

		// Create and show tab.
		JScrollPane	scroll	= new JScrollPane(comp);
		insertTab(title, icon, scroll, null, index);
		setSelectedIndex(index);
		tabs.put(id, scroll);

		for(int i=0; i<cls.length; i++)
			addChangeListener(cls[i]);
	}

	/**
	 *  Get the html converter.
	 */
	public IRepresentationConverter getHTMLConverter()
	{
		if(htmlconv==null && !htmlinited)
		{
			try
			{
				htmlconv = (IRepresentationConverter)SReflect.findClass("jadex.tools.common.ElementHTMLGenerator", null, null).newInstance();
			}
			catch(Throwable e)
			{
				System.out.println("Velocity template engine not installed: using plain text description.");
				//e.printStackTrace();
			}
			htmlinited = true;
		}
		return htmlconv;
	}

	/**
	 *  Get the default converter.
	 */
	public IRepresentationConverter getDefaultConverter()
	{
		if(defconv==null)
			defconv = new ElementStringGenerator();
		return defconv;
	}

	//-------- static part --------

	/**
	 *  Main method for testing.
	 */
	public static void	main(String[] args)
	{
/*		// Print properties from UI defaults
		System.out.println("defaults:");
		Map	table	= UIManager.getLookAndFeelDefaults();
		Object[]	keys	= table.keySet().toArray();
		for(int i=0; i<keys.length; i++)
		{
			if(table.get(keys[i]) instanceof Font)
			{
				//Color	col	= (Color)table.get(keys[i]);
				//if((""+keys[i]).indexOf("order")!=-1)
					System.out.println(""+keys[i]+":\t"+table.get(keys[i]));
			}
		}
*/
		HashMap element = new HashMap();
		HashMap ielement = new HashMap();
		HashMap iielement = new HashMap();

		iielement.put("ii1", "ii1_val");
		iielement.put("isencodeablepresentation", "true");

		ielement.put("inner", iielement);
		ielement.put("inner2", iielement);
		ielement.put("ia2", "ia_val");
		ielement.put("name", "innername_val");
		ielement.put("class", "innerclass_val");
		ielement.put("ia", "ia_val");
		ielement.put("isencodeablepresentation", "true");

		element.put("inner", ielement);
		element.put("inner2", ielement);
		element.put("a2", "a_val");
		element.put("name", "name_val");
		element.put("class", "class_val");
		element.put("a", "a_val");

		JFrame	frame	= new JFrame("Element Panel Test");
		ElementPanel	panel	= new ElementPanel("Element Panel", null);
		frame.getContentPane().add(panel);
		frame.setSize(640,480);
		frame.setVisible(true);

		panel.addElement(element, null);
		frame.addWindowListener(new WindowAdapter()
		{
			public void	windowClosing(WindowEvent we)
			{
				System.exit(0);
			}
		});
	}
}

