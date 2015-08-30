package agilesitesng.deploy.actor

import agilesitesng.Utils
import agilesitesng.deploy.model.SpoonModel
import agilesitesng.deploy.model.SpoonModel.{AttributeEditor, Site}

/**
 * Created by msciab on 20/08/15.
 */
class Decoder(site: String) extends Utils {

  /**
   * Create a map request for gather, specifiying the "a:" prefix in each field
   *
   * @param c
   * @param t2
   * @return map ready to be "gathered"
   */
  def gather(c: String, t2: Tuple2[String, String]*) = {
    val prefixedSeq = t2.map(x => "a:" + x._1 -> x._2)
    Map("op" -> "gather",
      "c" -> c,
      "site" -> site,
      "createdby" -> "agilesites",
      "updatedby" -> "agilesites") ++
      prefixedSeq
  }

  def apply(model: SpoonModel): Map[String, String] = model match {
    case Site(id, name) =>
      Map("op" -> "site", "id" -> id.toString, "name" -> name)

    case AttributeEditor(id, name, file) =>
      gather("AttrTypes",
        "id" -> id.toString,
        "name" -> name,
        "description" -> name,
        "urlxml" -> readFile(file),
        "urlxml_folder" -> "",
        "urlxml_file" -> s"${id}.txt"
      )

    case x => Map("op" -> "echo",
      "value" -> s"${x.getClass} not recognized")
  }

}
