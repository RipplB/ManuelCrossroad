package manuel;

import jason.environment.grid.GridWorldModel;
import jason.environment.grid.Location;

import java.util.Random;

class IntersectModel extends GridWorldModel {


    public static final int   GREEN  = 8;

    public static final int   RED  = 16;


    Random random = new Random(System.currentTimeMillis());

    private IntersectModel() {
        super(46, 46, 20);
        // setAgPos(0,0,0);

    }

    void nextSlot() throws Exception {
        Location r1 = getAgPos(0);
        r1.x++;
        if (r1.x == getWidth()) {
            r1.x = 0;
            r1.y++;
        }
        // finished searching the whole grid
        if (r1.y == getHeight()) {
            return;
        }
        setAgPos(0, r1);
        setAgPos(1, getAgPos(1)); // just to draw it in the view
    }

    void setNewPos(int x, int y, String agentName){
        agentName=agentName.substring(3);
        setAgPos(Integer.valueOf(agentName),x,y);
    }

    void moveTowards(int x, int y) throws Exception {
        Location r1 = getAgPos(0);
        if (r1.x < x)
            r1.x++;
        else if (r1.x > x)
            r1.x--;
        if (r1.y < y)
            r1.y++;
        else if (r1.y > y)
            r1.y--;
        setAgPos(0, r1);
        setAgPos(1, getAgPos(1)); // just to draw it in the view
    }

    void turnRight(){

    }

    void turnLeft(){

    }

    void pickGarb() {
        // r1 location has garbage
        if (model.hasObject(GARB, getAgPos(0))) {
            // sometimes the "picking" action doesn't work
            // but never more than MErr times
            if (random.nextBoolean() || nerr == MErr) {
                remove(GARB, getAgPos(0));
                nerr = 0;
                r1HasGarb = true;
            } else {
                nerr++;
            }
        }
    }
    void dropGarb() {
        if (r1HasGarb) {
            r1HasGarb = false;
            add(GARB, getAgPos(0));
        }
    }
    void burnGarb() {
        // r2 location has garbage
        if (model.hasObject(GARB, getAgPos(1))) {
            remove(GARB, getAgPos(1));
        }
    }
}