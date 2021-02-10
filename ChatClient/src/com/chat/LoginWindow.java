package com.chat;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class LoginWindow extends JFrame {
    private final ChatClient client;
    JTextField loginField = new JTextField();
    JPasswordField passwordField = new JPasswordField();
    JButton loginButton = new JButton("Login");

    public LoginWindow() {
        super("Login");

        this.client = new ChatClient("localhost", 8818);
        client.connect();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(loginField);
        p.add(passwordField);
        p.add(loginButton);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doLogin();
            }
        });

        getContentPane().add(p, BorderLayout.CENTER);

        pack();

        setVisible(true);
    }

    private void doLogin() {
        String login = loginField.getText();
        String password = passwordField.getText();

        try {
            if (client.login(login, password)) {

                UserListPane userListPane = new UserListPane(client);
                JFrame frame = new JFrame("<====="+login+"=====>"+"My Chats");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(400, 600);
                JButton button1=new JButton("logout");
                JLabel topic=new JLabel(" add topic");
                JLabel labelLeave=new JLabel("leave topic");
                JTextField addTopic=new JTextField();
                JTextField leaveTopic=new JTextField();
                button1.setMinimumSize(new Dimension(50,50));
                button1.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            client.logoff();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                });
                leaveTopic.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            client.leave(leaveTopic.getText());
                            leaveTopic.setText("");
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                });
                addTopic.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            client.addTopic(addTopic.getText());
                            addTopic.setText("");
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                });
                JPanel panel =new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                panel.setBorder(new EmptyBorder(new Insets(10,0,450,0)));
                panel.setBackground(Color.ORANGE);
                panel.add(button1);
                panel.add(topic);
                panel.add(addTopic);
                panel.add(labelLeave);
                panel.add(leaveTopic);
                panel.add(leaveTopic);
                frame.getContentPane().add(panel,BorderLayout.EAST);
                frame.getContentPane().add(userListPane, BorderLayout.CENTER);

                frame.setVisible(true);

                setVisible(false);
            } else {
                // show error message
                JOptionPane.showMessageDialog(this, "Invalid login/password.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        LoginWindow loginWin = new LoginWindow();
        loginWin.setVisible(true);
    }
}
