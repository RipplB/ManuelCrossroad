package manuel;

import jason.environment.grid.GridWorldModel;
import jason.environment.grid.Location;

class IntersectModel extends GridWorldModel {
    public static final int   GREEN  = 8;
    public static final int   RED  = 16;
    public static final int AMBULANCE = 32;

    private IntersectModelView intersectModelView;

    private final int laneLength;


    IntersectModel(int laneLength, int numOfCars,Env env) {
        super(2*laneLength+6, 2*laneLength+6, numOfCars);
        this.laneLength = laneLength;

        intersectModelView  = new IntersectModelView(this,"Smart Intersect",900,env);
        this.setView(intersectModelView);

        //set Lamps
        //side 0 (TOP)
        this.set(RED,laneLength,laneLength-1 );
        this.set(RED,laneLength+1,laneLength-1 );
        this.set(RED,laneLength+2,laneLength-1 );

        //side 1 (RIGHT)
        this.set(RED,laneLength+6,laneLength );
        this.set(RED,laneLength+6,laneLength+1 );
        this.set(RED,laneLength+6,laneLength+2 );

        //side 2 (BOTTOM)
        this.set(RED,laneLength+3,laneLength+6 );
        this.set(RED,laneLength+4,laneLength+6 );
        this.set(RED,laneLength+5,laneLength+6 );

        //side 3 (LEFT)
        this.set(RED,laneLength-1 ,laneLength+3 );
        this.set(RED,laneLength-1 ,laneLength+4 );
        this.set(RED,laneLength-1 ,laneLength+5 );

        //setWalls
        //top left
        this.addWall(0,0,laneLength-1,laneLength-1);

        //top right
        this.addWall(laneLength+6,0, 2*laneLength+5,laneLength-1);

        //bottom left
        this.addWall(0,laneLength+6,laneLength-1,2*laneLength+5);

        //bottom right
        this.addWall(laneLength+6,laneLength+6,2*laneLength+5,2*laneLength+5);
    }

    void moveAmbulance(Location fromLocation, Location nextLocation) {
        if (nextLocation != null)
            add(AMBULANCE, nextLocation);
        if (fromLocation != null)
            remove(AMBULANCE, fromLocation);
    }

    void setNewPos(int x, int y, String agentName){
        agentName = agentName.substring(3);
        setAgPos(Integer.parseInt(agentName), x, y);
    }

    void greenLight(int side, int lane){
        Location gL = Env.logicalCoordinateToModelCoordinate(side,lane, laneLength - 1);
        this.remove(RED, gL.x, gL.y);
        this.add(GREEN, gL.x, gL.y);
    }

    void redLight(int side, int lane){
        Location gL = Env.logicalCoordinateToModelCoordinate(side,lane, laneLength - 1);
        this.remove(GREEN, gL.x, gL.y);
        this.add(RED, gL.x, gL.y);
    }

    @Override
    public void setAgPos(int ag, Location l) {
        Location oldLoc = getAgPos(ag);
        agPos[ag] = l;
        add(AGENT, l.x, l.y);
        if (oldLoc != null) {
            remove(AGENT, oldLoc.x, oldLoc.y);
        }
    }

}