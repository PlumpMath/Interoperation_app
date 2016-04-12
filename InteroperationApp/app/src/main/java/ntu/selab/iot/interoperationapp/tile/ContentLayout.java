package ntu.selab.iot.interoperationapp.tile;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by User on 2015/6/5.
 */
public class ContentLayout extends LinearLayout {
    public ContentLayout(Context context) {
        super(context,null);
    }


    @Override
    protected void removeDetachedView(View child, boolean animate) {
        super.removeDetachedView(child, false);

    }
}