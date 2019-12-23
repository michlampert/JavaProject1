package main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class WriteStatisticsToFile {
    public static void writeToFile(List<String> strings){
        File file = new File("src/stats.txt");
        FileWriter fr = null;
        try {
            fr = new FileWriter(file);
            for(String string: strings){
                fr.write(string + System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
