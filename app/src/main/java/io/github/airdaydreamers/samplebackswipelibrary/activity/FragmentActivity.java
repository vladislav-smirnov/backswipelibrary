package io.github.airdaydreamers.samplebackswipelibrary.activity;


import android.os.Bundle;
import androidx.fragment.app.Fragment;

import io.github.airdaydreamers.backswipelibrary.listeners.OnFragmentChangeListener;
import com.airdaydreamers.samplebackswipelibrary.R;
import io.github.airdaydreamers.samplebackswipelibrary.fragment.FirstFragment;

public class FragmentActivity extends androidx.fragment.app.FragmentActivity implements OnFragmentChangeListener {

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
                .setCustomAnimations(R.anim.bsl_anim_fragment_enter, R.anim.bsl_anim_fragment_exit, R.anim.bsl_anim_fragment_pop_enter, R.anim.bsl_anim_fragment_pop_exit)
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
