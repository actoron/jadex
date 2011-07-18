package com.wutka.dtd;

public class DTDParseException extends java.io.IOException
{
    public String uriID = "";
    public int lineNumber;
    public int column;

    public DTDParseException()
    {
        lineNumber=-1;
        column=-1;
    }

    public DTDParseException(String message)
    {
        super(message);
        lineNumber=-1;
        column=-1;
    }

    public DTDParseException(String message, int line, int col)
    {
        super("At line "+line+", column "+col+": "+message);
        lineNumber = line;
        column = col;
    }

    public DTDParseException(String id, String message, int line, int col)
    {
        super(((null != id && id.length() > 0) ? "URI " + id + " at " : "At ")
                + "line " + line + ", column " + col + ": " + message);
        if (null != id)
            uriID = id;

        lineNumber = line;
        column = col;
    }

    public String getId() { return(uriID); }
    public int getLineNumber() { return lineNumber; }
    public int getColumn() { return column; }
}
