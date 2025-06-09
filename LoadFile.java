package Main;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class LoadFile {
       public LoadFile() {
           Menu.itemOpen.addActionListener(new ActionListener() {

               @Override
               public void actionPerformed(ActionEvent e) {
                   JFileChooser chooser = new JFileChooser();
                   JOptionPane.showMessageDialog(null, "wybrano plik1");
                   int wynik = chooser.showOpenDialog(null);
                   JOptionPane.showMessageDialog(null, "wybrano plik2");
                   if (wynik == JFileChooser.APPROVE_OPTION) {
                       File fileToLoad = chooser.getSelectedFile();
                       JOptionPane.showMessageDialog(null, "wzieto plik w ifie");

                       // Wyczyść obecne dane
                       SimN.T.clear();
                       SimN.N.clear();
                       SimN.R.clear();
                       JOptionPane.showMessageDialog(null, "przeczyszczono dane 1");

                       if(SimN.Nseries != null)
                       SimN.Nseries.clear();
                       if(SimN.Rseries != null)
                       SimN.Rseries.clear();
                       JOptionPane.showMessageDialog(null, "przeczyszczono dane");

                       try (BufferedReader reader = new BufferedReader(new FileReader(fileToLoad))) {
                           String line;
                           int lineNumber = 0;

                           while ((line = reader.readLine()) != null) {
                               lineNumber++;

                               // Pomijamy nagłówki
                               if (lineNumber <= 2 || line.trim().isEmpty()) continue;

                               JOptionPane.showMessageDialog(null, "pomieto naglowki");

                               // Rozdziel kolumny – wielokrotne spacje/taby
                               String[] parts = line.trim().split("\\s+");
                               JOptionPane.showMessageDialog(null, "rozdzielono kolumny");
                               if (parts.length >= 1) {
                                   try {
                                       double czas = Double.parseDouble(parts[0]);
                                       double nuklidy = Double.parseDouble(parts[1]);
                                       double aktywnosc = Double.parseDouble(parts[2]);

                                       SimN.T.add(czas);
                                       SimN.N.add(nuklidy);
                                       SimN.R.add(aktywnosc);
                                   } catch (NumberFormatException ex) {
                                       JOptionPane.showMessageDialog(null, "Błąd parsowania danych w linii " + lineNumber);
                                   }
                               }
                           }

                           JOptionPane.showMessageDialog(null, "Dane zostały wczytane!");
                       } catch (IOException ex) {
                           JOptionPane.showMessageDialog(null, "Błąd podczas wczytywania danych");
                       }
                       for (int i = 0; i < GUI.userTimeHop + 1; i++) {
                           double dt = SimN.T.get(1) - SimN.T.get(0);
                           SimN.T.add(dt*i);
                           SimN.Rseries.add(dt * i / GUI.mapTimediv.get(GUI.userTimeRange), SimN.R.get(i));
                           SimN.Nseries.add(dt * i / GUI.mapTimediv.get(GUI.userTimeRange), SimN.N.get(i));
                       }
                       GUI.chartPanel1.repaint();
                       GUI.chartPanel2.repaint();
                   }
               }
           });
       }
}
