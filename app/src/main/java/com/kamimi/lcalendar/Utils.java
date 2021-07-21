package com.kamimi.lcalendar;

import android.accounts.NetworkErrorException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Utils {

    private static final ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

    /**
     * 提交异步任务
     */
    public static Future<?> submitTask(Runnable runnable) {
        return cachedThreadPool.submit(runnable);
    }

    public static <T> Future<T> submitTask(Callable<T> callable) {
        return cachedThreadPool.submit(callable);
    }

    /**
     * 提交延迟任务
     */
    public static Timer submitDelay(TimerTask task, long millis) {
        Timer timer = new Timer();
        timer.schedule(task, millis);
        return timer;
    }

    /**
     * 随机整数
     */
    public static int randomInt(int from, int to) {
        int gap = to - from;
        if (gap <= 0) {
            throw new IllegalArgumentException("非法入参:" + from + "," + to);
        }
        return new Random().nextInt(gap) + from;
    }

    /**
     * 随机整数
     */
    public static int randomInt(long seed, int from, int to) {
        int gap = to - from;
        if (gap <= 0) {
            throw new IllegalArgumentException("非法入参:" + from + "," + to);
        }
        return new Random(seed).nextInt(gap) + from;
    }

    /**
     * 随机浮点数
     */
    public static float randomFloat(float from, float to) {
        float gap = to - from;
        if (gap <= 0) {
            throw new IllegalArgumentException("非法入参:" + from + "," + to);
        }
        return new Random().nextFloat() * gap + from;
    }

    /**
     * 随机浮点数
     */
    public static float randomFloat(long seed, float from, float to) {
        float gap = to - from;
        if (gap <= 0) {
            throw new IllegalArgumentException("非法入参:" + from + "," + to);
        }
        return new Random(seed).nextFloat() * gap + from;
    }

    /**
     * 随机
     */
    public static <T> T randomFrom(T[] arr) {
        return arr[new Random().nextInt(arr.length)];
    }

    /**
     * 月份转英文名称
     */
    public static String monthToEn(int i) {
        switch (i) {
            case 1: return "January";
            case 2: return "February";
            case 3: return "March";
            case 4: return "April";
            case 5: return "May";
            case 6: return "June";
            case 7: return "July";
            case 8: return "August";
            case 9: return "September";
            case 10: return "October";
            case 11: return "November";
            case 12: return "December";
            default: throw new IllegalArgumentException("Invalid month number:" + i);
        }
    }

    /**
     * 格式化时间
     */
    public static String dateFormat(Date date, String format) {
        return new SimpleDateFormat(format, Locale.CHINESE).format(date);
    }

    /**
     * HTTP请求
     */
    public static JSONObject httpGet(String url) throws IOException, NetworkErrorException, JSONException {
        URL fetchUrl = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) fetchUrl.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(2000);
        conn.connect();
        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new NetworkErrorException(String.valueOf(conn.getResponseCode()));
        }
        String responseStr = inToString(conn.getInputStream());
        return new JSONObject(responseStr);
    }

    private static String inToString(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buff = new byte[1024];
        int len;
        while ((len = in.read(buff)) != -1) {
            out.write(buff, 0, len);
        }
        in.close();
        String mes = out.toString();
        out.close();
        return mes;
    }

}
