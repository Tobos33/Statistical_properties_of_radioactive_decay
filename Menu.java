package Main;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static Main.SimN.chartActivity;
import static Main.SimN.chartParticleNumber;

public class Menu extends JMenuBar {
    JMenuBar menuBar;
    JMenu menu;
    JMenu menuChartCustom;
    JMenu subMenuSaveData;
    JMenu subMenuChooseColor;
    static JMenuItem itemSaveData;
    JMenuItem itemSaveDataSQL;
    static JMenuItem itemSaveChart;
    JMenuItem itemOpen;
    JMenuItem itemNDataColor;
    JMenuItem itemRDataColor;
    JMenuItem itemNAnaliDataColor;
    JMenuItem itemRAnaliDataColor;
    JCheckBoxMenuItem itemChartBackgroundGrid;
    JCheckBoxMenuItem showAnali;
    static boolean selectedshowAnali = false;
    JMenu MenuPoisson;
    JMenuItem Poisson;
    static Color NDataColor = Color.BLUE;
    static Color RDataColor = Color.BLUE;
    static Color NAnaliDataColor = Color.RED;
    static Color RAnaliDataColor = Color.RED;

    Menu(){

        menu = new JMenu("Menu");
        this.add(menu);

        menuChartCustom = new JMenu("Personalizacja wykresów");
        this.add(menuChartCustom);

        MenuPoisson = new JMenu("Poisson");
        Poisson = new JMenuItem("Rozpad jako rozkład Poissona");
        MenuPoisson.add(Poisson);
        this.add(MenuPoisson);

        subMenuChooseColor = new JMenu("Wybiezrz kolor krzywej");
        menuChartCustom.add(subMenuChooseColor);

        itemNDataColor = new JMenuItem("N(t) - symulacja");
        subMenuChooseColor.add(itemNDataColor);
        itemRDataColor = new JMenuItem("R(t) - symulacja");
        subMenuChooseColor.add(itemRDataColor);
        itemNAnaliDataColor = new JMenuItem("N(t) - teoria");
        subMenuChooseColor.add(itemNAnaliDataColor);
        itemRAnaliDataColor = new JMenuItem("R(t) - teoria");
        subMenuChooseColor.add(itemRAnaliDataColor);

        showAnali = new JCheckBoxMenuItem("Pokaż wykresy Analityczne");
        showAnali.setSelected(false);


        menuChartCustom.add(showAnali);

        itemChartBackgroundGrid = new JCheckBoxMenuItem("Siatka w tle");
        itemChartBackgroundGrid.setSelected(true);
        menuChartCustom.add(itemChartBackgroundGrid);

        subMenuSaveData = new JMenu("Zapisz dane...");
        menu.add(subMenuSaveData);

        itemSaveData = new JMenuItem("Zapisz dane na dysku");
        subMenuSaveData.add(itemSaveData);

        itemSaveDataSQL = new JMenuItem("Zapisz dane do bazy SQL");
        subMenuSaveData.add(itemSaveDataSQL);



        itemSaveChart = new JMenuItem("Zapisz wykresy na dysku");
        menu.add(itemSaveChart);


        itemOpen = new JMenuItem("Otwórz");
        menu.add(itemOpen);

        itemChartBackgroundGrid.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean selected = itemChartBackgroundGrid.isSelected();
                XYPlot plotActivity = chartActivity.getXYPlot();
                XYPlot plotParticleNumber = chartParticleNumber.getXYPlot();

                plotActivity.setDomainGridlinesVisible(selected);
                plotActivity.setRangeGridlinesVisible(selected);
                plotParticleNumber.setDomainGridlinesVisible(selected);
                plotParticleNumber.setRangeGridlinesVisible(selected);
                GUI.chartPanel1.repaint();
                GUI.chartPanel2.repaint();

            }
        });

        showAnali.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectedshowAnali = showAnali.isSelected();
                if (selectedshowAnali) {
                    if(SimN.NseriesAnali != null) {
                        SimN.Ncollection.addSeries(SimN.NseriesAnali);
                        SimN.Rcollection.addSeries(SimN.RseriesAnali);

                        SimN.rendererN.setSeriesLinesVisible(1, selectedshowAnali);
                        SimN.rendererN.setSeriesShapesVisible(1, false);
                        SimN.rendererN.setSeriesPaint(1, Color.RED);

                        SimN.rendererR.setSeriesLinesVisible(1, Menu.selectedshowAnali);
                        SimN.rendererR.setSeriesShapesVisible(1, false);
                        SimN.rendererR.setSeriesPaint(1, Color.RED);

                        SimN.plotN.setRenderer(SimN.rendererN);
                        SimN.plotR.setRenderer(SimN.rendererR);

                        GUI.chartPanel1.repaint();
                        GUI.chartPanel2.repaint();
                    }

                }
                else if(!selectedshowAnali){
                    if(SimN.NseriesAnali != null) {
                        SimN.Ncollection.removeSeries(SimN.NseriesAnali);
                        SimN.Rcollection.removeSeries(SimN.RseriesAnali);
                        GUI.chartPanel1.repaint();
                        GUI.chartPanel2.repaint();
                    }
                }

            }
        });

        Poisson.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                XYSeries PoissonSeries = new XYSeries("Poisson");
                XYSeries PoissonSeriesAnali = new XYSeries("Poisson Anali");
                XYSeriesCollection PoissonCollection = new XYSeriesCollection();
                PoissonCollection.addSeries(PoissonSeries);
                PoissonCollection.addSeries(PoissonSeriesAnali);
                List<Integer> K = new ArrayList<>();
                List<Double> M = new ArrayList<>();

                for(int i = 1; i < 21; i++){
                    K.add(0);
                    M.add(GUI.userStartNucle*i/100);
                }

                for(int i = 0; i< SimN.P.size(); i++){
                    for(int j = 0; j < 20; j++){
                        if(SimN.P.get(i) < M.get(j)){
                            K.set(j, K.get(j)+1);
                            break;
                        }
                    }

                }

                double lambda = mean(SimN.P);
                System.out.println(lambda);

                for(int i = 0; i< 20; i++){
                    PoissonSeries.add((double)M.get(i), ((double)K.get(i))/(GUI.userTimeHop+1));
                    PoissonSeriesAnali.add((double)M.get(i), Math.pow(lambda, i)*Math.exp(-lambda)/factorial(i));
                }


                JFreeChart Poisson = ChartFactory.createScatterPlot(
                        "Liczba pozostałych nuklidów w czasie",
                        "k",
                        " ",
                        PoissonCollection,
                        PlotOrientation.VERTICAL,
                        true,
                        true,
                        false
                );

                ChartPanel PoissonChartPanel = new ChartPanel(Poisson);
                JFrame chartFrame = new JFrame("Wykres");
                chartFrame.setSize(600, 400);
                chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                chartFrame.add(PoissonChartPanel);
                chartFrame.setLocationRelativeTo(null);
                chartFrame.setVisible(true);
            }
        });


        itemNDataColor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Menu.NDataColor = JColorChooser.showDialog(GUI.getFrames()[0], "Wybierz kolor", Color.WHITE);
                if (SimN.NseriesAnali != null) {
                    SimN.rendererN.setSeriesPaint(0, Menu.NDataColor);
                    SimN.plotN.setRenderer(SimN.rendererN);
                    GUI.chartPanel1.repaint();
                }
            }
        });

        itemRDataColor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                RDataColor = JColorChooser.showDialog(GUI.getFrames()[0], "Wybierz kolor", Color.WHITE);
                if (SimN.NseriesAnali != null) {
                    SimN.rendererR.setSeriesPaint(0, RDataColor);
                    SimN.plotN.setRenderer(SimN.rendererR);
                    GUI.chartPanel2.repaint();
                }
            }
        });

        itemRAnaliDataColor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                NAnaliDataColor = JColorChooser.showDialog(GUI.getFrames()[0], "Wybierz kolor", Color.WHITE);
                if (SimN.NseriesAnali != null) {
                    SimN.rendererN.setSeriesPaint(1, NAnaliDataColor);
                    SimN.plotN.setRenderer(SimN.rendererN);
                    GUI.chartPanel1.repaint();
                }
            }
        });

        itemNAnaliDataColor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                RAnaliDataColor = JColorChooser.showDialog(GUI.getFrames()[0], "Wybierz kolor", Color.WHITE);
                if (SimN.NseriesAnali != null) {
                    SimN.rendererR.setSeriesPaint(1, RAnaliDataColor);
                    SimN.plotN.setRenderer(SimN.rendererR);
                    GUI.chartPanel2.repaint();
                }
            }
        });

    }

    public static double mean(List<Integer> probability) {
        double sum = 0.0;
        for (int i = 0; i < probability.size(); i++) {
            sum += probability.get(i)/(GUI.userStartNucle/100);
        }
        return sum / probability.size();
    }

    public static double factorial(int n) {
        if (n <= 1) return 1;
        return n * factorial(n - 1);
    }
}
