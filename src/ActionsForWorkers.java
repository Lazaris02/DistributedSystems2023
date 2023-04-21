import java.io.*;
import java.net.*;

public class ActionsForWorkers extends Thread{
    /* This class is used to perform the actions the workers ask for */
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Worker worker;
    public ActionsForWorkers(Socket connection){
        try{
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());
        }catch(IOException exc){exc.printStackTrace();}
    } // Constructor

    public ActionsForWorkers(String name){super(name);} // Constructor

    /* getters */

    public ObjectInputStream getIn(){return in;}
    public ObjectOutputStream getOut(){return out;}
    public Worker getWorker() {return worker;}

    @Override
    public void run(){

    }

}
