/*
description:每日自动计算业务员项目经理考核分值
version: 1.1
date:20181115
author:tom
log:
20190529 出现java.lang.NumberFormatException: For input string: "Infinity" 的错误, 判断因为部分项目经理调整,造成rs和xmjlrl作为被除数为0,这里调整处理,当rs为0时候, 直接continue跳出循环
*/

CCService cs = new CCService((UserInfo)userInfo);
java.util.Calendar cal = java.util.Calendar.getInstance();
cal.setTime(new Date());
int year = cal.get(Calendar.YEAR);//当前年份
int month = cal.get(Calendar.MONTH)+1;//当前月份

//抽取人员绩效中的相关栏位,tdwj团队稳健,xmjlrl项目经理人力,bkhr被考核人,xmmc项目名称,tddyyjmb团队当月业绩目标,tddyyjwcs团队当月业绩完成数,tdjs团队建设,xmdyldsmps项目当月联动上门批数,grdywcyjs个人当月完成业绩数,记录id,recordtype记录类型,grdyyjmb个人当月业绩目标
List<CCObject> list = cs.cqlQuery("ryjx","select tdwj,xmjlrl,bkhr,xmmc,tddyyjmb,round(tddyyjwcs,2) as tddyyjwcs,ifnull(tdjs,0) as tdjsfz,round(xmdyldsmps) as xmdyldsmps,round(grdywcyjs,2) as grdywcyjs,id,recordtype,grdyyjmb from ryjx where nd='"+year+"' and yf ='"+month+"' and is_deleted = '0'");
if(list.size()>0){
	for(CCObject item:list){
		String id = item.get("id")==null?"":item.get("id")+"";//id
		String recordtype = item.get("recordtype")==null?"":item.get("recordtype")+"";//记录类型
		double grdyyjmb = item.get("grdyyjmb")==null?0.0:Double.valueOf(item.get("grdyyjmb")+"");//个人当月业绩目标
		double grdywcyjs = item.get("grdywcyjs")==null?0.0:Double.valueOf(item.get("grdywcyjs")+"");//个人当月业绩完成数
		Integer xmdyldsmps = 0; //项目当月联动上门批数
		//try获取项目当月联动上门批次
		try{
			xmdyldsmps = item.get("xmdyldsmps")==null?0:Integer.valueOf(item.get("xmdyldsmps")+"");
		}catch(NumberFormatException e) {
			String ldsmps = item.get("xmdyldsmps")==null?"0.0":item.get("xmdyldsmps")+"";
			if(ldsmps.trim().indexOf('.')>0){
				ldsmps = ldsmps.substring(0,ldsmps.indexOf('.'));
			}
			xmdyldsmps = Integer.valueOf(ldsmps);
		}
		double tdjsfz = item.get("tdjsfz")==null?0.0:Double.valueOf(item.get("tdjsfz")+"");//团队建设

		double tddyyjwcs = item.get("tddyyjwcs")==null?0.0:Double.valueOf(item.get("tddyyjwcs")+"");//团队业绩完成数
		double tddyyjmb = item.get("tddyyjmb")==null?0.0:Double.valueOf(item.get("tddyyjmb")+"");//团队业绩目标

		int rjpc = 0;//人均批次
		double tdwjfz = 0.0;//团队稳健分值
		double khfz = 0.0;//考核分值
		double yjfz = 0.0;//业绩分值
		double ldfz = 0.0;//转介联动分值
		double khwh = 0.0;//客户满意度

		String xmmc = item.get("xmmc")==null?"":item.get("xmmc")+"";//项目名称id
		String bkhr = item.get("bkhr")==null?"":item.get("bkhr")+"";//被考核人
		if("2018ED6B4DF92033DeWs".equals(recordtype)){//项目经理
			Integer rs = cs.cqlQuery("ProjectSaleGroup","select id from ProjectSaleGroup where is_deleted = '0' and xmmc ='"+xmmc+"' and xmjl = '"+bkhr+"'").size();//获取项目经理下对应的项目销售小组的人数
            //Double  xmjlrl = item.get("xmjlrl")==null?0.0:Double.parseDouble(item.get("xmjlrl")+""); //项目经理人力, 目前为0.!!!这里最重要的是处理月中进来的员工!!!
            if (rs==0){
                continue;
            }
			//计算人均批次,并去掉小数部分
            //String txt = (xmdyldsmps/(xmjlrl+rs))+""; 
            String txt = (xmdyldsmps/rs)+""; 
			if(txt.trim().indexOf('.')>0){
				txt = txt.substring(0,txt.indexOf('.'));
			}
			if(!"".equals(txt)){
				rjpc = Integer.parseInt(txt.trim());
			}
			
			//因为只保留了整数部分,大于0即表示人均1次完成
			if(rjpc>0){
				ldfz = 20 + (xmdyldsmps-rs)*15; //人均1次完成+20, 其后每多一次+15. 20181115改
            }
            
            if(xmdyldsmps==0){
				ldfz =-10;

			}

			tdwjfz = item.get("tdwj")==null?0.0:item.get("tdwj")==""?0.0:Double.valueOf(item.get("tdwj")+"");//获取团队稳健分数

		}else{//业务员
			ldfz = xmdyldsmps*15;//联动每完成一次+15,20181115修改
			//获取客户评价分值
			List<CCObject> khwhlist = cs.cqlQuery("Account","select sum(ifnull(khmyd,0)) as khwh from Account where is_deleted = '0' and xmmc = '"+xmmc+"' and createbyid = '"+bkhr+"' and YEAR(createdate) = '"+year+"' and MONTH(createdate) = '"+month+"'");
			for(CCObject khwhitem:khwhlist){
				khwh = khwhitem.get("khwh")==""?0.0:khwhitem.get("khwh")==null?0.0:Double.valueOf(khwhitem.get("khwh")+"");//客户维护
			}
			//控制上限值20
			cs.cqlQuery("ryjx","update ryjx set khwh = ifnull("+khwh+",0) where bkhr ='"+bkhr+"' and xmmc = '"+xmmc+"' and is_deleted = '0' and nd = '"+year+"' and yf = '"+month+"'");
			if(khwh>20) khwh=20;
		}
		
		//业绩分值计算
		if("201884204B9C199odbgA".equals(recordtype)){//记录类型为业务员

			if(grdywcyjs>grdyyjmb && grdyyjmb>0){
				yjfz = 80;
			}else if(grdywcyjs<grdyyjmb && grdywcyjs>0 && grdyyjmb>0){
				yjfz = (grdywcyjs/grdyyjmb)*80;
			}
		}else if("2018ED6B4DF92033DeWs".equals(recordtype)){//记录类型为项目经理
			if(tddyyjwcs>tddyyjmb && tddyyjmb>0){
				yjfz = 80;
			}else if(tddyyjwcs<tddyyjmb && tddyyjwcs>0 && tddyyjmb>0){
				yjfz = (tddyyjwcs/tddyyjmb)*80;
			}
		}
		//累加各积分项,yjfz业绩分值,ldfz联动分值,tdjsfz团队建设分值,khwh客户维护分,tdwjfz团队稳健分值 
		khfz=yjfz+ldfz+tdjsfz+khwh+tdwjfz;
		if(khfz>100){
			khfz = 100;
		}
		//将结果写入人员绩效表
		CCObject obj = new CCObject("ryjx");
		obj.put("id",id);
		obj.put("khfz",khfz);
		cs.updateLt(obj);
	}
}