# ViewPagerTransformer
ViewPager一屏幕多页显示，自定义Transformer


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
