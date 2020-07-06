
package cliente;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 *  @author Emily && David
 */
public class TransferCliente extends Thread {
    //Declare the variables 
    private Socket ClientSoc;

    private DataInputStream din;
    private DataOutputStream dout;
    private BufferedReader br;

    private String folder;

    public TransferCliente(Socket soc) {
        try {
            ClientSoc = soc;
            din = new DataInputStream(ClientSoc.getInputStream());
            dout = new DataOutputStream(ClientSoc.getOutputStream());
            br = new BufferedReader(new InputStreamReader(System.in));
        } catch (Exception ex) {
        }
    }

    public boolean logIn(String name, String password) throws IOException {
        dout.writeUTF("verify");
        dout.writeUTF(name);
        dout.writeUTF(password);
        if (din.readUTF().equals("Exists")) {
            return true;
        } else {
            return false;
        }
    }

    public void createFolder(String name) {
        try {
            System.out.println("Enter the path to create a directory: ");
            String desktopPath = System.getProperty("user.home") + "/Desktop";
            folder = desktopPath + "/Dropbox " + name;
            //Creating a File object
            File file = new File(folder);
            //Creating the directory
            boolean bool = file.mkdirs();

            if (bool) {
                System.out.println("Directory created successfully");
            } else {
                System.out.println("Sorry couldnt create specified directory");
            }
            this.start();

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {

        }

    }

    void SendFile() throws Exception {

        String filename;
        System.out.print("Enter File Name :");
        filename = br.readLine();

        File f = new File(filename);
        if (!f.exists()) {
            System.out.println("File not Exists...");
            dout.writeUTF("File not found");
            return;
        }

        dout.writeUTF(filename);

        String msgFromServer = din.readUTF();
        if (msgFromServer.compareTo("File Already Exists") == 0) {
            String Option;
            System.out.println("File Already Exists. Want to OverWrite (Y/N) ?");
            Option = br.readLine();
            if (Option == "Y") {
                dout.writeUTF("Y");
            } else {
                dout.writeUTF("N");
                return;
            }
        }

        System.out.println("Sending File ...");
        FileInputStream fin = new FileInputStream(f);
        int ch;
        do {
            ch = fin.read();
            dout.writeUTF(String.valueOf(ch));
        } while (ch != -1);
        fin.close();
        System.out.println(din.readUTF());

    }

    @Override
    public void run() {
        String command;
        try {
            while (true) {
                command = din.readUTF();
                if (command.equals("GetClientFileList")) {
                    for (String file : getFileList()) {
                        dout.writeUTF(file);
                    }
                    dout.writeUTF("done");
                }
                receiveFiles();
                sendFiles();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     *
     */
    private void receiveFiles() throws IOException {
        String received;

        received = din.readUTF();

        if (received.equals("updateFolder")) {
            //read first file
            received = din.readUTF();

            while (!received.equals("done")) {
                receiveFile(received);

                //read next file
                received = din.readUTF();
            }
        }
    }

    /**
     *
     * @param filename
     */
    void receiveFile(String filename) throws IOException {
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

    /**
     *
     * @param fileName
     * @param fileSize
     * @param data
     */
    private void saveFileToDisk(String fileName, int fileSize, byte[] data) {
        FileOutputStream fos;
        BufferedOutputStream bos;
        try {
            fos = new FileOutputStream(folder + "/" + fileName);
            bos = new BufferedOutputStream(fos);

            bos.write(data, 0, fileSize);
            bos.flush();

            bos.close();
            fos.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TransferCliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TransferCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     */
    private void sendFiles() throws IOException {
        String received;

        received = din.readUTF();

        if (received.equals("requestFiles")) {
            //read first file
            received = din.readUTF();

            while (!received.equals("done")) {
                System.out.println("Server request: " + received);
                sendFileToServer(received);

                //read next file
                received = din.readUTF();
            }
        }
    }

    private void sendFileToServer(String fileName) throws IOException {
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        
        File file = new File(this.folder + "/" + fileName);
        
        int fileSize = (int) file.length();
        int read = 0;
        int offset = 0;

        dout.writeUTF(String.valueOf(fileSize));

        // send file
        byte[] mybytearray = new byte[fileSize];
        fis = new FileInputStream(file);
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

    /**
     *
     * @return get files names of client
     */
    private List<String> getFileList() {
        File folder = new File(this.folder);
        File[] listOfFilesClient = folder.listFiles();
        List<String> clientList = new ArrayList();
        for (File clientFileName : listOfFilesClient) {
            clientList.add(clientFileName.getName());
        }
        return clientList;
    }
}
