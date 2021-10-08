package org.cgsuite.util

import java.awt.image.BufferedImage

import java.io.File
import javax.imageio.{IIOImage, ImageIO, ImageTypeSpecifier}
import javax.imageio.metadata.{IIOMetadata, IIOMetadataNode}

import scala.jdk.CollectionConverters._

object ImageUtil {

  def writeHighDpiImage(image: BufferedImage, output: File, format: String, dpi: Int): Unit = {

    val writer = ImageIO.getImageWritersByFormatName(format).asScala find { w =>
      val writeParam = w.getDefaultWriteParam
      val metadata = w.getDefaultImageMetadata(typeSpecifier, writeParam)
      !metadata.isReadOnly && metadata.isStandardMetadataFormatSupported
    } getOrElse {
      throw new RuntimeException(s"Could not find metadata for format: $format")
    }
    val writeParam = writer.getDefaultWriteParam
    val metadata = writer.getDefaultImageMetadata(typeSpecifier, writeParam)
    setDpi(metadata, dpi)

    val stream = ImageIO.createImageOutputStream(output)
    try {
      writer.setOutput(stream)
      writer.write(metadata, new IIOImage(image, null, metadata), writeParam)
    } finally {
      stream.close()
    }

  }

  val typeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_ARGB)

  private def setDpi(metadata: IIOMetadata, dpi: Int): Unit = { // for PNG, it's dots per millimeter
    val dotsPerMm = dpi / 25.4
    val horiz = new IIOMetadataNode("HorizontalPixelSize")
    horiz.setAttribute("value", dotsPerMm.toString)
    val vert = new IIOMetadataNode("VerticalPixelSize")
    vert.setAttribute("value", dotsPerMm.toString)
    val dim = new IIOMetadataNode("Dimension")
    dim.appendChild(horiz)
    dim.appendChild(vert)
    val root = new IIOMetadataNode("javax_imageio_1.0")
    root.appendChild(dim)
    metadata.mergeTree("javax_imageio_1.0", root)
  }
}
