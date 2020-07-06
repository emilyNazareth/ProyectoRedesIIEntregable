package serverftp;

import Domain.User;
import java.net.*;
import java.io.*;
import java.util.ArrayList;

/**
 *
 * @author Emily && David
 */
public class TransferFile extends Thread {

    private Socket clientSoc;

    private DataInputStream din;
    private DataOutputStream dout;
    private CheckingFiles checking;

    private String userName;

    public TransferFile(Socket soc) {
        try {
            clientSoc = soc;
            din = new DataInputStream(clientSoc.getInputStream());
            dout = new DataOutputStream(clientSoc.getOutputStream());
            System.out.println("FTP Client Connected ...");

            if (clientLogin()) {
                checking = new CheckingFiles(userName, clientSoc);
                checking.start();
            } else {
                clientSoc.close();
            }
        } catch (IOException e) {

        }
    }

    /**
     *
     * @return verify login
     */
    public boolean verifyUser(String name, String password) {
        FileInputStream fileInput = null;
        ObjectInputStream objectInput = null;
        ArrayList<User> usersList = new ArrayList<User>();
        try {

            fileInput = new FileInputStream("user.dat");
            objectInput = new ObjectInputStream(fileInput);

            usersList = (ArrayList) objectInput.readObject();

            for (User user : usersList) {
                if (user.getName().equals(name) && user.getPassword().equals(password)) {
                    fileInput.close();
                    objectInput.close();
                    return true;
                }
            }

        } catch (Exception e) {

            System.out.println(e.getMessage());
        } finally {
            try {
                if (fileInput != null) {
                    fileInput.close();
                }
                if (objectInput != null) {
                    objectInput.close();
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        return false;
    }

    /**
     *
     * @return client login
     */
    private boolean clientLogin() throws IOException {
        String command;
        String name;
        String password;

        command = din.readUTF();

        if (command.compareTo("verify") == 0) {
            name = din.readUTF();
            password = din.readUTF();
            if (verifyUser(name, password)) {
                dout.writeUTF("Exists");
                userName = name;
                return true;
            } else {
                dout.writeUTF("No Exists");
            }
        }
        return false;
    }
}
