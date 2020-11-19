<cc:page type="normal" style="standard" showSidebar="false" showHeader="false" />
<cc!>
/*
新增 岗位登记api,新增岗位信息
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
	CCService cs = new CCService(userInfo); 
	String uid = userInfo.getUserId();//当前登录用户id 
	List<CCObject> ccuserl = cs.cqlQuery("ccuser","SELECT loginname as name FROM ccuser WHERE id = '"+uid+"'"); //登录人的账号
	String userName =ccuserl.get(0).get("name")==null?"":ccuserl.get(0).get("name")+""; 
	JSONObject jsonmsg = new JSONObject(); // 返回对象
	// String params = request.getParameter("params")==null?"":encodeParameters(request.getParameter("params")+"");//获取post里的数据
	// out.print("1^"+params);
	// JSONArray paramsjson=JSONArray.fromObject(params);
	// out.print("#1"+paramsjson.size());
	//String uid = "0052018470A714CaerX7"; //李荣武id 测试用 
	// String profid = userInfo.getProfileId();//getProfileId当前登录用户的简档id 
try {
	String gwtype = request.getParameter("type")==null?"":encodeParameters(request.getParameter("type")+"");//获取类型,取哪一块的值
	// out.print("1^"+gwtype);
	// 错误填写多次时,可以删除无用的数据
        // 根据条件删除
	if("deledate".equals(gwtype)) {
		// out.print("删除区域");
		String selname = request.getParameter("selname")==null?"":encodeParameters(request.getParameter("selname")+"");//姓名
		// out.print("selname:"+selname);
        String selsfzh = request.getParameter("selsfzh")==null?"":encodeParameters(request.getParameter("selsfzh")+"");//身份证号
		// out.print("selsfzh:"+selsfzh);
        String namesql = ""; // 存储 名称 条件 sql片段
        String sfzhsql = ""; // 存储 身份证号 条件 sql 片段
        if (selname != null && !"".equals(selname)) { // 判断是否输入了名字
            namesql = " and name = '" + selname.trim() + "'";
        }
        if (selsfzh != null && !"".equals(selsfzh)) { // 判断是否输入了身份证号
            sfzhsql = " and sfzh = '" + selsfzh.trim() + "'";
        }
        if ("".equals(selsfzh) && "".equals(selname)) { // 如果 名称  和 身份证号都没填写, 只展示默认值
            namesql = " and name = '' ";
            sfzhsql = " and sfzh = '' ";
		}
		// 删除基本信息
		String deletjbxxsql = "update gwdjb set  is_deleted = '1' where 1=1 and is_deleted='0' " + namesql + sfzhsql;
		// out.print(deletjbxxsql);
		cs.cqlQuery("gwdjb",deletjbxxsql);
		// 删除教育背景的 sql
        String jybjsql = "update gwdjjybj set is_deleted='1' where 1=1 and gwdjlx = 'jybjtype'" + namesql + sfzhsql + " and is_deleted='0'";
        cs.cqlQuery("gwdjjybj",jybjsql);
        // 删除 工作经历的sql
        String gzjlsql = "update gwdjjybj set is_deleted='1' where 1=1 and gwdjlx = 'gzjltype'" + namesql + sfzhsql + " and is_deleted='0'";
        cs.cqlQuery("gwdjjybj",gzjlsql);
        // 删除 社会关系sql
        String jtbjsql = "update gwdjjybj set is_deleted='1' where 1=1 and gwdjlx = 'jtbjtype'" + namesql + sfzhsql + " and is_deleted='0'";
        cs.cqlQuery("gwdjjybj",jtbjsql);
	}else if ("gwdj".equals(gwtype)) {
		// String formDataList = request.getParameter("formDataList")==null?"":encodeParameters(request.getParameter("formDataList")+"");//获取类型,取哪一块的值
		// JSONObject jsonary=JSONObject.fromObject(formDataList);
		out.print("基本信息开始取数!");
		// String param = request.getParameter("param")==null?"":encodeParameters(request.getParameter("param")+"");//数据集合
		// out.print("1^"+param);
		String name = request.getParameter("name")==null?"":encodeParameters(request.getParameter("name")+"");//姓名
		out.print("2^"+name);
		String xb = request.getParameter("xb")==null?"":encodeParameters(request.getParameter("xb")+"");//性别
		String csrqStr = request.getParameter("csrq")==null?"":encodeParameters(request.getParameter("csrq")+"");//出生日期
		Date csrqdate = new SimpleDateFormat("yyyy-MM-dd").parse(csrqStr); 
		String csrq = new SimpleDateFormat("yyyy-MM-dd").format(csrqdate);
		// out.print("2^");
		// out.print("01^"+csrq.toString());
		// String xp = request.getParameter("xp")==null?"":encodeParameters(request.getParameter("xp")+"");//相片
		String hyzk = request.getParameter("hyzk")==null?"":encodeParameters(request.getParameter("hyzk")+"");//婚姻状况
		String baby = request.getParameter("baby")==null?"":encodeParameters(request.getParameter("baby")+"");
		String mz = request.getParameter("mz")==null?"":encodeParameters(request.getParameter("mz")+"");//名族
		String sg = request.getParameter("sg")==null?"":encodeParameters(request.getParameter("sg")+"");//身高
		String jg = request.getParameter("jg")==null?"":encodeParameters(request.getParameter("jg")+"");//籍贯
		// out.print("3^");
		String hkszd = request.getParameter("hkszd")==null?"":encodeParameters(request.getParameter("hkszd")+"");//户口所在地
		String hjlx = request.getParameter("hjlx")==null?"":encodeParameters(request.getParameter("hjlx")+"");//户籍类型
		String chmxjb = request.getParameter("chmxjb")==null?"":encodeParameters(request.getParameter("chmxjb")+"");//曾患慢性疾病/重大疾病
		String mqjkzk = request.getParameter("mqjkzk")==null?"":encodeParameters(request.getParameter("mqjkzk")+"");//目前健康状况
		String cnjynwsyjh = request.getParameter("cnjynwsyjh")==null?"":encodeParameters(request.getParameter("cnjynwsyjh")+"");//承诺近一年无生育计划
		String zgxl = request.getParameter("zgxl")==null?"":encodeParameters(request.getParameter("zgxl")+"");//最高学历
		String zy = request.getParameter("zy")==null?"":encodeParameters(request.getParameter("zy")+"");//专业
		String gznx = request.getParameter("gznx")==null?"":encodeParameters(request.getParameter("gznx")+"");//工作年限
		// out.print("4^");
		String lxdh = request.getParameter("lxdh")==null?"":encodeParameters(request.getParameter("lxdh")+"");//联系电话
		String dzyx = request.getParameter("dzyx")==null?"":encodeParameters(request.getParameter("dzyx")+"");//电子邮箱
		String sfzh = request.getParameter("sfzh")==null?"":encodeParameters(request.getParameter("sfzh")+"");//身份证号
		String mqzz = request.getParameter("mqzz")==null?"":encodeParameters(request.getParameter("mqzz")+"");//目前住址
		String wxh = request.getParameter("wxh")==null?"":encodeParameters(request.getParameter("wxh")+"");//微信号

		String yptj = request.getParameter("yptj")==null?"":encodeParameters(request.getParameter("yptj")+"");//应聘途径: [ 网络/媒体;人才中介/市场; 朋友/员工推荐(需要填写推荐人);猎头公司;校园招聘;其他(需要写哪里推荐) ]
		// out.print("1^"+yptj);
		String jsjsp = request.getParameter("jsjsp")==null?"":encodeParameters(request.getParameter("jsjsp")+"");//计算机水平
		String jszz = request.getParameter("jszz")==null?"":encodeParameters(request.getParameter("jszz")+"");//驾驶执照

		String yysp = request.getParameter("yysp")==null?"":encodeParameters(request.getParameter("yysp")+"");//英语水平
		String yueysp = request.getParameter("yueysp")==null?"":encodeParameters(request.getParameter("yueysp")+"");//粤语水平
		String yyah = request.getParameter("yyah")==null?"":encodeParameters(request.getParameter("yyah")+"");//业余爱好
		String tc = request.getParameter("tc")==null?"":encodeParameters(request.getParameter("tc")+"");//特长
		String jjlxr = request.getParameter("jjlxr")==null?"":encodeParameters(request.getParameter("jjlxr")+"");//紧急联系人姓名
		String jjrgx = request.getParameter("jjrgx")==null?"":encodeParameters(request.getParameter("jjrgx")+"");//紧急人关系
		String jjrdh = request.getParameter("jjrdh")==null?"":encodeParameters(request.getParameter("jjrdh")+"");//紧急人电话
		String jjrdz = request.getParameter("jjrdz")==null?"":encodeParameters(request.getParameter("jjrdz")+"");//紧急人地址
		// out.print("5^");
		String rxhhyxm = request.getParameter("rxhhyxm")==null?"":encodeParameters(request.getParameter("rxhhyxm")+"");//瑞信行好友姓名
		String rxhhygx = request.getParameter("rxhhygx")==null?"":encodeParameters(request.getParameter("rxhhygx")+"");//瑞信行好友关系
		String rxhhydh = request.getParameter("rxhhydh")==null?"":encodeParameters(request.getParameter("rxhhydh")+"");//瑞信行好友电话
		String xqah = request.getParameter("xqah")==null?"":encodeParameters(request.getParameter("xqah")+"");//兴趣爱好
		// String name = jsonary.get("name")==null?"":jsonary.get("name")+"";//姓名
		// String xb = jsonary.get("xb")==null?"":jsonary.get("xb")+"";//性别
		// String csrq = jsonary.get("csrq")==null?"":jsonary.get("csrq")+"";//出生日期
		// // String xp = jsonary.get("xp")==null?"":jsonary.get("xp")+"";//相片
		// String hyzk = jsonary.get("hyzk")==null?"":jsonary.get("hyzk")+"";//婚姻状况
		// String mz = jsonary.get("mz")==null?"":jsonary.get("mz")+"";//名族
		// String sg = jsonary.get("sg")==null?"":jsonary.get("sg")+"";//身高
		// String jg = jsonary.get("jg")==null?"":jsonary.get("jg")+"";//籍贯
		
		// String hkszd = jsonary.get("hkszd")==null?"":jsonary.get("hkszd")+"";//户口所在地
		// String hjlx = jsonary.get("hjlx")==null?"":jsonary.get("hjlx")+"";//户籍类型
		// String chmxjb = jsonary.get("chmxjb")==null?"":jsonary.get("chmxjb")+"";//曾患慢性疾病/重大疾病
		// String mqjkzk = jsonary.get("mqjkzk")==null?"":jsonary.get("mqjkzk")+"";//目前健康状况
		// String cnjynwsyjh = jsonary.get("cnjynwsyjh")==null?"":jsonary.get("cnjynwsyjh")+"";//承诺近一年无生育计划
		// String zgxl = jsonary.get("zgxl")==null?"":jsonary.get("zgxl")+"";//最高学历
		// String zy = jsonary.get("zy")==null?"":jsonary.get("zy")+"";//专业
		// String gznx = jsonary.get("gznx")==null?"":jsonary.get("gznx")+"";//工作年限

		// String lxdh = jsonary.get("lxdh")==null?"":jsonary.get("lxdh")+"";//联系电话
		// String dzyx = jsonary.get("dzyx")==null?"":jsonary.get("dzyx")+"";//电子邮箱
		// String sfzh = jsonary.get("sfzh")==null?"":jsonary.get("sfzh")+"";//身份证号
		// String mqzz = jsonary.get("mqzz")==null?"":jsonary.get("mqzz")+"";//目前住址
		// String wxh = jsonary.get("wxh")==null?"":jsonary.get("wxh")+"";//微信号
		// // String yptj = jsonary.get("yptj")==null?"":jsonary.get("yptj")+"";//应聘途径: [ 网络/媒体;人才中介/市场; 朋友/员工推荐(需要填写推荐人);猎头公司;校园招聘;其他(需要写哪里推荐) ]

		// String jsjsp = jsonary.get("jsjsp")==null?"":jsonary.get("jsjsp")+"";//计算机水平
		// String jszz = jsonary.get("jszz")==null?"":jsonary.get("jszz")+"";//工作驾驶执照年限

		// String yysp = jsonary.get("yysp")==null?"":jsonary.get("yysp")+"";//英语水平
		// String yueysp = jsonary.get("yueysp")==null?"":jsonary.get("yueysp")+"";//粤语水平
		// String yyah = jsonary.get("yyah")==null?"":jsonary.get("yyah")+"";//业余爱好
		// String tc = jsonary.get("tc")==null?"":jsonary.get("tc")+"";//特长
		// String jjlxr = jsonary.get("jjlxr")==null?"":jsonary.get("jjlxr")+"";//紧急联系人姓名
		// String jjrgx = jsonary.get("jjrgx")==null?"":jsonary.get("jjrgx")+"";//紧急人关系
		// String jjrdh = jsonary.get("jjrdh")==null?"":jsonary.get("jjrdh")+"";//紧急人电话
		// String jjrdz = jsonary.get("jjrdz")==null?"":jsonary.get("jjrdz")+"";//紧急人地址

		// String rxhhyxm = jsonary.get("rxhhyxm")==null?"":jsonary.get("rxhhyxm")+"";//瑞信行好友姓名
		// String rxhhygx = jsonary.get("rxhhygx")==null?"":jsonary.get("rxhhygx")+"";//瑞信行好友关系
		// String rxhhydh = jsonary.get("rxhhydh")==null?"":jsonary.get("rxhhydh")+"";//瑞信行好友电话
		// String xqah = jsonary.get("xqah")==null?"":jsonary.get("xqah")+"";//兴趣爱好
		// 添加到岗位登记中
		CCObject Event1 = new CCObject("gwdjb");
		Event1.put("name",name);
		Event1.put("xb",xb);
		Event1.put("csrq",csrq);
		// Event1.put("xp",xp);
		Event1.put("hyzk",hyzk+";"+baby);
		Event1.put("mz",mz);
		Event1.put("sg",sg+" CM");
		Event1.put("jg",jg);

		Event1.put("hkszd",hkszd);
		Event1.put("hjlx",hjlx);
		Event1.put("chmxjb",chmxjb);
		Event1.put("mqjkzk",mqjkzk);
		Event1.put("cnjynwsyjh",cnjynwsyjh);
		Event1.put("zgxl",zgxl);
		Event1.put("zy",zy);
		Event1.put("gznx",gznx);

		Event1.put("lxdh",lxdh);
		Event1.put("dzyx",dzyx);
		Event1.put("sfzh",sfzh);
		Event1.put("mqzz",mqzz);
		Event1.put("wxh",wxh);
		Event1.put("yptj",yptj);
		Event1.put("jsjsp",jsjsp);
		Event1.put("jszz",jszz);

		Event1.put("yysp",yysp);
		Event1.put("yueysp",yueysp);
		Event1.put("yyah",yyah);
		Event1.put("tc",tc);
		Event1.put("jjlxr",jjlxr);
		Event1.put("jjrgx",jjrgx);
		Event1.put("jjrdh",jjrdh);
		Event1.put("jjrdz",jjrdz);

		Event1.put("rxhhyxm",rxhhyxm);
		Event1.put("rxhhygx",rxhhygx);
		Event1.put("rxhhydh",rxhhydh);
		Event1.put("xqah",xqah);
		cs.insert(Event1);
		// out.print(sfzh);
		jsonmsg.put("sfzh", sfzh);
	}else if ("gwdjjytype".equals(gwtype) || "gzjltype".equals(gwtype) || "jtbjtype".equals(gwtype)) {
		String gwdata = request.getParameter("param")==null?"":encodeParameters(request.getParameter("param")+"");//提交的数据
		JSONObject jsonobj = JSONObject.fromObject(gwdata);//把String转换为json对象
		String jsonsfzh = (String)jsonobj.getString("sfzh"); // 获取身份证号
		out.print("2^"+gwdata);
		String jsondatastr = (String)jsonobj.getString("data"); // 获取表格数据
		JSONArray jsonary=JSONArray.fromObject(jsondatastr); // 将json字符串转换为json集合对象
		out.print(jsonsfzh+"身份证号");
		// out.print(jsonary+"^^");
		if("gwdjjytype".equals(gwtype)){ //教育背景处理代码块
			// out.print("教育背景");
			for(int i=0;i<jsonary.size();i++){
				JSONObject cymx_0 = jsonary.getJSONObject(i); // 获取单个json对象
				// CCObject Event1 = new CCObject("gwdjjybj"); // 教育背景表
				CCObject Event1 = new CCObject("gwdjjybj");
				if ("".equals(cymx_0.getString("xxqzsj")) || null == cymx_0.getString("xxqzsj")) {
					break;
				}
				Event1.put("xxqzsj",cymx_0.getString("xxqzsj")); // 学习起止时间
				Event1.put("xx",cymx_0.getString("xx")); // 学校
				Event1.put("zy",cymx_0.getString("zy")); // 专业
				Event1.put("xl",cymx_0.getString("xl")); // 学历
				Event1.put("sfzh",jsonsfzh); // 身份证号
				Event1.put("gwdjlx","jybjtype"); //教育背景类型
				cs.insert(Event1);
			}
		} else if ("gzjltype".equals(gwtype)){  //工作经历代码块
			for(int i=0;i<jsonary.size();i++){
				JSONObject cymx_0 = jsonary.getJSONObject(i);
				// CCObject Event1 = new CCObject("gwdjgzjl");
				CCObject Event1 = new CCObject("gwdjjybj");
				if ("".equals(cymx_0.getString("gzqzsj")) || null == cymx_0.getString("gzqzsj")) {
					break;
				}
				Event1.put("hcdh",cymx_0.getString("hcdh")); // 核查电话
				Event1.put("gzdw",cymx_0.getString("gzdw")); // 工作单位
				Event1.put("zmr",cymx_0.getString("zmr")); // 证明人
				Event1.put("lzyy",cymx_0.getString("lzyy")); // 离职原因
				Event1.put("gzqzsj",cymx_0.getString("gzqzsj")); // 工作起止时间
				Event1.put("yx",cymx_0.getString("yx")); // 月薪
				Event1.put("zw",cymx_0.getString("zw")); // 职位						
				Event1.put("sfzh",jsonsfzh); // 身份证号
				Event1.put("gwdjlx","gzjltype"); //工作经历类型
				cs.insert(Event1);
			}
		} else if ("jtbjtype".equals(gwtype)){  //家庭背景取值代码块
			for(int i=0;i<jsonary.size();i++){
				JSONObject cymx_0 = jsonary.getJSONObject(i);
				// CCObject Event1 = new CCObject("gwdjjtbj");
				CCObject Event1 = new CCObject("gwdjjybj");
				if ("".equals(cymx_0.getString("xmname")) || null == cymx_0.getString("xmname")) {
					break;
				}
				Event1.put("xmname",cymx_0.getString("xmname")); // 姓名
				Event1.put("gx",cymx_0.getString("gx")); // 关系
				Event1.put("nl",cymx_0.getString("nl")); // 年龄
				Event1.put("zwzy",cymx_0.getString("zwzy")); // 职务/职业
				Event1.put("lxdh",cymx_0.getString("lxdh")); // 联系电话
				Event1.put("jrgzdw",cymx_0.getString("jrgzdw")); // 工作单位/地址
				Event1.put("sfzh",jsonsfzh); //身份证号
				Event1.put("gwdjlx","jtbjtype"); //家庭背景类型
				cs.insert(Event1);
			}
		}
		jsonmsg.put("sfzh", jsonsfzh);
	}
    jsonmsg.put("success", true);
    jsonmsg.put("message", gwtype);
} catch (Exception e) {
	jsonmsg.put("success", false);
	jsonmsg.put("message", e.getMessage());
} 
out.print("json:"+jsonmsg.toString());
request.setAttribute("jsonmsg", jsonmsg.toString());
</cc>
<!--<html>
	hello
</html>-->
<cc:forward page="/platform/activity/task/getNodes.jsp"/>