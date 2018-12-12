package jadex.tools.comanalyzer.graph;

import java.awt.Font;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.FourPassImageShaper;
import jadex.tools.comanalyzer.Component;
import jadex.tools.comanalyzer.graph.GraphCanvas.AgentGroup;

/**
 * This is a collection of vertex transformer that transforms agent group
 * objects into other Objects.
 */
public class VertexTransformer {

	/**
	 * A Transformer for creating labels for agent groups.
	 */
	public static final class Label implements Transformer {

		// -------- attributes --------
		
		/** If the label transformer is enabled. */
		private boolean enabled;

		// -------- Transformer interface --------
		
		/**
		 * Transform an agent group into a String.
		 * Returns the id of the agent.
		 * 
		 * @param group The agent group.
		 * @return The String for the label.
		 */
		public Object transform(Object group)
		{
			if (enabled) {
				if (((AgentGroup)group).isSingelton()) {
					// only apply for singelton
					Component agent = (Component)((AgentGroup)group).getSingelton();
					String mls = agent.getId();
					return mls;
				}
			}
			return null;
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
	 * A Transformer for assigning a normal or bold Font to the agent group
	 * labels.
	 */
	public final static class LabelFont implements Transformer {

		// -------- constants --------
		
		/** The normal font. */
		public static final Font f = new Font("Helvetica", Font.PLAIN, 12);

		/** The bold font */
		public static final Font b = new Font("Helvetica", Font.BOLD, 12);
		
		// -------- attributes --------
		
		/** If the font is bold */
		protected boolean bold = false;

		// -------- Transformer interface --------

		/**
		 * Returns the bold font if <code>bold</code> is <code>true</code>,
		 * else the normal font.
		 * 
		 * @param group The a gent group
		 * @return The font for the label.
		 */
		public Object transform(Object group)
		{
			if (bold)
				return b;
			else
				return f;
		}
		
		// -------- LabelFont methods --------

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
	 * A transformer for assigning an icon of a specific size to the agent
	 * group. The size is calculated from the degree (vertex weight) of the
	 * agent group (the number of incomimng and outgoing edges from the vertex)
	 * in relation to the highest degree of all agent groups in the graph.
	 */
	public final static class IconSize implements Transformer {

		// -------- constants --------
		
		/** The min vertex icon size */
		public static final int MIN_VERTEX_SIZE = 24;

		/** The max vertex icon size */
		public static final int MAX_VERTEX_SIZE = 100;

		// -------- attributes --------
		
		/** The icon map to save transformed icons for performance reason */
		protected Map iconMap = new HashMap();

		/** If icon scaling is enabled */
		protected boolean scale = false;

		/** The scale range. (0<= scaleRange <= MAX_ICON_SIZE - MIN_ICON_SIZE */
		protected int scaleRange = 1;

		/** The proportional factor (1<= proFactor<= 100) */
		protected int propFactor = 50;

		/** The graph */
		protected ComponentGroupMultiGraph graph;

		// -------- constructor --------
		
		/**
		 * Creates a IconSize transformer for the graph.
		 * 
		 * @param graph The graph.
		 */
		public IconSize(ComponentGroupMultiGraph graph) {
			this.graph = graph;

		}

		// -------- Transformer interface --------
		
		/**
		 * Returns the icon for the given agent group.
		 * 
		 * @param group The agent group.
		 * @return The icon.
		 */
		public Object transform(Object group)
		{

			Component agent = (Component)((AgentGroup)group).getSingelton();
			String iconname = null;
			// returns the iconname from the agent state
			if (agent.getState().equals(Component.STATE_OBSERVED)) {
				iconname = "agent_standard";
			} else if (agent.getState().equals(Component.STATE_IGNORED)) {
				iconname = "agent_standard";
			} else if (agent.getState().equals(Component.STATE_DEAD)) {
				iconname = "agent_dead";
			} else if (agent.getState().equals(Component.STATE_DUMMY)) {
				iconname = "agent_dummy";
			} else {
				iconname = "agent_unknown";
			}

			if (scale) {
				// get the highest vertex weight for scaling
				int highest = graph.getHighestVertexWeight() + 1;
				int degree = graph.degree(group) + 1;
				// calc proportion for current edge to the highest number
				double factor = (double) (100 - propFactor) / (double) (propFactor);
				double iconsize = MIN_VERTEX_SIZE + Math.pow((double) degree / (double) highest, factor) * scaleRange;

				// is the icon already created for that size?
				Icon icon = (Icon)iconMap.get(iconname + (int) iconsize);

				if (icon == null) {
					// get the icon
					ImageIcon image = (ImageIcon) GraphCanvas.icons.getIcon(iconname + "_big");
					// scale the icon to the new size
					icon = new ImageIcon(image.getImage().getScaledInstance((int) iconsize, (int) iconsize, Image.SCALE_SMOOTH));
					// save the transformed icon in the map.
					iconMap.put(iconname + (int) iconsize, icon);
				}
				return icon;
			} else {
				return GraphCanvas.icons.getIcon(iconname + "_small");
			}
		}
		
		// -------- IconSize methods --------

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
			return this.scale;
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
		public void setPropFactor(int value)
		{
			this.propFactor = value;

		}

	}

	/**
	 * A transformer for assigning the shape size. The shape is the border for
	 * the arrow head, therefore the shape and the image outline must be
	 * (almost) identical.
	 */
	public final static class ShapeSize implements Transformer {

		// -------- attributes --------
		
		/** The graph. */
		protected Graph graph;

		/** The transformer for the icon. */
		protected IconSize iconTransformer;

		/** The shape map to save transformed shapes for performance reason */
		protected Map shapeMap = new HashMap();

		// -------- constructor --------
		
		/**
		 * Creates a ShapeSize transformer for the graph.
		 * 
		 * @param graph The graph.
		 * @param iconTransformer The icon transformer
		 */
		public ShapeSize(Graph graph, IconSize iconTransformer) {
			this.graph = graph;
			this.iconTransformer = iconTransformer;
		}

		// -------- Transformer interface --------
		
		/**
		 * Returns the shape for the agent group. The shape is calculated from
		 * the icon calculated by the icon transformer.
		 * 
		 * @param group The agent group.
		 * @return The shape.
		 */
		public Object transform(Object group)
		{
			// get the icon from the icon transformer.
			Icon icon = (Icon)iconTransformer.transform(group);

			if (icon != null && icon instanceof ImageIcon) {
				Image image = ((ImageIcon) icon).getImage();
				Shape shape = (Shape) shapeMap.get(image);

				if (shape == null) {
					// if the shape is not already created,
					// transform the icon to a shape
					shape = FourPassImageShaper.getShape(image, 30);
					if (shape.getBounds().getWidth() > 0 && shape.getBounds().getHeight() > 0) {
						// don't cache a zero-sized shape,
						// wait for the image to be ready
						int width = image.getWidth(null);
						int height = image.getHeight(null);
						AffineTransform transform = AffineTransform.getTranslateInstance(-width / 2, -height / 2);
						shape = transform.createTransformedShape(shape);
						shapeMap.put(image, shape);
					}
				}
				return shape;
			} else {
				return null;
			}

		}

	}

	/**
	 * A transformer for tool tips.
	 */
	public static final class ToolTips implements Transformer {

		// -------- Transformer interface --------
		
		/**
		 * Returns the id of the agent in the agent group.
		 * 
		 * @param group The agent group.
		 * @return The Id.
		 */
		public Object transform(Object group)
		{
			if (((AgentGroup)group).isSingelton()) {
				return ((Component)((AgentGroup)group).getSingelton()).getId();
			} else {
				return null;
			}
		}
	}

}
