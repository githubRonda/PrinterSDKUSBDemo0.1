package com.printer.sdk.android.usb;

import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.printer.sdk.api.USBPrinter;


/**
 * This Activity appears as a dialog. It lists any paired devices and
 * devices detected in the area after discovery. When a device is chosen
 * by the user, the MAC address of the device is sent back to the parent
 * Activity in the result Intent.
 */
public class DeviceListActivity extends Activity {
    private static final String TAG = "DeviceListActivity";
    // Member fields
    private ArrayAdapter<String> mFoundDevicesArrayAdapter;
    private ListView mFoundDevicesListView;
    private Button scanButton;
    private HashMap<String, UsbDevice> usbPrinterMap = new HashMap<String, UsbDevice>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Setup the window
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.device_list);
        setTitle(R.string.select_device);

        // Set result CANCELED incase the user backs out
        setResult(Activity.RESULT_CANCELED);

        // Initialize the button to perform device discovery
        scanButton = (Button) findViewById(R.id.button_scan);
        scanButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                doDiscovery();
                //v.setVisibility(View.GONE);
                v.setEnabled(false);
            }
        });

        // Initialize array adapters. One for already paired devices and
        // one for newly discovered devices
        mFoundDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
        // mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);

        // Find and set up the ListView for paired devices
        mFoundDevicesListView = (ListView) findViewById(R.id.paired_devices);
        mFoundDevicesListView.setAdapter(mFoundDevicesArrayAdapter);
        mFoundDevicesListView.setOnItemClickListener(mDeviceClickListener);
    }

    /**
     * Start device discover with the BluetoothAdapter
     */
    private void doDiscovery() {
        Log.d(TAG, "doDiscovery()");

        // Indicate scanning in the title
        setProgressBarIndeterminateVisibility(true);
        setTitle(R.string.scanning);
        mFoundDevicesArrayAdapter.clear();
        mFoundDevicesListView.setEnabled(true);

        // Turn on sub-title for new devices
        //findViewById(R.id.ssssss).setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        USBPrinter printer = new USBPrinter(getApplicationContext());
        printer.getDeviceList(mHandler);
    }

    public Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case USBPrinter.Handler_Get_Device_List_Found:
				UsbDevice device = (UsbDevice)msg.obj;
				String itemName = device.getDeviceName() + "\nVendor Id: " + device.getVendorId() + " Product Id: " + device.getProductId();
				mFoundDevicesArrayAdapter.add(itemName);
				usbPrinterMap.put(itemName, device);
				break;
			case USBPrinter.Handler_Get_Device_List_No_Device:
			case USBPrinter.Handler_Get_Device_List_Completed:
			case USBPrinter.Handler_Get_Device_List_Error:
				setProgressBarIndeterminateVisibility(false);
                setTitle(R.string.select_device);
                if (mFoundDevicesArrayAdapter.getCount() == 0) {
                    String noDevices = getResources().getText(R.string.none_found).toString();
                    mFoundDevicesArrayAdapter.add(noDevices);
                    mFoundDevicesListView.setEnabled(false);
                }
                scanButton.setEnabled(true);
				break;

			default:
				break;
			}
		}
    };

    private void returnToPreviousActivity(UsbDevice device)
    {
        // Create the result Intent and include the MAC address
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelable(UsbManager.EXTRA_DEVICE, device);
        // Set result and finish this Activity
        intent.putExtras(bundle);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
    // The on-click listener for all devices in the ListViews
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
        	// Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
        	returnToPreviousActivity(usbPrinterMap.get(info));
        }
    };

    private OnItemLongClickListener mDeviceLongClickListener = new OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			//如果返回true 就不会调用下面onCreateContextMenu事件
			return false;
		}
	};

	//长按弹出菜单选项的点击选择事件监听
	private final OnMenuItemClickListener mOnMenuItemClickListener = new OnMenuItemClickListener(){
		public boolean onMenuItemClick(MenuItem item) {
			String info = ((TextView)((AdapterContextMenuInfo)item.getMenuInfo()).targetView).getText().toString();
			switch (item.getItemId()) {
			case 0://连接
				returnToPreviousActivity(usbPrinterMap.get(info));
				break;
			}
			return false;
		}
	};
}
