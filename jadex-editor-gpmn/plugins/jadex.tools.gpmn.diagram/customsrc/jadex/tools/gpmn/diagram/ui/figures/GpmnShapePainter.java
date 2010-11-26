/**
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id$
 */
package jadex.tools.gpmn.diagram.ui.figures;


import jadex.tools.gpmn.GoalType;
import jadex.tools.gpmn.diagram.part.GpmnDiagramEditorPlugin;
import jadex.tools.gpmn.diagram.ui.AbstractGpmnFigure;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gmf.runtime.draw2d.ui.mapmode.MapModeUtil;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class GpmnShapePainter
{
	
	protected static Image notFoundImage;
	static
	{
		try
		{
			ImageDescriptor desc = GpmnDiagramEditorPlugin
					.imageDescriptorFromPlugin(GpmnDiagramEditorPlugin.ID,
							"/icons/background/NotFound.png"); //$NON-NLS-0$ 
			notFoundImage = desc.createImage();
		}
		catch (Exception e)
		{
			notFoundImage = PlatformUI.getWorkbench().getSharedImages()
					.getImage(ISharedImages.IMG_DEC_FIELD_WARNING);
		}
	}
	
	
	/**
	 * Paint the given string centered into the given rectangle from the graphics
	 * 
	 * @param graphics the graphic to draw
	 * @param rect the rectangle to center the string
	 * @param s the string to draw
	 */
	public static void paintCenteredString(Graphics graphics, PrecisionRectangle rect, String s)
	{
		PrecisionPoint center = new PrecisionPoint(rect.preciseX + (rect.preciseWidth / 2.0),
				   rect.preciseY + (rect.preciseHeight / 2.0));
		double startX = center.preciseX - FigureUtilities.getTextWidth(s, graphics.getFont())/2.0;
		double startY = center.preciseY - graphics.getFontMetrics().getAscent()/2.0;
		PrecisionPoint pp = new PrecisionPoint(startX, startY);
		pp.x = ((int)Math.round(pp.preciseX));
		pp.y = ((int)Math.round(pp.preciseY));

		graphics.drawString(s, pp);
	}
	
	/**
	 * Paints sub process marker figure.
	 * 
	 * @param graphics
	 *            The Graphics object used for painting
	 * @param fig
	 *            marker figure.
	 */
	public static void paintSubProcessMarkerInsideFigure(Graphics graphics, Rectangle figureRect ,Shape fig)
	{

		PrecisionRectangle markerRect = getSubMarkerBounds(figureRect);
		
		int lineWidth = MapModeUtil.getMapMode(fig).LPtoDP(2);
		paintSubProcessMarker(graphics, markerRect, lineWidth);
	}
	
	/**
	 *  Paints an opaque, black oval.
	 *  @param graphics The Graphics object used for painting
	 *  @param bounds Bounds of the oval
	 */
	public static void paintOpaqueOval(Graphics graphics, Rectangle bounds)
	{
		paintOpaqueOval(graphics, bounds, 1.0f);
	}
	
	/**
	 *  Paints an opaque, black oval.
	 *  @param graphics The Graphics object used for painting
	 *  @param bounds Bounds of the oval
	 *  @param lineWidth The line width.
	 */
	public static void paintOpaqueOval(Graphics graphics, Rectangle bounds, float lineWidth)
	{
		PrecisionRectangle b = new PrecisionRectangle(bounds);
		graphics.pushState();
		graphics.setLineWidthFloat(lineWidth);
		
		graphics.setForegroundColor(ColorConstants.white);
		graphics.fillOval(b);
		
		graphics.setForegroundColor(ColorConstants.black);
		graphics.drawOval(b);
		
		graphics.popState();
	}
	
	/**
	 *  Paints a black plus sign.
	 *  @param graphics The Graphics object used for painting
	 *  @param bounds Bounds of the plus sign
	 *  @param boundsDist distance of the plus sign from the bounds
	 *  @param lineWidth The line width.
	 */
	public static void paintPlus(Graphics graphics, Rectangle bounds, float boundsDist, float lineWidth)
	{
		PrecisionRectangle b = new PrecisionRectangle(bounds);
		
		graphics.pushState();
		graphics.setLineWidthFloat(lineWidth);
		graphics.setForegroundColor(ColorConstants.black);
		graphics.drawLine(new PrecisionPoint(b.preciseX + b.preciseWidth / 2.0, b.preciseY + boundsDist), new PrecisionPoint(b.preciseX + b.preciseWidth / 2.0, b.preciseY + b.preciseHeight - boundsDist));
		paintMinus(graphics, bounds, boundsDist, lineWidth);
		graphics.popState();
	}
	
	/**
	 *  Paints a black minus sign.
	 *  @param graphics The Graphics object used for painting
	 *  @param bounds Bounds of the minus sign
	 *  @param boundsDist distance of the minus sign from the bounds
	 *  @param lineWidth The line width.
	 */
	public static void paintMinus(Graphics graphics, Rectangle bounds, float boundsDist, float lineWidth)
	{
		PrecisionRectangle b = new PrecisionRectangle(bounds);
		
		graphics.pushState();
		graphics.setLineWidthFloat(lineWidth);
		graphics.setForegroundColor(ColorConstants.black);
		graphics.drawLine(new PrecisionPoint(b.preciseX + boundsDist, b.preciseY + b.preciseHeight / 2.0), new PrecisionPoint(b.preciseX + b.preciseWidth - boundsDist, b.preciseY + b.preciseHeight / 2.0));
		graphics.popState();
	}

	/**
	 * Paints sub process marker figure.
	 * @param graphics The Graphics object used for painting
	 * @param rect 
	 * @param lineWidth
	 */
	public static void paintSubProcessMarker(Graphics graphics, Rectangle rect,
			int lineWidth)
	{
		graphics.pushState();
		
		graphics.setForegroundColor(ColorConstants.darkGray);
		
		graphics.drawRectangle(rect);
		int d = lineWidth;
		Point l = rect.getLeft().translate(d, 0);
		Point r = rect.getRight().translate(-d, 0);
		Point t = rect.getTop().translate(0, d);
		Point b = rect.getBottom().translate(0, -d);
		
		graphics.drawLine(l, r);
		graphics.drawLine(t, b);
		
		graphics.popState();
	}
	
	/**
	 * Paints sub process marker figure.
	 * 
	 * @param graphics
	 *            The Graphics object used for painting
	 * @param fig
	 *            marker figure.
	 */
	public static void paintUnsetSubProcessMarkerInsideFigure(Graphics graphics, Rectangle figureRect ,Shape fig)
	{

		PrecisionRectangle markerRect = getSubMarkerBounds(figureRect);
		
		int lineWidth = MapModeUtil.getMapMode(fig).LPtoDP(2);
		paintUnsetSubProcessMarker(graphics, markerRect, lineWidth);
	}
	
	/**
	 * Paints sub process marker figure.
	 * @param graphics The Graphics object used for painting
	 * @param rect 
	 * @param lineWidth
	 */
	public static void paintUnsetSubProcessMarker(Graphics graphics, Rectangle rect,
			int lineWidth)
	{
		graphics.pushState();
		
		graphics.setForegroundColor(ColorConstants.darkGray);
		
		graphics.drawRectangle(rect);
		
		GpmnShapePainter.paintCenteredString(graphics, new PrecisionRectangle(rect), "?");
		
		graphics.popState();
	}
	
    
    /**
     * Calculated loop marker bounds for the figure with the specified bounds
     * 
     * @param figureRect
     *            figure bounds
     * @return calculated loop marker bounds
     */
    public static PrecisionRectangle getSubMarkerBounds(Rectangle figureRect) {
        final double RATIO = 5.0;
        double markerHeight = figureRect.height / RATIO;
        double markerWidth = Math.min(markerHeight, figureRect.width);

        double markerX = figureRect.x + (figureRect.width - markerWidth) / 2.0;
        double markerY = figureRect.y + figureRect.height - (markerHeight * 4/3);

        //markerY -= markerHeight / 3.0; // 2.5
        
        PrecisionRectangle bounds = new PrecisionRectangle();
        bounds.setX(markerX);
        bounds.setY(markerY);
        bounds.setWidth(markerWidth);
        bounds.setHeight(markerHeight);

        return bounds;
    }
    
    
    /**
     * Calculated loop marker bounds for the figure with the specified bounds
     * 
     * @param figureRect
     *            figure bounds
     * @return calculated loop marker bounds
     */
    public static PrecisionRectangle getTopTitleMarkerBounds(Rectangle figureRect) {
        
    	final double RATIO = 4.0;
    	
        double markerHeight = figureRect.height / RATIO;
        double markerWidth = figureRect.width / 2.0;

        double markerX = figureRect.x + (figureRect.width - markerWidth) / 2.0;
        double markerY = figureRect.y + (figureRect.height * 1/(RATIO*RATIO) );
        
        PrecisionRectangle bounds = new PrecisionRectangle();
        bounds.setX(markerX);
        bounds.setY(markerY);
        bounds.setWidth(markerWidth);
        bounds.setHeight(markerHeight);

        return bounds;
    }
    
    
	/**
	 * Paints sequential loop goal inside figure with the specified bounds.
	 * 
	 * @param graphics
	 *            The Graphics object used for painting
	 * @param figureRect
	 *            figure bounds
	 */
	public static void paintLoopInsideFigure(Graphics graphics,
			Rectangle figureRect, IFigure fig)
	{
		PrecisionRectangle loopRect = getSubMarkerBounds(figureRect);
		paintLoop(graphics, loopRect, fig);
	}
	

    /**
     * Paints sequential loop goal marker with the specified bounds.
     * 
     * @param graphics
     *            The Graphics object used for painting
     * @param loopRect
     *            loop marker bounds.
     */
    private static void paintLoop(Graphics graphics, PrecisionRectangle loopRect,IFigure fig) 
    {
        graphics.pushState();
        graphics.setLineWidth(MapModeUtil.getMapMode(fig).LPtoDP(2));

        int angleGrad = 30;// between 0 and 90 - angle between vertical axis
        // and start of arc
        graphics.drawArc(loopRect, -(90 - angleGrad), 360 - 2 * angleGrad);

        // now calculate end of arc coordinates
        double dx = loopRect.preciseWidth / 2
                * Math.cos(Math.toRadians(90 - angleGrad));
        double dy = loopRect.preciseHeight / 2
                * Math.sin(Math.toRadians(90 - angleGrad));

        double endX = loopRect.preciseX + loopRect.preciseWidth / 2 - dx;
        double endY = loopRect.preciseY + loopRect.preciseHeight / 2 + dy;
        double length = endX - loopRect.preciseX;

        // and draw arrow
        PrecisionPoint pp1 = new PrecisionPoint(loopRect.preciseX, endY);
        PrecisionPoint pp2 = new PrecisionPoint(endX, endY);
        graphics.drawLine(pp1, pp2);
        PrecisionPoint pp3 = new PrecisionPoint(endX, endY - length);
        graphics.drawLine(pp2, pp3);

        graphics.popState();
    }
    
    
    /**
	 * Paints parallel goal marker inside figure with the specified bounds.
	 * 
	 * @param graphics
	 *            The Graphics object used for painting
	 * @param figureRect
	 *            figure bounds
	 */
	public static void paintParallelInsideFigure(Graphics graphics,
			Rectangle figureRect, IFigure fig)
	{
		PrecisionRectangle loopRect = getSubMarkerBounds(figureRect);
		paintParallel(graphics, loopRect, fig);
	}
    
    /**
     * Paints parallel goal marker with the specified bounds.
     * 
     * @param graphics
     *            The Graphics object used for painting
     * @param parallelRect
     *            loop marker bounds.
     */
    private static void paintParallel(Graphics graphics, PrecisionRectangle parallelRect,IFigure fig) 
    {
        graphics.pushState();
        graphics.setLineWidth(MapModeUtil.getMapMode(fig).LPtoDP(2));

        // calculate points
        double dx1 = parallelRect.preciseX + parallelRect.preciseWidth * 1/4;
        double dy1 = parallelRect.preciseY + parallelRect.preciseHeight * 1/5;

        double dx2 = parallelRect.preciseX + parallelRect.preciseWidth * 3/4;
        double dy2 = parallelRect.preciseY + parallelRect.preciseHeight * 1/5;
        
        double endY1 = parallelRect.preciseY + parallelRect.preciseHeight * 4/5;
        double endY2 = parallelRect.preciseY + parallelRect.preciseHeight * 4/5;
        
        // and draw marker
        PrecisionPoint sp1 = new PrecisionPoint(dx1, dy1);
        PrecisionPoint ep1 = new PrecisionPoint(dx1, endY1);
        
        PrecisionPoint sp2 = new PrecisionPoint(dx2, dy2);
        PrecisionPoint ep2 = new PrecisionPoint(dx2, endY2);
        
        graphics.drawLine(sp1, ep1);
        graphics.drawLine(sp2, ep2);

        graphics.popState();
    }

    /**
     * Paints ordered mode goal marker inside figure with the specified bounds.
     * 
     * @param graphics
     * @param figureRect
     * @param figure
     */
    public static void paintModeOrderedInsideFigure(Graphics graphics,
			Rectangle figureRect, Figure figure)
	{
    	PrecisionRectangle rect = getSubMarkerBounds(figureRect);
		
    	//paintModeOrderedMarker(graphics, loopRect, goalFigure);
    	paintCenteredString(graphics, rect, "1...n");
	}


	static PrecisionRectangle getTypeMarkerBounds(Rectangle figureRect)
	{
		final double RATIO = 0.7;
	    double markerSize = Math.min(figureRect.height, figureRect.width) * RATIO;
	
	    // move 1/4 of size to the middle 
	    Point center = figureRect.getCenter();
	    double markerX = center.x - markerSize/2;
	    double markerY = center.y - markerSize/2;
	
	    PrecisionRectangle bounds = new PrecisionRectangle();
	    bounds.setX(markerX);
	    bounds.setY(markerY);
	    bounds.setWidth(markerSize);
	    bounds.setHeight(markerSize);
	
	    return bounds;
	}




	static void paintTypeImageInFigure(Graphics graphics, Rectangle bounds,
			AbstractGpmnFigure figure, Image image)
	{
		PrecisionRectangle markerBounds = getTypeMarkerBounds(bounds);
		//graphics.drawRectangle(markerBounds);
	
		int alpha = 20;
		// int alpha = GpmnDiagramEditorPlugin.getInstance().getPreferenceStore().
		// getInt(DiagramPreferenceInitializer.PREF_SHOW_SHADOWS_TRANSPARENCY);
		if (alpha <= 0)
		{
			return;
		}
	
		graphics.pushState();
		
	  	int oriAlpha = graphics.getAlpha();
	  	graphics.setAlpha(alpha);
	  	
		Rectangle imgBounds = new Rectangle(image.getBounds().x, image.getBounds().y, image.getBounds().width, image.getBounds().height);
		graphics.drawImage(image, imgBounds, (Rectangle)markerBounds);
		
		graphics.setAlpha(oriAlpha);
		
		graphics.popState();
	}

	/**
	 * Create the background image for the goal type
	 * @return The type background image for the current {@link GoalType}
	 */
	public static Image getBackgroundImage(String imgName)
	{
		assert imgName != null;
		
		//String baseURI = "/jadex.tools.gpmn.edit/icons/full/obj16/";
		String baseURI = "/icons/background/";
		String imageURI = imgName;
		
		ImageDescriptor desc = 
			//GpmnDiagramEditorPlugin.findImageDescriptor(baseURI + imageURI);
			GpmnDiagramEditorPlugin.imageDescriptorFromPlugin(GpmnDiagramEditorPlugin.ID, baseURI + imageURI); 
		if (null != desc)
		{
			return desc.createImage();
		}
		else
		{
			return notFoundImage;
		}

	}
	
}
