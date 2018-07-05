package jadex.bpmn.tools.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.shape.mxBasicShape;
import com.mxgraph.swing.handler.mxConnectionHandler;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxStylesheet;

import jadex.base.gui.CMSUpdateHandler;
import jadex.bpmn.editor.gui.BpmnGraph;
import jadex.bpmn.editor.gui.BpmnGraphComponent;
import jadex.bpmn.editor.gui.GuiConstants;
import jadex.bpmn.editor.gui.ModelContainer;
import jadex.bpmn.editor.gui.ZoomSlider;
import jadex.bpmn.editor.model.visual.BpmnVisualModelReader;
import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.editor.model.visual.VElement;
import jadex.bpmn.editor.model.visual.VExternalSubProcess;
import jadex.bpmn.editor.model.visual.VSubProcess;
import jadex.bpmn.features.IInternalBpmnComponentFeature;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.io.SBpmnModelReader;
import jadex.bpmn.tools.ProcessThreadInfo;
import jadex.bridge.BulkMonitoringEvent;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ImmediateComponentStep;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.cms.IComponentManagementService.CMSStatusEvent;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.commons.IBreakpointPanel;
import jadex.commons.IFilter;
import jadex.commons.SUtil;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.FutureTerminatedException;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.gui.JSplitPanel;
import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.commons.gui.future.SwingIntermediateResultListener;
import jadex.commons.gui.future.SwingResultListener;
import jadex.commons.gui.jtable.ResizeableTableHeader;
import jadex.commons.gui.jtable.TableSorter;
import jadex.commons.transformation.annotations.Classname;
import jadex.tools.debugger.micro.MicroAgentViewPanel;

/**
 *  Panel that shows a bpmn process visually.
 */
public class VisualProcessViewPanel extends JPanel
{
	/** Style class */
	public static Class<?> styleclass = BpmnStylesheetSelections.class;
	
	//------- attributes --------
	
	/** The process. */
	protected IExternalAccess access;
		
	/** The list model for the activations. */
	protected ProcessThreadModel ptmodel;

	/** The list model for the history. */
	protected HistoryModel	hmodel;
	
	/** The list for the activations. */
	protected JTable threads;
	
	/** The list for the history. */
	protected JTable history;
	
	/** The breakpoint panel. */
	protected IBreakpointPanel	bpp;

	/** The model container. */
	protected ModelContainer modelcontainer;
	
	/** The change listener. */
	protected ISubscriptionIntermediateFuture<IMonitoringEvent> sub;
	
	/** The map of currently active steps (thread id -> step id). */
	protected Map<String, String> threadstosteps;
	
	//------- constructors --------
	
	/**
	 *  Create an agenda panel.
	 */
	public VisualProcessViewPanel(final IExternalAccess access, IBreakpointPanel bpp, CMSUpdateHandler cmshandler) 
	{
		try
		{
			this.access = access;
			this.bpp	= bpp;
			
			this.ptmodel = new ProcessThreadModel();
			this.hmodel	= new HistoryModel();
			this.modelcontainer = new ModelContainer(null);
			this.threadstosteps = new HashMap<String, String>();
			
//			BpmnStylesheetSelections sheet = new BpmnStylesheetSelections();
			mxStylesheet sheet = (mxStylesheet) styleclass.newInstance();
			final BpmnGraph graph = new BpmnGraph(modelcontainer, sheet);
			graph.setCellsMovable(false);
			graph.setCellsResizable(false);
			graph.setCellsLocked(true);
			final BpmnVisualModelReader vreader = new BpmnVisualModelReader(graph)
			{
				public VActivity createActivity() 
				{
					return new VActivity(graph)
					{
						public String getStyle()
						{
							return getStyleHelper(super.getStyle(), getBpmnElement().getId());
						}
					};
				}
				
				public VSubProcess createSuboprocess()
				{
					return new VSubProcess(graph)
					{
						public String getStyle()
						{
							return getStyleHelper(super.getStyle(), getBpmnElement().getId());
						}
					};
				}
				
				public VExternalSubProcess createExternalSuboprocess()
				{
					return new VExternalSubProcess(graph)
					{
						public String getStyle()
						{
							return getStyleHelper(super.getStyle(), getBpmnElement().getId());
						}
					};
				}
				
				protected String getStyleHelper(String ret, String myid)
				{
					boolean w = false;
					boolean r = false;
					for(ProcessThreadInfo pti: ptmodel.getThreadInfos())
					{
						if(myid.equals(pti.getActId()))
						{
							if(pti.isWaiting())
							{
								w = true;
							}
							else
							{
								r = true;
								break;
							}
						}
					}
					
					if(r)
					{
						ret += "_ready";
					}
					else if(w)
					{
						ret += "_waiting";
					}
					return ret;
				}
			};

			modelcontainer.setGraph(graph);

			BpmnGraphComponent bpmncomp = new BpmnGraphComponent(graph)
			{
				// Do not allow connection drawing
				protected mxConnectionHandler createConnectionHandler()
				{
					return null;
				}
			};
			JPanel bpmnpan = new JPanel(new BorderLayout());
			bpmnpan.add(bpmncomp, BorderLayout.CENTER);
			
			ZoomSlider zs = new ZoomSlider(bpmncomp, modelcontainer);
//			zs.setMinimumSize(zs.getPreferredSize());
//			zs.setPreferredSize(zs.getPreferredSize());
			zs.setMaximumSize(zs.getPreferredSize());
			JPanel tp = new JPanel();
//			tp.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
			BoxLayout bl = new BoxLayout(tp, BoxLayout.LINE_AXIS);
			tp.setLayout(bl);
			tp.add(new JPanel(new GridLayout(1,1))); // necessary to show zoomslider correctly
			tp.add(zs);
			bpmnpan.add(tp, BorderLayout.SOUTH);
			graph.getView().setScale(GuiConstants.DEFAULT_ZOOM);
			
			final ListSelectionListener sellistener = new ListSelectionListener()
		    {
		        public void valueChanged(ListSelectionEvent e)
		        {
		        	int vrow = threads.getSelectedRow();
		        	if(vrow!=-1)
		        	{
		        		int row = threads.convertRowIndexToModel(vrow);
		        		ProcessThreadInfo pti = (ProcessThreadInfo)threads.getModel().getValueAt(row, -1);
		        		mxICell cell = (mxICell)graph.getModel().getRoot();
		        		VElement elem = graph.getVisualElementById(cell, pti.getActId());
		        		if(elem!=null)
		        		{
		        			graph.setEventsEnabled(false);
		        			graph.setSelectionCell(elem);
		        			graph.setEventsEnabled(true);
		        		}
		        	}
		        }
		    };
			
			bpmncomp.init(modelcontainer);
			
			// Not possible to use the selection listener because if selected a click on the selected element is not detected
//			modelcontainer.getGraph().getSelectionModel().addListener(mxEvent.CHANGE, new mxIEventListener()
//			{
//				public void invoke(Object sender, mxEventObject evt)
//				{
//				}
//			});
			
			modelcontainer.getGraphComponent().getGraphControl().addMouseListener(new MouseAdapter() 
			{
				public void mouseClicked(MouseEvent e) 
				{
//					System.out.println("clicked: "+e);
					Object cell = modelcontainer.getGraphComponent().getCellAt(e.getX(), e.getY());
					if(cell instanceof VElement)
					{
						VElement elem = (VElement)cell;
//						System.out.println("Cell: "+ve.getBpmnElement()); 
						
						// Determine the thread that should be executed
						if(elem!=null && elem.getBpmnElement()!=null)
						{
							String id = elem.getBpmnElement().getId();
							boolean set = false;
							List<Integer> sels = new ArrayList<Integer>();
							for(int row=0; row<threads.getModel().getRowCount() && !set; row++)
							{
								ProcessThreadInfo pti = (ProcessThreadInfo)threads.getModel().getValueAt(row, -1);
								if(pti.getActId()!=null && pti.getActId().equals(id))
								{
									sels.add(Integer.valueOf(row));
								}
							}
							
							if(sels.size()==1)
							{
								int sel = sels.get(0).intValue();
//								System.out.println("sel0: "+sel);
								threads.getSelectionModel().removeListSelectionListener(sellistener);
								threads.setRowSelectionInterval(sel, sel);
								threads.getSelectionModel().addListSelectionListener(sellistener);
							}
							else if(sels.size()>1)
							{
								int sel = -1;
								int curs = getSelectedThreadRow();
								for(int i=0; i<sels.size() && sel==-1; i++)
								{
									int nexts = sels.get(i).intValue();
									if(nexts==curs || curs==-1)
									{
										if(i+1<sels.size())
										{
											sel = sels.get(i+1);
										}
										else
										{
											sel = sels.get(i-1);
										}
									}
								}
								threads.getSelectionModel().removeListSelectionListener(sellistener);
								// Set the thread that should be executed in the table
								threads.setRowSelectionInterval(sel, sel);
								threads.getSelectionModel().addListSelectionListener(sellistener);
							}
							else
							{
								threads.getSelectionModel().removeListSelectionListener(sellistener);
								threads.clearSelection();
								threads.getSelectionModel().addListSelectionListener(sellistener);
							}
							
							// Step when double click on thread
							if(sels.size()>0 && e.getClickCount()==2 && getStepInfo()!=null)
							{
								doStep();
							}
							
							// Double click on element toggles breakpoint
							if(sels.size()==0 && e.getClickCount()==2 && elem.getBpmnElement() instanceof MActivity)
							{
//								toggleBreakPoint(((MActivity)elem.getBpmnElement()).getBreakpointId());
								toggleBreakPoint(((MActivity)elem.getBpmnElement()).getId());
							}
						}
					}
				}
			});
			
			modelcontainer.getGraphComponent().refresh();
			
			TableSorter sorter = new TableSorter(ptmodel);
			sorter.setSortingStatus(0, TableSorter.ASCENDING);
			this.threads = new JTable(sorter);
			ResizeableTableHeader header = new ResizeableTableHeader(threads.getColumnModel());
			header.setIncludeHeaderWidth(true);
	//		threads.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			threads.setTableHeader(header);
			threads.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			sorter.setTableHeader(header);
//			threads.getColumnModel().setColumnMargin(10);
		    threads.getSelectionModel().addListSelectionListener(sellistener);
		    
		    threads.addMouseListener(new MouseAdapter()
			{
		    	public void mouseClicked(MouseEvent e)
		    	{
		    		int[] sels = threads.getSelectedRows();
		    		 // Step when double click on thread in table
					if(sels.length>0 && e.getClickCount()==2 && getStepInfo()!=null)
					{
						doStep();
					}
		    	}
			});
		
	
			sorter = new TableSorter(hmodel);
			this.history = new JTable(sorter);
			header = new ResizeableTableHeader(history.getColumnModel());
			header.setIncludeHeaderWidth(true);
	//		history.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			history.setTableHeader(header);
			history.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			sorter.setTableHeader(header);
//			history.getColumnModel().setColumnMargin(10);
			
			final JCheckBox hon = new JCheckBox("Store History");
			hon.setSelected(true);
				
			// todo: initial/current state
			
			sub = access.subscribeToEvents(new IFilter<IMonitoringEvent>()
			{
				@Classname("eventfilter")
				public boolean filter(IMonitoringEvent ev)
				{
					return true;	
				}
			}, true, PublishEventLevel.FINE);
			sub.addResultListener(new SwingIntermediateResultListener<IMonitoringEvent>(new IntermediateDefaultResultListener<IMonitoringEvent>()
			{
				public void intermediateResultAvailable(IMonitoringEvent event)
				{
					if(event==null)
						return;
					
					// todo: hide decomposing bulk events
					if(event instanceof BulkMonitoringEvent)
					{
						BulkMonitoringEvent bev = (BulkMonitoringEvent)event;
						if(bev.getBulkEvents().length>0)
						{
							IMonitoringEvent[] events = bev.getBulkEvents();
							for(int i=0; i<events.length; i++)
							{
								intermediateResultAvailable(events[i]);
							}
						}
					}
					else if(event.getType().endsWith(IInternalBpmnComponentFeature.TYPE_THREAD))
					{
						ProcessThreadInfo pti = (ProcessThreadInfo)event.getProperty("details");
						if(event.getType().startsWith(IMonitoringEvent.EVENT_TYPE_CREATION))
						{
//							System.out.println("created thread: "+pti);
							ptmodel.addValue(pti);
						}
						else if(event.getType().startsWith(IMonitoringEvent.EVENT_TYPE_DISPOSAL))
						{
//							System.out.println("removed thread: "+pti);
							ptmodel.removeValue(pti);
//							threadinfos.remove(pti);
						}
						else if(event.getType().startsWith(IMonitoringEvent.EVENT_TYPE_MODIFICATION))
						{
//							System.out.println("changed thread: "+pti);
							ptmodel.updateValue(pti);
						}
					}
					else if(event.getType().endsWith(IInternalBpmnComponentFeature.TYPE_ACTIVITY))
					{
						if(event.getType().startsWith(IMonitoringEvent.EVENT_TYPE_DISPOSAL))
						{
							hmodel.addValue(0, (ProcessThreadInfo)event.getProperty("details"));
//							historyinfos.add(0, (ProcessThreadInfo)event.getProperty("details"));
						}
					}
					// 
					else if(event.getType().startsWith(IMonitoringEvent.EVENT_TYPE_CREATION) && event.getType().endsWith("step"))
					{
						Map<String, String> det = (Map<String, String>)event.getProperty("details");
						if(det!=null && det.containsKey("threadid"))
						{
							String threadid = det.get("threadid");
							String stepid = (String)det.get("Id");
							threadstosteps.put(threadid, stepid);
						}
					}
					else if(event.getType().startsWith(IMonitoringEvent.EVENT_TYPE_DISPOSAL) && event.getType().endsWith("step"))
					{
						Map<String, String> det = (Map<String, String>)event.getProperty("details");
						if(det!=null && det.containsKey("threadid"))
						{
							String threadid = det.get("threadid");
							threadstosteps.remove(threadid);
						}
					}
					
	//				System.out.println("ti: "+threadinfos.size()+" "+cce.getSourceName()+" "+cce.getSourceType()+" "+cce.getEventType());
					updateViews();
				}
				
				public void exceptionOccurred(Exception exception)
				{
					// Todo: why future terminated exception thrown?
					if(!(exception instanceof FutureTerminatedException))
					{
						super.exceptionOccurred(exception);
					}
				}
			}));
							
			JButton clear = new JButton("Clear");
			clear.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					hmodel.clear();
//					historyinfos.clear();
//					history.repaint();
				}
			});
			
			JPanel	procp	= new JPanel(new BorderLayout());
			procp.add(new JScrollPane(threads));
			procp.setBorder(BorderFactory.createTitledBorder("Processes"));
			
			JPanel	historyp	= new JPanel(new BorderLayout());
			historyp.add(new JScrollPane(history));
			historyp.setBorder(BorderFactory.createTitledBorder("History"));
			
			JPanel buts = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			buts.add(hon);
			buts.add(clear);
			historyp.add(buts, BorderLayout.SOUTH);

			JSplitPanel tmp2 = new JSplitPanel(JSplitPane.VERTICAL_SPLIT);
			tmp2.add(bpmnpan);
			tmp2.add(procp);
			tmp2.setDividerLocation(0.8);
			tmp2.setOneTouchExpandable(true);
			tmp2.setResizeWeight(1);
			
//			JSplitPane tmp = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
//			tmp.add(procp);
//			tmp.add(tmp2);
//			tmp.setDividerLocation(200); // Hack?!
			
			JSplitPanel	sp	= new JSplitPanel(JSplitPane.HORIZONTAL_SPLIT, tmp2, new MicroAgentViewPanel(access, null, true));
			sp.setDividerLocation(0.8);
			sp.setOneTouchExpandable(true);
			sp.setResizeWeight(1);
			
			setLayout(new BorderLayout());
			add(sp, BorderLayout.CENTER);
			
			// Asynchronously load the visual model (maybe from remote).
			access.scheduleStep(new ImmediateComponentStep<String>()
			{
				@Classname("loadModel")
				public IFuture<String> execute(IInternalAccess ia)
				{
					Future<String>	ret	= new Future<String>();
					try
					{
						InputStream	is	= SUtil.getResource(ia.getModel().getFilename(), ia.getClassLoader());
						Scanner	s	= new Scanner(is, "UTF-8");
						s.useDelimiter("\\Z");
						String	content	= s.next(); 
						s.close();
						ret.setResult(content);
					}
					catch(Exception fnfe)
					{
						ret.setException(fnfe);
					}
					return ret;
				}
			})
			.addResultListener(new SwingDefaultResultListener<String>(this)
			{
				public void customResultAvailable(String content)
				{
					try
					{
						graph.deactivate();
						graph.setEventsEnabled(false);
						graph.getModel().beginUpdate();
						MBpmnModel mmodel = SBpmnModelReader.readModel(new ByteArrayInputStream(content.getBytes("UTF-8")), access.getModel().getFilename(), vreader);
						graph.getModel().endUpdate();
						graph.setEventsEnabled(true);
						graph.activate();
						modelcontainer.setBpmnModel(mmodel);
//						modelcontainer.setFile(new File(filename));	// file not available locally?
						
						updateViews();
					}
					catch(Exception e)
					{
						customExceptionOccurred(e);
					}
				}
			});
			
			cmshandler.addCMSListener(access.getIdentifier())
				.addResultListener(new IIntermediateResultListener<CMSStatusEvent>()
			{

				@Override
				public void exceptionOccurred(Exception exception)
				{
					// TODO Auto-generated method stub
					
				}

				@Override
				public void resultAvailable(Collection<CMSStatusEvent> result)
				{
					// TODO Auto-generated method stub
					
				}

				@Override
				public void intermediateResultAvailable(CMSStatusEvent result)
				{
					IComponentDescription	desc	= result.getComponentDescription();
					try
					{
						final String[] bps = access.getModel().getBreakpoints();
						final List<String> abps = SUtil.arrayToList(desc.getBreakpoints());
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								if(bps!=null && bps.length>0)
								{
									mxICell cell = (mxICell)graph.getModel().getRoot();
									for(String bp: bps)
									{
										if(abps.contains(bp))
										{
											VElement ve = getVElement(cell, bp);
											if(ve!=null)
											{
												BreakpointMarker pbm = getBreakpointMarker(ve);
												if(pbm==null)
												{
													mxGeometry pgeo = ve.getGeometry();
													double ow = pgeo.getWidth();
													double oh = pgeo.getHeight();
													double w = ow/8;
													double h = oh/8;
	
													double shift = 10;
													if(ve.getBpmnElement() instanceof MActivity)
													{
														MActivity mact = (MActivity)ve.getBpmnElement();
														if(mact.isEvent())
														{
															shift = 0;
														}
														else if(mact.isGateway())
														{
															shift = 0;
														}
													}
													
													pbm = new BreakpointMarker(graph);
													double s = Math.max(14, Math.min(w, h));
													mxGeometry geo = new mxGeometry(ow-s-shift, oh-s-shift, s, s);
		//											geo.setRelative(true);
													pbm.setGeometry(geo);
		//											ve.insert(pbm);
													graph.addCell(pbm, ve);
		//											graph.refreshCellView(ve);
		//											graph.refreshCellView(pbm);
	//												System.out.println("added: "+pbm+" "+ve.getBpmnElement());
												}
											}
	//										else
	//										{
	//											System.out.println("no velem found for: "+bp);
	//										}
										}
										else
										{
											VElement ve = getVElement(cell, bp);
											if(ve!=null)
											{
												for(int i=0; i<ve.getChildCount(); i++)
												{
													mxICell cc = ve.getChildAt(i);
													if(cc instanceof BreakpointMarker)
													{
														graph.removeCells(new Object[]{cc});
	//													System.out.println("removed: "+cc+" "+ve.getBpmnElement());
														break;
													}
												}
											}
	//										else
	//										{
	//											System.out.println("no velem found for: "+bp);
	//										}
										}
									}
								}
							}
						});
					}
					catch(ComponentTerminatedException e)
					{
						// nop, component can be terminated
					}
				}

				@Override
				public void finished()
				{
					// TODO Auto-generated method stub
					
				}
				
			});
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	//-------- methods --------
	
	/**
	 *  Dispose the panel and remove any listeners.
	 */
	public void	dispose()
	{
		sub.terminate();
	}
	
	//-------- helper methods --------
	
	/**
	 *  Update views.
	 */
	protected void	updateViews()
	{
		assert SwingUtilities.isEventDispatchThread();
		
//		ProcessThreadInfo sel = null;
//		int row = getSelectedThredRow();
//		if(row!=-1)
//		{
//			sel = (ProcessThreadInfo)threads.getModel().getValueAt(row, -1);
//		}
//		
//		ptmodel.fireTableDataChanged();
//		hmodel.fireTableDataChanged();
//		threads.repaint();
//		history.repaint();
//		
//		if(sel!=null)
//		{
//			for(row=0; row<threads.getModel().getRowCount(); row++)
//			{
//				ProcessThreadInfo pti = (ProcessThreadInfo)threads.getModel().getValueAt(row, -1);
//				if(sel.equals(pti))
//				{
//					threads.setRowSelectionInterval(row, row);
//					break;
//				}
//			}
//		}
		
		if(bpp!=null)
		{
			List<String> sel_bps = new ArrayList<String>();
			for(Iterator<ProcessThreadInfo> it=ptmodel.getThreadInfos().iterator(); it.hasNext(); )
			{
				ProcessThreadInfo info = it.next();
				if(info.getActivity()!=null)
				{
					sel_bps.add(info.getActivity());
				}
			}
			bpp.setSelectedBreakpoints((String[])sel_bps.toArray(new String[sel_bps.size()]));
		}
		
		modelcontainer.getGraph().getView().invalidate();
		modelcontainer.getGraph().getView().clear(modelcontainer.getGraph().getModel().getRoot(), true, true);
		modelcontainer.getGraph().getView().validate();
		modelcontainer.getGraph().refresh();
//		modelcontainer.getGraphComponent().refresh();
	}
	
	/**
	 * 
	 */
	protected BreakpointMarker getBreakpointMarker(VElement ve)
	{
		assert SwingUtilities.isEventDispatchThread();

		BreakpointMarker ret = null;
		for(int i=0; i<ve.getChildCount(); i++)
		{
			mxICell cc = ve.getChildAt(i);
			if(cc instanceof BreakpointMarker)
			{
				ret = (BreakpointMarker)cc;
				break;
			}
		}
		return ret;
	}
	
	//-------- helper classes --------
	
	/**
	 *  List model for activations.
	 */
	protected class ProcessThreadModel extends AbstractTableModel
	{
		protected String[] colnames = new String[]{"Process-Id", "Parent-Id", "Activity", "Pool", "Lane", "Exception", "Data", "Edge Data", "Status"};
		
		/** The displayed process threads. */
		protected List<ProcessThreadInfo> threadinfos;
		
		public ProcessThreadModel()
		{
			assert SwingUtilities.isEventDispatchThread();

			this.threadinfos	= new ArrayList<ProcessThreadInfo>();
		}
		
		public String getColumnName(int column)
		{
			assert SwingUtilities.isEventDispatchThread();

			return colnames[column];
		}

		public int getColumnCount()
		{
			assert SwingUtilities.isEventDispatchThread();

			return colnames.length;
		}
		
		public int getRowCount()
		{
			assert SwingUtilities.isEventDispatchThread();

			return threadinfos.size();
		}
		
		public List<ProcessThreadInfo> getThreadInfos()
		{
			assert SwingUtilities.isEventDispatchThread();

			return threadinfos;
		}
		
		public void addValue(ProcessThreadInfo pti)
		{
			assert SwingUtilities.isEventDispatchThread();

			int idx = threadinfos.indexOf(pti);
			if(idx!=-1)
			{
				threadinfos.set(idx, pti);
				fireTableRowsUpdated(idx, idx);
			}
			else
			{
				threadinfos.add(pti);
				fireTableRowsInserted(threadinfos.size()-1, threadinfos.size()-1);
			}
			
		}
		
		public void removeValue(ProcessThreadInfo pti)
		{
			assert SwingUtilities.isEventDispatchThread();

			int idx = threadinfos.indexOf(pti);
			if(idx!=-1)
			{
//				System.out.println("Removed: "+pti);
				threadinfos.remove(idx);
				fireTableRowsDeleted(idx, idx);
			}
//			else
//			{
//				System.out.println("Cannot remove: "+pti);
//			}
		}
		
		public void updateValue(ProcessThreadInfo pti)
		{
			assert SwingUtilities.isEventDispatchThread();

			int idx = threadinfos.indexOf(pti);
			if(idx!=-1)
			{
//				System.out.println("Updated: "+pti);
				threadinfos.remove(idx);
				threadinfos.add(idx, pti);
				fireTableRowsUpdated(idx, idx);
			}
//			else
//			{
//				System.out.println("Cannot update: "+pti);
//			}
		}
		
		public Object getValueAt(int row, int column)
		{
			assert SwingUtilities.isEventDispatchThread();

			Object ret = null;
			ProcessThreadInfo info = (ProcessThreadInfo)threadinfos.toArray()[row];
			
			if(column==0)
			{
				ret = info.getThreadId();
			}
			else if(column==1)
			{
				ret = info.getParentId();
			}
			else if(column==2)
			{
				ret = info.getActivity();
			}
			else if(column==3)
			{
				ret = info.getPool(); 
			}
			else if(column==4)
			{
				ret = info.getLane(); 
			}
			else if(column==5)
			{
				ret = info.getException();
			}
			else if(column==6)
			{
				ret = info.getData();
			}
			else if(column==7)
			{
				ret = info.getEdges();
			}
			else if(column==8)
			{
				ret = info.isWaiting() ? "waiting" : "ready";
			}
			else
			{
				ret = info;
			}
			return ret;
		}
	}
	
	/**
	 *  List model for history.
	 */
	protected class HistoryModel extends AbstractTableModel
	{
		protected String[] colnames = new String[]{"Process-Id", "Activity", "Pool", "Lane"};
		
		/** The previous process thread steps. */
		protected List<ProcessThreadInfo> historyinfos;
		
		public HistoryModel()
		{
			this.historyinfos = new ArrayList<ProcessThreadInfo>();
		}
		
		public String getColumnName(int column)
		{
			assert SwingUtilities.isEventDispatchThread();

			return colnames[column];
		}

		public int getColumnCount()
		{
			assert SwingUtilities.isEventDispatchThread();

			return colnames.length;
		}
		
		public int getRowCount()
		{
			assert SwingUtilities.isEventDispatchThread();

			return historyinfos.size();
		}
		
		public Object getValueAt(int row, int column)
		{
			assert SwingUtilities.isEventDispatchThread();

			Object ret = null;
			ProcessThreadInfo	info	= (ProcessThreadInfo)historyinfos.get(row);
			if(column==0)
			{
				ret = info.getThreadId();
			}
			else if(column==1)
			{
				ret = info.getActivity();
			}
			else if(column==2)
			{
				ret = info.getPool(); 
			}
			else if(column==3)
			{
				ret = info.getLane(); 
			}
			else
			{
				ret = info;
			}

			return ret;
		}
		
		public void addValue(int idx, ProcessThreadInfo pti)
		{
			assert SwingUtilities.isEventDispatchThread();

			historyinfos.add(idx, pti);
			fireTableRowsInserted(idx, idx);
		}
		
		public void clear()
		{
			assert SwingUtilities.isEventDispatchThread();

			int size = historyinfos.size();
			historyinfos.clear();
			fireTableRowsDeleted(0, size-1);
		}
	}
	
	/**
	 *  Get the step info. Help to decide which component step to perform next.
	 *  @return Step info for debugging.
	 */
	public String getStepInfo()
	{
		String ret = null;
		int row = getSelectedThreadRow();
		if(row!=-1)
		{
			ProcessThreadInfo pti = (ProcessThreadInfo)threads.getModel().getValueAt(row, -1);
			ret = pti.getThreadId();
			ret = threadstosteps.get(ret);
		}
		return ret;
	}
	
	/**
	 * 
	 */
	protected int getSelectedThreadRow()
	{
		int ret = -1;
		int vrow = threads.getSelectedRow();
    	if(vrow!=-1)
    	{
    		ret = threads.convertRowIndexToModel(vrow);
    	}
    	return ret;
    }
	
	/**
	 *  Perform a step.
	 */
	protected void doStep()
	{
		access.searchService( new ServiceQuery<>( IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM))
			.addResultListener(new SwingDefaultResultListener<IComponentManagementService>(this)
		{
			public void customResultAvailable(final IComponentManagementService cms)
			{
				IFuture<Void> ret = cms.stepComponent(access.getIdentifier(), getStepInfo());
				ret.addResultListener(new SwingResultListener<Void>(new IResultListener<Void>()
				{
					public void resultAvailable(Void result)
					{
						updateViews();
					}
					
					public void exceptionOccurred(Exception exception)
					{
						exception.printStackTrace();
					}
				}));
			}
		});
	}
	
	/**
	 * 
	 */
	public IFuture<Void> toggleBreakPoint(final String bp)
	{
		final Future<Void> ret = new Future<Void>();
		
		List<String> bps = Arrays.asList(access.getModel().getBreakpoints());
		
		if(bps.contains(bp))
		{
			getActiveBreakpoints().addResultListener(new ExceptionDelegationResultListener<List<String>, Void>(ret)
			{
				public void customResultAvailable(final List<String> abps)
				{
//					System.out.println("active breakpoints: "+abps);
					
					if(abps.contains(bp))
					{
						abps.remove(bp);
					}
					else
					{
						abps.add(bp);
					}
					
					access.searchService( new ServiceQuery<>( IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM))
						.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
					{
						public void customResultAvailable(final IComponentManagementService cms)
						{
							cms.setComponentBreakpoints(access.getIdentifier(), (String[])abps.toArray(new String[abps.size()]))
								.addResultListener(new DelegationResultListener<Void>(ret));
						}
					});
				}
			});
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected IFuture<List<String>> getActiveBreakpoints()
	{
		final Future<List<String>> ret = new Future<List<String>>();
		
		access.searchService( new ServiceQuery<>( IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM))
			.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, List<String>>(ret)
		{
			public void customResultAvailable(final IComponentManagementService cms)
			{
				cms.getComponentDescription(access.getIdentifier())
					.addResultListener(new ExceptionDelegationResultListener<IComponentDescription, List<String>>(ret)
				{
					public void customResultAvailable(IComponentDescription desc) 
					{
						List<String> bps = new ArrayList<String>(Arrays.asList(desc.getBreakpoints()));
						ret.setResult(bps);
					}
				});
			}
		});
				
		return ret;
	}
	
	/**
     *  Find the velement of the graph that fits to the bpmn id.
     *  @param cell The start cell.
     *  @param brpid The activity id.
     *  @return The element.
     */
    protected VElement getVElement(mxICell cell, String brpid)
    {
    	VElement ret = null;
    	if(cell instanceof VElement)
		{
			VElement ve = (VElement)cell;
//			if(ve.getBpmnElement() instanceof MActivity && ((MActivity)ve.getBpmnElement()).getBreakpointId().equals(brpid))
			if(ve.getBpmnElement() instanceof MActivity && ((MActivity)ve.getBpmnElement()).getId().equals(brpid))
			{
				ret = ve;
			}
		}
    	
    	if(ret==null)
    	{
    		for(int i=0; i<cell.getChildCount() && ret==null; i++)
    		{
    			ret = getVElement(cell.getChildAt(i), brpid);
    		}
    	}
    	   	
    	return ret;
    }
    
	static
	{
		mxGraphics2DCanvas.putShape(BreakpointMarker.class.getSimpleName(), new mxBasicShape()
		{
			public Shape createShape(mxGraphics2DCanvas canvas, mxCellState state)
			{
				Rectangle temp = state.getRectangle();
				int x = temp.x;
				int y = temp.y;
				int w = temp.width;
				int h = temp.height;
				double mw = Math.min(w, h);

				double l = mw/(GuiConstants.SINE_45*2+1);
				double a = GuiConstants.SINE_45*l;
				
				GeneralPath bar = new GeneralPath();
				bar.moveTo(x+a, y);
				bar.lineTo(x+a+l, y);
				bar.lineTo(x+a+a+l, y+a);
				bar.lineTo(x+a+a+l, y+a+l);
				bar.lineTo(x+a+l, y+a+a+l);
				bar.lineTo(x+a, y+a+a+l);
				bar.lineTo(x+0, y+a+l);
				bar.lineTo(x+0, y+a);
				bar.lineTo(x+a, y);
				bar.closePath();
				Area ret = new Area(bar);
				
				return ret;
			}
		});
	}
}


