package com.printer.sdk.android.usb;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.printer.sdk.android.usb.util.PrintUtils;
import com.printer.sdk.api.PrinterType;
import com.printer.sdk.api.USBPrinter;

public class PrinterSDKDemo_Plus_USB extends Activity implements View.OnClickListener{
	private final static String TAG = "PrinterSDKDemo_Plus_USB";

	private static final String ACTION_USB_PERMISSION = "com.android.usb.USB_PERMISSION";
	private Button openConnect;
	private Button printImage;
	private Button printCustomImage;

	private Button printText;
	private Button printTable;
	private Button printTitle;

	private Button printNote;
	private Button printBarCode;

	private RadioButton paperWidth_58;
	private RadioButton paperWidth_80;

	private RadioButton printer_type_remin;
	private RadioButton printer_type_styuls;

	private EditText mBarCodeNum;

	// Name of the connected device
    private String mConnectedDeviceName = null;
    public static USBPrinter mPrinter;
    private UsbDevice mUsbDevice;

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;

    //menu context
    private static final int MENU_CONNECT = 0;
    private static final int MENU_CONNECT_OTHER = 1;
    private static final int MENU_DISCONNECT = 2;

    private Context mContext;
    private boolean isConnected;
    private PendingIntent pendingIntent;

    //条码类型集合
    private List<String> list = new ArrayList<String>();
    private Spinner barCodeSpinner;
    private ArrayAdapter<String> adapter;

    //编码类型
    private List<String> encodingTypeList = new ArrayList<String>();
    private Spinner encodingTypeSpinner;
    private ArrayAdapter<String> encodingTypeAdapter;
    private String encodeType;

    private boolean is_58mm;
    private boolean is_thermal;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.main);
		//getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		mContext = this;
		initView();

		pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
		IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);

		filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
		filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);

		registerReceiver(mUsbReceiver, filter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			//mPrinter.receive();
			if (mPrinter != null) {
				closeConnection();
			}
			isConnected = false;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		android.os.Process.killProcess(android.os.Process.myPid());
	}

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	menu.clear();
    	if(isConnected){
    		menu.add(0, MENU_DISCONNECT, 0, R.string.disconnect).setIcon(android.R.drawable.ic_media_ff);
    	}else{
    		menu.add(0, MENU_CONNECT, 0, R.string.connect).setIcon(android.R.drawable.ic_media_play);
    	}
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case MENU_CONNECT:
            // Launch the DeviceListActivity to see devices and do scan
            Intent serverIntent = new Intent(mContext, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
            return true;
        case MENU_DISCONNECT:
            // disconnect
        	closeConnection();
            return true;
        }
        return false;
    }

    public void openConnection(UsbDevice device) {
		// TODO Auto-generated method stub
    	mPrinter.setAutoReceiveData(true, mHandler);
		isConnected = mPrinter.openConnection(device);
		setButtonState();
	}

    public void closeConnection(){
    	mPrinter.closeConnection();
    	isConnected = false;
    	setButtonState();
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
	        case REQUEST_CONNECT_DEVICE:
	            if (resultCode == Activity.RESULT_OK) {
	            	UsbDevice device = data.getParcelableExtra(UsbManager.EXTRA_DEVICE);
	                mConnectedDeviceName = device.getDeviceName();
	                Toast.makeText(mContext, "REQUEST_CONNECT_DEVICE return", Toast.LENGTH_LONG).show();
	                initPrinter(device);
	            }
	            break;
	        default:
	        	break;
		}
	}

	private void initView(){
		openConnect = (Button) this.findViewById(R.id.btnOpen);
		openConnect.setOnClickListener(this);

		printText = (Button) this.findViewById(R.id.btnPrintText);
		printText.setOnClickListener(this);

		printTable = (Button) this.findViewById(R.id.btnPrintTable);
		printTable.setOnClickListener(this);

		printImage = (Button) this.findViewById(R.id.btnPrintImage);
		printImage.setOnClickListener(this);

		printCustomImage = (Button) this.findViewById(R.id.btnPrintCustomImage);
		printCustomImage.setOnClickListener(this);

		printTitle = (Button) this.findViewById(R.id.btnPrintTitle);
		printTitle.setOnClickListener(this);

		printNote = (Button) this.findViewById(R.id.btnPrintNote);
		printNote.setOnClickListener(this);

		printBarCode = (Button) this.findViewById(R.id.btnPrintBarCode);
		printBarCode.setOnClickListener(this);

		paperWidth_58 = (RadioButton)findViewById(R.id.width_58mm);
		paperWidth_58.setOnClickListener(this);
		is_58mm = paperWidth_58.isChecked();

		paperWidth_80 = (RadioButton)findViewById(R.id.width_80mm);
		paperWidth_80.setOnClickListener(this);

		printer_type_remin = (RadioButton)findViewById(R.id.type_remin);
		printer_type_remin.setOnClickListener(this);
		is_thermal = printer_type_remin.isChecked();

		printer_type_styuls = (RadioButton)findViewById(R.id.type_styuls);
		printer_type_styuls.setOnClickListener(this);

		// Set up the custom title
        mBarCodeNum = (EditText)findViewById(R.id.bar_code_num);

        //编码方式部分
        encodingTypeSpinner = (Spinner)findViewById(R.id.spinner_encoding_type);
        encodingTypeList.add("GBK");
        encodingTypeList.add("BIG5");
        encodingTypeList.add("GB18030");
        encodingTypeList.add("GB2312");

      //第二步：为下拉列表定义一个适配器，用到里前面定义的list。
        encodingTypeAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, encodingTypeList);
        //第三步：为适配器设置下拉列表下拉时的菜单样式。
        encodingTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //第四步：将适配器添加到下拉列表上
        encodingTypeSpinner.setAdapter(encodingTypeAdapter);
        //第五步：为下拉列表设置各种事件的响应，这个事响应菜单被选中
        encodingTypeSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            public void onItemSelected(AdapterView arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                /* 将所选mySpinner 的值带入myTextView 中*/
                //myTextView.setText("您选择的是："+ adapter.getItem(arg2));
            	encodeType = encodingTypeAdapter.getItem(arg2);
            	if(mPrinter != null)
            	{
            		mPrinter.setEncoding(encodeType);
            	}
                /* 将mySpinner 显示*/
                arg0.setVisibility(View.VISIBLE);
            }
            public void onNothingSelected(AdapterView arg0) {
                // TODO Auto-generated method stub
                //myTextView.setText("NONE");
                arg0.setVisibility(View.VISIBLE);
            }
        });


		//条码类型部分设置-------start
        barCodeSpinner = (Spinner)findViewById(R.id.spinner_barcode_type);
        //第一步: 填充list
        list.add("UPC_A");
        list.add("UPC_E");
        list.add("JAN13(EAN13)");
        list.add("JAN8(EAN8)");
        list.add("CODE39");
        list.add("ITF");
        list.add("CODEBAR");
        list.add("CODE93");
        list.add("CODE128");
        list.add("PDF417");
        list.add("DATAMATRIX");
        list.add("QRCODE");

        //第二步：为下拉列表定义一个适配器，用到里前面定义的list。
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);
        //第三步：为适配器设置下拉列表下拉时的菜单样式。
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //第四步：将适配器添加到下拉列表上
        barCodeSpinner.setAdapter(adapter);
        //第五步：为下拉列表设置各种事件的响应，这个事响应菜单被选中
        barCodeSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            public void onItemSelected(AdapterView arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                /* 将所选mySpinner 的值带入myTextView 中*/
                //myTextView.setText("您选择的是："+ adapter.getItem(arg2));
            	String item = adapter.getItem(arg2);
            	if(item.equals("UPC_A") || item.equals("UPC_E") || item.equals("JAN13(EAN13)")){
            		mBarCodeNum.setText("123456789012");
            	}else if(item.equals("CODE128")){
            		mBarCodeNum.setText("No.123456");
            	}else if(item.equals("JAN8(EAN8)")){
            		mBarCodeNum.setText("1234567");
            	}else if(item.equals("PDF417") || item.equals("DATAMATRIX") || item.equals("QRCODE"))
            	{
            		mBarCodeNum.setText("123,45,67,89,01");
            	}else{
            		mBarCodeNum.setText("123456");
            	}
                /* 将mySpinner 显示*/
                arg0.setVisibility(View.VISIBLE);
            }
            public void onNothingSelected(AdapterView arg0) {
                // TODO Auto-generated method stub
                //myTextView.setText("NONE");
                arg0.setVisibility(View.VISIBLE);
            }
        });
        /*下拉菜单弹出的内容选项触屏事件处理*/
//        barCodeSpinner.setOnTouchListener(new Spinner.OnTouchListener(){
//            public boolean onTouch(View v, MotionEvent event) {
//                // TODO Auto-generated method stub
//                /* 将mySpinner 隐藏，不隐藏也可以，看自己爱好*/
//                v.setVisibility(View.INVISIBLE);
//                return false;
//            }
//        });
//        /*下拉菜单弹出的内容选项焦点改变事件处理*/
//        barCodeSpinner.setOnFocusChangeListener(new Spinner.OnFocusChangeListener(){
//        public void onFocusChange(View v, boolean hasFocus) {
//        // TODO Auto-generated method stub
//            v.setVisibility(View.VISIBLE);
//        }
//        });
        //条码类型部分设置-------end

        setButtonState();
	}

	private void setButtonState(){
		openConnect.setText(isConnected ? R.string.disconnect : R.string.connect);
		printTable.setEnabled(isConnected);

		printCustomImage.setEnabled(isConnected);
		printText.setEnabled(isConnected);
		printImage.setEnabled(isConnected);

		printNote.setEnabled(isConnected);
		printTitle.setEnabled(isConnected);
		mBarCodeNum.setEnabled(isConnected);

		printBarCode.setEnabled(isConnected);

		printer_type_remin.setEnabled(isConnected);
		printer_type_styuls.setEnabled(isConnected);

		paperWidth_58.setEnabled(isConnected);
		paperWidth_80.setEnabled(isConnected);
		encodingTypeSpinner.setEnabled(isConnected);
		barCodeSpinner.setEnabled(isConnected);
	}

    // use device to init printer.
    private void initPrinter(UsbDevice device){
    	mUsbDevice = device;
    	mPrinter = new USBPrinter(mContext);
    	if(is_thermal)
    	{
    		mPrinter.setCurrentPrintType(is_58mm ? PrinterType.TIII : PrinterType.T9);
    	}else{
    		mPrinter.setCurrentPrintType(PrinterType.T5);
    	}
    	mPrinter.setEncoding(encodeType);
    	UsbManager mUsbManager = (UsbManager) getSystemService(USB_SERVICE);
    	if(mUsbManager.hasPermission(mUsbDevice))
    	{
    		openConnection(mUsbDevice);
    	}else{
			// 没有权限询问用户是否授予权限
			mUsbManager.requestPermission(mUsbDevice, pendingIntent); // 该代码执行后，系统弹出一个对话框
    	}
    }

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.e(TAG, action);
			Toast.makeText(mContext, action, 0).show();

			if (ACTION_USB_PERMISSION.equals(action)) {
				synchronized (this) {
					UsbDevice mUsbDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
					if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
						openConnection(mUsbDevice);
					} else {
						Log.d(TAG, "permission denied for device " + mUsbDevice);
					}
				}
			}
			else if(UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action))
			{
			}else if(UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)){
				UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
				if(device != null && isConnected && device.equals(mPrinter.getCurrentDevice())){
					closeConnection();
				}
			}
		}
	};

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		if (view == openConnect) {
			if(isConnected){
				closeConnection();
			}else{
				Intent serverIntent = new Intent(this, DeviceListActivity.class);
		        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
			}
		} else if (view == printText) {
			Intent edit_intent = new Intent(mContext, PrinterTextEdit.class);
			startActivity(edit_intent);
		} else if (view == printTable) {
			PrintUtils.printTable(mContext.getResources(), mPrinter);
		} else if (view == printImage) { //打印图形
			PrintUtils.printImage(mContext.getResources(), mPrinter, is_thermal);
		} else if (view == printCustomImage) { //自定义图形
			PrintUtils.printCustomImage(mContext.getResources(), mPrinter, is_thermal);
		} else if (view == printTitle) {
			PrintUtils.printTitle(mContext.getResources(), mPrinter);
		} else if (view == printNote){
			PrintUtils.printNote(mContext.getResources(), mPrinter);
		} else if (view == printBarCode){
			PrintUtils.printBarCode(mPrinter, mBarCodeNum.getText().toString(), barCodeSpinner.getSelectedItem().toString());
		} else if (view == paperWidth_58){
			is_58mm = true;
			paperWidth_58.setChecked(true);
			paperWidth_80.setChecked(false);
			if(is_thermal)
	    	{
	    		mPrinter.setCurrentPrintType(PrinterType.TIII);
	    	}else{
	    		mPrinter.setCurrentPrintType(PrinterType.T5);
	    	}
		} else if (view == paperWidth_80){
			is_58mm = false;
			paperWidth_58.setChecked(false);
			paperWidth_80.setChecked(true);
			if(is_thermal)
	    	{
	    		mPrinter.setCurrentPrintType(PrinterType.T9);
	    	}else{
	    		mPrinter.setCurrentPrintType(PrinterType.T5);
	    	}
		} else if (view == printer_type_remin){
			is_thermal = true;
			printer_type_remin.setChecked(true);
			printer_type_styuls.setChecked(false);
			mPrinter.setCurrentPrintType(PrinterType.TIII);
			if(is_58mm)
	    	{
	    		mPrinter.setCurrentPrintType(PrinterType.TIII);
	    	}else{
	    		mPrinter.setCurrentPrintType(PrinterType.T9);
	    	}
		} else if (view == printer_type_styuls){
			is_thermal = false;
			printer_type_remin.setChecked(false);
			printer_type_styuls.setChecked(true);
			if(is_58mm)
	    	{
	    		mPrinter.setCurrentPrintType(PrinterType.T5);
	    	}else{
	    		mPrinter.setCurrentPrintType(PrinterType.T5);
	    	}
		}
	}

	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			Toast.makeText(mContext, "receive data is: " + String.valueOf(msg.obj) + " ...length is : " + msg.arg1, 0).show();
		}
	};

}