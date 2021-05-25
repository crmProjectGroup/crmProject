<cc:page type="normal" style="standard" showSidebar="false" showHeader="false" />
<cc!>
/*
description:代理排行榜api dlphbapi
version: 1.0
date:20190614
author:tom    问题记录   结算比例获取方式, 从 业务机会 里获取 需分出比例/金额xfcblje
                前提: 该 业务机会没有 结算开票
                    因为: 没有结算开票, 所以没有佣金比例, 不知道正真的佣金
                    编辑 佣金 , 自动计算 创佣 
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
String profileid = userInfo.getProfileId();
String userid = userInfo.getUserId();
String type = "";
String starttime = "";
String endtime = "";

JSONArray rtnMsg = new JSONArray();
JSONObject rtnjbdata = new JSONObject();

String msg = "";

try {
	type = request.getParameter("type")==null?"":encodeParameters(request.getParameter("type")+"");// 执行类型
    starttime = request.getParameter("starttime")==null?"":encodeParameters(request.getParameter("starttime")+"");//开始时间
    endtime = request.getParameter("endtime")==null?"":encodeParameters(request.getParameter("endtime")+"");//结束时间
    String datetime = " and o.cjsj >= str_to_date('"+starttime+" 00:00:00', '%Y-%m-%d %H:%i:%s')  AND o.cjsj <= str_to_date('"+endtime+" 23:59:59', '%Y-%m-%d %H:%i:%s') ";
    JSONArray cjfcjsonarray = new JSONArray();
    if ("setxfcblje".equals(type)) { // 页面修改结算比例
        String oid = request.getParameter("oid")==null?"":encodeParameters(request.getParameter("oid")+"");// 业务机会的id
        Double jyblvalue = request.getParameter("jyblvalue")==null?0.0:Double.valueOf(encodeParameters(request.getParameter("jyblvalue")+""));// 临时结算分佣的值
        String updatejyblsql = "update Opportunity set xfcblje = "+jyblvalue +" where id = '"+oid+"'";
        cs.cqlQuery("Opportunity",updatejyblsql);
        jsonmsg.put("success", true);
        jsonmsg.put("message", "success");
        jsonmsg.put("data", updatejyblsql);
    }else if("selfchz".equals(type)){ // 获取成交汇总页面的数据
        String cjfcsql = "select c.id as cid,c.name as cname,p.name as pname,p.xmjl as pxmjl,a.name as khname,o.id as oid,o.xfcblje,o.khmc,o.xmjl,o.spzt,o.ownerid,o.name,o.cjdw,o.cjmj,o.cjdj,ROUND(o.cjmj*o.cjdj,2) as zongjia,date_format(o.cjsj,'%Y-%m-%d %H:%i:%s') as cjsj,o.createdate from Opportunity as  o left join account a on o.khmc = a.id join project p on p.id=o.xmmc join ccuser c on o.ownerid=c.id where  p.is_deleted='0' and  a.is_deleted='0' and o.is_deleted = '0' and o.spzt = '审批通过' and o.jieduan='成交'  "+datetime+" order by o.cjsj desc";
        // out.print("1^"+cjfcsql);
        List<CCObject> cjfclist = cs.cqlQuery("Opportunity",cjfcsql); // 获取所有的业务机会的成交数据
        // 收集分成用户的id
        Set<String> userset = new HashSet<String>();
        for (CCObject cjfcobj:cjfclist) { // 遍历成交数据, 增加业绩分成数据
            JSONObject cjfcjson = new JSONObject(); // 存储对象
            String oid = cjfcobj.get("oid")==null?"":cjfcobj.get("oid")+ ""; // 业务机会的oid
            String cid = cjfcobj.get("cid")==null?"":cjfcobj.get("cid")+ ""; // 所有人的id
			String cname = cjfcobj.get("cname")==null?"":cjfcobj.get("cname")+ ""; // 所有人名称
            String khname = cjfcobj.get("khname")==null?"":cjfcobj.get("khname")+ ""; // 客户名称
            String cjdw = cjfcobj.get("cjdw")==null?"":cjfcobj.get("cjdw")+ ""; // 成交单位
            double cjmj = cjfcobj.get("cjmj")==null?0.0:Double.valueOf(cjfcobj.get("cjmj")+ ""); // 成交面积
			String cjdj = cjfcobj.get("cjdj")==null?"":cjfcobj.get("cjdj")+ ""; // 成交单价
            double zongjia = cjfcobj.get("zongjia")==null?0.0:Double.valueOf(cjfcobj.get("zongjia")+ ""); // 总价 = 成交单价 * 成交面积
            String cjsj = cjfcobj.get("cjsj")==null?"":cjfcobj.get("cjsj")+ ""; // 成交时间
            // 预计佣金分成比例
            //  1, 因为有成交,但没结算, 不知道佣金分成, 临时存储佣金分成.(以结算单为主)
            //  2, 如果有结算, 以结算单里的分成计算
            double xfcblje = cjfcobj.get("xfcblje")==null?0.0:Double.valueOf(cjfcobj.get("xfcblje")+ ""); 
            out.print("!2^^"+xfcblje);
            // 获取结算单 begin
            String jsmxsql = "select dlfjsbl,dlfjsje,xfcje from dljsmxb where is_deleted = '0' and ywjh = '"+oid+"'";
            // out.print("1^"+jsmxsql);
            List<CCObject> jsmxlist = cs.cqlQuery("dljsmxb",jsmxsql);
            double jsdbl = 0.0;
            double jsdjine=0.0;
            String kaipiao = "否";
            double xfcje = 0.0;
            if(jsmxlist.size()>0) {
                kaipiao = "是";
                double dlfjsbl = jsmxlist.get(0).get("dlfjsbl")==null?0.0:Double.valueOf(jsmxlist.get(0).get("dlfjsbl")+ ""); // 结算单里的---结算比例
                xfcje = jsmxlist.get(0).get("xfcje")==null?0.0:Double.valueOf(jsmxlist.get(0).get("xfcje")+ ""); // 需要分出金额
                // double dlfjsje = jsmxlist.get(0).get("dlfjsje")==null?0.0:Double.valueOf(jsmxlist.get(0).get("dlfjsje")+ ""); //  结算单里的---结算金额
                // out.print(dlfjsbl +"^^"+dlfjsje);
                xfcblje=dlfjsbl; // 如果有结算单, 按照结算单里的比例
                // zongjia=dlfjsje; // 如果有结算单, 按照结算单里的金额计算
                // out.print("^^2^"+xfcblje+"^^"+zongjia);
            }
            // 结算单 end

            // 业绩分成表 
            String yjfcsql = "select y.szxm,y.fcyh1,y.fcbl1,y.szxm2,y.fcyh2,y.fcbl2,y.szxm3,y.fcyh3,y.fcbl3  from yjfc y where y.is_deleted='0' and ywjkmc='"+oid+"'"; 
            List<CCObject> yjfclist = cs.cqlQuery("yjfc",yjfcsql); // 根据业务机会的 id , 获取业绩分成的数据
            if (yjfclist.size()>=1) { // 一个业务机会, 对应一个分成比例
                // 封装业务机会数据begin
                cjfcjson.put("oid",oid); // 业务机会的id
                cjfcjson.put("cname",cname); // 所有人名称
                cjfcjson.put("khname",khname); // 客户名称
                cjfcjson.put("cjdw",cjdw); // 成交单位
                cjfcjson.put("cjmj",cjmj); // 成交面积
                cjfcjson.put("cjdj",cjdj); // 成交单价
                cjfcjson.put("zongjia",zongjia); // 总价 = 成交面积 * 成交单价
                cjfcjson.put("cjsj",cjsj); // 成交时间
                // 封装业务机会数据end
                String fcyh1 = yjfclist.get(0).get("fcyh1")==null?"":yjfclist.get(0).get("fcyh1")+ ""; // 分成用户1的id
                // String fcbl1 = "";
                double fcbl1 = yjfclist.get(0).get("fcbl1")==null?0.0:Double.valueOf(yjfclist.get(0).get("fcbl1")+ ""); // 分成比例1
                String fcyh2 = yjfclist.get(0).get("fcyh2")==null?"":yjfclist.get(0).get("fcyh2")+ ""; // 分成用户2的id
                double fcbl2 = yjfclist.get(0).get("fcbl2")==null?0.0:Double.valueOf(yjfclist.get(0).get("fcbl2")+ ""); // 分成比例2
                String fcyh3 = yjfclist.get(0).get("fcyh3")==null?"":yjfclist.get(0).get("fcyh3")+ ""; // 分成用户3的id
                double fcbl3 = yjfclist.get(0).get("fcbl3")==null?0.0:Double.valueOf(yjfclist.get(0).get("fcbl3")+ ""); // 分成比例3
                // 获取项目名称 end
                // 获取用户名称 begin
                String userselsql = "select id,name from ccuser where is_deleted = '0'";
                if (!"".equals(fcyh1) ) { // 获取分成用户1的名称
                    double huansuan = fcbl1*0.01;
                    List<CCObject> userlist = cs.cqlQuery("ccuser",userselsql+" and id='"+fcyh1+"'");
                    String username1 = userlist.get(0).get("name")==null?"":userlist.get(0).get("name")+ ""; // 分成用户1的名称
                    // out.print(fcyh1);
                    cjfcjson.put("uid",fcyh1); // 用户id
                    cjfcjson.put("fcyh1",username1);
                    cjfcjson.put("fcbl1",fcbl1); // 分成比例1
                    cjfcjson.put("jsmj",cjmj*huansuan);// 结算面积
                    cjfcjson.put("jsyj",zongjia*(xfcblje*0.01)); // 结算佣金
                    cjfcjson.put("jsbl",xfcblje); // 结佣比例
                    cjfcjson.put("xfcje",xfcje); // 需分出金额
                    cjfcjson.put("chuangyong",(zongjia*(xfcblje*0.01)-xfcje)*huansuan); // 结算创佣
                    cjfcjson.put("kaipiao",kaipiao); // 是否开票
                    cjfcjsonarray.add(cjfcjson);
                    userset.add(fcyh1);
                }
                if (!"".equals(fcyh2) ) { // 获取分成用户2的名称
                    double huansuan = fcbl2*0.01;
                    List<CCObject> userlist2 = cs.cqlQuery("ccuser",userselsql+" and id='"+fcyh2+"'");
                    String username2 = userlist2.get(0).get("name")==null?"":userlist2.get(0).get("name")+ ""; // 分成用户2的名称
                    cjfcjson.put("uid",fcyh2); // 用户id
                    cjfcjson.put("fcyh1",username2);
                    cjfcjson.put("fcbl1",fcbl2); // 分成比例1
                    cjfcjson.put("jsmj",cjmj*huansuan);// 结算面积
                    cjfcjson.put("jsyj",zongjia*(xfcblje*0.01)); // 结算佣金
                    cjfcjson.put("jsbl",xfcblje); // 结佣比例
                    cjfcjson.put("xfcje",xfcje); // 需分出金额
                    cjfcjson.put("chuangyong",(zongjia*(xfcblje*0.01)-xfcje)*huansuan); // 结算创佣
                    cjfcjson.put("kaipiao",kaipiao); // 是否开票
                    cjfcjsonarray.add(cjfcjson);
                    userset.add(fcyh2);
                }
                if (!"".equals(fcyh3) ) { // 获取分成用户3的名称
                    double huansuan = fcbl3*0.01;
                    List<CCObject> userlist3 = cs.cqlQuery("ccuser",userselsql+" and id='"+fcyh3+"'");
                    String username3 = userlist3.get(0).get("name")==null?"":userlist3.get(0).get("name")+ ""; // 分成用户3的名称
                    cjfcjson.put("uid",fcyh3); // 用户id
                    cjfcjson.put("fcyh1",username3);
                    cjfcjson.put("fcbl1",fcbl3); // 分成比例1
                    cjfcjson.put("jsmj",cjmj*huansuan);// 结算面积
                    cjfcjson.put("jsyj",zongjia*(xfcblje*0.01)); // 结算佣金
                    cjfcjson.put("jsbl",xfcblje); // 结算比例
                    cjfcjson.put("xfcje",xfcje); // 需分出金额
                    cjfcjson.put("chuangyong",(zongjia*(xfcblje*0.01)-xfcje)*huansuan); // 结算创佣
                    cjfcjson.put("kaipiao",kaipiao); // 是否开票
                    cjfcjsonarray.add(cjfcjson);
                    userset.add(fcyh3);
                }
                // 获取用户名称 end

            } else { // 当没有分成绩效时
                // 封装业务机会数据begin
                cjfcjson.put("cname",cname); // 所有人名称
                cjfcjson.put("khname",khname); // 客户名称
                cjfcjson.put("cjdw",cjdw); // 成交单位
                cjfcjson.put("cjmj",cjmj); // 成交面积
                cjfcjson.put("cjdj",cjdj); // 成交单价
                cjfcjson.put("zongjia",zongjia); // 总价 = 成交面积 * 成交单价
                cjfcjson.put("cjsj",cjsj); // 成交时间
                // 封装业务机会数据end
                cjfcjson.put("oid",oid);
                cjfcjson.put("uid",cid); // 用户id
                cjfcjson.put("fcyh1",cname); // 分成用户1
                cjfcjson.put("fcbl1",100); // 分成比例1
                cjfcjson.put("jsmj",cjmj);// 结算面积
                out.print("!^^"+xfcblje);
                if (xfcblje > 0) {
                    out.print("进来了^^");
                    cjfcjson.put("jsbl",xfcblje); // 结算比例
                    cjfcjson.put("xfcje",xfcje); // 需分出金额
                    cjfcjson.put("jsyj",zongjia*(xfcblje*0.01)); // 结算佣金
                    cjfcjson.put("chuangyong",zongjia*(xfcblje*0.01)-xfcje); // 结算创佣
                } else {
                    cjfcjson.put("jsyj",zongjia); // 结算佣金
                    cjfcjson.put("jsbl",100); // 结算比例
                    cjfcjson.put("xfcje",xfcje); // 需分出金额
                    cjfcjson.put("chuangyong",zongjia-xfcje); // 结算创佣
                }
                cjfcjson.put("kaipiao",kaipiao); // 是否开票
                cjfcjsonarray.add(cjfcjson);
                userset.add(cid);
            } 
        }
        // 组建排行榜数据begin
        // int i=1;
        JSONArray paijsonarray = new JSONArray(); // 存储排行榜面积汇总的数据
        JSONObject paimjjson = new JSONObject(); // 存储面积对象
        JSONArray paijinearray = new JSONArray(); // 存储排行榜金额汇总
        JSONObject paijinejson = new JSONObject(); // 存储金额对象
        for(String user:userset) {
            double pmjall = 0.0; // 汇总的面积
            double pchuanall=0.0; // 汇总的金额
            String painame = "";
            for(int i=0;i<cjfcjsonarray.size();i++) {
                String uid = cjfcjsonarray.getJSONObject(i).get("uid").toString(); // 获取id
                double pjsmj = Double.parseDouble(cjfcjsonarray.getJSONObject(i).get("jsmj").toString()); // 获取面积
                double pchuany = Double.parseDouble(cjfcjsonarray.getJSONObject(i).get("chuangyong").toString()); // 获取创佣
                if (user.equals(uid)){
                    painame = cjfcjsonarray.getJSONObject(i).get("fcyh1").toString(); // 获取名称
                    pmjall = pmjall+pjsmj;
                    pchuanall = pchuanall + pchuany;
                }
            }
            paimjjson.put("painame",painame); // 组装名称 -
            paimjjson.put("pmjall",pmjall); // 总面积
            paijsonarray.add(paimjjson); // end
            paijinejson.put("painame",painame); // 名称
            paijinejson.put("pchuanall",pchuanall); // 总创佣
            paijinearray.add(paijinejson); //end
        }
        String paimjary =paijsonarray.toString();  // 存储汇总后的面积数据
        String paijeary =paijinearray.toString();  // 存储汇总后的金额数据
        // out.print(paiary+"^^^^^");
        // 排行榜 end
        String cjary =cjfcjsonarray.toString(); 
        // out.print(cjary);
        // out.print("1^"+cjary);
        jsonmsg.put("success", true);
        jsonmsg.put("message", "success");
        jsonmsg.put("data", cjary);
        jsonmsg.put("table1", paimjary);
        jsonmsg.put("table2", paijeary);
	} else if ("selcjfc".equals(type)){ // 获取成交分成页面的数据
        String cjfcsql = "select op.*,cu.name as xname from (select c.name as cname,p.name as pname,p.xmjl as pxmjl,a.name as khname,o.id as oid,o.khmc,o.xmjl,o.spzt,o.ownerid,o.name,o.cjdw,o.cjmj,o.cjdj,ROUND(o.cjmj*o.cjdj,2) as zongjia,date_format(o.cjsj,'%Y-%m-%d %H:%i:%s') as cjsj,o.createdate from Opportunity as  o left join account a on o.khmc = a.id join project p on p.id=o.xmmc join ccuser c on o.ownerid=c.id where  p.is_deleted='0' and  a.is_deleted='0' and o.is_deleted = '0' and o.spzt = '审批通过' and o.jieduan='成交'  "+datetime+") op left join ccuser cu on op.pxmjl=cu.id order by op.cjsj desc";
        List<CCObject> cjfclist = cs.cqlQuery("Opportunity",cjfcsql); // 获取所有的业务机会的成交数据
        for (CCObject cjfcobj:cjfclist) { // 遍历成交数据, 增加业绩分成数据
            JSONObject cjfcjson = new JSONObject(); // 存储对象
            String oid = cjfcobj.get("oid")==null?"":cjfcobj.get("oid")+ ""; // 业务机会的oid
            String proname = cjfcobj.get("pname")==null?"":cjfcobj.get("pname")+ ""; // 项目名称
			String cname = cjfcobj.get("cname")==null?"":cjfcobj.get("cname")+ ""; // 所有人名称
			String xname = cjfcobj.get("xname")==null?"":cjfcobj.get("xname")+ ""; // 项目经理
            String khname = cjfcobj.get("khname")==null?"":cjfcobj.get("khname")+ ""; // 客户名称
            String cjdw = cjfcobj.get("cjdw")==null?"":cjfcobj.get("cjdw")+ ""; // 成交单位
			String cjmj = cjfcobj.get("cjmj")==null?"":cjfcobj.get("cjmj")+ ""; // 成交面积
			String cjdj = cjfcobj.get("cjdj")==null?"":cjfcobj.get("cjdj")+ ""; // 成交单价
            String zongjia = cjfcobj.get("zongjia")==null?"":cjfcobj.get("zongjia")+ ""; // 总价 = 成交单价 * 成交面积
            String cjsj = cjfcobj.get("cjsj")==null?"":cjfcobj.get("cjsj")+ ""; // 成交时间
            String spzt = cjfcobj.get("spzt")==null?"":cjfcobj.get("spzt")+ ""; // 审批状态
            // 封装业务机会数据begin
            cjfcjson.put("pname",proname); // 项目名称
            cjfcjson.put("cname",cname); // 所有人名称
            cjfcjson.put("xname",xname); // 项目经理名称
            cjfcjson.put("khname",khname); // 客户名称
            cjfcjson.put("cjdw",cjdw); // 成交单位
            cjfcjson.put("cjmj",cjmj); // 成交面积
            cjfcjson.put("cjdj",cjdj); // 成交单价
            cjfcjson.put("zongjia",zongjia); // 总价 = 成交面积 * 成交单价
            cjfcjson.put("cjsj",cjsj); // 成交时间
            cjfcjson.put("spzt",spzt); // 审批状态
            // 封装业务机会数据end

            // 业绩分成表 
            String yjfcsql = "select y.szxm,y.fcyh1,y.fcbl1,y.szxm2,y.fcyh2,y.fcbl2,y.szxm3,y.fcyh3,y.fcbl3  from yjfc y where y.is_deleted='0' and ywjkmc='"+oid+"'"; 
            List<CCObject> yjfclist = cs.cqlQuery("yjfc",yjfcsql); // 根据业务机会的 id , 获取业绩分成的数据
            if (yjfclist.size() >= 1) { // 一个业务机会, 对应一个分成比例
                String szxm = yjfclist.get(0).get("szxm")==null?"":yjfclist.get(0).get("szxm")+ ""; // 所属项目1的id
                String fcyh1 = yjfclist.get(0).get("fcyh1")==null?"":yjfclist.get(0).get("fcyh1")+ ""; // 分成用户1的id
                String fcbl1 = yjfclist.get(0).get("fcbl1")==null?"":yjfclist.get(0).get("fcbl1")+ ""; // 分成比例1
                String szxm2 = yjfclist.get(0).get("szxm2")==null?"":yjfclist.get(0).get("szxm2")+ ""; // 所属项目2的id
                String fcyh2 = yjfclist.get(0).get("fcyh2")==null?"":yjfclist.get(0).get("fcyh2")+ ""; // 分成用户2的id
                String fcbl2 = yjfclist.get(0).get("fcbl2")==null?"":yjfclist.get(0).get("fcbl2")+ ""; // 分成比例2
                String szxm3 = yjfclist.get(0).get("szxm3")==null?"":yjfclist.get(0).get("szxm3")+ ""; // 所属项目3的id
                String fcyh3 = yjfclist.get(0).get("fcyh3")==null?"":yjfclist.get(0).get("fcyh3")+ ""; // 分成用户3的id
                String fcbl3 = yjfclist.get(0).get("fcbl3")==null?"":yjfclist.get(0).get("fcbl3")+ ""; // 分成比例3
                String proselsql = "select id,name from project where is_deleted = '0'"; // 根据项目id, 获取项目的名称
                // 获取项目名称 begin
                if (!"".equals(szxm)) { // 获取所属项目1的名称
                    List<CCObject> prolist = cs.cqlQuery("project",proselsql+" and id='"+szxm+"'");
                    String proname1 = prolist.get(0).get("name")==null?"":prolist.get(0).get("name")+ ""; // 所属项目1的名称
                    cjfcjson.put("szxm",proname1); // 封装所属项目1
                    if (!"".equals(szxm2) ) { // 获取所属项目2的名称
                        List<CCObject> prolist2 = cs.cqlQuery("project",proselsql+" and id='"+szxm2+"'");
                        String proname2 = prolist2.get(0).get("name")==null?"":prolist2.get(0).get("name")+ ""; // 所属项目2的名称
                        cjfcjson.put("szxm2",proname2); // 封装所属项目2
                        if (!"".equals(szxm3) ) { // 获取所属项目3的名称
                            List<CCObject> prolist3 = cs.cqlQuery("project",proselsql+" and id='"+szxm3+"'");
                            String proname3 = prolist3.get(0).get("name")==null?"":prolist3.get(0).get("name")+ ""; // 所属项目3的名称
                            cjfcjson.put("szxm3",proname3); // 封装所属项目3
                        }
                    } 
                }
                // 获取项目名称 end
                // 获取用户名称 begin
                String userselsql = "select id,name from ccuser where is_deleted = '0'";
                if (!"".equals(fcyh1) ) { // 获取分成用户1的名称
                    List<CCObject> userlist = cs.cqlQuery("ccuser",userselsql+" and id='"+fcyh1+"'");
                    String username1 = userlist.get(0).get("name")==null?"":userlist.get(0).get("name")+ ""; // 分成用户1的名称
                    cjfcjson.put("fcyh1",username1);
                    if (!"".equals(fcyh2) ) { // 获取分成用户2的名称
                        List<CCObject> userlist2 = cs.cqlQuery("ccuser",userselsql+" and id='"+fcyh2+"'");
                        String username2 = userlist2.get(0).get("name")==null?"":userlist2.get(0).get("name")+ ""; // 分成用户2的名称
                        cjfcjson.put("fcyh2",username2);
                        if (!"".equals(fcyh3) ) { // 获取分成用户3的名称
                            List<CCObject> userlist3 = cs.cqlQuery("ccuser",userselsql+" and id='"+fcyh3+"'");
                            String username3 = userlist3.get(0).get("name")==null?"":userlist3.get(0).get("name")+ ""; // 分成用户3的名称
                            cjfcjson.put("fcyh3",username3);
                        }
                    } 
                } 
                // 获取用户名称 end
                cjfcjson.put("fcbl1",fcbl1); // 分成比例1
                cjfcjson.put("fcbl2",fcbl2); // 分成比例2
                cjfcjson.put("fcbl3",fcbl3); // 分成比例3
            } else { // 当没有分成绩效时
                cjfcjson.put("szxm",proname); // 封装所属项目1
                cjfcjson.put("fcyh1",cname); // 分成用户1
                cjfcjson.put("fcbl1",100); // 分成比例1
            }
            cjfcjsonarray.add(cjfcjson);
        }
        String cjary =cjfcjsonarray.toString(); 
        jsonmsg.put("success", true);
        jsonmsg.put("message", "success");
        jsonmsg.put("sql",cjfcsql);
        jsonmsg.put("data", cjary);
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