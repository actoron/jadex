package jadex.bpmn.editor.gui;

import jadex.bpmn.editor.BpmnEditor;
import jadex.bpmn.editor.gui.stylesheets.BpmnStylesheetColor;

import java.awt.Color;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;

import com.mxgraph.util.mxUtils;

/**
 *  Toolbar implementing BPMN editing tools.
 *
 */
public class BpmnToolbar extends AbstractEditingToolbar
{
	/** The icon size. */
	protected int iconsize;
	
	/**
	 *  Creates a new tool bar for editing tools.
	 *  
	 *  @param modelcontainer The model container.
	 */
	public BpmnToolbar(int iconsize, ModelContainer modelcontainer)
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
		
		Logger.getLogger(BpmnEditor.APP_NAME).log(Level.INFO, "Icon generation time: " + String.valueOf(System.currentTimeMillis() - ts));
		//System.out.println("Icon generation time: " + (System.currentTimeMillis() - ts));
	}
	
	/*protected void addBpmnTools()
	{
		long ts = System.currentTimeMillis();
		
		ImageProvider imgprovider = new ImageProvider();
		int row = 0;
		
		String sym = "selectsym";
		addTool(row, ModelContainer.EDIT_MODE_SELECTION,
				imgprovider.generateGenericImageIconSet(ImageProvider.SHAPE_ROUNDED_RECTANGLE, ImageProvider.THIN_FRAME_TYPE, sym, mxUtils.parseColor(GuiConstants.SELECT_COLOR)),
				"Select");
		
		addSeparator(row);
		
		sym = "Pool";
		addTool(row, ModelContainer.EDIT_MODE_POOL,
				imgprovider.generateGenericImageIconSet(ImageProvider.SHAPE_RECTANGLE, ImageProvider.THIN_FRAME_TYPE, sym, mxUtils.parseColor(BpmnStylesheetColor.POOL_COLOR)),
				"Pool");
		
		
		sym = "Lane";
		addTool(row, ModelContainer.EDIT_MODE_LANE,
				imgprovider.generateGenericImageIconSet(ImageProvider.SHAPE_RECTANGLE, ImageProvider.THIN_FRAME_TYPE, sym, mxUtils.parseColor(BpmnStylesheetColor.LANE_COLOR)),
				"Lane");
		
		addSeparator(row);
		
		sym = "Task";
		addTool(row, ModelContainer.EDIT_MODE_TASK,
				imgprovider.generateGenericImageIconSet(ImageProvider.SHAPE_ROUNDED_RECTANGLE, ImageProvider.THIN_FRAME_TYPE, sym, mxUtils.parseColor(BpmnStylesheetColor.TASK_COLOR)),
				"Task");
		
		addSeparator(row);
		
		sym = "GW_X";
		addTool(row, ModelContainer.EDIT_MODE_GW_XOR,
				imgprovider.generateGenericImageIconSet(ImageProvider.SHAPE_RHOMBUS, sym, mxUtils.parseColor(BpmnStylesheetColor.GATEWAY_COLOR)),
				"XOR-Gateway");
		
		sym = "GW_+";
		addTool(row, ModelContainer.EDIT_MODE_GW_AND,
				imgprovider.generateGenericImageIconSet(ImageProvider.SHAPE_RHOMBUS, sym, mxUtils.parseColor(BpmnStylesheetColor.GATEWAY_COLOR)),
				"AND-Gateway");
		
		sym = "GW_O";
		addTool(row, ModelContainer.EDIT_MODE_GW_OR,
				imgprovider.generateGenericImageIconSet(ImageProvider.SHAPE_RHOMBUS, sym, mxUtils.parseColor(BpmnStylesheetColor.GATEWAY_COLOR)),
				"OR-Gateway");
		
		addSeparator(row);
		
		++row;
		
		sym = " ";
		addTool(row, ModelContainer.EDIT_MODE_EVENT_START_EMPTY,
				imgprovider.generateGenericImageIconSet(ImageProvider.SHAPE_ELLIPSE, ImageProvider.THIN_FRAME_TYPE, sym, mxUtils.parseColor(BpmnStylesheetColor.START_EVENT_COLOR)),
				"Empty Start Event");
		
		sym = "letter";
		addTool(row, ModelContainer.EDIT_MODE_EVENT_START_MESSAGE,
				imgprovider.generateGenericImageIconSet(ImageProvider.SHAPE_ELLIPSE, ImageProvider.THIN_FRAME_TYPE, sym, mxUtils.parseColor(BpmnStylesheetColor.START_EVENT_COLOR)),
				"Message Start Event");
		
		sym = "clock";
		addTool(row, ModelContainer.EDIT_MODE_EVENT_START_TIMER,
				imgprovider.generateGenericImageIconSet(ImageProvider.SHAPE_ELLIPSE, ImageProvider.THIN_FRAME_TYPE, sym, mxUtils.parseColor(BpmnStylesheetColor.START_EVENT_COLOR)),
				"Timer Start Event");
		
		sym = "page";
		addTool(row, ModelContainer.EDIT_MODE_EVENT_START_RULE,
				imgprovider.generateGenericImageIconSet(ImageProvider.SHAPE_ELLIPSE, ImageProvider.THIN_FRAME_TYPE, sym, mxUtils.parseColor(BpmnStylesheetColor.START_EVENT_COLOR)),
				"Rule Start Event");
		
		sym = "triangle";
		addTool(row, ModelContainer.EDIT_MODE_EVENT_START_SIGNAL,
				imgprovider.generateGenericImageIconSet(ImageProvider.SHAPE_ELLIPSE, ImageProvider.THIN_FRAME_TYPE, sym, mxUtils.parseColor(BpmnStylesheetColor.START_EVENT_COLOR)),
				"Signal Start Event");
		
		sym = "pentagon";
		addTool(row, ModelContainer.EDIT_MODE_EVENT_START_MULTIPLE,
				imgprovider.generateGenericImageIconSet(ImageProvider.SHAPE_ELLIPSE, ImageProvider.THIN_FRAME_TYPE, sym, mxUtils.parseColor(BpmnStylesheetColor.START_EVENT_COLOR)),
				"Multiple Start Event");
		
		addSeparator(row);
		
		sym = " ";
		addTool(row, ModelContainer.EDIT_MODE_EVENT_INTERMEDIATE_EMPTY,
				imgprovider.generateGenericImageIconSet(ImageProvider.SHAPE_ELLIPSE, ImageProvider.DOUBLE_FRAME_TYPE, sym, mxUtils.parseColor(BpmnStylesheetColor.INTERMEDIATE_EVENT_COLOR)),
				"Empty Intermediate Event");
		
		sym = "letter";
		addTool(row, ModelContainer.EDIT_MODE_EVENT_INTERMEDIATE_MESSAGE,
				imgprovider.generateGenericImageIconSet(ImageProvider.SHAPE_ELLIPSE, ImageProvider.DOUBLE_FRAME_TYPE, sym, mxUtils.parseColor(BpmnStylesheetColor.INTERMEDIATE_EVENT_COLOR)),
				"Message Intermediate Event");
		
		sym = "invletter";
		addTool(row, ModelContainer.EDIT_MODE_EVENT_INTERMEDIATE_MESSAGE_THROWING,
				imgprovider.generateGenericImageIconSet(ImageProvider.SHAPE_ELLIPSE, ImageProvider.DOUBLE_FRAME_TYPE, sym, mxUtils.parseColor(BpmnStylesheetColor.INTERMEDIATE_EVENT_COLOR)),
				"Throwing Message Intermediate Event");
		
		sym = "clock";
		addTool(row, ModelContainer.EDIT_MODE_EVENT_INTERMEDIATE_TIMER,
				imgprovider.generateGenericImageIconSet(ImageProvider.SHAPE_ELLIPSE, ImageProvider.DOUBLE_FRAME_TYPE, sym, mxUtils.parseColor(BpmnStylesheetColor.INTERMEDIATE_EVENT_COLOR)),
				"Timer Intermediate Event");
		
		sym = "page";
		addTool(row, ModelContainer.EDIT_MODE_EVENT_INTERMEDIATE_RULE,
				imgprovider.generateGenericImageIconSet(ImageProvider.SHAPE_ELLIPSE, ImageProvider.DOUBLE_FRAME_TYPE, sym, mxUtils.parseColor(BpmnStylesheetColor.INTERMEDIATE_EVENT_COLOR)),
				"Rule Intermediate Event");
		
		sym = "triangle";
		addTool(row, ModelContainer.EDIT_MODE_EVENT_INTERMEDIATE_SIGNAL,
				imgprovider.generateGenericImageIconSet(ImageProvider.SHAPE_ELLIPSE, ImageProvider.DOUBLE_FRAME_TYPE, sym, mxUtils.parseColor(BpmnStylesheetColor.INTERMEDIATE_EVENT_COLOR)),
				"Signal Intermediate Event");
		
		sym = "invtriangle";
		addTool(row, ModelContainer.EDIT_MODE_EVENT_INTERMEDIATE_SIGNAL_THROWING,
				imgprovider.generateGenericImageIconSet(ImageProvider.SHAPE_ELLIPSE, ImageProvider.DOUBLE_FRAME_TYPE, sym, mxUtils.parseColor(BpmnStylesheetColor.INTERMEDIATE_EVENT_COLOR)),
				"Throwing Signal Intermediate Event");
		
		sym = "pentagon";
		addTool(row, ModelContainer.EDIT_MODE_EVENT_INTERMEDIATE_MULTIPLE,
				imgprovider.generateGenericImageIconSet(ImageProvider.SHAPE_ELLIPSE, ImageProvider.DOUBLE_FRAME_TYPE, sym, mxUtils.parseColor(BpmnStylesheetColor.INTERMEDIATE_EVENT_COLOR)),
				"Multiple Intermediate Event");
		
		sym = "invpentagon";
		addTool(row, ModelContainer.EDIT_MODE_EVENT_INTERMEDIATE_MULTIPLE_THROWING,
				imgprovider.generateGenericImageIconSet(ImageProvider.SHAPE_ELLIPSE, ImageProvider.DOUBLE_FRAME_TYPE, sym, mxUtils.parseColor(BpmnStylesheetColor.INTERMEDIATE_EVENT_COLOR)),
				"Throwing Multiple Intermediate Event");
		
		addSeparator(row);
		
		sym = " ";
		addTool(row, ModelContainer.EDIT_MODE_EVENT_END_EMPTY,
				imgprovider.generateGenericImageIconSet(ImageProvider.SHAPE_ELLIPSE, sym, mxUtils.parseColor(BpmnStylesheetColor.END_EVENT_COLOR)),
				"Empty End Event");
		
		sym = "letter";
		addTool(row, ModelContainer.EDIT_MODE_EVENT_END_MESSAGE,
				imgprovider.generateGenericImageIconSet(ImageProvider.SHAPE_ELLIPSE, sym, mxUtils.parseColor(BpmnStylesheetColor.END_EVENT_COLOR)),
				"Message End Event");
		
		sym = "invletter";
		addTool(row, ModelContainer.EDIT_MODE_EVENT_END_MESSAGE_THROWING,
				imgprovider.generateGenericImageIconSet(ImageProvider.SHAPE_ELLIPSE, sym, mxUtils.parseColor(BpmnStylesheetColor.END_EVENT_COLOR)),
				"Throwing Message End Event");
		
		sym = "triangle";
		addTool(row, ModelContainer.EDIT_MODE_EVENT_END_SIGNAL,
				imgprovider.generateGenericImageIconSet(ImageProvider.SHAPE_ELLIPSE, sym, mxUtils.parseColor(BpmnStylesheetColor.END_EVENT_COLOR)),
				"Signal End Event");
		
		sym = "invtriangle";
		addTool(row, ModelContainer.EDIT_MODE_EVENT_END_SIGNAL_THROWING,
				imgprovider.generateGenericImageIconSet(ImageProvider.SHAPE_ELLIPSE, sym, mxUtils.parseColor(BpmnStylesheetColor.END_EVENT_COLOR)),
				"Throwing Signal End Event");
		
		toolgroup.setSelected(toolgroup.getElements().nextElement().getModel(), true);
		
		System.out.println("Icon generation time: " + (System.currentTimeMillis() - ts));
	}*/
	
	public static List<IconGenerationTask> getTaskList(ImageProvider imgprovider, int iconsize)
	{
		List<IconGenerationTask> tasks = new ArrayList<IconGenerationTask>();
		
		int row = 0;
		
		String sym = "selectsym";
		tasks.add(new IconGenerationTask(imgprovider, false, row, ModelContainer.EDIT_MODE_SELECTION, ImageProvider.SHAPE_ROUNDED_RECTANGLE, ImageProvider.THIN_FRAME_TYPE, sym, mxUtils.parseColor(GuiConstants.SELECT_COLOR),
				"Select", iconsize));
		
		sym = "Pool";
		tasks.add(new IconGenerationTask(imgprovider, true, row, ModelContainer.EDIT_MODE_POOL, ImageProvider.SHAPE_RECTANGLE, ImageProvider.THIN_FRAME_TYPE, sym, mxUtils.parseColor(BpmnStylesheetColor.POOL_COLOR),
				"Pool", iconsize));
		
		sym = "Lane";
		tasks.add(new IconGenerationTask(imgprovider, false, row, ModelContainer.EDIT_MODE_LANE, ImageProvider.SHAPE_RECTANGLE, ImageProvider.THIN_FRAME_TYPE, sym, mxUtils.parseColor(BpmnStylesheetColor.LANE_COLOR),
				"Lane", iconsize));
		
		sym = "Task";
		tasks.add(new IconGenerationTask(imgprovider, true, row, ModelContainer.EDIT_MODE_TASK, ImageProvider.SHAPE_ROUNDED_RECTANGLE, ImageProvider.THIN_FRAME_TYPE, sym, mxUtils.parseColor(BpmnStylesheetColor.TASK_COLOR),
				"Task", iconsize));
		
		sym = "ISP";
		tasks.add(new IconGenerationTask(imgprovider, false, row, ModelContainer.EDIT_MODE_SUBPROCESS, ImageProvider.SHAPE_ROUNDED_RECTANGLE, ImageProvider.THIN_FRAME_TYPE, sym, mxUtils.parseColor(BpmnStylesheetColor.SUBPROCESS_COLOR),
				"Internal Sub-Process", iconsize));
		
		sym = "ESP";
		tasks.add(new IconGenerationTask(imgprovider, false, row, ModelContainer.EDIT_MODE_EXTERNAL_SUBPROCESS, ImageProvider.SHAPE_ROUNDED_RECTANGLE, ImageProvider.THIN_FRAME_TYPE, sym, mxUtils.parseColor(BpmnStylesheetColor.SUBPROCESS_COLOR),
				"External Sub-Process", iconsize));
		
		sym = "GW_X";
		tasks.add(new IconGenerationTask(imgprovider, true, row, ModelContainer.EDIT_MODE_GW_XOR, ImageProvider.SHAPE_RHOMBUS, sym, mxUtils.parseColor(BpmnStylesheetColor.GATEWAY_COLOR),
				"XOR-Gateway", iconsize));
		
		sym = "GW_+";
		tasks.add(new IconGenerationTask(imgprovider, false, row, ModelContainer.EDIT_MODE_GW_AND, ImageProvider.SHAPE_RHOMBUS, sym, mxUtils.parseColor(BpmnStylesheetColor.GATEWAY_COLOR),
				"AND-Gateway", iconsize));
		
		sym = "GW_O";
		tasks.add(new IconGenerationTask(imgprovider, false, row, ModelContainer.EDIT_MODE_GW_OR, ImageProvider.SHAPE_RHOMBUS, sym, mxUtils.parseColor(BpmnStylesheetColor.GATEWAY_COLOR),
				"OR-Gateway", iconsize));
		
		++row;
		
		sym = " ";
		tasks.add(new IconGenerationTask(imgprovider, false, row, ModelContainer.EDIT_MODE_EVENT_START_EMPTY, ImageProvider.SHAPE_ELLIPSE, ImageProvider.THIN_FRAME_TYPE, sym, mxUtils.parseColor(BpmnStylesheetColor.START_EVENT_COLOR),
				"Empty Start Event", iconsize));
		
		sym = "letter";
		tasks.add(new IconGenerationTask(imgprovider, false, row, ModelContainer.EDIT_MODE_EVENT_START_MESSAGE, ImageProvider.SHAPE_ELLIPSE, ImageProvider.THIN_FRAME_TYPE, sym, mxUtils.parseColor(BpmnStylesheetColor.START_EVENT_COLOR),
				"Message Start Event", iconsize));
		
		sym = "clock";
		tasks.add(new IconGenerationTask(imgprovider, false, row, ModelContainer.EDIT_MODE_EVENT_START_TIMER, ImageProvider.SHAPE_ELLIPSE, ImageProvider.THIN_FRAME_TYPE, sym, mxUtils.parseColor(BpmnStylesheetColor.START_EVENT_COLOR),
				"Timer Start Event", iconsize));
		
		sym = "page";
		tasks.add(new IconGenerationTask(imgprovider, false, row, ModelContainer.EDIT_MODE_EVENT_START_RULE, ImageProvider.SHAPE_ELLIPSE, ImageProvider.THIN_FRAME_TYPE, sym, mxUtils.parseColor(BpmnStylesheetColor.START_EVENT_COLOR),
				"Rule Start Event", iconsize));
		
		sym = "triangle";
		tasks.add(new IconGenerationTask(imgprovider, false, row, ModelContainer.EDIT_MODE_EVENT_START_SIGNAL, ImageProvider.SHAPE_ELLIPSE, ImageProvider.THIN_FRAME_TYPE, sym, mxUtils.parseColor(BpmnStylesheetColor.START_EVENT_COLOR),
				"Signal Start Event", iconsize));
		
		sym = "pentagon";
		tasks.add(new IconGenerationTask(imgprovider, false, row, ModelContainer.EDIT_MODE_EVENT_START_MULTIPLE, ImageProvider.SHAPE_ELLIPSE, ImageProvider.THIN_FRAME_TYPE, sym, mxUtils.parseColor(BpmnStylesheetColor.START_EVENT_COLOR),
				"Multiple Start Event", iconsize));
		
		sym = " ";
		tasks.add(new IconGenerationTask(imgprovider, true , row, ModelContainer.EDIT_MODE_EVENT_INTERMEDIATE_EMPTY, ImageProvider.SHAPE_ELLIPSE, ImageProvider.DOUBLE_FRAME_TYPE, sym, mxUtils.parseColor(BpmnStylesheetColor.INTERMEDIATE_EVENT_COLOR),
				"Empty Intermediate Event", iconsize));
		
		sym = "letter";
		tasks.add(new IconGenerationTask(imgprovider, false, row, ModelContainer.EDIT_MODE_EVENT_INTERMEDIATE_MESSAGE, ImageProvider.SHAPE_ELLIPSE, ImageProvider.DOUBLE_FRAME_TYPE, sym, mxUtils.parseColor(BpmnStylesheetColor.INTERMEDIATE_EVENT_COLOR),
				"Message Intermediate Event", iconsize));
		
		sym = "invletter";
		tasks.add(new IconGenerationTask(imgprovider, false, row, ModelContainer.EDIT_MODE_EVENT_INTERMEDIATE_MESSAGE_THROWING, ImageProvider.SHAPE_ELLIPSE, ImageProvider.DOUBLE_FRAME_TYPE, sym, mxUtils.parseColor(BpmnStylesheetColor.INTERMEDIATE_EVENT_COLOR),
				"Throwing Message Intermediate Event", iconsize));
		
		sym = "clock";
		tasks.add(new IconGenerationTask(imgprovider, false, row, ModelContainer.EDIT_MODE_EVENT_INTERMEDIATE_TIMER, ImageProvider.SHAPE_ELLIPSE, ImageProvider.DOUBLE_FRAME_TYPE, sym, mxUtils.parseColor(BpmnStylesheetColor.INTERMEDIATE_EVENT_COLOR),
				"Timer Intermediate Event", iconsize));
		
		sym = "page";
		tasks.add(new IconGenerationTask(imgprovider, false, row, ModelContainer.EDIT_MODE_EVENT_INTERMEDIATE_RULE, ImageProvider.SHAPE_ELLIPSE, ImageProvider.DOUBLE_FRAME_TYPE, sym, mxUtils.parseColor(BpmnStylesheetColor.INTERMEDIATE_EVENT_COLOR),
				"Rule Intermediate Event", iconsize));
		
		sym = "triangle";
		tasks.add(new IconGenerationTask(imgprovider, false, row, ModelContainer.EDIT_MODE_EVENT_INTERMEDIATE_SIGNAL, ImageProvider.SHAPE_ELLIPSE, ImageProvider.DOUBLE_FRAME_TYPE, sym, mxUtils.parseColor(BpmnStylesheetColor.INTERMEDIATE_EVENT_COLOR),
				"Signal Intermediate Event", iconsize));
		
		sym = "invtriangle";
		tasks.add(new IconGenerationTask(imgprovider, false, row, ModelContainer.EDIT_MODE_EVENT_INTERMEDIATE_SIGNAL_THROWING, ImageProvider.SHAPE_ELLIPSE, ImageProvider.DOUBLE_FRAME_TYPE, sym, mxUtils.parseColor(BpmnStylesheetColor.INTERMEDIATE_EVENT_COLOR),
				"Throwing Signal Intermediate Event", iconsize));
		
		sym = "pentagon";
		tasks.add(new IconGenerationTask(imgprovider, false, row, ModelContainer.EDIT_MODE_EVENT_INTERMEDIATE_MULTIPLE, ImageProvider.SHAPE_ELLIPSE, ImageProvider.DOUBLE_FRAME_TYPE, sym, mxUtils.parseColor(BpmnStylesheetColor.INTERMEDIATE_EVENT_COLOR),
				"Multiple Intermediate Event", iconsize));
		
		sym = "invpentagon";
		tasks.add(new IconGenerationTask(imgprovider, false, row, ModelContainer.EDIT_MODE_EVENT_INTERMEDIATE_MULTIPLE_THROWING, ImageProvider.SHAPE_ELLIPSE, ImageProvider.DOUBLE_FRAME_TYPE, sym, mxUtils.parseColor(BpmnStylesheetColor.INTERMEDIATE_EVENT_COLOR),
				"Throwing Multiple Intermediate Event", iconsize));
		
		sym = " ";
		tasks.add(new IconGenerationTask(imgprovider, true, row, ModelContainer.EDIT_MODE_EVENT_END_EMPTY, ImageProvider.SHAPE_ELLIPSE, sym, mxUtils.parseColor(BpmnStylesheetColor.END_EVENT_COLOR),
				"Empty End Event", iconsize));
		
//		sym = "letter";
//		tasks.add(new IconGenerationTask(imgprovider, false, row, ModelContainer.EDIT_MODE_EVENT_END_MESSAGE, ImageProvider.SHAPE_ELLIPSE, sym, mxUtils.parseColor(BpmnStylesheetColor.END_EVENT_COLOR),
//				"Message End Event", iconsize));
		
		sym = "invletter";
		tasks.add(new IconGenerationTask(imgprovider, false, row, ModelContainer.EDIT_MODE_EVENT_END_MESSAGE_THROWING, ImageProvider.SHAPE_ELLIPSE, sym, mxUtils.parseColor(BpmnStylesheetColor.END_EVENT_COLOR),
				"Throwing Message End Event", iconsize));
		
//		sym = "triangle";
//		tasks.add(new IconGenerationTask(imgprovider, false, row, ModelContainer.EDIT_MODE_EVENT_END_SIGNAL, ImageProvider.SHAPE_ELLIPSE, sym, mxUtils.parseColor(BpmnStylesheetColor.END_EVENT_COLOR),
//				"Signal End Event", iconsize));
		
		sym = "invtriangle";
		tasks.add(new IconGenerationTask(imgprovider, false, row, ModelContainer.EDIT_MODE_EVENT_END_SIGNAL_THROWING, ImageProvider.SHAPE_ELLIPSE, sym, mxUtils.parseColor(BpmnStylesheetColor.END_EVENT_COLOR),
				"Throwing Signal End Event", iconsize));
		
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
