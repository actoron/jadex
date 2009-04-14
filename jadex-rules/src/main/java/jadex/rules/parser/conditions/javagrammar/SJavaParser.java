package jadex.rules.parser.conditions.javagrammar;

import jadex.commons.SReflect;
import jadex.rules.state.OAVObjectType;
import jadex.rules.state.OAVTypeModel;

import org.antlr.runtime.TokenStream;

/**
 *  Static helper methods for Java condition parser.
 */
// Currently not used (todo: replace syntactic predicates in primaryPrefix() with semantic ones)
public class SJavaParser
{
	/**
     *  Lookahead for a type.
     *  @param input	The token stream.
     *  @param tmodel	The OAV type model.
     *  @return	The token index of the last token (identifier) or -1 if no type could be matched.
     */
    protected static int	lookaheadType(TokenStream input, OAVTypeModel tmodel, String[] imports)
    {
    	int	index	= 1;
    	String	typename	= input.LT(index).getText();
    	OAVObjectType	type	= null;
    	while(type==null && index!=-1)
    	{
	    	try
	    	{
	        	type	= tmodel.getObjectType(typename);	        	
	    	}
	    	catch(Throwable e)
	    	{
	    		Class	clazz	= SReflect.findClass0(typename, imports, tmodel.getClassLoader());
	    		if(clazz!=null)
	    		{
	    			type	= tmodel.getJavaType(clazz);
	    		}
	    		else if(input.get(index+1).equals("."))
	    		{
	    			index	+= 2;
	    			typename	+= "." + input.get(index).getText();
	    		}
	    		else
	    		{
	    			index	= -1;
	    		}
	    	}
    	}
    	
    	return index;
    }

    /**
     *  Lookahead for an existential declaration (type var).
     *  @param input	The token stream.
     *  @param tmodel	The OAV type model.
     *  @return	True for an existential declaration.
     */
    protected static boolean	lookaheadExistential(TokenStream input, OAVTypeModel tmodel, String[] imports)
    {
    	int index	= lookaheadType(input, tmodel, imports);
    	return index!=-1 && input.LA(index+1)==JavaJadexLexer.IDENTIFIER;
    }

    /**
     *  Lookahead for a static field.
     *  @param input	The token stream.
     *  @param tmodel	The OAV type model.
     *  @return	True for a static field.
     */
    protected static boolean	lookaheadStaticField(TokenStream input, OAVTypeModel tmodel, String[] imports)
    {
    	int index	= lookaheadType(input, tmodel, imports);
    	return index!=-1 &&	input.LT(index+1).getText().equals(".") && input.LA(index+2)==JavaJadexLexer.IDENTIFIER;
    }
}
