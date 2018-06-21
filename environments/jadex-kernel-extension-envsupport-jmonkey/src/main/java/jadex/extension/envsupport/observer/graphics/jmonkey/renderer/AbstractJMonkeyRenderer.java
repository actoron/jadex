package jadex.extension.envsupport.observer.graphics.jmonkey.renderer;

import java.awt.Color;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;

import com.jme3.animation.AnimChannel;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioNode.Status;
import com.jme3.font.BitmapText;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.jme3.scene.control.LightControl;
import com.jme3.texture.Texture;

import jadex.extension.envsupport.environment.SpaceObject;
import jadex.extension.envsupport.math.Vector3Double;
import jadex.extension.envsupport.observer.graphics.drawable3d.DrawableCombiner3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.Object3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.PointLight3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.Primitive3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.Sound3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.Text3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.special.Animation;
import jadex.extension.envsupport.observer.graphics.drawable3d.special.SpatialControl;
import jadex.extension.envsupport.observer.graphics.jmonkey.ViewportJMonkey;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SimpleValueFetcher;



/**
 *
 */
public abstract class AbstractJMonkeyRenderer implements IJMonkeyRenderer
{
	// todo: comment me
	
	protected AssetManager assetManager;
	protected Geometry geo;
	public String identifier;
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
	public Spatial prepareAndExecuteDraw(DrawableCombiner3d dc, Primitive3d primitive, SpaceObject sobj, ViewportJMonkey vp)
	{
		if(_fetcher==null)
		{
			_fetcher = new SimpleValueFetcher(vp.getPerspective().getObserverCenter().getSpace().getFetcher());		
		}
		Spatial spatial = null;
		
		if(sobj!=null)
		{
			identifier = "Type: "+ primitive.getType()+ " HCode " +primitive.hashCode() + " sobjid " + sobj.hashCode();
		}
		else
		{
			identifier = "Type: "+ primitive.getType() + " HCode " +primitive.hashCode() + " sobjid " + "null hashcode";
		}

		assetManager = vp.getAssetManager();
		IParsedExpression drawcondition = primitive.getDrawCondition();
		boolean draw = drawcondition==null;
		if(!draw)
		{
			_fetcher.setValue("$object", sobj);
			_fetcher.setValue("$perspective", vp.getPerspective());
			draw = ((Boolean)drawcondition.getValue(_fetcher)).booleanValue();
		}

		if(draw)
		{	
				Vector3Double rotationD = ((Vector3Double)dc.getBoundValue(sobj,
						primitive.getRotation(), vp));
				rotation = new Vector3f(rotationD.getXAsFloat(),
						rotationD.getYAsFloat(), rotationD.getZAsFloat());

				Vector3Double sizelocalD = ((Vector3Double)dc.getBoundValue(sobj,
						primitive.getSize(), vp));
				sizelocal = new Vector3f(sizelocalD.getXAsFloat(),
						sizelocalD.getYAsFloat(), sizelocalD.getZAsFloat());

				Vector3Double positionlocalD = ((Vector3Double)dc.getBoundValue(
						sobj, primitive.getPosition(), vp));
				positionlocal = new Vector3f(positionlocalD.getXAsFloat(),
						positionlocalD.getYAsFloat(), positionlocalD.getZAsFloat());
				
				
				/*
				 * Here we create the CONCRETE Spatial
				 */
				spatial = draw(dc, primitive, sobj, vp);

				float[] angles = {rotation.x, rotation.y, rotation.z};
				quatation = spatial.getLocalRotation().fromAngles(angles);

				// Set the local size
				spatial.setLocalScale(sizelocal);

				// Set the local Translation
				spatial.setLocalTranslation(positionlocal);

				// Set the local Rotation
				spatial.setLocalRotation(quatation);
				
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
				
				
				if(primitive.getControler() != null)
				{
					for(SpatialControl control :  primitive.getControler())
					{
						Control tmp;
						try
						{
							Constructor con = Class.forName(control.getClasspath(), true, Thread.currentThread().getContextClassLoader()).getConstructor(
									new Class[]{});
							tmp = (Control)con.newInstance(new Object[]{});
							spatial.addControl(tmp);
						}
						catch(Exception e)
						{
							e.printStackTrace();
							System.out.println("exception creating controler");
						}
						

						
					}
				}


				// Set the Material if it is not one of the "Specials"
				//TODO: evil hack!
				if((spatial instanceof Geometry) && primitive.getType() != 6
						&& primitive.getType() != 8 && primitive.getType() != 9 && primitive.getType() != Primitive3d.PRIMITIVE_TYPE_EFFECT) 
					
					
				{

					Material mat_tt = new Material(assetManager,
							"Common/MatDefs/Light/Lighting.j3md");
					Color c = (Color)dc
							.getBoundValue(sobj, primitive.getColor(), vp);
					float alpha = ((float)c.getAlpha()) / 255;
					ColorRGBA color = new ColorRGBA(((float)c.getRed()) / 255,
							((float)c.getGreen()) / 255,
							((float)c.getBlue()) / 255, alpha);

					if(alpha < 1)
					{
						mat_tt.getAdditionalRenderState().setBlendMode(
								BlendMode.Alpha); // activate transparency
//						spatial.setQueueBucket(Bucket.Translucent);
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
//					Material mat2 = mat_tt.clone();
					spatial.setMaterial(mat_tt);
					
					
					
					String matpath = ((String)dc.getBoundValue(sobj, ((Primitive3d)primitive).getMaterialPath(), vp));
						if(matpath==null || matpath.equals(""))
						{
							matpath = (String)((Primitive3d) primitive).getMaterialPath();
						}
						
						Material mat;
						
						if(!matpath.equals("")&&texturepath.equals(""))
						{
							if(vp.materials.containsKey(matpath))
							{
								mat = vp.materials.get(matpath);
							}
							else
							{
								try
								{
									Material tmp = assetManager.loadMaterial(matpath);
									vp.materials.put(matpath, tmp);
									mat = tmp;
								}
								catch(RuntimeException e)
								{
									throw e;
								}
								
							}


							spatial.setMaterial(mat);
						}
						
						
				}
				
				
				
				if(primitive.getType()== Primitive3d.PRIMITIVE_TYPE_POINTLIGHT)
				{
					
					PointLight  light;
					Color color; 
						float radius = (float)((PointLight3d) primitive).getRadius();
						
						color = (Color)dc.getBoundValue(sobj, primitive.getColor(), vp);
						
						light = new PointLight();
						ColorRGBA rgbacolor = new ColorRGBA(((float)color.getRed()) / 255,
								((float)color.getGreen()) / 255,
								((float)color.getBlue()) / 255, ((float)color.getAlpha() /255));
						
						light.setColor(rgbacolor);
						light.setRadius(radius*20);
						
						light.setPosition(new Vector3f(spatial.getLocalTranslation().clone().add(0f,1f,0f)));
	
//						light.setRadius(100);
						
//						light.setPosition(new Vector3f(250, 10, 250));
						
						
						
						LightControl lightControl = new LightControl(light);
						spatial.addControl(lightControl); // this spatial controls the position of this light.
						
						
						
						vp.addLight(light);
						
				}
				
//				 Special Case 03: Sound
				else if(primitive.getType() == Primitive3d.PRIMITIVE_TYPE_SOUND)
				{
					if(spatial instanceof AudioNode)
					{
						AudioNode audionode = (AudioNode)spatial;

						Sound3d sound = (Sound3d)primitive;
						
						IParsedExpression soundcondition = sound.getCond();
						boolean soundActive = soundcondition==null;
						if(!soundActive)
						{
							_fetcher.setValue("$object", sobj);
							_fetcher.setValue("$perspective", vp.getPerspective());
							soundActive = ((Boolean)soundcondition.getValue(_fetcher)).booleanValue();
						}
						
						if(soundActive)
						{
//							System.out.println("audionode: " + audionode.getName() );
								audionode.play();

						}
						
						

					}
				}
			
				
			}

		return spatial;
	}

	public Spatial prepareAndExecuteUpdate(DrawableCombiner3d dc, Primitive3d primitive, SpaceObject sobj, ViewportJMonkey vp, Spatial sp)
	{	
		IParsedExpression drawcondition = primitive.getDrawCondition();
		boolean draw = drawcondition==null;
		if(!draw)
		{
			_fetcher.setValue("$object", sobj);
			_fetcher.setValue("$perspective", vp.getPerspective());
			draw = ((Boolean)drawcondition.getValue(_fetcher)).booleanValue();
		}
		
		if(draw)
		{
			
			if(primitive.getControler() == null)
			{
		
			Vector3Double rotationD = ((Vector3Double)dc.getBoundValue(sobj, primitive.getRotation(), vp));
			rotation = new Vector3f(rotationD.getXAsFloat(), rotationD.getYAsFloat(), rotationD.getZAsFloat());	
			float[] angles = {rotation.x, rotation.y, rotation.z};
			quatation = sp.getLocalRotation().fromAngles(angles);
			
			sp.setLocalRotation(quatation);
			
			}
			
			Vector3Double  sizelocalD = ((Vector3Double)dc.getBoundValue(sobj, primitive.getSize(), vp));	
			sizelocal =   new Vector3f(sizelocalD.getXAsFloat(), sizelocalD.getYAsFloat(), sizelocalD.getZAsFloat());	
			
			Vector3Double  positionlocalD = ((Vector3Double)dc.getBoundValue(sobj, primitive.getPosition(), vp));	
			positionlocal =   new Vector3f(positionlocalD.getXAsFloat(), positionlocalD.getYAsFloat(), positionlocalD.getZAsFloat());	
			sp.setLocalTranslation(positionlocal);
			sp.setLocalScale(sizelocal);
			
			
//			 Special Case 01: 3d-Text
			if(primitive.getType() == Primitive3d.PRIMITIVE_TYPE_TEXT3D)
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
						String text = Text3d.getReplacedText(dc, sobj, textP.getText(), vp);
						
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
			if(primitive.getType() == Primitive3d.PRIMITIVE_TYPE_SOUND)
			{
				if(sp instanceof AudioNode)
				{
					AudioNode audionode = (AudioNode)sp;

					Sound3d sound = (Sound3d)primitive;
					
					IParsedExpression soundcondition = sound.getCond();
					boolean soundActive = soundcondition==null;
					if(!soundActive)
					{
						_fetcher.setValue("$object", sobj);
						_fetcher.setValue("$perspective", vp.getPerspective());
						soundActive = ((Boolean)soundcondition.getValue(_fetcher)).booleanValue();
					}
					
					if(soundActive)
					{
						Status audiostatus = audionode.getStatus();
						if(audiostatus==Status.Playing)
						{
							

						}
						else
						{
							Node parent = sp.getParent();
							sp.removeFromParent();
							sp = draw(dc, primitive, sobj, vp);
							parent.attachChild(sp);
							audionode = (AudioNode) sp;
							
							audionode.play();
						}
					}
					else
					{
						if(!(boolean)((Sound3d) primitive).isContinuosly())
						{
							audionode.stop();
						}

					}
				}
			}
			
			// Special Case 02: Animation Updates
			else if(primitive.getType()==Primitive3d.PRIMITIVE_TYPE_OBJECT3D)
			{
				Object animation = sp.getUserData("Animation");
				boolean hasReallyAnimations = false;
				if(((Object3d)primitive).getAnimations() != null && ((Object3d)primitive).getAnimations().size()>0){
					hasReallyAnimations = true;
				}
				
				if((animation instanceof Boolean && ((Boolean)animation).booleanValue())||hasReallyAnimations)
				{
					HashMap<String, AnimChannel> anichannels = vp.getAnimChannels();
					ArrayList<Animation> animations = ((Object3d)primitive).getAnimations();
					
					for(Animation a : animations)
					{
						IParsedExpression animationcondition = a.getAnimationCondition();
						boolean animActive = animationcondition==null;
						if(!animActive)
						{
							_fetcher.setValue("$object", sobj);
							_fetcher.setValue("$perspective", vp.getPerspective());
							animActive = ((Boolean)animationcondition.getValue(_fetcher)).booleanValue();
						}
						
						if(animActive)
						{
							AnimChannel chan = anichannels.get(a.getChannel()+" "+ sobj.hashCode());
							
							//TODO: Animation Loop
//							if(a.isLoop())
//							{
//								chan.setLoopMode(LoopMode.DontLoop);
//							}
//							else
//							{
//								chan.setLoopMode(LoopMode.Loop);
//							}
//							
							
							
							chan.setSpeed(a.getSpeed());
							
							if(!a.getName().equals(chan.getAnimationName()))
							{
								
								try
								{
									chan.setAnim(a.getName());
									//System.out.println(a.getName());
								}
								catch(Exception e)
								{
								}
								

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
