package com.example.gamejava;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gamejava.widget.OnWheelChangedListener;
import com.example.gamejava.widget.OnWheelScrollListener;
import com.example.gamejava.widget.WheelView;
import com.example.gamejava.widget.adapter.AbstractWheelAdapter;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("ClickableViewAccessibility")
public class MainActivity extends Activity {
    private ImageView iv_pa;
    private int pre_y;
    private ArrayList<Integer> imageItem;
    private TextView text;
    private Handler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slot_machine_layout);
        imageItem = new ArrayList<>();
        text = (TextView) findViewById(R.id.pwd_status);
        mHandler = new Handler();

        initWheel(R.id.slot_1);
        initWheel(R.id.slot_2);
        initWheel(R.id.slot_3);

        iv_pa = (ImageView) findViewById(R.id.iv_pa);

        iv_pa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text.setText("運轉中");
                iv_pa.setImageResource(R.drawable.down_pa);
                iv_pa.setEnabled(false);
                mixWheel(R.id.slot_1);
                mHandler.postDelayed(slot_2, 500);
                mHandler.postDelayed(slot_3, 800);
            }
        });

//        iv_pa.setOnTouchListener((v, event) -> {
//            switch (event.getAction()) {
//                case MotionEvent.ACTION_DOWN:
//                    pre_y = (int) event.getY();
//                    break;
//                case MotionEvent.ACTION_MOVE:
//                    int now_y = (int) event.getY();
//                    if (pre_y < now_y) {
//                        text.setText("運轉中");
//                        iv_pa.setImageResource(R.drawable.down_pa);
//                        iv_pa.setEnabled(false);
//                        mixWheel(R.id.slot_1);
//                        mHandler.postDelayed(slot_2, 200);
//                        mHandler.postDelayed(slot_3, 300);
//                    }
//                    break;
//            }
//            return true;
//        });
    }

    final Runnable slot_2 = () -> mixWheel(R.id.slot_2);

    final Runnable slot_3 = () -> mixWheel(R.id.slot_3);

    private boolean wheelScrolled = false;


    OnWheelScrollListener scrolledListener = new OnWheelScrollListener() {
        public void onScrollingStarted(WheelView wheel) {
            wheelScrolled = true;
        }

        public void onScrollingFinished(WheelView wheel) {
            wheelScrolled = false;
            updateStatus();
        }
    };

    // Wheel changed listener
    private OnWheelChangedListener changedListener = (wheel, oldValue, newValue) -> {
        if (!wheelScrolled) {
				updateStatus();
        }
    };


    private void updateStatus() {
        if (test())
            text.setText("賓果");
        else
            text.setText("沒有");
        iv_pa.setImageResource(R.drawable.up_pa);
        iv_pa.setEnabled(true);
    }

    private void initWheel(int id) {
        WheelView wheel = getWheel(id);
        wheel.setViewAdapter(new SlotMachineAdapter(this));
        wheel.setCurrentItem((int) (Math.random() * 10));
		wheel.addChangingListener(changedListener);
		wheel.addScrollingListener(scrolledListener);
        wheel.setCyclic(true);
        wheel.setEnabled(false);
    }

//    private void initWheel_3(int id) {
//        WheelView wheel = getWheel(id);
//        wheel.setViewAdapter(new SlotMachineAdapter(this));
//        wheel.setCurrentItem((int) (Math.random() * 10));
//        wheel.addScrollingListener(scrolledListener);
//        wheel.setCyclic(true);
//        wheel.setEnabled(false);
//    }

    private WheelView getWheel(int id) {
        return (WheelView) findViewById(id);
    }

    private boolean test() {
        String value1 = String.valueOf(imageItem.get(getWheel(R.id.slot_1)
                .getCurrentItem()));
        String value2 = String.valueOf(imageItem.get(getWheel(R.id.slot_2)
                .getCurrentItem() + imageItem.size() * 1 / 3));
        String value3 = String.valueOf(imageItem.get(getWheel(R.id.slot_3)
                .getCurrentItem() + imageItem.size() * 2 / 3));

        if (value1.equals(value2) && value2.equals(value3)) {
            Log.e("value1", value1);
            Log.e("value2", value2);
            Log.e("value3", value3);
            Log.e("true", "true");
            return true;
        } else {
            Log.e("value1", value1);
            Log.e("value2", value2);
            Log.e("value3", value3);
            Log.e("false", "false");
            return false;
        }

    }

    /**
     * Tests wheel value
     *
     * @param id    the wheel Id
     * @param value the value to test
     * @return true if wheel value is equal to passed value
     */
    private boolean testWheelValue(int id, int value) {
        return getWheel(id).getCurrentItem() == value;
    }


    private void mixWheel(int id) {
        WheelView wheel = getWheel(id);
        //控制滾輪速度
        wheel.scroll(-350 + (int) (Math.random() * 50), 2000);
    }

    /**
     * Slot machine adapter
     */
    private class SlotMachineAdapter extends AbstractWheelAdapter {
        // Image size
        final int IMAGE_WIDTH = 100; //icon�e��
        final int IMAGE_HEIGHT = 100;

        // Slot machine symbols
        private final int items[] = new int[]{R.drawable.seven,
                R.drawable.money, R.drawable.pineapple, R.drawable.candy};

        // Cached images
        private List<SoftReference<Bitmap>> images;

        // Layout inflater
        private Context context;

        /**
         * Constructor
         */
        public SlotMachineAdapter(Context context){
            this.context = context;

            for (int i = 0; i < 30; i++) {
                int r1, r2;
                do {
                    r1 = (int) ((Math.random() * items.length));
                    r2 = (int) ((Math.random() * items.length));
                } while (r1 == r2);
                int temp;
                temp = items[r1];
                items[r1] = items[r2];
                items[r2] = temp;
            }

            String bbb = "";
            for (int i = 0; i < items.length; i++) {
                imageItem.add(items[i]);
                bbb = bbb + String.valueOf(items[i] + ",");
            }
            Log.e("bbb", bbb);

            images = new ArrayList<SoftReference<Bitmap>>(items.length);
            for (int id : items) {
                images.add(new SoftReference<Bitmap>(loadImage(id)));
            }
        }

        /**
         * Loads image from resources
         */
        private Bitmap loadImage(int id) {
            Bitmap bitmap = BitmapFactory.decodeResource(
                    context.getResources(), id);
            Bitmap scaled = Bitmap.createScaledBitmap(bitmap, IMAGE_WIDTH,
                    IMAGE_HEIGHT, true);
            bitmap.recycle();
            return scaled;
        }

        @Override
        public int getItemsCount() {
            return items.length;
        }

        // Layout params for image view
        final LayoutParams params = new LayoutParams(IMAGE_WIDTH, IMAGE_HEIGHT);

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            ImageView img;
            if (cachedView != null) {
                img = (ImageView) cachedView;
            } else {
                img = new ImageView(context);
            }
            img.setLayoutParams(params);
            SoftReference<Bitmap> bitmapRef = images.get(index);
            Bitmap bitmap = bitmapRef.get();
            if (bitmap == null) {
                bitmap = loadImage(items[index]);
                images.set(index, new SoftReference<Bitmap>(bitmap));
            }
            img.setImageBitmap(bitmap);

            return img;
        }
    }
}
