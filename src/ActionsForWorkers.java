import java.io.*;
import java.net.*;
import java.util.Arrays;


public class ActionsForWorkers extends Thread{
    /* This class is used to perform the actions the workers ask for */
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Master master;

    public ActionsForWorkers(Socket connection,Master master){
        try{
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());
            this.master = master;

        }catch(IOException exc){exc.printStackTrace();}
    } // Constructor



    @Override
    public void run(){

//        System.out.println("Hi from "+ Thread.currentThread().getName());
        Chunk toSend;

        try{
            while((toSend = master.fetchChunk()) == null){/*Blocks connection*/}
            out.writeObject(toSend);
            out.flush();

            String[] results = (String[])  in.readObject();
            /*results[0] -----> gpx_id*/
            master.addResult(results[0],results);
            Master.chunkMapped(results[0]);
            System.out.println(Arrays.toString(results));
            if(Master.startReduce(results[0])){master.reduce(results[0]);}


        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }



}
