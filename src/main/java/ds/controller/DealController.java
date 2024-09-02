package ds.controller;

import aj.org.objectweb.asm.TypeReference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import ds.common.AliPayConfig;
import ds.pojo.Deal;
import ds.pojo.Plane;
import ds.pojo.Ticket;
import ds.service.DealService;

import ds.service.PlaneService;
import ds.service.SeatService;
import ds.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

@Controller
@CrossOrigin
public class DealController {

    private static final String GATEWAY_URL = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";
    private static final String FORMAT = "JSON";
    private static final String CHARSET = "UTF-8";
    // 签名方式
    private static final String SIGN_TYPE = "RSA2";

    @Autowired
    @Qualifier("dealServiceImpl")
    private DealService dealService;

    @Autowired
    @Qualifier("ticketServiceImpl")
    TicketService ticketService;

    @Autowired
    @Qualifier("planeServiceImpl")
    PlaneService planeService;

    @Autowired
    @Qualifier("seatServiceImpl")
    SeatService seatService;

    @Autowired
    private AliPayConfig aliPayConfig;

    //封装一个alipay的方法
    private String alipay(Deal deal) throws AlipayApiException {
        AlipayClient alipayClient = new DefaultAlipayClient(GATEWAY_URL, aliPayConfig.getAppId(),
                aliPayConfig.getAppPrivateKey(), FORMAT, CHARSET, aliPayConfig.getAlipayPublicKey(), SIGN_TYPE);

        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(aliPayConfig.getNotifyUrl());
        cn.hutool.json.JSONObject bizContent = new cn.hutool.json.JSONObject();
        bizContent.set("out_trade_no", deal.getDeal_id());
        bizContent.set("total_amount", deal.getPrice());
        bizContent.set("subject", "ticket_id" + deal.getTicket_id());
        bizContent.set("product_code", "FAST_INSTANT_TRADE_PAY");
        request.setBizContent(bizContent.toString());
        request.setReturnUrl("http://localhost:8081/#/personal");

        return alipayClient.pageExecute(request).getBody(); // 生成表单
    }

    //付款一笔直达订单
    @ResponseBody
    @RequestMapping(value = "/pay",method = RequestMethod.POST)
    public String pay(HttpServletRequest req, HttpServletResponse httpResponse) throws Exception {

        //获得对象
        String s = new BufferedReader(new InputStreamReader(req.getInputStream())).readLine();
        String str=s.substring(8,s.length()-1);
        Deal deal= JSON.parseObject(str,Deal.class, Feature.InitStringFieldAsEmpty);

        //解码
        try {
            deal.setAttribute(java.net.URLDecoder.decode(deal.getAttribute(), "UTF-8"));
            deal.setId_number(java.net.URLDecoder.decode(deal.getId_number(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //插入一条订单信息
        int deal_id=dealService.addDeal(deal);
        //插入订单和票的绑定信息
        deal.setDeal_id(deal_id);
        dealService.addDealTicket(deal);//向dealticket表插入信息
        //转发对票的剩余数减一
        req.setAttribute("ticket_id",deal.getTicket_id());

        // 生成支付表单并写入响应
        String form = "";
        try {
            form = alipay(deal);

            // 直接返回生成的表单 HTML 字符串
            httpResponse.setContentType("text/html;charset=" + CHARSET);
            PrintWriter writer = httpResponse.getWriter();
            writer.print(form);
            writer.flush();
            writer.close();
        } catch (AlipayApiException e) {
            e.printStackTrace();
            httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Payment processing error");
        }
        System.out.println("Generated 这个真的是最新版form: " + form);

        //余票减一
        HashMap<String, Object> map = new HashMap<String, Object>();
        int ticket_id = deal.getTicket_id();
        map.put("ticket_id",ticket_id);
        ticketService.updateTicketMinus1(map);

        return form;
    }



    //付款一笔中转订单
    @ResponseBody
    @RequestMapping(value = "/pay2",method = RequestMethod.POST)
    public String pay2(HttpServletRequest req, HttpServletResponse httpResponse) throws IOException {

        //获得对象（绑定两张票的订单）
        String s = new BufferedReader(new InputStreamReader(req.getInputStream())).readLine();
        String str=s.substring(8,s.length()-1);
        Deal deal= JSON.parseObject(str,Deal.class, Feature.InitStringFieldAsEmpty);

        JSONObject oo= JSON.parseObject(str);
        String ticket_id1=JSON.toJSONString(oo.get("ticket_id1"));
        String ticket_id2=JSON.toJSONString(oo.get("ticket_id2"));

        //解码
        try {
            deal.setAttribute(java.net.URLDecoder.decode(deal.getAttribute(), "UTF-8"));
            deal.setId_number(java.net.URLDecoder.decode(deal.getId_number(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //订单表中的信息相同，插入一条即可
        int deal_id=dealService.addDeal(deal);

        //插入两条‘订单-票’绑定信息
        deal.setDeal_id(deal_id);
        deal.setTicket_id(Integer.parseInt(ticket_id1.substring(1,ticket_id1.length()-1)));
        req.setAttribute("ticket_id1",deal.getTicket_id());
        dealService.addDealTicket(deal);
        deal.setTicket_id(Integer.parseInt(ticket_id2.substring(1,ticket_id2.length()-1)));
        req.setAttribute("ticket_id2",deal.getTicket_id());
        dealService.addDealTicket(deal);

        //余票减一
        int ticket_id11=(int)req.getAttribute("ticket_id1");
        int ticket_id21=(int)req.getAttribute("ticket_id2");
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("ticket_id",ticket_id11);
        ticketService.updateTicketMinus1(map);
        HashMap<String, Object> map1 = new HashMap<String, Object>();
        map1.put("ticket_id",ticket_id21);
        ticketService.updateTicketMinus1(map1);

        // 生成支付表单并写入响应
        String form = "";
        try {
            form = alipay(deal);
            // 直接返回生成的表单 HTML 字符串
            httpResponse.setContentType("text/html;charset=" + CHARSET);
            PrintWriter writer = httpResponse.getWriter();
            writer.print(form);
            writer.flush();
            writer.close();
        } catch (AlipayApiException e) {
            e.printStackTrace();
            httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Payment processing error");
        }
        System.out.println("Generated 这个真的是最新版form: " + form);

        return form;
    }
    @ResponseBody
    @RequestMapping(value = "/dealPaid",method = RequestMethod.POST)
    public String dealPaid(HttpServletRequest req) throws IOException{
        //获得对象
        String s = new BufferedReader(new InputStreamReader(req.getInputStream())).readLine();
        String str=s.substring(8,s.length()-1);
        Deal deal= JSON.parseObject(str,Deal.class, Feature.InitStringFieldAsEmpty);

        System.out.println(str);
        //根据userID获得其绑定的所有乘客身份证号码
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("id",deal.getId());
        map.put("pay","YES");
        List<Deal> dealList =dealService.getDealListPlus(map);

        return JSON.toJSONString(dealList);

    }

    @ResponseBody
    @RequestMapping(value = "/dealUnpaid",method = RequestMethod.POST)
    public String dealUnpaid(HttpServletRequest req) throws IOException{
        //获得对象
        String s = new BufferedReader(new InputStreamReader(req.getInputStream())).readLine();
        String str=s.substring(8,s.length()-1);
        Deal deal= JSON.parseObject(str,Deal.class, Feature.InitStringFieldAsEmpty);

        //根据userID获得其绑定的所有乘客身份证号码
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("id",deal.getId());
        map.put("pay","NO");
        List<Deal> dealList =dealService.getDealListPlus(map);
        return JSON.toJSONString(dealList);

    }

    @RequestMapping(value = "/change",method = RequestMethod.POST)
    public String change(HttpServletRequest req) throws IOException{
        //获得对象
        String s = new BufferedReader(new InputStreamReader(req.getInputStream())).readLine();
        String str=s.substring(8,s.length()-1);
        Deal deal= JSON.parseObject(str,Deal.class, Feature.InitStringFieldAsEmpty);
        System.out.println(str);

        //更新改签后订单的价格，一般改签的原则是:多不退少补
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("deal_id",deal.getDeal_id());
        map.put("price",deal.getPrice());
        dealService.updateDeal(map);

        //在deal_ticket中删除原绑定，添加新绑定
        JSONObject oo= JSON.parseObject(str);
        String old_ticket_id=JSON.toJSONString(oo.get("old_ticket_id"));
        String new_ticket_id=JSON.toJSONString(oo.get("new_ticket_id"));

        HashMap<String, Object> map1 = new HashMap<String, Object>();
        map1.put("ticket_id",Integer.parseInt(old_ticket_id));
        map1.put("deal_id",deal.getDeal_id());
        dealService.deleteDealTicket(map1);

        deal.setTicket_id(Integer.parseInt(new_ticket_id));
        dealService.addDealTicket(deal);

        req.setAttribute("old_ticket_id",Integer.parseInt(old_ticket_id));
        req.setAttribute("new_ticket_id",Integer.parseInt(new_ticket_id));
        return "forward:ticketChange";
    }

    @RequestMapping(value = "/refund_direct",method = RequestMethod.POST)
    public String refund_direct(HttpServletRequest req) throws IOException{
        //获得对象
        String s = new BufferedReader(new InputStreamReader(req.getInputStream())).readLine();
        String str=s.substring(8,s.length()-1);
        Deal deal= JSON.parseObject(str,Deal.class, Feature.InitStringFieldAsEmpty);
        System.out.println(str);

        //将订单的pay改为NO
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("deal_id",deal.getDeal_id());
        map.put("pay",deal.getPay());
        dealService.updateDeal(map);

        req.setAttribute("ticket_id",deal.getTicket_id());

        return "forward:numberRestAdd1";
    }
    @RequestMapping(value = "/refund_transit",method = RequestMethod.POST)
    public String refund_transit(HttpServletRequest req) throws IOException{

        //获得对象
        String s = new BufferedReader(new InputStreamReader(req.getInputStream())).readLine();
        String str=s.substring(8,s.length()-1);
        Deal deal= JSON.parseObject(str,Deal.class, Feature.InitStringFieldAsEmpty);
        System.out.println(str);

        //将订单的价格改为剩下的一张票的价格，属性改为单程
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("deal_id",deal.getDeal_id());
        map.put("price",deal.getPrice());
        map.put("attribute",deal.getAttribute());
        dealService.updateDeal(map);

        req.setAttribute("ticket_id",deal.getTicket_id());


        //删除原绑定
        HashMap<String,Object> map1=new HashMap<>();
        map1.put("deal_id",deal.getDeal_id());
        map1.put("ticket_id",deal.getTicket_id());
        dealService.deleteDealTicket(map1);

        //新建一个订单存放已退票的信息//添加新绑定
        JSONObject oo= JSON.parseObject(str);
        String new_deal=JSON.toJSONString(oo.get("deal"));
        Deal deal1=JSON.parseObject(new_deal,Deal.class, Feature.InitStringFieldAsEmpty);
        int deal_id=dealService.addDeal(deal1);

        HashMap<String,Object> map2=new HashMap<>();
        map2.put("deal_id",deal_id);
        map2.put("pay","NO");
        dealService.updateDeal(map2);

        deal1.setDeal_id(deal_id);
        deal1.setTicket_id(deal.getTicket_id());
        dealService.addDealTicket(deal1);

        return  "forward:refund";
    }
    @ResponseBody
    @RequestMapping(value = "/cancel",method = RequestMethod.POST)
    public String cancel(HttpServletRequest req) throws IOException{

        Plane plane=(Plane) req.getAttribute("planes");

        for(Ticket ticket:plane.getTickets()){
            for (Deal deal:ticket.getDeals()){

                if(deal.getAttribute().equals("direct")){
                    //将订单的pay改为NO
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("deal_id",deal.getDeal_id());
                    map.put("pay","NO");
                    dealService.updateDeal(map);

                }else if(deal.getAttribute().equals("transit")){

                    //将订单的价格改为剩下的一张票的价格，属性改为单程
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("deal_id",deal.getDeal_id());
                    map.put("price",deal.getPrice()-ticket.getCoun()*plane.getPrice()/100-190);
                    map.put("attribute","direct");
                    dealService.updateDeal(map);

                    //删除原绑定
                    HashMap<String,Object> map1=new HashMap<>();
                    map1.put("deal_id",deal.getDeal_id());
                    map1.put("ticket_id",ticket.getTicket_id());
                    dealService.deleteDealTicket(map1);

                    //添加新绑定
                    deal.setPrice(plane.getPrice()*ticket.getCoun()/100+190);
                    deal.setAttribute("direct");
                    deal.setTicket_id(ticket.getTicket_id());
                    int deal_id=dealService.addDeal(deal);

                    HashMap<String,Object> map2=new HashMap<>();
                    map2.put("deal_id",deal_id);
                    map2.put("pay","NO");
                    dealService.updateDeal(map2);

                    deal.setDeal_id(deal_id);
                    dealService.addDealTicket(deal);

                }
            }
        }

        return "true";
    }
    @ResponseBody
    @RequestMapping(value = "/dealSearch",method = RequestMethod.POST)
    public String dealSearch(HttpServletRequest req) throws IOException{
        //获得对象
        String s = new BufferedReader(new InputStreamReader(req.getInputStream())).readLine();
        String str=s.substring(8,s.length()-1);
        Deal deal= JSON.parseObject(str,Deal.class, Feature.InitStringFieldAsEmpty);
        System.out.println(str);

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("deal_id",deal.getDeal_id());
        List<Deal> dealList =dealService.getDealListPlus(map);

        List<Deal> dealList2 =dealService.getDealList(map);
        if(dealList2.get(0).getId() != deal.getId() && deal.getId()!= 90000000)
            return null;

        return JSON.toJSONString(dealList);
    }

    //对于选座位的支持
    @ResponseBody
    @RequestMapping(value = "/updateSeatAndPlane", method = RequestMethod.POST)
    public String updateSeatAndPlane(HttpServletRequest req) throws IOException {
        // 读取请求体中的数据
        String s = new BufferedReader(new InputStreamReader(req.getInputStream())).readLine();
        String str = s.substring(8, s.length() - 1);
        Deal deal = JSON.parseObject(str, Deal.class, Feature.InitStringFieldAsEmpty);

        // 解码字段
        try {
            deal.setAttribute(java.net.URLDecoder.decode(deal.getAttribute(), "UTF-8"));
            deal.setId_number(java.net.URLDecoder.decode(deal.getId_number(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // 更新 plane表 的seat 信息
        HashMap<String, Object> seatParams = new HashMap<>();
        int idd = planeService.selectbydeal(deal.getDeal_id());
        seatParams.put("planeId",idd);

        if(deal.getSeat() == null)
            {
                HashMap<String, Object> mydeal = new HashMap<>();
                mydeal.put("deal_id", deal.getDeal_id());
                List<Deal> temp = dealService.getDealList(mydeal);
                Deal dd = temp.get(0);
                seatParams.put("seatIndex", dd.getSeat());
                seatParams.put("seatStatus", false);}
        else {
            seatParams.put("seatIndex",deal.getSeat());
            seatParams.put("seatStatus", true);
        }
        seatService.updateSeatStatus(seatParams);

        // 更新 deal表 的 seat 信息
        HashMap<String, Object> seat = new HashMap<>();
        seat.put("seat",deal.getSeat());
        seat.put("deal_id", deal.getDeal_id());
        dealService.updateDeal(seat);

        return "true";
    }



    @ResponseBody
    @RequestMapping(value = "/getSeatsArray", method = RequestMethod.POST)
    public boolean[] getSeatsArray(@RequestBody Map<String, Integer> requestParams) {
        int planeId = planeService.selectbydeal(requestParams.get("planeId"));
        // 从请求体中获取 planeId
        boolean[] seatsArray = seatService.getSeatsArray(planeId);
        System.out.println("返回的座位数组：" + Arrays.toString(seatsArray));
        // 返回布尔数组
        return seatsArray;
    }

    @ResponseBody
    @RequestMapping(value = "/getMyseat", method = RequestMethod.POST)
    public List<Integer> getMyseat(@RequestBody Map<String, Integer> requestParams) {
        int deal_id = requestParams.get("planeId");
        HashMap<String, Object> mydeal = new HashMap<>();
        mydeal.put("deal_id", deal_id);
        List<Deal> temp = dealService.getDealList(mydeal);
        Deal dd = temp.get(0);

        // 获取座位字符串
        String seatString = dd.getSeat();

        // 将座位字符串转换为整数列表
        List<Integer> seatList = new ArrayList<>();
        if (seatString != null && !seatString.isEmpty()) {
            // 使用逗号分隔字符串并转换为整数列表
            String[] seatArray = seatString.split(",");
            for (String seat : seatArray) {
                try {
                    seatList.add(Integer.parseInt(seat.trim()));
                } catch (NumberFormatException e) {
                    e.printStackTrace(); // 处理转换错误
                }
            }
        }

        return seatList;
    }

}


