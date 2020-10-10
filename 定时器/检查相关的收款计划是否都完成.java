/**
*描述：循环所有代理明细表,状态为全部开票的,检查相关的收款计划是否都完成,如果全部完成,修改状态为回款完成,  同时遍历所有未开票和部分开票的,提醒项目经理未开票; 
频率为每周2次,周一早上和周4早上;
*
*创建人：何俊
*创建时间：2018/11/30
*最后修改时间：2018/11/30
*/

//获取CCS
CCService ccs = new CCService((UserInfo)userInfo);

//-----------------获取所有状态为全部开票的代理明细记录-------------------
List<CCObject> dlmxlist = ccs.cquery("dljsmxb","jyzt = '全部开票'");

//定义一个bool型来控制是否回款完成
boolean isdone;

//--------------------检查相关的收款计划是否都完成,如果全部完成,修改状态为回款完成-----------------
for(CCObject dlmxitem:dlmxlist){
	String dlmxid = dlmxitem.get("dlmxid")==null?"":dlmxitem.get("dlmxid")+"";//代理明细id
	List<CCObject> skjhlist = ccs.cquery("cloudccskjh","jsmx = '"+dlmxid+"'");//搜索对应的收款计划
	isdone=true;
	for(CCObject skjhitem:skjhlist){
		//获取收款是否完成sksfwc
		String sksfwc = skjhitem.get("sksfwc")==null?"":skjhitem.get("sksfwc")+"";
		if("false".equals(sksfwc)) {
			isdone=false;
		}
	}
	if(isdone){
		dlmxitem.put("jyzt","回款完成"); 
		ccs.updateLt(dlmxitem);
	}
}

//-----------------遍历所有未开票和部分开票的,提醒项目经理未开票-------------------
