package tampilan;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import koneksi.koneksi;

public class PerawatPanel extends JPanel {
    private Connection conn;
    private JTextField txtCariNama;
    private JComboBox<String> cbSpesialis, cbJk;
    private JButton btnCari, btnReset, btnTambah;
    private JTable table;
    private DefaultTableModel model;
    private JDialog dialogForm;
    
    private Color colorPrimary = new Color(46, 125, 50);
    private Color colorSidebar = new Color(27, 94, 32);
    private Color colorSuccess = new Color(67, 160, 71);
    private Color colorDanger = new Color(229, 57, 53);
    private Color colorEdit = new Color(102, 187, 106);
    
    public PerawatPanel() {
        koneksi k = new koneksi();
        conn = k.getConnection();
        
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        initComponents();
        loadData();
    }
    
    private void initComponents() {
        // ========== PANEL FILTER ==========
        JPanel filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 230, 201)), 
            "Filter Data",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12),
            colorPrimary
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Nama Perawat
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblNama = new JLabel("Nama Perawat:");
        lblNama.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        filterPanel.add(lblNama, gbc);
        
        gbc.gridx = 1;
        txtCariNama = new JTextField(15);
        txtCariNama.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        filterPanel.add(txtCariNama, gbc);
        
        // Spesialis
        gbc.gridx = 2;
        JLabel lblSpesialis = new JLabel("Spesialis:");
        lblSpesialis.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        filterPanel.add(lblSpesialis, gbc);
        
        gbc.gridx = 3;
        String[] spesialisList = {"Semua", "Spesialis Anak", "Spesialis Penyakit Dalam", "Spesialis Bedah", "Spesialis Gigi", "Spesialis Kandungan"};
        cbSpesialis = new JComboBox<>(spesialisList);
        cbSpesialis.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        filterPanel.add(cbSpesialis, gbc);
        
        // Jenis Kelamin
        gbc.gridx = 4;
        JLabel lblJk = new JLabel("Jenis Kelamin:");
        lblJk.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        filterPanel.add(lblJk, gbc);
        
        gbc.gridx = 5;
        cbJk = new JComboBox<>(new String[]{"Semua", "L", "P"});
        cbJk.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        filterPanel.add(cbJk, gbc);
        
        // Tombol Terapkan Filter
        gbc.gridx = 6;
        btnCari = new JButton("🔍 Terapkan Filter");
        btnCari.setBackground(colorPrimary);
        btnCari.setForeground(Color.WHITE);
        btnCari.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCari.setFocusPainted(false);
        btnCari.setCursor(new Cursor(Cursor.HAND_CURSOR));
        filterPanel.add(btnCari, gbc);
        
        // Tombol Reset
        gbc.gridx = 7;
        btnReset = new JButton("↺ Reset");
        btnReset.setBackground(Color.GRAY);
        btnReset.setForeground(Color.WHITE);
        btnReset.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnReset.setFocusPainted(false);
        btnReset.setCursor(new Cursor(Cursor.HAND_CURSOR));
        filterPanel.add(btnReset, gbc);
        
        // ========== PANEL TOMBOL TAMBAH ==========
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        btnTambah = new JButton("+ Tambah Data Perawat Baru");
        btnTambah.setBackground(colorSuccess);
        btnTambah.setForeground(Color.WHITE);
        btnTambah.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnTambah.setFocusPainted(false);
        btnTambah.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTambah.setPreferredSize(new Dimension(220, 40));
        buttonPanel.add(btnTambah);
        
        // ========== TABEL ==========
        String[] columns = {"Kd Perawat", "Nama Perawat", "Spesialis", "JK", "No. Telp", "Alamat", "Aksi"};
        model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return column == 6;
            }
        };
        
        table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setSelectionBackground(new Color(220, 240, 220));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(colorSidebar);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(40);
        table.getColumnModel().getColumn(4).setPreferredWidth(120);
        table.getColumnModel().getColumn(5).setPreferredWidth(180);
        table.getColumnModel().getColumn(6).setPreferredWidth(120);
        
        // Renderer untuk kolom aksi
        table.getColumn("Aksi").setCellRenderer(new ButtonRenderer());
        table.getColumn("Aksi").setCellEditor(new ButtonEditor(new JCheckBox()));
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 230, 201)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        // ========== FOOTER TOTAL ==========
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        JLabel lblTotal = new JLabel("Total Perawat: 0");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTotal.setForeground(colorPrimary);
        footerPanel.add(lblTotal);
        
        // ========== LAYOUT UTAMA ==========
        add(filterPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);
        
        // ========== EVENT LISTENERS ==========
        btnCari.addActionListener(e -> filterData());
        btnReset.addActionListener(e -> resetFilter());
        btnTambah.addActionListener(e -> showFormDialog(null));
        
        // Update footer saat data berubah
        model.addTableModelListener(e -> {
            lblTotal.setText("Total Perawat: " + model.getRowCount());
        });
        
        // Initial footer update
        SwingUtilities.invokeLater(() -> {
            lblTotal.setText("Total Perawat: " + model.getRowCount());
        });
    }
    
    private void loadData() {
        model.setRowCount(0);
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM perawat ORDER BY kd_perawat");
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("kd_perawat"),
                    rs.getString("nama"),
                    rs.getString("spesialis"),
                    rs.getString("jk"),
                    rs.getString("no_telp"),
                    rs.getString("alamat"),
                    "Edit|Hapus"
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error load data: " + e.getMessage());
        }
    }
    
    private void filterData() {
        String nama = txtCariNama.getText().trim();
        String spesialis = cbSpesialis.getSelectedItem().toString();
        String jk = cbJk.getSelectedItem().toString();
        
        model.setRowCount(0);
        try {
            StringBuilder sql = new StringBuilder("SELECT * FROM perawat WHERE 1=1");
            if (!nama.isEmpty()) {
                sql.append(" AND nama LIKE '%").append(nama).append("%'");
            }
            if (!spesialis.equals("Semua")) {
                sql.append(" AND spesialis = '").append(spesialis).append("'");
            }
            if (!jk.equals("Semua")) {
                sql.append(" AND jk = '").append(jk).append("'");
            }
            sql.append(" ORDER BY kd_perawat");
            
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql.toString());
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("kd_perawat"),
                    rs.getString("nama"),
                    rs.getString("spesialis"),
                    rs.getString("jk"),
                    rs.getString("no_telp"),
                    rs.getString("alamat"),
                    "Edit|Hapus"
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error filter: " + e.getMessage());
        }
    }
    
    private void resetFilter() {
        txtCariNama.setText("");
        cbSpesialis.setSelectedIndex(0);
        cbJk.setSelectedIndex(0);
        loadData();
    }
    
    private void showFormDialog(String kdPerawat) {
        dialogForm = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            kdPerawat == null ? "Tambah Data Perawat" : "Edit Data Perawat", true);
        dialogForm.setSize(500, 520);
        dialogForm.setLocationRelativeTo(this);
        dialogForm.setLayout(new BorderLayout(10, 10));
        
        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Kode Perawat
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblKd = new JLabel("Kode Perawat:");
        lblKd.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(lblKd, gbc);
        gbc.gridx = 1;
        JTextField txtKdPerawat = new JTextField(20);
        txtKdPerawat.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(txtKdPerawat, gbc);
        
        // Nama Perawat
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblNama = new JLabel("Nama Perawat:");
        lblNama.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(lblNama, gbc);
        gbc.gridx = 1;
        JTextField txtNama = new JTextField(20);
        txtNama.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(txtNama, gbc);
        
        // Spesialis
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblSpesialis = new JLabel("Spesialis:");
        lblSpesialis.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(lblSpesialis, gbc);
        gbc.gridx = 1;
        JComboBox<String> cbSpesialisForm = new JComboBox<>(new String[]{
            "Spesialis Anak", "Spesialis Penyakit Dalam", "Spesialis Bedah", 
            "Spesialis Gigi", "Spesialis Kandungan"
        });
        cbSpesialisForm.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(cbSpesialisForm, gbc);
        
        // Jenis Kelamin
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel lblJk = new JLabel("Jenis Kelamin:");
        lblJk.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(lblJk, gbc);
        gbc.gridx = 1;
        JComboBox<String> cbJkForm = new JComboBox<>(new String[]{"L", "P"});
        cbJkForm.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(cbJkForm, gbc);
        
        // No Telepon
        gbc.gridx = 0; gbc.gridy = 4;
        JLabel lblTelp = new JLabel("No. Telepon:");
        lblTelp.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(lblTelp, gbc);
        gbc.gridx = 1;
        JTextField txtNoTelp = new JTextField(20);
        txtNoTelp.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(txtNoTelp, gbc);
        
        // Alamat
        gbc.gridx = 0; gbc.gridy = 5;
        JLabel lblAlamat = new JLabel("Alamat:");
        lblAlamat.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(lblAlamat, gbc);
        gbc.gridx = 1;
        JTextArea txtAlamat = new JTextArea(3, 20);
        txtAlamat.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtAlamat.setLineWrap(true);
        JScrollPane scrollAlamat = new JScrollPane(txtAlamat);
        formPanel.add(scrollAlamat, gbc);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        
        JButton btnSimpan = new JButton("💾 Simpan");
        btnSimpan.setBackground(colorSuccess);
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSimpan.setFocusPainted(false);
        btnSimpan.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSimpan.setPreferredSize(new Dimension(100, 38));
        
        JButton btnBatal = new JButton("❌ Batal");
        btnBatal.setBackground(Color.GRAY);
        btnBatal.setForeground(Color.WHITE);
        btnBatal.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnBatal.setFocusPainted(false);
        btnBatal.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBatal.setPreferredSize(new Dimension(100, 38));
        
        buttonPanel.add(btnSimpan);
        buttonPanel.add(btnBatal);
        
        dialogForm.add(formPanel, BorderLayout.CENTER);
        dialogForm.add(buttonPanel, BorderLayout.SOUTH);
        
        // Jika edit, load data
        if (kdPerawat != null) {
            loadDataToForm(kdPerawat, txtKdPerawat, txtNama, cbSpesialisForm, cbJkForm, txtNoTelp, txtAlamat);
            txtKdPerawat.setEnabled(false);
        }
        
        // Event Simpan
        btnSimpan.addActionListener(e -> {
            if (kdPerawat == null) {
                simpanPerawat(txtKdPerawat, txtNama, cbSpesialisForm, cbJkForm, txtNoTelp, txtAlamat);
            } else {
                updatePerawat(kdPerawat, txtNama, cbSpesialisForm, cbJkForm, txtNoTelp, txtAlamat);
            }
        });
        
        btnBatal.addActionListener(e -> dialogForm.dispose());
        
        dialogForm.setVisible(true);
    }
    
    private void loadDataToForm(String kdPerawat, JTextField txtKd, JTextField txtNama, 
                                 JComboBox<String> cbSpesialis, JComboBox<String> cbJk,
                                 JTextField txtTelp, JTextArea txtAlamat) {
        try {
            String sql = "SELECT * FROM perawat WHERE kd_perawat=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, kdPerawat);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                txtKd.setText(rs.getString("kd_perawat"));
                txtNama.setText(rs.getString("nama"));
                cbSpesialis.setSelectedItem(rs.getString("spesialis"));
                cbJk.setSelectedItem(rs.getString("jk"));
                txtTelp.setText(rs.getString("no_telp"));
                txtAlamat.setText(rs.getString("alamat"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    private void simpanPerawat(JTextField txtKd, JTextField txtNama, JComboBox<String> cbSpesialis,
                                JComboBox<String> cbJk, JTextField txtTelp, JTextArea txtAlamat) {
        if (txtKd.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(dialogForm, "Kode Perawat tidak boleh kosong!");
            return;
        }
        
        try {
            String sql = "INSERT INTO perawat VALUES (?,?,?,?,?,?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, txtKd.getText().trim());
            pst.setString(2, txtNama.getText().trim());
            pst.setString(3, cbSpesialis.getSelectedItem().toString());
            pst.setString(4, cbJk.getSelectedItem().toString());
            pst.setString(5, txtTelp.getText().trim());
            pst.setString(6, txtAlamat.getText().trim());
            pst.executeUpdate();
            
            JOptionPane.showMessageDialog(dialogForm, "Data perawat berhasil disimpan!");
            dialogForm.dispose();
            loadData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(dialogForm, "Error: " + e.getMessage());
        }
    }
    
    private void updatePerawat(String kdPerawat, JTextField txtNama, JComboBox<String> cbSpesialis,
                                JComboBox<String> cbJk, JTextField txtTelp, JTextArea txtAlamat) {
        try {
            String sql = "UPDATE perawat SET nama=?, spesialis=?, jk=?, no_telp=?, alamat=? WHERE kd_perawat=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, txtNama.getText().trim());
            pst.setString(2, cbSpesialis.getSelectedItem().toString());
            pst.setString(3, cbJk.getSelectedItem().toString());
            pst.setString(4, txtTelp.getText().trim());
            pst.setString(5, txtAlamat.getText().trim());
            pst.setString(6, kdPerawat);
            pst.executeUpdate();
            
            JOptionPane.showMessageDialog(dialogForm, "Data perawat berhasil diupdate!");
            dialogForm.dispose();
            loadData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(dialogForm, "Error: " + e.getMessage());
        }
    }
    
    private void hapusPerawat(String kdPerawat) {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Yakin ingin menghapus perawat " + kdPerawat + "?", 
            "Konfirmasi Hapus", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String sql = "DELETE FROM perawat WHERE kd_perawat=?";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, kdPerawat);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Data perawat berhasil dihapus!");
                loadData();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
    
    // ========== BUTTON RENDERER UNTUK TABEL ==========
    class ButtonRenderer extends JPanel implements TableCellRenderer {
        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 8, 5));
            setOpaque(true);
        }
        
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
            removeAll();
