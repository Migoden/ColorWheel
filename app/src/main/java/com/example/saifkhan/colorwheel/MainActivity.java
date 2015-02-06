package com.example.saifkhan.colorwheel;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class MainActivity extends Activity {

    private View mContainerView;

    private enum CommandType{
        RELATIVE(1),ABSOLUTE(2);
        private final int mCode;

        CommandType(int code) {
            mCode = code;
        }

        public int getCode() {
            return mCode;
        }
    }
    private class Command {

        public Command(CommandType type, int r, int g, int b) {
            command_type = type;
            R = r;
            G = g;
            B = b;
        }

        CommandType command_type;
        int R;
        int G;
        int B;
    }

    private ArrayList<Command> mCommands = new ArrayList<Command>();
    private Command mAbsoluteCommand = new Command(CommandType.ABSOLUTE, 127, 127, 127);
    private ArrayList<Command> mActiveRelativeCommands = new ArrayList<Command>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button connectButton = (Button) findViewById(R.id.connect_btn);
        mContainerView = connectButton.getRootView();
        final EditText ipEditText = (EditText) findViewById(R.id.ip_edit_text);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NetworkUtil.SocketConnectTask task = new NetworkUtil.SocketConnectTask(new NetworkUtil.NetworkRequestListener(){

                    @Override
                    public void didReadBufferedReader(BufferedReader reader,  InputStream inputStream) {

                        try {
                            DataInputStream dataInputStream = new DataInputStream(inputStream);
                            while (dataInputStream != null) {
                                parseCommand(dataInputStream);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void didFailWithMessage(final String message) {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
                task.execute(ipEditText.getText().toString());
            }
        });
    }

    private void parseCommand(DataInputStream dataInputStream) throws IOException {
        int r = 0;
        int g = 0;
        int b = 0;
        int commandTypeCode = dataInputStream.readByte();
        CommandType commandType = commandTypeCode == 1 ? CommandType.RELATIVE : CommandType.ABSOLUTE;


        if(commandType == CommandType.RELATIVE) {
            r = dataInputStream.readShort();
            g = dataInputStream.readShort();
            b = dataInputStream.readShort();
        } else { //absolute
            r = dataInputStream.readUnsignedByte();
            g = dataInputStream.readUnsignedByte();
            b = dataInputStream.readUnsignedByte();
        }
        Command command = new Command(commandType, r, g, b);
        Log.v("saif", "saif command be " + commandType + " with red value " + r + " " + g + " " + b);
        mCommands.add(command);
        updateForCommand(command);
    }

    private void updateForCommand(Command command) {
        if(command.command_type == CommandType.ABSOLUTE) {
            mAbsoluteCommand = command;
            mActiveRelativeCommands.clear();
        } else {
            mActiveRelativeCommands.add(command);
        }
        resetUI();
    }

    private void resetUI() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int r = mAbsoluteCommand.R;
                int g = mAbsoluteCommand.G;
                int b = mAbsoluteCommand.B;
                for(Command command : mActiveRelativeCommands) {
                    r += command.R;
                    g += command.G;
                    b += command.B;
                }
                mContainerView.setBackgroundColor(Color.rgb(r, g, b));
            }
        });
    }


}
