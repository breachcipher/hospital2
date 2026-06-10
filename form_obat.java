package tampilan;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import koneksi.koneksi;

public class form_obat extends JFrame {
    private Connection conn;
    private JTextField txtId, txtNama, txtHarga;
    private JButton btnSimpan, btnUpdate, btnHapus, btnClear;
    private JTable table;
    private DefaultTableModel model;
    
    public form_obat() {
        conn = new koneksi().Connect();
        setTitle("Data Obat");
        setSize(700, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
        loadData();
    }
    
    private void initComponents() {
        JPanel panelInput = new JPanel(new GridLayout(3, 2, 5, 5));
        panelInput.setBorder(BorderFactory.createTitledBorder("Input Data Obat"));
        
        panelInput.add(new JLabel("ID Obat:"));
        txtId = new JTextField();
        panelInput.add(txtId);
        
        panelInput.add(new JLabel("Nama Obat:"));
        txtNama = new JTextField();
        panelInput.add(txtNama);
        
        panelInput.add(new JLabel("Harga:"));
        txtHarga = new JTextField();
        panelInput.add(txtHarga);
        
        JPanel panelTombol = new JPanel(new FlowLayout());
        btnSimpan = new JButton("Simpan");
        btnUpdate = new JButton("Update");
        btnHapus = new JButton("Hapus");
        btnClear = new JButton("Clear");
        
        panelTombol.add(btnSimpan);
        panelTombol.add(btnUpdate);
        panelTombol.add(btnHapus);
        panelTombol.add(btnClear);
        
        model = new DefaultTableModel(new String[]{"ID Obat", "Nama Obat", "Harga"}, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        
        setLayout(new BorderLayout(10, 10));
        add(panelInput, BorderLayout.NORTH);
        add(panelTombol, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);
        
        btnSimpan.addActionListener(e -> simpan());
        btnUpdate.addActionListener(e -> update());
        btnHapus.addActionListener(e -> hapus());
        btnClear.addActionListener(e -> clearForm());
        
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtId.setText(model.getValueAt(row, 0).toString());
                    txtNama.setText(model.getValueAt(row, 1).toString());
                    txtHarga.setText(model.getValueAt(row, 2).toString());
                }
            }
        });
    }
    
    private void loadData() {
        model.setRowCount(0);
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM obat");
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("id_obat"),
                    rs.getString("nama_obat"),
                    rs.getString("harga")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    private void simpan() {
        try {
            String sql = "INSERT INTO obat VALUES (?,?,?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, txtId.getText());
            pst.setString(2, txtNama.getText());
            pst.setDouble(3, Double.parseDouble(txtHarga.getText()));
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data tersimpan");
            loadData();
            clearForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    private void update() {
        try {
            String sql = "UPDATE obat SET nama_obat=?, harga=? WHERE id_obat=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, txtNama.getText());
            pst.setDouble(2, Double.parseDouble(txtHarga.getText()));
            pst.setString(3, txtId.getText());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data terupdate");
            loadData();
            clearForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    private void hapus() {
        int confirm = JOptionPane.showConfirmDialog(this, "Yakin hapus?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String sql = "DELETE FROM obat WHERE id_obat=?";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, txtId.getText());
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Data terhapus");
                loadData();
                clearForm();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
    
    private void clearForm() {
        txtId.setText("");
        txtNama.setText("");
        txtHarga.setText("");
        txtId.requestFocus();
    }
}
