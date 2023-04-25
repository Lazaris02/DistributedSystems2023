import java.io.*;
import java.net.*;
import java.util.*;


public class Master extends Thread implements Server {
    private  static int num_of_workers;

    private static final int client_port = 5377;

    private static final int worker_port = 6769;

    private final int port;

    private static final int chunk_size = 4;

    /* The sockets that receive the requests */
    private ServerSocket serverSocket;

    /* The socket that handles the requests */
    private static Socket client_provider, worker_provider;

    /*Collections*/
    private static HashMap<String,Chunk> chunks;
    private static LinkedList<Chunk> readyChunks; /*TODO careful with static collections*/



    /* Constructor */

    public Master(int port){
        this.port = port;
        if(this.port == client_port){chunks = new HashMap<>();}
        if(this.port == worker_port){readyChunks=new LinkedList<>();}
    }



    /*Map function*/

    public void map(String[] key, String[] waypoint_lines,boolean last_waypoint){

        /*key[0]--> gpx_id
        *key[1]-->creator*/

        /*extracts the parameters*/

        String lat = extractLat(waypoint_lines[0]);
        String lon = extractLon(waypoint_lines[0]);
        String ele = extractEle(waypoint_lines[1]);
        String time = extractTime(waypoint_lines[2]);

        /*Creates the array*/

        String [] key_values = {key[0],lat,lon,ele,time,key[1]}; /*TODO works until here*/

        /*TODO This part needs synchronize altogether*/
        if(key[0].equals("5") || key[0].equals("6")) {
            System.out.println(key[0]);
        }
        addToChunk(key[0],key_values); /*adds to chunk also checks if chunk is ready*/
        addToReadyChunks(key[0],last_waypoint);

    }

    /*Helper functions for map*/

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




    /*Chunk Scheduling and Manipulation*/


    private synchronized void addToChunk(String key, String[] key_values){
        if(chunks.containsKey(key)){
            chunks.get(key).addData(key_values); /*check for ready here as well*/
        }else{
            Chunk c = new Chunk();
            c.addData(key_values);
            chunks.put(key,c);
        }
    }

    private  synchronized void addToReadyChunks(String key, boolean last_waypoint){
        /*adds a chunk to the ready queue -- removes it from hashmap
         if it is ready*/

        if(chunks.get(key).getData().size() == chunk_size || last_waypoint) { /*TODO check this condition*/
            System.out.println("Adding chunk to queue");
            readyChunks.add(chunks.get(key));
            chunks.get(key).empty_data();
        }
    }
    public synchronized boolean readyChunk(){return !readyChunks.isEmpty();}

    public synchronized Chunk fetchChunk(){
        return readyChunks.remove();
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

        System.out.println("Master "+work_num);

        Thread m_client = new Master(client_port); /*Handles the clients*/

        Thread m_worker = new Master(worker_port); /*Handles the workers*/

        m_client.start();
        m_worker.start();


        
    }
}





