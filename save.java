package Main;

import org.jfree.chart.ChartPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;

public class save {
    public save(){
        GUI.itemSaveChart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BufferedImage image = new BufferedImage(GUI.histogramsPanel.getWidth(), GUI.histogramsPanel.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = image.createGraphics();
                GUI.histogramsPanel.printAll(g2d);
                g2d.dispose();

                JFileChooser chooser = new JFileChooser();
                int wynik = chooser.showSaveDialog(null);
                if (wynik == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();

                    if (!file.getName().toLowerCase().endsWith(".png")) {
                        file = new File(file.getAbsolutePath() + ".png");
                    }

                    try {
                        ImageIO.write(image, "png", file);
                        JOptionPane.showMessageDialog(null, "Wykresy zostały zapisane!");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Błąd podczas zapisu wykresów.");
                    }
                }
            }
        });

        GUI.itemSaveData.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               String savetext = SimN.N.toString() + "\n"+ SimN.R.toString()+"\n"+SimN.T.toString();

                JFileChooser chooser = new JFileChooser();
                int wynik = chooser.showSaveDialog(null);
                if (wynik == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = chooser.getSelectedFile();

                    if (!fileToSave.getName().toLowerCase().endsWith(".txt")) {
                        fileToSave = new File(fileToSave.getAbsolutePath() + ".txt");
                    }


                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
                        writer.write(savetext);
                        JOptionPane.showMessageDialog(null, "Dane zostały zapisane!");
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "Błąd podczas zapisu danych");
                    }
                }
            }
        });




    }
}
