package ds.service;

import ds.mapper.CityMapper;
import ds.mapper.PlaneMapper;
import ds.pojo.City;
import ds.pojo.Plane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CityServiceImpl implements CityService{
    @Autowired
    PlaneMapper planemapper;
    CityMapper cityMapper;
    public void setCityMapper(CityMapper cityMapper) {
        this.cityMapper = cityMapper;
    }

    @Override
    public List<City> getCityList(Map map) {
        return cityMapper.getCityList(map);
    }

    @Override
    public List<City> getTransitCity(Map map,String start_city,String end_city){
        List<City> cities= cityMapper.getCityList(map);
        int s=cities.size();

        Map<String,Object> startM=new HashMap();
        startM.put("city_name",start_city);
        List<City> cc=cityMapper.getCityList(startM);

        Map<String,Object> endM=new HashMap();
        endM.put("city_name",end_city);
        List<City> ccc=cityMapper.getCityList(endM);
        int end=ccc.get(0).getCity_id();

        if (cc.size()>0&&ccc.size()>0) {
            int start = cc.get(0).getCity_id();
            for (int i = 0; i < s; i++) {
                if (cities.get(i).getCity_id() == start) {
                    start = i;
                    break;
                }
            }
            for (int i = 0; i < s; i++) {
                if (cities.get(i).getCity_id() == end) {
                    end = i;
                    break;
                }
            }

            List<City> mid_city = new ArrayList<>();
            for (int i = 0; i < s; i++) {
                City mid = cities.get(i);
                if (mid.getLocation().equals("china") && i != start && i != end) {
                    Map<String,Object> param1 = new HashMap();
                    Map<String,Object> param2 = new HashMap();
                    //前段航班
                    param1.put("start_city", start_city);
                    param1.put("end_city", mid.getCity_name());
                    //后段航班
                    param2.put("start_city", mid.getCity_name());
                    param2.put("end_city", end_city);

                    if( planemapper.getPlaneList(param1).size()>0 && planemapper.getPlaneList(param2).size()>0){
                        mid_city.add(mid);
                    }
                }
            }

            return mid_city;

        }
        else return null;

    }
}
