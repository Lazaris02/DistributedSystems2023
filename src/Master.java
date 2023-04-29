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

    /*Getters*/
    public int getChunk_size(){return chunk_size;}

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

        String gpx_id = String.valueOf(key[0]);
        String creator = String.valueOf(key[1]);

        /*Creates the array*/

        String [] key_values = {gpx_id,lat,lon,ele,time,creator};

        addToChunk(key[0],key_values,last_waypoint); /*adds to chunk also checks if chunk is ready*/


    }

    /*Helper functions for map*/

    private String extractLat(String line){
        String[] find_lat_lon = line.split("lon");
        String lat = find_lat_lon[0].split("lat=\"")[1];
        lat = lat.substring(0,lat.length()-2); /*gets lat*/
        return lat;
    }

    private String extractLon(String line){
        String[] find_lat_lon = line.split("lon");
        String lon = find_lat_lon[1].split("\"")[1];
        lon = lon.substring(0,lon.length()); /*gets lon*/
        return lon;
    }

    private String extractEle(String line){
        return line.strip().substring(5,line.strip().length()-6);
    }

    private String extractTime(String line){return line.strip().substring(6,line.strip().length()-7);}




    /*Chunk Scheduling and Manipulation*/


    private synchronized void addToChunk(String key, String[] key_values,boolean last_waypoint){
        if(chunks.containsKey(key)){
            chunks.get(key).addData(key_values); /*check for ready here as well*/
        }else{
            Chunk c = new Chunk();
            c.addData(key_values);
            chunks.put(key,c);
        }
        addToReadyChunks(key,last_waypoint);
    }

    private  void addToReadyChunks(String key, boolean last_waypoint){

        /*adds a chunk to the ready queue -- removes it from hashmap
         if it is ready*/

        if(chunks.get(key).getData().size() == chunk_size || last_waypoint) {
            readyChunks.add(new Chunk(chunks.get(key))); /*Deep copy the chunk*/
            chunks.remove(key);
        }
    }

    public synchronized Chunk fetchChunk(){
        if(readyChunks.isEmpty()){return null;}
        System.out.println(Thread.currentThread().getName()+" fetching chunk");
        return readyChunks.remove();
    }

    /*Reduce function*/
    public String[] reduce(String [][] results){
        double totalDist=0;
        double totalTime=0;
        double avSpeed;
        double totalElevation=0;

        for (String[] res:results){
            totalDist+=Double.parseDouble(res[2]);
            totalElevation+=Double.parseDouble(res[5]);
            totalTime += Double.parseDouble(res[3]);
        }
        avSpeed=totalDist/totalTime; //m/s
        avSpeed=3.6*avSpeed; //km/h
        return new String[]{Double.toString(totalDist),Double.toString(totalTime),
                Double.toString(avSpeed),Double.toString(totalElevation)};
    }

    private void printQueue(){
        for(Chunk c  : readyChunks){
            System.out.println(Arrays.toString(c.getData().get(0)));
        }
    } /*TODO delete later*/

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





