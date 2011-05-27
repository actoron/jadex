package jadex.extension.envsupport.observer.graphics;

import java.awt.Color;
import java.awt.Font;

/** 
 * Information on how to display a text.
 */
public class TextInfo
{
	private Font font;
	private Color color;
	private String text;
	
	public TextInfo(Font font, Color color, String text)
	{
		this.font = font;
		this.color = color;
		this.text = text;
	}
	
	public Font getFont()
	{
		return font;
	}
	
	public Color getColor()
	{
		return color;
	}
	
	public String getText()
	{
		return text;
	}
	
	public void setText(String text)
	{
		this.text = text;
	}
	
	public int hashCode()
	{
		return font.hashCode()*31 + color.hashCode()*15 + text.hashCode();
	}
	
	public boolean equals(Object obj)
	{
		if (obj instanceof TextInfo)
		{
			TextInfo other = (TextInfo) obj;
			return ((font.equals(other.font)) && (color.equals(other.color)) && (text.equals(other.text)));
		}
		return false;
	}
}
