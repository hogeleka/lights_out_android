package com.algorithmandblues.lightsout.levelselection;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
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

import com.algorithmandblues.lightsout.database.DatabaseConstants;
import com.algorithmandblues.lightsout.database.DatabaseHelper;
import com.algorithmandblues.lightsout.database.GameDataDBHandler;
import com.algorithmandblues.lightsout.database.Level;
import com.algorithmandblues.lightsout.game.GameGridActivity;
import com.algorithmandblues.lightsout.game.GameMode;
import com.algorithmandblues.lightsout.database.GameWinStateDBHandler;
import com.algorithmandblues.lightsout.database.LevelDBHandler;
import com.algorithmandblues.lightsout.R;
import com.algorithmandblues.lightsout.utils.SkillLevelConstants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LevelSelectorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LevelSelectorFragment extends Fragment {

    // the fragment initialization parameter
    private static final String ARG_PARAM1 = "gameMode";


    private static final String TAG = LevelSelectorFragment.class.getSimpleName();
    private static final int INDIVIDUAL_LEVEL_CELL_PADDING = 10;
    private static final int TEXT_SIZE = 24;
    private static final int TEXT_SIZE_BOTTOM_TEXT_BELOW_PROG_BAR = 16;
    private static final int STAR_IMAGE_SIZE_PX = 22;
    private static final int SKILL_LEVEL_TEXT_PADDING_TOP = 20;

    private static final int BORDER_WIDTH_LEVEL_CELL = 2;

    private static final float ALPHA_ENABLED = (float) 1.0;
    private static final float ALPHA_DISABLED = (float) 0.4;

    private static final int TABLE_ROW_MARGIN_HORIZONTAL = 4;
    private static final int PROGRESS_BAR_HORIZONTAL_PADDING = 48;
    private static final int CELL_MARGIN = 8;
    private static final int CELL_BORDER_RADIUS = 5;
    public static final float ONE_THIRD = (float) 0.33;

    DatabaseHelper databaseHelper;
    LevelDBHandler levelDBHandler;
    GameWinStateDBHandler gameWinStateDBHandler;
    Map<Integer, Level> dimensionAndLevel;
    GameDataDBHandler gameDataDBHandler;
    int gameMode;
    String selectLevelPrompt;
    int userProgressLevel;


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
        gameDataDBHandler = GameDataDBHandler.getInstance(databaseHelper);
        Log.d(TAG, "fetched game mode type: " + gameMode + " found data:--" + levels.toString());
        dimensionAndLevel = new HashMap<>();
        for (Level level : levels) {
            dimensionAndLevel.put(level.getDimension(), level);
        }

        if (gameMode == GameMode.CAMPAIGN) {
            userProgressLevel = getUserProgressLevel(levels);
            if (userProgressLevel < DatabaseConstants.MIN_DIMENSION) {
                selectLevelPrompt = getString(R.string.arcade_select_level_prompt_begin);
            } else {
                selectLevelPrompt = getString(R.string.arcade_select_level_prompt_continue);
            }
        } else {
            selectLevelPrompt = getString(R.string.classic_select_level_prompt);
            userProgressLevel = DatabaseConstants.MAX_DIMENSION;
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
        LinearLayout holder = (LinearLayout) view.findViewById(R.id.level_selector_linear_layout);

        TextView selectLevelPromptTextview = (TextView) holder.getChildAt(0);
        selectLevelPromptTextview.setText(selectLevelPrompt);
        selectLevelPromptTextview.setTextColor(getResources().getColor(R.color.custom_black));
        int rowHeight = useDisplayMetricsToCalculateRowHeight();
        LinearLayout.LayoutParams layoutParams = getLayoutParams(rowHeight);
        int dim = DatabaseConstants.MIN_DIMENSION;
        //start from row 1: first row is the select level prompt. 4th row is the row which might have progress bar
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

        TextView userProgressTextView = getTextViewDisplayForLevel(userProgressLevel);
        ProgressBar userProgressBar = getUserProgressBar(userProgressLevel);
        //we always ask them to complete their current progress + 1, to unlock a new title,
        //i.e, if I have completed 2x2, it should ask me to complete "3x3" to unclock new title
        TextView littlePromptBelowProgressBar = getPromptForUserProgressLevel(userProgressLevel+1);
        progressBarHolder.addView(userProgressTextView);
        progressBarHolder.addView(userProgressBar);
        progressBarHolder.addView(littlePromptBelowProgressBar);

        //if it is practice mode, we hide the progress bar.
        // This ensures that the rest of the page is still consistent with its positioning
        if (gameMode == GameMode.PRACTICE) {
            holder.getChildAt(4).setVisibility(View.INVISIBLE);
        }

    }

    private TextView getPromptForUserProgressLevel(int userProgressLevel) {
        TextView textView = new TextView(getContext());
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        String text;
        if (userProgressLevel > DatabaseConstants.MAX_DIMENSION) {
            text = getString(R.string.congrats_message_campaign_complete);
        } else {
            text = String.format(getString(R.string.solve_unlock_achievement_prompt), userProgressLevel, userProgressLevel);
        }
        textView.setText(text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE_BOTTOM_TEXT_BELOW_PROG_BAR);
        textView.setTextColor(getResources().getColor(R.color.custom_black));
        return textView;

    }

    private void selectLevel(Level level) {
        if (userHasExistingGame(level.getDimension(), level.getGameMode())) {
            buildDialogToRequestUserResponse(level);
        } else {
            goToNewGameActivity(level, false);
        }
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
        textView.setPadding(0, getPixels(SKILL_LEVEL_TEXT_PADDING_TOP), 0, 0);
        textView.setTextColor(getResources().getColor(R.color.custom_black));
        return textView;
    }

    private String getTextToDisplayForUserProgress(int nextLevelToUnlock) {
        return SkillLevelConstants.getSkillLevelForLevel(nextLevelToUnlock);
    }

    private int getUserProgressLevel(List<Level> levels) {
        for (int i = DatabaseConstants.MAX_DIMENSION; i>= DatabaseConstants.MIN_DIMENSION; i--) {
            if (dimensionAndLevel.get(i).getNumberOfStars() > 0) {
                return i;
            }
        }
        return DatabaseConstants.MIN_DIMENSION-1;
    }


    private ProgressBar getUserProgressBar(int level) {
//        int progress;
//        if (level.getDimension() < DatabaseConstants.MAX_DIMENSION || level.getNumberOfStars() == 0) {
//            progress = level.getDimension() - DatabaseConstants.MIN_DIMENSION;
//        } else {
//            progress = level.getDimension() - 1;
//        }
        return new ProgressBar(getContext(), null, android.R.attr.progressBarStyleHorizontal) {{
            setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_level_selector));
            setPadding(getPixels(PROGRESS_BAR_HORIZONTAL_PADDING), 0, getPixels(PROGRESS_BAR_HORIZONTAL_PADDING), 0);
            setProgress((int) (((double) (level-1) / (DatabaseConstants.MAX_DIMENSION-1)) * 100));
        }};
    }

    public void buildDialogToRequestUserResponse(Level level) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogStyle);
        builder.setTitle(String.format(getString(R.string.level_picker_resume_or_restart_title), level.getGameMode() == GameMode.CAMPAIGN ? GameMode.CAMPAIGN_STRING : GameMode.PRACTICE_STRING))
                .setMessage(String.format(getString(R.string.level_picker_resume_or_restart_message_prompt), level.getDimension(), level.getDimension()))
                .setCancelable(true)
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> goToNewGameActivity(level, true))
                .setNegativeButton(getString(R.string.no), (dialog, which) -> goToNewGameActivity(level, false));
        //Creating dialog box
        AlertDialog dialog  = builder.create();
        dialog.show();
    }

    public void goToNewGameActivity(Level level, boolean resumeGameFromDBFlag) {
        Intent intent = new Intent(getContext(), GameGridActivity.class);
        intent.putExtra(getString(R.string.dimension), level.getDimension());
        intent.putExtra(getString(R.string.resume_from_db_flag), resumeGameFromDBFlag);
        intent.putExtra(getString(R.string.set_random_state_flag), level.getGameMode() == GameMode.CAMPAIGN);
        intent.putExtra(getString(R.string.best_score_level_gameType), level.getNumberOfStars());
        startActivity(intent);
        getActivity().finish();
    }

    public boolean userHasExistingGame(int dimension, int gameMode) {
        return gameDataDBHandler.getMostRecentGameDataForGameType(dimension, gameMode) != null;
    }
}
