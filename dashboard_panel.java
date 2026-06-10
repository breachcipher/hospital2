package tampilan;

import javax.swing.*;
import java.awt.*;

public class dashboard_panel extends JPanel {
    private Color colorPrimary = new Color(46, 125, 50);
    
    public dashboard_panel() {
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        
        JLabel lblWelcome = new JLabel("Selamat Datang di Hospital Management System");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblWelcome.setForeground(colorPrimary);
        
        JLabel lblSub = new JLabel("Sistem Informasi Manajemen Rumah Sakit");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSub.setForeground(Color.GRAY);
        
        JLabel lblMenu = new JLabel("Silahkan pilih menu di samping kiri untuk memulai");
        lblMenu.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblMenu.setForeground(new Color(100, 100, 100));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(lblWelcome, gbc);
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 0, 0);
        add(lblSub, gbc);
        gbc.gridy = 2;
        gbc.insets = new Insets(20, 0, 0, 0);
        add(lblMenu, gbc);
    }
}
