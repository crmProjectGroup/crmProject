/*   aaa2018A4671C21Hu0yi  运营中心  aaa2018A38306ED9syVe   项目经理   aaa201723453E5EBNtzU   业务员    aaa201858C360ADNceRX   运营业务员  */



//获取CCS
CCService ccs = new CCService((UserInfo)userInfo);
java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd"); //日期 MM需大写
String date=sdf.format(new Date());
//-----------------获取今日工作日报id-------------------
List<CCObject> list = ccs.cquery("DailyReport","recordtype='2018DD9D1CC8460ZOydm' and to_days(createdate) = to_days(now())");

 //--------------------循环更新工作日报-----------------
for(CCObject item:list){
	String id = item.get("id")==null?"":item.get("id")+"";//id
        String ownerid = item.get("ownerid")==null?"":item.get("ownerid")+"";//所有人id
	List<CCObject> list1 = ccs.cquery("ProjectSaleGroup","xmxsy= '"+ownerid+"'");

		for(CCObject item1:list1){
			String xmmc = item1.get("xmmc")==null?"":item1.get("xmmc")+"";//项目名称
			List<CCObject> Projectlist = ccs.cqlQuery("Project","select id,name from Project where is_deleted = '0' and id = '"+xmmc+"'");
                        String xmid = "";
                        if(Projectlist.size()>0){
                             xmid = Projectlist.get(0).get("id")==null?"":Projectlist.get(0).get("id")+"";
                        }
                        ccs.cqlQuery("DailyReport","update DailyReport set xmmc='"+xmid+"' where id = '"+id+"'");
		}
	
							 
}