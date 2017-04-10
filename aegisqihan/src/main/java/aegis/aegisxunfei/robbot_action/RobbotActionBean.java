package aegis.aegisxunfei.robbot_action;

/**
 * 作者：jksfood on 2017/2/23 20:36
 */

public class RobbotActionBean {

    private String type;
    private String direction;
    private String position;
    private String color;
    private int angle;
    private int distance;
    private int speed;
    private int time;
    private boolean isFlickering;

    public boolean isFlickering() {
        return isFlickering;
    }

    public void setFlickering(boolean flickering) {
        isFlickering = flickering;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }
}
