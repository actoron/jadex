package jadex.android.controlcenter.componentViewer.properties;

import java.io.Serializable;

public class PropertyItem implements Serializable
{
	public String name;
	public Object value;

	public PropertyItem(String name, Object value)
	{
		this.name = name;
		this.value = value;
	}
	
	
}
