java.util.Calendar cal = java.util.Calendar.getInstance();
cal.setTime(new Date());
int year = cal.get(Calendar.YEAR);//当前年份
int month = cal.get(Calendar.MONTH)+1;//当前月份

List<CCObject> list = this.cquery("ryjx","nd='"+year+"' and yf ='"+month+"' and spzt ='审批通过' and recordtype='2018ED6B4DF92033DeWs'");
if(list.size()>0){
      for(CCObject item:list){
            String bkhr = item.get("bkhr")==null?"":item.get("bkhr")+"";//被考核人
            int lzrs=this.cquery("ccuser","is_deleted<>'1'and manager='"+bkhr+"' and YEAR(lastmodifydate)='"+year+"' and MONTH(lastmodifydate)='"+month+"' and isusing='0'").size();
            //查询本月离职人员
            int fs=lzrs*(-10);
            item.put("tdwj",fs+"");
            this.updateLt(item);
      }
}


