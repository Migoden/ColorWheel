package com.example.saifkhan.colorwheel;

import android.os.AsyncTask;

import java.io.BufferedReader;
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

        public SocketConnectTask(NetworkRequestListener listener) {
            this.listener = listener;
        }

        @Override
        protected String doInBackground(String... address) {

            try {
//                SSLSocketFactory sslFact =
//                        (SSLSocketFactory)SSLSocketFactory.getDefault();
//                SSLSocket socket =
//                        (SSLSocket)sslFact.createSocket(address[0], PORT);

                Socket socket = new Socket(address[0], PORT);
//                PrintWriter out = new PrintWriter(socket.getOutputStream(),
//                        true);
                InputStream inputStream = socket.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(
                       inputStream);
                BufferedReader in = new BufferedReader(inputStreamReader);
                listener.didReadBufferedReader(in, inputStream);
            } catch (UnknownHostException e) {
                listener.didFailWithMessage(e.getMessage());
            } catch (IOException e) {
                listener.didFailWithMessage(e.getMessage());
            }
            return "";
        }
    }


    public static abstract class NetworkRequestListener {
        public abstract void didReadBufferedReader(BufferedReader reader, InputStream inputStream);

        public abstract void didFailWithMessage(String message);
    }
}
