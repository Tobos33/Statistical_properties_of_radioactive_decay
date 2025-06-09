package Main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.*;

public class save {

    private static final String DB_URL = "jdbc:mysql://sql7.freesqldatabase.com/sql7783889";
    private static final String DB_USER = "sql7783889";
    private static final String DB_PASSWORD = "Yss3mGrEhv";
    public save(){
        Menu.itemSaveChart.addActionListener(new ActionListener() {
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

        Menu.itemSaveData.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

               StringBuilder savetext = new StringBuilder();
               savetext.append(GUI.comboNucleChoser.getSelectedItem().toString() + "\n");
               savetext.append(GUI.textNumber.getText().toString() + "\n");
               savetext.append(GUI.comboTimeChoser.getSelectedItem().toString() + "\n");
               savetext.append(GUI.sliderTimeHop.getValue() + "\n");
               savetext.append(GUI.sliderStartNucle.getValue() + "\n");
               savetext.append(String.format("%-12s %-20s %-15s\n", "Czas[" + GUI.comboTimeChoser.getSelectedItem()+"]", "Liczba nuklidów", "Aktywność [nBq]"));
               savetext.append("--------------------------------------------------------\n");

                for (int i = 0; i < Charts.T.size(); i++) {
                    Object czas = Charts.T.get(i);
                    Object nuklidy = Charts.N.get(i);
                    Object aktywnosc = Charts.R.get(i);
                    savetext.append(String.format("%-12s %-20s %-15s\n", czas.toString(), nuklidy.toString(), aktywnosc.toString()));
                }

                JFileChooser chooser = new JFileChooser();
                int wynik = chooser.showSaveDialog(null);
                if (wynik == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = chooser.getSelectedFile();

                    if (!fileToSave.getName().toLowerCase().endsWith(".txt")) {
                        fileToSave = new File(fileToSave.getAbsolutePath() + ".txt");
                    }


                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
                        writer.write(savetext.toString());
                        JOptionPane.showMessageDialog(null, "Dane zostały zapisane!");
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "Błąd podczas zapisu danych");
                    }
                }
            }
        });

        Menu.itemSaveDataSQL.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                new Thread(() -> {
                    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {

                        String tableName = createTable(conn);

                        String sql1 = "INSERT INTO " + tableName + " (nuclid, Text, dt, Iterations, N0) VALUES (?, ?, ?, ?, ?)";
                        PreparedStatement ps = conn.prepareStatement(sql1);

                        ps.setString(1, GUI.comboNucleChoser.getSelectedItem().toString());
                        ps.setString(2, GUI.textNumber.getText().toString());
                        ps.setString(3, GUI.comboTimeChoser.getSelectedItem().toString());
                        ps.setInt(4, GUI.sliderTimeHop.getValue());
                        ps.setInt(5, GUI.sliderStartNucle.getValue());

                        ps.executeUpdate();
                        ps.close();

                        String sql2 = "INSERT INTO " + tableName + " (T, N, R) VALUES (?, ?, ?)";


                        PreparedStatement stmt = conn.prepareStatement(sql2);

                        for (int i = 0; i < Charts.T.size(); i++) {
                            double czas = Charts.T.get(i);
                            double nuklidy = Charts.N.get(i);
                            double aktywnosc = Charts.R.get(i);

                            stmt.setDouble(1, czas);
                            stmt.setDouble(2, nuklidy);
                            stmt.setDouble(3, aktywnosc);

                            stmt.addBatch();


                        }
                        stmt.executeBatch(); // Wysłanie danych
                        JOptionPane.showMessageDialog(null, "Dane zostały zapisane do bazy MySQL!");
                        if (conn != null) {
                            conn.close();
                        }
                        if (stmt != null) stmt.close();


                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Błąd podczas zapisu do bazy MySQL.");
                    }
                }).start();
            }
        });


    }

    private String createTable(Connection conn) throws SQLException {
        String userTableName = JOptionPane.showInputDialog("Podaj nazwę nowej tabeli:");

        if (userTableName != null && userTableName.matches("[a-zA-Z0-9_]+")) {
            String sql = "CREATE TABLE `" + userTableName + "` (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "T DOUBLE," +
                    "N DOUBLE," +
                    "R DOUBLE," +
                    "nuclid Varchar(20)," +
                    "Text Varchar(20)," +
                    "dt Varchar(20)," +
                    "Iterations INT," +
                    "N0 INT" +
                    ")";

            try (PreparedStatement prep = conn.prepareStatement(sql)) {
                prep.executeUpdate();

            } catch (SQLException ex) {
                if (ex.getMessage().contains("already exists")) {
                    JOptionPane.showMessageDialog(null, "Tabela '" + userTableName + "' już istnieje.");
                    try {
                        throw new SQLException("Tabela już istnieje", "42S01");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
                else {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Błąd podczas tworzenia tabeli!");
                }

            }
        }
        else {
            JOptionPane.showMessageDialog(null, "Nieprawidłowa nazwa tabeli!");
        }
        return userTableName;
    }

}
