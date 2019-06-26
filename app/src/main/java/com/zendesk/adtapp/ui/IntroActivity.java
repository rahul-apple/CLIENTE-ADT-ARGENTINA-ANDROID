package com.zendesk.adtapp.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.zendesk.adtapp.R;

import java.util.ArrayList;


public class IntroActivity extends AppCompatActivity {
    Button btn_start;
    private static final Integer[] IMAGES = {R.drawable.screen_1, R.drawable.screen_2, R.drawable.screen_3, R.drawable.screen_4};
    private static ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private ArrayList<Integer> ImagesArray = new ArrayList<Integer>();
    private ActionBar actionBar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_activity);
        btn_start = findViewById(R.id.btn_satrt);
        mPager = (ViewPager) findViewById(R.id.intro_pager);
        actionBar = getSupportActionBar();
        actionBar.hide();
//        mPager.post(new Runnable() {
//            @Override
//            public void run() {
        mPager.setCurrentItem(1);
        init();
//            }
//        });

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RootViewActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
    private void init() {

        for (int i = 0; i < IMAGES.length; i++) {
            ImagesArray.add(IMAGES[i]);
        }
        mPager.setAdapter(new SlidingImage_Adapter(IntroActivity.this, ImagesArray));
        final float density = getResources().getDisplayMetrics().density;
        NUM_PAGES = IMAGES.length;


        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                currentPage = position;
                if (currentPage == 3) {
                    btn_start.setVisibility(View.VISIBLE);
                } else {
                    btn_start.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



    }

    public class SlidingImage_Adapter extends PagerAdapter {


        private ArrayList<Integer> IMAGES;
        private LayoutInflater inflater;
        private Context context;


        public SlidingImage_Adapter(Context context, ArrayList<Integer> IMAGES) {
            this.context = context;
            this.IMAGES = IMAGES;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return IMAGES.size();
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            try {
                View imageLayout = inflater.inflate(R.layout.slidingimages_layout, view, false);
                assert imageLayout != null;
                final ImageView imageView = (ImageView) imageLayout
                        .findViewById(R.id.image);
                try {
                    imageView.setImageResource(IMAGES.get(position));
                    view.addView(imageLayout, 0);
                } catch (OutOfMemoryError outOfMemoryError) {

                }
                return imageLayout;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }
    }
}




