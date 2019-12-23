package tests;

import main.Animal;
import main.Area;
import main.Vector2d;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AnimalTest {

    @Test
    void allGensTest(){
        Area map = new Area(0, 0, 10, 10, 10, 10,10, 0.3);
        Animal animal1 = new Animal(map, new Vector2d(2,2));
        Animal animal2 = new Animal(map, new Vector2d(2,3));
        assertTrue(animal1.checkDNA(animal1.dna));
        assertTrue(animal1.checkDNA(animal1.dna));
        Animal child = animal1.makeChild(animal2);
        assertTrue(child.checkDNA(child.dna));

        
    }
}
