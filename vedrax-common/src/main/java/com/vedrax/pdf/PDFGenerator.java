package com.vedrax.pdf;

import org.apache.commons.lang3.Validate;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.nio.file.FileSystems;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PDFGenerator {

    private Logger LOG = Logger.getLogger(PDFGenerator.class.getName());

    private ClassLoaderTemplateResolver templateResolver;
    private TemplateEngine templateEngine;

    public PDFGenerator(
            final String templatePrefix,
            final String templateSuffix) {

        this(templatePrefix, templateSuffix, "HTML5", "UTF-8");
    }

    public PDFGenerator(
            final String templatePrefix,
            final String templateSuffix,
            final String templateMode,
            final String templateEncoding) {

        this(new ClassLoaderTemplateResolver());

        this.templateResolver.setPrefix(templatePrefix);
        this.templateResolver.setSuffix(templateSuffix);
        this.templateResolver.setTemplateMode(templateMode);
        this.templateResolver.setCharacterEncoding(templateEncoding);
    }

    public PDFGenerator(ClassLoaderTemplateResolver templateResolver) {
        this.templateResolver = templateResolver;
    }

    public PDFGenerator(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    private TemplateEngine getTemplateEngine() {
        if (templateEngine == null) {
            templateEngine = new TemplateEngine();
            templateEngine.setTemplateResolver(templateResolver);
        }

        return templateEngine;
    }

    /**
     * Process the template and generate a PDF of this rendered template.
     *
     * @param template Source template.
     * @param model    The data for the template.
     */
    public byte[] generate(String template, Map<String, Object> model, Locale locale) throws Exception {
        Validate.notNull(template, "template must be provided");

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        final Context ctx = new Context();
        ctx.setLocale(locale);
        ctx.setVariables(model);

        final TemplateEngine templateEngine = getTemplateEngine();
        String htmlContent = templateEngine.process(template, ctx);

        LOG.log(Level.INFO, htmlContent);

        ITextRenderer renderer = new ITextRenderer();

        /*
        String baseUrl = FileSystems
                .getDefault()
                .getPath("src", "main", "resources")
                .toUri()
                .toURL()
                .toString();
        renderer.setDocumentFromString(htmlContent, baseUrl);

         */

        renderer.setDocumentFromString(htmlContent);
        renderer.layout();
        renderer.createPDF(out);

        return out.toByteArray();
    }

}
