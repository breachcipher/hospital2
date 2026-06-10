package tampilan;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import koneksi.koneksi;

public class form_pasien extends JFrame {
    private Connection conn;
    private JTextField txtId, txtNama, txtAlamat, txtNoHp;
    private JButton btnSimpan, btnUpdate, btnHapus, btnClear;
    private JTable table;
    private DefaultTableModel model;
    
    public form_pasien() {
        conn = new koneksi().Connect();
        setTitle("Data Pasien");
        setSize(750, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
        loadData();
    }
    
    private void initComponents() {
        JPanel panelInput = new JPanel(new GridLayout(4, 2, 5, 5));
        panelInput.setBorder(BorderFactory.createTitledBorder("Input Data Pasien"));
        
        panelInput.add(new JLabel("ID Pasien:"));
        txtId = new JTextField();
        panelInput.add(txtId);
        
        panelInput.add(new JLabel("Nama Lengkap:"));
        txtNama = new JTextField();
        panelInput.add(txtNama);
        
        panelInput.add(new JLabel("Alamat:"));
        txtAlamat = new JTextField();
        panelInput.add(txtAlamat);
        
        panelInput.add(new JLabel("No Telepon:"));
        txtNoHp = new JTextField();
        panelInput.add(txtNoHp);
        
        JPanel panelTombol = new JPanel(new FlowLayout());
        btnSimpan = new JButton("Simpan");
        btnUpdate = new JButton("Update");
        btnHapus = new JButton("Hapus");
        btnClear = new JButton("Clear");
        
        panelTombol.add(btnSimpan);
        panelTombol.add(btnUpdate);
        panelTombol.add(btnHapus);
        panelTombol.add(btnClear);
        
        model = new DefaultTableModel(new String[]{"ID Pasien", "Nama", "Alamat", "No Telepon"}, 0);
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
                    txtAlamat.setText(model.getValueAt(row, 2).toString());
                    txtNoHp.setText(model.getValueAt(row, 3).toString());
                }
            }
        });
    }
    
    private void loadData() {
        model.setRowCount(0);
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM pasien");
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("id_pasien"),
                    rs.getString("nama"),
                    rs.getString("alamat"),
                    rs.getString("no_hp")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error load data: " + e.getMessage());
        }
    }
    
    private void simpan() {
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "ID Pasien tidak boleh kosong");
            return;
        }
        try {
            String sql = "INSERT INTO pasien VALUES (?,?,?,?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, txtId.getText());
            pst.setString(2, txtNama.getText());
            pst.setString(3, txtAlamat.getText());
            pst.setString(4, txtNoHp.getText());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data tersimpan");
            loadData();
            clearForm();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error simpan: " + e.getMessage());
        }
    }
    
    private void update() {
        try {
            String sql = "UPDATE pasien SET nama=?, alamat=?, no_hp=? WHERE id_pasien=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, txtNama.getText());
            pst.setString(2, txtAlamat.getText());
            pst.setString(3, txtNoHp.getText());
            pst.setString(4, txtId.getText());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data terupdate");
            loadData();
            clearForm();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error update: " + e.getMessage());
        }
    }
    
    private void hapus() {
        int confirm = JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String sql = "DELETE FROM pasien WHERE id_pasien=?";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, txtId.getText());
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Data terhapus");
                loadData();
                clearForm();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error hapus: " + e.getMessage());
            }
        }
    }
    
    private void clearForm() {
        txtId.setText("");
        txtNama.setText("");
        txtAlamat.setText("");
        txtNoHp.setText("");
        txtId.requestFocus();
    }
}
