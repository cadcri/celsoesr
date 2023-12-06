package src;

import src.data.Data;
import src.data.NodeData;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Objects;

public class Server {

    // donnes tel que son ip et IP des voisins ainsi que du prox
    private NodeData data;

    int imagenb = 0; //image nb of the image currently transmitted
    VideoStream video; //src.VideoStream object used to access video frames
    static int FRAME_PERIOD = 100; //Frame period of the video to stream, in ms
    static int VIDEO_LENGTH = 500; //length of the video in frames
    byte[] buff = new byte[25000]; //buffer used to store the images to send to the client

    DatagramSocket RTPsocket;

    public Server(NodeData nodeData) {
        this.data = nodeData;
        try {
            video = new VideoStream("movie.Mjpeg");
            RTPsocket = new DatagramSocket();

            while(true){
                if (imagenb >= VIDEO_LENGTH) {
                    try {
                        video = new VideoStream("movie.Mjpeg");
                        imagenb=0;
                    } catch (Exception a) {
                        System.out.println("src.Servidor: erro no video: " + a.getMessage());
                        return;
                    }
                }

                imagenb++;
                int image_length = video.getnextframe(buff);
                CelsoPacket packet = new CelsoPacket((byte) 0x0, buff, image_length, "1".getBytes(), "1".getBytes().length);
                int size = packet.getPacketBytes(buff);
                DatagramPacket senddp = new DatagramPacket(buff, size, InetAddress.getByName(data.getProx()), Nodo.PORT);

                RTPsocket.send(senddp);
                Thread.sleep(FRAME_PERIOD);
            }

        } catch (Exception ex) {
            System.out.println("Exception caught: " + ex.getStackTrace().toString());
            ex.printStackTrace();
            System.exit(0);
        }
    }

    public static void main(String[] argv) {
        new Server(Data.data.get(argv[0]));
    }



}
