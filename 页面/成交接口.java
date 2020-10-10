<cc:page type="normal" style="standard" showSidebar="false" showHeader="false" />
<cc!>
/*
description:成交接口
1.查询销售小组的数据;
version: 1.0
date:20200821
author:tom
log:
1.20200821 新建成交单位对象
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

//java.util.Calendar cal = java.util.Calendar.getInstance();
//String qysj = request.getParameter("qysj")==null?"":encodeParameters(request.getParameter("qysj")+"");//签约时间
//java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

//cal.setTime(df.parse(qysj));
//int year = cal.get(Calendar.YEAR);//当前年份
//int month = cal.get(Calendar.MONTH)+1;//当前月份
//String profileid = userInfo.getProfileId();
String userid = userInfo.getUserId();
//Date now = new Date();
String type = "";
// Double dlfjsbl = 0.0;
// String oppoid = "";
// String bz = "";
//out.print("<script>alert(oppoid);</script>");

JSONArray rtnMsg = new JSONArray();
JSONObject rtnjbdata = new JSONObject();

String msg = "";

try {
	type = request.getParameter("type")==null?"":encodeParameters(request.getParameter("type")+"");
	 
	if("adddw".equals(type)){ //添加成交单位
        //项目id
        String xmid = request.getParameter("xmid")==null?"":encodeParameters(request.getParameter("xmid")+"");
        out.print("xmid:"+xmid);

        String oppoid = request.getParameter("oppoid")==null?"":encodeParameters(request.getParameter("oppoid")+"");
        out.print("oppoid:"+oppoid);

        String ldid = request.getParameter("ldid")==null?"":encodeParameters(request.getParameter("ldid")+"");
        out.print("ldid:"+ldid);

        String ldnm = request.getParameter("ldnm")==null?"":encodeParameters(request.getParameter("ldnm")+"");
        out.print("ldnm:"+ldnm);

        String dwids = request.getParameter("dwids")==null?"":java.net.URLDecoder.decode(request.getParameter("dwids")+"");
        out.print("dwids:"+dwids);
        dwids = dwids.replace("[", "");
        dwids = dwids.replace("]", "");
        dwids = dwids.replace("\"", "");
        String[] dwidsarr = dwids.split("\\,");
        for(String s:dwidsarr){
            //out.println("hey");
            //out.println(s);
            //先检查所选得是不是已经建立了成交单位
            List<CCObject> cjdw_l = cs.cqlQuery("cjdw","select * from cjdw where cjdw = '"+s+"' and ywjkmc= '"+oppoid+"' and is_deleted='0'");
            if(cjdw_l.size()==0){ //查不到新建
                CCObject cjdw0 = new CCObject("cjdw");
                cjdw0.put("xmmc",xmid); //项目名称
                cjdw0.put("ywjkmc",oppoid); //业务机会名称
                cjdw0.put("ldmc",ldid); //楼栋名称
                cjdw0.put("ldm",ldnm); //楼栋名
                cjdw0.put("cjdw",s); //成交单位
                List<CCObject> dwlist = cs.cqlQuery("ProjectDetail","select dw,jzmjs,dj from ProjectDetail where id= '"+s+"' and is_deleted ='0'");
                String dw= dwlist.get(0).get("dw")==null?"":dwlist.get(0).get("dw")+ "";
                Double jzmjs = dwlist.get(0).get("jzmjs")==null?0.0:Double.parseDouble(dwlist.get(0).get("jzmjs")+"");
                Double dj = dwlist.get(0).get("dj")==null?0.0:Double.parseDouble(dwlist.get(0).get("dj")+"");
                cjdw0.put("cjsw",dw); //房号
                cjdw0.put("mj",jzmjs); //面积
                cjdw0.put("dj",dj); //单价
			    cs.insert(cjdw0);//添加记录
            }
        }
        // List<CCObject> project_l = cs.cqlQuery("project","select p.xmjl as id , c.name as name from project p inner join ccuser c on p.xmjl = c.id where p.is_deleted='0' and p.id='"+projectid+"' and c.isusing='1'");
        // if(project_l.size()>0){
        //     String jlid = project_l.get(0).get("id")==null?"":project_l.get(0).get("id")+ "";  //经理id
        //     String jlname = project_l.get(0).get("name")==null?"":project_l.get(0).get("name")+ "";  //经理name
        //     rtnjbdata.put("id",jlid);
        //     rtnjbdata.put("name",jlname);
        //     rtnMsg.add(rtnjbdata);
        // }
        
        // List<CCObject> sales_l = cs.cqlQuery("projectsalegroup","select p.xmxsy as id , c.name as name from projectsalegroup p inner join ccuser c on p.xmxsy=c.id where p.is_deleted='0' and p.xmmc='"+projectid+"' and c.isusing='1'");
        
        // for(CCObject item:sales_l){
        //     String id = item.get("id")==null?"":item.get("id")+ "";  //业务员id
        //     String name = item.get("name")==null?"":item.get("name")+ "";  //业务员name
        //     rtnjbdata.put("id",id);
        //     rtnjbdata.put("name",name);
        //     rtnMsg.add(rtnjbdata);
        // }
        
        jsonmsg.put("success", true);
        //jsonmsg.put("message", projectid);
        jsonmsg.put("data", rtnMsg);
    } 

} catch (Exception e) {
	jsonmsg.put("success", false);

	jsonmsg.put("message", e.getMessage());
} 



request.setAttribute("jsonmsg", jsonmsg.toString());
</cc>
<html>
	hello
</html>