package Main;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.lang.Math;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;


public class GUI extends JFrame {
    Thread simThread;
    static List<String> atomsList;
    static List<Double> atomsLambdaConst;
    static HashMap<String, Double> mapLambdaConst;
    static HashMap<String, Double> mapTimediv = new HashMap<String, Double>();
    List<String> atomsHalfTime;
    HashMap<String,String> mapHalfTime;
    Double[] Timediv =   {1.0,     60.0,    3600.0,   7*24*3600.0, 30*24*3600.0, 365*24*3600.0, (365*24*3600*Math.pow(10, 6))};
    String[] timeList = {"sekund", "minut", "godzin", "tygodni", "miesięcy", "lat", "miliony lat"};
    JPanel startPanel;
    JPanel optionsPanel;
    JPanel timePanel;
    JButton start;
    JButton stop;
    static JComboBox comboNucleChoser;
    JLabel labelRadioactiveConstant;
    JLabel labelLambda;
    JLabel labelHalfTime;
    JLabel labelT;
    JLabel time;
    static JTextField textNumber;
    static JComboBox comboTimeChoser;
    JLabel sliderValueTimeHop;
    static JSlider sliderTimeHop;
    JLabel sliderValueStartNucle;
    static JSlider sliderStartNucle;
    static double userLambdaConst;
    static String userTimeRange;
    static int userTimeHop;
    static double userStartNucle = Math.pow(10, 6);
    Menu menuBar;
    static Charts histogramsPanel;


    public GUI() throws HeadlessException{
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(1200, 600);
        this.setLayout(new BorderLayout());
        this.setTitle("Statistical properties of radioactive decay simulation");

        startPanel = new JPanel();
        optionsPanel = new JPanel();
        histogramsPanel = new Charts();
        timePanel = new JPanel();

        this.add(startPanel, BorderLayout.SOUTH);
        this.add(optionsPanel, BorderLayout.CENTER);
        this.add(histogramsPanel, BorderLayout.WEST);

        optionsPanel.setLayout(new GridLayout(5, 1));
        histogramsPanel.setLayout(new GridLayout(2, 1));

        histogramsPanel.setPreferredSize(new java.awt.Dimension(800, 2*350));
        optionsPanel.setPreferredSize(new java.awt.Dimension(350, 2*350));

        start = new JButton("Start");

        stop = new JButton("Stop");
        stop.addActionListener(e -> simThread.interrupt());
        start.setBackground(Color.green);
        stop.setBackground(Color.red);
        start.setPreferredSize(new Dimension(200, 50));
        stop.setPreferredSize(new Dimension(200, 50));
        startPanel.add(start);
        startPanel.add(stop);

        menuBar = new Menu();
        this.setJMenuBar(menuBar);

        save savingCharts = new save();
        LoadFile loadingFiles = new LoadFile();


        start.addActionListener(e -> startSimulation());

        atomsList = new ArrayList<String>();
        atomsList.add("polon Po-218");
        atomsList.add("tryt H-3");
        atomsList.add("lit Li-8");
        atomsList.add("bor B-12");
        atomsList.add("wegiel C-14");
        atomsList.add("azot N-13");
        atomsList.add("azot N-16");
        atomsList.add("fluor F-18");
        atomsList.add("potas K-40");
        atomsList.add("technet Tc-99");
        atomsList.add("jod I-131");

        atomsList.add("radon Rn-222");
        atomsList.add("rad Ra-226");
        atomsList.add("uran U-238");


        atomsLambdaConst = new ArrayList<Double>();
        atomsLambdaConst.add(Math.log(2)/(45)*Math.pow(10, 9));
        atomsLambdaConst.add(Math.log(2)/(389)*Math.pow(10, 3));
        atomsLambdaConst.add(Math.log(2)/0.8*Math.pow(10,9));
        atomsLambdaConst.add(Math.log(2)/0.02*Math.pow(10,9));
        atomsLambdaConst.add(Math.log(2)/(5.73*3.6*2.4*3.65));
        atomsLambdaConst.add(Math.log(2)/(597.9)*Math.pow(10,9));
        atomsLambdaConst.add(Math.log(2)/(7.2)*Math.pow(10,9));
        atomsLambdaConst.add(Math.log(2)/(1.10*6)*Math.pow(10,6));
        atomsLambdaConst.add(Math.log(2)/(1.3*3.6*2.4*3.65)*Math.pow(10,-6));
        atomsLambdaConst.add(Math.log(2)/(6*3.6)*Math.pow(10,6));
        atomsLambdaConst.add(Math.log(2)/(693)*Math.pow(10,6));

        atomsLambdaConst.add(Math.log(2)/(330)*Math.pow(10,6));
        atomsLambdaConst.add(Math.log(2)/(50));
        atomsLambdaConst.add(Math.log(2)/(4.5*3.6*2.4*3.65)*Math.pow(10,-6));


        mapLambdaConst = new HashMap<String, Double>();
        for(int i= 0; i<atomsList.size();i++){
            mapLambdaConst.put(atomsList.get(i), atomsLambdaConst.get(i));
        }

        atomsHalfTime = new ArrayList<String>();
        atomsHalfTime.add("45 s");
        atomsHalfTime.add("12.32 lata");
        atomsHalfTime.add("0.8 s");
        atomsHalfTime.add("0.02 s");
        atomsHalfTime.add("5730 lat");
        atomsHalfTime.add("10 min");
        atomsHalfTime.add("7.2 s");
        atomsHalfTime.add("110 min");
        atomsHalfTime.add("1.3xE9 lat");
        atomsHalfTime.add("6 h");
        atomsHalfTime.add("8 dni");
        atomsHalfTime.add("3.8 dnia");
        atomsHalfTime.add("1600 lat");
        atomsHalfTime.add("4.5xE9 lat");

        mapHalfTime = new HashMap<String, String>();
        for(int i= 0; i<atomsList.size();i++){
            mapHalfTime.put(atomsList.get(i), atomsHalfTime.get(i));
        }

        for(int i= 0; i<Timediv.length;i++){
            mapTimediv.put(timeList[i], Timediv[i]);
        }


        JPanel nucleChoserPanel = new JPanel();
        optionsPanel.add(nucleChoserPanel);
        nucleChoserPanel.setLayout(new BorderLayout());
        comboNucleChoser = new JComboBox(atomsList.toArray());
        JPanel labelNucleChoserPanel = new JPanel();
        labelNucleChoserPanel.setLayout(new FlowLayout());
        JLabel labelNucleChoser = new JLabel("Wybierz nuklid (domyślnie węgiel):");
        nucleChoserPanel.add(labelNucleChoserPanel, BorderLayout.NORTH);
        labelNucleChoserPanel.add(labelNucleChoser);
        JPanel comboNucleChoserPanel = new JPanel();
        comboNucleChoserPanel.setLayout(new FlowLayout());
        nucleChoserPanel.add(comboNucleChoserPanel, BorderLayout.CENTER);
        comboNucleChoserPanel.add(comboNucleChoser);
        comboNucleChoser.setPreferredSize( new Dimension(250, 25));

        labelLambda = new JLabel(String.valueOf((mapLambdaConst.get("polon Po-218")*Math.pow(10, -9))));
        userLambdaConst = mapLambdaConst.get("polon Po-218"); // DOMYSLNIE WEGIEL MA BYC WYBRANY
        labelT = new JLabel(mapHalfTime.get("polon Po-218"));
        comboNucleChoser.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String key = (String) comboNucleChoser.getSelectedItem();
                if (key != null) {
                    userLambdaConst = mapLambdaConst.get(key);
                    labelLambda.setText(String.valueOf(mapLambdaConst.get(key)*Math.pow(10, -9)));
                    labelT.setText(mapHalfTime.get(key));
                }
            }
        });


        JPanel constantPanel = new JPanel();
        optionsPanel.add(constantPanel);
        constantPanel.setLayout(new GridLayout(2, 1));
        JPanel tempPanelConst = new JPanel();
        tempPanelConst.setLayout(new FlowLayout());
        constantPanel.add(tempPanelConst);
        JPanel tempPanelConst1 = new JPanel();
        tempPanelConst1.setLayout(new FlowLayout());
        constantPanel.add(tempPanelConst1);
        labelRadioactiveConstant = new JLabel("Stała rozpadu promieniotwórczego: λ = ");
        tempPanelConst.add(labelRadioactiveConstant);
        tempPanelConst.add(labelLambda);
        labelHalfTime = new JLabel("Czas połowicznego rozpadu: T = ");
        tempPanelConst1.add(labelHalfTime);
        tempPanelConst1.add(labelT);



        JPanel timePanel = new JPanel();
        timePanel.setLayout(new BorderLayout());
        optionsPanel.add(timePanel);
        time = new JLabel("Czas");
        JPanel tempPanel = new JPanel();
        tempPanel.setLayout(new FlowLayout());
        tempPanel.add(time);
        timePanel.add(tempPanel, BorderLayout.NORTH);



        JPanel timeChoserPanel = new JPanel();
        timeChoserPanel.setLayout(new FlowLayout());
        timePanel.add(timeChoserPanel, BorderLayout.CENTER);
        textNumber = new JTextField("10");
        textNumber.setPreferredSize(new Dimension(150, 50));
        timeChoserPanel.add(textNumber);
        comboTimeChoser = new JComboBox(timeList);
        comboTimeChoser.setSelectedIndex(0);
        comboTimeChoser.setPreferredSize(new Dimension(200, 50));
        timeChoserPanel.add(comboTimeChoser);

        userTimeRange = timeList[0];
        comboTimeChoser.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                userTimeRange = comboTimeChoser.getSelectedItem().toString();
            }
        });


        sliderTimeHop = new JSlider(10,100,55 );
        sliderTimeHop.setMajorTickSpacing(10);
        sliderTimeHop.setPaintTicks(true);
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        labelTable.put(10, new JLabel("10"));
        labelTable.put(100, new JLabel("100"));
        sliderTimeHop.setLabelTable(labelTable);
        sliderTimeHop.setPaintLabels(true);
        sliderTimeHop.addChangeListener(new SliderTimeHopChangeListener());
        JPanel timeHopPanel = new JPanel();
        timeHopPanel.setLayout(new BorderLayout());
        optionsPanel.add(timeHopPanel);
        JLabel labelTimeHop = new JLabel("Liczba skoków czasowych");
        sliderValueTimeHop = new JLabel(String.format("%d", sliderTimeHop.getValue()));
        userTimeHop = sliderTimeHop.getValue();
        JLabel labelTimeHop1 = new JLabel("Szybki wykres");
        JLabel labelTimeHop2 = new JLabel("Dokładny wykres");
        JPanel labelTimeHopPanel = new JPanel();
        labelTimeHopPanel.setLayout(new FlowLayout());
        timeHopPanel.add(labelTimeHopPanel, BorderLayout.NORTH);
        labelTimeHopPanel.add(labelTimeHop);
        labelTimeHopPanel.add(sliderValueTimeHop);
        timeHopPanel.add(sliderTimeHop, BorderLayout.CENTER);
        timeHopPanel.add(labelTimeHop1, BorderLayout.WEST);
        timeHopPanel.add(labelTimeHop2, BorderLayout.EAST);



        sliderStartNucle = new JSlider(4,8,6 );
        sliderStartNucle.setMajorTickSpacing(1);
        sliderStartNucle.setPaintTicks(true);
        Hashtable<Integer, JLabel> labelTable1 = new Hashtable<>();
        labelTable1.put(4, new JLabel("10e4"));
        labelTable1.put(6, new JLabel("10e6"));
        labelTable1.put(8, new JLabel("10e8"));
        sliderStartNucle.setLabelTable(labelTable1);
        sliderStartNucle.setPaintLabels(true);
        sliderStartNucle.addChangeListener(new SliderStartNucleChangeListener());
        JPanel startNuclePanel = new JPanel();
        startNuclePanel.setLayout(new BorderLayout());
        optionsPanel.add(startNuclePanel);
        JLabel labelStartNucle  = new JLabel("Początkowa liczba nuklidów:");
        sliderValueStartNucle = new JLabel("10e" + String.format("%d", sliderStartNucle.getValue()));
        JPanel labelStartNuclePanel = new JPanel();
        labelStartNuclePanel.setLayout(new FlowLayout());
        startNuclePanel.add(labelStartNuclePanel, BorderLayout.NORTH);
        labelStartNuclePanel.add(labelStartNucle);
        labelStartNuclePanel.add(sliderValueStartNucle);
        startNuclePanel.add(sliderStartNucle, BorderLayout.CENTER);





    }




    public class SliderStartNucleChangeListener implements ChangeListener{

        @Override
        public void stateChanged(ChangeEvent arg0) {
            String value = String.format("%d", sliderStartNucle.getValue());
            sliderValueStartNucle.setText("10e" + value);
            userStartNucle = Math.pow(10,sliderStartNucle.getValue());
        };

    }
    public class SliderTimeHopChangeListener implements ChangeListener{

        @Override
        public void stateChanged(ChangeEvent arg0) {
            String value = String.format("%d", sliderTimeHop.getValue());
            sliderValueTimeHop.setText(value);
            userTimeHop = sliderTimeHop.getValue();

        };

    }

    public void startSimulation() {
        if(simThread != null) {
            simThread.interrupt();
        }
        SimN simulation = new SimN();
        simThread = new Thread(simulation);
        simThread.start();
        simulation.AnalyticalValues();
        histogramsPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable(){
            public void run() {
                GUI Frame = new GUI();
                Frame.setVisible(true);
            }
        });

    }
}

