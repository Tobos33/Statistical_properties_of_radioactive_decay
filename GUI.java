package Main;
import javax.swing.*;
import org.jfree.chart.*;
import org.jfree.chart.labels.*;
import org.jfree.chart.plot.*;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.lang.Math;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import javax.swing.text.*;
import java.text.*;

public class GUI extends JFrame {
    List<String> atomsList;
    List<Double> atomsLambdaConst;
    Map<String,Double> mapLambdaConst;
    List<String> atomsHalfTime;
    Map<String,String> mapHalfTime;
    String[] timeList = {"sekund", "minut", "godzin", "tygodni", "miesięcy", "lat"};
    JPanel startPanel;
    JPanel optionsPanel;
    JPanel histogramsPanel;
    JPanel timePanel;
    JButton start;
    JButton stop;
    JMenuBar menuBar;
    JMenu menu;
    JMenu menuChartCustom;
    JMenu subMenuSaveData;
    JMenu subMenuSaveChart;
    JMenuItem itemNew;
    JMenuItem itemSaveData;
    JMenuItem itemSaveDataSQL;
    JMenuItem itemSaveChart;
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
    JTextField textNumber;
    JComboBox comboTimeChoser;
    JLabel sliderValueTimeHop;
    JSlider sliderTimeHop;
    JLabel sliderValueStartNucle;
    JSlider sliderStartNucle;
    JFreeChart chartParticleNumber;
    JFreeChart chartActivity;
    Double userLambdaConst;
    String userTimeRange;
    Float userTimeNumber;
    Integer userTimeHop;
    Double userStartNucle;

    ChartPanel chartPanel2;
    ChartPanel chartPanel1;
    public GUI() throws HeadlessException{
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
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

        chartParticleNumber = ChartFactory.createXYLineChart(
                "Wykres liczby jąder w próbce od czasu",
                "t",
                "N",
                new XYSeriesCollection(),
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        chartActivity = ChartFactory.createXYLineChart(
                "Wykres aktywności promieniotwórczej próbki od czasu",
                "t",
                "R",
                new XYSeriesCollection(),
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        Border chartborder = BorderFactory.createLineBorder(Color.BLACK, 1);

        ChartPanel chartPanel1 = new ChartPanel(chartParticleNumber);
        chartPanel1.setBorder(chartborder);
        //chartPanel1.setPreferredSize(new java.awt.Dimension(800, 350));

        ChartPanel chartPanel2 = new ChartPanel(chartActivity);
        chartPanel2.setBorder(chartborder);
        //chartPanel2.setPreferredSize(new java.awt.Dimension(800, 350));

        histogramsPanel.add(chartPanel1);
        histogramsPanel.add(chartPanel2);

        //LISTA PIERWIASTKOW I STAŁE

        atomsList = new ArrayList<String>();
        atomsList.add("węgiel C14");
        atomsList.add("uran-238");

        atomsLambdaConst = new ArrayList<Double>();
        atomsLambdaConst.add(3.9*Math.pow(10,-12));
        atomsLambdaConst.add(5.0*Math.pow(10,-18));

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
        //koniec LISTA PIERWIASTKO I STALE

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

        labelLambda = new JLabel( Double.toString(mapLambdaConst.get("węgiel C14")));
        userLambdaConst = mapLambdaConst.get("węgiel C14"); // DOMYSLNIE WEGIEL MA BYC WYBRANY
        labelT = new JLabel(mapHalfTime.get("węgiel C14"));
        comboNucleChoser.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String key = (String) comboNucleChoser.getSelectedItem();
                if (key != null) {
                    userLambdaConst = mapLambdaConst.get(key);
                    labelLambda.setText(userLambdaConst != null
                            ? userLambdaConst.toString()
                            : "N/A"
                    );
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
        textNumber.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
              //  userTimeNumber =  textNumber.getText(); DO NAPRAWIENIA, string na float sie nie skonwertuje
            }
        });

        sliderTimeHop = new JSlider(10,1000,500 );
        sliderTimeHop.setMajorTickSpacing(50);
        sliderTimeHop.setPaintTicks(true);
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        labelTable.put(10, new JLabel("10"));
        labelTable.put(500, new JLabel("500"));
        labelTable.put(1000, new JLabel("1000"));
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
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable(){
            public void run() {
                GUI Frame = new GUI();
                Frame.setVisible(true);
            }
        });

    }
}
