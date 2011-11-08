/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.grammar.xmlschema;

import java.util.Map;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.ReferenceContainer;
import com.sun.msv.grammar.ReferenceExp;

/**
 * XML Schema object.
 * 
 * <p>
 * A set of "schema components" that share the same target namespace.
 * It contains all global declarations.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class XMLSchemaSchema implements java.io.Serializable {

    public static final String XMLSchemaInstanceNamespace =
        "http://www.w3.org/2001/XMLSchema-instance";

    public XMLSchemaSchema( String targetNamespace, XMLSchemaGrammar parent ) {
        this.pool = parent.pool;
        this.targetNamespace = targetNamespace;
        parent.schemata.put( targetNamespace, this );
    }
    
    /** target namespace URI of this schema. */
    public final String targetNamespace;
    
    /** pool object which was used to construct this grammar. */
    public final ExpressionPool pool;
    
    /** choice of all global element declarations. */
    public Expression topLevel;
    
    @SuppressWarnings("serial")
    final public class SimpleTypeContainer extends ReferenceContainer {
        public SimpleTypeExp getOrCreate( String name ) {
            return (SimpleTypeExp)super._getOrCreate(name); }

        public SimpleTypeExp get( String name )
        { return (SimpleTypeExp)super._get(name); }

        protected ReferenceExp createReference( String name )
        { return new SimpleTypeExp(name); }
    }
    /** map from simple type name to SimpleTypeExp object */
    public final SimpleTypeContainer simpleTypes = new SimpleTypeContainer();
    
    @SuppressWarnings("serial")
    final public class ComplexTypeContainer extends ReferenceContainer {
        public ComplexTypeExp getOrCreate( String name ) {
            return (ComplexTypeExp)super._getOrCreate(name); }

        public ComplexTypeExp get( String name )
        { return (ComplexTypeExp)super._get(name); }

        protected ReferenceExp createReference( String name )
        { return new ComplexTypeExp(XMLSchemaSchema.this,name); }
    }
    /** map from simple type name to SimpleTypeExp object */
    public final ComplexTypeContainer complexTypes = new ComplexTypeContainer();

    @SuppressWarnings("serial")
    final public class AttributeGroupContainer extends ReferenceContainer {
        public AttributeGroupExp getOrCreate( String name ) {
            return (AttributeGroupExp)super._getOrCreate(name); }

        public AttributeGroupExp get( String name )
        { return (AttributeGroupExp)super._get(name); }

        protected ReferenceExp createReference( String name )
        { return new AttributeGroupExp(name); }
    }
    /** map from attribute group name to AttributeGroupExp object */
    public final AttributeGroupContainer attributeGroups = new AttributeGroupContainer();
    
    @SuppressWarnings("serial")
    final public class AttributeDeclContainer extends ReferenceContainer {
        public AttributeDeclExp getOrCreate( String name ) {
            return (AttributeDeclExp)super._getOrCreate(name); }

        public AttributeDeclExp get( String name )
        { return (AttributeDeclExp)super._get(name); }

        protected ReferenceExp createReference( String name )
        { return new AttributeDeclExp(name); }
    }
    /** map from attribute declaration name to AttributeDeclExp object */
    public final AttributeDeclContainer attributeDecls = new AttributeDeclContainer();
    
    @SuppressWarnings("serial")
    final public class ElementDeclContainer extends ReferenceContainer {
        public ElementDeclExp getOrCreate( String name ) {
            return (ElementDeclExp)super._getOrCreate(name); }

        public ElementDeclExp get( String name )
        { return (ElementDeclExp)super._get(name); }

        protected ReferenceExp createReference( String name )
        { return new ElementDeclExp(XMLSchemaSchema.this,name); }
    }
    /** map from attribute declaration name to AttributeDeclExp object */
    public final ElementDeclContainer elementDecls = new ElementDeclContainer();
    
    @SuppressWarnings("serial")
    final public class GroupDeclContainer extends ReferenceContainer {
        public GroupDeclExp getOrCreate( String name ) {
            return (GroupDeclExp)super._getOrCreate(name); }

        public GroupDeclExp get( String name )
        { return (GroupDeclExp)super._get(name); }

        protected ReferenceExp createReference( String name )
        { return new GroupDeclExp(name); }
    }
    /** map from attribute declaration name to AttributeDeclExp object */
    public final GroupDeclContainer groupDecls = new GroupDeclContainer();
    
    @SuppressWarnings("serial")
    final public class IdentityConstraintContainer implements java.io.Serializable {
        private final Map<String,IdentityConstraint> storage = new java.util.HashMap<String,IdentityConstraint>();
        public IdentityConstraint get( String name ) {
            return storage.get(name);
        }
        public void add( String name, IdentityConstraint idc ) {
            storage.put(name,idc);
        }
    }
    /** map from identity constraint name to IdentityConstraint object. */
    public final IdentityConstraintContainer identityConstraints = new IdentityConstraintContainer();
    
    // serialization support
    private static final long serialVersionUID = 1;    
}
