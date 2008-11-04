package nuggets;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/*
 * NuggetsInputStream.java
 * Copyright (c) 2005 by University of Hamburg. All Rights Reserved.
 * Departament of Informatics. 
 * Distributed Systems and Information Systems.
 *
 * Created by walczak on Feb 20, 2006.  
 * Last revision $Revision: 6926 $ by:
 * $Author: braubach $ on $Date: 2008-09-28 22:16:58 +0200 (So, 28 Sep 2008) $.
 */

/** NuggetsInputStream 
 * @author walczak
 * @since  Feb 20, 2006
 */
public class NuggetsInputStream
{
    private final BeanAssembler	ba;
	private InputStreamReader	isr;


	/** 
     * Constructor for NuggetsOutputStream.
     * @param is
     * @throws IOException 
     */
    public NuggetsInputStream(InputStream is) throws IOException {
        isr = new InputStreamReader(is);
        ba = new BeanAssembler();
        ba.setReader(new JavaXMLReader());
    }


	/** 
     * @return the object that hast been written to the file
     */
    public Object readObject() {
        return ba.assemble(isr);
    }
    
    /**
     * @throws IOException  
     * 
     */
    public void close() throws IOException {
    	isr.close();
    }
    
}


/* 
 * $Log$
 * Revision 1.3  2006/02/23 17:46:25  walczak
 * LF
 *
 * Revision 1.2  2006/02/20 15:00:56  walczak
 * ------------------------------------
 *
 * Revision 1.1  2006/02/20 14:11:24  walczak
 * Two help classes for the nuggets
 *
 */