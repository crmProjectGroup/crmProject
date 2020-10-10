/**
*
*更新合同上的累计回款金额及未回款额
*更新客户上的累计回款金额
*/
//String sjsksj = record_new.get("sjsksj")==null?"":String.valueOf(record_new.get("sjsksj"));//实际收款日期
double dqysje = record_new.get("dqysje")==null||"".equals(record_new.get("dqysje")+"")?0.0:Double.valueOf(record_new.get("dqysje")+"");//到期应收金额
double sjskje = record_new.get("sjskje")==null||"".equals(record_new.get("sjskje")+"")?0.0:Double.valueOf(record_new.get("sjskje")+"");//实际收款金额
double bqysye = dqysje - sjskje;//到期应收余额
//String sksfwc = record_new.get("sksfwc")==null?"":String.valueOf(record_new.get("sksfwc"));//收款是否完成
String khmc = record_new.get("khmc")==null?"":String.valueOf(record_new.get("khmc"));//客户名称
String htmc = record_new.get("htbh")==null?"":String.valueOf(record_new.get("htbh"));//合同名称
if(record_old==null){//新建时
	List<CCObject> htList = cquery("contract","id = '"+htmc+"'");
	if(htList.size()>0){
		double ljskje = htList.get(0).get("ljskje")==null||"".equals(htList.get(0).get("ljskje")+"")?0.0:Double.valueOf(htList.get(0).get("ljskje")+"");//合同累计回款金额
		double htje = htList.get(0).get("htje")==null||"".equals(htList.get(0).get("htje")+"")?0.0:Double.valueOf(htList.get(0).get("htje")+"");//合同金额
		ljskje += sjskje;
		double yskje = htje - ljskje;//应收款金额
		htList.get(0).put("ljskje",ljskje+"");
		htList.get(0).put("yskje",yskje+"");
		update(htList);
	}
	List<CCObject> khList = cquery("Account","id = '"+khmc+"'");
	if(khList.size()>0){
		double ljhkje = khList.get(0).get("ljhkje")==null||"".equals(khList.get(0).get("ljhkje")+"")?0.0:Double.valueOf(khList.get(0).get("ljhkje")+"");//客户累计回款金额
		ljhkje += sjskje;
		khList.get(0).put("ljhkje",ljhkje + "");
		update(khList);
	}
}else{//编辑时
	double sjskje_old = record_old.get("sjskje")==null||"".equals(record_old.get("sjskje")+"")?0.0:Double.valueOf(record_old.get("sjskje")+"");//实际收款金额
	//if(true){trigger.addErrorMessage(sjskje_old+"-"+sjskje);}
	if(sjskje!=sjskje_old){//收款金额修改
		List<CCObject> htList = cquery("contract","id = '"+htmc+"'");
		if(htList.size()>0){
			double ljskje = htList.get(0).get("ljskje")==null||"".equals(htList.get(0).get("ljskje")+"")?0.0:Double.valueOf(htList.get(0).get("ljskje")+"");//合同累计回款金额
			double htje = htList.get(0).get("htje")==null||"".equals(htList.get(0).get("htje")+"")?0.0:Double.valueOf(htList.get(0).get("htje")+"");//合同金额
			ljskje += sjskje - sjskje_old;
			double yskje = htje - ljskje;//应收款金额
			htList.get(0).put("ljskje",ljskje+"");
			htList.get(0).put("yskje",yskje+"");
			update(htList);
		}
		List<CCObject> khList = cquery("Account","id = '"+khmc+"'");
		if(khList.size()>0){
			double ljhkje = khList.get(0).get("ljhkje")==null||"".equals(khList.get(0).get("ljhkje")+"")?0.0:Double.valueOf(khList.get(0).get("ljhkje")+"");//客户累计回款金额
			ljhkje += sjskje - sjskje_old;
			khList.get(0).put("ljhkje",ljhkje + "");
			update(khList);
		}
	}
}
record_new.put("bqysye",bqysye+"");