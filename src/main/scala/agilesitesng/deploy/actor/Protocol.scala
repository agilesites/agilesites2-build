package agilesitesng.deploy.actor

import java.net.URL

/**
 * Created by msciab on 04/08/15.
 */
object Protocol {

  case class Login(url: URL, username: String, password: String)

  case class AuthId(authid: String)

  case class Deploy(filename: String)

}
