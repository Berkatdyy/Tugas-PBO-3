package CRUD;

import db.DatabaseConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BarangForm extends JFrame {
    private JTextField tfNama, tfHarga, tfStok;
    private final JTable table;
    private final DefaultTableModel model;

    public BarangForm() {
        setTitle("CRUD Barang");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel Form
        JPanel panelForm = new JPanel(new GridLayout(4, 2));
        panelForm.add(new JLabel("Nama:"));
        tfNama = new JTextField();
        panelForm.add(tfNama);

        panelForm.add(new JLabel("Harga:"));
        tfHarga = new JTextField();
        panelForm.add(tfHarga);

        panelForm.add(new JLabel("Stok:"));
        tfStok = new JTextField();
        panelForm.add(tfStok);

        JButton btnSave = new JButton("Save");
        panelForm.add(btnSave);
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveBarang();
            }
        });

        JButton btnUpdate = new JButton("Update");
        panelForm.add(btnUpdate);
        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateBarang();
            }
        });

        add(panelForm, BorderLayout.NORTH);

        // Table
        model = new DefaultTableModel(new String[]{"ID", "Nama", "Harga", "Stok"}, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton btnDelete = new JButton("Delete");
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteBarang();
            }
        });
        add(btnDelete, BorderLayout.SOUTH);

        loadBarang();
    }

    private void saveBarang() {
        String nama = tfNama.getText();
        double harga = Double.parseDouble(tfHarga.getText());
        int stok = Integer.parseInt(tfStok.getText());

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO barang (nama, harga, stok) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nama);
            stmt.setDouble(2, harga);
            stmt.setInt(3, stok);
            stmt.executeUpdate();
            loadBarang();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateBarang() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih barang yang ingin diupdate");
            return;
        }

        int id = (int) model.getValueAt(selectedRow, 0);
        String nama = tfNama.getText();
        double harga = Double.parseDouble(tfHarga.getText());
        int stok = Integer.parseInt(tfStok.getText());

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE barang SET nama = ?, harga = ?, stok = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nama);
            stmt.setDouble(2, harga);
            stmt.setInt(3, stok);
            stmt.setInt(4, id);
            stmt.executeUpdate();
            loadBarang();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteBarang() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih barang yang ingin dihapus");
            return;
        }

        int id = (int) model.getValueAt(selectedRow, 0);

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "DELETE FROM barang WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.executeUpdate();
            loadBarang();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadBarang() {
        model.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM barang";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String nama = rs.getString("nama");
                double harga = rs.getDouble("harga");
                int stok = rs.getInt("stok");
                model.addRow(new Object[]{id, nama, harga, stok});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new BarangForm().setVisible(true);
        });
    }
}
