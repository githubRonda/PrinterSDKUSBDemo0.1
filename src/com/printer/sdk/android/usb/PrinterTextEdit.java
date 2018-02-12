package com.printer.sdk.android.usb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.printer.sdk.api.USBPrinter;
import com.printer.sdk.android.usb.util.PrintUtils;


public class PrinterTextEdit extends Activity {
	Button btnOpen;
	Button btnSave;
	Button btnSaveAs;
	Button btnPrint;
	EditText editTxt;
	USBPrinter mPrinter = null;

	public static int flag = 0;
	private String curFilePath = "";

	public static final int OPEN_RESULT_CODE = 1;
	public static final int SAVE_RESULT_CODE = 2;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit);

		btnOpen = (Button) this.findViewById(R.id.btnOpen);
		btnOpen.setOnClickListener(new ClickEvent());
		btnSave = (Button) this.findViewById(R.id.btnSave);
		btnSave.setOnClickListener(new ClickEvent());
		btnSaveAs = (Button) this.findViewById(R.id.btnSaveAs);
		btnSaveAs.setOnClickListener(new ClickEvent());
		btnPrint = (Button) this.findViewById(R.id.btnPrint);
		btnPrint.setOnClickListener(new ClickEvent());
		editTxt = (EditText) findViewById(R.id.editTxt);

		mPrinter = PrinterSDKDemo_Plus_USB.mPrinter;
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	class ClickEvent implements View.OnClickListener {
		public void onClick(View v) {
			if (v == btnOpen) {
                Intent intent = new Intent(PrinterTextEdit.this,PrinterFileManager.class);
                startActivityForResult(intent, OPEN_RESULT_CODE);
                flag = 0;
			} else if (v == btnSave) {
				if(!editTxt.getText().toString().equals("")){ //ֻ�����ݲ�Ϊ���ǲű���
					if(!curFilePath.equals("")){ //����ļ��Ѿ����ڣ�ֱ�ӱ���
		            	File file = new File(curFilePath);
		            	try {
		            		OutputStream outstream = new FileOutputStream(file);
		            		OutputStreamWriter out = new OutputStreamWriter(outstream);
		            		out.write(editTxt.getText().toString());
		            		out.close();
		            		Toast.makeText(PrinterTextEdit.this, R.string.toast_save_success, 1000).show();
		            	} catch (java.io.IOException e) {
		            		e.printStackTrace();
		            	}
					}
					else{ //���û��ָ���ļ�������fileSeletorѡ��·������ʱ��SaveAsû����
						Intent intent = new Intent(PrinterTextEdit.this,PrinterFileManager.class);
						startActivityForResult(intent, SAVE_RESULT_CODE);
						flag = 1;
					}
				}
				else //Ϊ��ʱ��ʾ
					Toast.makeText(PrinterTextEdit.this, R.string.toast_save_null, 1000).show();
			} else if (v == btnSaveAs) {
				if(!editTxt.getText().toString().equals("")){
					Intent intent = new Intent(PrinterTextEdit.this,PrinterFileManager.class);
					startActivityForResult(intent, SAVE_RESULT_CODE);
					flag = 1;
				}
				else
					Toast.makeText(PrinterTextEdit.this, R.string.toast_save_null, 1000).show();
			} else if (v == btnPrint) {
				String mystr = editTxt.getText().toString();
				if(!mystr.equals("")){
					//System.out.println("++++++++++++++ mystr: " + mystr);
					PrintUtils.printText(mPrinter, mystr);
				}
				else{
					Toast.makeText(PrinterTextEdit.this, R.string.toast_print_null, 1000).show();
				}
			}
		}
	}

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    	Bundle bundle = null;

        if(OPEN_RESULT_CODE == requestCode){
            if(data!=null && (bundle=data.getExtras())!=null){
            	String filePath = bundle.getString("file");
            	String content = "";
            	//���ļ�
            	File file = new File(filePath);
            	if (!file.isDirectory()){{
            	  try {
            		InputStream instream = new FileInputStream(file);
            		if (instream != null) {
            			InputStreamReader inputreader = new InputStreamReader(instream);
            			BufferedReader buffreader = new BufferedReader(inputreader);
            			String line;
            			while (( line = buffreader.readLine()) != null) {
            				content += line + "\n";
            			}
            			editTxt.setText(content);
            			this.setTitle(filePath); //����titleΪ�ļ���·��
            			curFilePath = filePath;
            		}
            		instream.close();
            	  } catch (java.io.FileNotFoundException e) {
            	  } catch (IOException e) {
            		e.printStackTrace();
            	  }
            	}
            }
        }
        if(SAVE_RESULT_CODE == requestCode){
        	if(data!=null && (bundle=data.getExtras())!=null){
            	String filePath = bundle.getString("file");
            	//û��ָ�����ļ���û�к�׺�����Զ�����Ϊ.txt��ʽ
            	if(!filePath.endsWith(".txt") && !filePath.endsWith(".log"))
            		filePath += ".txt";
            	//�����ļ�
            	File file = new File(filePath);
            	try {
            		OutputStream outstream = new FileOutputStream(file);
            		OutputStreamWriter out = new OutputStreamWriter(outstream);
            		out.write(editTxt.getText().toString());
            		out.close();
            		this.setTitle(filePath); //����titleΪ�ļ���·��
            		curFilePath = filePath;
            	} catch (java.io.IOException e) {
            		e.printStackTrace();
            	}
        	}
        }
    }
    }
}