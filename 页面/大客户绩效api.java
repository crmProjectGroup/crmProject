<cc:page type="normal" style="standard" showSidebar="false" showHeader="false" />
<cc!>
/*
description:大客户绩效Api

/**
* 将ISO-8859-1编码格式的字符串转码为UTF-8
*
* @param parameterValue
* @return
* @throws UnsupportedEncodingException
*/
private static String encodeParameters(String parameterValue)
throws UnsupportedEncodingException {
	if (parameterValue != null && parameterValue.length() > 0) {
		byte[] iso = parameterValue.getBytes("ISO-8859-1");
		if (parameterValue.equals(new String(iso, "ISO-8859-1"))) {
			parameterValue = new String(iso, "UTF-8");
			return parameterValue;
		}
	}
	return parameterValue;
}
</cc>
<cc>
// 疑问列表:
//    1:  在季度内  客户信息里  如果有 一个 客户联系电话录入错误 或者 一个跟进记录没入,是否 有分数
JSONObject jsonmsg = new JSONObject();
CCService cs = new CCService(userInfo);
java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd"); // 时间格式
String ksrq = request.getParameter("ksrq") == null ? "" :request.getParameter("ksrq"); // 起始时间
String jsrq = request.getParameter("jsrq") == null ? "" :request.getParameter("jsrq"); // 结束时间
// 时间参数截取
ksrq = ksrq.split("%")[0];
jsrq = jsrq.split("%")[0];
// 组装 时间 sql 片段
//String datetime = "and TO_CHAR(a.createdate,'YYYY-MM-dd')>=TO_CHAR(TO_DATE('"+ksrq+"','YYYY-MM-dd'),'YYYY-MM-dd') and TO_CHAR(a.createdate,'YYYY-MM-dd')<=TO_CHAR(TO_DATE('"+jsrq+"','YYYY-MM-dd'),'YYYY-MM-dd')";
String datetime = " and a.createdate >= str_to_date('"+ksrq+" 00:00:00', '%Y-%m-%d %H:%i:%s')  AND a.createdate<= str_to_date('"+jsrq+" 23:59:59', '%Y-%m-%d %H:%i:%s')";
// 获取登陆人的id
String dengluId = userInfo.getUserId(); // 获取登陆人的id
String dkhProfileXmjl = "aaa2018A4671C21Hu0yi"; // 产品运营中心项目经理简档
String dkhProfile = "aaa201858C360ADNceRX"; // 产品运营中心人员简档
// text测试
// String dengluId = "005201827888B25tm2iq"; // 
// String dkhProfileXmjl = "aaa2018A38306ED9syVe"; // 产品运营中心项目经理简档
// String dkhProfile = "aaa201723453E5EBNtzU"; // 产品运营中心人员简档
// text测试 end
JSONArray rtnMsg = new JSONArray(); // 存放所有数据集合
JSONObject rtnjbdata = new JSONObject(); // 存放单个数据对象
double khkt = 0.0; // 客户开拓分数
double khxx = 0.0;// 客户信息分数
int kaiguan = 0; // 如果有一个客户跟进信息没有录入,就没有 khxx  这个分数
double khzj = 0.0; // 客户转介分数 --> 客户联动次数 * 5  不超过 30 分
double khmyd = 0.0; // 客户满意度分数 --> 统计之后获取平均分
double sczy = 10.0; // 市场资源分数(由于不好控制一个季度的每个星期的时间)   
try {
        List<CCObject> ccuserList = new ArrayList<CCObject>();
        // 获取登陆用户的 简档List 完成
         //out.print("^^0"+dengluId + "");
        List<CCObject> profileId = cs.cqlQuery("ccuser","select profile from ccuser where id = '"+dengluId+"' and isusing = '1' and is_deleted = '0'");
         
        if (profileId.size() == 0) {
            throw new RuntimeException("系统中没有此用户:" + dengluId);
        } else {
            String profile = profileId.get(0).get("profile")==null?"":profileId.get(0).get("profile")+"";
            //out.print("^^1"+profile + "");
            if ("aaa000001".equals(profile)) { // 如果是管理员, 则获取简档为:产品运营中心简档的所有人员
                String sql = "select id,name from ccuser where isusing = '1' and is_deleted = '0' and profile =  '"+dkhProfile+"'";
               // out.print("^^2"+sql);
                ccuserList = cs.cqlQuery("ccuser",sql);
            } else if(dkhProfileXmjl.equals(profile)) { // 如果是产品运营经理简档 (大客户)
                // 如果登陆的是项目经理: 则 获取 项目经理 下 的人员
                ccuserList = cs.cqlQuery("ccuser","select id,name from ccuser where manager = '"+dengluId+"' and isusing = '1' and is_deleted = '0' and profile =  '"+dkhProfile+"'");
            } else if (dkhProfile.equals(profile)) { // 如果是产品运营简档 人员
                // 获取登录人 下 是否有 人员 如果有 (则为项目经理) 否则  为 大客户业务员  (通过简档 为 profile =  'aaa201858C360ADNceRX'  产品运营  and  isusing = '1'  用户是否启用  1 启用 / 0 离职)
                ccuserList = cs.cqlQuery("ccuser","select id,name from ccuser where id = '"+dengluId+"' and isusing = '1' and is_deleted = '0' and profile =  '"+dkhProfile+"'");
            }
        }
        for (CCObject uid:ccuserList) {
            String userid = uid.get("id") == null?"":uid.get("id")+"";
            String name = uid.get("name") == null?"":uid.get("name")+"";
            String accountSql = "select id,lxrdh  from account a where a.is_deleted = '0' and a.createbyid = '"+userid+"'" + datetime ;
           // out.print("获取登陆id: "+ccuserList.size() + "人员,人员id为:" + userid + name);
            List<CCObject> accountList = cs.cqlQuery("account",accountSql); // 取出 在季度内 创建了几批客户
            // out.print("(" + accountSql+")");
            // out.print("开拓了多少个项目" + accountList.size());
            for (CCObject acc:accountList) {
                if(khkt < 30) { // 判断客户开拓分数不能超过 30 分
                    khkt += 5; //累加 客户 开拓分数
                }
                
                boolean rs = false; // 判断电话号是否正确
                String id = acc.get("id")==null?"":acc.get("id")+""; // 客户的id
                //String khgjxx ="select a.nr from ywjhgjmx a where a.is_deleted = '0' and (a.nr is not null or a.nr !='') and a.createbyid = '"+userid+"'" + datetime; // 获取季度内的客户跟进信息
                //List<CCObject> khgjList = cs.cqlQuery("ywjhgjmx",khgjxx); // 取 不区分 客户  获取季度内所有的跟进记录
                // 获取 根据 客户id,判断有没有客户跟进信息
                List<CCObject> nrList = cs.cqlQuery("ywjhgjmx","select a.nr from ywjhgjmx a where a.kh = '"+id+"' and a.is_deleted = '0' and (a.nr is not null or a.nr !='') and a.createbyid = '"+userid+"'" + datetime);
                String lxrdh = acc.get("lxrdh")==null?"":acc.get("lxrdh")+"";//客户 联系人电话
                if (kaiguan == 0) { // 如果在客户列表中,有一个客户跟进信息没录入,就没有  客户信息分数
                    if (!"".equals(lxrdh.trim())) { // 如果客户信息里的 联系人电话不等于 空
                        // rs 为 true 时, 手机号正确 
                        rs =  lxrdh.matches("^1[3|4|5|6|7|8|9][0-9]\\d{4,8}$");
                    } else {
                        kaiguan = 1;
                    }
                }  
                // 累加每个客户跟进记录之和*5 = 重复拜访的分数 (客户开拓)
                if (nrList.size() != 0) {
                    khkt  = (khkt + nrList.size() * 5) > 30 ? 30 : (khkt + nrList.size() * 5);   // 有跟进记录, 算 重复拜访  + 5 分
                }
                // 获取季度内的跟进记录
                // if (khgjList.size() != 0) {
                //     khkt  = (khkt + khgjList.size() * 5) > 30 ? 30 : (khkt + khgjList.size() * 5);   // 有跟进记录, 算 重复拜访  + 5 分
                // }
            }
            // 遍历完所有的客户, 且 手机号正确
            if ((kaiguan == 0) && (accountList.size() != 0)) {
                khxx = 10;// 客户信息分数
            }
            // 获取客户转介数量, 根据创建人的id 和 审批通过 一般代理转介
            String khzjSql = "select count(*) as num from wbzj a where is_deleted='0' and zt='审批通过' and a.createbyid = '"+userid+"'" + datetime;
            List<CCObject> khzjNum = cs.cqlQuery("wbzj",khzjSql);
            //out.print("^^2" + khzjSql);
            // 无需判断空的情况, 如论如何都有值
            int num = khzjNum.get(0).get("num")==null?0:Integer.valueOf(khzjNum.get(0).get("num")+"");// 审批通过 "客户转介"条数
            //out.print("^^2-3"+ num);
            if (num > 6) { // 超过 6 条 满分
           // out.print("^^3");
                khzj = 30;
            } else {
               // out.print("^^4");
                khzj = num * 5;
            }
            // 获取有效考勤总数 begin 日志分数获取
            String kaoqingCountSql = "select count(*) as num from attendance a where is_deleted='0' and ownerid='"+userid+"'" + datetime;
            List<CCObject> attendanceCount = cs.cqlQuery("attendance",kaoqingCountSql); 
            //out.print("^^2" + kaoqingCountSql);
            int dyycqts = attendanceCount.get(0).get("num")==null?0:Integer.valueOf(attendanceCount.get(0).get("num")+"");
            //有效日志数
            String rzNumCountSql = "select count(*) as dryxnum  from DailyReport a where ownerid='"+userid+"'  and (jrclsy is not null or jrclsy !='') " + datetime;
            List<CCObject> DailyReportCount = cs.cqlQuery("DailyReport",rzNumCountSql);
            //out.print("^^3" + rzNumCountSql);
            int yyxrzs = DailyReportCount.get(0).get("dryxnum")==null?0:Integer.valueOf(DailyReportCount.get(0).get("dryxnum")+"");
            double rz = 0.0;
            if( yyxrzs>=dyycqts && dyycqts > 0 && yyxrzs > 0) { // 有效日志数 大于 等于  有效考勤数 满分
            // out.print("^^5");
                rz = 5;
            }else{
               // out.print("^^6");
                rz = 0;
            }// 获取有效考勤总数 end  日志分数获取
            
            // 取 客户满意度 评分 begin
            //客户总数 前面已经取了客户集合
           // List<CCObject> Accountlist1 = cs.cqlQuery("Account","select count(1) as accountnum from Account a where  a.createbyid = '"+userid+"'  and is_deleted='0'" + datetime);
            //int Accountnum1 = Accountlist1.get(0).get("accountnum")==null?0:Integer.valueOf(Accountlist1.get(0).get("accountnum")+"");
            int Accountnum1 = accountList.size(); // 季度内开拓了客户总数
            //好评的客户数
            List<CCObject> Accountlist2 = cs.cqlQuery("Account","select count(*) as accountnum from Account a where a.createbyid = '"+userid+"' and is_deleted='0' and khmyd =5" + datetime);
            int Accountnum2 = Accountlist2.get(0).get("accountnum")==null?0:Integer.valueOf(Accountlist2.get(0).get("accountnum")+"");
            if(Accountnum1 != 0){ // 存在客户时
            //out.print("^^7");
                khmyd = (double) Math.round(15.00*((float)Accountnum2/Accountnum1) * 100) / 100; // 客户满意度分数
            }   
            // 客户满意度 评分end
            double countScore = khkt + khzj + khmyd + rz + sczy + khxx;
            countScore = (double) Math.round(countScore * 100) / 100;
            //out.print("^^8 = " + countScore);
            rtnjbdata.put("name",name); // 被考核人姓名
            rtnjbdata.put("khkt",khkt); // 客户开拓分数
            rtnjbdata.put("khzj",khzj); // 客户转介分数
            rtnjbdata.put("khmyd",khmyd); // 客户满意度分数
            rtnjbdata.put("rz",rz); // 日志分数
            rtnjbdata.put("sczy",sczy); // 市场资源分数
            rtnjbdata.put("khxx",khxx); // 客户信息分数
            rtnjbdata.put("countScore",countScore); // 总分
            rtnMsg.add(rtnjbdata);
        }

        jsonmsg.put("success", true);
        jsonmsg.put("data", rtnMsg);
} catch (Exception e) {
	jsonmsg.put("success", false);

	jsonmsg.put("message", e.getMessage());
} 
request.setAttribute("jsonmsg", jsonmsg.toString());
</cc>
<!--<html>
	hello
</html>-->
<cc:forward page="/platform/activity/task/getNodes.jsp"/>
