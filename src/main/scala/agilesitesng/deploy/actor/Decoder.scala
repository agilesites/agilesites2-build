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
      ("descript" -> description) +
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
        'type -> attributeType,
        'c -> "PageAttribute",
        'mul -> mul,
        'existDep -> "false",
        'notEmbedded -> "false",
        'attributetype -> editor.getOrElse(""),
        'assettypename -> assetType.getOrElse(""),
        'assetsubtypename -> subtypes.mkString("|")
      )

   case ParentDefinition(id, name, description, parentType, parent, attributes) =>
      deploy("ParentDefinition", id, name, description,
        'type -> "STRING",
        'c -> parentType,
        'parent -> parent.getOrElse("")
      )

    case FlexFamily(attr,contentDef,parentDef,content,parent,filter) => Map(
      "op" -> "flexFamily",
      "flexAttribute" -> attr,
      "flexParentDef" -> parentDef,
      "flexContentDef" -> contentDef,
      "flexParent" -> parent,
      "flexContent" -> content,
      "flexFilter" -> filter)

    case Site(id, name, enabledTypes) => Map(
      "op" -> "site",
      "id" -> id.toString,
      "name" -> name,
      "enabledTypes" -> enabledTypes.mkString("|"))

    case x => Map("op" -> "echo",
      "value" -> s"${x.getClass} not recognized")
  }

}
