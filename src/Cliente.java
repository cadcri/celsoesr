package src;

import src.data.Data;
import src.data.NodeData;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;

public class Cliente extends JFrame implements ActionListener {

    NodeData nodeData;

    public static void main(String[] argv) {
        new Cliente(Data.data.get(argv[0]), Nodo.PORT);
    }

    JLabel iconLabel;
    DatagramSocket RTPsocket;

    Timer cTimer;
    byte[] cBuf;

    DatagramSocket SendSocket;

    public Cliente(NodeData nodeData, int port) {
        super("Clientes");
        this.nodeData = nodeData;
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        JButton playButton = new JButton("Play");
        JButton pauseButton = new JButton("Pause");
        JPanel mainPanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        iconLabel = new JLabel();
        buttonPanel.setLayout(new GridLayout(1, 0));
        buttonPanel.add(playButton);
        buttonPanel.add(pauseButton);
        iconLabel.setIcon(null);
        mainPanel.setLayout(null);
        mainPanel.add(iconLabel);
        mainPanel.add(buttonPanel);
        iconLabel.setBounds(0, 0, 380, 280);
        buttonPanel.setBounds(0, 280, 380, 50);
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        setSize(new Dimension(390, 370));
        setVisible(true);
        //////////////////////////// STOP GUI

        playButton.addActionListener(e -> {
            cTimer.start();
        });
        cTimer = new Timer(20, this);
        cTimer.setInitialDelay(0);
        cTimer.setCoalesce(true);
        cBuf = new byte[15000];
        try {
            RTPsocket = new DatagramSocket(port);
            RTPsocket.setSoTimeout(5000);
        } catch (SocketException e) {
            System.out.println("src.Cliente: erro no socket: " + e.getMessage());
        }
        cTimer.start(); //// TOREMOVE

        try {
            SendSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        sendRequestWantStream();
    }

    private void sendRequestWantStream(){
        CelsoPacket packet = new CelsoPacket((byte) 0x1, null, 0, nodeData.getIps()[0].getBytes(),nodeData.getIps()[0].getBytes().length);
        int size = packet.getPacketBytes(cBuf);
        DatagramPacket senddp;
        try {
            senddp = new DatagramPacket(cBuf, size, InetAddress.getByName(nodeData.getProx()), 25000);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        try {
            SendSocket.send(senddp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void actionPerformed(ActionEvent e) {
        DatagramPacket rcvdp = new DatagramPacket(cBuf, cBuf.length);

        try {
            RTPsocket.receive(rcvdp);
            CelsoPacket packet = new CelsoPacket(rcvdp.getData(), rcvdp.getLength());

            int video_byte_size = packet.getVideoBytes(cBuf);

            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Image image = toolkit.createImage(cBuf, 0, video_byte_size);

            ImageIcon icon = new ImageIcon(image);
            iconLabel.setIcon(icon);


            int ip_byte_size = packet.getIPBytes(cBuf);
            byte[] string = new byte[ip_byte_size];
            for (int i = 0; i < ip_byte_size; i++){
                string[i] = cBuf[i];
            }

        } catch (InterruptedIOException iioe) {
            System.out.println("Nothing to read");
        } catch (IOException ioe) {
            System.out.println("Exception caught: " + ioe);
        }
    }

}