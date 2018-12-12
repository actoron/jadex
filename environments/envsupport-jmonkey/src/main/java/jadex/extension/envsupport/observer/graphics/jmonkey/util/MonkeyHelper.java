package jadex.extension.envsupport.observer.graphics.jmonkey.util;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.debug.Arrow;

public class MonkeyHelper
{
	public static Geometry giveArrow (ColorRGBA color, Vector3f direction, AssetManager assetManager)
	{
		Geometry ret = giveArrow (color, 1, direction, assetManager);
		return ret;
	}
	
	public static Geometry giveArrow (ColorRGBA color, float thickness, Vector3f direction, AssetManager assetManager)
	{
		Arrow a = new Arrow(direction);
		a.setLineWidth(thickness);
		Geometry ret = new Geometry("arrrow " + direction.toString(), a);
		Material m = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		m.setColor("Color", color);
		ret.setMaterial(m);
		return ret;
	}
}
