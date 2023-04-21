import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Dummy extends Thread{
    BufferedReader file;

    public Dummy(BufferedReader file){
        this.file = file;
    }

    @Override
    public void run(){
        ObjectOutputStream out= null ;
        ObjectInputStream in = null ;
        Socket requestSocket= null ;
        System.out.println(/*shows the threads i am in */);
        try {
            String host = "localhost";
            /* Create socket for contacting the server on port 4321*/
            requestSocket = new Socket(host, Master.getClient_port());

            /* Create the streams to send and receive data from server */
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());

            /*Sends the gpx file*/

            Gpx gpx = new Gpx(file);
            out.writeObject(gpx);
            out.flush();

            /*AWAITS FOR THE RESULTS FROM MASTER
            * READS IT IN SOME KIND OF LIST*/
            ArrayList<String> results = (ArrayList<String>) in.readObject();

        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                in.close(); out.close();
                requestSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }




    public static void main(String[] args) {
        /*Initializes the client - gets gpx file
        * starts their thread.
        * Waits for results
        * Prints results*/

        try{
            /*READS THE GPX FILES*/

            BufferedReader in1 = new BufferedReader(new FileReader("./gpx_files/route1.gpx"));
            BufferedReader in2 = new BufferedReader(new FileReader("./gpx_files/route2.gpx"));
            BufferedReader in3 = new BufferedReader(new FileReader("./gpx_files/route3.gpx"));
            BufferedReader in4 = new BufferedReader(new FileReader("./gpx_files/route4.gpx"));
            BufferedReader in5 = new BufferedReader(new FileReader("./gpx_files/route5.gpx"));
            BufferedReader in6 = new BufferedReader(new FileReader("./gpx_files/route6.gpx"));

            /*INIT CLIENTS*/
            Dummy dummy1 = new Dummy(in1);
            Dummy dummy2 = new Dummy(in2);
            Dummy dummy3 = new Dummy(in3);
            Dummy dummy4 = new Dummy(in4);
            Dummy dummy5 = new Dummy(in5);
            Dummy dummy6 = new Dummy(in6);

            dummy1.start();
            dummy2.start();
            dummy3.start();
            dummy4.start();
            dummy5.start();
            dummy6.start();


        } catch(IOException exc){
            exc.printStackTrace();
        }

    }

}