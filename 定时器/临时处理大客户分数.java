/*
description:临时处理大客户分数
*/

CCService cs = new CCService((UserInfo)userInfo);
//日期的处理
String year = "2021"; // 年度
String month = "5"; // 月度
String nowday = "2021-05-24"; // 月底
String begin_day="2021-05-01"; // 月初

// 大客户绩效分数修改-- 每日会重新统计这个月的数据-- 修改到ryjx表中 begin
    // 当月 1 号 至今
    String datetimeone = " and createdate >= str_to_date('"+begin_day+" 00:00:00', '%Y-%m-%d %H:%i:%s')  AND createdate<= str_to_date('"+nowday+" 23:59:59', '%Y-%m-%d %H:%i:%s')";
    List<CCObject> viplist = cs.cqlQuery("ryjx","select * from ryjx where nd='"+year+"' and yf ='"+month+"' and is_deleted = '0' and recordtype='2020A3CA317261AEpAQJ'");
    // 通过成交的情况产生的代理明细获取创佣金额,成交时间5月select * from `user` where month(birthday) = 8 ;(获取所有成交的数据)
    List<CCObject> cylist = this.cqlQuery("Opportunity","select d.sjsy as sjsy,o.id as oppoid,o.ownerid as ownerid,o.xmmc as xmmc from Opportunity o inner join dljsmxb d on o.id = d.ywjh and d.is_deleted='0' where o.is_deleted='0' and o.jieduan='成交' and o.spzt='审批通过' and o.cjsj >= str_to_date('"+begin_day+" 00:00:00', '%Y-%m-%d %H:%i:%s')  AND o.cjsj<= str_to_date('"+nowday+" 23:59:59', '%Y-%m-%d %H:%i:%s')");
    // 遍历所有大客户的人员(计算分数)
    for(CCObject vipobj:viplist) {  // 遍历每个大客户人员, 计算分数
        int kaiguan = 0; // 如果有一个客户跟进信息没有录入,就没有 khxx  这个分数
        double khkt = 0.0; // 存储客户开拓 和 客户跟进 的分数
        double khxx = 0.0; // 存储客户信息分数
// 1. 获取客户开拓分数begin
        String userid = vipobj.get("bkhr")==null?"":vipobj.get("bkhr")+""; // 获取绩效数据的 所有者
        // 测试 薛超一  0052020581DE67CmBKwy
        // select *  from account a where a.is_deleted = '0' and a.createbyid = '0052020581DE67CmBKwy' and createdate >= str_to_date('2020-11-01 00:00:00', '%Y-%m-%d %H:%i:%s')  AND createdate<= str_to_date('2020-11-23 23:59:59', '%Y-%m-%d %H:%i:%s') 
        String accountSql = "select id,lxrdh  from account a where a.is_deleted = '0' and a.createbyid = '"+userid+"'" + datetimeone ;
        // out.print("获取登陆id: "+ccuserList.size() + "人员,人员id为:" + userid + name);
        List<CCObject> accountList = cs.cqlQuery("account",accountSql); // 取出 在月度内创建了几批客户
        // out.print("(" + accountSql+")");
        // out.print("开拓了多少个项目" + accountList.size());
        for (CCObject acc:accountList) {
            if(khkt < 40) { // 判断客户开拓分数不能超过 40 分
                khkt += 15; //累加 客户 开拓分数
            } else {
                khkt = 40;
            }
            boolean rs = false; // 判断电话号是否正确
            String id = acc.get("id")==null?"":acc.get("id")+""; // 客户的id
            String lxrdh = acc.get("lxrdh")==null?"":acc.get("lxrdh")+"";//客户 联系人电话
            if (kaiguan == 0) { // 如果在客户列表中,有一个客户跟进信息没录入,就没有  客户信息分数
                if (!"".equals(lxrdh.trim())) { // 如果客户信息里的 联系人电话不等于 空
                    // rs 为 true 时, 手机号正确 
                    rs =  lxrdh.matches("^1[3|4|5|6|7|8|9][0-9]\\d{4,8}$");
                    if (!rs){ // 如果录入的客户手机号-->"不正确",则没有分数
                        kaiguan = 1;
                    }
                } else {
                    kaiguan = 1;
                }
            }  
        }// 获取客户开拓分数end
        // if(true){  // 打印日志
        //     trigger.addErrorMessage(String.valueOf(khkt));
        // }
// 2. 获取 获取跟进记录 / 含重复拜访 begin
        // 获取 根据 客户id,判断有没有客户跟进信息
        List<CCObject> nrList = cs.cqlQuery("ywjhgjmx","select a.nr from ywjhgjmx a where a.is_deleted = '0' and (a.nr is not null or a.nr !='') and a.createbyid = '"+userid+"'" + datetimeone);
        // 需要区分是 按照 月 计算, 还是按照 季度计算   计算方式 --> 累加每个客户跟进记录之和*15 = 重复拜访的分数 (客户开拓)   
        if (nrList.size() > 0 ) { // 月度查询时
            khkt = (khkt + nrList.size() * 15) > 40 ? 40 : (khkt + nrList.size() * 15);
        }
        // 遍历完所有的客户, 且 手机号正确 &&　都有跟进信息
        if ((kaiguan == 0) && (accountList.size() != 0) && (nrList.size() >= accountList.size())) {
            // out.print("^^1" + kaiguan +"^^2"+accountList.size());
            khxx = 20;// 客户信息分数
        }// 获取 获取跟进记录 / 含重复拜访 end
//3. 日志分数获取 begin
        // 考勤
         String kaoqingCountSql = "select count(*) as num from attendance a where is_deleted='0' and ownerid='"+userid+"'" + datetimeone;
         List<CCObject> attendanceCount = cs.cqlQuery("attendance",kaoqingCountSql); 
         //out.print("^^2" + kaoqingCountSql);
         //获取有效考勤总数
         int dyycqts = attendanceCount.get(0).get("num")==null?0:Integer.valueOf(attendanceCount.get(0).get("num")+"");
         //日志sql
         String rzNumCountSql = "select count(*) as dryxnum  from DailyReport a where ownerid='"+userid+"'  and (jrclsy is not null or jrclsy !='') " + datetimeone;
         List<CCObject> DailyReportCount = cs.cqlQuery("DailyReport",rzNumCountSql);
         //out.print("^^3" + rzNumCountSql);
         // 有效日志数
         int yyxrzs = DailyReportCount.get(0).get("dryxnum")==null?0:Integer.valueOf(DailyReportCount.get(0).get("dryxnum")+"");
         double rz = 0.0; // 日志分数 
         if( yyxrzs>=dyycqts  && yyxrzs > 0) { // 有效日志数 大于 等于  有效考勤数 满分
         // out.print("^^5");
             rz = 20;
         }else{
            // out.print("^^6");
             rz = 0;
         }// 获取有效考勤总数 end  日志分数获取
// 4.  取 客户满意度 评分 begin
            //客户总数 前面已经取了客户集合
           // List<CCObject> Accountlist1 = cs.cqlQuery("Account","select count(1) as accountnum from Account a where  a.createbyid = '"+userid+"'  and is_deleted='0'" + datetime);
            //int Accountnum1 = Accountlist1.get(0).get("accountnum")==null?0:Integer.valueOf(Accountlist1.get(0).get("accountnum")+"");
            double khmyd = 0.0;
            // 月度内开拓了客户总数
            int Accountnum1 = accountList.size(); 
            // 5分好评的客户总数
            List<CCObject> Accountlist2 = cs.cqlQuery("Account","select count(*) as accountnum from Account a where a.createbyid = '"+userid+"' and is_deleted='0' and khmyd =5" + datetimeone);
            int Accountnum2 = Accountlist2.get(0).get("accountnum")==null?0:Integer.valueOf(Accountlist2.get(0).get("accountnum")+"");
            if(Accountnum1 != 0){ // 存在客户时
            //out.print("^^7");
                khmyd = (double) Math.round(20.00*((float)Accountnum2/Accountnum1) * 100) / 100; // 客户满意度分数
            }// 客户满意度 评分end
// 5. 总分 的汇总
            double countScore = khkt  + khmyd + rz + khxx; 
            countScore = (double) Math.round(countScore * 100) / 100;
            // 客户开拓总批数 (含跟进记录)
            int countaccount = accountList.size() + nrList.size();
            // 设置 个人创佣完成值
            for(CCObject cywcobj:cylist) {
                String ownerid = cywcobj.get("ownerid")==null?"":cywcobj.get("ownerid")+"";//ownerid 成交者
                if (userid.equals(ownerid)) {
                    List<CCObject> rylist = cs.cqlQuery("ryjx","select id,grcywcz from ryjx where nd='"+year+"' and yf ='"+month+"'  and bkhr = '"+userid+"' and is_deleted = '0' and recordtype='2020A3CA317261AEpAQJ'");
                    double grcywcz = rylist.get(0).get("grcywcz")==null?0:Double.valueOf(rylist.get(0).get("grcywcz")+"");// 获取人员绩效里个人创佣完成值
                    //实际创佣
                    double sjsy = cywcobj.get("sjsy")==null?0:Double.valueOf(cywcobj.get("sjsy")+"");
                    vipobj.put("grcywcz",grcywcz + sjsy);
                    cs.updateLt(vipobj);
                }
            }
            // 加入到 数据库中
            vipobj.put("khkt",khkt); // 客户开拓分数 (个人当月完成业绩数 字段)
            vipobj.put("rz",rz); // 日志分数
            vipobj.put("khmyd",khmyd); // 客户满意度分数
            vipobj.put("khxx",khxx); // 客户信息分数 (客户及时录入 字段)
            vipobj.put("khfz",countScore); // 考核总分 (考核分值 字段)
            // 辅助 展示
            vipobj.put("khs",countaccount); // 客户开拓总批数 (客户数 字段)
            vipobj.put("scqs",dyycqts); // 实出勤数 (实出勤数 字段)
            vipobj.put("yyxrzs",yyxrzs); // 有效日志总数 (当月日志数 字段)
            cs.updateLt(vipobj);
    }// end