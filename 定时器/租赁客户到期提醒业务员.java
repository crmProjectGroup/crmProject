//-------------------获取系统当前时间-------------------
java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
String nowday = sdf.format(new Date());
java.util.Calendar cal = java.util.Calendar.getInstance();
CCService cs = new CCService((UserInfo)userInfo);
List<CCObject> list = cs.cqlQuery("Opportunity","select o.xmmc,o.khmc,c.name,o.ownerid,o.id,zlkssj,zlnx,c.mobile from Opportunity o,ccuser c where c.id = o.ownerid and o.is_deleted = '0' and jieduan = '成交' and recordtype='2018BD60B25D1A2mLTd7' and isusing='1' and zlkssj is not null");

//----------所有租赁成交客户-----------
for(CCObject item:list){
     String zlkssj = item.get("zlkssj")==null?"":item.get("zlkssj")+"";//租赁开始时间
     String khmc = item.get("khmc")==null?"":item.get("khmc")+"";//客户id
     double zlnx = item.get("zlnx")==null?0:Double.valueOf(item.get("zlnx")+"");//租赁年限
     String id = item.get("id")==null?"":item.get("id")+"";//id
     String ownerid = item.get("ownerid")==null?"":item.get("ownerid")+"";//所有人id
     String mobile = item.get("mobile")==null?"":item.get("mobile")+"";//所有人电话
     String name = item.get("name")==null?"":item.get("name")+"";//姓名
     String xmmc = item.get("xmmc")==null?"":item.get("xmmc")+"";//项目名称

     cal.setTime(sdf.parse(zlkssj));
     cal.add(Calendar.DATE,(int)zlnx*365);
     String txday = cal.get(Calendar.YEAR)+"-"+(cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.DAY_OF_MONTH);
     long time1 = sdf.parse(txday).getTime();
     long time2 = sdf.parse(nowday).getTime();
     long day = (time1-time2)/(24*60*60*1000);

     if(day==30){
             List<CCObject> accountlist = cs.cqlQuery("Account","select name from Account where is_deleted = '0' and id='"+khmc+"'");
             List<CCObject> projectlist = cs.cqlQuery("Project","select name from Project where is_deleted= '0' and id = '"+xmmc+"'");
             String accountname = "";
             String projectname = "";
             if(projectlist.size()>0)projectname = projectlist.get(0).get("name")+"";
             if(accountlist.size()>0)accountname = accountlist.get(0).get("name")+"";
             CCObject Event = new CCObject("Event");	  
             Event.put("remark",accountname+"还有一个月到期"); 	
	     Event.put("begintime",sdf.format(new Date()).substring(0,10)+" 8:00:00");
             Event.put("endtime",sdf.format(new Date()).substring(0,10)+" 10:00:00");
	     Event.put("belongtoid",ownerid);//被分配人
	     Event.put("ownerid",ownerid);//所有人
	     Event.put("name","租赁到期提醒");//事件名称          
             Event.put("relateid",khmc);
             Event.put("subject","租赁客户提前一个月到期提醒");//主题
	     Event.put("isemailnotification","false");//是否邮件通知
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
                       String txt = name+",您名下的"+accountname+"在"+projectname+"项目的租赁即将到期，请及时跟进维护客户并了解客户租赁需求";
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