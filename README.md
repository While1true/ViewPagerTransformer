>
### 支持无限循环，自动轮播，反转播放的ViewPagerAdapter
###  触摸暂停切换
###  缩放pagertransformer
### 如果是要无限循环的话，反射设置一个很大的数值来无限循环
>
### 效果
![GIF.gif](http://upload-images.jianshu.io/upload_images/6456519-b1c8fbe7b4f96dfe.gif?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
[github地址](https://github.com/While1true/ViewPagerTransformer)

## 使用
```
     LoopFragmentPagerAdapter adapter = new LoopFragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getActualCount() {
                return 3;
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
        adapter.setSwitchPeriod(5000);
        //不循环 反转轮播
        adapter.setLoop(false);
        //自动轮播
        adapter.setAutoSwitch(true);
        viewPager.setAdapter(adapter);
```
## 代码
```
/**
 * Created by 不听话的好孩子 on 2017/12/6.
 * <p>
 * 有RXjava用Rxjava实现切换，无Rxjava用post
 */

public abstract class LoopFragmentPagerAdapter extends FragmentStatePagerAdapter {
    /**
     * 用来启动动画 设置循环
     */
    private boolean isfirst = true;

    /**
     * 触摸的时候停止切换
     */
    private boolean touched = false;

    /**
     * 切换间隔
     */
    private int switchPeriod = 5000;

    /**
     * 自动切换
     */
    private boolean autoSwitch = true;

    /**
     * 是否需要无限循环
     */
    private boolean loop = true;

    /**
     * post runnable
     */
    private Runnable action;

    private int BIGCOUNT = 1000000;

    private Disposable disposable;

    private boolean reverse;

    /**
     * 有无rxjava
     */
    private boolean hasRx;

    public LoopFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return getActualItem(position % getActualCount());
    }

    @Override
    public void startUpdate(final ViewGroup container) {
        super.startUpdate(container);
        if (isfirst) {
            isfirst = false;
            if (loop) {
                try {
                    doSettCurrenPager(container);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (autoSwitch) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    startLoop((ViewPager) container);
                }
            }
        }
    }

    /**
     * 反射设置一个很大的位置达到伪无限循环
     *
     * @param container
     * @throws Exception
     */
    private void doSettCurrenPager(ViewGroup container) throws Exception {
        Field mCurItem = ViewPager.class.getDeclaredField("mCurItem");
        mCurItem.setAccessible(true);
        mCurItem.setInt(container, 1000000 - (BIGCOUNT % getActualCount()));
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return getActualPagerTitle(position % getActualCount());
    }

    @Override
    public int getCount() {
        return loop ? Integer.MAX_VALUE : getActualCount();
    }


    /**
     * 执行循环切换
     *
     * @param viewPager
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void startLoop(final ViewPager viewPager) {
        hasRx = checkIfHasRx();
        Log.w("LoopFragmentPager", "startLoop: " + (hasRx ? "当前使用Rxjava" : "当前使用View.post"), null);
        if (!hasRx) {
            usePost(viewPager);
        } else {
            userRx(viewPager);
        }
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        if (!touched) {
                            touched = true;
                            if (!hasRx) {
                                viewPager.removeCallbacks(action);
                            }
                        }
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        touched = false;
                        if (!hasRx) {
                            viewPager.postDelayed(action, switchPeriod);
                        }
                        break;
                }
                return false;
            }
        });
        viewPager.getViewTreeObserver().addOnWindowAttachListener(new ViewTreeObserver.OnWindowAttachListener() {
            @Override
            public void onWindowAttached() {

            }

            @Override
            public void onWindowDetached() {
                disposable.dispose();
                viewPager.removeCallbacks(action);
                action = null;
                viewPager.getViewTreeObserver().removeOnWindowAttachListener(this);
                viewPager.setOnTouchListener(null);
            }
        });

    }

    /**
     * 用Rxjava做循环切换
     *
     * @param viewPager
     */
    private void userRx(final ViewPager viewPager) {
        disposable = Observable.interval(switchPeriod, switchPeriod, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        if (!touched) {
                            viewPager.setCurrentItem(caculatItem(viewPager));
                        }
                    }
                });
    }
    /**
     * 用View.post作循环切换
     *
     * @param container
     */
    @SuppressLint("ClickableViewAccessibility")
    private void usePost(final ViewPager container) {
        if (action == null) {
            action = new Runnable() {
                @Override
                public void run() {

                    container.setCurrentItem(caculatItem(container));
                    container.postDelayed(this, switchPeriod);
                }
            };
        }
        container.postDelayed(action, switchPeriod);
    }
    private int caculatItem(ViewPager viewPager){
        int nextItem = !reverse ? (viewPager.getCurrentItem() + 1) : (viewPager.getCurrentItem() - 1);
        if (!loop) {
            if (nextItem >= getActualCount()) {
                reverse = true;
                nextItem = viewPager.getCurrentItem() - 1;
            }
            if (nextItem < 0) {
                reverse = false;
                nextItem = 1;
            }
        }
        return nextItem;
    }
    /**
     * 是否含有Rxjava库
     *
     * @return
     */
    private boolean checkIfHasRx() {
        try {
            Class.forName("io.reactivex.android.schedulers.AndroidSchedulers");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public abstract int getActualCount();

    public abstract Fragment getActualItem(int position);

    public abstract CharSequence getActualPagerTitle(int position);
}

```
