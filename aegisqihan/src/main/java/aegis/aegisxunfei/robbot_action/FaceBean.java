package aegis.aegisxunfei.robbot_action;

/**
 * 作者：jksfood on 2017/3/6 17:32
 */

public class FaceBean {
    private int w;
    private String birthday;
    private double bottom;
    private String gender;
    private double left;
    private String qlink;
    private double right;
    private String user;
    private int h;
    private double top;

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public double getBottom() {
        return bottom;
    }

    public void setBottom(double bottom) {
        this.bottom = bottom;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public double getLeft() {
        return left;
    }

    public void setLeft(double left) {
        this.left = left;
    }

    public String getQlink() {
        return qlink;
    }

    public void setQlink(String qlink) {
        this.qlink = qlink;
    }

    public double getRight() {
        return right;
    }

    public void setRight(double right) {
        this.right = right;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public double getTop() {
        return top;
    }

    public void setTop(double top) {
        this.top = top;
    }

    @Override
    public String toString() {
        return "FaceBean{" +
                "w=" + w +
                ", birthday='" + birthday + '\'' +
                ", bottom=" + bottom +
                ", gender='" + gender + '\'' +
                ", left=" + left +
                ", qlink='" + qlink + '\'' +
                ", right=" + right +
                ", user='" + user + '\'' +
                ", h=" + h +
                ", top=" + top +
                '}';
    }
}
