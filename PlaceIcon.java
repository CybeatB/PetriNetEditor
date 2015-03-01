import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class PlaceIcon extends JComponent implements PlaceListener, MouseListener, MouseMotionListener, SetArc {
	private static final int R = 20;
	private static final int P = R-(R/4);
	private static final int D = 6;
	private Place place;
	private boolean arcing;
	private boolean selected;
	private int x0;
	private int y0;

	public PlaceIcon(Place p) {
		this.place = p;
		this.place.addListener(this);
		this.arcing = false;
		this.selected = false;
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.setBounds(this.place.getX()-R, this.place.getY()-R,3*R,3*R);
		this.setVisible(true);
		revalidate();
		repaint();
	}

	protected void paintComponent(Graphics gg) {
		this.setBounds(this.place.getX()-R, this.place.getY()-R,3*R,3*R);
		super.paintComponent(gg);
		Graphics2D g = (Graphics2D)gg;
		if (this.selected == true && this.arcing == false) {
			g.setColor(Color.RED);
			g.fillArc(P, P, 2*R, 2*R, 0, 360);
			g.setColor(Color.BLACK);
		} else {
			g.setColor(Color.BLACK);
			if (this.arcing) {
				g.setColor(Color.BLUE);
			}
			g.drawArc(P, P, 2*R, 2*R, 0, 360);
		}
		g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
		g.drawString(place.getName(), R, 2*R);
		if (place.getInitialArcWeight() > 0) {
			g.drawString("☇ "+place.getInitialArcWeight(), R/4, R);
		}
		if (place.getMarking() > 0) {
			g.fillArc(R+(R/2), (2*R), 2*D, 2*D, 0, 360);
			g.setColor(Color.WHITE);
			g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 8));
			g.drawString(""+place.getMarking(), R+P, (2*R)+(R/2));
		}
	}

	public boolean isSelected() {
		return this.selected;
	}
	public void select() {
		this.selected = true;
		revalidate();
		repaint();
	}
	public void deselect() {
		this.selected = false;
		revalidate();
		repaint();
	}

	public void setArc(boolean a) {
		this.arcing = a;
		repaint();
	}

	public int xPos() {
		return this.place.getX() + R/2;
	}
	public int yPos() {
		return this.place.getY() + R;
	}

	public int edgeX(int x, int y) {
		int dx = x - this.xPos();
		int dy = y - this.yPos();
		int dd = (int)Math.sqrt((dx*dx)+(dy*dy));
		return ((dx*R)/dd);
	}
	public int edgeY(int x, int y) {
		int dx = x - this.xPos();
		int dy = y - this.yPos();
		int dd = (int)Math.sqrt((dx*dx)+(dy*dy));
		return ((dy*R)/dd);
	}

	public Place getPlace() {
		return this.place;
	}

	public void placeHasChanged() {
		revalidate();
		repaint();
	}

	public void mouseEntered(MouseEvent me) {
		//
	}
	public void mouseExited(MouseEvent me) {
		//
	}
	public void mousePressed(MouseEvent me) {
		this.x0 = me.getX();
		this.y0 = me.getY();
	}
	public void mouseReleased(MouseEvent me) {
		this.arcing = false;
		revalidate();
		repaint();
	}
	public void mouseClicked(MouseEvent me) {
		this.selected = !this.selected;
		revalidate();
		repaint();
	}
	public void mouseDragged(MouseEvent me) {
		if (this.arcing == false && this.selected == false) {
			int x = me.getX();
			int y = me.getY();
			this.place.moveBy(x-x0, y-y0);
		}
		revalidate();
		repaint();
	}
	public void mouseMoved(MouseEvent me) {
		//
	}
}
