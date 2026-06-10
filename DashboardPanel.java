package tampilan;

import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel {
    private Color colorPrimary = new Color(46, 125, 50);
    
    public DashboardPanel() {
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        
        JLabel lblWelcome = new JLabel("Selamat Datang di");
        lblWelcome.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        lblWelcome.setForeground(Color.GRAY);
        
        JLabel lblTitle = new JLabel("HOSPITAL MANAGEMENT SYSTEM");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTitle.setForeground(colorPrimary);
        
        JLabel lblSub = new JLabel("Sistem Informasi Manajemen Rumah Sakit");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSub.setForeground(Color.GRAY);
        
        JLabel lblMenu = new JLabel("Silakan pilih menu di sidebar kiri untuk memulai");
        lblMenu.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblMenu.setForeground(new Color(150, 150, 150));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(lblWelcome, gbc);
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 0, 0);
        add(lblTitle, gbc);
        gbc.gridy = 2;
        gbc.insets = new Insets(5, 0, 0, 0);
        add(lblSub, gbc);
        gbc.gridy = 3;
        gbc.insets = new Insets(30, 0, 0, 0);
        add(lblMenu, gbc);
    }
}
