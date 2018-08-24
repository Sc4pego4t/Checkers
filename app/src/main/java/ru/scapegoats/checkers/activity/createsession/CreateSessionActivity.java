package ru.scapegoats.checkers.activity.createsession;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.textfield.TextInputEditText;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import androidx.annotation.Nullable;
import ru.scapegoats.checkers.R;
import ru.scapegoats.checkers.moduls.BaseActivity;
import ru.scapegoats.checkers.util.ApiFactory;
import ru.scapegoats.checkers.util.CreatingObservers;

public class CreateSessionActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createsession);
        RangeSeekBar seekBar=findViewById(R.id.seekbar);



        findViewById(R.id.create).setOnClickListener(e->{
            String title=((TextInputEditText)findViewById(R.id.et1)).getText().toString()
                    ,minRate=seekBar.getSelectedMinValue()+""
                    ,maxRate=seekBar.getSelectedMaxValue()+"";
            Log.e("gg",title+"/"+usertoken+"/"+minRate+"/"+maxRate);
            ApiFactory.createSession(title,usertoken+"",minRate,maxRate).subscribe(
                    CreatingObservers.createSession(this)
            );
        });

    }
}
