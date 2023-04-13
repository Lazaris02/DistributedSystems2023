import java.io.*;
import java.net.*;

public class Master implements Server {
    private int num_of_workers, app_port, workers_port;

    /* The sockets that receive the requests */
    private ServerSocket app_s , workers_s;

    /* The sockets that hand;e the requests */
    Socket app_provider, workers_provider;

    Worker [] workers; /* to store the workers */

    /* NEEDS A HASHMAP FOR CLIENTS */

    public Master(int num_of_workers){
        this.num_of_workers =num_of_workers; // might be needed for round robbin
        this.app_port = 5007;
        this.workers_port = 6044;

        /* Open the  server with 2 ports */
        openServer();
        
        /* Initialize workers */
        workers = new Worker[num_of_workers];
        for(int i = 0; i<num_of_workers; i++)
            workers[i] = new Worker(i);

    } // Constructor

    public int getNum_of_workers(){return num_of_workers;}

    public int getApp_port(){return app_port;}

    public int getWorkers_port(){return workers_port;}

    /* Server Implementation */
    public void openServer(){
        /* TODO initialize pool of threads */
        Thread app_thread, worker_thread;
        try {
            /* Create Server Sockets */
            this.app_s = new ServerSocket(app_port,10);
            this.workers_s = new ServerSocket(workers_port,10);

            while (true) {
                /* Accept the connections via the providerSockets */

                app_provider = app_s.accept();
                workers_provider = workers_s.accept();

                /* Handle the request depending on the port number*/

                app_thread = new ActionsForClients(app_provider);
                worker_thread = new ActionsForWorkers(workers_provider);

                app_thread.start();
                worker_thread.start();
            }

        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                app_provider.close();
                workers_provider.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}





