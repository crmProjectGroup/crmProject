/**
 * 20210524 同步企微日报填写：同步昨天的日报（鉴于业务员写日报可能会超过晚上11点后，定时类只能最晚11点运行，所以又今天同步昨天的日报）
 */

CCService cs = new CCService((UserInfo)userInfo);
 // 获取日报 token begin
    String modeltoken = "";
    String corpiddaka = "ww5b99d45a63be2c50"; // 企业的id
    String secretdaka = "Fvqh0rbmtlmxVNgzX_PcMdhHB6y5wyEylyfzABT_rY8"; // 打卡模块的id
    String urldaka= "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid="+corpiddaka+"&corpsecret="+secretdaka+"";
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
    modeltoken = jsontokendaka.get("access_token").toString(); // 获取到了日报api 的token
// 获取日报 token end


//List<CCObject> userlist = cs.cqlQuery("ccuser","select id,qywxuserid,gh  from  ccuser  where is_deleted='0' and isusing='1' and profile in ('aaa2018A38306ED9syVe','aaa201723453E5EBNtzU','aaa000001')");
// 获取所有日报的单号 begin

    // 获取前一天的开始时间和结束时间 begin
    Date cur = new Date();
    Calendar calendar = new GregorianCalendar();
    //calendar.add(Calendar.DAY_OF_MONTH,-1);
    // calendar.add(Calendar.DAY_OF_MONTH,-1);
    
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
    useridjson.put("starttime",dayStart.getTime()/1000);
    useridjson.put("endtime",dayEnd.getTime()/1000);
    // useridjson.put("starttime",1617206492);
    // useridjson.put("endtime",1619798399);
    useridjson.put("cursor",0);
    useridjson.put("limit",100);
    String paramdaka = useridjson.toString();
    // if(true){  // 打印日志
    //     trigger.addErrorMessage(String.valueOf(paramdaka.toString())); 
    // }
    int dowhile=0;
    // do{
        String urlkaoqin= "https://qyapi.weixin.qq.com//cgi-bin/oa/journal/get_record_list?access_token="+modeltoken;
        java.net.URL kaoqinUrl = new java.net.URL(urlkaoqin);
        // 打开和URL之间的连接
        java.net.HttpURLConnection connkaoqin = (java.net.HttpURLConnection)kaoqinUrl.openConnection();
        net.sf.json.JSONObject ribaodhlist=null;
        List rbdhlist = new ArrayList(); // 存放所有人的日报单号
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
        ribaodhlist = net.sf.json.JSONObject.fromObject(reskaoqin);
        // if(true){  // 打印日志
        //     trigger.addErrorMessage(String.valueOf(ribaodhlist.toString())); 
        // }
        inkaoqin.close();
        // int endflag = ribaodhlist.get("endflag");
        // int next_cursor = ribaodhlist.get("next_cursor");
        rbdhlist = (List)ribaodhlist.get("journaluuid_list"); // 所有的日报单号
    // }while(endflag==0&&dowhile<5)
// 获取所有日报的单号 end
        // if(true){  // 打印日志
        //     trigger.addErrorMessage(rbdhlist.toString()); 
        // }
// 根据单号，获取详细日报，添加到crm中 begin
        String urlrbxx= "https://qyapi.weixin.qq.com/cgi-bin/oa/journal/get_record_detail?access_token="+modeltoken;
        java.net.URL rbUrl = new java.net.URL(urlrbxx);
        // 打开和URL之间的连接
        java.net.HttpURLConnection rbconnec = (java.net.HttpURLConnection)rbUrl.openConnection();
        net.sf.json.JSONObject rbxxjson=null; // 获取日报详细数据
        net.sf.json.JSONArray jsonArray = null;
        CCObject crmrbdata = new CCObject("DailyReport"); // 日报对象
        CCObject rbdata = new CCObject(); // 暂存日报对象
        // net.sf.json.JSONObject submituid=null; // 获取提交人
        // 设置通用的请求属性
        rbconnec.setRequestProperty("accept", "*/*");
        rbconnec.setRequestProperty("connection", "Keep-Alive");
        rbconnec.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
        // 发送POST请求必须设置如下两行
        rbconnec.setDoOutput(true);
        rbconnec.setDoInput(true);
        // 获取URLConnection对象对应的输出流
        java.io.PrintWriter outrb = new java.io.PrintWriter(rbconnec.getOutputStream());
        net.sf.json.JSONObject rbidjson = new net.sf.json.JSONObject();
        java.io.BufferedReader inrb = null;
        for (Object rb:rbdhlist) { // 遍历所有的日报id
            rbidjson.put("journaluuid",rb);
            String parbdata = rbidjson.toString();
            // 发送请求参数
            outrb.print(parbdata);
            // flush输出流的缓冲
            outrb.flush();
            //定义BufferedReader输入流来读取URL的响应
            inrb = new java.io.BufferedReader(new java.io.InputStreamReader(rbconnec.getInputStream()));
            String rbobj = inrb.readLine();
            rbxxjson = net.sf.json.JSONObject.fromObject(rbobj);
            rbxxjson = rbxxjson.getJSONObject("info");
            String getdate = rbxxjson.get("report_time").toString(); // 提交时间
            String sumqwuid = rbxxjson.getJSONObject("submitter").get("userid").toString(); // 提交人企微id
            rbdata.put("getdate",getdate);// 1. 封装提交时间
            rbdata.put("sumqwuid",sumqwuid); // 2. 封装提交人
            jsonArray = rbxxjson.getJSONObject("apply_data").getJSONArray("contents");
            for (int j = 0; j < jsonArray.size()-1; j++) {  // 遍历企微里单人详细日报信息
                net.sf.json.JSONObject jsonObject2 = jsonArray.getJSONObject(j); // 获取单个组件的数据
                String texttile = jsonObject2.getJSONObject("value").get("text").toString();// 日报信息
                rbdata.put("jinrb"+j,texttile);// 3. 封装日报信息
            }
            // recordtype ：  2018DD9D1CC8460ZOydm  业务员类型日报   20183BC7190A67ELt92g 项目经理类型
           String rbsql = "select * from ccuser c join DailyReport d on c.id=d.ownerid where d.is_deleted='0' and c.is_deleted='0' and c.isusing='1' and c.profile in ('aaa2018A38306ED9syVe','aaa201723453E5EBNtzU','aaa000001') and c.qywxuserid='"+sumqwuid+"'";     
           List<CCObject> attlist = cs.cqlQuery("DailyReport",rbsql);
            if(true){  // 打印日志
                trigger.addErrorMessage(String.valueOf(rbdata.toString())+"^^");
            }

        }
        inrb.close();  

// 根据单号，获取详细日报，添加到crm中 end
if(true){  // 打印日志
    trigger.addErrorMessage(String.valueOf(rbxxjson.toString()));
}