package com.insatoulouse.chatsystem.gui;

import com.insatoulouse.chatsystem.model.MessageSystem;

import javax.swing.*;

public class ChatPanel extends JScrollPane {

    public ChatPanel(ChatGUI chatGUI) {

        final JList<MessageSystem> messageList = new JList<MessageSystem>(chatGUI.getListMessage());
        messageList.setLayoutOrientation(JList.VERTICAL);
        messageList.setCellRenderer(new MessageLabel());
        messageList.setFocusable(false);
        this.setViewportView(messageList);
        this.setFocusable(false);


    }
}
