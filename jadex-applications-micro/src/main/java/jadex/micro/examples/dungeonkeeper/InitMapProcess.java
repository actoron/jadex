package jadex.micro.examples.dungeonkeeper;

import jadex.application.space.envsupport.environment.IEnvironmentSpace;
import jadex.application.space.envsupport.environment.ISpaceProcess;
import jadex.application.space.envsupport.environment.space2d.Space2D;
import jadex.application.space.envsupport.math.Vector2Double;
import jadex.application.space.envsupport.math.Vector2Int;
import jadex.commons.SUtil;
import jadex.commons.SimplePropertyObject;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.service.SServiceProvider;
import jadex.commons.service.clock.IClockService;
import jadex.commons.service.library.ILibraryService;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 *  Environment process for creating wastes.
 */
public class InitMapProcess extends SimplePropertyObject implements ISpaceProcess
{
	public static final String IMPENETRABLE_ROCK = "impenetrable_rock";
	public static final String ROCK = "rock";
	public static final String REINFORCED_WALL = "reinforced_wall";
	public static final String GOLD = "gold";
	public static final String GEMS = "gems";

	public static final String DIRT_PATH = "dirt_path";
	public static final String CLAIMED_PATH = "claimed_path";
	public static final String WATER = "water";
	public static final String LAVA = "lava";
	
	public static final String DUNGEONHEART = "dungeonheart";
	public static final String TREASURY = "treasury";
	public static final String HATCHERY = "hatchery";
	public static final String LAIR = "lair";
	
	public static Map imagenames;
	
	static
	{
		imagenames = new HashMap();
//		images.put("Oa", "?");
		
		imagenames.put("Ob", IMPENETRABLE_ROCK);
		imagenames.put("Oc", ROCK);
		imagenames.put("1B", REINFORCED_WALL);
		imagenames.put("Og", GOLD);
		imagenames.put("Oh", GEMS);
		
		imagenames.put("Od", DIRT_PATH);
		imagenames.put("1A", CLAIMED_PATH);
		imagenames.put("Oe", WATER);
		imagenames.put("Of", LAVA);
		
		imagenames.put("1G", DUNGEONHEART);
		imagenames.put("1C", TREASURY);
		imagenames.put("1F", HATCHERY);
		imagenames.put("1D", LAIR);
	}
	
	//-------- attributes --------
	
	/** The last tick. */
	protected double lasttick;
	
	//-------- ISpaceProcess interface --------
	
	/**
	 *  This method will be executed by the object before the process gets added
	 *  to the execution queue.
	 *  @param clock	The clock.
	 *  @param space	The space this process is running in.
	 */
	public void start(IClockService clock, final IEnvironmentSpace space)
	{		
		// Initialize the field.
		
		SServiceProvider.getService(space.getContext().getServiceProvider(), ILibraryService.class)
			.addResultListener(new DefaultResultListener()
		{
			
			public void resultAvailable(Object source, Object result)
			{
				try
				{
					final Space2D grid = (Space2D)space;
					ILibraryService ls = (ILibraryService)result;
					String mapfile = (String)getProperty("mapfile");
					InputStream is = SUtil.getResource(mapfile, ls.getClassLoader());
					BufferedReader br = new BufferedReader(new InputStreamReader(is));
			
					// dis.available() returns 0 if the file does not have more lines.
					while(br.ready()) 
					{
				        String data = br.readLine();
				        
				        if("MAP".equals(data))
				        {
				        	String size = br.readLine();
				        	int del = size.indexOf("X");
				        	String xstr = size.substring(0, del-1);
				        	String ystr = size.substring(del+1);
				        	int sizex = Integer.parseInt(xstr.trim())-2;
				        	int sizey = Integer.parseInt(ystr.trim())-2;
				        	
				    		grid.setAreaSize(new Vector2Int(sizex, sizey));
				        
				    		// Now init the field
				    		String line = br.readLine();
				    		for(int y=0; y<sizey; y++)
			    			{
				    			line = br.readLine();
			    				for(int x=0; x<sizex; x++)
			    				{
			    					String key = line.substring(x*2+2, x*2+4);
			    					String type = (String)imagenames.get(key);
			    					Map props = new HashMap();
			    					props.put("type", type);
			    					props.put(Space2D.PROPERTY_POSITION, new Vector2Int(x, y));
			    					grid.createSpaceObject("field", props, null);
	//			    					System.out.println(x+" "+y+" "+type);
			    				}
			    			}
				        }
				        
				        if("CREATURES".equals(data))
				        {
				        	int cnt = Integer.parseInt(br.readLine().trim());
	//				        	cnt = 1;
				        	for(int i=0; i<cnt; i++)
				        	{
				        		StringTokenizer stok = new StringTokenizer(br.readLine());
				        		while(stok.hasMoreTokens())
				        		{
				        			String type = stok.nextToken().toLowerCase();
				        			int x = Integer.parseInt(stok.nextToken());
				        			int y = Integer.parseInt(stok.nextToken());
				        			/*String level =*/ stok.nextToken();
				        			/*String owner =*/ stok.nextToken();
				        			
				        			HashMap props = new HashMap();
				        			props.put("type", type);
				        			props.put(Space2D.PROPERTY_POSITION, new Vector2Double(x, y));
				        			// todo: level, owner
				        			
				        			grid.createSpaceObject(type, props, null);
				        		}
				        	}
				        }
					}
					br.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
					
				space.removeSpaceProcess(getProperty(ISpaceProcess.ID));
			}
		});
			
	
//		System.out.println("Init process started.");
	}
	
	/**
	 *  This method will be executed by the object before the process is removed
	 *  from the execution queue.
	 *  @param clock	The clock.
	 *  @param space	The space this process is running in.
	 */
	public void shutdown(IEnvironmentSpace space)
	{
//		System.out.println("create waste process shutdowned.");
	}

	/**
	 *  Executes the environment process
	 *  @param clock	The clock.
	 *  @param space	The space this process is running in.
	 */
	public void execute(IClockService clock, IEnvironmentSpace space)
	{
		System.out.println("process called: "+space);
	}
}
