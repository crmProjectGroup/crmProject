/**
 * 20210305 监控所有代理项目并且审批通过的合同, 监控到期 三个月 和 六个月 时 发送 邮件 和 短信 给 王茜 郑柏  许畅
 */


//-------------------获取系统当前时间-------------------
java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
String nowday = sdf.format(new Date());
java.util.Calendar cal = java.util.Calendar.getInstance();
CCService cs = new CCService((UserInfo)userInfo);
//List<CCObject> list = cs.cqlQuery("Opportunity","select o.xmmc,o.khmc,c.name,o.ownerid,o.id,zlkssj,zlnx,c.mobile from Opportunity o,ccuser c where c.id = o.ownerid and o.is_deleted = '0' and jieduan = '成交' and recordtype='2018BD60B25D1A2mLTd7' and isusing='1' and zlkssj is not null");
List<CCObject> list = cs.cqlQuery("Contract","select id,htmc,htksrq,htjsrq,recordtype from Contract where is_deleted='0'  and zhuangtai = '审批通过'  and htksrq is not null and htjsrq is not null  and  recordtype in ('20181E6B59BB3F01y0dJ','2018DD003BF4BC34I6po','0072018BE8FEC55oPxGh')");

//----------所有代理合同-----------
for(CCObject item:list){
     String htksrq = item.get("htksrq")==null?"":item.get("htksrq")+"";//合同开始时间
     String htjsrq = item.get("htjsrq")==null?"":item.get("htjsrq")+"";//合同结束时间
     String id = item.get("id")==null?"":item.get("id")+"";//id
     String htname = item.get("htmc")==null?"":item.get("htmc")+"";//合同名称
     long nowdaytime = sdf.parse(nowday).getTime(); // 当前时间
     long jsdate = sdf.parse(htjsrq).getTime(); // 结束时间
     long day = (jsdate-nowdaytime)/(24*60*60*1000);

    //  if(true){  // 打印日志
    //     trigger.addErrorMessage(String.valueOf(day)); 
    // }
     if(day==90 || day==180){
         List <CCObject> users = cs.cqlQuery("ccuser","select id,mobile,email,name from ccuser where is_deleted='0' and isusing='1' and id in ('005201827888B25tm2iq','00520184563249EuLvVg','00520187C46607BlOfN0')");
         //List <CCObject> users = cs.cqlQuery("ccuser","select id,mobile,email,name from ccuser where is_deleted='0' and isusing='1' and id in ('0052021B8AF08B5or6n9')");   
         for(CCObject user:users) {
                String mobile = user.get("mobile")==null?"":user.get("mobile")+"";//电话
                String ownerid = user.get("id")==null?"":user.get("id")+"";//人的id
                CCObject Event = new CCObject("Event");	  
                Event.put("begintime",sdf.format(new Date()).substring(0,10)+" 8:00:00");
                Event.put("endtime",sdf.format(new Date()).substring(0,10)+" 10:00:00");
                Event.put("belongtoid",ownerid);//被分配人
                Event.put("ownerid",ownerid);//所有人
                Event.put("name","合同到期提醒");//事件名称          
                Event.put("relateid",htname);
                if (day==90) {
                    Event.put("remark",htname+"还有三个月到期"); 	
                    Event.put("subject","代理合同提前三个月到期提醒");//主题
                } else {
                    Event.put("remark",htname+"还有六个月到期"); 	
                    Event.put("subject","代理合同提前六个月到期提醒");//主题
                }
                Event.put("isemailnotification","true");//是否邮件通知
                Event.put("isremider","true");//是否提醒
                Event.put("remindertime",sdf.format(new Date()).substring(0,10)+" 9:00:00");//提醒时间
                Event.put("relateobj","002");	
                Event.put("istask","0");
                Event.put("sjlx","预约联系");	
                Event.put("status","进行中");
                Event.put("expiredate",nowday.substring(0,10));
                Event.put("createbyid","0052018FF9F7905NEd6u");
                cs.insertLt(Event);
                if(!"".equals(mobile)){
                    //恒邦置地大厦独家驻场租赁代理合同
                    String txt = htname+"即将到期，请及时跟进维护客户并了解客户需求";
                    String result = "-20";
                    String urls = "http://smsapi.c123.cn/OpenPlatform/OpenApi?action=sendOnce&ac=1001@1775810010&authkey=0d9f091e6cb422ef8d5c&cgid=0&csid=0&m="+mobile+"&c=【深圳瑞信行】"+java.net.URLEncoder.encode(txt,"utf-8");       
                    java.net.HttpURLConnection httpconn = null;
                    try{
                        result = urls;
                        java.net.URL url = new java.net.URL(urls);
                        httpconn = (java.net.HttpURLConnection) url.openConnection();
                        java.io.BufferedReader rd = new java.io.BufferedReader(new java.io.InputStreamReader(httpconn.getInputStream()));
                        String resultxml = rd.readLine();
                        result = resultxml.indexOf("result=") != -1?resultxml.substring(resultxml.indexOf("result=\"")+8,resultxml.indexOf("\">")):"";
                        rd.close();
                    }catch (java.net.MalformedURLException e) {
                        e.printStackTrace();
                            }catch (java.io.IOException e) {
                        e.printStackTrace();
                                }catch (Exception e){
                        e.printStackTrace();
                            }finally{
                                    if (httpconn != null) {
                            httpconn.disconnect();
                            httpconn = null;
                        }
                                }
                
                        }
            }
     }


}