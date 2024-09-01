package ds.service;

import aj.org.objectweb.asm.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ds.mapper.PlaneMapper;
import ds.mapper.SeatMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SeatServiceImpl implements SeatService {

    private SeatMapper seatMapper;

    @Autowired
    public void setSeatMapper(SeatMapper seatMapper) {
        this.seatMapper = seatMapper;
    }

    @Override
    public int updateSeatStatus(Map<String, Object> params) {
        return seatMapper.updateSeatStatus(params);
    }

    public void updateSeatStatus(int planeId, int seatIndex, boolean seatStatus) {
        Map<String, Object> params = new HashMap<>();
        params.put("planeId", planeId);
        params.put("seatIndex", seatIndex);
        params.put("seatStatus", seatStatus);

        // 调用 Mapper 方法更新座位状态
        updateSeatStatus(params);
    }

    public boolean[] getSeatsArray(int planeId) {
        // 从数据库获取 JSON 格式的座位信息
        String seatsJson = seatMapper.getSeatsByPlaneId(planeId);

        // 如果没有获取到数据，返回一个空数组
        if (seatsJson == null || seatsJson.isEmpty()) {
            return new boolean[0]; // 或者返回 null，视具体情况而定
        }

        ObjectMapper objectMapper = new ObjectMapper();
        boolean[] seatsArray = new boolean[0];

        try {
            // 将 JSON 字符串转换为布尔类型的列表
            List<Boolean> seats = objectMapper.readValue(seatsJson, new com.fasterxml.jackson.core.type.TypeReference<List<Boolean>>() {});

            // 将 List<Boolean> 转换为 boolean[] 数组
            seatsArray = new boolean[seats.size()];
            for (int i = 0; i < seats.size(); i++) {
                seatsArray[i] = seats.get(i);
            }
        } catch (Exception e) {
            e.printStackTrace(); // 处理异常
        }

        return seatsArray;
    }
}
