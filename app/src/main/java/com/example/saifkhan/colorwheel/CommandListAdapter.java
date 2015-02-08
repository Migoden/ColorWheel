package com.example.saifkhan.colorwheel;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.example.saifkhan.colorwheel.Command.*;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by saifkhan on 15-02-06.
 */
public class CommandListAdapter extends BaseAdapter {

    private ArrayList<Command> mCommands;
    private final LayoutInflater mLayoutInflater;

    public CommandListAdapter(LayoutInflater inflater, Context context) {
        mCommands = new ArrayList<Command>();
        mLayoutInflater = inflater;
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
        Command command = (Command) getItem(i);
        TextView typeTextView = (TextView) view.findViewById(R.id.command_type_text_view);
        TextView rgbTextView = (TextView) view.findViewById(R.id.rgb_text_view);
        TextView isSelectedTextView= (TextView) view.findViewById(R.id.is_selected_indicator);

        typeTextView.setText(command.command_type.getCommandCopy());
        rgbTextView.setText("R: " + command.R + " G: " + command.G + " B: " + command.B);
        if(command.getSelectedCount() > 0) {
            isSelectedTextView.setVisibility(View.VISIBLE);
            isSelectedTextView.setText((command.command_type == CommandType.ABSOLUTE) ? "Selected" : "Selected x " + command.getSelectedCount());
        } else {
            isSelectedTextView.setVisibility(View.GONE);
        }
        if(command.command_type == CommandType.ABSOLUTE) {
            view.setBackgroundColor(Color.rgb(command.R, command.G, command.B));
        } else {
            view.setBackgroundColor(Color.TRANSPARENT);
        }
        return view;
    }

    public void setCommands(ArrayList<Command> commands){
        mCommands = commands;
    }
}
