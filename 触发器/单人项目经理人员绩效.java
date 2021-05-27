/*
description:单人计算项目经理考核分数
    固定项目经理的  id

*/

CCService cs = new CCService((UserInfo)userInfo);
//日期的处理
Calendar cal = Calendar.getInstance();
int year = cal.get(Calendar.YEAR);//当前年份
int month = cal.get(Calendar.MONTH) ;//上月
// if(true) {trigger.addErrorMessage(year+"^"+month);}
if(month==0){
  year=year-1;
	month=12; 
}
java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd");

String begin_day=year+"-"+month+"-01";
if (month == 12) {
    cal.add(Calendar.YEAR,-1);
}
cal.set(Calendar.MONTH, month-1); // 获取上个月
cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE) - 7);
String last7dt = df.format(cal.getTime());
// if(true) {trigger.addErrorMessage(last7dt);}


//获取上月最后一天
//cal.add(Calendar.MONTH, -1);
cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
String nowday = df.format(cal.getTime());


//日志,企业顾问,商务岗,项目经理 + 市场数据 + 客户录入及时
List<CCObject> ryjxlist = this.cquery("ryjx","nd='"+year+"' and yf='"+month+"' and is_deleted='0' and bkhr='0052018983C9E3DFdNbv'"); //遍历所有人员绩效
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

    String recordtype = ryjitem.get("recordtype")==null?"":ryjitem.get("recordtype")+""; //考核记录类型
    //有效日志数
    List<CCObject> DailyReportlist = this.cqlQuery("DailyReport","select count(*) as dryxnum from DailyReport where ownerid='"+ownerid+"' and is_deleted='0' and (jrclsy is not null or jrclsy !='') and createdate >= str_to_date('"+begin_day+" 00:00:00', '%Y-%m-%d %H:%i:%s')  AND createdate<= str_to_date('"+nowday+" 23:59:59', '%Y-%m-%d %H:%i:%s')");
    int yyxrzs = DailyReportlist.get(0).get("dryxnum")==null?0:Integer.valueOf(DailyReportlist.get(0).get("dryxnum")+"");
    ryjitem.put("yyxrzs",yyxrzs);
    //this.update(ryjitem);

    //市场数据,企业和项目经理都是+20
    //ryjitem.put("scsj",20);

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
        ryjitem.put("khs",Accountnum1); //20201027 b.客户数不显示
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
            ryjitem.put("khmyd",40.00*((float)Accountnum2/Accountnum1)); //客户满意度由30变成40
        }

        //内部联动转介 zjnr
        int nblds = 0;
        List<CCObject> zjnrlist = this.cqlQuery("zjnr","select count(*) as num from zjnr where is_deleted='0' and zjtjr='"+ownerid+"' and zjsj >= str_to_date('"+begin_day+" 00:00:00', '%Y-%m-%d %H:%i:%s')  AND zjsj<= str_to_date('"+nowday+"  23:59:59', '%Y-%m-%d %H:%i:%s')"); 
        nblds = zjnrlist.get(0).get("num")==null?0:Integer.valueOf(zjnrlist.get(0).get("num")+"");//

        ryjitem.put("nblds",nblds);
    }

    //早晚例会 + 回款金额 +客户满意度 -项目经理  
    if("2018ED6B4DF92033DeWs".equals(recordtype)){
        List<CCObject> xmhyjllist = this.cqlQuery("xmhyjl","select count(*) as num from xmhyjl where createbyid='"+ownerid+"' and is_deleted='0' and createdate >= str_to_date('"+begin_day+" 00:00:00', '%Y-%m-%d %H:%i:%s')  AND createdate<= str_to_date('"+nowday+" 23:59:59', '%Y-%m-%d %H:%i:%s')");
        int ylhjls = xmhyjllist.get(0).get("num")==null?0:Integer.valueOf(xmhyjllist.get(0).get("num")+"");
            // if ("0052018283AFC55mqWeb".equals(ownerid)) {
            //     if(true){  // 打印日志
            //         trigger.addErrorMessage(ylhjls+"");
            //     }
            // }
        ryjitem.put("ylhjls",ylhjls);

        //回款金额
        //先获取对应的项目id
        String xmmc = ryjitem.get("xmmc")==null?"":ryjitem.get("xmmc")+"";
        ////cloudccskjh 收款计划 hkjl 回款记录
        List<CCObject> hkjlllist = this.cqlQuery("hkjl","select sum(h.sjsy) as sumskje from hkjl h inner join cloudccskjh s on h.skjhmc=s.id where s.xmmc='"+xmmc+"' and s.is_deleted='0' and h.is_deleted='0' and h.khrq >= str_to_date('"+begin_day+" 00:00:00', '%Y-%m-%d %H:%i:%s')  AND h.khrq<= str_to_date('"+nowday+" 23:59:59', '%Y-%m-%d %H:%i:%s') group by s.xmmc");//cloudccskjh
        if(hkjlllist.size()>0){
            double sumskje = hkjlllist.get(0).get("sumskje")==null?0:Double.valueOf(hkjlllist.get(0).get("sumskje")+"");//累计回款金额
            ryjitem.put("tdhkwcz",sumskje);
        }

        //客户满意度
        //客户总数
        List<CCObject> Accountlist1 = this.cqlQuery("Account","select count(*) as accountnum from Account where xmmc='"+xmmc+"' and is_deleted='0' and createdate >= str_to_date('"+begin_day+" 00:00:00', '%Y-%m-%d %H:%i:%s')  AND createdate<= str_to_date('"+nowday+" 23:59:59', '%Y-%m-%d %H:%i:%s')");
        int Accountnum1 = Accountlist1.get(0).get("accountnum")==null?0:Integer.valueOf(Accountlist1.get(0).get("accountnum")+"");
        //好评的客户数
        ryjitem.put("khs",Accountnum1); //20201027 b.客户数不显示
        List<CCObject> Accountlist2 = this.cqlQuery("Account","select count(*) as accountnum from Account where xmmc='"+xmmc+"' and is_deleted='0' and createdate >= str_to_date('"+begin_day+" 00:00:00', '%Y-%m-%d %H:%i:%s')  AND createdate<= str_to_date('"+nowday+" 23:59:59', '%Y-%m-%d %H:%i:%s') and khmyd =5");
        int Accountnum2 = Accountlist2.get(0).get("accountnum")==null?0:Integer.valueOf(Accountlist2.get(0).get("accountnum")+"");
        // if("0052018C4912C5ATnpuc".equals(ownerid)){
        //     trigger.addErrorMessage(Accountlist1.get(0).get("accountnum")+"");
        //     //trigger.addErrorMessage(String.valueOf(Accountnum1)+";"+String.valueOf(Accountnum2));
        // }
        if(Accountnum1==0){
            ryjitem.put("khmyd",0);
        } else{
            ryjitem.put("khmyd",30.00*((float)Accountnum2/Accountnum1));  //项目经理客户满意度还是30分
        }

        //内部联动转介 zjnr
        int nblds = 0;
        List<CCObject> zjnrlist = this.cqlQuery("zjnr","select count(*) as num from zjnr where is_deleted='0' and szxm='"+xmmc+"' and zjsj >= str_to_date('"+begin_day+" 00:00:00', '%Y-%m-%d %H:%i:%s')  AND zjsj<= str_to_date('"+nowday+"  23:59:59', '%Y-%m-%d %H:%i:%s')"); 
        nblds = zjnrlist.get(0).get("num")==null?0:Integer.valueOf(zjnrlist.get(0).get("num")+"");//

        ryjitem.put("nblds",nblds);

        //市场数据改为只考核项目经理,一周内一定要本人或项目上的人有录入项目经理可以继续保持20分, 当0分后再不做判断,因为只针对项目经理,整块放到项目经理代码块中
        Double scsj = ryjitem.get("scsj")==null?0:Double.valueOf(ryjitem.get("scsj")+"");
        
        if((ryjitem.get("scsj")==null) || (scsj-20)<0.000001){
            
            List<CCObject> cjqklist = this.cqlQuery("cjqk","select count(*) as num from cjqk where is_deleted='0' and (createbyid = '"+ownerid+"' or createbyid in (select id from ccuser where manager = '"+ownerid+"' and isusing='1')) and createdate >= str_to_date('"+last7dt+" 00:00:00', '%Y-%m-%d %H:%i:%s')  AND createdate<= str_to_date('"+nowday+" 23:59:59', '%Y-%m-%d %H:%i:%s')"); 
            // if ("0052018283AFC55mqWeb".equals(ownerid)) {
            //     if(true){  // 打印日志
            //         trigger.addErrorMessage("select count(*) as num from cjqk where is_deleted='0' and (createbyid = '"+ownerid+"' or createbyid in (select id from ccuser where manager = '"+ownerid+"' and isusing='1')) and createdate >= str_to_date('"+last7dt+" 00:00:00', '%Y-%m-%d %H:%i:%s')  AND createdate<= str_to_date('"+nowday+" 23:59:59', '%Y-%m-%d %H:%i:%s')"); 
            //     }
            // }
            int cjqknum = cjqklist.get(0).get("num")==null?0:Integer.valueOf(cjqklist.get(0).get("num")+"");
            if(cjqknum>0){
            //     if ("0052018283AFC55mqWeb".equals(ownerid)) {
            //     if(true){  // 打印日志
            //         trigger.addErrorMessage(cjqknum+"");
            //     }
            // }
                ryjitem.put("scsj",20);
            } else{
                ryjitem.put("scsj",0);
            }
        }
    }   

    ryjitem.put("tdcywcz",0); //20201027 a 先将创佣完成值都改为0,避免重复计算
    ryjitem.put("grcywcz",0); 
    
    this.update(ryjitem);
}

//分类型计算,项目经理
List<CCObject> list3 = cs.cqlQuery("ryjx","select * from ryjx where nd='"+year+"' and yf ='"+month+"' and is_deleted = '0' and recordtype='2018ED6B4DF92033DeWs' and bkhr='0052018983C9E3DFdNbv'");
for(CCObject item3:list3){
	//团队创佣目标 tdcymb 及 团队创佣完成值 tdcywcz
    //double tdcymb = item3.get("tdcymb")==null?0.0:Double.valueOf(item3.get("tdcymb")+"");//创佣目标
    double grcymb = item3.get("grcymb")==null?0.0:Double.valueOf(item3.get("grcymb")+"");//grcymb 个人当月创佣目标
    double tdcywcz = item3.get("tdcywcz")==null?0.0:Double.valueOf(item3.get("tdcywcz")+"");//当月创佣完成值
    double cy_d = 0.0 ;
    if(item3.get("grcymb")==null){
        cy_d = 0 ;
    } else{
        if(grcymb==0){
            cy_d = 0 ;
        } else if (tdcywcz>=grcymb){
            cy_d=100;
        }else{
            cy_d = tdcywcz/grcymb *100 ;
        }
    }
    //不再需要计算业绩分在月度考核中
	// if(cy_d<=1){
	// 	cy_d = cy_d * 100 *0.4;
	// } else if(cy_d>1&&cy_d<1.2){
	// 	cy_d = cy_d * 100 *0.4;
	// } else {
	// 	cy_d = 120 *0.4;
    // }
    item3.put("yjdf",cy_d); //加入业绩得分

	//团队回款
	// double tdhkmb = item3.get("tdhkmb")==null?0.0:Double.valueOf(item3.get("tdhkmb")+"");//团队回款目标
    // double tdhkwcz = item3.get("tdhkwcz")==null?0.0:Double.valueOf(item3.get("tdhkwcz")+"");//团队回款完成值
	// double hk_d = tdhkwcz/tdhkmb ;
	// if(hk_d<1){
	// 	hk_d = hk_d * 100 *0.1;
	// } else if(hk_d>1&&hk_d<1.2){
	// 	hk_d = hk_d * 120 *0.1;
	// } else {
	// 	hk_d = 120 *0.1;
	// }

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

	acgf_d = acgf_d* 0.30; //占比由2.5成改成3成

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
    // if ("0052018283AFC55mqWeb".equals(item3.get("bkhr"))) {
    //     if(true){  // 打印日志
    //         trigger.addErrorMessage(ylhjls+"^"+dyycqts+"^");
    //     }
    // }
	//客户满意度
	double khmyd = item3.get("khmyd")==null?0.0:Double.valueOf(item3.get("khmyd")+""); //客户满意度 khmyd

	double crm_d = (rz+scsj+zwlh+khmyd)*0.7; //由0.25改为0.7

    //double khfz = cy_d + hk_d + acgf_d + crm_d;
    double khfz =acgf_d + crm_d; //不再计算创佣和回款

	//CCObject obj = new CCObject("ryjx");
	//obj.put("id",id);
	item3.put("rz",rz);
	item3.put("zwlh",zwlh);
	item3.put("khfz",khfz);
	cs.updateLt(item3);
}