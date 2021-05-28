    /**
     * 20210524 同步企微日报填写：同步昨天的日报（鉴于业务员写日报可能会超过晚上11点后，定时类只能最晚11点运行，所以又今天同步昨天的日报）
     * calendar.add(Calendar.DAY_OF_MONTH,-1); 控制同步今天，还是昨天的时间
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
    // 获取所有日报的单号 begin
        // 获取前一天的开始时间和结束时间 begin
        Date cur = new Date();
        Calendar calendar = new GregorianCalendar();
        // calendar.add(Calendar.DAY_OF_MONTH,-1); // -1 ， 昨天
        // calendar.add(Calendar.DAY_OF_MONTH,-1); // -2 前天  ，如果删除，就是今天
        
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
            rbdhlist = (List)ribaodhlist.get("journaluuid_list"); // 所有的日报单号
        // }while(endflag==0&&dowhile<5)
    // 获取所有日报的单号 end
            // if(true){  // 打印日志
            //     trigger.addErrorMessage(rbdhlist.toString()); 
            // }
    // 根据单号，获取详细日报，添加到crm中 begin
            String urlrbxx= "https://qyapi.weixin.qq.com/cgi-bin/oa/journal/get_record_detail?access_token="+modeltoken;
            java.net.URL rbUrl = new java.net.URL(urlrbxx);

            net.sf.json.JSONObject rbxxjson=null; // 获取日报详细数据
            net.sf.json.JSONArray jsonArray = null;
            CCObject crmrbdata = new CCObject("DailyReport"); // 日报对象
            CCObject rbdata = new CCObject(); // 封装企微日报对象
            // net.sf.json.JSONObject submituid=null; // 获取提交人
            // 设置通用的请求属性
            java.io.BufferedReader inrb = null;
            boolean inrbor = false;
            // 获取URLConnection对象对应的输出流
            net.sf.json.JSONObject rbidjson = new net.sf.json.JSONObject();
            // if(true){  // 打印日志
            //     trigger.addErrorMessage(String.valueOf(rbdhlist.toString())+"^^1");
            // }
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd"); // 日期格式
            java.text.SimpleDateFormat subsdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 时间格式
            for (Object rb:rbdhlist) { // 遍历所有的日报id
                 // 打开和URL之间的连接
                 java.net.HttpURLConnection rbconnec = (java.net.HttpURLConnection)rbUrl.openConnection();
                 rbconnec.setRequestProperty("accept", "*/*");
                 rbconnec.setRequestProperty("connection", "Keep-Alive");
                 rbconnec.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
                 // 发送POST请求必须设置如下两行
                 rbconnec.setDoOutput(true);
                 rbconnec.setDoInput(true);        
                 java.io.PrintWriter outrb = new java.io.PrintWriter(rbconnec.getOutputStream());
                 rbidjson.put("journaluuid",rb);
                 String parbdata = rbidjson.toString();
                 // 发送请求参数
                 outrb.print(parbdata);
                 // flush输出流的缓冲
                 outrb.flush();
                 //定义BufferedReader输入流来读取URL的响应
                 inrb = new java.io.BufferedReader(new java.io.InputStreamReader(rbconnec.getInputStream()));
                 inrbor=true; // 给关闭流方法，做个开关
                 String rbobj = inrb.readLine();
                 rbxxjson = net.sf.json.JSONObject.fromObject(rbobj);
    // 获取企微日报接口返回的所有信息
                 rbxxjson = rbxxjson.getJSONObject("info");
                 // xpf 处理提交时间，因为是 毫秒数存储，需要转为为时间 begin
                 String getdate = rbxxjson.get("report_time").toString(); // 提交时间
                 Long lastupdate = Long.parseLong(getdate)*1000;//返回long基本数据类型
                 Date date = new Date();
                 date.setTime(lastupdate);
		         String sb=sdf.format(date.getTime()); // sql查询的日期
                 // xpf end
                 String sumqwuid = rbxxjson.getJSONObject("submitter").get("userid").toString(); // 提交人企微id
                 rbdata.put("getdate",subsdf.format(date.getTime()));// 1. 封装提交时间
                //  rbdata.put("sumqwuid",sumqwuid); // 2. 封装提交人
 //  根据日报单号，获取日报 的信息
                 jsonArray = rbxxjson.getJSONObject("apply_data").getJSONArray("contents");
                 int len = jsonArray.size()-1;
                 for (int j = 0; j < len ; j++) {  // 遍历企微里单人详细日报信息
                     net.sf.json.JSONObject jsonObject2 = jsonArray.getJSONObject(j); // 获取单个组件的数据
                     String texttile = jsonObject2.getJSONObject("value").get("text").toString();// 日报信息
                     rbdata.put("jinrb"+j,texttile);// 3. 封装日报信息
                 }
                // recordtype ：  2018DD9D1CC8460ZOydm  业务员类型日报   20183BC7190A67ELt92g 项目经理类型
                String useridsql = "select id from ccuser where qywxuserid= '"+sumqwuid+"'"; // 根据企业微信id，查询crm账户的id
                List<CCObject> useridlist = cs.cqlQuery("ccuser",useridsql);
        // 如果通过日报里的提交人，在CRM中没有找到用户，则这个日报不处理
                if (useridlist.size()==0) {
                     //   if(true){  // 打印日志
                    //       trigger.addErrorMessage(String.valueOf(sumqwuid.toString())+"^^"+"在crm中不存在企微id");
                    //   }
                    continue;
                } 
                String userid  = useridlist.get(0).get("id")==null?"":useridlist.get(0).get("id")+"";
                String datetime = " and createdate >= str_to_date('"+sb+" 00:00:00', '%Y-%m-%d %H:%i:%s') AND createdate<= str_to_date('"+sb+" 23:59:59', '%Y-%m-%d %H:%i:%s')";
                String rbsql = "select * from DailyReport where ownerid='"+userid+"' and is_deleted='0' "+datetime; // 根据crm账户的id，和 昨天的时间，查询到系统昨天默认创建的日志
                List<CCObject> rbcrmlist = cs.cqlQuery("DailyReport",rbsql); // 根据企业微信，查询crm里的日报数据，在将企业微信里填写的日报，修改到crm日报中
                for(CCObject rbcrm:rbcrmlist) {
                    String id = rbcrm.get("id")+"";
                  if ("".equals(rbcrm.get("jrclsy")) || rbcrm.get("jrclsy")==null) {
                      rbcrm.put("jrclsy",rbdata.get("jinrb0")); // 设置到crm里的字段， 今日工作
                  }
                  if ("".equals(rbcrm.get("mrgzjh")) || rbcrm.get("mrgzjh")==null) {
                      rbcrm.put("mrgzjh",rbdata.get("jinrb1")); // 明日工作
                  }
                  if ("".equals(rbcrm.get("bz")) || rbcrm.get("bz")==null) {
                      rbcrm.put("bz",rbdata.get("jinrb2")); // 其他事项
                  }
                  rbcrm.put("ownerid",userid); // 所有人
                  rbcrm.put("lastmodifybyid",userid); // 最后修改人
                  rbcrm.put("lastmodifydate",rbdata.get("getdate")); // 最后修改时间
                  String updaisql = "update DailyReport set jrclsy='"+rbcrm.get("jrclsy")+"',mrgzjh='"+rbcrm.get("mrgzjh")+"',bz='"+rbcrm.get("bz")+"',ownerid='"+rbcrm.get("ownerid")+"',lastmodifybyid='"+rbcrm.get("lastmodifybyid")+"',lastmodifydate='"+rbcrm.get("lastmodifydate")+"' where id='"+id+"'";
                //   if(true){  // 打印日志
                //       trigger.addErrorMessage(String.valueOf(updaisql.toString()));
                //   }
                  cs.cqlQuery("DailyReport",updaisql);
                //   this.update(rbcrm); 由于系统自带的修改方式，没有修改最后修改人和最后修改时间
                  break; //  如果系统里，一天创建了多个日报，只修改一个
                }
                if (rbconnec != null) {
                    rbconnec.disconnect();
                    rbconnec = null;
                }
            }
            if (inrbor) {
                inrb.close();  
            }
try {

}finally{
    if (connkaoqin != null) {
        connkaoqin.disconnect();
        connkaoqin = null;
    }
    if (conndaka != null) {
        conndaka.disconnect();
        conndaka = null;
    }
} 
    // 根据单号，获取详细日报，添加到crm中 end
    // if(true){  // 打印日志
    //     trigger.addErrorMessage(String.valueOf(rbdata.toString()+"^^2"));
    // }