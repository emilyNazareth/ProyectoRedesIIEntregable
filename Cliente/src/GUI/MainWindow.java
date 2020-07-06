package GUI;

import cliente.TransferCliente;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 *
 * @author Emily && David
 */
public class MainWindow extends JFrame {

    private JPanel mainPanel = null;

    private JPanel loginUserPanel = null;
    private JTextField txtNameUser = null;
    private JPasswordField txtPassword = null;
    private JButton btnLogIn = null;

    public MainWindow() {
        super("Client");

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
            this.mainPanel.add(getLoginUserPanel(), constraints);
        }
        return this.mainPanel;
    }

    /**
     * Create login panel
     *
     * @return
     */
    private JPanel getLoginUserPanel() {
        if (this.loginUserPanel == null) {
            this.loginUserPanel = new JPanel();
            this.loginUserPanel.setLayout(new GridBagLayout());
            this.loginUserPanel.setBorder(
                    BorderFactory.createTitledBorder("Log In"));

            GridBagConstraints constraints = new GridBagConstraints();
            constraints.fill = GridBagConstraints.BOTH;
            constraints.insets = new Insets(10, 10, 10, 10);
            constraints.weightx = 0.0;

            constraints.gridx = 0;
            constraints.gridy = 0;
            this.loginUserPanel.add(new JLabel("Username"), constraints);

            constraints.gridx = 0;
            constraints.gridy = 1;
            this.loginUserPanel.add(new JLabel("Password"), constraints);

            constraints.gridx = 1;
            constraints.gridy = 0;
            constraints.weightx = 1.0;
            this.loginUserPanel.add(getTxtNameUser(), constraints);

            constraints.gridx = 1;
            constraints.gridy = 1;
            this.loginUserPanel.add(getTxtPassword(), constraints);

            constraints.gridx = 0;
            constraints.gridy = 2;
            constraints.gridwidth = 2;
            constraints.fill = GridBagConstraints.NONE;
            this.loginUserPanel.add(getBtnLogin(), constraints);
        }
        return this.loginUserPanel;
    }

    /**
     * Create create login button
     *
     * @return
     */
    private JButton getBtnLogin() {
        if (btnLogIn == null) {
            btnLogIn = new JButton("Log In");
            btnLogIn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    try {
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

                        //Connection wirh the server
                        Socket soc = new Socket("127.0.0.1", 4321);
                        TransferCliente t = new TransferCliente(soc);
                        //verify and create user folder
                         if (!t.logIn(userName, password)) {
                            JOptionPane.showMessageDialog(
                                    null,
                                    "The user not exists",
                                    "Failure",
                                    JOptionPane.ERROR_MESSAGE);
                            return;
                        } else {
                            t.createFolder(userName);
                        }

                        System.out.println(userName + " " + password);
                        getTxtNameUser().setEnabled(false);
                        getTxtPassword().setEnabled(false);
                        btnLogIn.setEnabled(false);

                    } catch (IOException ex) {
                        Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (Exception ex) {
                        Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            });
        }
        return btnLogIn;
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
