/**
 * 定时器： 今天CRM定时器同步企业微信昨天的打卡信息（每天执行）
 */


CCService cs = new CCService((UserInfo)userInfo);
// String rxhcorid = "ww5b99d45a63be2c50";
// String rxhappsecret = "ytqCG3x7-SR_cBJzMLdfXQEXl4dGqdBaLY4ifu_p3a0";
String accessTokenVal = "";
String body = "";
String result = "-20";
String txt = "";

// 通过企业的corid 和 appsecret 获取查询userid的token  begin
// String url= "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid="+rxhcorid+"&corpsecret="+rxhappsecret+"";
// 	// String param = "corpid="+rxhcorid+"&corpsecret="+rxhappsecret+"";
// 	java.net.URL realUrl = new java.net.URL(url);
// 	// 打开和URL之间的连接
// 	java.net.HttpURLConnection conn = (java.net.HttpURLConnection)realUrl.openConnection();
// 	// 设置通用的请求属性
//     // try{
// 	conn.setRequestProperty("accept", "*/*");
// 	conn.setRequestProperty("connection", "Keep-Alive");
// 	conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
// 	// 发送POST请求必须设置如下两行
// 	conn.setDoOutput(true);
// 	conn.setDoInput(true);
// 	// 获取URLConnection对象对应的输出流
// 	java.io.PrintWriter out2 = new java.io.PrintWriter(conn.getOutputStream());
// 	// 发送请求参数
// 	// out2.print(param);
// 	// flush输出流的缓冲
// 	// out2.flush();
// 	// 定义BufferedReader输入流来读取URL的响应
// 	java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream()));
// 	String res = in.readLine();
//     in.close();
//     net.sf.json.JSONObject jsontoken = net.sf.json.JSONObject.fromObject(res);
//     accessTokenVal = jsontoken.get("access_token").toString(); // 获取到了token
//     // if(true){  // 打印日志
//     //     trigger.addErrorMessage(String.valueOf(accessTokenVal.toString()+"^^")); 
//     // }
// // 通过企业的corid 和 appsecret 获取查询userid的token  end

// // 根据crm系统录入的手机号， 获取 企业微信的userid  begin
// // 获取userid的url
// String useridurl = "https://qyapi.weixin.qq.com/cgi-bin/user/getuserid?access_token="+accessTokenVal;
 // 获取crm里企业顾问的手机号
//List<CCObject> usermodellist = cs.cqlQuery("ccuser","select  mobile  from  ccuser  where is_deleted='0' and isusing='1' and profile in ('aaa2018A38306ED9syVe','aaa201723453E5EBNtzU')"); //去除部门限制

// 需要获取到,业务员所在的项目的id
// select c.id,c.mobile,c.gh,p.xmmc from ccuser c left join ProjectSaleGroup p on c.id = p.xmxsy  where c.name like '%刘%'  and c.isusing=1 and p.is_deleted='0'  select * from ccuser where name like '%王茜%'
//List<CCObject> usermodellist = cs.cqlQuery("ccuser","select c.id,c.mobile,c.gh,p.xmmc from ccuser c left join ProjectSaleGroup p on c.id = p.xmxsy  where c.name like '%王茜%'  and c.isusing=1");
// if(true){  // 打印日志
//     trigger.addErrorMessage(String.valueOf(usermodellist)); 
// }
// 存储所有的userid
// List arruserid = new ArrayList();
// Map<String, String> modelorweixuserid = new HashMap<>(); // 将手机好和企微的userid绑定
// java.net.URL realUrluser = new java.net.URL(useridurl);
// // 打开和URL之间的连接
// java.net.HttpURLConnection connuser = (java.net.HttpURLConnection)realUrluser.openConnection();
//         for(CCObject usermodel:usermodellist) {
//             String mobile =usermodel.get("mobile")==null?"":usermodel.get("mobile")+""; 
//             String crmuserid =usermodel.get("id")==null?"":usermodel.get("id")+"";
//             //String mobile = "17671655305";
//             net.sf.json.JSONObject mobilejson = new net.sf.json.JSONObject();
//             mobilejson.put("mobile",mobile);
//             String param = mobilejson.toString();
//             // if(true){  // 打印日志
//             //     trigger.addErrorMessage(String.valueOf(param)); 
//             // }
//             // 设置通用的请求属性
//             connuser.setRequestProperty("accept", "*/*");
//             connuser.setRequestProperty("connection", "Keep-Alive");
//             connuser.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
//             // 发送POST请求必须设置如下两行
//             connuser.setDoOutput(true);
//             connuser.setDoInput(true);
//             // 获取URLConnection对象对应的输出流
//             java.io.PrintWriter outuser = new java.io.PrintWriter(connuser.getOutputStream());
//             // 发送请求参数
//             outuser.print(param);
//             // flush输出流的缓冲
//             outuser.flush();
//             // 定义BufferedReader输入流来读取URL的响应
//             java.io.BufferedReader inuser = new java.io.BufferedReader(new java.io.InputStreamReader(connuser.getInputStream()));
//             String resuser = inuser.readLine();
//             inuser.close();
//             net.sf.json.JSONObject jsontuserid = net.sf.json.JSONObject.fromObject(resuser);
//             if (("ok").equals(jsontuserid.get("errmsg"))) { // 调用接口没有异常，则封装
//                 String userid = jsontuserid.get("userid").toString(); // 获取到了userid
//                 arruserid.add(userid); // 将所有的手机号查询到的企业微信的userid，封装到list中
//                 modelorweixuserid.put(crmuserid,userid); // 将CRM你的userid作为key，企微的userid作为value
//                 // if(true){  // 打印日志
//                 //     trigger.addErrorMessage(String.valueOf(userid)); 
//                 // }
//             }
//         }

// 根据crm系统录入的手机号， 获取 企业微信的userid  end
    // if(true){  // 打印日志
    //     trigger.addErrorMessage(String.valueOf(arruserid.size()!=0)); 
    // }
    // 存储所有的userid
    List qywxuseridlist = new ArrayList();
    List<CCObject> userlist = cs.cqlQuery("ccuser","select id,qywxuserid,gh  from  ccuser  where is_deleted='0' and isusing='1' and profile in ('aaa2018A38306ED9syVe','aaa201723453E5EBNtzU','aaa000001')");
    for (CCObject kaoq:userlist) {
        String qywxuserid =kaoq.get("qywxuserid")==null?"":kaoq.get("qywxuserid")+"";
        if ("".equals(qywxuserid)) {
            continue;
        }
        qywxuseridlist.add(qywxuserid); // 获取所有的企业微信id，放到list中
    }
    // if(true){  // 打印日志
    //     trigger.addErrorMessage(String.valueOf(qywxuseridlist.toString())); 
    // } 
    // 根据企业微信的userid集合， 获取所有的打卡详细信息。 begin
    String corpiddaka = "ww5b99d45a63be2c50"; // 企业的id
    String secretdaka = "1oi-LurCZZpenrSFxp0iuSCZjwUR_4l57hzOo4Fi8dM"; // 打卡模块的id
    String modeltoken = "";
    String urldaka= "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid="+corpiddaka+"&corpsecret="+secretdaka+"";
        // String param = "corpid="+rxhcorid+"&corpsecret="+rxhappsecret+"";
        java.net.URL dakaUrl = new java.net.URL(urldaka);
        // 打开和URL之间的连接
        java.net.HttpURLConnection conndaka = (java.net.HttpURLConnection)dakaUrl.openConnection();
        // 设置通用的请求属性
        conndaka.setRequestProperty("accept", "*/*");
        conndaka.setRequestProperty("connection", "Keep-Alive");
        conndaka.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
        // 发送POST请求必须设置如下两行
        conndaka.setDoOutput(true);
        conndaka.setDoInput(true);
        // 获取URLConnection对象对应的输出流
        java.io.PrintWriter outdaka = new java.io.PrintWriter(conndaka.getOutputStream());
        // 发送请求参数
        // out2.print(param);
        // flush输出流的缓冲
        // out2.flush();
        // 定义BufferedReader输入流来读取URL的响应
        java.io.BufferedReader indaka = new java.io.BufferedReader(new java.io.InputStreamReader(conndaka.getInputStream()));
        String resdaka = indaka.readLine();
        indaka.close();
        net.sf.json.JSONObject jsontokendaka = net.sf.json.JSONObject.fromObject(resdaka);
        modeltoken = jsontokendaka.get("access_token").toString(); // 获取到了打卡api 的token
        // if(true){  // 打印日志
        //     trigger.addErrorMessage(String.valueOf(modeltoken.toString())); 
        // }     
    // 根据打卡的token ， 获取 所有userid的考勤
    // String param = "corpid="+rxhcorid+"&corpsecret="+rxhappsecret+"";
    // 获取前一天的开始时间和结束时间 begin
        Date cur = new Date();
        Calendar calendar = new GregorianCalendar();
        // calendar.add(Calendar.DAY_OF_MONTH,-1); // 减一 昨天的时间
        // calendar.add(Calendar.DAY_OF_MONTH,-2); // 减二 前天的时间
        
        //一天的开始时间 yyyy:MM:dd 00:00:00
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        Date dayStart = calendar.getTime();
        
        //一天的结束时间 yyyy:MM:dd 23:59:59
        calendar.set(Calendar.HOUR_OF_DAY,23);
        calendar.set(Calendar.MINUTE,59);
        calendar.set(Calendar.SECOND,59);
        calendar.set(Calendar.MILLISECOND,999);
        Date dayEnd = calendar.getTime();
        // 开始时间和结束时间end
        // 封装json参数， 获取打卡信息
        net.sf.json.JSONObject useridjson = new net.sf.json.JSONObject();
        useridjson.put("opencheckindatatype",3);
        useridjson.put("starttime",dayStart.getTime()/1000);
        useridjson.put("endtime",dayEnd.getTime()/1000);
        // useridjson.put("starttime",1621526400);
        // useridjson.put("endtime",1621612799);
        useridjson.put("useridlist",qywxuseridlist);
        String paramdaka = useridjson.toString();
        // if(true){  // 打印日志
        //     trigger.addErrorMessage(String.valueOf(paramdaka)); 
        // }
       
        String urlkaoqin= "https://qyapi.weixin.qq.com/cgi-bin/checkin/getcheckindata?access_token="+modeltoken;
        java.net.URL kaoqinUrl = new java.net.URL(urlkaoqin);
        // 打开和URL之间的连接
        java.net.HttpURLConnection connkaoqin = (java.net.HttpURLConnection)kaoqinUrl.openConnection();
        net.sf.json.JSONObject kaoqinjson=null;
        net.sf.json.JSONArray jsonArray = null;
        // 设置通用的请求属性
        connkaoqin.setRequestProperty("accept", "*/*");
        connkaoqin.setRequestProperty("connection", "Keep-Alive");
        connkaoqin.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
        // 发送POST请求必须设置如下两行
        connkaoqin.setDoOutput(true);
        connkaoqin.setDoInput(true);
        // 获取URLConnection对象对应的输出流
        java.io.PrintWriter outkaoqin = new java.io.PrintWriter(connkaoqin.getOutputStream());
        // 发送请求参数
        outkaoqin.print(paramdaka);
        // flush输出流的缓冲
        outkaoqin.flush();
        // 定义BufferedReader输入流来读取URL的响应
        java.io.BufferedReader inkaoqin = new java.io.BufferedReader(new java.io.InputStreamReader(connkaoqin.getInputStream()));
        String reskaoqin = inkaoqin.readLine();
        kaoqinjson = net.sf.json.JSONObject.fromObject(reskaoqin);
        // if(true){  // 打印日志
        //     trigger.addErrorMessage(String.valueOf(paramdaka.toString())); 
        // }
        inkaoqin.close();
        jsonArray = kaoqinjson.getJSONArray("checkindata");
        // if(true){  // 打印日志
        //     trigger.addErrorMessage(String.valueOf(jsonArray.toString())); 
        // }
    //根据企业微信的userid集合， 获取所有的打卡详细信息。 end
    
    // 关闭请求的链接begin
    // 由于放在代码中关闭，导致连接失败
    try {

    }finally{
        // if (conn != null) {
        //     conn.disconnect();
        //     conn = null;
        // }
        // if (connuser != null) {
        //     connuser.disconnect();
        //     connuser = null;
        //  }
       if (conndaka != null) {
            conndaka.disconnect();
            conndaka = null;
         }
        if (connkaoqin != null) {
            connkaoqin.disconnect();
            connkaoqin = null;
        }
   } 
    // 关闭请求的链接end


    // 将遍历crm的每一个id，将获取到所有的考勤信息，汇总到各自id里，保存到crm中 begin 
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
   java.util.Calendar cal = java.util.Calendar.getInstance();
   net.sf.json.JSONArray objarray = new net.sf.json.JSONArray();
   String adduser = "";
   
   for(CCObject userall:userlist){
       String qywxuserid =userall.get("qywxuserid")==null?"":userall.get("qywxuserid")+""; //企微的id
       if ("".equals(qywxuserid)) {
           continue;
        }
        String dakauserid =userall.get("id")==null?"":userall.get("id")+""; // 获取crm的id  xmmc
        String gh =userall.get("gh")==null?"":userall.get("gh")+""; // 获取crm的工号gh
        CCObject crmdakaobj1 = new CCObject("attendance");
        //String xmmc =userall.get("xmmc")==null?"":userall.get("xmmc")+""; 
        // String qwid = modelorweixuserid.get(dakauserid);// 根据crm的id，获取企微的id
        //String qwid = "";
        for (int j = 0; j < jsonArray.size(); j++) {  // 遍历企微里所有打卡信息
            net.sf.json.JSONObject jsonObject2 = jsonArray.getJSONObject(j); // 获取单条记录
            if ("".equals(jsonObject2.get("deviceid").toString())) { // 有设备信息，代表打卡了
                continue;
            }
            String userid = jsonObject2.get("userid").toString(); // 企微的id
            if (!adduser.contains(userid) && qywxuserid.equals(userid)) { // 如果不在adduser中标示没有处理过， 并且 与这个json中的企微id相等
                String checkin_type = jsonObject2.get("checkin_type").toString(); // 企微的打卡类型 ， 上班打卡  下班打卡  外出打卡
                if ("上班打卡".equals(checkin_type) || "下班打卡".equals(checkin_type)) {
                    checkin_type = "内勤";
                } else if("外出打卡".equals(checkin_type)){
                    checkin_type = "外勤";
                } else {
                    checkin_type = "";
                }
                
                String checkin_time = jsonObject2.get("checkin_time").toString(); // 打卡时间
                long time = Long.parseLong(checkin_time)*1000;
                //转化Date
                java.util.Date dataTime = new java.util.Date(time);
                cal.setTime(dataTime);
                int year = cal.get(Calendar.YEAR);//当前年份
                int month = cal.get(Calendar.MONTH)+1;//当月
                int day = cal.get(Calendar.DAY_OF_MONTH);//日期
                String monstr="";
                if (month<10) {
                    monstr  = "0"+ month;
                } else {
                    monstr=month+"";
                }
                String qdrq = year+"-"+monstr+"-"+day; // 签到日期
                String curDate = sdf.format(cal.getTime());
                String location_detail = jsonObject2.get("location_detail").toString(); // 定位地址
                String lat = jsonObject2.get("lat").toString(); // 经度
                String lng = jsonObject2.get("lng").toString(); // 纬度
                String notes = jsonObject2.get("notes").toString(); // 打卡备注
                // if(true){  // 打印日志
                //     trigger.addErrorMessage(String.valueOf(dataTime+"^^"+checkin_time+"^^"+qdrq)); 
                // }
                if ("".equals(crmdakaobj1.get("qdsj")==null?"":crmdakaobj1.get("qdsj")+"")) { // 存储上班打卡的数据
                    crmdakaobj1.put("qdtype",checkin_type); // 打卡类型
                    crmdakaobj1.put("qdsj",curDate); // 签到时间
                    crmdakaobj1.put("qdwz",location_detail); // 签到地址
                    crmdakaobj1.put("ownerid",dakauserid); // 所有人
                    crmdakaobj1.put("gh",gh); // 工号
                    crmdakaobj1.put("rq",qdrq); // 签到日期
                    // crmdakaobj1.put("xmmc",xmmc); // 项目名称
                    crmdakaobj1.put("bz",notes); // 备注
                    crmdakaobj1.put("qdzb",lat+","+lng); // 签到坐标
                    crmdakaobj1.put("lastmodifydate",curDate); // 最后修改时间
                    crmdakaobj1.put("lastmodifybyid",dakauserid); // 最后修改人
                    crmdakaobj1.put("createdate",curDate); // 创建时间
                    crmdakaobj1.put("createbyid",dakauserid); // 创建人
                } else {
                    crmdakaobj1.put("qttype",checkin_type); // 打卡类型
                    crmdakaobj1.put("qtsj",curDate); // 签退时间
                    crmdakaobj1.put("qtwz",location_detail); // 签退地址
                    crmdakaobj1.put("qtzb",lat+","+lng); // 签退坐标
                    crmdakaobj1.put("lastmodifydate",curDate); // 最后修改时间
                    crmdakaobj1.put("lastmodifybyid",dakauserid); // 最后修改人
                }
            }
        }
        adduser=adduser+qywxuserid+",";
        objarray.add(crmdakaobj1);
       String crmqdsj =  crmdakaobj1.get("createbyid")==null?"":crmdakaobj1.get("createbyid")+"";
       String crmqrq =  crmdakaobj1.get("rq")==null?"":crmdakaobj1.get("rq")+"";
       String attsql = "select id,qdtype,qdsj,qdwz,ownerid,gh,rq,bz,qdzb,createbyid,createdate,qttype,qtsj,qtwz,qtzb,lastmodifydate,lastmodifybyid from attendance where createbyid='"+crmqdsj+"' and rq = '"+crmqrq+"' and is_deleted='0'";
        // if(true){  // 打印日志
        //     trigger.addErrorMessage(String.valueOf(attsql.toString())); 
        // }       
       List<CCObject> attlist = cs.cqlQuery("attendance",attsql);
        if (attlist.size()==1) {
            for(CCObject att:attlist) {
                String qdsj =att.get("qdsj")==null?"":att.get("qdsj")+"";
                if ("".equals(qdsj)) {
                    att.put("qdtype",crmdakaobj1.get("qdtype")); // 打卡类型
                    att.put("qdsj",crmdakaobj1.get("qdsj")); // 签到时间
                    att.put("qdwz",crmdakaobj1.get("qdwz")); // 签到地址
                    att.put("ownerid",crmdakaobj1.get("ownerid")); // 所有人
                    att.put("gh",crmdakaobj1.get("gh")); // 工号
                    att.put("rq",crmdakaobj1.get("rq")); // 签到日期
                    att.put("bz",crmdakaobj1.get("bz")); // 备注
                    att.put("qdzb",crmdakaobj1.get("qdzb")); // 签到坐标
                    att.put("lastmodifydate",crmdakaobj1.get("lastmodifydate")); // 最后修改时间
                    att.put("lastmodifybyid",crmdakaobj1.get("lastmodifybyid")); // 最后修改人
                    att.put("createdate",crmdakaobj1.get("createdate")); // 创建时间
                    att.put("createbyid",crmdakaobj1.get("createbyid")); // 创建人
                    List<CCObject> projectgroup = cs.cqlQuery("ProjectSaleGroup","select xmmc  from ProjectSaleGroup where xmxsy='"+att.get("createdate")+"' and is_deleted='0'");
                    for (CCObject group:projectgroup) {
                        String xmmc =group.get("xmmc")==null?"":group.get("xmmc")+"";
                        att.put("xmmc",xmmc);
                    }
                    this.update(att);
                }
                String qtsj =att.get("qtsj")==null?"":att.get("qtsj")+"";
                if ("".equals(qtsj)) {
                    att.put("qttype",crmdakaobj1.get("qttype")); // 打卡类型
                    att.put("qtsj",crmdakaobj1.get("qtsj")); // 签到时间
                    att.put("qtwz",crmdakaobj1.get("qtwz")); // 签到地址
                    att.put("qtzb",crmdakaobj1.get("qtzb")); // 签到坐标
                    att.put("lastmodifydate",crmdakaobj1.get("lastmodifydate")); // 创建时间
                    att.put("lastmodifybyid",crmdakaobj1.get("lastmodifybyid")); // 创建人
                    this.update(att);
                }
            }
        } else if(attlist.size()==0){
            if (!"".equals(crmqdsj)) { // 如果企微没有打卡数据，直接不插入
                // if(true){  // 打印日志
                //     trigger.addErrorMessage(String.valueOf(crmdakaobj1)); 
                // }
                this.insert(crmdakaobj1);
            }
        }
   }
// if(true){  // 打印日志
//     trigger.addErrorMessage(String.valueOf(objarray.toString()+"跳过了")); 
// }
// 将遍历crm的每一个id，将获取到所有的考勤信息，汇总到各自id里，保存到crm中 end

