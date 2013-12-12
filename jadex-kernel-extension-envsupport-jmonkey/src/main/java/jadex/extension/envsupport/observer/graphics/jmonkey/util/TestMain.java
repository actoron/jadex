package jadex.extension.envsupport.observer.graphics.jmonkey.util;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.BatchNode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 * test
 * @author normenhansen
 */
public class TestMain extends SimpleApplication {
    
    BatchNode batch = new BatchNode("Batch");
    
    Material mat01;
    Material mat02;
    Material mat03;
    Material mat04;
    Material mat05;
    
//	/** Random number generator. */
//	protected Random	rndgen	= new Random();
    
        

    public static void main(String[] args) {
        TestMain app = new TestMain();
        app.start();
    }


    public void simpleInitApp() {
    	initLight();
    	mat01 = assetManager.loadMaterial("models/tilesets/dirt/Dirt.j3m");
    	mat02 = assetManager.loadMaterial("models/tilesets/claimed/Claimedwall.j3m");
    	mat03 = assetManager.loadMaterial("models/tilesets/Gold.j3m");
    	mat04 = assetManager.loadMaterial("models/tilesets/Rock.j3m");
    	mat05 = assetManager.loadMaterial("models/tilesets/Lava.j3m");
    	
    	Material material[] = {mat01, mat02, mat03, mat04, mat05};
//        mat.setColor("Color", ColorRGBA.Blue);

        
        for(int x = -10; x<10; x++)
        {
            for(int y = -10; y<10; y++)
            {
                for(int z = -10; z<10; z++)
                    {
                		Spatial complex = assetManager.loadModel("models/tilesets/dirt/Dirt_00000000.j3o");

                		complex.setLocalScale(0.1f);
                        complex.setLocalTranslation(x, y, z);
                        
//                        int rnd = rndgen.nextInt(5);
                        complex.setMaterial(material[2]);
                        complex.setName("Box"+x+y+z);
                        batch.attachChild(complex);
                        
                    }
            }
        }
        
        batch.batch();
        


       

        rootNode.attachChild(batch);


//        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(
//                assetManager, inputManager, audioRenderer, guiViewPort);
//        /** Create a new NiftyGUI object */
//        Nifty nifty = niftyDisplay.getNifty();
//        /** Read your XML and initialize your custom ScreenController */
//        nifty.fromXml("Interface/NiftyGui.xml", "start", new MyStartScreen(this));

//        guiViewPort.addProcessor(niftyDisplay);

        flyCam.setDragToRotate(true);
        
        
        
        
    }
    
    public void remove()
    {
    	
//        for(int x = -5; x<5; x++)
//        {
//            for(int y = -5; y<5; y++)
//            {
//                for(int z = -5; z<5; z++)
//                    {
//
//                        Spatial sp =  batch.getChild("Box"+x+y+z);
//                        if(sp!=null)
//                        {
//                            batch.detachChild(sp);
//                            sp.removeFromParent();
//                        }
//
//                        
//                        
//                    }
//            }
//        }
//        
//        batch.batch();
        
      for(int x = -8; x<8; x=x+1)
        {
            for(int y = -8; y<8; y=y+1)
            {
                for(int z = -8; z<8; z=z+1)
                    {
                	
                	Spatial sp =  batch.getChild("Box"+x+y+z);
                        
                        Node test = (Node) sp;
                        test.detachAllChildren();
                        batch.detachChild(sp);
                        sp.removeFromParent();
                        

                    }
            }
        }
      
      batch.batch();
      
    }


    boolean removed = false;
    int mtimer = 0;
    public void simpleUpdate(float tpf) {

        if(!removed&&mtimer>100)
        {
            remove();
            removed = true;
        }
        else if(!removed)
        {
            mtimer++;
        }
        
    }
    
    public void initLight()
    {
		DirectionalLight sun = new DirectionalLight();
		sun.setColor(ColorRGBA.White.mult(0.2f));
		sun.setDirection(new Vector3f(0, -1f, 0).normalizeLocal());
		
		
		DirectionalLight sun1 = new DirectionalLight();
		sun1.setColor(ColorRGBA.White.mult(0.2f));
		sun1.setDirection(new Vector3f(-.5f, -.5f, -.5f).normalizeLocal());
		
		DirectionalLight sun2 = new DirectionalLight();
		sun2.setColor(ColorRGBA.White.mult(0.2f));
		sun2.setDirection(new Vector3f(.5f, -.5f, .5f).normalizeLocal());
		
		DirectionalLight sun3= new DirectionalLight();
		sun3.setColor(ColorRGBA.White.mult(0.2f));
		sun3.setDirection(new Vector3f(-.1f, -.5f, -.5f).normalizeLocal());
		
		DirectionalLight sun4 = new DirectionalLight();
		sun4.setColor(ColorRGBA.White.mult(0.2f));
		sun4.setDirection(new Vector3f(.5f, -.5f, .1f).normalizeLocal());
		
		
//		rootNode.addLight(sun);
		rootNode.addLight(sun1);
		rootNode.addLight(sun2);
		rootNode.addLight(sun3);
		rootNode.addLight(sun4);
//		rootNode.addLight(sun5);

		AmbientLight al = new AmbientLight();
		al.setColor(ColorRGBA.White.mult(2.5f));
		rootNode.addLight(al);
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
