package jadex.extension.envsupport.observer.graphics.jmonkey.renderer;

import jadex.extension.envsupport.math.Vector3Double;
import jadex.extension.envsupport.observer.graphics.drawable3d.DrawableCombiner3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.Primitive3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.Text3d;
import jadex.extension.envsupport.observer.graphics.jmonkey.ViewportJMonkey;
import jadex.extension.envsupport.observer.gui.SObjectInspector;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SimpleValueFetcher;

import java.awt.Color;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;



/**
 * @author 7willuwe
 *
 */

public abstract class AbstractJMonkeyRenderer implements IJMonkeyRenderer
{
	protected AssetManager assetManager;
	protected Geometry geo;
	protected String identifier;
	protected Vector3f rotation;
	protected Quaternion quatation;
	protected Vector3f positionlocal;
	protected Vector3f sizelocal;
	protected SimpleValueFetcher _fetcher;
	

	
	public AbstractJMonkeyRenderer()
	{
		_fetcher = null;
	}
	
	/**
	 * Returns null if Drawcondition == null
	 *
	 */
	public Spatial prepareAndExecuteDraw(DrawableCombiner3d dc, Primitive3d primitive, Object obj, ViewportJMonkey vp)
	{
		if(_fetcher==null)
		{
			_fetcher = new SimpleValueFetcher(vp.getPerspective().getObserverCenter().getSpace().getFetcher());		
		}
		Spatial spatial = null;
		identifier = "Type: "+ primitive.getType()+ " HCode " +primitive.hashCode();
		assetManager = vp.getAssetManager();
		IParsedExpression drawcondition = primitive.getDrawCondition();
		boolean draw = drawcondition==null;
		if(!draw)
		{
			_fetcher.setValue("$object", obj);
			_fetcher.setValue("$perspective", vp.getPerspective());
			draw = ((Boolean)drawcondition.getValue(_fetcher)).booleanValue();
		}
		
		if(draw)
		{	

			
				Vector3Double rotationD = ((Vector3Double)dc.getBoundValue(obj, primitive.getRotation(), vp));
				rotation = new Vector3f(rotationD.getXAsFloat(), rotationD.getYAsFloat(), rotationD.getZAsFloat());	
				
				Vector3Double  sizelocalD = ((Vector3Double)dc.getBoundValue(obj, primitive.getSize(), vp));	
				sizelocal =   new Vector3f(sizelocalD.getXAsFloat(), sizelocalD.getYAsFloat(), sizelocalD.getZAsFloat());	
				
				Vector3Double  positionlocalD = ((Vector3Double)dc.getBoundValue(obj, primitive.getPosition(), vp));	
				positionlocal =   new Vector3f(positionlocalD.getXAsFloat(), positionlocalD.getYAsFloat(), positionlocalD.getZAsFloat());	
				
				spatial = draw(dc, primitive, obj, vp);

				float[] angles = {rotation.x, rotation.y, rotation.z};
				quatation = spatial.getLocalRotation().fromAngles(angles);
				
				// Set the local size
				spatial.setLocalScale(sizelocal);
				
				// Set the local Translation
				spatial.setLocalTranslation(positionlocal);
				
				// Set the local Rotation
				spatial.setLocalRotation(quatation);
				
				// Set the Material
				if((spatial instanceof Geometry) && primitive.getType()!=6  && primitive.getType()!=8 &&  primitive.getType()!=9) // And not a Node -> Object3D
				{   
					Material mat_tt = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//					Material mat_tt = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");				
//					Material mat_t2 = new Material(assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");
//				mat_tt.getAdditionalRenderState().setWireframe(true);
					

					
					Color c = (Color)dc.getBoundValue(obj, primitive.getColor(), vp);
					float alpha= ((float)c.getAlpha())/255;
					ColorRGBA color = new ColorRGBA(((float)c.getRed())/255,((float)c.getGreen())/255,((float)c.getBlue())/255, alpha);
					mat_tt.setColor("Color",color);
					
					
//					mat_tt.setBoolean("UseMaterialColors",true);  // Set some parameters, e.g. blue.
//					mat_tt.setColor("Ambient", color);   // ... color of this object
//					mat_tt.setColor("Diffuse", color);   // ... color of light being reflected
			        
					if(alpha < 1)
					{
						 mat_tt.getAdditionalRenderState().setBlendMode(BlendMode.Alpha); // activate transparency
						 spatial.setQueueBucket(Bucket.Translucent);
					}
				
					if(!primitive.getTexturePath().equals(""))
					{
					
						Texture tex_ml = assetManager.loadTexture(primitive.getTexturePath());
						mat_tt.setTexture("ColorMap", tex_ml);
					}
				
					spatial.setMaterial(mat_tt);
				}
		}

		return spatial;
	}

	public Spatial prepareAndExecuteUpdate(DrawableCombiner3d dc, Primitive3d primitive, Object obj, ViewportJMonkey vp, Spatial sp)
	{
		IParsedExpression drawcondition = primitive.getDrawCondition();
		boolean draw = drawcondition==null;
		if(!draw)
		{
			_fetcher.setValue("$object", obj);
			_fetcher.setValue("$perspective", vp.getPerspective());
			draw = ((Boolean)drawcondition.getValue(_fetcher)).booleanValue();
		}
		
		if(draw)
		{
			Vector3Double rotationD = ((Vector3Double)dc.getBoundValue(obj, primitive.getRotation(), vp));
			rotation = new Vector3f(rotationD.getXAsFloat(), rotationD.getYAsFloat(), rotationD.getZAsFloat());	
			
			Vector3Double  sizelocalD = ((Vector3Double)dc.getBoundValue(obj, primitive.getSize(), vp));	
			sizelocal =   new Vector3f(sizelocalD.getXAsFloat(), sizelocalD.getYAsFloat(), sizelocalD.getZAsFloat());	
			
			Vector3Double  positionlocalD = ((Vector3Double)dc.getBoundValue(obj, primitive.getPosition(), vp));	
			positionlocal =   new Vector3f(positionlocalD.getXAsFloat(), positionlocalD.getYAsFloat(), positionlocalD.getZAsFloat());	


			float[] angles = {rotation.x, rotation.y, rotation.z};
			quatation = sp.getLocalRotation().fromAngles(angles);
			
			sp.setLocalScale(sizelocal);
			//TODO: das kann nicht gut sein oder?
			sp.setLocalTranslation(positionlocal.mult(2));
			sp.setLocalRotation(quatation);
			
//			 Special Case: 3d-Text
			if(primitive.getType() == 7)
			{
				if(sp instanceof Node)
				{
					Node textnode = (Node)sp;
					Spatial textspatial = textnode.getChild(0);
					if(textspatial instanceof BitmapText)
					{
						BitmapText txt=(BitmapText) textspatial;
						
						Text3d textP = (Text3d)primitive;
						// text = (String)((Text3d)primitive).getText();
						String text = Text3d.getReplacedText(dc, obj, textP.getText(), vp);
						
//						String text = ((String)dc.getBoundValue(obj, ((Text3d)primitive).getText(), vp));
//						 if (text==null)
//						 {
//							text= ((Text3d)primitive).getText();
//						 }
						
						txt.setText(text);
					}

				}
					
			}
		}
		else
		{
			sp.removeFromParent();
		}
		

		return sp;
	}
	
}
