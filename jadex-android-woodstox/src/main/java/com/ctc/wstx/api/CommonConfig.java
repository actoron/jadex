package com.ctc.wstx.api;

import java.util.*;

import org.codehaus.stax2.XMLStreamProperties;

import com.ctc.wstx.util.DataUtil;

/**
 * Shared common base class for variour configuration container implementations
 * for public factories Woodstox uses: implementations of
 * {@link javaxx.xml.stream.XMLInputFactory},
 * {@link javaxx.xml.stream.XMLOutputFactory} and
 * {@link org.codehaus.stax2.validation.XMLValidationSchemaFactory}.
 * Implements basic settings for some shared settings, defined by the
 * shared property interface {@link XMLStreamProperties}.
 */
abstract class CommonConfig
    implements XMLStreamProperties
{
    /*
    ///////////////////////////////////////////////////////////////////////
    // Implementation info
    ///////////////////////////////////////////////////////////////////////
    */

    protected final static String IMPL_NAME = "woodstox";
    
    /* !!! TBI: get from props file or so? Or build as part of Ant
     *    build process?
     */
    /**
     * This is "major.minor" version used for purposes of determining
     * the feature set. Patch level is not included, since those should
     * not affect API or feature set. Using applications should be
     * prepared to take additional levels, however, just not depend
     * on those being available.
     */
    protected final static String IMPL_VERSION = "4.1";

    /*
    ///////////////////////////////////////////////////////////////////////
    // Internal constants
    ///////////////////////////////////////////////////////////////////////
    */

    final static int PROP_IMPL_NAME = 1;
    final static int PROP_IMPL_VERSION = 2;

    final static int PROP_SUPPORTS_XML11 = 3;
    final static int PROP_SUPPORT_XMLID = 4;
    
    final static int PROP_RETURN_NULL_FOR_DEFAULT_NAMESPACE = 5; 

    /**
     * Map to use for converting from String property ids to enumeration
     * (ints). Used for faster dispatching.
     */
    final static HashMap sStdProperties = new HashMap(16);
    static {
        // Basic information about the implementation:
        sStdProperties.put(XMLStreamProperties.XSP_IMPLEMENTATION_NAME,
                           DataUtil.Integer(PROP_IMPL_NAME));
        sStdProperties.put(XMLStreamProperties.XSP_IMPLEMENTATION_VERSION,
                           DataUtil.Integer(PROP_IMPL_VERSION));

        // XML version support:
        sStdProperties.put(XMLStreamProperties.XSP_SUPPORTS_XML11,
                           DataUtil.Integer(PROP_SUPPORTS_XML11));

        // Xml:id support:
        sStdProperties.put(XMLStreamProperties.XSP_SUPPORT_XMLID,
                           DataUtil.Integer(PROP_SUPPORT_XMLID));

        sStdProperties.put(WstxInputProperties.P_RETURN_NULL_FOR_DEFAULT_NAMESPACE,
                DataUtil.Integer(PROP_RETURN_NULL_FOR_DEFAULT_NAMESPACE));

        /* 23-Apr-2008, tatus: Additional interoperability property,
         *    one that Sun implementation uses. Can map tor Stax2
         *    property quite easily.
         */
        sStdProperties.put("http://java.sun.com/xml/stream/properties/implementation-name",
                           DataUtil.Integer(PROP_IMPL_NAME));
                
    }

    protected CommonConfig() { }

    /*
    ///////////////////////////////////////////////////////////////////////
    // Public API, generic StAX config methods
    ///////////////////////////////////////////////////////////////////////
     */

    public Object getProperty(String propName)
    {
        /* Related to [WSTX-243]; would be nice to not to have to throw an
         * exception; but Stax spec suggests that we do need to indicate
         * unrecognized property by exception.
         */
        int id = findPropertyId(propName);
        if (id >= 0) {
            return getProperty(id);
        }
        id = findStdPropertyId(propName);
        if (id < 0) {
            reportUnknownProperty(propName);
            return null;
        }
        return getStdProperty(id);
    }

    public boolean isPropertySupported(String propName)
    {
        return (findPropertyId(propName) >= 0)
            || (findStdPropertyId(propName) >= 0);
    }

    /**
     * @return True, if the specified property was <b>succesfully</b>
     *    set to specified value; false if its value was not changed
     */
    public boolean setProperty(String propName, Object value)
    {
        int id = findPropertyId(propName);
        if (id >= 0) {
            return setProperty(propName, id, value);
        }
        id = findStdPropertyId(propName);
        if (id < 0) {
            reportUnknownProperty(propName);
            return false;
        }
        return setStdProperty(propName, id, value);
    }

    protected void reportUnknownProperty(String propName)
    {
        // see [WSTX-243] for discussion on whether to throw...
        throw new IllegalArgumentException("Unrecognized property '"+propName+"'");
    }
    
    /*
    ///////////////////////////////////////////////////////////////////////
    // Additional methods used by Woodstox core
    ///////////////////////////////////////////////////////////////////////
     */

    public final Object safeGetProperty(String propName)
    {
        int id = findPropertyId(propName);
        if (id >= 0) {
            return getProperty(id);
        }
        id = findStdPropertyId(propName);
        if (id < 0) {
            return null;
        }
        return getStdProperty(id);
    }

    /**
     * Method used to figure out the official implementation name
     * for input/output/validation factories.
     */
    public static String getImplName() { return IMPL_NAME; }

    /**
     * Method used to figure out the official implementation version
     * for input/output/validation factories.
     */
    public static String getImplVersion() { return IMPL_VERSION; }

    /*
    ///////////////////////////////////////////////////////////////////////
    // Interface sub-classes have to implement / can override
    ///////////////////////////////////////////////////////////////////////
     */

    /**
     * @return Internal enumerated int matching the String name
     *   of the property, if one found: -1 to indicate no match
     *   was found.
     */
    protected abstract int findPropertyId(String propName);

    protected boolean doesSupportXml11() {
        /* Woodstox does support xml 1.1 ... but sub-classes can
         * override it if/as necessary (validator factories might not
         * support it?)
         */
        return true;
    }

    protected boolean doesSupportXmlId() {
        /* Woodstox does support Xml:id ... but sub-classes can
         * override it if/as necessary.
         */
        return true;
    }

    protected boolean returnNullForDefaultNamespace() {
        return Boolean.getBoolean(WstxInputProperties.P_RETURN_NULL_FOR_DEFAULT_NAMESPACE);
    }
    
    protected abstract Object getProperty(int id);

    protected abstract boolean setProperty(String propName, int id, Object value);

    /*
    ///////////////////////////////////////////////////////////////////////
    // Internal methods
    ///////////////////////////////////////////////////////////////////////
     */

    protected int findStdPropertyId(String propName)
    {
        Integer I = (Integer) sStdProperties.get(propName);
        return (I == null) ? -1 : I.intValue();
    }

    protected boolean setStdProperty(String propName, int id, Object value)
    {
        // None of the current shared properties are settable...
        return false;
    }

    protected Object getStdProperty(int id)
    {
        switch (id) {
        case PROP_IMPL_NAME:
            return IMPL_NAME;
        case PROP_IMPL_VERSION:
            return IMPL_VERSION;
        case PROP_SUPPORTS_XML11:
            return doesSupportXml11() ? Boolean.TRUE : Boolean.FALSE;
        case PROP_SUPPORT_XMLID:
            return doesSupportXmlId() ? Boolean.TRUE : Boolean.FALSE;
        case PROP_RETURN_NULL_FOR_DEFAULT_NAMESPACE:
            return returnNullForDefaultNamespace() ? Boolean.TRUE : Boolean.FALSE;
        default: // sanity check, should never happen
            throw new IllegalStateException("Internal error: no handler for property with internal id "+id+".");
        }
    }
}
