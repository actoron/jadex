/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.grammar.util;

import java.util.Iterator;
import java.util.Set;

import com.sun.msv.grammar.AnyNameClass;
import com.sun.msv.grammar.ChoiceNameClass;
import com.sun.msv.grammar.DifferenceNameClass;
import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.NamespaceNameClass;
import com.sun.msv.grammar.NotNameClass;
import com.sun.msv.grammar.SimpleNameClass;
import com.sun.msv.util.StringPair;

/**
 * Minimizes a name class.
 * 
 * Sometimes, a name class could become unnecessarily big. For example,
 * 
 * <PRE><XMP>
 * <choice>
 *   <anyName/>
 *   <anyName/>
 *   <anyName/>
 * </choice>
 * </XMP></PRE>
 * 
 * This procedure converts those name classes to the equivalent small name class.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class NameClassSimplifier {
    
    public static NameClass simplify( NameClass nc ) {
        final Set<StringPair> possibleNames = PossibleNamesCollector.calc(nc);
        final String MAGIC = PossibleNamesCollector.MAGIC;
        
        Set<String> uris = new java.util.HashSet<String>();
        
        Iterator<StringPair> itr = possibleNames.iterator();
        while( itr.hasNext() ) {
            StringPair name = itr.next();
            if( name.localName!=MAGIC ) {
                // a simple name.
                if( nc.accepts(name)==nc.accepts( name.namespaceURI, MAGIC ) ) {
                    itr.remove();
                    continue;
                }
            } else
            if( name.namespaceURI!=MAGIC ) {
                // a ns name
                if( nc.accepts(name)==nc.accepts(MAGIC,MAGIC) ) {
                    itr.remove();
                    continue;
                }
            }
            
            // collect the remainig namespace URIs.
            if( name.namespaceURI!=MAGIC )
                uris.add(name.namespaceURI);
        }
        
        if( !nc.accepts(MAGIC,MAGIC) )
            possibleNames.remove( new StringPair(MAGIC,MAGIC) );
        
        NameClass result = null;
        Iterator<String> jtr = uris.iterator();
        while( jtr.hasNext() ) {
            final String uri = jtr.next();
            
            NameClass local = null;
            itr = possibleNames.iterator();
            while( itr.hasNext() ) {
                final StringPair name = (StringPair)itr.next();
                
                if(!name.namespaceURI.equals(uri))        continue;
                if(name.localName==MAGIC)                continue;
                
                if(local==null)    local = new SimpleNameClass(name);
                else            local = new ChoiceNameClass(local,new SimpleNameClass(name));
            }
            if(possibleNames.contains(new StringPair(uri,MAGIC))) {
                if(local==null)
                    local = new NamespaceNameClass(uri);
                else
                    local = new DifferenceNameClass(new NamespaceNameClass(uri),local);
            }
            
            if(local!=null) {
                if(result==null)    result = local;
                else                result = new ChoiceNameClass(result,local);
            }
        }
        
        if( nc.accepts(MAGIC,MAGIC) ) {
            if(result==null)        result = NameClass.ALL;
            else                    result = new NotNameClass(result);
        }
        
        if( result==null )
            result = AnyNameClass.NONE;
        
        return result;
    }
}
