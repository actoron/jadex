/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.relax.core;


/**
 * Used to parse module.
 * 
 * As stand-alone, this state is used to parse a module included by another module.
 * By a base class, this state is used to parse a "head" module.
 * 
 * This class checks consistency between targetNamespace attribute
 * and the namespace specified by its caller (grammar/module).
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ModuleMergeState extends DivInModuleState
{
    protected ModuleMergeState( String expectedTargetNamespace )
    {
        this.expectedTargetNamespace = expectedTargetNamespace;
    }
    
    /** expected targetNamespace for this module.
     * 
     * null indicates that module must have targetNamespace attribute.
     * 
     * <p>
     * If RELAX module has 'targetNamespace' attribute, then its value
     * must be equal to this value, or this value must be null.
     * 
     * <p>
     * If RELAX module doesn't have the attribute, then this value is
     * used as the target namespace. If this value is null, then it is
     * an error.
     */
    protected final String expectedTargetNamespace;

    /**
     * computed targetNamespace.
     * 
     * actual target namespace depends on expected target namespace
     * and module. this field is set in startSelf method.
     */
    protected String targetNamespace;
    
    protected void startSelf()
    {
        super.startSelf();
        
        {// check relaxCoreVersion
            final String coreVersion = startTag.getAttribute("relaxCoreVersion");
            if( coreVersion==null )
                reader.reportWarning( RELAXCoreReader.ERR_MISSING_ATTRIBUTE, "module", "relaxCoreVersion" );
            else
            if(!"1.0".equals(coreVersion))
                reader.reportWarning( RELAXCoreReader.WRN_ILLEGAL_RELAXCORE_VERSION, coreVersion );
        }
        
        targetNamespace = startTag.getAttribute("targetNamespace");
        
        if(targetNamespace!=null)
        {
            // check accordance with expected namespace
            if( expectedTargetNamespace!=null
            &&  !expectedTargetNamespace.equals(targetNamespace) )
            {// error
                reader.reportError( RELAXCoreReader.ERR_INCONSISTENT_TARGET_NAMESPACE,
                                    targetNamespace, expectedTargetNamespace );
                // recover by ignoring one specified in the module
                targetNamespace = expectedTargetNamespace;
            }
        }
        else
        {// no targetnamespace attribute is given.
            if( expectedTargetNamespace==null )
            {
                reader.reportError( RELAXCoreReader.ERR_MISSING_TARGET_NAMESPACE );
                targetNamespace = "";    // recover by assuming the default namespace
            }
            else
                targetNamespace = expectedTargetNamespace;
        }
    }
}
