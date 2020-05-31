package com.algorithmandblues.lightsout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.algorithmandblues.lightsout.FirstLightsOutRuleFragment;

public class RulesPageAdapter extends FragmentStateAdapter {

    public RulesPageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return FirstLightsOutRuleFragment.newInstance();
            case 1:
                return SecondLightsOutRuleFragment.newInstance();
        }
        return FirstLightsOutRuleFragment.newInstance();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
