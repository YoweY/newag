package aegis.aegisxunfei;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

/**
 * 作者：jksfood on 2017/1/12 15:13
 */

public class MyConstant {

    /**
     * 随机返回 机器人动作 问候语
     * @return
     */
    public static String randomRobbotAction(){
        String [] stringArray = {"OK","好的"};
        Random random = new Random();
        int m = random.nextInt(stringArray.length);
        return stringArray[m];
    }

    /**
     * 随机主动打招呼
     * @return
     */
    public static String randomSayHello(){
        String [] stringArray = {"嗨，您好，请问有什么可以帮助您的吗？"};
        Random random = new Random();
        int m = random.nextInt(stringArray.length);
        return stringArray[m];
    }

    /**
     * 将毫秒转换为 00:00:00时间格式
     *
     * @return
     */
    public static String formatMillTool(long duration) {
        String time = null;
        if (duration != 0) {
//            duration /= 1000;
            if (duration < 60) {
//                time = "00:00:" + transform(duration);
                time = "00:" + transform(duration);
            } else if (duration >= 60 && duration < 60 * 60) {
                long min = duration / 60;
//                time = "00:" + transform(min) + ":" + transform(duration % 60);
                time =  transform(min) + ":" + transform(duration % 60);
            } else {
                long hour = duration / 60 / 60;
                long min = 0;
                long sec = 0;
                if (duration - 3600 * hour >= 60) {
                    min = (duration - 3600 * hour) / 60;
                    sec = (duration - 3600 * hour) % 60;
                } else if (duration - 3600 * hour < 60) {
                    min = 00;
                    sec = duration - 3600 * hour;
                }
                time = transform(hour) + ":" + transform(min) + ":" + transform(sec);
            }
        }
        if (time == null) {
//            time = "00:00:00";
            time = "00:00";
        }
        return time;
    }

    /**
     * 工具
     *
     * @param i
     * @return
     */
    private static String transform(long i) {
        if (i >= 10) {
            return i + "";
        } else if (i < 10) {
            return "0" + i;
        }
        return null;
    }
}
