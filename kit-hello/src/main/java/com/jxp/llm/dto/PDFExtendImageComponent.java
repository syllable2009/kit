package com.jxp.llm.dto;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.contentstream.operator.DrawObject;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.contentstream.operator.state.Concatenate;
import org.apache.pdfbox.contentstream.operator.state.Restore;
import org.apache.pdfbox.contentstream.operator.state.Save;
import org.apache.pdfbox.contentstream.operator.state.SetGraphicsStateParameters;
import org.apache.pdfbox.contentstream.operator.state.SetMatrix;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.util.Matrix;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jiaxiaopeng
 * Created on 2025-05-19 17:14
 */
@Slf4j
public class PDFExtendImageComponent extends PDFStreamEngine {
    private PDDocument pdDocument;
    private PDFont font;

    @Setter
    @Getter
    private Function<PDImageXObject, String> imageProcessor;
    private final File pdfFile;

    private File getFontFile() {
        File tf = FileUtil.createTempFile();
        FileUtil.writeFromStream(ResourceUtil.getStream("微软雅黑.ttf"), tf);
        log.info("ft size: {}", tf.length());
        return tf;
    }

    /**
     * Default constructor.
     */
    public PDFExtendImageComponent(File pdfFile) {
        this.pdfFile = pdfFile;
        addOperator(new Concatenate(this));
        addOperator(new DrawObject(this));
        addOperator(new SetGraphicsStateParameters(this));
        addOperator(new Save(this));
        addOperator(new Restore(this));
        addOperator(new SetMatrix(this));
    }

    public File extendImage() {
        try (PDDocument document = Loader.loadPDF(pdfFile)) {
            this.pdDocument = document;
            this.font = PDType0Font.load(document, getFontFile());
            int pageNum = 0;
            for (PDPage page : document.getPages()) {
                pageNum++;
                log.info("Processing file {} page {}", pdfFile.getName(), pageNum);
                this.processPage(page);
            }
            File out = FileUtil.createTempFile(".pdf", true);
            document.save(out);
            return out;
        } catch (Throwable th) {
            log.error(th.getMessage(), th);
            return null;
        }
    }

    /**
     * This is used to handle an operation.
     *
     * @param operator The operation to perform.
     * @param operands The list of arguments.
     * @throws IOException If there is an error processing the operation.
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    @Override
    protected void processOperator(Operator operator, List<COSBase> operands) throws IOException {
        String operation = operator.getName();
        if (OperatorName.DRAW_OBJECT.equals(operation)) {
            COSName objectName = (COSName) operands.get(0);
            PDXObject xObject = getResources().getXObject(objectName);
            if (xObject instanceof PDImageXObject) {
                PDImageXObject image = (PDImageXObject) xObject;
                // int imageWidth = image.getWidth();
                // int imageHeight = image.getHeight();
                log.info("*******************************************************************");
                log.info("Found image [" + objectName.getName() + "]");
                Matrix ctmNew = getGraphicsState().getCurrentTransformationMatrix();
                float imageXScale = ctmNew.getScalingFactorX();
                float imageYScale = ctmNew.getScalingFactorY();
                Point2D.Float p1 = new Point2D.Float(0, 0);
                Point2D.Float p2 = new Point2D.Float(image.getWidth(), image.getHeight());
                ctmNew.transform(p1);
                ctmNew.transform(p2);
                Rectangle2D rect = new Rectangle2D.Float(p1.x, p1.y, p2.x - p1.x, p2.y - p1.y);

                try (PDPageContentStream cs = new PDPageContentStream(pdDocument, getCurrentPage(),
                        PDPageContentStream.AppendMode.APPEND, true, true)) {
                    cs.setNonStrokingColor(0.8F, 0.8F, 0.8F); // 设置填充颜色
                    cs.addRect((float) rect.getX(), (float) rect.getY(), imageXScale, imageYScale);
                    cs.fill();
                    float tx0 = (float) rect.getX() + 2;
                    float ty0 = (float) rect.getY() + imageYScale - 2;

                    cs.beginText();
                    cs.setFont(font, 0.01F);
                    cs.setNonStrokingColor(1.0F, 0, 0); // 设置文本颜色
                    cs.newLineAtOffset(tx0, ty0);
                    String extendTxt = imageProcessor.apply(image);
                    cs.showText(extendTxt);
                    // cs.showText("图片呀[" + objectName.getName() + "]");
                    cs.endText();
                }
            } else if (xObject instanceof PDFormXObject) {
                PDFormXObject form = (PDFormXObject) xObject;
                showForm(form);
            }
        } else {
            super.processOperator(operator, operands);
        }
    }
}
