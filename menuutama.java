package tampilan;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import koneksi.koneksi;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import koneksi.koneksi;
public class form_perawat extends JPanel {
    private Connection conn;
    private JTextField txtCariNama, txtKdPerawat, txtNama, txtNoTelp, txtAlamat;
    private JComboBox<String> cbSpesialis, cbJk;
    private JButton btnCari, btnReset, btnTambah, btnSimpan, btnUpdate, btnHapus, btnBatal;
    private JTable table;
    private DefaultTableModel model;
    private JDialog dialogForm;
    private Color colorPrimary = new Color(46, 125, 50);   // Hijau tua #2E7D32
    private Color colorSidebar = new Color(27, 94, 32);    // Hijau lebih tua #1B5E20
    private Color colorSuccess = new Color(67, 160, 71);   // Hijau sukses #43A047
    private Color colorDanger = new Color(229, 57, 53);    // Merah #E53935
    
    public form_perawat() {
        conn = new koneksi().Connect();
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        initComponents();
        loadData();
    }
    
    private void initComponents() {
        // Panel Filter
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
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        filterPanel.add(new JLabel("Nama Perawat:"), gbc);
        gbc.gridx = 1;
        txtCariNama = new JTextField(15);
        filterPanel.add(txtCariNama, gbc);
        
        gbc.gridx = 2;
        filterPanel.add(new JLabel("Spesialis:"), gbc);
        gbc.gridx = 3;
        cbSpesialis = new JComboBox<>(new String[]{"Semua", "Spesialis Anak", "Spesialis Penyakit Dalam", "Spesialis Bedah", "Spesialis Gigi", "Spesialis Kandungan"});
        filterPanel.add(cbSpesialis, gbc);
        
        gbc.gridx = 4;
        filterPanel.add(new JLabel("Jenis Kelamin:"), gbc);
        gbc.gridx = 5;
        cbJk = new JComboBox<>(new String[]{"Semua", "L", "P"});
        filterPanel.add(cbJk, gbc);
        
        gbc.gridx = 6;
        btnCari = new JButton("🔍 Terapkan Filter");
        btnCari.setBackground(colorPrimary);
        btnCari.setForeground(Color.WHITE);
        btnCari.setFocusPainted(false);
        btnCari.setCursor(new Cursor(Cursor.HAND_CURSOR));
        filterPanel.add(btnCari, gbc);
        
        gbc.gridx = 7;
        btnReset = new JButton("↺ Reset");
        btnReset.setBackground(Color.GRAY);
        btnReset.setForeground(Color.WHITE);
        btnReset.setFocusPainted(false);
        btnReset.setCursor(new Cursor(Cursor.HAND_CURSOR));
        filterPanel.add(btnReset, gbc);
        
        // Panel Tombol Tambah
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        btnTambah = new JButton("+ Tambah Data Perawat Baru");
        btnTambah.setBackground(colorSuccess);
        btnTambah.setForeground(Color.WHITE);
        btnTambah.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnTambah.setFocusPainted(false);
        btnTambah.setCursor(new Cursor(Cursor.HAND_CURSOR));
        buttonPanel.add(btnTambah);
        
        // Tabel
        String[] columns = {"Kd Perawat", "Nama Perawat", "Spesialis", "JK", "No. Telp", "Alamat", "Aksi"};
        model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return column == 6;
            }
        };
        table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(colorSidebar);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setReorderingAllowed(false);
        
        // Renderer untuk kolom aksi (button)
        table.getColumn("Aksi").setCellRenderer(new ButtonRenderer());
        table.getColumn("Aksi").setCellEditor(new ButtonEditor(new JCheckBox()));
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 230, 201)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        // Footer Total
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footerPanel.setBackground(Color.WHITE);
        JLabel lblTotal = new JLabel("Total Perawat: 0");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTotal.setForeground(colorPrimary);
        footerPanel.add(lblTotal);
        
        // Layout
        add(filterPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);
        
        // Events
        btnCari.addActionListener(e -> filterData());
        btnReset.addActionListener(e -> resetFilter());
        btnTambah.addActionListener(e -> showFormDialog(null));
        
        // Update footer setelah load data
        SwingUtilities.invokeLater(() -> {
            int rowCount = model.getRowCount();
            lblTotal.setText("Total Perawat: " + rowCount);
        });
        
        model.addTableModelListener(e -> {
            int rowCount = model.getRowCount();
            lblTotal.setText("Total Perawat: " + rowCount);
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
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    private void filterData() {
        String nama = txtCariNama.getText().trim();
        String spesialis = cbSpesialis.getSelectedItem().toString();
        String jk = cbJk.getSelectedItem().toString();
        
        model.setRowCount(0);
        try {
            StringBuilder sql = new StringBuilder("SELECT * FROM perawat WHERE 1=1");
            if (!nama.isEmpty()) sql.append(" AND nama LIKE '%").append(nama).append("%'");
            if (!spesialis.equals("Semua")) sql.append(" AND spesialis = '").append(spesialis).append("'");
            if (!jk.equals("Semua")) sql.append(" AND jk = '").append(jk).append("'");
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
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    private void resetFilter() {
        txtCariNama.setText("");
        cbSpesialis.setSelectedIndex(0);
        cbJk.setSelectedIndex(0);
        loadData();
    }
    
    private void showFormDialog(String kdPerawat) {
        dialogForm = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), kdPerawat == null ? "Tambah Perawat" : "Edit Perawat", true);
        dialogForm.setSize(450, 450);
        dialogForm.setLocationRelativeTo(this);
        dialogForm.setLayout(new BorderLayout(10, 10));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Komponen form
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblKd = new JLabel("Kode Perawat:");
        lblKd.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(lblKd, gbc);
        gbc.gridx = 1;
        txtKdPerawat = new JTextField(15);
        formPanel.add(txtKdPerawat, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblNama = new JLabel("Nama Perawat:");
        lblNama.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(lblNama, gbc);
        gbc.gridx = 1;
        txtNama = new JTextField(15);
        formPanel.add(txtNama, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblSpesialis = new JLabel("Spesialis:");
        lblSpesialis.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(lblSpesialis, gbc);
        gbc.gridx = 1;
        JComboBox<String> cbSpesialisForm = new JComboBox<>(new String[]{"Spesialis Anak", "Spesialis Penyakit Dalam", "Spesialis Bedah", "Spesialis Gigi", "Spesialis Kandungan"});
        formPanel.add(cbSpesialisForm, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel lblJk = new JLabel("Jenis Kelamin:");
        lblJk.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(lblJk, gbc);
        gbc.gridx = 1;
        JComboBox<String> cbJkForm = new JComboBox<>(new String[]{"L", "P"});
        formPanel.add(cbJkForm, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        JLabel lblTelp = new JLabel("No. Telepon:");
        lblTelp.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(lblTelp, gbc);
        gbc.gridx = 1;
        txtNoTelp = new JTextField(15);
        formPanel.add(txtNoTelp, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        JLabel lblAlamat = new JLabel("Alamat:");
        lblAlamat.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(lblAlamat, gbc);
        gbc.gridx = 1;
        txtAlamat = new JTextField(15);
        formPanel.add(txtAlamat, gbc);
        
        // Panel tombol
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        btnSimpan = new JButton("💾 Simpan");
        btnSimpan.setBackground(colorSuccess);
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setFocusPainted(false);
        btnSimpan.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBatal = new JButton("❌ Batal");
        btnBatal.setBackground(Color.GRAY);
        btnBatal.setForeground(Color.WHITE);
        btnBatal.setFocusPainted(false);
        btnBatal.setCursor(new Cursor(Cursor.HAND_CURSOR));
        buttonPanel.add(btnSimpan);
        buttonPanel.add(btnBatal);
        
        dialogForm.add(formPanel, BorderLayout.CENTER);
        dialogForm.add(buttonPanel, BorderLayout.SOUTH);
        
        // Jika edit, load data
        if (kdPerawat != null) {
            loadDataToForm(kdPerawat, cbSpesialisForm, cbJkForm);
            txtKdPerawat.setEnabled(false);
        }
        
        btnSimpan.addActionListener(e -> {
            if (kdPerawat == null) {
                simpanPerawat(cbSpesialisForm, cbJkForm);
            } else {
                updatePerawat(kdPerawat, cbSpesialisForm, cbJkForm);
            }
        });
        
        btnBatal.addActionListener(e -> dialogForm.dispose());
        
        dialogForm.setVisible(true);
    }
    
    private void loadDataToForm(String kdPerawat, JComboBox<String> cbSpesialisForm, JComboBox<String> cbJkForm) {
        try {
            String sql = "SELECT * FROM perawat WHERE kd_perawat=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, kdPerawat);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                txtKdPerawat.setText(rs.getString("kd_perawat"));
                txtNama.setText(rs.getString("nama"));
                cbSpesialisForm.setSelectedItem(rs.getString("spesialis"));
                cbJkForm.setSelectedItem(rs.getString("jk"));
                txtNoTelp.setText(rs.getString("no_telp"));
                txtAlamat.setText(rs.getString("alamat"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    private void simpanPerawat(JComboBox<String> cbSpesialisForm, JComboBox<String> cbJkForm) {
        if (txtKdPerawat.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(dialogForm, "Kode Perawat tidak boleh kosong");
            return;
        }
        try {
            String sql = "INSERT INTO perawat VALUES (?,?,?,?,?,?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, txtKdPerawat.getText());
            pst.setString(2, txtNama.getText());
            pst.setString(3, cbSpesialisForm.getSelectedItem().toString());
            pst.setString(4, cbJkForm.getSelectedItem().toString());
            pst.setString(5, txtNoTelp.getText());
            pst.setString(6, txtAlamat.getText());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(dialogForm, "Data tersimpan");
            dialogForm.dispose();
            loadData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(dialogForm, "Error: " + e.getMessage());
        }
    }
    
    private void updatePerawat(String kdPerawat, JComboBox<String> cbSpesialisForm, JComboBox<String> cbJkForm) {
        try {
            String sql = "UPDATE perawat SET nama=?, spesialis=?, jk=?, no_telp=?, alamat=? WHERE kd_perawat=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, txtNama.getText());
            pst.setString(2, cbSpesialisForm.getSelectedItem().toString());
            pst.setString(3, cbJkForm.getSelectedItem().toString());
            pst.setString(4, txtNoTelp.getText());
            pst.setString(5, txtAlamat.getText());
            pst.setString(6, kdPerawat);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(dialogForm, "Data terupdate");
            dialogForm.dispose();
            loadData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(dialogForm, "Error: " + e.getMessage());
        }
    }
    
    private void hapusPerawat(String kdPerawat) {
        int confirm = JOptionPane.showConfirmDialog(this, "Yakin hapus perawat " + kdPerawat + "?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String sql = "DELETE FROM perawat WHERE kd_perawat=?";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, kdPerawat);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Data terhapus");
                loadData();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
    
    // Renderer untuk tombol di tabel
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
            btnEdit.setBackground(new Color(102, 187, 106));  // Hijau muda
            btnHapus.setBackground(colorDanger);
            btnEdit.setForeground(Color.WHITE);
            btnHapus.setForeground(Color.WHITE);
            btnEdit.setFocusPainted(false);
            btnHapus.setFocusPainted(false);
            btnEdit.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnHapus.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            String kdPerawat = table.getValueAt(row, 0).toString();
            btnEdit.addActionListener(e -> showFormDialog(kdPerawat));
            btnHapus.addActionListener(e -> hapusPerawat(kdPerawat));
            
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
