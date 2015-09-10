package agilesitesng.deploy.model


/**
 * Created by msciab on 15/07/15.
 */

trait SpoonModel

object SpoonModel extends ModelUtil {

  val classTypes = List(
    classOf[Site],
    classOf[FlexFamily],
    classOf[ContentDefinition],
    classOf[ParentDefinition],
    classOf[AssetAttribute],
    classOf[Attribute],
    classOf[AttributeEditor],
    classOf[StartMenu],
    classOf[Arg],
    classOf[Template],
    classOf[CSElement],
    classOf[SiteEntry],
    classOf[Controller])

  case class DeployObjects(deployObjects: List[SpoonModel]) {
    def apply(n: Int) = deployObjects(n)
  }

  case class Site(id: Long, name: String, enabledTypes:List[String]) extends SpoonModel

  case class FlexFamily(attribute:String, contentDef:String, parentDef:String, content:String, parent:String, filter:String) extends SpoonModel

  case class AttributeEditor(id: Long, name: String, file: String) extends SpoonModel

  case class Attribute(id: Long, name: String, description:String, mul:String, attibuteType:String, editor:Option[String], assetType:Option[String], subtypes: List[String]) extends SpoonModel

  case class ContentDefinition(id: Long, name: String, description:String, contentType:String, parent:Option[String], attributes:List[AssetAttribute]) extends SpoonModel

  case class ParentDefinition(id: Long, name: String, description:String, parentType:String, parent:Option[String], attributes:List[AssetAttribute]) extends SpoonModel

  case class StartMenu(id: Long, name: String, description:String, menuType:String, assetType:String, assetSubtype:String, args:List[Arg])  extends SpoonModel

  case class Template(id: Long, name: String, file: String) extends SpoonModel

  case class CSElement(id: Long, name: String, file: String) extends SpoonModel

  case class SiteEntry(id: Long, name: String) extends SpoonModel

  case class Controller(id: Long, name: String, file: String) extends SpoonModel

  case class AssetAttribute(name: String, required: Boolean)

  case class Arg(name:String, value:String)

}
