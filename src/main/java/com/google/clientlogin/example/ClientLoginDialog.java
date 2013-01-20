package com.google.clientlogin.example;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import com.google.clientlogin.CaptchaRequiredException;
import com.google.clientlogin.ClientLoginException;
import com.google.clientlogin.GoogleClientLogin;

public class ClientLoginDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField textField;
	private JPasswordField passwordField;
	private JTextField captchaTextField;
	private JLabel lblCaptchaimage;
	private JLabel lblEnterKey;
	private GoogleClientLogin client = new GoogleClientLogin("ndev");

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			ClientLoginDialog dialog = new ClientLoginDialog();
			dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public ClientLoginDialog() {
		setTitle("Google ClientLogin");
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0 };
		gbl_contentPanel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
		gbl_contentPanel.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		gbl_contentPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 1.0, 0.0,
				Double.MIN_VALUE };
		contentPanel.setLayout(gbl_contentPanel);
		{
			JLabel lblUsername = new JLabel("username");
			GridBagConstraints gbc_lblUsername = new GridBagConstraints();
			gbc_lblUsername.fill = GridBagConstraints.VERTICAL;
			gbc_lblUsername.gridwidth = 6;
			gbc_lblUsername.anchor = GridBagConstraints.WEST;
			gbc_lblUsername.insets = new Insets(0, 0, 5, 5);
			gbc_lblUsername.gridx = 2;
			gbc_lblUsername.gridy = 1;
			contentPanel.add(lblUsername, gbc_lblUsername);
		}
		{
			textField = new JTextField();
			GridBagConstraints gbc_textField = new GridBagConstraints();
			gbc_textField.insets = new Insets(0, 0, 5, 0);
			gbc_textField.fill = GridBagConstraints.HORIZONTAL;
			gbc_textField.gridx = 8;
			gbc_textField.gridy = 1;
			contentPanel.add(textField, gbc_textField);
			textField.setColumns(10);
		}
		{
			JLabel lblPassword = new JLabel("password");
			GridBagConstraints gbc_lblPassword = new GridBagConstraints();
			gbc_lblPassword.anchor = GridBagConstraints.WEST;
			gbc_lblPassword.gridwidth = 6;
			gbc_lblPassword.insets = new Insets(0, 0, 5, 5);
			gbc_lblPassword.gridx = 2;
			gbc_lblPassword.gridy = 2;
			contentPanel.add(lblPassword, gbc_lblPassword);
		}
		{
			passwordField = new JPasswordField();
			GridBagConstraints gbc_passwordField = new GridBagConstraints();
			gbc_passwordField.insets = new Insets(0, 0, 5, 0);
			gbc_passwordField.fill = GridBagConstraints.HORIZONTAL;
			gbc_passwordField.gridx = 8;
			gbc_passwordField.gridy = 2;
			contentPanel.add(passwordField, gbc_passwordField);
		}
		{
			lblCaptchaimage = new JLabel("");
			GridBagConstraints gbc_lblCaptchaimage = new GridBagConstraints();
			gbc_lblCaptchaimage.insets = new Insets(0, 0, 5, 0);
			gbc_lblCaptchaimage.gridx = 8;
			gbc_lblCaptchaimage.gridy = 3;
			contentPanel.add(lblCaptchaimage, gbc_lblCaptchaimage);
		}
		{
			lblEnterKey = new JLabel("Enter key");
			lblEnterKey.setVisible(false);
			GridBagConstraints gbc_lblEnterKey = new GridBagConstraints();
			gbc_lblEnterKey.anchor = GridBagConstraints.WEST;
			gbc_lblEnterKey.gridwidth = 5;
			gbc_lblEnterKey.insets = new Insets(0, 0, 0, 5);
			gbc_lblEnterKey.gridx = 2;
			gbc_lblEnterKey.gridy = 4;
			contentPanel.add(lblEnterKey, gbc_lblEnterKey);
		}
		{
			captchaTextField = new JTextField();
			captchaTextField.setVisible(false);
			GridBagConstraints gbc_captchaTextField = new GridBagConstraints();
			gbc_captchaTextField.fill = GridBagConstraints.HORIZONTAL;
			gbc_captchaTextField.gridx = 8;
			gbc_captchaTextField.gridy = 4;
			contentPanel.add(captchaTextField, gbc_captchaTextField);
			captchaTextField.setColumns(10);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (isCaptcha()) {
							try {
								client.authenticate(textField.getText().trim(),
										passwordField.getText().trim(),
										captchaTextField.getText().trim());
								showMessage("Auth Token: \n"+client.getAuthToken());

							} catch (CaptchaRequiredException e1) {
								showMessage("Incorrect captcha key.\nPlease try again");
								showCaptcha(true);
								
							} catch (ClientLoginException e1) {
								showMessage(e1.getMessage());
							}

						} else {
							try {
								client.authenticate(textField.getText().trim(),
										passwordField.getText().trim());
								showMessage("Auth Token: \n"+client.getAuthToken());
							} catch (CaptchaRequiredException e1) {
								showCaptcha(true);
								//
							} catch (ClientLoginException e1) {
								showMessage(e1.getMessage());
							}
						}
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton captchaTestButton = new JButton("Test Captcha");
				captchaTestButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						ExecutorService executor = Executors.newFixedThreadPool(10);
						executor.execute(new ForceCaptchaRunnable());
					}
				});
				captchaTestButton.setActionCommand("Captcha");
				buttonPane.add(captchaTestButton);
			}
		}
	}

	private void showCaptcha(boolean b) {

		captchaTextField.setVisible(b);
		lblEnterKey.setVisible(b);
		if (b) {
			BufferedImage image = client.getCaptchaImage();
			lblCaptchaimage.setIcon(new ImageIcon(image));
		} else {
			lblCaptchaimage.setIcon(null);
			captchaTextField.setText("");
		}
		this.repaint();
		this.validate();

	}

	private void showMessage(String msg) {
		showCaptcha(false);
		JTextArea txt =new JTextArea(msg);
		txt.setPreferredSize(new Dimension(350, 200));
		txt.setLineWrap(true);
		txt.setEditable(false);
		JScrollPane sp= new JScrollPane(txt,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		sp.setPreferredSize(new Dimension(300, 200));
		sp.validate();
		JOptionPane.showMessageDialog(this, sp);
	}

	private boolean isCaptcha() {
		return (captchaTextField.isVisible() && lblEnterKey.isVisible());
	}
	
	private class ForceCaptchaRunnable implements Runnable{

		public void run() {
			Random r = new Random();
			boolean isCaptcha = false;
			while (!isCaptcha) {
				try {
					client.authenticate(
							textField.getText().trim(),
							passwordField.getText().trim()
									+ r.nextInt(100));
					showMessage("Auth Token: "+client.getAuthToken());
				} catch (CaptchaRequiredException e1) {
					
					isCaptcha = true;
					showCaptcha(true);
					
				} catch (ClientLoginException e1) {
					
				}
			}
			
		}
		
	}
}
