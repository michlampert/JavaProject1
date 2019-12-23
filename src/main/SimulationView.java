package main;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SimulationView extends JFrame implements ActionListener {

    Area map;

    JButton ssButton = new JButton("start");
    JButton statsButton = new JButton("save stats");
    JLabel mapStats = new JLabel();
    JButton commonButton = new JButton("show most common DNA");

    MapView panel;

    List<String> statistics = new ArrayList<>();

    public SimulationView(Area map) throws InterruptedException {

        this.map = map;

        //this.setSize(1000,1040);
        this.setTitle("SimulationView");
        this.setLayout(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setVisible(true);

        ssButton.setBounds(10,10,100,20);
        ssButton.addActionListener(this);
        this.add(ssButton);

        statsButton.setBounds(120,10,100,20);
        statsButton.addActionListener(this);
        this.add(statsButton);

        mapStats.setBounds(340,10,570,20);
        mapStats.setText("day: " + this.map.generation + ", animals: " + map.animals.size() + ", plants: "+ map.plants.size() + ", average age: " + String.format("%.2f", map.averageAge()) + ", average fertility: " + String.format("%.2f", map.averageChildrenCounter()) + ", average energy: " + String.format("%.2f", map.averageEnergy()));
        this.add(mapStats);

        commonButton.setBounds(230,10,100,20);
        commonButton.addActionListener(this);
        this.add(commonButton);

        panel = new MapView(this.map, commonButton);
        prepareFrame(this, panel, panel.w, panel.h);

        this.setSize(970,1035);

    }

    public static void runTwo(SimulationView view1, SimulationView view2) throws InterruptedException {
        while(true){
            if(view1.ssButton.getText().equals("stop")){
                view1.panel.repaint(1,0,40,1000,1000);
                view1.mapStats.setText("day: " + view1.map.generation + ", animals: " + view1.map.animals.size() + ", plants: "+ view1.map.plants.size() + ", average age: " + String.format("%.2f", view1.map.averageAge()) + ", average fertility: " + String.format("%.2f", view1.map.averageChildrenCounter()) + ", average energy: " + String.format("%.2f", view1.map.averageEnergy()));
                view1.statistics.add("day: " + view1.map.generation + ", animals: " + view1.map.animals.size() + ", plants: "+ view1.map.plants.size() + ", average age: " + String.format("%.2f", view1.map.averageAge()) + ", average fertility: " + String.format("%.2f", view1.map.averageChildrenCounter()) + ", average energy: " + String.format("%.2f", view1.map.averageEnergy()));
                view1.map.nextDay();
            }
            if(view2.ssButton.getText().equals("stop")){
                view2.panel.repaint(1,0,40,1000,1000);
                view2.mapStats.setText("day: " + view2.map.generation + ", animals: " + view2.map.animals.size() + ", plants: "+ view2.map.plants.size() + ", average age: " + String.format("%.2f", view2.map.averageAge()) + ", average fertility: " + String.format("%.2f", view2.map.averageChildrenCounter()) + ", average energy: " + String.format("%.2f", view2.map.averageEnergy()));
                view2.statistics.add("day: " + view2.map.generation + ", animals: " + view2.map.animals.size() + ", plants: "+ view2.map.plants.size() + ", average age: " + String.format("%.2f", view2.map.averageAge()) + ", average fertility: " + String.format("%.2f", view2.map.averageChildrenCounter()) + ", average energy: " + String.format("%.2f", view2.map.averageEnergy()));
                view2.map.nextDay();
            }
            TimeUnit.SECONDS.sleep(1);
        }
    }

    public void run() throws InterruptedException {
        while(true){
            TimeUnit.MILLISECONDS.sleep(100);
            while(ssButton.getText().equals("stop")){
                panel.repaint(1,0,40,1000,1000);
                mapStats.setText("day: " + this.map.generation + ", animals: " + map.animals.size() + ", plants: "+ map.plants.size() + ", average age: " + String.format("%.2f", map.averageAge()) + ", average fertility: " + String.format("%.2f", map.averageChildrenCounter()) + ", average energy: " + String.format("%.2f", map.averageEnergy()));
                this.statistics.add("day: " + this.map.generation + ", animals: " + map.animals.size() + ", plants: "+ map.plants.size() + ", average age: " + String.format("%.2f", map.averageAge()) + ", average fertility: " + String.format("%.2f", map.averageChildrenCounter()) + ", average energy: " + String.format("%.2f", map.averageEnergy()));
                this.map.nextDay();
                TimeUnit.SECONDS.sleep(1);
            }
        }
    }

    private void prepareFrame(JFrame frame, MapView visualization, int width, int height){
        frame.add(visualization);
        frame.setSize(1000 , 1000);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        Object source = e.getSource();
        if(source == ssButton)
        {
            if(ssButton.getText().equals("start")) {
                ssButton.setText("stop");
            }
            else{
                ssButton.setText("start");
            }
        }
        else if(source == statsButton){
            WriteStatisticsToFile.writeToFile(this.statistics);
        }
        else if(source == commonButton)
        {
            if(commonButton.getText().equals("show most common DNA")) {
                commonButton.setText("hide most common DNA");
            }
            else{
                commonButton.setText("show most common DNA");
            }
        }
    }
}
