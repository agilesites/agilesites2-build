package agilesitesng.deploy.actor

import agilesitesng.Utils
import agilesitesng.deploy.model.SpoonModel
import agilesitesng.deploy.model.SpoonModel.{AttributeEditor, Site}

/**
 * Created by msciab on 20/08/15.
 */
object Decoder extends Utils  {
  def apply(model: SpoonModel): Map[String,String] = model match {
    case Site(id, name) =>
      Map("op"-> "site", "id" -> id.toString, "name" -> name)
    case AttributeEditor(id, name, file) =>
      Map("op" -> "gather", "id" -> id.toString, "name" -> name, "file" -> readFile(file))
  }

}
