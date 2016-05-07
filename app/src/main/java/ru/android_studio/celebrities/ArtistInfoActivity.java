package ru.android_studio.celebrities;

import android.os.Bundle;
import android.support.annotation.AnimRes;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;

import java.util.ArrayList;

/*
* Активити с информацией об исполнителе
* */
public class ArtistInfoActivity extends AppCompatActivity {

    private GestureDetectorCompat detector;

    private Integer orderId;
    private ArrayList<Integer> orders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_info);

        detector = new GestureDetectorCompat(this, new MyGestureListener());

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                orderId = extras.getInt(ArtistListActivity.EXTRAS_ORDER_ID);
                orders = extras.getIntegerArrayList(ArtistListActivity.EXTRAS_ORDERS);
            }

            loadFragment();
        } else {
            orderId = savedInstanceState.getInt(ArtistListActivity.EXTRAS_ORDER_ID);
            changeFragment(R.anim.enter_from_bottom, R.anim.exit_to_top);
        }
    }

    private void loadFragment() {
        ArtistInfoFragment oneFragment = new ArtistInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ArtistListActivity.EXTRAS_ORDER_ID, orderId);

        oneFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_top, R.anim.exit_to_bottom);
        transaction.add(R.id.fragment, oneFragment, "ArtistInfoFragment");
        transaction.commit();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt(ArtistListActivity.EXTRAS_ORDER_ID, orderId);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        orderId = savedInstanceState.getInt(ArtistListActivity.EXTRAS_ORDER_ID);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent event) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                    }
                    result = true;
                } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeBottom();
                    } else {
                        onSwipeTop();
                    }
                }
                result = true;

            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }

    private void changeFragment(@AnimRes int enter, @AnimRes int exit) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(enter, exit);

        ArtistInfoFragment oneFragment = new ArtistInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ArtistListActivity.EXTRAS_ORDER_ID, orderId);

        oneFragment.setArguments(bundle);
        transaction.replace(R.id.fragment, oneFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void onSwipeRight() {
        if (orderId == 0) {
            return;
        }
        orderId = orders.get(orders.indexOf(orderId) - 1);

        changeFragment(R.anim.enter_from_left, R.anim.exit_to_right);
    }

    public void onSwipeLeft() {
        orderId = orders.get(orders.indexOf(orderId) + 1);

        changeFragment(R.anim.enter_from_right, R.anim.exit_to_left);
    }

    public void onSwipeTop() {
        if (orderId == 0) {
            return;
        }
        orderId = orders.get(orders.indexOf(orderId) - 1);

        changeFragment(R.anim.enter_from_bottom, R.anim.exit_to_top);
    }

    public void onSwipeBottom() {
        orderId = orders.get(orders.indexOf(orderId) + 1);

        changeFragment(R.anim.enter_from_top, R.anim.exit_to_bottom);
    }
}