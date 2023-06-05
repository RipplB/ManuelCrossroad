package manuel;

// Environment code for project project

import jason.asSyntax.*;
import jason.environment.*;
import jason.asSyntax.parser.*;
import jason.environment.grid.Location;
import jason.runtime.Settings;

import java.rmi.RemoteException;
import java.util.Random;
import java.util.logging.*;

public class Env extends Environment {

    static final int LANE_LENGTH = 20;
    static final int SIZE = 2 * LANE_LENGTH + 6;
    static final int NB_CARS = 1;

    private final Logger logger = Logger.getLogger("project."+Env.class.getName());
    private final Random random = new Random(System.currentTimeMillis());

    private IntersectModel model;

    /** Called before the MAS execution with the args informed in .mas2j */
    @Override
    public void init(String[] args) {
        super.init(args);
        model = new IntersectModel(LANE_LENGTH, NB_CARS);
    }

    @Override
    public boolean executeAction(String agName, Structure action) {
        if (action.getFunctor().equals("start")) {
            for (int i = 0; i < NB_CARS; i++) {
                createCar(i);
            }
        } else if (action.getFunctor().equals("move")) {
            if (action.getArity() < 3)
                return false;
            int side = Integer.parseInt(action.getTerm(0).toString());
            int lane = Integer.parseInt(action.getTerm(1).toString());
            int dist = Integer.parseInt(action.getTerm(2).toString());
            Location newLocation = logicalCoordinateToModelCoordinate(side, lane, dist);
            if (model.getAgAtPos(newLocation) > -1)
                return false;
            model.setNewPos(newLocation.x, newLocation.y, agName);
            updatePercepts();
        }
        else {
            logger.info(() -> String.format("executing: %s, but not implemented!", action));
        }
        if (true) { // you may improve this condition
             informAgsEnvironmentChanged();
        }
        return true; // the action was executed with success
    }

    private void updatePercepts() {
        clearPercepts();
        perceptCars();
    }

    /** Called before the end of MAS execution */
    @Override
    public void stop() {
        super.stop();
    }

    public void createCar(int n) {
        Settings settings = new Settings();
        int side = random.nextInt(3);
        int target;
        do {
            target = random.nextInt(3);
        } while (target != side);
        settings.addOption(Settings.INIT_BELS, String.format("pos(%d,%d,0), target(%d)", side, random.nextInt(2), target));
        try {
            String newAgentName = getEnvironmentInfraTier().getRuntimeServices()
                    .createAgent(String.format("car%d", n), "car.asl", null, null, null, settings, null);
            getEnvironmentInfraTier().getRuntimeServices().startAgent(newAgentName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Location logicalCoordinateToModelCoordinate(int side, int lane, int distance) {
        return switch (side) {
            case 0 -> new Location(LANE_LENGTH + 3 - lane, distance);
            case 1 -> new Location(SIZE - distance - 1, LANE_LENGTH + 2 - lane);
            case 2 -> new Location(LANE_LENGTH + 3 + lane, SIZE - distance - 1);
            case 3 -> new Location(distance, LANE_LENGTH + 3 + lane);
            default -> throw new IllegalStateException("Unexpected value: " + side);
        };
    }

    private void perceptCars() {
        for (int i = 0; i < NB_CARS; i++) {
            LogicalCoordinate coor = LogicalCoordinate.of(model.getAgPos(i));
            addPercept(Literal.parseLiteral(String.format("car(%d, %d, %d)", coor.side, coor.lane, coor.distance)));
        }
    }

}
