<cc:page type="normal" style="standard" showSidebar="false" showHeader="false" />
<cc!>
/*

*/

/**
* 将ISO-8859-1编码格式的字符串转码为UTF-8
* 1: 注意事项: 两个项目比较, 字段key值必须一样才能融入代码
*
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
String userid = userInfo.getUserId();
String options = ""; // 选择的下拉
String type="";// 区分类型

JSONArray rtnMsg = new JSONArray();
JSONObject rtnjbdata = new JSONObject();

String msg = "";

try {
    type = request.getParameter("type")==null?"":encodeParameters(request.getParameter("type")+"");//客户id
    if ("jingpin".equals(type)) { // 竞品对比
        options = request.getParameter("options")==null?"":java.net.URLDecoder.decode(request.getParameter("options")+"","UTF-8"); // 获取需要展示的json字符串
		String rxhpro = request.getParameter("rxhpro")==null?"":encodeParameters(request.getParameter("rxhpro")+"");//客户id
		String scpypro = request.getParameter("scpypro")==null?"":encodeParameters(request.getParameter("scpypro")+"");//客户id
        JSONArray jsonary=JSONArray.fromObject(options); // 将字符串转化为json对像
		String addsql = "";
		for(int i=0;i<jsonary.size();i++){
			if (i!=0) {
				addsql+=",";
			}
            JSONObject opjsonobj = jsonary.getJSONObject(i); 
			String addziduan = opjsonobj.getString("value");
			addsql += " p."+addziduan+" "+"p"+addziduan +",s."+addziduan+" "+"s"+addziduan;  // 由于两个表的比较字段一样, 用"别名"区分
		}
		// 组装获取数据的sql
		String sqlsel = "select "+addsql+" from project p,scpy s where p.is_deleted='0' and s.is_deleted='0' and p.id='"+rxhpro+"' and s.id='"+scpypro+"'";
        List<CCObject> selsqlList = cs.cqlQuery("Project",sqlsel); //获取两个项目的数据集合
		for(int i=0;i<jsonary.size();i++){ // 封装到object 中
            JSONObject opjson = jsonary.getJSONObject(i); 
			String iflable = opjson.getString("lable");
			String ifvalue = opjson.getString("value");
			for(CCObject selobj:selsqlList) {
				String rxhname = selobj.get("p"+ifvalue)==null?"":selobj.get("p"+ifvalue)+ "";
				String scpyname = selobj.get("s"+ifvalue)==null?"":selobj.get("s"+ifvalue)+ "";
				rtnjbdata.put("name",iflable);
				rtnjbdata.put("rxhvalue",rxhname);
				rtnjbdata.put("scpyvalue",scpyname);
				rtnMsg.add(rtnjbdata);
			}
		}
        // out.print(rtnMsg.toString());
		jsonmsg.put("data", rtnMsg);
        jsonmsg.put("success", true);
        jsonmsg.put("message", addsql);
    } else if ("sjtable".equals(type)) {
		String yearzu = request.getParameter("yearzu1")==null?"":encodeParameters(request.getParameter("yearzu1")+""); // 总月数/12=总年数
		String zjdj = request.getParameter("zjdj1")==null?"":encodeParameters(request.getParameter("zjdj1")+"");// 租金单价
		String arrone = request.getParameter("arrone")==null?"0":encodeParameters(request.getParameter("arrone")+"");//租金 前两年 不变，
		String arrtwo = request.getParameter("arrtwo")==null?"0":encodeParameters(request.getParameter("arrtwo")+"");//第 三 年起
		String arrthree = request.getParameter("arrthree")==null?"0":encodeParameters(request.getParameter("arrthree")+"");//逐年递增 6% 的幅度
		String jzmj = request.getParameter("jzmj1")==null?"0":encodeParameters(request.getParameter("jzmj1")+"");//租金单价
		String mzq = request.getParameter("mzq1")==null?"0":encodeParameters(request.getParameter("mzq1")+"");//免租期
		// out.print(yearzu1+"^^"+arr1+"^^"+zjdj1);
		Calendar cal = Calendar.getInstance();
		int month = cal.get(Calendar.MONTH) + 1;
		int newnonth = month-1; // 获取月
        int year = cal.get(Calendar.YEAR); // 获取现在年
		int jiayear=0;
		jiayear=year; // 备份年
		Double zuj = Double.valueOf(zjdj); // 租金单价
		Double dbjzmj = Double.valueOf(jzmj); // 建筑面积
		int arrdz = Integer.valueOf(arrthree); // 递增率
		Double countzuj = 0.0;
		Boolean mzqkg = true; // 免租期开关, 免租期只执行一次
		out.print("^表^"+zuj);
		for(int i=0;i<Integer.valueOf(yearzu);i++) {
			
			int newyear = year+1+i;
			if (i>=Integer.valueOf(arrone)) { // 如果租期前几年不为0时
				zuj = zuj*arrdz*0.01+zuj;
				String zujstr = String.format("%.2f", zuj);
				rtnjbdata.put("date",jiayear+"年"+month+"月~"+newyear+"年"+newnonth+"月");
				rtnjbdata.put("dzl",arrdz);
				rtnjbdata.put("zujing",zujstr);
				out.print("判断1:"+mzqkg);
				if (mzqkg) {
					out.print("判断:"+String.valueOf(Integer.valueOf(mzq)!=0));
					if (Integer.valueOf(mzq)!=0) {
						out.print("^1:^"+zujstr);
						countzuj += zuj*dbjzmj*(12-Integer.valueOf(mzq));
						mzqkg = false;
						out.print("^2:^"+countzuj);
						out.print("^3:^"+mzq);
					} else {
						countzuj += zuj*dbjzmj*12;
						out.print("^4:^"+countzuj+"^"+zuj+"^"+"^"+dbjzmj+"^");
					}
				} else {
					out.print("^5:^"+zujstr);
					countzuj += zuj*dbjzmj*12;
					out.print("^6:^"+countzuj);
				}
				
			}else {
				String zujstr = String.format("%.2f", zuj);
				rtnjbdata.put("date",jiayear+"年"+month+"月~"+newyear+"年"+newnonth+"月");
				rtnjbdata.put("dzl",0);
				rtnjbdata.put("zujing",zujstr);
				if (mzqkg) {
					if (Integer.valueOf(mzq)!=0) {
						// out.print("^5^"+zujstr);
						countzuj += zuj*dbjzmj*(12-Integer.valueOf(mzq));
						mzqkg = false;
						// out.print("^6^"+countzuj);
					} else {
						countzuj += zuj*dbjzmj*12;	
					}
				} else {
					countzuj += zuj*dbjzmj*12;
					// out.print("^7^"+countzuj);
				}
			}
			jiayear = newyear;
			rtnMsg.add(rtnjbdata);
		}
		jsonmsg.put("data", rtnMsg);
        jsonmsg.put("success", true);
        jsonmsg.put("message", mzq);
		jsonmsg.put("countzuj", Math.round(countzuj));

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