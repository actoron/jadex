/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.datatype.xsd;

import java.util.Collection;
import java.util.Iterator;
import java.util.Stack;
import java.util.Vector;

import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.ValidationContext;
import org.xml.sax.Locator;

import com.sun.msv.datatype.xsd.DatatypeFactory;
import com.sun.msv.datatype.xsd.FinalComponent;
import com.sun.msv.datatype.xsd.StringType;
import com.sun.msv.datatype.xsd.TypeIncubator;
import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.msv.datatype.xsd.XSDatatypeImpl;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.reader.GrammarReader;
import com.sun.msv.reader.State;

/**
 * A wrapper of XSDatatype that serves as an expression
 * and encapsulates lazy-constructed datatypes.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
@SuppressWarnings("serial")
public class XSDatatypeExp extends ReferenceExp implements GrammarReader.BackPatch
{
    /** Creates this object from existing XSDatatype. */
    public XSDatatypeExp( XSDatatype dt, ExpressionPool _pool ) {
        super(dt.getName());
        this.namespaceUri = dt.getNamespaceUri();
        this.dt = dt;
        this.pool = _pool;
        this.ownerState = null;
        this.renderer = null;
        super.exp = _pool.createData(dt);
    }
    
    /** Creates lazily created datatype. */
    public XSDatatypeExp( String nsUri, String typeName, GrammarReader reader, Renderer _renderer ) {
//        this(typeName, reader, reader.getCurrentState(), _renderer );
        super(typeName);
        this.namespaceUri = nsUri;
        this.dt = null;
        this.ownerState = reader.getCurrentState();
        this.renderer = _renderer;
        this.pool = reader.pool;
        
        reader.addBackPatchJob(this);
    }
    
    /** Used only for cloning */
    private XSDatatypeExp( String nsUri, String localName ) {
        super(localName);
        this.namespaceUri = nsUri;
    }
    
    /**
     * Namespace URI of this datatype.
     * Local name is stored in the name field of ReferenceExp.
     */
    private final String namespaceUri;
    
    /**
     * Creates an incubator so that the caller can add more facets
     * and derive a new type.
     */
    public XSTypeIncubator createIncubator() {
        if( isLateBind() )
            return new LazyTypeIncubator(this,ownerState.reader);
        
        // normal incubator
        return new XSTypeIncubator() {
            private final TypeIncubator core = new TypeIncubator(dt);
            
            public void addFacet( String name, String value, boolean fixed, ValidationContext context ) throws DatatypeException {
                core.addFacet(name,value,fixed,context);
            }
            public XSDatatypeExp derive(String uri,String localName) throws DatatypeException {
                return new XSDatatypeExp( core.derive(uri,localName), pool );
            }
        };
    }
    
    
    /**
     * Datatype object wrapped by this expression.
     * 
     * This field can be null if the datatype object is not available
     * at this moment (say, because of the forward reference). In this
     * case, {@link #ownerState} and {@link #renderer} fields are 
     * available.
     */
    private XSDatatype dt;

    /** ExpressionPool that can be used if necessary. */
    private ExpressionPool pool;
    
    /**
     * Gets a encapsulated datatype object
     * <b>This method can be called only after all the datatypes are created.</b>
     * 
     * <p>
     * Some of the datatypes are lazily during the back-patching phase.
     */
    public XSDatatype getCreatedType() {
        if(dt==null)    throw new IllegalStateException();
        return dt;
    }
    
    /**
     * Gets the type definition.
     * This method renders the datatype object if it's not rendered yet.
     * Internal use only.
     */
    public XSDatatype getType( RenderingContext context ) {
        
        if(dt!=null)
            // the datatype is already rendered.
            return dt;
        
        if(context==null)    // create a new context.
            context = new RenderingContext();
        
        if( context.callStack.contains(this) ) {
            // a recursive definition is detected.
            Vector<Locator> locs = new Vector<Locator>();
            for( int i=0; i<context.callStack.size(); i++ )
                locs.add( ((XSDatatypeExp)context.callStack.get(i)).ownerState.getLocation() );
                
            ownerState.reader.reportError(
                (Locator[])locs.toArray(new Locator[0]),
            GrammarReader.ERR_RECURSIVE_DATATYPE, null );
            return StringType.theInstance;
        }
        context.callStack.push(this);
            
        try {
            dt = renderer.render(context);
        } catch( DatatypeException e ) {
            ownerState.reader.reportError( GrammarReader.ERR_BAD_TYPE,
                new Object[]{e}, e, new Locator[]{ownerState.getLocation()} );
            dt = StringType.theInstance;    // recover by assuming a valid type.
        }
        
        context.callStack.pop();
        
        if( dt==null )
            throw new Error();    // renderer must render some datatype.
                                // if there was an error, it must recover from it.
        
        // set the expression
        this.exp = pool.createData(dt);

        return dt;
    }

    /** Renders the type (GrammarReader.BackPatch implementation). */
    public void patch() { getType(null); }
    
    public State getOwnerState() { return ownerState; }
    
    
    
    // these fields should not be serialized
    
    /**
     * State object that creates this late-binding object.
     * The source location of this state is used for error message.
     */
    private transient State ownerState;

    /**
     * Once the parsing is completed, this function object should
     * be able to render the actual datatype object.
     */
    private transient Renderer renderer;

    
    
    public final boolean isLateBind() { return dt==null; }
    
    /** Gets a clone of this object. */
    public XSDatatypeExp getClone() {
        XSDatatypeExp t = new XSDatatypeExp(this.namespaceUri,this.name);
        t.redefine(this);
        return t;
    }
    
    /** Updates this object by copying the state from rhs */
    public void redefine( XSDatatypeExp rhs ) {
//        this.name   = rhs.name;
        this.exp    = rhs.exp;
        this.dt     = rhs.dt;
        this.pool   = rhs.pool;
        this.ownerState = rhs.ownerState;
        this.renderer = rhs.renderer;
        
        // rhs might be a late-bind object.
        // note that it's safe to call this method
        // if this is not a late-bind object.
        if(ownerState!=null)
            ownerState.reader.addBackPatchJob(this);
    }
    
    
    /** Derives a new type by setting final values. */
    public XSDatatypeExp createFinalizedType( final int finalValue, GrammarReader reader ) {
        if(finalValue==0)
            return this;    // there is no need to create a new object.
        
        if( !isLateBind() )
            return new XSDatatypeExp(
                new FinalComponent( (XSDatatypeImpl)dt, finalValue ), pool );
        
        // create datatype lazily
        return new XSDatatypeExp( this.namespaceUri, this.name, reader, new Renderer() {
            public XSDatatype render( RenderingContext context ) throws DatatypeException {
                return new FinalComponent(
                    (XSDatatypeImpl)getType(context), finalValue );
            }
        });
    }
    
    /** Derives a new type by list. */
    public static XSDatatypeExp makeList(
        final String nsUri, final String typeName,
        final XSDatatypeExp itemType, GrammarReader reader )
            throws DatatypeException {
        
        if(!itemType.isLateBind())
            // create it normally
            return new XSDatatypeExp(
                DatatypeFactory.deriveByList(nsUri,typeName,itemType.dt),
                reader.pool );
        
        // create it lazily
        return new XSDatatypeExp( nsUri, typeName, reader, new Renderer() {
            public XSDatatype render( RenderingContext context ) throws DatatypeException {
                return DatatypeFactory.deriveByList( nsUri, typeName,
                    itemType.getType(context) );
            }
        });
    }
    
    /** Derives a new type by union. */
    public static XSDatatypeExp makeUnion( final String typeNameUri,
        final String typeName, final Collection<XSDatatypeExp> members, GrammarReader reader )
            throws DatatypeException {
        
        final XSDatatype[] m = new XSDatatype[members.size()];
        
        int i=0;
        Iterator<XSDatatypeExp> itr = members.iterator();
        while(itr.hasNext()) {
            XSDatatypeExp item = (XSDatatypeExp)itr.next();
            
            if( item.isLateBind() ) {
                // create the union lazily.
                return new XSDatatypeExp(typeNameUri,typeName,reader,
                    new Renderer(){
                        public XSDatatype render( RenderingContext context )
                            throws DatatypeException {
                            
                            int i=0;
                            Iterator<XSDatatypeExp> itr = members.iterator();
                            while(itr.hasNext())
                                m[i++] = ((XSDatatypeExp)itr.next()).getType(context);
                            
                            return DatatypeFactory.deriveByUnion( typeNameUri, typeName, m );
                        }
                    });
            }
            
            m[i++] = item.dt;
        }
        
        return new XSDatatypeExp(
            DatatypeFactory.deriveByUnion( typeNameUri, typeName, m ),
            reader.pool );
    }
        
    
//
//
// related interfaces
//
//
    
    /** this object renders the actual datatype object. */
    public interface Renderer {
        /**
         * creates (or retrieves, whatever) the actual, concrete, real
         * XSDatatype object.
         * 
         * <p>
         * This method is typically called from the wrapUp method of the GrammarReader.
         * 
         * @return
         *        the XSDatatype object which this LateBindDatatype object is representing.
         *        It shall not return an instance of LateBindDatatype object.
         * 
         * @param context
         *        If this renderer calls the getBody method of the other
         *        LateBindDatatype objects, then this context should be passed
         *        to the getBody method. This context object is responsible for
         *        detecting recursive references.
         * 
         * @exception DatatypeException
         *        If an error occurs during rendering, the renderer should throw
         *        a DatatypeException instead of trying to report an error by itself.
         *        The caller of this method will report an error message to the appropriate
         *        handler.
         */
        XSDatatype render( RenderingContext context ) throws DatatypeException;
    }
    
    /**
     * this object is used to keep the information about
     * the dependency between late-bind datatype objects.
     * 
     * <p>
     * Consider the following schema:
     * 
     * <PRE><XMP>
     * <xs:simpleType name="foo">
     *   <xs:restriction base="bar">
     *     <xs:minLength value="3"/>
     *   </xs:restriction>
     * </xs:simpleType>
     * <xs:simpleType name="bar">
     *   <xs:restriction base="foo">
     *     <xs:minLength value="3"/>
     *   </xs:restriction>
     * </xs:simpleType>
     * </XMP></PRE>
     * 
     * Since two types are depending on each other, if you call the
     * getBody method of "foo" type, it will call the getBody method of "bar" type.
     * Then in turn it will call "foo" again. So this will result in the
     * infinite recursion.
     * 
     * <p>
     * This context object is used to detect such condition and reports the
     * dependency to the user.
     * 
     * <p>
     * No method is publicly accessible.
     */
    public static class RenderingContext {
        RenderingContext() {}
        
        private final Stack<Object> callStack = new Stack<Object>();
    }
}
