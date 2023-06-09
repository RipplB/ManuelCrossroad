package manuel;

// Environment code for project project

import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.environment.Environment;
import jason.environment.grid.Location;
import jason.runtime.RuntimeServices;
import jason.runtime.Settings;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.logging.Logger;

public class Env extends Environment {

    static final int LANE_LENGTH = 18;
    static final int SIZE = 2 * LANE_LENGTH + 6;
    static final int NB_CARS = 60;
    static final String AMBULANCE_NAME = "ambulance";

    private final Logger logger = Logger.getLogger("project."+Env.class.getName());
    private final Random random = new Random(System.currentTimeMillis());
    private final Map<String, Function<String[], Boolean>> actions;

    Runnable ambulanceExitSignal;

    private IntersectModel model;

    private Location ambulanceLocation;
    private int ambulanceTarget;

    public Env() {
        actions = new HashMap<>();
        actions.put("start", this::initAllCars);
        actions.put("sleep", this::sleep);
        actions.put("move", this::moveCar);
        actions.put("finish", this::finisherMoves);
        actions.put("lights", this::changeLight);
    }

    /** Called before the MAS execution with the args informed in .mas2j */
    @Override
    public void init(String[] args) {
        super.init(args);
        model = new IntersectModel(LANE_LENGTH, NB_CARS,this);
    }

    @Override
    public boolean executeAction(String agName, Structure action) {
        if (!actions.containsKey(action.getFunctor())) {
            logger.info(() -> String.format("executing: %s, but not implemented!", action));
            return false;
        }
        if (agName.contains("car")) {
            try {
                Thread.sleep(2L + random.nextInt(30));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

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
        if (model.getAgAtPos(newLocation) > -1 || newLocation.equals(ambulanceLocation)) {
            logger.severe("There is something blocking the way");
            return false;
        }
        if (args[0].contains("car")) {
            model.setNewPos(newLocation.x, newLocation.y, args[0]);
            return true;
        }
        model.moveAmbulance(ambulanceLocation, newLocation);
        ambulanceLocation = newLocation;
        return true;
    }

    private boolean finisherMoves(String[] args) {
        if (args.length < 2)
            return false;
        boolean isAmbulance = args[0].equals(AMBULANCE_NAME);
        int side = Integer.parseInt(args[1]);
        int target = isAmbulance ? ambulanceTarget : Integer.parseInt(args[2]);
        Location currentPosition = isAmbulance ? ambulanceLocation : model.getAgPos(Integer.parseInt(args[0].substring(3)));
        Location targetPosition = switch (side) {
            case 0 -> {
                int desiredY = target == 2 ? SIZE : LANE_LENGTH + (target == 1 ? 3 : 0);
                yield currentPosition.y < desiredY ? new Location(currentPosition.x, currentPosition.y + 1) : new Location(currentPosition.x + (2 - target), currentPosition.y);
            }
            case 1 -> {
                int desiredX = target == 3 ? -1 : LANE_LENGTH + (target == 2 ? 2 : 5);
                yield currentPosition.x > desiredX ? new Location(currentPosition.x - 1, currentPosition.y) : new Location(currentPosition.x, currentPosition.y + (target - 1));
            }
            case 2 -> {
                int desiredY = target == 0 ? -1 : LANE_LENGTH + (target == 3 ? 2 : 5);
                yield currentPosition.y > desiredY ? new Location(currentPosition.x, currentPosition.y - 1) : new Location(currentPosition.x + (2 - target), currentPosition.y);
            }
            default -> {
                int desiredX = target == 1 ? SIZE : LANE_LENGTH + (target == 0 ? 3 : 0);
                yield currentPosition.x < desiredX ? new Location(currentPosition.x + 1, currentPosition.y) : new Location(currentPosition.x, currentPosition.y + (target - 1));
            }
        };
        if (model.getAgAtPos(targetPosition) > -1 || model.hasObject(IntersectModel.RED, currentPosition) || targetPosition.equals(ambulanceLocation)) {
            logger.warning("There is something blocking the way");
            return false;
        }
        if (!model.inGrid(targetPosition)) {
            if (!isAmbulance)
                recreateCar(args[0]);
            else {
                model.moveAmbulance(ambulanceLocation, null);
                ambulanceLocation = null;
                ambulanceExitSignal.run();
                try {
                    getEnvironmentInfraTier().getRuntimeServices().killAgent(AMBULANCE_NAME, null, 0);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        else {
            if (!isAmbulance)
                model.setNewPos(targetPosition.x, targetPosition.y, args[0]);
            else {
                model.moveAmbulance(ambulanceLocation, targetPosition);
                ambulanceLocation = targetPosition;
            }
        }
        return true;
    }

    private boolean changeLight(String[] args) {
        if (args.length < 4)
            return false;

        if ("red".equals(args[3])) {
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 3; j++) {
                    model.redLight(i, j);
                }
            }
            return true;
        }
        int side = Integer.parseInt(args[1]);
        int lane = Integer.parseInt(args[2]);
        model.greenLight(side, lane);
        return true;
    }

    private void recreateCar(String agName) {
        RuntimeServices rs = getEnvironmentInfraTier().getRuntimeServices();
        try {
            rs.killAgent(agName, null, 0);
            while (!rs.getNewAgentName(agName).equals(agName)) {
                Thread.sleep(2);
            }
        } catch (RemoteException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        initCar(Integer.parseInt(agName.substring(3)));
    }

    private void updatePercepts() {
        clearPercepts();
        perceptCars();
    }

    public void initCar(int n) {
        int side = random.nextInt(4);
        int lane = random.nextInt(3);
        int target;
        do {
            target = random.nextInt(4);
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

    public void initAmbulance(int side) {
        int lane = random.nextInt(3);
        int potentialTarget = side + lane + 1;
        ambulanceTarget = potentialTarget > 3 ? potentialTarget - 4 : potentialTarget;
        ambulanceLocation = logicalCoordinateToModelCoordinate(side, lane, 0);
        model.moveAmbulance(null, ambulanceLocation);
        Settings settings = new Settings();
        String beliefs = String.format("pos(%d,%d,0), xdistance(%d)", side, lane, LANE_LENGTH);
        logger.info(() -> String.format("Creating amb with %s which is on %d %d", beliefs, ambulanceLocation.x, ambulanceLocation.y));
        settings.addOption(Settings.INIT_BELS, beliefs);
        try {
            String newAgentName = getEnvironmentInfraTier().getRuntimeServices()
                    .createAgent(AMBULANCE_NAME, "ambulance.asl", null, null, null, settings, null);
            getEnvironmentInfraTier().getRuntimeServices().startAgent(newAgentName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static Location logicalCoordinateToModelCoordinate(int side, int lane, int distance) {
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
            Location loc = model.getAgPos(i);
            if (loc == null)
                continue;
            LogicalCoordinate coor = LogicalCoordinate.of(loc);
            addPercept(Literal.parseLiteral(String.format("car(%d, %d, %d, car%d)", coor.side, coor.lane, coor.distance, i)));
        }
        if (ambulanceLocation == null)
            return;
        LogicalCoordinate coor = LogicalCoordinate.of(ambulanceLocation);
        addPercept(Literal.parseLiteral(String.format("car(%d, %d, %d, ambulance)", coor.side, coor.lane, coor.distance)));
    }

}
