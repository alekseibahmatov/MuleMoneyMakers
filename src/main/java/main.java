import muling.Muling;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
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
        return 0;
    }

    @Override
    public final void onExit() {
        log("This will be printed to the logger when the script exits");
        try {
            muling.dos.writeUTF("exit");
        } catch (IOException e) {
            e.printStackTrace();
        }
        muling.stop();
    }


}
