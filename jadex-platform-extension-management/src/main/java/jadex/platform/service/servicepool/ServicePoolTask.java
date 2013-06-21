package jadex.platform.service.servicepool;

import jadex.bpmn.model.IModelContainer;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MProperty;
import jadex.bpmn.model.task.ITask;
import jadex.bpmn.model.task.ITaskContext;
import jadex.bpmn.model.task.ITaskPropertyGui;
import jadex.bpmn.model.task.annotation.Task;
import jadex.bpmn.model.task.annotation.TaskProperty;
import jadex.bpmn.model.task.annotation.TaskPropertyGui;
import jadex.bridge.ClassInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.autocombo.AutoComboTableCellEditor;
import jadex.commons.gui.autocombo.AutoComboTableCellRenderer;
import jadex.commons.gui.autocombo.AutoCompleteCombo;
import jadex.commons.gui.autocombo.FixedClassInfoComboModel;
import jadex.javaparser.SJavaParser;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

/**
 */
@Task(description="Task for initializing a service pool.", properties={
	@TaskProperty(name="mappings", clazz=List.class, description="The list of elements containing service type and component filename")},
	gui=@TaskPropertyGui(ServicePoolTask.ServicePoolTaskGui.class)
)
public class ServicePoolTask implements ITask
{
	//-------- constants --------
	
	/** Parameter for mappings. */
	public static final String PROPERTY_MAPPINGS	= "mappings"; 
	
	//-------- ITask interface --------
	

	/**
	 *  Execute the task.
	 *  @param context	The accessible values.
	 *  @param process	The process instance executing the task.
	 *  @return	To be notified, when the task has completed.
	 */
	public IFuture<Void> execute(final ITaskContext context, final IInternalAccess process)
	{
		final Future<Void>	ret	= new Future<Void>();

		SServiceProvider.getService(process.getServiceContainer(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(process.createResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
		{
			public void customResultAvailable(final IComponentManagementService cms)
			{
				CreationInfo ci = new CreationInfo(process.getComponentIdentifier());
				cms.createComponent(null, ServicePoolAgent.class.getName()+".class", ci, null)
					.addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, Void>(ret)
				{
					public void customResultAvailable(IComponentIdentifier cid) 
					{
						SServiceProvider.getService(process.getServiceContainer(), cid, IServicePoolService.class)
							.addResultListener(process.createResultListener(new ExceptionDelegationResultListener<IServicePoolService, Void>(ret)
						{
							public void customResultAvailable(IServicePoolService sps)
							{
								String[] maps = (String[])context.getActivity().getParsedPropertyValue(PROPERTY_MAPPINGS);
								if(maps!=null && maps.length>0)
								{
									CounterResultListener<Void> lis = new CounterResultListener<Void>(maps.length/2, new DelegationResultListener<Void>(ret));
									for(int i=0; i<maps.length; i++)
									{
										ClassInfo ci = maps[i]==null? null: new ClassInfo(maps[i++]);
										String fi = maps[i]==null? null: maps[i];
										sps.addServiceType(ci.getType(process.getClassLoader()), fi).addResultListener(lis);
									}
								}
								else
								{
									ret.setResult(null);
								}
							}
						}));
					}
				});
			}
		}));
		
		return ret;
	}
	
	/**
	 *  Compensate in case the task is canceled.
	 *  @return	To be notified, when the compensation has completed.
	 */
	public IFuture<Void> cancel(IInternalAccess instance)
	{
		// Todo: how to compensate service call!?
		return IFuture.DONE;
	}
	
	/**
	 *  The swing gui for the service task.
	 */
	public static class ServicePoolTaskGui implements ITaskPropertyGui
	{
		/** The panel. */
		protected JPanel panel;
		
		/** The model container. */
		protected IModelContainer modelcontainer;
		
		/** The task. */
		protected MActivity task;
		
		/**
		 *  Once called to init the component.
		 */
		public void init(IModelContainer container, final MActivity task, final ClassLoader cl)
		{
			this.modelcontainer = container;
			this.task = task;
			
			panel = new JPanel(new GridBagLayout());
			
			final MappingsTableModel tm = new MappingsTableModel();
			final JTable table = new JTable(tm);
			panel.add(new JScrollPane(table), new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.NORTHEAST, GridBagConstraints.BOTH, new Insets(0,5,5,0), 0, 0));
			final AutoCompleteCombo acc = new AutoCompleteCombo(null, null);
			final FixedClassInfoComboModel accm = new FixedClassInfoComboModel(acc, 20, container.getInterfaces());
			acc.setModel(accm);
			
			TableColumn col = table.getColumnModel().getColumn(0);
			col.setCellEditor(new AutoComboTableCellEditor(acc));
			col.setCellRenderer(new AutoComboTableCellRenderer(acc));
			
//			acc.setRenderer(new BasicComboBoxRenderer()
//			{
//				public Component getListCellRendererComponent(JList list, Object value,
//					int index, boolean isSelected, boolean cellHasFocus)
//				{
//					ClassInfo cl = (ClassInfo)value;
//					String txt = SReflect.getUnqualifiedTypeName(cl.getTypeName());//+" - "+cl.getPackage().getName();
//					return super.getListCellRendererComponent(list, txt, index, isSelected, cellHasFocus);
//				}
//			});
			
//			final JComboBox box = new JComboBox(container.getInterfaces().toArray());
//			box.setEditable(true);
			
			MProperty mprop = task.getProperties().get(PROPERTY_MAPPINGS);
			if(mprop.getInitialValue()!=null)
			{
				String[] vals = (String[])SJavaParser.parseExpression(mprop.getInitialValue(), null, cl).getValue(null);
				if(vals!=null)
				{
					for(int i=0; i<vals.length; i++)
					{
						ClassInfo ci = vals[i]==null? null: new ClassInfo(vals[i++]);
						String fi = vals[i]==null? null: vals[i];
						tm.addEntry(new Object[]{ci, fi});
					}
				}
			}
			
			Action addaction = new AbstractAction("Add")
			{
				public void actionPerformed(ActionEvent e)
				{
					tm.addEntry(new Object[2]);
					modelcontainer.setDirty(true);
				}
			};
			Action remaction = new AbstractAction("Remove")
			{
				public void actionPerformed(ActionEvent e)
				{
					int[] rows = table.getSelectedRows();
					tm.removeRows(rows);
					modelcontainer.setDirty(true);
				}
			};
			
			JButton buadd = new JButton(addaction);
			JButton burem = new JButton(remaction);
			JPanel bupa = new JPanel(new GridBagLayout());
			bupa.add(buadd, new GridBagConstraints(0,0,1,1,1,1,GridBagConstraints.NORTHEAST,GridBagConstraints.HORIZONTAL,new Insets(2,2,2,2),0,0));
			bupa.add(burem, new GridBagConstraints(0,1,1,1,1,1,GridBagConstraints.NORTHEAST,GridBagConstraints.HORIZONTAL,new Insets(2,2,2,2),0,0));
			
			panel.add(bupa, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0,0,5,5), 0, 0));
		}
		
		/**
		 *  Informs the panel that it should stop all its computation.
		 */
		public void shutdown()
		{
		}
		
		/**
		 *  The component to be shown in the gui.
		 *  @return	The component to be displayed.
		 */
		public JComponent getComponent()
		{
			return panel;
		}
		
		/**
		 *  Table model for imports.
		 */
		protected class MappingsTableModel extends AbstractTableModel
		{
			protected String[] colnames = new String[]{"Service Interface", "Component Filename"};
			
			protected List<Object[]> entries = new ArrayList<Object[]>();
			
			/**
			 *  Gets the column name.
			 *  @return The column name.
			 */
			public String getColumnName(int column)
			{
				return colnames[column];
			}
			
			/**
		     *  Returns whether a cell is editable.
		     *  @param  rowIndex The row being queried.
		     *  @param  columnIndex The column being queried.
		     *  @return If a cell is editable.
		     */
			public boolean isCellEditable(int rowIndex, int columnIndex)
			{
				return true;
			}
			
			/**
			 *  Returns the row count.
			 *  @return The row count.
			 */
			public int getRowCount()
			{
				return entries.size();
			}
			
			/**
			 *  Returns the column count.
			 *  @return The column count.
			 */
			public int getColumnCount()
			{
				return 2;
			}
			
			/**
			 *  Gets the value.
			 *  @param rowIndex The row.
			 *  @param columnIndex The column.
			 *  @return The value.
			 */
			public Object getValueAt(int rowIndex, int columnIndex)
			{
				return entries.get(rowIndex)[columnIndex];
			}
			
			/**
			 *  Sets the value.
			 *  @param value The value.
			 *  @param rowIndex The row.
			 *  @param columnIndex The column.
			 */
			public void setValueAt(Object value, int rowIndex, int columnIndex)
			{
//				if(value!=null)
//					System.out.println("setValue: "+value+" "+value.getClass());
				
				Object[] val;
				if(rowIndex<entries.size())
				{
					val = entries.get(rowIndex);
				}
				else
				{
					val = new Object[2];
					entries.add(val);
				}

				val[columnIndex] = value;
				
				MProperty mprop = task.getProperties().get(PROPERTY_MAPPINGS);
				
				StringBuffer buf = new StringBuffer();
				buf.append("new String[]{");
				if(entries.size()>0)
				{
					for(int i=0; i<entries.size(); i++)
					{
						Object[] entry = entries.get(i);
						buf.append("\"").append(entry[0]==null? "null": ((ClassInfo)entry[0]).getTypeName()).append("\",");
						buf.append("\"").append(entry[1]==null? "null": (String)entry[1]).append("\"");
						if(i+1<entries.size())
							buf.append(",");
					}
				}
				buf.append("}");
				
//				System.out.println(buf.toString());
				
				UnparsedExpression uexp = new UnparsedExpression(null, 
					String[].class, buf.toString(), null);
				mprop.setInitialValue(uexp);
				
				modelcontainer.setDirty(true);
				fireTableCellUpdated(rowIndex, columnIndex);
			}
			
			/**
			 * 
			 */
			protected void addEntry(Object[] entry)
			{
				entries.add(entry);
				fireTableRowsInserted(entries.size()-1, entries.size()-1);
			}
			
			/**
			 * 
			 */
			protected void removeRow(int row)
			{
				entries.remove(row);
				fireTableRowsDeleted(row, row);
			}
			
			/**
			 * 
			 */
			protected void removeRows(int[] rows)
			{
				Arrays.sort(rows);
				for(int i=rows.length-1; i>=0; i--)
				{
					entries.remove(rows[i]);
					fireTableRowsDeleted(rows[i], rows[i]);
				}
			}
		}
	}
	
	public static void main(String[] args)
	{
		ServicePoolTaskGui gui = new ServicePoolTaskGui();
		gui.init(null, null, null);
		JFrame f = new JFrame();
		f.add(gui.getComponent(), BorderLayout.CENTER);
		f.pack();
		f.setVisible(true);
		
	}
}
