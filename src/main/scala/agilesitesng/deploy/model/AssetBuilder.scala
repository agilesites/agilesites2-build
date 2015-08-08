package agilesitesng.deploy.model

/**
 * Created by msciab on 16/07/15.
 */

import java.util.Date

object AssetBuilder{

  import AssetModel._

  def CSElement(_id: Long, _name: String, blob: Blob)(implicit _site: String) =
    Asset(c = "CSElement"
          , cid = _id
          , subtype = ""
          , name = _name
          , publist = List(_site)
          , attribute = List(
            Attribute("rootelement", Data(stringValue=Some(s"${_site}/${_name}")))
            , Attribute("elementname", Data(stringValue=Some(s"${_site}/${_name}")))
            , Attribute("resdetails1", Data(stringValue=Some(s"eid=${_id}")))
            , Attribute("url", Data(blobValue=Some(blob))
          )))
}
