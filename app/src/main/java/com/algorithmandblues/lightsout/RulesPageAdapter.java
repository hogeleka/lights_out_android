package com.algorithmandblues.lightsout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class RulesPageAdapter extends FragmentStateAdapter {

    public RulesPageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return BasicRuleFragment.newInstance();
            case 1:
                return HintAndSolutionRuleFragment.newInstance();
            case 2:
                return StarRulesFragment.newInstance();
        }
        return BasicRuleFragment.newInstance();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
