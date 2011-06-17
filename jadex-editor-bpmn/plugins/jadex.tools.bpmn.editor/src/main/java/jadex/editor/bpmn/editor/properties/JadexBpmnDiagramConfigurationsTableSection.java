package jadex.editor.bpmn.editor.properties;

import jadex.editor.bpmn.editor.properties.template.AbstractBpmnMultiColumnTablePropertySection;
import jadex.editor.bpmn.editor.properties.template.IConfigurationChangedListener;
import jadex.editor.bpmn.editor.properties.template.JadexBpmnPropertiesUtil;
import jadex.editor.common.model.properties.table.MultiColumnTable.MultiColumnTableRow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;

/**
 * 
 */
public class JadexBpmnDiagramConfigurationsTableSection extends AbstractBpmnMultiColumnTablePropertySection
{
	public static final String[] COLUMN_NAMES = new String[]{"Name", "Activated Pool.Lane"};
	public static final String[] COLUMN_TYPES = new String[]{TEXT, TEXT};
	public static final int[] COLUMN_WEIGHTS = new int[]{1, 6};
	public static final String[] DEFAULT_LISTELEMENT_ATTRIBUTE_VALUES = new String[]{"name", ""};
	public static final int UNIQUE_LIST_ELEMENT_ATTRIBUTE_INDEX = 0;
	
	// ---- global attributes ----
	
	private static Map<EModelElement, JadexBpmnDiagramConfigurationsTableSection> configurationSectionsMap = new HashMap<EModelElement, JadexBpmnDiagramConfigurationsTableSection>();

	// ---- attributes ----
	
	private String currentConfiguration;
	private List<IConfigurationChangedListener> configurationListener;

	// ---- constructor ----

	/**
	 *  Default constructor, initializes super class.
	 */
	public JadexBpmnDiagramConfigurationsTableSection()
	{
		super(JadexBpmnPropertiesUtil.JADEX_GLOBAL_ANNOTATION, JadexBpmnPropertiesUtil.JADEX_CONFIGURATIONS_LIST_DETAIL,
			"Configurations", UNIQUE_LIST_ELEMENT_ATTRIBUTE_INDEX, null);
		
		this.configurationListener  = new ArrayList<IConfigurationChangedListener>();
	}

	// ---- static methods ----
	
	public static JadexBpmnDiagramConfigurationsTableSection getConfigurationSectionInstanceForModelElement(EModelElement element)
	{
		if(element == null)
			return null;
		
		// ensure we have a BpmnDiagram as key!
		// todo: fix me: is a BpmnDiagramImpl that is not accessible
//		if(!(element instanceof BpmnDiagram))
		{
			element = JadexBpmnPropertiesUtil.retrieveBpmnDiagram(element);
		}
		
		return configurationSectionsMap.get(element);
	}
	
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
		String oldConfiguration = currentConfiguration;
		this.currentConfiguration = newConfiguration;
		
		// update model
		JadexBpmnPropertiesUtil.updateJadexEAnnotationDetail(modelElement, JadexBpmnPropertiesUtil.JADEX_GLOBAL_ANNOTATION, 
			JadexBpmnPropertiesUtil.JADEX_ACTIVE_CONFIGURATION_DETAIL, newConfiguration);

//		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		tabbedPage.refresh();
		
//		Control[] controls = sectionComposite.getChildren();
//		System.out.println("a: "+controls);
		
		// call Hook!
//		configurationChangedHook(oldConfiguration, newConfiguration);
		
		// We need listeners, because the common superclass of sections is NOT in BPMN
//		for(IConfigurationChangedListener configurationChangedListener : configurationListener)
//		{
//			configurationChangedListener.fireConfigurationChanged(oldConfiguration, newConfiguration);
//		}
	}
	
//	/**
//	 * Add a listener to be informed of configuration changes
//	 * @param listener
//	 * @return true, if listener was added
//	 */
//	protected boolean addConfigurationChangedListener(IConfigurationChangedListener listener)
//	{
//		//System.err.println("Registered Listener: " + configurationListener + " -- add listener: " + listener);
//		return configurationListener.add(listener);
//	}
//	
//	/**
//	 * Remove a registered listener
//	 * @param listener
//	 * @return true, if listener was removed
//	 */
//	protected boolean removeConfigurationChangedListener(IConfigurationChangedListener listener)
//	{
//		//System.err.println("Registered Listener: " + configurationListener + " -- remove listener: " + listener);
//		return configurationListener.remove(listener);
//	}
	
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
	 * 
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
		
		String selectedConfiguration;
		// TODO: Lars, please remove this condition at your desire!
		// only set active configuration on "name" column cell selection  
		if(newCell.getColumnIndex() == UNIQUE_LIST_ELEMENT_ATTRIBUTE_INDEX)
		{
			ISelection iSelection = tableViewer.getSelection();
			if (iSelection != null && !iSelection.isEmpty())
			{
				MultiColumnTableRow selectedRow = (MultiColumnTableRow) ((IStructuredSelection) tableViewer
					.getSelection()).getFirstElement();
				selectedConfiguration = selectedRow.getColumnValueAt(UNIQUE_LIST_ELEMENT_ATTRIBUTE_INDEX);
			}
			else
			{
				selectedConfiguration = ((MultiColumnTableRow)newCell.getElement()).getColumnValueAt(UNIQUE_LIST_ELEMENT_ATTRIBUTE_INDEX);
			}
			if (selectedConfiguration != null && !selectedConfiguration.equals(currentConfiguration))
			{
				setCurrentConfiguration(selectedConfiguration);
				//System.err.println("New selection: " + selectedConfiguration);
			}
		}
		
		System.out.println("selected config is: "+currentConfiguration);
	}
	
}
