package serverApp;

import java.io.IOException;
import java.io.InputStreamReader;

import java.io.PrintWriter;

import java.net.ServerSocket;
import java.net.Socket;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class ServerCommunication extends Thread{
    private ServerSocket serverSocket = null;
    private Socket clientSocket = null;
    private Scanner in = null;
    private PrintWriter out = null;

    private int _socketPort;
    
    /**
     * Server Communication communicates Android clients by processing device signals
     * SC also sends information to the devices
     */
    public ServerCommunication(int port) throws IOException{
        _socketPort = port;
    }
    
    /**
     * Begin running server-side client
     */
    public void run(){
        System.out.println(_socketPort + " port");
        try {
            serverSocket = new ServerSocket(_socketPort);
        } catch (IOException e) {
            System.err.println("Could not listen on port:" + _socketPort);
            System.exit(1);
        }
        InputStreamReader isr = null;

        while(true){
            try {
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
            //Setting up io
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                isr = new InputStreamReader(clientSocket.getInputStream());
                in = new Scanner(new InputStreamReader(clientSocket.getInputStream()));
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                System.out.println("Wtf");
                e1.printStackTrace();
                
            }
            String inputLine, outputLine = "";
            
            
            System.out.println("Listening...");
            try{
                while(true){
                    if(isr.ready()){
                        System.out.println("Found something 1");
                        inputLine = in.nextLine();
                        if(inputLine.equals("RESTART_CONNECTION")){
                            break;
                        }
                        outputLine = CommandHandler.processInput(inputLine);
                        System.out.println("Trying to send: Received " + inputLine);
                        //out.println("Server says Received " + inputLine);
                    }
                    if(!outputLine.equals("")){
                        System.out.println("Sending to client: " + outputLine);
                        out.println(outputLine);
                        outputLine = "";
                    }
                }
            }
            catch(NoSuchElementException | IOException e3){
                continue;
            }
            System.out.println("Terminating... (Server)");
            
            out.close();
            in.close();
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            
            System.out.println("Terminated (Server) - Restarting...");
        }

    }
    

    
    public static void main(String[] args) throws IOException{
        Thread server = new Thread(new ServerCommunication(4444));
        server.start();
        Thread server1 = new Thread(new ServerCommunication(4445));
        server1.start();
        Thread serverUserInput = new Thread(new UserInput());
        serverUserInput.start();
        
        
    }
}
