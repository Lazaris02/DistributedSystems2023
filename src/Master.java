import java.io.*;
import java.net.*;


public class Master extends Thread implements Server {
    private static int num_of_workers;
    private static final int client_port = 5377;
    private static final int worker_port = 6769;
    private final int port;

    private static final int chunk_size = 4;

    /* The sockets that receive the requests */
    private ServerSocket serverSocket;

    /* The socket that handles the requests */
    private Socket provider;

    private static Worker [] workers; /* to store the workers */
    private static int worker_turn = 0;


    private Chunk chunk;

    /* Constructors */

    public Master(int workers_num,int port){

        num_of_workers = workers_num;
        this.port = port;

        /* Initialize workers */

        workers = new Worker[num_of_workers];

        for(int i = 0; i<num_of_workers; i++){
            workers[i] = new Worker(i); /*TODO might need to add arguments to the constructor*/
        }

        /*Initialize chunk*/
        chunk = new Chunk(chunk_size);

    }

    public Master(int port){this.port = port;}

    /*Getters*/

    public int getNum_of_workers(){return num_of_workers;}

    public int getPort(){return this.port;}

    public static int getClient_port(){return client_port;}

    public static int getWorker_port(){return worker_port;}
    public static Worker[] getWorkers(){return workers;}

    /*Map function*/

    public void map(String key, String[] waypoint_lines,boolean last_waypoint){

        /*extracts the parameters*/

        String lat = extractLat(waypoint_lines[0]);
        String lon = extractLon(waypoint_lines[0]);
        String ele = extractEle(waypoint_lines[1]);
        String time = extractTime(waypoint_lines[2]);

        /*Creates the array*/

        String [] key_values = {key,lat,lon,ele,time};

        if((chunk.getData().size() == chunk_size) || last_waypoint){
            /*Send chunk + empty chunk*/
        }
        else{chunk.addData(key_values);}

    }

    /*helper functions for map*/
    private String extractLat(String line){
        String[] find_lat_log = line.split("lon"); /*something like "lat_num" ... >*/
        String lat = find_lat_log[0].split("lat=\"")[1];
        lat = lat.substring(0,lat.length()-2); /*gets lat*/
        return lat;
    }

    private String extractLon(String line){
        String[] find_lat_log = line.split("lon"); /*something like "lat_num" ... >*/
        String lon = find_lat_log[1].split("\"")[1];
        lon = lon.substring(0,lon.length()-2); /*gets lat*/
        return lon;
    }

    private String extractEle(String line){
        return line.strip().substring(5,line.strip().length()-6);
    }

    private String extractTime(String line){return line.strip().substring(6,line.strip().length()-7);}

    private synchronized void sendToWorker(String key, String[] values){
        /*Sends the key - value pairs to the workers with round robbin*/

    }


    /* Server Implementation */

    public void openServer(){
        try {
            Thread t;

            /* Create Server Sockets */

            serverSocket = new ServerSocket(port, 10);

            while (true) {
                /* Accept the connections via the providerSockets */

                provider = serverSocket.accept();

                /* Handle the request depending on the port number*/
                if(this.port == client_port){
                    t = new ActionsForClients(provider,this);
                    t.start();
                } else if (this.port == worker_port) {
                    t = new ActionsForWorkers(provider);
                    t.start();
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

        int work_num = Integer.parseInt(args[0]);
        Thread m_client = new Master(work_num,client_port); /*Handles the clients*/
        Thread m_worker = new Master(worker_port); /*Handles the workers*/

        m_client.start();
        m_worker.start();
        
    }
}





