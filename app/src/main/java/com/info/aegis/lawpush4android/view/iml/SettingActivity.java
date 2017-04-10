package com.info.aegis.lawpush4android.view.iml;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.info.aegis.lawpush4android.R;

public class SettingActivity extends BaseActivity {
    SharedPreferences roboteSP;
    SharedPreferences.Editor roboteEditor;

    String eventArea = "";
    String eventDetail = "";
    String eventId = "";
    String gender = "";
    String height = "";
    String hometown = "";
    String id = "";
    String name = "";
    String position = "";
    String time = "";
    String weight = "";
    String industryId = "";
    int voiceLimit;

    EditText eventAreaEdit,
            eventDetailEdit,
            eventIdEdit,
            genderEdit,
            heightEdit,
            hometownEdit,
            idEdit,
            nameEdit,
            positionEdit,
            timeEdit, weightEdit,industryIdEdit,voiceLimitEdit;
    Button button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        roboteSP = getSharedPreferences("robbotSP", MODE_PRIVATE);
        roboteEditor = roboteSP.edit();

        eventArea = roboteSP.getString("eventArea", "");
        eventDetail = roboteSP.getString("eventDetail", "");
        eventId = roboteSP.getString("eventId", "");
        gender = roboteSP.getString("gender", "");
        height = roboteSP.getString("height", "");
        hometown = roboteSP.getString("hometown", "");
        id = roboteSP.getString("id", "");
        name = roboteSP.getString("name", "");
        position = roboteSP.getString("position", "");
        time = roboteSP.getString("time", "");
        weight = roboteSP.getString("weight", "");
        industryId = roboteSP.getString("industryId", "");
        voiceLimit = roboteSP.getInt("voiceLimit", 10);

        initViews();
        initData();
    }

    private void initViews() {
        eventAreaEdit = (EditText) findViewById(R.id.id_eventArea);
        eventDetailEdit = (EditText) findViewById(R.id.id_eventDetail);
        eventIdEdit = (EditText) findViewById(R.id.id_eventId);
        genderEdit = (EditText) findViewById(R.id.id_gender);
        heightEdit = (EditText) findViewById(R.id.id_height);
        hometownEdit = (EditText) findViewById(R.id.id_hometown);
        idEdit = (EditText) findViewById(R.id.id_id);
        nameEdit = (EditText) findViewById(R.id.id_name);
        positionEdit = (EditText) findViewById(R.id.id_position);
        timeEdit = (EditText) findViewById(R.id.id_time);
        weightEdit = (EditText) findViewById(R.id.id_weight);
        industryIdEdit = (EditText) findViewById(R.id.id_industryId);
        button = (Button) findViewById(R.id.id_save_btn);
        voiceLimitEdit = (EditText) findViewById(R.id.id_voiceLimit);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(SettingActivity.this).setTitle("确认修改？").setPositiveButton("确认",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        save();
                        finish();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                }).show();
            }
        });

    }

    void  initData(){
        eventAreaEdit.setText(eventArea);
        eventDetailEdit.setText(eventDetail);
        eventIdEdit.setText(eventId);
        genderEdit.setText(gender);
        heightEdit.setText(height);
        hometownEdit.setText(hometown);
        idEdit.setText(id);
        nameEdit.setText(name);
        positionEdit.setText(position);
        timeEdit.setText(time);
        weightEdit.setText(weight);
        industryIdEdit.setText(industryId);
        voiceLimitEdit.setText(voiceLimit +"");
    }

    void save(){
        roboteEditor.putString("eventArea",eventAreaEdit.getText().toString());
        roboteEditor.putString("eventDetail",eventDetailEdit.getText().toString());
        roboteEditor.putString("eventId",eventIdEdit.getText().toString());
        roboteEditor.putString("gender",genderEdit.getText().toString());
        roboteEditor.putString("height",heightEdit.getText().toString());
        roboteEditor.putString("hometown",hometownEdit.getText().toString());
        roboteEditor.putString("id",idEdit.getText().toString());
        roboteEditor.putString("name",nameEdit.getText().toString());
        roboteEditor.putString("position",positionEdit.getText().toString());
        roboteEditor.putString("time",timeEdit.getText().toString());
        roboteEditor.putString("weight",weightEdit.getText().toString());
        roboteEditor.putString("industryId",industryIdEdit.getText().toString());
        roboteEditor.putInt("voiceLimit",Integer.parseInt(voiceLimitEdit.getText().toString()));
        roboteEditor.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        button.callOnClick();
    }
}
