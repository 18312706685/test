package com.example.dfz.mymusicgame.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.dfz.mymusicgame.R;
import com.example.dfz.mymusicgame.model.IAlertDialogButtonListener;

public class Util {

	private static AlertDialog mAlertDialog;
	public static View getView(Context context, int layoutId) {
		LayoutInflater inflater = (LayoutInflater) context.
				getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View layout = inflater.inflate(layoutId, null);

		return layout;
	}

	public static void startActivity(Context context, Class destination) {
		Intent intent = new Intent();
		intent.setClass(context, destination);
		context.startActivity(intent);

		((Activity) context).finish();
	}

	public static void showDialog(Context context, String message, final IAlertDialogButtonListener mListener) {
		View dialogview = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.myTransparent);
		dialogview = getView(context, R.layout.dialog_view);
		;
		ImageButton btnOkView = (ImageButton) dialogview.findViewById(R.id.buytip_btn_ok);
		ImageButton btnCancelView = (ImageButton) dialogview.findViewById(R.id.buytip_btn_cancel);
		TextView textMessageView = (TextView) dialogview.findViewById(R.id.text_dialog_message);
		textMessageView.setText(message);
		btnCancelView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mAlertDialog!=null){
					mAlertDialog.cancel();
				}
			}
		});
		btnOkView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mAlertDialog!=null){
					mAlertDialog.cancel();
				}
				if(mListener!=null){
					mListener.onClick();
				}
			}
		});
		builder.setView(dialogview);
		mAlertDialog=builder.create();
		mAlertDialog.show();
	}

	public static void closeDialog(){
		mAlertDialog.cancel();
	}
}