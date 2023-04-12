import java.io.*;
import java.net.*;

public class Master {
    int num_of_workers;
    int app_port, workers_port;
    Worker [] workers; // An array for workers

    public Master(int num_of_workers){
        this.num_of_workers =num_of_workers; // might be needed for round robbin
        this.app_port = 5007;
        this.workers_port = 6044;

        /* Open the 2 servers */
        new Server().openServer(app_port); // app server
        new Server().openServer(workers_port); // workers server
        
        /* Initialize workers */
        workers = new Worker[num_of_workers];
        for(int i = 0; i<num_of_workers; i++)
            workers[i] = new Worker(i);

    } // Constructor

    private class Server {
        /* The socket that receives the requests */
        ServerSocket s;

        /* The socket that handles the connection */

        Socket providerSocket;
        void openServer(int port_number) {
            try {

                /* Create Server Socket */
                s = new ServerSocket(port_number, 10);

                while (true) {
                    /* Accept the connection */
                    providerSocket = s.accept();

                    /* Handle the request depending on the port number*/
                    Thread d = null;
                    if(port_number == app_port) {
                        d = new ActionsForClients(providerSocket);
                        d.start();
                    } else if (port_number == workers_port) {
                        d = new ActionsForWorkers(providerSocket);
                        d.start();
                    }
                }

            } catch (IOException ioException) {
                ioException.printStackTrace();
            } finally {
                try {
                    providerSocket.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
    }


}
