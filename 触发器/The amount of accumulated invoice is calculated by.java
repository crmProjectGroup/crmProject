/*************************国际化代码**************************/
String jsonStr = "{\"审批通过前请填写开票日期及发票号码信息！\":{\"zh\":\"审批通过前请填写开票日期及发票号码信息！\",\"en\":\"Please fill in the invoice date and invoice number before  submitting for approval !\"}}";

net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(jsonStr );
//查询当前用户语言
String Uid = userInfo.getUserId()+"";
String languageLocaleKey = userInfo.getLanguage();

//new 语言转换对象
LangSw langsw = new LangSw(languageLocaleKey,jsonObject);	
/****************************************************************/


String spzt = record_new.get("spzt")==null?"":String.valueOf(record_new.get("spzt"));//审批状态
String spzt_old = record_old.get("spzt")==null?"":String.valueOf(record_old.get("spzt"));//审批状态old
String htmc = record_new.get("htmc")==null?"":String.valueOf(record_new.get("htmc"));//合同名称
String khmc = record_new.get("khmc")==null?"":String.valueOf(record_new.get("khmc"));//客户名称
String kprq = record_new.get("kprq")==null?"":String.valueOf(record_new.get("kprq"));//开票日期
String fphm = record_new.get("fphm")==null?"":String.valueOf(record_new.get("fphm"));//发票号码
if("审批通过".equals(spzt)&&!"审批通过".equals(spzt_old)){
	if("".equals(kprq)||"".equals(fphm)){
		trigger.addErrorMessage(langsw.switchLang("审批通过前请填写开票日期及发票号码信息！"));
	}
	double fpje = record_new.get("fpje")==null?0.0:Double.valueOf(record_new.get("fpje")+"");//发票金额
	//累加开票金额到合同
	List<CCObject> htList = cquery("contract","id = '"+htmc+"'");
	if(htList.size()>0){
		double ljkpje = htList.get(0).get("ljkpje")==null?0.0:Double.valueOf(htList.get(0).get("ljkpje")+"");//累计开票金额
		double htje = htList.get(0).get("htje")==null?0.0:Double.valueOf(htList.get(0).get("htje")+"");//合同金额
		ljkpje += fpje;
		double wkpje = htje - ljkpje;//累计未开票金额
		htList.get(0).put("ljkpje",""+ljkpje);
		htList.get(0).put("wkpje",""+wkpje);
		update(htList);
	}
	//累加开票金额到客户
	List<CCObject> khList = cquery("Account","id = '"+khmc+"'");
	if(khList.size()>0){
		double ljkpje = khList.get(0).get("ljkpje")==null?0.0:Double.valueOf(khList.get(0).get("ljkpje")+"");//累计开票金额
		ljkpje += fpje;
		khList.get(0).put("ljkpje",""+ljkpje);
		update(khList);
	}
}
