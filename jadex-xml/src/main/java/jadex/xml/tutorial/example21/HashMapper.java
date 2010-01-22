
package jadex.xml.tutorial.example21;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

//import org.jibx.runtime.IAliasable;
//import org.jibx.runtime.IMarshallable;
//import org.jibx.runtime.IMarshaller;
//import org.jibx.runtime.IMarshallingContext;
//import org.jibx.runtime.IUnmarshaller;
//import org.jibx.runtime.IUnmarshallingContext;
//import org.jibx.runtime.JiBXException;
//import org.jibx.runtime.impl.MarshallingContext;
//import org.jibx.runtime.impl.UnmarshallingContext;

public class HashMapper //implements IMarshaller, IUnmarshaller, IAliasable
{
//    private static final String SIZE_ATTRIBUTE_NAME = "size";
//    private static final String ENTRY_ELEMENT_NAME = "entry";
//    private static final String KEY_ATTRIBUTE_NAME = "key";
//    private static final int DEFAULT_SIZE = 10;
//    
//    private String m_uri;
//    private int m_index;
//    private String m_name;
//    
//    public HashMapper() {
//        m_uri = null;
//        m_index = 0;
//        m_name = "hashmap";
//    }
//    
//    public HashMapper(String uri, int index, String name) {
//        m_uri = uri;
//        m_index = index;
//        m_name = name;
//    }
//    
//    /* (non-Javadoc)
//     * @see org.jibx.runtime.IMarshaller#isExtension(java.lang.String)
//     */
//    public boolean isExtension(String mapname) {
//        return false;
//    }
//
//    /* (non-Javadoc)
//     * @see org.jibx.runtime.IMarshaller#marshal(java.lang.Object,
//     *  org.jibx.runtime.IMarshallingContext)
//     */
//    public void marshal(Object obj, IMarshallingContext ictx)
//        throws JiBXException {
//        
//        // make sure the parameters are as expected
//        if (!(obj instanceof HashMap)) {
//            throw new JiBXException("Invalid object type for marshaller");
//        } else if (!(ictx instanceof MarshallingContext)) {
//            throw new JiBXException("Invalid object type for marshaller");
//        } else {
//            
//            // start by generating start tag for container
//            MarshallingContext ctx = (MarshallingContext)ictx;
//            HashMap map = (HashMap)obj;
//            ctx.startTagAttributes(m_index, m_name).
//                attribute(m_index, SIZE_ATTRIBUTE_NAME, map.size()).
//                closeStartContent();
//            
//            // loop through all entries in hashmap
//            Iterator iter = map.entrySet().iterator();
//            while (iter.hasNext()) {
//                Map.Entry entry = (Map.Entry)iter.next();
//                ctx.startTagAttributes(m_index, ENTRY_ELEMENT_NAME);
//                if (entry.getKey() != null) {
//                    ctx.attribute(m_index, KEY_ATTRIBUTE_NAME,
//                        entry.getKey().toString());
//                }
//                ctx.closeStartContent();
//                if (entry.getValue() instanceof IMarshallable) {
//                    ((IMarshallable)entry.getValue()).marshal(ctx);
//                    ctx.endTag(m_index, ENTRY_ELEMENT_NAME);
//                } else {
//                    throw new JiBXException("Mapped value is not marshallable");
//                }
//            }
//            
//            // finish with end tag for container element
//            ctx.endTag(m_index, m_name);
//        }
//    }
//
//    /* (non-Javadoc)
//     * @see org.jibx.runtime.IUnmarshaller#isPresent(org.jibx.runtime.IUnmarshallingContext)
//     */
//    public boolean isPresent(IUnmarshallingContext ctx) throws JiBXException {
//        return ctx.isAt(m_uri, m_name);
//    }
//
//    /* (non-Javadoc)
//     * @see org.jibx.runtime.IUnmarshaller#unmarshal(java.lang.Object,
//     *  org.jibx.runtime.IUnmarshallingContext)
//     */
//    public Object unmarshal(Object obj, IUnmarshallingContext ictx)
//        throws JiBXException {
//        
//        // make sure we're at the appropriate start tag
//        UnmarshallingContext ctx = (UnmarshallingContext)ictx;
//        if (!ctx.isAt(m_uri, m_name)) {
//            ctx.throwStartTagNameError(m_uri, m_name);
//        }
//        
//        // create new hashmap if needed
//        int size = ctx.attributeInt(m_uri, SIZE_ATTRIBUTE_NAME, DEFAULT_SIZE);
//        HashMap map = (HashMap)obj;
//        if (map == null) {
//            map = new HashMap(size);
//        }
//        
//        // process all entries present in document
//        ctx.parsePastStartTag(m_uri, m_name);
//        while (ctx.isAt(m_uri, ENTRY_ELEMENT_NAME)) {
//            Object key = ctx.attributeText(m_uri, KEY_ATTRIBUTE_NAME, null);
//            ctx.parsePastStartTag(m_uri, ENTRY_ELEMENT_NAME);
//            Object value = ctx.unmarshalElement();
//            map.put(key, value);
//            ctx.parsePastEndTag(m_uri, ENTRY_ELEMENT_NAME);
//        }
//        ctx.parsePastEndTag(m_uri, m_name);
//        return map;
//    }
}