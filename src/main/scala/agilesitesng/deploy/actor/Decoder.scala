package agilesitesng.deploy.actor

import agilesitesng.Utils
import agilesitesng.deploy.model.SpoonModel
import agilesitesng.deploy.model.SpoonModel._

/**
 * Created by msciab on 20/08/15.
 */
class Decoder(site: String, username: String, password: String) extends Utils {

  /**
   * @return a map ready to be deployed
   */
  def deploy(value: String, id: Long, name: String, description: String, t2: Tuple2[Symbol, String]*) =
    t2.map(x => x._1.name -> x._2).toMap +
      ("op" -> "deploy") +
      ("value" -> value) +
      ("cid" -> id.toString) +
      ("name" -> name) +
      ("description" -> description) +
      ("site" -> site) +
      ("username" -> username) +
      ("password" -> password)

  def apply(model: SpoonModel): Map[String, String] = model match {

    //TODO add description here
    case AttributeEditor(id, name, file) =>
      deploy("AttributeEditor", id, name, name,
        'cid -> id.toString,
        'xml -> readFile(file))

    // TODO mul cab be Single, Multiple and ORDERED - need another field
    case Attribute(id, name, description, mul, attributeType, editor, assetType, subtypes) =>
      deploy("Attribute", id, name, description,
        'type -> "STRING",
        'c -> "PageAttribute",
        'mul -> "S",
        'existDep -> "false",
        'notEmbedded -> "false",
        'attributetype -> "0", //not defined
        'assettypename -> "Page",
        'assetsubtypename -> "" // a|b|c
      )

    case Site(id, name) => Map(
      "op" -> "site",
      "id" -> id.toString,
      "name" -> name)

    case Controller(id, name, file) => deploy("Controller", id, name, name,
      'filename -> new java.io.File(file).getName,
      'filefolder -> name.split("\\.").init.mkString("WCS_Controller/", "/", "/"),
      'fileext -> file.split("\\.").last,
      'filebody -> readFile(file)
    )

    //case CSElement(id, name) => deploy("CSElement", id, name, name)

    //case Template(id, name) => deploy("Template", id, name, name)

    //case SiteEntry(id, name) => deploy("SiteEntry", id, name, name)

    case x => Map("op" -> "echo",
      "value" -> s"${x.getClass} not recognized")
  }

}
