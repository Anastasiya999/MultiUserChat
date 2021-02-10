package com.chat;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;

public class ServerThread extends Thread {

    private final Socket clientSocket;
    private final Server server;
    private String login = null;
    private OutputStream outputStream;
    private  ObjectOutputStream out;
    private HashSet<String> topicSet = new HashSet<>();
    private ObjectInputStream in;

    public ServerThread(Server server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            handleClientSocket();

        } catch (IOException | InterruptedException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void handleClientSocket() throws IOException, InterruptedException, ClassNotFoundException {
        InputStream inputStream = clientSocket.getInputStream();
        ObjectInputStream in=new ObjectInputStream(inputStream);
        this.outputStream = clientSocket.getOutputStream();
        this.out=new ObjectOutputStream(outputStream);

        String line;

        while (true) {

            Message message=(Message) in.readObject();
            line=message.toString();
            String[] tokens = StringUtils.split(line);
            if (tokens != null && tokens.length > 0) {
                String cmd = tokens[0];
                if ("logoff".equals(cmd) || "quit".equalsIgnoreCase(cmd)) {
                    handleLogoff();
                    break;
                } else if ("login".equalsIgnoreCase(cmd)) {
                    handleLogin(out, tokens);
                } else if ("msg".equalsIgnoreCase(cmd)) {
                    String[] tokensMsg = StringUtils.split(line, null, 3);
                    handleMessage(tokensMsg);
                } else if ("join".equalsIgnoreCase(cmd)) {
                    handleJoin(tokens);
                } else if ("leave".equalsIgnoreCase(cmd)) {
                    handleLeave(tokens);
                } else {
                    String msg = "unknown " + cmd + "\n";
                    Message message1=new Message(msg);
                    out.writeObject(message1);
                    out.flush();
                }
            }
        }

        clientSocket.close();
    }

    private void handleLeave(String[] tokens) throws IOException {
        if (tokens.length > 1) {
            String topic = tokens[1];
            topicSet.remove(topic);
            send(new Message("leave "+topic));
        }
    }

    public boolean isMemberOfTopic(String topic) {
        return topicSet.contains(topic);
    }

    private void handleJoin(String[] tokens) throws IOException {
        if (tokens.length > 1) {
            String topic = tokens[1];
            topicSet.add(topic);
            Message msg=new Message("join "+topic);
            send(msg);
        }
    }
    public String getLogin() {
        return login;
    }

    private void handleMessage(String[] tokens) throws IOException {
        String sendTo = tokens[1];
        String body = tokens[2];
        boolean isTopic = sendTo.charAt(0) == '#';
        Message message;
        List<ServerThread> workerList = server.getWorkerList();
        for(ServerThread worker : workerList) {
            if (isTopic) {
                if (worker.isMemberOfTopic(sendTo)) {
                    String outMsg = "msg " + sendTo + " :" + login + " " + body + "\n";
                    message=new Message(outMsg);
                    worker.send(message);
                }
            } else {
                if (sendTo.equalsIgnoreCase(worker.getLogin())) {
                    String outMsg = "msg " + login + " " + body + "\n";
                    message=new Message(outMsg);
                    worker.send(message);
                }
            }
        }
    }

    private void handleLogoff() throws IOException {
        server.removeWorker(this);
        List<ServerThread> workerList = server.getWorkerList();

        // send other online users current user's status
        String onlineMsg = "offline " + login + "\n";
        Message offline=new Message(onlineMsg);
        for(ServerThread worker : workerList) {
            if (!login.equals(worker.getLogin())) {
                worker.send(offline);
            }
        }
        clientSocket.close();
    }



    private void handleLogin(OutputStream outputStream, String[] tokens) throws IOException {
        if (tokens.length == 3) {
            String login = tokens[1];
            String password = tokens[2];
                this.login = login;
                System.out.println("User logged in succesfully: " + login);

                List<ServerThread> workerList = server.getWorkerList();


                //wysyla danemu uzytkownikowi loginy innych uzytkownikow kto online
                for(ServerThread worker : workerList) {
                    if (worker.getLogin() != null) {
                        if (!login.equals(worker.getLogin())) {
                            String msg2 = "online " + worker.getLogin() + "\n";
                            Message online=new Message(msg2);
                            send(online);
                        }
                    }
                }

                //wysyla innym uzytkownikam status danego klienta
                String onlineMsg = "online " + login + "\n";
                Message onl=new Message(onlineMsg);
                for(ServerThread worker : workerList) {
                    if (!login.equals(worker.getLogin())) {
                        worker.send(onl);
                    }
                }

        }
    }

    private void send(Message msg) throws IOException {
        if (login != null) {
            try {
                out.writeObject(msg);
                out.flush();
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}
