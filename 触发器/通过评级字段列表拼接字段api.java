//通过该触发器验证客户名称是否存在重复
String pjzdlb=null==record_new.get("pjzdlb")?"":String.valueOf(record_new.get("pjzdlb"));//评级字段列表
if(!pjzdlb.equals("")){

String [] strArray = pjzdlb.split(";");

	

trigger.addErrorMessage("1111111:"+strArray.length);

}
