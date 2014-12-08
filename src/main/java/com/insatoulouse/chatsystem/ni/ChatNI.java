package com.insatoulouse.chatsystem.ni;

import com.insatoulouse.chatsystem.Controller;
import com.insatoulouse.chatsystem.exception.PacketException;
import com.insatoulouse.chatsystem.exception.TechnicalException;
import com.insatoulouse.chatsystem.model.*;
import com.insatoulouse.chatsystem.utils.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * Created by tlk on 27/11/14.
 */
public class ChatNI {

    private static int message_id = 0;
    private Controller controller;
    private TcpListener tcpListener;
    private UdpListener udpListener;
    private NetworkInvoker invoker;

    private static final Logger l = LogManager.getLogger(ChatNI.class.getName());

    public ChatNI(Controller c) throws TechnicalException {
        this.controller = c;
        this.tcpListener = new TcpListener();
        this.udpListener = new UdpListener(this);
        this.udpListener.start();
        this.invoker = new NetworkInvoker();
        this.invoker.start();
    }

    public ArrayList<InetAddress> getNetworkBroadcastAddresses() throws TechnicalException
    {
        ArrayList<InetAddress> ret = new ArrayList<InetAddress>();
        try {
            Enumeration list;
            list = NetworkInterface.getNetworkInterfaces();

            while(list.hasMoreElements())
            {
                NetworkInterface iface = (NetworkInterface) list.nextElement();
                if(iface == null)
                {
                    continue;
                }

                if(!iface.isLoopback() && iface.isUp())
                {
                    Iterator it = iface.getInterfaceAddresses().iterator();
                    while(it.hasNext())
                    {
                        InterfaceAddress address = (InterfaceAddress) it.next();
                        if(address == null)
                        {
                            continue;
                        }

                        InetAddress broadcast = (InetAddress) address.getBroadcast();
                        if(broadcast != null)
                        {
                            ret.add(broadcast);
                        }

                    }
                }
            }

        } catch (SocketException e) {
            throw new TechnicalException("Impossible de récuperer la liste des interfaces");
        }
        return ret;
    }

    public void processPacket(DatagramPacket packet) {

        PacketParser parser = AbstractFactory.getFactory(AbstractFactory.Type.JSON).getPacketParser();
        String data = new String(packet.getData(), packet.getOffset(), packet.getLength());

        l.debug("processing incomming packet : " + data);

        try {
            Packet message = parser.read(data);
            if(message instanceof Hello)
            {
                this.controller.processHello((Hello) message, packet.getAddress());
            } else if(message instanceof HelloAck)
            {
                this.controller.processHelloAck((HelloAck) message, packet.getAddress());
            }else if(message instanceof Goodbye)
            {
                this.controller.processRemoteGoodBye(packet.getAddress());
            } else if(message instanceof Message)
            {
                this.controller.processMessage((Message) message, packet.getAddress());
            }
        } catch (PacketException e) {
            l.debug("message JSON non valide : "+data,e);
        }

    }

    public void sendHello(User u) throws TechnicalException
    {
        Packet p = new Hello(u.getName());
        sendBroadcast(p);
    }

    public void sendHelloAck(User from, User to) throws TechnicalException
    {
        Packet p = new HelloAck(from.getName());
        sendUnicast(p, to.getIp());
    }

    public void sendGoodbye() throws TechnicalException
    {
        Packet p = new Goodbye();
        sendBroadcast(p);
    }

    public void sendMessage(User u, String message) throws TechnicalException
    {
        Packet p = new Message(ChatNI.message_id, message);
        ChatNI.message_id += 1;
        sendUnicast(p, u.getIp());
    }

    private void sendUnicast(Packet p, InetAddress addr) throws TechnicalException
    {
        PacketParser parser = AbstractFactory.getFactory(AbstractFactory.Type.JSON).getPacketParser();
        String data = parser.write(p);
        if(data != null)
        {
            DatagramPacket dp = new DatagramPacket(data.getBytes(), data.length());
            dp.setAddress(addr);
            dp.setPort(Integer.parseInt(Config.getInstance().getProperties(Config.CONFIG_PORT)));
            UdpSenderCommand cmd = new UdpSenderCommand(dp);
            this.invoker.addCommand(cmd);
        } else {
            l.error("Impossible de générer le JSON : " + p);
        }
    }

    private void sendBroadcast(Packet p) throws TechnicalException
    {
        try {
            this.sendUnicast(p, InetAddress.getByName(Config.getInstance().getProperties(Config.CONFIG_ADDRESS)));
        } catch (UnknownHostException e) {
            throw new TechnicalException("impossible de toper l'adresse de broadcast");
        }
    }

    public void exit() {
        tcpListener.close();
        udpListener.close();
        invoker.close();
    }
}
