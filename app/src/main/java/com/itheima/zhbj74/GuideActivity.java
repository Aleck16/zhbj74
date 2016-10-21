package com.itheima.zhbj74;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.itheima.zhbj74.utils.PrefUtils;

import java.util.ArrayList;

/**
 * Created by Aleck_ on 2016/10/20.
 */

public class GuideActivity extends Activity {

    protected static final String tag = "GuideActivity";

    private ViewPager mViewPager;

    /**
     * 引导页图片数组
     */
    private int[] mImageIds = new int[]{R.drawable.guide_1, R.drawable.guide_2, R.drawable.guide_3};

    private ArrayList<ImageView> mImageViewList;    //image集合
    private LinearLayout llContainer;
    private ImageView ivRedPoint;
    private Button btnstart;

    //小红点移动的距离
    private int mPointDis;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_guide);

        mViewPager = (ViewPager) findViewById(R.id.vp_guide);
        llContainer = (LinearLayout) findViewById(R.id.ll_container);
        ivRedPoint = (ImageView) findViewById(R.id.iv_red_point);
        btnstart = (Button) findViewById(R.id.btn_start);

        initData();

        mViewPager.setAdapter(new GuideAdapter());  //设置数据

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //当页面滑动过程中的回调
                Log.i(tag, "mPointDis = " + mPointDis + ";  当前位置：" + position + ";移动偏移百分比：" + positionOffset);
                //更新小红点距离
                int leftMargin = (int) (mPointDis * positionOffset) + position * mPointDis;  //计算小红点当前的左边距；
                Log.i(tag,"leftMargin左边距 = "+leftMargin+"；");
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ivRedPoint.getLayoutParams();
                params.leftMargin = leftMargin;       //修改左边距

                //重新设置布局参数
                ivRedPoint.setLayoutParams(params);
            }

            @Override
            public void onPageSelected(int position) {
                //某个页面被选中
                if (position == mImageViewList.size() - 1) {  //当为最后一个页面，显示开始按钮
                    btnstart.setVisibility(View.VISIBLE);
                } else {          //否则隐藏开始按钮
                    btnstart.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //当页面状态发生变化的回调
            }
        });

        //计算两个圆点的距离，用已经画好的黑色的点计算，它们之间的像素间隔是10px,但是相对距离单位需要用一下方法计算
        //移动距离=第二个圆点left值 - 第一个圆点left值
        //measure->layout(确定位置)->draw(activity的onCreate方法执行结束之后才会走此流程)
        //mPointDis = llcontainer.getChildAt(1).getLeft() - llcontainer.getChildAt(0).getLeft();
        //System.out.println("圆点距离" + mPointDis);

        //监听layout方法结束的事件，位置确定好之后再获取圆点间距
        //视图树

        ivRedPoint.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //移除监听，避免重复回掉
                ivRedPoint.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                //layout方法执行结束的回调
                mPointDis = llContainer.getChildAt(1).getLeft() - llContainer.getChildAt(0).getLeft();
                Log.i(tag, "mPointDis圆点距离：" + mPointDis);
            }
        });

        btnstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //更新sp,已经不是第一次进入了
                PrefUtils.setBoolean(getApplicationContext(), "is_first_enter", false);

                //跳转到主页面
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });

    }

    /**
     * 初始化对象
     */
    private void initData() {
        mImageViewList = new ArrayList<ImageView>();
        for (int i = 0; i < mImageIds.length; i++) {
            ImageView view = new ImageView(this);
            view.setBackgroundResource(mImageIds[i]);       //通过设置背景可以让宽高填充布局
//            view.setImageResource();                      //不一定填充
            mImageViewList.add(view);

            //初始化小圆点
            ImageView point = new ImageView(this);
            point.setImageResource(R.drawable.shape_point_gray);        //设置图片形状

            //初始化布局参数，宽高包裹内容，父控件是谁，就是谁声明的布局参数、
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            if (i > 0) {
                params.leftMargin = 10;
            }
            point.setLayoutParams(params);  //设置布局参数

            llContainer.addView(point);     //给容器添加圆点

        }
    }

    class GuideAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mImageViewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        //初始化item
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView view = mImageViewList.get(position);
            container.addView(view);
            return view;
        }

        //销毁item
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
