package jadex.extension.envsupport.observer.graphics.drawable3d;

import java.util.ArrayList;

import jadex.extension.envsupport.observer.graphics.IViewport3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.special.SpatialControl;
import jadex.javaparser.IParsedExpression;

/**
 * 
 */
public class Text3d extends Primitive3d
{
	/** Model path. */
	protected String			_text;

	/**
	 * Creates default Text3d.
	 * 
	 * @param modelPath resource path of the model
	 */
	public Text3d(String text)
	{
		super();
		type = Primitive3d.PRIMITIVE_TYPE_TEXT3D;
		_text = text;
	}

	/**
	 * Creates a new Polygon drawable.
	 * 
	 * @param position position or position-binding
	 * @param xrotation xrotation or rotation-binding
	 * @param yrotation yrotation or rotation-binding
	 * @param zrotation zrotation or rotation-binding
	 * @param size size or size-binding
	 * @param absFlags flags for setting position, size and rotation as absolutes
	 * @param c modulation color or binding
	 * @param modelPath resource path of the texture
	 */
	public Text3d(Object position, Object rotation, Object size, int absFlags, Object c, String materialpath, String texturePath, String text, IParsedExpression drawcondition, String shadowtype, ArrayList<SpatialControl> controler)
	{
		super(Primitive3d.PRIMITIVE_TYPE_TEXT3D, position, rotation, size, absFlags, c, materialpath, texturePath, drawcondition, shadowtype, controler);
		_text = text;
	}
	
	/**
	 *  Set the primitive type (Disabled).
	 *  @param type The type to set.
	 */
	public void setType(int type)
	{
		throw new RuntimeException("Set type not supported: " + getClass().getCanonicalName());
	}
	
	/**
	 *  Returns the model path.
	 *  @return The model path.
	 */
	public String getText()
	{
		return _text;
	}
	
	
	public final static String getReplacedText(DrawableCombiner3d dc, Object obj, String text, IViewport3d vp)
	{
		String[] tokens = text.split("\\$");
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < tokens.length; ++i)
		{
//			System.out.println("hier3");
			if ((i & 1) == 0)
			{
				sb.append(tokens[i]);
			}
			else
			{
				if("".equals(tokens[i]))
				{
					sb.append("$");
				}
				else
				{
					sb.append(String.valueOf(dc.getBoundValue(obj, tokens[i], vp)));
//					sb.append(String.valueOf(SObjectInspector.getProperty(obj, tokens[i])));
				}
			}
		}
		return sb.toString();
	}
}

