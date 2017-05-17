package com.example.dfz.mymusicgame.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.example.dfz.mymusicgame.R;
import com.example.dfz.mymusicgame.util.Util;

/**
 * Created by dfz on 2017/4/29.
 */

public class AllPassView extends Activity {


    private View view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_pass_view);
        view=findViewById(R.id.layout_bar_coin);
        view.setVisibility(View.INVISIBLE);
        view=findViewById(R.id.btn_bar_back);
        view.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Util.startActivity(AllPassView.this,MainActivity.class);
                                    }
                                }
        );
    }
}
