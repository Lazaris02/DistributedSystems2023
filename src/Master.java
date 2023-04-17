import java.io.*;
import java.net.*;


public class Master extends Thread implements Server {
    private static int num_of_workers;
    private static final int client_port = 5377;
    private static final int worker_port = 6769;
    private int port;

    /* The sockets that receive the requests */
    private ServerSocket serverSocket;

    /* The sockets that handle the requests */
    private Socket provider;

    static Worker [] workers; /* to store the workers */


    /* Constructors */

    public Master(int workers_num){

        num_of_workers = workers_num;

        /* Initialize workers */

        workers = new Worker[num_of_workers];

        for(int i = 0; i<num_of_workers; i++){
            workers[i] = new Worker(i);
        }
    }

    public Master(){/*Default Constructor*/}


    public int getNum_of_workers(){return num_of_workers;}

    public int getPort(){return this.port;}

    /* Server Implementation */

    public void openServer(){
        try {
            Thread t = null;

            /* Create Server Sockets */

            serverSocket = new ServerSocket(port, 10);

            while (true) {
                /* Accept the connections via the providerSockets */

                provider = serverSocket.accept();

                /* Handle the request depending on the port number*/
                if(this.port == client_port){
                    t = new ActionsForClients(provider);
                    t.start();
                } else if (this.port == worker_port) {
                    t = new ActionsForWorkers(provider);
                }


            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                provider.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    @Override
    public void run(){
        openServer();
    }

                /*OPEN THE SERVER*/
    public static void main(String[] args){
        /*I am trying to run two separate threads of the server
         *The first thread handles the client requests
         * The second thread handles the worker requests*/

        Thread m_client = new Master(Integer.parseInt(args[0]));
        Thread m_worker = new Master();
        m_client.start();
        m_worker.start();
        
    }
}





