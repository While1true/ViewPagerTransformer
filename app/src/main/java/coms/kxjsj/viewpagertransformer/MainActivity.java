package coms.kxjsj.viewpagertransformer;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewPager viewPager=findViewById(R.id.viewPager);
        viewPager.setPageTransformer(false,new AlphaTransformer());
        LoopFragmentPagerAdapter adapter = new LoopFragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getActualCount() {
                return 12;
            }

            @Override
            public Fragment getActualItem(int position) {
                return new MyFragment();
            }

            @Override
            public CharSequence getActualPagerTitle(int position) {
                return "标题" + position;
            }
        };
        //不循环 反转轮播
        adapter.setLoop(false);
        //自动轮播
        adapter.setAutoSwitch(true);
        viewPager.setAdapter(adapter);

    }
}
