package ChatServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

public class ServerWorker extends Thread {
    private final Socket clientSocket;
    private final Server server;

    private String login = null;
    private OutputStream outputStream;

    public ServerWorker(final Server server, final Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            handleClientSocket();
        } catch (final Exception e) {
            e.printStackTrace();
        }

    }

    private void handleClientSocket() {
        try {
            final InputStream inputStream = clientSocket.getInputStream();
            this.outputStream = clientSocket.getOutputStream();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                final String[] tokens = line.split("\s");

                for (final String item : tokens) {
                    System.out.println(item);
                }

                if (tokens != null && tokens.length > 0) {
                    final String cmd = tokens[0];
                    if ("quit".equalsIgnoreCase(line)) {
                        handleLogoff();
                    } else if ("login".equalsIgnoreCase(cmd)) {
                        handleLogin(outputStream, tokens);
                    } else {
                        final String msg = "unknown " + cmd + "\n";
                        outputStream.write(msg.getBytes());
                    }

                }

            }
            clientSocket.close();
        } catch (final Exception e) {
            e.printStackTrace();
        }

    }

    private void handleLogoff() {
        final List<ServerWorker> workerList = server.getWorkerList();
        try {
            clientSocket.close();
            final String onlineMsg = "offline: " + login + "\n";
                    for (final ServerWorker worker : workerList) {
                        if (!login.equals(worker.getLogin())) {
                            worker.send(onlineMsg);
                        }
                    }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public String getLogin() {
        return login;
    }

    private void handleLogin(final OutputStream outputStream, final String[] tokens) {
        if (tokens.length == 3) {
            final String login = tokens[1];
            final String password = tokens[2];

            if ((login.equals("zen") && password.equals("zen"))
                    || (login.equals("yum") && password.equals("yum"))) {
                final String msg = "ok login\n";
                try {
                    outputStream.write(msg.getBytes());
                    this.login = login;
                    System.out.println("User logged in successfully: " + login);

                    final List<ServerWorker> workerList = server.getWorkerList();

                    for (final ServerWorker worker : workerList) {
                        if (worker.getLogin() != null) {
                            final String msg2 = "online: " + worker.getLogin() + "\n";
                            send(msg2);
                        }
                    }

                    final String onlineMsg = "online" + login + "\n";
                    for (final ServerWorker worker : workerList) {
                        if (!login.equals(worker.getLogin())) {
                            worker.send(onlineMsg);
                        }
                    }
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            } else {
                final String msg = "Error login\n";
                try {
                    outputStream.write(msg.getBytes());
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void send(final String onlineMsg) {
        try {
            outputStream.write(onlineMsg.getBytes());
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}