package src;

import src.data.Data;
import src.data.NodeData;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Nodo {

    // donnes tel que son ip et IP des voisins ainsi que du prox
    private NodeData data;

    // listes des clients du stream
    private Set<String> ips = new HashSet<>();

    public static void main(String[] argv) {
        new Nodo(Data.data.get(argv[0]));
    }

    public static final int PORT = 25000;

    private DatagramSocket RTPsocket;

    public Nodo(NodeData data) {
        this.data = data;

        byte[] sBuf = new byte[15000];

        try {
            RTPsocket = new DatagramSocket(PORT);
            RTPsocket.setSoTimeout(10);
        } catch (SocketException e) {
            System.out.println("src.Cliente: erro no socket: " + e.getMessage());
        }

        while (true) {
            DatagramPacket rcvdp = new DatagramPacket(sBuf, sBuf.length);
            try {
                RTPsocket.receive(rcvdp);
                CelsoPacket packet = new CelsoPacket(rcvdp.getData(), rcvdp.getLength());

                byte type = packet.getType();

                int ip_byte_size = packet.getIPBytes(sBuf);
                byte[] string = new byte[ip_byte_size];
                for (int i = 0; i < ip_byte_size; i++){
                    string[i] = sBuf[i];
                }
                String packetData = new String(string);

                if (type == 0x0){
                    // stream video to RP packet
                    System.out.println("nbsaltos: "+packetData);

                    // change nbsaltos to nbsaltos+1 and send to prox
                    int video_byte_size = packet.getVideoBytes(sBuf);
                    CelsoPacket newPacket = new CelsoPacket((byte) 0x0, sBuf, video_byte_size, packetData.getBytes(), packetData.getBytes().length);
                    int size = newPacket.getPacketBytes(sBuf);
                    rcvdp = new DatagramPacket(sBuf, sBuf.length);
                    DatagramPacket senddp = new DatagramPacket(rcvdp.getData(), rcvdp.getLength(), InetAddress.getByName(data.getProx().trim()), PORT);
                    RTPsocket.send(senddp);
                }
                if (type == 0x1){
                    // ip to add to table

                }

                if (type == 0x2) {
                    // stream video to clients

                }


            } catch (InterruptedIOException iioe) {
                //System.out.println("Nothing to read");
            } catch (IOException ioe) {
                System.out.println("Exception caught: " + ioe);
            }
        }
    }
}