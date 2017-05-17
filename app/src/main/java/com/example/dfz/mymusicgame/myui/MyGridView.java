package com.example.dfz.mymusicgame.myui;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

import com.example.dfz.mymusicgame.R;
import com.example.dfz.mymusicgame.model.IWordButtonClickListener;
import com.example.dfz.mymusicgame.model.WordButton;
import com.example.dfz.mymusicgame.ui.MainActivity;
import com.example.dfz.mymusicgame.util.Util;

import java.util.ArrayList;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Created by dfz on 2017/4/22.
 */

public class MyGridView extends GridView {
    private ArrayList<WordButton> mArrayList =new ArrayList<WordButton>();
    private MyGridAdapter mAdapter;
    private Context mContext;
    private Animation mScaleAnimation;
    private IWordButtonClickListener iWordButtonClickListener;

    public MyGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext=context;
        mAdapter=new MyGridAdapter();

        this.setAdapter(mAdapter);
    }

    public void updateData(ArrayList<WordButton> list){
        mArrayList =list;
        setAdapter(mAdapter);
    }

    class MyGridAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return mArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final WordButton holder;
            mScaleAnimation = AnimationUtils.loadAnimation(mContext,R.anim.scale);
            if(convertView==null){
                convertView= Util.getView(mContext, R.layout.self_ui_gridview_item);
                holder=mArrayList.get(position);
                holder.mIndex=position;
                holder.mViewButton= (Button)convertView.findViewById(R.id.item_btn);
                holder.mViewButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        iWordButtonClickListener.onWordButtonClick(holder);
                    }
                });
                convertView.setTag(holder);
            }else {
                holder= (WordButton) convertView.getTag();
            }
//            holder.mViewButton=new Button(mContext);
            holder.mViewButton.setText(holder.mWordString);
            mScaleAnimation.setStartOffset(position*100);
            holder.mViewButton.startAnimation(mScaleAnimation);
            return convertView;
        }
    }
    public void registOnWordButtonClick(IWordButtonClickListener listener){
        iWordButtonClickListener=listener;
    }
}
