import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Worker extends Thread {
    private int id;
    private int port;
    public Worker(int id){
        this.id = id;
        this.port=Master.getWorker_port();
    } // Constructor
    public int getWorkerId(){return id;}
    public int getPort(){return this.port;}
    @Override
    public void run(){
        ObjectOutputStream out= null ;
        ObjectInputStream in = null ;
        Socket requestSocket= null ;
        System.out.println(/*shows the threads i am in */);
        try {
            String host = "localhost";
            /* Create socket for contacting the server on port 4321*/
            requestSocket = new Socket(host, Master.getWorker_port());

            /* Create the streams to send and receive data from server */
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());

            Chunk chunk=(Chunk) in.readObject();



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

    public static void main(String[] args){
        Worker[] workers=Master.getWorkers();
        for(Worker w:workers){
            w.start();
        }
    }

}
