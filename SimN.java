package Main;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static Main.GUI.sliderStartNucle;
import static java.lang.Math.abs;

public class SimN implements Runnable {
    //Double R0 = GUI.userLambdaConst* Math.pow(10,sliderStartNucle.getValue());
    Double Np = Math.pow(10, sliderStartNucle.getValue());
    Double Rp;
    static JFreeChart chartParticleNumber;
    static JFreeChart chartActivity;
    XYSeries Nseries;
    XYSeries Rseries;
    XYSeriesCollection Rcollection;
    XYSeriesCollection Ncollection;

    double dt;
    static List<Double> N = new ArrayList<>();
    static List<Double> R = new ArrayList<>();
    static List<Double> T = new ArrayList<>();


    public SimN() {

        Nseries = new XYSeries("N(t)");
        Rseries = new XYSeries("R(t)");
        Ncollection = new XYSeriesCollection(Nseries);
        Rcollection = new XYSeriesCollection(Rseries);

        chartParticleNumber.getXYPlot().setDataset(Ncollection);
        chartActivity.getXYPlot().setDataset(Rcollection);

    }


    public void run() {

        try {
            dt = GUI.mapTimediv.get(GUI.userTimeRange) * Double.parseDouble(GUI.textNumber.getText()) / GUI.userTimeHop;
        } catch (NumberFormatException exception) {
            GUI.textNumber.setText("Wrong number format.");
        }

        N.clear();
        R.clear();
        T.clear();

        Rseries = new XYSeries("R(t)");
        Nseries = new XYSeries("N(t)");
        Rcollection.removeAllSeries();
        Rcollection.addSeries(Rseries);
        Ncollection.removeAllSeries();
        Ncollection.addSeries(Nseries);
        CalculationN();

        for (int i = 0; i < GUI.userTimeHop + 1; i++) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                break;
            }
            T.add(dt*i);
            Rseries.add(dt * i / GUI.mapTimediv.get(GUI.userTimeRange), R.get(i));
            Nseries.add(dt * i / GUI.mapTimediv.get(GUI.userTimeRange), N.get(i));
        }

    }


    public void CalculationN() {

        dt = GUI.mapTimediv.get(GUI.userTimeRange)*Double.parseDouble(GUI.textNumber.getText())/GUI.userTimeHop;
        //R0 = GUI.userLambdaConst* Math.pow(10, sliderStartNucle.getValue())*Math.pow(10, -9);
        R.add(0.0);
        N.add(Math.pow(10, sliderStartNucle.getValue()));
        double los;

        for (int j = 0; j < GUI.userTimeHop; j++) {
            int k = 0;
            int l;
            for (int i = 0; i < Np.intValue(); i++) {
                los = ThreadLocalRandom.current().nextDouble(0, 1000000000);
                if(GUI.userLambdaConst*dt >= los)
                    k += 1;
            }
            if(Np >0) {
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