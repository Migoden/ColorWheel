package com.example.saifkhan.colorwheel;

/**
 * Created by saifkhan on 15-02-08.
 */

public class Command {

    public Command(CommandType type, int r, int g, int b) {
        commandType = type;
        R = r;
        G = g;
        B = b;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public enum CommandType{
        RELATIVE("Relative"),ABSOLUTE("Absolute");
        private final String mCommandCopy;

        CommandType(String commandCopy) {
            mCommandCopy = commandCopy;
        }

        public String getCommandCopy() {
            return mCommandCopy;
        }
    }

    private int mSelectedCount;
    private CommandType commandType;
    int R;
    int G;
    int B;

    public int getSelectedCount() {
        return mSelectedCount;
    }

    public void setSelected(int isSelected) {
        this.mSelectedCount = isSelected;
    }
}