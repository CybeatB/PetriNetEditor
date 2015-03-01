public class PetrinetFormatException extends Exception
{
    private int lineNr;
    
    public PetrinetFormatException(int lineNr, String msg)
    {
	super(msg);
	this.lineNr= lineNr;
    }
    
    
    public String toString()
    {
	return lineNr+":0:"+super.toString();
    }
}
