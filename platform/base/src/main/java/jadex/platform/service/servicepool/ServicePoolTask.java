package jadex.platform.service.servicepool;

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
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
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
	public static final String PROPERTY_OLD_MAPPINGS	= "mappings";
	
	/** Parameter for class infos. */
	public static final String PROPERTY_CLASS_INFOS	= "classinfos";
	/** Parameter for file infos. */
	public static final String PROPERTY_FILE_INFOS	= "fileinfos";
	/** Parameter for monitoring flags. */
	public static final String PROPERTY_MONITORINGS	= "monitorings";
	
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

		IComponentManagementService cms	= process.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>( IComponentManagementService.class));
		CreationInfo ci = new CreationInfo(process.getIdentifier());
		cms.createComponent(null, ServicePoolAgent.class.getName()+".class", ci, null)
			.addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, Void>(ret)
		{
			public void customResultAvailable(IComponentIdentifier cid) 
			{
				process.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>(IServicePoolService.class, cid))
					.addResultListener(process.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<IServicePoolService, Void>(ret)
				{
					public void customResultAvailable(IServicePoolService sps)
					{
						String[] oldmaps = (String[])context.getActivity().getParsedPropertyValue(PROPERTY_OLD_MAPPINGS);
						String[] classinfos = (String[])context.getActivity().getParsedPropertyValue(PROPERTY_CLASS_INFOS);
						String[] fileinfos = (String[])context.getActivity().getParsedPropertyValue(PROPERTY_FILE_INFOS);
						String[] monitorings = (String[])context.getActivity().getParsedPropertyValue(PROPERTY_MONITORINGS);
						List<MappingEntry> mappingentries = getMappingEntries(oldmaps, classinfos, fileinfos, monitorings);
						
						if(mappingentries.size() > 0)
						{
							CounterResultListener<Void> lis = new CounterResultListener<Void>(mappingentries.size(), new DelegationResultListener<Void>(ret));
							for(MappingEntry mentry : mappingentries)
							{
								CreationInfo cinfo = new CreationInfo();
								cinfo.setMonitoring(mentry.getMonitoring());
								sps.addServiceType(mentry.getClassinfo().getType(process.getClassLoader(), process.getModel().getAllImports()), mentry.getFileinfo()).addResultListener(lis);
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
	
	public static final String generateExpressionString(int colnum, List<MappingEntry> entries)
	{
		StringBuffer buf = new StringBuffer();
		if(colnum == 2)
		{
			buf.append("new Boolean[]{");
		}
		else
		{
			buf.append("new String[]{");
		}
		if(entries.size()>0)
		{
			for(MappingEntry entry : entries)
			{
				Object ent = null;
				switch (colnum)
				{
					case 0:
					default:
						ent = entry.getClassinfo() != null? entry.getClassinfo().getTypeName() : null;
						break;
					case 1:
						ent = entry.getFileinfo();
						break;
					case 2:
						ent = entry.getMonitoring();
						break;
				}
				if (colnum == 2)
				{
					if (ent == null)
					{
						buf.append("null");
					}
					else
					{
						buf.append(ent.toString());
					}
				}
				else
				{
					buf.append("\"").append(ent==null? "null": String.valueOf(ent)).append("\"");
				}
//				if(i+1<entries.size())
				buf.append(",");
			}
			buf.replace(buf.length() - 1, buf.length(), "}");
//			buf.append("}");
		}
		else
		{
			buf.append("}");
		}
		
		
		return buf.toString();
	}
	
	/**
	 *  Retrieve mapping entries.
	 */
	public static final List<MappingEntry> getMappingEntries(String[] oldmaps, String[] classinfos, String[] fileinfos, String[] monitorings)
	{
		List<MappingEntry> mappingentries = new ArrayList<ServicePoolTask.MappingEntry>();
		
		if (oldmaps != null && oldmaps.length > 0)
		{
			for (int i = 0; i < oldmaps.length; ++i)
			{
				ClassInfo ci = oldmaps[i]==null? null: new ClassInfo(oldmaps[i++]);
				String fi = oldmaps[i]==null? null: oldmaps[i];
				mappingentries.add(new MappingEntry(ci, fi, null));
			}
		}
		
		if (classinfos != null && classinfos.length > 0)
		{
			for (int i = 0; i < classinfos.length; ++i)
			{
				ClassInfo ci = classinfos[i]==null? null: new ClassInfo(classinfos[i]);
				String fi = null;
				if (fileinfos != null && fileinfos.length > i && fileinfos[i] != null)
				{
					fi = fileinfos[i];
				}
				PublishEventLevel mon = null;
				if(monitorings != null && monitorings.length > i && monitorings[i] != null)
				{
					mon = PublishEventLevel.valueOf(monitorings[i]);
				}
				mappingentries.add(new MappingEntry(ci, fi, mon));
			}
		}
		return mappingentries;
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
			
			if(task.getProperties()!=null)
			{
				MProperty mprop = task.getProperties().removeKey(PROPERTY_OLD_MAPPINGS);
				String[] oldmaps = mprop == null ? null : mprop.getInitialValue()!=null ? (String[])SJavaParser.parseExpression(mprop.getInitialValue(), null, cl).getValue(null) : null;
				mprop = task.getProperties().removeKey(PROPERTY_CLASS_INFOS);
				String[] classinfos = mprop == null ? null : mprop.getInitialValue()!=null ? (String[])SJavaParser.parseExpression(mprop.getInitialValue(), null, cl).getValue(null) : null;
				mprop = task.getProperties().removeKey(PROPERTY_FILE_INFOS);
				String[] fileinfos = mprop == null ? null : mprop.getInitialValue()!=null ? (String[])SJavaParser.parseExpression(mprop.getInitialValue(), null, cl).getValue(null) : null;
				mprop = task.getProperties().removeKey(PROPERTY_MONITORINGS);
				String[] monitorings = mprop == null ? null : mprop.getInitialValue()!=null ? (String[])SJavaParser.parseExpression(mprop.getInitialValue(), null, cl).getValue(null) : null;
				
				List<MappingEntry> mappingentries = getMappingEntries(oldmaps, classinfos, fileinfos, monitorings);
				if(mappingentries.size() > 0)
				{
					for(MappingEntry mentry : mappingentries)
					{
						tm.addEntry(mentry);
					}
				}
				UnparsedExpression uexp = new UnparsedExpression(null, 
						String[].class, generateExpressionString(0, mappingentries), null);
				task.setPropertyValue(PROPERTY_CLASS_INFOS, uexp);
				uexp = new UnparsedExpression(null, 
						String[].class, generateExpressionString(1, mappingentries), null);
				task.setPropertyValue(PROPERTY_FILE_INFOS, uexp);
				uexp = new UnparsedExpression(null, 
						String[].class, generateExpressionString(2, mappingentries), null);
				task.setPropertyValue(PROPERTY_MONITORINGS, uexp);
			}
			
//			if(mprop.getInitialValue()!=null)
//			{
//				String[] vals = (String[])SJavaParser.parseExpression(mprop.getInitialValue(), null, cl).getValue(null);
//				if(vals!=null)
//				{
//					for(int i=0; i<vals.length; i++)
//					{
//						ClassInfo ci = vals[i]==null? null: new ClassInfo(vals[i++]);
//						String fi = vals[i]==null? null: vals[i];
//						tm.addEntry(new Object[]{ci, fi});
//					}
//				}
//			}
			
			Action addaction = new AbstractAction("Add")
			{
				public void actionPerformed(ActionEvent e)
				{
					tm.addEntry(new MappingEntry());
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
			protected String[] colnames = new String[]{"Service Interface", "Component Filename", "Monitoring"};
			
			protected List<MappingEntry> entries = new ArrayList<MappingEntry>();
			
			public MappingsTableModel()
			{
			}
			
			/**
			 *  Gets the column name.
			 *  @return The column name.
			 */
			public String getColumnName(int column)
			{
				return colnames[column];
			}
			
			/**
			 *  Gets the column class.
			 */
			public Class<?> getColumnClass(int columnIndex)
			{
				if (columnIndex == 2)
				{
					return Boolean.class;
				}
				return super.getColumnClass(columnIndex);
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
				return 3;
			}
			
			/**
			 *  Gets the value.
			 *  @param rowIndex The row.
			 *  @param columnIndex The column.
			 *  @return The value.
			 */
			public Object getValueAt(int rowIndex, int columnIndex)
			{
				Object ret = null;
				switch (columnIndex)
				{
					case 0:
					default:
					{
						ClassInfo ci = entries.get(rowIndex).getClassinfo();
//						String type = ci != null ? ci.getTypeName() != null? ci.getTypeName() : "" : "";
						ret = ci;
						break;
					}
					case 1:
					{
						ret = entries.get(rowIndex).getFileinfo();
						break;
					}
					case 2:
					{
						ret = entries.get(rowIndex).getMonitoring();
						break;
					}
				}
				return ret;
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
				
				MappingEntry val = entries.get(rowIndex);
//				if(rowIndex<entries.size())
//				{
//					val = entries.get(rowIndex);
//				}
//				else
//				{
//					val = new Object[2];
//					entries.add(val);
//				}
				
				switch (columnIndex)
				{
					case 0:
					default:
					{
//						if (value instanceof String && ((String) value).length() > 0)
						if (value instanceof ClassInfo)
						{
							val.setClassinfo((ClassInfo) value);
						}
						else
						{
							val.setClassinfo(null);
						}
						
						UnparsedExpression uexp = new UnparsedExpression(null, 
								String[].class, generateExpressionString(columnIndex, entries), null);
						task.setPropertyValue(PROPERTY_CLASS_INFOS, uexp);
						break;
					}
					case 1:
					{
						if (value instanceof String && ((String) value).length() > 0)
						{
							val.setFileinfo((String) value);
						}
						else
						{
							val.setFileinfo(null);
						}
						
						UnparsedExpression uexp = new UnparsedExpression(null, 
								String[].class, generateExpressionString(columnIndex, entries), null);
						task.setPropertyValue(PROPERTY_FILE_INFOS, uexp);
						break;
					}
					case 2:
					{
						if(value != null)
						{
							val.setMonitoring(PublishEventLevel.valueOf((String)value));
						}
						else
						{
							val.setMonitoring(null);
						}
						
						UnparsedExpression uexp = new UnparsedExpression(null, 
								String[].class, generateExpressionString(columnIndex, entries), null);
						task.setPropertyValue(PROPERTY_MONITORINGS, uexp);
						break;
					}
				}

//				val[columnIndex] = value;
				
//				MProperty mprop = task.getProperties().get(PROPERTY_MAPPINGS);
//				
//				StringBuffer buf = new StringBuffer();
//				buf.append("new String[]{");
//				if(entries.size()>0)
//				{
//					for(int i=0; i<entries.size(); i++)
//					{
//						Object[] entry = entries.get(i);
//						buf.append("\"").append(entry[0]==null? "null": ((ClassInfo)entry[0]).getTypeName()).append("\",");
//						buf.append("\"").append(entry[1]==null? "null": (String)entry[1]).append("\"");
//						if(i+1<entries.size())
//							buf.append(",");
//					}
//				}
//				buf.append("}");
				
//				System.out.println(buf.toString());
				
//				UnparsedExpression uexp = new UnparsedExpression(null, 
//					String[].class, buf.toString(), null);
//				mprop.setInitialValue(uexp);
				
				modelcontainer.setDirty(true);
				fireTableCellUpdated(rowIndex, columnIndex);
			}
			
			/**
			 * 
			 */
			protected void addEntry(MappingEntry entry)
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
	
	/**
	 * 
	 */
	protected static class MappingEntry
	{
		protected ClassInfo classinfo;
		protected String fileinfo;
		protected PublishEventLevel monitoring;
		
		/**
		 *  Create a new MappingEntry.
		 */
		public MappingEntry()
		{
		}
		
		/**
		 *  Create a new MappingEntry.
		 */
		public MappingEntry(ClassInfo classinfo, String fileinfo, PublishEventLevel monitoring)
		{
			this.classinfo = classinfo;
			this.fileinfo = fileinfo;
			this.monitoring = monitoring;
		}

		public ClassInfo getClassinfo()
		{
			return classinfo;
		}

		public String getFileinfo()
		{
			return fileinfo;
		}

		public void setClassinfo(ClassInfo classinfo)
		{
			this.classinfo = classinfo;
		}

		public void setFileinfo(String fileinfo)
		{
			this.fileinfo = fileinfo;
		}

		public void setMonitoring(PublishEventLevel monitoring)
		{
			this.monitoring = monitoring;
		}

		public PublishEventLevel getMonitoring()
		{
			return monitoring;
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
