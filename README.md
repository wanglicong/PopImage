# PopImage
让View,添加发射桃心的功能,有中奖几率以及事件监听

### 使用方法:

# 步骤 1:
将这几行代码复制到 app里的gradl文件dependencies 行 的>>上<<面

    allprojects {
        repositories {
            maven { url 'https://jitpack.io' }
        }
    }
将这一行代码复制到 app里的gradl文件dependencies { 的>>里<<面

    compile 'com.github.wanglicong:PopImage:-SNAPSHOT'



# 步骤2:
找到最 外层 的RelativeLayout布局  和 需要 设置 特效的 View ;

    例如:
    
    RelativeLayout relativeLayout= (RelativeLayout) findViewById(R.id.activity_main);
    Button btn=(Button)findViewById(R.id.btn);
    //创建吐心对象
    popImage = new PopImage(relativeLayout, btn);
    

好了可以运行查看基本效果了



### 更多 可选  设置 =================================
 
    popImage.setAnimSpeed(500); //设置子弹的速度 默认400
    popImage.setColseSound(true); //设置声音 true关闭  false打开
    popImage.setLOCATION(1f); //设置出现的位置 范围(0.0f~1.0f)
    popImage.setOffset(0.1f);//设置上下偏移  默认是0.1f
    popImage.setScaleAnimOrSize(1.3f);//设置按钮的缩放倍数 默认1.3f
    popImage.setWin(10);//设置中奖几率 默认是3; 公式1/?
    //设置监听
    popImage.setOnPopListener(new PopImage.OnPopListener() {
        @Override
        public void win() { //中奖监听
          Toast.makeText(PopImageActivity.this,"中奖",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void noWin() { //没中间监听
        }

        @Override
        public void txOnTouch(View view, MotionEvent motionEvent) {
          // iv = (ImageView) findViewById(R.id.iv)的触摸事件
        }
        });







