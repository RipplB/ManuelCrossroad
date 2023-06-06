package manuel;

import jason.environment.grid.GridWorldModel;
import jason.environment.grid.Location;

import java.util.Random;

class IntersectModel extends GridWorldModel {
    public static final int   GREEN  = 8;
    public static final int   RED  = 16;

    private IntersectModelView intersectModelView;

    private LogicalCoordinate logicalCoordinate;

    private final int LaneLength;


    Random random = new Random(System.currentTimeMillis());

    IntersectModel(int LaneLength, int numOfCars) {
        super(2*LaneLength+6, 2*LaneLength+6, numOfCars);
        this.LaneLength = LaneLength;

        intersectModelView  = new IntersectModelView(this,"Pööööcs",600);
        this.setView(intersectModelView);


        //set Lamps
        //side 0 (TOP)
        this.set(RED,LaneLength,LaneLength-1 );
        this.set(RED,LaneLength+1,LaneLength-1 );
        this.set(RED,LaneLength+2,LaneLength-1 );

        //side 1 (RIGHT)
        this.set(RED,LaneLength+6,LaneLength );
        this.set(RED,LaneLength+6,LaneLength+1 );
        this.set(RED,LaneLength+6,LaneLength+2 );

        //side 2 (BOTTOM)
        this.set(RED,LaneLength+3,LaneLength+6 );
        this.set(RED,LaneLength+4,LaneLength+6 );
        this.set(RED,LaneLength+5,LaneLength+6 );

        //side 3 (LEFT)
        this.set(RED,LaneLength-1 ,LaneLength+3 );
        this.set(RED,LaneLength-1 ,LaneLength+4 );
        this.set(RED,LaneLength-1 ,LaneLength+5 );


        //setWalls
        //top left
        this.addWall(0,0,LaneLength-1,LaneLength-1);

        //top right
        this.addWall(LaneLength+6,0, 2*LaneLength+5,LaneLength-1);

        //bottom left
        this.addWall(0,LaneLength+6,LaneLength-1,2*LaneLength+5);

        //bottom right
        this.addWall(LaneLength+6,LaneLength+6,2*LaneLength+5,2*LaneLength+5);
    }

    void setNewPos(int x, int y, String agentName){
        agentName=agentName.substring(3);
        setAgPos(Integer.valueOf(agentName),x,y);
    }

    void greenLight(int side, int lane){
        Location gL = Env.logicalCoordinateToModelCoordinate(side,lane,LaneLength-1);
        this.remove(RED,gL.x,gL.y);
        this.set(GREEN, gL.x, gL.y);
    }

    void redLight(int side, int lane){
        Location gL = Env.logicalCoordinateToModelCoordinate(side,lane,LaneLength-1);
        this.set(0,gL.x,gL.y);
        this.set(RED, gL.x, gL.y);
    }


}