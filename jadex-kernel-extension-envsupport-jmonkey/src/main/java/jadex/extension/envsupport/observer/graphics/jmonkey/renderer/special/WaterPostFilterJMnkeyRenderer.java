package jadex.extension.envsupport.observer.graphics.jmonkey.renderer.special;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.water.WaterFilter;

import jadex.extension.envsupport.environment.SpaceObject;
import jadex.extension.envsupport.observer.graphics.drawable3d.DrawableCombiner3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.Primitive3d;
import jadex.extension.envsupport.observer.graphics.jmonkey.ViewportJMonkey;
import jadex.extension.envsupport.observer.graphics.jmonkey.renderer.AbstractJMonkeyRenderer;

public class WaterPostFilterJMnkeyRenderer extends AbstractJMonkeyRenderer{

	private Node waterNode = new Node("waterNode");
	public Spatial draw(DrawableCombiner3d dc, Primitive3d primitive,
			SpaceObject sobj, ViewportJMonkey vp) {

		Vector3f lightDir = new Vector3f(1f, 1f, 1f);
        WaterFilter water = new WaterFilter(waterNode, lightDir);
        water.setWaterHeight(-0.2f);
        water.setUseFoam(false);
        water.setUseRipples(true);
        water.setDeepWaterColor(ColorRGBA.Black.mult(0.1f));
        water.setWaterColor(ColorRGBA.Black.mult(0.15f));
        water.setWaterTransparency(0.001f);
        water.setMaxAmplitude(0.3f);
        water.setWaveScale(0.008f);
        water.setSpeed(0.5f);
        water.setShoreHardness(1.0f);
        water.setRefractionConstant(0.2f);
        water.setShininess(0.3f);
        water.setSunScale(1.0f);
        water.setColorExtinction(new Vector3f(10.0f, 20.0f, 30.0f));
        
        waterNode.setUserData("water", water);
        
		return waterNode;
	}

}
