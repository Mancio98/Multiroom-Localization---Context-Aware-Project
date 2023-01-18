package server;


import com.google.gson.Gson;

import server.database.DatabaseManager;
import server.messages.localization.MessageScanResult;
import server.messages.localization.MessageStartScan;
import server.messages.connection.MessageConnection;
import server.messages.connection.MessageConnectionBack;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;


public class SocketHandler extends Thread {
    private final Socket clientSocket;
    private final DatabaseManager dbm;
    //private final SpeakerManager speakerManager;
    private boolean isRunning;
    
    public SocketHandler(Socket clientSocket, DatabaseManager dbm) {//, SpeakerManager speakerManager) {
        this.clientSocket = clientSocket;
        this.dbm = dbm;
        //this.speakerManager = speakerManager;
    }
    
    @Override
    public void run() {
    	isRunning = true;
        DataInputStream dataIn = null;
        DataOutputStream dataOut = null;
        Gson gson = new Gson();
        
        if(clientSocket == null)
            return;
        
        String clientId;
        try {
        	dataIn = new DataInputStream(clientSocket.getInputStream());
            dataOut = new DataOutputStream(clientSocket.getOutputStream());
            
            String json = dataIn.readUTF();
            MessageConnection connection = gson.fromJson(json, MessageConnection.class);
            clientId = connection.getId();
            if (clientId == null) {
                System.out.println("Client is null");
                dataOut.writeUTF(gson.toJson(new MessageConnectionBack(clientId, false)));
                dataOut.close();
                return;
            }

            dataOut.writeUTF(gson.toJson(new MessageConnectionBack(clientId, true)));
        } 
        catch(IOException e) {
            e.printStackTrace();
            return;
        }
        
        System.out.println("START SERVING: " + clientId);

        
        while (isRunning) {
            try {
                // Find a change in the start/stop state
                //send start to the client
                dataOut.writeUTF(gson.toJson(new MessageStartScan(true)));
                dataOut.flush();
                MessageScanResult resultMessage = gson.fromJson(dataIn.readUTF(), MessageScanResult.class);
                //System.out.println(resultMessage.toJson());
            }
            catch (SocketException e) {
                e.printStackTrace();
                System.out.println("Connection closed");
                isRunning = false;
            }
            catch (EOFException e) {
            	e.printStackTrace();
                System.out.println("EOF");
                isRunning = false;
            }
            catch (IOException e) {
            	e.printStackTrace();
                System.err.println("Error while reading from the socket");
                isRunning = false;
            }
        }
        
        
        System.out.println("STOP SERVING: " + clientId);
    }
    
    public void stopSocketHandler() {
		isRunning = false;
		this.interrupt();
	}
}
