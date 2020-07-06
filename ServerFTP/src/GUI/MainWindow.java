package GUI;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import serverftp.ServerFTP;

/**
 *
 *  @author Emily && David
 */
public class MainWindow extends JFrame {

    private JPanel mainPanel = null;

    private JPanel serverPanel = null;
    private JTextField txtPort = null;
    private JButton btnStart = null;

    private JPanel createUserPanel = null;
    private JTextField txtNameUser = null;
    private JPasswordField txtPassword = null;
    private JButton btnCreate = null;

    private ServerFTP server = null;

    public MainWindow() {
        super("Server");

        this.setSize(300, 300);
        this.setContentPane(this.getMainPanel());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                e.getWindow().dispose();
            }
        });

        this.setVisible(true);
    }

    /**
     * create main panel
     *
     * @return
     */
    private JPanel getMainPanel() {
        if (this.mainPanel == null) {
            this.mainPanel = new JPanel();
            this.mainPanel.setLayout(new GridBagLayout());

            GridBagConstraints constraints = new GridBagConstraints();
            constraints.fill = GridBagConstraints.BOTH;
            constraints.insets = new Insets(10, 10, 10, 10);
            constraints.weightx = 1.0;

            constraints.gridx = 0;
            constraints.gridy = 0;
            this.mainPanel.add(getServerPanel(), constraints);

            constraints.gridx = 0;
            constraints.gridy = 1;
            this.mainPanel.add(getCreateUserPanel(), constraints);
        }
        return this.mainPanel;
    }

    /**
     * create server panel
     *
     * @return
     */
    private JPanel getServerPanel() {
        if (this.serverPanel == null) {
            this.serverPanel = new JPanel();
            this.serverPanel.setLayout(new GridBagLayout());
            this.serverPanel.setBorder(
                    BorderFactory.createTitledBorder("FTP Server Management"));

            GridBagConstraints constraints = new GridBagConstraints();
            constraints.fill = GridBagConstraints.BOTH;
            constraints.insets = new Insets(10, 10, 10, 10);
            constraints.weightx = 1.0;

            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.fill = GridBagConstraints.NONE;
            this.serverPanel.add(getBtnStart(), constraints);
        }
        return this.serverPanel;
    }

    /**
     * Create port textField
     *
     * @return
     */
    private JTextField getTxtPort() {
        if (txtPort == null) {
            txtPort = new JTextField("21");
        }
        return txtPort;
    }

    /**
     * Create Start button
     *
     * @return
     */
    private JButton getBtnStart() {
        if (btnStart == null) {
            btnStart = new JButton("Start");
            btnStart.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    int port;
                    try {
                        port = Integer.parseInt(getTxtPort().getText());
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(
                                null,
                                "Invalid port",
                                "Failure",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    try {
                        //creating instance of server and start
                        server = ServerFTP.getInstance();
                        server.start();
                        btnStart.setEnabled(false);
                        getTxtPort().setEnabled(false);
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(
                                null,
                                "Unable to start FTP server: " + e.getMessage(),
                                "Failure",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
        }
        return btnStart;
    }

    /**
     * Create user panel
     *
     * @return
     */
    private JPanel getCreateUserPanel() {
        if (this.createUserPanel == null) {
            this.createUserPanel = new JPanel();
            this.createUserPanel.setLayout(new GridBagLayout());
            this.createUserPanel.setBorder(
                    BorderFactory.createTitledBorder("Create user"));

            GridBagConstraints constraints = new GridBagConstraints();
            constraints.fill = GridBagConstraints.BOTH;
            constraints.insets = new Insets(10, 10, 10, 10);
            constraints.weightx = 0.0;

            constraints.gridx = 0;
            constraints.gridy = 0;
            this.createUserPanel.add(new JLabel("Username"), constraints);

            constraints.gridx = 0;
            constraints.gridy = 1;
            this.createUserPanel.add(new JLabel("Password"), constraints);

            constraints.gridx = 1;
            constraints.gridy = 0;
            constraints.weightx = 1.0;
            this.createUserPanel.add(getTxtNameUser(), constraints);

            constraints.gridx = 1;
            constraints.gridy = 1;
            this.createUserPanel.add(getTxtPassword(), constraints);

            constraints.gridx = 0;
            constraints.gridy = 2;
            constraints.gridwidth = 2;
            constraints.fill = GridBagConstraints.NONE;
            this.createUserPanel.add(getBtnCreate(), constraints);
        }
        return this.createUserPanel;
    }

    /**
     * Create create user button
     *
     * @return
     */
    private JButton getBtnCreate() {
        if (btnCreate == null) {
            btnCreate = new JButton("Create");
            btnCreate.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    String userName = getTxtNameUser().getText();
                    String password = getTxtPassword().getText();
                    if (userName.isEmpty() || password.isEmpty()) {
                        JOptionPane.showMessageDialog(
                                null,
                                "Username or password empty",
                                "Failure",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    //ftpServer.addUser(userName, password);
                    System.out.println(userName + " " + password);
                    getTxtNameUser().setText("");
                    getTxtPassword().setText("");
                    server.addUser(userName, password);
                }
            });
        }
        return btnCreate;
    }

    /**
     * Create name user textField
     *
     * @return
     */
    private JTextField getTxtNameUser() {
        if (txtNameUser == null) {
            txtNameUser = new JTextField();
        }
        return txtNameUser;
    }

    /**
     * Create password user textField
     *
     * @return
     */
    private JPasswordField getTxtPassword() {
        if (txtPassword == null) {
            txtPassword = new JPasswordField();
        }
        return txtPassword;
    }

}
