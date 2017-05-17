package com.example.dfz.mymusicgame.model;

import android.view.LayoutInflater;
import android.widget.Button;

import com.example.dfz.mymusicgame.R;
import com.example.dfz.mymusicgame.ui.MainActivity;

/**
 * ������������
 * 
 * @author Li Jian
 *
 */
public class WordButton {

	public int mIndex;
	public boolean mIsVisiable;
	public String mWordString;
	
	public Button mViewButton;
	
	public WordButton() {
		mIsVisiable = true;
		mWordString = "";
	}
}
