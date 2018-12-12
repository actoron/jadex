package jadex.tools.comanalyzer.diagram;

import java.awt.Color;


/**
 * Drawing constants for agent and message canvas.
 */
public class DiagramConstants
{

	/** arrow constants */
	public static final int arrowHeight = 10;

	public static final int arrowWidth = 8;

	public static final int arrowNotch = 4;

	/** The x-distance between timelines. */
	public static final int xDistTimeline = 80;

	/** The y-distance between timelines. */
	public static final int yDistTimeline = 30;

	/** The x-offset of timeline. */
	public static final int xOffsetTimeline = 45;

	/** The y-offset of timeline. */
	public static final int yOffsetTimeline = 20;

	/** The height of the componentbox. */
	public static final int heightComponentbox = 30;

	/** The width of the componentbox. */
	public static final int widthComponentbox = 50;

	/** The y-offset to componentbox */
	public static final int yOffsetComponentbox = xOffsetTimeline - (widthComponentbox / 2); //20 bei 50 width;

	/** The x- and y-offset of the message number */
	public static final int xOffsetMessageNumber = 10;

	public static final int yOffsetMessageNumber = 3;

	/** The y-offset for the label */
	public static final int yOffsetMessageLabel = -8;

	/** Color of the message number*/
	public static final Color COLOR_MESSAGENUMBER = new Color(150, 50, 50);

	/** Color of the timeline. */
	public static final Color COLOR_TIMELINE = Color.LIGHT_GRAY; //new Color(0, 100, 50);

	/** The height for the timeline in ComponentCanvas */
	public static final int heigtComponentboxTimeline = 15;


	// -------- helper methods --------


	/**
	 * Get the x-position for the timeline from the index of an component.
	 * @param component_position The index of the component
	 * @return x-position of the timeline.
	 */
	public static int getTimelineX(int component_position)
	{
		return component_position * xDistTimeline + xOffsetTimeline;
	}

	/**
	 * Get the y-position for the timeline from the index of a message.
	 * @param message_position The index of the message
	 * @return y-position of the timeline.
	 */
	public static int getTimelineY(int message_position)
	{
		return (message_position * yDistTimeline) + yOffsetTimeline;
	}


}
