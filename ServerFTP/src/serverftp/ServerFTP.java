package serverftp;

import Domain.User;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Emily && David
 */
public class ServerFTP extends Thread {

    private static ServerFTP instance;
    private static ServerSocket soc;

    public static ServerFTP getInstance() {
        try {
            if (instance == null) {
                instance = new ServerFTP();
            }
            //create sockect Server
            soc = new ServerSocket(4321);

        } catch (IOException ex) {
            Logger.getLogger(ServerFTP.class.getName()).log(Level.SEVERE, null, ex);
        }
        return instance;
    }

    //adding user in server
    public void addUser(String name, String password) {
        //To write
        FileOutputStream fileOutput = null;
        ObjectOutputStream objectOutput = null;

        //To read
        FileInputStream fileInput = null;
        ObjectInputStream objectInput = null;
        ArrayList<User> usersList = new ArrayList<User>();
        try {

            System.out.println("Enter the path to create a directory: ");
            String desktopPath = System.getProperty("user.home") + "/Desktop";
            String folder = desktopPath + "/NubeFTP/" + name;
            //Creating a File object
            File file = new File(folder);
            //Creating the directory
            boolean bool = file.mkdirs();
            if (bool) {
                System.out.println("Directory created successfully");
            } else {
                System.out.println("Sorry couldnt create specified directory");
            }

            file = new File("user.dat");

            if (file.exists()) {
                //read the users
                fileInput = new FileInputStream("user.dat");
                objectInput = new ObjectInputStream(fileInput);
                usersList = (ArrayList) objectInput.readObject(); // <--- los mete aquí
            }

            //write into the list
            fileOutput = new FileOutputStream("user.dat");
            objectOutput = new ObjectOutputStream(fileOutput);

            User user = new User(name, password); //create user
            usersList.add(user); // add user into the list

            objectOutput.writeObject(usersList); //save the list into file

        } catch (Exception ex) {
            Logger.getLogger(ServerFTP.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (fileOutput != null) {
                    fileOutput.close();
                }
                if (objectOutput != null) {
                    objectOutput.close();
                }
            } catch (IOException e) {
                System.out.println("3" + e.getMessage());
            }
        }
    }

    //Waiting for connection
    @Override
    public void run() {
        while (true) {
            try {
                System.out.println("Waiting for Connection ...");
                //accept connection with client
                TransferFile t = new TransferFile(soc.accept());

            } catch (IOException ex) {
                Logger.getLogger(ServerFTP.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}
