java.util.Calendar cal = java.util.Calendar.getInstance();
cal.setTime(new Date());
int year = cal.get(Calendar.YEAR);//当前年份
int month = cal.get(Calendar.MONTH)+1;//当前月份
CCService cs = new CCService(userInfo);

String myd = "0";//客户维护
double grdywcyjs = 0.00;//个人当月完成业绩数

String sql = "select * from ryjx where YEAR(createdate)='"+year+"' and MONTH(createdate)='"+month+"'";
List<CCObject> list = cs.cqlQuery("ryjx",sql);
if(list.size()>0){
      for(CCObject obj:list){
            String uid = obj.get("bkhr")==null?"":obj.get("bkhr")+"";
            String opprsql = "select ifnull(cjmj,0) as cjmj from Opportunity where ownerid = '"+uid+"' and YEAR(qysj) ='"+year+"' and MONTH(qysj) ='"+month+"' and jieduan ='成交' and spzt = '审批通过'";
            List<CCObject> opprlist = cs.cqlQuery("Opportunity",opprsql);       
            if(opprlist.size()>0){
                 for(CCObject cobj:opprlist){
                      grdywcyjs += Double.valueOf(cobj.get("cjmj")+"");
                 }
            }
            String mydsql = "select format(avg(ifnull(khmyd,0)),0) as myd from account where YEAR(createdate) = '"+year+"' and MONTH(createdate) = '"+month+"'";  

            List<CCObject> mydlist = cs.cqlQuery("account",mydsql);

            if(mydlist.size()>0){
                 myd = mydlist.get(0).get("myd")+"";
            }
            String ryjxsql = "select id as id from ryjx where bkhr = '"+uid+"' and YEAR(createdate) = '"+year+"' and MONTH(createdate) = '"+month+"'";
            List<CCObject> ryjxlist = cs.cqlQuery("ryjx",ryjxsql);
            if(ryjxlist.size()>0){
                for(CCObject item:ryjxlist){
                    item.put("khwh",myd);
                    item.put("grdywcyjs",grdywcyjs);                              
                    update(item);
                    
                }
            }
      }

}