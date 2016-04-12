package ntu.selab.iot.interoperationapp.view;

import ntu.selab.android.util.ImageReader;
import ntu.selab.iot.interoperationapp.R;
import android.content.Context;
import android.support.v4.util.Pools.SynchronizedPool;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class SensorDataView extends RelativeLayout {
	private LinearLayout expression;
	private EditText	  valueField;
	private TextView  unitField;
	private TextView	  title;
	private ImageView icon;
	private Context context;
	
	private static final SynchronizedPool<SensorDataView> sPool = new SynchronizedPool<SensorDataView>(10);
	
	public  SensorDataView(Context context) {
		this(context, null);
		this.context = context;
	}
	
	public SensorDataView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initDataView(context);
	}

	private void initDataView(Context context) {
		LayoutInflater.from(context).inflate(R.layout.sensor_data_view, this, true);
		
		this.setBackgroundResource(android.R.color.white);
		expression =(LinearLayout) findViewById(R.id.sensor_data_view_expression);
		valueField = (EditText)  findViewById(R.id.sensor_data_view_edit);
		unitField  = (TextView)  findViewById(R.id.sensor_data_view_unit);
		title      = (TextView)  findViewById(R.id.sensor_data_view_title);
		icon       = (ImageView) findViewById(R.id.sensor_data_view_icon);
		
		valueField.setEnabled(false);
		valueField.setFocusable(false);
		
		setTextSize(context, valueField, 0.04f);
		setTextSize(context, unitField,  0.04f);
		setTextSize(context, title,      0.05f);
		
	}

	public void setValueText(CharSequence text) {
		valueField.setText(text);
	}

	private void setTextSize(Context context, TextView view, float percentage) {
		WindowManager window = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = window.getDefaultDisplay();
		
		view.setTextSize(TypedValue.COMPLEX_UNIT_PX, display.getWidth() * percentage);
	}

	public CharSequence getValueText() {
		return valueField.getText();
	}
	
	public void setUnitText(CharSequence text) {
		unitField.setText(text);
	}
	
	public void addexpression(CharSequence value,CharSequence unit){
		EditText valueField = new EditText(context);
		TextView unitField = new TextView(context);
		valueField.setText(value);
		unitField.setText(unit);
		setTextSize(context, valueField, 0.04f);
		setTextSize(context, unitField,  0.04f);
		expression.addView(valueField);
		expression.addView(unitField);
	}
	
	public CharSequence getUnitText() {
		return unitField.getText();
	}
	
	public void setTitle(CharSequence desc) {
		title.setText(desc);
	}
	
	public CharSequence getTitle() {
		return title.getText();
	}
	
	public void setIcon(int resId) {
		icon.setImageBitmap(ImageReader.readBitmapByResId(getContext(), resId));
		icon.setScaleType(ScaleType.CENTER_INSIDE);
	}
	
	
	// Listener Setters
	
	public void setIconOnClickListener(View.OnClickListener listener) {
		icon.setOnClickListener(listener);
	}
	
	public void setIconOnLongClickListener(View.OnLongClickListener listener) {
		icon.setOnLongClickListener(listener);
	}
	
	public static SensorDataView obtain(Context context){
		SensorDataView instance = sPool.acquire();
		return (instance != null) ? instance : new SensorDataView(context);
	}
	
	public void recycle(){
		setValueText(null);
		setUnitText(null);
		setTitle(null);
		sPool.release(this);
	}
}
