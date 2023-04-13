import java.io.*;
import java.net.*;

public class ActionsForClients extends Thread {
    /* This class is used to perform the actions the app clients ask for */
    private ObjectInputStream in; //this should be a gpx file
    private ObjectOutputStream out;
    public ActionsForClients(Socket connection){
        try{
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());
        }catch(IOException exc){exc.printStackTrace();}
    }

    /* getters */
    public ObjectInputStream getIn(){return in;}

    public ObjectOutputStream getOut(){return out;}


    @Override
    public void run(){}
}
