package jadex.bpmn.editor.gui;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;

import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxRectangle;

import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.editor.model.visual.VEdge;
import jadex.bpmn.editor.model.visual.VElement;
import jadex.bpmn.editor.model.visual.VExternalSubProcess;
import jadex.bpmn.editor.model.visual.VLane;
import jadex.bpmn.editor.model.visual.VPool;
import jadex.bpmn.editor.model.visual.VSequenceEdge;
import jadex.bpmn.editor.model.visual.VSubProcess;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MEdge;
import jadex.bpmn.model.MIdElement;
import jadex.bpmn.model.MSequenceEdge;
import jadex.bpmn.model.MSubProcess;
import jadex.bridge.service.annotation.ParameterInfo;
import jadex.commons.Tuple2;
import jadex.commons.collection.BiHashMap;


/**
 *  Static helper methods.
 */
public class SHelper
{
	// Should be in SReflect but requires asm
    // and also exists in SHelper
	
	/**
	 *  Get parameter names via asm reader.
	 *  @param m The method.
	 *  @return The list of parameter names or null
	 */
	public static List<String> getParameterNames(Method m)
	{
		List<String> ret = null;
		
		// Try to find via annotation
		boolean anused = false;
		Annotation[][] annos = m.getParameterAnnotations();
		if(annos!=null && annos.length>0)
		{
			ret = new ArrayList<String>();
			for(Annotation[] ans: annos)
			{
				boolean found = false;
				for(Annotation an: ans)
				{
					if(an instanceof ParameterInfo)
					{
						ret.add(((ParameterInfo)an).value());
						found = true;
						anused = true;
						break;
					}
				}
				if(!found)
					ret.add(null);
			}
		}
		
		// Try to find via debug info
		if(!anused)
		{
			Class<?> deccl = m.getDeclaringClass();
			String mdesc = Type.getMethodDescriptor(m);
			String url = Type.getType(deccl).getInternalName() + ".class";
	
			InputStream is = deccl.getClassLoader().getResourceAsStream(url);
			if(is!=null)
			{
				ClassNode cn = null;
				try
				{
					cn = new ClassNode();
					ClassReader cr = new ClassReader(is);
					cr.accept(cn, 0);
				}
				catch(Exception e)
				{
				}
				finally
				{
					try
					{
						is.close();
					}
					catch(Exception e)
					{
					}
				}
		
				if(cn!=null)
				{
					List<MethodNode> methods = cn.methods;
					ret = new ArrayList<String>();
					for(MethodNode method: methods)
					{
						if(method.name.equals(m.getName()) && method.desc.equals(mdesc))
						{
							Type[] argtypes = Type.getArgumentTypes(method.desc);
			
							List<LocalVariableNode> lvars = method.localVariables;
							if(lvars!=null && lvars.size()>0)
							{
								for(int i=0; i<argtypes.length; i++)
								{
									// first local variable represents the "this" object
									ret.add(lvars.get(i+1).name);
								}
							}
							break;
						}
					}
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Get return value name.
	 *  @param m The method.
	 */
	public static String getReturnValueName(Method m)
	{
		String ret = null;
		
		// Try to find via annotation
		Annotation[] annos = m.getAnnotations();
		if(annos!=null && annos.length>0)
		{
			for(Annotation an: annos)
			{
				if(an instanceof ParameterInfo)
				{
					ret = ((ParameterInfo)an).value();
					break;
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Tests if an object is a event subprocess.
	 */
	public static final boolean isEventSubProcess(Object obj)
	{
		boolean ret = false;
		if (obj instanceof VActivity)
		{
			obj = ((VActivity) obj).getBpmnElement();
		}
		
		if (obj instanceof MSubProcess)
		{
			MSubProcess sp = (MSubProcess) obj;
			if (MSubProcess.SUBPROCESSTYPE_EVENT.equals(sp.getSubprocessType()))
			{
				ret = true;
			}
		}
		return ret;
	}
	
	/**
	 *  Copies a set of selected elements.
	 *  
	 *  @param graph The graph.
	 *  @param model The BPMN model.
	 *  @param incells The cells to be copied.
	 */
	public static final List<VElement> copy(BpmnGraph graph, final MBpmnModel model, Object[] incells)
	{
		Set<MIdElement> tmpmelems = new HashSet<MIdElement>();
		for (Object cell : incells)
		{
			if (cell instanceof VElement &&
				!(cell instanceof VPool) &&
				!(cell instanceof VLane))
			{
				VElement velem = (VElement) cell;
				MIdElement melem = velem.getBpmnElement();
				tmpmelems.add(melem);
			}
		}
		
		Map<String, VElement> vmap = new HashMap<String, VElement>();
		final Set<MIdElement> melems = new HashSet<MIdElement>();
		for (Object cell : incells)
		{
			if (cell instanceof VElement &&
					!(cell instanceof VPool) &&
					!(cell instanceof VLane))
			{
				VElement velem = (VElement) cell;
				MIdElement melem = velem.getBpmnElement();
				MIdElement parent = model.getParent(melem);
				if (parent == null || !model.isContainedInParentSet(tmpmelems, parent))
				{
					melems.add(melem);
					vmap.put(melem.getId(), velem);
				}
			}
		}
		tmpmelems = null;
		
		Tuple2<BiHashMap<String, String>, List<MIdElement>> cloned = model.cloneElements(melems);
		List<VElement> clonedvisuals = generateVisualClones(graph, cloned.getFirstEntity(), vmap, cloned.getSecondEntity(), null, true);
		
		for (VElement clonedvisual : clonedvisuals)
		{
			String oldid = cloned.getFirstEntity().rget(clonedvisual.getBpmnElement().getId());
			VElement orig = vmap.get(oldid);
			clonedvisual.setVisualParent(orig.getParent());
		}
		
		return clonedvisuals;
	}
	
	/**
	 * 
	 * @param graph
	 * @param mclones
	 * @param vclones
	 * @return
	 */
	protected static final List<VElement> generateVisualClones(BpmnGraph graph, BiHashMap<String, String> idmap, Map<String, VElement> oldvmap, List<MIdElement> mclones, Map<String, VElement> vclones, boolean istoplevelelement)
	{
		List<VElement> ret = new ArrayList<VElement>();
		if (vclones == null)
		{
			vclones = new HashMap<String, VElement>();
		}
		
		List<MEdge> medges = new ArrayList<MEdge>();
		
		for (MIdElement mclone : mclones)
		{
			VElement genelem = null;
			if (mclone instanceof MSubProcess)
			{
				MSubProcess msp = (MSubProcess) mclone;
				if (msp.hasProperty("file") || msp.hasProperty("filename"))
				{
					VExternalSubProcess vextsp = new VExternalSubProcess(graph);
					genelem = vextsp;
				}
				else
				{
					genelem = new VSubProcess(graph);
					
					List<MIdElement> subelements = new ArrayList<MIdElement>();
					if (msp.getActivities() != null)
					{
						subelements.addAll(msp.getActivities());
						if (msp.getEdges() != null)
						{
							subelements.addAll(msp.getEdges());
						}
					}
					
					List<VElement> elements = generateVisualClones(graph, idmap, oldvmap, subelements, vclones, false);
					for (VElement element : elements)
					{
						genelem.insert(element);
					}
				}
			}
			else if (mclone instanceof MActivity)
			{
				VActivity act = new VActivity(graph);
				genelem = act;
			}
			else if (mclone instanceof MEdge)
			{
				medges.add((MEdge) mclone);
			}
			
			if (genelem != null)
			{
				if (genelem instanceof VActivity)
				{
					String oldid = idmap.rget(mclone.getId());
					VElement oldelem = graph.getVisualElementById(oldid);
					
					if (((VActivity) oldelem).getInternalParameters() != null && ((VActivity) oldelem).getInternalParameters().size() > 0)
					{
						((VActivity) genelem).setInternalParameters(((VActivity) oldelem).getInternalParameters());
					}
					
					genelem.setCollapsed(oldelem.isCollapsed());
					mxGeometry oldgeo = oldelem.getGeometry();
					int shift = istoplevelelement? GuiConstants.PASTE_SHIFT : 0;
					mxGeometry newgeo = new mxGeometry(oldgeo.getX() + shift, oldgeo.getY() + shift, oldgeo.getWidth(), oldgeo.getHeight());
					mxRectangle ab = oldgeo.getAlternateBounds() != null? new mxRectangle(oldgeo.getAlternateBounds()) : null;
					newgeo.setAlternateBounds(ab);
					genelem.setGeometry(newgeo);
					
					List<MActivity> handlers = ((MActivity) mclone).getEventHandlers();
					if (handlers != null)
					{
						for (MActivity handler : handlers)
						{
							VActivity hact = new VActivity(graph);
							hact.setVisualParent(genelem);
							hact.setBpmnElement(handler);
						}
					}
				}
				
				ret.add(genelem);
				genelem.setBpmnElement(mclone);
				vclones.put(genelem.getBpmnElement().getId(), genelem);
			}
		}
		
		for (MEdge medge : medges)
		{
			VEdge vedge = null;
			if (medge instanceof MSequenceEdge)
			{
				vedge = new VSequenceEdge(graph);
				vedge.setSource(vclones.get(medge.getSource().getId()));
				vedge.setTarget(vclones.get(medge.getTarget().getId()));
				vedge.setBpmnElement(medge);
				
				ret.add(vedge);
			}
		}
		
		return ret;
	}
	
	/** Checks if an object is a visual event. */
	public static final boolean isVisualEvent(Object obj)
	{
		boolean ret = false;
		if (obj instanceof VActivity)
		{
			VActivity vactivity = (VActivity) obj;
			MActivity mactivity = vactivity.getMActivity();
			ret = mactivity != null && mactivity.getActivityType() != null && mactivity.getActivityType().startsWith("Event");
		}
		return ret;
	}
}
