/**
*删除时
*更新合同上的累计回款金额及未回款额
*更新客户上的累计回款金额
*/
double sjskje_old = record_old.get("sjskje")==null||"".equals(record_old.get("sjskje")+"")?0.0:Double.valueOf(record_old.get("sjskje")+"");//实际收款金额
String khmc = record_old.get("khmc")==null?"":String.valueOf(record_old.get("khmc"));//客户名称
String htmc = record_old.get("htbh")==null?"":String.valueOf(record_old.get("htbh"));//合同名称
List<CCObject> htList = cquery("contract","id = '"+htmc+"'");
if(htList.size()>0){
	double ljskje = htList.get(0).get("ljskje")==null||"".equals(htList.get(0).get("ljskje")+"")?0.0:Double.valueOf(htList.get(0).get("ljskje")+"");//合同累计回款金额
	double htje = htList.get(0).get("htje")==null||"".equals(htList.get(0).get("htje")+"")?0.0:Double.valueOf(htList.get(0).get("htje")+"");//合同金额
	ljskje = ljskje - sjskje_old;
	double yskje = htje - ljskje;//应收款金额
	htList.get(0).put("ljskje",ljskje+"");
	htList.get(0).put("yskje",yskje+"");
	update(htList);
}
List<CCObject> khList = cquery("Account","id = '"+khmc+"'");
if(khList.size()>0){
	double ljhkje = khList.get(0).get("ljhkje")==null||"".equals(khList.get(0).get("ljhkje")+"")?0.0:Double.valueOf(khList.get(0).get("ljhkje")+"");//客户累计回款金额
	ljhkje = ljhkje - sjskje_old;
	khList.get(0).put("ljhkje",ljhkje + "");
	update(khList);
}