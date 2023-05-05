import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

public class Dummy extends Thread{
    private File file;
    private static String server_ip;

    public Dummy(File file){
        this.file = file;
    }

    @Override
    public void run(){
        ObjectOutputStream out= null ;
        ObjectInputStream in = null ;
        Socket requestSocket= null ;

        System.out.println(/*shows the threads Ι am in */);
        try {
            String host = server_ip;
            /* Create socket for contacting the server on port 4321*/
            requestSocket = new Socket(host, 5377);

            /* Create the streams to send and receive data from server */
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());

            /*Sends the gpx file*/

            out.writeObject(file);
            out.flush();

            /*Results from Master*/

            String[] results=(String[]) in.readObject();
            String[] stats=(String[]) in.readObject();
            Double[] individual_results = (Double[]) in.readObject();

            System.out.println("Your stats: "+Arrays.toString(results)+Thread.currentThread().getName());
            System.out.println("Your average stats: "+Arrays.toString(individual_results)+Thread.currentThread().getName());
            System.out.println("Total average stats: "+Arrays.toString(stats)+Thread.currentThread().getName());



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

            server_ip = args[0];
            /*READS THE GPX FILES*/

            File in1 = new File("./gpx_files/route1.gpx");
            File in2 = new File("./gpx_files/route2.gpx");
            File in3 = new File("./gpx_files/route3.gpx");
            File in4 = new File("./gpx_files/route4.gpx");
            File in5 = new File("./gpx_files/route5.gpx");
            File in6 = new File("./gpx_files/route6.gpx");


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

    }

}