/*
 * @(#)XmlChars.java    1.1 00/08/05
 *
 * Copyright (c) 1998 Sun Microsystems, Inc. All Rights Reserved.
 */

package com.sun.msv.scanner.dtd;

/**
 * Base class for entity declarations as used by the parser.
 *
 * @author David Brownell
 * @author Janet Koenig
 * @version 1.3 00/02/24
 */
class EntityDecl {
    String    name;        // <!ENTITY name ... >

    boolean    isFromInternalSubset;
    boolean    isPE;
}
