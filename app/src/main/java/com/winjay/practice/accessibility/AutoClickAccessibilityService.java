package com.winjay.practice.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;


public class AutoClickAccessibilityService extends AccessibilityService {
    private static final String TAG = AutoClickAccessibilityService.class.getName();
    private static AutoClickAccessibilityService mAutoClickAccessibilityService;
    public static boolean mIsAppLaunched = false;

    public AutoClickAccessibilityService() {
        super();
        mAutoClickAccessibilityService = this;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
    }

    public static AutoClickAccessibilityService getInstance() {
        return mAutoClickAccessibilityService;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void onClick(int x, int y) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.d(TAG, "TYPE_VIEW_CLICKED x " + x);
            Log.d(TAG, "TYPE_VIEW_CLICKED y " + y);
            GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
            if (x != -1 || y != -1) {
                Path path = new Path();
                path.moveTo(x, y);
                gestureBuilder.addStroke(new GestureDescription.StrokeDescription(path, 10, 10));
                dispatchGesture(gestureBuilder.build(), new GestureResultCallback() {
                    @Override
                    public void onCompleted(GestureDescription gestureDescription) {
                        Log.d(TAG, "TYPE_VIEW_CLICKED Gesture Completed");
                        super.onCompleted(gestureDescription);
                    }
                }, null);
            }
        }
    }

    public void onLongPress(int x, int y) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.d(TAG, "Long Press  x " + x);
            Log.d(TAG, "Long Press  y " + y);
            GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
            if (x != -1 || y != -1) {
                Path path = new Path();
                path.moveTo(x, y);
                gestureBuilder.addStroke(new GestureDescription.StrokeDescription(path, 200, 600));
                dispatchGesture(gestureBuilder.build(), new GestureResultCallback() {
                    @Override
                    public void onCompleted(GestureDescription gestureDescription) {
                        Log.w(TAG, "Long Press Gesture Completed");
                        super.onCompleted(gestureDescription);
                    }
                }, null);
            }
        }
    }

    public void onBackClick() {
        Log.d(TAG, "onBackClick");
        performGlobalAction(GLOBAL_ACTION_BACK);
    }

    public void onHomeClick() {
        Log.d(TAG, "onHomeKeyClick");
        performGlobalAction(GLOBAL_ACTION_HOME);
    }

    public void onAppLaunched() {
        Log.d(TAG, "onAppLaunched");
        mIsAppLaunched = true;
    }

    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;

    public void onSwipe(int x, int y, int endX, int endY, int velocityX_duration, int velocityY_duration) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.d(TAG, "TYPE_VIEW_onSwipe x " + x);
            Log.d(TAG, "TYPE_VIEW_onSwipe y " + y);
            Log.d(TAG, "TYPE_VIEW_onSwipe endx " + endX);
            Log.d(TAG, "TYPE_VIEW_onSwipe endy " + endY);
            Log.d(TAG, "TYPE_VIEW_onSwipe velocityX_duration " + velocityX_duration);
            Log.d(TAG, "TYPE_VIEW_onSwipe velocityY_duration " + velocityY_duration);
            int widthPixels = getResources().getDisplayMetrics().widthPixels;
            int heightPixels = getResources().getDisplayMetrics().heightPixels;
            Log.d(TAG, "Mobile  widthPixels  = " + widthPixels + " heightPixels  = " + heightPixels);

            Path path = new Path();
            path.moveTo(x, y);
            path.lineTo(endX, endY);

            GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();


            int diffY = endY - y;
            int diffX = endX - x;
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD) {
                    if (velocityX_duration == velocityY_duration) {
                        gestureBuilder.addStroke(new GestureDescription.StrokeDescription(path, (velocityX_duration / 8), (velocityX_duration / 7)));
                    } else {
                        double distance = Math.abs(Math.sqrt((x - endX) * (x - endX) + (y - endY) * (y - endY)));
                        //Log.d(TAG, "onFling: ((distance/widthPixels)*1000) = "+(int)((distance/widthPixels)*1000));
                        gestureBuilder.addStroke(new GestureDescription.StrokeDescription(path, 0, (int) ((distance / widthPixels) * 1000)));
                    }

                }
            } else if (Math.abs(diffY) > SWIPE_THRESHOLD) {
                if (velocityX_duration == velocityY_duration) {
                    gestureBuilder.addStroke(new GestureDescription.StrokeDescription(path, (velocityY_duration / 8), (velocityY_duration / 7)));
                } else {
                    double distance = Math.abs(Math.sqrt((x - endX) * (x - endX) + (y - endY) * (y - endY)));
                    //Log.d(TAG, "onFling: yaozong distance Y1 = "+(int)((distance/heightPixels)*1000));
                    if (((int) ((distance / heightPixels) * 1000)) > 850) {
                        gestureBuilder.addStroke(new GestureDescription.StrokeDescription(path, 0, 1));
                    }
                }
            }

            dispatchGesture(gestureBuilder.build(), new GestureResultCallback() {
                @Override
                public void onCompleted(GestureDescription gestureDescription) {
                    Log.w(TAG, "TYPE_ANNOUNCEMENT Gesture Completed");
                    super.onCompleted(gestureDescription);
                }
            }, null);


//            int diffY = endY - y;
//            int diffX = endX - x;
//            if (Math.abs(diffX) > Math.abs(diffY)) {
//                if (Math.abs(diffX) > SWIPE_THRESHOLD) {
//                    if (diffX > 0) {
//                        onSwipeRight();
//                    } else {
//                        onSwipeLeft();
//                    }
//                }
//            }
//            else if (Math.abs(diffY) > SWIPE_THRESHOLD) {
//                if (diffY > 0) {
//                    onSwipeBottom();
//                } else {
//                    onSwipeTop();
//                }
//            }
        }
    }

//    @RequiresApi(api = Build.VERSION_CODES.N)
//    private void onSwipeRight() {
//        Log.d(TAG, "onSwipeRight");
//
//
//        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
//
//        int middleYValue = displayMetrics.heightPixels / 2;
//        final int leftSideOfScreen = displayMetrics.widthPixels / 4;
//        final int rightSizeOfScreen = leftSideOfScreen * 3;
//        Path path = new Path();
//        //Swipe right
//        path.moveTo(leftSideOfScreen, middleYValue);
//        path.lineTo(rightSizeOfScreen, middleYValue);
//
//        GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
//        gestureBuilder.addStroke(new GestureDescription.StrokeDescription(path, 0L, 100L));
//        dispatchGesture(gestureBuilder.build(), new GestureResultCallback() {
//            @Override
//            public void onCompleted(GestureDescription gestureDescription) {
//                Log.w(TAG, "TYPE_ANNOUNCEMENT Gesture Completed");
//                super.onCompleted(gestureDescription);
//            }
//        }, null);
//
//
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.N)
//    private void onSwipeLeft()  {
//        Log.d(TAG, "onSwipeLeft");
//
//        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
//
//        int middleYValue = displayMetrics.heightPixels / 2;
//        final int leftSideOfScreen = displayMetrics.widthPixels / 4;
//        final int rightSizeOfScreen = leftSideOfScreen * 3;
//        Path path = new Path();
//        //Swipe left
//        path.moveTo(rightSizeOfScreen, middleYValue);
//        path.lineTo(leftSideOfScreen, middleYValue);
//
//        GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
//        gestureBuilder.addStroke(new GestureDescription.StrokeDescription(path, 0L, 100L));
//        dispatchGesture(gestureBuilder.build(), new GestureResultCallback() {
//            @Override
//            public void onCompleted(GestureDescription gestureDescription) {
//                Log.w(TAG, "TYPE_ANNOUNCEMENT Gesture Completed");
//                super.onCompleted(gestureDescription);
//            }
//        }, null);
//
//
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.N)
//    private void onSwipeTop() {
//        Log.d(TAG, "onSwipeTop");
//
//        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
//
//        int middleXValue = displayMetrics.widthPixels / 2;
//
//
//        final int topSideOfScreen = displayMetrics.heightPixels / 4;
//        final int bottomSizeOfScreen = topSideOfScreen * 3;
//        Path path = new Path();
//        //Swipe top
//        path.moveTo(middleXValue, bottomSizeOfScreen);
//        path.lineTo(middleXValue, topSideOfScreen);
//
//        GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
//        gestureBuilder.addStroke(new GestureDescription.StrokeDescription(path, 0L, 100L));
//        dispatchGesture(gestureBuilder.build(), new GestureResultCallback() {
//            @Override
//            public void onCompleted(GestureDescription gestureDescription) {
//                Log.w(TAG, "TYPE_ANNOUNCEMENT Gesture Completed");
//                super.onCompleted(gestureDescription);
//            }
//        }, null);
//
//
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.N)
//    private void onSwipeBottom() {
//        Log.d(TAG, "onSwipeBottom");
//
//
//        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
//
//        int middleXValue = displayMetrics.widthPixels / 2;
//
//
//        final int topSideOfScreen = displayMetrics.heightPixels / 4;
//        final int bottomSizeOfScreen = topSideOfScreen * 3;
//        Path path = new Path();
//        //Swipe bottom
//        path.moveTo(middleXValue, topSideOfScreen);
//        path.lineTo(middleXValue, bottomSizeOfScreen);
//
//        GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
//        gestureBuilder.addStroke(new GestureDescription.StrokeDescription(path, 0L, 100L));
//        dispatchGesture(gestureBuilder.build(), new GestureResultCallback() {
//            @Override
//            public void onCompleted(GestureDescription gestureDescription) {
//                Log.w(TAG, "TYPE_ANNOUNCEMENT Gesture Completed");
//                super.onCompleted(gestureDescription);
//            }
//        }, null);
//
//    }
//


    @Override
    public void onInterrupt() {
    }

}
