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

    /* getters */
    public ObjectInputStream getIn(){return in;}

    public ObjectOutputStream getOut(){return out;}

    /*Functions used in run*/
    private String getClientId(String line){
        /*gets the "creator of the string"*/
        String[] words = line.split("creator=");
        return words[1].split("\"")[1];
    }

    @Override
    public void run() {
        /*Read the gpx file from the input stream
         * read the name of the client from the gpx file
         * start a for loop and send lines from the gpx file + id of gpx file
         * String client_id + String [] waypoint_lines
         * when the gpx file is done the thread blocks until I have reduced
         * the intermediate results
         * after that the reduce process starts and the file*/

        try{
            Gpx gpx = (Gpx) in.readObject();
            BufferedReader file = gpx.getFile();
            /*find the "creator" and use it as an id */

            file.readLine(); // first line is useless
            String line = file.readLine(); // second line contains the creator
            String id = getClientId(line); // should return user1 f.e

            /*Split into waypoints -- every waypoint is within <wpt> </wpt>
             */
            String [] waypoint_lines = new String[4];
            while(!line.contains("</gpx>")){ /*TODO this only works for the specific format of the gpx files
                                                maybe needs to change*/
                int i =0;
                while(!line.contains("</wpt>")){
                    line = file.readLine();
                    waypoint_lines[i] = line;
                    i++;
                }

                /* pass ID and a WAYPOINT to the map function of master*/
                master.map(id,waypoint_lines);
            }

        }catch (IOException exc){
            exc.printStackTrace();
        }catch (ClassNotFoundException exc){
            throw new RuntimeException(exc);
        }
    }


}
