package tampilan;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import koneksi.koneksi;

public class form_periksa extends JFrame {
    private Connection conn;
    private JComboBox<String> cbDaftar;
    private JTextField txtIdPeriksa, txtDiagnosa;
    private JButton btnSimpan, btnHapus, btnClear;
    private JTable table;
    private DefaultTableModel model;
    
    public form_periksa() {
        conn = new koneksi().Connect();
        setTitle("Pemeriksaan Pasien");
        setSize(750, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
        loadData();
        loadCombo();
    }
    
    private void initComponents() {
        JPanel panelInput = new JPanel(new GridLayout(3, 2, 5, 5));
        panelInput.setBorder(BorderFactory.createTitledBorder("Form Pemeriksaan"));
        
        panelInput.add(new JLabel("ID Periksa:"));
        txtIdPeriksa = new JTextField();
        panelInput.add(txtIdPeriksa);
        
        panelInput.add(new JLabel("Pendaftaran:"));
        cbDaftar = new JComboBox<>();
        panelInput.add(cbDaftar);
        
        panelInput.add(new JLabel("Diagnosa:"));
        txtDiagnosa = new JTextField();
        panelInput.add(txtDiagnosa);
        
        JPanel panelTombol = new JPanel(new FlowLayout());
        btnSimpan = new JButton("Simpan");
        btnHapus = new JButton("Hapus");
        btnClear = new JButton("Clear");
        
        panelTombol.add(btnSimpan);
        panelTombol.add(btnHapus);
        panelTombol.add(btnClear);
        
        model = new DefaultTableModel(new String[]{"ID Periksa", "Pasien", "Dokter", "Diagnosa", "Tanggal Periksa"}, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        
        setLayout(new BorderLayout(10, 10));
        add(panelInput, BorderLayout.NORTH);
        add(panelTombol, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);
        
        btnSimpan.addActionListener(e -> simpan());
        btnHapus.addActionListener(e -> hapus());
        btnClear.addActionListener(e -> clearForm());
        
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtIdPeriksa.setText(model.getValueAt(row, 0).toString());
                }
            }
        });
    }
    
    private void loadCombo() {
        try {
            String sql = "SELECT d.id_daftar, p.nama as pasien, dk.nama as dokter " +
                         "FROM daftar_berobat d " +
                         "JOIN pasien p ON d.id_pasien = p.id_pasien " +
                         "JOIN dokter dk ON d.id_dokter = dk.id_dokter " +
                         "LEFT JOIN periksa pr ON d.id_daftar = pr.id_daftar " +
                         "WHERE pr.id_periksa IS NULL";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                cbDaftar.addItem(rs.getString("id_daftar") + " - " + rs.getString("pasien") + " - " + rs.getString("dokter"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    private void loadData() {
        model.setRowCount(0);
        try {
            String sql = "SELECT p.id_periksa, ps.nama as pasien, dk.nama as dokter, p.diagnosa, p.tgl_periksa " +
                         "FROM periksa p " +
                         "JOIN daftar_berobat d ON p.id_daftar = d.id_daftar " +
                         "JOIN pasien ps ON d.id_pasien = ps.id_pasien " +
                         "JOIN dokter dk ON d.id_dokter = dk.id_dokter";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("id_periksa"),
                    rs.getString("pasien"),
                    rs.getString("dokter"),
                    rs.getString("diagnosa"),
                    rs.getString("tgl_periksa")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    private void simpan() {
        try {
            String idDaftar = cbDaftar.getSelectedItem().toString().split(" - ")[0];
            
            String sql = "INSERT INTO periksa (id_periksa, id_daftar, diagnosa, tgl_periksa) VALUES (?,?,?, CURDATE())";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, txtIdPeriksa.getText());
            pst.setString(2, idDaftar);
            pst.setString(3, txtDiagnosa.getText());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data pemeriksaan tersimpan");
            loadData();
            loadCombo();
            clearForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    private void hapus() {
        int confirm = JOptionPane.showConfirmDialog(this, "Yakin hapus?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String sql = "DELETE FROM periksa WHERE id_periksa=?";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, txtIdPeriksa.getText());
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Data terhapus");
                loadData();
                loadCombo();
                clearForm();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
    
    private void clearForm() {
        txtIdPeriksa.setText("");
        txtDiagnosa.setText("");
        cbDaftar.setSelectedIndex(0);
        txtIdPeriksa.requestFocus();
    }
}
