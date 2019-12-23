package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;

import static java.lang.Integer.max;
import static java.lang.Integer.min;

public class MapView extends JPanel implements MouseListener {

    private final Area map;
    public int w, h;
    private int cellSize;
    private int freeSpace = 40;

    JButton commonButton ;

    public MapView(Area map, JButton button){
        this.map = map;
        this.w = map.savannaUpperRight.x + 1;
        this.h = map.savannaUpperRight.y + 1;
        this.cellSize = 960 / max(this.w, this.h);
        this.setSize(new Dimension(this.cellSize * this.w, this.cellSize * this.h  + this.freeSpace));
        this.addMouseListener(this);

        this.commonButton = button;
        this.commonButton.setText("show most common DNA");
        //this.commonButton.addActionListener(this);
        //this.add(this.commonButton);
    }

    @Override
    public void paintComponent(Graphics g) {
        fillMap(g);
        paintPlants(g);
        paintAnimals(g);
    }

    public void fillMap(Graphics g){
        //super.paintComponent(g);
        this.drawMap(g);
        this.drawGrid(g);

    }

    public void drawMap(Graphics g){
        g.setColor(new Color(100,150, 100));
        g.fillRect(0,this.freeSpace, (this.w)*this.cellSize, (this.h)*this.cellSize);
    }

    public void drawGrid(Graphics g){
        g.setColor(Color.BLACK);
        for(int i = 0; i < w; i++){
            for(int j = 0 ; j < h; j++){
                g.drawRect(i* this.cellSize, j* this.cellSize + this.freeSpace, this.cellSize, this.cellSize);
            }
        }
    }

    public void paintAnimals(Graphics g){
        for(int i = 0; i < w; i++){
            for(int j = 0; j < h; j++){
                if(this.map.cells.containsKey(new Vector2d(i, j)) && this.map.cells.get(new Vector2d(i, j)).size() > 0 ){

                    int amount = this.map.cells.get(new Vector2d(i, j)).size();

                    for(int n = 0; n < amount; n++){
                        if(commonButton.getText().equals("hide most common DNA") && Arrays.equals(this.map.cells.get(new Vector2d(i, j)).get(n).dna, this.map.commonDNA)){
                            g.setColor(new Color(min(255,this.map.cells.get(new Vector2d(i, j)).get(n).energy), min(255,this.map.cells.get(new Vector2d(i, j)).get(n).energy), 255));
                        }
                        else{
                            g.setColor(new Color(255, min(255,this.map.cells.get(new Vector2d(i, j)).get(n).energy), min(255,this.map.cells.get(new Vector2d(i, j)).get(n).energy)));
                        }
                        g.fillOval(i * this.cellSize + (n+1) * this.cellSize/ (2 * (amount + 1)), j * this.cellSize + this.cellSize/4  + this.freeSpace, this.cellSize - this.cellSize/2, this.cellSize - this.cellSize/2);
                    }
                }
            }
        }
    }

    public void paintPlants(Graphics g){
        for(int i = 0; i < w; i++){
            for(int j = 0; j < h; j++){
                if(this.map.plants.containsKey(new Vector2d(i, j))){
                        g.setColor(new Color(0,100,0));
                        g.fillOval(i * this.cellSize + this.cellSize/4, j * this.cellSize + this.cellSize/4  + this.freeSpace, this.cellSize - this.cellSize/2, this.cellSize - this.cellSize/2);
                }
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        if(x <= this.cellSize * this.w && y <= this.cellSize * this.h  + this.freeSpace && y >= this.freeSpace){
            int vx = x / this.cellSize;
            int vy = (y - this.freeSpace) / this.cellSize;

            JOptionPane.showMessageDialog(this, this.map.informationAbout(new Vector2d(vx, vy)), "Statistics about " + new Vector2d(2,2).toString() + " position in " + this.map.generation + " day",JOptionPane.INFORMATION_MESSAGE);
        }


    }

    @Override
    public void mousePressed(MouseEvent e) {

    }


    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

//    @Override
//    public void actionPerformed(ActionEvent e) {
//        Object source = e.getSource();
//        if(source == commonButton)
//        {
//            if(commonButton.getText().equals("show most common DNA")) {
//                commonButton.setText("hide most common DNA");
//            }
//            else{
//                commonButton.setText("show most common DNA");
//            }
//        }
//
//    }
}
