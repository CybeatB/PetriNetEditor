import java.util.Set;

public interface Blob
{
    //Add the BlobListener pl to this blob. 
    //Note: A blob can have multiple listeners
    public void addListener(BlobListener pl);


    //Delete the BlobListener pl from this blob.
    public void deleteListener(BlobListener pl);


    //Move the position of this blob 
    //by (dx,dy) from its current position
    public void moveBy(int dx, int dy);
    

    //Return the X position of this blob
    public int getX();
    

    //Return the Y position of this blob
    public int getY();


    //in the form (without the quotes, of course!) :
    //"blobName(xPos,yPos)"  
    public String toString();
}
