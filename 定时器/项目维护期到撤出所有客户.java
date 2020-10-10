CCService cs = new CCService((UserInfo)userInfo);

List<CCObject> list = cs.cqlQuery("Project","select * from Project where is_deleted = '0' and xmyxrq < CURDATE() and xmzt ='代理中'");
for(CCObject cobj:list){
     cobj.put("xmzt","已结束");
     cs.updateLt(cobj);
     String id = cobj.get("id")==null?"":cobj.get("id")+"";//项目id
     List<CCObject> accountlist = cs.cqlQuery("Account","select * from Account where is_deleted = '0' and xmmc = '"+id+"'");
     for(CCObject obj:accountlist){
          obj.put("ownerid","00520189113408D8wGET");
          cs.updateLt(obj);
     }
}