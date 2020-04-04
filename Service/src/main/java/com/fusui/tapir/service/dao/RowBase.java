package com.fusui.tapir.service.dao;

public class RowBase {

	
}



//
//
//package com.bom.acs
//
///*
//  This module manipulates raw database rows and convert them to data structure required by admin manager.
// */
//
//import spray.json._
//import spray.json.DefaultJsonProtocol
//
//import com.bom.acscommon._
//
//import Constants._
//
//trait DBRecord {
//  def pk: Long
//  def df: Boolean
//  def creator: Long
//  def cdate: Long
//  def unique: String = pk.toString
//}
//
//trait NamedRecord extends DBRecord {
//  def mid: Long
//  def name: String
//  def desc: String
//  def enabled: Boolean
//}
//
//case class NullRecord(pk: Long = 0L, df: Boolean = false, creator: Long = 0, cdate: Long = 0) extends DBRecord
//
//trait DBCacheable extends Cacheable {
//  val ns = "ACS-OBJECTS"
//}
//
//trait DBRecords[T <: DBRecord] {
//  def tbl: List[T]
//
//  // find the ID of the latest record
//  def loadback(tn: String) = if (tbl.isEmpty) LoadBack(tn, 0L) else LoadBack(tn, tbl.map(_.pk).max)
//
//  // select the latest version of all the records
//  def latest = last(0L)
//
//  // Filter a set of rows by given time and delete flag.
//  private def filterRows(ts: Long, rows: List[T]) =
//    (if (ts == 0) rows else rows.filter(_.cdate <= ts)).sortBy(_.cdate) match {
//      case xs if xs.isEmpty => List.empty
//      case vs if vs.last.df => List.empty
//      case vs => vs
//    }
//
//  // Group the given list by the unique key, and for each group, select the row right before the specified time.
//  def last(ts: Long): List[T] = tbl match {
//    case x if x.isEmpty => List.empty
//      // groupby by master policy id with different timestamp
//    case _ => tbl.groupBy(_.unique).toList.map(_._2).flatMap { rows =>
//      filterRows(ts, rows) match {
//        case x if x.isEmpty => None
//        case rs => Some(rs.last)
//      }
//    }
//  }
//
//  // Select the last record of the given unique key along with creator, creation date, modifier and mod date
//  def getDetail(key: String, ts: Long): Option[(T, Long, Long, Long, Long)] = tbl.filter(_.unique == key) match {
//    case x if x.isEmpty => None
//    case rows => filterRows(ts, rows) match {
//      case x if x.isEmpty => None
//      case rs => Some((rs.last, rs.head.creator, rs.head.cdate, rs.last.creator, rs.last.cdate))
//    }
//  }
//
//  def creators = tbl.map(_.creator).distinct
//}
//
//case object NullObject extends CacheableObject
//
//// Single rows
////
//case class PolicyRecord(pk: Long, mid: Long, df: Boolean, creator: Long, cdate: Long,
//                        name: String, desc: String, enabled: Boolean, predefined: Int) extends NamedRecord {
//  def policyInfo = PolicyInfo(mid, name, desc, enabled, predefined)
//  override def unique = mid.toString
//
//  // return policy info along with a flag that indicates whether the policy may be restricted by user license.
//  def limitedPolicyInfo(user: User, rs: RS) = PolicyInfo(mid, name, desc, enabled, predefined, rs.limited(user, mid))
//
//}
//object PolicyRecord extends DefaultJsonProtocol {
//  implicit val fmt = jsonFormat9(PolicyRecord.apply)
//}
//
//case class GroupRecord(pk: Long, mid: Long, df: Boolean, creator: Long, cdate: Long,
//                       name: String, desc: String, enabled: Boolean) extends NamedRecord {
//  def groupInfo = GroupInfo(mid, name, desc, enabled)
//  override def unique = mid.toString
//}
//object GroupRecord extends DefaultJsonProtocol {
//  implicit val fmt = jsonFormat8(GroupRecord.apply)
//}
//
//case class GrantRecord(pk: Long, df: Boolean, creator: Long, cdate: Long,
//                       uid: Long, gid: Long, pid: Long) extends DBRecord {
//  override def unique = s"$uid-$gid-$pid"
//}
//object GrantRecord extends DefaultJsonProtocol {
//  implicit val fmt = jsonFormat7(GrantRecord.apply)
//}
//
//case class RuleRecord(pk: Long, mid: Long, pid: Long, df: Boolean, creator: Long, cdate: Long,
//                      name: String, desc: String, enabled: Boolean, tn: String) extends DBRecord {
//  override def unique = mid.toString
//}
//object RuleRecord extends DefaultJsonProtocol {
//  implicit val fmt = jsonFormat10(RuleRecord.apply)
//}
//
//sealed trait RuleRelatedRecord extends DBRecord {
//  def rid: Long
//}
//
//case class ActionRecord(pk: Long, rid: Long, df: Boolean, creator: Long, cdate: Long, name: String) extends RuleRelatedRecord {
//  override def unique = s"$rid-$name"
//}
//object ActionRecord extends DefaultJsonProtocol {
//  implicit val fmt = jsonFormat6(ActionRecord.apply)
//}
//
//case class ViewRecord(pk: Long, rid: Long, df: Boolean, creator: Long, cdate: Long, name: String) extends RuleRelatedRecord {
//  override def unique = s"$rid-$name"
//}
//object ViewRecord extends DefaultJsonProtocol {
//  implicit val fmt = jsonFormat6(ViewRecord.apply)
//}
//
//case class CategoryRecord(pk: Long, rid: Long, df: Boolean, creator: Long, cdate: Long, cat: Long) extends RuleRelatedRecord {
//  override def unique = s"$rid-$cat"
//}
//object CategoryRecord extends DefaultJsonProtocol {
//  implicit val fmt = jsonFormat6(CategoryRecord.apply)
//}
//
//case class TemplateRecord(pk: Long, rid: Long, df: Boolean, creator: Long, cdate: Long, tmp: Long) extends RuleRelatedRecord {
//  override def unique = s"$rid-$tmp"
//}
//object TemplateRecord extends DefaultJsonProtocol {
//  implicit val fmt = jsonFormat6(TemplateRecord.apply)
//}
//
//case class AttributeRecord(pk: Long, rid: Long, df: Boolean, creator: Long, cdate: Long, name: String, v: String) extends RuleRelatedRecord {
//  override def unique = s"$rid-$name"
//}
//object AttributeRecord extends DefaultJsonProtocol {
//  implicit val fmt = jsonFormat7(AttributeRecord.apply)
//}
//
//case class CriteriaRecord(pk: Long, amid:Long, rid: Long, df: Boolean, creator: Long, cdate: Long, operator:String, values: String) extends RuleRelatedRecord {
//  override def unique = s"$rid-$amid-$operator"
//}
//object CriteriaRecord extends DefaultJsonProtocol {
//  implicit val fmt = jsonFormat8(CriteriaRecord.apply)
//}
//
//case class MemberRecord(pk: Long, df: Boolean, creator: Long, cdate: Long, uid: Long, gid: Long) extends DBRecord {
//  override def unique = s"$uid-$gid"
//}
//object MemberRecord extends DefaultJsonProtocol {
//  implicit val fmt = jsonFormat6(MemberRecord.apply)
//}
//
//// A table of rows
////
//case class PS(tbl: List[PolicyRecord]) extends DBRecords[PolicyRecord] with ReturnType with CacheableObject {
//  def pids(ts: Long = 0L) = last(ts).map(_.mid)
//  def toMap(ts: Long = 0L) = last(ts).map(p => (p.mid, p)).toMap
//  def merge(nv: PS) = PS(tbl ++ nv.tbl)
//
//  def policyinfo(pid:Long, ts: Long = 0L) : PolicyInfo = last(ts).find(_.mid == pid) match {
//      case None => PolicyInfo (-1, "", "", false)
//      case Some(p) => p.policyInfo
//  }
//
//  def policyinfos(pids: List[Long], ts: Long = 0L): List[PolicyInfo] = last(ts).collect {
//    case p if pids.contains(p.mid) => p.policyInfo
//  }
//  def limitedPolicyinfos(pids: List[Long], user: User, rs: RS, ts: Long = 0L) = last(ts).collect {
//    case p if pids.contains(p.mid) => p.limitedPolicyInfo(user, rs)
//  }
//}
//object PS extends DefaultJsonProtocol with DBCacheable {
//  implicit val fmt = jsonFormat1(PS.apply)
//  val tn = type_policies
//  def fromCache(s: String): PS = s.parseJson.convertTo[PS]
//  def toCache(v: CacheableObject) = v.asInstanceOf[PS].toJson.toString()
//}
//
//case class GS(tbl: List[GroupRecord]) extends DBRecords[GroupRecord] with ReturnType with CacheableObject {
//  def gids(ts: Long = 0L) = last(ts).map(_.mid)
//  def toMap(ts: Long = 0L) = last(ts).map(g => (g.mid, g)).toMap
//  def merge(nv: GS) = GS(tbl ++ nv.tbl)
//  def groupinfos(gids: List[Long], ts: Long = 0L): List[GroupInfo] = last(ts).collect {
//    case g if gids.contains(g.mid) => g.groupInfo
//  }
//}
//object GS extends DefaultJsonProtocol with DBCacheable {
//  implicit val fmt = jsonFormat1(GS.apply)
//  val tn = type_groups
//  def fromCache(s: String): GS = s.parseJson.convertTo[GS]
//  def toCache(v: CacheableObject) = v.asInstanceOf[GS].toJson.toString()
//}
//
//case class US(uids: List[Long]) extends ReturnType with CacheableObject
//object US extends DefaultJsonProtocol with DBCacheable {
//  implicit val fmt = jsonFormat1(US.apply)
//  val tn = type_userlist
//  def toCache(v: CacheableObject) = v.asInstanceOf[US].toJson.toString()
//  def fromCache(s: String): US = s.parseJson.convertTo[US]
//}
//
//case class GRS(tbl: List[GrantRecord]) extends DBRecords[GrantRecord] with ReturnType with CacheableObject {
//  def merge(nv: GRS) = GRS(tbl ++ nv.tbl)
//
//  // return a list of policy ids that are assigned to the given user and groups
//  // Caller is responsible for filtering out deleted policies and/or deleted groups
//  def getPoliciesByAssignees(uid: Long, gids: List[Long], ts: Long = 0L) =
//    last(ts).collect {
//      case r if (uid != 0 && r.uid == uid) || gids.contains(r.gid) => r.pid
//    }.distinct
//
//  // Return a list of user ids and a list of group ids that the given policies are assigned to
//  // Caller is responsible for filtering out deleted policies and/or deleted groups
//  def getAssigneesByPolicies(pids: List[Long], ts: Long = 0L): (List[Long], List[Long]) = {
//    val (glist, ulist) = last(ts).filter(x => pids.contains(x.pid)).partition(_.uid == 0)
//
//    (ulist.map(_.uid).distinct, glist.map(_.gid).distinct)
//  }
//}
//object GRS extends DefaultJsonProtocol with DBCacheable {
//  implicit val fmt = jsonFormat1(GRS.apply)
//  val tn = type_grants
//  def fromCache(s: String): GRS = s.parseJson.convertTo[GRS]
//  def toCache(v: CacheableObject) = v.asInstanceOf[GRS].toJson.toString()
//}
//
//case class MS(tbl: List[MemberRecord]) extends DBRecords[MemberRecord] with ReturnType with CacheableObject {
//  def merge(nv: MS) = MS(tbl ++ nv.tbl)
//
//  // return a list of groups that the given user is a member of
//  // Caller is responsible for filtering out deleted groups.
//  def getGroupsByMember(uid: Long, ts: Long = 0L) = last(ts).filter(_.uid == uid).map(_.gid)
//
//  // return a list of users who are members of the given groups
//  // Caller is responsible for filtering out deleted groups
//  def getMembersByGroups(gids: List[Long], ts: Long = 0L) =
//    last(ts).collect { case m if gids.contains(m.gid) => m.uid }.distinct
//
//  // Group groups by user IDs
//  // Caller is responsible for filtering out deleted groups.
//  def groupGroupsByMember(ts: Long = 0L): Map[Long, List[Long]] =
//    last(ts).groupBy(_.uid).map { case (uid, xs) => (uid, xs.map(_.gid)) }
//
//  // Group members by group IDs
//  // Caller is responsible for filtering out deleted groups.
//  def groupMembersByGroup(ts: Long): Map[Long, List[Long]] =
//    last(ts).groupBy(_.gid).map { case (gid, xs) => (gid, xs.map(_.uid)) }
//}
//object MS extends DefaultJsonProtocol with DBCacheable {
//  implicit val fmt = jsonFormat1(MS.apply)
//  val tn = type_members
//  def fromCache(s: String): MS = s.parseJson.convertTo[MS]
//  def toCache(v: CacheableObject) = v.asInstanceOf[MS].toJson.toString()
//}
//
//case class ActionRecords(tbl: List[ActionRecord]) extends DBRecords[ActionRecord] {
//  def getActionsByRule(rid: Long, ts: Long = 0L) = last(ts).filter(_.rid == rid).map(_.name)
//}
//object ActionRecords extends DefaultJsonProtocol {
//  implicit val fmt = jsonFormat1(ActionRecords.apply)
//}
//
//case class ViewRecords(tbl: List[ViewRecord]) extends DBRecords[ViewRecord] {
//  def getViewsByRule(rid: Long, ts: Long) = last(ts).filter(_.rid == rid).map(_.name)
//}
//object ViewRecords extends DefaultJsonProtocol {
//  implicit val fmt = jsonFormat1(ViewRecords.apply)
//}
//
//case class CategoryRecords(tbl: List[CategoryRecord]) extends DBRecords[CategoryRecord] {
//  def getIDsByRuleID(rid: Long, ts: Long = 0L) = last(ts).filter(_.rid == rid).map(_.cat)
//}
//object CategoryRecords extends DefaultJsonProtocol {
//  implicit val fmt = jsonFormat1(CategoryRecords.apply)
//}
//
//case class TemplateRecords(tbl: List[TemplateRecord]) extends DBRecords[TemplateRecord] {
//  def getIDsByRuleID(rid: Long, ts: Long = 0L): List[Long] = last(ts).filter(_.rid == rid).map(_.tmp)
//}
//object TemplateRecords extends DefaultJsonProtocol {
//  implicit val fmt = jsonFormat1(TemplateRecords.apply)
//}
//
//
//case class AttributeRecords(tbl: List[AttributeRecord]) extends DBRecords[AttributeRecord] {
//  def getAttributesByRule(rid: Long, ts: Long): Map[String, String] = last(ts).filter(_.rid == rid).map(x => (x.name, x.v)).toMap
//}
//object AttributeRecords extends DefaultJsonProtocol {
//  implicit val fmt = jsonFormat1(AttributeRecords.apply)
//}
//
//case class CriteriaRecords(tbl: List[CriteriaRecord]) extends DBRecords[CriteriaRecord] {
//  // def getCriteriaByRule(rid: Long, ts: Long): List[CriteriaInfo] = last(ts).filter(_.rid == rid).map(x => CriteriaInfo( undefined_property_id, x.amid, "", x.operator, x.values))
//
//  def getCriteriaByRule(rid: Long, tnMap : Map[String, AdditionalAttribute], ts: Long): List[CriteriaInfo] = last(ts).filter(_.rid == rid).map(
//    x => CriteriaInfo( undefined_property_id, x.amid,
//                       if (tnMap.isEmpty) "" else tnMap.get(x.amid.toString).getOrElse(AdditionalAttribute.empty).name,
//                       x.operator, x.values, CriteriaInfo.CRITERIA_DEFAULT_LOGIC_OP /* AND */)
//  )
//  def getRuleIDsByAttributeID(amid: Long, ts: Long = 0L): List[Long] = last(ts).filter (_.amid == amid).map (_.rid).distinct
//}
//object CriteriaRecords extends DefaultJsonProtocol {
//  implicit val fmt = jsonFormat1(CriteriaRecords.apply)
//}
//
//// RS is an in-memory block representing the rules and its coresponding tables stored in DB for a particular workspace
//case class RS(tbl: List[RuleRecord], acts: ActionRecords, vws: ViewRecords, cats: CategoryRecords, tmps: TemplateRecords,
//              attrs: AttributeRecords, crs:CriteriaRecords) extends DBRecords[RuleRecord] with ReturnType with CacheableObject {
//  def rids = latest.map(_.mid)
//  def toMap(ts: Long = 0L) = last(ts).map(r => (r.mid, r)).toMap
//
//  // return rule info for the given rule ID
//  def ruleInfo(rid: Long, ts: Long = 0L): Option[RuleInfo] = last(ts).find(_.mid == rid) match {
//    case None => None
//    case Some(r) => Some(RuleInfo(rid, r.pid, r.name, r.desc, r.enabled, r.tn, r.tn,
//        acts.getActionsByRule(rid, ts).mkString(","), vws.getViewsByRule(rid, ts).mkString(","),
//        cats.getIDsByRuleID(rid, ts),
//        tmps.getIDsByRuleID(rid,ts),
//        attrs.getAttributesByRule(rid, ts), crs.getCriteriaByRule(r.mid, Map.empty, ts) ))
//  }
//
//  // return a list of rule infos for the given policy
//  def ruleInfos(pid: Long, ts: Long = 0L, catmap: Map[Long, List[Long]] = Map.empty): List[RuleInfo] = {
//    last(ts).filter(_.pid == pid).map { r =>
//      RuleInfo(r.mid, r.pid, r.name, r.desc, r.enabled, r.tn, r.tn, acts.getActionsByRule(r.mid, ts).mkString(","),
//        vws.getViewsByRule(r.mid, ts).mkString(","),
//        if (catmap.isEmpty) cats.getIDsByRuleID(r.mid, ts) else catmap.getOrElse(r.mid, List.empty),
//        tmps.getIDsByRuleID(r.mid, ts), //else catmap.getOrElse(r.mid, List.empty),
//        attrs.getAttributesByRule(r.mid, ts), crs.getCriteriaByRule(r.mid, Map.empty, ts))
//    }
//  }
//
////  def ruleUsageByAttributeIds (aids: Seq[Long], ps:PS, ts: Long = 0L): Seq[RuleUsage ] = {
////    val thisRS: List[RuleRecord] = last(ts)
////    val usages : Seq[List[RuleUsage]] =
////      aids.map(aid => {
////        crs.getRuleIDsByAttributeID(aid).map(rid =>
////          thisRS.find(_.mid == rid)).filter(_.isDefined).map(_.get).map(r => RuleUsage (aid, "", r.mid, r.name, r.tn, r.pid, ps.policyinfo(r.pid, ts).name))
////      }
////    )
////    usages.flatten
////  }
//
//
//  // simplify ruleInfo without action, view, cat, template. AdminManager.deleteCriteria() will call ModifyRule with criteriaOnly set to true and passing it to AdminDao.updateRule()
//  //def rulesByAttributeIds (amids: Seq[Long], ps:PS, ts: Long = 0L): Seq[( Long, RuleInfo, PolicyInfo )] = {
//  def rulesByAttributeIds (amids: Seq[Long], ps:PS, tnMap: Map[String, AdditionalAttribute], ts: Long = 0L): Seq[RuleUsage ] = {
//
//    val thisRS: List[RuleRecord] = last(ts)
//    val info : Seq[List[RuleUsage]]=
//      amids.map(amid => {
//
//        crs.getRuleIDsByAttributeID(amid).map(rid =>
//          thisRS.find(_.mid == rid)).filter(_.isDefined).map(_.get).map(r => (
//            RuleUsage ( amid,
//            RuleInfo(r.mid, r.pid, r.name, r.desc, r.enabled, r.tn, r.tn,
//              "", // actions
//              "", // views
//              List.empty, // cat
//              List.empty, // templates
//              Map.empty,
//              crs.getCriteriaByRule(r.mid, tnMap, ts)),
//            ps.policyinfo(r.pid, ts)
//          )
//          )
//        )
//      }
//    )
//    info.flatten
//
//  }
//
//
//  // return true if some rules are limited by given license
//  def limited(user: User, pid: Long) =
//    latest.filter(_.pid == pid).exists { r =>
//      acts.getActionsByRule(r.mid).diff(Metadata.get(r.tn).limitActions(user)).nonEmpty
//    }
//
//  def getCatMap: Map[Long, List[Long]] =
//    cats.latest.groupBy(_.rid).map { case (rid, cs) => (rid, cs.map(_.cat))}
//}
//object RS extends DefaultJsonProtocol with DBCacheable {
//  implicit val fmt = jsonFormat7(RS.apply)
//  val tn = type_rules
//  def fromCache(s: String): RS = s.parseJson.convertTo[RS]
//  def toCache(v: CacheableObject) = v.asInstanceOf[RS].toJson.toString()
//}

