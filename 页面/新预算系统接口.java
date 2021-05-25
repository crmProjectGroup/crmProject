<cc:page type="normal" style="standard" showSidebar="false" showHeader="false" />
<cc!>
/*
description:新预算系统接口 
处理各类预算数据相关处理:
1.查询整体预算数据,反馈表数据;
2.单个整体预算的编辑页面数据,传入项目预算id,返回相关值;
3.修改单个项目整体预算的接口,传入id和数据修改;
version: 1.0
date:20201224
author:tom
log:
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
    if("ins_tb".equals(type)){

        //获取部门预算,以年度为条件
        List<CCObject> bmyslist = cs.cqlQuery("bmys","SELECT id,name FROM bmys WHERE nd='2020' and is_deleted ='0' ");
        String bmysid = bmyslist.get(0).get("id")==null?"":bmyslist.get(0).get("id")+ ""; //simple id a492020E3AFF2EB3W5AQ
        
        //获取项目预算
        List<CCObject> xmyslist = cs.cqlQuery("xmys","SELECT * FROM xmys WHERE bm='"+bmysid +"' and is_deleted='0'");
        
        JSONObject xmysghjson = new JSONObject();
        JSONArray xmysghjsonarr = new JSONArray(); 
        String jsa =""; 

        for(CCObject item:xmyslist){ 
            String id = item.get("id")==null?"":item.get("id")+ ""; //记录id
            String projectid = item.get("xmmc")==null?"":item.get("xmmc")+ ""; //项目id
            List<CCObject> projectlist = cs.cqlQuery("Project","SELECT id, name FROM Project WHERE id = '"+projectid+"' and is_deleted='0'"); 
            String pjnm = projectlist.get(0).get("name")==null?"":projectlist.get(0).get("name")+""; 
            //项目是销售还是租赁类型如何取?
            Double syhl = item.get("syhl")==null?0.0:Double.parseDouble(item.get("syhl")+""); //syhl 剩余货量
            //单价
            Double dj = item.get("jj")==null?0.0:Double.parseDouble(item.get("jj")+""); //jj
            //备注
            String bz = item.get("bz")==null?"":item.get("bz")+ ""; 
            //类型 : 销售or租赁项目r
            String xmtype="租赁";
            xmysghjson.put("id",id);
            xmysghjson.put("pjid",projectid);
            xmysghjson.put("pjid",projectid);
            xmysghjson.put("pjnm",pjnm);
            xmysghjson.put("type",xmtype);
            xmysghjson.put("syhl",syhl);
            xmysghjson.put("dj",dj);
    
            Double cjbl = item.get("cjbl_lxqd")==null?0.0:Double.parseDouble(item.get("cjbl_lxqd")+""); //cjbl_lxqd 成交比例 cjbl
            Double cjmj = item.get("cjmj_lxqd")==null?0.0:Double.parseDouble(item.get("cjmj_lxqd")+""); //cjmj_lxqd 成交面积 cjmj
    
            //分成3块值 团队,正常,保守
            //理想值,团队考核
            String valuetype = "理想值";
            //再分渠道和上门
            //成交总面积;创收合计
            Double cjzmj = item.get("cjzmj_lx")==null?0.0:Double.parseDouble(item.get("cjzmj_lx")+""); //cjzmj_lx 成交总面积
            Double hj = item.get("cs_lxhj")==null?0.0:Double.parseDouble(item.get("cs_lxhj")+""); //cs_lxhj 创收合计
            
            xmysghjson.put("valuetype",valuetype);
            xmysghjson.put("cjzmj",cjzmj);
            xmysghjson.put("hj",hj);
    
            //获取费率
            Double qdfl = item.get("qdswtj")==null?0.0:Double.parseDouble(item.get("qdswtj")+""); //fl 渠道费率 qdswtj
            Double smfl = item.get("zcswtj")==null?0.0:Double.parseDouble(item.get("zcswtj")+""); //fl 费率 zcswtj
    
            //渠道部分
            String ly = "渠道";
            
            //Double fl = item.get("qdswtj")==null?0.0:Double.parseDouble(item.get("qdswtj")+""); //fl 费率 qdswtj
            Double csje = item.get("cs_lxqd")==null?0.0:Double.parseDouble(item.get("cs_lxqd")+""); //cs 创收 cs_lxqd
            xmysghjson.put("ly",ly);
            xmysghjson.put("cjbl",cjbl);
            xmysghjson.put("cjmj",cjmj);
            xmysghjson.put("fl",qdfl);
            xmysghjson.put("cs",csje);
    
            xmysghjsonarr.add(xmysghjson); 
    
            //自然上门部分
            ly = "上门";
            cjbl = item.get("cjbl_lxsm")==null?0.0:Double.parseDouble(item.get("cjbl_lxsm")+""); //cjbl_lxqd 成交比例 cjbl
            cjmj = item.get("cjmj_lxsm")==null?0.0:Double.parseDouble(item.get("cjmj_lxsm")+""); //cjmj_lxqd 成交面积 cjmj
            //Double fl = item.get("qdswtj")==null?0.0:Double.parseDouble(item.get("qdswtj")+""); //fl 费率 qdswtj
            csje = item.get("cs_lxsm")==null?0.0:Double.parseDouble(item.get("cs_lxsm")+""); //cs 创收 cs_lxqd
            xmysghjson.put("ly",ly);
            xmysghjson.put("cjbl",cjbl);
            xmysghjson.put("cjmj",cjmj);
            xmysghjson.put("fl",smfl);
            xmysghjson.put("cs",csje);
    
            xmysghjsonarr.add(xmysghjson); 
    
            //正常值,团队考核
            valuetype = "正常值";
            //再分渠道和上门
            //成交总面积;创收合计
            cjzmj = item.get("cjzmj_zc")==null?0.0:Double.parseDouble(item.get("cjzmj_zc")+""); //成交总面积
            hj = item.get("cs_zchj")==null?0.0:Double.parseDouble(item.get("cs_zchj")+""); //创收合计
            
            xmysghjson.put("valuetype",valuetype);
            xmysghjson.put("cjzmj",cjzmj);
            xmysghjson.put("hj",hj);
    
            //渠道部分
            ly = "渠道";
            cjbl = item.get("cjbl_zcqd")==null?0.0:Double.parseDouble(item.get("cjbl_zcqd")+""); //cjbl_lxqd 成交比例 cjbl
            cjmj = item.get("cjmj_zcqd")==null?0.0:Double.parseDouble(item.get("cjmj_zcqd")+""); //cjmj_lxqd 成交面积 cjmj
            //Double fl = item.get("qdswtj")==null?0.0:Double.parseDouble(item.get("qdswtj")+""); //fl 费率 qdswtj
            csje = item.get("cs_zcqd")==null?0.0:Double.parseDouble(item.get("cs_zcqd")+""); //cs 创收 cs_lxqd
            xmysghjson.put("ly",ly);
            xmysghjson.put("cjbl",cjbl);
            xmysghjson.put("cjmj",cjmj);
            xmysghjson.put("fl",qdfl);
            xmysghjson.put("cs",csje);
    
            xmysghjsonarr.add(xmysghjson); 
    
            //自然上门部分
            ly = "上门";
            cjbl = item.get("cjbl_zcsm")==null?0.0:Double.parseDouble(item.get("cjbl_zcsm")+""); //cjbl_lxqd 成交比例 cjbl
            cjmj = item.get("cjmj_zcsm")==null?0.0:Double.parseDouble(item.get("cjmj_zcsm")+""); //cjmj_lxqd 成交面积 cjmj
            //Double fl = item.get("qdswtj")==null?0.0:Double.parseDouble(item.get("qdswtj")+""); //fl 费率 qdswtj
            csje = item.get("cs_zcsm")==null?0.0:Double.parseDouble(item.get("cs_zcsm")+""); //cs 创收 cs_lxqd
            xmysghjson.put("ly",ly);
            xmysghjson.put("cjbl",cjbl);
            xmysghjson.put("cjmj",cjmj);
            xmysghjson.put("fl",smfl);
            xmysghjson.put("cs",csje);
    
            xmysghjsonarr.add(xmysghjson); 
    
            //保守值,团队考核
            valuetype = "保守值";
            //再分渠道和上门
            //成交总面积;创收合计
            cjzmj = item.get("cjzmj_bs")==null?0.0:Double.parseDouble(item.get("cjzmj_bs")+""); //成交总面积
            hj = item.get("cs_bshj")==null?0.0:Double.parseDouble(item.get("cs_bshj")+""); //创收合计
            
            xmysghjson.put("valuetype",valuetype);
            xmysghjson.put("cjzmj",cjzmj);
            xmysghjson.put("hj",hj);
    
            //渠道部分
            ly = "渠道";
            cjbl = item.get("cjbl_bsqd")==null?0.0:Double.parseDouble(item.get("cjbl_bsqd")+""); //cjbl_lxqd 成交比例 cjbl
            cjmj = item.get("cjmj_bsqd")==null?0.0:Double.parseDouble(item.get("cjmj_bsqd")+""); //cjmj_lxqd 成交面积 cjmj
            //Double fl = item.get("qdswtj")==null?0.0:Double.parseDouble(item.get("qdswtj")+""); //fl 费率 qdswtj
            csje = item.get("cs_bsqd")==null?0.0:Double.parseDouble(item.get("cs_bsqd")+""); //cs 创收 cs_lxqd
            xmysghjson.put("ly",ly);
            xmysghjson.put("cjbl",cjbl);
            xmysghjson.put("cjmj",cjmj);
            xmysghjson.put("fl",qdfl);
            xmysghjson.put("cs",csje);
    
            xmysghjsonarr.add(xmysghjson); 
    
            //自然上门部分
            ly = "上门";
            cjbl = item.get("cjbl_bssm")==null?0.0:Double.parseDouble(item.get("cjbl_bssm")+""); //cjbl_lxqd 成交比例 cjbl
            cjmj = item.get("cjmj_bssm")==null?0.0:Double.parseDouble(item.get("cjmj_bssm")+""); //cjmj_lxqd 成交面积 cjmj
            //Double fl = item.get("qdswtj")==null?0.0:Double.parseDouble(item.get("qdswtj")+""); //fl 费率 qdswtj
            csje = item.get("cs_bssm")==null?0.0:Double.parseDouble(item.get("cs_bssm")+""); //cs 创收 cs_lxqd
            xmysghjson.put("ly",ly);
            xmysghjson.put("cjbl",cjbl);
            xmysghjson.put("cjmj",cjmj);
            xmysghjson.put("fl",smfl);
            xmysghjson.put("cs",csje);
    
            xmysghjsonarr.add(xmysghjson); 
        }
    
        //jsa=xmysghjsonarr.toString();
        jsonmsg.put("success", true);
        jsonmsg.put("message", bmysid);
        jsonmsg.put("data", xmysghjsonarr);
	
	} else if("ins_edt".equals(type)){ //单个项目编辑前查询数据
		//获取传入的项目预算的id
        String id = request.getParameter("id")==null?"":encodeParameters(request.getParameter("id")+"");
        //out.print(lev);

        //sql查询
        List<CCObject> xmyslist = cs.cqlQuery("xmys","SELECT * FROM xmys WHERE id='"+id+"' and is_deleted='0'");
        if(xmyslist.size()==1){
            // String qy = request.getParameter("qy")==null?"":encodeParameters(request.getParameter("qy")+"");
            // int lc = request.getParameter("lc")==null?0:Integer.parseInt(encodeParameters(request.getParameter("lc")+""));
            // Double mj = request.getParameter("mj")==null?0.0:Double.valueOf(encodeParameters(request.getParameter("mj")+""));
            //项目名称
            String xmid = xmyslist.get(0).get("xmmc")==null?"":xmyslist.get(0).get("xmmc")+"";
            List<CCObject> projectlist = cs.cqlQuery("Project","SELECT id, name FROM Project WHERE id = '"+xmid+"' and is_deleted='0'"); 
            String pjnm = projectlist.get(0).get("name")==null?"":projectlist.get(0).get("name")+"";
            
            Double syhl = xmyslist.get(0).get("syhl")==null?0.0:Double.valueOf(xmyslist.get(0).get("syhl")+"");
            Double jj = xmyslist.get(0).get("jj")==null?0.0:Double.valueOf(xmyslist.get(0).get("jj")+"");
            Double qdfl = xmyslist.get(0).get("qdswtj")==null?0.0:Double.valueOf(xmyslist.get(0).get("qdswtj")+""); //渠道费率
            Double smfl = xmyslist.get(0).get("zcswtj")==null?0.0:Double.valueOf(xmyslist.get(0).get("zcswtj")+""); //上门费率

            Double cjzmj_lx = xmyslist.get(0).get("cjzmj_lx")==null?0.0:Double.valueOf(xmyslist.get(0).get("cjzmj_lx")+""); //理想成交总面积'',
            Double cjbl_lxqd = xmyslist.get(0).get("cjbl_lxqd")==null?0.0:Double.valueOf(xmyslist.get(0).get("cjbl_lxqd")+""); //理想成交比例(渠道)
            Double cjbl_lxsm = xmyslist.get(0).get("cjbl_lxsm")==null?0.0:Double.valueOf(xmyslist.get(0).get("cjbl_lxsm")+""); //理想成交比例(上门)
            Double cjmj_lxqd = xmyslist.get(0).get("cjmj_lxqd")==null?0.0:Double.valueOf(xmyslist.get(0).get("cjmj_lxqd")+""); //理想渠道成交面积
            Double cjmj_lxsm = xmyslist.get(0).get("cjmj_lxsm")==null?0.0:Double.valueOf(xmyslist.get(0).get("cjmj_lxsm")+""); //理想上门成交面积
            Double cs_lxqd = xmyslist.get(0).get("cs_lxqd")==null?0.0:Double.valueOf(xmyslist.get(0).get("cs_lxqd")+""); //理想渠道创收
            Double cs_lxsm = xmyslist.get(0).get("cs_lxsm")==null?0.0:Double.valueOf(xmyslist.get(0).get("cs_lxsm")+""); //理想上门创收
            Double cs_lxhj = xmyslist.get(0).get("cs_lxhj")==null?0.0:Double.valueOf(xmyslist.get(0).get("cs_lxhj")+""); //理想创收合计

            Double cjzmj_zc = xmyslist.get(0).get("cjzmj_zc")==null?0.0:Double.valueOf(xmyslist.get(0).get("cjzmj_zc")+""); //正常成交总面积'',
            Double cjbl_zcqd = xmyslist.get(0).get("cjbl_zcqd")==null?0.0:Double.valueOf(xmyslist.get(0).get("cjbl_zcqd")+""); //正常成交比例(渠道)
            Double cjbl_zcsm = xmyslist.get(0).get("cjbl_zcsm")==null?0.0:Double.valueOf(xmyslist.get(0).get("cjbl_zcsm")+""); //正常成交比例(上门)
            Double cjmj_zcqd = xmyslist.get(0).get("cjmj_zcqd")==null?0.0:Double.valueOf(xmyslist.get(0).get("cjmj_zcqd")+""); //正常渠道成交面积
            Double cjmj_zcsm = xmyslist.get(0).get("cjmj_zcsm")==null?0.0:Double.valueOf(xmyslist.get(0).get("cjmj_zcsm")+""); //正常上门成交面积
            Double cs_zcqd = xmyslist.get(0).get("cs_zcqd")==null?0.0:Double.valueOf(xmyslist.get(0).get("cs_zcqd")+""); //正常渠道创收
            Double cs_zcsm = xmyslist.get(0).get("cs_zcsm")==null?0.0:Double.valueOf(xmyslist.get(0).get("cs_zcsm")+""); //正常上门创收
            Double cs_zchj = xmyslist.get(0).get("cs_zchj")==null?0.0:Double.valueOf(xmyslist.get(0).get("cs_zchj")+""); //正常创收合计
            
            Double cjzmj_bs = xmyslist.get(0).get("cjzmj_bs")==null?0.0:Double.valueOf(xmyslist.get(0).get("cjzmj_bs")+""); //保守成交总面积'',
            Double cjbl_bsqd = xmyslist.get(0).get("cjbl_bsqd")==null?0.0:Double.valueOf(xmyslist.get(0).get("cjbl_bsqd")+""); //保守成交比例(渠道)
            Double cjbl_bssm = xmyslist.get(0).get("cjbl_bssm")==null?0.0:Double.valueOf(xmyslist.get(0).get("cjbl_bssm")+""); //保守成交比例(上门)
            Double cjmj_bsqd = xmyslist.get(0).get("cjmj_bsqd")==null?0.0:Double.valueOf(xmyslist.get(0).get("cjmj_bsqd")+""); //保守渠道成交面积
            Double cjmj_bssm = xmyslist.get(0).get("cjmj_bssm")==null?0.0:Double.valueOf(xmyslist.get(0).get("cjmj_bssm")+""); //保守上门成交面积
            Double cs_bsqd = xmyslist.get(0).get("cs_bsqd")==null?0.0:Double.valueOf(xmyslist.get(0).get("cs_bsqd")+""); //保守渠道创收
            Double cs_bssm = xmyslist.get(0).get("cs_bssm")==null?0.0:Double.valueOf(xmyslist.get(0).get("cs_bssm")+""); //保守上门创收
            Double cs_bshj = xmyslist.get(0).get("cs_bshj")==null?0.0:Double.valueOf(xmyslist.get(0).get("cs_bshj")+""); //保守创收合计

            rtnjbdata.put("id",id);
            rtnjbdata.put("pjnm",pjnm);
            rtnjbdata.put("syhl",syhl);
            rtnjbdata.put("jj",jj);
            rtnjbdata.put("qdfl",qdfl);
            rtnjbdata.put("smfl",smfl);
            rtnjbdata.put("cjzmj_lx",cjzmj_lx);
            rtnjbdata.put("cjbl_lxqd",cjbl_lxqd);
            rtnjbdata.put("cjbl_lxsm",cjbl_lxsm);
            rtnjbdata.put("cjmj_lxqd",cjmj_lxqd);
            rtnjbdata.put("cjmj_lxsm",cjmj_lxsm);
            rtnjbdata.put("cs_lxqd",cs_lxqd);
            rtnjbdata.put("cs_lxsm",cs_lxsm);
            rtnjbdata.put("cs_lxhj",cs_lxhj); 
            rtnjbdata.put("cjzmj_zc",cjzmj_zc);
            rtnjbdata.put("cjbl_zcqd",cjbl_zcqd);
            rtnjbdata.put("cjbl_zcsm",cjbl_zcsm);
            rtnjbdata.put("cjmj_zcqd",cjmj_zcqd);
            rtnjbdata.put("cjmj_zcsm",cjmj_zcsm);
            rtnjbdata.put("cs_zcqd",cs_zcqd);
            rtnjbdata.put("cs_zcsm",cs_zcsm);
            rtnjbdata.put("cs_zchj",cs_zchj);
            rtnjbdata.put("cjzmj_bs",cjzmj_bs);
            rtnjbdata.put("cjbl_bsqd",cjbl_bsqd);
            rtnjbdata.put("cjbl_bssm",cjbl_bssm);
            rtnjbdata.put("cjmj_bsqd",cjmj_bsqd);
            rtnjbdata.put("cjmj_bssm",cjmj_bssm);
            rtnjbdata.put("cs_bsqd",cs_bsqd);
            rtnjbdata.put("cs_bssm",cs_bssm);
            rtnjbdata.put("cs_bshj",cs_bshj);
            rtnMsg.add(rtnjbdata);

            jsonmsg.put("success", true);
            jsonmsg.put("message", id);
            jsonmsg.put("data", rtnMsg);

        } else {
            jsonmsg.put("success", false);
	        jsonmsg.put("message", id+"项目预算异常!");
        }
        
    } else if("upd_ztxmys".equals(type)){ //单个项目编辑前查询数据
        String ztys = request.getParameter("ztys")==null?"":java.net.URLDecoder.decode(request.getParameter("ztys")+"","UTF-8");

        JSONObject xmys_0 = JSONObject.fromObject(ztys);
        //JSONObject cymx_0 = jsonary.getJSONObject(i); 
			//out.print(cydx.get("cydx"));

        String id = xmys_0.getString("id");

        Double cjzmj_lx = Double.valueOf(xmys_0.getString("cjzmj_lx")); //理想成交总面积'',
        Double cjbl_lxqd = Double.valueOf(xmys_0.getString("cjbl_lxqd")); //理想成交比例(渠道)
        Double cjbl_lxsm = Double.valueOf(xmys_0.getString("cjbl_lxsm"));  //理想成交比例(上门)
        Double cjmj_lxqd = Double.valueOf(xmys_0.getString("cjmj_lxqd")); //理想渠道成交面积
        Double cjmj_lxsm = Double.valueOf(xmys_0.getString("cjmj_lxsm")); //理想上门成交面积
        Double cs_lxqd = Double.valueOf(xmys_0.getString("cs_lxqd"));  //理想渠道创收
        Double cs_lxsm = Double.valueOf(xmys_0.getString("cs_lxsm"));  //理想上门创收
        Double cs_lxhj = Double.valueOf(xmys_0.getString("cs_lxhj"));  //理想创收合计

        Double cjzmj_zc = Double.valueOf(xmys_0.getString("cjzmj_zc")); //正常成交总面积'',
        Double cjbl_zcqd = Double.valueOf(xmys_0.getString("cjbl_zcqd")); //正常成交比例(渠道)
        Double cjbl_zcsm = Double.valueOf(xmys_0.getString("cjbl_zcsm")); //正常成交比例(上门)
        Double cjmj_zcqd = Double.valueOf(xmys_0.getString("cjmj_zcqd")); //正常渠道成交面积
        Double cjmj_zcsm = Double.valueOf(xmys_0.getString("cjmj_zcsm")); //正常上门成交面积
        Double cs_zcqd = Double.valueOf(xmys_0.getString("cs_zcqd")); //正常渠道创收
        Double cs_zcsm = Double.valueOf(xmys_0.getString("cs_zcsm")); //正常上门创收
        Double cs_zchj = Double.valueOf(xmys_0.getString("cs_zchj")); //正常创收合计
        
        Double cjzmj_bs = Double.valueOf(xmys_0.getString("cjzmj_bs")); //保守成交总面积'',
        Double cjbl_bsqd = Double.valueOf(xmys_0.getString("cjbl_bsqd")); //保守成交比例(渠道)
        Double cjbl_bssm = Double.valueOf(xmys_0.getString("cjbl_bssm")); //保守成交比例(上门)
        Double cjmj_bsqd = Double.valueOf(xmys_0.getString("cjmj_bsqd")); //保守渠道成交面积
        Double cjmj_bssm = Double.valueOf(xmys_0.getString("cjmj_bssm")); //保守上门成交面积
        Double cs_bsqd = Double.valueOf(xmys_0.getString("cs_bsqd")); //保守渠道创收
        Double cs_bssm = Double.valueOf(xmys_0.getString("cs_bssm")); //保守上门创收
        Double cs_bshj = Double.valueOf(xmys_0.getString("cs_bshj")); //保守创收合计
        
        CCObject xmys = new CCObject("xmys");
		xmys.put("id",id); //维护的对象
        xmys.put("cjzmj_lx",cjzmj_lx);
        xmys.put("cjbl_lxqd",cjbl_lxqd);
        xmys.put("cjbl_lxsm",cjbl_lxsm);
        xmys.put("cjmj_lxqd",cjmj_lxqd);
        xmys.put("cjmj_lxsm",cjmj_lxsm);
        xmys.put("cs_lxqd",cs_lxqd);
        xmys.put("cs_lxsm",cs_lxsm);
        xmys.put("cs_lxhj",cs_lxhj); 
        xmys.put("cjzmj_zc",cjzmj_zc);
        xmys.put("cjbl_zcqd",cjbl_zcqd);
        xmys.put("cjbl_zcsm",cjbl_zcsm);
        xmys.put("cjmj_zcqd",cjmj_zcqd);
        xmys.put("cjmj_zcsm",cjmj_zcsm);
        xmys.put("cs_zcqd",cs_zcqd);
        xmys.put("cs_zcsm",cs_zcsm);
        xmys.put("cs_zchj",cs_zchj);
        xmys.put("cjzmj_bs",cjzmj_bs);
        xmys.put("cjbl_bsqd",cjbl_bsqd);
        xmys.put("cjbl_bssm",cjbl_bssm);
        xmys.put("cjmj_bsqd",cjmj_bsqd);
        xmys.put("cjmj_bssm",cjmj_bssm);
        xmys.put("cs_bsqd",cs_bsqd);
        xmys.put("cs_bssm",cs_bssm);
        xmys.put("cs_bshj",cs_bshj);
	
        cs.updateLt(xmys);//更新记录
        
        jsonmsg.put("success", true);
        jsonmsg.put("message", id);
    } else if("ins_pjlist".equals(type)){ //查询项目列表作为新增项目时候的选项
        List<CCObject> projectlist = cs.cqlQuery("project","SELECT id,name FROM project WHERE xmzt='代理中' and is_deleted ='0' and ssbm in ('代理一部','代理二部') ");
        for(CCObject item:projectlist){
            String id = item.get("id")==null?"":item.get("id")+ ""; 
            String name = item.get("id")==null?"":item.get("name")+ ""; 
            rtnjbdata.put("id",id);
            rtnjbdata.put("name",name);
            rtnMsg.add(rtnjbdata);


        }
        jsonmsg.put("success", true);
        jsonmsg.put("message", String.valueOf(projectlist.size()));
        jsonmsg.put("data", rtnMsg);
    } else if("add_xmys".equals(type)){ //新增项目预算
        String ztys = request.getParameter("parms")==null?"":java.net.URLDecoder.decode(request.getParameter("parms")+"","UTF-8");
        String bmysid = request.getParameter("bmysid")==null?"":java.net.URLDecoder.decode(request.getParameter("bmysid")+"","UTF-8");

        JSONObject xmys_0 = JSONObject.fromObject(ztys);
        //JSONObject cymx_0 = jsonary.getJSONObject(i); 
			//out.print(cydx.get("cydx"));

        String xmmc = xmys_0.getString("pjid");

        Double syhl = Double.valueOf(xmys_0.getString("syhl")); //剩余货量
        Double jj = Double.valueOf(xmys_0.getString("jj")); //均价
        Double zcswtj = Double.valueOf(xmys_0.getString("smfl"));  //上门费率
        Double qdswtj = Double.valueOf(xmys_0.getString("qdfl")); //渠道费率

        CCObject xmys = new CCObject("xmys");
        xmys.put("bm",bmysid);
        xmys.put("xmmc",xmmc);
        xmys.put("syhl",syhl);
        xmys.put("jj",jj);
        xmys.put("zcswtj",zcswtj);
        xmys.put("qdswtj",qdswtj);

        cs.insertLt(xmys);
        jsonmsg.put("success", true);
        jsonmsg.put("message", "新增成功!");
    } else if("ins_yftablelx".equals(type)){ //查询项目每月预算理想值
        //获取部门预算,以年度为条件
        List<CCObject> bmyslist = cs.cqlQuery("bmys","SELECT id,name FROM bmys WHERE nd='2020' and is_deleted ='0' ");
        String bmysid = bmyslist.get(0).get("id")==null?"":bmyslist.get(0).get("id")+ ""; //simple id a492020E3AFF2EB3W5AQ
        
        //获取项目预算
        List<CCObject> xmyslist = cs.cqlQuery("xmys","SELECT * FROM xmys WHERE bm='"+bmysid +"' and is_deleted='0'");
        
        // JSONObject xmysghjson = new JSONObject();
        // JSONArray xmysghjsonarr = new JSONArray(); 
        // String jsa =""; 

        for(CCObject item:xmyslist){ 
            String id = item.get("id")==null?"":item.get("id")+ ""; //记录id
            String projectid = item.get("xmmc")==null?"":item.get("xmmc")+ ""; //项目id
            List<CCObject> projectlist = cs.cqlQuery("Project","SELECT id, name FROM Project WHERE id = '"+projectid+"' and is_deleted='0'"); 
            String pjnm = projectlist.get(0).get("name")==null?"":projectlist.get(0).get("name")+""; 
            //类型 : 面积和创收
            String valtype="面积";
            rtnjbdata.put("id",id); //项目预算记录id
            rtnjbdata.put("pjnm",pjnm);
            rtnjbdata.put("type",valtype);
            
            //每个月的理想目标面积
            String valnm = "";
            Double totalval= 0.0;
            for(int i=1;i<13;i++){
                valnm = "ymbmj"+i+"lx";
                Double value = item.get(valnm)==null?0.0:Double.parseDouble(item.get(valnm)+"");
                totalval += value;
                rtnjbdata.put("value"+i,value);
            }
            rtnjbdata.put("valuetot",totalval);
            rtnMsg.add(rtnjbdata);

            valtype="创收";
            rtnjbdata.put("pjnm",pjnm);
            rtnjbdata.put("type",valtype);
            
            totalval=0.0;
            //每个月的理想目标面积
            for(int i=1;i<13;i++){
                valnm = "ycsmb"+i+"lx";
                Double value = item.get(valnm)==null?0.0:Double.parseDouble(item.get(valnm)+"");
                totalval += value;
                rtnjbdata.put("value"+i,value);
            }
            rtnjbdata.put("valuetot",totalval);
            rtnMsg.add(rtnjbdata);
        }

                //jsa=xmysghjsonarr.toString();
        jsonmsg.put("success", true);
        jsonmsg.put("message", bmysid);
        jsonmsg.put("data", rtnMsg);
    
    } else if("ins_yftablezc".equals(type)){ //查询项目每月预算理想值
        //获取部门预算,以年度为条件
        List<CCObject> bmyslist = cs.cqlQuery("bmys","SELECT id,name FROM bmys WHERE nd='2020' and is_deleted ='0' ");
        String bmysid = bmyslist.get(0).get("id")==null?"":bmyslist.get(0).get("id")+ ""; //simple id a492020E3AFF2EB3W5AQ
        
        //获取项目预算
        List<CCObject> xmyslist = cs.cqlQuery("xmys","SELECT * FROM xmys WHERE bm='"+bmysid +"' and is_deleted='0'");
        
        // JSONObject xmysghjson = new JSONObject();
        // JSONArray xmysghjsonarr = new JSONArray(); 
        // String jsa =""; 

        for(CCObject item:xmyslist){ 
            String id = item.get("id")==null?"":item.get("id")+ ""; //记录id
            String projectid = item.get("xmmc")==null?"":item.get("xmmc")+ ""; //项目id
            List<CCObject> projectlist = cs.cqlQuery("Project","SELECT id, name FROM Project WHERE id = '"+projectid+"' and is_deleted='0'"); 
            String pjnm = projectlist.get(0).get("name")==null?"":projectlist.get(0).get("name")+""; 
            //类型 : 面积和创收
            String valtype="面积";
            rtnjbdata.put("id",id); //项目预算记录id
            rtnjbdata.put("pjnm",pjnm);
            rtnjbdata.put("type",valtype);
            
            //每个月的理想目标面积
            String valnm = "";
            Double totalval= 0.0;
            for(int i=1;i<13;i++){
                valnm = "ymbmj"+i;
                Double value = item.get(valnm)==null?0.0:Double.parseDouble(item.get(valnm)+"");
                totalval += value;
                rtnjbdata.put("value"+i,value);
            }
            rtnjbdata.put("valuetot",totalval);
            rtnMsg.add(rtnjbdata);

            valtype="创收";
            rtnjbdata.put("pjnm",pjnm);
            rtnjbdata.put("type",valtype);
            
            totalval=0.0;
            //每个月的理想目标创收
            for(int i=1;i<13;i++){
                valnm = "ycsmb"+i+"zc";
                Double value = item.get(valnm)==null?0.0:Double.parseDouble(item.get(valnm)+"");
                totalval += value;
                rtnjbdata.put("value"+i,value);
            }
            rtnjbdata.put("valuetot",totalval);
            rtnMsg.add(rtnjbdata);
        }
    
        //jsa=xmysghjsonarr.toString();
        jsonmsg.put("success", true);
        jsonmsg.put("message", bmysid);
        jsonmsg.put("data", rtnMsg);
    } else if("ins_edtyfys".equals(type)){ //查询项目每月目标成交面积和创收, 根据valtype返回理想值和正常值
        //获取项目预算id
        String xmysid = request.getParameter("id")==null?"":java.net.URLDecoder.decode(request.getParameter("id")+"","UTF-8");

        //获取valtype, 返回理想值或正常值
        String valtype = request.getParameter("valtype")==null?"":java.net.URLDecoder.decode(request.getParameter("valtype")+"","UTF-8");
        
        //获取项目预算
        List<CCObject> xmyslist = cs.cqlQuery("xmys","SELECT * FROM xmys WHERE id='"+xmysid +"' and is_deleted='0'");

        String xmmc = xmyslist.get(0).get("xmmc")==null?"":xmyslist.get(0).get("xmmc")+""; //项目id

        List<CCObject> projectlist = cs.cqlQuery("Project","SELECT id, name FROM Project WHERE id = '"+xmmc+"' and is_deleted='0'"); 
        String pjnm = projectlist.get(0).get("name")==null?"":projectlist.get(0).get("name")+""; //项目名称

        //两个成交比例,单价,两个费率
        Double cjbl_qd = 0.0;
        Double cjbl_sm = 0.0;

        Double smfl = xmyslist.get(0).get("zcswtj")==null?0.0:Double.parseDouble(xmyslist.get(0).get("zcswtj")+""); //上门费率
        Double qdfl = xmyslist.get(0).get("qdswtj")==null?0.0:Double.parseDouble(xmyslist.get(0).get("qdswtj")+""); //渠道费率

        Double jj = xmyslist.get(0).get("jj")==null?0.0:Double.parseDouble(xmyslist.get(0).get("jj")+""); //均价

        rtnjbdata.put("xmysid",xmysid);
        rtnjbdata.put("pjnm",pjnm);
        //rtnjbdata.put("cjzmj",cjzmj);
        rtnjbdata.put("smfl",smfl);
        rtnjbdata.put("qdfl",qdfl);
        rtnjbdata.put("jj",jj);

        //理想值
        if("lx".equals(valtype)){
            cjbl_qd = xmyslist.get(0).get("cjbl_lxqd")==null?0.0:Double.parseDouble(xmyslist.get(0).get("cjbl_lxqd")+""); //理想渠道比例
            cjbl_sm = xmyslist.get(0).get("cjbl_lxsm")==null?0.0:Double.parseDouble(xmyslist.get(0).get("cjbl_lxsm")+""); //理想上门比例
            rtnjbdata.put("cjbl_qd",cjbl_qd);
            rtnjbdata.put("cjbl_sm",cjbl_sm);

            Double cjzmj = xmyslist.get(0).get("cjzmj_lx")==null?0.0:Double.parseDouble(xmyslist.get(0).get("cjzmj_lx")+""); //理想计划成交总面积 cjzmj_lx

            //成交总面积(正常) cjzmj_zc

            Double mj1 = xmyslist.get(0).get("ymbmj1lx")==null?0.0:Double.parseDouble(xmyslist.get(0).get("ymbmj1lx")+""); //1月目标面积
            Double mj2 = xmyslist.get(0).get("ymbmj2lx")==null?0.0:Double.parseDouble(xmyslist.get(0).get("ymbmj2lx")+""); //2月目标面积
            Double mj3 = xmyslist.get(0).get("ymbmj3lx")==null?0.0:Double.parseDouble(xmyslist.get(0).get("ymbmj3lx")+"");
            Double mj4 = xmyslist.get(0).get("ymbmj4lx")==null?0.0:Double.parseDouble(xmyslist.get(0).get("ymbmj4lx")+"");
            Double mj5 = xmyslist.get(0).get("ymbmj5lx")==null?0.0:Double.parseDouble(xmyslist.get(0).get("ymbmj5lx")+"");
            Double mj6 = xmyslist.get(0).get("ymbmj6lx")==null?0.0:Double.parseDouble(xmyslist.get(0).get("ymbmj6lx")+"");
            Double mj7 = xmyslist.get(0).get("ymbmj7lx")==null?0.0:Double.parseDouble(xmyslist.get(0).get("ymbmj7lx")+"");
            Double mj8 = xmyslist.get(0).get("ymbmj8lx")==null?0.0:Double.parseDouble(xmyslist.get(0).get("ymbmj8lx")+"");
            Double mj9 = xmyslist.get(0).get("ymbmj9lx")==null?0.0:Double.parseDouble(xmyslist.get(0).get("ymbmj9lx")+"");
            Double mj10 = xmyslist.get(0).get("ymbmj10lx")==null?0.0:Double.parseDouble(xmyslist.get(0).get("ymbmj10lx")+"");
            Double mj11 = xmyslist.get(0).get("ymbmj11lx")==null?0.0:Double.parseDouble(xmyslist.get(0).get("ymbmj11lx")+"");
            Double mj12 = xmyslist.get(0).get("ymbmj12lx")==null?0.0:Double.parseDouble(xmyslist.get(0).get("ymbmj12lx")+"");

            Double cs1 = xmyslist.get(0).get("ycsmb1lx")==null?0.0:Double.parseDouble(xmyslist.get(0).get("ycsmb1lx")+""); //1月创收目标
            Double cs2 = xmyslist.get(0).get("ycsmb2lx")==null?0.0:Double.parseDouble(xmyslist.get(0).get("ycsmb2lx")+""); //2月创收目标
            Double cs3 = xmyslist.get(0).get("ycsmb3lx")==null?0.0:Double.parseDouble(xmyslist.get(0).get("ycsmb3lx")+"");
            Double cs4 = xmyslist.get(0).get("ycsmb4lx")==null?0.0:Double.parseDouble(xmyslist.get(0).get("ycsmb4lx")+"");
            Double cs5 = xmyslist.get(0).get("ycsmb5lx")==null?0.0:Double.parseDouble(xmyslist.get(0).get("ycsmb5lx")+"");
            Double cs6 = xmyslist.get(0).get("ycsmb6lx")==null?0.0:Double.parseDouble(xmyslist.get(0).get("ycsmb6lx")+"");
            Double cs7 = xmyslist.get(0).get("ycsmb7lx")==null?0.0:Double.parseDouble(xmyslist.get(0).get("ycsmb7lx")+"");
            Double cs8 = xmyslist.get(0).get("ycsmb8lx")==null?0.0:Double.parseDouble(xmyslist.get(0).get("ycsmb8lx")+"");
            Double cs9 = xmyslist.get(0).get("ycsmb9lx")==null?0.0:Double.parseDouble(xmyslist.get(0).get("ycsmb9lx")+"");
            Double cs10 = xmyslist.get(0).get("ycsmb10lx")==null?0.0:Double.parseDouble(xmyslist.get(0).get("ycsmb10lx")+"");
            Double cs11 = xmyslist.get(0).get("ycsmb11lx")==null?0.0:Double.parseDouble(xmyslist.get(0).get("ycsmb11lx")+"");
            Double cs12 = xmyslist.get(0).get("ycsmb12lx")==null?0.0:Double.parseDouble(xmyslist.get(0).get("ycsmb12lx")+"");

            rtnjbdata.put("mj1",mj1);
            rtnjbdata.put("mj2",mj2);
            rtnjbdata.put("mj3",mj3);
            rtnjbdata.put("mj4",mj4);
            rtnjbdata.put("mj5",mj5);
            rtnjbdata.put("mj6",mj6);
            rtnjbdata.put("mj7",mj7);
            rtnjbdata.put("mj8",mj8);
            rtnjbdata.put("mj9",mj9);
            rtnjbdata.put("mj10",mj10);
            rtnjbdata.put("mj11",mj11);
            rtnjbdata.put("mj12",mj12);

            rtnjbdata.put("cjzmj",cjzmj);

            rtnjbdata.put("cs1",cs1);
            rtnjbdata.put("cs2",cs2);
            rtnjbdata.put("cs3",cs3);
            rtnjbdata.put("cs4",cs4);
            rtnjbdata.put("cs5",cs5);
            rtnjbdata.put("cs6",cs6);
            rtnjbdata.put("cs7",cs7);
            rtnjbdata.put("cs8",cs8);
            rtnjbdata.put("cs9",cs9);
            rtnjbdata.put("cs10",cs10);
            rtnjbdata.put("cs11",cs11);
            rtnjbdata.put("cs12",cs12);
        } else if("zc".equals(valtype)){
            cjbl_qd = xmyslist.get(0).get("cjbl_zcqd")==null?0.0:Double.parseDouble(xmyslist.get(0).get("cjbl_lxqd")+""); 
            cjbl_sm = xmyslist.get(0).get("cjbl_zcsm")==null?0.0:Double.parseDouble(xmyslist.get(0).get("cjbl_lxsm")+""); 
            rtnjbdata.put("cjbl_qd",cjbl_qd);
            rtnjbdata.put("cjbl_sm",cjbl_sm);
            Double cjzmj = xmyslist.get(0).get("cjzmj_zc")==null?0.0:Double.parseDouble(xmyslist.get(0).get("cjzmj_zc")+""); //成交总面积(正常) cjzmj_zc

            for(int i=1;i<13;i++){
                rtnjbdata.put("mj"+i,xmyslist.get(0).get("ymbmj"+i)==null?0.0:Double.parseDouble(xmyslist.get(0).get("ymbmj"+i)+""));
                rtnjbdata.put("cs"+i,xmyslist.get(0).get("ycsmb"+i+"zc")==null?0.0:Double.parseDouble(xmyslist.get(0).get("ycsmb"+i+"zc")+""));
            }
            rtnjbdata.put("cjzmj",cjzmj);
        }
    
        //jsa=xmysghjsonarr.toString();
        jsonmsg.put("success", true);
        jsonmsg.put("message", xmysid);
        jsonmsg.put("data", rtnjbdata);
    } else if("upd_yfxmys".equals(type)){ //单个项目编辑月份预算
        String yfys = request.getParameter("yfys")==null?"":java.net.URLDecoder.decode(request.getParameter("yfys")+"","UTF-8");
        String valtype = request.getParameter("valtype")==null?"":request.getParameter("valtype")+"";

        JSONObject xmys_0 = JSONObject.fromObject(yfys);
        //JSONObject cymx_0 = jsonary.getJSONObject(i); 
			//out.print(cydx.get("cydx"));

        String id = xmys_0.getString("xmysid");

        CCObject xmys = new CCObject("xmys");
		xmys.put("id",id); //维护的对象

        double[] mjList = new double[12];
        double[] csList = new double[12];
        Double totmj =0.0;
        Double totcs =0.0;

        if("lx".equals(valtype)){
            for(int i=0;i<12;i++) {
                mjList[i]= Double.valueOf(xmys_0.getString("mj"+(i+1)));
                xmys.put("ymbmj"+(i+1)+"lx",mjList[i]);
                totmj += mjList[i];
            
                csList[i]= Double.valueOf(xmys_0.getString("cs"+(i+1)));
                xmys.put("ycsmb"+(i+1)+"lx",csList[i]);
                totcs += csList[i];
            }
            xmys.put("cjmjhjlx",totmj);   //cjmjhjlx 成交面积合计(理想)
            xmys.put("cshjlx",totcs); //cshjlx 理想创收合计
            
            cs.updateLt(xmys);//更新记录

            jsonmsg.put("success", true);
            jsonmsg.put("message", id);
        } else if("zc".equals(valtype)){
            for(int i=0;i<12;i++) {
                mjList[i]= Double.valueOf(xmys_0.getString("mj"+(i+1)));
                xmys.put("ymbmj"+(i+1),mjList[i]);
                totmj += mjList[i];
            
                csList[i]= Double.valueOf(xmys_0.getString("cs"+(i+1)));
                xmys.put("ycsmb"+(i+1)+"zc",csList[i]);
                totcs += csList[i];
            }
            xmys.put("cjmjhjzc",totmj);   //cjmjhjlx 成交面积合计(理想)
            xmys.put("cshjzc",totcs); //cshjlx 理想创收合计
            
            cs.updateLt(xmys);//更新记录

            jsonmsg.put("success", true);
            jsonmsg.put("message", id);
        } else{
            jsonmsg.put("success", false);
            jsonmsg.put("message", "wrong valtype");
        }
    } else if("ins_hktable".equals(type)){ //预算回款汇总表
        //获取年份
        //type = request.getParameter("type")==null?"":encodeParameters(request.getParameter("type")+"");//客户id
        int nd = request.getParameter("nd")==null?0:Integer.parseInt(request.getParameter("nd")+"");

        //获取部门预算,以年度为条件
        List<CCObject> bmyslist = cs.cqlQuery("bmys","SELECT id,name FROM bmys WHERE nd='"+nd+"' and is_deleted ='0' ");
        String bmysid = bmyslist.get(0).get("id")==null?"":bmyslist.get(0).get("id")+ ""; //simple id a492020E3AFF2EB3W5AQ

        //获取上一年的部门预算
        int lastnd = nd -1;
        List<CCObject> lastbmyslist = cs.cqlQuery("bmys","SELECT id,name FROM bmys WHERE nd='"+lastnd+"' and is_deleted ='0' ");
        String lastbmysid = lastbmyslist.get(0).get("id")==null?"":lastbmyslist.get(0).get("id")+ ""; //simple id a492020E3AFF2EB3W5AQ
        
        //获取项目预算
        List<CCObject> xmyslist = cs.cqlQuery("xmys","SELECT * FROM xmys WHERE bm='"+bmysid +"' and is_deleted='0'");

        //预算中的创收目标是下月的回款目标,那么1月的回款目标为上一年的12月的正常创收目标
        for(CCObject item:xmyslist){ 
            //double[] ysList = new double[12]; //预算数组
            String id = item.get("id")==null?"":item.get("id")+ ""; //记录id
            String projectid = item.get("xmmc")==null?"":item.get("xmmc")+ ""; //项目id
            List<CCObject> projectlist = cs.cqlQuery("Project","SELECT id, name FROM Project WHERE id = '"+projectid+"' and is_deleted='0'"); 
            String pjnm = projectlist.get(0).get("name")==null?"":projectlist.get(0).get("name")+""; 
            //类型 : 面积和创收
            rtnjbdata.put("id",id); //项目预算记录id
            rtnjbdata.put("pjnm",pjnm);

            //获取上一年12月的正常创收目标
            Double ys12 = 0.0;
            List<CCObject> xmys12list = cs.cqlQuery("xmys","SELECT ycsmb12zc FROM xmys WHERE bm='"+lastbmysid +"' and xmmc = '"+projectid +"' and is_deleted='0'");
            if(xmys12list.size()==1){
                ys12 = xmys12list.get(0).get("ycsmb12zc")==null?0.0:Double.parseDouble(xmys12list.get(0).get("ycsmb12zc")+"");
            } else{
                ys12 = 0.0;
            }
            rtnjbdata.put("ys1",ys12);
            Double totalval=0.0; //累计预算
            Double totalsyval=0.0; //累计收益
            String valnm = "";
            //取nd下的1月到11月的预算给到2月到12月的回款目标
            for(int i=1;i<12;i++){
                valnm = "ycsmb"+i+"zc";
                Double value = item.get(valnm)==null?0.0:Double.parseDouble(item.get(valnm)+"");
                totalval += value;
                rtnjbdata.put("ys"+(i+1),value);
            }
            rtnjbdata.put("yshj",totalval+ys12);

            for(int i=1;i<13;i++){
                // valnm = "ycsmb"+i+"zc";
                // Double value = item.get(valnm)==null?0.0:Double.parseDouble(item.get(valnm)+"");
                // totalval += value;
                // rtnjbdata.put("ys"+i,value);

                
                List<CCObject> hklist = cs.cqlQuery("hkjl","select ifnull(ROUND(sum(h.sjsy),2),0) as sjsy from hkjl h left join cloudccskjh s on h.skjhmc = s.id where s.xmmc = '"+projectid+"' and hkqrzt='已确认' and month(h.khrq)= '"+i+"' and year(h.khrq)='2020' and h.is_deleted='0' "); 
                Double syvalue = hklist.get(0).get("sjsy")==null?0.0:Double.parseDouble(hklist.get(0).get("sjsy")+"");
                rtnjbdata.put("sj"+i,syvalue);
                totalsyval += syvalue;
                //ROUND(sum(cjmj),3)
                //select * from hkjl h left join cloudccskjh s on h.skjhmc = s.id where s.xmmc = 'a052018119B8FB52E6PM' and hkqrzt='已确认' and month(h.khrq)= '12' and year(h.khrq)='2020' 
            }
            //rtnjbdata.put("yshj",totalval);
            rtnjbdata.put("sjhj",totalsyval);
            rtnMsg.add(rtnjbdata);
        }

            jsonmsg.put("success", true);
            jsonmsg.put("message", bmysid);
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