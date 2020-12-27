package com.example.appchat.Socket;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketClient {
    private Socket mClient;{
        try {
            mClient = IO.socket("http://13.250.34.4:5000");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public Socket getmClient() {
        return mClient;
    }

    public SocketClient (){
        mClient.connect();
    }
}
