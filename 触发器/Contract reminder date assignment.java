String syrdqtz = record_new.get("syrdqtz")==null?"":String.valueOf(record_new.get("syrdqtz"));//合同到期提醒
String htjsrq = record_new.get("htjsrq")==null?"":String.valueOf(record_new.get("htjsrq"));//合同结束日期
if(!"".equals(syrdqtz)){
	/**
	String[] tss = syrdqtz.split("天");
	long ts = Integer.parseInt(tss[0]);
	*/
	long ts = 0;
	String str = "";
	for(int i=0;i<syrdqtz.length();i++){
		if(syrdqtz.charAt(i)>=48 && syrdqtz.charAt(i)<=57){//根据字节判断是否为数字
			str+=syrdqtz.charAt(i);
		}
	}
	ts = Integer.parseInt(str);
	//if(true){trigger.addErrorMessage(ts+"");}
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // 日期格式
	Date date = sdf.parse(htjsrq); // 指定日期
	long time = date.getTime(); // 得到指定日期的毫秒数
	ts = ts*24*60*60*1000; // 要加上的天数转换成毫秒数
	time = time - ts; // 相加得到新的毫秒数
	String txsj = sdf.format(time);// 输出格式化后的日期
	record_new.put("txsj",txsj);
}else{
	record_new.put("txsj","");
}