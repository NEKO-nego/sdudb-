/*
package ds.controller;

import cn.hutool.json.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import ds.common.AliPayConfig;
import ds.common.AliPayConfig;
import ds.pojo.Deal;
import ds.service.DealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/alipay")
public class AliPayController {

    // 支付宝沙箱网关地址
    private static final String GATEWAY_URL = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";
    private static final String FORMAT = "JSON";
    private static final String CHARSET = "UTF-8";
    // 签名方式
    private static final String SIGN_TYPE = "RSA2";

    @Autowired
    private AliPayConfig aliPayConfig;

    @Autowired
    private DealService dealService;

    @GetMapping("/alipay")//接受前端的唯一订单号，在数据库中查询已有的未支付订单，并接通alipay
    public void alipay(String orderNo, HttpServletResponse httpResponse) throws Exception {
        // 根据订单号查询订单信息
        Deal deal = dealService.selectByOrderNo(orderNo);

        // 1. 创建AlipayClient，初始化支付宝客户端，使用alipay-sdk-java
        AlipayClient alipayClient = new DefaultAlipayClient(GATEWAY_URL, aliPayConfig.getAppId(),
                aliPayConfig.getAppPrivateKey(), FORMAT, CHARSET, aliPayConfig.getAlipayPublicKey(), SIGN_TYPE);

        // 2. 创建Request并设置Request参数
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest(); // 发起请求的Request类
        request.setNotifyUrl(aliPayConfig.getNotifyUrl());
        JSONObject bizContent = new JSONObject();
        bizContent.set("out_trade_no", orders.getOrderNo()); // 我们自己生成的订单编号
        bizContent.set("total_amount", orders.getTotal()); // 订单的总金额
        bizContent.set("subject", orders.getGoodsName()); // 支付的名称
        bizContent.set("product_code", "FAST_INSTANT_TRADE_PAY"); // 固定配置
        request.setBizContent(bizContent.toString());
        request.setReturnUrl("http://localhost:8080/orders");

        // 执行请求，拿到响应的表单，返回给前端
        String form = "";
        try {
            form = alipayClient.pageExecute(request).getBody(); // 调用SDK生成表单
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        httpResponse.setContentType("text/html;charset=" + CHARSET);
        httpResponse.getWriter().write(form); // 直接将完整的表单html输出到页面
        httpResponse.getWriter().flush();
        httpResponse.getWriter().close();
    }
}
*/