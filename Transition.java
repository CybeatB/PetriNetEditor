import java.util.Set;

public interface Transition
{
    //Return a set containing all places that lead to this Transition
    public Set<Place> preSet();


    //Return a set containing all places reachable from this Transition
    public Set<Place> postSet();


    //Move the position of this Transition 
    //by (dx,dy) from its current position
    public void moveBy(int dx, int dy);
    

    //Return the name of this Transition 
    public String getName();
    

    //Return the X position of this Transition
    public int getX();
    

    //Return the Y position of this Transition
    public int getY();


    //Return a string containing information about this Transition 
    //in the form: transitionName(xPos,yPos)
    public String toString();


    //Add a TransitionListener to this class
    public void addListener(TransitionListener pl);


    //Remove a TransitionListener from this class
    public void removeListener(TransitionListener pl);
}
