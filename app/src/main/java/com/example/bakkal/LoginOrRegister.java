package com.example.bakkal;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.Objects;
import java.util.concurrent.Executor;

public class LoginOrRegister extends AppCompatActivity {

    LinearLayout panelRegister, panelLogin, panelForgotPassword, newPassword;
    String request = "", userEmail = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_or_register);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        panelRegister = findViewById(R.id.panelRegister);
        panelLogin = findViewById(R.id.panelLogin);
        panelForgotPassword = findViewById(R.id.panelForgotPassword);
        newPassword = findViewById(R.id.panelNewPassword);

        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getString("request", "") != null) {
                request = getIntent().getExtras().getString("request", "");
            }
        }

        switch (request) {
            case "login":
                iWannaTry(null);
                break;
            case "register":
                notAlreadyMember(null);
                break;
            case "forgot_password":
                forgotPassword(null);
                break;
            default:
                iWannaTry(null);
        }
    }

    public void alreadyMember(View view) {
        panelForgotPassword.setVisibility(View.INVISIBLE);
        panelLogin.setVisibility(View.VISIBLE);
        panelRegister.setVisibility(View.INVISIBLE);
        newPassword.setVisibility(View.INVISIBLE);
    }

    public void forgotPassword(View view) {
        panelForgotPassword.setVisibility(View.VISIBLE);
        panelLogin.setVisibility(View.INVISIBLE);
        panelRegister.setVisibility(View.INVISIBLE);
        newPassword.setVisibility(View.INVISIBLE);
    }

    public void register(View view) {
        Functions.WebResult result = MainMenu.user.register(getValue(R.id.etRegisterName), getValue(R.id.etRegisterSurName), getValue(R.id.etRegisterEmail), getValue(R.id.etRegisterPassword), getValue(R.id.etRegisterPasswordRepeat), getValue(R.id.etRegisterAddress), getValue(R.id.etRegisterPhone));

        if (result.isConnected()) {
            if (result.isSuccess()) {
                Functions.message(this, "", result.getData(), false, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alreadyMember(null);
                    }
                });
            } else {
                Functions.message(this, "", result.getData(), true);
            }
        } else {
            Functions.message(this, "", getString(R.string.connection_error), true);
        }
    }

    public void login(View view) {
        String email = ((EditText) findViewById(R.id.etLoginEmail)).getText().toString();
        String password = ((EditText) findViewById(R.id.etLoginPassword)).getText().toString();

        if (email != null && !email.equals("") && password != null && !password.equals("")) {
            Functions.WebResult result = MainMenu.user.login(email, password);

            if (result.isSuccess()) {
                //todo
                Functions.message(this, "", result.getData(), false, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        //        MainActivity.loadUser();
                    }
                });
            } else {
                Functions.message(this, "", result.getData(), true);
            }

        } else {
            Functions.message(this, "", getResources().getString(R.string.null_email_or_password), true);
        }
    }

    public void notAlreadyMember(View view) {
        panelForgotPassword.setVisibility(View.INVISIBLE);
        panelLogin.setVisibility(View.INVISIBLE);
        panelRegister.setVisibility(View.VISIBLE);
        newPassword.setVisibility(View.INVISIBLE);
    }

    public void iWannaTry(View view) {
        alreadyMember(null);
    }

    public void sendForgotMail(View view) {
        final EditText email = findViewById(R.id.etForgotPasswordEmail);

        if (email != null && email.getText() != null && email.getText().toString().equals("") == false) {
            Functions.WebResult result = API.User.getPIN(email.getText().toString());

            if (result.isConnected() && result.isSuccess()) {
                Functions.message(this, "", result.getData(), false, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        panelForgotPassword.setVisibility(View.INVISIBLE);
                        newPassword.setVisibility(View.VISIBLE);

                        userEmail = email.getText().toString();
                    }
                });
            } else {
                Functions.message(this, "", result.getData(), true);
            }
        } else {
            Functions.message(this, "", getString(R.string.null_email), true);
        }
    }

    public String getValue(int id) {
        try {
            EditText et = ((EditText) findViewById(id));
            if (et.getText() == null)
                return "";

            return et.getText().toString();
        } catch (Exception e) {
            return "";
        }
    }

    public void sendNewPassword(View view) {
        if (userEmail.equals("")) {

        }

        EditText etPIN = findViewById(R.id.etPIN);
        EditText etPassword = findViewById(R.id.etRegisterForgotPassword);
        EditText etPasswordRepeat = findViewById(R.id.etRegisterForgotPasswordRepeat);

        if (etPIN == null || etPIN.getText() == null) {
            Functions.message(this, "", getString(R.string.null_pin), true);
            return;
        }


        if (etPassword == null || etPassword.getText() == null) {
            Functions.message(this, "", getString(R.string.null_password), true);
            return;
        }


        if (etPasswordRepeat == null || etPasswordRepeat.getText() == null) {
            Functions.message(this, "", getString(R.string.null_password_repeat), true);
            return;
        }

        String password = etPassword.getText().toString();

        if (password.equals(etPasswordRepeat.getText().toString()) == false) {
            Functions.message(this, "", getString(R.string.password_match), true);
            return;
        }

        String pin = etPIN.getText().toString();

        Functions.WebResult result = API.User.newPassword(userEmail, pin, password);

        if (result.isConnected() && result.isSuccess()) {
            Functions.message(this, "", result.getData(), false, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alreadyMember(null);
                }
            });
        } else {
            Functions.message(this, "", result.getData(), true);
        }
    }
}
