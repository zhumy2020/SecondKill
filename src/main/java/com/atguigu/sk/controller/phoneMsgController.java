package com.atguigu.sk.controller;

import com.atguigu.sk.controller.utils.JedisPoolUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.Jedis;

@Controller
public class phoneMsgController {

    @ResponseBody
    @PostMapping("/sk/getPhoneMsg")
    public String getPhoneMsg(String phoneNum){
        Jedis jedis = JedisPoolUtil.getJedisPoolInstance().getResource();
        int creditNum = (int)(1000000*Math.random());
        jedis.setex(phoneNum,300,creditNum+"");
        System.out.println("验证码为"+creditNum);
        return "ok";
    }
    @PostMapping("/sk/subCredNum")
    public String subCredNum(String phoneNum,String creditNum){
        Jedis jedis = JedisPoolUtil.getJedisPoolInstance().getResource();
//        System.out.println(jedis.get(phoneNum));
        if (jedis.get(phoneNum).equals(creditNum)){
            return "ok";
        }
        return "fail";
    }
}
