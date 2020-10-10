String shouji = record_new.get("shouji") == null ? "" : String.valueOf(record_new.get("shouji"));// 手机
shouji=shouji.trim();
if(!shouji.equals("")){
boolean rs =  shouji.matches("^1[3|4|5|6|7|8|9][0-9]\\d{4,8}$");
if(!rs){
	trigger.addErrorMessage("手机号输入有误，请重新输入");
}
}