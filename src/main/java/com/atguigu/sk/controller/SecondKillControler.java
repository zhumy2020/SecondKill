package com.atguigu.sk.controller;

import com.atguigu.sk.controller.utils.JedisPoolUtil;
import com.sun.xml.internal.bind.v2.runtime.output.Encoded;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;


@RestController
public class SecondKillControler {
    static String secKillScript = "local userid=KEYS[1];\r\n"
            + "local prodid=KEYS[2];\r\n"
            + "local qtkey='sk:'..prodid..\":qt\";\r\n"
            + "local usersKey='sk:'..prodid..\":usr\";\r\n"
            + "local userExists=redis.call(\"sismember\",usersKey,userid);\r\n"
            + "if tonumber(userExists)==1 then \r\n"
            + "   return 2;\r\n"
            + "end\r\n"
            + "local num= redis.call(\"get\" ,qtkey);\r\n"
            + "if tonumber(num)<=0 then \r\n"
            + "   return 0;\r\n"
            + "else \r\n"
            + "   redis.call(\"decr\",qtkey);\r\n"
            + "   redis.call(\"sadd\",usersKey,userid);\r\n"
            + "end\r\n"
            + "return 1";
    @PostMapping(value = "/sk/doSecondKillByLUA",produces = "text/html;charset=UTF-8")
    public String doSecondKillByLUA(Integer id){
        Integer usrId = (int)(10000*Math.random());
//        Jedis jedis = new Jedis("192.168.19.128",6379);
        Jedis jedis = JedisPoolUtil.getJedisPoolInstance().getResource();
        String s = jedis.scriptLoad(secKillScript);
        Object evalsha = jedis.evalsha(s, 2, usrId + "", id + "");
        jedis.close();
        int res = (int)((long)evalsha);
        if(res==0){
            return "库存不足";
        }else if(res==2){
            System.err.println("该用户已经秒杀过了"+usrId);
            return "该用户已经秒杀过了";
        }
        System.out.println("秒杀成功："+usrId);
        return "ok";
    }


    @PostMapping(value = "/sk/doSecondKill",produces = "text/html;charset=UTF-8")
    public String doSecondKill(Integer id){
        //随机生成用户ID
        Integer usrId = (int)(10000*Math.random());
        //秒杀商品的ID
        Integer pid = id;
        //秒杀业务
        String qtKey = "sk:" + pid +":qt";
        String usrKey = "sk:" + pid +":usr";
//        Jedis jedis = new Jedis("192.168.19.128",6379);
        Jedis jedis = JedisPoolUtil.getJedisPoolInstance().getResource();
        //判断该用户是否已经秒杀过
        Boolean sismember = jedis.sismember(usrKey, usrId + "");
        jedis.watch(qtKey);
        if(sismember){
            System.err.println("该用户已经秒杀过了"+usrId);
            jedis.close();
            return "该用户已经秒杀过了";
        }
        String qtStr = jedis.get(qtKey);
        if(StringUtils.isEmpty(qtStr)){
            jedis.close();
            return "秒杀尚未开始";
        }
        int qtNum = Integer.parseInt(qtStr);
        if(qtNum<=0){
            System.err.println("库存不足");
            jedis.close();
            return "库存不足";
        }
        Transaction multi = jedis.multi();
//        multi.decr(qtKey);
        Response<Long> decr = multi.decr(qtKey);
//        System.out.println(rest);
        //将用户加入到秒杀成功的列表中
        multi.sadd(usrKey,usrId+"");
//        String rest = multi.get(qtKey).get();
        multi.exec();
        Long rest = decr.get();
//        System.out.println("秒杀成功："+usrId+"还剩");
        System.out.println("秒杀成功："+usrId+"还剩"+rest);
        jedis.close();
        return "ok";
    }
}
