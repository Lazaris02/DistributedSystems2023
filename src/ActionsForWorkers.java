import java.io.*;
import java.net.*;
import java.util.HashMap;

public class ActionsForWorkers extends Thread{
    /* This class is used to perform the actions the workers ask for */
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Master master;

    public ActionsForWorkers(Socket connection,Master master){
        try{
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());
            this.master = master;

        }catch(IOException exc){exc.printStackTrace();}
    } // Constructor


    /* getters */

    public ObjectInputStream getIn(){return in;}
    public ObjectOutputStream getOut(){return out;}


    @Override
    public void run(){
        while(Master.getChunks().isEmpty()){System.out.println("Waiting for chunk");}
        System.out.println("Preparing to send chunk");
        Chunk c = master.fetchChunk();
        try{
            out.writeObject(c);
            out.flush();
        }catch(IOException exc){
            exc.printStackTrace();
        }

    }




}
