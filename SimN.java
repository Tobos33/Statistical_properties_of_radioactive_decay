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
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static Main.GUI.sliderStartNucle;
import static java.lang.Math.abs;
import java.awt.geom.Ellipse2D;

public class SimN implements Runnable {
    //Double R0 = GUI.userLambdaConst* Math.pow(10,sliderStartNucle.getValue());
    Double Np = Math.pow(10, sliderStartNucle.getValue());
    Double Rp;
    static JFreeChart chartParticleNumber;
    static JFreeChart chartActivity;
    XYSeries Nseries;
    XYSeries Rseries;
    XYSeries NseriesPoints;
    XYSeries RseriesPoints;
    XYSeries NseriesAnali;
    XYSeries RseriesAnali;
    //XYSeries Poisson;
    XYSeriesCollection Rcollection;
    XYSeriesCollection Ncollection;
    static XYPlot plotN;
    static XYPlot plotR;
    static XYLineAndShapeRenderer rendererN;
    static XYLineAndShapeRenderer rendererR;



    double dt;
    static List<Integer> P = new ArrayList<>();
    static List<Double> N = new ArrayList<>();
    static List<Double> R = new ArrayList<>();
    static List<Double> T = new ArrayList<>();


    public SimN() {

        Nseries = new XYSeries("N(t)");
        Rseries = new XYSeries("R(t)");
        NseriesPoints = new XYSeries("");
        RseriesPoints = new XYSeries("");
        NseriesAnali = new XYSeries("N Anali");
        RseriesAnali = new XYSeries("R Anali");
        //Poisson = new XYSeries("Poisson");

        Ncollection = new XYSeriesCollection();
        Ncollection.addSeries(Nseries);
        Ncollection.addSeries(NseriesPoints);
        Ncollection.addSeries(NseriesAnali);


        Rcollection = new XYSeriesCollection();
        Rcollection.addSeries(Rseries);
        Rcollection.addSeries(RseriesPoints);
        Rcollection.addSeries(RseriesAnali);
        //Rcollection.addSeries(Poisson);





        chartParticleNumber.getXYPlot().setDataset(Ncollection);
        chartActivity.getXYPlot().setDataset(Rcollection);

        plotN = chartParticleNumber.getXYPlot();
        plotR = chartActivity.getXYPlot();

        rendererN = new XYLineAndShapeRenderer();

        rendererN.setSeriesLinesVisible(0, true);
        rendererN.setSeriesShapesVisible(0, false);
        rendererN.setSeriesPaint(0, Color.BLUE);

        // Seria 1 - punkty (z samymi kształtami)
        Shape circle = new Ellipse2D.Double(-3, -3, 6, 6);
        rendererN.setSeriesLinesVisible(1, false);
        rendererN.setSeriesShapesVisible(1, true);
        rendererN.setSeriesPaint(1, Color.BLUE);
        rendererN.setSeriesShape(1, circle);

        rendererN.setSeriesLinesVisible(2, Menu.selectedshowAnali);
        rendererN.setSeriesShapesVisible(2, false);
        rendererN.setSeriesPaint(2, Color.RED);


        rendererR = new XYLineAndShapeRenderer();

        rendererR.setSeriesLinesVisible(0, true);
        rendererR.setSeriesShapesVisible(0, false);
        rendererR.setSeriesPaint(0, Color.BLUE);

        // Seria 1 - punkty (z samymi kształtami)
        rendererR.setSeriesLinesVisible(1, false);
        rendererR.setSeriesShapesVisible(1, true);
        rendererR.setSeriesPaint(1, Color.BLUE);
        rendererR.setSeriesShape(1, circle);

        rendererR.setSeriesLinesVisible(2, Menu.selectedshowAnali);
        rendererR.setSeriesShapesVisible(2, false);
        rendererR.setSeriesPaint(2, Color.RED);

        //rendererR.setSeriesShapesVisible(3, false);
        //rendererR.setSeriesShapesVisible(3, true);
        //rendererR.setSeriesPaint(3, Color.GREEN);
        //rendererR.setSeriesShape(3, circle);






        plotN.setRenderer(rendererN);
        plotR.setRenderer(rendererR);

    }


    public void run() {

        try {
            dt = GUI.mapTimediv.get(GUI.userTimeRange) * Double.parseDouble(GUI.textNumber.getText()) / GUI.userTimeHop;
        } catch (NumberFormatException exception) {
            GUI.textNumber.setText("Wrong number format.");
        }

        int N0 = (int) GUI.userStartNucle;
        double lambda = GUI.userLambdaConst;
        double n;

        N.clear();
        R.clear();
        T.clear();

        Rseries.clear();
        Nseries.clear();
        NseriesPoints.clear();
        RseriesPoints.clear();

        //Rcollection.removeAllSeries();
        //Rcollection.addSeries(Rseries);
        //Rcollection.addSeries(RseriesPoints);
        //Ncollection.removeAllSeries();
        //Ncollection.addSeries(Nseries);
        //Ncollection.addSeries(NseriesPoints);

        CalculationN();
        double parametrLambda = average(R);

        for (int i = 0; i < GUI.userTimeHop + 1; i++) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                break;
            }

            T.add(dt*i);
            Rseries.add(dt * i / GUI.mapTimediv.get(GUI.userTimeRange), R.get(i));
            Nseries.add(dt * i / GUI.mapTimediv.get(GUI.userTimeRange), N.get(i));
            RseriesPoints.add(dt * i / GUI.mapTimediv.get(GUI.userTimeRange), R.get(i));
            NseriesPoints.add(dt * i / GUI.mapTimediv.get(GUI.userTimeRange), N.get(i));
            //Poisson.add(dt*i / GUI.mapTimediv.get(GUI.userTimeRange), Math.pow(parametrLambda, dt*i)*Math.exp(-parametrLambda)/factorial((int)dt*i));
            //NseriesAnali.add(dt * i / GUI.mapTimediv.get(GUI.userTimeRange), N0*Math.exp(-lambda*dt*Math.pow(10, -9)));
            //RseriesAnali.add(dt * i / GUI.mapTimediv.get(GUI.userTimeRange), N0*Math.exp(-lambda*dt*GUI.mapTimediv.get(GUI.userTimeRange)*Math.pow(10, -9)));
        }

    }

    public static double average(List<Double> list) {
        if (list.isEmpty()) return 0; // lub rzucić wyjątek, zależnie od wymagań
        double sum = 0.0;
        for (double num : list) {
            sum += num;
        }
        return sum / list.size();
    }

    public static double factorial(int n) {
        if (n <= 1) return 1;
        return n * factorial(n - 1);
    }



    public void AnalyticalValues() {
        NseriesAnali.clear();
        int T = (int) (Double.parseDouble(GUI.textNumber.getText()));
        int N0 = (int) Math.pow(10, sliderStartNucle.getValue());
        double lambda = GUI.userLambdaConst*Math.pow(10, -9);


        dt = GUI.mapTimediv.get(GUI.userTimeRange) * Double.parseDouble(GUI.textNumber.getText()) / GUI.userTimeHop;
        for (int i = 0; i < T+1; i++) {
            NseriesAnali.add(i, N0*Math.exp(-lambda*i*GUI.mapTimediv.get(GUI.userTimeRange)));
            RseriesAnali.add(i, N0*lambda*Math.pow(10, 9)*Math.exp(-lambda*i*GUI.mapTimediv.get(GUI.userTimeRange)));

        }

    }
    public void CalculationN() {

        dt = GUI.mapTimediv.get(GUI.userTimeRange)*Double.parseDouble(GUI.textNumber.getText())/GUI.userTimeHop;
        //R0 = GUI.userLambdaConst* Math.pow(10, sliderStartNucle.getValue())*Math.pow(10, -9);
        //R.add(0.0);
        N.add(Math.pow(10, sliderStartNucle.getValue()));
        double los;
        P.clear();

        for (int j = 0; j < GUI.userTimeHop+1; j++) {
            int k = 0;
            for (int i = 0; i < Np.intValue(); i++) {
                los = ThreadLocalRandom.current().nextDouble(0, 1000000000);
                if(GUI.userLambdaConst*dt >= los)
                    k += 1;
            }
            if(Np >0) {
                P.add(k);
                Np = Np - k;
                Rp = k/dt*Math.pow(10, 9);
                R.add(Rp);
                N.add(Np);
            }
            else {
                Rp = (double) 0;
                Np = (double) 0;
                R.add(Rp);
                N.add(Np);
            }
        }

    }

}