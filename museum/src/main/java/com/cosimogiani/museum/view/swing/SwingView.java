package com.cosimogiani.museum.view.swing;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.cosimogiani.museum.model.Artist;
import com.cosimogiani.museum.model.Work;

public class SwingView extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	private JList<Artist> listArtist;
	private DefaultListModel<Artist> artistListModel;
	
	private JPanel contentPane;
	private JTextField textArtistName;
	private JTextField textWorkTitle;
	private JTextField textDescription;
	private JButton btnArtistDelete;

	private JComboBox<String> comboBoxWorkType;
	private DefaultComboBoxModel<String> comboBoxWorkTypeModel;

	private JList<Work> listWorks;
	private DefaultListModel<Work> workListModel;
	
	private JList<Work> listSearch;
	private DefaultListModel<Work> searchListModel;
	
	private JComboBox<Artist> comboBoxSearch;
	private DefaultComboBoxModel<Artist> comboBoxSearchModel;

	private JButton btnWorkDelete;
	private JButton btnArtistAdd;
	private JButton btnWorkAdd;
	private JLabel lblWorkTitle;
	private JLabel lblArtistError;
	private JLabel labelWorkError;

	private JButton btnSearch;

	private JLabel labelSearchError;

	/**
	 * Launch the application.
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
	}*/
	

	/**
	 * Create the frame.
	 */
	public SwingView() {
		setBackground(UIManager.getColor("MenuItem.acceleratorForeground"));
		setResizable(false);
		setTitle("MUSEUM VIEW");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 868, 737);
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
		gbc_lblArtist.fill = GridBagConstraints.VERTICAL;
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
		
		btnArtistAdd = new JButton("ADD ARTIST");
		btnArtistAdd.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnArtistAdd.setEnabled(false);
		GridBagConstraints gbc_btnArtistAdd = new GridBagConstraints();
		gbc_btnArtistAdd.insets = new Insets(0, 0, 5, 5);
		gbc_btnArtistAdd.fill = GridBagConstraints.BOTH;
		gbc_btnArtistAdd.gridx = 3;
		gbc_btnArtistAdd.gridy = 2;
		btnArtistAdd.setName("btnArtistAdd");
		panelArtist.add(btnArtistAdd, gbc_btnArtistAdd);
		
		JScrollPane scrollPaneArtist = new JScrollPane();
		GridBagConstraints gbc_scrollPaneArtist = new GridBagConstraints();
		gbc_scrollPaneArtist.gridwidth = 3;
		gbc_scrollPaneArtist.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPaneArtist.fill = GridBagConstraints.BOTH;
		gbc_scrollPaneArtist.gridx = 1;
		gbc_scrollPaneArtist.gridy = 3;
		panelArtist.add(scrollPaneArtist, gbc_scrollPaneArtist);
		
		artistListModel = new DefaultListModel<Artist>();
		listArtist = new JList<>(artistListModel);
		listArtist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listArtist.setName("listArtist");
		scrollPaneArtist.setViewportView(listArtist);
		
		btnArtistDelete = new JButton("DELETE ARTIST");
		btnArtistDelete.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnArtistDelete.setEnabled(false);
		GridBagConstraints gbc_btnArtistDelete = new GridBagConstraints();
		gbc_btnArtistDelete.gridwidth = 3;
		gbc_btnArtistDelete.insets = new Insets(0, 0, 5, 5);
		gbc_btnArtistDelete.gridx = 1;
		gbc_btnArtistDelete.gridy = 4;
		panelArtist.add(btnArtistDelete, gbc_btnArtistDelete);
		
		lblArtistError = new JLabel(" ");
		lblArtistError.setForeground(Color.RED);
		GridBagConstraints gbc_lblArtistError = new GridBagConstraints();
		gbc_lblArtistError.gridwidth = 3;
		gbc_lblArtistError.insets = new Insets(0, 0, 0, 5);
		gbc_lblArtistError.gridx = 1;
		gbc_lblArtistError.gridy = 5;
		lblArtistError.setName("lblArtistError");
		panelArtist.add(lblArtistError, gbc_lblArtistError);
		
		JPanel panelSearch = new JPanel();
		panelSearch.setBackground(UIManager.getColor("PasswordField.inactiveForeground"));
		GridBagConstraints gbc_panelSearch = new GridBagConstraints();
		gbc_panelSearch.gridheight = 2;
		gbc_panelSearch.insets = new Insets(0, 0, 5, 0);
		gbc_panelSearch.fill = GridBagConstraints.BOTH;
		gbc_panelSearch.gridx = 1;
		gbc_panelSearch.gridy = 0;
		contentPane.add(panelSearch, gbc_panelSearch);
		GridBagLayout gbl_panelSearch = new GridBagLayout();
		gbl_panelSearch.columnWidths = new int[]{30, 357, 30, 0};
		gbl_panelSearch.rowHeights = new int[]{30, 30, 30, 0, 0, 0, 30, 0, 30, 38, 354, 26, 0};
		gbl_panelSearch.columnWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panelSearch.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panelSearch.setLayout(gbl_panelSearch);
		
		JLabel lblSearch = new JLabel("SEARCH");
		lblSearch.setForeground(new Color(178, 34, 34));
		lblSearch.setFont(new Font("SansSerif", Font.BOLD, 16));
		GridBagConstraints gbc_lblSearch = new GridBagConstraints();
		gbc_lblSearch.fill = GridBagConstraints.VERTICAL;
		gbc_lblSearch.insets = new Insets(0, 0, 5, 5);
		gbc_lblSearch.gridx = 1;
		gbc_lblSearch.gridy = 1;
		panelSearch.add(lblSearch, gbc_lblSearch);
		
		JLabel lblInfoText1 = new JLabel("Select an artist from the list, then press the");
		GridBagConstraints gbc_lblInfoText1 = new GridBagConstraints();
		gbc_lblInfoText1.insets = new Insets(0, 0, 5, 5);
		gbc_lblInfoText1.gridx = 1;
		gbc_lblInfoText1.gridy = 3;
		lblInfoText1.setName("infoText1");
		panelSearch.add(lblInfoText1, gbc_lblInfoText1);
		
		JLabel lblInfoText2 = new JLabel("\"Search\" button to see the works of the selected");
		GridBagConstraints gbc_lblInfotext2 = new GridBagConstraints();
		gbc_lblInfotext2.insets = new Insets(0, 0, 5, 5);
		gbc_lblInfotext2.gridx = 1;
		gbc_lblInfotext2.gridy = 4;
		lblInfoText2.setName("infoText2");
		panelSearch.add(lblInfoText2, gbc_lblInfotext2);
		
		JLabel lblInfoText3 = new JLabel("artist and related informations.");
		GridBagConstraints gbc_lblInfoText3 = new GridBagConstraints();
		gbc_lblInfoText3.insets = new Insets(0, 0, 5, 5);
		gbc_lblInfoText3.gridx = 1;
		gbc_lblInfoText3.gridy = 5;
		lblInfoText3.setName("infoText3");
		panelSearch.add(lblInfoText3, gbc_lblInfoText3);
		
		comboBoxSearchModel = new DefaultComboBoxModel<>();
		comboBoxSearch = new JComboBox<>(comboBoxSearchModel);
		comboBoxSearch.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		comboBoxSearch.setBackground(SystemColor.text);
		GridBagConstraints gbc_comboBoxSearch = new GridBagConstraints();
		gbc_comboBoxSearch.insets = new Insets(0, 0, 5, 5);
		gbc_comboBoxSearch.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBoxSearch.gridx = 1;
		gbc_comboBoxSearch.gridy = 7;
		comboBoxSearch.setName("comboBoxSearch");
		panelSearch.add(comboBoxSearch, gbc_comboBoxSearch);
		
		btnSearch = new JButton("SEARCH");
		btnSearch.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnSearch.setEnabled(false);
		GridBagConstraints gbc_btnSearch = new GridBagConstraints();
		gbc_btnSearch.anchor = GridBagConstraints.NORTH;
		gbc_btnSearch.insets = new Insets(0, 0, 5, 5);
		gbc_btnSearch.gridx = 1;
		gbc_btnSearch.gridy = 9;
		panelSearch.add(btnSearch, gbc_btnSearch);
		
		JScrollPane scrollPaneSearch = new JScrollPane();
		GridBagConstraints gbc_scrollPaneSearch = new GridBagConstraints();
		gbc_scrollPaneSearch.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPaneSearch.fill = GridBagConstraints.BOTH;
		gbc_scrollPaneSearch.gridx = 1;
		gbc_scrollPaneSearch.gridy = 10;
		panelSearch.add(scrollPaneSearch, gbc_scrollPaneSearch);
		
		searchListModel = new DefaultListModel<Work>();
		listSearch = new JList<>(searchListModel);
		listSearch.setFocusable(false);
		listSearch.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listSearch.setSelectionBackground(Color.WHITE);
		listSearch.setName("listSearch");
		scrollPaneSearch.setViewportView(listSearch);
		
		labelSearchError = new JLabel(" ");
		labelSearchError.setForeground(Color.RED);
		GridBagConstraints gbc_labelSearchError = new GridBagConstraints();
		gbc_labelSearchError.insets = new Insets(0, 0, 0, 5);
		gbc_labelSearchError.gridx = 1;
		gbc_labelSearchError.gridy = 11;
		labelSearchError.setName("lblSearchError");
		panelSearch.add(labelSearchError, gbc_labelSearchError);
		
		JPanel panelWork = new JPanel();
		panelWork.setBackground(UIManager.getColor("PasswordField.inactiveForeground"));
		GridBagConstraints gbc_panelWork = new GridBagConstraints();
		gbc_panelWork.insets = new Insets(0, 0, 0, 5);
		gbc_panelWork.fill = GridBagConstraints.BOTH;
		gbc_panelWork.gridx = 0;
		gbc_panelWork.gridy = 1;
		contentPane.add(panelWork, gbc_panelWork);
		GridBagLayout gbl_panelWork = new GridBagLayout();
		gbl_panelWork.columnWidths = new int[]{30, 0, 203, 64, 30, 0};
		gbl_panelWork.rowHeights = new int[]{30, 30, 30, 0, 31, 193, 0, 30, 0};
		gbl_panelWork.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panelWork.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panelWork.setLayout(gbl_panelWork);
		
		JLabel lblArtwork = new JLabel("WORK");
		lblArtwork.setForeground(new Color(178, 34, 34));
		lblArtwork.setFont(new Font("SansSerif", Font.BOLD, 16));
		GridBagConstraints gbc_lblArtwork = new GridBagConstraints();
		gbc_lblArtwork.gridwidth = 3;
		gbc_lblArtwork.fill = GridBagConstraints.VERTICAL;
		gbc_lblArtwork.insets = new Insets(0, 0, 5, 5);
		gbc_lblArtwork.gridx = 1;
		gbc_lblArtwork.gridy = 1;
		panelWork.add(lblArtwork, gbc_lblArtwork);
		
		lblWorkTitle = new JLabel("Title");
		GridBagConstraints gbc_lblWorkTitle = new GridBagConstraints();
		gbc_lblWorkTitle.fill = GridBagConstraints.BOTH;
		gbc_lblWorkTitle.insets = new Insets(0, 0, 5, 5);
		gbc_lblWorkTitle.gridx = 1;
		gbc_lblWorkTitle.gridy = 2;
		panelWork.add(lblWorkTitle, gbc_lblWorkTitle);
		
		textWorkTitle = new JTextField();
		GridBagConstraints gbc_textWorkTitle = new GridBagConstraints();
		gbc_textWorkTitle.insets = new Insets(0, 0, 5, 5);
		gbc_textWorkTitle.fill = GridBagConstraints.BOTH;
		gbc_textWorkTitle.gridx = 2;
		gbc_textWorkTitle.gridy = 2;
		panelWork.add(textWorkTitle, gbc_textWorkTitle);
		textWorkTitle.setName("textWorkTitle");
		textWorkTitle.setColumns(10);
		
		btnWorkAdd = new JButton("ADD WORK");
		btnWorkAdd.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnWorkAdd.setEnabled(false);
		GridBagConstraints gbc_btnWorkAdd = new GridBagConstraints();
		gbc_btnWorkAdd.insets = new Insets(0, 0, 5, 5);
		gbc_btnWorkAdd.gridx = 3;
		gbc_btnWorkAdd.gridy = 2;
		panelWork.add(btnWorkAdd, gbc_btnWorkAdd);
		
		JLabel lblType = new JLabel("Type");
		GridBagConstraints gbc_lblType = new GridBagConstraints();
		gbc_lblType.fill = GridBagConstraints.BOTH;
		gbc_lblType.insets = new Insets(0, 0, 5, 5);
		gbc_lblType.gridx = 1;
		gbc_lblType.gridy = 3;
		panelWork.add(lblType, gbc_lblType);
		
		comboBoxWorkTypeModel = new DefaultComboBoxModel<String>();
		comboBoxWorkType = new JComboBox<>(comboBoxWorkTypeModel);
		comboBoxWorkType.setModel(new DefaultComboBoxModel<>(new String[] {
				"---",
				"Painting",
				"Sculpture",
				"Jewellery art",
				"Ceramic art",
				"Mosaic"
		}));
		comboBoxWorkType.setBackground(SystemColor.text);
		GridBagConstraints gbc_comboBoxWorkType = new GridBagConstraints();
		gbc_comboBoxWorkType.gridwidth = 2;
		gbc_comboBoxWorkType.insets = new Insets(0, 0, 5, 5);
		gbc_comboBoxWorkType.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBoxWorkType.gridx = 2;
		gbc_comboBoxWorkType.gridy = 3;
		comboBoxWorkType.setName("comboBoxWorkType");
		panelWork.add(comboBoxWorkType, gbc_comboBoxWorkType);
		
		JLabel lblDescription = new JLabel("Description");
		GridBagConstraints gbc_lblDescription = new GridBagConstraints();
		gbc_lblDescription.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblDescription.insets = new Insets(0, 0, 5, 5);
		gbc_lblDescription.gridx = 1;
		gbc_lblDescription.gridy = 4;
		panelWork.add(lblDescription, gbc_lblDescription);
		
		textDescription = new JTextField();
		GridBagConstraints gbc_textDescription = new GridBagConstraints();
		gbc_textDescription.gridwidth = 2;
		gbc_textDescription.insets = new Insets(0, 0, 5, 5);
		gbc_textDescription.fill = GridBagConstraints.BOTH;
		gbc_textDescription.gridx = 2;
		gbc_textDescription.gridy = 4;
		panelWork.add(textDescription, gbc_textDescription);
		textDescription.setName("textDescription");
		textDescription.setColumns(10);
		
		JScrollPane scrollPaneWork = new JScrollPane();
		GridBagConstraints gbc_scrollPaneWork = new GridBagConstraints();
		gbc_scrollPaneWork.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPaneWork.gridwidth = 3;
		gbc_scrollPaneWork.fill = GridBagConstraints.BOTH;
		gbc_scrollPaneWork.gridx = 1;
		gbc_scrollPaneWork.gridy = 5;
		panelWork.add(scrollPaneWork, gbc_scrollPaneWork);
		
		workListModel = new DefaultListModel<>();
		listWorks = new JList<>(workListModel);
		listWorks.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listWorks.setName("listWorks");
		scrollPaneWork.setViewportView(listWorks);
		
		btnWorkDelete = new JButton("DELETE WORK");
		btnWorkDelete.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnWorkDelete.setEnabled(false);
		GridBagConstraints gbc_btnWorkDelete = new GridBagConstraints();
		gbc_btnWorkDelete.insets = new Insets(0, 0, 5, 5);
		gbc_btnWorkDelete.gridwidth = 3;
		gbc_btnWorkDelete.gridx = 1;
		gbc_btnWorkDelete.gridy = 6;
		panelWork.add(btnWorkDelete, gbc_btnWorkDelete);
		
		labelWorkError = new JLabel(" ");
		labelWorkError.setForeground(Color.RED);
		GridBagConstraints gbc_labelWorkError = new GridBagConstraints();
		gbc_labelWorkError.gridwidth = 3;
		gbc_labelWorkError.insets = new Insets(0, 0, 0, 5);
		gbc_labelWorkError.gridx = 1;
		gbc_labelWorkError.gridy = 7;
		labelWorkError.setName("lblWorkError");
		panelWork.add(labelWorkError, gbc_labelWorkError);
	}

}
