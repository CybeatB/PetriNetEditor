import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class TransitionIcon extends JComponent implements TransitionListener, MouseListener, MouseMotionListener, SetArc {
	private static final int L = 64;
	private static final int H = 16;
	private Transition trans;
	private boolean selected;
	private boolean arcing;
	private int x0;
	private int y0;

	TransitionIcon(Transition t) {
		this.trans = t;
		this.trans.addListener(this);
		this.selected = false;
		this.arcing = false;
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.setBounds(this.trans.getX(), this.trans.getY(), L, 2*H);
		this.setVisible(true);
		revalidate();
		repaint();
	}

	protected void paintComponent(Graphics gg) {
		this.setBounds(this.trans.getX(), this.trans.getY(), L, 2*H);
		super.paintComponent(gg);
		Graphics2D g = (Graphics2D)gg;
		if (this.selected == true) {
			g.setColor(Color.RED);
		} else if (this.arcing) {
			g.setColor(Color.BLUE);
		} else {
			g.setColor(Color.BLACK);
		}
		g.fillRect(0, 0, L, H);
		g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
		g.setColor(Color.WHITE);
		g.drawString(trans.getName(), L/4, H/2);
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
		return this.trans.getX() + (L/2);
	}
	public int yPos() {
		return this.trans.getY() + (H/2);
	}

	public int edgeX(int x, int y) {
		int dx = x - this.xPos();
		int dy = y - this.yPos();
		int s = max(abs(dx/(L/2)), abs(dy/(H/2)));
		return (dx/s);
	}
	public int edgeY(int x, int y) {
		int dx = x - this.xPos();
		int dy = y - this.yPos();
		int s = max(abs(dx/(L/2)), abs(dy/(H/2)));
		return (dy/s);
	}

	private int max(int a, int b) {
		return a >= b ? a : b;
	}
	private int abs(int a) {
		return a < 0 ? -a : a;
	}

	public Transition getTransition() {
		return this.trans;
	}

	public void transitionHasChanged() {
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
		if (this.arcing == false) {
			int x = me.getX();
			int y = me.getY();
			this.trans.moveBy(x-x0, y-y0);
		}
		revalidate();
		repaint();
	}
	public void mouseMoved(MouseEvent me) {
		//
	}
}
