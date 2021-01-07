/**
    20201221 删除成交情况的重复数据
    逻辑梳理:
        1, 获取 每个项目的id
            2, 根据每个项目 , 获取 cjqk 里 recordtype = '201945938A54BAEfBWgh' 的类型, (获取上门进线情况, 得到id,  连接--成交情况 relationid)
            3, 根据上门 id , 获取 id下的所有成交情况, 遍历 是否有重复
    重复规则:
        上门 , 进线 , 成交手数 , 套数 , 楼层 , 面积 , 单价 , 行业 , 来源区域 , 备注
 */
CCService cs = new CCService(userInfo);
String uid = userInfo.getUserId();  //获取用户ID
String profileid = userInfo.getProfileId();//简档id
//日期的处理
Calendar cal = Calendar.getInstance();
int year = cal.get(Calendar.YEAR);//当前年份
// int month = cal.get(Calendar.MONTH) + 1;//当前月份 0代表1月
// if(month==0){
//   year=year-1;
// 	month=12; 
// }
java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd");
String nowday = df.format(new Date());
// String begin_day=year+"-"+month+"-01";
String datetime = "and a.createdate >= str_to_date('2017-01-01 00:00:00', '%Y-%m-%d %H:%i:%s')  AND a.createdate<= str_to_date('"+nowday+"  23:59:59', '%Y-%m-%d %H:%i:%s')";
//String datetime = "and a.createdate >= str_to_date('2020-05-31 00:00:00', '%Y-%m-%d %H:%i:%s')  AND a.createdate<= str_to_date('2020-05-31 23:59:59', '%Y-%m-%d %H:%i:%s')";

// 获取所有市场盘源项目的id (二级租赁项目) // recordtype = '2018D7CDD5A5418hbgiJ'
// String scpysql = "select scpymc as id from cjqk a where  a.recordtype = '201945938A54BAEfBWgh' and is_deleted='0' " + datetime + " group by scpymc"; 
String scpysql = "select scpymc as id from cjqk a where  a.recordtype = '201945938A54BAEfBWgh' and is_deleted='0'  group by scpymc"; 
List<CCObject> scpyidlist = cs.cqlQuery("cjqk",scpysql); // 获取创建过市场数据的 所有项目的id
for(CCObject scpyid:scpyidlist) {
    String xmid = scpyid.get("id")==null?"":scpyid.get("id")+ "";  // 获取项目的id
    // 获取上门进线的数据 , 
    // String smjxsql = "select id,sms,jxs,cjss,cjts from cjqk a where a.scpymc = '"+xmid+"' and a.recordtype = '201945938A54BAEfBWgh' and is_deleted='0' " + datetime + "order by createdate desc";
    //String smjxsql = "select id,sms,jxs,cjss,cjts,createbyid,scpymc,createdate from cjqk a where a.scpymc = '"+xmid+"' and a.recordtype = '201945938A54BAEfBWgh' and is_deleted='0' order by createdate desc";
    String smjxsql = "select id,sms,jxs,cjss,cjts,createbyid,scpymc,createdate from cjqk a where a.scpymc = '"+xmid+"' and a.recordtype = '201945938A54BAEfBWgh' and is_deleted='0' "+datetime+"  order by createdate";
    // if(true){  // 打印日志
    //     // trigger.addErrorMessage(String.valueOf(smida+"2^"+cjqkida + "^"+cjqkidb)); 
    //     trigger.addErrorMessage(String.valueOf(smjxsql)); 
    // }
    List<CCObject>  smjxlist = cs.cqlQuery("cjqk",smjxsql); // 根据项目id, 获取每个项目 创建了多少条上门进线数据
    for (int i= 0; i < smjxlist.size(); i++) {   // CCObject smjx:smjxlist
        String smida = smjxlist.get(i).get("id")==null?"":smjxlist.get(i).get("id")+ "";  // 获取上门进线 id  是  成交情况 relationid
        String createbyuserid = smjxlist.get(i).get("createbyid")==null?"":smjxlist.get(i).get("createbyid")+ ""; //创建人id
        String xmmcid = smjxlist.get(i).get("scpymc")==null?"":smjxlist.get(i).get("scpymc")+ "";  // 项目id
        // String smsa = smjxlist.get(i).get("sms")==null?"":smjxlist.get(i).get("sms")+ "";  // 上门数
        // String jxsa = smjxlist.get(i).get("jxs")==null?"":smjxlist.get(i).get("jxs")+ ""; // 进线数
        // String cjssa = smjxlist.get(i).get("cjss")==null?"":smjxlist.get(i).get("cjss")+ ""; // 成交手数
        // String cjtsa = smjxlist.get(i).get("cjts")==null?"":smjxlist.get(i).get("cjts")+ ""; // 成交套数
        // 根据 上门进线的  id ,  获取成交情况 a
        String cjqksqla = "select a.relationid,id,ifnull(a.lc,'-') as lc,ifnull(a.mj,'-') as mj,ifnull(a.dj,'-') as dj,ifnull(a.xy,'-') as xy,ifnull(a.qy,'-') as qy,ifnull(a.bz,'-') as bz from cjqk a where a.createdate >= str_to_date('"+smjxlist.get(i).get("createdate")+"', '%Y-%m-%d %H:%i:%s')  AND a.createdate<= str_to_date('"+smjxlist.get(i).get("createdate")+"'+interval 5 second, '%Y-%m-%d %H:%i:%s')  and a.is_deleted = '0' and a.createbyid = '"+createbyuserid+"' and a.scpymc = '"+xmmcid+"'  and (a.relationid='"+smida+"'  or (a.createdate<'2020-12-09 00:00:00' and a.relationid is null)) and a.recordtype in ('20186A33481F087wkKC5','20186B76C925373c6GQa','2020CA38DA2EA62GseBx') and a.relationid is null  order by a.createdate";
        
        // if(true){  // 打印日志
        //     // trigger.addErrorMessage(String.valueOf(smida+"2^"+cjqkida + "^"+cjqkidb)); 
        //     trigger.addErrorMessage(String.valueOf(cjqksqla)); 
        // }
        List<CCObject> cjqklista = cs.cqlQuery("cjqk",cjqksqla); // 根据 每个上门进线数据的id, 获取每个id创建了几条成交数据
        for (CCObject cjqkobj:cjqklista) {
            String relationid = cjqkobj.get("relationid")==null?"":cjqkobj.get("relationid")+"";
            if (!"".equals(relationid)) {
                continue;
            }
            String cjqkid = cjqkobj.get("id")==null?"":cjqkobj.get("id")+"";
            String cjqkupdate = "update cjqk set relationid = '"+smida+"' where id = '"+cjqkid+"'";
            // if(true){  // 打印日志
            //     // trigger.addErrorMessage(String.valueOf(smida+"2^"+cjqkida + "^"+cjqkidb)); 
            //     trigger.addErrorMessage(String.valueOf(cjqkupdate));
            // }
            cs.cqlQuery("cjqk",cjqkupdate);
        }
        //二 for(int j= 0; j < cjqklista.size(); j++) {
        //     String cjqkida = cjqklista.get(j).get("id")==null?"":cjqklista.get(j).get("id")+ "";  // 获取成交信息的id
        //     String lca = cjqklista.get(j).get("lc")==null?"":cjqklista.get(j).get("lc")+ "";  // 楼层
        //     String mja = cjqklista.get(j).get("mj")==null?"":cjqklista.get(j).get("mj")+ ""; // 面积
        //     String dja = cjqklista.get(j).get("dj")==null?"":cjqklista.get(j).get("dj")+ ""; // 单价
        //     String xya = cjqklista.get(j).get("xy")==null?"":cjqklista.get(j).get("xy")+ ""; // 行业
        //     String qya = cjqklista.get(j).get("qy")==null?"":cjqklista.get(j).get("qy")+ ""; // 区域
        //     String bza = cjqklista.get(j).get("bz")==null?"":cjqklista.get(j).get("bz")+ ""; // b备注
        //     for(int k = j+1; k < cjqklista.size(); k++) {
        //         String cjqkidb = cjqklista.get(k).get("id")==null?"":cjqklista.get(k).get("id")+ "";  // 获取成交信息的id
        //         String lcb = cjqklista.get(k).get("lc")==null?"":cjqklista.get(k).get("lc")+ "";  // 楼层
        //         String mjb = cjqklista.get(k).get("mj")==null?"":cjqklista.get(k).get("mj")+ ""; // 面积
        //         String djb = cjqklista.get(k).get("dj")==null?"":cjqklista.get(k).get("dj")+ ""; // 单价
        //         String xyb = cjqklista.get(k).get("xy")==null?"":cjqklista.get(k).get("xy")+ ""; // 行业
        //         String qyb = cjqklista.get(k).get("qy")==null?"":cjqklista.get(k).get("qy")+ ""; // 区域
        //         String bzb = cjqklista.get(k).get("bz")==null?"":cjqklista.get(k).get("bz")+ ""; // b备注
        //         if (lca.equals(lcb)) { // 对比 楼层
        //             if (mja.equals(mjb)) { // 对比 面积
        //                 if (dja.equals(djb)) { // 对比单价
        //                     if (xya.equals(xyb)) { // 对比行业
        //                         if (qya.equals(qyb)) { // 对比区域
        //                             if (bza.equals(bzb)) { // 对比备注
        //                                 if(true){  // 打印日志
        //                                     // trigger.addErrorMessage(String.valueOf(smida+"2^"+cjqkida + "^"+cjqkidb)); 
        //                                     trigger.addErrorMessage(String.valueOf(smjxsql +"^^"+cjqksqla)); 
        //                                 }
        //                             }
        //                         }
        //                     }
        //                 }
        //             }
        //         }
        //     }
        // } 二

        // for (int j= i+1; j < smjxlist.size(); j++) {
        //     String smidb = smjxlist.get(j).get("id")==null?"":smjxlist.get(j).get("id")+ "";  // 获取上门进线 id  是  成交情况 relationid
        //     String smsb = smjxlist.get(j).get("sms")==null?"":smjxlist.get(j).get("sms")+ "";  // 上门数
        //     String jxsb = smjxlist.get(j).get("jxs")==null?"":smjxlist.get(j).get("jxs")+ ""; // 进线数
        //     String cjssb = smjxlist.get(j).get("cjss")==null?"":smjxlist.get(j).get("cjss")+ ""; // 成交手数
        //     String cjtsb = smjxlist.get(j).get("cjts")==null?"":smjxlist.get(j).get("cjts")+ ""; // 成交套数
        //     if (smsa.equals(smsb)) { // 判断上门数 是否相等
        //         if (jxsa.equals(jxsb)) { // 判断进线数 是否相等
        //             if (cjssa.equals(cjssb)) { // 判断成交手数 是否相等
        //                 if (cjtsa.equals(cjtsb)) { // 判断成交套数 是否相等.
        //                     // if(true){  // 打印日志
        //                     //     trigger.addErrorMessage(String.valueOf(smida +"^^"+smidb)); 
        //                     // }
        //                     // 根据 上门进线的  id ,  获取成交情况 a
        //                     String cjqksqla = "select id,ifnull(c.lc,'-') as lc,ifnull(c.mj,'-') as mj,ifnull(c.dj,'-') as dj,ifnull(c.xy,'-') as xy,ifnull(c.qy,'-') as qy,ifnull(c.bz,'-') as bz from cjqk c where c.is_deleted = '0'  and c.relationid = '"+smida+"' and c.recordtype in ('20186A33481F087wkKC5','20186B76C925373c6GQa','2020CA38DA2EA62GseBx')";
        //                     // if(true){  // 打印日志
        //                     //     trigger.addErrorMessage(String.valueOf(cjqksqla)); 
        //                     // }
        //                     List<CCObject> cjqklista = cs.cqlQuery("cjqk",cjqksqla);
        //                     // 根据 上门进线的  id ,  获取成交情况 b
        //                     String cjqksqlb = "select id,ifnull(c.lc,'-') as lc,ifnull(c.mj,'-') as mj,ifnull(c.dj,'-') as dj,ifnull(c.xy,'-') as xy,ifnull(c.qy,'-') as qy,ifnull(c.bz,'-') as bz from cjqk c where c.is_deleted = '0'  and c.relationid = '"+smidb+"' and c.recordtype in ('20186A33481F087wkKC5','20186B76C925373c6GQa','2020CA38DA2EA62GseBx')";
        //                     // if(true){  // 打印日志
        //                     //     trigger.addErrorMessage(String.valueOf(cjqksqla+"^^"+cjqksqlb)); 
        //                     // }
        //                     List<CCObject> cjqklistb = cs.cqlQuery("cjqk",cjqksqlb);
        //                     for(CCObject cjqka:cjqklista) {
        //                         String ida = cjqka.get("id")==null?"":cjqka.get("id")+""; // 获取成交情况id
        //                         String lca = cjqka.get("lc")==null?"":cjqka.get("lc")+""; // 获取楼层
        //                         String mja = cjqka.get("mj")==null?"":cjqka.get("mj")+""; // 获取面积
        //                         String dja = cjqka.get("dj")==null?"":cjqka.get("dj")+""; // 获取单价
        //                         String xya = cjqka.get("xy")==null?"":cjqka.get("xy")+""; // 获取行业
        //                         String qya = cjqka.get("qy")==null?"":cjqka.get("qy")+""; // 获取区域
        //                         String bza = cjqka.get("bz")==null?"":cjqka.get("bz")+""; // 获取备注
        //                         for(CCObject cjqkb:cjqklistb) {
        //                             String idb = cjqkb.get("id")==null?"":cjqkb.get("id")+""; // 获取成交情况id
        //                             String lcb = cjqkb.get("lc")==null?"":cjqkb.get("lc")+""; // 获取楼层
        //                             String mjb = cjqkb.get("mj")==null?"":cjqkb.get("mj")+""; // 获取面积
        //                             String djb = cjqkb.get("dj")==null?"":cjqkb.get("dj")+""; // 获取单价
        //                             String xyb = cjqkb.get("xy")==null?"":cjqkb.get("xy")+""; // 获取行业
        //                             String qyb = cjqkb.get("qy")==null?"":cjqkb.get("qy")+""; // 获取区域
        //                             String bzb = cjqkb.get("bz")==null?"":cjqkb.get("bz")+""; // 获取备注
        //                             if (lca.equals(lcb)) { // 对比 楼层
        //                                 if (mja.equals(mjb)) { // 对比 面积
        //                                     if (dja.equals(djb)) { // 对比单价
        //                                         if (xya.equals(xyb)) { // 对比行业
        //                                             if (qya.equals(qyb)) { // 对比区域
        //                                                 if (bza.equals(bzb)) { // 对比备注
        //                                                     if(true){  // 打印日志
        //                                                         trigger.addErrorMessage(String.valueOf(smida +"^"+smidb+"2^"+ida + "^"+idb)); 
        //                                                     }
        //                                                 }
        //                                             }
        //                                         }
        //                                     }
        //                                 }
        //                             }
        //                         }
        //                     }
        //                 }
        //             }
        //         }
        //     }
        // }
    }
}