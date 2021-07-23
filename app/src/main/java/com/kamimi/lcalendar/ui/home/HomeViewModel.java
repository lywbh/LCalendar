package com.kamimi.lcalendar.ui.home;

import android.accounts.NetworkErrorException;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kamimi.lcalendar.Utils;

import org.json.JSONObject;

import java.util.Calendar;

import static com.kamimi.lcalendar.obj.GlobalConstants.BLANK_GAP;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> titleText;
    private final MutableLiveData<String> mText;
    private final MutableLiveData<String> hText;

    public HomeViewModel() {
        Calendar calendar = Calendar.getInstance();
        titleText = new MutableLiveData<>();
        titleText.setValue(Utils.monthToEn(calendar.get(Calendar.MONTH)) + "  " + calendar.get(Calendar.YEAR));
        mText = new MutableLiveData<>();
        hText = new MutableLiveData<>();

        Utils.submitTask(() -> {
            try {
                JSONObject holidayJson = Utils.httpGet("http://timor.tech/api/holiday/tts/");
                if (holidayJson.getInt("code") != 0) {
                    throw new NetworkErrorException("返回码不正确：" + holidayJson);
                }
                mText.postValue(BLANK_GAP + holidayJson.getString("tts"));
            } catch (Exception e) {
                Log.e("ERROR", "timor网络异常", e);
                mText.postValue("获取今天的数据出现了一些问题哟。");
            }
            try {
                JSONObject hitokotoJson = Utils.httpGet("https://v1.hitokoto.cn/");
                hText.postValue(BLANK_GAP + hitokotoJson.getString("hitokoto")
                        + "——" + hitokotoJson.getString("from"));
            } catch (Exception e) {
                Log.e("ERROR", "hitokoto网络异常", e);
                hText.postValue("请稍后再回来看看~");
            }
        });
    }

    public MutableLiveData<String> getTitleText() {
        return titleText;
    }

    public MutableLiveData<String> getMText() {
        return mText;
    }

    public MutableLiveData<String> getHText() {
        return hText;
    }

}