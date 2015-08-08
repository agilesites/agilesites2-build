package agilesitesng.deploy.spoon

import java.lang.annotation.Annotation

import agilesites.annotations.Site
import agilesitesng.deploy.model.{Uid, DeployModel, Spooler}
import spoon.processing.{AbstractAnnotationProcessor, ProcessorProperties, AnnotationProcessor}
import spoon.reflect.declaration.{CtAnnotation, CtClass}
import spoon.reflect.factory.Factory

/**
 * Created by msciab on 06/08/15.
 */
class SiteAnnotationProcessor extends AbstractAnnotationProcessor[Site, CtClass[_]] {

  def process(a: Site, cl: CtClass[_]) {
    val name = cl.getQualifiedName
    Spooler.insert(100, DeployModel.Site(Uid.generate(s"Site.${name}"), name))
    println("...Site!!!")
   }

}
