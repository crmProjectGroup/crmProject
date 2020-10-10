<cc:page type="normal" style="standard" showSidebar="false" showHeader="false" />
<cc!>
/*
description:结算开票拆佣接口 
version: 1.0
date:20190614
author:tom
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
//String qysj = request.getParameter("qysj")==null?"":encodeParameters(request.getParameter("qysj")+"");//签约时间
java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

//cal.setTime(df.parse(qysj));
//int year = cal.get(Calendar.YEAR);//当前年份
//int month = cal.get(Calendar.MONTH)+1;//当前月份
//String profileid = userInfo.getProfileId();
String userid = userInfo.getUserId();
//Date now = new Date();
String type = "";
Double dlfjsbl = 0.0;
String oppoid = "";
String bz = "";
//out.print("<script>alert(oppoid);</script>");

JSONArray rtnMsg = new JSONArray();
JSONObject rtnjbdata = new JSONObject();

String msg = "";

try {
	type = request.getParameter("type")==null?"":encodeParameters(request.getParameter("type")+"");//客户id
	out.print(type);
    
	
    //String cymx = request.getParameter("cymx")==null?"":encodeParameters(request.getParameter("cymx")+"");
    //String cymx = request.getParameter("cymx")==null?"":URLDecoder.decode(request.getParameter("cymx")+"");
    String cymx = request.getParameter("cymx")==null?"":java.net.URLDecoder.decode(request.getParameter("cymx")+"","UTF-8");
    //String cymx = request.getParameter("cymx")==null?"":request.getParameter("cymx")+"";
    out.print(cymx);
    out.print("didi");
    //1.把字符串类型的json数组对象转化JSONArray
    JSONArray jsonary=JSONArray.fromObject(cymx);

    for(int i=0;i<jsonary.size();i++){
            JSONObject cymx_0 = jsonary.getJSONObject(i); 
			//out.print(cydx.get("cydx"));
			String kpsq = cymx_0.getString("kpsq");
			Double cjzj = Double.valueOf(cymx_0.getString("cjzj"));
			Double jsbl = Double.valueOf(cymx_0.getString("jsbl"));
			Double jsyj = Double.valueOf(cymx_0.getString("jsyj"));
			String hkzt = cymx_0.getString("hkzt");
			String jssj = cymx_0.getString("jssj");
			String cydx = cymx_0.getString("cydx");
			String ownerid = cymx_0.getString("ownerid");
			Double fybl = Double.valueOf(cymx_0.getString("fybl"));
			String yjlx = cymx_0.getString("yjlx");
			//获取业务机会,不再直接获取比例和金额
			String ywjh = cymx_0.getString("ywjh");
			Double tcbl =0.0;
			Double khds =0.0;
			List<CCObject> Opportunity = cs.cqlQuery("Opportunity","select recordtype,xmmc from Opportunity where id='"+ywjh+"' and is_deleted='0' ");
			String recordtype = Opportunity.get(0).get("recordtype")==null?"":Opportunity.get(0).get("recordtype")+"";
			List<CCObject> ccuser = cs.cqlQuery("ccuser","select zjzd from ccuser where id='"+ownerid+"' and is_deleted='0' ");
			String zjzd = ccuser.get(0).get("zjzd")==null?"":ccuser.get(0).get("zjzd")+"";
			if("2018BD60B25D1A2mLTd7".equals(recordtype)){ //租赁
				if("业务代表".equals(zjzd)) { //csbm ywy3zl
					List<CCObject> rxhcsbn = cs.cqlQuery("rxhcsbn ","select value,jxkhds from rxhcsbn where csbm='ywy3zl' and is_deleted='0' ");
					tcbl = rxhcsbn.get(0).get("value")==null?0.0:Double.parseDouble(rxhcsbn.get(0).get("value")+"");
					khds = rxhcsbn.get(0).get("jxkhds")==null?0.0:Double.parseDouble(rxhcsbn.get(0).get("jxkhds")+"");
				} else if("高级业务代表".equals(zjzd)){
					List<CCObject> rxhcsbn = cs.cqlQuery("rxhcsbn ","select value,jxkhds from rxhcsbn where csbm='ywy2zl' and is_deleted='0' ");
					tcbl = rxhcsbn.get(0).get("value")==null?0.0:Double.parseDouble(rxhcsbn.get(0).get("value")+"");
					khds = rxhcsbn.get(0).get("jxkhds")==null?0.0:Double.parseDouble(rxhcsbn.get(0).get("jxkhds")+""); 
				} else if("资深业务代表".equals(zjzd)){
					List<CCObject> rxhcsbn = cs.cqlQuery("rxhcsbn ","select value,jxkhds from rxhcsbn where csbm='ywy1zl' and is_deleted='0' ");
					tcbl = rxhcsbn.get(0).get("value")==null?0.0:Double.parseDouble(rxhcsbn.get(0).get("value")+"");
					khds = rxhcsbn.get(0).get("jxkhds")==null?0.0:Double.parseDouble(rxhcsbn.get(0).get("jxkhds")+""); 
				} else if("项目经理".equals(zjzd)){
					List<CCObject> rxhcsbn = cs.cqlQuery("rxhcsbn ","select value,jxkhds from rxhcsbn where csbm='xmjlzl' and is_deleted='0' ");
					tcbl = rxhcsbn.get(0).get("value")==null?0.0:Double.parseDouble(rxhcsbn.get(0).get("value")+"");
					khds = rxhcsbn.get(0).get("jxkhds")==null?0.0:Double.parseDouble(rxhcsbn.get(0).get("jxkhds")+""); 
				}
			} else { //销售 20183DD9667FA14v9YSK
				if("业务代表".equals(zjzd)) { //csbm ywy3xs
					List<CCObject> rxhcsbn = cs.cqlQuery("rxhcsbn ","select value,jxkhds from rxhcsbn where csbm='ywy3xs' and is_deleted='0' ");
					tcbl = rxhcsbn.get(0).get("value")==null?0.0:Double.parseDouble(rxhcsbn.get(0).get("value")+"");
					khds = rxhcsbn.get(0).get("jxkhds")==null?0.0:Double.parseDouble(rxhcsbn.get(0).get("jxkhds")+"");
				} else if("高级业务代表".equals(zjzd)){
					List<CCObject> rxhcsbn = cs.cqlQuery("rxhcsbn ","select value,jxkhds from rxhcsbn where csbm='ywy2xs' and is_deleted='0' ");
					tcbl = rxhcsbn.get(0).get("value")==null?0.0:Double.parseDouble(rxhcsbn.get(0).get("value")+"");
					khds = rxhcsbn.get(0).get("jxkhds")==null?0.0:Double.parseDouble(rxhcsbn.get(0).get("jxkhds")+""); 
				} else if("资深业务代表".equals(zjzd)){
					List<CCObject> rxhcsbn = cs.cqlQuery("rxhcsbn ","select value,jxkhds from rxhcsbn where csbm='ywy1xs' and is_deleted='0' ");
					tcbl = rxhcsbn.get(0).get("value")==null?0.0:Double.parseDouble(rxhcsbn.get(0).get("value")+"");
					khds = rxhcsbn.get(0).get("jxkhds")==null?0.0:Double.parseDouble(rxhcsbn.get(0).get("jxkhds")+""); 
				} else if("项目经理".equals(zjzd)){
					List<CCObject> rxhcsbn = cs.cqlQuery("rxhcsbn ","select value,jxkhds from rxhcsbn where csbm='xmjlxs' and is_deleted='0' ");
					tcbl = rxhcsbn.get(0).get("value")==null?0.0:Double.parseDouble(rxhcsbn.get(0).get("value")+"");
					khds = rxhcsbn.get(0).get("jxkhds")==null?0.0:Double.parseDouble(rxhcsbn.get(0).get("jxkhds")+""); 
				}
			}

			String xmmc = Opportunity.get(0).get("xmmc")==null?"":Opportunity.get(0).get("xmmc")+"";

			//Double tcbl = Double.valueOf(cymx_0.getString("tcbl"));
			//Double llcyje = Double.valueOf(cymx_0.getString("llcyje"));
			Double llcyje = jsyj * fybl/100 * tcbl/100 ;//结算佣金*分佣的比例*提成比例
			

			CCObject dlfcymxb = new CCObject("dlfcymxb");
			dlfcymxb.put("kpsq",kpsq);
			dlfcymxb.put("cjzj",cjzj); 
			dlfcymxb.put("jsbl",jsbl); //结算比例
			dlfcymxb.put("jsyj",jsyj); 
			dlfcymxb.put("hkzt",hkzt); 
			dlfcymxb.put("jssj",jssj);
			dlfcymxb.put("cydx",cydx);
			dlfcymxb.put("spzt","审批状态");
			dlfcymxb.put("ownerid",ownerid);
			dlfcymxb.put("fybl",fybl); //分佣合作比例

        	dlfcymxb.put("yjlx",yjlx);
			dlfcymxb.put("tcbl",tcbl);//对应提成点数
			dlfcymxb.put("khds",khds); //考核部分
			dlfcymxb.put("llcyje",llcyje); //理论拆佣金额
			dlfcymxb.put("ywjh",ywjh); //ywjh 业务机会
			dlfcymxb.put("xmmc",xmmc); //ywjh 业务机会
			dlfcymxb.put("createbyid",userid);  //createbyid赋值为项目经理
			ServiceResult sr = cs.insert(dlfcymxb);
			out.print(sr.toString());

    }
	jsonmsg.put("success", true);
    //jsonmsg.put("message", jsid);

	

} catch (Exception e) {
	jsonmsg.put("success", false);

	jsonmsg.put("message", e.getMessage());
} 



request.setAttribute("jsonmsg", jsonmsg.toString());
</cc>
<html>
	hello
</html>