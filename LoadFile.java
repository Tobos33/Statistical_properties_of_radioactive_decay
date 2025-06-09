package Main;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.sql.*;

public class LoadFile {
    private static final String DB_URL = "jdbc:mysql://sql7.freesqldatabase.com/sql7783889";
    private static final String DB_USER = "sql7783889";
    private static final String DB_PASSWORD = "Yss3mGrEhv";
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
                       Charts.T.clear();
                       Charts.N.clear();
                       Charts.R.clear();
                       JOptionPane.showMessageDialog(null, "przeczyszczono dane 1");

                       if(Charts.Nseries != null) Charts.Nseries.clear();
                       if(Charts.Rseries != null) Charts.Rseries.clear();

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

                                       Charts.T.add(czas);
                                       Charts.N.add(nuklidy);
                                       Charts.R.add(aktywnosc);
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
                           double dt = Charts.T.get(1) - Charts.T.get(0);
                           Charts.T.add(dt*i);
                           Charts.Rseries.add(dt * i / GUI.mapTimediv.get(GUI.userTimeRange), Charts.R.get(i));
                           Charts.Nseries.add(dt * i / GUI.mapTimediv.get(GUI.userTimeRange), Charts.N.get(i));
                       }
                       GUI.histogramsPanel.repaint();
                   }
               }
           });

           Menu.itemLoadDataSQL.addActionListener(new ActionListener() {

               @Override
               public void actionPerformed(ActionEvent e) {
                   try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                        Statement stmt = conn.createStatement()) {


                        String tableName = JOptionPane.showInputDialog("Podaj nazwę tabeli, z której chcesz pobrać dane:");

                        if (tableName == null || tableName.trim().isEmpty()) {
                            JOptionPane.showMessageDialog(null, "Nie podano nazwy tabeli.");
                            return;
                        }

                        // Wyczyść obecne dane
                        Charts.T.clear();
                        Charts.N.clear();
                        Charts.R.clear();

                        if (Charts.Nseries != null) Charts.Nseries.clear();
                        if (Charts.Rseries != null) Charts.Rseries.clear();



                       String sql = "SELECT T, N, R FROM `" + tableName + "`";
                       ResultSet rs = stmt.executeQuery(sql);

                       while (rs.next()) {
                           double czas = rs.getDouble("T");
                           double nuklidy = rs.getDouble("N");
                           double aktywnosc = rs.getDouble("R");

                           Charts.T.add(czas);
                           Charts.N.add(nuklidy);
                           Charts.R.add(aktywnosc);
                       }

                       rs.close();


                   } catch (SQLException ex) {
                       ex.printStackTrace();
                       JOptionPane.showMessageDialog(null, "Błąd podczas pobierania danych z bazy.");
                       return;
                   }

                   // Tworzenie serii do wykresów
                   for (int i = 0; i < GUI.userTimeHop + 1 && i < Charts.T.size(); i++) {
                       double dt = Charts.T.get(1) - Charts.T.get(0);
                       Charts.Rseries.add(dt * i / GUI.mapTimediv.get(GUI.userTimeRange), Charts.R.get(i));
                       Charts.Nseries.add(dt * i / GUI.mapTimediv.get(GUI.userTimeRange), Charts.N.get(i));
                   }

                   GUI.histogramsPanel.repaint();
                   JOptionPane.showMessageDialog(null, "Dane zostały wczytane z bazy danych!");
               }
           });
       }

}
