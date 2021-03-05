package utils;

import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.constants.Banks;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.utility.ConditionalSleep;

import java.util.Random;

public class BankManager extends MethodProvider {
    public void openBank() {
        if (getBank().isOpen()) return;

        if (getInventory().isItemSelected()) getInventory().deselectItem();

        Filter<RS2Object> geBoothFilter = rs2Object -> rs2Object.hasAction("Bank");

        RS2Object geBooth = getObjects().closest(geBoothFilter);
        NPC banker = getNpcs().closest("Banker");

        int random = new Random().nextInt(10);
        if (geBooth != null && random < 5) {
            if (!geBooth.isVisible()) getCamera().toEntity(geBooth);
            log("Opening bank through geBooth");
            geBooth.interact("Bank");
            new ConditionalSleep(2500, 3000) {
                @Override
                public boolean condition() {
                    return getBank().isOpen();
                }
            }.sleep();
        }

        if (banker != null && random >= 5) {
            if (!banker.isVisible()) getCamera().toEntity(banker);
            log("Opening bank through banker");
            banker.interact("Bank");
            new ConditionalSleep(2500, 3000) {
                @Override
                public boolean condition() {
                    return getBank().isOpen();
                }
            }.sleep();
        }

        if (!getBank().isOpen()) openBank();
    }

}
