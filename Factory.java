import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class Factory
{
    private ArrayList<FactoryListener> listeners;
    private HashSet<BlobImpl> blobs;

    public Factory()
    {
	blobs= new HashSet<BlobImpl>();
	listeners= new ArrayList<FactoryListener>();
    }


    //Add the FactoryListener fl to this factory.
    //Note: A factory can have multiple listeners
    public void addListener(FactoryListener fl)
    {
	listeners.add(fl);
    }


    //Delete the FactoryListener fl from this factory.
    public void deleteListener(FactoryListener fl)
    {
	listeners.remove(fl);
    }



    //Create a new Blob and add it to this factory
    //Return the new blob
    public Blob addBlob(int xPos, int yPos)
      throws IllegalArgumentException
    {
	System.out.println("factory.addBlob");
	BlobImpl bi= new BlobImpl(xPos,yPos);
	blobs.add(bi);
	blobsChanged();
	return bi;
    }


    public Set<Blob> getBlobs()
    {
	Set<Blob> blobSet= new HashSet<Blob>();
	for( BlobImpl bi: blobs ){
	    blobSet.add(bi);
	}
	return blobSet;
    }


    //Delete the blob p from the factory
    public void deleteBlob(Blob b)
    {
	BlobImpl bi= (BlobImpl)b;
	blobs.remove(bi);
	blobsChanged();
    }


    private void blobsChanged()
    {
	System.out.println("factory.blobsChanged");
	for( FactoryListener fl: listeners ){
	    fl.blobsChanged();
	}
    }


    //Returns a string that contains (in this order):
    //for each blob in the factory, a line (terminated by \n)
    //  BLOB followed the toString result for that blob
    //for each road in the factory, a line (terminated by \n)
    public String toString()
    {
	StringBuilder sb= new StringBuilder();
	for( Blob p: blobs ){
	    sb.append("BLOB ");
	    sb.append(p.toString());
	    sb.append("\n");
	}
	return sb.toString();
    }



    /////////////////////////////////////////////////////
    private static class BlobImpl
      implements Blob
    {
	private int xc;
	private int yc;
	private ArrayList<BlobListener> listeners;
	
	public BlobImpl(int xc, int yc)
	{
	    this.xc= xc;
	    this.yc= yc;
	    listeners= new ArrayList<BlobListener>();
	}
	

	public void addListener(BlobListener bl)
	{
	    listeners.add(bl);
	}
	
	public void deleteListener(BlobListener bl)
	{
	    listeners.remove(bl);
	}
	
	private void blobChanged()
	{
	System.out.println("blob.blobChanged");
	    for( BlobListener bl: listeners ){
		bl.blobChanged();
	    }
	}
	
	
	
	public void moveBy(int dx, int dy)
	{
	    System.out.println("blob.moveBy");
	    xc= xc+dx;
	    yc= yc+dy;
	    blobChanged();
	}
	
	
	public int getX()
	{
	    return xc;
	}
	
	
	public int getY()
	{
	    return yc;
	}
	
	
	public String toString()
	{
	    return "("+xc+","+yc+")";
	}
    }
}
