/**
*描述：自动创建工作日报
*      
*创建人：刘东
*创建时间：2018/06/10
*最后修改时间：2018/07/10
*/


/*   aaa2018A4671C21Hu0yi  运营中心  aaa2018A38306ED9syVe   项目经理   aaa201723453E5EBNtzU   业务员    aaa201858C360ADNceRX   运营业务员  */



//获取CCS
CCService ccs = new CCService((UserInfo)userInfo);
java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd"); //日期 MM需大写
String date=sdf.format(new Date());
//-----------------获取所有启用用户id-------------------
List<CCObject> list = ccs.cquery("ccuser","isusing = '1' and profile in ('aaa201723453E5EBNtzU','aaa2018A38306ED9syVe','aaa2018A4671C21Hu0yi','aaa201858C360ADNceRX')");

 //--------------------循环添加工作日报-----------------
for(CCObject item:list){
	String id = item.get("id")==null?"":item.get("id")+"";//id
	String name = item.get("name")==null?"":item.get("name")+"";//姓名
	String profile = item.get("profile")==null?"":item.get("profile").toString();//简档
	CCObject obj = new CCObject("DailyReport");
	if("aaa2018A38306ED9syVe".equals(profile) || "aaa2018A4671C21Hu0yi".equals(profile)){              
  	obj.put("name",name+date);
  	obj.put("ownerid",id);
  	obj.put("createbyid",id);
  	obj.put("lastmodifybyid",id);
        obj.put("xkhtj","0");
        obj.put("jxtj","0");
        obj.put("jrld","0");
	obj.put("recordtype","20183BC7190A67ELt92g");
  	ccs.insertLt(obj);
  }else{
  	obj.put("name",name+date);
  	obj.put("ownerid",id);
  	obj.put("createbyid",id);
        obj.put("xkhtj","0");
        obj.put("jxtj","0");
        obj.put("jrld","0");
  	obj.put("lastmodifybyid",id);
	obj.put("recordtype","2018DD9D1CC8460ZOydm");
	ccs.insertLt(obj);	 
}							 
}