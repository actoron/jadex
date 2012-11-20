package jadex.simulation.analysis.common.util.workflowGraph;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MSequenceEdge;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.superClasses.service.view.session.subprocess.ASubProcessView;
import jadex.simulation.analysis.common.util.AConstants;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.shape.mxStencilShape;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxConnectionConstraint;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxGraphSelectionModel;
import com.mxgraph.view.mxStylesheet;

/**
 * JGraph panel. Adds all Tasks, gateways and Events of a given Jadex Workflow
 * Soft the elements
 * @author 5Haubeck
 *
 */
public class GraphPanel extends mxGraphComponent
{
	final protected Map<MActivity, Object> vertex = new HashMap<MActivity, Object>();
	final protected Map<MSequenceEdge, Object> edges = new HashMap<MSequenceEdge, Object>();
	
	public GraphPanel(final ASubProcessView parent, IExternalAccess exta)
	{
		super(new mxGraph());
		initBpmnElements(getGraph());
		initWorkflow(exta);
		// initTestWorkflow();
		initListener(parent);
		setConnectable(false);
		setDragEnabled(false);
		graph.setCellsBendable(false);
		graph.setCellsCloneable(false);
		graph.setCellsDeletable(false);
		graph.setCellsDisconnectable(false);
		graph.setCellsEditable(false);
		graph.setCellsLocked(true);
		graph.setCellsMovable(false);
		graph.setCellsResizable(false);
		graph.setCellsSelectable(true);
		graph.setConnectableEdges(false);
		graph.setDropEnabled(false);

		getViewport().setOpaque(true);
		getViewport().setBackground(Color.WHITE);
		setPreferredSize(new Dimension(900, 300));
	}

	private void initListener(final ASubProcessView parent)
	{
		mxGraphSelectionModel model = graph.getSelectionModel();
		model.setSingleSelection(true);
		model.addListener(mxEvent.CHANGE, new mxIEventListener()
		{
			@Override
			public void invoke(Object sender, mxEventObject evt)
				{
					if (sender instanceof mxGraphSelectionModel)
					{
						JPanel property = parent.getPropertyPanel();
						if (((mxGraphSelectionModel) sender).getCells().length == 0)
						{
							property.removeAll();
							property.add( parent.getSessionProperties(), new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
						}
						for (Object cell : ((mxGraphSelectionModel) sender).getCells())
						{
							if (cell instanceof mxCell)
							{
								mxCell theCell = (mxCell) cell;
								ActivityUserObject userObj = (ActivityUserObject) theCell.getValue();

								if (userObj.getActivity().getActivityType().equals(MBpmnModel.TASK))
								{
									property.removeAll();
									property.add(parent.getTaskView(userObj.getActivity().getName()), new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH,
											new Insets(1, 1, 1, 1), 0, 0));
								}

							}
						}
						property.revalidate();
						property.repaint();
					}

				}
		});

	}

	public void initWorkflow(IExternalAccess exta)
	{
		exta.scheduleStep(new IComponentStep<Void>()
		{
			@Override
			public IFuture<Void> execute(IInternalAccess ia)
			{

				Map<String, MActivity> allActivities = ((BpmnInterpreter) ia).getModelElement().getAllActivities();
				graph.getModel().beginUpdate();

				Object parent = graph.getDefaultParent();

				// System.out.println(allActivities);
				for (final MActivity activity : allActivities.values())
				{
					// System.out.println(activity);
					ActivityUserObject userObj = new ActivityUserObject(activity);
					int counter = 0;
					int gapx = 100;
					int gapy = 40;
					Object v = null;

					if (activity.getActivityType().equals(MBpmnModel.TASK))
					{
						v = graph.insertVertex(parent, null, userObj, counter * gapx, counter * gapy, 150,
								50, "BPMN - Task");
					}
					else if (activity.getActivityType().equals(MBpmnModel.GATEWAY_PARALLEL))
					{
						v = graph.insertVertex(parent, null, userObj, counter * gapx, counter * gapy, 50,
								30, "BPMN - Gateway-Parallel-AND");
					}
					else if (activity.getActivityType().equals(MBpmnModel.GATEWAY_DATABASED_INCLUSIVE))
					{
						v = graph.insertVertex(parent, null, userObj, counter * gapx, counter * gapy, 50,
								30, "BPMN - Gateway-Inclusive-OR");
					}
					else if (activity.getActivityType().equals(MBpmnModel.GATEWAY_DATABASED_EXCLUSIVE))
					{
						v = graph.insertVertex(parent, null, userObj, counter * gapx, counter * gapy, 50,
								30, "BPMN - Gateway-Exclusive-XOR-Data-Based");
					}
					else if (activity.getActivityType().contains("EventStart"))
					{
						v = graph.insertVertex(parent, null, userObj, counter * gapx, counter * gapy, 30,
								30, "BPMN - Start-Event");
					}
					else if (activity.getActivityType().contains("EventEnd"))
					{
						v = graph.insertVertex(parent, null, userObj, counter * gapx, counter * gapy, 30,
								30, "BPMN - End-Event");
					}
					else if (activity.getActivityType().contains("EventIntermediate"))
					{
						v = graph.insertVertex(parent, null, userObj, counter * gapx, counter * gapy, 30,
								30, "BPMN - Intermediate-Event");
					}
					counter++;
					vertex.put(activity, v);

					// INCOMING
					List<MSequenceEdge> incedges = activity.getIncomingSequenceEdges();

					if (incedges != null)
					{
						for (MSequenceEdge edge : incedges)
						{
							if (!edges.containsKey(edge))
							{
								if (vertex.containsKey(edge.getSource()))
								{
									Object e = graph.insertEdge(parent, null, "", vertex.get(edge.getSource()), vertex.get(edge.getTarget()));
									edges.put(edge, e);
								}
							}
						}

					}

					// OUTGONING
					List<MSequenceEdge> outedges = activity.getOutgoingSequenceEdges();

					if (outedges != null)
					{
						for (MSequenceEdge edge : outedges)
						{
							if (!edges.containsKey(edge))
							{
								if (vertex.containsKey(edge.getTarget()))
								{
									Object e = graph.insertEdge(parent, null, "", vertex.get(edge.getSource()), vertex.get(edge.getTarget()));
									edges.put(edge, e);
								}
							}
						}
					}
				}
				;

				mxHierarchicalLayout layout = new mxHierarchicalLayout(graph, JLabel.WEST);
				layout.setFineTuning(true);
//				layout.setResizeParent(false);
//				layout.setMoveParent(false);
				// layout.setDisableEdgeStyle(false);
				// for (iterable_type iterable_element : iterable)
				// {
				//
				// }
				// layout.setOrthogonalEdge(edge, false);
				layout.execute(parent);

				graph.getModel().endUpdate();
				return IFuture.DONE;
			}
		});

	}

	public void initBpmnElements(mxGraph graph)
	{

		// mxStylesheet stylesheet = graph.getStylesheet();

		// Hashtable<String, Object> edgeStyle = (Hashtable<String, Object>) stylesheet.getDefaultEdgeStyle();
		// edgeStyle.put(mxConstants.STYLE_EDGE, mxConstants.EDGESTYLE_ORTHOGONAL);
		// edgeStyle.put(mxConstants.STYLE_ROUTING_CENTER_X, 0.5);
		// edgeStyle.put(mxConstants.STYLE_ROUTING_CENTER_Y, 0.5);
		// stylesheet.setDefaultEdgeStyle(edgeStyle);

		// Reads the BPMN shapes in the shapes folder (given Names: BPMN - Gateway, BPMN - Task, ...)
		try
		{
			mxStylesheet stylesheet = graph.getStylesheet();

			String path = new File("..").getCanonicalPath()
							+ "/sodekovs-simulationcontrol/src/main/java/jadex/simulation/analysis/common/util/workflowGraph/shapes/";

			for (File f : new File(path).listFiles(
							new FilenameFilter()
					{
						public boolean accept(File dir,
								String name)
						{
							return name.toLowerCase().endsWith(
									".shape");
						}
					}))
			{
				String nodeXml = mxUtils.readFile(f.getAbsolutePath());

				int lessthanIndex = nodeXml.indexOf("<");
				nodeXml = nodeXml.substring(lessthanIndex);
				mxStencilShape newShape = new mxStencilShape(nodeXml);
				String name = newShape.getName();
				mxGraphics2DCanvas.putShape(name, newShape);

				
				if (name.contains("Gateway"))
				{
					Hashtable<String, Object> style = new Hashtable<String, Object>();
					style.put(mxConstants.STYLE_SHAPE, name);
					style.put(mxConstants.STYLE_PERIMETER, mxConstants.PERIMETER_RHOMBUS);
					stylesheet.putCellStyle(name, style);
				}
				else if (name.contains("Event"))
				{
					Hashtable<String, Object> style = new Hashtable<String, Object>();
					style.put(mxConstants.STYLE_SHAPE, name);
					style.put(mxConstants.STYLE_PERIMETER, mxConstants.PERIMETER_ELLIPSE);
					stylesheet.putCellStyle(name, style);
				}
				else if (name.contains("Task"))
				{
					Hashtable<String, Object> style = new Hashtable<String, Object>();
					Hashtable<String, Object> styleGreen = new Hashtable<String, Object>();
					Hashtable<String, Object> stylePink = new Hashtable<String, Object>();
					Hashtable<String, Object> styleRed = new Hashtable<String, Object>();
					style.put(mxConstants.STYLE_SHAPE, name);
					styleGreen.put(mxConstants.STYLE_SHAPE, name);
					stylePink.put(mxConstants.STYLE_SHAPE, name);
					styleRed.put(mxConstants.STYLE_SHAPE, name);
					styleGreen.put(mxConstants.STYLE_FILLCOLOR, mxUtils.getHexColorString(new Color(155, 187, 89)));
					stylePink.put(mxConstants.STYLE_FILLCOLOR, mxUtils.getHexColorString(new Color(128, 100, 162)));
					styleRed.put(mxConstants.STYLE_FILLCOLOR, mxUtils.getHexColorString(new Color(30, 240, 30)));
					stylesheet.putCellStyle(name, style);
					stylesheet.putCellStyle(name + "_run", styleGreen);
					stylesheet.putCellStyle(name + "_user", stylePink);
					stylesheet.putCellStyle(name + "_abbruch", styleRed);
				}
				// style.put(mxConstants.STYLE_EDGE, mxEdgeStyle.BOTTOM);

				

				// System.out.println(name);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void setTaskStatus(MActivity activity, String status)
	{
		graph.getModel().beginUpdate();
		mxCell cell = (mxCell) vertex.get(activity);
		if (status.equals(AConstants.TASK_LAEUFT))
		{
			graph.getModel().setStyle(cell, "BPMN - Task" + "_run");
		} else if (status.equals(AConstants.TASK_USER))
		{
			graph.getModel().setStyle(cell, "BPMN - Task" + "_user");
		} else if (status.equals(AConstants.TASK_ABBRUCH))
		{
			graph.getModel().setStyle(cell, "BPMN - Task" + "_abbruch");
		} else
		{
			graph.getModel().setStyle(cell, "BPMN - Task");
		}
		
		graph.getModel().endUpdate();
	}

	public static void main(String[] args)
	{
		JFrame frame = new JFrame("FrameDemo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 800);
		// GraphPanel panel = new GraphPanel(null, frame);
		// frame.getContentPane().add(panel);
		// frame.pack();
		// frame.setVisible(true);
	}
	private void initTestWorkflow()
	{
		graph.getModel().beginUpdate();

		mxConnectionConstraint left = new mxConnectionConstraint(new mxPoint(0.5, 0), true);
		mxConnectionConstraint right = new mxConnectionConstraint(new mxPoint(0.5, 1), true);

		Object parent = graph.getDefaultParent();

		Set<Object> tasks = new HashSet<Object>();

		Object s = graph.insertVertex(parent, null, "", 0, 0, 30,
				30, "BPMN - Start-Event");
		Object t1 = graph.insertVertex(parent, null, "Task1", 0, 0, 80,
				40, "BPMN - Task");
		tasks.add(t1);

		Object edge = graph.insertEdge(getParent(), null, "", s, t1);
		// graph.setConnectionConstraint(edge, s, true, right);
		// graph.setConnectionConstraint(edge, t1, false, left);

		Object g1 = graph.insertVertex(parent, null, "", 100, 20, 30,
				30, "BPMN - Gateway-Parallel-AND");
		edge = graph.insertEdge(getParent(), null, "", t1, g1);
		// graph.setConnectionConstraint(edge, t1, true, right);
		// graph.setConnectionConstraint(edge, g1, false, left);

		Object t2 = graph.insertVertex(parent, null, "Task2", 0, 0, 80,
				40, "BPMN - Task");
		edge = graph.insertEdge(getParent(), null, "", g1, t2);
		// graph.setConnectionConstraint(edge, g1, true, right);
		// graph.setConnectionConstraint(edge, t2, false, left);
		tasks.add(t2);

		Object t3 = graph.insertVertex(parent, null, "Task3", 0, 0, 80,
				40, "BPMN - Task");
		edge = graph.insertEdge(getParent(), null, "", g1, t3);
		// graph.setConnectionConstraint(edge, t1, true, right);
		// graph.setConnectionConstraint(edge, t3, false, left);
		tasks.add(t3);

		Object g2 = graph.insertVertex(parent, null, "", 300, 20, 30,
				30, "BPMN - Gateway-Parallel-AND");
		edge = graph.insertEdge(getParent(), null, "", t2, g2);
		// graph.setConnectionConstraint(edge, t2, true, right);
		// graph.setConnectionConstraint(edge, g2, false, left);

		edge = graph.insertEdge(getParent(), null, "", t3, g2);
		// graph.setConnectionConstraint(edge, t3, true, right);
		// graph.setConnectionConstraint(edge, g2, false, left);

		Object t4 = graph.insertVertex(parent, null, "Task4", 0, 0, 80,
				40, "BPMN - Task");
		edge = graph.insertEdge(getParent(), null, "", g2, t4);
		// graph.setConnectionConstraint(edge, g2, true, right);
		// graph.setConnectionConstraint(edge, t4, false, left);
		tasks.add(t4);

		edge = graph.insertEdge(getParent(), null, "", t4, t1);
		// graph.setConnectionConstraint(edge, t4, true, right);
		// graph.setConnectionConstraint(edge, t1, false, left);

		Object e = graph.insertVertex(parent, null, "", 400, 400, 30,
				30, "BPMN - End-Event");
		graph.insertEdge(getParent(), null, "", t4, e);
		// graph.setConnectionConstraint(edge, t4, true, right);
		// graph.setConnectionConstraint(edge, e, false, left);

		mxHierarchicalLayout layout = new mxHierarchicalLayout(graph, JLabel.WEST);
		// layout.setDisableEdgeStyle(false);
		layout.setOrthogonalEdge(edge, false);
		layout.execute(parent);

		// layout.setInterRankCellSpacing(200.0);
		// System.out.println("PES" + layout.getParallelEdgeSpacing());
		// System.out.println("IHS" + layout.getInterHierarchySpacing());
		// System.out.println("IRCS" + layout.getInterRankCellSpacing());

		graph.getModel().endUpdate();
	}

}
