package main;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.Math.round;
import static java.lang.Math.sqrt;

public class Area implements IWorldMap{

    ArrayList<Animal> animals = new ArrayList<>();
    Map<Vector2d, ArrayList<Animal>> cells = new HashMap<>();
    Map<Vector2d, Integer> plants = new HashMap<>();

    Vector2d savannaLowerLeft = new Vector2d(0,0);
    Vector2d savannaUpperRight;
    Vector2d jungleLowerLeft;
    Vector2d jungleUpperRight;
    int startEnergy;
    int moveEnergy;
    int plantEnergy;
    int freeSavannaCells;
    int freeJungleCells;
    int generation = 0;
    Random generator = new Random();
    int[] commonDNA;

    MapVisualizer mapVisualizer = new MapVisualizer(this);

    public Area(String file){
        this(new Settings(file).animalsCount, new Settings(file).plantsCount, new Settings(file).width, new Settings(file).height, new Settings(file).startEnergy, new Settings(file).moveEnergy, new Settings(file).plantEnergy, new Settings(file).jungleRatio);
    }


    public Area(int animalsCount, int plantsCount, int width, int height,int startEnergy, int moveEnergy, int plantEnergy, double jungleRatio){


        int x1, x2, y1, y2;
        x1 = (int) round(sqrt(jungleRatio) * width);
        x2 = (int) round(sqrt(1 - jungleRatio) * width);
        y1 = (int) round(sqrt(jungleRatio) * width);
        y2 = (int) round(sqrt(1 - jungleRatio) * width);

        this.savannaUpperRight = new Vector2d(width, height);
        this.jungleLowerLeft = new Vector2d(x1, y1);
        this.jungleUpperRight = new Vector2d(x2, y2);
        this.startEnergy = startEnergy;
        this.moveEnergy = moveEnergy;
        this.plantEnergy = plantEnergy;

        freeJungleCells = this.Sjungle();
        freeSavannaCells = this.Ssavanna();

        while(animalsCount > 0){
            int x = generator.nextInt(width);
            int y = generator.nextInt(height);
            if(!this.cells.containsKey(new Vector2d(x,y))){
                this.placeAnimal(new Animal(this, new Vector2d(x, y), startEnergy));
                animalsCount--;
            }

        }
        while(plantsCount > 0){
            addPlantAnywhere();
            plantsCount--;
        }
        this.commonDNA = this.mostCommonDNA();
    }

    public String toString(){
        return mapVisualizer.draw(savannaLowerLeft, savannaUpperRight);
    }


    public void placeAnimal(Animal animal){
        if(animal == null) return;
        if(!this.cells.containsKey(animal.getPosition())){
            this.cells.put(animal.getPosition(), new ArrayList<>());
        }
        this.animals.add(animal);
        this.cells.get(animal.getPosition()).add(animal);
    }

    public void removeAnimal(Animal animal){
        this.animals.remove(animal);
        this.cells.get(animal.getPosition()).remove(animal);
        if(this.cells.get(animal.getPosition()).size() == 0){
            this.cells.remove(animal.getPosition());
        }
    }

    public void run(){
        ArrayList<Animal> animalsCopy = (ArrayList<Animal>) this.animals.clone();

        for(Animal animal : animalsCopy){
            animal.move();
        }
    }

    public void nextDay(){
        this.run();
        this.feedAnimals();
        this.addAllChilds();
        this.addPlantInJungle();
        this.addPlantInSavanna();
        this.generation++;
        this.commonDNA = this.mostCommonDNA();
    }

    private void placePlant(Vector2d position) throws Exception {
        //if(position == null) return;
        if(!this.plants.containsKey(position)){
            this.plants.put(position, plantEnergy);
        }
        else{
            throw new Exception(position.toString() + " position is occurred by plant");
        }
    }

    private void addPlantAnywhere(){
        if(freeJungleCells < 1 && freeSavannaCells < 1) return;
        int x, y;
        x = generator.nextInt(this.jungleUpperRight.x);
        y = generator.nextInt(this.jungleUpperRight.y);
        while(this.plants.containsKey(new Vector2d(x, y))) {
            x = generator.nextInt(this.jungleUpperRight.x);
            y = generator.nextInt(this.jungleUpperRight.y);
        }
        Vector2d position = new Vector2d(x, y);
        if(this.isInSavanna(position)){
            freeSavannaCells--;
        }
        else if (this.isInJungle(position)){
            freeJungleCells--;
        }
        try {
            this.placePlant(new Vector2d(x, y));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isInJungle(Vector2d position){
        return (position.follows(jungleLowerLeft) && position.precedes(jungleUpperRight)  && !this.plants.containsKey(position));
    }

    public boolean isInSavanna(Vector2d position){
        return (!isInJungle(position) && position.follows(savannaLowerLeft) && position.precedes(savannaUpperRight) && !this.plants.containsKey(position));
    }

    private void addPlantInJungle(){
        if(freeJungleCells < 1) return;
        this.freeJungleCells--;
        Vector2d position;
        do {
            position = this.savannaUpperRight.random();
        }while(!this.isInJungle(position));
        this.plants.put(position, this.plantEnergy);
    }

    private void addPlantInSavanna(){
        if(freeSavannaCells < 1) return;
        this.freeSavannaCells--;
        Vector2d position;
        do {
            position = this.savannaUpperRight.random();
        }while(!this.isInSavanna(position));
        this.plants.put(position, this.plantEnergy);
    }

    public void feedAnimalsAt(Vector2d position){
        if(!this.plants.containsKey(position)) return;
        if(!this.cells.containsKey(position)) return;

        ArrayList<Animal> biggestAnimals =  this.biggestAnimals(position);

        int tmp = this.plants.get(position);
        int onePortion = (int) tmp / (biggestAnimals.size());
        tmp = this.plants.get(position) - (onePortion * (biggestAnimals.size()));
        for(Animal animal : biggestAnimals ){
            animal.energy += onePortion;
            if(tmp > 0){
                animal.energy++;
                tmp--;
            }
        }
        this.plants.remove(position);
        if(this.isInSavanna(position)){
            freeSavannaCells++;
        }
        else{
            freeJungleCells++;
        }
    }

    public void feedAnimals(){
        for(Vector2d position : this.cells.keySet()){
            feedAnimalsAt(position);
        }
    }

    private ArrayList<Animal> biggestAnimals(Vector2d position){
        ArrayList<Animal> result = new ArrayList<>();
        int maxEnergy = 0;
        ArrayList<Animal> animals = this.cells.get(position);
        for(int i = 0; i < animals.size(); i++ ){
            if(animals.get(i).energy == maxEnergy){
                result.add(animals.get(i));
            }
            else if(animals.get(i).energy > maxEnergy){
                maxEnergy = animals.get(i).energy;
                result.clear();
                result.add(animals.get(i));
            }
        }
        return result;
    }

    public Animal newChild(Vector2d position){
        Animal parent1 = null, parent2 = null;
        ArrayList<Animal> animals = this.cells.get(position);
        if( animals.size() < 2) return null;
        int parent1id = 0, maxEnergy = 0;
        for(int i = 0; i < animals.size(); i++){
            if(animals.get(i).energy > maxEnergy){
                parent1 = animals.get(i);
                parent1id = i;
            }
        }
        maxEnergy = 0;
        for(int i = 0; i < animals.size(); i++){
            if(animals.get(i).energy > maxEnergy && i != parent1id){
                parent2 = animals.get(i);
            }
        }
        if(parent1.energy < this.startEnergy / 2 || parent2.energy < this.startEnergy / 2) return null;
        return parent1.makeChild(parent2);
    }

    public void addAllChilds(){
        ArrayList<Animal> childs = new ArrayList<>();

        for(Vector2d position : this.cells.keySet()){
            Animal child = this.newChild(position);
            if(child != null){
                childs.add(child);
            }
        }

        for(Animal child : childs){
            this.placeAnimal(child);
        }
    }

    @Override
    public boolean isOccupied(Vector2d position) {
        return true;
//        if(this.cells.get(position) != null || this.plants.get(position) != null) return true;
//        return false;
}

    @Override
    public Object objectAt(Vector2d position) {
        if(this.cells.get(position) != null){
            return this.cells.get(position).size();
//            if(this.cells.get(position).size() > 1) return Integer.toString(this.cells.get(position).size());
//            else return "#";
        }
        if(this.plants.get(position) != null ) return 0;
        return -1;
    }

    public int Ssavanna(){
        return (savannaUpperRight.x - savannaLowerLeft.x + 1) * ( savannaUpperRight.y - savannaLowerLeft.y + 1);
    }

    public int Sjungle(){
        return (jungleUpperRight.x - jungleLowerLeft.x + 1) * ( jungleUpperRight.y - jungleLowerLeft.y + 1) - this.Ssavanna();
    }

    public double averageAge(){
        int sum = 0;
        for(Animal animal : this.animals){
            sum += this.generation - animal.generation;
        }
        return (double) sum/this.animals.size();
    }

    public double averageEnergy(){
        int sum = 0;
        for(Animal animal : this.animals){
            sum += animal.energy;
        }
        return (double) sum/this.animals.size();
    }

    public double averageChildrenCounter(){
        int sum = 0;
        for(Animal animal : this.animals){
            sum += animal.childrenCounter;
        }
        return (double) sum/this.animals.size();
    }

    public String informationAbout(Vector2d position){
        if(this.cells.containsKey(position)){
            if(this.cells.get(position).size()>1){
                return ("Here are "+ Integer.toString(this.cells.get(position).size()) + " animals");
            }
            else{
                Animal animal = this.cells.get(position).get(0);
                return ("Birth date: "+ Integer.toString(animal.generation) + "\nChildren " + Integer.toString(animal.childrenCounter) + "\nDNA: "+ animal.textDNA(animal.dna) + "\nEnergy: " + animal.energy);
            }
        }
        else if(this.plants.containsKey(position)){
            return "Here is a plant";
        }
        else{
            return "There is nothing here";
        }
    }

    public int[] mostCommonDNA(){
        ArrayList<Animal> animalstmp = (ArrayList<Animal>) this.animals.clone();
        Map<int[], Long> groupedGenes = animalstmp.stream().map(animal -> animal.dna).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        try{return Collections.max(groupedGenes.entrySet(), (entry1, entry2) -> (int) (entry1.getValue() - entry2.getValue())).getKey();}
        catch(NoSuchElementException e){ return null; }
    }

}
