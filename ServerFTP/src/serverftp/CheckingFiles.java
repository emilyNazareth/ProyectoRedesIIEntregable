package serverftp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Emily && David
 */
public class CheckingFiles extends Thread {

    private String address;
    private String userName;

    private DataInputStream din;
    private DataOutputStream dout;
    private Socket clientSoc;

    public CheckingFiles(String userName, Socket clientSoc) {
        String desktopPath = System.getProperty("user.home") + "/Desktop";
        String folder = desktopPath + "/Dropbox " + userName;
        this.address = folder;
        this.userName = userName;
        this.clientSoc = clientSoc;
        try {
            this.din = new DataInputStream(this.clientSoc.getInputStream());
            this.dout = new DataOutputStream(this.clientSoc.getOutputStream());
        } catch (Exception e) {

        }

    }

    private void sendFileToClient(File myFile) throws IOException {
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        int fileSize = (int) myFile.length();
        int read = 0;
        int offset = 0;

        dout.writeUTF(myFile.getName());
        dout.writeUTF(String.valueOf(fileSize));

        // send file
        byte[] mybytearray = new byte[fileSize];
        fis = new FileInputStream(myFile);
        bis = new BufferedInputStream(fis);

        do {
            read += bis.read(mybytearray, offset, fileSize - read);
            offset = read;
        } while (read < fileSize);

        dout.write(mybytearray, 0, read);
        din.readUTF();
        bis.close();
        fis.close();
    }

    void receiveFileFromClient(String filename) throws IOException {
        String received;
        int fileSize;
        int read = 0;
        int offset = 0;
        byte[] fileData;

        //File size
        received = din.readUTF();
        fileSize = Integer.parseInt(received);

        System.out.println(filename + " with size " + fileSize);

        // receive file
        fileData = new byte[fileSize];

        do {
            read += din.read(fileData, offset, fileSize - read);
            offset = read;
        } while (read < fileSize);
        dout.writeUTF("thanks");

        saveFileToDisk(filename, fileSize, fileData);
    }

    private void saveFileToDisk(String fileName, int fileSize, byte[] data) {
        FileOutputStream fos;
        BufferedOutputStream bos;
        String desktopPath = System.getProperty("user.home") + "/Desktop";
        String route = desktopPath + "/NubeFTP/" + userName;
        try {
            fos = new FileOutputStream(route + "/" + fileName);
            bos = new BufferedOutputStream(fos);

            bos.write(data, 0, fileSize);
            bos.flush();

            bos.close();
            fos.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CheckingFiles.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CheckingFiles.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void folderChecking() throws IOException, Exception {
        List<String> clientList = new ArrayList();
        String response;

        dout.writeUTF("GetClientFileList");

        response = din.readUTF();
        while (!response.equals("done")) {
            clientList.add(response);
            response = din.readUTF();
        }

        //FOLDER SERVER
        String desktopPath = System.getProperty("user.home") + "/Desktop";
        String route = desktopPath + "/NubeFTP/" + userName;
        File serverFolder = new File(route);
        File[] listOfFilesServer = serverFolder.listFiles();

        //send files to client
        dout.writeUTF("updateFolder");
        for (File serverFile : listOfFilesServer) {
            if (!clientList.contains(serverFile.getName())) {
                //send to client file name
                sendFileToClient(serverFile);
            }
        }
        dout.writeUTF("done");

        dout.writeUTF("requestFiles");
        //receive files to client
        for (String clientFile : clientList) {
            boolean found = false;
            for (File serverFile : listOfFilesServer) {
                if (clientFile.equals(serverFile.getName())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                System.out.println("Requesting: " + clientFile);
                dout.writeUTF(clientFile);
                receiveFileFromClient(clientFile);
            }
        }
        dout.writeUTF("done");
    }

    //listener the server in order to detect with a file changed
    public void run() {
        try {
            while (true) {
                folderChecking();
                Thread.sleep(1000);
            }
        } catch (IOException ex) {
            Logger.getLogger(CheckingFiles.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(CheckingFiles.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
