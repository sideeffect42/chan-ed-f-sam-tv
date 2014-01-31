package ch.maniswebdesign.chaned.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Font;
import java.awt.Window;

import javax.swing.JCheckBox;
import javax.swing.UIManager;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ListMerge extends ListMergeMethods {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	
	private JTextField tfFileToMerge;
	private JButton btnChoose;
	private JTextField tfOldList;

	public JCheckBox chkbxChanged;
	public JCheckBox chkbxMissing;
	public JCheckBox chkbxAdded;
	
	private JButton btnMerge;
	private JLabel lblMergeStatus;
	
	private JButton btnSaveNew;
	private JButton btnSave;
	
	/**
	 * Create the frame.
	 */
	public ListMerge() {
		this.setResizable(false);
		
		setTitle("List Merge");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 439, 359);
		contentPane = new JPanel();
		contentPane.setBackground(UIManager.getColor("control"));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{25, 78, 63, 44, 80, 19, 61, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 29, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JLabel label_3 = new JLabel("0");
		label_3.setFont(new Font("Lucida Grande", Font.PLAIN, 18));
		GridBagConstraints gbc_label_3 = new GridBagConstraints();
		gbc_label_3.anchor = GridBagConstraints.EAST;
		gbc_label_3.insets = new Insets(0, 0, 5, 5);
		gbc_label_3.gridx = 0;
		gbc_label_3.gridy = 0;
		contentPane.add(label_3, gbc_label_3);
		
		JLabel lblThisFileWill = new JLabel("This file will be merged with your new file:");
		GridBagConstraints gbc_lblThisFileWill = new GridBagConstraints();
		gbc_lblThisFileWill.gridwidth = 4;
		gbc_lblThisFileWill.anchor = GridBagConstraints.WEST;
		gbc_lblThisFileWill.insets = new Insets(0, 0, 5, 5);
		gbc_lblThisFileWill.gridx = 1;
		gbc_lblThisFileWill.gridy = 0;
		contentPane.add(lblThisFileWill, gbc_lblThisFileWill);
		
		JSeparator separator_3 = new JSeparator();
		GridBagConstraints gbc_separator_3 = new GridBagConstraints();
		gbc_separator_3.gridwidth = 2;
		gbc_separator_3.fill = GridBagConstraints.HORIZONTAL;
		gbc_separator_3.insets = new Insets(0, 0, 5, 0);
		gbc_separator_3.gridx = 5;
		gbc_separator_3.gridy = 0;
		contentPane.add(separator_3, gbc_separator_3);
		
		tfOldList = new JTextField();
		tfOldList.setEditable(false);
		GridBagConstraints gbc_tfOldList = new GridBagConstraints();
		gbc_tfOldList.gridwidth = 6;
		gbc_tfOldList.insets = new Insets(0, 0, 5, 0);
		gbc_tfOldList.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfOldList.gridx = 1;
		gbc_tfOldList.gridy = 1;
		contentPane.add(tfOldList, gbc_tfOldList);
		tfOldList.setColumns(10);
		
		
		JLabel label = new JLabel("1");
		label.setFont(new Font("Lucida Grande", Font.PLAIN, 18));
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.anchor = GridBagConstraints.EAST;
		gbc_label.insets = new Insets(0, 0, 5, 5);
		gbc_label.gridx = 0;
		gbc_label.gridy = 2;
		contentPane.add(label, gbc_label);
		
		JLabel lblChooseTheFile = new JLabel("Choose the file to merge with");
		GridBagConstraints gbc_lblChooseTheFile = new GridBagConstraints();
		gbc_lblChooseTheFile.gridwidth = 3;
		gbc_lblChooseTheFile.insets = new Insets(0, 0, 5, 5);
		gbc_lblChooseTheFile.anchor = GridBagConstraints.WEST;
		gbc_lblChooseTheFile.gridx = 1;
		gbc_lblChooseTheFile.gridy = 2;
		contentPane.add(lblChooseTheFile, gbc_lblChooseTheFile);
		
		JSeparator separator = new JSeparator();
		GridBagConstraints gbc_separator = new GridBagConstraints();
		gbc_separator.gridwidth = 3;
		gbc_separator.fill = GridBagConstraints.HORIZONTAL;
		gbc_separator.insets = new Insets(0, 0, 5, 0);
		gbc_separator.gridx = 4;
		gbc_separator.gridy = 2;
		contentPane.add(separator, gbc_separator);
		
		tfFileToMerge = new JTextField();
		tfFileToMerge.setEditable(false);
		tfFileToMerge.setEnabled(false);
		GridBagConstraints gbc_tfFileToMerge = new GridBagConstraints();
		gbc_tfFileToMerge.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfFileToMerge.gridwidth = 5;
		gbc_tfFileToMerge.anchor = GridBagConstraints.NORTH;
		gbc_tfFileToMerge.insets = new Insets(0, 0, 5, 5);
		gbc_tfFileToMerge.gridx = 1;
		gbc_tfFileToMerge.gridy = 3;
		contentPane.add(tfFileToMerge, gbc_tfFileToMerge);
		tfFileToMerge.setColumns(10);
		
		JButton btnChoose = new JButton("Choose");
		btnChoose.setEnabled(false);
		btnChoose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chooseFileToMerge();
			}
		});
		GridBagConstraints gbc_btnChoose = new GridBagConstraints();
		gbc_btnChoose.insets = new Insets(0, 0, 5, 0);
		gbc_btnChoose.anchor = GridBagConstraints.NORTH;
		gbc_btnChoose.gridx = 6;
		gbc_btnChoose.gridy = 3;
		contentPane.add(btnChoose, gbc_btnChoose);
		this.btnChoose = btnChoose;
		
		JLabel label_1 = new JLabel("2");
		label_1.setFont(new Font("Lucida Grande", Font.PLAIN, 18));
		GridBagConstraints gbc_label_1 = new GridBagConstraints();
		gbc_label_1.anchor = GridBagConstraints.EAST;
		gbc_label_1.insets = new Insets(0, 0, 5, 5);
		gbc_label_1.gridx = 0;
		gbc_label_1.gridy = 4;
		contentPane.add(label_1, gbc_label_1);
		
		JLabel lblChooseWhatTo = new JLabel("Choose what to merge");
		GridBagConstraints gbc_lblChooseWhatTo = new GridBagConstraints();
		gbc_lblChooseWhatTo.gridwidth = 2;
		gbc_lblChooseWhatTo.anchor = GridBagConstraints.WEST;
		gbc_lblChooseWhatTo.insets = new Insets(0, 0, 5, 5);
		gbc_lblChooseWhatTo.gridx = 1;
		gbc_lblChooseWhatTo.gridy = 4;
		contentPane.add(lblChooseWhatTo, gbc_lblChooseWhatTo);
		
		JSeparator separator_1 = new JSeparator();
		GridBagConstraints gbc_separator_1 = new GridBagConstraints();
		gbc_separator_1.insets = new Insets(0, 0, 5, 0);
		gbc_separator_1.gridwidth = 4;
		gbc_separator_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_separator_1.gridx = 3;
		gbc_separator_1.gridy = 4;
		contentPane.add(separator_1, gbc_separator_1);
		
		JCheckBox chckbxChangedProperties = new JCheckBox("Changed Properties");
		chckbxChangedProperties.setEnabled(false);
		chckbxChangedProperties.setSelected(true);
		GridBagConstraints gbc_chckbxChangedProperties = new GridBagConstraints();
		gbc_chckbxChangedProperties.insets = new Insets(0, 0, 5, 0);
		gbc_chckbxChangedProperties.gridwidth = 6;
		gbc_chckbxChangedProperties.anchor = GridBagConstraints.WEST;
		gbc_chckbxChangedProperties.gridx = 1;
		gbc_chckbxChangedProperties.gridy = 5;
		contentPane.add(chckbxChangedProperties, gbc_chckbxChangedProperties);
		this.chkbxChanged = chckbxChangedProperties;
		
		JCheckBox chckbxRemoveMissingChannels = new JCheckBox("Remove missing channels");
		chckbxRemoveMissingChannels.setEnabled(false);
		chckbxRemoveMissingChannels.setSelected(true);
		GridBagConstraints gbc_chckbxRemoveMissingChannels = new GridBagConstraints();
		gbc_chckbxRemoveMissingChannels.insets = new Insets(0, 0, 5, 0);
		gbc_chckbxRemoveMissingChannels.anchor = GridBagConstraints.WEST;
		gbc_chckbxRemoveMissingChannels.gridwidth = 6;
		gbc_chckbxRemoveMissingChannels.gridx = 1;
		gbc_chckbxRemoveMissingChannels.gridy = 6;
		contentPane.add(chckbxRemoveMissingChannels, gbc_chckbxRemoveMissingChannels);
		this.chkbxMissing = chckbxRemoveMissingChannels;
		
		JCheckBox chckbxAddNewChannels = new JCheckBox("Add new channels");
		chckbxAddNewChannels.setEnabled(false);
		chckbxAddNewChannels.setSelected(true);
		GridBagConstraints gbc_chckbxAddNewChannels = new GridBagConstraints();
		gbc_chckbxAddNewChannels.insets = new Insets(0, 0, 5, 0);
		gbc_chckbxAddNewChannels.anchor = GridBagConstraints.WEST;
		gbc_chckbxAddNewChannels.gridwidth = 6;
		gbc_chckbxAddNewChannels.gridx = 1;
		gbc_chckbxAddNewChannels.gridy = 7;
		contentPane.add(chckbxAddNewChannels, gbc_chckbxAddNewChannels);
		this.chkbxAdded = chckbxAddNewChannels;
		
		JLabel label_2 = new JLabel("3");
		label_2.setFont(new Font("Lucida Grande", Font.PLAIN, 18));
		GridBagConstraints gbc_label_2 = new GridBagConstraints();
		gbc_label_2.anchor = GridBagConstraints.EAST;
		gbc_label_2.insets = new Insets(0, 0, 5, 5);
		gbc_label_2.gridx = 0;
		gbc_label_2.gridy = 8;
		contentPane.add(label_2, gbc_label_2);
		
		JLabel lblMergeLists = new JLabel("Merge lists");
		GridBagConstraints gbc_lblMergeLists = new GridBagConstraints();
		gbc_lblMergeLists.anchor = GridBagConstraints.WEST;
		gbc_lblMergeLists.insets = new Insets(0, 0, 5, 5);
		gbc_lblMergeLists.gridx = 1;
		gbc_lblMergeLists.gridy = 8;
		contentPane.add(lblMergeLists, gbc_lblMergeLists);
		
		JSeparator separator_2 = new JSeparator();
		GridBagConstraints gbc_separator_2 = new GridBagConstraints();
		gbc_separator_2.insets = new Insets(0, 0, 5, 0);
		gbc_separator_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_separator_2.gridwidth = 5;
		gbc_separator_2.gridx = 2;
		gbc_separator_2.gridy = 8;
		contentPane.add(separator_2, gbc_separator_2);
		
		JButton btnMerge = new JButton("Merge");
		btnMerge.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mergeLists(chkbxChanged.isSelected(), chkbxMissing.isSelected(), chkbxAdded.isSelected());
			}
		});
		btnMerge.setEnabled(false);
		GridBagConstraints gbc_btnMerge = new GridBagConstraints();
		gbc_btnMerge.insets = new Insets(0, 0, 5, 5);
		gbc_btnMerge.gridx = 1;
		gbc_btnMerge.gridy = 9;
		contentPane.add(btnMerge, gbc_btnMerge);
		this.btnMerge = btnMerge;
		
		JLabel lblMergeStatus = new JLabel("Status");
		GridBagConstraints gbc_lblMergeStatus = new GridBagConstraints();
		gbc_lblMergeStatus.gridwidth = 5;
		gbc_lblMergeStatus.anchor = GridBagConstraints.WEST;
		gbc_lblMergeStatus.insets = new Insets(0, 0, 5, 0);
		gbc_lblMergeStatus.gridx = 2;
		gbc_lblMergeStatus.gridy = 9;
		contentPane.add(lblMergeStatus, gbc_lblMergeStatus);
		this.lblMergeStatus = lblMergeStatus;
		
		JButton btnSaveNew = new JButton("Save Into New List");
		btnSaveNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveToNew();
			}
		});
		btnSaveNew.setEnabled(false);
		GridBagConstraints gbc_btnSaveNew = new GridBagConstraints();
		gbc_btnSaveNew.anchor = GridBagConstraints.EAST;
		gbc_btnSaveNew.gridwidth = 3;
		gbc_btnSaveNew.insets = new Insets(0, 0, 0, 5);
		gbc_btnSaveNew.gridx = 3;
		gbc_btnSaveNew.gridy = 10;
		contentPane.add(btnSaveNew, gbc_btnSaveNew);
		this.btnSaveNew = btnSaveNew;
		
		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveList();
			}
		});
		btnSave.setEnabled(false);
		GridBagConstraints gbc_btnSave = new GridBagConstraints();
		gbc_btnSave.gridx = 6;
		gbc_btnSave.gridy = 10;
		contentPane.add(btnSave, gbc_btnSave);
		this.btnSave = btnSave;
		
		fillInBaseData();
	}

	@Override
	protected void setStep0Text(String text) {
		this.tfOldList.setText(text);
	}
	
	@Override
	protected void setTfFileToMerge(String text) {
		this.tfFileToMerge.setText(text);
	}
	
	@Override
	protected void setEnabledStep1(boolean value) {
		this.tfFileToMerge.setEnabled(value);
		this.btnChoose.setEnabled(value);
	}

	@Override
	protected void setEnabledStep2(boolean value) {
		this.chkbxChanged.setEnabled(value);
		this.chkbxMissing.setEnabled(value);
		this.chkbxAdded.setEnabled(value);
	}
	@Override
	protected void setEnabledStep3(boolean value) {
		this.btnMerge.setEnabled(value);
	}

	@Override
	protected void setMergeStatus(final String text) {
		if (this.btnMerge.isEnabled()) {
			/*SwingUtilities.invokeLater(new Runnable() {
				public void run() {*/
					lblMergeStatus.setText(text);
				/*}
			});*/
		}
	}

	@Override
	protected void enableSaveButtons(boolean value) {
		this.btnSaveNew.setEnabled(value);
		this.btnSave.setEnabled(value);
	}

	@Override
	protected void closeWindow() {
		System.out.println("Disposing window");
		this.dispose();
	}
}
