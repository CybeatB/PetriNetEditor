import java.util.Set;

public interface Place
{
    //Return a set containing all transitions that lead to this place
    public Set<Transition> preSet();


    //Return a set containing all transitions reachable from this place
    public Set<Transition> postSet();


    //Return the weight of the initial arc, if any
    //If there is no initial arc, returns 0.
    public int getInitialArcWeight();


    //Move the position of this place 
    //by (dx,dy) from its current position
    public void moveBy(int dx, int dy);
    

    //Return the name of this place 
    public String getName();
    

    //Return the X position of this place
    public int getX();
    

    //Return the Y position of this place
    public int getY();


    //Return the current marking of this place
    public int getMarking();


    //Return a string containing information about this place 
    //in the form: placeName(xPos,yPos)
    public String toString();


    //Add a PlaceListener to this class
    public void addListener(PlaceListener pl);


    //Remove a PlaceListener from this class
    public void removeListener(PlaceListener pl);
}
