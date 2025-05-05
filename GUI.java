package Main;
import javax.swing.*;
import org.jfree.chart.*;
import org.jfree.chart.labels.*;
import org.jfree.chart.plot.*;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.lang.Math;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;
import javax.swing.text.*;
import java.text.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static Main.SimN.chartParticleNumber;
import static Main.SimN.chartActivity;

public class GUI extends JFrame {
    Thread simThread;
    static boolean running = true;
    static List<String> atomsList;
    List<Double> atomsLambdaConst;
    static HashMap<String, Double> mapLambdaConst;
    static HashMap<String, Double> mapTimediv = new HashMap<String, Double>();
    List<String> atomsHalfTime;
    HashMap<String,String> mapHalfTime;
    Double[] Timediv =   {1.0,     60.0,    3600.0,   7*24*3600.0, 30*24*3600.0, 365*24*3600.0, (365*24*3600*Math.pow(10, 6))};
    String[] timeList = {"sekund", "minut", "godzin", "tygodni", "miesięcy", "lat", "miliony lat"};
    JPanel startPanel;
    JPanel optionsPanel;
    static JPanel histogramsPanel;
    JPanel timePanel;
    JButton start;
    JButton stop;
    JMenuBar menuBar;
    JMenu menu;
    JMenu menuChartCustom;
    JMenu subMenuSaveData;
    JMenu subMenuSaveChart;
    JMenuItem itemNew;
    static JMenuItem itemSaveData;
    JMenuItem itemSaveDataSQL;
    static JMenuItem itemSaveChart;
    JMenuItem itemSaveChartSQL;
    JMenuItem itemOpen;
    JMenuItem itemDataColor;
    JCheckBoxMenuItem itemChartBackgroundGrid;
    JComboBox comboNucleChoser;
    JLabel labelRadioactiveConstant;
    JLabel labelLambda;
    JLabel labelHalfTime;
    JLabel labelT;
    JLabel time;
    static JTextField textNumber;
    JComboBox comboTimeChoser;
    JLabel sliderValueTimeHop;
    JSlider sliderTimeHop;
    JLabel sliderValueStartNucle;
    static JSlider sliderStartNucle;
    static double userLambdaConst;
    static String userTimeRange;
    static double userTimeNumber = 10.0;
    static int userTimeHop;
    static double userStartNucle;

    ChartPanel chartPanel2;
    ChartPanel chartPanel1;
    public GUI() throws HeadlessException{
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(1200, 600);
        this.setLayout(new BorderLayout());
        this.setTitle("Statistical properties of radioactive decay simulation");

        startPanel = new JPanel();
        optionsPanel = new JPanel();
        histogramsPanel = new JPanel();
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


        menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);

        menu = new JMenu("Menu");
        menuBar.add(menu);

        menuChartCustom = new JMenu("Personalizacja wykresów");
        menuBar.add(menuChartCustom);

        itemDataColor = new JMenuItem("Zmień kolor danych");
        menuChartCustom.add(itemDataColor);

        itemChartBackgroundGrid = new JCheckBoxMenuItem("Siatka w tle");
        itemChartBackgroundGrid.setSelected(true);
        menuChartCustom.add(itemChartBackgroundGrid);

        itemChartBackgroundGrid.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean selected = itemChartBackgroundGrid.isSelected();
                XYPlot plotActivity = chartActivity.getXYPlot();
                XYPlot plotParticleNumber = chartParticleNumber.getXYPlot();

                plotActivity.setDomainGridlinesVisible(selected);
                plotActivity.setRangeGridlinesVisible(selected);
                plotParticleNumber.setDomainGridlinesVisible(selected);
                plotParticleNumber.setRangeGridlinesVisible(selected);
                chartPanel1.repaint();
                chartPanel2.repaint();
            }
        });



        itemNew = new JMenuItem("Nowy");
        menu.add(itemNew);

        subMenuSaveData = new JMenu("Zapisz dane...");
        menu.add(subMenuSaveData);

        itemSaveData = new JMenuItem("Zapisz dane na dysku");
        subMenuSaveData.add(itemSaveData);

        itemSaveDataSQL = new JMenuItem("Zapisz dane do bazy SQL");
        subMenuSaveData.add(itemSaveDataSQL);

        subMenuSaveChart = new JMenu("Zapisz wykresy...");
        menu.add(subMenuSaveChart);

        itemSaveChart = new JMenuItem("Zapisz wykresy na dysku");
        subMenuSaveChart.add(itemSaveChart);

        itemSaveChartSQL = new JMenuItem("Zapisz wykresy do bazy SQL");
        subMenuSaveChart.add(itemSaveChartSQL);

        itemOpen = new JMenuItem("Otwórz");
        menu.add(itemOpen);

        save savingCharts = new save();



        chartParticleNumber = ChartFactory.createXYLineChart(
                "Liczba pozostałych nuklidów w czasie",
                "t",
                "N(t)",
                new XYSeriesCollection(),
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        chartPanel1 = new ChartPanel(chartParticleNumber);
        chartPanel1.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        chartActivity = ChartFactory.createXYLineChart(
                "Wykres aktywności promieniotwórczej próbki od czasu",
                "t "+ userTimeRange,
                "R [nBq]",
                new XYSeriesCollection(),
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        Border chartborder = BorderFactory.createLineBorder(Color.BLACK, 1);

        chartPanel2 = new ChartPanel(chartActivity);
        chartPanel2.setBorder(chartborder);

        histogramsPanel.add(chartPanel1);
        histogramsPanel.add(chartPanel2);

        start.addActionListener(e -> startSimulation());

        atomsList = new ArrayList<String>();
        atomsList.add("węgiel C14");
        atomsList.add("uran-238");

        atomsLambdaConst = new ArrayList<Double>();
        atomsLambdaConst.add(Math.log(2)/(5.57*3.6*2.4*3.65));
        atomsLambdaConst.add(Math.log(2)/(4.5*3.6*2.4*3.65)*Math.pow(10,-6)); //9+3+1+2 = 15 zer

        mapLambdaConst = new HashMap<String, Double>();
        for(int i= 0; i<atomsList.size();i++){
            mapLambdaConst.put(atomsList.get(i), atomsLambdaConst.get(i));
        }

        atomsHalfTime = new ArrayList<String>();
        atomsHalfTime.add("5570 lat");
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
        comboNucleChoser = new JComboBox(new Vector(mapLambdaConst.keySet()));
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

        labelLambda = new JLabel(String.valueOf((mapLambdaConst.get("węgiel C14")*Math.pow(10, -9))));
        userLambdaConst = mapLambdaConst.get("węgiel C14"); // DOMYSLNIE WEGIEL MA BYC WYBRANY
        labelT = new JLabel(mapHalfTime.get("węgiel C14"));
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
        /*textNumber.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                userTimeNumber =  Double.parseDouble(textNumber.getText()); //DO NAPRAWIENIA, string na float sie nie skonwertuje
            }
        });*/

        sliderTimeHop = new JSlider(10,100,55 );
        sliderTimeHop.setMajorTickSpacing(10);
        sliderTimeHop.setPaintTicks(true);
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        labelTable.put(10, new JLabel("10"));
        //labelTable.put(50, new JLabel("50"));
        labelTable.put(100, new JLabel("100"));
        sliderTimeHop.setLabelTable(labelTable);
        sliderTimeHop.setPaintLabels(true);
        sliderTimeHop.addChangeListener(new SliderTimeHopChangeListener());
        //sliderTimeHop.setPreferredSize(new Dimension(425, 70));
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



        sliderStartNucle = new JSlider(4,8,6 );    //TRZEBA BEDZIE podnosić 10 do potegi sczytanej ze slidera
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
        //JLabel labelStartNucle1  = new JLabel("                        ");
        //JLabel labelStartNucle2  = new JLabel("              ");
        JPanel labelStartNuclePanel = new JPanel();
        labelStartNuclePanel.setLayout(new FlowLayout());
        startNuclePanel.add(labelStartNuclePanel, BorderLayout.NORTH);
        labelStartNuclePanel.add(labelStartNucle);
        labelStartNuclePanel.add(sliderValueStartNucle);
        startNuclePanel.add(sliderStartNucle, BorderLayout.CENTER);
        //startNuclePanel.add(labelStartNucle1, BorderLayout.EAST);
        //startNuclePanel.add(labelStartNucle2, BorderLayout.WEST);



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
        SimN simulation = new SimN(chartPanel1, chartPanel2);
        simThread = new Thread(simulation);
        simThread.start();


    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable(){
            public void run() {
                GUI Frame = new GUI();
                //System.out.println(" "+userTimeHop);
                Frame.setVisible(true);
            }
        });

    }
}

