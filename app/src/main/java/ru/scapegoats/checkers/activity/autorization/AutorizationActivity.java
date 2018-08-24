package ru.scapegoats.checkers.activity.autorization;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import androidx.annotation.Nullable;
import ru.scapegoats.checkers.R;
import ru.scapegoats.checkers.activity.registration.RegistrationActivity;
import ru.scapegoats.checkers.moduls.BaseActivity;
import ru.scapegoats.checkers.util.ApiFactory;
import ru.scapegoats.checkers.util.CreatingObservers;

public class AutorizationActivity extends BaseActivity {
    TextInputEditText et1,et2;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autorization);
        et1=findViewById(R.id.et1);
        et2=findViewById(R.id.et2);
        findViewById(R.id.reg).setOnClickListener(e->{
            this.startActivity(new Intent(this,RegistrationActivity.class));
        });
        findViewById(R.id.accept).setOnClickListener(e->{
            String password=et2.getText().toString()
                    ,nick=et1.getText().toString();
            ApiFactory.autorize(password,nick).subscribe(
                    CreatingObservers.getToken(this));
        });
    }
}
