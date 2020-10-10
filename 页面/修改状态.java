<cc:page type="normal" style="standard" showSidebar="false" showHeader="false" />
<cc!>
/**
* 将ISO-8859-1编码格式的字符串转码为UTF-8
* 
* @param parameterValue
* @return
* @throws UnsupportedEncodingException
*/
private static String encodeParameters(String parameterValue)
    throws UnsupportedEncodingException {
if (parameterValue != null && parameterValue.length() > 0) {
    byte[] iso = parameterValue.getBytes("ISO-8859-1");
    if (parameterValue.equals(new String(iso, "ISO-8859-1"))) {
        parameterValue = new String(iso, "UTF-8");
        return parameterValue;
    }
}
return parameterValue;
}
</cc>
<cc>
JSONObject jsonmsg = new JSONObject();
CCService cs = new CCService(userInfo);
try {
	String ids = request.getParameter("ids")==null?"":encodeParameters(request.getParameter("ids")+"");//id集合
        String type = request.getParameter("type")==null?"":encodeParameters(request.getParameter("type")+"");//类型
        String [] id = ids.split(",");
        int j = 0;
        if("删除".equals(type)){
            for(int i = 0;i<id.length;i++){
                  List<CCObject> list = cs.cqlQuery("ProjectDetail","update ProjectDetail set is_deleted='1' where id = '"+id[i]+"'");
                  j += list.size();
            }
        }else{
            for(int i = 0;i<id.length;i++){
                  List<CCObject> list = cs.cqlQuery("ProjectDetail","update ProjectDetail set lfzt='"+type+"' where id = '"+id[i]+"'");
                  j += list.size();
            }
        }
        if(j>0){
             jsonmsg.put("success", true);
        }else{
             jsonmsg.put("success", false);
        }
} catch (Exception e) {
	jsonmsg.put("success", false);
        
	jsonmsg.put("message", e.getMessage());
}
request.setAttribute("jsonmsg", jsonmsg.toString());
</cc>
<cc:forward page="/platform/activity/task/getNodes.jsp"/>
