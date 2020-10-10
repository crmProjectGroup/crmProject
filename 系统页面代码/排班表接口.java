<cc:page type="normal" style="standard" showSidebar="false" showHeader="false" />
<cc!>
/*
description:排班表接口 
排班表接口:处理排班数据的相关处理
1.查询销售小组的数据;
version: 1.0
date:20200702
author:tom
log:
1.20191230 
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
// Double dlfjsbl = 0.0;
// String oppoid = "";
// String bz = "";
//out.print("<script>alert(oppoid);</script>");

JSONArray rtnMsg = new JSONArray();
JSONObject rtnjbdata = new JSONObject();

String msg = "";

try {
	type = request.getParameter("type")==null?"":encodeParameters(request.getParameter("type")+"");//
	 
	if("getsales".equals(type)){ //获取项目下人员情况
		//项目id
        String projectid = request.getParameter("projectid")==null?"":encodeParameters(request.getParameter("projectid")+"");

        List<CCObject> project_l = cs.cqlQuery("project","select p.xmjl as id , c.name as name from project p inner join ccuser c on p.xmjl = c.id where p.is_deleted='0' and p.id='"+projectid+"' and c.isusing='1'");
        if(project_l.size()>0){
            String jlid = project_l.get(0).get("id")==null?"":project_l.get(0).get("id")+ "";  //经理id
            String jlname = project_l.get(0).get("name")==null?"":project_l.get(0).get("name")+ "";  //经理name
            rtnjbdata.put("id",jlid);
            rtnjbdata.put("name",jlname);
            rtnMsg.add(rtnjbdata);
        }
        
        List<CCObject> sales_l = cs.cqlQuery("projectsalegroup","select p.xmxsy as id , c.name as name from projectsalegroup p inner join ccuser c on p.xmxsy=c.id where p.is_deleted='0' and p.xmmc='"+projectid+"' and c.isusing='1'");
        
        for(CCObject item:sales_l){
            String id = item.get("id")==null?"":item.get("id")+ "";  //业务员id
            String name = item.get("name")==null?"":item.get("name")+ "";  //业务员name
            rtnjbdata.put("id",id);
            rtnjbdata.put("name",name);
            rtnMsg.add(rtnjbdata);
        }
        
        jsonmsg.put("success", true);
        jsonmsg.put("message", projectid);
        jsonmsg.put("data", rtnMsg);
    } else if("insertpb".equals(type)){//插入排班数据
        String pbmx = request.getParameter("pbmx")==null?"":java.net.URLDecoder.decode(request.getParameter("pbmx")+"","UTF-8");
        //out.print(pbmx);

        JSONArray jsonary=JSONArray.fromObject(pbmx);

        for(int i=0;i<jsonary.size();i++){
            JSONObject pbmx_0 = jsonary.getJSONObject(i); 
			//out.print(cydx.get("cydx"));
			String pbdx = pbmx_0.getString("pbdx");
			String pbrq = pbmx_0.getString("pbrq");
			String pblx = pbmx_0.getString("pblx");
			String xmmc = pbmx_0.getString("xmmc");
			String ownerid = pbdx;

			CCObject ccschedule = new CCObject("ccschedule");
			ccschedule.put("pbdx",pbdx);
			ccschedule.put("pbrq",pbrq); 
			ccschedule.put("pblx",pblx); 
			ccschedule.put("xmmc",xmmc); 
			ccschedule.put("ownerid",pbdx); 
			ServiceResult sr = cs.insert(ccschedule);
            //out.print(sr.toString());
            rtnMsg.add(sr);

        }
        jsonmsg.put("success", true);
        jsonmsg.put("message", "插入成功!");
        jsonmsg.put("data", rtnMsg);
    } else if("getsalesn".equals(type)){ //获取项目下人员情况,并获取是否已经排班过,已经排班结果
		//项目id
        String projectid = request.getParameter("projectid")==null?"":encodeParameters(request.getParameter("projectid")+"");

        List<CCObject> project_l = cs.cqlQuery("project","select p.xmjl as id , c.name as name from project p inner join ccuser c on p.xmjl = c.id where p.is_deleted='0' and p.id='"+projectid+"' and c.isusing='1'");
        if(project_l.size()>0){
            String jlid = project_l.get(0).get("id")==null?"":project_l.get(0).get("id")+ "";  //经理id
            String jlname = project_l.get(0).get("name")==null?"":project_l.get(0).get("name")+ "";  //经理name
            rtnjbdata.put("id",jlid); 
            rtnjbdata.put("name",jlname);
            rtnMsg.add(rtnjbdata);
        }
        
        List<CCObject> sales_l = cs.cqlQuery("projectsalegroup","select p.xmxsy as id , c.name as name from projectsalegroup p inner join ccuser c on p.xmxsy=c.id where p.is_deleted='0' and p.xmmc='"+projectid+"' and c.isusing='1'");
        
        for(CCObject item:sales_l){
            String id = item.get("id")==null?"":item.get("id")+ "";  //业务员id
            String name = item.get("name")==null?"":item.get("name")+ "";  //业务员name
            rtnjbdata.put("id",id);
            rtnjbdata.put("name",name);
            rtnMsg.add(rtnjbdata);
        }

        //周起始时间及结束时间
        String sdate = request.getParameter("sdate")==null?"":request.getParameter("sdate")+"";
        sdate = sdate.replaceAll("/","-");
        String edate = request.getParameter("edate")==null?"":request.getParameter("edate")+"";
        edate = edate.replaceAll("/","-");
        //String edate = request.getParameter("edate")==null?"":encodeParameters(request.getParameter("edate")+"");

        //确认是否已经做了排班
        List<CCObject> ccschedule_l = cs.cqlQuery("ccschedule","select count(*) as num from ccschedule where is_deleted='0' and xmmc='"+projectid+"' and pbrq >= str_to_date('"+sdate+" 00:00:00', '%Y-%m-%d %H:%i:%s')  AND pbrq <= str_to_date('"+edate+" 23:59:59', '%Y-%m-%d %H:%i:%s') ");
        int num = ccschedule_l.get(0).get("num")==null?0:Integer.parseInt(ccschedule_l.get(0).get("num")+"");
        boolean insStatus = false;
        if(num>0){
            insStatus = true; //给标识位已经做过排班
            for(int i=0;i<rtnMsg.size();i++){ //获取排班内容
                JSONObject item = rtnMsg.getJSONObject(i);
                String itemid = item.get("id")+"";
                List<CCObject> ccschedule_dl = cs.cqlQuery("ccschedule","select pblx from ccschedule where is_deleted='0' and xmmc='"+projectid+"' and pbrq >= str_to_date('"+sdate+" 00:00:00', '%Y-%m-%d %H:%i:%s')  AND pbrq <= str_to_date('"+edate+" 23:59:59', '%Y-%m-%d %H:%i:%s')  and pbdx= '0052018983C9E3DFdNbv' order by pbrq");
                int index = 1;
                for(CCObject dlitem:ccschedule_dl){
                    String pblx = dlitem.get("pblx")+"";
                    item.put("d"+index,pblx);
                    index = 1 + index;
                }
            }

        } else{
            insStatus = false;
        }
        
        
        jsonmsg.put("success", true);
        jsonmsg.put("message", insStatus);
        jsonmsg.put("data", rtnMsg);
    } 

} catch (Exception e) {
	jsonmsg.put("success", false);

	jsonmsg.put("message", e.getMessage());
} 



request.setAttribute("jsonmsg", jsonmsg.toString());
</cc>
// <<html>
// 	hello
// </html>
<cc:forward page="/platform/activity/task/getNodes.jsp"/>