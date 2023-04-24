import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.*;

public class Worker extends Thread {
    private int worker_id;
    private int port;

    private Chunk chunk;
    private ArrayList<String[]> waypoints;



    public Worker(int id){
        this.worker_id = id;
        this.port=Master.getWorker_port();
    } // Constructor

    public int getWorkerId(){return worker_id;}

    public int getPort(){return this.port;}



    //getters
    @Override
    public void run(){
        ObjectOutputStream out= null ;
        ObjectInputStream in = null ;
        Socket requestSocket= null ;


        try {
            String host = "localhost";
            /* Create socket for contacting the server on port worker_port*/
            requestSocket = new Socket(host, Master.getWorker_port());

            /* Create the streams to send and receive data from server */
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());

            chunk=(Chunk) in.readObject();

            waypoints=extractChunk(chunk);

            System.out.println(Arrays.toString(waypoints.get(0)) +" Worker");

            double lat1=getStartLat(waypoints);
            double lat2=getFinalLat(waypoints);
            double lon1=getStartLon(waypoints);
            double lon2=getFinalLon(waypoints);
            double ele1=getStartEle(waypoints);
            double ele2=getFinalEle(waypoints);

            LocalDateTime time1=getStartTime(waypoints);
            LocalDateTime time2=getFinalTime(waypoints);

            List<Double> elevationlist=getElevations(waypoints);

            double elevation;
            double totalTi=totalTime(time1,time2);
            double totalDis=distance(lat1,lat2,lon1,lon2,ele1,ele2);
            double avSpeed=averageSpeed(totalDis,totalTi);

            if(elevationlist.size()>1){elevation=getTotElevation(elevationlist);}
            else elevation=0;

            Double results[]={totalDis,totalTi,avSpeed,elevation};
            System.out.println(results[1]);
            out.writeObject(results);
            out.flush();

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


    private static ArrayList<String[]> extractChunk(Chunk chunk){
        return chunk.getData();
    }
    private static double getStartLat(ArrayList<String[]> waypoints){
        return Double.parseDouble(waypoints.get(0)[1]);
    }
    private static double getStartLon(ArrayList<String[]> waypoints){
        return Double.parseDouble(waypoints.get(0)[2]);
    }
    private static double getFinalLat(ArrayList<String[]> waypoints){
        return Double.parseDouble(waypoints.get(waypoints.size()-1)[1]);
    }
    private static double getFinalLon(ArrayList<String[]> waypoints){
        return Double.parseDouble(waypoints.get(waypoints.size()-1)[2]);
    }
    private static double getStartEle(ArrayList<String[]> waypoints){
        return Double.parseDouble(waypoints.get(0)[3]);
    }
    private static double getFinalEle(ArrayList<String[]> waypoints){
        return Double.parseDouble(waypoints.get(waypoints.size()-1)[3]);
    }
    private static LocalDateTime getStartTime(ArrayList<String[]> waypoints){
        return LocalDateTime.parse(waypoints.get(0)[4].substring(0,waypoints.get(0)[4].length()-1));
    }
    private static LocalDateTime getFinalTime(ArrayList<String[]> waypoints){
        return LocalDateTime.parse(waypoints.get(waypoints.size()-1)[4].substring(0,waypoints.get(waypoints.size()-1)[4].length()-1));
    }
    private List<Double> getElevations(ArrayList<String[]> waypoints) {
        List<Double> ele=new ArrayList<>();
        for(String[] w:waypoints){
            ele.add(Double.parseDouble(w[3]));
        }
        return ele;
    }
    private static double distance(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }
    private static double totalTime(LocalDateTime time1,LocalDateTime time2){
        Duration duration=Duration.between(time1,time2);
        return duration.toSeconds();
    }
    private static double averageSpeed(double distance,double time){
        double avspeed= distance/time;// m/s
        return 3.6*avspeed;//km/h
    }
    private double getTotElevation(List<Double> elevations) {
        double sum=0;
        for(int i =1;i<elevations.size();i++){
            if(elevations.get(i)> elevations.get(i - 1)){sum+=elevations.get(i - 1)-elevations.get(i);}
        }
        return sum;
    }


    public static void main(String[] args){
        int num_workers = Master.getWorker_num();
        System.out.println(num_workers);

        Worker[] workers=new Worker[num_workers];

        for(int i=0; i<num_workers; i++){
            workers[i] = new Worker(i);
            System.out.println("hi from worker "+i);
            workers[i].start();
        }
    }

}
