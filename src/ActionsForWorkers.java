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
    @Override
    public void run(){}
}
