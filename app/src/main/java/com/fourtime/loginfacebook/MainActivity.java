package com.fourtime.loginfacebook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private CallbackManager callbackManager;

    ImageView iv_foto;
    TextView tv_nome;
    TextView tv_email;
    TextView tv_date;
    EditText ed_email;
    EditText ed_senha;
    LoginButton bt;
    URL profili_picture;

    String nome,email,data_nascimento,id;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt = (LoginButton) findViewById(R.id.bt_login);
        iv_foto = (ImageView) findViewById(R.id.iv_foto);
        tv_nome = (TextView) findViewById(R.id.tv_name);
        tv_email = (TextView) findViewById(R.id.tv_email);
        tv_date = (TextView) findViewById(R.id.tv_date);
        ed_email = (EditText) findViewById(R.id.ed_email);
        ed_senha = (EditText) findViewById(R.id.ed_senha);

        callbackManager = CallbackManager.Factory.create();

        bt.setReadPermissions("email","public_profile","user_birthday");

        bt.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String userId = loginResult.getAccessToken().getUserId();

                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        displayUserInfo(object);
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields","first_name, last_name, email, id, birthday");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    public void displayUserInfo(JSONObject object){
        try {
            nome = object.getString("first_name") +" "+ object.getString("last_name");
            email = object.getString("email");
            data_nascimento = object.getString("birthday");
            id = object.getString("id");
            try {
                profili_picture = new URL("https://graph.facebook.com/"+object.getString("id")+"/picture?width=250&height=250");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        tv_nome.setText("Nome: "+nome);
        tv_date.setText("Data de nascimento: "+data_nascimento);
        tv_email.setText("Email: "+email);
        Glide.with(this).load(profili_picture).into(iv_foto);
    }

}
