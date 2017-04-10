package aegis.aegisxunfei;

import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @type Log
 * @copyright 版权：版权所有 2013-2014 jksfood
 * @description 描述： Log 日志工具类 用于产生相关日志工作
 * @creator 创建人：jksfood
 * @creatime 创建时间：2015-6-3
 *
 * @modification_record
 *
 */
public class MyLog2 {
    static final boolean SHOW_LOG = false;
    static final boolean SHOW_LOG_SDCARD = true;

    static String LOG_PATH_SDCARD_DIR = "//qingdun//";

    /** INFO 常规 信息输出 
     * @param tag
     * @param string
     */
    public static void i(String tag, String string) {
        if (SHOW_LOG) android.util.Log.i(tag, string);
        if (SHOW_LOG_SDCARD)  appendLog("i",tag,string);
    }

    /** ERROR 错误信息 输出
     * @param tag
     * @param string
     */
    public static void e(String tag, String string) {
        if (SHOW_LOG) android.util.Log.e(tag, string);
        if (SHOW_LOG_SDCARD)  appendLog("e",tag,string);
    }
    public static void e(String tag, String string, Throwable tr) {
        if (SHOW_LOG) android.util.Log.e(tag, string, tr);
        if (SHOW_LOG_SDCARD)  appendLog("e",tag,string);
    }

    /** DEBUG 调试状态 输出
     * @param tag
     * @param string
     */
    public static void d(String tag, String string) {
        if (SHOW_LOG) android.util.Log.d(tag, string);
        if (SHOW_LOG_SDCARD)  appendLog("d",tag,string);
    }

    /** WARING 警告状态 输出
     * @param tag
     * @param string
     */
    public static void w(String tag, String string) {
        if (SHOW_LOG) android.util.Log.w(tag, string);
        if (SHOW_LOG_SDCARD)  appendLog("w",tag,string);
    }
    public static void w(String tag, String string, Throwable tr) {
        if (SHOW_LOG) android.util.Log.w(tag, string, tr);
        if (SHOW_LOG_SDCARD)  appendLog("w",tag,string);
    }


    /** Verbose 详细的 冗余的 输出
     * @param tag
     * @param string
     */
    public static void v(String tag, String string) {
        if (SHOW_LOG) android.util.Log.v(tag, string);
        if (SHOW_LOG_SDCARD)  appendLog("v",tag,string);
    }
    public static void v(String tag, String string, Throwable tr) {
        if (SHOW_LOG) android.util.Log.v(tag, string, tr);
        if (SHOW_LOG_SDCARD)  appendLog("v",tag,string);
    }

    /** 写入SD 卡
     * @param tag
     * @param text
     */
    public static void appendLog(String logtype,String tag,String text) {

        // 新建或打开日志文件   
        SimpleDateFormat logfile = new SimpleDateFormat("yyyy-MM-dd");
        String LOGFILEName = "LawPush4Android.txt";
        SimpleDateFormat LogSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 日志的输出格式
        Date nowTime = new Date();
        String needWriteFile = logfile.format(nowTime);
        String needWriteMessage = LogSdf.format(nowTime) + "    " + logtype + "    " + tag + "    " + text;
        android.util.Log.v("SD 卡", Environment.getExternalStorageDirectory().getPath());
        String log_path = Environment.getExternalStorageDirectory().getPath() + LOG_PATH_SDCARD_DIR+needWriteFile + LOGFILEName;
        File file = new File(log_path);
        if(!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        try{
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FileWriter fileWriter = new FileWriter(file, true);
            BufferedWriter buf = new BufferedWriter(fileWriter);
            buf.append(needWriteMessage);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

