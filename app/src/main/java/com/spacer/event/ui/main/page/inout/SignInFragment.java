package com.spacer.event.ui.main.page.inout;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.spacer.event.R;
import com.spacer.event.listener.FireBaseSetDocumentResultListener;
import com.spacer.event.ui.main.MainActivity;
import com.spacer.event.ui.main.page.LoadingScreenDialog;
import com.spacer.event.ui.widget.fragmentnavigationcontroller.PresentStyle;
import com.spacer.event.ui.widget.fragmentnavigationcontroller.SupportFragment;
import com.spacer.event.util.PreferenceUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignInFragment extends SupportFragment implements DialogInterface.OnCancelListener {
    public static final String TAG = "SignInFragment";

    public static SignInFragment newInstance() {

        Bundle args = new Bundle();

        SignInFragment fragment = new SignInFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @BindView(R.id.root) View mRoot;

    @BindView(R.id.btn_sign_in)
    TextView btnSignIn;

    @BindView(R.id.btn_google) TextView btnGoogle;

    @BindView(R.id.btn_facebook) TextView btnFacebook;

    @BindView(R.id.edit_email)
    TextInputLayout mEditMail;

    @BindView(R.id.txt_email)
    TextInputEditText txtEmail;

    @BindView(R.id.edit_password) TextInputLayout mEditPassword;

    @BindView(R.id.txt_password) TextInputEditText txtPassword;
    @BindView(R.id.chb_remember)
    CheckBox mRememberCheckBox;

    @OnClick(R.id.forgot_password)
    void goToPasswordRecovery() {

    }

    @OnClick(R.id.panel)
    void clickPanel(View view){
        if(mEditPassword.getEditText()!=null)
        mEditPassword.getEditText().clearFocus();

        if(mEditMail.getEditText()!=null)
        mEditMail.getEditText().clearFocus();
    }

    private boolean validateAccount(String email, String password){

        mEditMail.setError(null);
        mEditPassword.setError(null);

        if(email.isEmpty()){
            mEditMail.setError(getString(R.string.email_empty));
            return false;
        }

        if(!isValidEmail(email)){
            mEditMail.setError(getString(R.string.email_invalid));
            return false;
        }

        if(password.isEmpty()){
            mEditPassword.setError(getString(R.string.password_empty));
           return false;
        }

        if(password.length()<6){
            mEditPassword.setError(getString(R.string.password_length));
            return false;
        }

        return true;
    }

    public boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }


    @OnClick({R.id.root,R.id.close})
    void back() {
        getNavigationController().dismissFragment();
    }



    /*@OnClick(R.id.back)
    void back() {
        getMainActivity().dismiss();
    }*/


    @Nullable
    @Override
    protected View onCreateView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.sign_in,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        autoFilled();
    }


    private void autoFilled() {
        if(PreferenceUtil.getInstance().isRememberAccount())
        {
            String email = PreferenceUtil.getInstance().getSavedAccount();
            if(email!=null&&!email.isEmpty()&&mEditMail.getEditText()!=null) {
                mEditMail.getEditText().setText(email);

                if(mEditPassword.getEditText()!=null)
                mEditPassword.getEditText().requestFocus();
            }

            mRememberCheckBox.setChecked(true);
        }
    }

    @OnClick(R.id.btn_google)
    void sigInWithGoogle() {

    }

    @OnClick(R.id.btn_facebook)
    void signInWithFaceBook() {
        // l??c nh???n n??t faceb??k th?? n?? v?? ????y n??
        // ??ng c??? l??m gi???ng nh?? h???i ????? ??n ??

        showLoading(); // n?? s??? show ra c??i loading xoay tr??m
        // khi pit ket qua r thi
        successDismissLoading(null);
        // h??nh nh?? k???t qu??? n?? tr??? v??? ??? onActivityResult ????ng hong
    }
    LoadingScreenDialog mLoadingDialog = null;

    private void showLoading() {

        mLoadingDialog = LoadingScreenDialog.newInstance(getContext());
        mLoadingDialog.show(getChildFragmentManager(),"LoadingScreenDialog");
    }

    private void successDismissLoading(FirebaseUser user) {
        Log.d(TAG, "successDismissLoading: current " + FirebaseAuth.getInstance().getCurrentUser());
        Log.d(TAG, "successDismissLoading: parameter "+user);

        // c??i l??c n??y l?? l??c m?? k???t qu??? tr??? v??? th??nh c??ng ??

        // ph???n d?????i n??y l?? gi???ng nhau gi???a 2 c??i sign in v?? signup
        // b??? c??i loading ??i
        // ch??? 1250 s th?? ????ng fragment n??y l???i, v?? th??ng b??o cho activity l?? ???? ????ng nh???p
        mLoadingDialog.showSuccessThenDismiss("Hi there, welcome back!");
        btnSignIn.postDelayed(() -> {

            if(getActivity() instanceof MainActivity)
            ((MainActivity)getActivity()).justSignIn(user);
            getNavigationController().dismissFragment();
        }, 1250);

    }

    private void failureDismissLoading(String error) {
        if(mLoadingDialog!=null) {
            mLoadingDialog.showFailureThenDismiss(error);
            mLoadingDialog = null;
        }
    }

    @OnClick(R.id.btn_sign_in)
    void signInWithForm() {
        String email="",password="";

        EditText editText = mEditMail.getEditText();
        if(editText!=null) email = editText.getText().toString();

        editText = mEditPassword.getEditText();
        if(editText!=null) password = editText.getText().toString();

        PreferenceUtil.getInstance().setRememberAccount(mRememberCheckBox.isChecked());
        PreferenceUtil.getInstance().setSavedAccount(email);

        if(validateAccount(email,password)) {
            // ????y n??
            // l??c ngta nh???n n??t
            // v?? form h???p l???
            showLoading(); // c??i n??y show ra 1 c??i loading (n?? che m??n hinh ??i)
            FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(email,password)
                    .addOnSuccessListener(mSignInWithFormListener) // c??i listener
                    .addOnFailureListener(mSignInWithFormListener);
        }

    }

    @Override
    public int defaultTransition() {
        return PresentStyle.ROTATE_DOWN_LEFT;
    }

    @Override
    public int defaultOpenExitTransition() {
        return PresentStyle.SLIDE_LEFT;
    }

    private FireBaseSetDocumentResultListener mSignInWithFormListener = new FireBaseSetDocumentResultListener() {
        @Override
        public void onSuccess(AuthResult authResult) {
            successDismissLoading(authResult.getUser());
        }

        @Override
        public void onFailure(@NonNull Exception e) {
            failureDismissLoading(e.getMessage().toString());
        }
    };

    @Override
    public void onCancel(DialogInterface dialog) {

    }

    // vi du cai signup cx tuong tu cai sign in nay
}
