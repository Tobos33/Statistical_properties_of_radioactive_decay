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

import static Main.Charts.*;
import static Main.GUI.sliderStartNucle;
import java.awt.geom.Ellipse2D;

public class SimN implements Runnable {
    //Double R0 = GUI.userLambdaConst* Math.pow(10,sliderStartNucle.getValue());
    static Double Np = Math.pow(10, sliderStartNucle.getValue());
    static Double Rp;
    static double dt;

    public SimN() {

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

        if(Charts.Rseries != null)
        Charts.Rseries.clear();
        if(Charts.Nseries != null)
        Charts.Nseries.clear();


        CalculationN();

        for (int i = 0; i < GUI.userTimeHop + 1; i++) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                break;
            }

            T.add(dt*i);
            Charts.Rseries.add(dt * i / GUI.mapTimediv.get(GUI.userTimeRange), R.get(i));
            Charts.Nseries.add(dt * i / GUI.mapTimediv.get(GUI.userTimeRange), N.get(i));
        }

    }




    public static void AnalyticalValues() {
        Charts.NseriesAnali.clear();
        Charts.RseriesAnali.clear();
        if(Double.parseDouble(GUI.textNumber.getText())<10){
            int T = (int) (Double.parseDouble(GUI.textNumber.getText())*1000);
            int N0 = (int) Math.pow(10, sliderStartNucle.getValue());
            double lambda = GUI.userLambdaConst*Math.pow(10, -9);
            double ddt = GUI.mapTimediv.get(GUI.userTimeRange)/1000;

            for (int i = 0; i < T+1; i++) {
                Charts.NseriesAnali.add(((double)i)/1000, N0*Math.exp(-lambda*i*ddt));
                Charts.RseriesAnali.add(((double)i)/1000, N0*lambda*Math.pow(10, 9)*Math.exp(-lambda*i*ddt));

            }
        }
        else if(Double.parseDouble(GUI.textNumber.getText())<100){
            int T = (int) (Double.parseDouble(GUI.textNumber.getText())*100);
            int N0 = (int) Math.pow(10, sliderStartNucle.getValue());
            double lambda = GUI.userLambdaConst*Math.pow(10, -9);
            double ddt = GUI.mapTimediv.get(GUI.userTimeRange)/100;

            for (int i = 0; i < T+1; i++) {
                Charts.NseriesAnali.add(((double)i)/100, N0*Math.exp(-lambda*i*ddt));
                Charts.RseriesAnali.add(((double)i)/100, N0*lambda*Math.pow(10, 9)*Math.exp(-lambda*i*ddt));

            }
        }
        else{
            int T = (int) (Double.parseDouble(GUI.textNumber.getText()));
            int N0 = (int) Math.pow(10, sliderStartNucle.getValue());
            double lambda = GUI.userLambdaConst*Math.pow(10, -9);


            double ddt = GUI.mapTimediv.get(GUI.userTimeRange);
            for (int i = 0; i < T+1; i++) {
                Charts.NseriesAnali.add(i, N0*Math.exp(-lambda*i*ddt));
                Charts.RseriesAnali.add(i, N0*lambda*Math.pow(10, 9)*Math.exp(-lambda*i*ddt));

            }
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

