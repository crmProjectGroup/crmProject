//定时类,月度获取项目经理手下所有本月无变动的业务员人力,本月加入的用创建日期过滤掉,删除的不在项目销售小组中
java.util.Calendar cal = java.util.Calendar.getInstance();
cal.setTime(new Date());
int year = cal.get(Calendar.YEAR);//当前年份
int month = cal.get(Calendar.MONTH)+1;//当前月份
String yearstr=year+"";
String monthstr=month+"";
CCService cs = new CCService(userInfo);

//抽取当前年月下所有项目经理的数据
String sql = "select * from ryjx where YEAR(createdate)='"+year+"' and MONTH(createdate)='"+month+"' and recordtype='2018ED6B4DF92033DeWs' and is_deleted=0";  
List<CCObject> list = cs.cqlQuery("ryjx",sql);
if(list.size()>0){
	for(CCObject obj:list){
	  String uid = obj.get("bkhr")==null?"":obj.get("bkhr")+""; //通过被考核人获取项目经理的ID
	  String id = obj.get("id")==null?"":obj.get("id")+"";
	  double xmjlrl = obj.get("xmjlrl")==null?0.0:Double.valueOf(obj.get("xmjlrl")+""); //获取之前已经计算的人力(调入和跳出产生的不足1人人力)
	  //通过项目经理的ID选取对应的销售小组中名下的业务员的数量(不包括这个月新加入的) 
	  List<CCObject> xmxsxz_list = cs.cquery("ProjectSaleGroup","xmjl='"+uid+"' and (YEAR(createdate)<>'"+year+"' or MONTH(createdate)='"+month+"')");  //获取项目销售小组中的人力
	  int xsynum = xmxsxz_list.size();  //只要数量
	  xmjlrl+=xsynum; //计算人力,加上之前变动人力
	  CCObject obj1 = new CCObject("ryjx"); //人员绩效表
		obj1.put("xmjlrl",xmjlrl);
		obj1.put("id",id);
		this.updateLt(obj1);
	}
}      
