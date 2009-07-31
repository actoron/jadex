package com.daimler.client.gui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.daimler.client.gui.components.parts.GuiCategoryPanel;
import com.daimler.client.gui.event.FetchDataTaskSelectAction;

public class GuiDataScrollPanel extends JScrollPane
{
	private static final String CATEGORY_NAME_PLACEHOLDER = "Category Name Placeholder";
	
	private ArrayList theCategories;
	
	private Color theLightBlue = new Color(0.6f, 0.8f, 1.0f);
	
	private Color theDarkGray = new Color(0.75f, 0.75f, 0.75f);
	
	private JPanel pGround = new JPanel();
	
	private boolean enabled = true;
	
	private FetchDataTaskSelectAction theFetchDataTaskSelection = null;
	
	public GuiDataScrollPanel(Map initVals, Collection parameters, FetchDataTaskSelectAction fdtsa)
	{
		setViewportBorder(null);
		setBorder(null);
		theFetchDataTaskSelection = fdtsa;
		theCategories = new ArrayList();
		setBackground(Color.WHITE);
		
		pGround.setBackground(Color.WHITE);
		String[] sCategories = { CATEGORY_NAME_PLACEHOLDER };
		int iRows = sCategories.length;
		BoxLayout theLayout = new BoxLayout(pGround, BoxLayout.Y_AXIS);
		pGround.setLayout(theLayout);
		pGround.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		//List<GetInputTaskProperty> category;
		GuiCategoryPanel catPan;
		//first add the general category
		int iGeneralCatIndex = -1;
		boolean useBlue = true;
		//if only one category and it is the general category than add it without a title
		//if (iRows == 1 && sCategories[0].equalsIgnoreCase(GetInputTaskProperty.GENERAL_CATEGORY))
		//{
		catPan = new GuiCategoryPanel(initVals, parameters, null, theLightBlue);
		pGround.add(catPan);
		theCategories.add(catPan);
		/*}
		else
		{
			for (int i = 0; i < iRows; i++)
			{
				if (sCategories[i].equalsIgnoreCase(GetInputTaskProperty.GENERAL_CATEGORY))
				{
					iGeneralCatIndex = i;
					category = GuiEnvConnUtils.getTaskPropertiesForCategory(dataToFetch, sCategories[i]);
					catPan = new GuiCategoryPanel(category, GetInputTaskProperty.GENERAL_CATEGORY, theLightBlue);
					useBlue = false;
					pGround.add(catPan);
					theCategories.add(catPan);
				}
			}
			
			for (int i = 0; i < iRows; i++)
			{
				if (i != iGeneralCatIndex)
				{
					Color currentBgColor;
					if (useBlue)
					{
						currentBgColor = theLightBlue;
					}
					else
					{
						currentBgColor = theDarkGray;
					}
					useBlue = !useBlue;
					category = GuiEnvConnUtils.getTaskPropertiesForCategory(dataToFetch, sCategories[i]);
					catPan = new GuiCategoryPanel(category, sCategories[i], currentBgColor);
					pGround.add(catPan);
					theCategories.add(catPan);
				}
			}
		}*/
		pGround.add(new Box.Filler(new Dimension(0, 0),new Dimension(0,0), new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE)));
		getViewport().setView(pGround);
	}
	
	/**
	 * check whether all inputfields marked as required have apropriate values filled in
	 * @return true if all required fields are filled
	 */
	public boolean isFilled()
	{
		boolean bFilled = true;
		for (int i = 0; i < theCategories.size(); i++)
		{
			bFilled = bFilled & ((GuiCategoryPanel) theCategories.get(i)).isCategoryFilled();
		}
		return bFilled;
	}
	
	public void setExplanantionText(String textOrURL)
	{
		//TODO: Fixme
		/*URL url = null;
		try
		{
			String sRelPath = textOrURL;
			if (sRelPath.startsWith("/"))
				sRelPath = sRelPath.substring(1);
			url = ClassLoader.getSystemClassLoader().getResource(sRelPath);
			if (url == null)
			url = new URL(textOrURL);
			
		}
		catch (MalformedURLException err)
		{
		}
		JEditorPane ep = new JEditorPane()
		{
			public void reshape(int x, int y, int w, int h)
			{
				super.reshape(x, y, w, h);
				setMaximumSize(new Dimension(getMaximumSize().width, getMinimumSize().height));
				setPreferredSize(new Dimension(getPreferredSize().width, getMinimumSize().height));
			}
		};
		//ep.setPreferredSize(new Dimension(100, 100));
		ep.setEditable(false);
		ep.setBorder(BorderFactory.createEmptyBorder(0, 10, 15, 10));
		if (url == null)
		{
			ep.setContentType("text/html");
			textOrURL = GuiEnvConnUtils.fillContextValuesForPlaceholders(textOrURL, theFetchDataTaskSelection.getThePlan());
			ep.setText(textOrURL);
		}
		else
		{
			try
			{
				if (isImageURL(url.toString()))
				{
					 Image imag = Toolkit.getDefaultToolkit().createImage(url);
					 ImageIcon ii = new ImageIcon(imag);
					 JLabel lbImage = new JLabel();
					 lbImage.setBackground(Color.WHITE);
					 lbImage.setIcon(ii);
					 pGround.add(lbImage, 0);
					 return;
				}
				HTMLEditorKit kit = new HTMLEditorKit();
				ep.setEditorKit(kit);
				ep.getDocument().remove(0, ep.getDocument().getLength());
				InputStream in = url.openStream();
				try
				{
					ep.getEditorKit().read(GuiEnvConnUtils.convertStream(in, theFetchDataTaskSelection.getThePlan()) , ep.getDocument(), 0);
				}
				catch (ChangedCharSetException e1) 
				{
				    String charSetSpec = e1.getCharSetSpec();
				    if (e1.keyEqualsCharSet()) 
				    {
						ep.putClientProperty("charset", charSetSpec);
				    }
				    in.close();
				    URLConnection conn = url.openConnection();
				    in = GuiEnvConnUtils.convertStream(conn.getInputStream(), theFetchDataTaskSelection.getThePlan());
				    try 
				    {
				    	ep.getDocument().remove(0, ep.getDocument().getLength());
				    }
				    catch (BadLocationException e) {}
				    ep.getDocument().putProperty("IgnoreCharsetDirective", Boolean.valueOf(true));
				    ep.getEditorKit().read(GuiEnvConnUtils.convertStream(in, theFetchDataTaskSelection.getThePlan()) , ep.getDocument(), 0);
				    in.close();
				}
			}
			catch (Exception err)
			{
				err.printStackTrace();
			}
		}
		pGround.add(ep, 0);*/
	}
	
	
	private boolean isImageURL(String s)
	{
		int i = s.lastIndexOf('.'); 
		if (i > 0)
		{
			String sExtension = s.substring(i).toLowerCase();
			if (sExtension.equals(".jpeg") || sExtension.equals(".jpg")) return true;
			//if (sExtension.equals(".bmp")) return true;
			if (sExtension.equals(".png")) return true;
			if (sExtension.equals(".gif")) return true;
		}
		return false;
	}
	
	public void setEnabled(boolean b)
	{
		enabled = b;
		super.setEnabled(b);
		for (int i = 0; i < theCategories.size(); i++)
		{
			((GuiCategoryPanel) theCategories.get(i)).setEnabled(enabled);
		}
	}
	
	public Map getTheData()
	{
		Map ret = new HashMap();
		for (int i = 0; i < theCategories.size(); i++)
		{
			ret.putAll(((GuiCategoryPanel) theCategories.get(i)).getTheFetchedData());
		}
		
		return ret;
		/*ArrayList<IdentifierValueTuple> ret = new ArrayList<IdentifierValueTuple>();
		for (int i = 0; i < theCategories.size(); i++)
		{
			ret.addAll(((GuiCategoryPanel) theCategories.get(i)).getTheFetchedData());
		}
		return ret;*/
	}
	
	public boolean isEnabled()
	{
		return enabled;
	}
}
