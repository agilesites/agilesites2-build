package agilesitesng.deploy.model

/**
 * Created by msciab on 06/08/15.
 */
class Spooler(val map: Map[Int, List[Object]] = Map.empty[Int,List[Object]]) {
  def push(pri: Int, obj: Object) = new Spooler(map + (pri -> map(pri)))

  def pop(): (Object, Spooler) = {
    val top = map.keys.max
    val ls = map(top)

    /* val spool1 = Spooler(
     if(ls.size == 1)
        map - top
      else
        map + (top -> ls.tail)
    )*/
   this -> this
  }

}

object Spooler {

  var spool = new Spooler

  def insert(pri: Int, obj: Object): Unit = {
    spool = spool.push(pri, obj)
  }
  def extract(): Object = {

   null
  }
}
