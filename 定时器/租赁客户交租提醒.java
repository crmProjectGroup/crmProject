CCService cs = new CCService(userInfo);
//-------------------获取系统当前时间-------------------
java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
String nowday = sdf.format(new Date());
java.util.Calendar cal = java.util.Calendar.getInstance();

List<CCObject> list = cs.cqlQuery("Contract","select id,ownerid,chuzjzsj,czfmcs,jzzq,txjzrq from Contract where is_deleted = '0' and recordtype = '2018688E6A16761lGRZm' and zhuangtai = '审批通过' and id = '007201828D3A9ABcFhNX'");
for(CCObject item:list){
     String id = item.get("id")==null?"":item.get("id")+"";//id
     String ownerid = item.get("ownerid")==null?"":item.get("ownerid")+"";//所有人
     String czfmcs = item.get("czfmcs")==null?"":item.get("czfmcs")+"";//承租方id
     int jzzq = item.get("jzzq")==null?0:Double.valueOf(item.get("jzzq")+"").intValue();;//交租周期 
     String txjzrq = item.get("txjzrq")==null?"":item.get("txjzrq")+"";//提醒交租日期
     String chuzjzsj = item.get("chuzjzsj")==null?"":item.get("chuzjzsj")+"";//出租截止时间
     Date czjzdate = sdf.parse(chuzjzsj);
     Date nowdate = sdf.parse(sdf.format(new Date()));

 if(czjzdate.getTime()>nowdate.getTime()){
        if(!"".equals(txjzrq)){
        cal.setTime(sdf.parse(txjzrq));
        cal.add(Calendar.MONTH,jzzq);
        String txrq = sdf.format(cal.getTime());
        String rq = sdf.format(sdf.parse(txjzrq));

        if(rq.equals(nowday)){

                         List<CCObject> accountlist = cs.cqlQuery("Account","select name from Account where is_deleted = '0' and id='"+czfmcs+"'");
                         String accountname = "";
                         if(accountlist.size()>0)accountname = accountlist.get(0).get("name")+"";
                         CCObject Event = new CCObject("Event");	  
                         Event.put("remark","请提醒客户："+accountname+"交租！"); 	
		         Event.put("begintime",sdf.format(new Date()).substring(0,10)+" 8:00:00");
		         Event.put("endtime",sdf.format(new Date()).substring(0,10)+" 12:00:00");
		         Event.put("belongtoid",ownerid);//被分配人
		         Event.put("ownerid",ownerid);//所有人
	         	 Event.put("name","跟进提醒");//事件名称          
         		 Event.put("relateid",czfmcs);
		         Event.put("subject","提前五天收租提醒");//主题
		         Event.put("isemailnotification","true");//是否邮件通知
	         	 Event.put("isremider","true");//是否提醒
	         	 Event.put("remindertime",sdf.format(new Date()).substring(0,10)+" 12:00:00");//提醒时间
		         Event.put("relateobj","001");	
		         Event.put("istask","0");
                         Event.put("sjlx","预约联系");	
		         Event.put("status","进行中");
                         Event.put("expiredate",nowday.substring(0,10));
                         Event.put("createbyid","0052018FF9F7905NEd6u");
                         cs.insert(Event);
                         cs.cqlQuery("Contract","update Contract set txjzrq = '"+txrq+"' where id = '"+id+"'");
                        
         }
     
        }
     
     }
     


   
     
    
    
}