package ChatServer;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
public class Server extends Thread{
    private final int serverPort;
    private final ArrayList<ServerWorker> workerList = new ArrayList<>();

    public Server(final int serverPort) {
        this.serverPort = serverPort;
    }

    public List<ServerWorker> getWorkerList(){
        return workerList;
    }
    @Override
    public void run(){
        try {
            final ServerSocket serverSocket = new ServerSocket(serverPort);
            while (true) {
                System.out.println("About to accept client connection ...");
                final Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted connection from: " + clientSocket);
                final ServerWorker worker = new ServerWorker(this,clientSocket);
                workerList.add(worker);
                worker.start();
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}