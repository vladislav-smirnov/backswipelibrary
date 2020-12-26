package io.github.airdaydreamers.samplebackswipelibrary.activity;

import android.os.Bundle;

import io.github.airdaydreamers.backswipelibrary.BackSwipeHelper;
import io.github.airdaydreamers.backswipelibrary.activity.BackSwipeActivity;
import com.airdaydreamers.samplebackswipelibrary.R;

public class FirstActivity extends BackSwipeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        //implementation
        setEdgeOrientation(BackSwipeHelper.EdgeOrientation.LEFT);
        setEdgeSizeLevel(BackSwipeHelper.EdgeSizeLevel.MIN);
    }
}
