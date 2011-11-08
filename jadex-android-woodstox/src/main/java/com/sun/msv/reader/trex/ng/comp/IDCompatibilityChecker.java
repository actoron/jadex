package com.sun.msv.reader.trex.ng.comp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.relaxng.datatype.Datatype;
import org.xml.sax.Locator;

import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.DataExp;
import com.sun.msv.grammar.DataOrValueExp;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.ListExp;
import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.SimpleNameClass;
import com.sun.msv.grammar.ValueExp;
import com.sun.msv.grammar.util.ExpressionWalker;
import com.sun.msv.grammar.util.RefExpRemover;
import com.sun.msv.util.StringPair;

/**
 * checks the compatibility of RELAX NG grammar with the ID/IDREF feature.
 */
class IDCompatibilityChecker extends CompatibilityChecker {
    
    IDCompatibilityChecker( RELAXNGCompReader reader ) {
        super(reader);
    }
    
    protected void setCompatibility( boolean val ) {
        grammar.isIDcompatible = val;
    }
    
    private static class IDAttMap {
        final ElementExp sampleDecl;
        final Map<StringPair,Object> idatts = new java.util.HashMap<StringPair,Object>();
        
        IDAttMap( ElementExp e ) { this.sampleDecl=e; }
    }
    
    public void test( ) {
        grammar.isIDcompatible = true;
        
        // a map from element names(StringPair) to DefAttMap
        final Map<StringPair,Object> name2value = new HashMap<StringPair,Object>();
        
        // a set of all ElementExps in the grammar.
        final Set<ElementExp> elements = new HashSet<ElementExp>();
        
        final RefExpRemover remover = new RefExpRemover(reader.pool,false);
        
        /*
        The first pass
        --------------
        
        create a "(element name,attribute name)->Id datatype name" map.
        Also detects invalid use of datatypes.
        */
        reader.getGrammar().visit( new ExpressionWalker(){
            
            /** current element name. Only available when in a simple-name element */
            private StringPair elementName=null;
            /** current element. */
            private ElementExp curElm=null;
            
            private IDAttMap curAtts = null;
            
            public void onElement( ElementExp exp ) {
                if(!elements.add(exp))
                    return;    // this element has already processed.
                
                StringPair _en = elementName;
                IDAttMap _curAtts = curAtts;
                ElementExp _curElm = curElm;
                
                NameClass nc = exp.getNameClass();
                if(nc instanceof SimpleNameClass) {
                    elementName = new StringPair((SimpleNameClass)nc);
                    curAtts = (IDAttMap)name2value.get(elementName);    // maybe null.
                } else
                    elementName = null;
                curElm = exp;
                
//                System.out.println("tested:" + 
//                    com.sun.msv.grammar.util.ExpressionPrinter.printContentModel(
//                    exp.contentModel.visit(remover)));
                // visit the content model, but remove reference exps first.
                exp.contentModel.visit(remover).visit(this);
                
                if( elementName!=null && curAtts!=null )
                    name2value.put(elementName,curAtts);
                
                elementName = _en;
                curAtts = _curAtts;
                curElm = _curElm;
            }
            
            public void onAttribute( AttributeExp exp ) {
                
                if(!(exp.exp instanceof DataOrValueExp)) {
                    // otherwise visit the content model normally
                    // so that we can find any invalid use of ID/IDREF types.
                    exp.exp.visit(this);
                    return;
                }
                
                DataOrValueExp texp = (DataOrValueExp)exp.exp;
                            
                if(texp.getType().getIdType()==Datatype.ID_TYPE_NULL) {
                    // if this type is not ID/IDREF type, then it's OK
                    return;
                }
                
                if(!(exp.nameClass instanceof SimpleNameClass)) {
                    reportCompError(
                        new Locator[]{reader.getDeclaredLocationOf(exp)},
                        CERR_ID_TYPE_WITH_NON_SIMPLE_ATTNAME,
                        new Object[]{
                            texp.getName().localName,
                            getSemanticsStr(texp.getType().getIdType())} );
                    return;
                }
                    
                StringPair attName = new StringPair((SimpleNameClass)exp.nameClass);
                
                if( elementName==null ) {
                    reportCompError(
                        new Locator[]{
                            reader.getDeclaredLocationOf(exp),
                            reader.getDeclaredLocationOf(curElm)},
                        CERR_ID_TYPE_WITH_NON_SIMPLE_ELEMENTNAME,
                        new Object[]{
                            texp.getName().localName,
                            getSemanticsStr(texp.getType().getIdType())} );
                    return;
                }
                    
                // the enclosing attribute name is simple, and
                // the enclosing element name is simple, too.
                // this is the only place we can have ID/IDREF types.
                                    
                // store that this attribute is used for ID/IDREF.
                if(curAtts==null) {
                    curAtts = new IDAttMap(curElm);
                }
                curAtts.idatts.put(attName,texp.getName());
                            
            }
            
            public void onData( DataExp exp )    { checkIdType(exp); }
            public void onValue( ValueExp exp ) { checkIdType(exp); }
            private void checkIdType( DataOrValueExp exp ) {
                if(exp.getType().getIdType()!=Datatype.ID_TYPE_NULL) {
                    // ID/IDREF type in all other locations are subject to
                    // a compatibility error.
                    reportCompError(
                        new Locator[]{reader.getDeclaredLocationOf(exp)},
                        CERR_MALPLACED_ID_TYPE,
                        new Object[]{
                            exp.getName().localName,
                            getSemanticsStr(exp.getType().getIdType())});
                }
            }
        });
        
        
        if(!grammar.isIDcompatible)
            // if an compatibility error has been found, abort further check.
            return;
        
        /*
        2nd pass
        ========
        
        make sure that no other attributes are competing with id attributes.
        */
        Iterator<ElementExp> itr = elements.iterator();
        final Vector<Object> vec = new Vector<Object>();    // IDAttMaps of the competing elements
        while( itr.hasNext() ) {
            final ElementExp eexp = (ElementExp)itr.next();
            
            // list up all competing elements.
            vec.clear();
            Iterator<Map.Entry<StringPair,Object>> jtr = name2value.entrySet().iterator();
            while(jtr.hasNext()) {
                Map.Entry<StringPair,Object> e = jtr.next();
                if( eexp.getNameClass().accepts((StringPair)e.getKey()) )
                    vec.add( e.getValue()/*IDAttMap*/ );
            }
            
            if(vec.size()==0)
                continue;    // this element does not comete with anything.
                            // no need to check
            
            // make sure that no attributes are actually competing.
            eexp.contentModel.visit(remover).visit( new ExpressionWalker() {
                public void onElement( ElementExp exp ) {
                    return;    // do not recurse child elements.
                }
                public void onAttribute( AttributeExp exp ) {
                    if(exp.exp instanceof DataOrValueExp) {
                        DataOrValueExp texp = (DataOrValueExp)exp.exp;
                        if(texp.getType().getIdType()!=Datatype.ID_TYPE_NULL) {
                            // if the schema is OK with the 1st pass check
                            // and if this element contains the ID type, then
                            // this element must be simple-named.
                            // so at most one IDAttMap can match it.
                            _assert(vec.size()==1);
                            
                            // by the same assumption, the attribute name must be
                            // simple.
                            SimpleNameClass attName = (SimpleNameClass)exp.nameClass;
                            
                            IDAttMap iam = (IDAttMap)vec.get(0);
                            if(!texp.getName().equals(iam.idatts.get(new StringPair(attName))))
                                reportCompError(
                                    new Locator[]{
                                        reader.getDeclaredLocationOf(exp),
                                        reader.getDeclaredLocationOf(iam.sampleDecl)},
                                    CERR_COMPETING,
                                    new Object[]{
                                        texp.getName().localName,
                                        getSemanticsStr(texp.getType().getIdType())
                                    }
                                    );
                            
                            return;
                        }
                    }
                    
                    // otherwise, make sure that this attribute name doesn't 
                    // compete with ID.
                    for( int i=vec.size()-1; i>=0; i-- ) {
                        IDAttMap iam = (IDAttMap)vec.get(i);
                        Iterator<Map.Entry<StringPair,Object>> jtr = iam.idatts.entrySet().iterator();
                        while( jtr.hasNext() ) {
                            Map.Entry<StringPair,Object> e = jtr.next();
                            if(exp.nameClass.accepts(e.getKey() )) {
                                // competing attributes
                                reportCompError(
                                    new Locator[]{
                                        reader.getDeclaredLocationOf(exp),
                                        reader.getDeclaredLocationOf(eexp),
                                        reader.getDeclaredLocationOf(iam.sampleDecl)},
                                    CERR_COMPETING2,
                                    new Object[]{
                                        ((StringPair)e.getKey()).localName,
                                        ((StringPair)e.getValue()).localName});
                                return;
                            }
                        }
                    }
                }
                public void onList( ListExp exp ) {
                    // since there can be no AttributeExp within a list,
                    // there is no need to visit its children.
                }
            });
        }
    }
    
    private static String getSemanticsStr( int type ) {
        switch(type) {
        case Datatype.ID_TYPE_ID:        return "ID";
        case Datatype.ID_TYPE_IDREF:    return "IDREF";
        case Datatype.ID_TYPE_IDREFS:    return "IDREFS";
        default:                        throw new Error();
        }
    }
    
    private static final void _assert( boolean b ) {
        if(!b)    throw new Error("assertion failed");
    }
    
    
    private static final String CERR_MALPLACED_ID_TYPE = // arg:2
        "RELAXNGReader.Compatibility.ID.MalplacedIDType";
    private static final String CERR_ID_TYPE_WITH_NON_SIMPLE_ATTNAME = // arg:2
        "RELAXNGReader.Compatibility.ID.IDTypeWithNonSimpleAttName";
    private static final String CERR_ID_TYPE_WITH_NON_SIMPLE_ELEMENTNAME = // arg:2
        "RELAXNGReader.Compatibility.ID.IDTypeWithNonSimpleElementName";
    private static final String CERR_COMPETING = // arg:2
        "RELAXNGReader.Compatibility.ID.Competing";
    private static final String CERR_COMPETING2 = // arg:0
        "RELAXNGReader.Compatibility.ID.Competing2";
}
