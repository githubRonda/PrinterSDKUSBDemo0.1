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
		// �ֺź�����������һ��
		mPrinter.setCharacterMultiple(1, 1);
		mPrinter.printText(resources.getString(R.string.shop_company_title) + "\n");

		mPrinter.setPrinter(USBPrinter.COMM_ALIGN, USBPrinter.COMM_ALIGN_LEFT);
		// �ֺ�ʹ��Ĭ��
		mPrinter.setCharacterMultiple(0, 0);
		sb.append(resources.getString(R.string.shop_num) + "574001\n");
		sb.append(resources.getString(R.string.shop_receipt_num) + "S00003169\n");
		sb.append(resources.getString(R.string.shop_cashier_num) + "s004_s004\n");

		sb.append(resources.getString(R.string.shop_receipt_date) + "2012-06-17\n");
		sb.append(resources.getString(R.string.shop_print_time) + "2012-06-17 13:37:24\n");
		mPrinter.printText(sb.toString()); //��ӡ

		printTable1(resources, mPrinter); //��ӡ���

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
		//getCanvasImage������û�����������ͼ��,printImage������ӡͼ��.
		if(is_thermal)
		{
			mPrinter.printImage(bitmap);
		}
		else
		{
			mPrinter.printImageDot(bitmap, 1, 0);
		}
		mPrinter.setPrinter(USBPrinter.COMM_PRINT_AND_WAKE_PAPER_BY_LINE, 1); //��1��
	}

	public static void printCustomImage(Resources resources, USBPrinter mPrinter, boolean is_thermal) {
		mPrinter.init();
		// TODO Auto-generated method stub

		PrintGraphics pg = new PrintGraphics();
		/*
		 * ��ʼ�������������Ŀ��Ϊ������һ��������ѡ�� 1��58mm�ͺŴ�ӡ��ʵ�ʿ�����48mm��48*8=384px
		 * 2��80mm�ͺŴ�ӡ��ʵ�ʿ�����72mm��72*8=576px
		 * ��Ϊ�����ĸ߶��������Ƶģ������ڴ���䷽�濼��ҪС��4M�ȽϺ��ʣ� ����Ԥ��Ϊ��ȵ�10����
		 */
		//pg.initCanvas(576);
		/*
		 * ��ʼ�����ʣ�Ĭ�������У� 1��������� 2�����û�����ɫΪ��ɫ 3������ͼ��Ϊ����ģʽ
		 */
		//pg.initPaint();

		// init ��������pg.initCanvas(550)��pg.initPaint(), T9��ӡ���Ϊ72mm,����Ϊ47mm.
		pg.init(mPrinter.getCurrentPrintType());

		/*
		 * ����ͼƬ����: drawImage(float x, float y, String path)
		 * ����(x,y)��ָ����ͼƬ�����϶������ꡣ
		 */
		pg.drawImage(0, 0, "/sdcard/sp1.jpg");

		// ʵ����������
		FontProperty fp = new FontProperty();

		/*
		 * ��ʼ�����庯����
		 * setFont(boolean bBold, boolean bItalic, boolean
		 * bUnderLine, boolean bStrikeout, int iSize, Typeface sFace)
		 * ��1���������Ƿ���壨ȡֵΪtrue/false�� ������,��������������Ϊ����,��ӡ������.
		 * ��2���������Ƿ�б�壨ȡֵΪtrue/false��
		 * ��3���������Ƿ��»��ߣ�ȡֵΪtrue/false��
		 * ��4���������Ƿ�ɾ���ߣ�ȡֵΪtrue/false��
		 * ��5�������������С��ȡֵΪһ������
		 * ��6���������������ͣ�һ������Ϊnull����ʾʹ��ϵͳĬ�����壩
		 */
		fp.setFont(false, false, false, false, 28, null);
		// ͨ����ʼ���������������û���
		pg.setFontProperty(fp);
		

		// ����������Ⱥ�����setLinewidth(float w)
		pg.setLineWidth(8);
		/*
		 * ������������drawLine(float x1, float y1, float x2, float y2)
		 * ����(x1,y1)��ʾ������꣬(x2,y2)��ʾ�յ�����
		 */
		pg.drawLine(0, 180, 576, 180);
		// ע�⣺���ı���ʱ��һ��Ҫ��������Ȼָ���0

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
		 * ������Բ����Բ������drawEllips(float x1, float y1, float x2, float y2)
		 * ����(x1,y1)��Բ��Ӿ��λ��͵����ϵ����� (x2,y2)��Բ��Ӿ��λ��͵����µ�����
		 */
		pg.drawEllips(20, 320, 340, 480);
		pg.drawEllips(360, 320, 520, 480);
		/*
		 * ���ƾ��λ��ͺ�����drawRectangle(float x1, float y1, float x2, float
		 * y2) ����(x1,y1)�Ǿ��λ��͵����ϵ����� (x2,y2)�Ǿ��λ��͵����µ�����
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

		//���������ͼ�δ���һ��
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
		//getTable����:����1,���ض����ŷָ�������; 2,�����ָ���;
		//3,������ռ�ַ����,����2��,Ӣ��1��. Ĭ�������ܹ���Ҫ����48
		//��񳬳����ֻ�����һ�д�ӡ.�����ֶ�����,�ɼ�\n.
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

		//setTitle����:����1,������; 2,������; 3,logo
		//��ӡЧ��Ϊlogo��������һ��,��Ϊlogo,��Ϊ������; ����������һ��. ��������ʾ.
		mPrinter.setTitle(resources.getString(R.string.company_name), resources.getString(R.string.company_sub_title), BitmapFactory.decodeResource(resources, R.drawable.logo));
		//mPrinter.printText("\n1.��logo,������͸�����:\n");
		mPrinter.printTitle();

		mPrinter.setTitle(null, resources.getString(R.string.company_sub_title), BitmapFactory.decodeResource(resources, R.drawable.logo));
		//mPrinter.printText("\n2.��logo�͸�����:\n");
		mPrinter.printTitle();

		mPrinter.setTitle(resources.getString(R.string.company_name), null, BitmapFactory.decodeResource(resources, R.drawable.logo));
		//mPrinter.printText("\n3.��logo��������:\n");
		mPrinter.printTitle();

		mPrinter.setTitle(resources.getString(R.string.company_name), resources.getString(R.string.company_sub_title), null);
		//mPrinter.printText("\n4.��������͸�����:\n");
		mPrinter.printTitle();

		mPrinter.setPrinter(USBPrinter.COMM_PRINT_AND_WAKE_PAPER_BY_LINE, 2);
	}

	public static void printText(USBPrinter mPrinter, String content) {
		mPrinter.init();
		//�����и�
		//mPrinter.setPrinter(USBPrinter.COMM_LINE_HEIGHT, 80);

		//��ӡ�ı�
		mPrinter.printText(content);
		//����
		mPrinter.setPrinter(USBPrinter.COMM_PRINT_AND_NEWLINE);
	}

	public static void printBarCode(USBPrinter mPrinter, String codeNum, String type) {
		mPrinter.init();
		// TODO Auto-generated method stub
		Log.i("12345", "xxxxxxxxxxxxxxxxxxx");
		mPrinter.setCharacterMultiple(0, 0);
		/**
		 * ������߾�,nL,nH
		 * ���ÿ��Ϊ(nL+nH*256)* �����ƶ���λ.
		 * ������߾�Դ�ӡ�����ע��λ����Ӱ��.
		 */
		mPrinter.setLeftMargin(15, 0);
		//mPrinter.setPrinter(USBPrinter.COMM_ALIGN,USBPrinter.COMM_ALIGN_LEFT);

		/**
		 * ����1: ������������� 2<=n<=6,Ĭ��Ϊ2
		 * ����2: ��������߶� 1<=n<=255,Ĭ��162
		 * ����3: ��������ע�ʹ�ӡλ��.0����ӡ,1�Ϸ�,2�·�,3���·�����,Ĭ��Ϊ0
		 * ����4: ������������.USBPrinter.BAR_CODE_TYPE_ ��ͷ�ĳ���,Ĭ��ΪCODE128
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
