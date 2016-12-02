package com.riso.popimager;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class PopImageActivity extends Activity {

    private ImageView iv;
    private PopImage popImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //需要 外层RelativeLayout
        RelativeLayout rootView = (RelativeLayout) View.inflate(this, R.layout.activity_pop_image, null);
        //设置ContentView
        setContentView(rootView);
        //找控件
        iv = (ImageView) findViewById(R.id.iv);
        //一行代码搞定
        popImage = new PopImage(rootView, iv);


        //更多 可选  设置 =================================
       /* popImage.setAnimSpeed(500); //设置 子弹 的速度
        popImage.setColseSound(true); //设置声音 关闭
        popImage.setLOCATION(1f); //设置 出现的 位置 左边
            //设置监听
        popImage.setOnPopListener(new PopImage.OnPopListener() {
            @Override
            public void win() {
                Toast.makeText(PopImageActivity.this,"中奖",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void noWin() {

            }

            @Override
            public void txOnTouch(View view, MotionEvent motionEvent) {

            }
        });*/

    }
}
