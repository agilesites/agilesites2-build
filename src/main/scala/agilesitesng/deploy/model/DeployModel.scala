package agilesitesng.deploy.model

import java.util.Date

/**
 * Created by msciab on 15/07/15.
 */
object DeployModel extends DeployModelUtil {

  trait DeployObject

  case class DeployObjects(deployObjects: List[DeployObject]) {
    def apply(n: Int) = deployObjects(n)
  }

  case class Site(id: Long, name: String) extends DeployObject

  case class Asset(c: String
                   , cid: Long
                   , name: String
                   , description: String = ""
                   , subtype: String = ""
                   , status: String = "ED"
                   , publist: List[String] = Nil
                   , createdby: String = "agilesites"
                   , updatedby: String = "agilesites"
                   , createddate: Date = new java.util.Date()
                   , updateddate: Date = new java.util.Date()
                   , associations: Option[Associations] = None
                   , attribute: List[Attribute] = List.empty
                   // ,schemaLocation: String = "http://www.fatwire.com/schema/rest/1.0" // TODO add http://${sitesUrl}/schema/rest-api.xsd",
                    ) extends DeployObject

  case class Associations(href: String) extends DeployObject

  case class Attribute(name: String, data: Data) extends DeployObject

  case class Data(stringValue: Option[String] = None,
                  longValue: Option[Long] = None,
                  blobValue: Option[Blob] = None) extends DeployObject

  case class Blob(filename: String,
                  foldername: String,
                  filedata: String) extends DeployObject

  val classTypes = List(
    classOf[DeployObjects],
    classOf[Site],
    classOf[Asset],
    classOf[Associations],
    classOf[Attribute],
    classOf[Data],
    classOf[Blob])

}
