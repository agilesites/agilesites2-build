package agilesitesng.deploy.model

import java.util.Date

/**
 * Created by msciab on 15/07/15.
 */

trait SpoonModel

object SpoonModel extends ModelUtil {

  val classTypes = List(
    classOf[Site],
    classOf[ContentDefinition],
    classOf[ParentDefinition],
    classOf[Attribute],
    classOf[AttributeEditor],
    classOf[Template],
    classOf[CSElement],
    classOf[SiteEntry],
    classOf[Controller])

  case class DeployObjects(deployObjects: List[SpoonModel]) {
    def apply(n: Int) = deployObjects(n)
  }

  case class Site(id: Long, name: String) extends SpoonModel

  case class AttributeEditor(id: Long, name: String, file: String) extends SpoonModel

  case class Attribute(id: Long, name: String, description:String, mul: Boolean, attibuteType:String, editor:String, assetType:String, subtypes: List[String]) extends SpoonModel

  case class ContentDefinition(id: Long, name: String) extends SpoonModel

  case class ParentDefinition(id: Long, name: String) extends SpoonModel

  case class Template(id: Long, name: String, file: String) extends SpoonModel

  case class CSElement(id: Long, name: String, file: String) extends SpoonModel

  case class SiteEntry(id: Long, name: String) extends SpoonModel

  case class Controller(id: Long, name: String, file: String) extends SpoonModel

}
