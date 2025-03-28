package Main;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI extends JFrame {

    JPanel startPanel;
    JPanel optionsPanel;
    JPanel histogramsPanel;
    JPanel timePanel;
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


    public GUI() throws HeadlessException{
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.setTitle("Statistical properties of radioactive decay simulation");

        startPanel = new JPanel();
        optionsPanel = new JPanel();
        histogramsPanel = new JPanel();
        timePanel = new JPanel();

        this.add(startPanel, BorderLayout.SOUTH);
        this.add(optionsPanel, BorderLayout.EAST);
        this.add(histogramsPanel, BorderLayout.WEST);
        optionsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        optionsPanel.setLayout(new GridLayout(6, 1));
        histogramsPanel.setLayout(new GridLayout(2, 1));

        //optionsPanel.add(timePanel);

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
    }
    public static void main(String[] args) {
        GUI Frame = new GUI();
        Frame.setVisible(true);
    }
}
