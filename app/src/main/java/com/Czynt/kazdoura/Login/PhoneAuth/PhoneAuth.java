package com.Czynt.kazdoura.Login.PhoneAuth;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.Czynt.kazdoura.R;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.raycoarana.codeinputview.CodeInputView;

import java.util.Objects;


public class PhoneAuth extends Fragment implements View.OnClickListener {

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
    private CountDownTimer count;
    private TextView timer;
    private CodeInputView codeInput;
    private TextInputLayout phoneNumberLayout;
    private LottieAnimationView loader;
    private ConstraintLayout mainLayout;
    private TextWatcher textWatcher = new TextWatcher() {
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.phone_auth, container, false);

        phoneAuthViewModel = ViewModelProviders.of(this).get(PhoneAuthViewModel.class);

        phoneAuthViewModel.getRegistrationFlag().observe(getViewLifecycleOwner(), registrationSuccess -> {

            if (registrationSuccess) {

                updateUi(STATE_SIGNIN_SUCCESS);

            } else {

                updateUi(STATE_SIGNIN_FAILED);
            }

        });

        phoneAuthViewModel.getIsUpdating().observe(getViewLifecycleOwner(), isUpdating -> {
            if (isUpdating) {
                loader.setVisibility(View.VISIBLE);
                loader.playAnimation();

            } else {
                loader.cancelAnimation();
                loader.setVisibility(View.INVISIBLE);
            }
        });
        phoneAuthViewModel.getVerificationFlag().observe(getViewLifecycleOwner(), verificationSuccess -> {

            if (!verificationSuccess) {

                updateUi(STATE_VERIFY_FAILED);

            }

        });

        phoneAuthViewModel.getCodeSentFlag().observe(getViewLifecycleOwner(), codeSent -> {

            if (codeSent) {

                updateUi(STATE_CODE_SENT);

            }

        });

        initializeViews(v);

        return v;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    private void initializeViews(View v) {

        navController = Navigation.findNavController(Objects.requireNonNull(getActivity()), R.id.nav_host_fragment);

        phoneNumberLayout = v.findViewById(R.id.phoneNumberLayout);
        phoneNumberLayout.setStartIconTintList(null);
        mainLayout = v.findViewById(R.id.mainLayout);
        timer = v.findViewById(R.id.timer);
        codeInput = v.findViewById(R.id.code);
        enterThePin = v.findViewById(R.id.enterThePin);
        etPhoneNumber = v.findViewById(R.id.phoneNumber);
        verify = v.findViewById(R.id.verify);
        resend = v.findViewById(R.id.resendCode);
        TextView tvEdit = v.findViewById(R.id.tvEditNumber);

        etPhoneNumber.addTextChangedListener(textWatcher);


        loader = v.findViewById(R.id.loader);


        count = new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                timer.setText("" + millisUntilFinished / 1000);
            }

            public void onFinish() {
                timer.setText("0");
                resend.setEnabled(true);
            }

        };

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

            if (loader.getVisibility() == View.VISIBLE) {
                loader.cancelAnimation();
                loader.setVisibility(View.INVISIBLE);

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

                Snackbar snackbar = Snackbar.make(etPhoneNumber, phoneAuthViewModel.getException(), Snackbar.LENGTH_SHORT);
                View snackBarView = snackbar.getView();
                snackBarView.setBackgroundColor(getResources().getColor(R.color.smokyWhite));
                snackbar.setTextColor(getResources().getColor(R.color.textColor));
                snackbar.show();


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

                navController.navigate(R.id.action_global_Find);

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


}
