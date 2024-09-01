package ds.service;

import org.springframework.stereotype.Service;

import java.util.Map;


public interface SeatService {
    int updateSeatStatus(Map<String, Object> params);
    public void updateSeatStatus(int planeId, int seatIndex, boolean seatStatus);
    public boolean[] getSeatsArray(int planeId);
}

