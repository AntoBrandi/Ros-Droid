package com.example.ros_droid;


public class ConnectionListener {

    private boolean isConnected = false;
    private ChangeListener listener;

    public boolean getIsConnected(){
        return this.isConnected;
    }

    public void setIsConnected(boolean isConnected){
        this.isConnected = isConnected;
        if(this.listener!=null){
            listener.onChange();
        }
    }

    public ChangeListener getListener(){
        return this.listener;
    }

    public void setListener(ChangeListener listener){
        this.listener = listener;
    }

    public interface ChangeListener {
        void onChange();
    }
}
