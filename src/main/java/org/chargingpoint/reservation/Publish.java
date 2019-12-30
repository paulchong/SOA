package org.chargingpoint.reservation;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Publish {
    private final static String QUEUE_NAME = "purchase";
    private static ConnectionFactory factory;
    private static Connection conn;
    private static Channel channel;

    public static void connect() throws Exception {    
        String RABBIT_HOST = System.getenv().containsKey("RABBIT_HOST") ? System.getenv("RABBIT_HOST") : "localhost";
        factory = new ConnectionFactory();
        factory.setHost(RABBIT_HOST);
        conn = factory.newConnection();
        channel = conn.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        
    }

    public Publish() {
        try {
            connect();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void publish(String json) {
        try {
            channel.basicPublish("", QUEUE_NAME, null, json.getBytes());    
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}