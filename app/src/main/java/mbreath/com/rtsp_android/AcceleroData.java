package mbreath.com.rtsp_android;

public class AcceleroData {
    private long timeStamp;
    private float x;
    private float y;
    private float z;

    public AcceleroData(long timeStamp, float x, float y, float z) {
        this.timeStamp = timeStamp;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return String.format("%d,%f,%f,%f", timeStamp, x, y, z);
    }
}
