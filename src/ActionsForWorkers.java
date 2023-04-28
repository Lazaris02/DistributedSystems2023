import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.HashMap;

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

        System.out.println("Hi from "+ Thread.currentThread().getName());

        while(master.readyQueueEmpty()){/*Blocks connection*/}
        System.out.println(Thread.currentThread().getName()+"sending");
        Chunk toSend = master.fetchChunk();

        try{
            out.writeObject(toSend);
            out.flush();


            String[] results = (String[])  in.readObject();


        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }



}
