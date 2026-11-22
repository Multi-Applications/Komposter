package de.multiapplications.komposter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.PathInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.core.widget.NestedScrollView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final long FLIP_DURATION = 300L;
    private final List<String> players = new ArrayList<>();
    private final List<Integer> playerRevealOrder = new ArrayList<>();
    private final List<Integer> komposterIndices = new ArrayList<>();
    private final LinkedHashSet<String> selectedCategories = new LinkedHashSet<>();
    private final Random random = new Random();
    private boolean showHints = true;
    private String secretWord;
    private String secretHint;
    private int currentPlayerIndex;
    private boolean cardRevealed;
    private boolean cardHasBeenFlipped;
    private boolean updatingCategories;
    private int editingPlayerIndex = -1;
    private int selectedVoteIndex = -1;
    private int uiMode;
    private View rootView;
    private View setupScreen;
    private View playersScreen;
    private View categoriesScreen;
    private View cardScreen;
    private View discussionScreen;
    private View voteScreen;
    private View revealScreen;
    private View playersRow;
    private TextView playersCount;
    private MaterialSwitch showHintsSwitch;
    private View categoriesRow;
    private TextView categoriesLabel;
    private TextView categoriesCount;
    private MaterialButton startGameButton;
    private MaterialButton playersBackButton;
    private ViewGroup playersListContainer;
    private MaterialButton addPlayerButton;
    private MaterialButton categoriesBackButton;
    private MaterialButton toggleAllCategoriesButton;
    private ViewGroup categoriesListContainer;
    private MaterialButton closeGameButton;
    private View cardWrapper;
    private View cardFront;
    private TextView cardFrontPlayerName;
    private TextView cardBackWordText;
    private TextView cardBackHintText;
    private ImageView cardBackArrow;
    private MaterialButton nextPlayerButton;
    private MaterialButton closeDiscussionButton;
    private TextView startingPlayerText;
    private MaterialButton voteButton;
    private MaterialButton voteBackButton;
    private ViewGroup voteListContainer;
    private MaterialButton voteContinueButton;
    private MaterialButton closeRevealButton;
    private TextView revealPrefix;
    private TextView revealNames;
    private TextView revealSecretWord;
    private MaterialButton playAgainButton;
    private MaterialButton backToStartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getWindow().setStatusBarContrastEnforced(false);
            getWindow().setNavigationBarContrastEnforced(false);
        }

        uiMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        boolean isNightMode = uiMode == Configuration.UI_MODE_NIGHT_YES;
        WindowInsetsControllerCompat windowInsetsController = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        if (windowInsetsController != null) {
            windowInsetsController.show(WindowInsetsCompat.Type.systemBars());
            windowInsetsController.setAppearanceLightStatusBars(!isNightMode);
            windowInsetsController.setAppearanceLightNavigationBars(!isNightMode);
        }

        setContentView(R.layout.activity_main);
        bindViews();
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (view, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });
        ViewCompat.requestApplyInsets(rootView);

        loadConfig();
        setupListeners();
        setupCardInteraction();
        cardBackArrow.animate().cancel();
        ObjectAnimator arrowBounce = ObjectAnimator.ofFloat(cardBackArrow, View.TRANSLATION_Y, 0f, 6f * getResources().getDisplayMetrics().density);
        arrowBounce.setDuration(650L);
        arrowBounce.setRepeatCount(ValueAnimator.INFINITE);
        arrowBounce.setRepeatMode(ValueAnimator.REVERSE);
        arrowBounce.start();
        updateSetupSummary();
        showScreen(setupScreen);
    }

    private void bindViews() {
        rootView = findViewById(R.id.root);
        setupScreen = findViewById(R.id.setup_screen);
        playersScreen = findViewById(R.id.players_screen);
        categoriesScreen = findViewById(R.id.categories_screen);
        cardScreen = findViewById(R.id.card_screen);
        discussionScreen = findViewById(R.id.discussion_screen);
        voteScreen = findViewById(R.id.vote_screen);
        revealScreen = findViewById(R.id.reveal_screen);

        playersRow = findViewById(R.id.players_row);
        playersCount = findViewById(R.id.players_count);
        showHintsSwitch = findViewById(R.id.show_hints);
        categoriesRow = findViewById(R.id.categories_row);
        categoriesLabel = findViewById(R.id.categories_label);
        categoriesCount = findViewById(R.id.categories_count);
        startGameButton = findViewById(R.id.start_game_button);

        playersBackButton = findViewById(R.id.players_back_button);
        playersListContainer = findViewById(R.id.players_list_container);
        addPlayerButton = findViewById(R.id.add_player_button);

        categoriesBackButton = findViewById(R.id.categories_back_button);
        toggleAllCategoriesButton = findViewById(R.id.toggle_all_categories_button);
        categoriesListContainer = findViewById(R.id.categories_list_container);

        closeGameButton = findViewById(R.id.close_game_button);
        cardWrapper = findViewById(R.id.card_wrapper);
        cardFront = findViewById(R.id.card_front);
        cardFrontPlayerName = findViewById(R.id.card_front_player_name);
        cardBackWordText = findViewById(R.id.card_back_word_text);
        cardBackHintText = findViewById(R.id.card_back_hint_text);
        cardBackArrow = findViewById(R.id.card_back_arrow);
        nextPlayerButton = findViewById(R.id.next_player_button);

        closeDiscussionButton = findViewById(R.id.close_discussion_button);
        startingPlayerText = findViewById(R.id.starting_player_text);
        voteButton = findViewById(R.id.vote_button);

        voteBackButton = findViewById(R.id.vote_back_button);
        voteListContainer = findViewById(R.id.vote_list_container);
        voteContinueButton = findViewById(R.id.vote_continue_button);

        closeRevealButton = findViewById(R.id.close_reveal_button);
        revealPrefix = findViewById(R.id.reveal_prefix);
        revealNames = findViewById(R.id.reveal_names);
        revealSecretWord = findViewById(R.id.reveal_secret_word);
        playAgainButton = findViewById(R.id.play_again_button);
        backToStartButton = findViewById(R.id.back_to_start_button);
    }

    private void loadConfig() {
        GameStore.Config config = GameStore.loadConfig(this);
        players.clear();
        players.addAll(config.players);
        showHints = config.showHints;
        selectedCategories.clear();
        selectedCategories.addAll(config.selectedCategories);
    }

    private void setupListeners() {
        playersRow.setOnClickListener(view -> {
            renderPlayersList();
            showScreen(playersScreen);
        });

        categoriesRow.setOnClickListener(view -> {
            renderCategoriesList();
            showScreen(categoriesScreen);
        });

        showHintsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            showHints = isChecked;
            persistConfig();
        });

        startGameButton.setOnClickListener(view -> startGame());
        playersBackButton.setOnClickListener(view -> showScreen(setupScreen));
        categoriesBackButton.setOnClickListener(view -> showScreen(setupScreen));
        addPlayerButton.setOnClickListener(view -> addPlayer());
        toggleAllCategoriesButton.setOnClickListener(view -> toggleAllCategories());

        closeGameButton.setOnClickListener(view -> openCancelGameDialog());
        closeDiscussionButton.setOnClickListener(view -> openCancelGameDialog());
        closeRevealButton.setOnClickListener(view -> showScreen(setupScreen));
        voteButton.setOnClickListener(view -> openVoteScreen());
        voteBackButton.setOnClickListener(view -> showScreen(discussionScreen));
        voteContinueButton.setOnClickListener(view -> showRevealScreen());

        playAgainButton.setOnClickListener(view -> {
            if (prepareGameSession()) {
                resetCardState();
                showScreen(cardScreen);
            }
        });
        backToStartButton.setOnClickListener(view -> showScreen(setupScreen));

        nextPlayerButton.setOnClickListener(view -> {
            currentPlayerIndex++;
            if (currentPlayerIndex >= players.size()) {
                finishRoundAndShowDiscussion();
            } else {
                resetCardState();
            }
        });
    }

    private void setupCardInteraction() {
        final int touchSlop = ViewConfiguration.get(this).getScaledTouchSlop();
        cardWrapper.setOnTouchListener(new View.OnTouchListener() {
            private float startY;
            private boolean dragging;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        startY = event.getY();
                        dragging = false;
                        cardFront.animate().cancel();
                        if (view.getParent() != null) {
                            view.getParent().requestDisallowInterceptTouchEvent(true);
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        float distance = event.getY() - startY;
                        if (!dragging && Math.abs(distance) > touchSlop) {
                            dragging = true;
                        }
                        if (dragging) {
                            float height = cardFront.getHeight();
                            float base = cardRevealed ? -height : 0f;
                            float translation = base + distance;
                            if (translation > 0f) {
                                translation = 0f;
                            }
                            if (translation < -height) {
                                translation = -height;
                            }
                            cardFront.setTranslationY(translation);
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
                        if (view.getParent() != null) {
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                        }
                        if (!dragging) {
                            setCardRevealed(!cardRevealed);
                        } else {
                            float distanceUp = event.getY() - startY;
                            float threshold = 60f * getResources().getDisplayMetrics().density;
                            if (!cardRevealed && distanceUp < -threshold) {
                                setCardRevealed(true);
                            } else if (cardRevealed && distanceUp > threshold) {
                                setCardRevealed(false);
                            } else {
                                applyCardPosition(true);
                            }
                        }
                        dragging = false;
                        return true;
                    case MotionEvent.ACTION_CANCEL:
                        if (view.getParent() != null) {
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                        }
                        dragging = false;
                        applyCardPosition(true);
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    private void showScreen(View target) {
        View[] screens = {setupScreen, playersScreen, categoriesScreen, cardScreen, discussionScreen, voteScreen, revealScreen};
        for (View screen : screens) {
            if (screen != null) {
                screen.setVisibility(screen == target ? View.VISIBLE : View.GONE);
            }
        }
        if (target instanceof NestedScrollView) {
            NestedScrollView scrollView = (NestedScrollView) target;
            scrollView.post(() -> scrollView.scrollTo(0, 0));
        }
        if (target == setupScreen) {
            updateSetupSummary();
        }
    }

    private void updateSetupSummary() {
        playersCount.setText(String.valueOf(players.size()));
        showHintsSwitch.setChecked(showHints);

        if (selectedCategories.isEmpty()) {
            categoriesLabel.setText(R.string.categories_choose);
            categoriesCount.setText("");
        } else {
            String firstId = selectedCategories.iterator().next();
            String firstName = getString(R.string.categories_title);
            for (GameContent.Category category : GameContent.CATEGORIES) {
                if (category.id.equals(firstId)) {
                    firstName = category.name;
                    break;
                }
            }
            categoriesLabel.setText(firstName);
            categoriesCount.setText(selectedCategories.size() > 1 ? " +" + (selectedCategories.size() - 1) : "");
        }
    }

    private void renderPlayersList() {
        playersListContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < players.size(); i++) {
            View row = inflater.inflate(R.layout.item_player, null, false);
            TextView name = row.findViewById(R.id.player_name);
            name.setText(players.get(i));

            View divider = row.findViewById(R.id.player_divider);
            divider.setVisibility(i == players.size() - 1 ? View.GONE : View.VISIBLE);

            final int index = i;
            row.findViewById(R.id.edit_player_button).setOnClickListener(view -> openEditPlayerDialog(index));
            row.findViewById(R.id.delete_player_button).setOnClickListener(view -> {
                if (players.size() > 3) {
                    players.remove(index);
                    persistConfig();
                    renderPlayersList();
                    updateSetupSummary();
                } else {
                    Toast.makeText(this, R.string.toast_min_players, Toast.LENGTH_SHORT).show();
                }
            });

            playersListContainer.addView(row);
        }
    }

    private void renderCategoriesList() {
        categoriesListContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < GameContent.CATEGORIES.size(); i++) {
            GameContent.Category category = GameContent.CATEGORIES.get(i);
            View row = inflater.inflate(R.layout.item_check_row, categoriesListContainer, false);
            TextView name = row.findViewById(R.id.check_row_name);
            name.setText(category.name);

            MaterialCheckBox checkBox = row.findViewById(R.id.check_row_checkbox);
            checkBox.setChecked(selectedCategories.contains(category.id));
            checkBox.setOnClickListener(view -> updateSelectedCategoriesFromList());
            row.setOnClickListener(view -> checkBox.performClick());

            View divider = row.findViewById(R.id.check_row_divider);
            divider.setVisibility(i == GameContent.CATEGORIES.size() - 1 ? View.GONE : View.VISIBLE);

            categoriesListContainer.addView(row);
        }
        updateToggleAllLabel();
    }

    private MaterialCheckBox categoryCheckBoxAt(int index) {
        return categoriesListContainer.getChildAt(index).findViewById(R.id.check_row_checkbox);
    }

    private void updateSelectedCategoriesFromList() {
        selectedCategories.clear();
        for (int i = 0; i < categoriesListContainer.getChildCount(); i++) {
            MaterialCheckBox checkBox = categoryCheckBoxAt(i);
            if (checkBox != null && checkBox.isChecked() && i < GameContent.CATEGORIES.size()) {
                selectedCategories.add(GameContent.CATEGORIES.get(i).id);
            }
        }
        persistConfig();
        updateSetupSummary();
        updateToggleAllLabel();
    }

    private void updateToggleAllLabel() {
        boolean allChecked = true;
        for (int i = 0; i < categoriesListContainer.getChildCount(); i++) {
            MaterialCheckBox checkBox = categoryCheckBoxAt(i);
            if (checkBox != null && !checkBox.isChecked()) {
                allChecked = false;
                break;
            }
        }
        toggleAllCategoriesButton.setText(allChecked ? R.string.none : R.string.all);
    }

    private void toggleAllCategories() {
        boolean allChecked = true;
        for (int i = 0; i < categoriesListContainer.getChildCount(); i++) {
            MaterialCheckBox checkBox = categoryCheckBoxAt(i);
            if (checkBox != null && !checkBox.isChecked()) {
                allChecked = false;
                break;
            }
        }

        for (int i = 0; i < categoriesListContainer.getChildCount(); i++) {
            MaterialCheckBox checkBox = categoryCheckBoxAt(i);
            if (checkBox != null) {
                checkBox.setChecked(!allChecked);
            }
        }
        updateSelectedCategoriesFromList();
    }

    private void startGame() {
        if (selectedCategories.isEmpty()) {
            Toast.makeText(this, R.string.toast_choose_category, Toast.LENGTH_SHORT).show();
            return;
        }
        if (prepareGameSession()) {
            resetCardState();
            showScreen(cardScreen);
        }
    }

    private boolean prepareGameSession() {
        playerRevealOrder.clear();
        for (int i = 0; i < players.size(); i++) {
            playerRevealOrder.add(i);
        }

        List<Integer> shuffledIndices = new ArrayList<>(playerRevealOrder);
        Collections.shuffle(shuffledIndices, random);
        komposterIndices.clear();
        komposterIndices.addAll(shuffledIndices.subList(0, 1));

        GameContent.Word word = pickSecretWord();
        if (word == null) {
            Toast.makeText(this, R.string.toast_choose_category, Toast.LENGTH_SHORT).show();
            return false;
        }

        secretWord = word.word;
        secretHint = word.hint;
        currentPlayerIndex = 0;
        cardRevealed = false;
        cardHasBeenFlipped = false;
        return true;
    }

    private GameContent.Word pickSecretWord() {
        List<GameContent.Word> pool = new ArrayList<>();
        for (GameContent.Word word : GameContent.WORDS) {
            if (selectedCategories.contains(word.category)) {
                pool.add(word);
            }
        }
        if (pool.isEmpty()) {
            return null;
        }

        List<String> usedWords = GameStore.loadUsedWords(this);
        List<GameContent.Word> available = new ArrayList<>();
        for (GameContent.Word word : pool) {
            if (!usedWords.contains(word.word)) {
                available.add(word);
            }
        }
        if (available.isEmpty()) {
            usedWords = new ArrayList<>();
            available = pool;
        }

        GameContent.Word picked = available.get(random.nextInt(available.size()));
        usedWords.add(picked.word);
        GameStore.saveUsedWords(this, usedWords);
        return picked;
    }

    private void updateCardScreenUI() {
        int realIndex = playerRevealOrder.get(currentPlayerIndex);
        boolean isKomposter = komposterIndices.contains(realIndex);

        cardFrontPlayerName.setText(players.get(realIndex));
        nextPlayerButton.setText(currentPlayerIndex == players.size() - 1 ? R.string.start_game : R.string.next);

        if (isKomposter) {
            cardBackWordText.setText(R.string.you_are_komposter);
            cardBackWordText.setTextColor(ContextCompat.getColor(this, R.color.colorError));
            if (showHints) {
                String hint = secretHint == null || secretHint.isEmpty() ? getString(R.string.fallback_hint) : secretHint;
                cardBackHintText.setText(getString(R.string.hint_prefix, hint));
                cardBackHintText.setVisibility(View.VISIBLE);
            } else {
                cardBackHintText.setVisibility(View.GONE);
            }
        } else {
            cardBackWordText.setText(secretWord);
            cardBackWordText.setTextColor(ContextCompat.getColor(this, R.color.colorOnSurface));
            cardBackHintText.setVisibility(View.GONE);
        }

        updateNextButtonState();
    }

    private void updateNextButtonState() {
        nextPlayerButton.setEnabled(cardHasBeenFlipped && !cardRevealed);
    }

    private void setCardRevealed(boolean revealed) {
        cardRevealed = revealed;
        if (revealed) {
            cardHasBeenFlipped = true;
        }
        updateNextButtonState();
        applyCardPosition(true);
    }

    private void applyCardPosition(boolean animate) {
        int height = cardFront.getHeight();
        if (height <= 0) {
            return;
        }
        float target = cardRevealed ? -height : 0f;
        cardFront.animate().cancel();
        if (animate) {
            cardFront.animate()
                    .translationY(target)
                    .setDuration(FLIP_DURATION)
                    .setInterpolator(new PathInterpolator(0.22f, 1f, 0.36f, 1f))
                    .start();
        } else {
            cardFront.setTranslationY(target);
        }
    }

    private void resetCardState() {
        cardRevealed = false;
        cardHasBeenFlipped = false;
        cardFront.animate().cancel();
        cardFront.setTranslationY(0f);
        updateCardScreenUI();
    }

    private void finishRoundAndShowDiscussion() {
        String starter = players.get(random.nextInt(players.size()));
        startingPlayerText.setText(getString(R.string.starting_player, starter));
        showScreen(discussionScreen);
    }

    private void showRevealScreen() {
        List<String> names = new ArrayList<>();
        for (int index : komposterIndices) {
            names.add(players.get(index));
        }

        revealPrefix.setText(names.size() > 1 ? R.string.komposter_plural : R.string.komposter_singular);
        revealNames.setText(TextUtils.join(" & ", names));

        String label = getString(R.string.secret_word) + " ";
        SpannableString secretText = new SpannableString(label + secretWord);
        secretText.setSpan(new StyleSpan(Typeface.BOLD), label.length(), secretText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        revealSecretWord.setText(secretText);

        showScreen(revealScreen);
    }

    private void openVoteScreen() {
        selectedVoteIndex = -1;
        renderVoteList();
        updateVoteContinueState();
        showScreen(voteScreen);
    }

    private void renderVoteList() {
        voteListContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < players.size(); i++) {
            View row = inflater.inflate(R.layout.item_check_row, voteListContainer, false);
            TextView name = row.findViewById(R.id.check_row_name);
            name.setText(players.get(i));

            MaterialCheckBox checkBox = row.findViewById(R.id.check_row_checkbox);
            checkBox.setChecked(i == selectedVoteIndex);
            final int index = i;
            checkBox.setOnClickListener(view -> selectVote(index));
            row.setOnClickListener(view -> checkBox.performClick());

            View divider = row.findViewById(R.id.check_row_divider);
            divider.setVisibility(i == players.size() - 1 ? View.GONE : View.VISIBLE);

            voteListContainer.addView(row);
        }
    }

    private void selectVote(int index) {
        selectedVoteIndex = index;
        for (int i = 0; i < voteListContainer.getChildCount(); i++) {
            MaterialCheckBox checkBox = voteListContainer.getChildAt(i).findViewById(R.id.check_row_checkbox);
            if (checkBox != null) {
                checkBox.setChecked(i == index);
            }
        }
        updateVoteContinueState();
    }

    private void updateVoteContinueState() {
        voteContinueButton.setEnabled(selectedVoteIndex >= 0);
    }

    private void openEditPlayerDialog(int index) {
        editingPlayerIndex = index;
        View content = getLayoutInflater().inflate(R.layout.dialog_edit_player, null, false);
        TextInputEditText input = content.findViewById(R.id.edit_player_input);
        input.setText(players.get(index));
        if (input.getText() != null) {
            input.setSelection(input.getText().length());
        }

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.edit_player_title)
                .setView(content)
                .setPositiveButton(R.string.save, null)
                .setNeutralButton(R.string.remove, null)
                .create();

        dialog.setOnShowListener(shownDialog -> {
            Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(view -> {
                String value = input.getText() == null ? "" : input.getText().toString().trim();
                if (!value.isEmpty() && editingPlayerIndex >= 0 && editingPlayerIndex < players.size()) {
                    players.set(editingPlayerIndex, value);
                    persistConfig();
                    renderPlayersList();
                    updateSetupSummary();
                }
                dialog.dismiss();
            });

            Button neutralButton = dialog.getButton(DialogInterface.BUTTON_NEUTRAL);
            neutralButton.setTextColor(ContextCompat.getColor(this, R.color.colorError));
            neutralButton.setVisibility(players.size() <= 3 ? View.GONE : View.VISIBLE);
            neutralButton.setOnClickListener(view -> {
                if (players.size() > 3 && editingPlayerIndex >= 0 && editingPlayerIndex < players.size()) {
                    players.remove(editingPlayerIndex);
                    persistConfig();
                    renderPlayersList();
                    updateSetupSummary();
                }
                dialog.dismiss();
            });

            input.requestFocus();
            Window window = dialog.getWindow();
            if (window != null) {
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        });

        dialog.setOnDismissListener(dismissedDialog -> editingPlayerIndex = -1);
        dialog.show();
    }

    private void addPlayer() {
        if (players.size() < 15) {
            players.add("Spieler " + (players.size() + 1));
            persistConfig();
            renderPlayersList();
            updateSetupSummary();
            openEditPlayerDialog(players.size() - 1);
        }
    }

    private void openCancelGameDialog() {
        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.cancel_game_title)
                .setMessage(R.string.cancel_game_message)
                .setPositiveButton(R.string.keep_playing, null)
                .setNeutralButton(R.string.leave_game, null)
                .create();

        dialog.setOnShowListener(shownDialog -> {
            Button neutralButton = dialog.getButton(DialogInterface.BUTTON_NEUTRAL);
            neutralButton.setTextColor(ContextCompat.getColor(this, R.color.colorError));
            neutralButton.setOnClickListener(view -> {
                dialog.dismiss();
                showScreen(setupScreen);
            });
        });

        dialog.show();
    }

    private void persistConfig() {
        GameStore.saveConfig(this, players, showHints, selectedCategories);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int mode = newConfig.uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (mode != uiMode) {
            uiMode = mode;
            recreate();
        } else if (cardScreen != null && cardScreen.getVisibility() == View.VISIBLE) {
            cardFront.post(() -> applyCardPosition(false));
        }
    }

    @Override
    public void onBackPressed() {
        if (setupScreen != null && setupScreen.getVisibility() == View.VISIBLE) {
            super.onBackPressed();
        } else if (voteScreen.getVisibility() == View.VISIBLE) {
            showScreen(discussionScreen);
        } else if (playersScreen.getVisibility() == View.VISIBLE
                || categoriesScreen.getVisibility() == View.VISIBLE
                || revealScreen.getVisibility() == View.VISIBLE) {
            showScreen(setupScreen);
        } else {
            openCancelGameDialog();
        }
    }
}