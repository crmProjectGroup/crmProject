java.util.Calendar cal = java.util.Calendar.getInstance();
cal.setTime(new Date());
int year = cal.get(Calendar.YEAR);//当前年份
int month = cal.get(Calendar.MONTH)+1;//当前月份
int day = cal.get(Calendar.DATE);//当前天数

List<CCObject> userlist = this.cquery("DailyReport","YEAR(createdate)='"+year+"' and MONTH(createdate)='"+month+"' and DAY(createdate)='"+day+"' and recordtype='2018DD9D1CC8460ZOydm'");
if(userlist.size()>0){
       for(CCObject obj:userlist){
            String uid = obj.get("createbyid")==null?"":obj.get("createbyid")+"";//创建人
            List<CCObject> list = this.cquery("Account","YEAR(createdate)='"+year+"' and MONTH(createdate)='"+month+"' and DAY(createdate)='"+day+"' and createbyid = '"+uid+"'");
            List<CCObject> jxlist = this.cquery("Account","YEAR(createdate)='"+year+"' and MONTH(createdate)='"+month+"' and DAY(createdate)='"+day+"' and createbyid = '"+uid+"' and recordtype='2018525F215221DtlTXV'");
            List<CCObject> zjlist = this.cquery("zjnr","YEAR(createdate)='"+year+"' and MONTH(createdate)='"+month+"' and DAY(createdate)='"+day+"' and zjtjr = '"+uid+"'");
            obj.put("xkhtj",list.size());
            obj.put("jxtj",jxlist.size());
            obj.put("jrld",zjlist.size());
            this.updateLt(obj);
       }
}



