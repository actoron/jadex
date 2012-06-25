package jadex.xml.stax;

public interface ILocation {
  int getLineNumber();

  int getColumnNumber();
  
  int getCharacterOffset();

  public String getPublicId();

  public String getSystemId();
}



