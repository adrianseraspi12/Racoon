package com.suzei.racoon.ui.preference;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Patterns;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.suzei.racoon.R;

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    private FirebaseUser mUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        initObj();
        bindPreferenceSummaryToValue(findPreference("email"));
        setClickListener();
    }

    private void initObj() {
        mUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void setClickListener() {
        Preference logOutPref = findPreference("logout");
        Preference sendFeedBackPref = findPreference("sendFeedback");
        Preference emailPref = findPreference("email");
        Preference passwordPref = findPreference("password");

        logOutPref.setOnPreferenceClickListener(this);
        sendFeedBackPref.setOnPreferenceClickListener(this);
        emailPref.setOnPreferenceClickListener(this);
        passwordPref.setOnPreferenceClickListener(this);
    }

    private void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(summaryToValueListener);

        summaryToValueListener.onPreferenceChange(
                preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    private Preference.OnPreferenceChangeListener summaryToValueListener = (preference,
                                                                                   newValue) -> {
        String stringValue = newValue.toString();

        if (preference.getKey().equals("email")) {
            // change email
            updateEmail(stringValue, preference);
        }

        return true;
    };

    private void updateEmail(String email, Preference preference) {
        if (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mUser.updateEmail(email).addOnCompleteListener(task -> preference.setSummary(email));
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();

        switch (key) {

            case "sendFeedback":
                Intent feedbackIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "adrianseraspi12@gmail.com", null));
                feedbackIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
                startActivity(Intent.createChooser(feedbackIntent, "Send Feedback:"));
                break;

            case "email":
                Intent emailIntent = new Intent(getActivity(), AccountActivity.class);
                emailIntent.putExtra(AccountActivity.CHANGE, AccountActivity.CHANGE_EMAIL);
                startActivity(emailIntent);
                break;

            case "password":
                Intent passwordIntent = new Intent(getActivity(), AccountActivity.class);
                passwordIntent.putExtra(AccountActivity.CHANGE, AccountActivity.RESET_PASSWORD);
                startActivity(passwordIntent);
                break;
        }

        return true;
    }
}
