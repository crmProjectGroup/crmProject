CCService cs = new CCService((UserInfo)userInfo);

List<CCObject> list = cs.cqlQuery("attendance","select id,ownerid from attendance where is_deleted = '0' and  TO_DAYS(createdate) = TO_DAYS(NOW())");

for(CCObject item:list){
	String id = item.get("id")==null?"":item.get("id")+"";//id
	String ownerid = item.get("ownerid")==null?"":item.get("ownerid")+"";//所有人id
	List<CCObject> salegrouplist = cs.cqlQuery("ProjectSaleGroup","select xmmc from ProjectSaleGroup where is_deleted = '0' and xmxsy = '"+ownerid+"'");
	String xmmc = "";
	if(salegrouplist.size()>0){
		for(CCObject saleobj:salegrouplist){
			xmmc = saleobj.get("xmmc")==null?"":saleobj.get("xmmc")+"";//项目名称
		}
	} 

        if(salegrouplist.size()==0){
		List<CCObject> projectlist = cs.cqlQuery("Project","select id from Project where is_deleted = '0' and xmjl = '"+ownerid+"' and xmzt = '代理中'");
		for(CCObject project:projectlist){
			xmmc = project.get("id")==null?"":project.get("id")+"";//项目id
		}
	}
	List<CCObject> userlist = cs.cqlQuery("ccuser","select gh from ccuser where isusing = '1' and id = '"+ownerid+"'");
	String gh = "";
	for(CCObject userobj:userlist){
		gh = userobj.get("gh")==null?"":userobj.get("gh")+"";//工号
	}
        cs.cqlQuery("attendance","update attendance set xmmc = '"+xmmc+"',gh = '"+gh+"' where id = '"+id+"'");
}
