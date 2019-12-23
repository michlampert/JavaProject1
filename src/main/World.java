package main;

public class World {

    public static void main(String[] args) throws InterruptedException {

        if(args.length > 0 && args[0].equals("2")){
            SimulationView.runTwo(new SimulationView(new Area("src/parameters.json")), new SimulationView(new Area("src/parameters.json")));
        }
        else{
        SimulationView okienko = new SimulationView(new Area("src/parameters.json"));
        okienko.run();
        }
    }

}
