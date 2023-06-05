package manuel;

// Environment code for project project

import jason.asSyntax.*;
import jason.environment.*;
import jason.asSyntax.parser.*;
import jason.runtime.Settings;

import java.util.Random;
import java.util.logging.*;

public class Env extends Environment {

    private Logger logger = Logger.getLogger("project."+Env.class.getName());
    private Random random = new Random(System.currentTimeMillis());

    /** Called before the MAS execution with the args informed in .mas2j */
    @Override
    public void init(String[] args) {
        super.init(args);
    }

    @Override
    public boolean executeAction(String agName, Structure action) {
        if (action.equals(Literal.parseLiteral("createCar(pls)"))) {
            createCar();
        } else if (action.equals(Literal.parseLiteral("start"))) {
            createCar();
        } else {
            logger.info(() -> String.format("executing: %s, but not implemented!", action));
        }
        if (true) { // you may improve this condition
             informAgsEnvironmentChanged();
        }
        return true; // the action was executed with success
    }

    /** Called before the end of MAS execution */
    @Override
    public void stop() {
        super.stop();
    }

    public void createCar() {
        Settings settings = new Settings();
        int side = random.nextInt(3);
        int target;
        do {
            target = random.nextInt(3);
        } while (target != side);
        settings.addOption(Settings.INIT_BELS, String.format("pos(%d,%d,0), target(%d)", side, random.nextInt(2), target));
        try {
            String newAgentName = getEnvironmentInfraTier().getRuntimeServices()
                    .createAgent("car", "car.asl", null, null, null, settings, null);
            getEnvironmentInfraTier().getRuntimeServices().startAgent(newAgentName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
