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
        double cjmj = 0.0;
        double cjje = 0.0;
        String [] id = ids.split(",");
        int j = 0;
        for(int i = 0;i<id.length;i++){
              List<CCObject> detaillist = cs.cqlQuery("ProjectDetail","select * from ProjectDetail where id = '"+id[i]+"' and is_deleted = '0'");
              if(detaillist.size()>0){
                    String detailtype = detaillist.get(0).get("lfzt")==null?"":detaillist.get(0).get("lfzt")+"";
                    if(!detailtype.equals(type)){
                          double mj = detaillist.get(0).get("jzmjs")==null?0.0:Double.valueOf(detaillist.get(0).get("jzmjs")+"");//建筑面积
                          double dj = detaillist.get(0).get("dj")==null?0.0:Double.valueOf(detaillist.get(0).get("dj")+"");//单价
                          if("已租".equals(type)){
                               if("已售".equals(detailtype) || "未售".equals(detailtype)){
                                     break;
                               }
                               cjmj = cjmj + mj;
                               cjje = cjje + (mj*dj);
                               List<CCObject> list = cs.cqlQuery("ProjectDetail","update ProjectDetail set lfzt='"+type+"' where id = '"+id[i]+"'");
                          }else if("待租".equals(type)){
                               if("已售".equals(detailtype) || "未售".equals(detailtype)){
                                     break;
                               }
                               cjmj = cjmj - mj;
                               cjje = cjje - (mj*dj);
                               List<CCObject> list = cs.cqlQuery("ProjectDetail","update ProjectDetail set lfzt='"+type+"' where id = '"+id[i]+"'");
                          }else if("已售".equals(type)){
                               if("待租".equals(detailtype) || "已租".equals(detailtype)){
                                     break;
                               }
                               cjmj = cjmj + mj;
                               cjje = cjje + (mj*dj);
                               List<CCObject> list = cs.cqlQuery("ProjectDetail","update ProjectDetail set lfzt='"+type+"' where id = '"+id[i]+"'");
                          }else if("未售".equals(type)){
                               if("待租".equals(detailtype) || "已租".equals(detailtype)){
                                     break;
                               }
                               cjmj = cjmj - mj;
                               cjje = cjje - (mj*dj);
                               List<CCObject> list = cs.cqlQuery("ProjectDetail","update ProjectDetail set lfzt='"+type+"' where id = '"+id[i]+"'");
                          }else if("他方成交".equals(type)){
                               cjmj = cjmj - mj;
                               cjje = cjje - (mj*dj);
                               List<CCObject> list = cs.cqlQuery("ProjectDetail","update ProjectDetail set lfzt='"+type+"' where id = '"+id[i]+"'");
                          }
                    }

              }
}
        if(id.length>0){
            String projectdetailid = id[0];//单位明细ID
            List<CCObject> xmlist = cs.cqlQuery("xmssxx","select * from xmssxx where xmmc =(select l.xmmc as pid from ProjectDetail d,ldxx l where d.ldmc = l.id and l.is_deleted = '0' and d.is_deleted = '0' and d.id = '"+projectdetailid+"')");
            for(CCObject item:xmlist){
                 double scqymj = item.get("scqymj")==null?0.0:Double.valueOf(item.get("scqymj")+"");//市场成交面积
                 item.put("scqymj",scqymj+cjmj);
                 cs.updateLt(item);
            }
            j += xmlist.size();
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
 

