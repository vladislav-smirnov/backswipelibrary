package io.github.airdaydreamers.samplebackswipelibrary;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import io.github.airdaydreamers.backswipelibrary.BackSwipeHelper;
import io.github.airdaydreamers.backswipelibrary.activity.BackSwipeActivity;

import com.airdaydreamers.samplebackswipelibrary.R;

import io.github.airdaydreamers.samplebackswipelibrary.activity.FirstActivity;
import io.github.airdaydreamers.samplebackswipelibrary.activity.FragmentActivity;
import io.github.airdaydreamers.samplebackswipelibrary.activity.SecondActivity;

public class MainActivity extends BackSwipeActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.buttonFirst).setOnClickListener(this);
        findViewById(R.id.buttonSecond).setOnClickListener(this);
        findViewById(R.id.buttonFragmentActivity).setOnClickListener(this);

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        TextView textView = toolbar.findViewById(R.id.textToolbar);
        textView.setText(getString(R.string.bsl_App_name));

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //implementation
        setEdgeOrientation(BackSwipeHelper.EdgeOrientation.LEFT);
        setEdgeSizeLevel(BackSwipeHelper.EdgeSizeLevel.MED);
        setEnableSwipe(true);
        //setScrollChildView(findViewById(R.id.scrollID));
        setTouchSlopThreshold(700.2f);
        //getBackSwipeViewGroup().setBackgroundResource(R.color.colorAccent);
        //setEnableSwipe(false);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonFirst:
                Intent intent = new Intent(this, FirstActivity.class);
                startActivity(intent);
                break;
            case R.id.buttonSecond:
                Intent intent2 = new Intent(this, SecondActivity.class);
                startActivity(intent2);
                break;
            case R.id.buttonFragmentActivity:
                Intent intentFragment = new Intent(this, FragmentActivity.class);
                startActivity(intentFragment);
                break;
        }
    }
}
