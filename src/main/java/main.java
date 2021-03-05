import muling.Muling;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.utility.ConditionalSleep;
import utils.BankManager;
import utils.Trading;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

@ScriptManifest(version = 0.1, author = "Z3Die", name = "Mule money maker V2", info = "Collects a gold from bots", logo = "")
public class main extends Script {

    private BankManager bankManager = new BankManager();
    private Trading trading = new Trading();

    private Socket socket = null;
    private Muling muling = null;

  private Area tradeSpots[] = {
    new Area(3171, 3482, 3174, 3478),
    new Area(3152, 3483, 3157, 3478),
    new Area(3157, 3493, 3160, 3488),
    new Area(3237, 3200, 3234, 3204),
  };

    @Override
    public void onStart() {
        bankManager.exchangeContext(getBot());
        trading.exchangeContext(getBot());

        try {
            socket = new Socket("127.0.0.1" , 7878);

            muling = new Muling(socket);

            muling.exchangeContext(getBot());

            new Thread(muling).start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onLoop() throws InterruptedException {
      log(muling.isExecuting());
      if (muling.isExecuting()) {
        log("executing");
        if(getWorlds().getCurrentWorld() != muling.getWorld()) {
          log("Hopping to world " + muling.getWorld());
          getWorlds().hop(muling.getWorld());
        }

        while (!tradeSpots[muling.getPlace()].contains(myPosition())) {
          log("Running to spot");
          getWalking().webWalk(tradeSpots[muling.getPlace()]);
        }

        while(getInventory().isEmpty()) {
          if (getPlayers().closest(muling.getWorkerNickname()) != null) {
            Trading trading = new Trading();

            if (trading.trade(muling.getWorkerNickname())) {
              new ConditionalSleep(5000, 500) {
                @Override
                public boolean condition() throws InterruptedException {
                  return !getTrade().getTheirOffers().contains("Coins");
                }
              }.sleep();
              if (trading.acceptTrade(true)) log("Trading completed");
            }
          }
        }

        while (!getBank().closest().getArea(2).contains(myPosition())) getWalking().webWalk(getBank().closest().getArea(2));

        BankManager bankManager = new BankManager();

        bankManager.openBank();

        if(getBank().isOpen()) {
          getBank().depositAll();
          String request = "UpdateStatus;Available";
          try {
            muling.dos.write(request);
            muling.dos.newLine();
            muling.dos.flush();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }

        return random(300, 500);
    }

    @Override
    public final void onExit() {
        log("This will be printed to the logger when the script exits");
        try {
            muling.dos.write("exit");
            muling.dos.newLine();
            muling.dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        muling.stop();
    }


}
