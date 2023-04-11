package com.cosimogiani.museum.view.swing;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class SwingView extends JFrame {
	
	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
	private JTextField textArtistName;

	/**
	 * Launch the application.
	*/
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SwingView frame = new SwingView();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	

	/**
	 * Create the frame.
	 */
	public SwingView() {
		/*setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);*/
		setBackground(UIManager.getColor("MenuItem.acceleratorForeground"));
		setResizable(false);
		setTitle("MUSEUM VIEW");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 818, 714);
		contentPane = new JPanel();
		contentPane.setName("MUSEUM VIEW");
		contentPane.setBackground(new Color(135, 206, 235));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{397, 424, 0};
		gbl_contentPane.rowHeights = new int[]{283, 393, 0};
		gbl_contentPane.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JPanel panelArtist = new JPanel();
		panelArtist.setBackground(UIManager.getColor("PasswordField.inactiveForeground"));
		GridBagConstraints gbc_panelArtist = new GridBagConstraints();
		gbc_panelArtist.insets = new Insets(0, 0, 5, 5);
		gbc_panelArtist.fill = GridBagConstraints.BOTH;
		gbc_panelArtist.gridx = 0;
		gbc_panelArtist.gridy = 0;
		contentPane.add(panelArtist, gbc_panelArtist);
		GridBagLayout gbl_panelArtist = new GridBagLayout();
		gbl_panelArtist.columnWidths = new int[]{30, 0, 235, 0, 30, 0};
		gbl_panelArtist.rowHeights = new int[]{30, 30, 0, 143, 0, 30, 0};
		gbl_panelArtist.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panelArtist.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panelArtist.setLayout(gbl_panelArtist);
		
		JLabel lblArtist = new JLabel("ARTIST");
		lblArtist.setForeground(new Color(178, 34, 34));
		lblArtist.setFont(new Font("SansSerif", Font.BOLD, 16));
		GridBagConstraints gbc_lblArtist = new GridBagConstraints();
		gbc_lblArtist.gridwidth = 3;
		gbc_lblArtist.insets = new Insets(0, 0, 5, 5);
		gbc_lblArtist.fill = GridBagConstraints.BOTH;
		gbc_lblArtist.gridx = 1;
		gbc_lblArtist.gridy = 1;
		panelArtist.add(lblArtist, gbc_lblArtist);
		
		JLabel lblArtistName = new JLabel("Name");
		GridBagConstraints gbc_lblArtistName = new GridBagConstraints();
		gbc_lblArtistName.fill = GridBagConstraints.BOTH;
		gbc_lblArtistName.insets = new Insets(0, 0, 5, 5);
		gbc_lblArtistName.gridx = 1;
		gbc_lblArtistName.gridy = 2;
		panelArtist.add(lblArtistName, gbc_lblArtistName);
		
		textArtistName = new JTextField();
		GridBagConstraints gbc_textArtistName = new GridBagConstraints();
		gbc_textArtistName.insets = new Insets(0, 0, 5, 5);
		gbc_textArtistName.fill = GridBagConstraints.BOTH;
		gbc_textArtistName.gridx = 2;
		gbc_textArtistName.gridy = 2;
		textArtistName.setName("textArtistName");
		panelArtist.add(textArtistName, gbc_textArtistName);
		textArtistName.setColumns(10);
	}

}
