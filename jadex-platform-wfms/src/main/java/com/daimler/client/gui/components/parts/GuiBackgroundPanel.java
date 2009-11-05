package com.daimler.client.gui.components.parts;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.jdesktop.swingx.painter.AlphaPainter;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.ImagePainter;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.painter.Painter;
import org.jdesktop.swingx.painter.PinstripePainter;

import com.daimler.util.swing.autohidepanel.HideablePanelGlassPane;

public class GuiBackgroundPanel extends JXPanel
{
	private String theTitle = "";
	private String theCategory = "";
	// private ImageIcon theImage;
	// private Image theImage;

	int main_top_X = 0;
	int main_top_Y = 0;
	int main_bottom_X = 0;
	int main_bottom_Y = 0;
	int offset_tasks_top = 0;
	int offset_tasks_bottom = 0;
	int title_X = 0;
	int title_Y = 0;
	int category_X = 0;
	int category_Y = 0;
	String taskPanePosition = HideablePanelGlassPane.WEST;

	public GuiBackgroundPanel()
	{
		init();
		setLayout(new BorderLayout());
		setOpaque(false);
	}

	private void init()//Configuration conf)
	{
		main_top_X = 50;
	    main_top_Y = 160;
	    main_bottom_X = 30;
	    main_bottom_Y = 60;
	    offset_tasks_top = 0;
	    offset_tasks_bottom = 0;
	    title_X = 60;
	    title_Y = 140;
	    category_X = 700;
	    category_Y = 85;
        /*main_top_X = conf.getIntProperty("Top_Main_X", 0, "GUI_Options");
        main_top_Y = conf.getIntProperty("Top_Main_Y", 0, "GUI_Options");
        main_bottom_X = conf.getIntProperty("Bottom_Main_X", 0, "GUI_Options");
        main_bottom_Y = conf.getIntProperty("Bottom_Main_Y", 0, "GUI_Options");
        title_X = conf.getIntProperty("Title_X", -1, "GUI_Options");
        title_Y = conf.getIntProperty("Title_Y", -1, "GUI_Options");
        category_X = conf.getIntProperty("Category_X", -1, "GUI_Options");
        category_Y = conf.getIntProperty("Category_Y", -1, "GUI_Options");
        offset_tasks_top = conf.getIntProperty("Offset_Tasks_Top", 0, "GUI_Options");
        offset_tasks_bottom = conf.getIntProperty("Offset_Tasks_Bottom", 0, "GUI_Options");*/
	    
        // TODO: find better default
	    String sImgPath = "com/daimler/client/gui/images/DiPPBG.jpg"; //conf.getProperty("Backgroundimage", "", "GUI_Options");
        /*String sPanelPosition = conf
        .getProperty("Tasks_Position", "Left", "GUI_Options");
        if (sPanelPosition.equals("Right")) {
            taskPanePosition = HideablePanelGlassPane.EAST;
        } else if (sPanelPosition.equals("Left")) {*/
        taskPanePosition = HideablePanelGlassPane.WEST;
        //}
        
        // theImage = new ImageIcon(ClassLoader.getSystemResource(sImgPath));
        // BufferedImage bi = new BufferedImage()
        try {
            ImagePainter painter = new ImagePainter(
                    ClassLoader.getSystemResource(sImgPath));
            /*{
                public void doPaint(Graphics2D g, Object comp, int width,
                        int height) {
                    super.doPaint(g, comp, width, height);
                    Font defaultFont = g.getFont();
                    Color defaultColor = g.getColor();
                    if (title_X > -1 && title_Y > -1) {
                        g.setFont(new Font(null, Font.BOLD, 16));
                        g.setColor(new Color(229, 242, 248));
                        g.drawString(theTitle, title_X, title_Y);
                    }

                    if (category_X > -1 && category_Y > -1) {
                        g.setFont(new Font(null, Font.BOLD, 12));
                        g.setColor(new Color(229, 242, 248));
                        int iCatHalfWidth = g.getFontMetrics().charsWidth(
                                theCategory.toCharArray(), 0,
                                theCategory.length()) / 2;
                        g.drawString(theCategory, category_X - iCatHalfWidth,
                                category_Y);
                    }

                    g.setFont(defaultFont);
                    g.setColor(defaultColor);
                }
            };*/
            setBackgroundPainter(painter);
        } catch (IOException err) {
            System.err
                    .println("Cannot open background image for GUI-Connector. Please check the path.");
        }
        // setAlpha(1.0f);
        // setInheritAlpha(false);
    }

	public void paint(Graphics g)
	{
		super.paint(g);
		Font defaultFont = g.getFont();
		Color defaultColor = g.getColor();
		if (title_X > -1 && title_Y > -1)
		{
			g.setFont(new Font(null, Font.BOLD, 16));
			g.setColor(new Color(229, 242, 248));
			g.drawString(theTitle, title_X, title_Y);
		}

		if (category_X > -1 && category_Y > -1)
		{
			g.setFont(new Font(null, Font.BOLD, 12));
			g.setColor(new Color(229, 242, 248));
			int iCatHalfWidth = g.getFontMetrics().charsWidth(
					theCategory.toCharArray(), 0, theCategory.length()) / 2;
			g.drawString(theCategory, category_X - iCatHalfWidth, category_Y);
		}

		g.setFont(defaultFont);
		g.setColor(defaultColor);
	}

	/*
	 * public void paintComponents(Graphics g) { System.out.println("painting
	 * comps"); }
	 */

	public Dimension getPreferredSize()
	{
		int iWidth = ((ImagePainter) getBackgroundPainter()).getImage()
				.getWidth();
		int iHeight = ((ImagePainter) getBackgroundPainter()).getImage()
				.getHeight();
		return new Dimension(iWidth + 8, iHeight + 27);

		// return new Dimension(theImage.getIconWidth() + 8, theImage
		// .getIconHeight() + 27);

	}

	public void add(JPanel p)
	{
		if (p.getBorder() != null)
		{
			p.setBorder(BorderFactory.createCompoundBorder(BorderFactory
					.createEmptyBorder(main_top_Y, main_top_X, main_bottom_Y,
							main_bottom_X), p.getBorder()));
		} else
		{
			p.setBorder(BorderFactory.createEmptyBorder(main_top_Y, main_top_X,
					main_bottom_Y, main_bottom_X));
		}
		p.setOpaque(false);
		super.add(p, BorderLayout.CENTER);
	}

	public void add(JScrollPane p)
	{
		if (p.getBorder() != null)
		{
			p.setBorder(BorderFactory.createCompoundBorder(BorderFactory
					.createEmptyBorder(main_top_Y, main_top_X, main_bottom_Y,
							main_bottom_X), p.getBorder()));
		} else
		{
			p.setBorder(BorderFactory.createEmptyBorder(main_top_Y, main_top_X,
					main_bottom_Y, main_bottom_X));
		}
		p.setOpaque(false);
		super.add(p, BorderLayout.CENTER);
	}

	public int getCategory_X()
	{
		return category_X;
	}

	public int getCategory_Y()
	{
		return category_Y;
	}

	public int getMain_bottom_X()
	{
		return main_bottom_X;
	}

	public int getMain_bottom_Y()
	{
		return main_bottom_Y;
	}

	public int getMain_top_X()
	{
		return main_top_X;
	}

	public int getMain_top_Y()
	{
		return main_top_Y;
	}

	public int getTitle_X()
	{
		return title_X;
	}

	public int getTitle_Y()
	{
		return title_Y;
	}

	public int getTasks_Offset_Top()
	{
		return offset_tasks_top;
	}

	public int getTasks_Offset_Bottom()
	{
		return offset_tasks_bottom;
	}

	public String getTheTitle()
	{
		return theTitle;
	}

	public void setTheTitle(String theTitle)
	{
		this.theTitle = theTitle;
		repaint();
	}

	public String getTheCategory()
	{
		return theCategory;
	}

	public void setTheCategory(String theCategory)
	{
		this.theCategory = theCategory;
		repaint();
	}

	public String getTaskPanePosition()
	{
		return taskPanePosition;
	}

}
