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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

public class GUI extends JFrame {

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
    JSlider sliderTimeHop;
    JSlider sliderStartNucle;
    JFreeChart chartParticleNumber;
    JFreeChart chartActivity;


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
        this.add(optionsPanel, BorderLayout.EAST);
        this.add(histogramsPanel, BorderLayout.WEST);

        optionsPanel.setLayout(new GridLayout(6, 1));
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
        menuChartCustom.add(itemChartBackgroundGrid);

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


        optionsPanel.add(new JPanel());
        optionsPanel.add(new JPanel()); // DO USUNIECIA NARAZIE PRZESUWA MI sliderStartNucle na (3,1) w gridLayout
        sliderTimeHop = new JSlider(10,1000,500 );
        sliderTimeHop.setMajorTickSpacing(50);
        sliderTimeHop.setPaintTicks(true);
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        labelTable.put(10, new JLabel("10"));
        labelTable.put(1000, new JLabel("1000"));
        sliderTimeHop.setLabelTable(labelTable);
        sliderTimeHop.setPaintLabels(true);
        optionsPanel.add(sliderTimeHop);

        sliderStartNucle = new JSlider(10,1000,500 );    //TRZEBA BEDZIE MNOZYC TE WARTOSCI ZE SLIDERA x1000
        sliderStartNucle.setMajorTickSpacing(50);
        sliderStartNucle.setPaintTicks(true);
        Hashtable<Integer, JLabel> labelTable1 = new Hashtable<>();
        labelTable1.put(10, new JLabel("10e4"));
        labelTable1.put(1000, new JLabel("10e6"));
        sliderStartNucle.setLabelTable(labelTable1);
        sliderStartNucle.setPaintLabels(true);
        optionsPanel.add(sliderStartNucle);


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
