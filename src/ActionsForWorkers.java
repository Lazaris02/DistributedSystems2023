import java.io.*;
import java.net.*;

public class ActionsForWorkers extends Thread{
    /* This class is used to perform the actions the workers ask for */
    ObjectOutputStream out;
    ObjectInputStream in;
    public ActionsForWorkers(Socket connection){
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
