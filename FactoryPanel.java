import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Scanner;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import javax.swing.border.BevelBorder;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class FactoryPanel extends JPanel
  implements MouseListener, MouseMotionListener, FactoryListener
{
    private Factory factory;

    private int x0;
    private int y0;
    private Rectangle rect;
    private HashMap<Blob,BlobIcon> blobs;

    public FactoryPanel(Factory factory)
    {
	//Disable default layout manager
	super(null);
	this.factory= factory;

	rect= new Rectangle(0,0,0,0);
	this.addMouseListener(this);
	this.addMouseMotionListener(this);
	this.setVisible(true);
	blobs= new HashMap<Blob,BlobIcon>();

	//Force an update
	blobsChanged();
    }


    //////////////////////
    //from FactoryListener
    //////////////////////
    public void blobsChanged()
    {
//	System.out.println("FactoryPanel.blobsChanged");
        Set<Blob> actualBlobs= factory.getBlobs();

	//Add new blobs
	for( Blob p: actualBlobs ){
	    if( !blobs.containsKey(p) ){
		//Blob added
		BlobIcon pi= new BlobIcon(p);
		p.addListener(pi);
		blobs.put(p,pi);
		this.add(pi);
	    }
	}
	repaint();
    }


    protected void paintComponent(Graphics gg)
    {
//	System.out.println("FactoryPanel.paintComponent");
	super.paintComponent(gg);
	Graphics2D g= (Graphics2D)gg;

	g.setColor(Color.MAGENTA);
	g.drawRect(rect.x,rect.y,rect.width,rect.height);
    }


    //MouseListener
    public void mouseEntered(MouseEvent me)
    {
    }
    public void mouseExited(MouseEvent me)
    {
    }
    public void mousePressed(MouseEvent me)
    {
	x0= me.getX();
	y0= me.getY();
	rect= new Rectangle(x0,y0,0,0);
    }
    public void mouseReleased(MouseEvent me)
    {
	rect= new Rectangle(0,0,0,0);
	repaint();
    }

    public void mouseClicked(MouseEvent me)
    {
    }


    //MouseMotionListener
    public void mouseDragged(MouseEvent me)
    {
	int x= me.getX();
	int y= me.getY();

	rect= new Rectangle(x0,y0,x-x0,y-y0);

	//See if we have encircled anyone...
	for( Component c: this.getComponents() ){
	    boolean isHit= rect.intersects(c.getBounds());
	    BlobIcon bi= (BlobIcon)c;
	    bi.setSelected(isHit);
	}
	repaint();
    }


    public void mouseMoved(MouseEvent me)
    {
    }
}
