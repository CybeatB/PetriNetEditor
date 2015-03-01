import java.util.*;

public class PlaceImpl implements Place {
	private String name;
	private int xpos;
	private int ypos;
	private int marking;
	private int initwgt;
	private Set<Transition> preset;
	private Set<Transition> postset;
	private Set<PlaceListener> listeners;

	PlaceImpl(String n, int x, int y) {
		this.name = n;
		this.xpos = x;
		this.ypos = y;
		this.marking = 0;
		this.initwgt = 0;
		this.preset = new HashSet<Transition>();
		this.postset = new HashSet<Transition>();
		this.listeners = new HashSet<PlaceListener>();
	}

	public Set<Transition> preSet() {
		return this.preset;
	}
	public Set<Transition> postSet() {
		return this.postset;
	}
	public int getInitialArcWeight() {
		return this.initwgt;
	}
	public void setInitialArcWeight(int w) {
		this.marking -= this.initwgt;
		this.initwgt = w;
		this.marking += this.initwgt;
		this.hasChanged();
	}
	public void setMarking(int m) {
		this.marking = m + this.initwgt;
		this.hasChanged();
	}
	public void simFire(int m) {
		this.marking += m;
		if (this.marking <= 0) {
			this.marking = this.initwgt;
		}
		this.hasChanged();
	}
	public void moveBy(int dx, int dy) {
		this.xpos += dx;
		this.ypos += dy;
		this.hasChanged();
	}
	public String getName() {
		return this.name;
	}
	public int getX() {
		return this.xpos;
	}
	public int getY() {
		return this.ypos;
	}
	public int getMarking() {
		return this.marking;
	}
	public String toString() {
		return this.name + "(" + this.xpos + "," + this.ypos + ")";
	}
	public boolean equals(Place p) {
		return this.name.equals(p.getName());
	}
	public void addListener(PlaceListener pl) {
		listeners.add(pl);
		return;
	}
	public void removeListener(PlaceListener pl) {
		listeners.remove(pl);
		return;
	}
	private void hasChanged() {
		for (PlaceListener pl : this.listeners) {
			pl.placeHasChanged();
		}
	}
}
