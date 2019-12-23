package main;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;


public class Settings {

    int animalsCount = 10;
    int plantsCount = 10;
    int width = 20;
    int height = 20;
    int startEnergy = 100;
    int moveEnergy = 1;
    int plantEnergy = 10;
    double jungleRatio = 0.3;

    public Settings(String file){
        JSONParser parser = new JSONParser();
        try (Reader reader = new FileReader(file)) {
            JSONObject jsonObject = (JSONObject) parser.parse(reader);

            width =(int) (long) jsonObject.get("width");
            height = (int) (long) jsonObject.get("height");
            startEnergy = (int) (long) jsonObject.get("startEnergy");
            moveEnergy = (int) (long) jsonObject.get("moveEnergy");
            plantEnergy = (int) (long) jsonObject.get("plantEnergy");
            animalsCount = (int) (long) jsonObject.get("animalsCount");
            plantsCount = (int) (long) jsonObject.get("plantsCount");
            jungleRatio = (double) jsonObject.get("jungleRatio");
        } catch (IOException | org.json.simple.parser.ParseException e) {
            System.out.println("Exception caught during loading settings.");
            e.printStackTrace();
        }


    }


}
