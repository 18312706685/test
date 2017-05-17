package com.example.dfz.mymusicgame.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.dfz.mymusicgame.R;
import com.example.dfz.mymusicgame.data.Const;
import com.example.dfz.mymusicgame.model.IAlertDialogButtonListener;
import com.example.dfz.mymusicgame.model.IWordButtonClickListener;
import com.example.dfz.mymusicgame.model.Song;
import com.example.dfz.mymusicgame.model.WordButton;
import com.example.dfz.mymusicgame.myui.MyGridView;
import com.example.dfz.mymusicgame.util.MyPlayer;
import com.example.dfz.mymusicgame.util.Util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity implements IWordButtonClickListener {
    //唱片动画
    private final static int COUNTS_WORDS = 24;
    public final static int STATUS_ANSWER_RIGHT = 1;
    public final static int STATUS_ANSWER_WRONG = 2;
    public final static int STATUS_ANSWER_LACK = 3;
    public final static int SPARKTIMES = 6;
    public final static int ID_DIALOG_DELETE_WORD=1;
    public final static int ID_DIALOG_TIP_ANSWER=2;
    public final static int ID_DIALOG_LACK_COINS=3;

    private Animation mPanAnim;
    private LinearInterpolator mPanLin;
    private Animation mBarInAnim;
    private LinearInterpolator mBarInLin;
    private Animation mBarOutAnim;
    private LinearInterpolator mBarOutLin;
    private ImageButton mBtnPlayStart;
    private ImageView mViewPan;
    private ImageView mViewBar;
    private boolean mIsRunning = false;
    private ArrayList<WordButton> mAllWords;
    private ArrayList<WordButton> mSelectWords;
    private MyGridView mMyGridView;
    private LinearLayout mLinearlayout;
    private Animation mTranslateAnim;
    private Song mCurrentSong;
    private int mCurrentStageIndex = -1;
    private View mPassView;
    private int mCurrentCoins = Const.TOTAL_COINS;
    private TextView mViewCurrentCoins;
    private ImageButton imageButton1;
    private ImageButton imageButton2;
    private int mDeletable = COUNTS_WORDS;
    private TextView mCurrentStagePassView;
    private TextView mCurrentSongNamePassView;
    private TextView mCurrentStageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setContentView(inflater.inflate(R.layout.activity_main, null));

        mViewBar = (ImageView) findViewById(R.id.imageView2);
        mViewPan = (ImageView) findViewById(R.id.imageView1);


        mMyGridView = (MyGridView) findViewById(R.id.gridview);
        mMyGridView.registOnWordButtonClick(this);

        mViewCurrentCoins = (TextView) findViewById(R.id.txt_bar_coins);
        mViewCurrentCoins.setText(mCurrentCoins + "");


        mLinearlayout = (LinearLayout) findViewById(R.id.word_select_container);

        imageButton1= (ImageButton) findViewById(R.id.btn_next);
        imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(judgePassed()){
                    Util.startActivity(MainActivity.this,AllPassView.class);
                }else{
                    mPassView.setVisibility(View.GONE);

                    initCurrentStageData();
                }
            }
        });
        imageButton2= (ImageButton) findViewById(R.id.btn_share);


        mPanAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
        mPanLin = new LinearInterpolator();
        mPanAnim.setInterpolator(mPanLin);
//        mPanAnim.setFillAfter(true);
        mPanAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mViewBar.startAnimation(mBarOutAnim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        mBarInAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_45);
        mBarInLin = new LinearInterpolator();
        mBarInAnim.setInterpolator(mBarInLin);
        mBarInAnim.setFillAfter(true);
        mBarInAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mViewPan.startAnimation(mPanAnim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mBarOutAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_d_45);
        mBarOutLin = new LinearInterpolator();
        mBarOutAnim.setInterpolator(mBarOutLin);
        mBarOutAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mIsRunning = false;
                mBtnPlayStart.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mBtnPlayStart = (ImageButton) findViewById(R.id.btn_play_start);
        mBtnPlayStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlePlayButton();
            }
        });
        mTranslateAnim = AnimationUtils.loadAnimation(this, R.anim.translate);
        mTranslateAnim.setDuration(2600);
        mLinearlayout.startAnimation(mTranslateAnim);
        initCurrentStageData();
        handleDeleteWord();
        handleTipsAnswer();
    }

    private void handlePlayButton() {
        if (!mIsRunning) {
            mIsRunning = true;
            mViewBar.startAnimation(mBarInAnim);
            mBtnPlayStart.setVisibility(View.INVISIBLE);
            MyPlayer.playSong(MainActivity.this,mCurrentSong.getSongFileName());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mViewPan.clearAnimation();
        MyPlayer.stopTheSong();
        Util.closeDialog();
    }

    private Song loadStageInfo(int stageIndex) {
        Song song = new Song();
        String[] stage = Const.SONG_INFO[stageIndex];
        song.setSongFileName(stage[Const.INDEX_FILE_NAME]);
        song.setSongName(stage[Const.INDEX_SONG_NAME]);
        return song;
    }

    private void initCurrentStageData() {

        mCurrentSong = loadStageInfo(++mCurrentStageIndex);
        mSelectWords = initSelectWord();
        mDeletable = COUNTS_WORDS;
        mLinearlayout.removeAllViews();
        for (int i = 0; i < mCurrentSong.getNameLength(); i++) {
            mLinearlayout.addView(mSelectWords.get(i).mViewButton, 180, 180);
        }

        mCurrentStageView= (TextView) findViewById(R.id.text_current_stage);
        mCurrentStageView.setText(mCurrentStageIndex+1+"");
        mAllWords = initAllWord();
        mMyGridView.updateData(mAllWords);

        handlePlayButton();
    }

    private ArrayList<WordButton> initAllWord() {
        ArrayList<WordButton> data = new ArrayList<WordButton>();
        String[] words = generateWords();
        for (int i = 0; i < COUNTS_WORDS; i++) {
            WordButton button = new WordButton();
            button.mWordString = words[i];
            data.add(button);
        }
        return data;
    }

    private ArrayList<WordButton> initSelectWord() {
        ArrayList<WordButton> data = new ArrayList<WordButton>();
        for (int i = 0; i < mCurrentSong.getNameLength(); i++) {
            View view = Util.getView(MainActivity.this, R.layout.self_ui_gridview_item);
            final WordButton button = new WordButton();
            button.mIsVisiable = false;
            button.mViewButton = (Button) view.findViewById(R.id.item_btn);
            button.mViewButton.setText("");
            button.mViewButton.setTextColor(Color.WHITE);
            button.mViewButton.setBackgroundResource(R.drawable.game_wordblank);
            button.mViewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clearTheAnswer(button);
                    for (int i = 0; i < mSelectWords.size(); i++) {
                        mSelectWords.get(i).mViewButton.setTextColor(Color.WHITE);
                    }
                }
            });
            data.add(button);
        }
        return data;
    }

    @Override
    public void onWordButtonClick(WordButton wordButton) {
        setSelectWord(wordButton);

        int checkResult = checkTheAnswer();
        if (checkResult == STATUS_ANSWER_RIGHT) {
            handlePassEvent();
        } else if (checkResult == STATUS_ANSWER_WRONG) {
            sparkTheWord();
        } else if (checkResult == STATUS_ANSWER_LACK) {
            for (int i = 0; i < mSelectWords.size(); i++) {
                mSelectWords.get(i).mViewButton.setTextColor(Color.WHITE);
            }
        }
    }

    private void setSelectWord(WordButton wordButton) {
        for (int i = 0; i < mSelectWords.size(); i++) {
            if (mSelectWords.get(i).mWordString.length() == 0) {
                mSelectWords.get(i).mViewButton.setText(wordButton.mWordString);
                mSelectWords.get(i).mIsVisiable = true;
                mSelectWords.get(i).mWordString = wordButton.mWordString;
                mSelectWords.get(i).mIndex = wordButton.mIndex;
                setButtonVisible(wordButton, View.INVISIBLE);
                break;
            }
        }
    }

    private void setButtonVisible(WordButton wordButton, int visibility) {
        wordButton.mViewButton.setVisibility(visibility);
        if (wordButton.mIndex == 0) {
            mMyGridView.getChildAt(0).setVisibility(visibility);
        }
        wordButton.mIsVisiable = (visibility == View.VISIBLE) ? true : false;
    }

    private char getRandomChar() {
        String str = "";
        int hightPos;
        int lowPos;

        Random random = new Random();

        hightPos = (176 + Math.abs(random.nextInt(39)));
        lowPos = (161 + Math.abs(random.nextInt(93)));

        byte[] b = new byte[2];
        b[0] = (Integer.valueOf(hightPos)).byteValue();
        b[1] = (Integer.valueOf(lowPos)).byteValue();

        try {
            str = new String(b, "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str.charAt(0);
    }

    private String[] generateWords() {
        Random random = new Random();
        char a;
        String[] words = new String[COUNTS_WORDS];

        // 存入歌名
        for (int i = 0; i < mCurrentSong.getNameLength(); i++) {
            words[i] = mCurrentSong.getNameCharacters()[i] + "";
        }

        // 获取随机文字并存入数组
        for (int i = mCurrentSong.getNameLength();
             i < COUNTS_WORDS; i++) {
            a = getRandomChar();
            for (int j = 0; j < i; j++) {
                if (a == words[j].charAt(0)) {
                    a = getRandomChar();
                } else {
                    break;
                }
            }
            words[i] = a + "";
        }

        // 打乱文字顺序：首先从所有元素中随机选取一个与第一个元素进行交换，
        // 然后在第二个之后选择一个元素与第二个交换，知道最后一个元素。
        // 这样能够确保每个元素在每个位置的概率都是1/n。
        for (int i = COUNTS_WORDS - 1; i >= 0; i--) {
            int index = random.nextInt(i + 1);

            String buf = words[index];
            words[index] = words[i];
            words[i] = buf;
        }

        return words;
    }

    private void clearTheAnswer(WordButton wordButton) {
        wordButton.mViewButton.setText("");
        wordButton.mWordString = "";
        wordButton.mIsVisiable = false;

        setButtonVisible(mAllWords.get(wordButton.mIndex), View.VISIBLE);
    }

    private int checkTheAnswer() {
        for (int i = 0; i < mSelectWords.size(); i++) {
            if (mSelectWords.get(i).mWordString.length() == 0) {
                return STATUS_ANSWER_LACK;
            }
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mSelectWords.size(); i++) {
            sb.append(mSelectWords.get(i).mWordString);
        }
        return (sb.toString().equals(mCurrentSong.getSongName())) ? STATUS_ANSWER_RIGHT : STATUS_ANSWER_WRONG;
    }

    private void sparkTheWord() {
        TimerTask task = new TimerTask() {
            boolean mchange = false;
            int mSparkTimes = 0;

            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (++mSparkTimes > SPARKTIMES) {
                            return;
                        }
                        for (int i = 0; i < mSelectWords.size(); i++) {
                            mSelectWords.get(i).mViewButton.setTextColor(mchange ? Color.RED : Color.WHITE);
                        }
                        mchange = !mchange;
                    }
                });
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 1, 150);
    }

    private void handlePassEvent() {
        mPassView = (LinearLayout) findViewById(R.id.pass_view);
        mPassView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mPassView.setVisibility(View.VISIBLE);
        mCurrentCoins+=3;

        mViewCurrentCoins.setText(mCurrentCoins+"");

        mViewPan.clearAnimation();
        MyPlayer.stopTheSong();

        mCurrentStagePassView= (TextView) findViewById(R.id.text_current_stage_pass);
        if(mCurrentStagePassView!=null){
            mCurrentStagePassView.setText((mCurrentStageIndex+1)+"");
        }

        mCurrentSongNamePassView= (TextView) findViewById(R.id.text_current_song_name_pass);
        if(mCurrentSongNamePassView!=null){
            mCurrentSongNamePassView.setText(mCurrentSong.getSongName());
        }

    }

    private void handleDeleteWord() {
        final ImageButton button = (ImageButton) findViewById(R.id.btn_delete_word);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDeletable > mCurrentSong.getNameLength()) {
                    mDeletable--;
                    showConfirmButtonDialog(ID_DIALOG_DELETE_WORD);
                    if (mDeletable == mCurrentSong.getNameLength()) {
                        button.setImageResource(R.drawable.game_buy2_sel);
                        button.setEnabled(false);
                    }
                }
            }
        });
    }

    private void handleTipsAnswer() {
        ImageButton button = (ImageButton) findViewById(R.id.btn_tip_answer);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                tipAnswer();
                showConfirmButtonDialog(ID_DIALOG_TIP_ANSWER);
            }
        });
    }

    private IAlertDialogButtonListener mBtnOkTipAnswerListener=new IAlertDialogButtonListener() {
        @Override
        public void onClick() {
            tipAnswer();
        }
    };
    private IAlertDialogButtonListener mBtnOkDeleteWordListener=new IAlertDialogButtonListener() {
        @Override
        public void onClick() {
            deleteOneWord();
        }
    };
    private IAlertDialogButtonListener mBtnCancelListener=new IAlertDialogButtonListener() {
        @Override
        public void onClick() {

        }
    };
    private IAlertDialogButtonListener mBtnOkLackCoinsListener=new IAlertDialogButtonListener() {
        @Override
        public void onClick() {

        }
    };

    private void showConfirmButtonDialog(int id){
        switch (id){
            case ID_DIALOG_DELETE_WORD:
                Util.showDialog(MainActivity.this,"确认花掉"+getDeleteWordCoins()+"个金币去掉一个错误答案？",mBtnOkDeleteWordListener);
                break;
            case ID_DIALOG_TIP_ANSWER:
                Util.showDialog(MainActivity.this,"确认花掉"+getTipCoins()+"个金币提示一个正确答案？",mBtnOkTipAnswerListener);
                break;
            case ID_DIALOG_LACK_COINS:
                Util.showDialog(MainActivity.this,"金币不足",mBtnOkLackCoinsListener);
                break;
        }
    }

    private boolean handleCoins(int data) {
        // 判断当前总的金币数量是否可被减少
        if (mCurrentCoins + data >= 0) {
            mCurrentCoins += data;

            mViewCurrentCoins.setText(mCurrentCoins + "");

            return true;
        } else {
            // 金币不够
            return false;
        }
    }

    private int getDeleteWordCoins() {
        return this.getResources().getInteger(R.integer.pay_delete_word);
    }

    private int getTipCoins() {
        return this.getResources().getInteger(R.integer.pay_tip_answer);
    }

    private void deleteOneWord() {
        // 减少金币
        if (!handleCoins(-getDeleteWordCoins())) {
            // 金币不够，显示提示对话框
            showConfirmButtonDialog(ID_DIALOG_LACK_COINS);
            return;
        }

        // 将这个索引对应的WordButton设置为不可见
        setButtonVisible(findNotAnswerWord(), View.INVISIBLE);
    }

    private WordButton findNotAnswerWord() {
        Random random = new Random();
        WordButton buf = null;

        while (true) {
            int index = random.nextInt(COUNTS_WORDS);

            buf = mAllWords.get(index);

            if (buf.mIsVisiable && !isTheAnswerWord(buf)) {
                return buf;
            }
        }
    }

    private WordButton findIsAnswerWord(int index) {
        WordButton buf = null;

        for (int i = 0; i < COUNTS_WORDS; i++) {
            buf = mAllWords.get(i);

            if (buf.mWordString.equals("" + mCurrentSong.getNameCharacters()[index])) {
                return buf;
            }
        }

        return null;
    }

    private boolean isTheAnswerWord(WordButton word) {
        boolean result = false;

        for (int i = 0; i < mCurrentSong.getNameLength(); i++) {
            if (word.mWordString.equals
                    ("" + mCurrentSong.getNameCharacters()[i])) {
                result = true;

                break;
            }
        }

        return result;
    }

    private void tipAnswer() {
        // 减少金币数量
        if (!handleCoins(-getTipCoins())) {
            // 金币数量不够，显示对话框
            showConfirmButtonDialog(ID_DIALOG_LACK_COINS);
            return;
        }
        boolean tipWord = false;
        for (int i = 0; i < mSelectWords.size(); i++) {
            if (mSelectWords.get(i).mWordString.length() == 0) {
                // 根据当前的答案框条件选择对应的文字并填入
                onWordButtonClick(findIsAnswerWord(i));

                tipWord = true;
                break;
            }
        }
        if (!tipWord) {
            // 闪烁文字提示用户
            sparkTheWord();
        }
    }

    private boolean judgePassed(){

        return (mCurrentStageIndex ==Const.SONG_INFO.length-1);
    }
}
