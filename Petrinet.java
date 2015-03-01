import java.util.Set;

public interface Petrinet 
{
    //Create a new Place and add it to this Petri-net
    //Returns the new place
    //Throws IllegalArgumentException if:
    //the name is not valid or is the same as that
    //of an existing place
    public Place newPlace(String name, int x, int y)
      throws IllegalArgumentException;


    //Remove a place from the Petri-net
    //If the place does not exist, returns without error
    public void removePlace(Place p);


    //Find and return the Place with the given name
    //If no place exists with given name, return NULL
    public Place findPlace(String placeName);


    //Return a set containing all the places in this Petri-net
    public Set<Place> getPlaces();
    

    //Create a new Transition and add it to this Petri-net
    //Returns the new transition
    //Throws IllegalArgumentException if:
    //the name is not valid or is the same as that
    //of an existing transition
    public Transition newTransition(String name, int x, int y)
      throws IllegalArgumentException;


    //Remove a transition from the Petri-net
    //If the transition does not exist, returns without error
    public void removeTransition(Transition t);


    //Find and return the Transition with the given name
    //If no transition exists with given name, return NULL
    public Transition findTransition(String name);


    //Return a set containing all the transitions in this Petri-net
    public Set<Transition> getTransitions();
    

    //Add a new arc from place, p, to transition, t, with weight, w
    //Create a new arc and add it to this Petri-net
    //Throws IllegalArgumentException if:
    //  Place p does not exist or
    //  Transition t does not exist
    //  w is not a valid value
    public void addArc(Place p, Transition t, int w)
      throws IllegalArgumentException;


    //Add a new arc from transition, t, to place, p, with weight w
    //Throws IllegalArgumentException if:
    //  Transition t does not exist
    //  Place p does not exist or
    //  w is not a valid value
    public void addArc(Transition t, Place p, int w) 
      throws IllegalArgumentException;


    //Get the weight of the arc from place, p, to transition, t.
    //Returns 0 if there is no arc.
    public int getArcWeight(Place p, Transition t);


    //Get the weight of the arc from transition, t, to place, p.
    //Returns 0 if there is no arc.
    public int getArcWeight(Transition t, Place p);


    //Remove the arc from place, p, to transition, t.
    //If the Arc does not exist, returns without error
    public void removeArc(Place p, Transition t);


    //Remove the arc from transition, t, to place p.
    //If the Arc does not exist, returns without error
    public void removeArc(Transition t, Place p);


    //Add an initial arc to place, p, with weight w
    //Throws IllegalArgumentException if:
    //  Place p does not exist
    //  w is not a valid value
    public void addInitialArc(Place p, int w)
      throws IllegalArgumentException;


    //Remove the initial arc from place, p.
    //If the place does not exist, returns without error
    //If there was no initial arc, returns without error
    public void removeInitialArc(Place p);


    //Initialise the simulation of this  Petri-net to its initial marking
    public void simInitialise();


    //Returns a set containing all the transitions that are currently
    //enabled.
    public Set<Transition> simEnabledTransitions();


    //Update the simulation by firing transition, t.
    //throws IllegalArgumentException, if t is not enabled.
    public void simFire(Transition t)
      throws IllegalArgumentException;


    //Return a string describing this Petri-net
    //Returns a string that contains (in this order):
    //for each place in the Petri-net, a line (terminated by \n)
    //  PLACE followed by the toString result for that place
    //for each transition in the Petri-net, a line (terminated by \n)
    //  TRANSITION followed by the toString result for that transition
    //for each arc from a place to a a transition, a line (terminated by \n)
    //  ARCPT nameOfPlace(weight)nameOfTransition
    //for each arc from a place to a a transition, a line (terminated by \n)
    //  ARCTP nameOfTransition(weight)nameOfPlace
    //for each initial arc in the Petri-net, a line (terminated by \n)
    //  INITIAL nameOfPlace(weight)
    public String toString();


    //Add a PetrinetListener to this class
    public void addListener(PetrinetListener pl);


    //Remove a PetrinetListener from this class
    public void removeListener(PetrinetListener pl);
}
