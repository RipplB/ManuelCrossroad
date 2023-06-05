package manuel;

// Environment code for project project

import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.environment.Environment;
import jason.environment.grid.Location;
import jason.runtime.Settings;

import java.util.Random;
import java.util.logging.Logger;

public class Env extends Environment {

    static final int LANE_LENGTH = 10;
    static final int SIZE = 2 * LANE_LENGTH + 6;
    static final int NB_CARS = 2;

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
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (action.getFunctor().equals("start")) {
            for (int i = 0; i < NB_CARS; i++) {
                initCar(i, true);
            }
        } else if (action.getFunctor().equals("move")) {
            if (action.getArity() < 3)
                return false;
            int side = Integer.parseInt(action.getTerm(0).toString());
            int lane = Integer.parseInt(action.getTerm(1).toString());
            int dist = Integer.parseInt(action.getTerm(2).toString());
            Location newLocation = logicalCoordinateToModelCoordinate(side, lane, dist);
            if (model.getAgAtPos(newLocation) > -1) {
                logger.warning("There is something blocking the way");
                return false;
            }
            if (dist >= SIZE)
                initCar(agName);
            else
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

    private void initCar(String agName) {
        initCar(Integer.parseInt(agName.substring(3)), false);
    }

    private void updatePercepts() {
        clearPercepts();
        perceptCars();
    }

    public void initCar(int n, boolean create) {
        int side = random.nextInt(3);
        int lane = random.nextInt(2);
        int target;
        do {
            target = random.nextInt(3);
        } while (target != side);
        Location location = logicalCoordinateToModelCoordinate(side, lane, 0);
        String carName = String.format("car%d", n);
        model.setNewPos(location.x, location.y, carName);
        if (!create)
            return;
        Settings settings = new Settings();
        settings.addOption(Settings.INIT_BELS, String.format("pos(%d,%d,0), target(%d)", side, lane, target));
        try {
            String newAgentName = getEnvironmentInfraTier().getRuntimeServices()
                    .createAgent(carName, "car.asl", null, null, null, settings, null);
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
