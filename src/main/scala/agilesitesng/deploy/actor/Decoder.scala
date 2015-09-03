package agilesitesng.deploy.actor

import agilesitesng.Utils
import agilesitesng.deploy.model.SpoonModel
import agilesitesng.deploy.model.SpoonModel.{AttributeEditor, Site}

/**
 * Created by msciab on 20/08/15.
 */
class Decoder(site: String, username: String, password: String) extends Utils {

  /**
   * @return a map ready to be deployed
   */
  def map(op: String, t2: Tuple2[String, String]*) = t2.toMap +
    ("op" -> op) +
    ("site" -> site) +
    ("username" -> username) +
    ("password" -> password)

  def apply(model: SpoonModel): Map[String, String] = model match {
    case Site(id, name) => map("site",
      "id" -> id.toString,
      "name" -> name)

    case AttributeEditor(id, name, file) => map("deploy",
      "id" -> id.toString,
      "name" -> name,
      "description" -> name,
      "value" -> "AttributeEditor",
      "debug" -> "yes",
      "cid" -> id.toString,
      "xml" -> readFile(file))

    case x => map("echo",
      "value" -> s"${x.getClass} not recognized")
  }

}
