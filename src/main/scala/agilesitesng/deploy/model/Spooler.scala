package agilesitesng.deploy.model

import net.liftweb.json.Serialization._
import agilesitesng.deploy.model.DeployModel.{DeployObjects, DeployObject}
import net.liftweb.json.{FullTypeHints, Serialization}

/**
 * A spooler class to collect object and serialize them in priority order
 *
 * @param map
 */
case class Spooler(val map: Map[String, List[DeployObject]] = Map.empty) {

  def push(i: Int, obj: DeployObject) = {
    val pri = i.toString
    val ls = if (map.contains(pri)) obj :: map(pri) else List(obj)
    new Spooler(map + (pri -> ls))
  }

  def pop(): (DeployObject, Spooler) = {
    val top = map.keys.map(_.toInt).max.toString
    val ls = map(top)

    val nmap = if (ls.size == 1)
      map - top
    else
      map + (top -> ls.tail)

    ls.head -> new Spooler(nmap)
  }

  def size = map.size

  override def toString = map.toString
}

object Spooler {

  var spool = new Spooler

  def insert(pri: Int, obj: DeployObject) {
    val t = spool.push(pri, obj)
    spool = t
  }

  def extract(): Option[DeployObject] = {
    if (spool.size == 0)
      None
    else {
      val (h, q) = spool.pop
      spool = q
      Some(h)
    }
  }

  import net.liftweb.json._
  import net.liftweb.json.Serialization.{read, write}

  implicit val formats = Serialization.formats(ShortTypeHints(DeployModel.classTypes))

  def save = {
    var res = List.empty[DeployObject]
    var o = extract()
    while (o.isDefined) {
      res = o.get :: res
      o = extract()
    }
    write(DeployObjects(res))
  }

  def load(ser: String) = read[DeployObjects](ser)
}
