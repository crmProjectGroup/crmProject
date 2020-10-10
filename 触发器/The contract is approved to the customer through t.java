String khmc = record_new.get("khmc")==null?"":String.valueOf(record_new.get("khmc"));//客户名称
double htje = record_new.get("htje")==null||"".equals(record_new.get("htje")+"")?0.0:Double.valueOf(record_new.get("htje")+"");//合同金额
String spzt = record_new.get("zhuangtai")==null?"":String.valueOf(record_new.get("zhuangtai"));//审批状态
String spzt_old = record_old.get("zhuangtai")==null?"":String.valueOf(record_old.get("zhuangtai"));//审批状态
if(!"审批通过".equals(spzt_old)&&"审批通过".equals(spzt)){
	List<CCObject> khList = cquery("Account","id = '"+khmc+"'");
	if(khList.size()>0){
		double ljhtje = khList.get(0).get("ljhtje")==null?0.0:Double.valueOf(khList.get(0).get("ljhtje")+"");//累计合同金额
		ljhtje += htje;
		khList.get(0).put("ljhtje",""+ljhtje);
		update(khList);
	}
}