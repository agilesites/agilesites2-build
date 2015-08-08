package agilesitesng.deploy.model

import java.util.Date

/**
  * Created by msciab on 15/07/15.
  */
trait AssetModel

object AssetModel extends ModelUtil {

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
                     ) extends AssetModel

   case class Associations(href: String) extends AssetModel

   case class Attribute(name: String, data: Data) extends AssetModel

   case class Data(stringValue: Option[String] = None,
                   longValue: Option[Long] = None,
                   blobValue: Option[Blob] = None) extends AssetModel

   case class Blob(filename: String,
                   foldername: String,
                   filedata: String) extends AssetModel


 }
