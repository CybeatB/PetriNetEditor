import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Editor extends JFrame
{
    public static void main(String[] args)
    {
	Factory f= new Factory();
	Blob b1= f.addBlob(30,30);
	Blob b2= f.addBlob(100,100);
	Blob b3= f.addBlob(200,100);

	new Editor(f);

	//If you uncomment the next line, the factory will have TWO editors
	//attached to it (they will appear on top of one another on the screen)
	//You will see that you can move the blobs in ne panel, and they
	//automatically move in the other panel.
	//Notice that the selected/nonselected property belongs to the 
	//BlobIcon, not to the Blob, so different blobs can appear "selected"
	//in each editor.

	//UNCOMMENT ME
	//new Editor(f);

	Scanner sc= new Scanner(System.in);
	for(;;){
	    System.out.println(">>");
	    int dx= sc.nextInt();
	    int dy= sc.nextInt();
	    b1.moveBy(dx,dy);
	}
    }



    public Editor(Factory f)
    {
	FactoryPanel fp= new FactoryPanel(f);
	f.addListener(fp);

	this.setPreferredSize(new Dimension(800,400));
	Container contentPane= this.getContentPane();
	contentPane.add(fp);

	this.pack();
	this.setVisible(true);
    }
}
