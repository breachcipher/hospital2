package tampilan;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import koneksi.koneksi;

public class form_daftar extends JFrame {
    private Connection conn;
    private JComboBox<String> cbPasien, cbDokter;
    private JTextField txtIdDaftar;
    private JButton btnSimpan, btnHapus, btnClear;
    private JTable table;
    private DefaultTableModel model;
    
    public form_daftar() {
        conn = new koneksi().Connect();
        setTitle("Pendaftaran Berobat");
        setSize(750, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
        loadData();
        loadCombo();
    }
    
    private void initComponents() {
        JPanel panelInput = new JPanel(new GridLayout(4, 2, 5, 5));
        panelInput.setBorder(BorderFactory.createTitledBorder("Form Pendaftaran"));
        
        panelInput.add(new JLabel("ID Daftar:"));
        txtIdDaftar = new JTextField();
        panelInput.add(txtIdDaftar);
        
        panelInput.add(new JLabel("Pasien:"));
        cbPasien = new JComboBox<>();
        panelInput.add(cbPasien);
        
        panelInput.add(new JLabel("Dokter:"));
        cbDokter = new JComboBox<>();
        panelInput.add(cbDokter);
        
        JPanel panelTombol = new JPanel(new FlowLayout());
        btnSimpan = new JButton("Simpan");
        btnHapus = new JButton("Hapus");
        btnClear = new JButton("Clear");
        
        panelTombol.add(btnSimpan);
        panelTombol.add(btnHapus);
        panelTombol.add(btnClear);
        
        model = new DefaultTableModel(new String[]{"ID Daftar", "Pasien", "Dokter", "Tanggal Daftar"}, 0);
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
                    txtIdDaftar.setText(model.getValueAt(row, 0).toString());
                }
            }
        });
    }
    
    private void loadCombo() {
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT id_pasien, nama FROM pasien");
            while (rs.next()) {
                cbPasien.addItem(rs.getString("id_pasien") + " - " + rs.getString("nama"));
            }
            
            rs = st.executeQuery("SELECT id_dokter, nama FROM dokter");
            while (rs.next()) {
                cbDokter.addItem(rs.getString("id_dokter") + " - " + rs.getString("nama"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    private void loadData() {
        model.setRowCount(0);
        try {
            String sql = "SELECT d.*, p.nama as nama_pasien, dk.nama as nama_dokter " +
                         "FROM daftar_berobat d " +
                         "JOIN pasien p ON d.id_pasien = p.id_pasien " +
                         "JOIN dokter dk ON d.id_dokter = dk.id_dokter";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("id_daftar"),
                    rs.getString("nama_pasien"),
                    rs.getString("nama_dokter"),
                    rs.getString("tgl_daftar")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    private void simpan() {
        try {
            String idPasien = cbPasien.getSelectedItem().toString().split(" - ")[0];
            String idDokter = cbDokter.getSelectedItem().toString().split(" - ")[0];
            
            String sql = "INSERT INTO daftar_berobat (id_daftar, id_pasien, id_dokter, tgl_daftar) VALUES (?,?,?, CURDATE())";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, txtIdDaftar.getText());
            pst.setString(2, idPasien);
            pst.setString(3, idDokter);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Pendaftaran berhasil");
            loadData();
            clearForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    private void hapus() {
        int confirm = JOptionPane.showConfirmDialog(this, "Yakin hapus pendaftaran?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String sql = "DELETE FROM daftar_berobat WHERE id_daftar=?";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, txtIdDaftar.getText());
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
        txtIdDaftar.setText("");
        cbPasien.setSelectedIndex(0);
        cbDokter.setSelectedIndex(0);
        txtIdDaftar.requestFocus();
    }
}
