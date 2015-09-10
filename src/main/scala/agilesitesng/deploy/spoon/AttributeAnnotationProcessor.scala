package agilesitesng.deploy.spoon

import agilesites.annotations.{AssetAttribute, AssetSubtypes, Attribute, BlobAttribute}
import agilesitesng.deploy.model.{Spooler, SpoonModel, Uid}
import org.slf4j.LoggerFactory
import spoon.processing.AbstractAnnotationProcessor
import spoon.reflect.declaration.CtField
import spoon.support.reflect.declaration.CtAnnotationImpl
import spoon.support.reflect.reference.CtArrayTypeReferenceImpl

import scala.collection.JavaConversions._

/**
 * Created by msciab on 06/08/15.
 */
class AttributeAnnotationProcessor extends AbstractAnnotationProcessor[Attribute, CtField[_]] {

  def logger =  LoggerFactory.getLogger(classOf[AttributeAnnotationProcessor])

  def process(a: Attribute, cl: CtField[_]) {
    val name = cl.getSimpleName
    val description = if (a.description() == null || a.description().isEmpty) name else  a.description()
    val fieldClass = cl.getReference.getType.getActualClass
    val multiple = fieldClass.isArray
    val editor = a.editor() match {
      case b if b.isEmpty => None
      case b => Some(b)
    }
    val attributeClass = if (multiple) fieldClass.getComponentType else fieldClass
    val attributeType = attributeClass match {
      case ba if ba == classOf[BlobAttribute] => "blob"
      case st if st == classOf[String] => "string"
      case fa if fa == classOf[Float] => "float"
      case ia if ia == classOf[Integer] => "int"
      case da if da == classOf[java.util.Date] => "date"
      case as if as == classOf[AssetAttribute[_]] => "asset"
      case _ => "unknown"
    }
    val (assetType, assetSubtypes) = attributeType match {
      case "asset" =>
        val subtypes = cl.getAnnotations collect {
          case b if b.getAnnotationType.getActualClass == classOf[AssetSubtypes] => b.getActualAnnotation.asInstanceOf[AssetSubtypes].values()
        }
        val componentType = if (multiple)
          cl.getReference.getType.asInstanceOf[CtArrayTypeReferenceImpl[_]].getComponentType
        else
          cl.getReference.getType
        (Some(componentType.getActualTypeArguments.get(0).getSimpleName),subtypes.flatten.toList)
      case _ => (None, Nil)
    }
    val mul = if (multiple) "M" else "S"
    logger.debug(s"Attribute - name:$name description: $description attributeType: $attributeType mul: $mul editor: $editor assetType: $assetType subtypes: $assetSubtypes")
    Spooler.insert(90, SpoonModel.Attribute(Uid.generate(s"Attribute.$name"), name, description, mul, attributeType, editor, assetType, assetSubtypes))

  }

}
