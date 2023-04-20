import java.io.BufferedReader;
import java.io.Serializable;

public class Gpx implements Serializable {
    private BufferedReader file;
    public Gpx(BufferedReader file){
        this.file = file;
    }

    public  BufferedReader getFile(){return this.file;}
}

