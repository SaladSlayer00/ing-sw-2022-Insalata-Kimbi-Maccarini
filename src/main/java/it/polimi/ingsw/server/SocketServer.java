package it.polimi.ingsw.server;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.message.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Socket server is the main way the SocketClientHandler uses to communicate with the server,
 * both sending and receiving messages.
 */
public class SocketServer implements Runnable{
    private final Server server;
    private final int port;
    ServerSocket serverSocket;

    public SocketServer(Server server, int port){
        this.server = server;
        this.port = port;
    }
    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            Server.LOGGER.info(() -> "Socket server started on port " + port + ".");
        } catch (IOException e) {
            Server.LOGGER.severe("Server could not start!");
            return;
        }

        while (!Thread.currentThread().isInterrupted()) {
            try {
                Socket client = serverSocket.accept();

                client.setSoTimeout(5000);

                SocketClientHandler clientHandler = new SocketClientHandler(this, client);
                Thread thread = new Thread(clientHandler, "ss_handler" + client.getInetAddress());
                thread.start();
            } catch (IOException e) {
                Server.LOGGER.severe("Connection dropped");
            }
        }
    }

    /**
     * Handles the addition of a new client.
     *
     * @param nickname      the nickname of the new client.
     * @param clientHandler the ClientHandler of the new client.
     */
    public void addClient(String nickname, int id, ClientHandler clientHandler) throws noMoreStudentsException {
        server.addClient(nickname, id, clientHandler);
    }

    /**
     * Forwards a received message from the client to the Server.
     *
     * @param message the message to be forwarded.
     */
    public void onMessageReceived(Message message) throws emptyDecktException, noMoreStudentsException, fullTowersException, noStudentException, noTowerException, invalidNumberException, maxSizeException, noTowersException {
        server.onMessageReceived(message);
    }

    /**
     * Handles a client disconnection.
     *
     * @param clientHandler the ClientHandler of the disconnecting client.
     */
    public void onDisconnect(ClientHandler clientHandler) {
        server.onDisconnect(clientHandler);
    }
}
