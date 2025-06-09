package Main;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;


public class Charts extends JPanel {
    static java.util.List<Integer> P = new ArrayList<>();
    static java.util.List<Double> N = new ArrayList<>();
    static java.util.List<Double> R = new ArrayList<>();
    static List<Double> T = new ArrayList<>();
    static JFreeChart chartParticleNumber;
    static JFreeChart chartActivity;
    static XYSeries Nseries;
    static XYSeries Rseries;
    static XYSeries NseriesAnali;
    static XYSeries RseriesAnali;
    //XYSeries Poisson;
    static XYSeriesCollection Rcollection;
    static XYSeriesCollection Ncollection;
    static XYPlot plotN;
    static XYPlot plotR;
    static XYLineAndShapeRenderer rendererN;
    static XYLineAndShapeRenderer rendererR;
    static ChartPanel chartPanel2;
    static ChartPanel chartPanel1;
    public Charts() {




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
                "t ",
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
        this.add(chartPanel1);
        this.add(chartPanel2);

        Nseries = new XYSeries("N(t)");
        Rseries = new XYSeries("R(t)");

        NseriesAnali = new XYSeries("N Analityczne");
        RseriesAnali = new XYSeries("R Analityczne");
        //Poisson = new XYSeries("Poisson");

        Ncollection = new XYSeriesCollection();
        Ncollection.addSeries(Nseries);



        Rcollection = new XYSeriesCollection();
        Rcollection.addSeries(Rseries);
        //Rcollection.addSeries(Poisson);





        chartParticleNumber.getXYPlot().setDataset(Ncollection);
        chartActivity.getXYPlot().setDataset(Rcollection);

        plotN = chartParticleNumber.getXYPlot();
        plotR = chartActivity.getXYPlot();

        rendererN = new XYLineAndShapeRenderer();

        Shape circle = new Ellipse2D.Double(-3, -3, 6, 6);
        rendererN.setSeriesLinesVisible(0, true);
        rendererN.setSeriesShapesVisible(0, true);
        rendererN.setSeriesShape(0, circle);
        rendererN.setSeriesPaint(0, Menu.NDataColor);


        rendererR = new XYLineAndShapeRenderer();

        rendererR.setSeriesLinesVisible(0, true);
        rendererR.setSeriesShapesVisible(0, true);
        rendererR.setSeriesPaint(0, Menu.RDataColor);
        rendererR.setSeriesShape(0, circle);
        plotN.setRenderer(rendererN);
        plotR.setRenderer(rendererR);


    }
}
