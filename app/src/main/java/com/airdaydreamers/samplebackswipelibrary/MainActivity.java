package com.airdaydreamers.samplebackswipelibrary;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.airdaydreamers.backswipelibrary.BackSwipeHelper;
import com.airdaydreamers.backswipelibrary.activity.BackSwipeActivity;
import com.airdaydreamers.samplebackswipelibrary.activity.FirstActivity;
import com.airdaydreamers.samplebackswipelibrary.activity.FragmentActivity;

public class MainActivity extends BackSwipeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setEdgeOrientation(BackSwipeHelper.EdgeOrientation.LEFT);
        setEdgeSizeLevel(BackSwipeHelper.EdgeSizeLevel.MED);
        setEnableSwipe(true);
        //setScrollChildView(findViewById(R.id.scrollID));
        setTouchSlopThreshold(700.2f);
        //getBackSwipeViewGroup().setBackgroundResource(R.color.colorAccent);
        //setEnableSwipe(false);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonFirst:
                Intent intent = new Intent(this, FirstActivity.class);
                startActivity(intent);
                break;
            case  R.id.buttonSecond:
                Intent intent2 = new Intent(this, FragmentActivity.class);
                startActivity(intent2);
                break;
        }
    }
}
