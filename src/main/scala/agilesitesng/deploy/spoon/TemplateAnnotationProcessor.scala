package agilesitesng.deploy.spoon

import agilesites.annotations.{Template, Site}
import spoon.processing.AbstractAnnotationProcessor
import spoon.reflect.declaration.CtClass

/**
 * Created by msciab on 06/08/15.
 */
class TemplateAnnotationProcessor extends AbstractAnnotationProcessor[Template, CtClass[_]] {

  def process(a: Template, cl: CtClass[_]) {
    println("...Template!!!")
  }

}
