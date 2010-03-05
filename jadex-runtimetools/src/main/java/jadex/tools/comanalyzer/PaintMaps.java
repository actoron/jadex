package jadex.tools.comanalyzer;

import java.awt.Color;
import java.awt.Paint;
import java.util.HashMap;
import java.util.Map;


/**
 * The maps for the different paint modes. This class retrievs informations from
 * given messages and agents based on different parameters and creates unique
 * (up to a certain length) paint maps for each parameter.
 */
public class PaintMaps
{

	/** The default paint mode provides only the default color */
	public static final int PAINTMODE_DEFAULT = 0;

	/** The paint mode for differ conversation ids */
	public static final int PAINTMODE_CONVERSATION = 1;

	/** The paint mode for differ performatives */
	public static final int PAINTMODE_PERFORMATIV = 2;

	/** The paint mode for differ protocols */
	public static final int PAINTMODE_PROTOCOL = 3;

	/** The paint mode for differ agents */
	public static final int COLOR_COMPONENT = 4; // used only by the chart

	/** The standard default color */
	public static final Paint DEFAULT_COLOR = Color.BLACK;

	/** The paint map for conversation ids */
	protected Map conversationPaints = new HashMap();

	/** The paint map for performatives */
	protected Map performativPaints = new HashMap();

	/** The paint map for protocols */
	protected Map protocolPaints = new HashMap();

	/** The paint map for agents */
	protected Map agentPaints = new HashMap();

	/** The default color */
	private Paint defaultPaint;

	/** The color table with all available colors */
	private Paint paintTable[];

	// counter for all paint modes to save the last position in the color table
	private int conversationCounter = 0;

	private int performativCounter = 0;

	private int protocolCounter = 0;

	private int agentCounter = 0;

	/**
	 * Default constructor with standard default color.
	 */
	public PaintMaps()
	{
		this(DEFAULT_COLOR);
	}

	/**
	 * Constructor with given default color.
	 * 
	 * @param defaultPaint The default color.
	 */
	public PaintMaps(Paint defaultPaint)
	{
		this.defaultPaint = defaultPaint;
		this.paintTable = ToolColor.createDefaultPaintArray();

		// set null value to the default color
		conversationPaints.put(null, defaultPaint);
		performativPaints.put(null, defaultPaint);
		protocolPaints.put(null, defaultPaint);
		agentPaints.put(null, defaultPaint);
	}

	/**
	 * Retrievs information from the message and creates unique colors.
	 * 
	 * @param message The message the information is retrieved from.
	 */
	public void createColor(Message message)
	{
		String convid = (String)message.getParameter(Message.CONVERSATION_ID);
		String perform = (String)message.getParameter(Message.PERFORMATIVE);
		String protocol = (String)message.getParameter(Message.PROTOCOL);

		if(convid != null && !conversationPaints.containsKey(convid))
		{
			Color paint = (Color)paintTable[conversationCounter++ % paintTable.length];
			conversationPaints.put(convid, paint);
		}

		if(perform != null && !performativPaints.containsKey(perform))
		{
			Color paint = (Color)paintTable[performativCounter++ % paintTable.length];
			performativPaints.put(perform, paint);
		}

		if(protocol != null && !protocolPaints.containsKey(protocol))
		{
			Color paint = (Color)paintTable[protocolCounter++ % paintTable.length];
			protocolPaints.put(protocol, paint);
		}

		Component sender = message.getSender();
		Component receiver = message.getReceiver();

		if(!agentPaints.containsKey(sender))
		{
			Color paint = (Color)paintTable[agentCounter++ % paintTable.length];
			agentPaints.put(sender, paint);
		}
		if(!agentPaints.containsKey(receiver))
		{
			Color paint = (Color)paintTable[agentCounter++ % paintTable.length];
			agentPaints.put(receiver, paint);
		}
	}

	/**
	 * Returns the color for a given message and a given paint mode.
	 * 
	 * @param message The message.
	 * @param paintMode The paint mode.
	 * @return The color for the message and the paint mode.
	 */
	public Paint getMessagePaint(Message message, int paintMode)
	{
		Paint paint = defaultPaint;

		switch(paintMode)
		{
			case PAINTMODE_CONVERSATION:
				String convid = (String)message.getParameter(Message.CONVERSATION_ID);
				paint = (Paint)conversationPaints.get(convid);
				break;
			case PAINTMODE_PERFORMATIV:
				String perform = (String)message.getParameter(Message.PERFORMATIVE);
				paint = (Paint)performativPaints.get(perform);
				break;
			case PAINTMODE_PROTOCOL:
				String protocol = (String)message.getParameter(Message.PROTOCOL);
				paint = (Paint)protocolPaints.get(protocol);
				break;
		}

		return paint == null ? defaultPaint : paint;
	}

	/**
	 * Returns the created paint map for the given paint mode.
	 * 
	 * @param paintMode The paint mode.
	 * @return The paint map for the paint mode.
	 */
	public Map getPaints(int paintMode)
	{

		switch(paintMode)
		{
			case PAINTMODE_CONVERSATION:
				return conversationPaints;
			case PAINTMODE_PERFORMATIV:
				return performativPaints;
			case PAINTMODE_PROTOCOL:
				return protocolPaints;
			case COLOR_COMPONENT:
				return agentPaints;
		}

		return null;
	}

	/**
	 * Returns a color for the given key and the given paint mode.
	 * 
	 * @param key The key.
	 * @param paintType The paint mode.
	 * @return The color for the key and paint mode.
	 */
	public Paint getPaint(Comparable key, int paintType)
	{
		return getPaint(key, paintType, defaultPaint);
	}

	/**
	 * Returns a color for the given key and the given paint mode. If no such key
	 * is contained in the paint map the given default color is used.
	 * 
	 * @param key The key.
	 * @param paintType The paint mode.
	 * @param defaultPaint The default color.
	 * @return The color for the key and paint mode.
	 */
	public Paint getPaint(Comparable key, int paintType, Paint defaultPaint)
	{
		Paint paint = null;
		switch(paintType)
		{
			case PAINTMODE_CONVERSATION:
				paint = (Paint)conversationPaints.get(key);
				break;
			case PAINTMODE_PERFORMATIV:
				paint = (Paint)performativPaints.get(key);
				break;
			case PAINTMODE_PROTOCOL:
				paint = (Paint)protocolPaints.get(key);
				break;
			case COLOR_COMPONENT:
				paint = (Paint)agentPaints.get(key);
				break;
		}
		return paint == null ? defaultPaint : paint;
	}

	/**
	 * @return The default color.
	 */
	public Paint getDefaultPaint()
	{
		return defaultPaint;
	}

	/**
	 * @param defaultPaint The default color to set.
	 */
	public void setDefaultPaint(Paint defaultPaint)
	{
		this.defaultPaint = defaultPaint;
	}

}
