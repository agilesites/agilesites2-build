package agilesitesng.deploy.spoon

import java.lang.annotation.Annotation

import agilesites.annotations.Site
import spoon.processing.{AbstractAnnotationProcessor, ProcessorProperties, AnnotationProcessor}
import spoon.reflect.declaration.{CtAnnotation, CtClass}
import spoon.reflect.factory.Factory

/**
 * Created by msciab on 06/08/15.
 */
class SiteAnnotationProcessor extends AbstractAnnotationProcessor[Site, CtClass[_]] {

  def process(a: Site, cl: CtClass[_]) {
    println("...Site!!!")
    cl.getSimpleName
  }

}
