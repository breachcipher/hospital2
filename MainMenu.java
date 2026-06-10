package tampilan;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainMenu extends JFrame {
    private JPanel contentPanel;
    private JLabel lblTitle;
    private CardLayout cardLayout;
    
    // Warna Hijau Tua
    private Color colorSidebar = new Color(27, 94, 32);      // #1B5E20
    private Color colorHover = new Color(56, 142, 60);       // #388E3C
    private Color colorPrimary = new Color(46, 125, 50);     // #2E7D32
    
    public MainMenu() {
        setTitle("Hospital Management System");
        setSize(1300, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1100, 600));
        
        initComponents();
        
        // Tampilkan dashboard awal
        showPanel("dashboard");
        lblTitle.setText("Dashboard");
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // SIDEBAR KIRI
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);
        
        // MAIN PANEL KANAN
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        
        // HEADER
        JPanel header = createHeader();
        mainPanel.add(header, BorderLayout.NORTH);
        
        // CONTENT PANEL dengan CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Tambahkan semua panel ke card layout
        contentPanel.add(new DashboardPanel(), "dashboard");
        contentPanel.add(new PerawatPanel(), "perawat");
        contentPanel.add(new DokterPanel(), "dokter");
        contentPanel.add(new PasienPanel(), "pasien");
        contentPanel.add(new ObatPanel(), "obat");
        contentPanel.add(new PendaftaranPanel(), "pendaftaran");
        contentPanel.add(new PemeriksaanPanel(), "pemeriksaan");
        contentPanel.add(new ResepPanel(), "resep");
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(colorSidebar);
        sidebar.setPreferredSize(new Dimension(260, getHeight()));
        
        // Logo / Title
        JLabel lblLogo = new JLabel("🏥 HOSPITAL", JLabel.CENTER);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblLogo.setBorder(BorderFactory.createEmptyBorder(25, 10, 15, 10));
        sidebar.add(lblLogo);
        
        JLabel lblSubLogo = new JLabel("Management System", JLabel.CENTER);
        lblSubLogo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubLogo.setForeground(new Color(200, 230, 201));
        lblSubLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSubLogo.setBorder(BorderFactory.createEmptyBorder(0, 10, 20, 10));
        sidebar.add(lblSubLogo);
        
        // Separator
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(56, 142, 60));
        separator.setMaximumSize(new Dimension(220, 2));
        separator.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(separator);
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Menu Items
        sidebar.add(createMenuItem("🏠 Dashboard", "dashboard"));
        sidebar.add(createMenuItem("👩‍⚕️ Data Perawat", "perawat"));
        sidebar.add(createMenuItem("👨‍⚕️ Data Dokter", "dokter"));
        sidebar.add(createMenuItem("👤 Data Pasien", "pasien"));
        sidebar.add(createMenuItem("📋 Pendaftaran Berobat", "pendaftaran"));
        sidebar.add(createMenuItem("💊 Data Obat", "obat"));
        sidebar.add(createMenuItem("🔍 Pemeriksaan", "pemeriksaan"));
        sidebar.add(createMenuItem("📝 Resep Obat", "resep"));
        
        sidebar.add(Box.createVerticalGlue());
        
        // Logout Button
        JButton btnLogout = createMenuItem("🚪 Logout", "logout");
        btnLogout.addActionListener(e -> logout());
        sidebar.add(btnLogout);
        sidebar.add(Box.createRigidArea(new Dimension(0, 25)));
        
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
        btn.setMaximumSize(new Dimension(260, 45));
        btn.setPreferredSize(new Dimension(260, 45));
        
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
                showPanel(panelName);
                lblTitle.setText(text);
            });
        }
        
        return btn;
    }
    
    private void showPanel(String panelName) {
        cardLayout.show(contentPanel, panelName);
    }
    
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(200, 230, 201)));
        header.setPreferredSize(new Dimension(0, 65));
        
        lblTitle = new JLabel("Dashboard");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(colorPrimary);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        header.add(lblTitle, BorderLayout.WEST);
        
        // User info panel
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setBackground(Color.WHITE);
        
        JLabel lblUser = new JLabel("👤 Administrator");
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblUser.setForeground(Color.DARK_GRAY);
        userPanel.add(lblUser);
        
        header.add(userPanel, BorderLayout.EAST);
        
        return header;
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Apakah Anda yakin ingin logout?", 
            "Konfirmasi Logout", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            new LoginForm().setVisible(true);
            this.dispose();
        }
    }
}
