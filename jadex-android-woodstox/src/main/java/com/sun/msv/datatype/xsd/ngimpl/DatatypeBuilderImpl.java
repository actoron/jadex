package com.sun.msv.datatype.xsd.ngimpl;

import com.sun.msv.datatype.xsd.TypeIncubator;
import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.msv.datatype.xsd.XSDatatypeImpl;
import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeBuilder;
import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.ValidationContext;

class DatatypeBuilderImpl implements DatatypeBuilder {
    
    private TypeIncubator incubator;
        
    DatatypeBuilderImpl( XSDatatype baseType ) {
        incubator = new TypeIncubator(baseType);
    }
    
    public void addParameter( String name, String value, ValidationContext context )
                    throws DatatypeException {
        
        if( name.equals(XSDatatype.FACET_ENUMERATION) )
            // the enumeration facet is not allowed for RELAX NG.
            throw new DatatypeException(
                XSDatatypeImpl.localize(XSDatatypeImpl.ERR_NOT_APPLICABLE_FACET, name) );
            
        
        incubator.addFacet( name, value, false, context );
        
        if( name.equals(XSDatatype.FACET_PATTERN) )
            // if the pattern facet is specified, we have to
            // derive a new type so that multiple pattern facets can
            // work as ANDing.
            incubator = new TypeIncubator(incubator.derive(null,null));
    }
    
    public Datatype createDatatype() throws DatatypeException {
        return incubator.derive(null,null);
    }
}
