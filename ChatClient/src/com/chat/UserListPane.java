package com.chat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;

public class UserListPane extends JPanel implements UserStatusListener {


    private final ChatClient client;
    private JList<ListEntry> userListUI;
    private DefaultListModel<ListEntry> userListModel;
    private ArrayList<String> name=new ArrayList<>(15);
    private ArrayList<ListEntry>listIcon=new ArrayList<>();
    private int i=0;


    public UserListPane(ChatClient client) {
        name.add("1.png");
        name.add("2.png");
        name.add("3.png");
        name.add("4.png");
        name.add("5.png");
        this.client = client;
        this.client.addUserStatusListener(this);
        userListModel = new DefaultListModel<>();
        userListUI = new JList<>(userListModel);
        userListUI.setFont(new Font(Font.DIALOG, Font.BOLD, 30));
        userListUI.setCellRenderer(new ListEntryCellRenderer());
        setLayout(new BorderLayout());
        add(new JScrollPane(userListUI), BorderLayout.CENTER);

        userListUI.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1) {
                    String login = userListUI.getSelectedValue().value;
                    MessagePane messagePane = new MessagePane(client, login);

                    JFrame f = new JFrame("Message: " + login);
                    f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    f.setSize(500, 500);
                    f.getContentPane().add(messagePane, BorderLayout.CENTER);
                    f.setVisible(true);
                }
            }
        });
    }


    @Override
    public void online(String login) {
        ListEntry element =new ListEntry(login,new ImageIcon("C:\\images\\"+name.get(i) ));
        userListModel.addElement(element);
        listIcon.add(element);
        i++;
    }

    @Override
    public void offline(String login) {
        for (ListEntry item:listIcon) {
            if(item.value.equals(login))userListModel.removeElement(item);
        }
        i--;
    }

    class ListEntry {
        private String value;
        private ImageIcon icon;

        public ListEntry(String value, ImageIcon icon) {
            this.value = value;
            this.icon = icon;
        }

        public String getValue() {
            return value;
        }

        public ImageIcon getIcon() {
            return icon;
        }

        public String toString() {
            return value;
        }
    }

    class ListEntryCellRenderer
            extends JLabel implements ListCellRenderer {
        private JLabel label;

        public Component getListCellRendererComponent(JList list, Object value,
                                                      int index, boolean isSelected,
                                                      boolean cellHasFocus) {
            ListEntry entry = (ListEntry) value;

            setText(value.toString());
            setIcon(entry.getIcon());

            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            setEnabled(list.isEnabled());
            setFont(list.getFont());
            setOpaque(true);

            return this;
        }
    }
}
