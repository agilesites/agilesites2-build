package agilesitesng.deploy.model

import java.util.Date

/**
 * Created by msciab on 15/07/15.
 */
object DeployModel extends DeployModelUtil {


  case class Site(id: String, name: String)

  case class Asset(id: String
                  ,subtype: String
                  ,name: String
                  ,description: String = ""
                  ,status: String = "ED"
                  ,publist: List[String] = Nil
                  ,createdby: String = "agilesites"
                  ,updatedby: String = "agilesites"
                  ,createddate: Date = new java.util.Date()
                  ,updateddate: Date= new java.util.Date()
                  ,associations: Option[Associations] = None
                  ,schemaLocation: String = "http://www.fatwire.com/schema/rest/1.0" // TODO add http://${sitesUrl}/schema/rest-api.xsd",
                  ,attribute: List[Attribute]
                  )

  case class Associations(href: String)

  case class Attribute(name: String, data: Data)

  case class Data(stringValue: Option[String]=None, longValue: Option[Long]=None, blobValue: Option[Blob]=None)

  case class Blob(filename: String, foldername: String, filedata: String)



}
