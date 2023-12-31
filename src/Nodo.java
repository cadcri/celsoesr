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

                    System.out.println("Receved stream image "+packetData+", sending it to "+data.getProx());
                    // change nbsaltos to nbsaltos+1 and send to prox
                    int nbsaltos = Integer.parseInt(packetData);
                    nbsaltos++;
                    packetData = String.valueOf(nbsaltos);

                    int video_byte_size = packet.getVideoBytes(sBuf);
                    CelsoPacket newPacket = new CelsoPacket((byte) 0x0, sBuf, video_byte_size, packetData.getBytes(), packetData.getBytes().length);
                    int size = newPacket.getPacketBytes(sBuf);
                    rcvdp = new DatagramPacket(sBuf, sBuf.length);
                    DatagramPacket senddp = new DatagramPacket(rcvdp.getData(), rcvdp.getLength(), InetAddress.getByName(data.getProx().trim()), PORT);

                    RTPsocket.send(senddp);
                }
                if (type == 0x1){
                    // ip to add to table
                    System.out.println("Receved stream request from "+packetData+", sending it to "+data.getProx());

                    // decoder la liste des ip de packet data et trouver celui qui match avec ses voisins
                    // puis l'ajouter a cette liste
                    ips.add(packetData);

                    if (ips.size() == 1){
                        System.out.println("first client. sending want to stream..");
                        // envoyer packer demande stream
                        // transformer la liste des IPs de node data em un seul string et envoyer ca au lieu de ips[0]
                        CelsoPacket packet2 = new CelsoPacket((byte) 0x1, null, 0, data.getIps()[0].getBytes(),data.getIps()[0].getBytes().length);
                        int size = packet2.getPacketBytes(sBuf);
                        DatagramPacket senddp;
                        try {
                            senddp = new DatagramPacket(sBuf, size, InetAddress.getByName(data.getProx()), 25000);
                        } catch (UnknownHostException e) {
                            throw new RuntimeException(e);
                        }
                        try {
                            RTPsocket.send(senddp);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (type == 0x2) {

                    if (!ips.isEmpty()){
                        packet.setType((byte)0x2);
                        int size = packet.getPacketBytes(sBuf);
                        for (String ip : ips){
                            DatagramPacket senddp = new DatagramPacket(sBuf, size, InetAddress.getByName(ip), Nodo.PORT);
                            RTPsocket.send(senddp);
                            System.out.println("sendinf packet");
                        }
                    }

                }


            } catch (InterruptedIOException iioe) {
                //System.out.println("Nothing to read");
            } catch (IOException ioe) {
                System.out.println("Exception caught: " + ioe);
            }
        }
    }
}