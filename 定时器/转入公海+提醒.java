/*
version: 2.0
date:20190527
author:tom
Old version: 遍历所有成交和丢单的业务机会, 遍历相关的跟进记录, 按创建时间排序, 判断最新的一次与现实时间的差距, 分成有建过跟进和没建过跟进(取客户创建时间), 分别对7天和14天做提醒和跳公客提醒;   弊病:同一个客户可能对应多个业务机会

New version: 剔除成交和丢单,转介,进线客户, 公客,(这里可以用业务机会的数量控制转介客户,业务机会数>0) , 以客户id去找跟进记录.  其他逻辑与旧版一致.
*/

//-------------------获取系统当前时间-------------------
java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
String nowday = sdf.format(new Date());

CCService cs = new CCService((UserInfo)userInfo);
//-------------------筛选未成交与成交信息--------------------
//List<CCObject> list = cs.cqlQuery("Opportunity","select o.id,o.ownerid,o.khmc,o.createdate,o.xmmc from Opportunity o,Account a where o.is_deleted='0' and jieduan <> '成交' and jieduan <> '未成交' and o.xmmc not in ('a052018F7FCFA55YIdRp','a05201955D07905hz3vm') and a.ghkh = '否' and a.id = o.khmc");
List<CCObject> list = cs.cqlQuery("Account","select a.id, a.name, a.ownerid, a.createdate, a.xmmc from Account a where (select count(*) from Opportunity o where o.khmc=a.id and o.is_deleted='0' and (jieduan = '成交' or jieduan = '未成交')) = 0 and (select count(*) from Opportunity o where o.khmc=a.id and o.is_deleted='0') > 0 and a.is_deleted='0' and a.xmmc not in ('a052018F7FCFA55YIdRp','a05201955D07905hz3vm') and a.ghkh = '否' and a.sfzjkh='否'");

if(list.size()>0){
	for(CCObject item:list){
		String id = item.get("id")==null?"":item.get("id")+"";//客户id
		String ownerid = item.get("ownerid")==null?"":item.get("ownerid")+"";//所有人
		//String khmc = item.get("khmc")==null?"":item.get("khmc")+"";//客户id
		String xmmc = item.get("xmmc")==null?"":item.get("xmmc")+"";//项目id
		String accountname = item.get("name")==null?"":item.get("name")+"";//客户名

		List<CCObject> detaillist = cs.cqlQuery("ywjhgjmx","select * from ywjhgjmx where kh = '"+id+"' order by createdate desc");//跟进详情
		if(detaillist.size()>0){
			String createdate = detaillist.get(0).get("createdate")==null?"":detaillist.get(0).get("createdate")+"";//创建时间
			long time1 = sdf.parse(nowday).getTime();
			long time2 = sdf.parse(createdate).getTime();
			long day = (time1-time2)/(24*60*60*1000);
			List<CCObject> ccuserlist = cs.cqlQuery("ccuser","select manager,name from ccuser where id = '"+ownerid+"'");
			//List<CCObject> accountlist = cs.cqlQuery("Account","select name from Account where is_deleted = '0' and id='"+khmc+"'");
			//String accountname = "";
			String name = "";
			String manager = "";
			if(ccuserlist.size()>0){
				name = ccuserlist.get(0).get("name")+"";
				manager = ccuserlist.get(0).get("manager")+"";
			}

			//if(accountlist.size()>0)accountname = accountlist.get(0).get("name")+"";
			if(day==7){
				CCObject Event = new CCObject("Event");
				Event.put("remark","七天未跟进"+accountname);
				Event.put("begintime",sdf.format(new Date()).substring(0,10)+" 8:00:00");
				Event.put("endtime",sdf.format(new Date()).substring(0,10)+" 10:00:00");
				Event.put("belongtoid",ownerid);//被分配人
				Event.put("ownerid",ownerid);//所有人
				Event.put("name","跟进提醒");//事件名称
				//Event.put("relateid",khmc);
				Event.put("relateid",id);
				Event.put("subject","七天未跟进客户提醒");//主题
				Event.put("isemailnotification","false");//是否邮件通知
				Event.put("isremider","true");//是否提醒
				Event.put("remindertime",sdf.format(new Date()).substring(0,10)+" 9:00:00");//提醒时间
				Event.put("relateobj","001");
				Event.put("istask","0");
				Event.put("sjlx","预约联系");
				Event.put("status","进行中");
				Event.put("expiredate",nowday.substring(0,10));
				Event.put("createbyid","0052018FF9F7905NEd6u");
				if(ccuserlist.size()>0){
					CCObject events = new CCObject("Event");
					events.put("belongtoid",manager);
					events.put("subject",name+"七天未跟进客户提醒");//主题
					events.put("ownerid",manager);
					events.put("name","业务员七天未跟进提醒");//事件名称
					events.put("remark",name+"七天未跟进"+accountname);
					events.put("relateobj","001");
					events.put("istask","0");
					events.put("createbyid","0052018FF9F7905NEd6u");
					events.put("expiredate",nowday.substring(0,10));
					events.put("remindertime",sdf.format(new Date()).substring(0,10)+" 9:00:00");//提醒时间
					events.put("isemailnotification","false");//是否邮件通知
					events.put("isremider","true");//是否提醒
					events.put("sjlx","预约联系");
					events.put("status","进行中");
					events.put("begintime",sdf.format(new Date()).substring(0,10)+" 8:00:00");
					events.put("endtime",sdf.format(new Date()).substring(0,10)+" 10:00:00");
					cs.insert(events);
				}
				cs.insert(Event);
			}else if(day>=14){
				//List<CCObject> share_list = cs.cquery("Account","parentid='"+khmc+"'",false);
				List<CCObject> share_list = cs.cquery("Account","parentid='"+id+"'",false); //原先客户id为khmc 现为id
				if(share_list.size()==0){
					List<CCObject> grouplist = cs.cquery("ProjectSaleGroup","xmmc = '"+xmmc+"'");
					for(CCObject groupitem:grouplist){
						String uid = groupitem.get("xmxsy")==null?"":groupitem.get("xmxsy")+"";
						CCObject accountshare = new CCObject("Account",CCObject.IS_SHARED);
                        //accountshare.put("parentid",khmc);
                        accountshare.put("parentid",id);
						accountshare.put("accesslevel", "Read");
						accountshare.put("rowcause", "Manual");
						accountshare.put("userorgroupid",uid);
						cs.insertLt(accountshare);
					}
				}
				if(ccuserlist.size()>0){
					CCObject Account = new CCObject("Account");
                    //Account.put("id",khmc);
                    Account.put("id",id);
					Account.put("ownerid",manager);
					Account.put("ghkh","是");
                    Account.put("sflzgk","否");
                    cs.updateLt(Account);
                    //因为一个客户可能对应多个业务机会, 这里改成for方法修改业务机会所有人
                    List<CCObject> Opportunitylist = cs.cquery("Opportunity","khmc = '"+id+"'");
                    for(CCObject Opportunityitem:Opportunitylist) {
                        String Opportunityid = Opportunityitem.get("id")==null?"":Opportunityitem.get("id")+"";
					    CCObject Opportunity = new CCObject("Opportunity");
					    Opportunity.put("id",Opportunityid);
					    Opportunity.put("ownerid",manager);
                        cs.updateLt(Opportunity);
                    }
				}
			}
	    }else{
		    String createdate = item.get("createdate")==null?"":item.get("createdate")+"";//创建时间
		    long time1 = sdf.parse(nowday).getTime();
		    long time2 = sdf.parse(createdate).getTime();
		    long day = (time1-time2)/(24*60*60*1000);
		    List<CCObject> ccuserlist = cs.cqlQuery("ccuser","select manager,name from ccuser where id = '"+ownerid+"'");
		    //List<CCObject> accountlist = cs.cqlQuery("Account","select name from Account where is_deleted = '0' and id='"+khmc+"'");
		    //String accountname = "";
		    String name = "";
		    String manager = "";
		    if(ccuserlist.size()>0){
		    	name = ccuserlist.get(0).get("name")+"";
		    	manager = ccuserlist.get(0).get("manager")+"";
		    }
		    //if(accountlist.size()>0)accountname = accountlist.get(0).get("name")+"";
		    if(day==7){
		    	CCObject Event= new CCObject("Event");
		    	Event.put("remark","七天未跟进"+accountname);
		    	Event.put("begintime",sdf.format(new Date()).substring(0,10)+" 8:00:00");
		    	Event.put("endtime",sdf.format(new Date()).substring(0,10)+" 10:00:00");
		    	Event.put("belongtoid",ownerid);//被分配人
		    	Event.put("ownerid",ownerid);//所有人
		    	Event.put("name","跟进提醒");//事件名称
                //Event.put("relateid",khmc);
                Event.put("relateid",id);
		    	Event.put("subject","七天未跟进客户提醒");//主题
		    	Event.put("isemailnotification","false");//是否邮件通知
		    	Event.put("isremider","true");//是否提醒
		    	Event.put("remindertime",sdf.format(new Date()).substring(0,10)+" 9:00:00");//提醒时间
		    	Event.put("relateobj","001");
		    	Event.put("istask","0");
		    	Event.put("sjlx","预约联系");
		    	Event.put("status","进行中");
		    	Event.put("createbyid","0052018FF9F7905NEd6u");
		    	Event.put("expiredate",nowday.substring(0,10));
		    	if(ccuserlist.size()>0){
		    	    CCObject events = new CCObject("Event");
		    	    events.put("belongtoid",manager);
		    	    events.put("subject",name+"七天未跟进客户提醒");//主题
		    	    events.put("ownerid",manager);
		    	    events.put("name","业务员七天未跟进提醒");//事件名称
		    	    events.put("remark",name+"七天未跟进"+accountname);
		    	    events.put("relateobj","001");
		    	    events.put("istask","0");
		    	    events.put("createbyid","0052018FF9F7905NEd6u");
		    	    events.put("expiredate",nowday.substring(0,10));
		    	    events.put("remindertime",sdf.format(new Date()).substring(0,10)+" 9:00:00");//提醒时间
		    	    events.put("isemailnotification","false");//是否邮件通知
		    	    events.put("isremider","true");//是否提醒
		    	    events.put("sjlx","预约联系");
		    	    events.put("status","进行中");
		    	    events.put("begintime",sdf.format(new Date()).substring(0,10)+" 8:00:00");
		    	    events.put("endtime",sdf.format(new Date()).substring(0,10)+" 10:00:00");
		    	    cs.insert(events);
		        }
		        cs.insert(Event);
	        }else if(day>=14){
                //List<CCObject> share_list = cs.cquery("Account","parentid='"+khmc+"'",false);
                List<CCObject> share_list = cs.cquery("Account","parentid='"+id+"'",false);
	        	if(share_list.size()==0){
	        		List<CCObject> grouplist = cs.cquery("ProjectSaleGroup","xmmc = '"+xmmc+"'");
	        		for(CCObject groupitem:grouplist){
	        			String uid = groupitem.get("xmxsy")==null?"":groupitem.get("xmxsy")+"";
	        			CCObject accountshare = new CCObject("Account",CCObject.IS_SHARED);
                        //accountshare.put("parentid",khmc);
                        accountshare.put("parentid",id);
	        			accountshare.put("accesslevel", "Read");
	        			accountshare.put("rowcause", "Manual");
	        			accountshare.put("userorgroupid",uid);
	        			cs.insertLt(accountshare);
	        		}
	        	}
	        	if(ccuserlist.size()>0){
	        		CCObject Account = new CCObject("Account");
                    //Account.put("id",khmc);
                    Account.put("id",id);
	        		Account.put("ownerid",manager);
	        		Account.put("ghkh","是");
	        		// CCObject Opportunity = new CCObject("Opportunity");
	        		// Opportunity.put("id",id);
	        		// Opportunity.put("ownerid",manager);
	        		cs.updateLt(Account);
                    // cs.updateLt(Opportunity);
                    //因为一个客户可能对应多个业务机会, 这里改成for方法修改业务机会所有人
                    List<CCObject> Opportunitylist = cs.cquery("Opportunity","khmc = '"+id+"'");
                    for(CCObject Opportunityitem:Opportunitylist) {
                        String Opportunityid = Opportunityitem.get("id")==null?"":Opportunityitem.get("id")+"";
					    CCObject Opportunity = new CCObject("Opportunity");
					    Opportunity.put("id",Opportunityid);
					    Opportunity.put("ownerid",manager);
                        cs.updateLt(Opportunity);
	        		}
	        	}
			}
		}
    }
}

			//20190422 当掉入公海,将客户是否来自公客标识改为'否'