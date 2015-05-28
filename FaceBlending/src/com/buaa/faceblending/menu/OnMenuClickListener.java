package com.buaa.faceblending.menu;

import android.view.View;
import android.widget.AdapterView;

/**
 * �˵���Ŀѡ�м�����
 * @author maylian.mei
 *
 */
public interface OnMenuClickListener
{
	/**
	 * �˵������Ļ���õķ���
	 * @param parent
	 * @param view
	 * @param position
	 */
	public void onMenuItemClick(AdapterView<?> parent, View view, int position);
	
	/**
	 * �˵����ػ���õķ���
	 */
	public void hideMenu();
}
