/**
*描述：历史数据从新计算 预计创收  公寓类型
1.讨论付款方式:是; 
2.客户投资经历  除住宅 和 暂未了解外,有投资过的就是A; 
3.锁定房号;
4.购买因素:投资,自用,都有;
5.认可点 增加 暂未了解 ;

满足3个以上 A
满足 1-2个 B
满足 0 个 C
* 
*/

List<CCObject> accountlist = this.cqlQuery("account","select * from account where recordtype = '2020F8FFFACC18DmPXQ1' and is_deleted = '0'");
// List<CCObject> accountlist = this.cqlQuery("account","select * from account where recordtype = '2020F8FFFACC18DmPXQ1' and is_deleted = '0' and id = '001202011714869NKHyQ'");
for (CCObject acc:accountlist) {
    double gydjf = 0.0; // 公寓定级分数
    String khdj = "";
    String id = acc.get("id")==null?"":acc.get("id")+"";
    //begin 考核点
    String sptlfkfs = acc.get("sptlfkfs")==null?"":acc.get("sptlfkfs")+"";//是否讨论付款方式
    String khtzjl = acc.get("khtzjl")==null?"":acc.get("khtzjl")+"";//客户投资经历 (排除  住宅 和 暂未了解)
    String spmqsdfh = acc.get("spmqsdfh")==null?"":acc.get("spmqsdfh")+""; // 是否明确锁定房号
    String sdfh = acc.get("sdfh")==null?"":acc.get("sdfh")+""; // 锁定房号
    String gmys_gy = acc.get("gmys_gy")==null?"":acc.get("gmys_gy")+""; // 购买因素(公寓)
    String zlrkd = acc.get("zlrkd")==null?"":acc.get("zlrkd")+""; // 认可点
    //end 考核点
    if ("是".equals(sptlfkfs)) {  // 是否讨论付款方式
        gydjf += 1.0;
    }
    
    if ((!"住宅;".equals(khtzjl)) && (!"住宅".equals(khtzjl)) && (!"暂未了解;".equals(khtzjl)) && (!"暂未了解".equals(khtzjl)) && (!"".equals(khtzjl))) { // 客户投资经历
        gydjf += 1.0;
    }
    // if(true){
	//     trigger.addErrorMessage("^"+String.valueOf(khtzjl)+"^"+String.valueOf(gydjf));
    // }
    if (("是".equals(spmqsdfh)) && (!"".equals(sdfh))) { // 是否明确锁定房号 和 锁定房号
        gydjf += 1.0;
    }
    
    if ((!"".equals(gmys_gy)) && (!"暂未了解;".equals(gmys_gy)) && (!"暂未了解".equals(gmys_gy))) { // 购买因素
        gydjf += 1.0;
    }
    
    if ((!"".equals(zlrkd)) && (!"暂未了解;".equals(zlrkd)) && (!"暂未了解".equals(zlrkd))) { //认可点
        gydjf += 1.0;
    }
    // if(true){
	//     trigger.addErrorMessage("^"+String.valueOf(zlrkd)+"^"+String.valueOf(gydjf));
    // }
    if (gydjf >= 3) {
        khdj = "A";
    } else if (0 < gydjf && gydjf < 3) {
        khdj = "B";
    } else {
        khdj = "C";
    } 
    // if(true){
	//     trigger.addErrorMessage(String.valueOf(khdj)
    //                             +"^"+String.valueOf(gydjf)
    //                             +"^"+String.valueOf(sptlfkfs)
    //                             +"^"+String.valueOf(khtzjl)
    //                             +"^"+String.valueOf(sdfh)
    //                             +"^"+String.valueOf(gmys_gy)
    //                             +"^"+String.valueOf(zlrkd)
    //                             );
	// }
    CCObject obj1 = new CCObject("Account");
    obj1.put("id",id);
    obj1.put("khdj",khdj);
    this.updateLt(obj1);
}