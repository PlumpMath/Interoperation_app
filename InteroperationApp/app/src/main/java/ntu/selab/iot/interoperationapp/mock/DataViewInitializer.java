package ntu.selab.iot.interoperationapp.mock;

import ntu.selab.iot.interoperationapp.R;
import ntu.selab.iot.interoperationapp.view.SensorDataView;
import android.content.Context;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Author: Keith Hung (kamael@selab.csie.ncu.edu.tw) 
 * Date: 2014.07.15
 * Last Update: 2014.07.16
 * */

public class DataViewInitializer {
	private static final int FIELDS_NUM = 6;
	private final static int FIELD_IMAGE[] = {R.drawable.temperature, R.drawable.humidity, 
											  R.drawable.acceleration, R.drawable.pressure, 
											  R.drawable.gyroscope, R.drawable.magnet};
	
	private final static String FIELD_TITLE[] = {"Temperature", "Humidity", 
												 "Acceleration", "Barometric Pressure", 
												 "Gyroscope", "Magnetic Field"};
	private final static String FIELD_UNIT[] = {"℃", "%", "m/s^2", "hPa", "", ""};
	private final static String FIELD_VALUE[] = {"31.5", "61.7", "x=+0.02 y=-0.13 z=+0.09", 
												 "1013.25", "x=-0.63 y=+1.63 z=-2.19", 
												 "x=+0.09 y=+0.21 z=+0.01"};
	
	public static void init(ViewGroup viewGroup) {		
		for (int i = 0; i < FIELDS_NUM; i++) {
			SensorDataView dataField = new SensorDataView(viewGroup.getContext());
			dataField.setTitle(FIELD_TITLE[i]);
			dataField.setIcon(FIELD_IMAGE[i]);
			dataField.setUnitText(FIELD_UNIT[i]);
			dataField.setValueText(FIELD_VALUE[i]);
			dataField.setTag(i);
			
			viewGroup.addView(dataField);
			
			if (dataField.getLayoutParams() instanceof LinearLayout.LayoutParams) {
				LinearLayout.LayoutParams params = 
						(LinearLayout.LayoutParams) dataField.getLayoutParams();
				
				params.bottomMargin = (int) Math.round(dip2px(viewGroup.getContext(), 10));
				dataField.setLayoutParams(params);
			}
		}
	}
	
	private static float dip2px(Context context, int dip) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, 
				context.getResources().getDisplayMetrics());
	}
}
