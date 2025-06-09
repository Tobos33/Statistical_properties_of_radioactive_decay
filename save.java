package Main;

import org.jfree.chart.ChartPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.Statement;
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
               //String savetext = SimN.N.toString() + "\n"+ SimN.R.toString()+"\n"+SimN.T.toString();
               StringBuilder savetext = new StringBuilder();
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


                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)){
                    String tableName = createTable(conn);
                    String sql = "INSERT INTO "+tableName+" (T, N, R) VALUES (?, ?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    for (int i = 0; i < Charts.T.size(); i++) {
                        Object czas = Charts.T.get(i);
                        Object nuklidy = Charts.N.get(i);
                        Object aktywnosc = Charts.R.get(i);

                        stmt.setString(1, czas.toString());
                        stmt.setString(2, nuklidy.toString());
                        stmt.setString(3, aktywnosc.toString());

                        stmt.addBatch();


                    }
                    stmt.executeBatch(); // Wysłanie danych
                    JOptionPane.showMessageDialog(null, "Dane zostały zapisane do bazy MySQL!");
                    if (conn != null) {
                        conn.close();
                    }
                    if (stmt != null) stmt.close();


                }
                catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Błąd podczas zapisu do bazy MySQL.");
                }
            }
        });


    }

    private String createTable(Connection conn) throws SQLException {
        String userTableName = JOptionPane.showInputDialog("Podaj nazwę nowej tabeli:");

        if (userTableName != null && userTableName.matches("[a-zA-Z0-9_]+")) {
            String sql = "CREATE TABLE IF NOT EXISTS `" + userTableName + "` (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "T VARCHAR(50)," +
                    "N VARCHAR(50)," +
                    "R VARCHAR(50)" +
                    ")";

            try (Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
                JOptionPane.showMessageDialog(null, "Tabela '" + userTableName + "' została utworzona!");

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Błąd podczas tworzenia tabeli!");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Nieprawidłowa nazwa tabeli!");
        }
        return userTableName;
    }

}
