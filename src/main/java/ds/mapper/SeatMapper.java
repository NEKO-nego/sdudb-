package ds.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface SeatMapper {
    int updateSeatStatus(Map<String, Object> params);
    String getSeatsByPlaneId(@Param("planeId") int planeId);

}
