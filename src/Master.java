import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;


public class Master extends Thread implements Server {
    private static int num_of_workers;

    private static final int client_port = 5377;

    private static final int worker_port = 6769;

    private final int port;

    private static final int chunk_size = 4;

    /* The sockets that receive the requests */
    private ServerSocket serverSocket;

    /* The socket that handles the requests */
    private static Socket client_provider, worker_provider;

    private static Worker [] workers; /* to store the workers */

    private static HashMap<String,Chunk> chunks;


    /* Constructors */

    public Master(int workers_num,int port){

        num_of_workers = workers_num;
        this.port = port;

        /* Initialize workers */

        workers = new Worker[num_of_workers];

        for(int i = 0; i<num_of_workers; i++){
            workers[i] = new Worker(i);
        }

        /*Initialize chunks*/
        chunks = new ArrayList<>();

    }

    public Master(int port){this.port = port;}

    /*Getters*/

    public int getNum_of_workers(){return num_of_workers;}

    public int getPort(){return this.port;}

    public static int getClient_port(){return client_port;}

    public static int getWorker_port(){return worker_port;}
    public static Worker[] getWorkers(){return workers;}

    public static HashMap<String,Chunk> getChunks(){return chunks;}

    /*Map function*/

    public void map(String key, String[] waypoint_lines,boolean last_waypoint){

        /*extracts the parameters*/

        String lat = extractLat(waypoint_lines[0]);
        String lon = extractLon(waypoint_lines[0]);
        String ele = extractEle(waypoint_lines[1]);
        String time = extractTime(waypoint_lines[2]);

        /*Creates the array*/

        String [] key_values = {key,lat,lon,ele,time}; /*TODO checked till here*/

        addToChunk(key,key_values);



//        for(String k : key_values){System.out.println(k);}

//        if((chunk.getData().size() == chunk_size) || last_waypoint){
//            /*send the chunk  to the workers with round robbin via TCP connection*/
//            System.out.println("I am in and I am " + key_values[0]);
//
//
//
//        }

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

    private synchronized  void addToChunk(String key,String[] key_values){
        if(chunks.containsKey(key)){
            chunks.get(key).addData(key_values);
        }else{
            Chunk c = new Chunk(chunk_size);
            c.addData(key_values);
            chunks.put(key,c);
        }
    }


    /* Server Implementation */

    public void openServer(){
        try {
            Thread t;

            /* Create Server Sockets */

            serverSocket = new ServerSocket(port, 10);

            while (true) {
                /* Accept the connections via the providerSockets */
                /* Handle the request depending on the port number*/
                if(this.port == client_port){
                    client_provider = serverSocket.accept();
                    t = new ActionsForClients(client_provider,this);
                    t.start();
                } else if (this.port == worker_port) {
                    worker_provider = serverSocket.accept();
                    t = new ActionsForWorkers(worker_provider, this );
                    t.start();
                }


            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                if(this.port == client_port){client_provider.close();}
                if(this.port == worker_port){worker_provider.close();}
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
        System.out.println("hi from thread1");
        m_worker.start();
        System.out.println("hi from thread2");
        
    }
}





