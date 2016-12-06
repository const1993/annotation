package processing;

import sun.plugin2.message.Message;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import annotatons.Threadable;

/**
 * Created by haria on 27.11.2016.
 */
public class TreadableProcessor extends AbstractProcessor{

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        final Set<String> annotations = new LinkedHashSet<String>();
        annotations.add(Threadable.class.getName());
        return annotations;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(Threadable.class)) {
            Threadable threadable = annotatedElement.getAnnotation(Threadable.class);
            String name = annotatedElement.getSimpleName().toString();
            char[] c = name.toCharArray();
            c[0] = Character.toUpperCase(c[0]);
            name = new String(name);
            TypeElement clazz = (TypeElement) annotatedElement.getEnclosingElement();
            try {
                JavaFileObject f = processingEnv.getFiler().
                        createSourceFile(clazz.getQualifiedName() + "Autogenerate");
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
                        "Creating " + f.toUri());
                Writer w = f.openWriter();
                try {
                    String pack = clazz.getQualifiedName().toString();
                    PrintWriter pw = new PrintWriter(w);
                    pw.println("package "
                            + pack.substring(0, pack.lastIndexOf('.')) + ";");
                    pw.println("\npublic class "
                            + clazz.getSimpleName() + "Autogenerate {");

                    TypeMirror type = annotatedElement.asType();


                    pw.println("\n    protected " + clazz.getSimpleName()
                            + "Autogenerate() {}");
                    pw.println("\n    /** Handle something. */");
                    pw.println("\n//" + annotatedElement);
                    pw.println("    }");
                    pw.println("}");
                    pw.flush();
                } finally {
                    w.close();
                }
            } catch (IOException x) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                        x.toString());
            }
        }

        return false;
    }
}
