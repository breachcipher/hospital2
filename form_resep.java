package tampilan;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import koneksi.koneksi;

public class form_resep extends JFrame {
    private Connection conn;
    private JComboBox<String> cbPeriksa, cbObat;
    private JTextField txtIdResep, txtJumlah;
    private JButton btnSimpan, btnHapus, btnClear;
    private JTable table;
    private DefaultTableModel model;
    
    public form_resep() {
        conn = new koneksi().Connect();
        setTitle("Resep Obat");
        setSize(800, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
        loadData();
        loadCombo();
    }
    
    private void initComponents() {
        JPanel panelInput = new JPanel(new GridLayout(4, 2, 5, 5));
        panelInput.setBorder(BorderFactory.createTitledBorder("Form Resep"));
        
        panelInput.add(new JLabel("ID Resep:"));
        txtIdResep = new JTextField();
        panelInput.add(txtIdResep);
        
        panelInput.add(new JLabel("Pemeriksaan:"));
        cbPeriksa = new JComboBox<>();
        panelInput.add(cbPeriksa);
        
        panelInput.add(new JLabel("Obat:"));
        cbObat = new JComboBox<>();
        panelInput.add(cbObat);
        
        panelInput.add(new JLabel("Jumlah:"));
        txtJumlah = new JTextField();
        panelInput.add(txtJumlah);
        
        JPanel panelTombol = new JPanel(new FlowLayout());
        btnSimpan = new JButton("Simpan");
        btnHapus = new JButton("Hapus");
        btnClear = new JButton("Clear");
        
        panelTombol.add(btnSimpan);
        panelTombol.add(btnHapus);
        panelTombol.add(btnClear);
        
        model = new DefaultTableModel(new String[]{"ID Resep", "Pasien", "Dokter", "Obat", "Jumlah", "Tanggal"}, 0);
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
                    txtIdResep.setText(model.getValueAt(row, 0).toString());
                }
            }
        });
    }
    
    private void loadCombo() {
        try {
            String sqlPeriksa = "SELECT p.id_periksa, ps.nama as pasien, dk.nama as dokter " +
                                "FROM periksa p " +
                                "JOIN daftar_berobat d ON p.id_daftar = d.id_daftar " +
                                "JOIN pasien ps ON d.id_pasien = ps.id_pasien " +
                                "JOIN dokter dk ON d.id_dokter = dk.id_dokter";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sqlPeriksa);
            while (rs.next()) {
                cbPeriksa.addItem(rs.getString("id_periksa") + " - " + rs.getString("pasien") + " - " + rs.getString("dokter"));
            }
            
            rs = st.executeQuery("SELECT id_obat, nama_obat FROM obat");
            while (rs.next()) {
                cbObat.addItem(rs.getString("id_obat") + " - " + rs.getString("nama_obat"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    private void loadData() {
        model.setRowCount(0);
        try {
            String sql = "SELECT r.id_resep, ps.nama as pasien, dk.nama as dokter, o.nama_obat, r.jumlah, p.tgl_periksa " +
                         "FROM resep r " +
                         "JOIN periksa pr ON r.id_periksa = pr.id_periksa " +
                         "JOIN daftar_berobat d ON pr.id_daftar = d.id_daftar " +
                         "JOIN pasien ps ON d.id_pasien = ps.id_pasien " +
                         "JOIN dokter dk ON d.id_dokter = dk.id_dokter " +
                         "JOIN obat o ON r.id_obat = o.id_obat";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("id_resep"),
                    rs.getString("pasien"),
                    rs.getString("dokter"),
                    rs.getString("nama_obat"),
                    rs.getString("jumlah"),
                    rs.getString("tgl_periksa")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    private void simpan() {
        try {
            String idPeriksa = cbPeriksa.getSelectedItem().toString().split(" - ")[0];
            String idObat = cbObat.getSelectedItem().toString().split(" - ")[0];
            
            String sql = "INSERT INTO resep (id_resep, id_periksa, id_obat, jumlah) VALUES (?,?,?,?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, txtIdResep.getText());
            pst.setString(2, idPeriksa);
            pst.setString(3, idObat);
            pst.setInt(4, Integer.parseInt(txtJumlah.getText()));
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Resep tersimpan");
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
                String sql = "DELETE FROM resep WHERE id_resep=?";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, txtIdResep.getText());
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
        txtIdResep.setText("");
        txtJumlah.setText("");
        cbPeriksa.setSelectedIndex(0);
        cbObat.setSelectedIndex(0);
        txtIdResep.requestFocus();
    }
}
