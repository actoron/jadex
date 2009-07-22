package com.daimler.client.gui.components.parts;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

import com.daimler.util.ResourceLocator;

public class GuiHelpBrowser extends JDialog
{

	private JScrollPane docPane;

	private JEditorPane docEditorPane;

	public GuiHelpBrowser()
	{
		init();
	}

	private void init()
	{
		setTitle("Help");
		setSize(new Dimension(600, 600));
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		this.docEditorPane = new JEditorPane();

		this.docEditorPane.setEditable(false);
		this.docEditorPane.addHyperlinkListener(new Hyperactive());

		this.docPane = new JScrollPane(this.docEditorPane);
		this.docPane.setAutoscrolls(true);

		getContentPane().add(this.docPane);
	}

	public boolean accept(File pathname)
	{
		if (pathname.isDirectory())
			return true;
		int i = pathname.getName().lastIndexOf('.');
		if (i < 1)
			return false;
		String sExtension = pathname.getName().substring(i + 1);
		if (sExtension.equalsIgnoreCase("HTML")
				|| sExtension.equalsIgnoreCase("HTM"))
			return true;
		return false;
	}

	public void show(String contentOrURI)
	{
		try
		{
			// URI uri = ResourceLocator.getURIForResource(contentOrURI,
			// theModulePath);
			// URI uri = new URI(contentOrURI);
			URI uri = null;
			String sUrlLoader = contentOrURI;
			if (sUrlLoader.startsWith("/"))
				sUrlLoader = sUrlLoader.substring(1);
			URL url = ClassLoader.getSystemResource(sUrlLoader);
			if (url == null)
				uri = ResourceLocator.getURIForResource(contentOrURI, null);
			else
				uri = url.toURI();
			Document doc = docEditorPane.getDocument();
			doc.putProperty(Document.StreamDescriptionProperty, null);
			// System.out.println("Got URI: " + uri + " (for " + theModulePath +
			// ":::" + contentOrURI + ")");
			if (uri == null)
			{
				docEditorPane.setText(contentOrURI);
			} else
			{
				url = uri.toURL();
				docEditorPane.setPage(url);
			}
		} catch (URISyntaxException err)
		{
			docEditorPane.setText(contentOrURI);
		} catch (MalformedURLException err)
		{
			docEditorPane.setText(contentOrURI);
		} catch (IOException err)
		{
			docEditorPane.setText(contentOrURI);
		} catch (IllegalArgumentException err)
		{
			docEditorPane.setText(contentOrURI);
		}
		if (!isVisible())
			setVisible(true);

	}

	private static class Hyperactive implements HyperlinkListener
	{

		public void hyperlinkUpdate(HyperlinkEvent e)
		{
			if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
			{
				JEditorPane pane = (JEditorPane) e.getSource();
				if (e instanceof HTMLFrameHyperlinkEvent)
				{
					HTMLFrameHyperlinkEvent evt = (HTMLFrameHyperlinkEvent) e;
					HTMLDocument doc = (HTMLDocument) pane.getDocument();
					doc.processHTMLFrameHyperlinkEvent(evt);

				} else
				{
					try
					{
						pane.setPage(e.getURL());
					} catch (Throwable t)
					{
						t.printStackTrace();
					}
				}
			}
		}
	}
}
