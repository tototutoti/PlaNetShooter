package view.server;

import javax.swing.*;

import java.awt.*;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import static javax.swing.SwingConstants.CENTER;

public class ServerFrame extends JFrame {
    private String ip = null;

    public ServerFrame() {
        super();

        try {
            DatagramSocket socket = new DatagramSocket();
            socket.connect(InetAddress.getByName("8.8.8.8"), 42873);
            ip = socket.getLocalAddress().getHostAddress();
            if (ip.equals("0.0.0.0"))
                ip = "localhost";
        }
        catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }

        this.createFrame();
    }

    private void createFrame() {
        this.setTitle("PlaNetShooter Server");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(300, 100);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setLayout(new GridLayout(2,1));

        JLabel closeInformation = new JLabel("Close to stop the game server.");
        closeInformation.setHorizontalAlignment(CENTER);
        this.getContentPane().add(closeInformation);

        if (ip != null) {
            JTextPane hostInformation = new JTextPane();
            hostInformation.setContentType("text/html");
            hostInformation.setText("<html><center>Host : " +ip+ "</center></html>");
            hostInformation.setEditable(false);
            hostInformation.setBackground(null);
            this.getContentPane().add(hostInformation);
        }

        this.setVisible(true);
    }
}