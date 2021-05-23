package com.abbad.smartonapp.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.anastr.speedviewlib.SpeedView;

public class DashboardViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public DashboardViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }
    public void onLoadSpeedMeters(SpeedView s1,SpeedView s2,SpeedView s3,SpeedView s4,
                                  SpeedView s5,SpeedView s6,SpeedView s7,SpeedView s8){
        s1.speedTo((float) 500.5);
        s2.speedTo((float) 380.6);
        s3.speedTo((float) 460.3);
        s4.speedTo((float) 720.4);
        s5.speedTo((float) 840.8);
        s6.speedTo((float) 460.2);
        s7.speedTo((float) 736.4);
        s8.speedTo((float) 374.9);
    }
    public LiveData<String> getText() {
        return mText;
    }
}