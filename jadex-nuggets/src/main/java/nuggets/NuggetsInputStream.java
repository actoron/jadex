package nuggets;


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
//    private final BeanAssembler	ba;
//	private InputStreamReader	isr;


	/** 
     * Constructor for NuggetsOutputStream.
     * @param is
     * @throws IOException 
     * /
    public NuggetsInputStream(InputStream is) throws IOException {
        isr = new InputStreamReader(is);
        ba = new BeanAssembler();
        ba.setReader(new JavaXMLReader());
    }*/


	/** 
     * @return the object that hast been written to the file
     * /
    public Object readObject() {
        return ba.assemble(isr);
    }
    
    /**
     * @throws IOException  
     * 
     * /
    public void close() throws IOException {
    	isr.close();
    }*/
    
}