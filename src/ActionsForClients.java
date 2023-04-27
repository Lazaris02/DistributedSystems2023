import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Random;

public class ActionsForClients extends Thread {
    /* This class is used to perform the actions the app clients ask for */
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Master master; /*The thread needs to know the master thread it belongs so that master
                            can send the mapped pairs to the workers*/

    private static int id =0;

    public ActionsForClients(Socket connection, Master master){
        try{
            in = new ObjectInputStream(connection.getInputStream());
            out = new ObjectOutputStream(connection.getOutputStream());
            this.master = master;

        }catch(IOException exc){exc.printStackTrace();}
    } // Constructor

    /* getters */
   private synchronized String getGpxId(){
       id++;

       return String.valueOf(id);
   }

    /*Functions used in run*/
    private String getCreator(String line){
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
            File gpx = (File) in.readObject();

            /*find the "creator" and use it as an id */
            BufferedReader file = new BufferedReader(new FileReader(gpx));

            file.readLine(); // first line is useless
            String line = file.readLine(); // second line contains the creator

            String creator = getCreator(line);// should return user1 f.e
            String gpxId = getGpxId(); // we also need an id for the gpx file

            String[] key = {gpxId,creator};

            /*Split into waypoints -- every waypoint is within <wpt> </wpt>
             */

            String [] waypoint_lines = new String[4];

            boolean last_waypoint = false;

            line = file.readLine();
            while(!line.contains("</gpx>")){
                int i =0;
                while(!line.contains("</wpt>")){
                    if(i!=0)
                        line = file.readLine();
                    waypoint_lines[i] = line;
                    i++;
                }
                line = file.readLine();
                if(line.contains("</gpx>")){last_waypoint = true;}

                /* pass ID and a WAYPOINT to the map function of master*/

                master.map(key,waypoint_lines,last_waypoint);
            }

        }catch (IOException exc){
            exc.printStackTrace();
        }catch (ClassNotFoundException exc){
            throw new RuntimeException(exc);
        }
    }


}
