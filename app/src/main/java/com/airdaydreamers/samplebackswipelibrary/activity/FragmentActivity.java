package com.airdaydreamers.samplebackswipelibrary.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.airdaydreamers.backswipelibrary.listeners.OnFragmentChangeListener;
import com.airdaydreamers.samplebackswipelibrary.R;
import com.airdaydreamers.samplebackswipelibrary.fragment.FirstFragment;

public class FragmentActivity extends android.support.v4.app.FragmentActivity implements OnFragmentChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        FirstFragment firstFragment = FirstFragment.newInstance();
        loadFragment(firstFragment);
    }


    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    private void addFragment(Fragment fromFragment, Fragment toFragment) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.h_fragment_enter, R.anim.h_fragment_exit, R.anim.h_fragment_pop_enter, R.anim.h_fragment_pop_exit)
                .add(R.id.frameId, toFragment, toFragment.getClass().getSimpleName())
                .hide(fromFragment)
                .addToBackStack(toFragment.getClass().getSimpleName())
                .commit();
    }

    private void loadFragment(Fragment toFragment) {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.frameId, toFragment, toFragment.getClass().getSimpleName())
                .addToBackStack(toFragment.getClass().getSimpleName())
                .commit();
    }

    @Override
    public void onFragmentAdded(Fragment fromFragment, Fragment toFragment) {
        addFragment(fromFragment, toFragment);
    }
}
