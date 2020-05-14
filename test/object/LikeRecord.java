package object;

import java.sql.Timestamp;

/**
 * @Description:
 * @auther: zhaiyao
 * @date: 2020/5/13
 */
public class LikeRecord {
    private int statusId;
    private int uid;
    private Timestamp time;

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }
}
