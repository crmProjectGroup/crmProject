/**
*描述：工作日报客户批次计算(经理) 
*      
*创建人：武申金
*创建时间：2018/07/10
*最后修改时间：2018/07/10
*/
//获取CCS
CCService css = new CCService((UserInfo)userInfo);
java.util.Calendar cal = java.util.Calendar.getInstance();
cal.setTime(new Date());
int year = cal.get(Calendar.YEAR);//当前年份
int month = cal.get(Calendar.MONTH)+1;//当前月份
int day = cal.get(Calendar.DATE);//当前天数

//查询经理工作日报
List<CCObject> userlist = css.cquery("DailyReport","YEAR(createdate)='"+year+"' and MONTH(createdate)='"+month+"' and DAY(createdate)='"+day+"' and recordtype='20183BC7190A67ELt92g'");



 for(CCObject obj:userlist){
            String uid = obj.get("ownerid")==null?"":obj.get("ownerid")+""; //所有人id
	    List<CCObject> list = css.cquery("Account","YEAR(createdate)='"+year+"' and MONTH(createdate)='"+month+"' and DAY(createdate)='"+day+"' and createbyid in (select id from ccuser where isusing = '1' and manager='"+uid+"')");
            List<CCObject> jxlist = css.cquery("Account","YEAR(createdate)='"+year+"' and MONTH(createdate)='"+month+"' and DAY(createdate)='"+day+"' and createbyid in (select id from ccuser where isusing = '1' and manager='"+uid+"') and recordtype='2018525F215221DtlTXV'");
            List<CCObject> zjlist = css.cquery("zjnr","YEAR(createdate)='"+year+"' and MONTH(createdate)='"+month+"' and DAY(createdate)='"+day+"' and zjtjr in (select id from ccuser where isusing = '1' and manager='"+uid+"')");
            obj.put("xkhtj",list.size());
            obj.put("jxtj",jxlist.size());
            obj.put("jrld",zjlist.size());
            css.updateLt(obj);
       }


      