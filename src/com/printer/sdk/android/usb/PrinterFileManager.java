package com.printer.sdk.android.usb;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class PrinterFileManager extends ListActivity {
    private List<String> items = null;
    private List<String> paths = null;
    private String rootPath = "/sdcard";
    private String filePath = "";
    private EditText mPath;

    //private View ov = null; //old view

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.filemanager);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mPath = (EditText) findViewById(R.id.mPath);
        mPath.setEnabled(false);

        Button buttonConfirm = (Button) findViewById(R.id.buttonConfirm);
        buttonConfirm.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	myDialog("");
            }
        });

        //如果是打开文件操作，则disable确定按钮
        if(PrinterTextEdit.flag == 0){
        	buttonConfirm.setEnabled(false);
        }


        Button buttonCancle = (Button) findViewById(R.id.buttonCancle);
        buttonCancle.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        getFileDir(rootPath);
    }
    private void getFileDir(String path) {
        mPath.setText(path);
        items = new ArrayList<String>();
        paths = new ArrayList<String>();
        File f = new File(path);
        File[] files = f.listFiles();
        if (!path.equals(rootPath)) {
            items.add("ROOTDIR");
            paths.add(rootPath);
            items.add("PREVDIR");
            paths.add(f.getParent());
        }
        if(files == null){
        	return;
        }
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            //只有当文件类型为目录或文本文件时(txt或log后缀)才显示，且目录为非隐藏目录
            if (file.isDirectory() && !file.getName().toString().startsWith(".")){
            	items.add(file.getName());
            	paths.add(file.getPath());
            }
            else{
            	String[] subnames = file.getName().toString().split("\\.");
            	if (subnames.length > 1){
            		if(subnames[subnames.length-1].equals("txt") || subnames[subnames.length-1].equals("log")){
            			items.add(file.getName());
            			paths.add(file.getPath());
            		}
            	}
            }
        }
        setListAdapter(new PrinterAdapter(this, items, paths));
//      }catch(ArrayIndexOutOfBoundsException e1){
//        	  System.out.println(e1);
//        	}catch(IndexOutOfBoundsException e2){
//        		System.out.println(e2);
//        	}
    }
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	//super.onListItemClick(l, v, position, id);
        File file = new File(paths.get(position));
        if (file.isDirectory()) { //文件为目录时，点击进入下一层
            getFileDir(paths.get(position));

        } else {
        	//将当前文件的路径更新到地址栏
        	//mPath.setText(paths.get(position));
        	if(PrinterTextEdit.flag == 0){
              Intent data = new Intent(PrinterFileManager.this, PrinterTextEdit.class);
              Bundle bundle = new Bundle();
              bundle.putString("file", paths.get(position));
              data.putExtras(bundle);
              setResult(2, data);
              finish();
        	}
        	else{
        		String[] subn = paths.get(position).split("/");
        		String fn = subn[subn.length-1];
        		myDialog(fn);
        	}
        }

    	//选中文件时背景高亮
//        if(ov == null){
//    		v.setBackgroundColor(Color.BLUE); //选中高亮
//    		ov = v;
//    	}
//        else{
//    		ov.setBackgroundColor(Color.WHITE); //恢复之前的高亮
//    		v.setBackgroundColor(Color.BLUE); //设置当前高亮
//    		ov = v;
//    	}

//        if(ov != null){
//        	ov.setSelected(false);
//        	ov.setBackgroundColor(Color.WHITE);
//        }
//        v.setSelected(true);
//        v.setBackgroundColor(Color.BLUE); //选中高亮
//        ov = v;
	}

    public void myDialog(String n) {
    	Builder dialog = new AlertDialog.Builder(this);
    	LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.dialog, null);
    	dialog.setView(layout);
    	final EditText nameTxt = (EditText)layout.findViewById(R.id.name);
    	if(n != ""){
    		nameTxt.setText(n);
    	}
    	dialog.setPositiveButton(R.string.save_ok, new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int which) {
	    		String namestr = nameTxt.getText().toString();
	    		if(!namestr.equals("")){
					filePath = mPath.getText() + "/" + namestr;
					Intent data = new Intent(PrinterFileManager.this, PrinterTextEdit.class);
					Bundle bundle = new Bundle();
					bundle.putString("file", filePath);
					data.putExtras(bundle);
					setResult(2, data);
					finish();
	    		}
	    		else
	    			Toast.makeText(PrinterFileManager.this, R.string.toast_input_name, 1000).show();
//    	     Intent intent = new Intent();
//    	     Bundle bundle = new Bundle();
//    	     bundle.putString("search", searchC);
//    	     intent.putExtras(bundle);
//    	     intent.setClass(ViewResultActivity.this, SearchResult.class);
//    	     ViewResultActivity.this.startActivity(intent);
    		}
    	});
    	dialog.setNegativeButton(R.string.save_cancel, new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int which) {
    			//finish(); //这将会退回到编辑画面；什么都不做提示框会自动消失
    		}
    	});
    	dialog.show();
	}
}