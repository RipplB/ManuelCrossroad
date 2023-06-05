package manuel;

// Environment code for project project

import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.environment.Environment;
import jason.environment.grid.Location;
import jason.runtime.Settings;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.logging.Logger;

public class Env extends Environment {

    static final int LANE_LENGTH = 10;
    static final int SIZE = 2 * LANE_LENGTH + 6;
    static final int NB_CARS = 10;

    private final Logger logger = Logger.getLogger("project."+Env.class.getName());
    private final Random random = new Random(System.currentTimeMillis());
    private final Map<String, Function<String[], Boolean>> actions;

    private IntersectModel model;

    public Env() {
        actions = new HashMap<>();
        actions.put("start", this::initAllCars);
        actions.put("sleep", this::sleep);
        actions.put("move", this::moveCar);
    }

    /** Called before the MAS execution with the args informed in .mas2j */
    @Override
    public void init(String[] args) {
        super.init(args);
        model = new IntersectModel(LANE_LENGTH, NB_CARS);
    }

    @Override
    public boolean executeAction(String agName, Structure action) {
        try {
            Thread.sleep(190 - 4 * LANE_LENGTH);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (!actions.containsKey(action.getFunctor()))
            logger.info(() -> String.format("executing: %s, but not implemented!", action));

        logger.info(() -> String.format("executing: %s", action));

        String[] actionArguments = new String[action.getArity() + 1];
        actionArguments[0] = agName;
        for (int i = 0; i < action.getArity(); i++) {
            actionArguments[i + 1] = action.getTerm(i).toString();
        }
        boolean result = actions.get(action.getFunctor()).apply(actionArguments);

        updatePercepts();
        informAgsEnvironmentChanged();
        return result; // the action was executed with success
    }

    private boolean initAllCars(String[] arr) {
        for (int i = 0; i < NB_CARS; i++) {
            initCar(i);
        }
        return true;
    }

    private boolean sleep(String[] arr) {
        return true;
    }

    private boolean moveCar(String[] args) {
        if (args.length < 3)
            return false;
        int side = Integer.parseInt(args[1]);
        int lane = Integer.parseInt(args[2]);
        int dist = Integer.parseInt(args[3]);
        Location newLocation = logicalCoordinateToModelCoordinate(side, lane, dist);
        if (model.getAgAtPos(newLocation) > -1) {
            logger.warning("There is something blocking the way");
            return false;
        }
        if (dist >= SIZE)
            recreateCar(args[0]);
        else
            model.setNewPos(newLocation.x, newLocation.y, args[0]);
        return true;
    }

    private void recreateCar(String agName) {
        try {
            getEnvironmentInfraTier().getRuntimeServices().killAgent(agName, null, 0);
            logger.warning(() -> String.format("Killed %s", agName));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        initCar(Integer.parseInt(agName.substring(3)));
    }

    private void updatePercepts() {
        //clearAllPercepts();
        clearPercepts();
        perceptCars();
    }

    public void initCar(int n) {
        int side = random.nextInt(3);
        int lane = random.nextInt(2);
        int target;
        do {
            target = random.nextInt(3);
        } while (target == side);
        Location location = logicalCoordinateToModelCoordinate(side, lane, 0);
        String carName = String.format("car%d", n);
        model.setNewPos(location.x, location.y, carName);
        Settings settings = new Settings();
        String beliefs = String.format("pos(%d,%d,0), target(%d), xdistance(%d)", side, lane, target, LANE_LENGTH);
        logger.info(() -> String.format("Creating car with %s which is on %d %d", beliefs, location.x, location.y));
        settings.addOption(Settings.INIT_BELS, beliefs);
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
            case 0 -> new Location(LANE_LENGTH + 2 - lane, distance);
            case 1 -> new Location(SIZE - distance - 1, LANE_LENGTH + 2 - lane);
            case 2 -> new Location(LANE_LENGTH + 3 + lane, SIZE - distance - 1);
            case 3 -> new Location(distance, LANE_LENGTH + 3 + lane);
            default -> throw new IllegalStateException("Unexpected value: " + side);
        };
    }

    private void perceptCars() {
        for (int i = 0; i < NB_CARS; i++) {
            LogicalCoordinate coor = LogicalCoordinate.of(model.getAgPos(i));
            int finalI = i;
            logger.info(() -> String.format("Car%d is now at (%d, %d, %d)", finalI, coor.side, coor.lane, coor.distance));
            addPercept(Literal.parseLiteral(String.format("car(%d, %d, %d)", coor.side, coor.lane, coor.distance)));
        }
    }

}
