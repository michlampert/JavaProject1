package tests;

import main.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AreaTest {

    @Test
    void moveTest(){
        Area map = new Area(0, 0, 10, 10, 10, 10,10, 0.3);
        map.placeAnimal(new Animal(map, new Vector2d(2,2)));
        assertEquals((map.objectAt(new Vector2d(2,2))), 1);
        map.nextDay();
        assertNotEquals(map.objectAt(new Vector2d(2,2)), 1);
    }

    @Test
    void typeOfCellRecognition(){
        Area map = new Area(0, 0, 10, 10, 10, 10,10, 0.3);
        assertTrue(map.isInSavanna(new Vector2d(0,0)));
        assertFalse(map.isInSavanna(new Vector2d(5,5)));
        assertTrue(map.isInJungle(new Vector2d(6,6)));
        assertFalse(map.isInJungle(new Vector2d(0,0)));
    }
}
