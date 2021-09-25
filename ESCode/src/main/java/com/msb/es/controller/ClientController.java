package com.msb.es.controller;

import lombok.SneakyThrows;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;

@RestController
public class ClientController {
    @GetMapping("/transport")
    @SneakyThrows
    public String transport() {
        TransportClient client = new PreBuiltTransportClient(Settings.EMPTY)
                .addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300))
                .addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9301));

        Settings settings = Settings.builder()
                .put("cluster.name", "myClusterName").build();
        client = new PreBuiltTransportClient(settings);
        //Add transport addresses and do something with the client...
        client.close();
        return "";
    }


}
