package michat.socketChat;

import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Client {

    public static DatagramPacket getPacket(DatagramSocket ds) throws Exception {
        byte[] buf = new byte[4096];
        DatagramPacket incoming = new DatagramPacket(buf, buf.length);
        ds.receive(incoming);
        return incoming;
    }

    public static String getString(DatagramPacket dp) throws Exception {
        return new String(dp.getData(), 0, dp.getLength());
    }

    public static void sendString(DatagramSocket ds, String s, InetAddress server, int port) throws Exception {
        byte[] sending = s.getBytes();
        DatagramPacket outsending = new DatagramPacket(sending, sending.length, server,
                port);
        ds.send(outsending);

    }

    public static void start() throws UnknownHostException, SocketException, InterruptedException {
        final DatagramSocket ds = new DatagramSocket(3000);
        Thread receive = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            while (true) {

                                DatagramPacket dp = getPacket(ds);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

        receive.start();
        receive.join();

    }
}

