package ch.maniswebdesign.chaned.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.GridBagLayout;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Insets;
import javax.swing.JTree;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.JScrollPane;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.JPopupMenu;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;

public class Main {

	protected static JFrame frmChannelEditor;
	protected static JTable table;
	protected static JProgressBar progressBar;
	protected static JLabel statusBar;
	protected static JTree contentTree;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new Main();
					Main.frmChannelEditor.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Main() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmChannelEditor = new JFrame();
		frmChannelEditor.setTitle("Chan. Ed. f. Sam. TV");
		frmChannelEditor.setBounds(100, 100, 609, 412);
		frmChannelEditor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{527, 128, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 0.0, 0.0, Double.MIN_VALUE};
		frmChannelEditor.getContentPane().setLayout(gridBagLayout);
				
		JSplitPane splitPane = new JSplitPane();
		splitPane.setContinuousLayout(true);
		GridBagConstraints gbc_splitPane = new GridBagConstraints();
		gbc_splitPane.insets = new Insets(0, 0, 5, 0);
		gbc_splitPane.gridwidth = 2;
		gbc_splitPane.fill = GridBagConstraints.BOTH;
		gbc_splitPane.gridx = 0;
		gbc_splitPane.gridy = 0;
		frmChannelEditor.getContentPane().add(splitPane, gbc_splitPane);
		
		JScrollPane scrollPane_tree = new JScrollPane();
		splitPane.setLeftComponent(scrollPane_tree);
		
		final JTree tree = new JTree();
		tree.setModel(null);
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			
			public void valueChanged(TreeSelectionEvent e) {
				MainMethods.selectNewFile();
			}
		});
		scrollPane_tree.setViewportView(tree);
		Main.contentTree = tree;
		
		final JPopupMenu treePopupMenu = new JPopupMenu();
		tree.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				if (contentTree.getModel() != null) treePopupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		});
		
		JMenuItem mntmRefresh = new JMenuItem("Refresh");
		mntmRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainMethods.updateTreeView(MainMethods.openFile);
			}
		});
		treePopupMenu.add(mntmRefresh);
		
		JScrollPane scrollPane_table = new JScrollPane();
		splitPane.setRightComponent(scrollPane_table);
		
		table = new JTable();
		
		final JPopupMenu tablePopupMenu = new JPopupMenu();
		addPopup(scrollPane_table, tablePopupMenu);
		
		JMenuItem mntmMove_1 = new JMenuItem("Move...");
		mntmMove_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainMethods.moveChannel();
			}
		});
		
		JMenuItem mntmGetInfo = new JMenuItem("Get Info...");
		mntmGetInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainMethods.getChannelInfo();
			}
		});
		tablePopupMenu.add(mntmGetInfo);
		
		JSeparator separator_4 = new JSeparator();
		tablePopupMenu.add(separator_4);
		tablePopupMenu.add(mntmMove_1);
		
		JMenuItem mntmRemove = new JMenuItem("Remove...");
		mntmRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainMethods.removeChannel();
			}
		});
		tablePopupMenu.add(mntmRemove);

		
		table.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				int row = table.rowAtPoint(e.getPoint());
				if (row >= 0 && row < table.getRowCount()) {
					table.setRowSelectionInterval(row, row);
				} else {
					table.clearSelection();
				}
				
				int rowIndex = table.getSelectedRow();
				if (e.isPopupTrigger() && e.getComponent() instanceof JTable && rowIndex >= 0) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				tablePopupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		});

		
		scrollPane_table.setViewportView(table);
		table.setModel(new DefaultTableModel(
			null,
			new String[] {
				"#", "Name"
			}
		));
		
		JLabel lblStatus = new JLabel("Ready.");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 1;
		frmChannelEditor.getContentPane().add(lblStatus, gbc_lblNewLabel);
		Main.statusBar = lblStatus;
		
		JProgressBar progressBar = new JProgressBar();
		Main.progressBar = progressBar;
		GridBagConstraints gbc_progressBar = new GridBagConstraints();
		gbc_progressBar.fill = GridBagConstraints.HORIZONTAL;
		gbc_progressBar.insets = new Insets(0, 0, 5, 0);
		gbc_progressBar.gridx = 1;
		gbc_progressBar.gridy = 1;
		frmChannelEditor.getContentPane().add(progressBar, gbc_progressBar);
		
		JSeparator separator = new JSeparator();
		GridBagConstraints gbc_separator = new GridBagConstraints();
		gbc_separator.gridwidth = 2;
		gbc_separator.gridx = 0;
		gbc_separator.gridy = 2;
		frmChannelEditor.getContentPane().add(separator, gbc_separator);
		
		JMenuBar menuBar = new JMenuBar();
		frmChannelEditor.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmNew = new JMenuItem("New");
		mntmNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainMethods.fileNew();
			}
		});
		mntmNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
		mnFile.add(mntmNew);
		
		JMenuItem mntmOpen = new JMenuItem("Open...");
		mntmOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		mntmOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainMethods.fileOpen();
			}
		});
		
		JMenuItem mntmSave = new JMenuItem("Save");
		mntmSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainMethods.fileSave();
			}
		});
		mnFile.add(mntmOpen);
		mnFile.add(mntmSave);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainMethods.fileExit();
			}
		});
		
		JMenu mnSaveAs = new JMenu("Save As");
		mnFile.add(mnSaveAs);
		
		JMenuItem mntmOriginalType = new JMenuItem("Original Type");
		mntmOriginalType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainMethods.fileSaveAsAuto();
			}
		});
		mnSaveAs.add(mntmOriginalType);
		
		JSeparator separator_2 = new JSeparator();
		mnSaveAs.add(separator_2);
		
		JMenuItem mntmChannelFile = new JMenuItem("Channel File");
		mntmChannelFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainMethods.fileSaveAsFile();
			}
		});
		mnSaveAs.add(mntmChannelFile);
		
		JMenuItem mntmScmBundle = new JMenuItem("SCM Bundle");
		mntmScmBundle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainMethods.fileSaveAsSCM();
			}
		});
		mnSaveAs.add(mntmScmBundle);
		
		JSeparator separator_1 = new JSeparator();
		mnFile.add(separator_1);
		mnFile.add(mntmExit);
		
		JMenu mnList = new JMenu("List");
		menuBar.add(mnList);
		
		JMenuItem mntmReparse = new JMenuItem("Reparse");
		mntmReparse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainMethods.listReparse();
			}
		});
		mnList.add(mntmReparse);
		
		JSeparator separator_3 = new JSeparator();
		mnList.add(separator_3);
		
		JMenuItem mntmMerge = new JMenuItem("Merge...");
		mntmMerge.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainMethods.listMerge();
			}
		});
		mnList.add(mntmMerge);
	}
	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
}
