package aegis.aegisxunfei.robbot_action;

/**
 * 作者：jksfood on 2017/2/23 20:54
 */

public class Wheel {
    private String direction;
    private int angle;
    private int distance;

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    private int speed;

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "Wheel{" +
                "direction='" + direction + '\'' +
                ", angle=" + angle +
                ", distance=" + distance +
                ", speed=" + speed +
                '}';
    }
}
