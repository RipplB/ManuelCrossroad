package manuel;

import jason.environment.grid.GridWorldModel;
import jason.environment.grid.Location;

import java.util.Random;

class IntersectModel extends GridWorldModel {
    public static final int   GREEN  = 8;
    public static final int   RED  = 16;

    private IntersectModelView intersectModelView;


    Random random = new Random(System.currentTimeMillis());

    private IntersectModel(int LaneLength, int numOfCars) {
        super(2*LaneLength+6, 2*LaneLength+6, numOfCars);

        intersectModelView  = new IntersectModelView(this,"Pööööcs",1000);
        this.setView(intersectModelView);


        //set Lamps
        //side 0 (TOP)
        this.set(RED,LaneLength+1,LaneLength-1 );
        this.set(RED,LaneLength+2,LaneLength-1 );
        this.set(RED,LaneLength+3,LaneLength-1 );

        //side 1 (RIGHT)
        this.set(RED,LaneLength+7,LaneLength+1 );
        this.set(RED,LaneLength+7,LaneLength+2 );
        this.set(RED,LaneLength+7,LaneLength+3 );

        //side 2 (BOTTOM)
        this.set(RED,LaneLength+4,LaneLength+7 );
        this.set(RED,LaneLength+5,LaneLength+7 );
        this.set(RED,LaneLength+6,LaneLength+7 );

        //side 3 (LEFT)
        this.set(RED,LaneLength-1 ,LaneLength+4 );
        this.set(RED,LaneLength-1 ,LaneLength+5 );
        this.set(RED,LaneLength-1 ,LaneLength+6 );


        //setWalls
        //top left
        this.addWall(LaneLength,0,LaneLength,LaneLength);
        this.addWall(0,LaneLength,LaneLength,LaneLength);

        //top right
        this.addWall(LaneLength+6,0,LaneLength+6,LaneLength+6);
        this.addWall(LaneLength+6,LaneLength+6,2*LaneLength+6,LaneLength);

        //bottom left
        this.addWall(LaneLength,LaneLength+6,LaneLength,2*LaneLength+6);
        this.addWall(0,LaneLength,LaneLength,LaneLength+6);

        //bottom right
        this.addWall(LaneLength+6,LaneLength+6,2*LaneLength+6,LaneLength+6);
        this.addWall(LaneLength+6,LaneLength+6,LaneLength+6,2*LaneLength+6);


        // setAgPos(0,0,0);

    }

    void setNewPos(int x, int y, String agentName){
        agentName=agentName.substring(3);
        setAgPos(Integer.valueOf(agentName),x,y);
    }


}