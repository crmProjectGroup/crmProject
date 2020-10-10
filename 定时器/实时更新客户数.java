// 新增 客户上门数
// 只统计 当月份 每天递增 求和
//获取CS
CCService cs=new CCService(userInfo); // 获取操作类
java.util.Calendar cal = java.util.Calendar.getInstance(); // 获取时间对象
int nd = cal.get(Calendar.YEAR);//当前年份
int yf = cal.get(Calendar.MONTH)+1;//当前月份
java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd");  // 日期格式
String nowday = df.format(new Date()); // 获取当天的日期 如:("2020-09-30") 注意:需要 转换 格式
int  xmshangmen = 0;  // 汇总当月的进门人数 (按 每天 递增)
// 查询 所有的  被考核人   和   人员绩效 类型 (过滤月份)
List<CCObject> ryjxList = cs.cqlQuery("ryjx","select r.id,r.bkhr,r.recordtype,r.xmmc,r.khs from ryjx r where r.nd = '"+nd+"' and r.yf = '"+yf+"' and r.is_deleted = '0' ");
// if(true){
//         trigger.addErrorMessage(String.valueOf(ryjxList));
//     }

for(CCObject ry:ryjxList) { // 拿到过滤后的数据

     String bkhrId = ry.get("bkhr")==null?"":ry.get("bkhr")+""; // 获取被考核 人的id
     String ryType = ry.get("recordtype")==null?"":ry.get("recordtype")+""; // 获取人员的角色（项目经理 /  业务员）
     String xmmc = ry.get("xmmc")==null?"":ry.get("xmmc")+""; // 获取项目名称
     int khs = ry.get("khs")==null?0:Integer.parseInt(ry.get("khs")+""); // 获取当月 统计 客户数

    //  if(true){
    //     trigger.addErrorMessage(String.valueOf(bkhrId));
    // }
    
    // 如果是 “项目经理” 时 
    if("2018ED6B4DF92033DeWs".equals(ryType)) { 
        // 目的: 取 每个项目经理的  每个项目  的 总 客户上门数
        // 项目经理汇总上门数 (获取 account a 和 project p  根据 客户表的 xmmc（项目名称） =  项目表的 xmmc (xmmc号)  where 项目表的 xmjl (项目经理) = 绩效表的 bkhr (被考核人) )     不是  进线
        String xmjlSql = "select count(1) as xmshang  from account a join project p on a.xmmc = '"+ xmmc +"'  where  p.xmjl = '"+ bkhrId +"' and  a.recordtype <> '2018525F215221DtlTXV' and a.createdate <=  str_to_date('"+nowday+" 23:59:59', '%Y-%m-%d %H:%i:%s') and a.createdate >= str_to_date('"+nowday+" 00:00:00', '%Y-%m-%d %H:%i:%s') and a.is_deleted='0' ";
        List<CCObject> countXmjl=cs.cqlQuery("account",xmjlSql);
        if (countXmjl.size() > 0) {
            // 取 非 进线的总数--项目经理  
            xmshangmen = countXmjl.get(0).get("xmshang")==null?0:Integer.parseInt(countXmjl.get(0).get("xmshang")+"");
            xmshangmen += khs; // 将 当月的 客户数 累加 当天的 客户数
        }
    }
    else {   
        // 目的:  业务人员 创建了 总的  客户上门数 
        // 业务人员的 recordtype ('201884204B9C199odbgA')
        //获取业务人员的进线总数 (租凭 20186166515AE4A8ZfOc、销售 2018525F215221DtlTXV ) 
        // 业务人员汇总上门数 (非进线)                     获取 account a (客户表)  where 创建人 = 被考核人id and 不是进线  
        String ywrySql = "select count(1) as ywshangmen from account a where a.createbyid = '"+ bkhrId +"' and  a.recordtype <> '2018525F215221DtlTXV' and a.createdate <=  str_to_date('"+nowday+" 23:59:59', '%Y-%m-%d %H:%i:%s') and a.createdate >= str_to_date('"+nowday+" 00:00:00', '%Y-%m-%d %H:%i:%s')  and a.is_deleted='0' ";
        List<CCObject> countYwry=cs.cqlQuery("account",ywrySql);
        if (countYwry.size() > 0) {
            xmshangmen = countYwry.get(0).get("ywshangmen")==null?0:Integer.parseInt(countYwry.get(0).get("ywshangmen")+"");
            xmshangmen += khs; // 将 当月的 客户数 累加 当天的 客户数
        }
    }
    
     ry.put("khs",xmshangmen);
     cs.updateLt(ry);
}


