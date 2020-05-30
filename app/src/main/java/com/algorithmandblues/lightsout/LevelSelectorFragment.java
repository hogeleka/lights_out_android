package com.algorithmandblues.lightsout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LevelSelectorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LevelSelectorFragment extends Fragment {

    private static final String TAG = LevelSelectorFragment.class.getSimpleName();
    // TODO: Rename parameter arguments, choose names that match
    private static final int INDIVIDUAL_LEVEL_CELL_PADDING = 10;
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final int TEXT_SIZE = 24;
    private static final String ARG_PARAM1 = "gameMode";
    private static final int STAR_IMAGE_SIZE_PX = 22;
    DatabaseHelper databaseHelper;
    LevelDBHandler levelDBHandler;
    Map<Integer, Level> dimensionAndLevel;
    int gameMode;
//    String gameModeDescription;
    String selectLevelPrompt;
    int userProgressLevel;

    private static final float ALPHA_ENABLED = (float) 1.0;
    private static final float ALPHA_DISABLED = (float) 0.4;

    private static final int TABLE_ROW_MARGIN_HORIZONTAL = 8;
    private static final int PROGRESS_BAR_HORIZONTAL_PADDING = 48;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LevelSelectorFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LevelSelectorFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LevelSelectorFragment newInstance(int gameMode) {
        LevelSelectorFragment fragment = new LevelSelectorFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, gameMode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            gameMode = getArguments().getInt(ARG_PARAM1);
        }
        databaseHelper = DatabaseHelper.getInstance(getContext());
        levelDBHandler = LevelDBHandler.getInstance(databaseHelper);
        List<Level> levels = levelDBHandler.fetchLevelsForGameMode(gameMode);
        Log.d(TAG, "fetched game mode type: " + gameMode + " found data:--" + levels.toString());
        dimensionAndLevel = new HashMap<>();
        for (Level level : levels) {
            dimensionAndLevel.put(level.getDimension(), level);
        }

        if (gameMode == GameMode.ARCADE) {
            selectLevelPrompt = getString(R.string.arcade_select_level_prompt);
            userProgressLevel = getUserProgressLevel(levels);
        } else {
            selectLevelPrompt = getString(R.string.classic_select_level_prompt);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_level_selector_tab, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        LinearLayout holder = (LinearLayout) view.findViewById(R.id.arcadeTabFragment);

//        TextView gameModeDescriptionTextview = (TextView) holder.getChildAt(0);
//        gameModeDescriptionTextview.setText(gameModeDescription);
//        gameModeDescriptionTextview.setTextColor(getResources().getColor(R.color.custom_black));

        TextView selectLevelPromptTextview = (TextView) holder.getChildAt(0);
        selectLevelPromptTextview.setText(selectLevelPrompt);
        selectLevelPromptTextview.setTextColor(getResources().getColor(R.color.custom_black));

        int rowHeight = useDisplayMetricsToCalculateRowHeight();
        LinearLayout.LayoutParams layoutParams = getLayoutParams(rowHeight);
        int dim = DatabaseConstants.MIN_DIMENSION;
        //start from row 1: first row is the select level prompt
        for (int row = 1; row < 4; row++) {
            ((LinearLayout) holder.getChildAt(row)).setGravity(Gravity.CENTER);
            holder.getChildAt(row).setLayoutParams(layoutParams);
            for (int col = 0; col < 3; col++) {
                Level level = dimensionAndLevel.get(dim);
                String label = String.format(getString(R.string.level_chooser_button_label), dim, dim);
                LinearLayout container = (LinearLayout)((LinearLayout) holder.getChildAt(row)).getChildAt(col);
                ((TextView) container.getChildAt(0)).setText(label);
                ((TextView) container.getChildAt(0)).setTextColor(Color.BLACK);
                int cellPadding = getPixels(INDIVIDUAL_LEVEL_CELL_PADDING);
                container.setPadding(cellPadding, cellPadding, cellPadding, cellPadding);
                TypedValue outValue = new TypedValue();
                getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
                container.setBackgroundResource(outValue.resourceId);
                container.setGravity(Gravity.CENTER);
                (container.getChildAt(0)).setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                LinearLayout rowOfStars = (LinearLayout) container.getChildAt(1);
                rowOfStars.setGravity(Gravity.CENTER);
                for (int i = 1; i <= 3; i++) {
                    ImageView image = (ImageView) rowOfStars.getChildAt(i-1);
                    int imageSize = getPixels(STAR_IMAGE_SIZE_PX);
                    image.setLayoutParams(new LinearLayout.LayoutParams(imageSize, imageSize));
                    image.setAlpha(i <= level.getNumberOfStars() ? ALPHA_ENABLED : ALPHA_DISABLED);
                }
                container.setClickable(true);
                container.setAlpha(level.getIsLocked() != DatabaseConstants.LOCKED_LEVEL ? ALPHA_ENABLED : ALPHA_DISABLED);
                container.setEnabled(level.getIsLocked() != DatabaseConstants.LOCKED_LEVEL);
                container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "chose a level: " + level.toString());
                        selectLevel(level);
                    }
                });
                dim++;
            }
        }
        LinearLayout progressBarHolder = (LinearLayout) holder.getChildAt(4);

        if (gameMode == GameMode.ARCADE) {
            TextView userProgressTextView = getTextViewDisplayForLevel(userProgressLevel);
            ProgressBar userProgressBar = getUserProgressBar(userProgressLevel);
            progressBarHolder.addView(userProgressTextView);
            progressBarHolder.addView(userProgressBar);
        }

    }

    private void selectLevel(Level level) {
        int dimension = level.getDimension();
        boolean shouldSetRandomStateFlag = level.getGameMode() == GameMode.ARCADE;
        boolean resumeFromDB = false;
        int bestScoreForGameModeAndType = level.getNumberOfStars();
        Intent intent = new Intent(getActivity(), GameGridActivity.class);
        intent.putExtra(getString(R.string.dimension), dimension);
        intent.putExtra(getString(R.string.set_random_state_flag), shouldSetRandomStateFlag);
        intent.putExtra(getString(R.string.best_score_level_gameType), bestScoreForGameModeAndType);
        intent.putExtra(getString(R.string.resume_from_db_flag), resumeFromDB);
        startActivity(intent);
        getActivity().finish();
    }

    private int getPixels(int value) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (value * scale + 0.5f);
    }

    private int useDisplayMetricsToCalculateRowHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        //we are dividing screen width by 4 for the level chooser;
        return displayMetrics.widthPixels / 4;
    }

    private LinearLayout.LayoutParams getLayoutParams(int rowHeight) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, rowHeight);
        layoutParams.leftMargin = getPixels(TABLE_ROW_MARGIN_HORIZONTAL);
        layoutParams.rightMargin = getPixels(TABLE_ROW_MARGIN_HORIZONTAL);
        return layoutParams;
    }

    private TextView getTextViewDisplayForLevel(int level) {
        TextView textView = new TextView(getContext());
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        String levelTitle = getTextToDisplayForUserProgress(level);
        textView.setText(levelTitle);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE);
        textView.setTextColor(getResources().getColor(R.color.custom_black));
        return textView;
    }

    private String getTextToDisplayForUserProgress(int nextLevelToUnlock) {
        return SkillLevelConstants.getSkillLevelForLevel(nextLevelToUnlock);
    }

    private int getUserProgressLevel(List<Level> levels) {
        int result = DatabaseConstants.MIN_DIMENSION;
        for (Level level : levels) {
            if (level.getDimension() > result && level.getIsLocked() == DatabaseConstants.UNLOCKED_LEVEL) {
                result = level.getDimension();
            }
        }
        return result;
    }

//    private ProgressBar getUserProgressBar(int level) {
//        ProgressBar progressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleHorizontal);
//        progressBar.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_level_selector));
//        int horizontalPadding = getPixels(PROGRESS_BAR_HORIZONTAL_PADDING);
//        progressBar.setPadding(horizontalPadding, 0, horizontalPadding, 0);
////        int level = nextLevel-1;
//        int progress = (int) (((double) (level-1) / DatabaseConstants.MAX_DIMENSION) * 100);
//        progressBar.setProgress(progress);
//        Log.d(TAG, "progress: " + progressBar.getProgress());
//        return progressBar;
//    }

    private ProgressBar getUserProgressBar(int level) {
        return new ProgressBar(getContext(), null, android.R.attr.progressBarStyleHorizontal) {{
            setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_level_selector));
            setPadding(getPixels(PROGRESS_BAR_HORIZONTAL_PADDING), 0, getPixels(PROGRESS_BAR_HORIZONTAL_PADDING), 0);
            setProgress((int) (((double) (level-1) / (DatabaseConstants.MAX_DIMENSION-1)) * 100));
        }};
    }
}
