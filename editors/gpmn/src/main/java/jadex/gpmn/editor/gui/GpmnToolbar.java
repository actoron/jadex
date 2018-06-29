package jadex.gpmn.editor.gui;

import java.awt.Color;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;

import com.mxgraph.util.mxUtils;

import jadex.gpmn.editor.GpmnEditor;

/**
 *  Toolbar implementing BPMN editing tools.
 *
 */
public class GpmnToolbar extends AbstractEditingToolbar
{
	/** The icon size. */
	protected int iconsize;
	
	/**
	 *  Creates a new tool bar for editing tools.
	 *  
	 *  @param modelcontainer The model container.
	 */
	public GpmnToolbar(int iconsize, ModelContainer modelcontainer)
	{
		super(modelcontainer);
		this.iconsize = iconsize;
		
		addBpmnTools();
	}
	
	/**
	 *  Resets the icon size.
	 *  
	 *  @param iconsize The new icon size.
	 */
	public void setIconSize(int iconsize)
	{
		this.iconsize = iconsize;
		removeAll();
		addBpmnTools();
		getParent().validate();
	}
	
	/**
	 *  Adds the tools to the toolbar.
	 */
	protected void addBpmnTools()
	{
		long ts = System.currentTimeMillis();
		
		ImageProvider imgprovider = modelcontainer.getImageProvider();
		
		List<IconGenerationTask> tasks = getTaskList(imgprovider, iconsize);
		
		final IconGenerationTask[] todo = tasks.toArray(new IconGenerationTask[tasks.size()]);
		
		int threads = Runtime.getRuntime().availableProcessors() + 1;
		
		final CyclicBarrier barrier = new CyclicBarrier(threads + 1);
		final int[] count = new int[1];
		count[0] = 0;
		for (int i = 0; i < threads; ++i)
		{
			Thread t = new Thread(new Runnable()
			{
				public void run()
				{
					boolean working = false;
					do
					{
						int target = 0;
						synchronized (count)
						{
							target = count[0];
							count[0] = count[0] + 1;
						}
						
						working = target < todo.length;
						
						if (working)
						{
							todo[target].run();
						}
					} 
					while (working);
					
					try
					{
						barrier.await();
					}
					catch (Exception e)
					{
					}
				}
			});
			t.start();
		}
		
		try
		{
			barrier.await();
		}
		catch (Exception e)
		{
		}
		
		/*for (int i = 0; i < todo.length; ++i)
		{
			todo[i].run();
		}*/
		
		for (int i = 0; i < todo.length; ++i)
		{
			if (todo[i].isSeparator())
			{
				addSeparator(todo[i].getRow());
			}
			
			addTool(todo[i].getRow(), todo[i].getMode(), todo[i].getIconSet(), todo[i].getTooltip());
		}
		
		toolgroup.setSelected(toolgroup.getElements().nextElement().getModel(), true);
		
		Logger.getLogger(GpmnEditor.APP_NAME).log(Level.INFO, "Icon generation time: " + String.valueOf(System.currentTimeMillis() - ts));
		//System.out.println("Icon generation time: " + (System.currentTimeMillis() - ts));
	}
	
	public static List<IconGenerationTask> getTaskList(ImageProvider imgprovider, int iconsize)
	{
		List<IconGenerationTask> tasks = new ArrayList<IconGenerationTask>();
		
		int row = 0;
		
		String sym = "selectsym";
		tasks.add(new IconGenerationTask(imgprovider, false, row, ModelContainer.SELECT_MODE, ImageProvider.SHAPE_ROUNDED_RECTANGLE, ImageProvider.THIN_FRAME_TYPE, sym, mxUtils.parseColor(GuiConstants.SELECT_COLOR),
				"Select", iconsize));
		
		sym = "cpsym";
		tasks.add(new IconGenerationTask(imgprovider, false, row, ModelContainer.CONTROL_POINT_MODE, ImageProvider.SHAPE_ROUNDED_RECTANGLE, ImageProvider.THIN_FRAME_TYPE, sym, mxUtils.parseColor(GuiConstants.CONTROL_POINT_COLOR),
				"Add Control Point", iconsize));
		
		sym = "A";
		tasks.add(new IconGenerationTask(imgprovider, true, row, ModelContainer.ACHIEVE_GOAL_MODE, ImageProvider.SHAPE_ELLIPSE, ImageProvider.THIN_FRAME_TYPE, sym, mxUtils.parseColor(GuiConstants.ACHIEVE_GOAL_COLOR),
				"Add Achieve Goal", iconsize));
		
		sym = "P";
		tasks.add(new IconGenerationTask(imgprovider, false, row, ModelContainer.PERFORM_GOAL_MODE, ImageProvider.SHAPE_ELLIPSE, ImageProvider.THIN_FRAME_TYPE, sym, mxUtils.parseColor(GuiConstants.PERFORM_GOAL_COLOR),
				"Add Perform Goal", iconsize));
		
		sym = "M";
		tasks.add(new IconGenerationTask(imgprovider, false, row, ModelContainer.MAINTAIN_GOAL_MODE, ImageProvider.SHAPE_ELLIPSE, ImageProvider.THIN_FRAME_TYPE, sym, mxUtils.parseColor(GuiConstants.MAINTAIN_GOAL_COLOR),
				"Add Maintain Goal", iconsize));
		
		sym = "Q";
		tasks.add(new IconGenerationTask(imgprovider, false, row, ModelContainer.QUERY_GOAL_MODE, ImageProvider.SHAPE_ELLIPSE, ImageProvider.THIN_FRAME_TYPE, sym, mxUtils.parseColor(GuiConstants.QUERY_GOAL_COLOR),
				"Add Query Goal", iconsize));
		
		sym = "P";
		tasks.add(new IconGenerationTask(imgprovider, true, row, ModelContainer.REF_PLAN_MODE, ImageProvider.SHAPE_ROUNDED_RECTANGLE, ImageProvider.THIN_FRAME_TYPE, sym, mxUtils.parseColor(GuiConstants.REF_PLAN_COLOR),
				"Add Plan", iconsize));
		
		sym = "A";
		tasks.add(new IconGenerationTask(imgprovider, false, row, ModelContainer.ACTIVATION_PLAN_MODE, ImageProvider.SHAPE_ROUNDED_RECTANGLE, ImageProvider.THIN_FRAME_TYPE, sym, mxUtils.parseColor(GuiConstants.ACTIVATION_PLAN_COLOR),
				"Add Activation Plan", iconsize));
		
		sym = "supp_edge";
		tasks.add(new IconGenerationTask(imgprovider, true, row, ModelContainer.SUPPRESSION_EDGE_MODE, ImageProvider.SHAPE_ROUNDED_RECTANGLE, ImageProvider.THIN_FRAME_TYPE, sym, mxUtils.parseColor(GuiConstants.SUPPRESSION_EDGE_COLOR),
				"Add Suppression Edge", iconsize));
		
		return tasks;
	}
	
	public static class IconGenerationTask implements Runnable
	{
		public ImageProvider imgprovider;
		public String mode;
		public int row;
		public boolean separator;
		
		public Shape baseshape;
		public int frametype;
		public String sym;
		public Color color;
		public String tooltip;
		public int iconsize;
		
		public Icon[] iconset;
		
		public IconGenerationTask(ImageProvider imgprovider, boolean separator, int row, String mode, Shape baseshape, String symbol, Color color, String tooltip, int iconsize)
		{
			this(imgprovider, separator, row, mode, baseshape, ImageProvider.THICK_FRAME_TYPE, symbol, color, tooltip, iconsize);
		}
		
		public IconGenerationTask(ImageProvider imgprovider, boolean separator, int row, String mode, Shape baseshape, int frametype, String symbol, Color color, String tooltip, int iconsize)
		{
			this.imgprovider = imgprovider;
			this.mode = mode;
			this.row = row;
			this.separator = separator;
			this.baseshape = baseshape;
			this.frametype = frametype;
			this.sym = symbol;
			this.tooltip = tooltip;
			this.color = color;
			this.iconsize = iconsize;
		}
		
		public void run()
		{
			iconset = imgprovider.generateGenericButtonIconSet(iconsize, baseshape, frametype, sym, color);
		}
		
		public Icon[] getIconSet()
		{
			return iconset;
		}
		
		public int getRow()
		{
			return row;
		}
		
		public String getMode()
		{
			return mode;
		}
		
		public boolean isSeparator()
		{
			return separator;
		}
		
		public String getTooltip()
		{
			return tooltip;
		}
	}
}
