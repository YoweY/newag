package aegis.aegisxunfei.robbot_action;

/**
 * 作者：jksfood on 2017/2/24 11:00
 */

public class Led {
    private String position;
    private String color;
    private int time;
    private boolean isFlickering;

    public boolean isFlickering() {
        return isFlickering;
    }

    public void setFlickering(boolean flickering) {
        isFlickering = flickering;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getPosition() {

        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
