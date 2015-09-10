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
    val enabledTypes = a.enabledTypes
    Spooler.insert(100, SpoonModel.Site(Uid.generate(s"Site.${name}"), name, enabledTypes.toList))
    println("...Site!!!")
   }

}
