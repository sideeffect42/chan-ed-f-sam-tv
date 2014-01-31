package ch.maniswebdesign.chaned.gui;

import java.awt.BorderLayout;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;

import ch.maniswebdesign.chaned.channelmanager.Channel;

import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class GetInfo extends JDialog {

	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();
	private final Channel channel;
	
	private final JLabel lblHeaderChannelNum;
	private final JLabel lblHeaderChannelName;
	
	/**
	 * Create the dialog.
	 */
	public GetInfo(Channel channel) {
		this.channel = channel;
		
		setTitle("Get Info");
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPanel.rowHeights = new int[]{1, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		
		
		
		{
			this.lblHeaderChannelNum = new JLabel("####");
			this.lblHeaderChannelNum.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
			GridBagConstraints gbc_lblHeaderChannelNum = new GridBagConstraints();
			gbc_lblHeaderChannelNum.insets = new Insets(0, 0, 5, 5);
			gbc_lblHeaderChannelNum.fill = GridBagConstraints.BOTH;
			gbc_lblHeaderChannelNum.gridx = 0;
			gbc_lblHeaderChannelNum.gridy = 0;
			contentPanel.add(this.lblHeaderChannelNum, gbc_lblHeaderChannelNum);
		}
		{
			this.lblHeaderChannelName = new JLabel("Channel Name");
			this.lblHeaderChannelName.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
			GridBagConstraints gbc_lblHeaderChannelName = new GridBagConstraints();
			gbc_lblHeaderChannelName.fill = GridBagConstraints.HORIZONTAL;
			gbc_lblHeaderChannelName.insets = new Insets(0, 0, 5, 5);
			gbc_lblHeaderChannelName.gridwidth = 13;
			gbc_lblHeaderChannelName.anchor = GridBagConstraints.NORTH;
			gbc_lblHeaderChannelName.gridx = 1;
			gbc_lblHeaderChannelName.gridy = 0;
			contentPanel.add(this.lblHeaderChannelName, gbc_lblHeaderChannelName);
		}
		
		// Fill In Information
		this.fillData();
		
		// Show Dialog
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setVisible(true);
	}

	
	public void fillData() {
		this.setTitle((String) this.channel.getProperty("name"));
		this.lblHeaderChannelNum.setText("####\n");
		this.lblHeaderChannelName.setText("Channel");
	}
}
