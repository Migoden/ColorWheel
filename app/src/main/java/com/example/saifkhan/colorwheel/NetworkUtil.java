package com.example.saifkhan.colorwheel;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by saifkhan on 15-02-05.
 */
public class NetworkUtil {

    public static class SocketConnectTask extends AsyncTask<String, String, String> {
        NetworkRequestListener listener;
        private final int PORT = 1234;
        private InputStream mInputStream;

        public SocketConnectTask(NetworkRequestListener listener) {
            this.listener = listener;
        }

        @Override
        protected String doInBackground(String... address) {

            try {
                Socket socket = new Socket(address[0], PORT);
                mInputStream = socket.getInputStream();
                listener.didReadBufferedReader(new DataInputStream(mInputStream));
            } catch (UnknownHostException e) {
                listener.didFailWithMessage(e.getMessage());
            } catch (IOException e) {
                listener.didFailWithMessage(e.getMessage());
            }
            return "";
        }

        public void closeSteam() {
            if(mInputStream != null) {
                try {
                    mInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static abstract class NetworkRequestListener {
        public abstract void didReadBufferedReader(DataInputStream inputStream);

        public abstract void didFailWithMessage(String message);
    }
}
