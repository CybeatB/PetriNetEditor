import java.util.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;

public class EditorPanel extends JPanel implements PetrinetListener, MouseListener, MouseMotionListener {
	private Petrinet pnet;
	private Map<Place, PlaceIcon> placeicons;
	private Map<Transition, TransitionIcon> transicons;
	private Rectangle selection;
	private Line2D.Float rubberband;
	private int x0;
	private int y0;
	private int x1;
	private int y1;
	private boolean arcing;
	private SetArc src;
	private SetArc dst;

	public EditorPanel(Petrinet p) {
		super(null);
		this.pnet = p;
		this.arcing = false;
		this.selection = new Rectangle(0,0,0,0);
		this.rubberband = new Line2D.Float(0,0,0,0);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.placeicons = new HashMap<Place, PlaceIcon>();
		this.transicons = new HashMap<Transition, TransitionIcon>();
		this.petrinetHasChanged();
	}

	public void setPetrinet(Petrinet p) {
		this.pnet = p;
		this.petrinetHasChanged();
	}

	public Set<PlaceIcon> selectedPlaces() {
		Set<PlaceIcon> result = new HashSet<PlaceIcon>();
		for (Map.Entry<Place, PlaceIcon> it : this.placeicons.entrySet()) {
			if (it.getValue().isSelected()) {
				result.add(it.getValue());
			}
		}
		return result;
	}
	public Set<TransitionIcon> selectedTransitions() {
		Set<TransitionIcon> result = new HashSet<TransitionIcon>();
		for (Map.Entry<Transition, TransitionIcon> it : this.transicons.entrySet()) {
			if (it.getValue().isSelected()) {
				result.add(it.getValue());
			}
		}
		return result;
	}

	public void addArc() {
		this.arcing = true;
		this.src = null;
		for (Map.Entry<Place, PlaceIcon> it : this.placeicons.entrySet()) {
			it.getValue().deselect();
		}
		for (Map.Entry<Transition, TransitionIcon> it : this.transicons.entrySet()) {
			it.getValue().deselect();
		}
	}

	public void petrinetHasChanged() {
		this.removeAll();
		this.placeicons = new HashMap<Place, PlaceIcon>();
		for (Place p : pnet.getPlaces()) {
			PlaceIcon pi = new PlaceIcon(p);
			p.addListener(pi);
			this.placeicons.put(p, pi);
			this.add(pi);
			pi.addMouseListener(this);
			pi.addMouseMotionListener(this);
		}
		this.transicons = new HashMap<Transition, TransitionIcon>();
		for (Transition t : pnet.getTransitions()) {
			TransitionIcon ti = new TransitionIcon(t);
			t.addListener(ti);
			this.transicons.put(t, ti);
			this.add(ti);
			ti.addMouseListener(this);
			ti.addMouseMotionListener(this);
		}
		revalidate();
		repaint();
	}

	protected void paintComponent(Graphics gg) {
		super.paintComponent(gg);
		Graphics2D g = (Graphics2D)gg;
		g.draw(this.selection);
		for (Place pl : this.pnet.getPlaces()) {
			PlaceIcon p = this.placeicons.get(pl);
			for (Transition tr : pl.postSet()) {
				TransitionIcon t = this.transicons.get(tr);
				int x1 = p.xPos();
				int y1 = p.yPos();
				int x2 = t.xPos();
				int y2 = t.yPos();
				int dx = x2 - x1;
				int dy = y2 - y1;
				int d = (int)Math.sqrt((dx*dx)+(dy*dy));
				int xc = x1 + (dx/2) - ((32*dy)/d);
				int yc = y1 + (dy/2) + ((32*dx)/d);
				int px = p.edgeX(xc, yc) + x1;
				int py = p.edgeY(xc, yc) + y1;
				int tx = t.edgeX(xc, yc) + x2;
				int ty = t.edgeY(xc, yc) + y2;
				g.setColor(Color.BLACK);
				g.draw(new QuadCurve2D.Float(px, py, xc, yc, tx, ty));
				g.drawString(this.pnet.getArcWeight(pl, tr)+"", (px+xc)/2, (py+yc)/2);
				int ax = xc - tx;
				int ay = yc - ty;
				int r = (int)Math.sqrt((ax*ax)+(ay*ay));
				int xh = tx + ((8*ax)/r);
				int yh = ty + ((8*ay)/r);
				int ap = ((4*ay)/r);
				int aq = ((4*ax)/r);
				int[] xpoints = {tx, xh-ap, xh+ap};
				int[] ypoints = {ty, yh+aq, yh-aq};
				g.fillPolygon(xpoints, ypoints, 3);
			}
		}
		for (Transition tr : this.pnet.getTransitions()) {
			TransitionIcon t = this.transicons.get(tr);
			for (Place pl : tr.postSet()) {
				PlaceIcon p = this.placeicons.get(pl);
				int x1 = t.xPos();
				int y1 = t.yPos();
				int x2 = p.xPos();
				int y2 = p.yPos();
				int dx = x2 - x1;
				int dy = y2 - y1;
				int d = (int)Math.sqrt((dx*dx)+(dy*dy));
				int xc = x1 + (dx/2) - ((32*dy)/d);
				int yc = y1 + (dy/2) + ((32*dx)/d);
				int tx = t.edgeX(xc, yc) + x1;
				int ty = t.edgeY(xc, yc) + y1;
				int px = p.edgeX(xc, yc) + x2;
				int py = p.edgeY(xc, yc) + y2;
				g.setColor(Color.BLACK);
				g.draw(new QuadCurve2D.Float(tx, ty, xc, yc, px, py));
				g.drawString(this.pnet.getArcWeight(tr, pl)+"", (tx+xc)/2, (ty+yc)/2);
				int ax = xc - px;
				int ay = yc - py;
				int r = (int)Math.sqrt((ax*ax)+(ay*ay));
				int xh = px + ((8*ax)/r);
				int yh = py + ((8*ay)/r);
				int ap = ((4*ay)/r);
				int aq = ((4*ax)/r);
				int[] xpoints = {px, xh-ap, xh+ap};
				int[] ypoints = {py, yh+aq, yh-aq};
				g.fillPolygon(xpoints, ypoints, 3);
			}
		}
		g.setColor(Color.BLACK);
		if (this.arcing) {
			g.draw(this.rubberband);
		}
	}

	public void mouseEntered(MouseEvent me) {
		if (me.getSource() instanceof SetArc && this.arcing && me.getSource() != this.src) {
			this.dst = (SetArc)me.getSource();
			this.dst.setArc(true);
		}
	}
	public void mouseExited(MouseEvent me) {
		if (me.getSource() instanceof SetArc && this.arcing && me.getSource() != this.src) {
			if (this.dst != null) {
				this.dst.setArc(false);
			}
			this.dst = null;
		}
	}
	public void mousePressed(MouseEvent me) {
		if (me.getSource() == this) {
			this.selection = new Rectangle(me.getX(), me.getY(), 0, 0);
			this.arcing = false;
		}
		if (this.arcing == true) {
			if (me.getSource() instanceof SetArc) {
				this.src = (SetArc)me.getSource();
				this.src.setArc(true);
				if (this.dst == this.src) {
					this.dst = null;
				}
				this.x0 = this.src.xPos();
				this.y0 = this.src.yPos();
			}
		}
		this.x1 = me.getX();
		this.y1 = me.getY();
		revalidate();
		repaint();
	}
	public void mouseReleased(MouseEvent me) {
		if (me.getSource() == this) {
			for (Map.Entry<Place, PlaceIcon> it : this.placeicons.entrySet()) {
				if (this.selection.contains(it.getValue().getX(), it.getValue().getY())) {
					it.getValue().select();
				} else {
					it.getValue().deselect();
				}
			}
			for (Map.Entry<Transition, TransitionIcon> it : this.transicons.entrySet()) {
				if (this.selection.contains(it.getValue().getX(), it.getValue().getY())) {
					it.getValue().select();
				} else {
					it.getValue().deselect();
				}
			}
			this.arcing = false;
			if (this.src instanceof SetArc) {
				((SetArc)this.src).setArc(false);
			}
		}
		if (this.arcing == true && this.dst != null) {
			String weight = (String)JOptionPane.showInputDialog(this,
				"Input the Weight of the New Arc",
				"New Arc",
				JOptionPane.PLAIN_MESSAGE,
				null,
				null,
				"Weight"
			);
			if (weight != null) {
				int w = Integer.parseInt(weight);
				if (this.src instanceof PlaceIcon && this.dst instanceof TransitionIcon) {
					Place p = ((PlaceIcon)this.src).getPlace();
					Transition t = ((TransitionIcon)this.dst).getTransition();
					this.pnet.addArc(p, t, w);
					this.src.setArc(false);
					this.dst.setArc(false);
				} else if (this.src instanceof TransitionIcon && this.dst instanceof PlaceIcon) {
					Transition t = ((TransitionIcon)this.src).getTransition();
					Place p = ((PlaceIcon)this.dst).getPlace();
					this.pnet.addArc(t, p, w);
					this.src.setArc(false);
					this.dst.setArc(false);
				}
			}
			if (this.src != null) {
				this.src.setArc(false);
			}
			if (this.dst != null) {
				this.dst.setArc(false);
			}
			this.arcing = false;
		}
		this.selection = new Rectangle(0,0,0,0);
		this.rubberband.setLine(0,0,0,0);
		revalidate();
		repaint();
	}
	public void mouseClicked(MouseEvent me) {
		for (Map.Entry<Place, PlaceIcon> it : this.placeicons.entrySet()) {
			if (it.getValue() != me.getSource()) {
				it.getValue().deselect();
			}
		}
		for (Map.Entry<Transition, TransitionIcon> it : this.transicons.entrySet()) {
			if (it.getValue() != me.getSource()) {
				it.getValue().deselect();
			}
		}
		if (this.arcing) {
			this.arcing = false;
		}
		if (this.src instanceof SetArc && this.src != null) {
			this.src.setArc(false);
		}
		revalidate();
		repaint();
	}
	public void mouseDragged(MouseEvent me) {
		if (me.getSource() == this) {
			this.selection.add(me.getPoint());
		}
		if (this.arcing) {
			this.rubberband.setLine(x0, y0, x0+me.getX()-x1, y0+me.getY()-y1);
		}
		if (me.getSource() != this) {
			for (PlaceIcon pi : this.selectedPlaces()) {
				if (pi.getPlace() != me.getSource()) {
					pi.getPlace().moveBy(me.getX()-x1, me.getY()-y1);
				}
			}
			for (TransitionIcon ti : this.selectedTransitions()) {
				if (ti.getTransition() != me.getSource()) {
					ti.getTransition().moveBy(me.getX()-x1, me.getY()-y1);
				}
			}
		}
		revalidate();
		repaint();
	}
	public void mouseMoved(MouseEvent me) {
		//
	}
}
