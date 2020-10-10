/*
description:客户评价结果获取
version: 1.0
date:20200605
author:tom
log:
*/

//获取CCS
CCService ccs = new CCService((UserInfo)userInfo);
// java.util.Calendar cal = java.util.Calendar.getInstance();
// cal.setTime(new Date());
// int year = cal.get(Calendar.YEAR);//当前年份
// int month = cal.get(Calendar.MONTH)+2;//下月
// if (month==13) {
//  	month=1;
// }
// int day = cal.get(Calendar.DAY_OF_MONTH);//日期
// String yearstr=year+"";
// String monthstr=month+""; //下月
// String mbname= "ymbmj" + monthstr; //ymbmj6 

//直接遍历所有(当天)的客户评价
List<CCObject> cusreviewslist = ccs.cquery("cusreviews","is_deleted = '0' and to_days(createdate) = to_days(now())");
//if(true) trigger.addErrorMessage(String.valueOf(cusreviewslist.size()));
for(CCObject item:cusreviewslist){
    String phone = item.get("phone")==null?"":item.get("phone")+"";//客户手机号码
    String xmnm = item.get("xmnm")==null?"":item.get("xmnm")+"";//项目名称xmnm
    String ywynm = item.get("ywy")==null?"":item.get("ywy")+"";//ywy 业务员姓名
    String ywy_no = item.get("ywy_no")==null?"":item.get("ywy_no")+"";//ywy_no 业务员工号
    String rvid = item.get("id")==null?"":item.get("id")+"";
    // if(true) {
    //     trigger.addErrorMessage(rvid);
    // } 
	//根据业务员取获取对应的业务员id
	String sql= "select id from ccuser where name='"+ywynm+"' and is_deleted ='0' and isusing='1'";  
	//if(true) trigger.addErrorMessage(sql);
	List<CCObject> ywylist = ccs.cqlQuery("ccuser",sql); 
	// if(true) trigger.addErrorMessage(ywynm+String.valueOf(ywylist.size()));
	for(CCObject ywyitem:ywylist){ 
		String ywyid = ywyitem.get("id")==null?"":ywyitem.get("id")+"";
        //查询对应业务员创建的,手机号码能匹配的客户联系人,获取对应客户的id
        //select * from contact where createbyid = '0052019BBC3F9A4s63Lo' and khmc is not null and is_deleted='0' and shouji = '18927442686'
        //select * from contact where khmc is not null and createbeyid = '0052019BBC3F9A4s63Lo' and is_deleted='0' and shouji='18927442686'
        String sql1= "select * from contact where createbyid = '"+ywyid+"' and khmc is not null and is_deleted='0' and shouji = '"+phone+"'";  
        List<CCObject> contactlist = ccs.cqlQuery("contact",sql1); 
        //if(true) trigger.addErrorMessage(ywyid+String.valueOf(contactlist.size()));
        for(CCObject contactitem:contactlist){
            String review = item.get("review")==null?"":item.get("review")+"";  // a=满意}
            int si = review.lastIndexOf("a=") + 2;
            int ei = review.lastIndexOf("}");
            // if(true) {
            //     trigger.addErrorMessage(String.valueOf(si) + ";" + String.valueOf(ei));
            // } 
            //String review_sub = review.substring(review.lastIndexOf("a=") + 2，review.lastIndexOf("\\}"));
            String review_sub = review.substring(si,ei);
            // if(true) {trigger.addErrorMessage(review_sub);}
            String khid = contactitem.get("khmc")==null?"":contactitem.get("khmc")+"";
            //if(true) {trigger.addErrorMessage(khid);}
            //答案示例 "a=满意} ]]"
            CCObject obj = new CCObject("Account");
            obj.put("id",khid); //客户id
            if ("满意".equals(review_sub)) {
                obj.put("khmyd",5); //客户满意
            } else if("一般".equals(review_sub)){
                obj.put("khmyd",3); //客户一般
            } else if("不满意".equals(review_sub)){
                obj.put("khmyd",-5); //客户不满意
            } else {
                obj.put("khmyd",0); 
            }
			ccs.updateLt(obj);
        }
	}
}

