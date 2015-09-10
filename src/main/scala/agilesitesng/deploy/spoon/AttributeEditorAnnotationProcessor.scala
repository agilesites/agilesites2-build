package agilesitesng.deploy.spoon

import agilesitesng.deploy.model.{Uid, Spooler, SpoonModel}
import spoon.processing.AbstractAnnotationProcessor
import spoon.reflect.code.CtLiteral
import spoon.reflect.declaration.CtField
import agilesites.annotations.AttributeEditor

class AttributeEditorAnnotationProcessor
  extends AbstractAnnotationProcessor[AttributeEditor, CtField[_]]
  with SpoonUtils {
  def process(a: AttributeEditor, cl: CtField[_]) {
    val exp = cl.getDefaultExpression
    val lit = if (exp.isInstanceOf[CtLiteral[_]])
      cl.getDefaultExpression.asInstanceOf[CtLiteral[String]].getValue
     else ""

    //println("**"+lit)

    val name = cl.getSimpleName
    val path = s"${cl.getDeclaringType.getQualifiedName}.${cl.getSimpleName}.xml"
    val outfile = writeFileOutdir(path,
    s"""<?XML VERSION="1.0"?>
       |<PRESENTATIONOBJECT NAME="${name}">
       |${lit}
       |</PRESENTATIONOBJECT>
     """.stripMargin)
    val uid = Uid.generate(s"AttrTypes.${name}")
    //cl.getDefaultExpression.
    println("...AttributeEditor...");
    println(cl.getDeclaringType.getQualifiedName)

    Spooler.insert(95, SpoonModel.AttributeEditor(uid, name, outfile))
  }
}
