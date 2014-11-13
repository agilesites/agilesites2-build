package agilesites.wem

import dispatch._
import Defaults._
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathFactory
import javax.xml.xpath.XPathExpression
import javax.xml.xpath.XPathConstants
import javax.xml.transform.TransformerFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.stream.StreamResult
import java.io.StringWriter
import javax.xml.transform.dom.DOMSource
import org.xml.sax.InputSource
import java.io.ByteArrayInputStream

object RestUtil {

  def login(url: String, user: String, pass: String) = {

    // build the request
    val uri = new java.net.URL(url)
    val base = host(uri.getHost, uri.getPort)
    val req = base / "cas" / "v1" / "tickets" << Map(
      "username" -> user,
      "password" -> pass)

    // run the request then redirect to the second 
    val res = Http(req)
    val req1 = dispatch.url(res().getHeader("Location")) << Map("service" -> "*")
    val token = Http(req1 OK as.String)

    // finally return the resulting ticket
    token()
  }

  def get(base: String, arg: String, token: String): String = {
    val req = (dispatch.url(base) / "REST" / arg) <<? Map("multiticket" -> token)
    println(">>>" + req.url)
    //req.setHeader("X-CSRF-Token", token)

    Http(req OK as.String).apply
  }

  val factory = DocumentBuilderFactory.newInstance
  factory.setValidating(false)
  factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
  val parser = factory.newDocumentBuilder
  val xpath = XPathFactory.newInstance.newXPath

  def processXmlWithXpath(doc: String, args: Seq[String]) {
    //println(args)
    if (args.size == 0) {
      //println("EMPTY!!!")
      println(prettyPrintXml(doc))
    } else {
      //println("NOT EMPTY!!!")
      val exps = args.map(xpath.compile(_))
      def loop(s: String, e: XPathExpression): String = e.evaluate(parser.parse(new InputSource(new ByteArrayInputStream(s.getBytes("utf-8")))))
      val res = exps.foldLeft(doc)(loop(_, _))
      println(prettyPrintXml(res))
    }
  }

  def prettyPrintXml(doc: String) = {
    val factory = TransformerFactory.newInstance()
    factory.setAttribute("indent-number", 2);
    val transformer = factory.newTransformer()
    transformer.setOutputProperty(OutputKeys.INDENT, "yes")
    val result = new StreamResult(new StringWriter())
    val is = new InputSource(new ByteArrayInputStream(doc.getBytes("utf-8")))
    val source = new DOMSource(parser.parse(is))
    transformer.transform(source, result)
    result.getWriter().toString()
  }

}