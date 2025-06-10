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
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Menu extends JMenuBar {
    JMenu menu;
    JMenu menuChartCustom;
    JMenu subMenuSaveData;
    JMenu subMenuChooseColor;
    JMenu subMenuLoadData;
    static JMenuItem itemSaveData;
    static JMenuItem itemSaveDataSQL;
    static JMenuItem itemSaveChart;
    static JMenuItem itemOpen;
    static JMenuItem itemLoadDataSQL;
    static JMenuItem itemShowTables;
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

        itemSaveData = new JMenuItem("jako txt");
        subMenuSaveData.add(itemSaveData);

        itemSaveDataSQL = new JMenuItem("do bazy SQL");
        subMenuSaveData.add(itemSaveDataSQL);



        itemSaveChart = new JMenuItem("Zapisz wykresy na dysku");
        menu.add(itemSaveChart);

        subMenuLoadData = new JMenu("Wczytaj dane ...");
        menu.add(subMenuLoadData);


        itemOpen = new JMenuItem("z pliku txt");
        subMenuLoadData.add(itemOpen);

        itemLoadDataSQL = new JMenuItem("z bazy SQL");
        subMenuLoadData.add(itemLoadDataSQL);

        itemShowTables = new JMenuItem("Pokaż dostępne tabele SQL");
        subMenuLoadData.add(itemShowTables);

        itemChartBackgroundGrid.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean selected = itemChartBackgroundGrid.isSelected();
                XYPlot plotActivity = Charts.chartActivity.getXYPlot();
                XYPlot plotParticleNumber = Charts.chartParticleNumber.getXYPlot();

                plotActivity.setDomainGridlinesVisible(selected);
                plotActivity.setRangeGridlinesVisible(selected);
                plotParticleNumber.setDomainGridlinesVisible(selected);
                plotParticleNumber.setRangeGridlinesVisible(selected);
                GUI.histogramsPanel.repaint();

            }
        });

        showAnali.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectedshowAnali = showAnali.isSelected();
                if (selectedshowAnali) {
                    if(Charts.NseriesAnali != null) {
                        Charts.Ncollection.addSeries(Charts.NseriesAnali);
                        Charts.Rcollection.addSeries(Charts.RseriesAnali);
                        Charts.rendererN.setSeriesLinesVisible(1, selectedshowAnali);
                        Charts.rendererN.setSeriesShapesVisible(1, false);
                        Charts.rendererN.setSeriesPaint(1, Color.RED);
                        Charts.rendererR.setSeriesLinesVisible(1, Menu.selectedshowAnali);
                        Charts.rendererR.setSeriesShapesVisible(1, false);
                        Charts.rendererR.setSeriesPaint(1, Color.RED);
                        Charts.plotN.setRenderer(Charts.rendererN);
                        Charts.plotR.setRenderer(Charts.rendererR);


                        GUI.histogramsPanel.repaint();
                    }

                }
                else if(!selectedshowAnali){
                    if(Charts.NseriesAnali != null) {
                        Charts.Ncollection.removeSeries(Charts.NseriesAnali);
                        Charts.Rcollection.removeSeries(Charts.RseriesAnali);

                        GUI.histogramsPanel.repaint();
                    }
                }

            }
        });

        Poisson.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                XYSeries PoissonSeries = new XYSeries("Poisson");
                XYSeries PoissonSeriesAnali = new XYSeries("Poisson Analityczny");
                XYSeriesCollection PoissonCollection = new XYSeriesCollection();
                PoissonCollection.addSeries(PoissonSeries);
                PoissonCollection.addSeries(PoissonSeriesAnali);
                List<Integer> K = new ArrayList<>();
                List<Double> M = new ArrayList<>();

                for(int i = 1; i < 21; i++){
                    K.add(0);
                    M.add(GUI.userStartNucle*i/100);
                }

                for(int i = 0; i< Charts.P.size(); i++){
                    for(int j = 0; j < 20; j++){
                        if(Charts.P.get(i) < M.get(j)){
                            K.set(j, K.get(j)+1);
                            break;
                        }
                    }

                }

                double lambda = mean(Charts.P);

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
                if (Charts.NseriesAnali != null) {
                    Charts.rendererN.setSeriesPaint(0, Menu.NDataColor);
                    Charts.plotN.setRenderer(Charts.rendererN);
                    GUI.histogramsPanel.repaint();
                }
            }
        });

        itemRDataColor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                RDataColor = JColorChooser.showDialog(GUI.getFrames()[0], "Wybierz kolor", Color.WHITE);
                if (Charts.NseriesAnali != null) {
                    Charts.rendererR.setSeriesPaint(0, RDataColor);
                    Charts.plotR.setRenderer(Charts.rendererR);
                    GUI.histogramsPanel.repaint();
                }
            }
        });

        itemNAnaliDataColor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                NAnaliDataColor = JColorChooser.showDialog(GUI.getFrames()[0], "Wybierz kolor", Color.WHITE);
                if (Charts.NseriesAnali != null) {
                    Charts.rendererN.setSeriesPaint(1, NAnaliDataColor);
                    Charts.plotN.setRenderer(Charts.rendererN);
                    GUI.histogramsPanel.repaint();
                }
            }
        });

        itemRAnaliDataColor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                RAnaliDataColor = JColorChooser.showDialog(GUI.getFrames()[0], "Wybierz kolor", Color.WHITE);
                if (Charts.NseriesAnali != null) {
                    Charts.rendererR.setSeriesPaint(1, RAnaliDataColor);
                    Charts.plotR.setRenderer(Charts.rendererR);
                    GUI.histogramsPanel.repaint();
                }
            }
        });

        Menu.itemShowTables.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StringBuilder tablesList = new StringBuilder();
                new Thread(() -> {
                    try (Connection conn = DriverManager.getConnection(
                            "jdbc:mysql://sql7.freesqldatabase.com/sql7783889",
                            "sql7783889",
                            "Yss3mGrEhv");
                         Statement stmt = conn.createStatement()) {

                        String sql = "SHOW TABLES";
                        ResultSet rs = stmt.executeQuery(sql);

                        while (rs.next()) {
                            String tableName = rs.getString(1);
                            tablesList.append(tableName).append("\n");
                        }

                        JTextArea textArea = new JTextArea(tablesList.toString());
                        textArea.setEditable(false);
                        textArea.setLineWrap(true);
                        textArea.setWrapStyleWord(true);

                        JScrollPane scrollPane = new JScrollPane(textArea);
                        scrollPane.setPreferredSize(new Dimension(150, 150));

                        rs.close();

                        if (tablesList.length() == 0) {
                            JOptionPane.showMessageDialog(null, "Brak tabel w bazie danych.");
                        } else {
                            JOptionPane.showMessageDialog(null, scrollPane, "Lista dostępnych baz danych", JOptionPane.INFORMATION_MESSAGE);
                        }

                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Błąd przy pobieraniu listy tabel.");
                    }
                }).start();
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
