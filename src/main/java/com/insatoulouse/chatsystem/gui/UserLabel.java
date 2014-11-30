package com.insatoulouse.chatsystem.gui;

import com.insatoulouse.chatsystem.model.User;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by david on 27/11/14.
 */
public class UserLabel extends JPanel implements ListCellRenderer<User> {

    public static final Color colors[] = {Color.GREEN, Color.RED, Color.BLUE};

    @Override
    public Component getListCellRendererComponent(JList<? extends User> list, User value, int index, boolean isSelected, boolean cellHasFocus) {
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        this.setBackground(Color.WHITE);
        // this.setBou

        JPanel milieu = new JPanel();
        milieu.setLayout(new BoxLayout(milieu, BoxLayout.Y_AXIS));
        milieu.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        milieu.setBackground(UserLabel.colors[index%UserLabel.colors.length]);
        milieu.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));

        JLabel name = new JLabel(value.getName());
        name.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        name.setFont(new Font("Sans serif", Font.BOLD, 12));
        name.setForeground(Color.white);
        milieu.add(name);

        JLabel ip = new JLabel(value.getIp().toString());
        ip.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        ip.setFont(new Font("Sans serif", Font.BOLD, 11));
        ip.setForeground(Color.white);
        milieu.add(ip);

        this.add(milieu, BorderLayout.CENTER);

        return this;
    }
}
