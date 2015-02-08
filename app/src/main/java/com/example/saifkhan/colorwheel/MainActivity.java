package com.example.saifkhan.colorwheel;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.example.saifkhan.colorwheel.Command.*;

public class MainActivity extends Activity {

    private View mContainerView;
    private TextView mHexTextView;
    private CommandListAdapter mCommandListAdapter;

    private ArrayList<Command> mCommands = new ArrayList<Command>();
    private Command mAbsoluteCommand;
    private ColorMetaData currentColor = new ColorMetaData(127,127,127);
    private ArrayList<Command> mActiveRelativeCommands = new ArrayList<Command>();
    private NetworkUtil.SocketConnectTask mSocketTask;
    private EditText mIPEditTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button connectButton = (Button) findViewById(R.id.connect_btn);
        mContainerView = connectButton.getRootView();
        mHexTextView = (TextView) findViewById(R.id.current_hex_edit_text);
        mIPEditTextView = (EditText) findViewById(R.id.ip_edit_text);

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mSocketTask != null) {
                    mSocketTask.closeSteam();
                    mSocketTask.cancel(true);
                    resetCommands();
                }
                mSocketTask = new NetworkUtil.SocketConnectTask(new NetworkUtil.NetworkRequestListener(){

                    @Override
                    public void didReadBufferedReader(DataInputStream dataInputStream) {
                        try {
                            while (true) {
                                parseCommand(dataInputStream);
                            }
                        } catch (final IOException e) {
                            e.printStackTrace();
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void didFailWithMessage(final String message) {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                mSocketTask.execute(mIPEditTextView.getText().toString());
            }
        });

        mCommandListAdapter = new CommandListAdapter(getLayoutInflater(), this);
        ListView mCommandListView = (ListView) findViewById(R.id.recent_commands_list_view);
        mCommandListView.setAdapter(mCommandListAdapter);
        mCommandListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                updateDataForCommand((Command) mCommandListAdapter.getItem(i));
                updateUIForCommand((Command) mCommandListAdapter.getItem(i));
            }
        });
    }

    private void resetCommands() {
        mActiveRelativeCommands.clear();
        mCommands.clear();
        updateUIForCommand(new Command(CommandType.ABSOLUTE, 127, 127, 127));
    }

    private void parseCommand(DataInputStream dataInputStream) throws IOException {
        int commandTypeCode = dataInputStream.readByte();
        int r;
        int g;
        int b;
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
        mCommands.add(command);
        updateDataForCommand(command);
        updateUIForCommand(command);
    }

    private void deselectAbsoluteCommand() {
        if(mAbsoluteCommand != null) {
            mAbsoluteCommand.setSelected(0);
        }
    }

    private void clearActiveCommands() {
        if(mActiveRelativeCommands != null) {
            for(Command command : mActiveRelativeCommands) {
                command.setSelected(0);
            }
            mActiveRelativeCommands.clear();
        }
    }

    private void updateDataForCommand(Command command) {
        if(command.getCommandType() == CommandType.ABSOLUTE) {
            clearActiveCommands();
            deselectAbsoluteCommand();
            mAbsoluteCommand = command;
            command.setSelected(1);
        } else {
            command.setSelected(command.getSelectedCount() + 1);
            if(command.getSelectedCount() == 1) {
                mActiveRelativeCommands.add(command);
            }
        }
    }

    private void updateUIForCommand(final Command command) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(command.getCommandType() == CommandType.ABSOLUTE) {
                    currentColor = new ColorMetaData(command.R, command.G, command.B);
                } else {
                    currentColor.red += command.R;
                    currentColor.green += command.G;
                    currentColor.blue += command.B;
                }

                mContainerView.setBackgroundColor(Color.rgb(currentColor.red, currentColor.green, currentColor.blue));
                mHexTextView.setText("Current color : R: " + currentColor.red + " G: " + currentColor.green + " B: " + currentColor.blue);
                mCommandListAdapter.setCommands((ArrayList<Command>) mCommands.clone());
                mCommandListAdapter.notifyDataSetChanged();
            }
        });
    }

    class ColorMetaData {
        int red;
        int green;
        int blue;

        public ColorMetaData(int r, int g, int b) {
            red = r;
            green = g;
            blue = b;
        }
    }
}
