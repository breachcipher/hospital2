package tampilan;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class menu_utama extends JFrame {
    private JPanel contentPanel;
    private JLabel lblTitle;
    private Color colorPrimary = new Color(46, 125, 50);   // Hijau tua #2E7D32
    private Color colorSidebar = new Color(27, 94, 32);    // Hijau lebih tua #1B5E20
    private Color colorHover = new Color(56, 142, 60);     // Hijau hover #388E3C
    
    public menu_utama() {
        setTitle("Hospital Management System");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1000, 600));
        
        initComponents();
        
        // Default tampilkan dashboard
        showPanel(new dashboard_panel());
        lblTitle.setText("Dashboard");
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Sidebar
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);
        
        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        
        // Header
        JPanel header = createHeader();
        mainPanel.add(header, BorderLayout.NORTH);
        
        // Content Panel
        contentPanel = new JPanel(new CardLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(colorSidebar);
        sidebar.setPreferredSize(new Dimension(250, getHeight()));
        
        // Logo / Title
        JLabel lblLogo = new JLabel("HOSPITAL SYSTEM", JLabel.CENTER);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(lblLogo);
        
        // Separator
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(56, 142, 60));
        sidebar.add(separator);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Menu Items (SEMUA FITUR)
        sidebar.add(createMenuItem("🏠 Dashboard", "dashboard"));
        sidebar.add(createMenuItem("👩‍⚕️ Data Perawat", "perawat"));
        sidebar.add(createMenuItem("👨‍⚕️ Data Dokter", "dokter"));
        sidebar.add(createMenuItem("👤 Data Pasien", "pasien"));
        sidebar.add(createMenuItem("📋 Pendaftaran Berobat", "daftar"));
        sidebar.add(createMenuItem("💊 Data Obat", "obat"));
        sidebar.add(createMenuItem("🔍 Pemeriksaan", "periksa"));
        sidebar.add(createMenuItem("📝 Resep Obat", "resep"));
        
        sidebar.add(Box.createVerticalGlue());
        
        // Logout button
        JButton btnLogout = createMenuItem("🚪 Logout", "logout");
        btnLogout.addActionListener(e -> logout());
        sidebar.add(btnLogout);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));
        
        return sidebar;
    }
    
    private JButton createMenuItem(String text, String panelName) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(colorSidebar);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(colorHover);
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(colorSidebar);
            }
        });
        
        if (!"logout".equals(panelName)) {
            btn.addActionListener(e -> {
                showPanelForMenu(panelName);
                lblTitle.setText(text);
            });
        }
        
        return btn;
    }
    
    private void showPanelForMenu(String panelName) {
        switch (panelName) {
            case "dashboard":
                showPanel(new dashboard_panel());
                break;
            case "perawat":
                showPanel(new form_perawat());
                break;
            case "dokter":
                showPanel(new form_dokter());
                break;
            case "pasien":
                showPanel(new form_pasien());
                break;
            case "daftar":
                showPanel(new form_daftar());
                break;
            case "obat":
                showPanel(new form_obat());
                break;
            case "periksa":
                showPanel(new form_periksa());
                break;
            case "resep":
                showPanel(new form_resep());
                break;
        }
    }
    
    private void showPanel(JPanel panel) {
        contentPanel.removeAll();
        contentPanel.add(panel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 230, 201)));
        header.setPreferredSize(new Dimension(0, 60));
        
        lblTitle = new JLabel("Dashboard");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(colorPrimary);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        header.add(lblTitle, BorderLayout.WEST);
        
        // User info
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setBackground(Color.WHITE);
        JLabel lblUser = new JLabel("👤 Admin");
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userPanel.add(lblUser);
        header.add(userPanel, BorderLayout.EAST);
        
        return header;
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin logout?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            new form_login().setVisible(true);
            this.dispose();
        }
    }
}
