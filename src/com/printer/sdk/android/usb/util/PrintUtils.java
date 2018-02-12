package com.printer.sdk.android.usb.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.printer.sdk.android.usb.R;
import com.printer.sdk.api.FontProperty;
import com.printer.sdk.api.PrintGraphics;
import com.printer.sdk.api.PrinterType;
import com.printer.sdk.api.Table;
import com.printer.sdk.api.USBPrinter;

public class PrintUtils {
	public static void printNote(Resources resources, USBPrinter mPrinter) {
		mPrinter.init();
		StringBuffer sb = new StringBuffer();
		//mPrinter.setPrinter(USBPrinter.COMM_LINE_HEIGHT, 80);

		mPrinter.setPrinter(USBPrinter.COMM_ALIGN, USBPrinter.COMM_ALIGN_CENTER);
		// 字号横向纵向扩大一倍
		mPrinter.setCharacterMultiple(1, 1);
		mPrinter.printText(resources.getString(R.string.shop_company_title) + "\n");

		mPrinter.setPrinter(USBPrinter.COMM_ALIGN, USBPrinter.COMM_ALIGN_LEFT);
		// 字号使用默认
		mPrinter.setCharacterMultiple(0, 0);
		sb.append(resources.getString(R.string.shop_num) + "574001\n");
		sb.append(resources.getString(R.string.shop_receipt_num) + "S00003169\n");
		sb.append(resources.getString(R.string.shop_cashier_num) + "s004_s004\n");

		sb.append(resources.getString(R.string.shop_receipt_date) + "2012-06-17\n");
		sb.append(resources.getString(R.string.shop_print_time) + "2012-06-17 13:37:24\n");
		mPrinter.printText(sb.toString()); //打印

		printTable1(resources, mPrinter); //打印表格

		sb = new StringBuffer();
		if (mPrinter.getCurrentPrintType() == PrinterType.TIII || mPrinter.getCurrentPrintType() == PrinterType.T5) {
			sb.append(resources.getString(R.string.shop_goods_number) + "                6.00\n");
			sb.append(resources.getString(R.string.shop_goods_total_price) + "                35.00\n");
			sb.append(resources.getString(R.string.shop_payment) + "                100.00\n");
			sb.append(resources.getString(R.string.shop_change) + "                65.00\n");
		}else{
			sb.append(resources.getString(R.string.shop_goods_number) + "                                6.00\n");
			sb.append(resources.getString(R.string.shop_goods_total_price) + "                                35.00\n");
			sb.append(resources.getString(R.string.shop_payment) + "                                100.00\n");
			sb.append(resources.getString(R.string.shop_change) + "                                65.00\n");
		}

		sb.append(resources.getString(R.string.shop_company_name) + "\n");
		sb.append(resources.getString(R.string.shop_company_site) + "www.jiangsu1510.com\n");
		sb.append(resources.getString(R.string.shop_company_address) + "\n");
		sb.append(resources.getString(R.string.shop_company_tel) + "0574-88222999\n");
		sb.append(resources.getString(R.string.shop_Service_Line) + "4008-567-567 \n");
		if (mPrinter.getCurrentPrintType() == PrinterType.TIII || mPrinter.getCurrentPrintType() == PrinterType.T5) {
			sb.append("==============================\n");
		}else{
			sb.append("==============================================\n");
		}
		mPrinter.printText(sb.toString());

		mPrinter.setPrinter(USBPrinter.COMM_ALIGN, USBPrinter.COMM_ALIGN_CENTER);
		mPrinter.setCharacterMultiple(0, 1);
		mPrinter.printText(resources.getString(R.string.shop_thanks) + "\n");
		mPrinter.printText(resources.getString(R.string.shop_demo) + "\n\n\n");
	}

	public static void printImage(Resources resources, USBPrinter mPrinter, boolean is_thermal) {
		Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.goodwork);
		//getCanvasImage方法获得画布上所画的图像,printImage方法打印图像.
		if(is_thermal)
		{
			mPrinter.printImage(bitmap);
		}
		else
		{
			mPrinter.printImageDot(bitmap, 1, 0);
		}
		mPrinter.setPrinter(USBPrinter.COMM_PRINT_AND_WAKE_PAPER_BY_LINE, 1); //换1行
	}

	public static void printCustomImage(Resources resources, USBPrinter mPrinter, boolean is_thermal) {
		mPrinter.init();
		// TODO Auto-generated method stub

		PrintGraphics pg = new PrintGraphics();
		/*
		 * 初始化画布，画布的宽度为变量，一般有两个选择： 1、58mm型号打印机实际可用是48mm，48*8=384px
		 * 2、80mm型号打印机实际可用是72mm，72*8=576px
		 * 因为画布的高度是无限制的，但从内存分配方面考虑要小于4M比较合适， 所以预置为宽度的10倍。
		 */
		//pg.initCanvas(576);
		/*
		 * 初始化画笔，默认属性有： 1、消除锯齿 2、设置画笔颜色为黑色 3、设置图形为空心模式
		 */
		//pg.initPaint();

		// init 方法包含pg.initCanvas(550)和pg.initPaint(), T9打印宽度为72mm,其他为47mm.
		pg.init(mPrinter.getCurrentPrintType());

		/*
		 * 插入图片函数: drawImage(float x, float y, String path)
		 * 其中(x,y)是指插入图片的左上顶点坐标。
		 */
		pg.drawImage(0, 0, "/sdcard/sp1.jpg");

		// 实例化字体类
		FontProperty fp = new FontProperty();

		/*
		 * 初始化字体函数：
		 * setFont(boolean bBold, boolean bItalic, boolean
		 * bUnderLine, boolean bStrikeout, int iSize, Typeface sFace)
		 * 第1个参数：是否粗体（取值为true/false） 经测试,若单独设置中文为粗体,打印不出来.
		 * 第2个参数：是否斜体（取值为true/false）
		 * 第3个参数：是否下划线（取值为true/false）
		 * 第4个参数：是否删除线（取值为true/false）
		 * 第5个参数：字体大小（取值为一整数）
		 * 第6个参数：字体类型（一般设置为null，表示使用系统默认字体）
		 */
		fp.setFont(false, false, false, false, 28, null);
		// 通过初始化的字体属性设置画笔
		pg.setFontProperty(fp);
		

		// 设置线条宽度函数：setLinewidth(float w)
		pg.setLineWidth(8);
		/*
		 * 绘线条函数：drawLine(float x1, float y1, float x2, float y2)
		 * 其中(x1,y1)表示起点坐标，(x2,y2)表示终点坐标
		 */
		pg.drawLine(0, 180, 576, 180);
		// 注意：绘文本的时候，一定要将线条宽度恢复到0

		pg.setLineWidth(0);
		fp.setFont(false, false, false, false, 18, null);
		pg.setFontProperty(fp);
		pg.drawText(20, 200, resources.getString(R.string.text_common_point18));

		fp.setFont(false, false, true, false, 18, null);
		pg.setFontProperty(fp);
		pg.drawText(20, 220, resources.getString(R.string.text_underLine_point18));

		fp.setFont(false, false, false, true, 18, null);
		pg.setFontProperty(fp);
		pg.drawText(20, 240, resources.getString(R.string.text_strike_point18));

		fp.setFont(true, true, true, true, 24, null);
		pg.setFontProperty(fp);
		pg.drawText(20, 270, resources.getString(R.string.text_point24));



		pg.setLineWidth(2);
		pg.drawLine(0, 300, 576, 300);
		/*
		 * 绘制椭圆或正圆函数：drawEllips(float x1, float y1, float x2, float y2)
		 * 其中(x1,y1)是圆外接矩形或方型的左上点坐标 (x2,y2)是圆外接矩形或方型的右下点坐标
		 */
		pg.drawEllips(20, 320, 340, 480);
		pg.drawEllips(360, 320, 520, 480);
		/*
		 * 绘制矩形或方型函数：drawRectangle(float x1, float y1, float x2, float
		 * y2) 其中(x1,y1)是矩形或方型的左上点坐标 (x2,y2)是矩形或方型的右下点坐标
		 */
		pg.drawRectangle(20, 500, 340, 660);
		pg.drawRectangle(360, 500, 520, 660);
		pg.drawLine(0, 680, 576, 680);
		pg.setLineWidth(5);
		pg.drawEllips(20, 700, 340, 860);
		pg.drawEllips(360, 700, 520, 860);
		pg.drawRectangle(20, 880, 340, 1040);
		pg.drawRectangle(360, 880, 520, 1040);

		pg.setLineWidth(2);
		pg.drawLine(0, 1050, 576, 1050);

		pg.setLineWidth(0);
		fp.setFont(false, false, false, false, 18, null);
		pg.setFontProperty(fp);
		pg.drawText(50, 400, resources.getString(R.string.company_name));
		fp.setFont(false, false, false, false, 24, null);
		pg.setFontProperty(fp);
		pg.drawText(160, 580, resources.getString(R.string.company_name));

		// pg.printPng();

		//热敏与针打图形处理不一样
		if(is_thermal)
		{
			mPrinter.printImage(pg.getCanvasImage());
			mPrinter.setPrinter(USBPrinter.COMM_PRINT_AND_WAKE_PAPER_BY_LINE,2);
		}
		else
		{
			mPrinter.printImageDot(pg.getCanvasImage(),1,35);
		}
	}

	public static void printTable(Resources resources, USBPrinter mPrinter) {
		mPrinter.init();
		// TODO Auto-generated method stub
		//getTable方法:参数1,以特定符号分隔的列名; 2,列名分隔符;
		//3,各列所占字符宽度,中文2个,英文1个. 默认字体总共不要超过48
		//表格超出部分会另起一行打印.若想手动换行,可加\n.
		mPrinter.setCharacterMultiple(0, 0);
		String column = resources.getString(R.string.note_title);
		Table table;
		if (mPrinter.getCurrentPrintType() == PrinterType.TIII || mPrinter.getCurrentPrintType() == PrinterType.T5)
		{
			table = new Table(column, ";",new int[]{12,6,6,6});
		}else{
			table = new Table(column, ";",new int[]{18,8,8,8});
		}

		table.add("1," + resources.getString(R.string.coffee) + ";	    2.00;    5.00;   10.00");
		table.add("2," + resources.getString(R.string.tableware) + ";   2.00;   5.00;    10.00");
		table.add("3," + resources.getString(R.string.frog) + ";   1.00;   68.00;   68.00");
		table.add("4," + resources.getString(R.string.cucumber) + ";   1.00;   4.00;    4.00");
		table.add("5," + resources.getString(R.string.peanuts) + "; 1.00;   5.00;    5.00");
		table.add("6," + resources.getString(R.string.rice) + ";	    1.00;   2.00;    2.00");
		mPrinter.printTable(table);
	}

	public static void printTable1(Resources resources, USBPrinter mPrinter) {
		mPrinter.init();
		// TODO Auto-generated method stub
		String column = resources.getString(R.string.note_title);
		Table table;
		if (mPrinter.getCurrentPrintType() == PrinterType.TIII || mPrinter.getCurrentPrintType() == PrinterType.T5)
		{
			table = new Table(column, ";",new int[]{12,6,6,7});
		}else{
			table = new Table(column, ";",new int[]{20,10,8,10});
		}
		table.add("0009480\n" + resources.getString(R.string.bags) + ";10.00;1;10.00");
		table.add("0007316\n" + resources.getString(R.string.hook) + ";5.00;2;10.00");
		table.add("0007392\n" + resources.getString(R.string.umbrella) + ";5.00;3;15.00");
		table.setHasSeparator(true);
		mPrinter.printTable(table);
	}

	public static void printTitle(Resources resources, USBPrinter mPrinter) {
		// TODO Auto-generated method stub
		mPrinter.init();

		//setTitle方法:参数1,主标题; 2,副标题; 3,logo
		//打印效果为logo与主标题一行,左为logo,右为主标题; 副标题另起一行. 均居中显示.
		mPrinter.setTitle(resources.getString(R.string.company_name), resources.getString(R.string.company_sub_title), BitmapFactory.decodeResource(resources, R.drawable.logo));
		//mPrinter.printText("\n1.有logo,主标题和副标题:\n");
		mPrinter.printTitle();

		mPrinter.setTitle(null, resources.getString(R.string.company_sub_title), BitmapFactory.decodeResource(resources, R.drawable.logo));
		//mPrinter.printText("\n2.有logo和副标题:\n");
		mPrinter.printTitle();

		mPrinter.setTitle(resources.getString(R.string.company_name), null, BitmapFactory.decodeResource(resources, R.drawable.logo));
		//mPrinter.printText("\n3.有logo和主标题:\n");
		mPrinter.printTitle();

		mPrinter.setTitle(resources.getString(R.string.company_name), resources.getString(R.string.company_sub_title), null);
		//mPrinter.printText("\n4.有主标题和副标题:\n");
		mPrinter.printTitle();

		mPrinter.setPrinter(USBPrinter.COMM_PRINT_AND_WAKE_PAPER_BY_LINE, 2);
	}

	public static void printText(USBPrinter mPrinter, String content) {
		mPrinter.init();
		//设置行高
		//mPrinter.setPrinter(USBPrinter.COMM_LINE_HEIGHT, 80);

		//打印文本
		mPrinter.printText(content);
		//换行
		mPrinter.setPrinter(USBPrinter.COMM_PRINT_AND_NEWLINE);
	}

	public static void printBarCode(USBPrinter mPrinter, String codeNum, String type) {
		mPrinter.init();
		// TODO Auto-generated method stub
		Log.i("12345", "xxxxxxxxxxxxxxxxxxx");
		mPrinter.setCharacterMultiple(0, 0);
		/**
		 * 设置左边距,nL,nH
		 * 设置宽度为(nL+nH*256)* 横向移动单位.
		 * 设置左边距对打印条码的注释位置有影响.
		 */
		mPrinter.setLeftMargin(15, 0);
		//mPrinter.setPrinter(USBPrinter.COMM_ALIGN,USBPrinter.COMM_ALIGN_LEFT);

		/**
		 * 参数1: 设置条码横向宽度 2<=n<=6,默认为2
		 * 参数2: 设置条码高度 1<=n<=255,默认162
		 * 参数3: 设置条码注释打印位置.0不打印,1上方,2下方,3上下方均有,默认为0
		 * 参数4: 设置条码类型.USBPrinter.BAR_CODE_TYPE_ 开头的常量,默认为CODE128
		 */
		byte codeType = USBPrinter.BAR_CODE_TYPE_CODE128;
		if(type.equals("UPC_A")){
			codeType = USBPrinter.BAR_CODE_TYPE_UPC_A;
			mPrinter.setBarCode(2, 150, 2, codeType);
		}else if(type.equals("UPC_E")){
			codeType = USBPrinter.BAR_CODE_TYPE_UPC_E;
			mPrinter.setBarCode(2, 150, 2, codeType);
		}else if(type.equals("JAN13(EAN13)")){
			codeType = USBPrinter.BAR_CODE_TYPE_JAN13;
			mPrinter.setBarCode(2, 150, 2, codeType);
		}else if(type.equals("JAN8(EAN8)")){
			codeType = USBPrinter.BAR_CODE_TYPE_JAN8;
			mPrinter.setBarCode(2, 150, 2, codeType);
		}else if(type.equals("CODE39")){
			codeType = USBPrinter.BAR_CODE_TYPE_CODE39;
			mPrinter.setBarCode(2, 150, 2, codeType);
		}else if(type.equals("ITF")){
			codeType = USBPrinter.BAR_CODE_TYPE_ITF;
			mPrinter.setBarCode(2, 150, 2, codeType);
		}else if(type.equals("CODEBAR")){
			codeType = USBPrinter.BAR_CODE_TYPE_CODABAR;
			mPrinter.setBarCode(2, 150, 2, codeType);
		}else if(type.equals("CODE93")){
			codeType = USBPrinter.BAR_CODE_TYPE_CODE93;
			mPrinter.setBarCode(2, 150, 2, codeType);
		}else if(type.equals("CODE128")){
			codeType = USBPrinter.BAR_CODE_TYPE_CODE128;
			mPrinter.setBarCode(2, 150, 2, codeType);
		}else if(type.equals("PDF417")){
			codeType = USBPrinter.BAR_CODE_TYPE_PDF417;
			mPrinter.setBarCode(2, 3, 6, codeType);
		}else if(type.equals("DATAMATRIX")){
			codeType = USBPrinter.BAR_CODE_TYPE_DATAMATRIX;
			mPrinter.setBarCode(2, 3, 6, codeType);
		}else if(type.equals("QRCODE")){
			codeType = USBPrinter.BAR_CODE_TYPE_QRCODE;
			mPrinter.setBarCode(2, 3, 6, codeType);
		}

		mPrinter.printBarCode(codeNum);
	}
}
