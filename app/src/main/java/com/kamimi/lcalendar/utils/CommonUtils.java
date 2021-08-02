package com.kamimi.lcalendar.utils;

import android.accounts.NetworkErrorException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lombok.SneakyThrows;

/**
 * 常用工具类
 */
public class CommonUtils {

    private static final ExecutorService ASYNC_POOL = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>(), r -> new Thread(r, "async-task-thread"));
    private static final ScheduledExecutorService DELAY_POOL = new ScheduledThreadPoolExecutor(4, r -> new Thread(r, "delay-task-thread"));
    private static final Map<Integer, String> monthNameMap = new HashMap<>();
    static {
        monthNameMap.put(Calendar.JANUARY, "January");
        monthNameMap.put(Calendar.FEBRUARY, "February");
        monthNameMap.put(Calendar.MARCH, "March");
        monthNameMap.put(Calendar.APRIL, "April");
        monthNameMap.put(Calendar.MAY, "May");
        monthNameMap.put(Calendar.JUNE, "June");
        monthNameMap.put(Calendar.JULY, "July");
        monthNameMap.put(Calendar.AUGUST, "August");
        monthNameMap.put(Calendar.SEPTEMBER, "September");
        monthNameMap.put(Calendar.OCTOBER, "October");
        monthNameMap.put(Calendar.NOVEMBER, "November");
        monthNameMap.put(Calendar.DECEMBER, "December");
    }

    /**
     * 提交异步任务
     */
    public static Future<?> submitTask(Runnable runnable) {
        return ASYNC_POOL.submit(runnable);
    }

    public static <T> Future<T> submitTask(Callable<T> callable) {
        return ASYNC_POOL.submit(callable);
    }

    /**
     * 提交延迟任务
     */
    public static ScheduledFuture<?> submitDelay(Runnable task, long delay, TimeUnit unit) {
        return DELAY_POOL.schedule(task, delay, unit);
    }

    /**
     * 提交延迟任务
     */
    public static <T> ScheduledFuture<T> submitDelay(Callable<T> task, long delay, TimeUnit unit) {
        return DELAY_POOL.schedule(task, delay, unit);
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
     * 注意，Calendar的月份枚举是0~11
     */
    public static String monthToEn(int month) {
        return monthNameMap.get(month);
    }

    /**
     * 格式化时间
     */
    public static String dateFormat(Date date, String format) {
        return new SimpleDateFormat(format, Locale.CHINA).format(date);
    }

    /**
     * 解析时间字符串
     */
    @SneakyThrows
    public static Date parseDate(String dateStr, String format) {
        return new SimpleDateFormat(format, Locale.CHINA).parse(dateStr);
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
