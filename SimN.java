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

import static java.lang.Math.abs;

public class SimN extends JPanel implements Runnable {
    Double R0 = GUI.userLambdaConst*GUI.userStartNucle;
    Double Np = GUI.userStartNucle;
    Double Rp;
    static JFreeChart chartParticleNumber;
    static JFreeChart chartActivity;
    XYSeries Nseries;
    XYSeries Rseries;
    XYSeriesCollection Rcollection;
    XYSeriesCollection Ncollection;

    double dt;
    List<Double> N = new ArrayList<>();
    List<Double> R = new ArrayList<>();
    ChartPanel chartPanel1;
    ChartPanel chartPanel2;

    public SimN(ChartPanel chartPanel1, ChartPanel chartPanel2) {
        this.chartPanel2 = chartPanel2;
        this.chartPanel1 = chartPanel1;
        Nseries = new XYSeries("N(t)");
        Rseries = new XYSeries("R(t)");
        Ncollection = new XYSeriesCollection(Nseries);
        Rcollection = new XYSeriesCollection(Rseries);

        chartParticleNumber.getXYPlot().setDataset(Ncollection);
        chartActivity.getXYPlot().setDataset(Rcollection);

    }


    public void run() {
        try{
            dt = GUI.mapTimediv.get(GUI.userTimeRange)*Double.parseDouble(GUI.textNumber.getText())/GUI.userTimeHop;
        }
        catch(NumberFormatException exception){
            GUI.textNumber.setText("Wrong number format.");
        }

        Rseries = new XYSeries("R(t)");
        Nseries = new XYSeries("N(t)");
        Rcollection.removeAllSeries();
        Rcollection.addSeries(Rseries);
        Ncollection.removeAllSeries();
        Ncollection.addSeries(Nseries);
        CalculationN();

        for (int i = 0; i < GUI.userTimeHop+1; i++) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Rseries.add(dt * i/GUI.mapTimediv.get(GUI.userTimeRange), R.get(i));
            Nseries.add(dt * i/GUI.mapTimediv.get(GUI.userTimeRange), N.get(i));
            chartPanel1.repaint();
        }
        print();
    }


    public void CalculationN() {

        dt = GUI.mapTimediv.get(GUI.userTimeRange)*Double.parseDouble(GUI.textNumber.getText())/GUI.userTimeHop;
        R0 = GUI.userLambdaConst*GUI.userStartNucle*Math.pow(10, -9);
        R.add(0.0);
        N.add(GUI.userStartNucle);
        double los;

        for (int j = 0; j < GUI.userTimeHop+1; j++) {
            int k = 0;
            int l;
            for (int i = 0; i < Np.intValue(); i++) {
                los = ThreadLocalRandom.current().nextDouble(0, 1000000000);
                if(GUI.userLambdaConst*dt >= los)
                    k += 1;
            }
            if(Np >0) {
                Np = Np - k;
                Rp = k/dt;
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

    public void print() {
        System.out.println(R);
    }

}