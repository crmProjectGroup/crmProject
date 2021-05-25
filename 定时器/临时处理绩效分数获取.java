// 临时处理绩效分数获取
CCService cs = new CCService((UserInfo)userInfo);
//日期的处理
Calendar cal = Calendar.getInstance();
int year = 2021;
int month = 4;
java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd");
String nowday = "2021-04-30";
String begin_day=year+"-"+month+"-01";

List<CCObject> chuang = this.cquery("ryjx","nd = '"+year+"' and yf = '"+month+"' and is_deleted='0'");
for (CCObject c:chuang) {
    c.put("grcywcz",0);
    c.put("tdcywcz",0);
    this.update(c);
}

// 获取每月的创佣完成情况 20201027 a 换位置,先将完成置0,再重新获取
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
                List<CCObject> ryjxlist1 = this.cqlQuery("ryjx","select id,ifnull(tdcywcz,0) as tdcywcz from ryjx where spzt = '审批通过' and nd = '"+year+"' and yf = '"+month+"' and xmmc = '"+xm1+"' and is_deleted = '0' and bkhr=(select manager from ccuser where id='"+fcyh1+"' and is_deleted = '0')");
                if(ryjxlist1.size()==1){
                    double tdcywcz = ryjxlist1.get(0).get("tdcywcz")==null?0.0:Double.valueOf(ryjxlist1.get(0).get("tdcywcz")+"");
                    ryjxlist1.get(0).put("tdcywcz",tdcywcz+(sjsy*fcbl1)/100);
                    this.update(ryjxlist1.get(0));
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
                List<CCObject> ryjxlist1 = this.cqlQuery("ryjx","select id,ifnull(tdcywcz,0) as tdcywcz from ryjx where spzt = '审批通过' and nd = '"+year+"' and yf = '"+month+"' and xmmc = '"+xm2+"' and is_deleted = '0' and bkhr=(select manager from ccuser where id='"+fcyh2+"' and is_deleted = '0')");
                if(ryjxlist1.size()==1){
                    double tdcywcz = ryjxlist1.get(0).get("tdcywcz")==null?0.0:Double.valueOf(ryjxlist1.get(0).get("tdcywcz")+"");
                    ryjxlist1.get(0).put("tdcywcz",tdcywcz+(sjsy*fcbl2)/100);
                    this.update(ryjxlist1.get(0));
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
                List<CCObject> ryjxlist1 = this.cqlQuery("ryjx","select id,ifnull(tdcywcz,0) as tdcywcz from ryjx where spzt = '审批通过' and nd = '"+year+"' and yf = '"+month+"' and xmmc = '"+xm3+"' and is_deleted = '0' and bkhr=(select manager from ccuser where id='"+fcyh3+"' and is_deleted = '0')");
                if(ryjxlist1.size()==1){
                    double tdcywcz = ryjxlist1.get(0).get("tdcywcz")==null?0.0:Double.valueOf(ryjxlist1.get(0).get("tdcywcz")+"");
                    ryjxlist1.get(0).put("tdcywcz",tdcywcz+(sjsy*fcbl3)/100);
                    this.update(ryjxlist1.get(0));
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
                List<CCObject> ryjxlist1 = this.cqlQuery("ryjx","select id,ifnull(tdcywcz,0) as tdcywcz from ryjx where spzt = '审批通过' and nd = '"+year+"' and yf = '"+month+"' and xmmc = '"+xm4+"' and is_deleted = '0' and bkhr=(select manager from ccuser where id='"+fcyh4+"' and is_deleted = '0')");
                if(ryjxlist1.size()==1){
                    double tdcywcz = ryjxlist1.get(0).get("tdcywcz")==null?0.0:Double.valueOf(ryjxlist1.get(0).get("tdcywcz")+"");
                    ryjxlist1.get(0).put("tdcywcz",tdcywcz+(sjsy*fcbl4)/100);
                    this.update(ryjxlist1.get(0));
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
                List<CCObject> ryjxlist1 = this.cqlQuery("ryjx","select id,ifnull(tdcywcz,0) as tdcywcz from ryjx where spzt = '审批通过' and nd = '"+year+"' and yf = '"+month+"' and xmmc = '"+xm5+"' and is_deleted = '0' and bkhr=(select manager from ccuser where id='"+fcyh5+"' and is_deleted = '0')");
                if(ryjxlist1.size()==1){
                    double tdcywcz = ryjxlist1.get(0).get("tdcywcz")==null?0.0:Double.valueOf(ryjxlist1.get(0).get("tdcywcz")+"");
                    ryjxlist1.get(0).put("tdcywcz",tdcywcz+(sjsy*fcbl5)/100);
                    this.update(ryjxlist1.get(0));
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
        List<CCObject> ryjxlist1 = this.cqlQuery("ryjx","select id,ifnull(tdcywcz,0) as tdcywcz from ryjx where xmmc='"+xmmc+"' and spzt = '审批通过' and nd = '"+year+"' and yf = '"+month+"' and recordtype='2018ED6B4DF92033DeWs' and is_deleted = '0' and bkhr=(select manager from ccuser where id='"+ownerid+"' and is_deleted = '0')");
        if(ryjxlist1.size()==1){
              double tdcywcz = ryjxlist1.get(0).get("tdcywcz")==null?0.0:Double.valueOf(ryjxlist1.get(0).get("tdcywcz")+"");
              ryjxlist1.get(0).put("tdcywcz",tdcywcz+sjsy);
              this.update(ryjxlist1.get(0));
        } 
        // else if (ryjxlist1.size()==0) {
        //     // 如果是项目经理提交的成交，owerid 就是项目经理自己
        //     List<CCObject> prolist = this.cqlQuery("ccuser","select profile from ccuser where id='"+ownerid+"' and is_deleted = '0'");
        //     if (prolist.size()==1) { // 查到了经理
        //         String profile = prolist.get(0).get("profile")==null?"":prolist.get(0).get("profile")+"";
        //         if ("aaa2018A38306ED9syVe".equals(profile)) { // 如果是项目经理简档
        //             List<CCObject> ryjxlist1 = this.cqlQuery("ryjx","select id,ifnull(tdcywcz,0) as tdcywcz from ryjx where xmmc='"+xmmc+"' and spzt = '审批通过' and nd = '"+year+"' and yf = '"+month+"' and recordtype='2018ED6B4DF92033DeWs' and is_deleted = '0' and bkhr='"+ownerid+"'");
                    
        //         }
        //     }
        // }
    }
}

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
            cy_d = 0 ;
        } else{
            if (grcywcz>=grcymb) {
                cy_d=100;
            } else {
                cy_d = grcywcz/grcymb*100;
            }
        }
    }
    item1.put("yjdf",cy_d);
    cs.updateLt(item1);

}
//分类型计算,项目经理
List<CCObject> list3 = cs.cqlQuery("ryjx","select * from ryjx where nd='"+year+"' and yf ='"+month+"' and is_deleted = '0' and recordtype='2018ED6B4DF92033DeWs'");
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
        } else{
            if (tdcywcz>=grcymb) {
                cy_d=100;
            } else {
                cy_d = tdcywcz/grcymb *100 ;
            }
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
    cs.updateLt(item3);
    }