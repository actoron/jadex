package jadex.editor.bpmn.editor.properties;

import jadex.editor.bpmn.editor.properties.template.AbstractBpmnMultiColumnTablePropertySection;
import jadex.editor.bpmn.editor.properties.template.JadexBpmnPropertiesUtil;
import jadex.editor.common.model.properties.table.MultiColumnTable.MultiColumnTableRow;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.gmf.runtime.draw2d.ui.figures.FigureUtilities;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchPart;

/**
 * 
 */
public class JadexBpmnDiagramConfigurationsTableSection extends AbstractBpmnMultiColumnTablePropertySection
{
	public static final String[] COLUMN_NAMES = new String[]{"Id", "Name", "Activated Pool.Lane"};
	public static final String[] COLUMN_TYPES = new String[]{TEXT, TEXT, TEXT};
	public static final int[] COLUMN_WEIGHTS = new int[]{0, 1, 6};
	public static final String[] DEFAULT_LISTELEMENT_ATTRIBUTE_VALUES = new String[]{"id", "name", ""};
	public static final int UNIQUE_LIST_ELEMENT_ATTRIBUTE_INDEX = 0;
	
	// ---- global attributes ----
	
	private static Map<EModelElement, JadexBpmnDiagramConfigurationsTableSection> configurationSectionsMap = new HashMap<EModelElement, JadexBpmnDiagramConfigurationsTableSection>();

	// ---- attributes ----
	
	private String currentConfiguration;

	// ---- constructor ----

	/**
	 *  Default constructor, initializes super class.
	 */
	public JadexBpmnDiagramConfigurationsTableSection()
	{
		super(JadexBpmnPropertiesUtil.JADEX_GLOBAL_ANNOTATION, JadexBpmnPropertiesUtil.JADEX_CONFIGURATIONS_LIST_DETAIL,
			"Configurations", UNIQUE_LIST_ELEMENT_ATTRIBUTE_INDEX, null);
	}

	// ---- static methods ----
	
//	public static JadexBpmnDiagramConfigurationsTableSection getConfigurationSectionInstanceForModelElement(EModelElement element)
//	{
//		if(element == null)
//			return null;
//		
//		// ensure we have a BpmnDiagram as key!
//		// todo: fix me: is a BpmnDiagramImpl that is not accessible
////		if(!(element instanceof BpmnDiagram))
//		{
//			element = JadexBpmnPropertiesUtil.retrieveBpmnDiagram(element);
//		}
//		
//		return configurationSectionsMap.get(element);
//	}
	
	// ---- methods ----
	
	/**
	 * @return the currentConfiguration
	 */
	public String getCurrentConfiguration()
	{
		return currentConfiguration;
	}

	/**
	 * @param newConfiguration the currentConfiguration to set
	 */
	private void setCurrentConfiguration(String newConfiguration)
	{
//		System.out.println("config: "+newConfiguration);
		
		String oldConfiguration = currentConfiguration;
		this.currentConfiguration = newConfiguration;
		
		// update model
		JadexBpmnPropertiesUtil.updateJadexEAnnotationDetail(modelElement, JadexBpmnPropertiesUtil.JADEX_GLOBAL_ANNOTATION, 
			JadexBpmnPropertiesUtil.JADEX_ACTIVE_CONFIGURATION_DETAIL, newConfiguration);

		tabbedPage.refresh();
	}
	
	// ---- overrides ----

	/**
	 * @see jadex.editor.bpmn.editor.properties.template.AbstractBpmnMultiColumnTablePropertySection#setInput(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	public void setInput(IWorkbenchPart part, ISelection selection)
	{
		super.setInput(part, selection);
		if(lastModelElement != null && lastModelElement != modelElement)
		{
			System.err.println("Handle this model element change!");
		}
		configurationSectionsMap.put(modelElement, this);
		
	}
	
	protected String[] getDefaultListElementAttributeValues()
	{
		return DEFAULT_LISTELEMENT_ATTRIBUTE_VALUES;
	}

	protected void createColumns(TableViewer viewer)
	{
		super.createColumns(viewer, COLUMN_NAMES, COLUMN_TYPES, null);
	}
	
	/**
	 * @param viewer
	 */
	protected void setupTableLayout(TableViewer viewer)
	{
		// Overridden to hide the first column (graphically by setting size to 0 :-()
		
		TableColumn[] columns = viewer.getTable().getColumns();
		int[] columnWeights = getColumnWeights(columns);

		Font tableFont = viewer.getTable().getFont();
		
		TableLayout tableLayout = new TableLayout();
		tableLayout.addColumnData(new ColumnWeightData(0, 0, true));
		
		for(int columnIndex = 1; columnIndex < columns.length; columnIndex++)
		{
			tableLayout.addColumnData(new ColumnWeightData(columnWeights[columnIndex],
				FigureUtilities.getTextWidth(columns[columnIndex].getText(), tableFont), true));
		}
		
//		TableColumn col = viewer.getTable().getColumn(0);
//		col.setWidth(0);
		columns[0].setResizable(false);
		
		viewer.getTable().setLayout(tableLayout);
	}

	/**
	 *  Get the column weights.
	 */
	protected int[] getColumnWeights(TableColumn[] columns)
	{
		if(columns.length == COLUMN_WEIGHTS.length)
		{
			return COLUMN_WEIGHTS;
		}
		else
		{
			return super.getColumnWeights(columns);
		}
	}

	/**
	 *  Set the selected configuration when user clicks a row.
	 */
	protected void cellFocusChangedHook(ViewerCell newCell, ViewerCell oldCell)
	{
		if(newCell == null)
			return;
		
		String sel = null;
		// only set active configuration on "name" column cell selection  
		if(newCell.getColumnIndex() == UNIQUE_LIST_ELEMENT_ATTRIBUTE_INDEX+1)
		{
			ISelection iSelection = tableViewer.getSelection();
			if(iSelection != null && !iSelection.isEmpty())
			{
				MultiColumnTableRow selectedRow = (MultiColumnTableRow)((IStructuredSelection)tableViewer
					.getSelection()).getFirstElement();
				sel = selectedRow.getColumnValueAt(UNIQUE_LIST_ELEMENT_ATTRIBUTE_INDEX)+":"+selectedRow.getColumnValueAt(1);
			}
//			else
//			{
//				selectedConfiguration = ((MultiColumnTableRow)newCell.getElement()).getColumnValueAt(UNIQUE_LIST_ELEMENT_ATTRIBUTE_INDEX);
//			}
		}
		
		if(!(sel==currentConfiguration || sel!=null 
			&& sel.equals(currentConfiguration)))
		{
			setCurrentConfiguration(sel);
			//System.err.println("New selection: " + selectedConfiguration);
		}
		
//		System.out.println("selected config is: "+currentConfiguration);
	}
	
}
