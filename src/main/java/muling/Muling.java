package muling;

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.script.MethodProvider;

import org.osbot.rs07.utility.ConditionalSleep;
import utils.BankManager;
import utils.Trading;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Muling extends MethodProvider implements Runnable {

    private volatile boolean run = true;

    private boolean connected = false;

    private String workerNickname = "";
    private int world = -1;
    private int place = -1;

    private Area tradeSpots[] = {
            new Area(3171, 3482, 3174, 3478),
            new Area(3152, 3483, 3157, 3478),
            new Area(3157, 3493, 3160, 3488),
            new Area(3237, 3200, 3234, 3204),
    };

    private BufferedReader dis;
    public DataOutputStream dos;

    public Muling(Socket s) {
        try {
            this.dis = new BufferedReader(new InputStreamReader(s.getInputStream()));
            this.dos = new DataOutputStream(s.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        try {
            while (run) {
                if(!connected) {
                    String request = String.format("Con;1;%s;Available", myPlayer().getName());

                    dos.writeUTF(request);

                    String response = dis.readLine();

                    log(response);

                    if (response.contains("Connected")) connected = true;
                } else {
                    String response = "";

                    if((response = dis.readLine()) != null) {
                        String splittedResponse[] = response.split(";");

                        log(response);

                        if(splittedResponse[0].equals("Info")) {
                            workerNickname = splittedResponse[1];
                            world = Integer.parseInt(splittedResponse[2]);
                            place = Integer.parseInt(splittedResponse[3]);

                            String request = "UpdateStatus;Unavailable";

                            dos.writeUTF(request);

                            if(getWorlds().getCurrentWorld() != world) getWorlds().hop(world);

                            while (tradeSpots[place].contains(myPosition())) getWalking().webWalk(tradeSpots[place]);

                            while(getInventory().isEmpty()) {
                                if (getPlayers().closest(workerNickname) != null) {
                                    Trading trading = new Trading();

                                    if (trading.trade(workerNickname)) {
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
                                request = "UpdateStatus;Available";
                                dos.writeUTF(request);
                            }
                        }
                    }
                }
                Thread.sleep(256);
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        run = false;
    }
}
