//对象:开票申请
//累计应结算金额
//afterinser 新建累计所有选择的代理结算明细的应结算金额, 根据填写的比例,计算金额

CCService cs = new CCService(userInfo);
//获取选取的结算明细
String id = record_new.get("id")==null?"":record_new.get("id")+"";
String dljsmx = record_new.get("jsmx")==null?"":record_new.get("jsmx")+"";
if("".equals(dljsmx)){
	trigger.addErrorMessage("代理结算明细为空");
}
//sql选取所有选取对象
dljsmx="'"+dljsmx.replace(";","','")+"'";
String sql="id in (" + dljsmx +")";
//if(true) {trigger.addErrorMessage(sql);}
//本次结算比例
String bcjsbl = record_new.get("bcjsbl")==null?"":record_new.get("bcjsbl")+"";
List<CCObject> dljsmxlist = cs.cquery("dljsmxb",sql);

CCObject obj = new CCObject("kpsq");
obj.put("id",id);

double ljykpe=0.0; //累计应开票额
for(CCObject item:dljsmxlist){
	double kpje=item.get("dlfjsje")==null?0.0:Double.parseDouble(item.get("dlfjsje")+"");
	ljykpe=ljykpe+kpje;
}
double bcjsbld = Double.parseDouble(bcjsbl);
double fpje = ljykpe*bcjsbld/100;
obj.put("ljyjsje",ljykpe);
obj.put("fpje",fpje);
cs.updateLt(obj);

