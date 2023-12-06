package src;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Set;

import static src.Nodo.PORT;


public class RP  {

    public static void main(String[] argv) {
       new RP();
    }

    DatagramSocket RTPsocket;
    byte[] buff = new byte[150000];

    private Set<String> ips = new HashSet<>();

    private int saltosMax=100;

    public RP() {
        super();

        // teste
        ips.add("10.0.0.21");

        try {
            RTPsocket = new DatagramSocket(PORT);
            while (true) {
                DatagramPacket rcvdp = new DatagramPacket(buff, buff.length);
                RTPsocket.receive(rcvdp);
                CelsoPacket packet = new CelsoPacket(rcvdp.getData(), rcvdp.getLength());

                int ip_byte_size = packet.getIPBytes(buff);
                byte[] string = new byte[ip_byte_size];
                for (int i = 0; i < ip_byte_size; i++){
                    string[i] = buff[i];
                }
                String packetData = new String(string);

                byte type = packet.getType();
                if (type == 0x0){
                    // receved a stream video now we must send it back to IPs
                    System.out.println("nbsaltos: "+packetData);
                    int nbsaltos = Integer.parseInt(packetData);

                    if (nbsaltos<saltosMax){
                        saltosMax = nbsaltos;
                    }

                    if (!ips.isEmpty() && nbsaltos == saltosMax){
                        packet.setType((byte)0x2);
                        int size = packet.getPacketBytes(buff);
                        for (String ip : ips){
                            DatagramPacket senddp = new DatagramPacket(buff, size, InetAddress.getByName(ip), Nodo.PORT);
                            RTPsocket.send(senddp);
                            System.out.println("sendinf packet");
                        }
                    }
                }
                if (type == 0x1) {
                    // receved an IP adress that now must be served

                    //decoder packet data et trouver celui qui match avec le voisin
                    System.out.println("receved a streaming request from "+packetData);
                    ips.add(packetData);
                }
            }

        } catch (SocketException e) {
            System.out.println("src.Servidor: erro no socket: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}