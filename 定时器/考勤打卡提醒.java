java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
String nowday = sdf.format(new Date());
CCService cs = new CCService((UserInfo)userInfo);
/*   aaa2018A4671C21Hu0yi  运营中心  aaa2018A38306ED9syVe   项目经理   aaa201723453E5EBNtzU   业务员    aaa201858C360ADNceRX   运营业务员  */
List<CCObject> list = cs.cquery("ccuser","isusing = '1' and profile in ('aaa201723453E5EBNtzU','aaa2018A38306ED9syVe')");
for(CCObject item:list){
	CCObject Event1 = new CCObject("Event");
	Event1.put("remark","考勤签到提醒");
	Event1.put("begintime",sdf.format(new Date()).substring(0,10)+" 8:00:00");
	Event1.put("endtime",sdf.format(new Date()).substring(0,10)+" 20:00:00");
	Event1.put("belongtoid",item.get("id")==null?"":item.get("id")+"");//被分配人0052018FF9F7905NEd6u
	Event1.put("ownerid",item.get("id")==null?"":item.get("id")+"");//所有人
	Event1.put("name","考勤签到提醒");//事件名称
	//Event1.put("relateid","0012018D8533C4EVi4PW");
	Event1.put("subject","考勤签到提醒");//主题
	Event1.put("isemailnotification","false");//是否邮件通知
	Event1.put("isremider","true");//是否提醒
	Event1.put("remindertime",sdf.format(new Date()).substring(0,10)+" 8:45:00");//提醒时间
	//Event1.put("relateobj","001");
	Event1.put("istask","0");
	//Event1.put("sjlx","预约联系");
	Event1.put("status","进行中");
	Event1.put("expiredate",nowday.substring(0,10));
	Event1.put("createbyid","0052018FF9F7905NEd6u");
	cs.insert(Event1);
	
	CCObject Event2 = new CCObject("Event");
	Event2.put("remark","考勤签退提醒");
	Event2.put("begintime",sdf.format(new Date()).substring(0,10)+" 8:00:00");
	Event2.put("endtime",sdf.format(new Date()).substring(0,10)+" 20:00:00");
	Event2.put("belongtoid",item.get("id")==null?"":item.get("id")+"");//被分配人0052018FF9F7905NEd6u
	Event2.put("ownerid",item.get("id")==null?"":item.get("id")+"");//所有人
	Event2.put("name","考勤签退提醒");//事件名称
	//Event2.put("relateid","0012018D8533C4EVi4PW");
	Event2.put("subject","考勤签退提醒");//主题
	Event2.put("isemailnotification","false");//是否邮件通知
	Event2.put("isremider","true");//是否提醒
	Event2.put("remindertime",sdf.format(new Date()).substring(0,10)+" 18:00:00");//提醒时间
	//Event2.put("relateobj","001");
	Event2.put("istask","0");
	//Event2.put("sjlx","预约联系");
	Event2.put("status","进行中");
	Event2.put("expiredate",nowday.substring(0,10));
	Event2.put("createbyid","0052018FF9F7905NEd6u");
	cs.insert(Event2);
}