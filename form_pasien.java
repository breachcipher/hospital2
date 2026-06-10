package tampilan;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import koneksi.koneksi;

public class form_pasien extends JPanel {
    private Connection conn;
    private JTextField txtCariNama, txtIdPasien, txtNama, txtAlamat, txtNoTelp;
    private JComboBox<String> cbJk;
    private JButton btnCari, btnReset, btnTambah, btnSimpan, btnBatal;
    private JTable table;
    private DefaultTableModel model;
    private JDialog dialogForm;
    private Color colorPrimary = new Color(46, 125, 50);
    private Color colorSidebar = new Color(27, 94, 32);
    private Color colorSuccess = new Color(67, 160, 71);
    private Color colorDanger = new Color(229, 57, 53);
    
    public form_pasien() {
        conn = new koneksi().Connect();
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        initComponents();
        loadData();
    }
    
    private void initComponents() {
        JPanel filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 230, 201)), 
            "Filter Data Pasien", 
            TitledBorder.LEFT, 
            TitledBorder.TOP, 
            new Font("Segoe UI", Font.BOLD, 12),
            colorPrimary
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        filterPanel.add(new JLabel("Nama Pasien:"), gbc);
        gbc.gridx = 1;
        txtCariNama = new JTextField(15);
        filterPanel.add(txtCariNama, gbc);
        
        gbc.gridx = 2;
        filterPanel.add(new JLabel("Jenis Kelamin:"), gbc);
        gbc.gridx = 3;
        cbJk = new JComboBox<>(new String[]{"Semua", "L", "P"});
        filterPanel.add(cbJk, gbc);
        
        gbc.gridx = 4;
        btnCari = new JButton("🔍 Terapkan Filter");
        btnCari.setBackground(colorPrimary);
        btnCari.setForeground(Color.WHITE);
        btnCari.setFocusPainted(false);
        filterPanel.add(btnCari, gbc);
        
        gbc.gridx = 5;
        btnReset = new JButton("↺ Reset");
        btnReset.setBackground(Color.GRAY);
        btnReset.setForeground(Color.WHITE);
        btnReset.setFocusPainted(false);
        filterPanel.add(btnReset, gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        btnTambah = new JButton("+ Tambah Data Pasien Baru");
        btnTambah.setBackground(colorSuccess);
        btnTambah.setForeground(Color.WHITE);
        btnTambah.setFont(new Font("Segoe UI", Font.BOLD, 12));
        buttonPanel.add(btnTambah);
        
        String[] columns = {"ID Pasien", "Nama Pasien", "Jenis Kelamin", "No. Telp", "Alamat", "Aksi"};
        model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };
        table = new JTable(model);
        table.setRowHeight(35);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(colorSidebar);
        table.getTableHeader().setForeground(Color.WHITE);
        
        table.getColumn("Aksi").setCellRenderer(new ButtonRenderer());
        table.getColumn("Aksi").setCellEditor(new ButtonEditor(new JCheckBox()));
        
        JScrollPane scrollPane = new JScrollPane(table);
        
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footerPanel.setBackground(Color.WHITE);
        JLabel lblTotal = new JLabel("Total Pasien: 0");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTotal.setForeground(colorPrimary);
        footerPanel.add(lblTotal);
        
        add(filterPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);
        
        btnCari.addActionListener(e -> filterData());
        btnReset.addActionListener(e -> resetFilter());
        btnTambah.addActionListener(e -> showFormDialog(null));
        
        model.addTableModelListener(e -> {
            lblTotal.setText("Total Pasien: " + model.getRowCount());
        });
    }
    
    private void loadData() {
        model.setRowCount(0);
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM pasien ORDER BY id_pasien");
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("id_pasien"),
                    rs.getString("nama"),
                    rs.getString("jk"),
                    rs.getString("no_telp"),
                    rs.getString("alamat"),
                    "Edit|Hapus"
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    private void filterData() {
        String nama = txtCariNama.getText().trim();
        String jk = cbJk.getSelectedItem().toString();
        
        model.setRowCount(0);
        try {
            StringBuilder sql = new StringBuilder("SELECT * FROM pasien WHERE 1=1");
            if (!nama.isEmpty()) sql.append(" AND nama LIKE '%").append(nama).append("%'");
            if (!jk.equals("Semua")) sql.append(" AND jk = '").append(jk).append("'");
            sql.append(" ORDER BY id_pasien");
            
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql.toString());
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("id_pasien"),
                    rs.getString("nama"),
                    rs.getString("jk"),
                    rs.getString("no_telp"),
                    rs.getString("alamat"),
                    "Edit|Hapus"
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    private void resetFilter() {
        txtCariNama.setText("");
        cbJk.setSelectedIndex(0);
        loadData();
    }
    
    private void showFormDialog(String idPasien) {
        dialogForm = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), idPasien == null ? "Tambah Pasien" : "Edit Pasien", true);
        dialogForm.setSize(450, 450);
        dialogForm.setLocationRelativeTo(this);
        dialogForm.setLayout(new BorderLayout(10, 10));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("ID Pasien:"), gbc);
        gbc.gridx = 1;
        txtIdPasien = new JTextField(15);
        formPanel.add(txtIdPasien, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Nama Pasien:"), gbc);
        gbc.gridx = 1;
        txtNama = new JTextField(15);
        formPanel.add(txtNama, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Jenis Kelamin:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> cbJkForm = new JComboBox<>(new String[]{"L", "P"});
        formPanel.add(cbJkForm, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("No. Telepon:"), gbc);
        gbc.gridx = 1;
        txtNoTelp = new JTextField(15);
        formPanel.add(txtNoTelp, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Alamat:"), gbc);
        gbc.gridx = 1;
        txtAlamat = new JTextField(15);
        formPanel.add(txtAlamat, gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        btnSimpan = new JButton("💾 Simpan");
        btnSimpan.setBackground(colorSuccess);
        btnSimpan.setForeground(Color.WHITE);
        btnBatal = new JButton("❌ Batal");
        btnBatal.setBackground(Color.GRAY);
        btnBatal.setForeground(Color.WHITE);
        buttonPanel.add(btnSimpan);
        buttonPanel.add(btnBatal);
        
        dialogForm.add(formPanel, BorderLayout.CENTER);
        dialogForm.add(buttonPanel, BorderLayout.SOUTH);
        
        if (idPasien != null) {
            loadDataToForm(idPasien, cbJkForm);
            txtIdPasien.setEnabled(false);
        }
        
        btnSimpan.addActionListener(e -> {
            if (idPasien == null) {
                simpanPasien(cbJkForm);
            } else {
                updatePasien(idPasien, cbJkForm);
            }
        });
        
        btnBatal.addActionListener(e -> dialogForm.dispose());
        
        dialogForm.setVisible(true);
    }
    
    private void loadDataToForm(String idPasien, JComboBox<String> cbJkForm) {
        try {
            String sql = "SELECT * FROM pasien WHERE id_pasien=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, idPasien);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                txtIdPasien.setText(rs.getString("id_pasien"));
                txtNama.setText(rs.getString("nama"));
                cbJkForm.setSelectedItem(rs.getString("jk"));
                txtNoTelp.setText(rs.getString("no_telp"));
                txtAlamat.setText(rs.getString("alamat"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    private void simpanPasien(JComboBox<String> cbJkForm) {
        if (txtIdPasien.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(dialogForm, "ID Pasien tidak boleh kosong");
            return;
        }
        try {
            String sql = "INSERT INTO pasien VALUES (?,?,?,?,?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, txtIdPasien.getText());
            pst.setString(2, txtNama.getText());
            pst.setString(3, cbJkForm.getSelectedItem().toString());
            pst.setString(4, txtNoTelp.getText());
            pst.setString(5, txtAlamat.getText());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(dialogForm, "Data tersimpan");
            dialogForm.dispose();
            loadData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(dialogForm, "Error: " + e.getMessage());
        }
    }
    
    private void updatePasien(String idPasien, JComboBox<String> cbJkForm) {
        try {
            String sql = "UPDATE pasien SET nama=?, jk=?, no_telp=?, alamat=? WHERE id_pasien=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, txtNama.getText());
            pst.setString(2, cbJkForm.getSelectedItem().toString());
            pst.setString(3, txtNoTelp.getText());
            pst.setString(4, txtAlamat.getText());
            pst.setString(5, idPasien);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(dialogForm, "Data terupdate");
            dialogForm.dispose();
            loadData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(dialogForm, "Error: " + e.getMessage());
        }
    }
    
    private void hapusPasien(String idPasien) {
        int confirm = JOptionPane.showConfirmDialog(this, "Yakin hapus pasien " + idPasien + "?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String sql = "DELETE FROM pasien WHERE id_pasien=?";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, idPasien);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Data terhapus");
                loadData();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
    
    class ButtonRenderer extends JPanel implements TableCellRenderer {
        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
            setOpaque(true);
        }
        
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            removeAll();
            JButton btnEdit = new JButton("✏️ Edit");
            JButton btnHapus = new JButton("🗑️ Hapus");
            btnEdit.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            btnHapus.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            btnEdit.setBackground(new Color(102, 187, 106));
            btnHapus.setBackground(colorDanger);
            btnEdit.setForeground(Color.WHITE);
            btnHapus.setForeground(Color.WHITE);
            
            String idPasien = table.getValueAt(row, 0).toString();
            btnEdit.addActionListener(e -> showFormDialog(idPasien));
            btnHapus.addActionListener(e -> hapusPasien(idPasien));
            
            add(btnEdit);
            add(btnHapus);
            
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(table.getBackground());
            }
            return this;
        }
    }
    
    class ButtonEditor extends DefaultCellEditor {
        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
        }
        
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            return new ButtonRenderer().getTableCellRendererComponent(table, value, isSelected, true, row, column);
        }
    }
}
