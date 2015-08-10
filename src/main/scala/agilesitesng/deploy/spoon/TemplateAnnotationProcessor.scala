package agilesitesng.deploy.spoon

import agilesites.annotations.Template
import agilesitesng.deploy.model.{Spooler, SpoonModel, Uid}
import spoon.processing.AbstractAnnotationProcessor
import spoon.reflect.declaration.CtClass

/**
 * Created by msciab on 06/08/15.
 */
class TemplateAnnotationProcessor extends AbstractAnnotationProcessor[Template, CtClass[_]] {

  def process(a: Template, cl: CtClass[_]) {
    val name = cl.getQualifiedName
    Spooler.insert(50, SpoonModel.Template(Uid.generate(s"Template.${name}"), name))
    println("...Template!!!")
  }

}
