package agilesitesng.deploy.spoon

import agilesitesng.deploy.model.{Uid, Spooler, SpoonModel}
import spoon.processing.AbstractAnnotationProcessor
import spoon.reflect.declaration.CtField
import agilesites.annotations.AttributeEditor

class AttributeEditorAnnotationProcessor
  extends AbstractAnnotationProcessor[AttributeEditor, CtField[_]] {
  def process(a: AttributeEditor, cl: CtField[_]) {
    val name = cl.getSimpleName
    Spooler.insert(95, SpoonModel.AttributeEditor(Uid.generate(s"AttrTypes.${name}"), name))
  }
}
