package main;

import java.util.Arrays;
import java.util.Random;

public class Animal {

    public Area map;
    public int energy;
    public int[] dna = new int[32];
    public Vector2d position;
    public int generation;
    public int childrenCounter = 0;

    public Vector2d[] possibleSteps = {new Vector2d(0, 1),new Vector2d(1, 1),new Vector2d(1, 0),new Vector2d(1, -1),
                                                            new Vector2d(0, -1),new Vector2d(-1, -1),new Vector2d(-1, 0),new Vector2d(-1, 1)};

    Random generator = new Random();

    public String toString(){ return this.position.toString(); }

    public Animal(Area map, Vector2d position){
        this.map = map;
        this.position = position;
        this.energy = this.map.startEnergy;
        this.dna = randomDNA();
        this.generation = 0;
    }

    public Animal(Area map, Vector2d position, int energy){
        this.map = map;
        this.position = position;
        this.energy = energy;
        this.dna = randomDNA();
        this.generation = 0;
    }

    public Animal(Area map, Vector2d position, int energy, int[] dna, int generation){
        this.map = map;
        this.position = position;
        this.energy = energy;
        this.dna = dna;
        this.generation = generation;
    }

    public Vector2d getPosition(){
        return this.position;
    }

    public Animal makeChild(Animal otherAnimal){
        this.childrenCounter++;
        otherAnimal.childrenCounter++;
        int[] dna = mixDNA(this, otherAnimal);
        int energy = takeParentsEnergy(this, otherAnimal);
        return new Animal(this.map, this.childPosition(), energy, dna, this.map.generation);
    }

    public Vector2d childPosition(){
        Vector2d result = this.getPosition();
        for(Vector2d step : this.possibleSteps){
            result = this.getPosition().add(step);
            if(!this.map.cells.containsKey(result)){
                return result;
            }
        }
        return result;
    }

    public void eat(int food){
        this.energy += food;
    }

    private int takeParentsEnergy(Animal parent1, Animal parent2){
        int energy = 0;
        energy += (int) (parent1.energy / 4);
        parent1.energy -= (int) (parent1.energy / 4);
        energy += (int) (parent2.energy / 4);
        parent2.energy -= (int) (parent2.energy / 4);
        return energy;
    }

    private int[] randomDNA(){
        int[] dna = new int[32];

        for(int i = 0; i < 32; i++){
            dna[i] = this.generator.nextInt(8);
        }

        while(!checkDNA(dna)) {
            for(int i = 0; i < 32; i++){
                dna[i] = this.generator.nextInt(8);
            }
        }
        Arrays.sort(dna);
        return dna;
    }

    private int[] mixDNA(Animal parent1, Animal parent2){
        int[] dna = new int[32];

        for(int i = 0; i < 32; i++){
            dna[i] = 0;
        }

        while(!checkDNA(dna)){
            int range1 = this.generator.nextInt(32);
            int range2 = this.generator.nextInt(32);

            while(range1 == range2){ range2 = this.generator.nextInt(32); }
            if(range1 > range2){
                int tmpInt = range1;
                range1 = range2;
                range2 = tmpInt;
            }

            int[] tmpArray = new int[3];
            tmpArray[0] = this.generator.nextInt(2);
            tmpArray[1] = this.generator.nextInt(2);
            switch(tmpArray[0] + tmpArray[1]){
                case 0: {
                    tmpArray[2] = 1; break; }
                case 1:{
                    tmpArray[2] = this.generator.nextInt(2); break; }
                case 2:{
                    tmpArray[2] = 0; break;
                }
            }
            for(int i = 0; i < 32; i++){
                if(i < range1){
                    if(tmpArray[0] == 0) { dna[i] = parent1.dna[i]; }
                    else{ dna[i] = parent2.dna[i]; }
                }
                else if(range1 <= i && i < range2){
                    if(tmpArray[1] == 0) { dna[i] = parent1.dna[i]; }
                    else{ dna[i] = parent2.dna[i];; }
                }
                else{
                    if(tmpArray[2] == 0) { dna[i] = parent1.dna[i]; }
                    else{ dna[i] = parent2.dna[i]; }
                }
            }
        }
        Arrays.sort(dna);
        return dna;
    }

    public String textDNA(int[] dna){
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < 32; i++){
            result.append(Integer.toString(dna[i]));
        }
        return result.toString();
    }

    public boolean checkDNA(int[] dna){
        boolean [] tmp = new boolean[8];
        for(int i = 0; i < 8; i++){
            tmp[i] = false;
        }

        for(int i = 0; i < 32; i++){
            tmp[dna[i] ]= true;
        }

        for(int i = 0; i < 8; i++){
            if(tmp[i] == false) return false;
        }
        return true;
    }

    public Vector2d nextStep(){
        return (this.possibleSteps[this.dna[this.generator.nextInt(32)]]);
    }

    public Vector2d nextPosition(){
        return this.position.add(this.nextStep());
    }

    public void move(){
//        Vector2d oldPosition = this.position;
//        Vector2d newPosition = this.nextPosition();
        this.map.removeAnimal(this);
        this.energy -= this.map.moveEnergy;
        this.position = this.nextPosition().mod(map.savannaUpperRight);
        if(this.energy >  0){
            this.map.placeAnimal(this);
        }
    }
}
