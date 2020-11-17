<cc:page type="normal" style="standard" showSidebar="false" showHeader="false" />
<cc!>
/*
description:客户跟进接口
1.客户跟进接口:获取客户数据查询
version: 1.0
date:20191213
author:tom
log:
1.20200918 查询自己创建的客户信息
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
	type = request.getParameter("type")==null?"":encodeParameters(request.getParameter("type")+"");//客户id
    if("sel".equals(type)){ //查询录入
        // String datetime = request.getParameter("datetime")==null?"":encodeParameters(request.getParameter("datetime")+"");
        // datetime = datetime.replaceAll("%20", " ").replaceAll("%25", "%").replaceAll("%3E", ">").replaceAll("%3C", "<");  //%3
        //out.print("sel");
        //直接把组好的sql语句拿过来
        //业务员expresssql="select a.scpymc as scpymc,b.name as scpynm,c.name as usernm ,a.sms as sms,a.jxs as jxs,a.cjss as cjss, a.cjts as cjts,a.createbyid as createbyid,a.id as recid from cjqk a left join scpy b on a.scpymc=b.id left join ccuser c on a.createbyid=c.id where a.is_deleted='0' and a.recordtype ='201945938A54BAEfBWgh' " + datetime + "and a.createbyid='"+that.uid+ "' order by a.createdate desc";
        //项目经理expresssql="select a.scpymc as scpymc,b.name as scpynm,c.name as usernm ,a.sms as sms,a.jxs as jxs,a.cjss as cjss, a.cjts as cjts,a.createbyid as createbyid,a.id as recid from cjqk a left join scpy b on a.scpymc=b.id left join ccuser c on a.createbyid=c.id where a.is_deleted='0' and a.recordtype ='201945938A54BAEfBWgh' " + datetime + "and a.createbyid in "+userid_cond + " order by a.createdate desc";
        //其他expresssql="select a.scpymc as scpymc,b.name as scpynm,c.name as usernm ,a.sms as sms,a.jxs as jxs,a.cjss as cjss, a.cjts as cjts,a.createbyid as createbyid,a.id as recid from cjqk a left join scpy b on a.scpymc=b.id left join ccuser c on a.createbyid=c.id where a.is_deleted='0' and a.recordtype ='201945938A54BAEfBWgh' " + datetime + " order by a.createdate desc";
        String expresssql = request.getParameter("expresssql")==null?"":encodeParameters(request.getParameter("expresssql")+"");
        expresssql = expresssql.replaceAll("%20", " ").replaceAll("%25", "%").replaceAll("%3E", ">").replaceAll("%3C", "<");
        // out.print(expresssql);
        //记录id合集方便删除操作
        List<String> idlist = new ArrayList<String>();
        List<CCObject> scpy_l = new ArrayList<CCObject>();
        if("aaa20180D5809FBsQZab".equals(profid) || "aaa20180681351FmekUG".equals(profid) ||  "aaa2018E46BFCF90SnzU".equals(profid) || "aaa201854696184hq4oN".equals(profid) || "aaa000001".equals(profid) || "aaa20188BF02AA11vijc".equals(profid) ){
            out.print(profid);
            scpy_l = cs.cqlQuery("account",expresssql);
        }
        for(CCObject item:scpy_l){
            //out.print("cycle");
            //JSONObject ccuserjson= new JSONObject();
            String recid = item.get("id") == null?"":item.get("id")+""; // 客户id，用户获取跟进内容
            String name = item.get("name")==null?"":item.get("name")+ "";  //客户名称
            String lxrxm = item.get("lxrxm")==null?"":item.get("lxrxm")+ "";  //联系人姓名
            String khdj = item.get("khdj")==null?"":item.get("khdj")+ "";  //客户等级
            String smsj = item.get("smsj")==null?"":item.get("smsj")+ "";  //上门时间
            String lxrdh = item.get("lxrdh")==null?"":item.get("lxrdh")+ "";  //联系人电话
            String retype = item.get("recordtype")== null?"":item.get("recordtype")+""; // 类别的id
            String khlb = "";  //客户类别
            if ("2018525F215221DtlTXV".equals(retype)) {
                khlb = "进线客户";
            } else if("2018496272C934EtLhWs".equals(retype)) {
                khlb = "销售客户";
            } else if("20186166515AE4A8ZfOc".equals(retype)) {
                khlb = "租赁客户";
            } else if("2020F8FFFACC18DmPXQ1".equals(retype)) {
                khlb = "公寓客户";
            } else {
                khlb = "类型错误";
            }
            // String khlb = item.get("recortype").equals("2018525F215221DtlTXV") ? ("进线客户":item.get("recortype").equals("2018496272C934EtLhWs")?"销售客户":item.get("recortype").equals("20186166515AE4A8ZfOc")?"租赁客户":item.get("recortype").equals("2020F8FFFACC18DmPXQ1")?"公寓客户":"类型错误";  //客户类别
            String szxy = item.get("szxy")==null?"":item.get("szxy")+ "";  //所属行业
            String xbgqy = item.get("xbgqy")==null?"":item.get("xbgqy")+ "";  //原办公区域
            String xbgdx = item.get("xbgdx")==null?"":item.get("xbgdx")+ "";  //原办公大厦
            String rztj1 = item.get("rztj1")==null?"":item.get("rztj1")+ "";  //认知途径1
            String rztj2 = item.get("rztj2")==null?"":item.get("rztj2")+ "";  //认知途径2
            String zlyy = item.get("zlyy")==null?"":item.get("zlyy")+ "";  //租赁原因
            // String xqmj = item.get("xqmj")==null?"":item.get("xqmj")+ ""; //需求面积
            String xqmj = item.get("recordtype").equals("2020F8FFFACC18DmPXQ1") ?  (item.get("xqmj_gy")==null?"":item.get("xqmj_gy")+ "") : (item.get("xqmj")==null?"":item.get("xqmj")+ ""); //需求面积
            String zjyszl = item.get("zjyszl")==null?"":item.get("zjyszl")+ "";  //租金预算
            String khyxlx = item.get("khyxlx")==null?"":item.get("khyxlx")+ "";  //客户意向类型
            // String kxwt = item.get("kxwt")==null?"":item.get("kxwt")+ "";  //抗性问题
            String kxwt = item.get("recordtype").equals("2020F8FFFACC18DmPXQ1") ?  (item.get("kxwt")==null?"":item.get("kxwt")+ "") : (item.get("zlkxwt")==null?"":item.get("zlkxwt")+ ""); 
            // 取 跟进内容
            //String expresssql1="select a.nr from ywjhgjmx a where a.is_deleted = '0' and a.kh = '" +recid +"'"  + datetime + " order by a.createdate desc";
            String expresssql1="select a.nr from ywjhgjmx a where a.is_deleted = '0' and a.kh = '" +recid +"' order by a.createdate desc";
            JSONArray cjsjjsary = new JSONArray();
            List<CCObject> scsj_l = cs.cqlQuery("ywjhgjmx",expresssql1);
            if (scsj_l.isEmpty()) {
                continue;
            }
            rtnjbdata.put("name",name);
			rtnjbdata.put("lxrxm",lxrxm);
            rtnjbdata.put("khdj",khdj);
            rtnjbdata.put("smsj",smsj);
            rtnjbdata.put("lxrdh",lxrdh);
            rtnjbdata.put("khlb",khlb);
            rtnjbdata.put("szxy",szxy);
            rtnjbdata.put("xbgqy",xbgqy);
            rtnjbdata.put("xbgdx",xbgdx);
			rtnjbdata.put("rztj1",rztj1);
            rtnjbdata.put("rztj2",rztj2);
            rtnjbdata.put("zlyy",zlyy);
            rtnjbdata.put("xqmj",xqmj);
            rtnjbdata.put("zjyszl",zjyszl);
            rtnjbdata.put("khyxlx",khyxlx);
            rtnjbdata.put("kxwt",kxwt);
            rtnjbdata.put("recid",recid);
            idlist.add(recid);
            // 循环遍历跟进内容
            for(CCObject item1:scsj_l){
                String nr = item1.get("nr")==null?"":item1.get("nr")+ "";  //内容
                JSONObject cjsjjson= new JSONObject();
                cjsjjson.put("nr",nr);
                cjsjjsary.add(cjsjjson);
            }
            rtnjbdata.put("gklr",cjsjjsary);
            rtnjbdata.put("idlist",idlist);
            rtnMsg.add(rtnjbdata);
        }

        jsonmsg.put("success", true);
        jsonmsg.put("message", expresssql);
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
</html> -->
<cc:forward page="/platform/activity/task/getNodes.jsp"/>