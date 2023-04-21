import java.io.Serializable;
import java.util.ArrayList;


public class Chunk implements Serializable {

    private int size;
    private ArrayList<String[]> data;
    public Chunk(int size) {
        this.size = size;
        data = new ArrayList<String[]>();
    }
    public ArrayList<String[]> getData(){return this.data;}

    public void addData(String[] key_value){
        data.add(key_value);
    }




}
