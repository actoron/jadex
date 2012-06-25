package jadex.xml.stax;

public interface Location {
  int getLineNumber();

  int getColumnNumber();
  
  int getCharacterOffset();

  public String getPublicId();

  public String getSystemId();
}



