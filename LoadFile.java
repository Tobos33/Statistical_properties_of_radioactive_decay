package Main;
import javax.swing.*;
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

                   int wynik = chooser.showOpenDialog(null);

                   if (wynik == JFileChooser.APPROVE_OPTION) {
                       File fileToLoad = chooser.getSelectedFile();


                       Charts.T.clear();
                       Charts.N.clear();
                       Charts.R.clear();


                       if(Charts.Nseries != null) Charts.Nseries.clear();
                       if(Charts.Rseries != null) Charts.Rseries.clear();



                       try (BufferedReader reader = new BufferedReader(new FileReader(fileToLoad))) {
                           String line;
                           int lineNumber = 0;
                           GUI.comboNucleChoser.setSelectedItem(reader.readLine());
                           GUI.textNumber.setText(reader.readLine());
                           GUI.comboTimeChoser.setSelectedItem(reader.readLine());
                           GUI.sliderTimeHop.setValue(Integer.parseInt(reader.readLine()));
                           GUI.sliderStartNucle.setValue(Integer.parseInt(reader.readLine()));
                           while ((line = reader.readLine()) != null) {
                               lineNumber++;

                               if (lineNumber <= 2 || line.trim().isEmpty()) continue;




                               String[] parts = line.trim().split("\\s+");

                               if (parts.length >= 2) {
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
                       if (Charts.R.size() < GUI.userTimeHop || Charts.N.size() < GUI.userTimeHop) {
                           System.err.println("Za mało danych: R.size() = " + Charts.R.size() +"N.size(): " + Charts.N.size()+ ", userTimeHop = " + GUI.userTimeHop);
                       }
                       for (int i = 0; i < GUI.sliderTimeHop.getValue(); i++) {
                           double dt = GUI.mapTimediv.get(GUI.userTimeRange)*Double.parseDouble(GUI.textNumber.getText())/GUI.userTimeHop;
                           Charts.T.add(dt*i);
                           Charts.Rseries.add(dt * i / GUI.mapTimediv.get(GUI.userTimeRange), Charts.R.get(i));
                           Charts.Nseries.add(dt * i / GUI.mapTimediv.get(GUI.userTimeRange), Charts.N.get(i));
                       }
                       SimN.AnalyticalValues();
                       GUI.histogramsPanel.repaint();
                   }
               }
           });

           Menu.itemLoadDataSQL.addActionListener(new ActionListener() {

               @Override
               public void actionPerformed(ActionEvent e) {
                   if(Charts.Nseries != null) Charts.Nseries.clear();
                   if(Charts.Rseries != null) Charts.Rseries.clear();
                   Charts.T.clear();
                   Charts.N.clear();
                   Charts.R.clear();
                   new Thread(() -> {
                       try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                            Statement stmt = conn.createStatement()) {


                           String tableName = JOptionPane.showInputDialog("Podaj nazwę tabeli, z której chcesz pobrać dane:");

                           if (tableName == null || tableName.trim().isEmpty()) {
                               JOptionPane.showMessageDialog(null, "Nie podano nazwy tabeli.");
                               return;
                           }


                           if (Charts.Nseries != null) Charts.Nseries.clear();
                           if (Charts.Rseries != null) Charts.Rseries.clear();

                           String sql1 = "SELECT nuclid, Text, dt, Iterations, N0 From `" + tableName + "`";
                           ResultSet rs1 = stmt.executeQuery(sql1);

                           if (rs1.next()) {
                               GUI.comboNucleChoser.setSelectedItem(rs1.getString("nuclid"));
                               GUI.textNumber.setText(rs1.getString("Text"));
                               GUI.comboTimeChoser.setSelectedItem(rs1.getString("dt"));
                               GUI.sliderTimeHop.setValue(rs1.getInt("Iterations"));
                               GUI.sliderStartNucle.setValue(rs1.getInt("N0"));
                           } else {
                               JOptionPane.showMessageDialog(null, "Brak danych w tabeli: " + tableName);
                           }
                           rs1.close();


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

                       for (int i = 0; i < GUI.userTimeHop && i < Charts.T.size() - 1; i++) {
                           double dt = GUI.mapTimediv.get(GUI.userTimeRange) * Double.parseDouble(GUI.textNumber.getText()) / GUI.userTimeHop;
                           Charts.Rseries.add(dt * i / GUI.mapTimediv.get(GUI.userTimeRange), Charts.R.get(i + 1));
                           Charts.Nseries.add(dt * i / GUI.mapTimediv.get(GUI.userTimeRange), Charts.N.get(i + 1));
                       }
                       SimN.AnalyticalValues();
                       GUI.histogramsPanel.repaint();
                   }).start();
               }
           });
       }

}
