/*
description:自动根据预算为项目经理创建月度考核
version: 2.0
date:20190430
author:tom
log:
20190528 1.创建目标由理想值改为正常值;2.计算上月未完成面积,累计到当月目标;
20200827 改目标面积威创收金额,理想为团队得值,以理想值做团队的创佣目标,正常值作为项目经理个人的月创佣目标;
*/

//获取CCS
CCService ccs = new CCService((UserInfo)userInfo);
java.util.Calendar cal = java.util.Calendar.getInstance();
cal.setTime(new Date());
int year = cal.get(Calendar.YEAR);//当前年份
int month = cal.get(Calendar.MONTH)+2;//下月
if (month==13) {
 	month=1;
}
int day = cal.get(Calendar.DAY_OF_MONTH);//日期
String yearstr=year+"";
String monthstr=month+""; //下月
//String mbname= "ymbmj" + monthstr + "lx"; //ymbmj10lx;
//String mbname= "ymbmj" + monthstr; //ymbmj6
String mbname_td= "ycsmb" + monthstr +"lx"; // ycsmb10lx & ycsmb7zc
String mbname_gr= "ycsmb" + monthstr +"zc";
//if(true) trigger.addErrorMessage(yearstr+"/"+monthstr+"/"+mbname);

//直接遍历预算,根据预算内容去找对应项目的项目经理建绩效
List<CCObject> bmyslist = ccs.cquery("bmys","nd = '"+yearstr+"' and is_deleted = '0' ");
//if(true) trigger.addErrorMessage(String.valueOf(bmyslist.size()));
for(CCObject item:bmyslist){
	String id = item.get("id")==null?"":item.get("id")+"";//id
	String sql= "select a." + mbname_td + ",a."+mbname_gr+",b.xmjl,b.id from xmys a inner join Project b on a.xmmc = b.id where a.bm = '" + id + "' and a.is_deleted = '0' and b.xmzt='代理中'";  //"id in (" + dljsmx +")";
	//if(true) trigger.addErrorMessage(sql);
	List<CCObject> xmyslist = ccs.cqlQuery("xmys",sql); //二部预算id a4920190E9DDCFDf305F
	//if(true) trigger.addErrorMessage(String.valueOf(xmyslist.size()));
	for(CCObject xmysitem:xmyslist){
		//mbname =  ymbmj + month + "lx"; //ymbmj10lx
        double ycymb_td = xmysitem.get(mbname_td)==null?0.0:Double.valueOf(xmysitem.get(mbname_td)+"");
        double ycymb_gr = xmysitem.get(mbname_gr)==null?0.0:Double.valueOf(xmysitem.get(mbname_gr)+"");
		String xmmc = xmysitem.get("id")==null?"":xmysitem.get("id")+"";
		String bkhr = xmysitem.get("xmjl")==null?"":xmysitem.get("xmjl")+"";
		//if(true) trigger.addErrorMessage(ymb+"/"+xmmc+"/"+bkhr);
		if(ycymb_td>0){
			CCObject obj = new CCObject("ryjx");
			obj.put("xmmc",xmmc); //项目名称
			obj.put("bkhr",bkhr); //被考核人
            //obj.put("tddyyjmb",ymb); //tddyyjmb
            obj.put("grcymb",ycymb_gr); //tddyyjmb
            obj.put("tdcymb",ycymb_td); //tddyyjmb
			obj.put("nd",yearstr); //nd
			obj.put("yf",monthstr); //yf
			obj.put("ownerid",bkhr);//ownerid
			obj.put("spzt","审批通过");//spzt
			obj.put("recordtype","2018ED6B4DF92033DeWs"); //经理类型
			ccs.insertLt(obj);
		}
	}
}

