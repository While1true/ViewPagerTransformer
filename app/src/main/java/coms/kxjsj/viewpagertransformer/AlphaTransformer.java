package coms.kxjsj.viewpagertransformer;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by vange on 2017/12/6.
 */

public class AlphaTransformer implements ViewPager.PageTransformer {
    ViewPager viewPager;
    float excursion=-1;
    private float MINALPHA=0.5f;
    private int imgWidth;
    private int paddingLeft;

    public AlphaTransformer(ViewPager viewPager) {
        this.viewPager=viewPager;
    }

    @Override
    public void transformPage(View page, float position) {
        System.out.println(position);
        if(excursion==-1){
            excursion=-position;
//            //左边padding部分宽度
            paddingLeft = viewPager.getPaddingLeft();
            //一页的宽度
            imgWidth = viewPager.getWidth() - paddingLeft *2;
//            //padding部分所占百分比
//            excursion = -(float) paddingLeft /(float) imgWidth;
        }
        position=position+excursion;
        System.out.println("excursion"+excursion);
        if (position < -1 || position > 1) {
            page.setAlpha(MINALPHA);
            page.setScaleX(MINALPHA);
            page.setScaleY(MINALPHA);
                page.setTranslationX((imgWidth-paddingLeft)*0.25f*Math.signum(-position));
        }else {
            //不透明->半透明
            if (position < 0) {//[0,-1]
                float alpha = MINALPHA + (1 + position) * (1 - MINALPHA);
                page.setAlpha(alpha);
                page.setScaleX(alpha);
                page.setScaleY(alpha);
                page.setTranslationX((imgWidth-paddingLeft)*(1-alpha)/2);
            } else {//[1,0]
                //半透明->不透明
                float alpha = MINALPHA + (1 - position) * (1 - MINALPHA);
                page.setAlpha(alpha);
                page.setScaleX(alpha);
                page.setScaleY(alpha);
                page.setTranslationX((imgWidth-paddingLeft)*(alpha-1)/2);
            }
        }
    }
}