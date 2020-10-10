//定时类:公客池获取数清零
//在ccuser下田夏字段gkskz做没填跟进客数的限制,没到一天结束,将计数清零

//获取CCS
CCService ccs = new CCService((UserInfo)userInfo);
//java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd"); //日期 MM需大写
//String date=sdf.format(new Date());
//获取所有业务员的ccuser对象, 并循环将gkskz置零
//List<CCObject> list = ccs.cquery("tp_sys_user","profile='aaa201723453E5EBNtzU' and isusing = '1' ");

ccs.cqlQuery("ccuser","update ccuser set gkskz = 0 where profile='aaa201723453E5EBNtzU' and isusing = '1' ");

