<cc:page type="normal" style="standard" showSidebar="false" showHeader="false" />
<cc!>
/*
description:朗峻盘客表api  20210428

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
JSONObject jsonmsg = new JSONObject();
CCService cs = new CCService(userInfo);
String type = request.getParameter("type") == null ? "" :request.getParameter("type"); // 类型
String sja = ""; //表格中数据
try {
    // 获取单个业务员时间范围内所有的跟进基础信息  || 获取单个客户的id
    if ("genjinAll".equals(type) || "genjin".equals(type)) { 
        String expressions = request.getParameter("expressions") == null ? "" :request.getParameter("expressions"); // 查看sql
        List<CCObject> genjinlist = cs.cqlQuery("ywjhgjmx",expressions); // 获取相应的数据
        JSONArray gjjsonarray = JSONArray.fromObject(genjinlist);
	    sja=gjjsonarray.toString();
        out.print(sja);
        jsonmsg.put("success", true);
        jsonmsg.put("data", sja);
    } else if ("accbyuid".equals(type)) { // 根据userid，获取客户详情
        String datetime = request.getParameter("datetime") == null ? "" :request.getParameter("datetime"); // 时间范围
        String sinuid = request.getParameter("sinuid") == null ? "" :request.getParameter("sinuid"); // 单个业务员id
        String accbyuidsql = "select a.id,a.name,date_format(a.smsj, '%Y-%m-%d %H:%i:%s') as smsj,a.ybgqy,a.lxrxm,a.xjzxq,a.gyClientType,a.yzkhqk,a.gjfk,a.sfrg,a.familyStructure,a.gzqy,a.gzdd,a.gzqybz,a.lfcs,a.jzqy,a.jzdd,a.jzqybz,a.lcyx,a.accouAna,a.sfgzwlxx,a.xqah,a.gyNewCoChannel,a.rtdian,a.rentongdian,a.jgyq,a.gyResistance,a.gzcpmjd,a.zymd,a.recordtype from account a  where a.createbyid= '"+sinuid+"'   and a.xmmc='a0520206562E5ECNVIbC' "+datetime+"  order by a.createdate desc";
        List<CCObject> accbyuidlist = cs.cqlQuery("account",accbyuidsql); // 业务员所有客户数据
        JSONArray accbyuidarr = JSONArray.fromObject(accbyuidlist);
	    sja=accbyuidarr.toString();
        out.print(sja);
        jsonmsg.put("success", true);
        jsonmsg.put("data", sja);
    } else if ("accbyproid".equals(type)) {
        String datetime = request.getParameter("datetime") == null ? "" :request.getParameter("datetime"); // 时间范围
        String projectid = request.getParameter("projectid") == null ? "" :request.getParameter("projectid"); // 单个业务员id
        String projectidsql = "select a.id as aid,a.name as aname,date_format(a.smsj, '%Y-%m-%d %H:%i:%s') as smsj,a.ybgqy,a.lxrxm,a.xjzxq,a.gyClientType,a.yzkhqk,a.gjfk,a.sfrg,a.familyStructure,a.gzqy,a.gzdd,a.gzqybz,a.lfcs,a.jzqy,a.jzdd,a.jzqybz,a.lcyx,a.accouAna,a.sfgzwlxx,a.xqah,a.gyNewCoChannel,a.rtdian,a.rentongdian,a.jgyq,a.gyResistance,a.gzcpmjd,a.zymd,a.recordtype,u.id as uid,u.name as uname from account a left join ccuser u on a.createbyid=u.id  where a.xmmc='a0520206562E5ECNVIbC' "+datetime+"  order by a.createdate desc";
        List<CCObject> projectidlist = cs.cqlQuery("account",projectidsql); // 项目所有客户数据
        out.print(projectidsql);
        JSONArray projectidarr = JSONArray.fromObject(projectidlist);
	    sja=projectidarr.toString();
        jsonmsg.put("success", true);
        jsonmsg.put("data", sja);
    } else if ("ljghacc".equals(type)) {
        // 只获取朗峻广场项目，并且只获取 公寓进线客户 和 新公寓布局客户
        String ljghaccsql = "SELECT a.id,a.name,date_format(a.smsj, '%Y-%m-%d %H:%i:%s') as smsj,a.ybgqy,a.lxrxm,a.xjzxq,a.gyClientType,a.yzkhqk,a.gjfk,a.sfrg,a.familyStructure,a.gzqy,a.gzdd,a.gzqybz,a.lfcs,a.jzqy,a.jzdd,a.jzqybz,a.lcyx,a.accouAna,a.sfgzwlxx,a.xqah,a.gyNewCoChannel,a.rtdian,a.rentongdian,a.jgyq,a.gyResistance,a.gzcpmjd,a.zymd,CASE a.recordtype when '20186166515AE4A8ZfOc' then '租赁客户' when '2018525F215221DtlTXV' then '进线客户'  when '2020F8FFFACC18DmPXQ1' then '公寓客户'  when '202106851B81E2CJ2pos' then '公寓进线'  when '2021A6974399AE59RKpF' then '新公寓客户'  else '销售客户' end as recordtype FROM Account a inner join ccuser b on a.ownerid = b.id WHERE a.createbyid <> a.ownerid and a.ghkh = '是' and a.ownerid <> '005201852146611JQCTr' and b.profile = 'aaa2018A38306ED9syVe'  and a.lxrdh not like '%0000%' and a.is_deleted = '0' and xmmc='a0520206562E5ECNVIbC' and  a.recordtype in ('202106851B81E2CJ2pos','2021A6974399AE59RKpF') and a.id not in (SELECT khmc FROM Opportunity,account a WHERE khmc = a.id AND jieduan = '成交')";
        List<CCObject> accbyghdlist = cs.cqlQuery("account",ljghaccsql); // 朗峻公海客户
        JSONArray accbyghdarr = JSONArray.fromObject(accbyghdlist);
	    sja=accbyghdarr.toString();
        jsonmsg.put("success", true);
        jsonmsg.put("data", sja);
    } else if ("getuserlist".equals(type)) {
        // 获取朗峻销售小组
        String ljghaccsql = "select c.id,c.name from ProjectSaleGroup g left join ccuser c on g.xmxsy=c.id where g.xmmc='a0520206562E5ECNVIbC' and g.is_deleted = '0' and c.isusing='1'";
        List<CCObject> accbyghdlist = cs.cqlQuery("ProjectSaleGroup",ljghaccsql); // 朗峻公海客户
        JSONArray accbyghdarr = JSONArray.fromObject(accbyghdlist);
	    sja=accbyghdarr.toString();
        jsonmsg.put("success", true);
        jsonmsg.put("data", sja);
    } else if ("setljgkacc".equals(type)) {
        String accidlist = request.getParameter("accidlist") == null ? "" :request.getParameter("accidlist"); // 取客户集合
        String userid = request.getParameter("userid") == null ? "" :request.getParameter("userid"); // 业务员id
        // 获取朗峻销售小组
        String setljgksql = "update account set ghkh='否',ownerid='"+userid+"'  where id in "+accidlist+"') and is_deleted = '0' and xmmc='a0520206562E5ECNVIbC' and recordtype in ('2021A6974399AE59RKpF','202106851B81E2CJ2pos')";
        cs.cqlQuery("account",setljgksql); // 朗峻公海客户
        jsonmsg.put("success", true);
        jsonmsg.put("data", setljgksql);
    }
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