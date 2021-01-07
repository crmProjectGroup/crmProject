<cc:page type="normal" style="standard" showSidebar="false" showHeader="false" />
<cc!>
/*
description:会议记录接口
1.根据时间和项目查询 会议记录  和 附件
version: 1.0
date:20201228
author:tom
log:
1.20201228 查询相对应权限的会议记录 和 附件
*/

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

java.util.Calendar cal = java.util.Calendar.getInstance();
java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
String userid = userInfo.getUserId();
String profid = userInfo.getProfileId();//getProfileId当前登录用户的简档id 
//Date now = new Date();
String type = "";

JSONArray rtnMsg = new JSONArray();
JSONObject rtnjbdata = new JSONObject();

String msg = "";

try {
	type = request.getParameter("type")==null?"":encodeParameters(request.getParameter("type")+"");//获取操作类型
    if("selhuiyi".equals(type)){ //查询会议记录
        String timebegin = request.getParameter("timebegin")==null?"":encodeParameters(request.getParameter("timebegin")+""); // 获取起始时间
        String timeend = request.getParameter("timeend")==null?"":encodeParameters(request.getParameter("timeend")+""); // 获取结束时间
        // 组装 时间 sql片段
        String datetime = " and a.createdate >= str_to_date('"+timebegin+"', '%Y-%m-%d %H:%i:%s')  AND a.createdate <= str_to_date('"+timeend+"', '%Y-%m-%d %H:%i:%s') order by a.createdate desc";
        String projectId = request.getParameter("projectId")==null?"":encodeParameters(request.getParameter("projectId")+""); // 获取项目的id
        String fjvalue = request.getParameter("fjvalue")==null?"":encodeParameters(request.getParameter("fjvalue")+""); // 获取是否有附件的条件
        // 组装 项目id 的sql 片段
        String prosql = "";
        if (!"".equals(projectId)) {
            prosql = " and a.xmmc = '" + projectId+"'";
        }
        // 组装 有无附件 的sql片段
        String fjsql = "";
        if (!"".equals(fjvalue)) {
            if ("有附件".equals(fjvalue)) {
                fjsql = " and att.id is not null";
            } else {
                fjsql = " and att.id is null ";
            }
        }
        // 开始组装获取数据的sql (1. 先获取会议纪要的数据)
        //String xmhyjlsql = "select a.id as hyid,a.xmmc,a.createbyid,a.name,a.hyztxx,a.hynr,a.hyztxx,a.hyzt, att.id as attid from xmhyjl a  left join Attachement  att on a.id = att.relatedid  where a.is_deleted = '0'  and att.is_deleted = '0' "+ prosql + xmjlsql + datetime;
        String xmhyjlsql = "";
        if("aaa20180D5809FBsQZab".equals(profid) || "aaa20180681351FmekUG".equals(profid) || "aaa2018E46BFCF90SnzU".equals(profid) || "aaa201854696184hq4oN".equals(profid) || "aaa000001".equals(profid)){
            // out.print("1^"+profid);
            xmhyjlsql = "select a.id as hyid,a.name as hyname,att.id as fjid,a.xmmc,a.createbyid,a.createdate,a.hyztxx,a.hynr,a.hyzt,c.name as cname,c.id as cid,p.name as pname from xmhyjl a  left join Attachement att  on att.relatedid = a.id join project p on a.xmmc = p.id join ccuser c on p.xmjl = c.id where a.is_deleted='0'  and p.is_deleted='0' and c.is_deleted='0' and c.isusing='1' "+ prosql + fjsql + datetime;
        } else {
            // out.print("2^"+profid);
            xmhyjlsql = "select a.id as hyid,a.name as hyname,att.id as fjid,a.xmmc,a.createbyid,a.createdate,a.hyztxx,a.hynr,a.hyzt,c.name as cname,c.id as cid,p.name as pname from xmhyjl a  left join Attachement att  on att.relatedid = a.id join project p on a.xmmc = p.id join ccuser c on p.xmjl = c.id where a.is_deleted='0'  and p.is_deleted='0' and c.is_deleted='0' and c.isusing='1' and p.xmjl = '"+userid+"' "+ prosql + fjsql + datetime;
        }
        List<CCObject> xmhyjList =  cs.cqlQuery("xmhyjl",xmhyjlsql);
        for(CCObject xmhyobj:xmhyjList){
            String hyid = xmhyobj.get("hyid") == null?"":xmhyobj.get("hyid")+""; // 会议id
            String hyname = xmhyobj.get("hyname") == null?"":xmhyobj.get("hyname")+""; // 会议编号
            String xmmcid = xmhyobj.get("xmmc")==null?"":xmhyobj.get("xmmc")+ "";  //项目id
            String xmname = xmhyobj.get("pname")==null?"":xmhyobj.get("pname")+ "";  //项目名称
            String hyztxx = xmhyobj.get("hyztxx") == null?"":xmhyobj.get("hyztxx")+""; // 会议主题下拉选值
            String hyzt = xmhyobj.get("hyzt")==null?"":xmhyobj.get("hyzt")+ "";  //会议主题文本框值
            String hynr = xmhyobj.get("hynr")==null?"":xmhyobj.get("hynr")+ "";  //会议内容
            String hycreatedate = xmhyobj.get("createdate")==null?"":xmhyobj.get("createdate")+ "";  //会议创建时间
            String cid = xmhyobj.get("cid")==null?"":xmhyobj.get("cid")+ "";  // 所有人的id
            String cname = xmhyobj.get("cname")==null?"":xmhyobj.get("cname")+ "";  // 所有人的name
            String fjid = xmhyobj.get("fjid")==null?"":xmhyobj.get("fjid")+ "";  // 附件id
            
            rtnjbdata.put("hyid",hyid);
            rtnjbdata.put("hyname",hyname);
            rtnjbdata.put("xmmcid",xmmcid);
            rtnjbdata.put("pname",xmname);
            rtnjbdata.put("hyztxx",hyztxx);
            rtnjbdata.put("hyzt",hyzt);
            rtnjbdata.put("hynr",hynr);
            rtnjbdata.put("hycreatedate",hycreatedate);
            rtnjbdata.put("cid",cid);
            rtnjbdata.put("cname",cname);
            rtnjbdata.put("relatedid",fjid);
            if (!"".equals(fjid)) { // 如果有附件, 附件一列展示 查看   两字
                rtnjbdata.put("accessory","查看");
            } else {
                rtnjbdata.put("accessory","");
            }
            rtnMsg.add(rtnjbdata);
        }

        jsonmsg.put("success", true);
        jsonmsg.put("message", xmhyjlsql);
        jsonmsg.put("data", rtnMsg);

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