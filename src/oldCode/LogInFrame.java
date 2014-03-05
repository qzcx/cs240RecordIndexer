package oldCode;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import shared.comm.GetProjects_Results;
import shared.comm.LogIn_Params;
import client.clientComm.ClientComm;

public class LogInFrame extends JFrame {

	ClientComm comm;
	private final JTextField _hostTextField;
	private final JTextField _portTextField;
	private final JTextField _userTextField;
	private final JTextField _passwordTextField;
	private final JLabel projectLabel;

	private final String HOST_DEFAULT = "localhost";
	private final String PORT_DEFAULT = "2000";
	private final String USER_DEFAULT = "sheila";
	private final String PASSWORD_DEFAULT = "parker";
	
	
	public LogInFrame() {
		final JPanel mainPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		setTitle("Log in");
		setSize(400, 200);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		//Host label 
		c.weightx = 0.5;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		mainPanel.add(new JLabel("HOST:"),c);

		//Host Text Field
		this._hostTextField = new JTextField(20);
		_hostTextField.setText(HOST_DEFAULT);
		this._hostTextField.setMinimumSize(this._hostTextField.getPreferredSize());
		
		c.gridwidth = 3;
		c.gridx = 1;
		mainPanel.add(this._hostTextField,c);
		
		//Port Label
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 1;
		mainPanel.add(new JLabel("PORT:"),c);

		this._portTextField = new JTextField(10);
		_portTextField.setText(PORT_DEFAULT);
		this._portTextField.setMinimumSize(this._portTextField.getPreferredSize());
		
		c.gridwidth = 3;
		c.gridx = 1;
		mainPanel.add(this._portTextField,c);

		//User name Label
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 2;
		mainPanel.add(new JLabel("USERNAME:"),c);

		//User Text Field
		this._userTextField = new JTextField(15);
		_userTextField.setText(USER_DEFAULT);
		this._userTextField.setMinimumSize(this._userTextField.getPreferredSize());
		c.gridwidth = 3;
		c.gridx = 1;
		mainPanel.add(this._userTextField,c);

		//Password Label
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 3;
		mainPanel.add(new JLabel("PASSWORD:"),c);

		//Password Text Field
		this._passwordTextField = new JTextField(15);
		_passwordTextField.setText(PASSWORD_DEFAULT);
		this._passwordTextField.setMinimumSize(this._passwordTextField.getPreferredSize());
		c.gridwidth = 3;
		c.gridx = 1;
		mainPanel.add(this._passwordTextField , c);
		
		//Connect Button
		final JButton connectButton = new JButton("Connect");
		connectButton.setBounds(50, 60, 80, 30);

		connectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					final int portNum = Integer
							.parseInt(LogInFrame.this._portTextField.getText());
					LogInFrame.this.comm = new ClientComm(
							LogInFrame.this._hostTextField.getText(), portNum);
					final GetProjects_Results ret = LogInFrame.this.comm
							.getProjects(new LogIn_Params(
									LogInFrame.this._userTextField.getText(),
									LogInFrame.this._passwordTextField
											.getText()));
					LogInFrame.this.projectLabel.setText(ret.toString());
				} catch (final NumberFormatException e) {
					System.out.println("Illegal port num given");
				}
			}
		});

		
		c.gridwidth = 1;
		c.gridx = 1;
		c.gridy = 4;
		mainPanel.add(connectButton,c);

		this.projectLabel = new JLabel();

		mainPanel.add(this.projectLabel);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		add(mainPanel);

		add(mainPanel);

		// this.add(javax.swing.)
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				final LogInFrame ex = new LogInFrame();
				ex.setVisible(true);
			}
		});
	}

}
