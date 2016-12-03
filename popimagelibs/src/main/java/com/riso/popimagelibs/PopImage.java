package com.riso.popimagelibs;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by 王黎聪 on 2014/12/2.
 */

public class PopImage implements View.OnTouchListener {

    private OnPopListener onPopListener;  //接口回调 监听
    private Random random; // 随机动画
    private RelativeLayout rootView; //根布局
    private View txView; //需要添加特效的控件
    private Context context; //上下文
    private int rlWidth, rlHeight; //rootView的宽和高
    private float viewZoom=1.3f; // 特效View 要方法的倍数
    private ScaleAnimation viewZoomAnim;// 特效View的放大动画
    private int winSize=3;// 中奖的几率
    private int animSpeed=400;// 桃心飘逸的速度
    private ImageView targIV; // 要展示 目标的ImageView
    private ArrayList<ImageView> al_bullet; //放子弹的集合
    private ArrayList<Integer> al_targImages; //目标ImageView 的图片集合
    private int bulletCount; //发射的次数
    private int nextX; //targImage下一次位置的x坐标
    private int nextY; //targImage下一次位置的y坐标
    private TranslateAnimation translateAnimBullet;//射击的动画
    private float LOCATION; //发射的位置
    private float offset=0.1f; //设置偏移
    private boolean targIsShow; //判断图片是否在显示

    private int index; //targImage 显示的第几张图片
    private MediaPlayer soundPop_n; //没中奖的声音
    private MediaPlayer soundPop_p; //中奖的声音
    private boolean iscloseSound; //是否关闭声音

    private AnimationDrawable animBaozha;  //没中奖的帧动画
    private AnimationDrawable animBaozha1; //中奖的帧动画


    /**
     * 构造器
     * @param rootView 根布局(目前只支持RelativeLayout)
     * @param txView     需要设置特效的View
     */
    public PopImage(RelativeLayout rootView, View txView) {
        //初始化
        this.rootView = rootView;
        this.txView = txView;
        this.context = rootView.getContext();
        //获取rootView 的宽和高
        rlWidth = rootView.getResources().getDisplayMetrics().widthPixels;
        rlHeight = rootView.getResources().getDisplayMetrics().heightPixels;
        if (rlWidth<150||rlHeight<150){
            throw new RuntimeException("您的rootView的宽和高必须大于150px >>建议最外层使用全屏的RelativeLayout");
        }
        //初始化数据
        initData();
        //设置特效的View的变大动画
        setScaleAnimOrSize(viewZoom);
        //设置触摸事件
        txView.setOnTouchListener(this);
    }

    /**
     * 设置特效View的放大动画,或大小.默认有动画
     * @param zoomToUp 最好在0.5~1.5之间,>>如果设置为0将取消动画效果
     */
    public void setScaleAnimOrSize(float zoomToUp) {
        if (zoomToUp<=0){
            viewZoomAnim=null;
        }else{
            if (zoomToUp>2)zoomToUp=2;
            viewZoomAnim = new ScaleAnimation(1, zoomToUp, 1, zoomToUp, Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF,0.5f);
            viewZoomAnim.setDuration(80);
            viewZoomAnim.setFillAfter(false);
        }
    }

    /**
     * 设置pop 的 事件监听
     * @param onPopListener    事件监听
     */
    public void setOnPopListener(OnPopListener onPopListener){
        this.onPopListener=onPopListener;
    }

    /**
     * 设置子弹的速度
     * @param animSpeed 设置子弹的速度 默认 400 , 不能小于 100
     */
    public void setAnimSpeed(int animSpeed){
        if (animSpeed<100) animSpeed=100;
        this.animSpeed=animSpeed;
    }

    /**
     * 设置发射的起始方向
     * @param location 0.5->>是中间  1->>是右边  0->>是左边
     */
    public void setLOCATION(float location){
        location*=-1f;
        if (location>0) location=0;
        if (location<-1) location=-1;
        LOCATION=location;
    }

    /**
     * //设置开始关闭声音
     * @param iscloseSound true 代表关闭   false 代表打开
     */
    public void setColseSound(boolean iscloseSound){
        this.iscloseSound=iscloseSound;
    }

    /**
     * 设置中奖几率
     * @param win  1是100%中奖 数值越大中奖几率 越小 公式  1/win 中奖几率
     */
    public void setWin(int win){
        if (win<=0) win=1;
        winSize=win;
    }

    /**
     * 设置上下偏差
     * @param offset 默认是0.1f ,如果有偏差 请设置 0f尝试
     */
    public void setOffset(float offset){
        if(offset<0) offset=0;
        this.offset=offset;
    }

    //添加子弹
    private void addBullet() {

        if (!targIsShow){
            targIsShow=true;
            bulletCount=0;
            //添加目标tagView
            targIV.setImageResource(al_targImages.get(bulletCount));
            int xinSize=rlWidth/10;
            rootView.addView(targIV,xinSize,xinSize);  // ???
            //设置位置
            nextX = random.nextInt(rlWidth);
            nextY = random.nextInt(rlWidth);
            if (nextX>rlWidth/2){
                nextX-=xinSize;
            }
            targIV.setX(nextX);
            targIV.setY(nextY);
        }else{
            //添加子弹
            addBullets();
        }
    }

    private void addBullets() {
        if (bulletCount>=6) return;
        //设置子弹的大小
        int xinSize=rlWidth/12;
        final ImageView imageView = al_bullet.get(bulletCount++);
        //添加ImageView
        rootView.addView(imageView,xinSize,xinSize);
        //设置子弹的起始位置
        imageView.setX(rlWidth);
        imageView.setY(rlHeight);

        //获取屏幕位置的比例值
        float nx=(float)(1.0f-(nextX*1.0/rlWidth))*-1f;
        float ny=(float)(1.0f-(nextY*1.0/rlHeight))*-1f;
        ny+=ny*offset;

        //创建动画
        translateAnimBullet = new TranslateAnimation(Animation.RELATIVE_TO_PARENT,LOCATION, Animation.RELATIVE_TO_PARENT, nx, Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, ny);
        translateAnimBullet.setDuration(animSpeed);
        translateAnimBullet.setFillAfter(true);
        translateAnimBullet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {
                index++;

                if (imageView.equals(al_bullet.get(al_bullet.size()-1))){
                    rootView.removeView(imageView);
                    if(random.nextInt(winSize)==winSize/2){
                        targIV.setImageDrawable(animBaozha1);
                        if (!iscloseSound) soundPop_p.start(); //播放声音
                        animBaozha1.start();
                        if (onPopListener!=null) onPopListener.win();
                    }else{
                        targIV.setImageDrawable(animBaozha);
                        if (!iscloseSound) soundPop_n.start(); //播放声音
                        animBaozha.start();
                        if (onPopListener!=null) onPopListener.noWin();
                    }

                    handler.sendEmptyMessageDelayed(1,380);

                }else{
                    targIV.setImageResource(al_targImages.get(index));
                    rootView.removeView(imageView);
                }
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageView.startAnimation(translateAnimBullet);
    }


    //初始化数据
    private void initData() {
        //创建随机对象
        random = new Random();
        //创建目标ImageView
        targIV = new ImageView(context);

        //创建 桃心的集合 bullet
        al_bullet = new ArrayList<>();
        ImageView iv1 = new ImageView(context);
        ImageView iv2 = new ImageView(context);
        ImageView iv3 = new ImageView(context);
        ImageView iv4 = new ImageView(context);
        ImageView iv5 = new ImageView(context);
        ImageView iv6 = new ImageView(context);
        iv1.setImageResource(R.mipmap.onexin01);
        iv2.setImageResource(R.mipmap.onexin02);
        iv3.setImageResource(R.mipmap.onexin03);
        iv4.setImageResource(R.mipmap.onexin04);
        iv5.setImageResource(R.mipmap.onexin05);
        iv6.setImageResource(R.mipmap.onexin06);
        al_bullet.add(iv1);
        al_bullet.add(iv2);
        al_bullet.add(iv3);
        al_bullet.add(iv4);
        al_bullet.add(iv5);
        al_bullet.add(iv6);
        //创建 目标的 图片集合
        al_targImages = new ArrayList<>();
        al_targImages.add(R.mipmap.xin01);
        al_targImages.add(R.mipmap.xin02);
        al_targImages.add(R.mipmap.xin03);
        al_targImages.add(R.mipmap.xin04);
        al_targImages.add(R.mipmap.xin05);
        al_targImages.add(R.mipmap.xin06);

        //初始化爆炸效果
        animBaozha = (AnimationDrawable)context.getResources().getDrawable(R.drawable.zhendonghua_baozha);
        //初始化爆炸效果
        animBaozha1 = (AnimationDrawable)context.getResources().getDrawable(R.drawable.zhendonghua_baozha1);
        //创建音效
        soundPop_n = MediaPlayer.create(context, R.raw.xinpop_n);
        soundPop_p = MediaPlayer.create(context, R.raw.xinpop_p);

    }

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==1){
                index=0;
                rootView.removeView(targIV);
                targIsShow=false;
            }
        }
    };


    public static interface OnPopListener{
        /**
         * 中间后触发的方法回调
         */
        void win();
        /**
         *  没有中奖的方法回调
         */
        void noWin();
        /**
         * 特效 按钮 按下的 方法回调
         */
        void txOnTouch(View view, MotionEvent motionEvent);

    }


    //重写触摸事件
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (onPopListener!=null) onPopListener.txOnTouch(view, motionEvent);
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(viewZoomAnim!=null){
                    view.startAnimation(viewZoomAnim);
                }
                addBullet();
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return false;
    }
}
