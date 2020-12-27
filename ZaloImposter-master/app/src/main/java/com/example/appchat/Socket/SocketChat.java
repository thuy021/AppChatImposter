package com.example.appchat.Socket;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketChat {
    private Socket mClient;{
        try {
            mClient = IO.socket("http://13.229.207.6:6000");
            //mClient = IO.socket("http://192.168.1.8:6000");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public Socket getmClient() {
        return mClient;
    }

    public SocketChat (){
        mClient.connect();
    }
}
