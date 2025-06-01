package User.model;

import java.sql.Date;

/**
 * 캠핑카 예약 기간(시작일~종료일)을 담는 단순 VO 클래스
 */
public class Period {
    private Date startDate;
    private Date endDate;

    public Period(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate   = endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }
}
