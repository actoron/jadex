/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.grammar;

/**
 * Visitor interface for NameClass.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public interface NameClassVisitor {
    
    Object onChoice( ChoiceNameClass nc );
    Object onAnyName( AnyNameClass nc );
    Object onSimple( SimpleNameClass nc );
    Object onNsName( NamespaceNameClass nc );
    Object onNot( NotNameClass nc );
    Object onDifference( DifferenceNameClass nc );
}
