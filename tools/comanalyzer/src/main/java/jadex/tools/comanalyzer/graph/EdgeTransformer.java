package jadex.tools.comanalyzer.graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.GradientEdgePaintTransformer;
import jadex.tools.comanalyzer.Message;
import jadex.tools.comanalyzer.PaintMaps;
import jadex.tools.comanalyzer.graph.GraphCanvas.MessageGroup;


/**
 * This is a collection of edge transformer that transforms message group
 * objects into other Objects.
 */
public class EdgeTransformer
{

	/**
	 * A Transformer for creating labels for message groups.
	 */
	public static final class Label implements Transformer
	{
		//-------- attributes --------

		/** If the label transformer is enabled. */
		protected boolean enabled;

		// -------- Transformer interface --------

		/**
		 * Transform an message group into a String. Returns the size of the
		 * message group.
		 * @param group The message group.
		 * @return The String for the label.
		 */
		public Object transform(Object group)
		{
			Object ret = null;
			if(enabled)
				ret = Integer.toString(((MessageGroup)group).size());
			
			return ret;
		}

		// -------- Label methods --------

		/**
		 * Enable/disable the transformer.
		 * 
		 * @param enabled <code>true</code> if the transformer ought to be
		 * enabled.
		 */
		public void setEnabled(boolean enabled)
		{
			this.enabled = enabled;
		}

		/**
		 * @return <code>true</code> if the transformer is enabled.
		 */
		public boolean isEnabled()
		{
			return enabled;
		}

	}

	/**
	 * A Transformer for assigning a normal or bold Font to the message group
	 * labels.
	 */
	public static class LabelFont implements Transformer
	{
		//-------- constants --------

		/** The normal font. */
		public static final Font f = new Font("Helvetica", Font.PLAIN, 12);

		/** The bold font */
		public static final Font b = new Font("Helvetica", Font.BOLD, 12);

		//-------- attributes --------

		/**
		 * If the font is bold
		 */
		protected boolean bold = false;

		//-------- Transformer interface --------

		/**
		 * Returns the bold font if <code>bold</code> is <code>true</code>,
		 * else the normal font.
		 * 
		 * @param group The a message group
		 * @return The font for the label.
		 */
		public Object transform(Object e)
		{
			if(bold)
				return b;
			else
				return f;
		}

		//-------- LabelFont methods --------

		/**
		 * @param bold <code>true</code> if the bold font ought to be used.
		 */
		public void setBold(boolean bold)
		{
			this.bold = bold;
		}

		/**
		 * @return <code>true</code> if the bold font is used.
		 */
		public boolean getBold()
		{
			return bold;
		}
	}

	/**
	 * A Transformer for assigning a weighted stroke to the message group (the
	 * edge). The size is calculated from the edge weight of the message group
	 * (the number of elements (messages) inside the group) in relation to the
	 * highest edge weight of all message groups in the graph.
	 */
	public final static class WeightStroke implements Transformer
	{
		//-------- constants --------

		/** The min edge size */
		public static final int MIN_EDGE_SIZE = 1;

		/** The max edge size */
		public static final int MAX_EDGE_SIZE = 20;

		/** The basic stroke */
		public static final Stroke basic = new BasicStroke(1);

		/** The heavy stroke */
		public static final Stroke heavy = new BasicStroke(2);

		/** The dotted stroke */
		public static final Stroke dotted = RenderContext.DOTTED;

		//-------- attributes --------

		/** The graph */
		protected ComponentGroupMultiGraph graph;

		/** If stroke scaling is enabled */
		protected boolean scale = false;

		/** The scale range. (0<= scaleRange <= MAX_EDGE_SIZE - MIN_EDGE_SIZE */
		protected int scaleRange = 1;

		/** The proportional factor (1<= proFactor<= 100) */
		protected int propFactor = 50;

		//-------- constructor --------

		/**
		 * Creates a WeightStroke transformer for the graph.
		 * @param graph The graph.
		 */
		public WeightStroke(ComponentGroupMultiGraph graph)
		{
			this.graph = graph;
		}

		//-------- Transformer interface --------

		/**
		 * Returns the stroke for the given message group.
		 * 
		 * @param group The message group.
		 * @return The icon.
		 */
		public Object transform(Object group)
		{
			if(scale)
			{
				// get the highest edgeweight for scaling
				int highest = graph.getHighestEdgeWeight();
				// calc proportion for current edge to highest number
				double factor = (double)(100 - propFactor) / (double)(propFactor);
				double strokesize = Math.pow((double)((MessageGroup)group).size() / (double)highest, factor) * scaleRange;
				return strokesize > 1 ? new BasicStroke((float)strokesize) : heavy;
			}
			else
			{
				return heavy;
			}
		}

		// -------- WeightStroke methods --------

		/**
		 * @param scale <code>true</code> if scaling ought to be enabled.
		 */
		public void setScaling(boolean scale)
		{
			this.scale = scale;
		}

		/**
		 * @return <code>true</code> if scaling is enabled
		 */
		public boolean isScaling()
		{
			return scale;
		}

		/**
		 * @param scaleRange The scale range to set.
		 */
		public void setScaleRange(int scaleRange)
		{
			this.scaleRange = scaleRange;
		}

		/**
		 * @param value The proportional factor to set.
		 */
		public void setPropValue(int value)
		{
			this.propFactor = value;

		}

	}

	/**
	 * A transformer for colored egdes in respect of conversationid, performativ
	 * and protocol information.
	 */
	public final static class PaintMode implements Transformer
	{

		//-------- attributes --------

		/** The paint maps */
		protected PaintMaps paintMaps;

		/** The paint mode */
		protected int paintMode;

		//-------- constructor --------

		/**
		 * Create a transformer with given paint maps and paint mode.
		 * @param paintMaps The paint maps.
		 * @param paintMode The paint mode.
		 */
		public PaintMode(PaintMaps paintMaps, int paintMode)
		{
			this.paintMaps = paintMaps;
			this.paintMode = paintMode;
		}

		//-------- Transformer interface --------

		/**
		 * Transforms a message group into a Paint.
		 * @param group The message group.
		 * @return The paint for the message group.
		 */
		public Object transform(Object group)
		{
			if(((MessageGroup)group).isSingelton())
			{
				return paintMaps.getMessagePaint((Message)((MessageGroup)group).getSingelton(), paintMode);
			}
			else
			{
				// check if all msg colors are equal
				Set paints = new HashSet();
				for(Iterator it = ((MessageGroup)group).getElements().iterator(); it.hasNext();)
				{
					paints.add(paintMaps.getMessagePaint((Message)it.next(), paintMode));
				}
				if(paints.size() == 1)
				{
					// if all equal, return the color
					return paints.iterator().next();
				}
				else
				{
					// else default color (black)
					return paintMaps.getDefaultPaint();
				}
			}

		}

		//-------- PaintMode methods --------

		/**
		 * @return The paint mode.
		 */
		public int getPaintMode()
		{
			return paintMode;
		}

		/**
		 * @param paintMode The paint mode to set.
		 */
		public void setPaintMode(int paintMode)
		{
			this.paintMode = paintMode;
		}
	}

	/**
	 * A transformer for gradient egdes that includes support for represent
	 * picked state. The class uses the EdgePaint transformer to get the base
	 * color.
	 */
	public static final class GradientPaint extends GradientEdgePaintTransformer
	{

		//-------- constants --------

		/** The gradient level off */
		public static final int GRADIENT_NONE = 0;

		/** The gradient level on */
		public static final int GRADIENT_RELATIVE = 1;

		//-------- attributes --------

		/** The default paint transformer (provides the color). */
		protected Transformer defaultTransformer;

		/** The gradient level */
		protected int gradientLevel;

//		/** The self loop predicate */
//		protected Predicate selfLoop = new SelfLoopEdgePredicate();

		//-------- constructor --------

		/**
		 * Create a transformer with given default paint transformer, the
		 * visualization viewer to get the picked state of an edge and the
		 * gradient level.
		 * 
		 * @param defaultTransformer The default paint transformer.
		 * @param vv The visualization viewer.
		 * @param gradientLevel The gradient level.
		 */
		public GradientPaint(Transformer defaultTransformer, VisualizationViewer vv, int gradientLevel)
		{
			super(Color.WHITE, Color.BLACK, vv);
			this.defaultTransformer = defaultTransformer;
			this.gradientLevel = gradientLevel;
		}

		// -------- Transformer interface --------

		/**
		 * Transforms a message group into a Paint. If gradient level is none
		 * (off) delegate to default transformer. Else delegate to super class
		 * which takes the getColor1 and getColor2 methods to draw a gradient
		 * edge. The picked state is reppresented by simply swapping the colors.
		 * @param group The message group.
		 * @return The paint for the message group.
		 */
		public Paint transform(Object group)
		{
			if(gradientLevel == GRADIENT_NONE)
			{
				return (Paint)defaultTransformer.transform(group);
			}
			else
			{
				return super.transform(group);
			}
		}

		//-------- GradientPaint methods --------

		/**
		 * Sets the default paint transformer.
		 * @param defaultTransformer The default paint transformer to set.
		 */
		public void setDefaultTransformer(Transformer defaultTransformer)
		{
			this.defaultTransformer = defaultTransformer;
		}

		/**
		 * @param gradientLevel The gradient level to set
		 */
		public void setGradientLevel(int gradient_level)
		{
			this.gradientLevel = gradient_level;
		}

		/**
		 * @return The gradient level
		 */
		public int getGradientLevel()
		{
			return gradientLevel;
		}

		//-------- GradientEdgePaintTransformer methods --------

		/**
		 * The super class retrieves the start color.
		 */
		protected Color getColor1(Object e)
		{
			return vv.getPickedEdgeState().isPicked(e) ? (Color)defaultTransformer.transform(e) : Color.WHITE;
		}

		/**
		 * The super class retrieves the end color.
		 */
		protected Color getColor2(Object e)
		{
			return vv.getPickedEdgeState().isPicked(e) ? Color.WHITE : (Color)defaultTransformer.transform(e);

		}

	}

	/**
	 * A transformer for tool tips.
	 */
	public static final class ToolTips implements Transformer
	{
		//-------- Transformer interface --------

		/**
		 * Returns the id of the message in the message group.
		 * @param group The message group.
		 * @return The Id.
		 */
		public Object transform(Object group)
		{
			if(((MessageGroup)group).isSingelton())
			{
				return ((Message)((MessageGroup)group).getSingelton()).getId();
			}
			else
			{
				// TODO: tooltip for message groups with more than one message
				return null;
			}
		}

	}

}
