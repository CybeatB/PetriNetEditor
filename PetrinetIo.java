import java.io.Reader;
import java.io.IOException;
import java.io.Writer;

public interface PetrinetIo 
{
    //This class handles reading and writing of PETRI representations as 
    //described in the practical specification

    //Read the description of a Petri-netn from the 
    //Reader,r , and transfer it to Petri-net, p.
    public void read (Reader r, Petrinet p) 
      throws IOException, PetrinetFormatException;
    
    
    //Write a representation of the Petri-net, p, to the Writer, w
    public void write(Writer w, Petrinet p)
      throws IOException;
}
