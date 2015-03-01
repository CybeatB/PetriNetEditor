import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;

public class BlobIcon extends JComponent
    implements BlobListener, MouseListener, MouseMotionListener
{
    private static final int R=30;

    private Blob p;
    private int x0;
    private int y0;
    private boolean isSelected;

    public BlobIcon(Blob p)
    {
	this.p= p;
	isSelected= false;

	this.addMouseMotionListener(this);
	this.addMouseListener(this);
	this.setBounds(p.getX()-R,p.getY()-R,2*R,2*R);
	this.setVisible(true);
	repaint();
    }


    public void setSelected(boolean b)
    {
	if( isSelected==b ){
	    return;
	}

	isSelected= b;
	repaint();
    }


    public boolean isSelected()
    {
	return isSelected;
    }

    protected void paintComponent(Graphics gg)
    {
	System.out.println("BlobIcon.paintComponent");
	super.paintComponent(gg);
	Graphics2D g= (Graphics2D)gg;

	g.setColor(Color.RED);

	if( isSelected ){
	    g.fillArc(0,0,2*R,2*R,0,360);
	}else{
	    g.drawArc(0,0,2*R,2*R,0,360);
	}
    }


    //Bloblistener
    public void blobChanged()
    {
	this.setLocation(p.getX()-R,p.getY()-R);
	repaint();
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
	System.out.println("BlobIcon.mousePressed");
	x0= me.getX();
	y0= me.getY();
    }
    public void mouseReleased(MouseEvent me)
    {
    }
    public void mouseClicked(MouseEvent me)
    {
	System.out.println("BlobIcon.mouseClicked");
	isSelected= !isSelected;
        repaint();
    }



    //MouseMotionListener
    public void mouseDragged(MouseEvent me)
    {
	System.out.println("BlobIcon.mouseDragged");
	int x= me.getX();
	int y= me.getY();
	p.moveBy(x-x0,y-y0);
    }


    public void mouseMoved(MouseEvent me)
    {
    }
}
