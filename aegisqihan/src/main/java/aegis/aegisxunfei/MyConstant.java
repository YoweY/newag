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


}
