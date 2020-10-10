/*
description:实时计算考核分值
version: 1.0
date:20200915
author:tom
log:
*/

CCService cs = new CCService((UserInfo)userInfo);
//日期的处理
Calendar cal = Calendar.getInstance();
int year = cal.get(Calendar.YEAR);//当前年份
int month = cal.get(Calendar.MONTH) + 1;//当前月份 0代表1月
if(month==0){
  year=year-1;
	month=12; 
}
java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd");
String nowday = df.format(new Date());
String begin_day=year+"-"+month+"-01";
//out.print(nowday);
//String ksrq = request.getParameter("startTime")==null?begin_day:request.getParameter("startTime")+"";//开始日期
//out.print(ksrq);
//out.print(ksrq);
//String jsrq = request.getParameter("endTime")==null?nowday:request.getParameter("endTime")+"";//结束日期
//out.print(jsrq);
//String datetime = " and a.createdate >= str_to_date('"+ksrq+" 00:00:00', '%Y-%m-%d %H:%i:%s')  AND a.createdate <= str_to_date('"+jsrq+" 23:59:59', '%Y-%m-%d %H:%i:%s') ";


//String year = "2020"; //年
//String month = "8"; //月month

// 获取每月的创佣完成情况
// 通过成交的情况产生的代理明细获取创佣金额,成交时间5月select * from `user` where month(birthday) = 8 ;
List<CCObject> oppolist = this.cqlQuery("Opportunity","select d.sjsy as sjsy,o.id as oppoid,o.ownerid as ownerid,o.xmmc as xmmc from Opportunity o inner join dljsmxb d on o.id = d.ywjh and d.is_deleted='0' where o.is_deleted='0' and o.jieduan='成交' and o.spzt='审批通过' and o.cjsj >= str_to_date('"+begin_day+" 00:00:00', '%Y-%m-%d %H:%i:%s')  AND o.cjsj<= str_to_date('"+nowday+" 23:59:59', '%Y-%m-%d %H:%i:%s')");

//获取结算单的实际收益,业务机会的id去取值分佣比例,然后分别加到业务员对应的绩效考核上面
for(CCObject item:oppolist){
    String oppoid = item.get("oppoid")==null?"":item.get("oppoid")+"";//业务机会id
    double sjsy = item.get("sjsy")==null?0:Double.valueOf(item.get("sjsy")+"");//实际创佣
    String ownerid = item.get("ownerid")==null?"":item.get("ownerid")+"";//ownerid 成交者
    String xmmc = item.get("xmmc")==null?"":item.get("xmmc")+"";//xmmc 业务机会所属项目
    //根据业务机会id去获取对应的分佣情况 yjfc业绩分成 ywjkmc业务机会名称
    List<CCObject> yjfclist = this.cqlQuery("yjfc","select * from yjfc where ywjkmc='"+oppoid+"' and is_deleted='0'");
    //----------------------返写创佣---------------------
    if(yjfclist.size()>0){ //业务员又写业绩拆分的情况
        if(yjfclist.size()==1){
            String fcyh1 = yjfclist.get(0).get("fcyh1")==null?"":yjfclist.get(0).get("fcyh1")+"";//分成用户1
            String fcyh2 = yjfclist.get(0).get("fcyh2")==null?"":yjfclist.get(0).get("fcyh2")+"";//分成用户2
            String fcyh3 = yjfclist.get(0).get("fcyh3")==null?"":yjfclist.get(0).get("fcyh3")+"";//分成用户3
            String fcyh4 = yjfclist.get(0).get("fcyh4")==null?"":yjfclist.get(0).get("fcyh4")+"";//分成用户4
            String fcyh5 = yjfclist.get(0).get("fcyh5")==null?"":yjfclist.get(0).get("fcyh5")+"";//分成用户5
            double fcbl1 = yjfclist.get(0).get("fcbl1")==null?0:Double.valueOf(yjfclist.get(0).get("fcbl1")+"");//分成比例1
            double fcbl2 = yjfclist.get(0).get("fcbl2")==null?0:Double.valueOf(yjfclist.get(0).get("fcbl2")+"");//分成比例2  
            double fcbl3 = yjfclist.get(0).get("fcbl3")==null?0:Double.valueOf(yjfclist.get(0).get("fcbl3")+"");//分成比例3   
            double fcbl4 = yjfclist.get(0).get("fcbl4")==null?0:Double.valueOf(yjfclist.get(0).get("fcbl4")+"");//分成比例4
            double fcbl5 = yjfclist.get(0).get("fcbl5")==null?0:Double.valueOf(yjfclist.get(0).get("fcbl5")+"");//分成比例5
            String xm1 = yjfclist.get(0).get("szxm")==null?"":yjfclist.get(0).get("szxm")+"";//所属项目
            String xm2 = yjfclist.get(0).get("szxm2")==null?"":yjfclist.get(0).get("szxm2")+"";
            String xm3 = yjfclist.get(0).get("szxm3")==null?"":yjfclist.get(0).get("szxm3")+"";
            String xm4 = yjfclist.get(0).get("szxm4")==null?"":yjfclist.get(0).get("szxm4")+"";
            String xm5 = yjfclist.get(0).get("szxm5")==null?"":yjfclist.get(0).get("szxm5")+"";
            if(fcyh1.length()>0){
                List<CCObject> list1 = this.cquery("ryjx","nd = '"+year+"' and yf = '"+month+"' and xmmc='"+xm1+"' and bkhr = '"+fcyh1+"' and is_deleted='0'");
                //if(list1.size()==0) trigger.addErrorMessage("本月分成用户中包含未创建考核数据");
                if(list1.size()==1){
                Double grcywcz = list1.get(0).get("grcywcz")==null?0.0:Double.valueOf(list1.get(0).get("grcywcz")+"");
                //int frmj = list1.get(0).get("frmj")==null?0:Integer.valueOf(list1.get(0).get("frmj")+"");
                list1.get(0).put("grcywcz",grcywcz+(sjsy*fcbl1)/100);
                //list1.get(0).put("frmj",frmj+(cjmj*fcbl1)/100);
                this.update(list1.get(0));
                }
                List<CCObject> ryjxlist = this.cqlQuery("ryjx","select id,ifnull(tdcywcz,0) as tdcywcz from ryjx where spzt = '审批通过' and nd = '"+year+"' and yf = '"+month+"' and xmmc = '"+xm1+"' and is_deleted = '0' and bkhr=(select manager from ccuser where id='"+fcyh1+"' and is_deleted = '0')");
                if(ryjxlist.size()==1){
                    double tdcywcz = ryjxlist.get(0).get("tdcywcz")==null?0.0:Double.valueOf(ryjxlist.get(0).get("tdcywcz")+"");
                    ryjxlist.get(0).put("tdcywcz",tdcywcz+(sjsy*fcbl1)/100);
                    this.update(ryjxlist.get(0));
                }
            }
            if(fcyh2.length()>0){
                List<CCObject> list2 = this.cquery("ryjx","nd = '"+year+"' and yf = '"+month+"' and xmmc='"+xm2+"' and bkhr = '"+fcyh2+"' and is_deleted='0'");
                //if(list2.size()==0) trigger.addErrorMessage("本月分成用户中包含未创建考核数据");
                if(list2.size()==1) {
                Double grcywcz = list2.get(0).get("grcywcz")==null?0.0:Double.valueOf(list2.get(0).get("grcywcz")+"");
                list2.get(0).put("grcywcz",grcywcz+(sjsy*fcbl2)/100);
                //list1.get(0).put("frmj",frmj+(cjmj*fcbl1)/100);
                this.update(list2.get(0));
                }
                List<CCObject> ryjxlist = this.cqlQuery("ryjx","select id,ifnull(tdcywcz,0) as tdcywcz from ryjx where spzt = '审批通过' and nd = '"+year+"' and yf = '"+month+"' and xmmc = '"+xm2+"' and is_deleted = '0' and bkhr=(select manager from ccuser where id='"+fcyh2+"' and is_deleted = '0')");
                if(ryjxlist.size()==1){
                    double tdcywcz = ryjxlist.get(0).get("tdcywcz")==null?0.0:Double.valueOf(ryjxlist.get(0).get("tdcywcz")+"");
                    ryjxlist.get(0).put("tdcywcz",tdcywcz+(sjsy*fcbl2)/100);
                    this.update(ryjxlist.get(0));
                }
            }
            if(fcyh3.length()>0){
                List<CCObject> list3 = this.cquery("ryjx","nd = '"+year+"' and yf = '"+month+"' and xmmc='"+xm3+"' and bkhr = '"+fcyh3+"' and is_deleted='0'");
                //if(list3.size()==0) trigger.addErrorMessage("本月分成用户中包含未创建考核数据");
                if(list3.size()==1){
                Double grcywcz = list3.get(0).get("grcywcz")==null?0.0:Double.valueOf(list3.get(0).get("grcywcz")+"");
                list3.get(0).put("grcywcz",grcywcz+(sjsy*fcbl3)/100);
                //list1.get(0).put("frmj",frmj+(cjmj*fcbl1)/100);
                this.update(list3.get(0));
                }
                List<CCObject> ryjxlist = this.cqlQuery("ryjx","select id,ifnull(tdcywcz,0) as tdcywcz from ryjx where spzt = '审批通过' and nd = '"+year+"' and yf = '"+month+"' and xmmc = '"+xm3+"' and is_deleted = '0' and bkhr=(select manager from ccuser where id='"+fcyh3+"' and is_deleted = '0')");
                if(ryjxlist.size()==1){
                    double tdcywcz = ryjxlist.get(0).get("tdcywcz")==null?0.0:Double.valueOf(ryjxlist.get(0).get("tdcywcz")+"");
                    ryjxlist.get(0).put("tdcywcz",tdcywcz+(sjsy*fcbl3)/100);
                    this.update(ryjxlist.get(0));
                }
            }
            if(fcyh4.length()>0){
                List<CCObject> list4 = this.cquery("ryjx","nd = '"+year+"' and yf = '"+month+"' and xmmc='"+xm4+"' and bkhr = '"+fcyh4+"' and is_deleted='0'");
                //if(list4.size()==0) trigger.addErrorMessage("本月分成用户中包含未创建考核数据");
                if(list4.size()==1){
                Double grcywcz = list4.get(0).get("grcywcz")==null?0.0:Double.valueOf(list4.get(0).get("grcywcz")+"");
                list4.get(0).put("grcywcz",grcywcz+(sjsy*fcbl4)/100);
                this.update(list4.get(0));
                }
                List<CCObject> ryjxlist = this.cqlQuery("ryjx","select id,ifnull(tdcywcz,0) as tdcywcz from ryjx where spzt = '审批通过' and nd = '"+year+"' and yf = '"+month+"' and xmmc = '"+xm4+"' and is_deleted = '0' and bkhr=(select manager from ccuser where id='"+fcyh4+"' and is_deleted = '0')");
                if(ryjxlist.size()==1){
                    double tdcywcz = ryjxlist.get(0).get("tdcywcz")==null?0.0:Double.valueOf(ryjxlist.get(0).get("tdcywcz")+"");
                    ryjxlist.get(0).put("tdcywcz",tdcywcz+(sjsy*fcbl4)/100);
                    this.update(ryjxlist.get(0));
                }
            }
            if(fcyh5.length()>0){
                List<CCObject> list5 = this.cquery("ryjx","nd = '"+year+"' and yf = '"+month+"' and xmmc='"+xm5+"' and bkhr = '"+fcyh5+"' and is_deleted='0'");
                //if(list5.size()==0) trigger.addErrorMessage("本月分成用户中包含未创建考核数据");
                if(list5.size()==1) {
                Double grcywcz = list5.get(0).get("grcywcz")==null?0.0:Double.valueOf(list5.get(0).get("grcywcz")+"");
                list5.get(0).put("grcywcz",grcywcz+(sjsy*fcbl5)/100);
                this.update(list5.get(0));
                }
                List<CCObject> ryjxlist = this.cqlQuery("ryjx","select id,ifnull(tdcywcz,0) as tdcywcz from ryjx where spzt = '审批通过' and nd = '"+year+"' and yf = '"+month+"' and xmmc = '"+xm5+"' and is_deleted = '0' and bkhr=(select manager from ccuser where id='"+fcyh5+"' and is_deleted = '0')");
                if(ryjxlist.size()==1){
                    double tdcywcz = ryjxlist.get(0).get("tdcywcz")==null?0.0:Double.valueOf(ryjxlist.get(0).get("tdcywcz")+"");
                    ryjxlist.get(0).put("tdcywcz",tdcywcz+(sjsy*fcbl5)/100);
                    this.update(ryjxlist.get(0));
                }
            }

        }
                 
    } else{ //业务员没做业绩拆分,默认所有业绩属于一个业务员
        List<CCObject> list = this.cquery("ryjx","nd='"+year+"' and yf='"+month+"' and xmmc = '"+xmmc+"' and bkhr = '"+ownerid+"' and (recordtype='201884204B9C199odbgA' or recordtype='2020617BC5E66D4y0qLM')");
        if(list.size()==1){
            double grcywcz = list.get(0).get("grcywcz")==null?0.0:Double.valueOf(list.get(0).get("grcywcz")+"");
            list.get(0).put("grcywcz",grcywcz+sjsy);
            this.update(list.get(0));
        }
        List<CCObject> ryjxlist = this.cqlQuery("ryjx","select id,ifnull(tdcywcz,0) as tdcywcz from ryjx where xmmc='"+xmmc+"' and spzt = '审批通过' and nd = '"+year+"' and yf = '"+month+"' and recordtype='2018ED6B4DF92033DeWs' and is_deleted = '0' and bkhr=(select manager from ccuser where id='"+ownerid+"' and is_deleted = '0')");
        if(ryjxlist.size()==1){
              double tdcywcz = ryjxlist.get(0).get("tdcywcz")==null?0.0:Double.valueOf(ryjxlist.get(0).get("tdcywcz")+"");
              ryjxlist.get(0).put("tdcywcz",tdcywcz+sjsy);
              this.update(ryjxlist.get(0));
        }
    }
}

//wbzj 一般代理转介
List<CCObject> wbzjlist = this.cqlQuery("wbzj","select count(*) as num,createbyid from wbzj where is_deleted='0' and zt='审批通过' and createdate >= str_to_date('"+begin_day+" 00:00:00', '%Y-%m-%d %H:%i:%s')  AND createdate<= str_to_date('"+nowday+"  23:59:59', '%Y-%m-%d %H:%i:%s')"); 
for(CCObject wbzjitem:wbzjlist){
    String createbyid = wbzjitem.get("createbyid")==null?"":wbzjitem.get("createbyid")+"";//业务机会id
    double num = wbzjitem.get("num")==null?0:Double.valueOf(wbzjitem.get("num")+"");//条数
    if(num>0 && !"".equals(createbyid)){ //一般代理转介只考核业务员, 不对项目经理考核
        List<CCObject> ryjxlist = this.cquery("ryjx","nd = '"+year+"' and yf = '"+month+"' and bkhr = '"+createbyid+"' and is_deleted='0'");
        //if(ryjxlist.size()==0) trigger.addErrorMessage("本月分成用户中包含未创建考核数据");
        ryjxlist.get(0).put("xmdyldsmps",num);
        //list1.get(0).put("frmj",frmj+(cjmj*fcbl1)/100);
        this.update(ryjxlist.get(0));
    }
}


//日志,企业顾问,商务岗,项目经理 + 市场数据 + 客户录入及时
List<CCObject> ryjxlist = this.cquery("ryjx","nd='"+year+"' and yf='"+month+"' and is_deleted='0'"); //遍历所有人员绩效
for(CCObject ryjitem:ryjxlist){
    //double dyycqts = ryjitem.get("dyycqts")==null?0:Double.valueOf(ryjitem.get("dyycqts")+"");//应出勤天数 dyycqts
    
    String ownerid = ryjitem.get("bkhr")==null?"":ryjitem.get("bkhr")+""; //被考核人
    
    //改为收录实际考勤的情况的天数
    //List<CCObject> attendancelist = this.cqlQuery("attendance","select count(*) as num from attendance where is_deleted='0' and ownerid='"+ownerid+"' and createdate >= str_to_date('2020-07-01 00:00:00', '%Y-%m-%d %H:%i:%s')  AND createdate<= str_to_date('2020-07-31 23:59:59', '%Y-%m-%d %H:%i:%s')"); 
    //double dyycqts = attendancelist.get(0).get("dyycqts")==null?0:Double.valueOf(attendancelist.get(0).get("dyycqts")+"");
    double dyycqts = 0.0;
    // if("0052018983C9E3DFdNbv".equals(ownerid)){ //陈力
    //     dyycqts = 22;
    // } else if("0052019E6D80C3CYZwRS".equals(ownerid)){ //张自莉
    //     dyycqts = 25;
    // } else if("0052019BBC3F9A4s63Lo".equals(ownerid)){ //柳晖
    //     dyycqts = 26;
    // } else if("0052018DD656674WXYKg".equals(ownerid)){ //张贤华
    //     dyycqts = 27;
    // } else if("00520184A78C8815cOD1".equals(ownerid)){ //陈翠
    //     dyycqts = 21;
    // } else {
        List<CCObject> attendancelist = this.cqlQuery("attendance","select count(*) as num from attendance where is_deleted='0' and ownerid='"+ownerid+"' and createdate >= str_to_date('"+begin_day+" 00:00:00', '%Y-%m-%d %H:%i:%s')  AND createdate<= str_to_date('"+nowday+" 23:59:59', '%Y-%m-%d %H:%i:%s')"); 
        dyycqts = attendancelist.get(0).get("num")==null?0:Double.valueOf(attendancelist.get(0).get("num")+"");
    // }
    ryjitem.put("dyycqts",dyycqts);

    String recordtype = ryjitem.get("recordtype")==null?"":ryjitem.get("recordtype")+""; //被考核人
    //有效日志数
    List<CCObject> DailyReportlist = this.cqlQuery("DailyReport","select count(*) as dryxnum from DailyReport where ownerid='"+ownerid+"' and is_deleted='0' and (jrclsy is not null or jrclsy !='') and createdate >= str_to_date('"+begin_day+" 00:00:00', '%Y-%m-%d %H:%i:%s')  AND createdate<= str_to_date('"+nowday+" 23:59:59', '%Y-%m-%d %H:%i:%s')");
    int yyxrzs = DailyReportlist.get(0).get("dryxnum")==null?0:Integer.valueOf(DailyReportlist.get(0).get("dryxnum")+"");
    ryjitem.put("yyxrzs",yyxrzs);
    //this.update(ryjitem);

    //市场数据,企业和项目经理都是+20
    ryjitem.put("scsj",20);

    //客户信息是否在3小时内录入, 查询时差在3小时以上的客户
    List<CCObject> Accountlist = this.cqlQuery("Account","select count(*) as accountnum from Account where createbyid='"+ownerid+"' and is_deleted='0' and TIMESTAMPDIFF(hour,STR_TO_DATE(smsj,'%Y-%m-%d %H:%i:%s'),STR_TO_DATE(createdate,'%Y-%m-%d %H:%i:%s'))>3 and createdate >= str_to_date('"+begin_day+" 00:00:00', '%Y-%m-%d %H:%i:%s')  AND createdate<= str_to_date('"+nowday+" 23:59:59', '%Y-%m-%d %H:%i:%s')");
    int Accountnum = Accountlist.get(0).get("accountnum")==null?0:Integer.valueOf(Accountlist.get(0).get("accountnum")+"");
    if(Accountnum>0){ //有3小时以上的, 0分
        ryjitem.put("khjslr",0);
    } else { //都及时录入的,分岗位差别给分
        if("201884204B9C199odbgA".equals(recordtype)){ //企业顾问
            ryjitem.put("khjslr",30);
        } else if("2020617BC5E66D4y0qLM".equals(recordtype)){//商务岗
            ryjitem.put("khjslr",40);
        }
    }
    
    //客户满意度
    if("201884204B9C199odbgA".equals(recordtype) || "2020617BC5E66D4y0qLM".equals(recordtype)){
        //客户总数
        List<CCObject> Accountlist1 = this.cqlQuery("Account","select count(*) as accountnum from Account where createbyid='"+ownerid+"' and is_deleted='0' and createdate >= str_to_date('"+begin_day+" 00:00:00', '%Y-%m-%d %H:%i:%s')  AND createdate<= str_to_date('"+nowday+" 23:59:59', '%Y-%m-%d %H:%i:%s')");
        int Accountnum1 = Accountlist1.get(0).get("accountnum")==null?0:Integer.valueOf(Accountlist1.get(0).get("accountnum")+"");
        //好评的客户数
        List<CCObject> Accountlist2 = this.cqlQuery("Account","select count(*) as accountnum from Account where createbyid='"+ownerid+"' and is_deleted='0' and createdate >= str_to_date('"+begin_day+" 00:00:00', '%Y-%m-%d %H:%i:%s')  AND createdate<= str_to_date('"+nowday+" 23:59:59', '%Y-%m-%d %H:%i:%s') and khmyd =5");
        int Accountnum2 = Accountlist2.get(0).get("accountnum")==null?0:Integer.valueOf(Accountlist2.get(0).get("accountnum")+"");
        // if("0052019E6D80C3CYZwRS".equals(ownerid)){
        //     //trigger.addErrorMessage(Accountlist1.get(0).get("accountnum")+"");
        //     trigger.addErrorMessage(String.valueOf(Accountnum1)+";"+String.valueOf(Accountnum2)+";"+String.valueOf((float)Accountnum2/Accountnum1));
        // }
        if(Accountnum1==0){
            ryjitem.put("khmyd",0);
        } else{
            ryjitem.put("khmyd",30.00*((float)Accountnum2/Accountnum1));
        }
    }

    //早晚例会 + 回款金额 +客户满意度 -项目经理  
    if("2018ED6B4DF92033DeWs".equals(recordtype)){
        List<CCObject> xmhyjllist = this.cqlQuery("xmhyjl","select count(*) as num from xmhyjl where createbyid='"+ownerid+"' and is_deleted='0' and createdate >= str_to_date('"+begin_day+" 00:00:00', '%Y-%m-%d %H:%i:%s')  AND createdate<= str_to_date('"+nowday+" 23:59:59', '%Y-%m-%d %H:%i:%s')");
        int ylhjls = xmhyjllist.get(0).get("num")==null?0:Integer.valueOf(xmhyjllist.get(0).get("num")+"");
        ryjitem.put("ylhjls",ylhjls);

        //回款金额
        //先获取对应的项目id
        String xmmc = ryjitem.get("xmmc")==null?"":ryjitem.get("xmmc")+"";
        ////cloudccskjh 收款计划 hkjl 回款记录
        List<CCObject> hkjlllist = this.cqlQuery("hkjl","select sum(sjskje) as sumskje from hkjl h inner join cloudccskjh s on h.skjhmc=s.id where s.xmmc='"+xmmc+"' and s.is_deleted='0' and h.is_deleted='0' and h.khrq >= str_to_date('"+begin_day+" 00:00:00', '%Y-%m-%d %H:%i:%s')  AND h.khrq<= str_to_date('"+nowday+" 23:59:59', '%Y-%m-%d %H:%i:%s') group by s.xmmc");//cloudccskjh
        if(hkjlllist.size()>0){
            double sumskje = hkjlllist.get(0).get("sumskje")==null?0:Double.valueOf(hkjlllist.get(0).get("sumskje")+"");//累计回款金额
            ryjitem.put("tdhkwcz",sumskje);
        }

        //客户满意度
        //客户总数
        List<CCObject> Accountlist1 = this.cqlQuery("Account","select count(*) as accountnum from Account where xmmc='"+xmmc+"' and is_deleted='0' and createdate >= str_to_date('"+begin_day+" 00:00:00', '%Y-%m-%d %H:%i:%s')  AND createdate<= str_to_date('"+nowday+" 23:59:59', '%Y-%m-%d %H:%i:%s')");
        int Accountnum1 = Accountlist1.get(0).get("accountnum")==null?0:Integer.valueOf(Accountlist1.get(0).get("accountnum")+"");
        //好评的客户数
        List<CCObject> Accountlist2 = this.cqlQuery("Account","select count(*) as accountnum from Account where xmmc='"+xmmc+"' and is_deleted='0' and createdate >= str_to_date('"+begin_day+" 00:00:00', '%Y-%m-%d %H:%i:%s')  AND createdate<= str_to_date('"+nowday+" 23:59:59', '%Y-%m-%d %H:%i:%s') and khmyd =5");
        int Accountnum2 = Accountlist2.get(0).get("accountnum")==null?0:Integer.valueOf(Accountlist2.get(0).get("accountnum")+"");
        // if("0052018C4912C5ATnpuc".equals(ownerid)){
        //     trigger.addErrorMessage(Accountlist1.get(0).get("accountnum")+"");
        //     //trigger.addErrorMessage(String.valueOf(Accountnum1)+";"+String.valueOf(Accountnum2));
        // }
        if(Accountnum1==0){
            ryjitem.put("khmyd",0);
        } else{
            ryjitem.put("khmyd",30.00*((float)Accountnum2/Accountnum1));
        }
    }

    this.update(ryjitem);
}

// String urls = "https://k8mm1amta1700adb471ba12b.cloudcc.com/customize/page/9291p1140/yjdf.jsp?name=yjdf";
// java.net.HttpURLConnection httpconn = null;
// try{
//     //result = urls;
//     java.net.URL url = new java.net.URL(urls);
//     httpconn = (java.net.HttpURLConnection) url.openConnection();
//     httpconn.connect();
    
// }catch (java.net.MalformedURLException e) {
// 	e.printStackTrace();
// }catch (java.io.IOException e) {
// 	e.printStackTrace();
// }catch (Exception e){
// 	e.printStackTrace();
// }finally{
//     if (httpconn != null) {
// 	    httpconn.disconnect();
// 	    httpconn = null;
//     }
// }   


//分类型计算,企业顾问
List<CCObject> list1 = cs.cqlQuery("ryjx","select * from ryjx where nd='"+year+"' and yf ='"+month+"' and is_deleted = '0' and recordtype='201884204B9C199odbgA'");
for(CCObject item1:list1){
	//个人创佣目标 grcymb 及 个人创佣完成值 grcywcz
	double grcymb = item1.get("grcymb")==null?0.0:Double.valueOf(item1.get("grcymb")+"");//个人当月创佣目标
    double grcywcz = item1.get("grcywcz")==null?0.0:Double.valueOf(item1.get("grcywcz")+"");//grcywcz 个人当月创佣完成值
    double cy_d = 0.0 ;
    if(item1.get("grcymb")==null){
        cy_d = 0 ;
    } else{
        if(grcymb==0){
            cy_d = 1 ;
        } else{
            cy_d = grcywcz/grcymb ;
        }
    }

    // if(grcymb==0){
    //     cy_d = 0 ;
    // } else{
    //     cy_d = grcywcz/grcymb ;
    // }
	//double cy_d = grcywcz/grcymb ;
	if(cy_d<=1){
		cy_d = cy_d * 100 *0.4;
	} else if(cy_d>1&&cy_d<1.2){
		cy_d = cy_d * 120 *0.4;
	} else {
		cy_d = 120 *0.4;
	}

	//联动转介(只计算一般代理转介) xmdyldsmps
	Integer xmdyldsmps = 0;
	try{
		xmdyldsmps = item1.get("xmdyldsmps")==null?0:Integer.valueOf(item1.get("xmdyldsmps")+"");//项目当月联动上门批次
	}catch(NumberFormatException e) {
		String ldsmps = item1.get("xmdyldsmps")==null?"0.0":item1.get("xmdyldsmps")+""; 
		if(ldsmps.trim().indexOf('.')>0){
			ldsmps = ldsmps.substring(0,ldsmps.indexOf('.'));
		}
		xmdyldsmps = Integer.valueOf(ldsmps);
	}
	double ldzj_d=0.00;
	if(xmdyldsmps<3){
		ldzj_d = xmdyldsmps* 30*0.1;
	} else{
		ldzj_d = 100 *0.1;
	}

	//案场规范
	String yryb = item1.get("yryb")==null?"":item1.get("yryb")+"";//yryb仪容仪表 
	String khzblr = item1.get("khzblr")==null?"":item1.get("khzblr")+"";//28项技术指标录入 khzblr
	String hs = item1.get("hs")==null?"":item1.get("hs")+"";//话术hs
	String phgl = item1.get("phgl")==null?"":item1.get("phgl")+"";//配合管理phgl
	double acgf_d = 0.00;
	if("符合".equals(yryb)){
		acgf_d = acgf_d +10;
	} else{
		acgf_d = acgf_d +0;
	}

	if("优秀".equals(khzblr)){
		acgf_d = acgf_d +30;
	} else if("合格".equals(khzblr)){
		acgf_d = acgf_d +15;
	} else{
		acgf_d = acgf_d +0;
	}

	if("优秀".equals(hs)){
		acgf_d = acgf_d +30;
	} else if("合格".equals(hs)){
		acgf_d = acgf_d +15;
	} else{
		acgf_d = acgf_d +0;
	}

	if("优秀".equals(phgl)){
		acgf_d = acgf_d +30;
	} else if("合格".equals(phgl)){
		acgf_d = acgf_d +15;
	} else{
		acgf_d = acgf_d +0;
	}

	acgf_d = acgf_d* 0.1;

	//CRM录入
	double dyycqts = item1.get("dyycqts")==null?0:Double.valueOf(item1.get("dyycqts")+"");//dyycqts 当月应出勤天数
	int yyxrzs = item1.get("yyxrzs")==null?0:Integer.valueOf(item1.get("yyxrzs")+"");//该企业顾问当月有效日志数
	double rz = 0.0;
	if(yyxrzs>=dyycqts){
		rz = 20;
	}else{
		rz = 0;
	}

	double scsj = item1.get("scsj")==null?0.0:Double.valueOf(item1.get("scsj")+"");//市场数据scsj
	double khjslr = item1.get("khjslr")==null?0.0:Double.valueOf(item1.get("khjslr")+""); //客户信息及时录入khjslr
	double khmyd = item1.get("khmyd")==null?0.0:Double.valueOf(item1.get("khmyd")+""); //客户满意度 khmyd

	double crm_d = (rz+scsj+khjslr+khmyd)*0.4;

	double khfz = cy_d + ldzj_d + acgf_d + crm_d;

	//CCObject obj = new CCObject("ryjx");
	//obj.put("id",id);
	item1.put("rz",rz);
	item1.put("khfz",khfz);
	cs.updateLt(item1);
}

//分类型计算,项目经理
List<CCObject> list3 = cs.cqlQuery("ryjx","select * from ryjx where nd='"+year+"' and yf ='"+month+"' and is_deleted = '0' and recordtype='2018ED6B4DF92033DeWs'");
for(CCObject item3:list3){
	//团队创佣目标 tdcymb 及 团队创佣完成值 tdcywcz
    //double tdcymb = item3.get("tdcymb")==null?0.0:Double.valueOf(item3.get("tdcymb")+"");//创佣目标
    double grcymb = item3.get("grcymb")==null?0.0:Double.valueOf(item3.get("grcymb")+"");//grcymb 个人当月创佣目标
    double tdcywcz = item3.get("tdcywcz")==null?0.0:Double.valueOf(item3.get("tdcywcz")+"");//当月创佣完成值
    double cy_d = tdcywcz/grcymb ;
    if(item3.get("grcymb")==null){
        cy_d = 0 ;
    } else{
        if(grcymb==0){
            cy_d = 1 ;
        } else{
            cy_d = tdcywcz/grcymb ;
        }
    }
	if(cy_d<=1){
		cy_d = cy_d * 100 *0.4;
	} else if(cy_d>1&&cy_d<1.2){
		cy_d = cy_d * 100 *0.4;
	} else {
		cy_d = 120 *0.4;
	}

	//团队回款
	double tdhkmb = item3.get("tdhkmb")==null?0.0:Double.valueOf(item3.get("tdhkmb")+"");//团队回款目标
    double tdhkwcz = item3.get("tdhkwcz")==null?0.0:Double.valueOf(item3.get("tdhkwcz")+"");//团队回款完成值
	double hk_d = tdhkwcz/tdhkmb ;
	if(hk_d<1){
		hk_d = hk_d * 100 *0.1;
	} else if(hk_d>1&&hk_d<1.2){
		hk_d = hk_d * 120 *0.1;
	} else {
		hk_d = 120 *0.1;
	}

	//案场规范
	String xmda = item3.get("xmda")==null?"":item3.get("xmda")+"";//项目档案
	String xcgzgl = item3.get("xcgzgl")==null?"":item3.get("xcgzgl")+"";//现场工作管理
	String khzblr = item3.get("khzblr")==null?"":item3.get("khzblr")+"";//客户录入指标
	double acgf_d = 0.00;
	if("合格".equals(xmda)){
		acgf_d = acgf_d +20;
	} else{
		acgf_d = acgf_d +0;
	}

	if("优秀".equals(xcgzgl)){
		acgf_d = acgf_d +40;
	} else if("合格".equals(xcgzgl)){
		acgf_d = acgf_d +20;
	} else{
		acgf_d = acgf_d +0;
	}

	if("优秀".equals(khzblr)){
		acgf_d = acgf_d +40;
	} else if("合格".equals(khzblr)){
		acgf_d = acgf_d +20;
	} else{
		acgf_d = acgf_d +0;
	}

	acgf_d = acgf_d* 0.25;

	//CRM录入
	//日志
	Double dyycqts = item3.get("dyycqts")==null?0:Double.valueOf(item3.get("dyycqts")+"");//dyycqts 当月应出勤天数
	int yyxrzs = item3.get("yyxrzs")==null?0:Integer.valueOf(item3.get("yyxrzs")+"");//当月有效日志数
	double rz = 0.0;
	if(yyxrzs>=dyycqts){
		rz = 20;
	}else{
		rz = 0;
	}

	//市场数据
	double scsj = item3.get("scsj")==null?0.0:Double.valueOf(item3.get("scsj")+"");//市场数据scsj
	
	//早晚例会zwlh
	int ylhjls = item3.get("ylhjls")==null?0:Integer.valueOf(item3.get("ylhjls")+"");//月例会记录数
	double zwlh = 0.0;
	if(ylhjls>=dyycqts*2){
		zwlh = 30;
	}else{
		zwlh = 0;
	}

	//客户满意度
	double khmyd = item3.get("khmyd")==null?0.0:Double.valueOf(item3.get("khmyd")+""); //客户满意度 khmyd

	double crm_d = (rz+scsj+zwlh+khmyd)*0.25;

	double khfz = cy_d + hk_d + acgf_d + crm_d;

	//CCObject obj = new CCObject("ryjx");
	//obj.put("id",id);
	item3.put("rz",rz);
	item3.put("zwlh",zwlh);
	item3.put("khfz",khfz);
	cs.updateLt(item3);
}