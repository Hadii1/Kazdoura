package com.Czynt.kazdoura.Login.PhoneAuth;

import android.animation.Animator;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.Czynt.kazdoura.R;
import com.Czynt.kazdoura.di.ViewModels.ViewModelProviderFactory;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.raycoarana.codeinputview.CodeInputView;

import java.util.Objects;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;


public class PhoneAuth extends DaggerFragment implements View.OnClickListener {


    private final static int STATE_SIGNIN_SUCCESS = 2;
    private final static int STATE_VERIFY_FAILED = 3;
    private final static int STATE_CODE_SENT = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final String TAG = "PhoneAuthClass";
    private EditText etPhoneNumber;
    private PhoneAuthViewModel phoneAuthViewModel;
    private Button verify, resend;
    private TextView enterThePin;
    private NavController navController;
    private ConstraintLayout contentLayout;
    private CountDownTimer count;
    private TextView timer;
    private CodeInputView codeInput;
    private TextInputLayout phoneNumberLayout;
    private LottieAnimationView loaderAnim, successAnim;
    private final TextWatcher textWatcher = new TextWatcher() {


        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if (etPhoneNumber.getText().toString().length() == 8) {
                verify.setEnabled(true);

            } else {
                verify.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    public PhoneAuth() {
    }


    @Inject
    ViewModelProviderFactory providerFactory;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.phone_auth, container, false);
        Log.d(TAG, "onCreateView: ");


        assert Objects.requireNonNull(getActivity()).getApplication() != null;

        phoneAuthViewModel = ViewModelProviders.of(this, providerFactory).get(PhoneAuthViewModel.class);

        subscribeListeners();

        initViews(v);

        return v;
    }

    private void subscribeListeners() {
        phoneAuthViewModel.getRegistrationFlag().observe(getViewLifecycleOwner(), registrationSuccess -> {

            if (registrationSuccess) {

                updateUi(STATE_SIGNIN_SUCCESS);

            } else {

                updateUi(STATE_SIGNIN_FAILED);
            }

        });

        phoneAuthViewModel.getIsUpdating().observe(getViewLifecycleOwner(), isUpdating -> {
            if (isUpdating) {
                loaderAnim.setVisibility(View.VISIBLE);
                loaderAnim.playAnimation();

            } else {
                loaderAnim.cancelAnimation();
                loaderAnim.setVisibility(View.INVISIBLE);
            }
        });

        phoneAuthViewModel.getVerificationFlag().observe(getViewLifecycleOwner(), verificationFailed -> {

            if (verificationFailed) {

                updateUi(STATE_VERIFY_FAILED);

            }

        });

        phoneAuthViewModel.getCodeSentFlag().observe(getViewLifecycleOwner(), codeSent -> {

            if (codeSent) {

                updateUi(STATE_CODE_SENT);

            }

        });
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);


    }


    private void initViews(View v) {

        navController = Navigation.findNavController(Objects.requireNonNull(getActivity()), R.id.nav_host_fragment);

        phoneNumberLayout = v.findViewById(R.id.phoneNumberLayout);
        phoneNumberLayout.setStartIconTintList(null);
        timer = v.findViewById(R.id.timer);
        codeInput = v.findViewById(R.id.code);
        enterThePin = v.findViewById(R.id.enterThePin);
        etPhoneNumber = v.findViewById(R.id.phoneNumber);
        verify = v.findViewById(R.id.verify);
        resend = v.findViewById(R.id.resendCode);
        TextView tvEdit = v.findViewById(R.id.tvEditNumber);

        etPhoneNumber.addTextChangedListener(textWatcher);

        contentLayout = v.findViewById(R.id.phoneAuthContentLayout);

        loaderAnim = v.findViewById(R.id.loader);
        successAnim = v.findViewById(R.id.successAnim);


        count = new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                timer.setText("" + millisUntilFinished / 1000);
            }

            public void onFinish() {
                timer.setText("0");
                resend.setEnabled(true);
            }

        };

        successAnim.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                navController.navigate(R.id.action_global_Find);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });


        verify.setOnClickListener(this);
        resend.setOnClickListener(this);
        tvEdit.setOnClickListener(this);

        codeInput.addOnCompleteListener(code -> {
            InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(codeInput.getWindowToken(), 0);

            phoneAuthViewModel.signInWithCredential(code);

        });


    }

    public void onClick(View v) {

        if (v.getId() == R.id.verify) {

            phoneAuthViewModel.verifyNumber(etPhoneNumber.getText().toString());

            phoneNumberLayout.setHint("Mobile");
            phoneNumberLayout.setEnabled(false);

            verify.setEnabled(false);
        }

        if (v.getId() == R.id.resendCode) {

            phoneAuthViewModel.resendCode(etPhoneNumber.getText().toString());
            codeInput.clearError();
            codeInput.setCode("");
            codeInput.setEditable(true);
            resend.setEnabled(false);

        }
        if (v.getId() == R.id.tvEditNumber) {

            if (loaderAnim.getVisibility() == View.VISIBLE) {
                loaderAnim.cancelAnimation();
                loaderAnim.setVisibility(View.INVISIBLE);

            }

            phoneNumberLayout.setEnabled(true);

            etPhoneNumber.setEnabled(true);

            verify.setEnabled(true);

            count.cancel();
            timer.setText("");

            codeInput.clearError();
            codeInput.setCode("");
            codeInput.setVisibility(View.INVISIBLE);

            resend.setVisibility(View.INVISIBLE);
            resend.setEnabled(false);

            enterThePin.setVisibility(View.INVISIBLE);

            phoneNumberLayout.requestFocus();

        }

    }


    private void updateUi(int STATE) {
        switch (STATE) {

            case STATE_VERIFY_FAILED:

                Snackbar.make(etPhoneNumber, phoneAuthViewModel.getException(), Snackbar.LENGTH_SHORT).show();


                if (phoneAuthViewModel.getException().equals("Invalid Phone Number")) {

                    phoneNumberLayout.setError("Invalid Number");
                    etPhoneNumber.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.shake));

                }

                phoneNumberLayout.setEnabled(true);
                verify.setEnabled(true);

                break;

            case STATE_CODE_SENT:

                enterThePin.setVisibility(View.VISIBLE);
                enterThePin.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.unrepeated_fade_in));

                codeInput.setVisibility(View.VISIBLE);
                codeInput.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.unrepeated_fade_in));

                timer.setVisibility(View.VISIBLE);
                timer.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.unrepeated_fade_in));

                resend.setVisibility(View.VISIBLE);
                resend.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.unrepeated_fade_in));

                count.start();

                break;

            case STATE_SIGNIN_SUCCESS:

                contentLayout.setAlpha(0.05f);
                successAnim.setVisibility(View.VISIBLE);
                successAnim.playAnimation();

                break;


            case STATE_SIGNIN_FAILED:


                if (phoneAuthViewModel.getException().equals("Invalid Code")) {

                    codeInput.setError("Invalid Code");

                }

                codeInput.setCode("");
                codeInput.setEditable(true);
                codeInput.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.shake));
                codeInput.clearFocus();

                break;


        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        count.onFinish();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");

    }
}
