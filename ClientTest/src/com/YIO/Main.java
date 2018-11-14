package com.YIO;
import com.google.gson.*;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

public class Main {
    private static Gson GSON = new GsonBuilder().setLenient().create();

    public static void main(String[] args) throws IOException, InterruptedException {
        Patient patient = new Patient(3);
        patient.makeMeasure(110,140,190);
        String json = GSON.toJson(patient);
        System.out.print(json);
        InetSocketAddress crunchifyAddr = new InetSocketAddress("localhost", 1111);
        SocketChannel crunchifyClient = SocketChannel.open(crunchifyAddr);

        log("Connecting to Server on port 1111...");

        ArrayList<String> patinetsList = new ArrayList<String>();
        patinetsList.add(json);
        // create a ArrayList with companyName list


        patient.makeMeasure(120,160,200);
        patient.setMinutes(LocalDateTime.now().getMinute()+10);
        json = GSON.toJson(patient);
        patinetsList.add(json);
        patient.makeMeasure(60,85,120);
        patient.setMinutes(LocalDateTime.now().getMinute()+25);

        json = GSON.toJson(patient);
        patinetsList.add(json);
        patient.makeMeasure(80,100,140);
        patient.setMinutes(LocalDateTime.now().getMinute()+35);

        json = GSON.toJson(patient);
        patinetsList.add(json);

        for (String patJSON : patinetsList) {

            byte[] message = new String(patJSON).getBytes();
            ByteBuffer buffer = ByteBuffer.wrap(message);
            crunchifyClient.write(buffer);

            log("sending: " + patJSON);
            buffer.clear();

            // wait for 2 seconds before sending next message
            Thread.sleep(15000);
            patient = GSON.fromJson(patJSON,Patient.class);
            log("convert: " + patient.getHR());
        }
        byte[] message = new String("end").getBytes();
        ByteBuffer buffer = ByteBuffer.wrap(message);
        crunchifyClient.write(buffer);

        log("sending:" + "end");


        buffer.clear();

        crunchifyClient.close();

    }

    private static void log(String str) {
        System.out.println(str);
    }
}
