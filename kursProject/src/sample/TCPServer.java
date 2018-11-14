package sample;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
public class TCPServer {


    public TCPServer( PipedOutputStream outputPipe   ) {
        try {


            final PipedOutputStream output = outputPipe;

            // Selector: multiplexor of SelectableChannel objects
            Selector selector = Selector.open(); // selector is open here

            // ServerSocketChannel: selectable channel for stream-oriented listening sockets
            ServerSocketChannel crunchifySocket = ServerSocketChannel.open();
            InetSocketAddress crunchifyAddr = new InetSocketAddress("localhost", 1111);

            // Binds the channel's socket to a local address and configures the socket to listen for connections
            crunchifySocket.bind(crunchifyAddr);

            // Adjusts this channel's blocking mode.
            crunchifySocket.configureBlocking(false);

            int ops = crunchifySocket.validOps();
            SelectionKey selectKy = crunchifySocket.register(selector, ops, null);

            // Infinite loop..
            // Keep server running
            while (true) {

                log("i'm a server and i'm waiting for new connection and buffer select...");
                // Selects a set of keys whose corresponding channels are ready for I/O operations
                selector.select();

                // token representing the registration of a SelectableChannel with a Selector
                Set<SelectionKey> crunchifyKeys = selector.selectedKeys();
                Iterator<SelectionKey> crunchifyIterator = crunchifyKeys.iterator();

                while (crunchifyIterator.hasNext()) {
                    SelectionKey myKey = crunchifyIterator.next();

                    // Tests whether this key's channel is ready to accept a new socket connection
                    if (myKey.isAcceptable()) {
                        SocketChannel crunchifyClient = crunchifySocket.accept();

                        // Adjusts this channel's blocking mode to false
                        crunchifyClient.configureBlocking(false);

                        // Operation-set bit for read operations
                        crunchifyClient.register(selector, SelectionKey.OP_READ);
                        log("Connection Accepted: " + crunchifyClient.getLocalAddress() + "\n");

                        // Tests whether this key's channel is ready for reading
                    } else if (myKey.isReadable()) {

                        SocketChannel crunchifyClient = (SocketChannel) myKey.channel();
                        ByteBuffer crunchifyBuffer = ByteBuffer.allocate(256);
                        crunchifyClient.read(crunchifyBuffer);
                        String result = new String(crunchifyBuffer.array()).trim();

                     //   log("Message received: " + result);

                        if (result.equals("end")) {
                            crunchifyClient.close();
                            log("\nIt's time to close connection as we got last company name 'Crunchify'");
                            log("\nServer will keep running. Try running client again to establish new connection");
                        }
                        else {
                            try {
                                log("pipe loaded");
                                output.write(crunchifyBuffer.array());

                            } catch (IOException e) {
                                log("pipe trouble");
                                e.printStackTrace();
                            }
                        }
                    }
                    crunchifyIterator.remove();
                }
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void log(String str) {
        System.out.println(str);
    }


}
