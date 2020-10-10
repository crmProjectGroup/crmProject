<cc:page type="normal" style="standard" showSidebar="false" showHeader="false" />
<cc!>
/*
description:市场数据接口 
1.市场数据接口:处理各类市场数据相关处理
version: 1.0
date:20191213
author:tom
log:
1.20191230 因为楼层数据取了int,当多层或者汉字时候转换失败造成数据insert出错
2.20200628 cqlquery只对管理员可用后,查询录入的市场数据需要些一个接口来返回
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
	type = request.getParameter("type")==null?"":encodeParameters(request.getParameter("type")+"");//客户id
	//out.print(type);
    //jsonmsg.put("success", true);
    //jsonmsg.put("message",type);
	//String id = request.getParameter("id")==null?"":encodeParameters(request.getParameter("id")+"");//客户id
	//String xmmc = request.getParameter("xmmc")==null?"":encodeParameters(request.getParameter("xmmc")+"");//项目id
	//String xsdb = request.getParameter("xsdb")==null?"":encodeParameters(request.getParameter("xsdb")+"");//业务员id
	//String spzt = request.getParameter("spzt")==null?"":encodeParameters(request.getParameter("spzt")+"");//审批状态
	//Double cjdj = request.getParameter("cjdj")==null?0.0:Double.valueOf(encodeParameters(request.getParameter("cjdj")+""));//价格表总价
	//Double cjmj = request.getParameter("cjmj")==null?0.0:Double.valueOf(encodeParameters(request.getParameter("cjmj")+""));//成交面积
	//Double grdywcyjs = 0.0;
    //out.print(type);
    

    // name: 'scsjapi',
    // type: 'ins',
    // lev:that.radio,
    // xmmc: that.form2.xmmc,
    // sm: that.form2.sm,
    // jx: that.form2.jx,
    // cjss:that.form2.cjss,
    // ts:that.form2.ts,
    // cjxqlist2:taht.cjxqlist2
	
	if("ins".equals(type)){ //成交情况记录插入
		//获取记录类型, 是二级还是三级
        String lev = request.getParameter("lev")==null?"":encodeParameters(request.getParameter("lev")+"");
        //out.print(lev);
        if("er".equals(lev)){ //三级20186B76C925373c6GQa , 二级20186A33481F087wkKC5
            lev = "20186A33481F087wkKC5";
        } else if ("san".equals(lev)) {
            lev = "20186B76C925373c6GQa";
        } else if ("gongyu".equals(lev)) {
            lev = "2020CA38DA2EA62GseBx";
        }        

        //市场盘源名称
        String scpymc = request.getParameter("xmmc")==null?"":encodeParameters(request.getParameter("xmmc")+"");
        //out.print(scpymc);
        //上门数
        int sm = request.getParameter("sm")==null?0:Integer.parseInt(encodeParameters(request.getParameter("sm")+""));
        //Double sm = request.getParameter("sm")==null?0.0:Double.valueOf(encodeParameters(request.getParameter("sm")+""));//成交面积
        //out.print("!"+String.valueOf(sm)+"!");
        //进线数
        int jx = request.getParameter("jx")==null?0:Integer.parseInt(encodeParameters(request.getParameter("jx")+""));
        //out.print("!"+String.valueOf(jx)+"!");
        //成交手数
        int cjss = request.getParameter("cjss")==null?0:Integer.parseInt(encodeParameters(request.getParameter("cjss")+""));
        //out.print("!"+String.valueOf(cjss)+"!");
        //套数
        int ts = request.getParameter("ts")==null?0:Integer.parseInt(encodeParameters(request.getParameter("ts")+""));
        //out.print("!"+String.valueOf(ts)+"!");

        //添加上门进线成交情况,上门进线记录类型201945938A54BAEfBWgh
        CCObject cjqk0 = new CCObject("cjqk");
        cjqk0.put("recordtype","201945938A54BAEfBWgh");//记录类型
        cjqk0.put("scpymc",scpymc);//市场盘源名称
        cjqk0.put("sms",sm);//上门数
        cjqk0.put("jxs",jx);//进线数
        cjqk0.put("cjss",cjss);//成交手数
        cjqk0.put("cjts",ts);//成交套数

        cjqk0.put("ownerid",userid);
        cjqk0.put("createbyid",userid); 
        //out.print(cjqk0);
        //String sql= "insert into cjqk (recordtype,scpymc,sms,jxs,cjss,cjts,ownerid,createbyid) values ('201945938A54BAEfBWgh','"+scpymc+"','"+sm+"','"+jx+"','"+cjss+"','"+ts+"','"+userid+"','"+userid+"')";
        //out.print(sql);
        //cs.cqlQuery("cjqk",sql);
        //ServiceResult sr0 = cs.insert(cjqk0);
        ServiceResult sr0 = cs.insertLt(cjqk0);//insertLt
        //out.print(sr0.toString());
        String sr0id = sr0.getString("id");
        //out.print(sr0id);
        String sql= "update cjqk set sms = "+sm+",jxs="+jx+",cjss="+cjss+",cjts="+ts+" where id='"+sr0id+"'";
        //out.print(sql);
        cs.cqlQuery("kpsq",sql);


        //成交数据明细
        String cjxq = request.getParameter("cjxqlist2")==null?"":java.net.URLDecoder.decode(request.getParameter("cjxqlist2")+"","UTF-8");
        //out.print(cjxq);
        //1.把字符串类型的json数组对象转化JSONArray
        JSONArray jsonary=JSONArray.fromObject(cjxq);
        //out.print(String.valueOf(jsonary.size()));

		//out.print("1");
        //out.print(oppoid);
        for(int i=0;i<jsonary.size();i++){
            //out.print("insert"+i);
            JSONObject cjxq_0 = jsonary.getJSONObject(i); 
            //out.print("hahawa"+i);
            //out.print(cjxq_0);
            // cjxq["hy"] = ""; //行业
			// Vue.set(cjxq, 'hy', "");
            // cjxq["khqy"] = ""; //客户来源区域
            // cjxq["lc"] = ""; //楼层
            // cjxq["mj"] = 0; //面积
            // cjxq["dj"] = 0; //单价
            // cjxq["bz"] = ""; //备注
			//out.print(cydx.get("cydx"));
            String hy = cjxq_0.getString("hy");
            String khqy = cjxq_0.getString("khqy");
			String lc = cjxq_0.getString("lc");
			Double mj = Double.valueOf(cjxq_0.getString("mj"));
			Double dj = Double.valueOf(cjxq_0.getString("dj"));
            String bz = cjxq_0.getString("bz");
            
            //out.print("hihiya"+i);
            //循环添加成交情况
            CCObject cjqk = new CCObject("cjqk");
            //out.print("heheta"+i);
            cjqk.put("recordtype",lev);//记录类型
            cjqk.put("scpymc",scpymc);//市场盘源名称
            cjqk.put("xy",hy);//行业
            cjqk.put("lc",lc);//楼层
            cjqk.put("qy",khqy);//客户来源区域
            cjqk.put("mj",mj);//面积
            cjqk.put("dj",dj);//单价
            cjqk.put("bz",bz);//备注
	
			cjqk.put("ownerid",userid);
            cjqk.put("createbyid",userid);  //createbyid赋值为项目经理
            //out.print(userid);
			ServiceResult sr = cs.insertLt(cjqk);
			//out.print(sr.toString());
        }
        
        jsonmsg.put("success", true);
        jsonmsg.put("message", lev);
    } else if("del".equals(type)){ //成交记录删除
        //out.print("dede");
		//获取要删除成交情况记录的id
        String cjqkid = request.getParameter("idlist")==null?"":encodeParameters(request.getParameter("idlist")+"");
        //a2820198BB240CEyEWYL,a28201933AEFA1DNaLnk
        //out.print(cjqkid);
        cjqkid = "('" + cjqkid ;
		cjqkid = cjqkid + "')";
		cjqkid = cjqkid.replace(",", "\',\'");
		//out.print("1");
		//out.print(oppoid);
        //out.print(cjqkid);
        //String sql0 = "update cjqk set is_deleted='1' where id in "+cjqkid+" and is_deleted='0' ";
        //out.print(sql0);
		cs.cqlQuery("cjqk","update cjqk set is_deleted='1' where id in "+cjqkid+" and is_deleted='0' ");

		jsonmsg.put("success", true);
        jsonmsg.put("message", cjqkid);
    } else if("sel".equals(type)){ //查询录入
        out.print("sel");
        //时间 
        //var datetime = " and a.createdate >= str_to_date('"+that.timebegin+"', '%Y-%m-%d %H:%i:%s')  AND a.createdate <= str_to_date('"+that.timeend+"', '%Y-%m-%d %H:%i:%s') ";
        String datetime = request.getParameter("datetime")==null?"":encodeParameters(request.getParameter("datetime")+"");
        datetime = datetime.replaceAll("%20", " ").replaceAll("%25", "%").replaceAll("%3E", ">").replaceAll("%3C", "<");  //%3
        out.print(datetime);
        //直接把组好的sql语句拿过来
        //业务员expresssql="select a.scpymc as scpymc,b.name as scpynm,c.name as usernm ,a.sms as sms,a.jxs as jxs,a.cjss as cjss, a.cjts as cjts,a.createbyid as createbyid,a.id as recid from cjqk a left join scpy b on a.scpymc=b.id left join ccuser c on a.createbyid=c.id where a.is_deleted='0' and a.recordtype ='201945938A54BAEfBWgh' " + datetime + "and a.createbyid='"+that.uid+ "' order by a.createdate desc";
        //项目经理expresssql="select a.scpymc as scpymc,b.name as scpynm,c.name as usernm ,a.sms as sms,a.jxs as jxs,a.cjss as cjss, a.cjts as cjts,a.createbyid as createbyid,a.id as recid from cjqk a left join scpy b on a.scpymc=b.id left join ccuser c on a.createbyid=c.id where a.is_deleted='0' and a.recordtype ='201945938A54BAEfBWgh' " + datetime + "and a.createbyid in "+userid_cond + " order by a.createdate desc";
        //其他expresssql="select a.scpymc as scpymc,b.name as scpynm,c.name as usernm ,a.sms as sms,a.jxs as jxs,a.cjss as cjss, a.cjts as cjts,a.createbyid as createbyid,a.id as recid from cjqk a left join scpy b on a.scpymc=b.id left join ccuser c on a.createbyid=c.id where a.is_deleted='0' and a.recordtype ='201945938A54BAEfBWgh' " + datetime + " order by a.createdate desc";
        String expresssql = request.getParameter("expresssql")==null?"":encodeParameters(request.getParameter("expresssql")+"");
        expresssql = expresssql.replaceAll("%20", " ").replaceAll("%25", "%").replaceAll("%3E", ">").replaceAll("%3C", "<");;
        out.print(expresssql);

        //为了头像,把bindingcode拿过来 binding
        String bindingcode = request.getParameter("bindingcode")==null?"":encodeParameters(request.getParameter("bindingcode")+"");

        //记录id合集方便删除操作
        List<String> idlist = new ArrayList<String>();

        List<CCObject> scpy_l = cs.cqlQuery("cjqk",expresssql);
        for(CCObject item:scpy_l){
            out.print("cycle");
            //JSONObject ccuserjson= new JSONObject();
            String scpymc = item.get("scpymc")==null?"":item.get("scpymc")+ "";  //市场盘源的id
            String scpynm = item.get("scpynm")==null?"":item.get("scpynm")+ "";  //市场盘源的名称
            String usernm = item.get("usernm")==null?"":item.get("usernm")+ "";  //usernm 录入人的名字
            String sms = item.get("sms")==null?"":item.get("sms")+ "";  //上门数 sms
            String jxs = item.get("jxs")==null?"":item.get("jxs")+ "";  //进线数 jxs
            String cjss = item.get("cjss")==null?"":item.get("cjss")+ "";  //成交手数 cjss
            String cjts = item.get("cjts")==null?"":item.get("cjts")+ "";  //成交套数 cjts
            String createbyid = item.get("createbyid")==null?"":item.get("createbyid")+ "";  //录入人id createbyid
            String recid = item.get("recid")==null?"":item.get("recid")+ "";  //成交概况id recid
            rtnjbdata.put("scpymc",scpymc);
			rtnjbdata.put("scpynm",scpynm);
            rtnjbdata.put("usernm",usernm);
            rtnjbdata.put("sms",sms);
            rtnjbdata.put("jxs",jxs);
            rtnjbdata.put("cjss",cjss);
            rtnjbdata.put("cjts",cjts);
            rtnjbdata.put("createbyid",createbyid);
            rtnjbdata.put("recid",recid);
            idlist.add(recid);
            
            //var xm=item.scpymc; //盘源
			//var lrr=item.createbyid; //录入人
            //获取头像
            String imgsrc = "/distributor.action?serviceName=showChatterImage&type=user&id="+createbyid+"&binding="+bindingcode;
			//Vue.set(item, 'imgsrc', imgsrc);
            rtnjbdata.put("imgsrc",imgsrc);

            //var expresssql1="select a.xy as hy,a.qy as qy,a.lc as lc,a.mj as mj, a.dj as dj,a.bz as bz,a.id as recid from cjqk a where a.is_deleted='0' and a.recordtype in ('20186A33481F087wkKC5','20186B76C925373c6GQa') and a.createbyid ='"+ lrr +"' and a.scpymc = '" +xm +"'"  + datetime + " order by a.createdate desc";
            String expresssql1="select a.xy as hy,a.qy as qy,a.lc as lc,a.mj as mj, a.dj as dj,a.bz as bz,a.id as recid from cjqk a where a.is_deleted='0' and a.recordtype in ('20186A33481F087wkKC5','20186B76C925373c6GQa','2020CA38DA2EA62GseBx') and a.createbyid ='"+ createbyid +"' and a.scpymc = '" +scpymc +"'"  + datetime + " order by a.createdate desc";
            JSONArray cjsjjsary = new JSONArray();
            List<CCObject> scsj_l = cs.cqlQuery("cjqk",expresssql1);
            for(CCObject item1:scsj_l){
                String hy = item1.get("hy")==null?"":item1.get("hy")+ "";  //行业
                String qy = item1.get("qy")==null?"":item1.get("qy")+ "";  //区域
                String lc = item1.get("lc")==null?"":item1.get("lc")+ "";  //楼层
                String mj = item1.get("mj")==null?"":item1.get("mj")+ "";  //面积
                String dj = item1.get("dj")==null?"":item1.get("dj")+ "";  //单价
                String bz = item1.get("bz")==null?"":item1.get("bz")+ "";  //备注
                String recid_xq = item1.get("recid")==null?"":item1.get("recid")+ "";  //记录id
                JSONObject cjsjjson= new JSONObject();
                cjsjjson.put("hy",hy);
                cjsjjson.put("qy",qy);
                cjsjjson.put("lc",lc);
                cjsjjson.put("mj",mj);
                cjsjjson.put("dj",dj);
                cjsjjson.put("bz",bz);
                //cjsjjson.put("recid",recid);
                cjsjjsary.add(cjsjjson);
                idlist.add(recid_xq);
            }
            rtnjbdata.put("cjxq",cjsjjsary);
            rtnjbdata.put("idlist",idlist);
            rtnMsg.add(rtnjbdata);
        }

        jsonmsg.put("success", true);
        jsonmsg.put("message", idlist);
        jsonmsg.put("data", rtnMsg);

    } 

} catch (Exception e) {
	jsonmsg.put("success", false);

	jsonmsg.put("message", e.getMessage());
} 



request.setAttribute("jsonmsg", jsonmsg.toString());
</cc>
<!-- <html>
	hello
</html> -->
<cc:forward page="/platform/activity/task/getNodes.jsp"/>