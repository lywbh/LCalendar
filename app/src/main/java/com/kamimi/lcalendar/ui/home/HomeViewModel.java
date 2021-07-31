package com.kamimi.lcalendar.ui.home;

import android.accounts.NetworkErrorException;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kamimi.lcalendar.utils.CommonUtils;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Locale;

import static com.kamimi.lcalendar.obj.GlobalConstants.BLANK_GAP;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> titleText;
    private final MutableLiveData<String> mainText;
    private final MutableLiveData<String> hintText;

    public HomeViewModel() {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        titleText = new MutableLiveData<>();
        titleText.setValue(CommonUtils.monthToEn(calendar.get(Calendar.MONTH)) + "  " + calendar.get(Calendar.YEAR));
        mainText = new MutableLiveData<>();
        hintText = new MutableLiveData<>();
        CommonUtils.submitTask(() -> {
            try {
                JSONObject holidayJson = CommonUtils.httpGet("http://timor.tech/api/holiday/tts/");
                if (holidayJson.getInt("code") != 0) {
                    throw new NetworkErrorException("返回码不正确：" + holidayJson);
                }
                mainText.postValue(BLANK_GAP + holidayJson.getString("tts"));
            } catch (Exception e) {
                Log.e("ERROR", "timor网络异常", e);
                mainText.postValue("获取今天的数据出现了一些问题哟。");
            }
        });
        CommonUtils.submitTask(() -> {
            try {
                JSONObject hitokotoJson = CommonUtils.httpGet("https://v1.hitokoto.cn/");
                hintText.postValue(BLANK_GAP + hitokotoJson.getString("hitokoto")
                        + "——" + hitokotoJson.getString("from"));
            } catch (Exception e) {
                Log.e("ERROR", "hitokoto网络异常", e);
                hintText.postValue("请稍后再回来看看~");
            }
        });
    }

    public MutableLiveData<String> getTitleText() {
        return titleText;
    }

    public MutableLiveData<String> getMainText() {
        return mainText;
    }

    public MutableLiveData<String> getHintText() {
        return hintText;
    }

}