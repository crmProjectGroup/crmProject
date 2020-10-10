java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
String nowday = sdf.format(new Date());
CCService cs = new CCService((UserInfo)userInfo);
CCObject Event = new CCObject("Event");	  
Event.put("remark","事件添加测试"); 	
Event.put("begintime",sdf.format(new Date()).substring(0,10)+" 18:00:00");
Event.put("endtime",sdf.format(new Date()).substring(0,10)+" 22:00:00");
Event.put("belongtoid","0052018FF9F7905NEd6u");//被分配人0052018FF9F7905NEd6u
Event.put("ownerid","0052018FF9F7905NEd6u");//所有人
Event.put("name","事件添加测试");//事件名称          
//Event.put("relateid","0012018D8533C4EVi4PW");
Event.put("subject","事件添加测试");//主题
Event.put("isemailnotification","false");//是否邮件通知
Event.put("isremider","true");//是否提醒
Event.put("remindertime",sdf.format(new Date()).substring(0,10)+" 20:05:00");//提醒时间
//Event.put("relateobj","001");	
Event.put("istask","0");
//Event.put("sjlx","预约联系");	
Event.put("status","进行中");
Event.put("expiredate",nowday.substring(0,10));
Event.put("createbyid","0052018FF9F7905NEd6u");
cs.insert(Event);