package amc.group.pkg9;

import java.util.Date;

public class TimeBlock {
    private Date startTime;
    private Date endTime;
    private int rowNumber;

    public TimeBlock(){}

    public TimeBlock(Date startTime, Date endTime, int rowNumber) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.rowNumber = rowNumber;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public int getRowNumber() {
        return rowNumber;
    }
}
