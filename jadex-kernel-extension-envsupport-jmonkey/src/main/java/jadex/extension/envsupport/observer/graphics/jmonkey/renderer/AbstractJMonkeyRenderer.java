package jadex.extension.envsupport.observer.graphics.jmonkey.renderer;

import jadex.extension.envsupport.math.Vector3Double;
import jadex.extension.envsupport.observer.graphics.drawable3d.Animation;
import jadex.extension.envsupport.observer.graphics.drawable3d.DrawableCombiner3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.Object3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.Primitive3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.Sound3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.Text3d;
import jadex.extension.envsupport.observer.graphics.jmonkey.ViewportJMonkey;
import jadex.extension.envsupport.observer.gui.SObjectInspector;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SimpleValueFetcher;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;


import com.jme3.animation.AnimChannel;
import com.jme3.animation.LoopMode;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;



/**
 *
 */
public abstract class AbstractJMonkeyRenderer implements IJMonkeyRenderer
{
	// todo: comment me
	
	protected AssetManager assetManager;
	protected Geometry geo;
	protected String identifier;
	protected Vector3f rotation;
	protected Quaternion quatation;
	protected Vector3f positionlocal;
	protected Vector3f sizelocal;
	protected SimpleValueFetcher _fetcher;
	
	/**
	 *  Create a new AbstractJMonkeyRenderer.
	 */
	public AbstractJMonkeyRenderer()
	{
		_fetcher = null;
	}
	
	/**
	 * Returns null if Drawcondition == null
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
			// first create the Spatial
			spatial = draw(dc, primitive, obj, vp);

			Vector3Double rotationD = ((Vector3Double)dc.getBoundValue(obj,
					primitive.getRotation(), vp));
			rotation = new Vector3f(rotationD.getXAsFloat(),
					rotationD.getYAsFloat(), rotationD.getZAsFloat());

			Vector3Double sizelocalD = ((Vector3Double)dc.getBoundValue(obj,
					primitive.getSize(), vp));
			sizelocal = new Vector3f(sizelocalD.getXAsFloat(),
					sizelocalD.getYAsFloat(), sizelocalD.getZAsFloat());

			Vector3Double positionlocalD = ((Vector3Double)dc.getBoundValue(
					obj, primitive.getPosition(), vp));
			positionlocal = new Vector3f(positionlocalD.getXAsFloat(),
					positionlocalD.getYAsFloat(), positionlocalD.getZAsFloat());

			// Shadow, by default off
			spatial.setShadowMode(ShadowMode.Off);

			String shadow = primitive.getShadowtype();

			if(shadow.equals(Primitive3d.SHADOW_CAST))
			{
				spatial.setShadowMode(ShadowMode.Cast);
			}
			else if(shadow.equals(Primitive3d.SHADOW_RECEIVE))
			{
				spatial.setShadowMode(ShadowMode.Receive);
			}


			float[] angles = {rotation.x, rotation.y, rotation.z};
			quatation = spatial.getLocalRotation().fromAngles(angles);

			// Set the local size
			spatial.setLocalScale(sizelocal);

			// Set the local Translation
			spatial.setLocalTranslation(positionlocal);

			// Set the local Rotation
			spatial.setLocalRotation(quatation);


			// Set the Material
			if((spatial instanceof Geometry) && primitive.getType() != 6
					&& primitive.getType() != 8 && primitive.getType() != 9) // And
																				// not
																				// a
																				// Node
																				// ->
																				// Object3D
			{

				Material mat_tt = new Material(assetManager,
						"Common/MatDefs/Light/Lighting.j3md");
				Color c = (Color)dc
						.getBoundValue(obj, primitive.getColor(), vp);
				float alpha = ((float)c.getAlpha()) / 255;
				ColorRGBA color = new ColorRGBA(((float)c.getRed()) / 255,
						((float)c.getGreen()) / 255,
						((float)c.getBlue()) / 255, alpha);

				if(alpha < 1)
				{
					mat_tt.getAdditionalRenderState().setBlendMode(
							BlendMode.Alpha); // activate transparency
					spatial.setQueueBucket(Bucket.Translucent);
					spatial.setQueueBucket(Bucket.Transparent);
				}
				String texturepath = primitive.getTexturePath();

				if(!texturepath.equals(""))
				{
					mat_tt = new Material(assetManager,
							"Common/MatDefs/Misc/Unshaded.j3md");

					Texture tex_ml = assetManager.loadTexture(texturepath);
					mat_tt.setColor("Color", color);
					mat_tt.setTexture("ColorMap", tex_ml);


				}
				else
				{
					mat_tt.setColor("Diffuse", color);
					mat_tt.setColor("Ambient", color);
					mat_tt.setBoolean("UseMaterialColors", true);
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
			sp.setLocalTranslation(positionlocal);
			sp.setLocalRotation(quatation);
			
//			 Special Case 01: 3d-Text
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
			
//			 Special Case 03: Sound
			if(primitive.getType() == 10)
			{
//				System.out.println("spatial" + sp.toString());
				if(sp instanceof AudioNode)
				{
//					System.out.println("AUDIONODE!");
					AudioNode audionode = (AudioNode)sp;
					
					Sound3d sound = (Sound3d)primitive;
					
					IParsedExpression soundcondition = sound.getCond();
					boolean soundActive = soundcondition==null;
					if(!soundActive)
					{
						_fetcher.setValue("$object", obj);
						_fetcher.setValue("$perspective", vp.getPerspective());
						soundActive = ((Boolean)soundcondition.getValue(_fetcher)).booleanValue();
					}
					
					if(soundActive)
					{
						audionode.play();
//						System.out.println("PLAY!");
					}
					else
					{
						audionode.stop();
					}
				}
			}
			
			// Special Case 02: Animation Updates
			if(primitive.getType()==6)
			{
				Object animation = sp.getUserData("Animation");
				if(animation instanceof Boolean && ((Boolean)animation).booleanValue())
				{
					HashMap<String, AnimChannel> anichannels = vp.getAnimChannels();
					
					ArrayList<Animation> animations = ((Object3d)primitive).getAnimations();
					
					for(Animation a : animations)
					{
						IParsedExpression animationcondition = a.getAnimationCondition();
						boolean animActive = animationcondition==null;
						if(!animActive)
						{
							_fetcher.setValue("$object", obj);
							_fetcher.setValue("$perspective", vp.getPerspective());
							animActive = ((Boolean)animationcondition.getValue(_fetcher)).booleanValue();
						}
						
						if(animActive)
						{
							AnimChannel chan = anichannels.get(a.getChannel()+" "+ obj.hashCode());
							if(!a.getName().equals(chan.getAnimationName()))
							{
								
								try
								{
									chan.setAnim(a.getName());
								}
								catch(Exception e)
								{
//									System.out.println("Animation Existiert nicht");
								}
								
//								System.out.println("animation! " + a.getName());
							}
						}
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
