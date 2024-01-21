package com.example.communicationinterface30003;

import com.example.communicationinterface30003.client.WebClient;
import com.example.communicationinterface30003.server.WebServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class CommunicationInterface30003Application {

    public static void main(String[] args) throws Exception {
        ApplicationContext context =SpringApplication.run(CommunicationInterface30003Application.class, args);
        WebServer webServer=context.getBean(WebServer.class);
        webServer.start();
        WebClient webClient = context.getBean(WebClient.class);
        webClient.connect();
    }

}
