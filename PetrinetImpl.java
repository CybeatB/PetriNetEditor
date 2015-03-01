import java.util.*;
import java.util.regex.*;

public class PetrinetImpl implements Petrinet {
	private Set<PlaceImpl> places;
	private Set<TransitionImpl> transitions;
	private Set<Arc> ptarcs;
	private Set<Arc> tparcs;
	private Set<Initial> initials;
	private Set<PetrinetListener> listeners;

	public PetrinetImpl() {
		this.places = new HashSet<PlaceImpl>();
		this.transitions = new HashSet<TransitionImpl>();
		this.ptarcs = new HashSet<Arc>();
		this.tparcs = new HashSet<Arc>();
		this.initials = new HashSet<Initial>();
		this.listeners = new HashSet<PetrinetListener>();
	}

	public Place newPlace(String name, int x, int y) throws IllegalArgumentException {
		Pattern p = Pattern.compile("[^a-zA-Z0-9]");
		if (p.matcher(name).find()) {
			throw new IllegalArgumentException("Invalid Character in Place Name");
		}
		if (this.findPlace(name) != null) {
			throw new IllegalArgumentException("Attempted to Add Duplicate Place");
		}
		PlaceImpl newplace = new PlaceImpl(name, x, y);
		places.add(newplace);
		this.pingListeners();
		return newplace;
	}
	public void removePlace(Place p) {
		this.places.remove(p);
		for (Transition t : this.transitions) {
			t.preSet().remove(p);
			t.postSet().remove(p);
		}
		this.pingListeners();
	}
	public Place findPlace(String placeName) {
		for (Iterator<PlaceImpl> i = this.places.iterator(); i.hasNext();) {
			Place test = i.next();
			if (test.getName().equals(placeName)) {
				return test;
			}
		}
		return null;
	}
	public Set<Place> getPlaces() {
		return new HashSet<Place>(this.places);
	}

	public Transition newTransition(String name, int x, int y) throws IllegalArgumentException {
		Pattern p = Pattern.compile("[^a-zA-Z0-9]");
		if (p.matcher(name).find()) {
			throw new IllegalArgumentException("Invalid Character in Transition Name");
		}
		if (this.findTransition(name) != null) {
			throw new IllegalArgumentException("Attempted to Add Duplicate Transition");
		}
		TransitionImpl newtrans = new TransitionImpl(name, x, y);
		transitions.add(newtrans);
		this.pingListeners();
		return newtrans;
	}
	public void removeTransition(Transition t) {
		this.transitions.remove(t);
		for (Place p : this.places) {
			p.preSet().remove(t);
			p.postSet().remove(t);
		}
		this.pingListeners();
	}
	public Transition findTransition(String name) {
		for (Iterator<TransitionImpl> i = this.transitions.iterator(); i.hasNext();) {
			Transition test = i.next();
			if (test.getName().equals(name)) {
				return test;
			}
		}
		return null;
	}
	public Set<Transition> getTransitions() {
		return new HashSet<Transition>(this.transitions);
	}

	public void addArc(Place p, Transition t, int w) throws IllegalArgumentException {
		if (!this.places.contains(p) || !this.transitions.contains(t)) {
			throw new IllegalArgumentException("Place or Transition Does Not Exist");
		}
		Arc newarc = new Arc(p,t,w);
		if (this.ptarcs.add(newarc)) {
			p.postSet().add(t);
			t.preSet().add(p);
			this.pingListeners();
			return;
		}
		throw new IllegalArgumentException("Attempted to Add Duplicate Arc");
	}
	public void addArc(Transition t, Place p, int w) throws IllegalArgumentException {
		if (!this.places.contains(p) || !this.transitions.contains(t)) {
			throw new IllegalArgumentException("Place or Transition Does Not Exist");
		}
		Arc newarc = new Arc(t,p,w);
		if (this.tparcs.add(newarc)) {
			t.postSet().add(p);
			p.preSet().add(t);
			this.pingListeners();
			return;
		}
		throw new IllegalArgumentException("Attempted to Add Duplicate Arc");
	}
	public int getArcWeight(Place p, Transition t) {
		Arc search = new Arc(p,t,0);
		for (Iterator<Arc> i = this.ptarcs.iterator(); i.hasNext();) {
			Arc test = i.next();
			if (test.equals(search)) {
				return test.getWeight();
			}
		}
		return 0;
	}
	public int getArcWeight(Transition t, Place p) {
		Arc search = new Arc(t,p,0);
		for (Iterator<Arc> i = this.tparcs.iterator(); i.hasNext();) {
			Arc test = i.next();
			if (test.equals(search)) {
				return test.getWeight();
			}
		}
		return 0;
	}
	public void removeArc(Place p, Transition t) {
		p.postSet().remove(t);
		t.preSet().remove(p);
		Arc rem = new Arc(p, t, 0);
		this.ptarcs.remove(rem);
		this.pingListeners();
	}
	public void removeArc(Transition t, Place p) {
		t.postSet().remove(p);
		p.preSet().remove(t);
		Arc rem = new Arc(t, p, 0);
		this.tparcs.remove(rem);
		this.pingListeners();
	}

	public void addInitialArc(Place p, int w) {
		Initial newini = new Initial(p, w);
		this.initials.add(newini);
		PlaceImpl pi = (PlaceImpl)p;
		pi.setInitialArcWeight(w);
		this.pingListeners();
	}
	public void removeInitialArc(Place p) {
		Initial rem = new Initial(p, 0);
		this.initials.remove(rem);
		PlaceImpl pi = (PlaceImpl)p;
		pi.setInitialArcWeight(0);
		this.pingListeners();
	}

	public void simInitialise() {
		for (PlaceImpl p : this.places) {
			p.setMarking(0);
		}
		this.pingListeners();
	}

	public Set<Transition> simEnabledTransitions() {
		Set<Transition> result = new HashSet<Transition>();
		for (Arc a : this.ptarcs) {
			if (a.getPlace().getMarking() >= a.getWeight()) {
				result.add(a.getTransition());
			}
		}
		return result;
	}

	public void simFire(Transition t) throws IllegalArgumentException {
		if (this.transitions.contains(t) && t != null) {
			Set<Transition> enabled = this.simEnabledTransitions();
			if (enabled.contains(t) == false) {
				throw new IllegalArgumentException("Transition '"+t.toString()+"' Is Not Enabled");
			}
			for (Arc a : this.ptarcs) {
				if (a.getTransition().equals(t)) {
					PlaceImpl pi = (PlaceImpl)a.getPlace();
					pi.simFire(-a.getWeight());
				}
			}
			for (Arc a : this.tparcs) {
				if (a.getTransition().equals(t)) {
					PlaceImpl pi = (PlaceImpl)a.getPlace();
					pi.simFire(a.getWeight());
				}
			}
			this.pingListeners();
		} else {
			throw new IllegalArgumentException("Transition '"+t.toString()+"' Does Not Exist");
		}
	}

	public String toString() {
		String result = new String();
		for (Iterator<PlaceImpl> i = this.places.iterator(); i.hasNext();) {
			result += "PLACE " + i.next().toString() + "\n";
		}
		for (Iterator<TransitionImpl> i = this.transitions.iterator(); i.hasNext();) {
			result += "TRANSITION " + i.next().toString() + "\n";
		}
		for (Iterator<Arc> i = this.ptarcs.iterator(); i.hasNext();) {
			Arc a = i.next();
			result += "ARCPT " + a.getPlace().getName() + "(" + String.valueOf(a.getWeight()) + ")" + a.getTransition().getName() + "\n";
		}
		for (Iterator<Arc> i = this.tparcs.iterator(); i.hasNext();) {
			Arc a = i.next();
			result += "ARCTP " + a.getTransition().getName() + "(" + String.valueOf(a.getWeight()) + ")" + a.getPlace().getName() + "\n";
		}
		for (Iterator<Initial> i = this.initials.iterator(); i.hasNext();) {
			Initial n = i.next();
			result += "INITIAL " + n.getTarget().getName() + "(" + String.valueOf(n.getWeight()) + ")" + "\n";
		}
		return result;
	}

	public void addListener(PetrinetListener pl) {
		this.listeners.add(pl);
	}
	public void removeListener(PetrinetListener pl) {
		this.listeners.remove(pl);
	}
	private void pingListeners() {
		for (PetrinetListener pl : this.listeners) {
			pl.petrinetHasChanged();
		}
	}
}
