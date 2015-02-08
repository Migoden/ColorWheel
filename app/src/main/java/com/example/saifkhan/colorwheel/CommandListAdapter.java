package com.example.saifkhan.colorwheel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by saifkhan on 15-02-06.
 */
public class CommandListAdapter extends BaseAdapter {

    private ArrayList<MainActivity.Command> mCommands;
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;

    public CommandListAdapter(LayoutInflater inflater, Context context) {
        mCommands = new ArrayList<MainActivity.Command>();
        mLayoutInflater = inflater;
        mContext = context;
    }

    public void setCommands(ArrayList<MainActivity.Command> commands){
        mCommands = commands;
    }

    public ArrayList<MainActivity.Command> getCommands(){
        return mCommands;
    }
    @Override
    public int getCount() {
        return mCommands.size();
    }

    @Override
    public Object getItem(int i) {
        return mCommands.get(getCount() - 1 - i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null) {
            view = mLayoutInflater.inflate(R.layout.command_cell, viewGroup, false);
        }
        MainActivity.Command command = (MainActivity.Command) getItem(i);
        TextView typeTextView = (TextView) view.findViewById(R.id.command_type_text_view);
        TextView rgbTextView = (TextView) view.findViewById(R.id.rgb_text_view);

        typeTextView.setText(command.command_type.getCommandCopy());
        rgbTextView.setText("R: " + command.R + " G: " + command.G + " B: " + command.B);
        return view;
    }
}
