import java.io.*;
import java.net.*;

public class ActionsForClients extends Thread {
    /* This class is used to perform the actions the app clients ask for */
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Master master; /*The thread needs to know the master thread it belongs so that master
                            can send the mapped pairs to the workers*/

    public ActionsForClients(Socket connection, Master master){
        try{
            in = new ObjectInputStream(connection.getInputStream());
            out = new ObjectOutputStream(connection.getOutputStream());
            this.master = master;
        }catch(IOException exc){exc.printStackTrace();}
    } // Constructor

    public ActionsForClients(String name){
        super(name);
    } // Constructor

    /* getters */
    public ObjectInputStream getIn(){return in;}

    public ObjectOutputStream getOut(){return out;}

    @Override
    public void run() {
        /*Read the gpx file from the input stream
         * read the name of the client from the gpx file
         * start a for loop and send lines from the gpx file + id of gpx file
         * f.e client1,line_of_code
         * when the gpx file is done the thread blocks until I have reduced
         * the intermediate results
         * after that the reduce process starts and the file*/
        try{
            BufferedReader gpx = (BufferedReader) in.readObject();
            /*TODO might need synchronize*/
            /*find the "creator" and use it as an id
            * pass ID and a WAYPOINT to the map function of master*/


        }catch (IOException exc){
            exc.printStackTrace();
        }catch (ClassNotFoundException exc){
            throw new RuntimeException(exc);
        }
    }


}
