<cc:page type="normal" style="standard" showSidebar="false" showHeader="false" />
<cc!>
/*
description:结算开票接口 
1.结算接口:传入一个或者多个业务机会id,备注, 代理费结算比例; type(js),oppoid,bz,dlfjsbl
version: 1.0
date:20190614
author:tom
log:
20190806 增加数据简报接口
20190822 配合简报出累计回款金额
20191021 增加拆佣明细的insert操作
20191104 在接口中去获取提成比例
20191119 代理结算明细表里面的签约时间取业务机会中的成交时间,以免时间不匹配造成表错误
20191125 增加挞定调用接口
20191128 结算单编辑接口改为接受并直接修改结算金额
20200428 加入管理简报数据的接口操作
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

	
	if("js".equals(type)){ //业务机会结算
		//out.print("hello");
		//获取选择的业务机会id  多个id什么类型,怎么处理 (传入改为字符串, 直接用string获取)
		//JSONArray array = JSONArray.fromObject(request.getParameter("oppoid")==null?"":encodeParameters(request.getParameter("oppoid")+""));
		oppoid = request.getParameter("oppoid")==null?"":encodeParameters(request.getParameter("oppoid")+"");
		//两种处理方式, 1将oppoid["00220192D951A66k97Iq","002201966B45A94uYIPT"]改成sql的('xx','xx')的形式, 然后遍历ccobject 2将oppoid改为jsonarrary然后遍历. 暂取第一种
		//oppoid = oppoid.replace("[", "(");
		oppoid = "(" + oppoid ;
		oppoid = oppoid + ")";
		oppoid = oppoid.replace("\"", "\'");
		//out.print("1");
		//out.print(oppoid);

		//获取代理费结算比例
		dlfjsbl = request.getParameter("dlfjsbl")==null?0.0:Double.valueOf(encodeParameters(request.getParameter("dlfjsbl")+""));
		//out.print("2");
		//out.print(dlfjsbl);
	   //获取备注
		//bz = request.getParameter("bz")==null?"":encodeParameters(request.getParameter("bz")+"");
		bz = request.getParameter("bz")==null?"":java.net.URLDecoder.decode(request.getParameter("bz")+"","UTF-8");
		out.print("3");
		out.print(bz);

		// List<String> list = new ArrayList<String>(); 
		// JSONArray jsonArray = JSONArray.fromObject(oppoid);//把String转换为json 
		// list = JSONArray.toList(jsonArray);//这里的t是Class<T>
		String sql="select * from Opportunity where id in "+oppoid+" and is_deleted='0'";
		out.print(sql);
		List<CCObject> oppolist = cs.cqlQuery("Opportunity",sql);
		out.print(String.valueOf(oppolist.size()));
		//项目名称, 年月(给什么?成交日期/结算时日期?),成交单位,来访渠道(?),面积,成交单价,签约时间,代理费结算比例,开票金额,结佣状态,类型,所有人,创建人,客户/业主名称,月租金/总价,确认时间
		String xmmc ="";
		int nd = 0;
		int yf = 0;
		String cjdw ="";
		Double cjmj = 0.0;
		String rztj2 = ""; //认知途径决定来访渠道
		Double cjdj = 0.0;
		String qysj = "";
		String recordtype = ""; //记录类型决定是租赁还是销售
		//所有人和创建人取项目的项目经理?
		String khmc = ""; //客户和业主名称 取客户id
		//月租金/总价可计算
		//确认时间由项目经理确认得到
        Double cjzj = 0.0;
        String ywjh ="";

		//for(String item:list){
		for(CCObject item:oppolist){
            out.print("x");
            ywjh = item.get("id")==null?"":item.get("id")+ ""; 
			xmmc = item.get("xmmc")==null?"":item.get("xmmc")+ ""; 
			qysj = item.get("cjsj")==null?"":item.get("cjsj")+ ""; //签约时间取业务机会的成交时间
			cal.setTime(df.parse(qysj));
			nd = cal.get(Calendar.YEAR);//当前年份
			yf = cal.get(Calendar.MONTH)+1;//当前月份
			Date t = df.parse(qysj);
			qysj= df.format(t);
			khmc = item.get("khmc")==null?"":item.get("khmc")+ ""; //获取客户名称, 去获取认知途径
			List<CCObject> kh = cs.cqlQuery("Account","select rztj2 from Account where id = '"+khmc+"' and is_deleted='0'");
			rztj2 = "";
			if(kh.size()>0){
				rztj2 = kh.get(0).get("rztj2")==null?"":kh.get(0).get("rztj2")+ ""; 
			}
			cjdw = item.get("cjdw")==null?"":item.get("cjdw")+ "";
			cjmj = item.get("cjmj")==null?0.0:Double.parseDouble(item.get("cjmj")+"");
			cjdj = item.get("cjdj")==null?0.0:Double.parseDouble(item.get("cjdj")+"");
			recordtype = item.get("recordtype")==null?"":item.get("recordtype")+ "";
			cjzj = item.get("cjzj")==null?0.0:Double.parseDouble(item.get("cjzj")+"");

			//out.print(item);
			CCObject dljsmxb = new CCObject("dljsmxb");
			dljsmxb.put("xmmc",xmmc); //项目名称
			dljsmxb.put("nd",nd); //年
            dljsmxb.put("yf",yf); //月
            dljsmxb.put("ywjh",ywjh); //业务就会
			//序号- 对应结算编号 自动
			//房号 cjdw
			dljsmxb.put("fh",cjdw);
			//来访渠道
			if("甲方渠道转介".equals(rztj2)){
				dljsmxb.put("lfqd","甲方渠道");
			} else if("瑞信行渠道转介".equals(rztj2)){
				dljsmxb.put("lfqd","瑞信行渠道");
			} else{
				dljsmxb.put("lfqd","上门客户");
			}
			//面积
			dljsmxb.put("mj",cjmj);
			//成交单价
			dljsmxb.put("cjsj",cjdj);
			//签约时间
			dljsmxb.put("qysj",qysj);
			//代理费结算比例(%) dlfjsbl  比例另建对象, 在对象中去取,租金的比例以1月为基准,1代表一月
			dljsmxb.put("dlfjsbl",dlfjsbl);
			//代理费结算金额(开票额) dlfjsje
			//dljsmxb.put("dlfjsje",(cjmj*cjdj*dlfjsbl/100));
			//结佣状态变更为待确认
			dljsmxb.put("jyzt","待确认");
			//备注
			dljsmxb.put("bz",bz);
		
			dljsmxb.put("ownerid",userid);  //ownerid赋值为业务机会对应的项目经理
			dljsmxb.put("createbyid",userid);  //createbyid赋值为项目经理
			//记录类型 区分租赁 或 销售 类型
			if("2018BD60B25D1A2mLTd7".equals(recordtype)){ //租赁
				dljsmxb.put("recordtype","201877BBCEB2536vZE9X"); //租赁
				dljsmxb.put("khmc",khmc); //客户名称
				dljsmxb.put("yzj",(cjmj*cjdj)); //月租金
				dljsmxb.put("dlfjsje",(cjmj*cjdj*dlfjsbl/100));//代理费结算金额(开票额) 
                dljsmxb.put("sjsy",(cjmj*cjdj*dlfjsbl/100));//实际收益
				dljsmxb.put("qrsj",qysj); //确认时间
			} else{
				dljsmxb.put("recordtype","2018B1EF3529342cl95M"); //销售
				dljsmxb.put("yzmc",khmc); //业主名称
				dljsmxb.put("cjzj",cjzj); //成交总价
				dljsmxb.put("dlfjsje",(cjzj*dlfjsbl/100));//代理费结算金额(开票额)
                dljsmxb.put("sjsy",(cjzj*dlfjsbl/100));//实际收益
				dljsmxb.put("rgsj",qysj); //认购时间
			}
		
			cs.insert(dljsmxb);//添加记录
		}
		//oppoid = request.getParameter("oppoid");
		//String s= java.net.URLDecoder.decode(oppoid, "utf-8");
		//out.print("s:");
		//out.print("<script>alert(oppoid);</script>");
		//out.print(s);
		
		//out.print("oppoid:");
        //out.print(oppoid);
        jsonmsg.put("success", true);
        jsonmsg.put("message", oppoid +"/"+dlfjsbl+"/"+bz+"/"+qysj);
	}else if ("td".equals(type)){  //挞定生成对应的结算单
		oppoid = request.getParameter("oppoid")==null?"":encodeParameters(request.getParameter("oppoid")+""); //获取对应业务机会id
		out.print("td");
		//获取代理费结算金额
		Double dlfjsje = request.getParameter("dlfjsje")==null?0.0:Double.valueOf(encodeParameters(request.getParameter("dlfjsje")+""));
	   //获取备注
		//bz = request.getParameter("bz")==null?"":encodeParameters(request.getParameter("bz")+"");
		bz = request.getParameter("bz")==null?"":java.net.URLDecoder.decode(request.getParameter("bz")+"","UTF-8");

		String sql="select * from Opportunity where id = '"+oppoid+"' and is_deleted='0'";
		out.print(sql);
		List<CCObject> oppolist = cs.cqlQuery("Opportunity",sql);

		//项目名称, 年月(给什么?成交日期/结算时日期?),成交单位,来访渠道(?),面积,成交单价,签约时间,代理费结算比例,开票金额,结佣状态,类型,所有人,创建人,客户/业主名称,月租金/总价,确认时间
		String xmmc ="";
		int nd = 0;
		int yf = 0;
		String cjdw ="";
		Double cjmj = 0.0;
		String rztj2 = ""; //认知途径决定来访渠道
		Double cjdj = 0.0;
		String qysj = "";
		String recordtype = ""; //记录类型决定是租赁还是销售
		//所有人和创建人取项目的项目经理?
		String khmc = ""; //客户和业主名称 取客户id
		//月租金/总价可计算
		//确认时间由项目经理确认得到
        Double cjzj = 0.0;
		String ywjh ="";
		
		out.print(String.valueOf(oppolist.size()));

		if(oppolist.size()==1){
			out.print("td11");
            ywjh = oppolist.get(0).get("id")==null?"":oppolist.get(0).get("id")+ ""; 
			xmmc = oppolist.get(0).get("xmmc")==null?"":oppolist.get(0).get("xmmc")+ ""; 
			qysj = oppolist.get(0).get("cjsj")==null?"":oppolist.get(0).get("cjsj")+ ""; //签约时间取业务机会的成交时间
			cal.setTime(df.parse(qysj));
			nd = cal.get(Calendar.YEAR);//当前年份
			yf = cal.get(Calendar.MONTH)+1;//当前月份
			Date t = df.parse(qysj);
			qysj= df.format(t);
			khmc = oppolist.get(0).get("khmc")==null?"":oppolist.get(0).get("khmc")+ ""; //获取客户名称, 去获取认知途径
			List<CCObject> kh = cs.cqlQuery("Account","select rztj2 from Account where id = '"+khmc+"' and is_deleted='0'");
			rztj2 = "";
			if(kh.size()>0){
				rztj2 = kh.get(0).get("rztj2")==null?"":kh.get(0).get("rztj2")+ ""; 
			}
			cjdw = oppolist.get(0).get("cjdw")==null?"":oppolist.get(0).get("cjdw")+ "";
			cjmj = oppolist.get(0).get("cjmj")==null?0.0:Double.parseDouble(oppolist.get(0).get("cjmj")+"");
			cjdj = oppolist.get(0).get("cjdj")==null?0.0:Double.parseDouble(oppolist.get(0).get("cjdj")+"");
			recordtype = oppolist.get(0).get("recordtype")==null?"":oppolist.get(0).get("recordtype")+ "";
			cjzj = oppolist.get(0).get("cjzj")==null?0.0:Double.parseDouble(oppolist.get(0).get("cjzj")+"");

			//out.print(item);
			CCObject dljsmxb = new CCObject("dljsmxb");
			dljsmxb.put("xmmc",xmmc); //项目名称
			dljsmxb.put("nd",nd); //年
            dljsmxb.put("yf",yf); //月
            dljsmxb.put("ywjh",ywjh); //业务就会
			//序号- 对应结算编号 自动
			//房号 cjdw
			dljsmxb.put("fh",cjdw);
			//来访渠道
			if("甲方渠道转介".equals(rztj2)){
				dljsmxb.put("lfqd","甲方渠道");
			} else if("瑞信行渠道转介".equals(rztj2)){
				dljsmxb.put("lfqd","瑞信行渠道");
			} else{
				dljsmxb.put("lfqd","上门客户");
			}
			//面积
			dljsmxb.put("mj",cjmj);
			//成交单价
			dljsmxb.put("cjsj",cjdj);
			//签约时间
			dljsmxb.put("qysj",qysj);
			//代理费结算比例(%) dlfjsbl  比例另建对象, 在对象中去取,租金的比例以1月为基准,1代表一月
			dljsmxb.put("dlfjsbl",0);
			//代理费结算金额(开票额) dlfjsje
			dljsmxb.put("dlfjsje",dlfjsje);
            dljsmxb.put("sjsy",dlfjsje); //实际收益
			//结佣状态变更为待确认
			dljsmxb.put("jyzt","待确认");
			//备注
			dljsmxb.put("bz",bz);
		
			dljsmxb.put("ownerid",userid);  //ownerid赋值为业务机会对应的项目经理
			dljsmxb.put("createbyid",userid);  //createbyid赋值为项目经理
			//记录类型 区分租赁 或 销售 类型
			if("2018BD60B25D1A2mLTd7".equals(recordtype)){ //租赁
				dljsmxb.put("recordtype","201877BBCEB2536vZE9X"); //租赁
				dljsmxb.put("khmc",khmc); //客户名称
				dljsmxb.put("yzj",(cjmj*cjdj)); //月租金
				//dljsmxb.put("dlfjsje",(cjmj*cjdj*dlfjsbl/100));//代理费结算金额(开票额) 
				dljsmxb.put("qrsj",qysj); //确认时间
			} else{
				dljsmxb.put("recordtype","2018B1EF3529342cl95M"); //销售
				dljsmxb.put("yzmc",khmc); //业主名称
				dljsmxb.put("cjzj",cjzj); //成交总价
				//dljsmxb.put("dlfjsje",(cjzj*dlfjsbl/100));//代理费结算金额(开票额) 
				dljsmxb.put("rgsj",qysj); //认购时间
			}
		
			cs.insert(dljsmxb);//添加记录
			jsonmsg.put("success", true);
			jsonmsg.put("message", oppoid +"/"+dlfjsbl+"/"+bz+"/"+qysj);
		} else{
			jsonmsg.put("success", false);
			jsonmsg.put("message", oppoid +"/"+dlfjsbl+"/"+bz+"/"+qysj);
		}
		//oppoid = request.getParameter("oppoid");
		//String s= java.net.URLDecoder.decode(oppoid, "utf-8");
		//out.print("s:");
		//out.print("<script>alert(oppoid);</script>");
		//out.print(s);
		
		//out.print("oppoid:");
        //out.print(oppoid);
        //jsonmsg.put("success", true);
		//jsonmsg.put("message", oppoid +"/"+dlfjsbl+"/"+bz+"/"+qysj);
		

	}else if ("jsop_dqr_qr".equals(type)){
		out.print("yoyo");
		//获取结算单id, 然后将状态从'待确认'改为'已确认'
		String jsid = request.getParameter("jsid")==null?"":encodeParameters(request.getParameter("jsid")+"");

		cs.cqlQuery("dljsmxb","update dljsmxb set jyzt = '已确认' where id='"+jsid+"' and is_deleted='0' ");

		jsonmsg.put("success", true);
        jsonmsg.put("message", jsid);
	} else if ("jsop_dqr_ed".equals(type)){
		out.print("koko");
		//获取结算单id, 新的比例和备注, 计算新的结算金额
		String jsid = request.getParameter("jsid")==null?"":encodeParameters(request.getParameter("jsid")+"");
		out.print(jsid);
		//dlfjsbl = request.getParameter("dlfjsbl")==null?0.0:Double.valueOf(encodeParameters(request.getParameter("dlfjsbl")+""));
		Double dlfjsje = request.getParameter("dlfjsje")==null?0.0:Double.valueOf(encodeParameters(request.getParameter("dlfjsje")+""));
		bz = request.getParameter("bz")==null?"":encodeParameters(request.getParameter("bz")+"");

		List<CCObject> dljsmxb = cs.cqlQuery("dljsmxb","select * from dljsmxb where id='"+jsid+"' and is_deleted='0' ");
		
		//Double cjmj = dljsmxb.get(0).get("mj")==null?0.0:Double.parseDouble(dljsmxb.get(0).get("mj")+"");
		//Double cjdj = dljsmxb.get(0).get("cjsj")==null?0.0:Double.parseDouble(dljsmxb.get(0).get("cjsj")+"");
		//out.print(String.valueOf(cjmj));
		//out.print(String.valueOf(cjdj));
		//out.print(String.valueOf(dlfjsbl));
		CCObject dljsmxb1 = new CCObject("dljsmxb");
		dljsmxb1.put("id",jsid);
		//dljsmxb1.put("dlfjsbl",dlfjsbl);
		//代理费结算金额(开票额) dlfjsje
		//dljsmxb1.put("dlfjsje",(cjmj*cjdj*dlfjsbl/100));
		dljsmxb1.put("dlfjsje",dlfjsje);
		//结佣状态变更为待确认
		dljsmxb1.put("jyzt","待确认");
		//备注
		dljsmxb1.put("bz",bz);
		cs.updateLt(dljsmxb1);

		//cs.cqlQuery("dljsmxb","update dljsmxb set jyzt = '已确认' where id='"+jsid+"' and is_deleted='0' ");

		jsonmsg.put("success", true);
        jsonmsg.put("message", jsid);
	} else if ("jsop_dqr_edfy".equals(type)){ //需分佣渠道
		out.print("kokofy");
		//获取结算单id, 新的比例和备注, 计算新的结算金额
		String jsid = request.getParameter("jsid")==null?"":encodeParameters(request.getParameter("jsid")+"");
		out.print(jsid);
		//dlfjsbl = request.getParameter("dlfjsbl")==null?0.0:Double.valueOf(encodeParameters(request.getParameter("dlfjsbl")+""));
		Double dlfjsje = request.getParameter("dlfjsje")==null?0.0:Double.valueOf(encodeParameters(request.getParameter("dlfjsje")+""));
		bz = request.getParameter("bz")==null?"":encodeParameters(request.getParameter("bz")+"");

		String sfxfqd = request.getParameter("sfxfqd")==null?"":encodeParameters(request.getParameter("sfxfqd")+"");//sfxfqd 是否需分渠道
		Double xfcje = request.getParameter("xfcje")==null?0.0:Double.valueOf(encodeParameters(request.getParameter("xfcje")+""));//xfcje 需分出金额
		Double sjsy = request.getParameter("sjsy")==null?0.0:Double.valueOf(encodeParameters(request.getParameter("sjsy")+""));//sjsy 实际收益

		List<CCObject> dljsmxb = cs.cqlQuery("dljsmxb","select * from dljsmxb where id='"+jsid+"' and is_deleted='0' ");
		
		//Double cjmj = dljsmxb.get(0).get("mj")==null?0.0:Double.parseDouble(dljsmxb.get(0).get("mj")+"");
		//Double cjdj = dljsmxb.get(0).get("cjsj")==null?0.0:Double.parseDouble(dljsmxb.get(0).get("cjsj")+"");
		//out.print(String.valueOf(cjmj));
		//out.print(String.valueOf(cjdj));
		//out.print(String.valueOf(dlfjsbl));
		CCObject dljsmxb1 = new CCObject("dljsmxb");
		dljsmxb1.put("id",jsid);
		//dljsmxb1.put("dlfjsbl",dlfjsbl);
		//代理费结算金额(开票额) dlfjsje
		//dljsmxb1.put("dlfjsje",(cjmj*cjdj*dlfjsbl/100));
		dljsmxb1.put("dlfjsje",dlfjsje);
		//结佣状态变更为待确认
		dljsmxb1.put("jyzt","待确认");
		//备注
		dljsmxb1.put("bz",bz);

		//更新分渠道的部分
		dljsmxb1.put("sfxfqd","是");
		dljsmxb1.put("xfcje",xfcje);
		dljsmxb1.put("sjsy",sjsy);

		//更新
		cs.updateLt(dljsmxb1);

		//cs.cqlQuery("dljsmxb","update dljsmxb set jyzt = '已确认' where id='"+jsid+"' and is_deleted='0' ");

		jsonmsg.put("success", true);
        jsonmsg.put("message", jsid);
	}else if ("jsop_dqr_sc".equals(type)){
		out.print("soso");
		//获取结算单id, 删除结算单
		String jsid = request.getParameter("jsid")==null?"":encodeParameters(request.getParameter("jsid")+"");
		out.print(jsid);

		cs.cqlQuery("dljsmxb","update dljsmxb set is_deleted='1' where id='"+jsid+"' and is_deleted='0' ");

		jsonmsg.put("success", true);
        jsonmsg.put("message", jsid);
	} else if ("jsop_dqr_ht".equals(type)){
		out.print("soso");
		//获取结算单id, 回退状态为待确认
		String jsid = request.getParameter("jsid")==null?"":encodeParameters(request.getParameter("jsid")+"");
		out.print(jsid);

		cs.cqlQuery("dljsmxb","update dljsmxb set jyzt = '待确认' where id='"+jsid+"' and is_deleted='0' ");

		jsonmsg.put("success", true);
        jsonmsg.put("message", jsid);
	} else if ("jsop_qr_kp".equals(type)){
		out.print("kpkp");
		//获取结算单id, 回退状态为待确认
		String jsid = request.getParameter("jsid")==null?"":encodeParameters(request.getParameter("jsid")+"");
		jsid = jsid.replace("[", "");
		jsid = jsid.replace("]", "");
		jsid = jsid.replace("\"", "");
		jsid = jsid.replace(",", ";");
		out.print("1");
		out.print(jsid);
		//cs.cqlQuery("dljsmxb","update dljsmxb set jyzt = '未确认' where id='"+jsid+"' and is_deleted='0' ");
		
		String xmmc = request.getParameter("xmmc")==null?"":encodeParameters(request.getParameter("xmmc")+"");
		//String fplx = request.getParameter("fplx")==null?"":encodeParameters(request.getParameter("fplx")+"");
		String fplx = request.getParameter("fplx")==null?"":java.net.URLDecoder.decode(request.getParameter("fplx")+"","UTF-8");
		//String fptt = request.getParameter("fptt")==null?"":encodeParameters(request.getParameter("fptt")+"");
		String fptt = request.getParameter("fptt")==null?"":java.net.URLDecoder.decode(request.getParameter("fptt")+"","UTF-8");
		//String fpnr = request.getParameter("fpnr")==null?"":encodeParameters(request.getParameter("fpnr")+"");
		String fpnr = request.getParameter("fpnr")==null?"":java.net.URLDecoder.decode(request.getParameter("fpnr")+"","UTF-8");
		Double ljyjsje = request.getParameter("ljyjsje")==null?0.0:Double.valueOf(encodeParameters(request.getParameter("ljyjsje")+""));
		Double bcjsbl = request.getParameter("bcjsbl")==null?0.0:Double.valueOf(encodeParameters(request.getParameter("bcjsbl")+""));
		Double ljkpje = request.getParameter("ljkpje")==null?0.0:Double.valueOf(encodeParameters(request.getParameter("ljkpje")+""));
        Double ljsjsy = request.getParameter("ljsjsy")==null?0.0:Double.valueOf(encodeParameters(request.getParameter("ljsjsy")+""));
		//bz = request.getParameter("bz")==null?"":encodeParameters(request.getParameter("bz")+"");
		bz = request.getParameter("bz")==null?"":java.net.URLDecoder.decode(request.getParameter("bz")+"","UTF-8");

		CCObject kpsq = new CCObject("kpsq");
		kpsq.put("xmmc",xmmc); //项目名称
		kpsq.put("jsmx",jsid); 
		kpsq.put("fplx",fplx); 
		kpsq.put("fptt",fptt); 
		kpsq.put("fpnr",fpnr);
		kpsq.put("ljyjsje",ljyjsje); 
		kpsq.put("bcjsbl",bcjsbl);
        kpsq.put("sjsy",ljyjsje); 
		kpsq.put("fpje",ljkpje);
		kpsq.put("bz",bz);

        kpsq.put("spzt","草稿");
        kpsq.put("hkzt","未回款");
		kpsq.put("ownerid",userid);  //ownerid赋值为业务机会对应的项目经理
		kpsq.put("createbyid",userid);  //createbyid赋值为项目经理
		cs.insert(kpsq);

		jsonmsg.put("success", true);
        jsonmsg.put("message", jsid);
	} else if ("kpop_cg_sc".equals(type)){
		out.print("kpde");
		//获取结算单id, 删除结算单
		String kpsqid = request.getParameter("kpsqid")==null?"":encodeParameters(request.getParameter("kpsqid")+"");
		out.print(kpsqid);

		cs.cqlQuery("kpsq","update kpsq set is_deleted='1' where id='"+kpsqid+"' and is_deleted='0' ");

		jsonmsg.put("success", true);
        jsonmsg.put("message", kpsqid);
	} else if ("sjjb_zb".equals(type)){
		out.print("sjjb_zb");
		//根据获取到的项目id,回项目的简报数据详情
		String projectid_list = request.getParameter("projectid_list")==null?"":encodeParameters(request.getParameter("projectid_list")+"");
		out.print(projectid_list);
		//projectid_list=projectid_list.Substring(0,projectid_list.Length-1); //去掉最后一个多余的逗号
		projectid_list = projectid_list.substring(0,projectid_list.length() -1);
		String[] arr=projectid_list.split("\\,");

		//获取时间周期 ksrq_bd  jsrq_bd
		String ksrq_bd = request.getParameter("ksrq")==null?"":encodeParameters(request.getParameter("ksrq")+"");
		String jsrq_bd = request.getParameter("jsrq")==null?"":encodeParameters(request.getParameter("jsrq")+"");

		//定义一个专门供sql使用的date字段, 根据不同的对象进行组合
		String date_sql ="";
		//成交手数cjss,合计成交面积hjcjmj,合计成交额hjcje,结算单数量jsdsl,待确认数dqrs,已确认数yqrs,部分开票数bfkps,全部开票数qbkps,累计结算金额ljjsje,开票次数kpcs,累计开票金额ljkpje


		for(String s : arr){
			//项目名称
			List<CCObject> xm_l = cs.cqlQuery("Project","select name from Project where id='"+s+"' and is_deleted='0'");
			String xmmc = xm_l.get(0).get("name")==null?"":xm_l.get(0).get("name")+"";
			//成交情况
			date_sql = 	"select count(*) as cjss,ROUND(sum(cjmj),3) as hjcjmj, ROUND(sum(cjdj*cjmj),2) as hjcje from Opportunity where xmmc='"+s+"' and is_deleted='0' and spzt='审批通过' and jieduan='成交' and cjsj >= str_to_date('"+ksrq_bd+" 00:00:00', '%Y-%m-%d %H:%i:%s')  AND cjsj <= str_to_date('"+jsrq_bd+" 23:59:59', '%Y-%m-%d %H:%i:%s')";
			//date_sql = " and cjsj >= str_to_date('"+ksrq_bd+" 00:00:00', '%Y-%m-%d %H:%i:%s')  AND cjsj <= str_to_date('"+jsrq_bd+" 23:59:59', '%Y-%m-%d %H:%i:%s') ";
			//List<CCObject> oopo_l = cs.cqlQuery("Opportunity","select count(*) as cjss,ROUND(sum(cjmj),3) as hjcjmj, ROUND(sum(cjdj*cjmj),2) as hjcje from Opportunity where xmmc='"+s+"' and is_deleted='0' and spzt='审批通过'"+ date_sql+" and jieduan='成交' ");
			List<CCObject> oopo_l = cs.cqlQuery("Opportunity",date_sql);
			int cjss = oopo_l.get(0).get("cjss")==null?0:Integer.parseInt(oopo_l.get(0).get("cjss")+"");
			Double hjcjmj = oopo_l.get(0).get("hjcjmj")==null?0.0:Double.parseDouble(oopo_l.get(0).get("hjcjmj")+"");
			Double hjcje = oopo_l.get(0).get("hjcje")==null?0.0:Double.parseDouble(oopo_l.get(0).get("hjcje")+"");
			//结算情况
			date_sql = " and qysj >= str_to_date('"+ksrq_bd+" 00:00:00', '%Y-%m-%d %H:%i:%s')  AND qysj <= str_to_date('"+jsrq_bd+" 23:59:59', '%Y-%m-%d %H:%i:%s') ";
			List<CCObject> dljsmxb_zs = cs.cqlQuery("dljsmxb","select count(*) as jsdsl from dljsmxb where xmmc='"+s+"' and is_deleted='0'"+date_sql);
			int jsdsl = dljsmxb_zs.get(0).get("jsdsl")==null?0:Integer.parseInt(dljsmxb_zs.get(0).get("jsdsl")+"");
			List<CCObject> dljsmxb_dqr = cs.cqlQuery("dljsmxb","select count(*) as dqrs from dljsmxb where xmmc='"+s+"' and is_deleted='0' and jyzt='待确认'"+date_sql);
			int dqrs = dljsmxb_dqr.get(0).get("dqrs")==null?0:Integer.parseInt(dljsmxb_dqr.get(0).get("dqrs")+"");
			List<CCObject> dljsmxb_yqr = cs.cqlQuery("dljsmxb","select count(*) as yqrs from dljsmxb where xmmc='"+s+"' and is_deleted='0' and jyzt='已确认'"+date_sql);
			int yqrs = dljsmxb_yqr.get(0).get("yqrs")==null?0:Integer.parseInt(dljsmxb_yqr.get(0).get("yqrs")+"");
			List<CCObject> dljsmxb_bfkps = cs.cqlQuery("dljsmxb","select count(*) as bfkps from dljsmxb where xmmc='"+s+"' and is_deleted='0' and jyzt='部分开票'"+date_sql);
			int bfkps = dljsmxb_bfkps.get(0).get("bfkps")==null?0:Integer.parseInt(dljsmxb_bfkps.get(0).get("bfkps")+"");
			List<CCObject> dljsmxb_qbkps = cs.cqlQuery("dljsmxb","select count(*) as qbkps from dljsmxb where xmmc='"+s+"' and is_deleted='0' and jyzt='全部开票'"+date_sql);
			int qbkps = dljsmxb_qbkps.get(0).get("qbkps")==null?0:Integer.parseInt(dljsmxb_qbkps.get(0).get("qbkps")+"");
			List<CCObject> dljsmxb_ljjsje = cs.cqlQuery("dljsmxb","select ROUND(sum(dlfjsje),2) as ljjsje from dljsmxb where xmmc='"+s+"' and is_deleted='0'"+date_sql);
			Double ljjsje = dljsmxb_ljjsje.get(0).get("ljjsje")==null?0.0:Double.parseDouble(dljsmxb_ljjsje.get(0).get("ljjsje")+"");
			//开票情况
			date_sql = " and createdate >= str_to_date('"+ksrq_bd+" 00:00:00', '%Y-%m-%d %H:%i:%s')  AND createdate <= str_to_date('"+jsrq_bd+" 23:59:59', '%Y-%m-%d %H:%i:%s') ";
			List<CCObject> kpsq_l = cs.cqlQuery("kpsq","select count(*) as kpcs,ROUND(sum(fpje),2) as ljkpje,ROUND(sum(hkje),2) as ljhkje from kpsq where xmmc='"+s+"' and spzt='审批通过' and is_deleted='0'"+date_sql);
			int kpcs = kpsq_l.get(0).get("kpcs")==null?0:Integer.parseInt(kpsq_l.get(0).get("kpcs")+"");
            Double ljkpje = kpsq_l.get(0).get("ljkpje")==null?0.0:Double.parseDouble(kpsq_l.get(0).get("ljkpje")+"");
            Double ljhkje = kpsq_l.get(0).get("ljhkje")==null?0.0:Double.parseDouble(kpsq_l.get(0).get("ljhkje")+"");
			rtnjbdata.put("id",s);
			rtnjbdata.put("xmmc",xmmc);
			rtnjbdata.put("cjss",cjss);
			rtnjbdata.put("hjcjmj",hjcjmj);
			rtnjbdata.put("hjcje",hjcje);
			rtnjbdata.put("jsdsl",jsdsl);
			rtnjbdata.put("dqrs",dqrs);
			rtnjbdata.put("yqrs",yqrs);
			rtnjbdata.put("bfkps",bfkps);
			rtnjbdata.put("qbkps",qbkps);
			rtnjbdata.put("ljjsje",ljjsje);
			rtnjbdata.put("kpcs",kpcs);
            rtnjbdata.put("ljkpje",ljkpje);
            rtnjbdata.put("ljhkje",ljhkje);
			rtnMsg.add(rtnjbdata);
		}

		jsonmsg.put("success", true);
		jsonmsg.put("message", projectid_list);
		jsonmsg.put("sql", date_sql);//date_sql
		jsonmsg.put("data", rtnMsg);
	} else if ("gljb_zb".equals(type)){
		out.print("gljb_zb");
		//根据获取到的项目id,回项目的简报数据详情
		String projectid_list = request.getParameter("projectid_list")==null?"":encodeParameters(request.getParameter("projectid_list")+"");
		out.print(projectid_list);
		//projectid_list=projectid_list.Substring(0,projectid_list.Length-1); //去掉最后一个多余的逗号
		projectid_list = projectid_list.substring(0,projectid_list.length() -1);
		String[] arr=projectid_list.split("\\,");

		//获取时间周期 ksrq_bd  jsrq_bd
		String ksrq_bd = request.getParameter("ksrq")==null?"":encodeParameters(request.getParameter("ksrq")+"");
		String jsrq_bd = request.getParameter("jsrq")==null?"":encodeParameters(request.getParameter("jsrq")+"");

		//定义一个专门供sql使用的date字段, 根据不同的对象进行组合
		String date_sql ="";
		//成交手数cjss,合计成交面积hjcjmj,合计成交额hjcje,结算单数量jsdsl,待确认数dqrs,已确认数yqrs,部分开票数bfkps,全部开票数qbkps,累计结算金额ljjsje,开票次数kpcs,累计开票金额ljkpje


		for(String s : arr){
			//项目名称
			List<CCObject> xm_l = cs.cqlQuery("Project","select name from Project where id='"+s+"' and is_deleted='0'");
			String xmmc = xm_l.get(0).get("name")==null?"":xm_l.get(0).get("name")+"";
			// date_sql = 	"select count(*) as cjss,ROUND(sum(cjmj),3) as hjcjmj, ROUND(sum(cjdj*cjmj),2) as hjcje from Opportunity where xmmc='"+s+"' and is_deleted='0' and spzt='审批通过' and jieduan='成交' and cjsj >= str_to_date('"+ksrq_bd+" 00:00:00', '%Y-%m-%d %H:%i:%s')  AND cjsj <= str_to_date('"+jsrq_bd+" 23:59:59', '%Y-%m-%d %H:%i:%s')";
			// //date_sql = " and cjsj >= str_to_date('"+ksrq_bd+" 00:00:00', '%Y-%m-%d %H:%i:%s')  AND cjsj <= str_to_date('"+jsrq_bd+" 23:59:59', '%Y-%m-%d %H:%i:%s') ";
			// //List<CCObject> oopo_l = cs.cqlQuery("Opportunity","select count(*) as cjss,ROUND(sum(cjmj),3) as hjcjmj, ROUND(sum(cjdj*cjmj),2) as hjcje from Opportunity where xmmc='"+s+"' and is_deleted='0' and spzt='审批通过'"+ date_sql+" and jieduan='成交' ");
			// List<CCObject> oopo_l = cs.cqlQuery("Opportunity",date_sql);
			// int cjss = oopo_l.get(0).get("cjss")==null?0:Integer.parseInt(oopo_l.get(0).get("cjss")+"");
			// Double hjcjmj = oopo_l.get(0).get("hjcjmj")==null?0.0:Double.parseDouble(oopo_l.get(0).get("hjcjmj")+"");
			// Double hjcje = oopo_l.get(0).get("hjcje")==null?0.0:Double.parseDouble(oopo_l.get(0).get("hjcje")+"");

			//结算情况 , 所有在选取时间类的记录
			date_sql = " and d.qysj >= str_to_date('"+ksrq_bd+" 00:00:00', '%Y-%m-%d %H:%i:%s')  AND d.qysj <= str_to_date('"+jsrq_bd+" 23:59:59', '%Y-%m-%d %H:%i:%s') ";
			
			//结算情况 , 所有在选取时间类的记录
			List<CCObject> dljsmxb_cjjsze = cs.cqlQuery("dljsmxb","select ROUND(SUM(d.dlfjsje),2) as cjjsze_tot, ROUND(SUM(d.sjsy),2) as cjjsze_ws, ROUND(SUM(d.xfcje),2) as cjjsze_qd from dljsmxb d where d.xmmc='"+s+"' and d.is_deleted='0'"+date_sql);
			Double cjjsze_tot = dljsmxb_cjjsze.get(0).get("cjjsze_tot")==null?0.0:Double.parseDouble(dljsmxb_cjjsze.get(0).get("cjjsze_tot")+""); //合计,开票额之和
            Double cjjsze_ws = dljsmxb_cjjsze.get(0).get("cjjsze_ws")==null?0.0:Double.parseDouble(dljsmxb_cjjsze.get(0).get("cjjsze_ws")+""); //我司,实际收益之和
			Double cjjsze_qd = dljsmxb_cjjsze.get(0).get("cjjsze_qd")==null?0.0:Double.parseDouble(dljsmxb_cjjsze.get(0).get("cjjsze_qd")+""); //分给渠道,需分出金额之和
			
			//结算情况 , 未回款中的未开票
            List<CCObject> dljsmxb_whk_wkp = cs.cqlQuery("dljsmxb","select ROUND(SUM(d.dlfjsje),2) as whk_wkp_tot, ROUND(SUM(d.sjsy),2) as whk_wkp_ws, ROUND(SUM(d.xfcje),2) as whk_wkp_qd from dljsmxb d where d.xmmc='"+s+"' and d.jyzt in ('待确认','已确认') and d.is_deleted='0'"+date_sql);
			Double whk_wkp_tot = dljsmxb_whk_wkp.get(0).get("whk_wkp_tot")==null?0.0:Double.parseDouble(dljsmxb_whk_wkp.get(0).get("whk_wkp_tot")+""); //合计,开票额之和
            Double whk_wkp_ws = dljsmxb_whk_wkp.get(0).get("whk_wkp_ws")==null?0.0:Double.parseDouble(dljsmxb_whk_wkp.get(0).get("whk_wkp_ws")+""); //我司,实际收益之和
            Double whk_wkp_qd = dljsmxb_whk_wkp.get(0).get("whk_wkp_qd")==null?0.0:Double.parseDouble(dljsmxb_whk_wkp.get(0).get("whk_wkp_qd")+""); //分给渠道,需分出金额之和
            
            //结算情况,关联开票情况(被包含在开票的结算明细中,开票本身已经审批通过,未回款)
            List<CCObject> dljsmxb_whk_ykp = cs.cqlQuery("dljsmxb","select ROUND(SUM(d.dlfjsje),2) as whk_ykp_tot,ROUND(SUM(IFNULL(d.sjsy,0)),2) as whk_ykp_ws,ROUND(SUM(IFNULL(d.xfcje,0)),2) as whk_ykp_qd from dljsmxb d  where d.is_deleted ='0' and (select count(*) from kpsq k where locate(d.id,k.jsmx) >0 and k.is_deleted='0' and k.spzt='审批通过' and k.hkzt='未回款' ) >0 and d.xmmc='"+s+"' and d.jyzt in ('部分开票','全部开票') and d.is_deleted='0'"+date_sql);
			Double whk_ykp_tot = dljsmxb_whk_ykp.get(0).get("whk_ykp_tot")==null?0.0:Double.parseDouble(dljsmxb_whk_ykp.get(0).get("whk_ykp_tot")+""); //合计,开票额之和
            Double whk_ykp_ws = dljsmxb_whk_ykp.get(0).get("whk_ykp_ws")==null?0.0:Double.parseDouble(dljsmxb_whk_ykp.get(0).get("whk_ykp_ws")+""); //我司,实际收益之和
            Double whk_ykp_qd = dljsmxb_whk_ykp.get(0).get("whk_ykp_qd")==null?0.0:Double.parseDouble(dljsmxb_whk_ykp.get(0).get("whk_ykp_qd")+""); //分给渠道,需分出金额之和
			
			//结算情况,关联开票情况(被包含在开票的结算明细中,开票本身已经审批通过,部分回款或者全部回款)
			List<CCObject> dljsmxb_yhk = cs.cqlQuery("dljsmxb","select ROUND(SUM(d.dlfjsje),2) as yhk_tot,ROUND(SUM(IFNULL(d.sjsy,0)),2) as yhk_ws,ROUND(SUM(IFNULL(d.xfcje,0)),2) as yhk_qd from dljsmxb d  where d.is_deleted ='0' and (select count(*) from kpsq k where locate(d.id,k.jsmx) >0 and k.is_deleted='0' and k.spzt='审批通过' and k.hkzt in ('部分回款','回款完成') ) >0 and d.xmmc='"+s+"' and d.jyzt in ('部分开票','全部开票') and d.is_deleted='0'"+date_sql);
			Double yhk_tot = dljsmxb_yhk.get(0).get("yhk_tot")==null?0.0:Double.parseDouble(dljsmxb_yhk.get(0).get("yhk_tot")+""); //合计,开票额之和
			Double yhk_ws = dljsmxb_yhk.get(0).get("yhk_ws")==null?0.0:Double.parseDouble(dljsmxb_yhk.get(0).get("yhk_ws")+""); //我司,实际收益之和
			Double yhk_qd = dljsmxb_yhk.get(0).get("yhk_qd")==null?0.0:Double.parseDouble(dljsmxb_yhk.get(0).get("yhk_qd")+""); //分给渠道,需分出金额之和

            //财务回款
            String hkdate_sql = " and h.khrq >= str_to_date('"+ksrq_bd+" 00:00:00', '%Y-%m-%d %H:%i:%s')  AND h.khrq <= str_to_date('"+jsrq_bd+" 23:59:59', '%Y-%m-%d %H:%i:%s') ";
			List<CCObject> hkjllist = cs.cqlQuery("hkjl","select ROUND(sum(h.sjsy),2) as sjsy from hkjl h inner join cloudccskjh c on h.skjhmc = c.id where h.is_deleted ='0' and c.xmmc='"+s+"' and h.is_deleted='0'" +hkdate_sql );
			Double yhk_hksjsy = hkjllist.get(0).get("sjsy")==null?0.0:Double.parseDouble(hkjllist.get(0).get("sjsy")+""); //合计,收款实际收益

		
			//开票情况
			// date_sql = " and createdate >= str_to_date('"+ksrq_bd+" 00:00:00', '%Y-%m-%d %H:%i:%s')  AND createdate <= str_to_date('"+jsrq_bd+" 23:59:59', '%Y-%m-%d %H:%i:%s') ";
			// List<CCObject> kpsq_l = cs.cqlQuery("kpsq","select count(*) as kpcs,ROUND(sum(fpje),2) as ljkpje,ROUND(sum(hkje),2) as ljhkje from kpsq where xmmc='"+s+"' and spzt='审批通过' and is_deleted='0'"+date_sql);
			// int kpcs = kpsq_l.get(0).get("kpcs")==null?0:Integer.parseInt(kpsq_l.get(0).get("kpcs")+"");
            // Double ljkpje = kpsq_l.get(0).get("ljkpje")==null?0.0:Double.parseDouble(kpsq_l.get(0).get("ljkpje")+"");
            // Double ljhkje = kpsq_l.get(0).get("ljhkje")==null?0.0:Double.parseDouble(kpsq_l.get(0).get("ljhkje")+"");
			rtnjbdata.put("id",s);
			rtnjbdata.put("xmmc",xmmc);
			rtnjbdata.put("cjjsze_tot",cjjsze_tot);
			rtnjbdata.put("cjjsze_ws",cjjsze_ws);
			rtnjbdata.put("cjjsze_qd",cjjsze_qd);
			rtnjbdata.put("whk_wkp_tot",whk_wkp_tot);
			rtnjbdata.put("whk_wkp_ws",whk_wkp_ws);
			rtnjbdata.put("whk_wkp_qd",whk_wkp_qd);
			rtnjbdata.put("whk_ykp_tot",whk_ykp_tot);
			rtnjbdata.put("whk_ykp_ws",whk_ykp_ws);
			rtnjbdata.put("whk_ykp_qd",whk_ykp_qd);
			rtnjbdata.put("yhk_tot",yhk_tot);
			rtnjbdata.put("yhk_ws",yhk_ws);
			rtnjbdata.put("yhk_qd",yhk_qd);
            rtnjbdata.put("yhk_hksj",yhk_hksjsy);
			rtnMsg.add(rtnjbdata);
		}

		jsonmsg.put("success", true);
		jsonmsg.put("message", projectid_list);
		jsonmsg.put("sql", date_sql);//date_sql
		jsonmsg.put("data", rtnMsg);
	} else if ("cy_insert".equals(type)){
        //String cymx = request.getParameter("cymx")==null?"":encodeParameters(request.getParameter("cymx")+"");
        //String cymx = request.getParameter("cymx")==null?"":URLDecoder.decode(request.getParameter("cymx")+"");
        String cymx = request.getParameter("cymx")==null?"":java.net.URLDecoder.decode(request.getParameter("cymx")+"","UTF-8");
        //out.print(cymx);
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

	}

} catch (Exception e) {
	jsonmsg.put("success", false);

	jsonmsg.put("message", e.getMessage());
} 



request.setAttribute("jsonmsg", jsonmsg.toString());
</cc>
<!-- <html>
	hello
</html>   -->
<cc:forward page="/platform/activity/task/getNodes.jsp"/>