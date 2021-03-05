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

    public boolean executing = false;

    public String workerNickname = "";
  public int world = -1;
  public int place = -1;

    private Socket s;



    private BufferedReader dis;
    public BufferedWriter dos;

    public Muling(Socket s) {
        try {
          this.s = s;
          this.dis = new BufferedReader(new InputStreamReader(s.getInputStream()));
          this.dos = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        try {
            while (run) {
                if(!connected) {
                    log("Connecting");
                    String request = String.format("Con;1;%s;Available", myPlayer().getName());

                    dos.write(request);
                    dos.newLine();
                    dos.flush();

                    String response = dis.readLine();

                    if (response.contains("Connected")) {
                      log("Connected");
                      connected = true;
                    }
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

                          executing = true;


                          dos.write(request);
                            dos.newLine();
                            dos.flush();
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

  public boolean isExecuting() {
    return executing;
  }

  public String getWorkerNickname() {
    return workerNickname;
  }

  public int getWorld() {
    return world;
  }

  public int getPlace() {
    return place;
  }

  public void stop() {
        run = false;
    }
}
