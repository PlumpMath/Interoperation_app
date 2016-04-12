package ntu.selab.iot.interoperationapp.BroadcastReceiver;

import android.content.Intent;

/**
 * Created by User on 2015/6/3.
 */
public abstract class CommandHandler {
    protected  CommandHandler next;
    public abstract void handle(Intent intent);
    public void setNext(CommandHandler next){
        this.next = next;
    }
}
