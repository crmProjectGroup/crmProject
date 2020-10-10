String jieduan = record_new.get("jieduan")==null?"":String.valueOf(record_new.get("jieduan"));//阶段
String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());//格式化当前时间
if(record_old==null){
	record_new.put("jdzjgxsj",now);
}else{
	String jieduan_old = record_old.get("jieduan")==null?"":String.valueOf(record_old.get("jieduan"));//阶段旧值
	if(!jieduan.equals(jieduan_old)){
		record_new.put("jdzjgxsj",now);
	}
}