package com.lihe.smstransmit;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private final int CONTINUE = 3;//继续输入
    private final int CHANGE = 0;//修改号码
    private final int SAVE = 1;//保存号码
    private final int INPUT = 2;//输入号码
    private Button button;
    private EditText number;
    private Switch email_switch;
    private Switch title_switch;
    private boolean flag;
    private int state;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.button);
        number = (EditText) findViewById(R.id.number);
        email_switch = (Switch) findViewById(R.id.email_switch);
        title_switch = (Switch) findViewById(R.id.title_switch);
        flag = getSettingNote(this,"number").equals("");//判断是否为第一次进入软件

        if(flag){
            state = INPUT;
            buttonState(state);
        }else {
            state = CHANGE;
            buttonState(state);
        }

        number.setText(getSettingNote(this,"number"));//显示已经保存了的号码
        number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int count = s.length();
                Log.i("noco",count+"");
                if(count > 0 && count <11){
                    state = CONTINUE;
                    buttonState(state);
                }else if (count == 11){
                    state = SAVE;
                    buttonState(state);
                }else {
                    button.setEnabled(false);
                }
                if (getSettingNote(MainActivity.this,"number").equals(s.toString())){
                    state = CHANGE;
                    buttonState(state);
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numberStr = number.getText().toString();
                if(numberStr.length() == 11){
                    if (getSettingNote(MainActivity.this,"number").equals(numberStr)){
                        number.setText("");
                        state = INPUT;
                        buttonState(state);
                    }else {
                        saveSettingNote(MainActivity.this,"number",numberStr);
                        state = CHANGE;
                        buttonState(state);
                        Toast.makeText(MainActivity.this,"保存号码成功！", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(MainActivity.this,"输入号码有误，请重新输入！",Toast.LENGTH_SHORT).show();
                }
            }
        });


        if (getSettingNote(MainActivity.this, "email").equals("always")) {
            email_switch.setChecked(true);
        } else {
            email_switch.setChecked(false);
        }
        email_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    saveSettingNote(MainActivity.this, "email", "always");
                } else {
                    saveSettingNote(MainActivity.this, "email", "none");
                }
            }
        });

        if (getSettingNote(MainActivity.this, "title").equals("always")) {
            title_switch.setChecked(true);
        } else {
            title_switch.setChecked(false);
        }
        title_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    saveSettingNote(MainActivity.this, "title", "always");
                } else {
                    saveSettingNote(MainActivity.this, "title", "none");
                }
            }
        });
    }

    private void buttonState(int state){
        switch (state){
            case INPUT:
                number.setText("");
                button.setText("输入号码");
                button.setEnabled(false);
                break;
            case SAVE:
                button.setText("保存号码");
                button.setEnabled(true);
                break;
            case CHANGE:
                button.setText("修改号码");
                button.setEnabled(true);
                break;
            case CONTINUE:
                button.setText("继续输入");
                button.setEnabled(false);
                break;
        }
    }

    public static void saveSettingNote(Context context, String key, String saveData){//保存设置
        SharedPreferences.Editor note = context.getSharedPreferences("number_save", Activity.MODE_PRIVATE).edit();
        note.putString(key, saveData);
        note.commit();
    }
    public static String getSettingNote(Context context,String key){//获取保存设置
        SharedPreferences read = context.getSharedPreferences("number_save", Activity.MODE_PRIVATE);
        return read.getString(key, "");
    }
}
