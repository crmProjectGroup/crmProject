SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
String now = sdf.format(new Date());
String status = String.valueOf(record_new.get("status"));
String status_old = String.valueOf(null == record_old ? "" : record_old.get("status"));
if (!status_old.equals(status) && "已结束".equals(status)) {
    String whoobj = String.valueOf(null == record_new.get("whoobj") ? "" : record_new.get("whoobj"));// 名称对象
    String whoid = String.valueOf(null == record_new.get("whoid") ? "" : record_new.get("whoid"));// 名称
    String cql = "";
    String objectApiName = "";// 对象名称
    if ("003".equals(whoobj)) {
		objectApiName = "Contact";
    } else if ("004".equals(whoobj)) {
		objectApiName = "Lead";
    }
    if (!"".equals(objectApiName) && !"".equals(whoid)) {
		//cql = "update " + objectApiName + " set zjlxsj = sysdate(),qzkhzt='已联系' where id = '" + whoid + "'";
		CCObject o = this.cquery(objectApiName, "id = '" + whoid + "'").get(0);
		o.put("zjlxsj",now);
        if (objectApiName.equals("Lead")) {
            o.put("qzkhzt","已联系");
        }
		this.update(o);
    }
    objectApiName = "";
    String relateobj = String.valueOf(null == record_new.get("relateobj") ? "" : record_new.get("relateobj"));// 相关项对象
    String relateId = String.valueOf(null == record_new.get("relateId") ? "" : record_new.get("relateId"));// 相关项
    if ("001".equals(relateobj)) {
		objectApiName = "Account";
    } else if ("002".equals(relateobj)) {
		objectApiName = "Opportunity";
    }
    if (!"".equals(objectApiName) && !"".equals(relateId)) {
		//cql = "update " + objectApiName + " set zjlxsj = sysdate() where id = '" + relateId + "'";
		CCObject c = this.cquery(objectApiName, "id = '" + relateId + "'").get(0);
		c.put("zjlxsj",now);
		this.update(c);
    }
}