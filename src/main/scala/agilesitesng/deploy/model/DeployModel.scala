package agilesitesng.deploy.model

import java.util.Date

/**
 * Created by msciab on 15/07/15.
 */

trait DeployModel

object DeployModel extends ModelUtil {

  val classTypes = List(
    classOf[Site],
    classOf[ContentDefinition],
    classOf[ParentDefinition],
    classOf[Attribute],
    classOf[Template],
    classOf[CSElement],
    classOf[SiteEntry])

  case class DeployObjects(deployObjects: List[DeployModel]) {
    def apply(n: Int) = deployObjects(n)
  }

  case class Site(id: Long, name: String) extends DeployModel

  case class Attribute(id: Long, name: String) extends DeployModel

  case class ContentDefinition(id: Long, name: String) extends DeployModel

  case class ParentDefinition(id: Long, name: String) extends DeployModel

  case class Template(id: Long, name: String) extends DeployModel

  case class CSElement(id: Long, name: String) extends DeployModel

  case class SiteEntry(id: Long, name: String) extends DeployModel

}
