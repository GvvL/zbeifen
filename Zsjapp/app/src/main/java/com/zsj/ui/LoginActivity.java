package com.zsj.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.zsj.app.R;

import butterknife.BindView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;


public class LoginActivity extends AppCompatActivity{

    @BindView(R.id.bbb)
    BootstrapButton bb;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "aa@aa.com:aaaaa", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView userView;
    private EditText mPasswordView;
    private View mLoginFormView;

    private boolean goToNetWork() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info == null || !info.isAvailable()) {
//            new AlertDialog.Builder(this).setTitle("提醒：").setMessage("抱歉，目前无法连接网络。\n请检查您的手机网络连接！").setPositiveButton("打开网络设置", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    // TODO Auto-generated method stub
//                    Intent intent = null;
//                    //判断手机系统的版本  即API大于10 就是3.0或以上版本
//                    if (android.os.Build.VERSION.SDK_INT > 10) {
//                        intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
//                    } else {
//                        intent = new Intent();
//                        ComponentName component = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
//                        intent.setComponent(component);
//                        intent.setAction("android.intent.action.VIEW");
//                    }
//                    startActivity(intent);
//                }
//            }).setNegativeButton("知道了!", null).show();
            new SweetAlertDialog(LoginActivity.this,SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("网络异常")
                    .setContentText("请检查网络连接")
                    .setConfirmText("确定")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                            Intent intent = null;
                            //判断手机系统的版本  即API大于10 就是3.0或以上版本
                            if (android.os.Build.VERSION.SDK_INT > 10) {
                                intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                            } else {
                                intent = new Intent();
                                ComponentName component = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
                                intent.setComponent(component);
                                intent.setAction("android.intent.action.VIEW");
                            }
                            startActivity(intent);
                        }
                    })
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.cancel();
                        }
                    })
                    .show();

            return false;
        } else {
            //new AlertDialog.Builder(this).setMessage("网络正常可以使用").setPositiveButton("Ok", null).show();
            return true;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        goToNetWork();
        userView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);

    }
    @OnClick(R.id.bbb)
    void bbclick(){
        Toast.makeText(this,"--",Toast.LENGTH_SHORT).show();
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

    }

    private boolean mayRequestContacts() {

        return true;
    }

    /**
     * Callback received when a permissions request has been completed.
     */



    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        userView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = userView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            userView.setError(getString(R.string.error_field_required));
            focusView = userView;
            cancel = true;
        } else if (!isEmailValid(username)) {
            userView.setError(getString(R.string.error_invalid_email));
            focusView = userView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
//            showProgress(true);
//            mAuthTask = new UserLoginTask(email, password);
//            mAuthTask.execute((Void) null);
            toLogin(username,password);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.length()>3;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }


            return false;//登录失败
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            Log.i("---",success+"----");
            if (success) {
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }




    public void toLogin(String username, String password){
//        LoginService service = NetUtil.getInstance(this).createService(LoginService.class);
//        service.login(username, new Md5Util().getMD5ofStr(password))
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .subscribe(new ApiPsbCallBack<ResponseDataItem<String>>(LoginActivity.this) {
//                    @Override
//                    protected void success(ResponseDataItem<String> resultData) {
//                        Log.e("----------",resultData.getData()+"--");
//                        PreferencesUtil.put("token",resultData.getData());
//                        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
//                        LoginActivity.this.startActivity(intent);
//                        LoginActivity.this.finish();
//                    }
//
//                    @Override
//                    protected void error(int code, String str) {
//
//                    }
//                });
    }
}

