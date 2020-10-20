/**
*描述：历史数据从新计算 预计创收  公寓类型
* 
*/
double xqmj_d = 0.0;
List<CCObject> accountlist = this.cqlQuery("account","select * from account where recordtype = '2020F8FFFACC18DmPXQ1' and is_deleted = '0'");
for (CCObject acc:accountlist) {
    double cjjj = 0.0;
    String gongYuType = acc.get("recordtype")==null?"":acc.get("recordtype")+"";//获取客户类型(公寓类型)
    String id = acc.get("id")==null?"":acc.get("id")+"";//获取客户类型(公寓类型)
    if ("2020F8FFFACC18DmPXQ1".equals(gongYuType)) { // 如果是 "公寓类型" 则覆盖  成交均价 cjjj  和 需求面积 xqmj_d  并且 获取公寓的需求面积
        cjjj = 45000; // 公寓的成交均价 尚未定 目前按照 45000
        String xqmj_gy = acc.get("xqmj_gy")==null?"":acc.get("xqmj_gy")+""; //获取面积
        if("33平".equals(xqmj_gy)){
            xqmj_d = 33;
        } else if("39平".equals(xqmj_gy)){
            xqmj_d = 39;
        } else if("54平".equals(xqmj_gy)){
            xqmj_d = 54;
        } else if("67平".equals(xqmj_gy)){
            xqmj_d = 67;
        } else if("445平".equals(xqmj_gy)){
            xqmj_d = 445;
        } else {
            xqmj_d = 0;
        }
    }
    //获取费率
    double fl =0.0; //最终需要获取到费率和拆分渠道费率两个字段
    double cfqdfl =0.0;
    List<CCObject> jycllist=this.cqlQuery("jycl","select fl,cfqdfl,qddj from jycl where xmmc = 'a0520206562E5ECNVIbC' and is_deleted ='0' and qd_zr='自然'");
    fl = jycllist.get(0).get("fl")==null?0.0:Double.parseDouble(jycllist.get(0).get("fl")+"");
    Double yjcs = 0.0;
    yjcs = xqmj_d * cjjj * fl /100 ;
    CCObject obj1 = new CCObject("Account");
    obj1.put("id",id);
    obj1.put("yjcs",yjcs);
    this.updateLt(obj1);
}