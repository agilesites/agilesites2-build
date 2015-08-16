package agilesitesng.deploy.spoon

import agilesites.annotations.Site
import agilesitesng.deploy.model.{Spooler, SpoonModel, Uid}
import spoon.processing.AbstractAnnotationProcessor
import spoon.reflect.declaration.CtClass

/**
 * Created by msciab on 06/08/15.
 */
class SiteAnnotationProcessor extends AbstractAnnotationProcessor[Site, CtClass[_]] {

  def process(a: Site, cl: CtClass[_]) {
    val name = cl.getSimpleName
    Spooler.insert(100, SpoonModel.Site(Uid.generate(s"Site.${name}"), name))
    println("...Site!!!")
   }

}
