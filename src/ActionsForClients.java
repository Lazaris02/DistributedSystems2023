import java.io.*;
import java.net.*;

public class ActionsForClients extends Thread {
    /* This class is used to perform the actions the app clients ask for */
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public ActionsForClients(Socket connection){
        try{
            in = new ObjectInputStream(connection.getInputStream());
            out = new ObjectOutputStream(connection.getOutputStream());
        }catch(IOException exc){exc.printStackTrace();}
    } // Constructor

    public ActionsForClients(String name){
        super(name);
    } // Constructor

    /* getters */
    public ObjectInputStream getIn(){return in;}

    public ObjectOutputStream getOut(){return out;}


    @Override // probably does the mapping
    public void run(){}

}
